/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOSetting;

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
        // TODO: JIRA129 - default to static global context - Do Not use this convenience constructor outside of JUnit testing
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
                return compareSequences((SDOSequence)dataObject1.getSequence(), (SDOSequence)dataObject2.getSequence(), isDeep);
            }

            // First, compare properties that are not open content.
            // Attribute property differences will not be picked up in the sequence comparison
            boolean result = compare(dataObject1, dataObject2, isDeep, dataObject1.getType().getProperties());
            if (!result) {
                return false;
            }

            // Second, compare open content properties
            List properties_1 = ((SDODataObject)dataObject1)._getOpenContentProperties();
            List properties_2 = ((SDODataObject)dataObject2)._getOpenContentProperties();

            // different size of open content properties
            if ((properties_1.size() != properties_2.size()) || !properties_1.containsAll(properties_2)) {
                return false;
            }
            result = compare(dataObject1, dataObject2, isDeep, properties_1);
            if (!result) {
                return false;
            }

            List attrProperties_1 = ((SDODataObject)dataObject1)._getOpenContentPropertiesAttributes();
            List attrProperties_2 = ((SDODataObject)dataObject2)._getOpenContentPropertiesAttributes();

            // different size of open content properties
            if ((attrProperties_1.size() != attrProperties_2.size()) || !attrProperties_1.containsAll(attrProperties_2)) {
                return false;
            }
            result = compare(dataObject1, dataObject2, isDeep, attrProperties_1);
            if (!result) {
                return false;
            }

            return true;
        }

        // ToDo: the following meaning is still not sure !!
        // Note that properties to a containing DataObject are not compared
        // which
        // means two DataObject trees can be equal even if their containers are
        // not equal.
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
        if (aSequence.size() != aSequenceCopy.size()) {
            return false;
        }

        // the settings inside the sequence must be new objects
        SDOSetting originalSetting = null;
        SDOSetting copySetting = null;
        List originalSettingsList = aSequence.getSettings();
        List copySettingsList = aSequenceCopy.getSettings();
        if ((null == originalSettingsList) || (null == copySettingsList)) {
            return false;
        }

        Property originalProperty = null;
        Property copyProperty = null;

        /**
         * For shallow equal when dataType is false we do not check this setting,
         * the value will be unset (default value) in the shallow copy.
         * orig                  v1=String  v2=DataObject   v3=String
         * shallowcopy  v1=String  v2=null(default)  v3=String
         * deepcopy       v1=String  v2=DataObject    v3=String
         */
        for (int index = 0, size = aSequence.size(); index < size; index++) {
            originalSetting = (SDOSetting)originalSettingsList.get(index);
            copySetting = (SDOSetting)copySettingsList.get(index);

            originalProperty = originalSetting.getProperty();
            copyProperty = copySetting.getProperty();

            // we must handle null properties that represent unstructured text
            // both null = unstructured
            // one null = invalid state (return not equal)
            // both !null = valid state (check equality)
            if (((null == originalProperty) && (null != copyProperty)) || ((null != originalProperty) && (null == copyProperty))) {
                return false;
            }

            // the property field on the setting must point to the same property instance as the original
            if (originalProperty != copyProperty) {// handle both properties == null
                return false;
            }

            Object originalValue = originalSetting.getValue();
            Object copyValue = copySetting.getValue();

            // for unstructuredText (null property) and simple dataTypes we check equality directly
            if ((null == originalProperty) || originalProperty.getType().isDataType()) {
                // if one of the values is null return false
                if (((null == originalValue) && (null != copyValue)) ||//
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
                            if (!equal((DataObject)originalValue, (DataObject)copyValue)) {
                                return false;
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

                    /**
                     * For DataObjects in general anything that is deep equal is also shallow equal - but not the reverse.
                     * In the case of shallow equal on sequences.  We can ignore the state of the 2 complex objects.
                     *     UC1: if aSequenceCopy setting was from a shallowCopy then it will be unset.
                     *     UC2: if aSequenceCopy setting was from a deepCopy or a reversed argument shallowCopy then it may be unset or set.
                     * We will not check for a default value on either sequence setting.
                     */
                }
            }
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
            Property p = (Property)iterProperties.next();
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
    private boolean compareProperty(DataObject dataObject1, DataObject dataObject2, boolean isDeep, Property p) {
        if (p.isMany()) {
            return compareManyProperty(dataObject1, dataObject2, isDeep, p);
        }

        // base step
        if (p.getType().isDataType() && (p.getType() != SDOConstants.SDO_CHANGESUMMARY)) {
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
        if (isDeep && (p.getType() != SDOConstants.SDO_CHANGESUMMARY)) {
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
                    // TODO: process non-containment side of bidirectionals see #5853175                	
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

        if (p.getType().isDataType()) {
            if (dataObject1.isSet(p) != dataObject2.isSet(p)) {
                return false;
            }
            if (l1.size() != l2.size()) {
                return false;
            }
            return (l1.containsAll(l2));
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
        boolean result;
        while (iterObjects.hasNext()) {
            DataObject dataObject2 = (DataObject)iterObjects.next();
            result = compareDataObjects(dataObject1, dataObject2, true);
            if (result) {
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