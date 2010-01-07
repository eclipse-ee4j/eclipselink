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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.sequenced.Setting;

/**
 * <p><b>Purpose</b>: A helper class for checking deep or shallow equality of DataObjects.</p>
 * <p>ChangeSummary is not in scope for equality checking.
 *
 * @see org.eclipse.persistence.sdo.SDODataObject
 * @since Oracle TopLink 11.1.1.0.0
 *
 */
public class SDOEqualityHelper implements EqualityHelper {

    /*
    References:
        SDO59-DeepCopy.doc
        SDO_Ref_BiDir_Relationships_DesignSpec.doc
        http://files.oraclecorp.com/content/MySharedFolders/ST%20Functional%20Specs/AS11gR1/TopLink/SDO/SDO_Ref_BiDir_Relationships_DesignSpec.doc

     09/12/06 - Add bidirectional property support in compareProperty()
     09/18/06 - remove shallow opposite property check - just return true if both objects are set and are DO's
     01/29/07 - #5852525 handle null properties with isSet=true
     04/11/07 - Implement Sequence functionality
    */

    /** hold the context containing all helpers so that we can preserve inter-helper relationships */
    private HelperContext aHelperContext;

    /**
     * INTERNAL:
     * This default constructor must be used in conjunction with the setHelperContext() function.
     * The custom constructor that takes a HelperContext parameter is recommended over this default constructor.
     */
    public SDOEqualityHelper() {
    }

    /**
     * Constructor that takes in a HelperContext instance that contains this equalityHelper.<br/>
     * This is the recommended constructor.
     * @param aContext
     */
    public SDOEqualityHelper(HelperContext aContext) {
        aHelperContext = aContext;
    }

    /**
     * <p>Two DataObjects are equalShallow if
     *  they have the same {@link DataObject#getType Type}
     *  and all their compared Properties are equal.
     *  The set of Properties compared are the
     *  {@link DataObject#getInstanceProperties() instance properties}
     *  where property.getType().isDataType() is true
     *  and property.getType() is not ChangeSummaryType.
     * <br/>Two of these Property values are equal if they are both not
     *  {@link DataObject#isSet(Property) set}, or set to an equal value
     *  dataObject1.get(property).equals(dataObject2.get(property))
     * <br/>If the type is a sequenced type, the sequence entries must be the same.
     *  For each entry x in the sequence where the property is used in the comparison,
     *  dataObject1.getSequence().getValue(x).equals(
     *   dataObject2.getSequence().getValue(x)) and
     *  dataObject1.getSequence().getProperty(x) ==
     *   dataObject2.getSequence().getProperty(x)
     *  must be true.
     * </p>
     *  Returns true the objects have the same Type and all values of all compared Properties are equal.
     * @param dataObject1 DataObject to be compared
     * @param dataObject2 DataObject to be compared
     * @return true the objects have the same Type and all values of all compared Properties are equal.
     */
    public boolean equalShallow(DataObject dataObject1, DataObject dataObject2) {
        return compareDataObjects(dataObject1, dataObject2, false);
    }

    /**
     * <p>Two DataObjects are equal(Deep) if they are equalShallow,
     *  all their compared Properties are equal, and all reachable DataObjects in their
     *  graphs excluding containers are equal.
     *  The set of Properties compared are the
     *  {@link DataObject#getInstanceProperties() instance properties}
     *  where property.getType().isDataType() is false,
     *  and is not a container property, ie !property.getOpposite().isContainment()
     * <br/>Two of these Property values are equal if they are both not
     *  {@link DataObject#isSet(Property) set}, or all the DataObjects
     *  they refer to are {@link #equal(DataObject, DataObject) equal} in the
     *  context of dataObject1 and dataObject2.
     * <br/>Note that properties to a containing DataObject are not compared
     *  which means two DataObject trees can be equal even if their containers are not equal.
     * <br/>If the type is a sequenced type, the sequence entries must be the same.
     *  For each entry x in the sequence where the property is used in the comparison,
     *  equal(dataObject1.getSequence().getValue(x),
     *   dataObject2.getSequence().getValue(x)) and
     *  dataObject1.getSequence().getProperty(x) ==  dataObject2.getSequence().getProperty(x)
     *  must be true.
     * </p><p>
     * A DataObject directly or indirectly referenced by dataObject1 or dataObject2
     *  can only be equal to exactly one DataObject directly or indirectly referenced
     *  by dataObject1 or dataObject2, respectively.
     *  This ensures that dataObject1 and dataObject2 are equal if the graph formed by
     *  all their referenced DataObjects have the same shape.
     * </p>
     *  Returns true if the trees of DataObjects are equal(Deep).
     * @param dataObject1 DataObject to be compared
     * @param dataObject2 DataObject to be compared
     * @return true if the trees of DataObjects are equal(Deep).
     */
    public boolean equal(DataObject dataObject1, DataObject dataObject2) {
        return compareDataObjects(dataObject1, dataObject2, true);
    }

    /**
     * INTERNAL:
     * Separately, checks the declared properties and open content properties.
     * @param dataObject1   the DataObject to be compared
     * @param dataObject2   the DataObject to be compared
     * @param isDeep          if comparison is deep
     * @return              true if two DataObjects meet requirements of shallow equal or deep equal
     */
    private boolean compareDataObjects(DataObject dataObject1, DataObject dataObject2, boolean isDeep) {
        // Note that properties to a containing DataObject are not compared which
        // means two DataObject trees can be equal even if their containers are not equal.

        if (null == dataObject1) {
            return dataObject2 == null;
        }
        if (null == dataObject2) {// dataObject1 is not null while dataObject2 is null
            return false;
        }

        // they don't have the same type !! assumption is the same for now, but may be changed to use equals  !!
        if (dataObject1.getType() != dataObject2.getType()) {
            return false;
        } else {// type is the same for them

            /**
             * We have 2 strategies here - sequences or dataObjects first?
             * Analysis: A sequence and its dataObject share a subset of element properties.
             * The disjoint set for sequences includes the set of unstructured text settings.
             * The disjoint set for dataObject includes all attribute properties.
             *
             * The choice of which to do first comes down to performance
             */
            /**
             * Compare sequences - only out of order settings and unstructured
             * text will not be picked up by a dataObject comparison.
             * There is no need to check sequenced state on both objects because they share the same SDOType instance.
             */
            if (dataObject1.getType().isSequenced()) {
                if (!compareSequences((SDOSequence) dataObject1.getSequence(), (SDOSequence) dataObject2.getSequence(), isDeep)) {
                    return false;
                }
            }

            // First, compare properties that are not open content.
            // Attribute property differences will not be picked up in the sequence comparison
            if (!compare(dataObject1, dataObject2, isDeep, dataObject1.getType().getProperties())) {
                return false;
            }

            // Second, compare open content properties
            List properties_1 = ((SDODataObject)dataObject1)._getOpenContentProperties();
            List properties_2 = ((SDODataObject)dataObject2)._getOpenContentProperties();

            // different size of open content properties
            if ((properties_1.size() != properties_2.size()) || !properties_1.containsAll(properties_2)) {
                return false;
            }
            if (!compare(dataObject1, dataObject2, isDeep, properties_1)) {
                return false;
            }

            List attrProperties_1 = ((SDODataObject)dataObject1)._getOpenContentPropertiesAttributes();
            List attrProperties_2 = ((SDODataObject)dataObject2)._getOpenContentPropertiesAttributes();

            // different size of open content properties
            if ((attrProperties_1.size() != attrProperties_2.size()) || !attrProperties_1.containsAll(attrProperties_2)) {
                return false;
            }
            if (!compare(dataObject1, dataObject2, isDeep, attrProperties_1)) {
                return false;
            }

            return true;
        }
    }

    /**
     * INTERNAL: Return whether the 2 sequences are equal.
     * Element properties and unstructured text will be compared - attributes are out of scope.
     * <p>
     * For shallow equal - only dataType=true objects are compared, DataObject values are ignored but should be defaults.
     * Note: A setting object should handle its own isEqual() behavior
     *
     * @param aSequence
     * @param aSequenceCopy
     * @param isDeep
     * @return
     */
    private boolean compareSequences(SDOSequence aSequence, SDOSequence aSequenceCopy, boolean isDeep) {
        // corner case: isSequenced set to true after type definition had not sequence
        if ((null == aSequence) && (null == aSequenceCopy)) {
            return true;
        }

        // both sequences must be null 
        if ((null == aSequence) || (null == aSequenceCopy)) {
            return false;
        }

        // for shallow equal - match whether we skipped creating settings or set value=null for shallow copies
        if (isDeep && aSequence.size() != aSequenceCopy.size()) {
            return false;
        }
        // the settings inside the sequence must be new objects        
        List originalSettingsList = aSequence.getSettings();
        List copySettingsList = aSequenceCopy.getSettings();
        if ((null == originalSettingsList) || (null == copySettingsList)) {
            return false;
        }

        SDOProperty originalProperty = null;
        SDOProperty copyProperty = null;

        /**
         * For shallow equal when dataType is false we do not check this setting,
         * the value will be unset (default value) in the shallow copy.
         * orig                  v1=String  v2=DataObject   v3=String
         * shallowcopy  v1=String  v2=null(default)  v3=String
         * deepcopy       v1=String  v2=DataObject    v3=String
         */
        if (isDeep) {
            for (int index = 0, size = aSequence.size(); index < size; index++) {
                originalProperty = aSequence.getProperty((Setting) originalSettingsList.get(index));
                copyProperty = aSequenceCopy.getProperty((Setting) copySettingsList.get(index));

                // we must handle null properties that represent unstructured text
                // both null = unstructured
                // one null = invalid state (return not equal)
                // both !null = valid state (check equality)
                if (((null == originalProperty) && (null != copyProperty)) || ((null != originalProperty) && (null == copyProperty))) {
                    return false;
                }

                // the property field on the setting must point to the same property instance as the original
                if (!arePropertiesEqual(originalProperty, copyProperty)) {// handle both properties == null
                    return false;
                }

                Object originalValue = aSequence.getValue(index);
                Object copyValue = aSequenceCopy.getValue(index);

                // for unstructuredText (null property) and simple dataTypes we check equality directly
                if ((null == originalProperty) || originalProperty.getType().isDataType()) {
                    // if one of the values is null return false
                    if (((null == originalValue) && (null != copyValue)) || //
                            ((null != originalValue) && (null == copyValue))) {
                        return false;
                    }
                    // if both values are null - they are equal
                    if ((null != originalValue) && !originalValue.equals(copyValue)) {// we can also use !.equals()
                        return false;
                    }
                } else {
                    // For complex types
                    // we do not need to check deep equality on dataObjects twice here, just check instances
                    // because the dataObject compare will iterate all the properties of each dataObject
                    // only compare DataObjects when in a  deep equal
                    if (isDeep) {
                        if ((null != originalValue) && (null != copyValue)) {
                            // setting.isSet is ignored for sequences
                            // perform a deep equal on the single item
                            // the values may not match their types - return false instead of a CCE
                            if (originalValue instanceof DataObject && copyValue instanceof DataObject) {
                                if (!equal((DataObject) originalValue, (DataObject) copyValue)) {
                                    return false;
                                }
                            } else if (originalValue instanceof XMLRoot && copyValue instanceof XMLRoot) {
                                XMLRoot originalXMLRoot = (XMLRoot) originalValue;
                                XMLRoot copyXMLRoot = (XMLRoot) copyValue;
                                // compare local names of XMLRoot objects
                                if (!originalXMLRoot.getLocalName().equals(copyXMLRoot.getLocalName())) {
                                    return false;
                                }
                                // compare uris of XMLRoot objects
                                if (!originalXMLRoot.getNamespaceURI().equals(copyXMLRoot.getNamespaceURI())) {
                                    return false;
                                }
                                
                                Object originalUnwrappedValue = (originalXMLRoot).getObject();
                                Object copyUnwrappedValue = (copyXMLRoot).getObject();
                                if (originalUnwrappedValue instanceof DataObject && copyUnwrappedValue instanceof DataObject) {
                                    if (!equal((DataObject) originalUnwrappedValue, (DataObject) copyUnwrappedValue)) {
                                        return false;
                                    }
                                }
                            } else {
                                return false;
                            }
                        } else {
                            // both values must be null to be equal
                            if (((null == originalValue) && (null != copyValue)) || ((null == copyValue) && (null != originalValue))) {
                                return false;
                            }
                        }
                    } else {
                        // For DataObjects in general anything that is deep equal is also shallow equal - but not the reverse.
                        // In the case of shallow equal on sequences.  We can ignore the state of the 2 complex objects.
                        //   UC1: if aSequenceCopy setting was from a shallowCopy then it will be unset.
                        //   UC2: if aSequenceCopy setting was from a deepCopy or a reversed argument shallowCopy then it may be unset or set.
                        // We will not check for a default value on either sequence setting.
                    }
                }
            }
        } else {
            int cpyIdx = 0;
            boolean locatedSetting;
            // compare settings
            for (int idx = 0; idx < aSequence.getSettings().size(); idx++) {
                SDOProperty nextProperty = aSequence.getProperty(idx);
                if (nextProperty == null || nextProperty.getType().isDataType()) {
                    // compare to the next copy datatype setting
                    Object nextValue = aSequence.getValue(idx);
                    locatedSetting = false;
                    for (; cpyIdx < aSequenceCopy.getSettings().size(); cpyIdx++) {
                        SDOProperty nextCopyProperty = aSequenceCopy.getProperty(cpyIdx);
                        if (nextCopyProperty == null || nextCopyProperty.getType().isDataType()) {
                            // at this point we need to compare the two Settings and their properties
                            Object nextCopyValue = aSequenceCopy.getValue(cpyIdx);
                            if (nextValue == nextCopyValue && arePropertiesEqual(nextProperty, nextCopyProperty)) {
                                locatedSetting = true;
                            }
                            cpyIdx++;
                            break;
                        }
                    }
                    if (!locatedSetting) {
                        return false;
                    }
                }
            }
            // at this point there should not be any more copy settings
            if (getIndexOfNextDataTypeSetting(aSequenceCopy, cpyIdx) != -1) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * INTERNAL:
     * Convenience method for returning the index of the next DataType 
     * Setting in a given sequence.
     * 
     * @param aSequence
     * @param index
     * @return the next Setting after index in the sequence, or -1 if none 
     */
    private int getIndexOfNextDataTypeSetting(SDOSequence aSequence, int index) {
        List<Setting> settings = aSequence.getSettings();
        for (int i = index; i < settings.size(); i++) {
            SDOProperty nextProperty = aSequence.getProperty(i);
            if (nextProperty == null || nextProperty.getType().isDataType()) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * INTERNAL:
     * Convenience method that compares two Property objects for equality
     * @param prop1
     * @param prop2
     * @return
     */
    private boolean arePropertiesEqual(Property prop1, Property prop2) {
        if (((null == prop1) && (null != prop2)) || ((null != prop1) && (null == prop2))) {
            return false;
        }
        // the property field on the setting must point to the same property instance as the original
        if (!prop1.equals(prop2)) {
            return false;
        }
        return true;
    }
    
    /**
     * INTERNAL:
     * iterativly, compare the values of shared properties in two target DataObjects
     * @param dataObject1   the DataObject to be compared
     * @param dataObject2   the DataObject to be compared
     * @param isDeep          if comparison is deep
     * @param properties     list of properties shared by two DataObjects
     * @return                     true if two DataObjects meet requirements of shallow equal or deep equal
     */
    private boolean compare(DataObject dataObject1, DataObject dataObject2, boolean isDeep, List properties) {
        Iterator iterProperties = properties.iterator();

        while (iterProperties.hasNext()) {
            // !! assumption is two dataobjects share the same property    !!
            SDOProperty p = (SDOProperty)iterProperties.next();
            if (!compareProperty(dataObject1, dataObject2, isDeep, p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Recursively [PreOrder sequence] compare corresponding properties of 2 DataObjects
     * check value of property p when p is not a many type property.
     * @param dataObject1   the DataObject to be compared
     * @param dataObject2   the DataObject to be compared
     * @param isDeep          if comparison is deep
     * @param p             the property shared by two DataObjects
     * @return              true if two DataObjects meet requirements of shallow equal or deep equal
     */
    private boolean compareProperty(DataObject dataObject1, DataObject dataObject2, boolean isDeep, SDOProperty p) {
        if (p.isMany()) {
            return compareManyProperty(dataObject1, dataObject2, isDeep, p);
        }

        // base step
        if (p.getType().isDataType() && !p.getType().isChangeSummaryType()) {
            boolean isSet1 = dataObject1.isSet(p);
            boolean isSet2 = dataObject2.isSet(p);
            if (!isSet1 && !isSet2) {
                return true;
            }
            if (isSet1 && isSet2) {
                Object aProperty1 = dataObject1.get(p);
                Object aProperty2 = dataObject2.get(p);

                // if both properties are null then return equality
                if (null == aProperty1) {
                    return aProperty2 == null;
                }
                if (null == aProperty2) {// aProperty1 is not null while aPropertyt2 is null
                    return false;
                }

                return aProperty1.equals(aProperty2);
            }
            return false;
        }

        // recursive step
        if (isDeep && !p.getType().isChangeSummaryType()) {
            if (!dataObject1.isSet(p) && !dataObject2.isSet(p)) {
                return true;
            }

            // #5852525 handle null properties with isSet=true
            if (!p.getType().isDataType()) {
                if (null == p.getOpposite()) {
                    // compare unidirectional or containment=true properties - recursively
                    return compareDataObjects(dataObject1.getDataObject(p), dataObject2.getDataObject(p), isDeep);
                } else {
                    // 20060906: handle bidirectional properties 
                    // the check across to another branch in the tree will only go 1 shallow level deep
                    // avoiding an infinite recursive loop and deferring the check for that branch when it
                    // is encountered in its PreOrder sequence.                	
                    return compareDataObjects(dataObject1.getDataObject(p), dataObject2.getDataObject(p), false);

                    // Spec 3.10 "All reachable DOs in their graphs are equal"
                    // return true since both do1.get(p) and do2.get(p) are set and are instances of DO
                    //return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * INTERNAL:
     * iteratively check value of property p when p is many type property.
     * @param dataObject1   the DataObject to be compared
     * @param dataObject2   the DataObject to be compared
     * @param isShallow     if comparison is shallow
     * @param p             the property shared by two DataObjects
     * @return              true if two DataObjects meet requirements of shallow equal or deep equal
     */
    private boolean compareManyProperty(DataObject dataObject1, DataObject dataObject2, boolean isDeep, Property p) {
        List l1 = dataObject1.getList(p);
        List l2 = dataObject2.getList(p);

        if (((SDOType)p.getType()).isDataType()) {
            if (dataObject1.isSet(p) != dataObject2.isSet(p)) {
                return false;
            }
            if (l1.size() != l2.size()) {
                return false;
            }
            for (int i = 0; i < l1.size(); i++) {
                Object o1 = l1.get(i);
                Object o2 = l2.get(i);
                if (!o1.equals(o2)) {
                    return false;
                }
            }
            return true;
        }
        if (isDeep) {
            if (dataObject1.isSet(p) != dataObject2.isSet(p)) {
                return false;
            }
            if (l1.size() != l2.size()) {
                return false;
            }

            // !! can a list contains duplicated objects: {A, A'} and A equals A' or {A, A}
            for (int i = 0, size = l1.size(); i < size; i++) {
                DataObject o1_l1 = (DataObject)l1.get(i);
                DataObject o2_l2 = (DataObject)l2.get(i);
                if (!isADataObjectInList(o1_l1, l2)) {
                    return false;
                }
                if (!isADataObjectInList(o2_l2, l1)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * @param dataObject1
     * @param objects
     * @return
     */
    private boolean isADataObjectInList(DataObject dataObject1, List objects) {
        Iterator iterObjects = objects.iterator();
        while (iterObjects.hasNext()) {
            DataObject dataObject2 = (DataObject)iterObjects.next();
            if (compareDataObjects(dataObject1, dataObject2, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return the helperContext containing this equalityHelper.
     * @return
     */
    public HelperContext getHelperContext() {
        if(null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    /**
     * INTERNAL:
     * Set the helperContext if this equalityHelper was created using the default constructor.
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
