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

import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.sdo.DefaultValueStore;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.ValueStore;
import org.eclipse.persistence.exceptions.SDOException;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.sequenced.Setting;

/**
 * <b>Purpose:</b>
 * <ul><li>A helper class for making deep or shallow copies of DataObjects.</li>
 * </ul>
 * <p/>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Perform shallow and deep copy operations on {@link DataObject DataObject}s.</li>
 * </ul>
 *
 * @see org.eclipse.persistence.sdo.SDODataObject
 * @since Oracle TopLink 11.1.1.0.0
 */
public class SDOCopyHelper implements CopyHelper {

    /*
       09/12/06 - Add bidirectional property copy support
       09/16/06 - Use design #1a over #2
                           Modify 1 pass opposite property set algorithm to use 2 passes,
                           the first gathers a list of opposites, the 2nd pass iterates the list after tree completion.
                           Runtime is the same since we still need a hash lookup of the opposite DO
       09/19/06 - Adjust copy algorithm to check for opposites before checking containment
                           cont=false, opp=false -> unidirectional
                           cont=false, opp=true  -> bidirectional
                           cont=true,  opp=false -> normal containment
                           cont=true,  opp=true  -> bidirectional
                           The code for copyPropertyValue() has been moved up to copy()
                           where we use the two cached maps to set non-containment properties
       10/02/06 - Finish isMany 1-n bidirectional/unidirectional support in copy() function
       11/09/06 - Jira#129: implement HelperContext via new HelperProvider.getDefaultContext()
       01/29/07 - #5852525 handle null properties with isSet=true
       02/14/07 - #5878605 do not generate oldSettings during copy of cs with logging=true
       02/15/07 - #5878605 move double iteration of cs list in many case to single iteration
       02/23/07 - #5897730 implement ChangeSummary deep copy
       03/14/07 - p.142 limit scope of while by using for
                        - put isSet() check back into open content part of copyChangeSummary
       04/11/07 - Implement Sequence functionality
       05/01/07 - #6026714: because of enlarged scope, copy of copy sets previous iteration
                          value of unset complex setting
     */

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    /**
     * INTERNAL:
     * This default constructor must be used in conjunction with the setHelperContext() function.
     * The custom constructor that takes a HelperContext parameter is recommended over this default constructor.
     */
    public SDOCopyHelper() { 
    }

    /**
     * Constructor that takes in a HelperContext instance that contains this copyHelper.<br/>
     * This is the recommended constructor.
     * @param aContext
     */
    public SDOCopyHelper(HelperContext aContext) {
        aHelperContext = aContext;
    }

    /**
     * Create a shallow copy of the DataObject dataObject:
     *   Creates a new DataObject copiedDataObject with the same values
     *     as the source dataObject for each property where
     *       property.getType().isDataType() is true.
     *   The value of such a Property property in copiedDataObject is:
     *       dataObject.get(property) for single-valued Properties
     *       (copiedDataObject.get(property) equals() dataObject.get(property)), or
     *       a List where each member is equal to the member at the
     *       same index in dataObject for multi-valued Properties
     *        copiedDataObject.getList(property).get(i) equals() dataObject.getList(property).get(i)
     *   The copied Object is unset for each Property where
     *       property.getType().isDataType() is false
     *       since they are not copied.
     *   Read-only properties are copied.
     *   A copied object shares metadata with the source object
     *     sourceDO.getType() == copiedDO.getType()
     *   If a ChangeSummary is part of the source DataObject
     *     the copy has a new, empty ChangeSummary.
     *     Logging state is the same as the source ChangeSummary.
     *
     * @param dataObject to be copied
     * @return copy of dataObject
     */
    public DataObject copyShallow(DataObject dataObject) {
        if (null == dataObject) {
            return null;
        }
        SDODataObject copy = (SDODataObject)getHelperContext().getDataFactory().create(dataObject.getType().getURI(), dataObject.getType().getName());

        List ocListOriginal = ((SDODataObject)dataObject)._getOpenContentProperties();
        for (Iterator anOCIterator = ocListOriginal.iterator(); anOCIterator.hasNext();) {
            copy.addOpenContentProperty((Property)anOCIterator.next());
        }
        
        List ocAttrsListOriginal = ((SDODataObject)dataObject)._getOpenContentPropertiesAttributes();
        for (Iterator anOCAttrIterator = ocAttrsListOriginal.iterator(); anOCAttrIterator.hasNext();) {
            copy.addOpenContentProperty((Property)anOCAttrIterator.next());
        }

        List allProperties = copy.getInstanceProperties();// start iterating all copy's properties
        Iterator iterProperties = allProperties.iterator();
        while (iterProperties.hasNext()) {
            SDOProperty eachProperty = (SDOProperty)iterProperties.next();
            if (dataObject.isSet(eachProperty)) {
                Object o = getValue((SDODataObject)dataObject, eachProperty, null);
                if (eachProperty.getType().isDataType()) {
                    if (!eachProperty.getType().isChangeSummaryType()) {
                        // we defer sequence updates at this point
                        copy.setInternal(eachProperty, o, false);// make copy if current property is datatype
                    }
                }
            }
        }

        if (dataObject.getType().isSequenced()) {
            List settings = ((SDOSequence)dataObject.getSequence()).getSettings();
            for (int index = 0, size = dataObject.getSequence().size(); index < size; index++) {
                Setting nextSetting = (Setting)settings.get(index);

                Property prop = dataObject.getSequence().getProperty(index);
                if (prop == null || ((SDOType) prop.getType()).isDataType()) {
                    Setting copySetting = nextSetting.copy(copy);
                    copy.getSequence().getSettings().add(copySetting);
                    copy.getSequence().addValueToSettings(copySetting);
                }
            }
        }

        if ((copy != null) && (copy.getChangeSummary() != null) && (copy.getType().getChangeSummaryProperty() != null)) {
            if (((SDODataObject)dataObject).getChangeSummary().isLogging()) {
                copy.getChangeSummary().setLogging(true);
            }
        }

        return copy;
    }

    /**
     * Create a deep copy of the DataObject tree:
     *   Copies the dataObject and all its {@link commonj.sdo.Property#isContainment() contained}
     *     DataObjects recursively.
     *   Values of Properties are copied as in shallow copy,
     *     and values of Properties where
     *       property.getType().isDataType() is false
     *     are copied where each value copied must be a
     *       DataObject contained by the source dataObject.
     *     If a DataObject is outside the DataObject tree and the
     *       property is bidirectional, then the DataObject is skipped.
     *     If a DataObject is outside the DataObject tree and the
     *       property is unidirectional, then the same DataObject is referenced.
     *   Read-only properties are copied.
     *   If any DataObject referenced is not in the containment
     *     tree an IllegalArgumentException is thrown.
     *   If a ChangeSummary is part of the copy tree the new
     *     ChangeSummary refers to objects in the new DataObject tree.
     *     Logging state is the same as the source ChangeSummary.
     *
     * @param dataObject to be copied.
     * @return copy of dataObject
     * @throws IllegalArgumentException if any referenced DataObject
     *   is not part of the containment tree.
     */
    public DataObject copy(DataObject dataObject) throws IllegalArgumentException {
        return copy(dataObject, null);
    }

    /**
     * Create a deep copy of the DataObject tree:
     *   Copies the dataObject and all its {@link commonj.sdo.Property#isContainment() contained}
     *     DataObjects recursively.
     *   <p>For each Property where property.type.dataType is true, the values of Properties are
     *       copied as in shallow copy,  and values of Properties where
     *       property.getType().isDataType() is false
     *     are copied where each value copied must be a
     *       DataObject contained by the source dataObject.
     *     <p>If a DataObject is outside the DataObject tree and the
     *       property is bidirectional, then the DataObject is not copied and
     *       references to the object are also not copied.
     *     <p>If a DataObject is outside the DataObject tree and the
     *       property is unidirectional, then the same DataObject is referenced.
     *   <p>Read-only properties are copied.
     *   <p>If any DataObject referenced is not in the containment
     *     tree an IllegalArgumentException is thrown.
     *   <p>If a ChangeSummary is part of the copy tree the new
     *     ChangeSummary refers to objects in the new DataObject tree.
     *     Logging state is the same as the source ChangeSummary.
     *
     * @param dataObject to be copied.
     * @return copy of dataObject
     * @throws IllegalArgumentException if any referenced DataObject
     *    is not part of the containment tree.
     */
    public DataObject copy(DataObject dataObject, SDOChangeSummary cs) throws IllegalArgumentException {
        if (null == dataObject) {
            return null;
        } else {
            // cache one side of opposite property objects as we iterate to the 2nd side - non-deleted objects only
            //* @param doMap (cache original -> copy DataObject instances to set non-containment properties after tree construction)
            HashMap doMap = new HashMap();

            //* @param propMap (cache original DO:non-containment property values to be set after tree construction)            
            HashMap ncPropMap = new HashMap();

            // build object instances by recursing the tree        	            
            SDODataObject aCopy = copyPrivate((SDODataObject)dataObject, doMap, ncPropMap, cs);

            // After the copy containment tree has been built
            // Iterate the non-containment nodes and copy all uni/bi-directional properties on the copy.
            processNonContainmentNodesPrivate(doMap, ncPropMap);

            // Iterate the map of containment nodes and populate sequenced objects in the copy.
            processContainmentSequencesPrivate(doMap, cs);

            /**
             * ChangeSummary on Root Case:
             * We only need to handle a single root element - isMany=true root elements are not valid
             * Copy changeSummary state from original to copy on all cs-root elements that are also roots.
             * Setting logging to true only after recursively setting internal properties will not create oldSettings in the copy
             * The setLogging call will create oldContainer/oldContainmentProperty entries in the copy.
             */
            if ((aCopy != null) && (aCopy.getChangeSummary() != null) && (aCopy.getType().getChangeSummaryProperty() != null)) {
                // re-reference copy objects in the copy changeSummary
                if (((SDODataObject)dataObject).getChangeSummary().isLogging()) {// switch and use resume logging
                    (aCopy.getChangeSummary()).resumeLogging();//.setLogging(true);
                }
                copyChangeSummary(((SDODataObject)dataObject).getChangeSummary(),//
                                  aCopy.getChangeSummary(), doMap);
            }
            return aCopy;
        }
    }

    /**
     * INTERNAL:
     * Iterate the non-containment nodes and copy all uni/bi-directional properties on the copy.
     * @param doMap
     * @param ncPropMap
     */
    private void processNonContainmentNodesPrivate(HashMap doMap, HashMap ncPropMap) {
        // for the doMap and ncPropMap we can either deep copy them all now or each one during rereferencing later
        // iterate nc property map and set bidirectional/unidirectional properties that are in scope
        for (Iterator ncIterator = ncPropMap.keySet().iterator(); ncIterator.hasNext();) {// p.142 limit scope of while by using for
            // get current source dataobject (the one we will set properties on)
            DataObject sourceDO = (DataObject)ncIterator.next();

            // get the list of properties for the current do
            ArrayList aList = (ArrayList)ncPropMap.get(sourceDO);

            // iterate property list
            for (Iterator propIterator = aList.iterator(); propIterator.hasNext();) {// p.142 limit scope of while by using for
                // get current property
                SDOProperty aProperty = (SDOProperty)propIterator.next();

                /*
                 * Stored in the map we have
                 * doMap: (a=key, a'=value)
                 * ncPropMap (a=key, (list of props)=value
                 * We get the copy from the doMap
                 * We get the copy of the source by doing a get on the current source do
                 */

                // get original object that sourceDO points to via current property
                Object targetDO = sourceDO.get(aProperty);

                // flag whether the property is inside the copy tree
                boolean isPropertyInsideCopyTreeScope = false;

                // get sourceDO copy that we will be setting the property on
                DataObject sourceDOCopy = null;

                // lookup copy of targetDO in map
                Object targetDOCopy = null;

                /*
                 * Handle 1-n many case
                 * For containment=true
                 *   the DO's will be cached previously, and both bidirectional (one) and unidirectional
                 *   properties will set the copy on the copy object
                 * For containment=false
                 *   the DO's will not be cached (outside the tree), only unidirectional properties
                 *   will be set using the original list
                 */
                if (aProperty.isMany()) {
                    // create new list to hold copied list items
                    ListWrapper targetList = (ListWrapper)targetDO;

                    // get source\DO copy that we will be setting the property on
                    sourceDOCopy = (DataObject)doMap.get(sourceDO);

                    // lookup copy of targetDO in map
                    targetDOCopy = new ArrayList();
                    for (int i = 0, size = targetList.size(); i < size; i++) {
                        // get sourceDO key - used as a lookup in our doMap
                        DataObject sourceDOCopyKey = (DataObject)targetList.get(i);
                        DataObject sourceDOCopyValue = (DataObject)doMap.get(sourceDOCopyKey);

                        // add copy to new list
                        if (sourceDOCopyValue != null) {
                            // bidirectional/unidirectional inside copy tree - use copy object
                            ((List)targetDOCopy).add(sourceDOCopyValue);
                        } else {
                            // non-containment properties are not cached - store original for unidirectional
                            //targetDOCopy.add(sourceDOCopyKey);
                        }
                    }

                    // check if the target copies are in our map (inside copy tree scope) 
                    // when containment = false then targetDOCopy is empty
                    // Assume: all items in the list share the same property
                    isPropertyInsideCopyTreeScope = ((List)targetDOCopy).size() > 0;
                } else {
                    // handle 1-1 DataObject
                    // lookup copy of targetDO in map
                    targetDOCopy = doMap.get(targetDO);

                    // get sourceDO copy that we will be setting the property on
                    sourceDOCopy = (DataObject)doMap.get(sourceDO);

                    // check if the target copy is in our map (inside copy tree scope)
                    isPropertyInsideCopyTreeScope = targetDOCopy != null;
                }

                // set nc property if we are in the copy tree
                // check if the target copy is in our map (inside copy tree scope)
                if (isPropertyInsideCopyTreeScope) {
                    ((SDODataObject)sourceDOCopy).set(aProperty, targetDOCopy, false);
                } else {
                    // only set unidirectional properties
                    if (null == aProperty.getOpposite()) {
                        // spec 3.9.4 set property to original object when unidirectional
                        ((SDODataObject)sourceDOCopy).set(aProperty, targetDO, false);
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Make a copy of all settings on the original sequence onto the copy sequence while using a
     * cross-reference doMap that relates the original DataObject key to the copy DataObject key.
     * This function is used during deep copy and changeSummary copy.
     * @param origSequence
     * @param copySequence
     * @param doMap
     */
    private void replicateAndRereferenceSequenceCopyPrivate(SDOSequence origSequence, SDOSequence copySequence, DataObject dataObject, DataObject copy, Map doMap, SDOChangeSummary cs) {
        if (cs != null && cs.isDeleted(dataObject)) {
            origSequence = cs.getOldSequence(dataObject);
        }
        
        SDOProperty seqProperty = null;
        try {
            List settings = origSequence.getSettings();
            for (int index = 0, size = origSequence.size(); index < size; index++) {
                Setting nextSetting = (Setting)settings.get(index);                
                seqProperty = origSequence.getProperty(nextSetting);
                
                if ((null == seqProperty) || seqProperty.getType().isDataType()) {
                    Setting copySetting = nextSetting.copy(copy);
                    copySequence.getSettings().add(copySetting);
                    copySequence.addValueToSettings(copySetting);
                } else {
                    Object copySeqValue = null;
                    Object origSeqValue = origSequence.getValue(index);
                    
                    if (cs != null) {
                        Object orig = cs.getReverseDeletedMap().get(origSeqValue);
                        if (orig != null) {
                            origSeqValue = orig;
                        }
                    }
                    
                    if (origSeqValue instanceof XMLRoot) {
                        origSeqValue = ((XMLRoot)origSeqValue).getObject();
                    }

                    // lookup copy if not null, if null then the copySeqValue will be null in the reduced scope assignment above
                    if (null != origSeqValue) {
                        copySeqValue = doMap.get(origSeqValue);
                    } else {
                        // as a secondary verification to the assignment above - make sure the copy is null as well
                        copySeqValue = null;// this assignment is however redundant in our reduced scope assignment above
                    }

                    //now we have the new value
                    Setting copySetting = nextSetting.copy(copy, copySeqValue);
                    copySequence.getSettings().add(copySetting);
                    copySequence.addValueToSettings(copySetting);
                }

                /**
                 * Move assignment inside the loop to minimize scope and to
                 * initialize the variable to null to handle the case where the original is null and
                 * no lookup is performed.
                 * Do not move the scope of this variable outside the for loop or we will end up
                 * with the previous iterations' value set for a complex object that is unset(null).
                 * see #6026714
                 */
            }
        } catch (ClassCastException cce) {// catch any failure of a DataObject cast above          
            throw SDOException.foundSimpleValueForNonDataTypeProperty(seqProperty.getName());
        }
    }

    /**
     * INTERNAL:
     * Iterate the map of containment nodes and populate sequenced objects in the copy.
     * @param doMap
     */
    private void processContainmentSequencesPrivate(Map doMap, SDOChangeSummary cs) {

        /**
         * Prerequisites: - the copy tree has been built and the doMap has been
         * fully populated with containment nodes.<br/> Iterate the doMap and
         * for each containment dataObject populate the sequence
         */

        // iterate containment nodes		
        for (Iterator cIterator = doMap.keySet().iterator(); cIterator.hasNext();) {
            DataObject cObject = (DataObject)cIterator.next();
            DataObject copyDO = (DataObject)doMap.get(cObject);

            // process sequences
            if (cObject.getType().isSequenced()) {
                SDOSequence origSequence = (SDOSequence)cObject.getSequence();

                // iterate the original object sequence - in sequence and create settings on the copy
                replicateAndRereferenceSequenceCopyPrivate(origSequence, (SDOSequence)copyDO.getSequence(), cObject, copyDO, doMap, cs);
            }
        }
    }

    /**
     * INTERNAL: Create a new uninitialized ValueStore.
     *
     * @return
     */
    private ValueStore createValueStore() {
        return new DefaultValueStore();
    }

    /**
     * INTERNAL:
     * Implement ChangeSummary deep copy
     *      Note: a copy with a CS requires the DefautlValueStore implementation
     *      because of changes outside the ValueStore interface for the dataObject field
     * DeepCopy the original changeSummary into the copy dataObject.
     * All deleted keys in originalValueStore, deletedMap, deepCopies are the same original object.
     * We require the following relationships in order to build copies of originals and copies of copies of originals.
     *     origDOtoCopyDOMap
     *         original object (deleted + current) - in original DO : copy of original object - in copy DO
     *     copyDOtoCopyOfDOMap
     *
     *     Assumptions:
     *        Property objects instances are copied only by reference - metadata is the same in the copy.
     *        Deleted objects never exist in the currentValueStore (and are therefore not in the doMap).
     *        Created and modified objects are always in the currentValueStore (and are in the doMap).
     *
     * @param anOriginalCS
     * @param aCopyCS
     * @param doMap (map of original do's (CS1) to their copy do's in (CS2))
     */
    private void copyChangeSummary(ChangeSummary anOriginalCS, ChangeSummary aCopyCS,//
                                   Map origDOCS1toCopyDOCS2Map) {
        // cast interfaces to concrete classes in one place
        SDOChangeSummary originalCS = (SDOChangeSummary)anOriginalCS;
        SDOChangeSummary copyCS = (SDOChangeSummary)aCopyCS;

        // handled by copy constructor
        // map of copy of original ListWrapper (CS2) to its new copy of a copy (CS2) - link ValueStores to Elements 
        HashMap copyListWrapperCS2toCopyOfListCS2Map = new HashMap();

        // in the absence of a ListWrapper.getProperty() we keep a map
        HashMap propertyToOriginalListMap = new HashMap();

        /**
         * In 3 parts we add deleted objects to the global doMap and copy modified, created nodes
         **/

        // fields that need re-referencing from original to copy
        DataObject anOriginalObject = null;
        DataObject aCopyOfOriginalObject = null;

        // iterate deleted objects
        for (Iterator anIterator = originalCS.getDeleted().iterator(); anIterator.hasNext();) {
            anOriginalObject = (DataObject)anIterator.next();
            aCopyOfOriginalObject = copy(anOriginalObject, null);
            // fix deletedList
            copyCS.getDeleted().add(aCopyOfOriginalObject);
            // Assumption check do map before a possible re-add - reset()
            if (null == origDOCS1toCopyDOCS2Map.get(anOriginalObject)) {
                // add temp map of original  : copy of original            
                origDOCS1toCopyDOCS2Map.put(anOriginalObject, aCopyOfOriginalObject);
            } 
        }

        // iterate created objects    	
        for (Iterator aIterator = originalCS.getCreated().iterator(); aIterator.hasNext();) {
            copyCS.getCreated().add(origDOCS1toCopyDOCS2Map.get(aIterator.next()));
        }

        // add modified objects    	
        for (Iterator anIterator = originalCS.getModified().iterator(); anIterator.hasNext();) {
            copyCS.getModified().add(origDOCS1toCopyDOCS2Map.get(anIterator.next()));
        }

        /**
         * Fix originalValueStores by deep copying the original dataObject:key and the original valueStore:value
         * key is original deleted object in [deepCopies] - value is copy of the ValueStore
         */
        ValueStore aVSCopy = null;
        ValueStore aVSOriginal = null;
        for (Iterator anIterator = originalCS.getOriginalValueStores().keySet().iterator();
                 anIterator.hasNext();) {
            anOriginalObject = (DataObject)anIterator.next();
            // deep copy to get corresponding copy DataObject (deleted objects were added to doMap)
            aCopyOfOriginalObject = (DataObject)origDOCS1toCopyDOCS2Map.get(anOriginalObject);

            /**
             * Recursively shallow-copy elements (by iterating the ovs map and iterating the properties of each item)
             * Fix the dataObject pointer 
             */
            aVSCopy = createValueStore();
            aVSOriginal = (ValueStore)originalCS.getOriginalValueStores().get(anOriginalObject);
            // changes made to the copy VS must not affect the original -hence the dataObject field must be a copy of the original
            aVSCopy.initialize(aCopyOfOriginalObject);
            Object aVSPropertyItem = null;

            // get the # of non-opencontent properties for the object holding the CS - do not use DVS.getTypePropertyValues()
            for (int size = ((SDOType) anOriginalObject.getType()).getDeclaredProperties().size(), i = 0; 
                     i < size; i++) {
                aVSPropertyItem = aVSOriginal.getDeclaredProperty(i);
                // only iterate set properties
                if (aVSOriginal.isSetDeclaredProperty(i)) {
                    // shallow copy the object values
                    // handle single case
                    SDOProperty currentProperty = (SDOProperty)((SDOType)anOriginalObject.getType()).getDeclaredProperties().get(i);
                    if (currentProperty.isMany()) {
                        propertyToOriginalListMap.put(aVSPropertyItem, currentProperty);

                        // handle many case - handled by originalElements
                        // container DO must be in our reference map
                        SDODataObject copyContainer = (SDODataObject)origDOCS1toCopyDOCS2Map.get(anOriginalObject);
                        ListWrapper aCopyOfListCopy = (ListWrapper)((DataObject)copyContainer).getList(currentProperty);

                        // add reference of new copy of original List keyed on original List
                        copyListWrapperCS2toCopyOfListCS2Map.put((anOriginalObject).getList(currentProperty), aCopyOfListCopy);
                        aVSCopy.setDeclaredProperty(i, aCopyOfListCopy);
                    } else {
                        // COMPLEX SINGLE
                        if (!currentProperty.getType().isDataType()) {
                            // are we using the cast to DataObject as a sort of instance check that would throw a CCE?
                            aVSCopy.setDeclaredProperty(i, origDOCS1toCopyDOCS2Map.get(aVSPropertyItem));
                        } else {
                            // SIMPLE SINGLE
                            // skip changeSummary property
                            if (!currentProperty.getType().isChangeSummaryType()) {
                                // simple singles set
                                aVSCopy.setDeclaredProperty(i, aVSPropertyItem);
                            }
                        }
                    }
                }
            }

            // create list of unset and current open content properties
            List ocPropertiesList = new ArrayList();
            ocPropertiesList.addAll(originalCS.getUnsetOCProperties(anOriginalObject));
            // add existing properties
            ocPropertiesList.addAll(((SDODataObject)anOriginalObject)._getOpenContentProperties());
            ocPropertiesList.addAll(((SDODataObject)anOriginalObject)._getOpenContentPropertiesAttributes());
            // iterate existing open content properties            
            for (Iterator i = ocPropertiesList.iterator(); i.hasNext();) {
                SDOProperty ocProperty = (SDOProperty)i.next();
                if (aVSOriginal.isSetOpenContentProperty(ocProperty)) {
                    // get oc value              
                    Object anOCPropertyItem = aVSOriginal.getOpenContentProperty(ocProperty);

                    // get oc copy - shallow copy the object values
                    if (ocProperty.isMany()) {
                        // handle many case - handled by originalElements
                        // container DO must be in our reference map
                        SDODataObject copyContainer = (SDODataObject)origDOCS1toCopyDOCS2Map.get(anOriginalObject);
                        ListWrapper aCopyOfListCopy = (ListWrapper)((DataObject)copyContainer).getList(ocProperty);

                        // add reference of new copy of original List keyed on original List
                        copyListWrapperCS2toCopyOfListCS2Map.put((anOriginalObject).getList(ocProperty), aCopyOfListCopy);
                        aVSCopy.setOpenContentProperty(ocProperty, aCopyOfListCopy);
                    } else {
                        // handle complex single case
                        if (!ocProperty.getType().isDataType()) {
                            aVSCopy.setOpenContentProperty(ocProperty, origDOCS1toCopyDOCS2Map.get(aVSPropertyItem));
                        } else {
                            // simple singles set
                            aVSCopy.setOpenContentProperty(ocProperty, anOCPropertyItem);
                        }
                    }
                }
            }

            // set the copy map entry keyed on copy with value a deep copy of the copy
            copyCS.getOriginalValueStores().put(aCopyOfOriginalObject, aVSCopy);
        }

        // end originalValueStore iteration

        /**
         * Fix originalElements by deep copying the original dataObject:key and the original List:value
         * key is original deleted object in [deepCopies] - value is copy of the elements
         * The instances of ListWrapper inside the valueStores must be the same ones used in the originalElements
         */
        ListWrapper anOriginalListKey = null;
        ListWrapper aCopyListWrapper = null;
        List aCopyList = null;
        for (Iterator anIterator = originalCS.getOriginalElements().keySet().iterator();
                 anIterator.hasNext();) {
            anOriginalListKey = (ListWrapper)anIterator.next();
            // create a new ListWrapper
            SDOProperty aProperty = (SDOProperty) propertyToOriginalListMap.get(anOriginalListKey);
            aCopyListWrapper = (ListWrapper)copyListWrapperCS2toCopyOfListCS2Map.get(anOriginalListKey);
            aCopyList = new ArrayList();

            /**
             * For each key:ListWrapper
             *     - shallow copy all the items in the currentElements list
             *     - replace the dataObject with its copy of the copy
             *     - leave the property as is
             * For each value:ArrayList
             *     - replace all values with their copy
             */
            Object aListItem = null;
            Object aListItemCopy = null;
            for (Iterator anItemIterator = anOriginalListKey.iterator(); anItemIterator.hasNext();) {
                aListItem = anItemIterator.next();
                // for simple many types we use the original in the copy
                if (!aProperty.getType().isDataType()) {
                    // get the copy of the original (in the current valuestore) - we need do not make a copy of this copy
                    // we should have a copy of the copy for List items - ListWrapper.add(item) will remove the item from its original wrapper
                    aListItemCopy = origDOCS1toCopyDOCS2Map.get(aListItem);
                } else { 
                    aListItemCopy = aListItem;
                }
                aCopyList.add(aListItemCopy);
            }

            // add element list directly to the ListWrapper and bypass the cs element copy and containment updates
            aCopyListWrapper.setCurrentElements(aCopyList);
            List listValueCopy = new ArrayList();

            // fix ArrayList value
            List listValue = (List)originalCS.getOriginalElements().get(anOriginalListKey);
            aListItem = null;
            aListItemCopy = null;
            for (Iterator aListIterator = listValue.iterator(); aListIterator.hasNext();) {
                aListItem = aListIterator.next();
                // for simple many types we use the original in the copy
                if (!aProperty.getType().isDataType()) {
                    aListItemCopy = origDOCS1toCopyDOCS2Map.get(aListItem);                    
                } else {
                    aListItemCopy = aListItem;
                }

                // don't add nulls to the listWrapper so an undoChanges will encounter an NPE later
                if (aListItemCopy != null) {
                    listValueCopy.add(aListItemCopy);
                } 
            }

            // set the copy map entry keyed on copy with value a deep copy of the copy
            copyCS.getOriginalElements().put(aCopyListWrapper, listValueCopy);
        }

        // end originalist Iteration

        /**
         * fields that are already set when logging is turned on but need to be fixed (deleted objects need references)
         */
        Map oldContainersMap = originalCS.getOldContainers();
        Map copyContainersMap = copyCS.getOldContainers();
        DataObject oldContainerKey = null;
        DataObject copyContainerKey = null;

        // convert any existing entries in the Map - should normally be 0 - unless any OC properties were unset
        for (Iterator anIterator = oldContainersMap.keySet().iterator(); anIterator.hasNext();) {
            oldContainerKey = (DataObject)anIterator.next();
            // get corresponding copy
            copyContainerKey = (SDODataObject)origDOCS1toCopyDOCS2Map.get(oldContainerKey);
            // check existing copyContainers for existing objects - should be 0 - add all objects when pauseLogging() used
            DataObject oldContainerValue = null;
            if (null == copyContainersMap.get(copyContainerKey)) {
                oldContainerValue = (DataObject)oldContainersMap.get(oldContainerKey);
                // set copy key:value pair on copy map directly
                copyContainersMap.put(copyContainerKey, origDOCS1toCopyDOCS2Map.get(oldContainerValue));
            }
        }

        Map oldContainmentPropertyMap = originalCS.getOldContainmentProperty();
        Map copyContainmentPropertyMap = copyCS.getOldContainmentProperty();
        DataObject oldContainmentPropertyKey = null;
        DataObject copyContainmentPropertyKey = null;

        // convert any existing entries in the Map - should normally be 0 - unless any OC properties were unset
        for (Iterator iterContProp = oldContainmentPropertyMap.keySet().iterator();
                 iterContProp.hasNext();) {
            oldContainmentPropertyKey = (DataObject)iterContProp.next();
            // get corresponding copy
            copyContainmentPropertyKey = (SDODataObject)origDOCS1toCopyDOCS2Map.get(oldContainmentPropertyKey);
            // check existing copyContainers for existing objects - should be 0 - add all objects when pauseLogging() used
            if (null == copyContainmentPropertyMap.get(copyContainmentPropertyKey)) {
                // set copy key:value pair on copy map directly
                copyContainmentPropertyMap.put(copyContainmentPropertyKey, oldContainmentPropertyMap.get(oldContainmentPropertyKey));
            }
        }

        Map oldUnsetOCPropertyMap = originalCS.getUnsetOCPropertiesMap();
        SDODataObject oldOCPropertyContainer = null;

        // convert any existing entries in the Map - should normally be 0
        for (Iterator iterContainer = oldUnsetOCPropertyMap.keySet().iterator();
                 iterContainer.hasNext();) {
            // DataObject will be non-Null
            oldOCPropertyContainer = (SDODataObject)iterContainer.next();
            // check existing copyContainers for existing objects - should be 0 - add all objects when pauseLogging() used
            for (Iterator iterUnset = ((List)oldUnsetOCPropertyMap.get(oldOCPropertyContainer)).iterator();
                     iterUnset.hasNext();) {
                // set/create new list on copy Map with corresponding copy of container
                copyCS.setUnsetOCProperty((SDODataObject)origDOCS1toCopyDOCS2Map.get(//
                oldOCPropertyContainer), (Property)iterUnset.next());
            }
        }

        // process sequences

        /**
         * Fix originalSequences by deep copying the original dataObject:key and the original Sequence:value
         * key is original deleted object in [deepCopies] - value is copy of the settings in the sequence.
         * The instances of Sequence inside the originalSequences must be the same ones used in the originalElements
         */

        // iterate the map of <DataObject, Sequence>
        for (Iterator aMapIterator = originalCS.getOriginalSequences().keySet().iterator();
                 aMapIterator.hasNext();) {
            SDODataObject sequenceDataObjectKey = (SDODataObject)aMapIterator.next();
            SDOSequence originalSequence = (SDOSequence)originalCS.getOriginalSequences().get(sequenceDataObjectKey);

            // create a new Sequence with a pointer to the copy of the original DataObject backpointer
            // assume that all dataObject backpointers are containment objects.
            SDODataObject copyOriginalSequenceDataObject = (SDODataObject)origDOCS1toCopyDOCS2Map.get(originalSequence.getDataObject());
            SDOSequence copySequence = new SDOSequence(copyOriginalSequenceDataObject);

            replicateAndRereferenceSequenceCopyPrivate(originalSequence, copySequence, originalSequence.getDataObject(), copyOriginalSequenceDataObject, origDOCS1toCopyDOCS2Map, originalCS);

            // set the copy map entry keyed on copy with value a deep copy of the copy
            copyCS.getOriginalSequences().put(copyOriginalSequenceDataObject, copySequence);
        }

        /**
         * fields to ignore
        // aHelperContext    SDOHelperContext  (id=42)
        // dataGraph    null
        // logging    true
        // loggingMapping    true
        // unsetPropsMap    HashMap<K,V>  (id=117)
        // createdXPaths    null
        // deletedXPaths    null
        // modifiedDoms    null
         */
        /**
         * fields to ignore that are on-demand
         *     oldSettings    HashMap<K,V>  (id=110)
         * reverseDeletedMap    HashMap<K,V>  (id=116)
         * oldSequences    HashMap<K,V>  (id=88)
         */
    }

    /**
     * INTERNAL:
     * Build the copy tree and cache all reachable DataObjects with their copy<br>
     * Cache all non-containment properties - to be set after tree construction<br>
     * Recurse the tree in preorder traversal (root, child1-n)
     * Scope: We do not have to check the copyTree scope when iterating opposites since
     * we will not enter any opposite property dataTree that is outside of the copyTree scope
     * @param doMap (cache original -> copy DataObject instances to set non-containment properties after tree construction)
     * @param ncPropMap (cache original DO:non-containment property values to be set after tree construction)
     */
    private SDODataObject copyPrivate(SDODataObject dataObject, HashMap doMap,//
                                   HashMap ncPropMap, SDOChangeSummary cs) throws IllegalArgumentException {
        // check for null DataObject
        if (null == dataObject) {
            return null;// this is acceptable behavior
        }

        SDODataObject copy = (SDODataObject)getHelperContext()// 
        .getDataFactory().create(dataObject.getType().getURI(), dataObject.getType().getName());

        // store current object for reference by the non-containment map
        doMap.put(dataObject, copy);

        List ocListOriginal = dataObject._getOpenContentProperties();
        for (Iterator anOCIterator = ocListOriginal.iterator(); anOCIterator.hasNext();) {
            copy.addOpenContentProperty((Property)anOCIterator.next());
        }
        
        List ocAttrsListOriginal = dataObject._getOpenContentPropertiesAttributes();
        for (Iterator anOCAttrIterator = ocAttrsListOriginal.iterator(); anOCAttrIterator.hasNext();) {
            copy.addOpenContentProperty((Property)anOCAttrIterator.next());
        }

        //  start iterating all copy's properties
        for (Iterator iterInstanceProperties = copy.getInstanceProperties().iterator();
                 iterInstanceProperties.hasNext();) {
            SDOProperty eachProperty = (SDOProperty)iterInstanceProperties.next();
            boolean isSet = isSet(dataObject, eachProperty, cs);
            if (isSet) {
                Object o = getValue(dataObject, eachProperty, cs);
                if (eachProperty.getType().isDataType()) {
                    if (!eachProperty.getType().isChangeSummaryType()) {

                        /**
                         * ChangeSummaries must be cleared with logging set to the original state without creating oldSettings.
                         * The logging flag from the original will be set on the copy after this copy call completes
                         * and gets set on its container.
                         * The cs is off by default in the copy object.
                         * updateSequence flag is false - we will populate the sequence in order after subtree creation
                         */
                        copy.setInternal(eachProperty, o, false);
                    }
                } else {
                    // case matrix for containment and opposite combinations
                    // cont=false, opp=false -> unidirectional
                    // cont=false, opp=true  -> bidirectional
                    // cont=true,  opp=false -> normal containment
                    // cont=true,  opp=true  -> bidirectional
                    if (eachProperty.isContainment()) {
                        // process containment properties (normal, half of bidirectionals)                        
                        copyContainmentPropertyValue(copy, eachProperty, o, doMap, ncPropMap, cs);
                    } else {
                        // copy non-containment do (not properties (unidirectional, half of bidirectionals))
                        //copyPropertyValue(dataObject, copy, eachProperty, o, copyRoot, doMap);
                        //cacheNContainmentPropertyValue(copy, eachProperty, o, doMap, ncPropMap);
                        // store non-containment properties
                        ArrayList anArray = (ArrayList)ncPropMap.get(dataObject);
                        if (anArray == null) {
                            anArray = new ArrayList();
                            anArray.add(eachProperty);
                            // store property array into the map for the first time
                            ncPropMap.put(dataObject, anArray);
                        } else {
                            // add to existing array of nc properties in the map
                            anArray.add(eachProperty);
                        }
                    }
                }
            }            
        }

        // sequences will not be processed until the entire tree is copied so we can resolve any reference relationships
        return copy;
    }

    /**
     * INTERNAL:
     * Recursive function deep copies all contained properties.
     * Requirements: The value object has isSet=true for all callers.
     * @param copy
     * @param property
     * @param value
     * @param doMap (cache original -> copy DataObject instances to set non-containment properties after tree construction)
     * @param propMap (cache original DO:non-containment property values to be set after tree construction)
     */
    private void copyContainmentPropertyValue(SDODataObject copy, SDOProperty property, Object value,//
                                              HashMap doMap, HashMap ncPropMap, SDOChangeSummary cs) {
        if (property.isMany()) {
            List copyValue = new ArrayList();

            // set the copy to an empty list and add each items from the original in sequence
            // updateSequence flag is false - we will populate the sequence in order after subtree creation
            copy.setInternal(property, copyValue, false);
            for (Iterator iterValues = ((List)value).iterator(); iterValues.hasNext();) {
                SDODataObject o = (SDODataObject)iterValues.next();
                SDODataObject copyO = copyPrivate(o, doMap, ncPropMap, cs);
                ((ListWrapper)copy.getList(property)).add(copyO, false);
                // set changeSummary on all cs-root elements in the list after they are added to the containment tree
                if ((copyO != null) && (copyO.getChangeSummary() != null) && (copyO.getType().getChangeSummaryProperty() != null)) {
                    // re-reference copy objects in the copy changeSummary
                    if (o.getChangeSummary().isLogging()) {
                        (copyO.getChangeSummary()).setLogging(true);
                    }
                    copyChangeSummary(o.getChangeSummary(),//
                                      copyO.getChangeSummary(), doMap);
                }
            }
        } else {// handle non-many case
            // implementers of this function will always pass in a DataObject that may be null
            SDODataObject copyO = copyPrivate((SDODataObject)value, doMap, ncPropMap, cs);

            //  #5852525 handle null properties with isSet=true - fixed 20070130
            // we will set the isSet index in the ValueStore to true for all isSet=true objects, even NULL ones.
            // updateSequence flag is false - we will populate the sequence in order after subtree creation
            copy.setInternal(property, copyO, false);

            // set changeSummary on all cs-root elements in the list after they are added to the containment tree using the original logging value
            if ((copyO != null) && (copyO.getChangeSummary() != null) && (copyO.getType().getChangeSummaryProperty() != null)) {
                // re-reference copy objects in the copy changeSummary
                if (((SDODataObject)value).getChangeSummary().isLogging()) {
                    copyO.getChangeSummary().setLogging(true);
                }
                copyChangeSummary(((SDODataObject)value).getChangeSummary(),//
                                  copyO.getChangeSummary(), doMap);
            }
        }
    }

    /**
     * INTERNAL:
     * Return the helperContext containing this copyHelper.
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
     * Set the helperContext if this copyHelper was created using the default constructor.
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    /**
     * INTERNAL:
     * Used during XML Unmarshal.
     * @param dataObject
     * @param property
     * @param cs
     * @return
     */
    private boolean isSet(SDODataObject dataObject, Property property, SDOChangeSummary cs) {
        if (cs != null) {
            return cs.wasSet(dataObject, property);
        }
        return dataObject.isSetInternal(property);
    }

    /**
     * INTERNAL:
     * Used during XML Unmarshal.
     * @param dataObject
     * @param property
     * @param cs
     * @return
     */
    private Object getValue(SDODataObject dataObject, Property property, SDOChangeSummary cs) {
        if (cs != null) {
            Object returnValue = cs.getPropertyInternal(dataObject, property);
            if (property.isMany()) {
                if (cs.getOriginalElements().containsKey(returnValue)) {
                    // are we using the cast to List as a sort of instance check that would throw a CCE?
                    return cs.getOriginalElements().get(returnValue);
                }
            }
            return returnValue;
        }
        return dataObject.get(property);// get the value of current property                      
    }
}
