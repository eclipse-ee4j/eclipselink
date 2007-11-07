/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>Purpose</b>:Default implementation of the ValueStore interface.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Provide get/set/isset/unset access to the values of a DataObject
 * <li> Store the values of the declared and open content propeties in memory
 * </ul>
 */
public class DefaultValueStore implements ValueStore {
    private Map openContentValues;//open content values keyed on real prop name   
    private Object[] typePropertyValues;
	/** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private boolean[] typePropertiesIsSetStatus;
    private DataObject dataObject;

    public DefaultValueStore() {
    }

    /**
      * Get declared property by index.<br>
      * Returns the value of the given property of this object.<br>
      * If the property is {@link Property#isMany many-valued},
      * the result will be a {@link org.eclipse.persistence.sdo.helper.ListWrapper}
      * and each object in the List will be {@link Type#isInstance an instance of}
      * the property's {@link Property#getType type}.
      * Otherwise the result will directly be an instance of the property's type.
      * @param propertyIndex the property index of the value to fetch.
      * @return the value of the given property of the object.
      */
    public Object getDeclaredProperty(int propertyIndex) {
        if (typePropertyValues != null) {
            return typePropertyValues[propertyIndex];
        }
        return null;
    }

    /**
     * Get open-content property by name.<br>
     * Returns the value of the given property of this object.<br>
     * If the property is {@link Property#isMany many-valued},
     * the result will be a {@link org.eclipse.persistence.sdo.helper.ListWrapper}
     * and each object in the List will be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type}.
     * Otherwise the result will directly be an instance of the property's type.
     * @param property the property to fetch the value of.
     * @return the value of the given property of the object.
     */
    public Object getOpenContentProperty(Property property) {
        return getOpenContentValues().get(property);
    }

    /**
      * Set declared property by index.<br>
      * Sets the value of the given property of the object to the new value.
      * <p>
      * If the property is {@link Property#isMany many-valued},
      * the new value must be a {@link java.util.List}
      * and each object in that list must be {@link Type#isInstance an instance of}
      * the property's {@link Property#getType type};
      * the existing contents are cleared and the contents of the new value are added.
      * Otherwise the new value directly must be an instance of the property's type
      * and it becomes the new value of the property of the object.
      * @param propertyIndex the property name of the value to set.
      * @param value the new value for the property.s
      */
    public void setDeclaredProperty(int propertyIndex, Object value) {
        getTypePropertyValues()[propertyIndex] = value;
        getTypePropertiesIsSetStatus()[propertyIndex] = true;
    }

    /**
      * Set open-content property by name.<br>
      * Sets the value of the given property of the object to the new value.
      * <p>
      * If the property is {@link Property#isMany many-valued},
      * the new value must be a {@link java.util.List}
      * and each object in that list must be {@link Type#isInstance an instance of}
      * the property's {@link Property#getType type};
      * the existing contents are cleared and the contents of the new value are added.
      * Otherwise the new value directly must be an instance of the property's type
      * and it becomes the new value of the property of the object.
     * @param property the property to be set.
      * @param value the new value for the property.
      */
    public void setOpenContentProperty(Property property, Object value) {    
        getOpenContentValues().put(property, value);
    }

    /**
     * Get isSet boolean status for declared property by index.<br>
     * Returns whether the property of the object is considered to be set.
     * <p>
     * isSet() for many-valued Properties returns true if the List is not empty and
     * false if the List is empty.  For single-valued Properties:
     * <ul><li>If the Property has not been set() or has been unset() then isSet() returns false.</li>
     * <li>If the current value is not the Property's default or null, isSet() returns true.</li>
     * <li>For the remaining cases the implementation may decide between two policies: </li>
     * <ol><li>any call to set() without a call to unset() will cause isSet() to return true, or </li>
     *   <li>the current value is compared to the default value and isSet() returns true when they differ.</li>
     * </ol></ul><p>
     * @param propertyIndex the property index in question.
     * @return whether the property of the object is set.
     */
    public boolean isSetDeclaredProperty(int propertyIndex) {
        return getTypePropertiesIsSetStatus()[propertyIndex];
    }

    /**
      * Get isSet boolean status for open-content property by name.<br>
      * Returns whether the property of the object is considered to be set.
      * <p>
      * isSet() for many-valued Properties returns true if the List is not empty and
      * false if the List is empty.  For single-valued Properties:
      * <ul><li>If the Property has not been set() or has been unset() then isSet() returns false.</li>
      * <li>If the current value is not the Property's default or null, isSet() returns true.</li>
      * <li>For the remaining cases the implementation may decide between two policies: </li>
      * <ol><li>any call to set() without a call to unset() will cause isSet() to return true, or </li>
      *   <li>the current value is compared to the default value and isSet() returns true when they differ.</li>
      * </ol></ul><p>
     * @param property the property in question.
      * @return whether the property of the object is set.
      */
    public boolean isSetOpenContentProperty(Property property) {
        return getOpenContentValues().containsKey(property);
    }

    /**
       * Unset declared property by index position
       * <p>
       * If the property is {@link Property#isMany many-valued},
       * the value must be an {@link java.util.List}
       * and that list is cleared.
       * Otherwise, the value of the property of the object
       * is set to the property's {@link Property#getDefault default value}.
       * The property will no longer be considered {@link #isSet set}.
       * @see #isSetDeclaredProperty(int)
       * @see #setDeclaredProperty(int, Object)
       * @see #getDeclaredProperty(int)
       * @param propertyIndex
       * @return void
       */
    public void unsetDeclaredProperty(int propertyIndex) {
        Property prop = ((SDODataObject)dataObject).getInstanceProperty(propertyIndex);
        if (!prop.isMany()) {
            getTypePropertyValues()[propertyIndex] = null;
        }
        getTypePropertiesIsSetStatus()[propertyIndex] = false;
    }

    /**
     * Unset open-content property by name
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the value must be an {@link java.util.List}
     * and that list is cleared.
     * Otherwise, the value of the property of the object
     * is set to the property's {@link Property#getDefault default value}.
     * The property will no longer be considered {@link #isSet set}.
     * @see #isSetDeclaredProperty(int)
     * @see #setDeclaredProperty(int, Object)
     * @see #getDeclaredProperty(int)
     * @param property
     * @return void
     */
    public void unsetOpenContentProperty(Property property) {    
        getOpenContentValues().remove(property);
    }

    /**
      * Perform any post-instantiation integrity operations that could not be done during
      * ValueStore creation.<br>
      * Since the dataObject reference passed in may be bidirectional or self-referencing
      * - we cannot set this variable until the dataObject itself is finished instantiation
      * - hence the 2-step initialization.
      *
      * @param dataObject
      */
    public void initialize(DataObject aDataObject) {
        dataObject = aDataObject;
        setTypePropertiesIsSetStatus(new boolean[aDataObject.getType().getProperties().size()]);
        setTypePropertyValues(new Object[aDataObject.getType().getProperties().size()]);
    }

    /**
     * Set the values for declared properties
     * @param typePropertyValuesArray
     */
    public void setTypePropertyValues(Object[] typePropertyValuesArray) {
        typePropertyValues = typePropertyValuesArray;
    }

    /**
     * INTERNAL:
     * @param typePropertiesIsSetStatusArray  boolean[] of isSet values for declared properties
     */
    public void setTypePropertiesIsSetStatus(boolean[] typePropertiesIsSetStatusArray) {
        typePropertiesIsSetStatus = typePropertiesIsSetStatusArray;
    }

    /**
     * INTERNAL:
     * @return Object[] of the values of declared properties
     */
    public Object[] getTypePropertyValues() {
        return typePropertyValues;
    }

    /**
     * INTERNAL:
     * @return boolean[] of isSet values for declared properties
     */
    public boolean[] getTypePropertiesIsSetStatus() {
        return typePropertiesIsSetStatus;
    }

    /**
     * INTERNAL:
     * @param Map openContentValues
     */
    public void setOpenContentValues(Map openContentValues) {
        this.openContentValues = openContentValues;
    }

    /**
     * INTERNAL:
     * @return Map of values for open content properties
     */
    public Map getOpenContentValues() {
        if (openContentValues == null) {
            openContentValues = new HashMap();
        }
        return openContentValues;
    }

    //  Do not implement this function unless the valueStore handles its own object wrapping
    public void setManyProperty(Property property, Object value) {
    }

    /**
      * Get a shallow copy of the original ValueStore.
      * Changes made to the copy must not impact the original ValueStore
      * @return ValueStore
      */
    public ValueStore copy() {

        /**
         * Implementer: SDODataObject.resetChanges()
         */
        DefaultValueStore anOriginalValueStore = new DefaultValueStore();
        anOriginalValueStore.dataObject = dataObject;

        boolean[] currentIsSet = getTypePropertiesIsSetStatus();
        boolean[] isSetCopy = new boolean[currentIsSet.length];
        System.arraycopy(currentIsSet, 0, isSetCopy, 0, currentIsSet.length);

        Object[] currentValues = getTypePropertyValues();
        Object[] valueCopy = new Object[currentValues.length];
        System.arraycopy(currentValues, 0, valueCopy, 0, currentValues.length);
        // shallow copy isSet boolean
        anOriginalValueStore.setTypePropertiesIsSetStatus(isSetCopy);
        // shallow copy the object values
        anOriginalValueStore.setTypePropertyValues(valueCopy);

        //TODO: open content
        //Map clonedMap = (Map)getOpenContentValues().clone();
        HashMap clonedMap = new HashMap();

        /**
         * Note: if the oc Map is null we will return an empty map
         * this empty Map will be set on the copy but the original will still actually have a null Map
         * this is ok because any get on the original will create a new Map and we will
         * not have object equality between copy and original oc maps
         * Just be aware that the two openContentValues will not pass value equility unless
         * the public get function is used
         */
        clonedMap.putAll(getOpenContentValues());
        // shallow copy oc values 
        anOriginalValueStore.setOpenContentValues(clonedMap);

        return anOriginalValueStore;
    }
}