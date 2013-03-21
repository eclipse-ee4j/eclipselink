/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.internal.weaving.RelationshipInfo;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.DistributedSession;

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
    ChangeTracker, FetchGroupTracker, PersistenceWeavedRest {

    /**
     * Fetch properties manager.
     *
     * @return the dynamic properties manager
     */
    public abstract DynamicPropertiesManager fetchPropertiesManager();
    
    protected Map<String, PropertyWrapper> propertiesMap = new HashMap<String, PropertyWrapper>();
    
    /**
     * Instantiates a new dynamic entity impl.
     */
    public DynamicEntityImpl() {
        postConstruct(); // life-cycle callback
    }
    
    /**
     * Gets the properties map.
     *
     * @return the properties map
     */
    public Map<String, PropertyWrapper> getPropertiesMap() {
        return propertiesMap;
    }
    
    /**
     * Post construct.
     */
    protected void postConstruct() {
        DynamicPropertiesManager dpm = fetchPropertiesManager();
        dpm.postConstruct(this);
    }

    /**
     * Gets internal impl class of {@link DynamicType}.
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
    /* (non-Javadoc)
     * @see org.eclipse.persistence.dynamic.DynamicEntity#get(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.eclipse.persistence.dynamic.DynamicEntity#isSet(java.lang.String)
     */
    public boolean isSet(String propertyName) throws DynamicException {
        if (fetchPropertiesManager().contains(propertyName)) {
            if (_persistence_getFetchGroup() != null && 
                !_persistence_getFetchGroup().containsAttributeInternal(propertyName)) {
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

    /* (non-Javadoc)
     * @see org.eclipse.persistence.dynamic.DynamicEntity#set(java.lang.String, java.lang.Object)
     */
    public DynamicEntity set(String propertyName, Object value) throws DynamicException {
        return set(propertyName, value, true);
    }

    
    /**
     * Sets the.
     *
     * @param propertyName the property name
     * @param value the value
     * @param firePropertyChange the fire property change
     * @return the dynamic entity
     * @throws DynamicException the dynamic exception
     */
    public DynamicEntity set(String propertyName, Object value, boolean firePropertyChange) throws DynamicException {
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
        if (changeListener != null && firePropertyChange) {
            changeListener.propertyChange(new PropertyChangeEvent(this, propertyName, 
                oldValue, value));
        }
        return this;
    }

    public class PropertyWrapper {
        private Object value = null;
        private boolean isSet = false;
        
        /**
         * Instantiates a new property wrapper.
         */
        public PropertyWrapper() {
        }
        
        /**
         * Instantiates a new property wrapper.
         *
         * @param value the value
         */
        public PropertyWrapper(Object value) {
            setValue(value);
        }
        
        /**
         * Gets the value.
         *
         * @return the value
         */
        public Object getValue() {
            return value;
        }
        
        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(Object value) {
            this.value = value;
        }
        
        /**
         * Checks if is sets the.
         *
         * @return true, if is sets the
         */
        public boolean isSet() {
            return isSet;
        }
        
        /**
         * Checks if is set.
         *
         * @param isSet the is set
         */
        public void isSet(boolean isSet) {
            this.isSet = isSet;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
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
        
        /**
         * Instantiates a new unknown mapping.
         *
         * @param propertyName the property name
         */
        public UnknownMapping(String propertyName) {
            setAttributeName(propertyName);
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#buildBackupClone(java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)
         */
        public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {            
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#buildClone(java.lang.Object, org.eclipse.persistence.internal.identitymaps.CacheKey, java.lang.Object, java.lang.Integer, org.eclipse.persistence.internal.sessions.AbstractSession)
         */
        @Override
        public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#buildCloneFromRow(org.eclipse.persistence.internal.sessions.AbstractRecord, org.eclipse.persistence.internal.queries.JoinedAttributeManager, java.lang.Object, org.eclipse.persistence.internal.identitymaps.CacheKey, org.eclipse.persistence.queries.ObjectBuildingQuery, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, org.eclipse.persistence.internal.sessions.AbstractSession)
         */
        public void buildCloneFromRow(AbstractRecord databaseRow,
            JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery,
            UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#cascadePerformRemoveIfRequired(java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, java.util.Map)
         */
        public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow,
            Map visitedObjects) {
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#cascadeRegisterNewIfRequired(java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, java.util.Map)
         */
        public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow,
            Map visitedObjects) {
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#compareForChange(java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.sessions.ObjectChangeSet, org.eclipse.persistence.internal.sessions.AbstractSession)
         */
        public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner,
            AbstractSession session) {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#compareObjects(java.lang.Object, java.lang.Object, org.eclipse.persistence.internal.sessions.AbstractSession)
         */
        public boolean compareObjects(Object firstObject, Object secondObject,
            AbstractSession session) {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#fixObjectReferences(java.lang.Object, java.util.Map, java.util.Map, org.eclipse.persistence.queries.ObjectLevelReadQuery, org.eclipse.persistence.sessions.remote.RemoteSession)
         */
        public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects,
            ObjectLevelReadQuery query, DistributedSession session) {   
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#iterate(org.eclipse.persistence.internal.descriptors.DescriptorIterator)
         */
        public void iterate(DescriptorIterator iterator) {   
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#mergeChangesIntoObject(java.lang.Object, org.eclipse.persistence.internal.sessions.ChangeRecord, java.lang.Object, org.eclipse.persistence.internal.sessions.MergeManager, org.eclipse.persistence.internal.sessions.AbstractSession)
         */
        public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source,
            MergeManager mergeManager, AbstractSession targetSession) {
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.persistence.mappings.DatabaseMapping#mergeIntoObject(java.lang.Object, boolean, java.lang.Object, org.eclipse.persistence.internal.sessions.MergeManager, org.eclipse.persistence.internal.sessions.AbstractSession)
         */
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
    
    protected CacheKey cacheKey;
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.descriptors.PersistenceEntity#_persistence_getId()
     */
    @SuppressWarnings("unchecked")
    public Object _persistence_getId() {
        return this.primaryKey;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.descriptors.PersistenceEntity#_persistence_setId(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void _persistence_setId(Object pk) {
        this.primaryKey = pk;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.descriptors.PersistenceEntity#_persistence_getCacheKey()
     */
    public CacheKey _persistence_getCacheKey(){
        return this.cacheKey;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.descriptors.PersistenceEntity#_persistence_setCacheKey(org.eclipse.persistence.internal.identitymaps.CacheKey)
     */
    public void _persistence_setCacheKey(CacheKey cacheKey){
        this.cacheKey = cacheKey;
    }
    
    //ChangeTracker API
    /**
     * ChangeListener used for attribute change tracking processed in the
     * property. Set through
     * {@link ChangeTracker#_persistence_setPropertyChangeListener(PropertyChangeListener)}
     */
    private PropertyChangeListener changeListener = null;
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.descriptors.changetracking.ChangeTracker#_persistence_getPropertyChangeListener()
     */
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return this.changeListener;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.descriptors.changetracking.ChangeTracker#_persistence_setPropertyChangeListener(java.beans.PropertyChangeListener)
     */
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
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.queries.FetchGroupTracker#_persistence_getFetchGroup()
     */
    public FetchGroup _persistence_getFetchGroup() {
        return this.fetchGroup;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.queries.FetchGroupTracker#_persistence_setFetchGroup(org.eclipse.persistence.queries.FetchGroup)
     */
    public void _persistence_setFetchGroup(FetchGroup group) {
        this.fetchGroup = group;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.queries.FetchGroupTracker#_persistence_setShouldRefreshFetchGroup(boolean)
     */
    public void _persistence_setShouldRefreshFetchGroup(boolean shouldRefreshFetchGroup) {
        this.refreshFetchGroup = shouldRefreshFetchGroup;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.queries.FetchGroupTracker#_persistence_shouldRefreshFetchGroup()
     */
    public boolean _persistence_shouldRefreshFetchGroup() {
        return this.refreshFetchGroup;
    }
    
    /**
     * Return true if the attribute is in the fetch group being tracked.
     *
     * @param attribute the attribute
     * @return true, if successful
     */
    public boolean _persistence_isAttributeFetched(String attribute) {
        return this.fetchGroup == null || this.fetchGroup.containsAttributeInternal(attribute);
    }
    /**
     * Reset all attributes of the tracked object to the un-fetched state with
     * initial default values.
     */
    public void _persistence_resetFetchGroup() {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.weaving.PersistenceWeavedRest#_persistence_getRelationships()
     */
    public List<RelationshipInfo> _persistence_getRelationships(){
        List<RelationshipInfo> relationships = (List<RelationshipInfo>)get("_persistence_relationshipInfo");
        if (relationships == null){
            relationships = new ArrayList<RelationshipInfo>();
            _persistence_setRelationships(relationships);
        }
        return relationships;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.weaving.PersistenceWeavedRest#_persistence_setRelationships(java.util.List)
     */
    public void _persistence_setRelationships(List<RelationshipInfo> relationships){
        set("_persistence_relationshipInfo", relationships, false);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.weaving.PersistenceWeavedRest#getPersistence_href()
     */
    public Link _persistence_getHref() {
        return get("_persistence_href");
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.weaving.PersistenceWeavedRest#_persistence_setHref(org.eclipse.persistence.internal.jpa.rs.metadata.model.Link)
     */
    public void _persistence_setHref(Link href) {
        set("_persistence_href", href, false);
    }
    
    /**
     * Session cached by
     * {@link FetchGroupTracker#_persistence_setSession(Session)}
     */
    private Session session;
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.queries.FetchGroupTracker#_persistence_getSession()
     */
    public Session _persistence_getSession() {
        return this.session;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.persistence.queries.FetchGroupTracker#_persistence_setSession(org.eclipse.persistence.sessions.Session)
     */
    public void _persistence_setSession(Session session) {
        this.session = session;
    }

    /**
     * String representation of the dynamic entity using the entity type name
     * and the primary key values - something like {Emp 10} or {Phone 234-5678 10}.
     *
     * @return the string
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
