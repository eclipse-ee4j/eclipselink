/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.util.Collection;
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;

/**
 * This abstract class is used to represent an entity which typically is not
 * realized in Java code. In combination with the DynamicClassLoader ASM is used
 * to generate subclasses that will work within EclipseLink's framework. Since
 * no concrete fields or methods exist on this class the mappings used must be
 * customized to use a custom AttributeAccessor (
 * {@link EntityPropertyImpl.DynamicAttributeAccessor}).
 * <p>
 * <b>Type/Property Meta-model</b>: This dynamic entity approach also includes a
 * meta-model facade to simplify access to the types and property information so
 * that clients can more easily understand the model. Each
 * {@link DynamicTypeImpl} wraps the underlying EclipseLink
 * relational-descriptor and the {@link EntityPropertyImpl} wraps each mapping.
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
    
    static final Object UNKNOWN_VALUE = new Object();

    /**
     * The persistent values indexed by the descriptor's mappings and the
     * EntityType's corresponding property list.
     */
    protected Object[] values;
    /**
     * Dynamic type of this entity
     */
    protected transient DynamicTypeImpl type;
    
    // must use InstantiationPolicy: ensures attributes get appropriate defaults
    protected DynamicEntityImpl(DynamicTypeImpl type) {
        this.type = type;
        this.values = new Object[type.getNumberOfProperties()];
    }
    
    /**
     * Gets internal impl class of {@link DynamicType}
     * 
     * @return Dynamic type of this entity
     * @throws DynamicException if type is null
     */
    public DynamicTypeImpl getType() throws DynamicException {
        if (this.type == null) {
            throw DynamicException.entityHasNullType(this);
        }
        return this.type;
    }

    //DynamicEntity API
    @SuppressWarnings("unchecked")
    public <T> T get(String propertyName) {
        DatabaseMapping mapping = getType().getMapping(propertyName);
        Object value = mapping.getAttributeValueFromObject(this);
        if (mapping.isForeignReferenceMapping() && mapping.isLazy()) {
            // Force basic indirection to be instantiated
            if (value instanceof ValueHolderInterface) {
                value = ((ValueHolderInterface) value).getValue();
            }
            // Force transparent indirection to be instantiated
            if (value instanceof IndirectContainer) {
                ((IndirectContainer) value).getValueHolder().getValue();
            }
        }
        try {
            return (T) value;
        }
        catch (ClassCastException cce) {
            throw DynamicException.invalidGetPropertyType(mapping, cce);
        }
    }

    public DynamicEntity set(String propertyName, Object value) {
        DatabaseMapping mapping = getType().getMapping(propertyName);
        checkSetType(mapping, value);
        Object oldValue = UNKNOWN_VALUE;
        Object currentValue = mapping.getAttributeValueFromObject(this);
        if (currentValue instanceof ValueHolderInterface) {
            ValueHolderInterface vh = (ValueHolderInterface)currentValue;
            if (vh.isInstantiated()) {
                oldValue = vh.getValue();
            }
            vh.setValue(value);
        }
        else {
            oldValue = currentValue;
            mapping.setAttributeValueInObject(this, value);
            PropertyChangeListener pcl = _persistence_getPropertyChangeListener();
            if (pcl != null) {
                pcl.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, value));
            }
        }
        return this;
    }

    /**
     * Ensure the value being set is supported by the mapping. If the mapping is
     * direct/basic and the mapping's type is primitive ensure the non-primitive
     * type is allowed.
     */
    protected void checkSetType(DatabaseMapping mapping, Object value) {
        if (value == null) {
            if (mapping.isCollectionMapping() || 
                (mapping.getAttributeClassification() != null && 
                 mapping.getAttributeClassification().isPrimitive())) {
                throw DynamicException.invalidSetPropertyType(mapping, value);
            }
            return;
        }
        Class<?> expectedType = mapping.getAttributeClassification();
        if (mapping.isForeignReferenceMapping()) {
            if (mapping.isCollectionMapping()) {
                if (((CollectionMapping) mapping).getContainerPolicy().isMapPolicy()) {
                    expectedType = Map.class;
                } else {
                    expectedType = Collection.class;
                }
            } else {
                expectedType = ((ForeignReferenceMapping) mapping).getReferenceClass();
            }
        }
        if (expectedType != null && expectedType.isPrimitive() && !value.getClass().isPrimitive()) {
            expectedType = Helper.getObjectClass(expectedType);
        }
        if (expectedType != null && !expectedType.isAssignableFrom(value.getClass())) {
            throw DynamicException.invalidSetPropertyType(mapping, value);
        }
    }

    public boolean isSet(DatabaseMapping mapping) {
        ValuesAccessor accessor = (ValuesAccessor) mapping.getAttributeAccessor();
        return accessor.isSet(this);
    }
    public boolean isSet(String propertyName) {
        return isSet(getType().getMapping(propertyName));
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
        throw new UnsupportedOperationException("DynamicEntityImpl._persistence_resetFetchGroup:: NOT SUPPORTED");
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