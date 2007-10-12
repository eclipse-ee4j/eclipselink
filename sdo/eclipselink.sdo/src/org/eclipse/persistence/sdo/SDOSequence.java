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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.exceptions.SDOException;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;

/**
 * INTERNAL:
 * <p/>
 * <b>Purpose:</b>
 * <ul><li>This class Implements the Sequence interface and provides a
 * sequence of Settings that preserve order accross heterogeneos properties.</li>
 * </ul>
 * <p/>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to a Sequence of Properties</li>
 * <li>Provide get/add/remove access to Property/Value pair at each Setting
 * index</li>
 * </ul>
 * <p/>
 * This class uses an implementation of the XMLSetting interface as its sequencing data type.
 * <br/>
 * Adding or removing settings will update the properties on the containing
 * {@link DataObject dataObject}. Any change to the containing dataObject such as deletion of a
 * sequenced property will result in removal of the setting from this sequence.
 * <p/>
 * The methods on XMLDescriptor -get/setGetSettingsMethodName are used to
 * get/set these settings on the {@link Sequence sequence}.
 *
 * @see org.eclipse.persistence.sdo.SDODataObject
 * @see org.eclipse.persistence.internal.oxm.XMLSetting
 * @since Oracle TopLink 11.1.1.0.0
 */
public class SDOSequence implements Sequence {

    /**
     * NOTE:
     * The reflective field names for the private instance variable holding the List of settings
     * and the dataObject back pointer must match the name of the 2 private final fields referenced in SDOEqualityHelper.
     */
    /** DataObject back pointer to container of this sequence for Property resolution */
    private SDODataObject dataObject;

    /** List of SDOSetting objects of type List<XMLSetting<Property, Object>> */
    private List settingsList;

    public SDOSequence(SDODataObject aDataObject) {
        // catch a null dataObject early before we get NPE on any update operations during add/remove
        if (null == aDataObject) {
            throw SDOException.sequenceDataObjectInstanceFieldIsNull();
        }

        // we reference an implementation SDODataObject instead of its interface because of the use of out of spec functions on this field
        dataObject = aDataObject;
        settingsList = new ArrayList();// type <XMLSetting>
    }

    /**
     * Returns the number of entries in the sequence.
     *
     * @return the number of entries.
     */
    public int size() {
        return settingsList.size();
    }

    /**
     * INTERNAL:
     * Return the list of {@link SDOSetting SDOSetting} objects.
     * @return
     */
    public List getSettings() {
        return settingsList;
    }

    /**
     * INTERNAL:
     * Return the {@link SDODataObject} that this sequence is associated with.
     * @return
     */
    public SDODataObject getDataObject() {
        // implemented by SDOCopyHelper.copyChangeSummary()
        return dataObject;
    }

    /**
     * Returns the property for the given entry index. Returns <code>null</code>
     * for mixed text entries.
     *
     * @param index
     *            the index of the entry.
     * @return the property or <code>null</code> for the given entry index.
     */
    public Property getProperty(int index) {
        SDOSetting aSetting = null;
        try {
            aSetting = (SDOSetting)settingsList.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw SDOException.invalidIndex(e, index);
        }

        // TODO: aSetting will not be null unless we threw an exception above - do we want to throw our own?  
        return aSetting.getProperty();
    }

    /**
     * Returns the property value for the given entry index.
     *
     * @param index
     *            the index of the entry.
     * @return the value for the given entry index.
     */
    public Object getValue(int index) {
        SDOSetting aSetting = getSetting(index);

        // TODO: aSetting will not be null unless we threw an exception above - do we want to throw our own?  
        return aSetting.getValue();
    }

    /**
     * INTERNAL:
     * Sets the entry at a specified index to the new value.
     *
     * @param index
     *            the index of the entry.
     * @param value
     *            the new value for the entry.
     * @param updateContainer
     *            flag whether we update the backpointer dataObject..
     */
    private Object setValue(int index, Object value, boolean updateContainer) {
        // TODO: Implement - no shift here
        // get the Setting at this index and update the value        
        SDOSetting aSetting = getSetting(index);

        // handle changeSummary tracking by making the cs.originalSequences dirty 
        if ((null == getProperty(index)) && updateContainer) {
            setModified();
        }

        // TODO: require instanceof ListWrapper checking in the case we pass in a single index of a list
        aSetting.setValue(value);
        // update dataObject container only if we are not an unstructuredText setting
        if ((null != aSetting.getProperty()) && updateContainer) {
            dataObject.set(aSetting.getProperty(), value, false);
        }
        return value;
    }

    /**
     * Sets the entry at a specified index to the new value.
     *
     * @param index
     *            the index of the entry.
     * @param value
     *            the new value for the entry.
     */
    public Object setValue(int index, Object value) {
        return setValue(index, value, true);
    }

    /**
     * Adds a new entry with the specified property name and value to the end of the entries.
     *
     * @param propertyName
     *            the name of the entry's property.
     * @param value
     *            the value for the entry.
     */
    public boolean add(String propertyName, Object value) {
        Property prop = dataObject.getInstanceProperty(propertyName);

        return addPrivate(prop, value, true);
    }

    /**
     * Adds a new entry with the specified property index and value to the end of the entries.
     *
     * @param propertyIndex
     *            the index of the entry's property.
     * @param value
     *            the value for the entry.
     */
    public boolean add(int propertyIndex, Object value) {
        return addPrivate(dataObject.getInstanceProperty(propertyIndex), value, true);
    }

    /**
     * Adds a new entry with the specified property and value to the end of the entries.
     *
     * @param property
     *            the property of the entry.
     * @param value
     *            the value for the entry.
     */
    public boolean add(Property property, Object value) {
        return addPrivate(property, value, true);
    }

    /**
     * INTERNAL:
     * Adds a new entry with the specified property and value to the end of the entries.
     * If this add is actually a modify then we will handle duplication.
     * <p>
     * Does not do an update of the container dataObject. This function is used
     * internally by the SDODataObject implementation.
     *
     * @param aProperty
     *            the property of the entry.
     * @param anObject
     *            the value for the entry.
     */
    public boolean addWithoutUpdate(Property aProperty, Object anObject) {
        // do not update the container
        return addPrivate(aProperty, anObject, false);
    }

    /**
     * INTERNAL:
     * Shared logic for add(*, value) Add a new setting to the end of the sequence.<br/>
     * When there is an existing entry we move the entry when called from a sequence.add(),
     * and we update any existing entry in place when called from DataObject - JIRA-242
     *
     * @param aProperty
     * @param anObject
     * @param fromSequenceAPI - a call from DataObject will not require an update of the container dataObject - itself
     * @return
     */
    private boolean addPrivate(Property aProperty, Object anObject, boolean fromSequenceAPI) {
        Object settingValue = anObject;

        /**
         * Logic: <br>
         * 1. A single setting does not exist at index - we add the single object <br>
         * 2. A single setting exists at index - single object is already set - we replace existing value <br>
         * 3. A single setting exists at some other index - (modify op) - see JIRA-242<br>
         *   3.1 - (from DataObject) we update the previous instance in place<br>
         *   3.2a - (from Sequence.add) we add the new index and leave the existing index in place - creating duplicates.<br>
         *   3.2b - (from Sequence.add) throw exception - current implementation.
         * 4. A many setting does not exist at index - we add a new list with the new object<br>
         * 5. A many setting exists at index - we add to the existing list
         *
         * The logic for this addPrivate(index) is different than addPrivate() because the
         * latter function can be called from DataObject where will may convert an add
         * into a setValue depending on whether an existing entry for a single property exists.

         */

        // add only element properties or null (unstructured text) properties
        if ((aProperty != null) && dataObject._getHelperContext().getXSDHelper().isAttribute(aProperty)) {
            throw SDOException.sequenceAttributePropertyNotSupported(aProperty.getName());
        } else {
            boolean existingEntryWasUpdatedInPlace = false;

            // update(from DataObject) or add(from Sequence) an existing single entry
            if ((null != aProperty) && !aProperty.isMany()) {
                // search for an existing duplicate of the object we are preparing to add/modify
                for (int index = settingsList.size() - 1; index >= 0; index--) {
                    SDOSetting aSetting = getSetting(index);

                    // setting value may be null but property must be !null
                    Property settingProperty = aSetting.getProperty();

                    // ignore unstructured text property==null
                    if ((null != settingProperty) && (settingProperty == aProperty)) {
                        if (!fromSequenceAPI) {
                            // update the setting without updating the backpointer dataObject if we are called from DataObject
                            setValue(index, anObject, false);
                            existingEntryWasUpdatedInPlace = true;
                        } else {

                            /**
                             * It has been decided that the case where an add of
                             * an existing (non-many) object is attempted - we will throw a runtime
                             * exception instead of assuming one of the following actions.
                             * 1) remove previous and add new setting at the end of the sequence (in effect a move(x, end)
                             * 2) add a duplicate of the object and retain the old one in place - creating
                             * potential problems with getValue() setValue() (which one to get first, nth occurrence?)
                             * We will return an exception when attempting to add a setting to a sequence that already has
                             * an existing entry.  The existing entry will not be updated or moved to the end of the sequence.
                             * This exception occurs only for complex single types.
                            */

                            // do not remove(i, false) an existing entry (we always add to the end of the sequence - spec. 3.4.3.1)
                            throw SDOException.sequenceDuplicateSettingNotSupportedForComplexSingleObject(index, aProperty.getName());
                        }
                    }
                }
            }

            // update dataObject container if not unstructured text - add at the end of the list
            if (!existingEntryWasUpdatedInPlace) {
                addPrivateUpdateDataObjectContainer(settingsList.size(), aProperty, settingValue, fromSequenceAPI);
            }
            return true;
        }
    }

    /**
     * INTERNAL:
     * @param aProperty
     * @param settingValue
     * @param updateContainment
     * @return
     */
    private void addPrivateUpdateDataObjectContainer(int index, Property aProperty, Object settingValue, boolean updateContainment) {
        // update dataObject container if not unstructured text
        if (aProperty != null) {

            /**
             * Handle many properties: We will assume that If the
             * property.isMany=true then the value passed in must be a List or
             * the single Object must be added to an existing List when adding
             * to the containing dataObject.
             * If the current many value is not an empty ListWrapper then create one
             * and add the settingValue as the first element.
             */
            if (aProperty.isMany()) {
                // Note: this case is the same as the ListWrapper.addAll() case
                // get existing list (will create an empy ListWrapper)
                ListWrapper existingList = (ListWrapper)dataObject.get(aProperty);

                // create a single setting for each item in the list
                if (settingValue instanceof List) {
                    // iterate list
                    int count = 0;
                    for (Iterator i = ((List)settingValue).iterator(); i.hasNext();) {
                        // add a setting to the end of the sequence
                        Object aValue = i.next();
                        SDOSetting aSetting = new SDOSetting(aProperty, aValue);
                        settingsList.add(index + count++, aSetting);
                        // no need to check updateContainment flag - ListWrapper.add() will not pass an entire List here
                        existingList.add(aValue, false);
                    }
                } else {
                    // set individual list ltem
                    // add a setting to the end of the sequence
                    SDOSetting aSetting = new SDOSetting(aProperty, settingValue);
                    settingsList.add(index, aSetting);
                    if (updateContainment) {
                        // caller is sequence.add()
                        // add to existing list                        
                        int newIndex = getIndexInList(aProperty, settingValue);
                        existingList.add(newIndex, settingValue, false);
                    }
                }
            } else {
                // add a setting to the end of the sequence
                SDOSetting aSetting = new SDOSetting(aProperty, settingValue);
                settingsList.add(index, aSetting);
                if (updateContainment) {
                    // caller is sequence.add()
                    // update dataObject container only if we are not an unstructuredText setting
                    dataObject.set(aProperty, settingValue, false);
                }
            }
        } else {

            /**
             * See SDODataObject.undoChanges() UC4.
             * Modify sequence without modifying container DataObject.
             * This is a use case where we must create an entry in changeSummary.originalSequences
             * independent of any changes to the container.
             * A subsequent undo of the container will pick up this change.
             */

            // handle changeSummary tracking by making the cs.originalSequences dirty
            setModified();

            /**
             * TODO: possible refactor of these 3 setting blocks into 1
             * create a new setting and insert at the index position Shifts the
             * element currently at that position (if any) and any subsequent
             * elements to the right (adds one to their indices).
             */

            // add a setting to the end of the sequence for unstructured text
            SDOSetting aSetting = new SDOSetting(aProperty, settingValue);
            settingsList.add(index, aSetting);
        }
    }

    /**
     * INTERNAL:
     * Save the original sequence on the changeSummary when logging is on
     * for the first modification.
     */
    private void setModified() {
        if ((dataObject.getChangeSummary() != null) &&//
                ((SDOChangeSummary)dataObject.getChangeSummary()).isLogging()) {
            // dont store an original sequence if there are already is one in the map 
            // - this block is skipped if we attempt a second modification
            if (!((SDOChangeSummary)dataObject.getChangeSummary()).isDirty(this)) {
                // handle Sequences only in UC2 where we have modified the container object - not when only the sequence is dirty
                // deep copy the list of settings so a modification of the current sequence will not affect a setting in the originalSequences map
                SDOSequence copySequence = copy();

                // store original sequence on ChangeSummary 
                ((SDOChangeSummary)dataObject.getChangeSummary()).getOriginalSequences().put(dataObject, copySequence);
            }
        }
    }

    /**
     * INTERNAL: shared logic for add(index, *, value) Add a new setting at the specified index.
     *
     * @param index
     * @param aProperty
     * @param value
     * @param allowNullProperties
     */
    private void addPrivate(int index, Property aProperty, Object anObject) {
        Object settingValue = anObject;

        /**
         * Logic: 1. A single setting does not exist at index - we add the
         * single object 2. A single setting exists at index - single object is
         * already set - we replace existing value 3. A many setting does not
         * exist at index - we add a new list with the new object 3. A many
         * setting exists at index - we add to the existing list.
         *
         * The logic for this addPrivate(index) is different than addPrivate() because the
         * latter function can be called from DataObject where will may convert an add
         * into a setValue depending on whether an existing entry for a single property exists.
         */

        // TODO: 
        // check out of bounds exceptions
        if ((index < 0) || (index > size())) {
            // TODO: throw exception or let the List generated one propagate
            return;
        }

        // add only element properties or null (unstructured text) properties
        if ((aProperty != null) && dataObject._getHelperContext().getXSDHelper().isAttribute(aProperty)) {
            throw SDOException.sequenceAttributePropertyNotSupported(aProperty.getName());
        } else {
            // update dataObject container if not unstructured text
            addPrivateUpdateDataObjectContainer(index, aProperty, settingValue, true);
        }
    }

    /**
     * Adds a new entry with the specified property name and value at the
     * specified entry index.
     *
     * @param index
     *            the index at which to add the entry.
     * @param propertyName
     *            the name of the entry's property.
     * @param value
     *            the value for the entry.
     */
    public void add(int index, String propertyName, Object value) {
        addPrivate(index, dataObject.getInstanceProperty(propertyName), value);
    }

    /**
     * Adds a new entry with the specified property index and value at the
     * specified entry index.
     *
     * @param index
     *            the index at which to add the entry.
     * @param propertyIndex
     *            the index of the entry's property.
     * @param value
     *            the value for the entry.
     */
    public void add(int index, int propertyIndex, Object value) {
        addPrivate(index, dataObject.getInstanceProperty(propertyIndex), value);
    }

    /**
     * Adds a new entry with the specified property and value at the specified
     * entry index.
     *
     * @param index
     *            the index at which to add the entry.
     * @param property
     *            the property of the entry.
     * @param value
     *            the value for the entry.
     */
    public void add(int index, Property property, Object value) {
        addPrivate(index, property, value);
    }

    /**
     * INTERNAL:
     * Return the index corresponding the the setting based on the property value pair.
     * Note: this function will only remove the first occurrence of multiple primitives like int.
     * Returns -1 for index not found
     * @param aProperty
     * @param aValue
     * @param occurrencePosition 0=first, 1=second..
     * @return
     */
    public int getIndex(Property aProperty, Object aValue, int occurrencePosition) {
        int index = -1;
        for (int i = 0, size = size(), occurrence = 0; i < size; i++) {//
            SDOSetting aSetting = getSetting(i);

            // watch for null properties that match a null aProperty parameter - use to get the index of the first unstructured text setting
            if (aSetting.getProperty() == aProperty) {
                // check object equality and remove the first match
                if (aValue == aSetting.getValue()) {
                    occurrence++;
                    // decide whether to keep searching
                    if (occurrence > occurrencePosition) {
                        index = i;
                        // break out of loop
                        i = size();
                    }
                }
            }
        }
        return index;
    }

    /**
     * Removes the entry at the given entry index.
     *
     * @param index
     *            the index of the entry.
     */
    public void remove(int index) {
        remove(index, true);
    }

    /**
     * INTERNAL:
     * Remove all entries at the indexes that correspond to the given propertyName.
     *
     * @see org.eclipse.persistence.sdo.SDODataObject unsetInternal()
     * @param propertyName
     * @param updateContainer
     */
    public void remove(String propertyName, String uri, boolean updateContainer) {
        // search for the list and remove each indexed position in the sequence
        // that corresponds to propertyName
        // TODO: linear performance hit
        int size = size();

        // count down and handle decreasing size of settings list
        for (int i = size - 1; i >= 0; i--) {
            Property aProperty = getProperty(i);

            // TODO: handle null property for unstructured text
            if ((null != aProperty) && aProperty.getName().equals(propertyName) && aProperty.getType().getURI().equals(uri)) {
                // do not breakout of loop with this property, iterate until no more are found
                remove(i, updateContainer);
            }
        }
    }

    /**
     * INTERNAL:
     * Remove the entry at the given sequence index
     *
     * @param index
     * @param updateContainer
     */
    public void remove(int index, boolean updateContainer) {
        Property aProperty = getProperty(index);

        /**
            * Save object state for unstructured text settings before we remove them.
            *
         * See SDODataObject.undoChanges() UC4.
         * Modify sequence without modifying container DataObject.
         * This is a use case where we must create an entry in changeSummary.originalSequences
         * independent of any changes to the container.
         * Only unstructured text entries are not tracked by lw.remove or do.unset.
         * A subsequent undo of the container will pick up this change.
         */

        // handle changeSummary tracking by making the cs.originalSequences dirty 
        if ((null == aProperty) && updateContainer) {
            setModified();
        }

        // shifting is handled by List during the remove
        SDOSetting aSetting = (SDOSetting)settingsList.remove(index);// aSetting is the same as the get above

        // update dataObject container
        if ((aSetting != null) && (aProperty != null) && updateContainer) {
            // Remove the property value or single many element from the containing dataObject
            // we are calling unset/detach in a [fromDelete=true] context
            if (aProperty.isMany()) {
                // complex/simple many
                ((ListWrapper)dataObject.getList(aProperty)).remove(aSetting.getValue(), false, false);
            } else {
                // complex/simple single
                dataObject.unset(aProperty, true, false);
            }
        }
    }

    /**
     * Moves the entry at <code>fromIndex</code> to <code>toIndex</code>.
     *
     * @param toIndex
     *            the index of the entry destination.
     * @param fromIndex
     *            the index of the entry to move.
     */
    public void move(int toIndex, int fromIndex) {
        // perform index checking
        if (toIndex == fromIndex) {
            return;
        }

        //call getSetting on toIndex to handle outofboundsexception
        getSetting(toIndex);
        Property aProperty = getProperty(fromIndex);

        // handle changeSummary tracking by making the cs.originalSequences dirty 
        if (null == aProperty) {
            setModified();
        }

        // get the Setting to shift by removing it and shift settings
        SDOSetting aSetting = (SDOSetting)settingsList.remove(fromIndex);

        // set the Setting into its new position and shift existing Settings
        settingsList.add(toIndex, aSetting);

        if ((aProperty != null) && aProperty.isMany()) {
            ListWrapper lw = (ListWrapper)dataObject.getList(aProperty);
            Object value = aSetting.getValue();
            int currentIndexInLw = lw.indexOf(value);
            lw.remove(currentIndexInLw, false);
            int newIndexInLw = getIndexInList(aProperty, value);
            lw.add(newIndexInLw, value, false);
        }
    }

    private int getIndexInList(Property manyProp, Object value) {
        int returnIndex = -1;
        for (int i = 0; i < settingsList.size(); i++) {
            SDOSetting nextSetting = (SDOSetting)settingsList.get(i);
            if (nextSetting.getProperty().equals(manyProp)) {
                returnIndex++;
                if (value.equals(nextSetting.getValue())) {
                    return returnIndex;
                }
            }
        }
        return returnIndex;
    }

    /**
     * Adds a new text entry to the end of the Sequence.
     *
     * @deprecated replaced by {@link #addText(String)} in 2.1.0
     */
    public void add(String text) {
        addText(text);
    }

    /**
     * Adds a new text entry at the given index.
     *
     * @deprecated replaced by {@link #addText(int, String)} in 2.1.0
     */
    public void add(int index, String text) {
        addText(index, text);

    }

    /**
     * Adds a new text entry to the end of the Sequence.
     *
     * @param text
     *            value of the entry.
     */
    public void addText(String text) {
        // add a new unstructured text entry with Property==null
        addPrivate(size(), null, text);
    }

    /**
     * Adds a new text entry at the given index.
     *
     * @param index
     *            the index at which to add the entry.
     * @param text
     *            value of the entry.
     */
    public void addText(int index, String text) {
        // add a new unstructured text entry with Property==null
        addPrivate(index, null, text);
    }

    /**
     * INTERNAL:
     * This function allows us to replace the entire list of settings with a new list.
     * Use of this function to modify the sequence outside of the API functions is not
     * advise as the state of the containing DataObject and the ChangeSummary will
     * be out of synchronization.
     * Use only to create a copy of a sequence that is not used in a live DataObject.
     * @param newList
     *             the list to replace the current list of settings.
     */
    private void setSettings(List newList) {
        // TODO: If the settingsList is replaced we are breaking any ChangeSummary or DataObject synchronization
        // We want to keep this function private so that the user cannot modify the sequence outside of its DataObject/ChangeSummary context
        settingsList = newList;
    }

    /**
     * INTERNAL:
     * Return a deep copy of the SDOSequence object.
     * The settings in the sequence are not shared between the 2 sequence objects.
     * @return
     */
    public SDOSequence copy() {
        // any modification to the current sequence will not affect a copy in the originalSequences
        SDOSequence copySequence = new SDOSequence(dataObject);
        List copyList = new ArrayList();//getSettings()); // type <Setting>
        for (Iterator i = getSettings().iterator(); i.hasNext();) {
            SDOSetting aSetting = (SDOSetting)i.next();
            SDOSetting copySetting = new SDOSetting(aSetting.getProperty(), aSetting.getValue());
            copyList.add(copySetting);
        }

        // set the list of settings without affecting the container
        copySequence.setSettings(copyList);
        return copySequence;
    }

    /**
      * INTERNAL:
      */
    private SDOSetting getSetting(int index) {
        try {
            return (SDOSetting)settingsList.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw SDOException.invalidIndex(e, index);
        }
    }

    /**
     * INTERNAL:
     * Print out a String representation of this object
     */
    public String toString() {
        // TODO: expand past HashCode output
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(getClass().getName());
        aBuffer.append("@");
        aBuffer.append(hashCode());
        aBuffer.append(", do: ");
        aBuffer.append(dataObject);
        // settingsList is always set in the constructor, therefore !null
        List aList = getSettings();
        aBuffer.append(", ");
        aBuffer.append(aList.size());
        aBuffer.append(" Settings: <");
        boolean first = true;
        for (Iterator i = aList.iterator(); i.hasNext();) {
            if (first) {
                first = false;
            } else {
                aBuffer.append(", ");
            }
            aBuffer.append(i.next());// null values are handled
        }
        aBuffer.append(">]");
        return aBuffer.toString();
    }
}