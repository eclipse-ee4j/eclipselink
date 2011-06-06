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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.util.HashMap;
import java.util.Iterator;
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

    public Object getDeclaredProperty(int propertyIndex) {
        if (typePropertyValues != null) {
            return typePropertyValues[propertyIndex];
        }
        return null;
    }

    public Object getOpenContentProperty(Property property) {
        return getOpenContentValues().get(property);
    }

    public void setDeclaredProperty(int propertyIndex, Object value) {
        getTypePropertyValues()[propertyIndex] = value;
        getTypePropertiesIsSetStatus()[propertyIndex] = true;
    }

    public void setOpenContentProperty(Property property, Object value) {    
        getOpenContentValues().put(property, value);
    }

    public boolean isSetDeclaredProperty(int propertyIndex) {
        return getTypePropertiesIsSetStatus()[propertyIndex];
    }

    public boolean isSetOpenContentProperty(Property property) {
        return getOpenContentValues().containsKey(property);
    }

    public void unsetDeclaredProperty(int propertyIndex) {
        Property prop = ((SDODataObject)dataObject).getInstanceProperty(propertyIndex);
        if (!prop.isMany()) {
            getTypePropertyValues()[propertyIndex] = null;
        }
        getTypePropertiesIsSetStatus()[propertyIndex] = false;
    }

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
    
    /**
     *  Indicates if a given ValueStore is equal to this.  The following 
     *  attributes are tested for equality:
     *      - data object
     *      - type property values
     *      - open content property values
     *      - property isSet values
     */
    public boolean equals(Object obj) {
        DefaultValueStore dvs;
        try {
            dvs = (DefaultValueStore) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        // Compare data object
        if (dvs.dataObject != this.dataObject) {
            return false;
        }
        // Compare declared properties and isSet status
        // All lists must be the same length
        if (dvs.getTypePropertyValues().length != this.getTypePropertyValues().length || 
                dvs.getTypePropertiesIsSetStatus().length != this.getTypePropertiesIsSetStatus().length) {
            return false;
        }
        for (int i=0; i<dvs.getTypePropertyValues().length; i++) {
            // isSet values must be equal
            if (dvs.isSetDeclaredProperty(i) != this.isSetDeclaredProperty(i)) {
                return false;
            }
            Object dvsPropVal  = dvs.getDeclaredProperty(i);
            Object thisPropVal = this.getDeclaredProperty(i);
            // Both values need to be null or non-null            
            if (dvsPropVal == null) {
                if (thisPropVal != null) {
                    return false;
                }
                // Here both are null so no need to check anything
            } else {
                if (!(dvsPropVal.equals(thisPropVal))) {
                    return false;
                }
            }
        }
        // Compare open content properties
        if (dvs.getOpenContentValues().size() != this.getOpenContentValues().size()) {
            return false;
        }
        Iterator<Property> keyIt = dvs.getOpenContentValues().keySet().iterator();
        while (keyIt.hasNext()) {
            Property key = keyIt.next();
            Object dvsOCVal  = dvs.getOpenContentProperty(key);
            Object thisOCVal = this.getOpenContentProperty(key);
            // Both values need to be null or non-null            
            if (dvsOCVal == null) {
                if (thisOCVal != null) {
                    return false;
                }
                // Here both are null so no need to check anything
            } else {
                if (!(dvsOCVal.equals(thisOCVal))) {
                    return false;
                }
            }
        }
        return true;
    }
}
