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

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * ValueAccessor is a specialized AttributeAccessor enabling usage of the
 * {@link DynamicEntityImpl#values} (Object[]) instead of a field/property
 * access available in static domain classes.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class ValuesAccessor extends AttributeAccessor {

    /**
     * NULL_VALUE is a singleton value used to indicate that a null value was
     * explicitly put in a given 'slot' and not just there due to the default
     * creation of an Object[].
     */
    protected final static Object NULL_VALUE = new Object();

    protected DatabaseMapping mapping;

    /**
     * {@link DynamicType} used to reset Object[] size when needed.
     */
    protected DynamicType type;

    /**
     * Index in the values Object[] where the owning mapping's value is stored.
     * This index assumes that the mappings remain in a static order.
     */
    protected int index;

    public ValuesAccessor(DynamicType type, DatabaseMapping mapping, int index) {
        super();
        this.type = type;
        this.mapping = mapping;
        this.index = index;
    }

    public DatabaseMapping getMapping() {
        return this.mapping;
    }

    public int getIndex() {
        return this.index;
    }

    public DynamicType getType() {
        return this.type;
    }

    /**
     * Access the Object[] from the {@link DynamicEntity}.
     * <p>
     * If the length of the array is incorrect this is where it will be lazily
     * fixed.
     */
    private Object[] getValues(Object entity) {
        Object[] values = ((DynamicEntityImpl) entity).values;

        if (getIndex() >= values.length) {
            Object[] newValues = new Object[getType().getNumberOfProperties()];
            System.arraycopy(values, 0, newValues, 0, values.length);
            ((DynamicEntityImpl) entity).values = newValues;
            values = newValues;
        }

        return values;
    }

    /**
     * <b>INTERNAL</b>: Direct access to the value in the Object[] for this
     * mapping. This method is provided for advanced users and can provide
     * direct access to he NULL_VALUE. All application access should be done
     * using the {@link DynamicEntity} get/set API.
     */
    public Object getRawValue(Object entity) {
        return getValues(entity)[getIndex()];
    }

    public Object getAttributeValueFromObject(Object entity) throws DescriptorException {
        Object value = getRawValue(entity);

        return value == NULL_VALUE ? null : value;
    }

    /**
     * <b>INTERNAL</b>: Direct access to the value in the Object[] for this
     * mapping. This method is provided for advanced users and BYPASSES THE USE
     * OF NULL_VALUE. All application access should be done using the
     * {@link DynamicEntity} API
     */
    public void setRawValue(Object entity, Object value) {
        getValues(entity)[getIndex()] = value;
    }

    public void setAttributeValueInObject(Object entity, Object value) throws DescriptorException {
        setRawValue(entity, value == null ? NULL_VALUE : value);
    }

    protected boolean isSet(Object entity) throws DescriptorException {
        Object[] values = getValues(entity);
        Object value = values[getIndex()];
        // check for LAZY - only set if {Indirect}.isInstantiated
        if (value instanceof IndirectContainer) {
            return ((IndirectContainer)value).isInstantiated();
        }
        if (value instanceof ValueHolderInterface) {
            return ((ValueHolderInterface)value).isInstantiated();
        }
        return value != null || value == NULL_VALUE;
    }

    @Override
    public Class<?> getAttributeClass() {
        if (getMapping().isForeignReferenceMapping()) {
            ForeignReferenceMapping refMapping = (ForeignReferenceMapping) getMapping();

            if (refMapping.isCollectionMapping()) {
                return ((CollectionMapping) refMapping).getContainerPolicy().getContainerClass();
            }
            if (refMapping.usesIndirection()) {
                return ValueHolderInterface.class;
            }
            return refMapping.getReferenceClass();
        } else {
            if (getMapping().getAttributeClassification() == null) {
                return ClassConstants.OBJECT;
            }
            return getMapping().getAttributeClassification();
        }
    }
}
