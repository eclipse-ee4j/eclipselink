/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.dynamic;

//javase imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;

/**
 * This abstract class is used to represent an entity which typically is not
 * realized in Java code. In combination with the DynamicClassLoader ASM is used
 * to generate subclasses that will work within EclipseLink's framework. Since
 * no concrete fields or methods exist on this class the mappings used must be
 * customized to use a custom AttributeAccessor ({@link ValuesAccessor}).
 * <p>
 * <b>Type/Property Meta-model</b>: This dynamic entity approach also includes a
 * meta-model facade to simplify access to the types and property information so
 * that clients can more easily understand the model. Each
 * {@link DynamicTypeImpl} wraps the underlying EclipseLink
 * relational-descriptor and the {@link DynamicPropertiesManager} wraps each mapping.
 * The client application can use these types and properties to facilitate
 * generic access to the entity instances and are required for creating new
 * instances as well as for accessing the Java class needed for JPA and
 * EclipseLink native API calls.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public abstract class DynamicEntityImpl implements DynamicEntity, PersistenceEntity,
    ChangeTracker, FetchGroupTracker {

    public abstract DynamicPropertiesManager fetchPropertiesManager();
    
    protected Map<String, PropertyWrapper> propertiesMap = new HashMap<String, PropertyWrapper>();
    
    public DynamicEntityImpl() {
        postConstruct(); // life-cycle callback
    }
    
    public Map<String, PropertyWrapper> getPropertiesMap() {
        return propertiesMap;
    }
    
    protected void postConstruct() {
        DynamicPropertiesManager dpm = fetchPropertiesManager();
        dpm.postConstruct(this);
    }

    /**
     * Gets internal impl class of {@link DynamicType}
     * 
     * @return Dynamic type of this entity
     * @throws DynamicException if type is null
     */
    public DynamicTypeImpl getType() throws DynamicException {
        DynamicType type = fetchPropertiesManager().getType();
        if (type == null) {
            throw DynamicException.entityHasNullType(this);
        }
        return (DynamicTypeImpl)type;
    }

    //DynamicEntity API
    public <T> T get(String propertyName) throws DynamicException {
        DynamicPropertiesManager dpm = fetchPropertiesManager();
        if (dpm.contains(propertyName)) {
            if (_persistence_getFetchGroup() != null) {
                String errorMsg = _persistence_getFetchGroup().onUnfetchedAttribute(this,
                    propertyName);
                if (errorMsg != null) {
                    throw DynamicException.invalidPropertyName(dpm.getType(), propertyName);
                }
            }
            PropertyWrapper wrapper = propertiesMap.get(propertyName);
            if (wrapper == null) { // properties can be added after constructor is called
                wrapper = new PropertyWrapper();
                propertiesMap.put(propertyName, wrapper);
            }          
            Object value = wrapper.getValue();
            // trigger any indirection
            if (value instanceof ValueHolderInterface) {
                value = ((ValueHolderInterface)value).getValue();
            }
            else if (value instanceof IndirectContainer) {
                value = ((IndirectContainer)value).getValueHolder().getValue();
            }
            try {
                return (T)value;
            }
            catch (ClassCastException cce) {
                ClassDescriptor descriptor = getType().getDescriptor();
                DatabaseMapping dm = null;
                if (descriptor != null) {
                    dm = descriptor.getMappingForAttributeName(propertyName);
                }
                else {
                    dm = new UnknownMapping(propertyName);
                }
                throw DynamicException.invalidGetPropertyType(dm, cce);
            }
        }
        else {
            throw DynamicException.invalidPropertyName(dpm.getType(), propertyName);
        }
    }

    public boolean isSet(String propertyName) throws DynamicException {
        if (fetchPropertiesManager().contains(propertyName)) {
            if (_persistence_getFetchGroup() != null && 
                !_persistence_getFetchGroup().containsAttribute(propertyName)) {
                return false;
            }
            PropertyWrapper wrapper = propertiesMap.get(propertyName);
            if (wrapper == null) { // properties can be added after constructor is called
                wrapper = new PropertyWrapper();
                propertiesMap.put(propertyName, wrapper);
            }
            return wrapper.isSet();
        }
        else {
            throw DynamicException.invalidPropertyName(fetchPropertiesManager().getType(),
                propertyName);
        }
    }

    public DynamicEntity set(String propertyName, Object value) throws DynamicException {
        DynamicPropertiesManager dpm = fetchPropertiesManager();
        dpm.checkSet(propertyName, value); // life-cycle callback
        if (_persistence_getFetchGroup() != null) {
            String errorMsg = _persistence_getFetchGroup().onUnfetchedAttributeForSet(this,
                propertyName);
            if (errorMsg != null) {
                throw DynamicException.invalidPropertyName(dpm.getType(), propertyName);
            }
        }
        PropertyWrapper wrapper = propertiesMap.get(propertyName);
        if (wrapper == null) { // properties can be added after constructor is called
            wrapper = new PropertyWrapper();
            propertiesMap.put(propertyName, wrapper);
        }
        Object oldValue = null;
        Object wrapperValue = wrapper.getValue();
        if (wrapperValue instanceof ValueHolderInterface) {
            ValueHolderInterface vh = (ValueHolderInterface)wrapperValue;
            if (vh.isInstantiated()) {
                oldValue = vh.getValue();
            }
            vh.setValue(value);
            wrapper.isSet(true);
        }
        else {
            oldValue = wrapperValue;
            wrapper.setValue(value);
            wrapper.isSet(true);
        }
        if (changeListener != null) {
            changeListener.propertyChange(new PropertyChangeEvent(this, propertyName, 
                oldValue, value));
        }
        return this;
    }

    public class PropertyWrapper {
        private Object value = null;
        private boolean isSet = false;
        public PropertyWrapper() {
        }
        public PropertyWrapper(Object value) {
            setValue(value);
        }
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }
        public boolean isSet() {
            return isSet;
        }
        public void isSet(boolean isSet) {
            this.isSet = isSet;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (isSet) {
                sb.append("[T]");
            }
            else {
                if (value == null) {
                    sb.append("[F]");
                }
                else {
                    sb.append("[d]");
                }
            }
            if (value == null) {
                sb.append("<null>");
            }
            else {
                sb.append(value.toString());
            }
            return sb.toString();
        }
    }

    class UnknownMapping extends DatabaseMapping {
        public UnknownMapping(String propertyName) {
            setAttributeName(propertyName);
        }
        public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {            
        }
        @Override
        public void buildClone(Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession) {
        }
        public void buildCloneFromRow(AbstractRecord databaseRow,
            JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery,
            UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        }
        public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow,
            Map visitedObjects) {
        }
        public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow,
            Map visitedObjects) {
        }
        public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner,
            AbstractSession session) {
            return null;
        }
        public boolean compareObjects(Object firstObject, Object secondObject,
            AbstractSession session) {
            return false;
        }
        public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects,
            ObjectLevelReadQuery query, RemoteSession session) {   
        }
        public void iterate(DescriptorIterator iterator) {   
        }
        public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source,
            MergeManager mergeManager, AbstractSession targetSession) {
        }
        public void mergeIntoObject(Object target, boolean isTargetUninitialized, Object source,
            MergeManager mergeManager, AbstractSession targetSession) {
        }
    }
    
    //PersistenceEntity API
    /**
     * Cache the primary key within the entity
     * 
     * @see PersistenceEntity#_persistence_setId(Object)
     */
    private Object primaryKey;
    
    @SuppressWarnings("unchecked")
    public Object _persistence_getId() {
        return this.primaryKey;
    }
    @SuppressWarnings("unchecked")
    public void _persistence_setId(Object pk) {
        this.primaryKey = pk;
    }
    
    //ChangeTracker API
    /**
     * ChangeListener used for attribute change tracking processed in the
     * property. Set through
     * {@link ChangeTracker#_persistence_setPropertyChangeListener(PropertyChangeListener)}
     */
    private PropertyChangeListener changeListener = null;
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return this.changeListener;
    }
    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.changeListener = listener;
    }

    //FetchGroup API
    /**
     * FetchGroup cached by
     * {@link FetchGroupTracker#_persistence_setFetchGroup(FetchGroup)}
     */
    private FetchGroup fetchGroup;
    /**
     * {@link FetchGroupTracker#_persistence_setShouldRefreshFetchGroup(boolean)}
     */
    private boolean refreshFetchGroup = false;
    public FetchGroup _persistence_getFetchGroup() {
        return this.fetchGroup;
    }
    public void _persistence_setFetchGroup(FetchGroup group) {
        this.fetchGroup = group;
    }
    public void _persistence_setShouldRefreshFetchGroup(boolean shouldRefreshFetchGroup) {
        this.refreshFetchGroup = shouldRefreshFetchGroup;
    }
    public boolean _persistence_shouldRefreshFetchGroup() {
        return this.refreshFetchGroup;
    }
    /**
     * Return true if the attribute is in the fetch group being tracked.
     */
    public boolean _persistence_isAttributeFetched(String attribute) {
        return this.fetchGroup == null || this.fetchGroup.containsAttribute(attribute);
    }
    /**
     * Reset all attributes of the tracked object to the un-fetched state with
     * initial default values.
     */
    public void _persistence_resetFetchGroup() {
        throw new UnsupportedOperationException("DynamicEntity._persistence_resetFetchGroup:: NOT SUPPORTED");
    }
    /**
     * Session cached by
     * {@link FetchGroupTracker#_persistence_setSession(Session)}
     */
    private Session session;
    public Session _persistence_getSession() {
        return this.session;
    }
    public void _persistence_setSession(Session session) {
        this.session = session;
    }

    /**
     * String representation of the dynamic entity using the entity type name
     * and the primary key values - something like {Emp 10} or {Phone 234-5678 10}
     */
    @Override
    public String toString() {
        // this will print something like {Emp 10} or {Phone 234-5678 10}
        StringBuilder sb = new StringBuilder(20);
        sb.append('{');
        sb.append(getShortClassName(this.getClass()));
        if (primaryKey != null) {
            sb.append(' ');
            sb.append(primaryKey);
        }
        sb.append('}');
        return sb.toString();
    }
}