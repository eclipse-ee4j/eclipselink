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
package org.eclipse.persistence.sdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.HelperContext;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;

/**
 * <p><b>Purpose</b>:A change summary is used to record changes to DataObjects.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Track changes to DataObjects that are within the scope of this ChangeSummary (based on the root object of the Changesummary).
 * <li> Track if those DataObjects are created, modified or deleted.
 * <li> Return the values at the time that ChangeSummary logging was turned on for DataObjects in scope.
 * </ul>
 * <p>This class is implemented as a Memento (283) [GOF - Gamma, Helm, Johnson, Vlissides] Design Pattern.<br>
 * (Without violating encapsulation, capture and externalize an object's internal state so that the object can be restored to this state later.)<br>
 * <p>The class also realizes some aspects of the Command (233) Pattern - the undo-able operation part (from the first change).<br>
 * (Encapsulate a request as an object, thereby letting you parameterize clients with different requests,
 * queue or log requests, and support undo-able operations.)
 */
public class SDOChangeSummary implements ChangeSummary {
    private SDODataObject rootDataObject;
    private boolean logging;

    //loggingMapping is the boolean that's mapped in OX, we don't really want to turn
    //on logging until after the XML has all been processed at which time we set the 
    //real logging boolean
    private boolean loggingMapping;
    private DataGraph dataGraph;
    private List createdList;
    private List deletedList;
    private Map deepCopies;
    private List createdXPaths;//for ox mapping

    /** The deletedXPaths field is picked up reflectively during marshal/unmarshal operations. */
    @SuppressWarnings("unused") private List deletedXPaths; //for ox mapping     
    private List modifiedDoms;
    private Map unsetPropsMap;

    /** To implement ChangeSummary undo we require a copy of the original state of our model.
     * The originalValueStores will be populated when we start logging - it will then point to the
     * currentValueStore while the currentValueStore will be shallow copied (its child ValueStores
     * are shared between both ValueStores)
     */
    private Map originalValueStores;// HashMap<DataObject, ValueStore>
    private Map originalElements;

    // old values
    private Map oldContainer;// Key is DataObject, value is DataObject
    private Map oldContainmentProperty;// Key is DataObject, value is Property

    /** Cache map of originalSequences Map<DataObject, Sequence> */
    private Map oldSequences;// Key is DataObject, value is Sequence

    /** Map of originalSequence Map<DataObject, Sequence> */
    private Map originalSequences;// Key is DataObject, value is Sequence
    private Map unsetOCPropsMap;

    /** A HashMap of List objects keyed off SDODataObject, null key:value permitted */
    private Map oldSettings;// List of SDOSettings

    /** */
    private Map reverseDeletedMap;

    /** Hold the context containing all helpers so that we can preserve inter-helper relationships */
    private HelperContext aHelperContext;

    public SDOChangeSummary() {
        // HelperContext is set during unmarshalling in SDOUnmarshalListener
        createdList = new ArrayList();
        deepCopies = new HashMap();
        deletedList = new ArrayList();
        oldSettings = new HashMap();
        oldContainer = new HashMap();
        oldContainmentProperty = new HashMap();
        unsetPropsMap = new HashMap();
        unsetOCPropsMap = new HashMap();
        originalValueStores = new HashMap();
        originalElements = new HashMap();
        reverseDeletedMap = new HashMap();
    }

    public SDOChangeSummary(SDODataObject dataObject, HelperContext aContext) {
        this();
        aHelperContext = aContext;
        rootDataObject = dataObject;
    }

    public SDOChangeSummary(SDODataGraph dataGraph, HelperContext aContext) {
        this();
        aHelperContext = aContext;
        this.dataGraph = dataGraph;
    }

    /**
     * Indicates whether change logging is on (<code>true</code>) or off (<code>false</code>).
     * @return <code>true</code> if change logging is on.
     * @see #beginLogging
     * @see #endLogging
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * INTERNAL:
    * Set flag created value.
    * @param created   flag created's new value.
    */
    public void setCreated(DataObject anObject, boolean created) {
        if (getRootObject() == anObject) {
            return;
        }

        // Explicitly clear the flag
        if (isLogging() && !created) {
            createdList.remove(anObject);
        }

        if (isLogging() && !isCreated(anObject)) {
            if (created) {
                // remove from other sets
                deletedList.remove(anObject);
                // add to set
                createdList.add(anObject);
            }
        }
    }

    /**
     * INTERNAL:
    * Set flag modified value.
    * @param deleted   flag modified's new value.
    */
   public boolean setDeleted(DataObject anObject, boolean deleted) {
        if (getRootObject() == anObject) {
            return false;
        }

        // Explicitly clear the flag
        if (isLogging() && !deleted) {
            deletedList.remove(anObject);
        }

        if (isLogging() && !this.isDeleted(anObject)) {            
            if (deleted) {            
                // remove from other sets
                if(isCreated(anObject)){
                  createdList.remove(anObject);
                  
                  oldSettings.remove(anObject);
                  originalValueStores.remove(anObject);
                  originalElements.remove(anObject);                  
                  return false;
                }else {
                  pauseLogging();
                  deletedList.add(anObject);
                  resumeLogging();
                }
            }
        }
        return true;
    }

    /**
      * INTERNAL:
     * @param aKey
     * @param aValue
     * void
     */
    public void setOldContainer(SDODataObject aKey, DataObject aValue) {
        oldContainer.put(aKey, aValue);
    }

    /**
     * INTERNAL:
     *
     * @return
     */
    public Map getOldContainers() {
        return oldContainer;
    }

    /**
     * INTERNAL:
     * @param aKey DataObject
     * @param aValue Property
     * void
     *
     */
    public void setOldContainmentProperty(SDODataObject aKey, Property aValue) {
        oldContainmentProperty.put(aKey, aValue);
    }

    /**
     * INTERNAL:
     * @param aKey DataObject
     * @param aValue Property
     * void
     *
     */
    public void setOldSequence(SDODataObject aKey, Sequence aValue) {
        getOldSequences().put(aKey, aValue);
    }

    /**
     * Returns the {@link DataGraph data graph} associated with this change summary or null.
     * @return the data graph.
     * @see DataGraph#getChangeSummary
     */
    public DataGraph getDataGraph() {
        return dataGraph;
    }

    /**
     * Returns a list consisting of all the {@link DataObject data objects} that have been changed while {@link #isLogging logging}.
     * <p>
     * The {@link #isCreated new} and {@link #isModified modified} objects in the List are references to objects
     * associated with this ChangeSummary.
     * The {@link #isDeleted deleted} objects in the List are references to objects
     * at the time that event logging was enabled;
     * <p> Each changed object must have exactly one of the following methods return true:
     *   {@link #isCreated isCreated},
     *   {@link #isDeleted isDeleted}, or
     *   {@link #isModified isModified}.
     * @return a list of changed data objects.
     * @see #isCreated(DataObject)
     * @see #isDeleted(DataObject)
     * @see #isModified(DataObject)
     */
    public List getChangedDataObjects() {
        // merge all the sets
        ArrayList aList = new ArrayList();
        aList.addAll(getModified());
        if (deletedList != null) {
            aList.addAll(deletedList);
        }
        if (createdList != null) {
            aList.addAll(createdList);
        }
        return aList;
    }

    /**
     * INTERNAL:
     * Return all modified objects
     * @return
     * Set
     */
    public List getModified() {
        ArrayList modifiedList = new ArrayList();
        getModified(rootDataObject, modifiedList);
        return modifiedList;
    }
    
    private void getModified(SDODataObject sdoDataObject, List modifiedList) {
        if(null == sdoDataObject) {
            return;
        }
        
        if(isModified(sdoDataObject)) {
            modifiedList.add(sdoDataObject);
        }
        List<Property> properties = sdoDataObject.getInstanceProperties();
        for(int x=0; x<properties.size(); x++) {
            Property property = properties.get(x);
            if(property.isContainment()) {
                if(property.isMany()) {
                    List<SDODataObject> dataObjects = sdoDataObject.getList(property);
                    for(int y=0; y<dataObjects.size(); y++) {
                        getModified(dataObjects.get(y), modifiedList);
                    }
                } else {
                    if ((property.getType() != null) && !(((SDOType)property.getType()).isChangeSummaryType())) {
                        getModified(sdoDataObject.getDataObject(property), modifiedList);
                    }
                }
            }
        }
    }
    

    /**
     * INTERNAL:
     * Return all deleted objects
     * @return
     * Set
     */
    public List getDeleted() {
        return deletedList;
    }

    /**
     * INTERNAL:
     * Return all created objects
     * @return
     * Set
     */
    public List getCreated() {
        return createdList;
    }

    /**
     * Returns whether or not the specified data object was created while {@link #isLogging logging}.
     * Any object that was added to the scope
     * but was not in the scope when logging began,
     * will be considered created.
     * @param dataObject the data object in question.
     * @return <code>true</code> if the specified data object was created.
     * @see #getChangedDataObjects
     */
    public boolean isCreated(DataObject dataObject) {
        return (createdList != null) && createdList.contains(dataObject);
    }

    /**
     * Returns whether or not the specified data object was deleted while {@link #isLogging logging}.
     * Any object that is not in scope but was in scope when logging began
     * will be considered deleted.
     * @param dataObject the data object in question.
     * @return <code>true</code> if the specified data object was deleted.
     * @see #getChangedDataObjects
     */
    public boolean isDeleted(DataObject dataObject) {
        return (deletedList != null) && deletedList.contains(dataObject);
    }

    /**
     * Returns whether or not the specified data object was updated while {@link #isLogging logging}.
     * An object that was contained in the scope when logging began
     * and remains in the scope when logging ends will be considered potentially modified.
     * <p> An object considered modified must have at least one old value setting.
     * @param dataObject the data object in question.
     * @return <code>true</code> if the specified data object was modified.
     * @see #getChangedDataObjects
     */
    public boolean isModified(DataObject dataObject) {
        // a modified data object is present in the original value 
        // stores list and has not been deleted
        if (this.originalValueStores.get(dataObject) == null || isDeleted(dataObject)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a list of {@link ChangeSummary.Setting settings}
     * that represent the property values of the given <code>dataObject</code>
     * at the point when logging {@link #beginLogging() began}.
     * <p>In the case of a {@link #isDeleted(DataObject) deleted} object,
     * the List will include settings for all the Properties.
     * <p> An old value setting indicates the value at the
     * point logging begins.  A setting is only produced for
     * {@link #isModified modified} objects if
     * either the old value differs from the current value or
     * if the isSet differs from the current value.
     * <p> No settings are produced for {@link #isCreated created} objects.
     * @param dataObject the object in question.
     * @return a list of settings.
     * @see #getChangedDataObjects
     */
    public List getOldValues(DataObject dataObject) {
        if ((dataObject == null) || (!isDeleted(dataObject) && ((((SDODataObject)dataObject).getChangeSummary() != null) && (((SDODataObject)dataObject).getChangeSummary() != this)))) {
            return new ArrayList();
        }
        if (!isCreated(dataObject) && isDirty(dataObject)) {
            List oldSettingsList = new ArrayList();
            for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {
                SDOProperty nextProp = (SDOProperty)dataObject.getInstanceProperties().get(i);
                Setting setting = getOldValueForChangedDataObject(dataObject, nextProp);
                if (setting != null) {
                    oldSettingsList.add(setting);
                }
            }
            
            List openProps = (List)getUnsetOCPropertiesMap().get(dataObject);
            if(openProps != null){
              for(int i=0; i< openProps.size(); i++){
                SDOProperty nextProp = (SDOProperty)openProps.get(i);
                Setting setting = getOldValueForChangedDataObject(dataObject, nextProp);
                  if (setting != null) {
                      oldSettingsList.add(setting);
                  }
              }
            }
            
            return oldSettingsList;
        }
        
        return new ArrayList();// Note: spec did not mention null value case.
    }

    /**
     * INTERNAL:
     *
     * @param dataObject
     * @return
     */
    public List getUnsetProps(DataObject dataObject) {
        //call getOldValues to populate oldSettings which will populate unsetPropsMap
        getOldValues(dataObject);
        Object value = unsetPropsMap.get(dataObject);
        if (value == null) {
            return new ArrayList();
        }
        return (List)value;

    }

    /**
     * INTERNAL:
     * Return the entire HashMap of lists of open content properties that were unset
     * keyed on dataObject
     * @return
     */
    public Map getUnsetOCPropertiesMap() {
        return unsetOCPropsMap;
    }

    /**
     * INTERNAL:
     * Return a List containing all open content properties that were unset
     * @param dataObject
     * @return
     */
    public List getUnsetOCProperties(DataObject dataObject) {
        Object value = unsetOCPropsMap.get(dataObject);
        if (null == value) {
            return new ArrayList();
        }
        return (List)value;
    }

    /**
     * INTERNAL:
     * Add an open content property that has been unset to the list keyed on dataObject
     * @param dataObject
     * @param ocKey
     */
    public void setUnsetOCProperty(DataObject dataObject, Property ocKey) {
        Object value = unsetOCPropsMap.get(dataObject);
        if (null == value) {
            // create a new list and populate
            List aList = new ArrayList();
            aList.add(ocKey);
            unsetOCPropsMap.put(dataObject, aList);
        } else {
            // dont replace existing key (ie we readd a previously unset oc property)        	
            if (!((List)value).contains(ocKey)) {
                ((List)value).add(ocKey);
            }
        }
    }

    /**
     * INTERNAL:
     * Delete an open content property from the list of unset oc properties keyed on dataObject
     * @param dataObject
     * @param ocKey
     */
    public void removeUnsetOCProperty(DataObject dataObject, Property ocKey) {
        Object value = unsetOCPropsMap.get(dataObject);
        if (value != null) {
            // if we referenced the property - check if it is the only one left in the map value
            if (((List)value).remove(ocKey)) {
            	if(!(((List)value).size() > 0)) {
            		unsetOCPropsMap.remove(dataObject);
            	}
            }
        }
    }

    /**
     * Clears the List of {@link #getChangedDataObjects changes} and turns change logging on.
     * No operation occurs if logging is already on.
     * @see #endLogging
     * @see #isLogging
     */
    public void beginLogging() {
        if (!logging) {
            logging = true;
            loggingMapping = true;
            resetChanges();
            rootDataObject.resetChanges();
        }
    }

    /**
     * INTERNAL:
     * Turn both logging flags back on.
     */
    public void resumeLogging() {
        if (!logging) {
            logging = true;
            loggingMapping = true;
        }
    }

    /**
     * An implementation that requires logging may throw an UnsupportedOperationException.
     * Turns change logging off.  No operation occurs if logging is already off.
     * @see #beginLogging
     * @see #isLogging
     */
    public void endLogging() {
        logging = false;
        loggingMapping = false;
        //October 12, 1007 - as per the spec do not clear anything on Changesummary on endLogging
    }

    /**
     * INTERNAL:
     * Turn both logging flags on.
     */
    public void pauseLogging() {
        logging = false;
        loggingMapping = false;
    }

    /**
     * INTERNAL:
     * Called from beginLogging and undoChanges
     */
    private void resetChanges() {
        createdList.clear();
        deletedList.clear();
        // See spec. p.30 "List of changed DataObjects cleared"        
        oldSettings.clear();
        deepCopies.clear();
        oldContainer.clear();
        oldContainmentProperty.clear();
        unsetPropsMap.clear();
        unsetOCPropsMap.clear();
        originalValueStores.clear();
        originalElements.clear();
        reverseDeletedMap.clear();
        getOldSequences().clear();
        getOriginalSequences().clear();
    }

    /**
     * Returns the ChangeSummary root DataObject - the object from which
     * changes are tracked.
     * When a DataGraph is used, this is the same as getDataGraph().getRootObject().
     * @return the ChangeSummary root DataObject
     */
    public SDODataObject getRootObject() {
        return rootDataObject;
    }

    /**
     * Returns a {@link ChangeSummary.Setting setting} for the specified property
     * representing the property value of the given <code>dataObject</code>
     * at the point when logging {@link #beginLogging() began}.
     * <p>Returns null if the property was not modified and
     * has not been {@link #isDeleted(DataObject) deleted}.
     * @param dataObject the object in question.
     * @param property the property of the object.
     * @return the Setting for the specified property.
     * @see #getChangedDataObjects
     */
    public SDOChangeSummary.Setting getOldValue(DataObject dataObject, Property property) {
        if ((dataObject == null) || (!isDeleted(dataObject) && ((((SDODataObject)dataObject).getChangeSummary() != null) && (((SDODataObject)dataObject).getChangeSummary() != this)))) {
            return null;
        }

        if (!isCreated(dataObject) && isDirty(dataObject)) {
            return getOldValueForChangedDataObject(dataObject, (SDOProperty) property);
        }
        return null; 
    }

    /**
     * INTERNAL:
    * @param dataObject which is not null and not created and is dirty in the scope of this changesummary
    * @param property
    * @return new or already existing Setting
    */
    private ChangeSummary.Setting getOldValueForChangedDataObject(DataObject dataObject, SDOProperty property) {
        if ((null == property) || property.getType().isChangeSummaryType()) {
            return null;
        }
        Setting setting = getPropertyInOldSettings(dataObject, property);
        if (setting == null) {
            SDODataObject sdoDataObject = ((SDODataObject)dataObject);
            boolean isDeleted = isDeleted(dataObject);
            Object oldValue = getPropertyInternal(sdoDataObject, property);
            Object currentValue = sdoDataObject.getPropertyInternal(property);
            boolean isSet = sdoDataObject.isSetInternal(property);
            boolean wasSet = wasSet(sdoDataObject, property);

            if (property.isMany()) {
                currentValue = ((ListWrapper)currentValue).getCurrentElements();
                if (isDirty(((ListWrapper)oldValue))) {
                    List elements = (List)getOriginalElements().get(oldValue);
                    oldValue = new ArrayList(elements);

                    if (!property.getType().isDataType()) {
                        for (int i = 0; i < ((List)oldValue).size(); i++) {
                            Object next = ((List)oldValue).get(i);

                            Object deepCopy = getOrCreateDeepCopy((DataObject)next);
                            ((List)oldValue).set(i, deepCopy);
                        }
                    }
                } else {
                    oldValue = currentValue;
                }
            }            
            if (isDeleted || ((wasSet != isSet) || (oldValue != currentValue))) {
                if ((oldValue != null) && !property.getType().isDataType()) {
                    if (oldValue instanceof DataObject) {
                        oldValue = getOrCreateDeepCopy((DataObject)oldValue);
                    }
                }
                setting = buildAndAddOldSetting((SDODataObject)dataObject, property, oldValue, wasSet);
            }
        }
        return setting;
    }

    /**
     * INTERNAL:
     *
     * @param dataObject
     * @param property
     * @param value
     * @param isSet
     * @return
     */
    private Setting buildAndAddOldSetting(SDODataObject dataObject, Property property, Object value, boolean isSet) {
        SDOSetting setting = new SDOSetting(property, value);
        setting.setIsSet(isSet);

        if (oldSettings.get(dataObject) == null) {
            List aList = new ArrayList();
            aList.add(setting);
            oldSettings.put(dataObject, aList);
        } else {
            List theList = (List)oldSettings.get(dataObject);
            theList.add(setting);
        }

        if (!setting.isSet()) {
            List theList = (List)unsetPropsMap.get(dataObject);
            if (theList == null) {
                List aList = new ArrayList();
                aList.add(setting.getProperty().getName());
                unsetPropsMap.put(dataObject, aList);
            } else {
                if (!theList.contains(setting.getProperty().getName())) {
                    theList.add(setting.getProperty().getName());
                }
            }
        }

        return setting;
    }

    /**
     * Returns the value of the {@link DataObject#getContainer container} data object
     * at the point when logging {@link #beginLogging() began}.
     * @param dataObject the object in question.
     * @return the old container data object.
     */
    public SDODataObject getOldContainer(DataObject dataObject) {
        return (SDODataObject) oldContainer.get(dataObject);
    }

    /**
     * Returns the value of the {@link DataObject#getContainmentProperty containment property} data object property
     * at the point when logging {@link #beginLogging() began}.
     * @param dataObject the object in question.
     * @return the old containment property.
     */
    public SDOProperty getOldContainmentProperty(DataObject dataObject) {
        return (SDOProperty) oldContainmentProperty.get(dataObject);
    }

    /**
     * Returns the value of the {@link DataObject#getSequence sequence} for the data object
     * at the point when logging {@link #beginLogging() began}.
     * @param dataObject the object in question.
     * @return the old containment property.
     */
    public SDOSequence getOldSequence(DataObject dataObject) {
        if ((dataObject == null) || (!isDeleted(dataObject) && ((((SDODataObject)dataObject).getChangeSummary() != null) && (((SDODataObject)dataObject).getChangeSummary() != this)))) {
            return null;
        }
        if (!isCreated(dataObject) && dataObject.getType().isSequenced()) {
            // check cache first
            if (getOldSequences().containsKey(dataObject)) {
                return (SDOSequence) getOldSequences().get(dataObject);
            }

            // no sequence - get from the original sequence map
            SDOSequence originalSeq = (SDOSequence) getOriginalSequences().get(dataObject);
            if (originalSeq == null) {
                originalSeq = (SDOSequence) dataObject.getSequence();
            }
            
            SDOSequence seqWithDeepCopies = new SDOSequence((SDODataObject) dataObject);
            for (int i = 0; i < originalSeq.size(); i++) {
                // setting/value may be null in some cases
                Object nextOriginalSettingValue = originalSeq.getValue(i);
                if (nextOriginalSettingValue == null) {
                    continue;
                }

                // property may be null if the setting contains unstructured text
                SDOProperty nextOriginalSettingProp = originalSeq.getProperty(i);
                if (nextOriginalSettingProp == null) {
                    // handle unstructured text
                    seqWithDeepCopies.addText(nextOriginalSettingValue.toString());
                } else if (nextOriginalSettingProp.getType().isDataType()) {
                    // handle simple types
                    seqWithDeepCopies.addSettingWithoutModifyingDataObject(nextOriginalSettingProp, nextOriginalSettingValue, false);
                } else {
                    // handle complex types
                    seqWithDeepCopies.addSettingWithoutModifyingDataObject(nextOriginalSettingProp, getOrCreateDeepCopy((DataObject) nextOriginalSettingValue), false);
                }
            }
            // store deep copy of old sequence in cache
            getOldSequences().put(dataObject, seqWithDeepCopies);
            return seqWithDeepCopies;
        }
        return null;
    }

    /**
     * This method is intended for use by service implementations only.
     * Undoes all changes in the log to restore the tree of
     * DataObjects to its original state when logging began.
     * isLogging() is unchanged.  The log is cleared.
     * @see #beginLogging
     * @see #endLogging
     * @see #isLogging
     */
    public void undoChanges() {

        /**
         * See Jira SDO-109/107 and 125/225 for open issues with move optimization
         * See bug#5882923 for smart local undo via set()/unset()
         */
        Property oldProp = getOldContainmentProperty(rootDataObject);
        String oldName = null;
        if (oldProp != null) {
            oldName = oldProp.getName();
        }
        rootDataObject.undoChanges(true, this, getOldContainer(rootDataObject), oldName);
        resetChanges();
        rootDataObject.resetChanges();
    }

    /**
     * INTERNAL:
     * Set the root DataObject for this ChangeSummary.
     * @param dataObject  the root of DataObject tree this ChangeSummary belongs to
     */
    public void setRootDataObject(DataObject dataObject) {
        rootDataObject = (SDODataObject)dataObject;
    }

    /**
     * INTERNAL:
    * Used by CopyHelper to set logging when creating a copy of a changesummary
    * @param logging      logging status
    */
    public void setLogging(boolean logging) {
        if (logging) {
            beginLogging();
        } else {
            endLogging();
        }

        this.loggingMapping = logging;
    }

    /**
     * INTERNAL:
     * Check if a property is in its DataObject's oldsetting list
     * @param dataObject      property's owner
     * @param property        property to be checked
     * @return                property's Setting if this property is in list
     */
    private ChangeSummary.Setting getPropertyInOldSettings(DataObject dataObject, Property property) {
        Iterator iterOldSettings = null;
        List aList = (List)oldSettings.get(dataObject);
        if (aList != null) {
            iterOldSettings = aList.iterator();
            ChangeSummary.Setting curSetting;
            while (iterOldSettings.hasNext()) {
                curSetting = (ChangeSummary.Setting)iterOldSettings.next();
                if (curSetting.getProperty().equals(property)) {
                    return curSetting;
                }
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Set the helperContext if the default SDOChangeSummary constructor was used
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    /**
     * INTERNAL:
     *
     * @param createdXPathsList
     */
    public void setCreatedXPaths(List createdXPathsList) {
        createdXPaths = createdXPathsList;
    }

    /**
     * INTERNAL:
     *
     * @return
     */
    public List getCreatedXPaths() {
        return createdXPaths;
    }

    /**
     * INTERNAL:
     * Return the logging state during mapping operations
     * @return logging state
     */
    public boolean isLoggingMapping() {
        return loggingMapping;
    }

    /**
     * INTERNAL:
     *
     * @param modifiedDomsList
     */
    public void setModifiedDoms(List modifiedDomsList) {
        modifiedDoms = modifiedDomsList;
    }

    /**
     * INTERNAL:
     *
     * @return
     */
    public List getModifiedDoms() {
        return modifiedDoms;
    }

    /**
     * INTERNAL:
     * The deletedXPaths field is picked up reflectively during marshal/unmarshal operations.
     * @param deletedXPathsList
     */
    public void setDeletedXPaths(List deletedXPathsList) {
        deletedXPaths = deletedXPathsList;
    }

    public Map getOldContainmentProperty() {
        return oldContainmentProperty;
    }

    public Map getOldContainer() {
        return oldContainer;
    }

    /**
     * INTERNAL:
     *
     * @param dataObject
     * @param property
     * @return
     */
    public boolean wasSet(DataObject dataObject, Property property) {
        ValueStore vs = (ValueStore)originalValueStores.get(dataObject);
        if (null == vs) {
            vs = ((SDODataObject)dataObject)._getCurrentValueStore();
        }
        if (property.isOpenContent()) {
            return vs.isSetOpenContentProperty(property);
        } else {
            return vs.isSetDeclaredProperty(((SDOProperty)property).getIndexInType());
        }
    }

    /**
     * INTERNAL:
     *
     * @param dataObject
     * @param property
     * @return
     */
    public Object getPropertyInternal(DataObject dataObject, Property property) {
        ValueStore vs = (ValueStore)originalValueStores.get(dataObject);

        if (null == vs) {
            vs = ((SDODataObject)dataObject)._getCurrentValueStore();
        }

        if (property.isOpenContent()) {
            return vs.getOpenContentProperty(property);
        } else {
            return vs.getDeclaredProperty(((SDOProperty)property).getIndexInType());
        }
    }

    /**
     * INTERNAL:
     *
     * @param dataObject
     * @param property
     * @param value
     */
    public void setPropertyInternal(DataObject dataObject, Property property, Object value) {
        ValueStore vs = (ValueStore)originalValueStores.get(dataObject);
        if (property.isOpenContent()) {
            vs.setOpenContentProperty(property, value);
        } else {
            vs.setDeclaredProperty(((SDOProperty)property).getIndexInType(), value);
        }
    }

    /**
     * INTERNAL:
     * Return the map of original ValueStores keyed on
     * @return
     */
    public Map getOriginalValueStores() {
        return originalValueStores;
    }

    /**
     * INTERNAL:
     * Return whether the <code>dataObject</code> has been modified.
     * @param dataObject
     * @return
     */
    public boolean isDirty(DataObject dataObject) {
        ValueStore vs = (ValueStore)originalValueStores.get(dataObject);
        return vs != null;
    }

    /**
     * INTERNAL:
     * Return whether the <code>aListWrapper</code> has been modified.
     * @param aListWrapper
     * @return
     */
    public boolean isDirty(ListWrapper aListWrapper) {
        Object originalList = getOriginalElements().get(aListWrapper);
        return originalList != null;
    }

    /**
     * INTERNAL:
     * Return whether the <code>aSequence</code> has been modified.
     * @param aSequence
     * @return
     */
    public boolean isDirty(SDOSequence aSequence) {
        Object originalSequence = getOriginalSequences().get(aSequence.getDataObject());
        return originalSequence != null;
    }

    /**
     * INTERNAL:
     *
     * @param dataObject
     * @param property
     */
  public void unsetPropertyInternal(DataObject dataObject, Property property) {
        ValueStore vs = (ValueStore)originalValueStores.get(dataObject);
        if (property.isMany()) {                      
            ListWrapper currentValue = (ListWrapper)dataObject.getList(property);            
            originalElements.put(currentValue, new ArrayList());
            if (property.isOpenContent()) {
                vs.unsetOpenContentProperty(property);                
            } else {
                vs.unsetDeclaredProperty(((SDOProperty)property).getIndexInType());
            }
        } else {
            if (property.isOpenContent()) {
                vs.unsetOpenContentProperty(property);                
            } else {
                vs.unsetDeclaredProperty(((SDOProperty)property).getIndexInType());
            }
        }
    }

    /**
    * INTERNAL:
    * @return Map of original elements, key and value are both listwrappers
    */
    public Map getOriginalElements() {
        return originalElements;
    }

    /**
     * INTERNAL:
     * @param original
     * @return
     */
    private DataObject getOrCreateDeepCopy(DataObject original) {
        DataObject value = (DataObject)getDeepCopies().get(original);
        if (null == value) {
            DataObject undoneCopy = ((SDOCopyHelper)aHelperContext.getCopyHelper()).copy(original, this);

            // if original is null we will create only 1 key:value null:null pair
            getDeepCopies().put(original, undoneCopy);
            getReverseDeletedMap().put(undoneCopy, original);
            return undoneCopy;
        } else {
            return value;
        }
    }

    /**
     * INTERNAL:
     * @return Map of deep copies of DataObjects key is original dataobject
     */
    public Map getDeepCopies() {
        return deepCopies;
    }

    /**
     * INTERNAL:
     * @return Map of deep copies of DataObjects key is copy of dataobject
     */
    public Map getReverseDeletedMap() {
        return reverseDeletedMap;
    }

    /**
      * INTERNAL:
      * Return a map of original sequences keyed on DataObject.
      * @return Map of old Sequences
      */
    public Map getOriginalSequences() {
        if (null == originalSequences) {
            originalSequences = new HashMap();
        }
        return originalSequences;
    }

    /**
    * INTERNAL:
    * Return a map of original sequences keyed on DataObject (cached values).
    * @return Map of old Sequences that have deep copies of all DataObjects
    */
    public Map getOldSequences() {
        if (null == oldSequences) {
            oldSequences = new HashMap();
        }
        return oldSequences;
    }

    /**
     * INTERNAL:
     * Return the string representation of the receiver.
     */
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append("ChangeSummary@");
        aBuffer.append(getClass().hashCode());
        aBuffer.append(" [logging: ");
        aBuffer.append(logging);
        aBuffer.append(", root: ");
        aBuffer.append(rootDataObject);
        List aList = getChangedDataObjects();
        if (aList != null) {
            aBuffer.append(", ");
            aBuffer.append(aList.size());
            aBuffer.append(" changes: <");
            boolean first = true;
            for (Iterator i = aList.iterator(); i.hasNext();) {
                if (first) {
                    first = false;
                } else {
                    aBuffer.append(", ");
                }
                aBuffer.append(i.next());// null values are handled
            }
        }
        aBuffer.append(">]");
        return aBuffer.toString();
    }
}
