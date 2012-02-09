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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.ListIterator;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

/**
 * INTERNAL:
 * <p/>
 * <b>Purpose:</b>
 * <ul><li>This class wraps the ArrayList of currentElements that implement the List interface.</li>
 * </ul>
 * <p/>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Provide access many properties on {@link DataObject dataObject}s</li>
 * </ul>
 *
 * @see org.eclipse.persistence.sdo.SDODataObject
 * @since Oracle TopLink 11.1.1.0.0
 */
public class ListWrapper implements List, Serializable, Cloneable {

    /*
        05/23/06 - Update addAll(int,Collection), retainAll(Collection), set(int, Object),
                                         add(int, Object), remove(int)
        05/30/06 - SDO-55: JUnit test case adjustments
        07/27/06 - Add pluggable support for BC4J - make fields protected for *SDOList implementations
        08/21/06 - Remove null check on overloaded addAll()
        08/31/06 - Return instance variables to private state - SDOList has been removed
        01/03/07 - add(*) no longer running updateContainment on itself
        01/08/07 - add(object) - reverse order of and extract updateContainment call
                                                extract out common removeContainment from remove() - coverage from 97.5-99.4
        01/31/07 - add undo support by maintaining 2 internal lists
        02/04/07 - add undoChanges() to reset original elements
        02/08/07 - removed equals and hashCode methods as they seem to cause unexplained issues when we started using Listwrapper as a key in a map
        02/21/07 - #5895047: pass the recursive removeContainment flag fromDelete into remove() when called from detach()
                            This will invoke creation of an intact list copy before removing its containment and clearing its changeSummary
        04/16/07 - implement sequences
        04/25/07 - adding duplicate containment true dataObjects are ignored - only a single instance can exist
     */
    protected SDODataObject dataObject;
    protected SDOProperty property;

    /**
     * We are maintaining two pointers to potentially two ArrayList objects.
     * To implement ChangeSummary undo we require a copy of the original state of our model
     * - with special handling for ListWrapper to maintain object identity of the list
     * The List (originalElements) on ChangeSummary will maintain the current state of our model after logged changes.
     * The List (currentElements) will be a progressively deeper distinct shallow copy of the current list as it changes
     */
    protected List currentElements;

    /**
     * INTERNAL:
     * @return
     */
    private List getEmptyList() {
        // ArrayList will be our implementation of List for now - consolidate new calls here
        return new ArrayList();
    }

    public ListWrapper() {
        currentElements = getEmptyList();
    }

    public ListWrapper(SDODataObject theDataObject, Property theProperty) {
        this();
        dataObject = theDataObject;
        property = (SDOProperty) theProperty;
    }

    /**
     * Constructor for non-default Pluggable ValueStore implementations<br>
     * Prerequisites: Containment is already set on theList parameter.
     * Do not use this constructor for default implementations as containment is not updated.
     * The SDO Objects inside this ListWrapper are special case wrappers with no previous containment
     * We do not call updateContainment on the SDO Wrapper objects surrounding the POJO's
     * otherwise the containment of this list will be removed in the embedded detach() call
     * TestCase: the first get on a list.
     */
    public ListWrapper(SDODataObject theDataObject, Property theProperty, List theList) {
        this(theDataObject, theProperty);
        currentElements = theList;
    }

    /**
     *
     */
    public boolean add(Object item) {
        return add(item, true);
    }

    /**
     * INTERNAL:
     * @param item
     * @param updateSequence
     * @return
     */
    public boolean add(Object item, boolean updateSequence) {
        // Not allowed to add null if the property is non-nullable
        if (item == null && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }
        
        // update element arrays before we modify original object
        copyElements();
        
        boolean result = currentElements.add(item); 
        
        // update containment    	
        updateContainment(item, updateSequence);

        // update opposite property
        if (property != null && item != null) {
            Property oppositeProp = property.getOpposite();
            if (oppositeProp != null) {
            	((DataObject) item).set(oppositeProp, dataObject);
            }
        }

        return result;
    }

    /**
     * Inserts the specified element at the index position in this list.<br>
     * @param position (start at 0 = prepend, length = append)
     * @param item
     * @return void
     */
    public void add(int index, Object item) {
        add(index, item, true);
    }

    /**
     * INTERNAL:
     * @param index
     * @param item
     * @param updateSequence
     */
    public void add(int index, Object item, boolean updateSequence) {
        // Not allowed to add null if the property is non-nullable
        if (item == null && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }
        
        // see testLitWrapperAddMaintainsContainment()
        // fail-fast range checking
        if ((index < 0) || (index > size())) {
            return;
        }

        // update element arrays before we modify original object
        copyElements();

        // delegate to superclass
        currentElements.add(index, item);

        // update containment
        updateContainment(item, updateSequence);
        
        // update opposite property
        if (property != null && item != null) {
            Property oppositeProp = property.getOpposite();
            if (oppositeProp != null) {
            	((DataObject) item).set(oppositeProp, dataObject);
                dataObject.set(oppositeProp, null);
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected boolean isLogging() {
        return ((dataObject != null) && (dataObject.getChangeSummary() != null) && dataObject.getChangeSummary().isLogging());
    }

    /**
     * INTERNAL:
     * Shallow copy elements
     *
     */
    protected void copyElements() {
        // update element arrays before we modify original object        
        if (isLogging() && (!dataObject.getChangeSummary().isDirty(this))) {
            // original will maintain object identity - swap before copying            
            dataObject.getChangeSummary().getOriginalElements().put(this, currentElements);
            // current list will now be a shallow copy of original(itself) - we will not use ArrayList.clone()            
            currentElements = new ArrayList(currentElements);
        }
    }

    /**
     * INTERNAL:
     * Undo any changes and return the original List
     * @return
     */
    public void undoChanges(SDOChangeSummary cs) {
        // ignore logging state  
        if (null == cs) {
            return;
        }
        if (cs.isDirty(this)) {
            // swap elements, discard current state            
            currentElements = (List)cs.getOriginalElements().get(this);
            cs.getOriginalElements().remove(this);
        }
    }

    /**
     * INTERNAL:
     * Iterate the collection and add settings where appropriate.
     * @param aProperty
     * @param items
     * @param updateSequence
     */
    protected void updateSequence(Property aProperty, Collection items, boolean updateSequence) {
        if (updateSequence) {
            Iterator valuesIter = items.iterator();
            ArrayList duplicatesList = new ArrayList();// <DataObject>
            while (valuesIter.hasNext()) {
                Object next = valuesIter.next();

                // do not add duplicate settings for containment dataObjects
                if ((null != aProperty) && !((SDOProperty)aProperty).getType().isDataType() && aProperty.isContainment()) {// dataType and containment are mutually exclusive
                    if (!duplicatesList.contains(next)) {
                        updateSequenceSettingInternal(property, next);
                        duplicatesList.add(next);
                    }
                } else {
                    updateSequenceSettingInternal(property, next);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Update the sequence by appending an new setting containing this item object
     * @param item
     */
    private void updateSequenceSettingInternal(Property aProperty, Object item) {
        // create a new setting
        // Note: A non spec isSequenced=true after type define will throw a NPE    	
        if (dataObject.getType().isSequenced()) {
            dataObject.getSequence().addSettingWithoutModifyingDataObject(property, item);
        }
    }

    /**
     * INTERNAL:
     * Update the sequence by removing the setting containing this item object
     * @param item
     */
    private void removeSequenceSettingInternal(int occurrence, Property aProperty, Object item) {
        // get index corresponding to the property:value pair
        // Note: A non spec isSequenced=true after type define will throw a NPE
        if (dataObject.getType().isSequenced()) {
            dataObject.getSequence().removeSettingWithoutModifyingDataObject(property, item);
        }
    }

    /**
     * INTERNAL:
     * @param item
     * @param updateSequence
     */
    protected void updateContainment(Object item, boolean updateSequence) {
        if ((property != null) && property.isContainment() && item instanceof SDODataObject) {
            dataObject.updateContainment(property, (SDODataObject)item);
        } else {
        	if(dataObject != null) {
        		dataObject._setModified(true);
        	}
        }
        // update sequence for containment and non-containment objects
        if ((property != null) && updateSequence) {
            updateSequenceSettingInternal(property, item);
        }
    }
    
    protected void updateContainment(Collection items, boolean updateSequence) {
        if ((property != null) && property.isContainment()) {
            dataObject.updateContainment(property, items, updateSequence);
        } else {
        	if(dataObject != null) {
        		dataObject._setModified(true);
        	}
        }
    }

    /**
     * INTERNAL:
     * @param item
     * @param fromDelete
     * @param updateSequence
     */
    protected void removeContainment(Object item, boolean fromDelete, boolean updateSequence) {

        /**
         * Case multiple occurrences of a single item - which to remove?
         *         [0]    ItemImpl  (id=58) <- this one
         *         [1]    ItemImpl  (id=35)
         *         [2]    ItemImpl  (id=35)
        */

        // default to removing the first occurrence of item (as in the List interface)
        removeContainment(0, item, fromDelete, updateSequence);
    }

    /**
     * INTERNAL:
     * @param item
     * @param fromDelete
     * @param updateSequence
     */
    protected void removeContainment(int occurrence, Object item, boolean fromDelete, boolean updateSequence) {
        if ((property != null) && property.isContainment() && (item != null)) {
            // passing a false fromDelete flag will not remove containment
            ((SDODataObject)item).detachOrDelete(fromDelete);
        } else {
        	dataObject._setModified(true);
        }
        if ((property != null) && dataObject.getType().isSequenced() && updateSequence) {
            removeSequenceSettingInternal(occurrence, property, item);
        }
    }

    /**
     * INTERNAL:
     * Remove the item or first occurrence of the item.
     * @param item
     * @param fromDelete
     * @param updateSequence
     * @return
     */
    public boolean remove(Object item, boolean fromDelete, boolean updateSequence) {
        // update element arrays before we modify original object        
        copyElements();
        // pass the remove containment (fromDelete) flag back to the recursive delete/detach call to dataObject
        // fromDelete will always be false when called within ListWrapper
        removeContainment(item, fromDelete, updateSequence);
        // remove the first occurrence of any duplicates
        return currentElements.remove(item);
    }

    /**
     * @param item
     * @return
     */
    public boolean remove(Object item) {
        // remove item without removing containment
        return remove(item, false, true);
    }

    /**
     * Appends all of the currentElements in the specified Collection to the end of this list,
     * in the order that they are returned by the specified Collection's Iterator.
     * The behavior of this operation is undefined if the specified Collection is modified
     * while the operation is in progress. (This implies that the behavior of this call is undefined
     * if the specified Collection is this list, and this list is nonempty.)<br>
     * This operation is a special case of the general addAll(int, Collection).<br>
     *
     * From the SDO Specification: p18
     *  The getList(property) accessor is especially convenient for many-valued properties.
     *  If property.many is true then set(property, value) and setList(property, value)
     *  require that [value] be a java.util.Collection and List respectively.
     *  These methods are equivalent to getList(property).clear() followed by getList(property).addAll(value).
     *
     * @param items
     * @return boolean
     */
    public boolean addAll(Collection items) {
        // Not allowed to add null if the property is non-nullable
        if (items.contains(null) && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }
        return addAll(items, true);
    }

    /**
     * INTERNAL:
     * Appends all of the currentElements in the specified Collection to the end of this list,
     * in the order that they are returned by the specified Collection's Iterator.
     * This function calls the public addAll(Collection) with a sequence state flag.
     * @param items
     * @param updateSequence
     * @return
     */
    public boolean addAll(Collection items, boolean updateSequence) {
        // update element arrays before we modify original object
        copyElements();

        /**
         * The order of operations for add is
         * 1 - add elements
         * 2 - update containment
         *
         * This order is the reverse of the order in remove
         * 1 - remove containment
         * 2 - remove elements
         */

        // add new elements before we have updated containment on the items - duplicates will be removed 
        boolean modified = currentElements.addAll(items);

        // non-default Pluggable implementations do not require updateContainment
        dataObject._getCurrentValueStore().setManyProperty(property, this);

        /**
         * Corner case: Duplicate DataObjects
         * The effect of updateContainment on duplicates will be the removal of all but one of the duplicates
         * in the items collection that was added.
         * For sequences we must remove duplicates from items to match updateContainment for containment dataObjects
         */
       	updateContainment(items, updateSequence);

        // update opposite property
        if (property != null) {
	        Property oppositeProp = property.getOpposite();
	        if (oppositeProp != null) {
			    Iterator itemsIterator = items.iterator();
			    while(itemsIterator.hasNext()) {
			        Object item = itemsIterator.next();
			        if (item != null) {
				        ((DataObject) item).set(oppositeProp, dataObject);
			        }
			    }
		    }
        }
        
        // create new settings outside of updateContainment as we do earlier in currentElements.add
        updateSequence(property, items, updateSequence);

        return modified;
    }

    /**
     * Inserts all of the currentElements in the specified Collection into this list, starting at the
     * specified position. Shifts the element currently at that position (if any)
     * and any subsequent currentElements to the right (increases their indices).
     * The new currentElements will appear in the list in the order that they are returned by the
     * specified Collection's iterator.<br>
     * @param position (start at 0 = prepend, length = append)
     * @param items
     * @return boolean
     */
    public boolean addAll(int position, Collection items) {
        return addAll(position, items, true);
    }

    public boolean addAll(int position, Collection items, boolean updateSequence) {
        // fail-fast range checking
        if ((position < 0) || (position > size())) {
            return false;
        }
        if ((items == null) || (items.size() == 0)) {
            return false;
        }

        // Not allowed to add null if the property is non-nullable
        if (items.contains(null) && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }

        // delegate to superclass
        // update element arrays before we modify original object
        copyElements();

        /**
         * The order of operations for add is
         * 1 - add elements
         * 2 - update containment
         *
         * This order is the reverse of the order in remove
         * 1 - remove containment
         * 2 - remove elements
         */
        boolean modified = currentElements.addAll(position, items);

        /**
         * Corner case: Duplicate DataObjects
         * The effect of updateContainment on duplicates will be the removal of all but one of the duplicates
         * in the items collection that was added.
         * For sequences we must remove duplicates from items to match updateContainment for containment dataObjects
         */
        // update containment
        updateContainment(items, updateSequence);

        // update opposite property
        if (property != null) {
	        Property oppositeProp = property.getOpposite();
	        if (oppositeProp != null) {
			    Iterator itemsIterator = items.iterator();
			    while(itemsIterator.hasNext()) {
			        Object item = itemsIterator.next();
			        if (item != null) {
				        ((DataObject) item).set(oppositeProp, dataObject);
			            dataObject.set(oppositeProp, null);
			        }
			    }
		    }
        }
        
        // create new settings outside of updateContainment as we do earlier in currentElements.add
        updateSequence(property, items, updateSequence);

        return modified;
    }

    /**
     * Removes from this collection all of its currentElements that are contained in the specified collection.<br>
     * @param items
     * @return boolean
     */
    public boolean removeAll(Collection items) {
        return removeAll(items, true);
    }

    /**
     * INTERNAL:
     * Removes from this collection all of its currentElements that are contained in the specified collection.<br>
     * @param items
     * @param updateSequence
     * @return
     */
    public boolean removeAll(Collection items, boolean updateSequence) {
        Iterator iter = items.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            Object next = iter.next();
            remove(next, false, updateSequence);
            modified = true;
        }
        return modified;
    }

    /**
     * Retains only the currentElements in this collection that are contained in the specified collection
     * (optional operation).<br>
     * In other words, removes from this collection all of its currentElements that
     * are not contained in the specified collection.<br>
     * @param itemsToKeep
     * @return boolean
     */
    public boolean retainAll(Collection itemsToKeep) {
        // fail-fast range checking
        if (itemsToKeep == null) {
            return false;
        }

        if (itemsToKeep.size() == 0) {
            clear();
            return true;
        }

        boolean modified = false;

        // iterate across the full collection and remove only non-(itemsToKeep)    	
        // don't use an Iterator when modifying the list or we will
        // get a ConcurrentModificationException on the Iterator.
        int originalSize = size();// store: as size will reduce as we iterate
        int index = 0;

        // iterate all currentElements and update position only on skipped currentElements
        for (int original = 0; original < originalSize; original++) {
            // get object at current index
            Object anObject = get(index);

            // is this element in the keep list?    		
            if (!itemsToKeep.contains(anObject)) {
                remove(anObject);
                modified = true;
            } else {
                index++;
            }
        }

        // no delegation to superclass required
        return modified;
    }

    /**
     * Removes all of the currentElements from this list. The list will be empty after this call returns.
     */
    public void clear() {
        clear(true);
    }

    /**
     * INTERNAL:
     * @param updateSequence
     */
    public void clear(boolean updateSequence) {
        // update element arrays before we modify original object
        int size = this.size();
        for (int i = size - 1; i >= 0; i--) {
            remove(i, updateSequence);
        }
    }

    /**
     * Replaces the element at the specified index in this list with the specified element.<br>
     * @param index
     * @param item
     * @return Object (the element previously at the specified position)
     */
    public Object set(int index, Object item) {
        // fail-fast range checking
        if ((index < 0) || (index > size())) {
            throw new IndexOutOfBoundsException("index " + index + " is out of bounds.");
        }

        // delegate removal
        Object aPreviousObject = remove(index);

        // delegate insertion
        add(index, item);
        return aPreviousObject;
    }

    /**
     * INTERNAL:
     * Removes the element at the specified position in this list.<br>
     * Position index starts at 0.
     * @param index
     * @param updateSequence
     * @return Object (the element previously at the specified position)
     */
    public Object remove(int index, boolean updateSequence) {
        // fail-fast range checking
        if ((index < 0) || (index >= size())) {
            return null;
        }

        // update element arrays before we modify original object
        copyElements();

        /**
         * The order of operations for remove is
         * 1 - remove containment
         * 2 - remove elements
         *
         * This order is the reverse of the order in addAll
         * 1 - add elements
         * 2 - update containment
         */

        // Update containment of object and container, do not recursively remove containment
        int occurrence = getOccurrenceIndex(index);
        removeContainment(occurrence, currentElements.get(index), false, updateSequence);

        // delegate to superclass
        return currentElements.remove(index);
    }

    /**
     * INTERNAL:
     * Return the occurrence number for the current index in this list.
     * The return value will be non-zero when there are duplicates of an object instance.
     * Example: occurrence number of index 1 = 0, of index 2 = 1<br/>
     *     [index]                              occurrence<br/>
     *         [0]    ItemImpl  (id=58)        0<br/>
     *        [1]    ItemImpl  (id=35)        0<br/>
     *         [2]    ItemImpl  (id=35)           1<br/>
     *
     * @param index
     * @return
     */
    private int getOccurrenceIndex(int index) {
        // Assumption: no occurrences: return 0
        // Assumption: only 1 occurrence: return 0
        int occurrence = 0;
        boolean skipFirstOccurrence = true;
        Object targetObjectAtIndex = currentElements.get(index);
        for (int position = 0, size = size(); (position < size) && (position < (index + 1));
                 position++) {
            Object searchIndexObject = currentElements.get(position);

            // match only objects that are duplicates of this current one
            if (targetObjectAtIndex == searchIndexObject) {
                // skip counting the first occurrence
                if (skipFirstOccurrence) {
                    skipFirstOccurrence = false;
                } else {
                    occurrence++;
                }
            }
        }
        return occurrence;
    }

    /**
     * Removes the element at the specified position in this list.<br>
     * Position index starts at 0.
     * @param index
     * @return Object (the element previously at the specified position)
     */
    public Object remove(int index) {
        return remove(index, true);
    }

    public ListIterator listIterator() {
        return currentElements.listIterator();
    }

    public ListIterator listIterator(int position) {
        return currentElements.listIterator(position);
    }

    /**
     * Return a view of the specified portion of the list
     * @param start - low endpoint (inclusive) of the subList.
     * @param end - high endpoint (exclusive) of the subList.
     * @return
     */
    public List subList(int start, int end) {
        return currentElements.subList(start, end);
    }

    public Object[] toArray() {
        return currentElements.toArray();
    }

    /**
     * Returns an array containing all of the currentElements in this list in proper sequence;
     *   the runtime type of the returned array is that of the specified array.
     *   Obeys the general contract of the Collection.toArray(Object[]) method.<br>
     * Specified by: toArray in interface Collection<E><br>
     *
     * Throws:<br>
     * ArrayStoreException - if the runtime type of the specified array is not a supertype of the
     *   runtime type of every element in this list.<br>
     * NullPointerException - if the specified array is null.<br>
     *
     * @param items -the array into which the currentElements of this list are to be stored, if it is big enough;
     *   otherwise, a new array of the same runtime type is allocated for this purpose.<br>
     * @return Object[] - an array containing the currentElements of this list.
     */
    public Object[] toArray(Object[] items) {
        return currentElements.toArray(items);
    }

    public int size() {
        return currentElements.size();
    }

    public boolean isEmpty() {
        return currentElements.isEmpty();
    }

    public boolean contains(Object item) {
        return currentElements.contains(item);
    }

    public boolean containsAll(Collection items) {
        return currentElements.containsAll(items);
    }

    public Iterator iterator() {
        return currentElements.iterator();
    }

    public int indexOf(Object item) {
        return currentElements.indexOf(item);
    }

    public int lastIndexOf(Object item) {
        return currentElements.lastIndexOf(item);
    }

    public Object get(int position) {
    	try {
    		return currentElements.get(position);
        } catch (Exception e) {
        	// Return null in case of exception, as per SDO 2.1 Spec
        	return null;
        }    		    		
    }

    /**
     * INTERNAL:
     * Defined in SDO 2.01 spec on page 65 Externalizable function is called by
     * ObjectStream.writeObject() A replacement object for serialization can be
     * called here.
     * <p/>Security Note:
     *     This public function exposes a data replacement vulnerability where an outside client
     *     can gain access and modify their non-final constants.
     *     We may need to wrap the GZIP streams in some sort of encryption when we are not
     *     using HTTPS or SSL/TLS on the wire.
     *
     * @see org.eclipse.persistence.sdo.SDOResolvable
     */
    public Object writeReplace() {
        return currentElements;
    }

    /**
     * INTERNAL:
     * @return
     */
    public List getCurrentElements() {
        return currentElements;
    }

    /**
     * INTERNAL:
     * bypass containment and changesummary copy of element list on modifications
     *
     */
    public void setCurrentElements(List currentElementsList) {
        currentElements = currentElementsList;
    }
    
    /**
     * Clone the ListWrapper.
     * This creates a new ListWrapper with the same contents as the original (shallow clone)
     * Minimal clone operation implemented to support usage in JPA 
     */
    public Object clone() {
    	ListWrapper listWrapperClone = new ListWrapper(dataObject, property);
    	listWrapperClone.setCurrentElements(new ArrayList(currentElements));
    	return listWrapperClone;
    }
}
