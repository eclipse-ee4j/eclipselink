/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.jaxb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.ListWrapper;

import commonj.sdo.DataObject;

public class JAXBListWrapper extends ListWrapper {

    private JAXBValueStore jaxbValueStore;
    private DatabaseMapping mapping;
    private ContainerPolicy containerPolicy;
    private AbstractSession session;
    private Object elements;

    public JAXBListWrapper(JAXBValueStore theJAXBValueStore, SDOProperty theProperty) {
        super(theJAXBValueStore.getDataObject(), theProperty);

        this.jaxbValueStore = theJAXBValueStore;
        JAXBContext jaxbContext = (JAXBContext) jaxbValueStore.getJAXBHelperContext().getJAXBContext();
        this.session = jaxbContext.getXMLContext().getSession(jaxbValueStore.getEntity().getClass());
        this.mapping = jaxbValueStore.getJAXBMappingForProperty(theProperty);
        this.containerPolicy = mapping.getContainerPolicy();
        this.elements = mapping.getAttributeValueFromObject(jaxbValueStore.getEntity());
        if(this.elements == null) {
            this.elements = containerPolicy.containerInstance();
            mapping.setAttributeValueInObject(jaxbValueStore.getEntity(), elements);
        }
    }

    public JAXBListWrapper(JAXBValueStore theJAXBValueStore, SDOProperty theProperty, List list) {
        this(theJAXBValueStore, theProperty);
        this.elements  = list;
    }

    @SuppressWarnings("unused")
	private List getEmptyList() {
        if(containerPolicy == null) {
            return null;
        }
        return (List)containerPolicy.containerInstance();
    }

    public boolean add(Object item, boolean updateSequence) {
        // Not allowed to add null if the property is non-nullable
        if (item == null && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }

        // update element arrays before we modify original object
        // copyElements();

        Object itemToAdd = item;
        if(!this.property.getType().isDataType()) {
            itemToAdd = this.jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)item); 
        }

        boolean result = this.containerPolicy.addInto(itemToAdd, elements, session);

        // update containment    	
        updateContainment(item, updateSequence);

        return result;
    }

    public void add(int index, Object item, boolean updateSequence) {
        // Not allowed to add null if the property is non-nullable
        if (item == null && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }

        // fail-fast range checking
        if ((index < 0) || (index > size())) {
            return;
        }

        // update element arrays before we modify original object
        copyElements();

        // delegate to superclass
        Object itemToAdd = item;
        if(!property.getType().isDataType()) {
            itemToAdd = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)item);
        }
        //ContainerPolicy doesn't support adding with index. 
        Vector v = containerPolicy.vectorFor(elements, session);
        v.add(index, itemToAdd);
        containerPolicy.clear(elements);
        for(Object next:v) {
            containerPolicy.addInto(next, elements, session);
        }

        // update containment
        updateContainment(item, updateSequence);
    }

    protected void copyElements() {
        // update element arrays before we modify original object
        if (isLogging() && (!((SDOChangeSummary)dataObject.getChangeSummary()).isDirty(this))) {
            //getElements will return a new Vector containing the elements. No need to update the main collection.
            ((SDOChangeSummary)dataObject.getChangeSummary()).getOriginalElements().put(this, getCurrentElements());
        }
    }

    private boolean isLogging() {
        return ((dataObject != null) && (dataObject.getChangeSummary() != null) && dataObject.getChangeSummary().isLogging());
    }

    public List getCurrentElements() {
        if(property.getType().isDataType()) {
            return containerPolicy.vectorFor(elements, session);
        } else {
            if(property.isContainment()) {
                return jaxbValueStore.getJAXBHelperContext().wrap(containerPolicy.vectorFor(elements, session), property, jaxbValueStore.getDataObject());
            } else {
                return jaxbValueStore.getJAXBHelperContext().wrap(containerPolicy.vectorFor(elements, session));
            }
        }
    }

    public void setCurrentElements(List newElements) {
        if(newElements == null || newElements.size() == 0) {
            this.elements = containerPolicy.containerInstance();
        }
        this.elements = containerPolicy.containerInstance(newElements.size());
        List elementsToAdd = newElements;
        if(!property.getType().isDataType()) {
            elementsToAdd = jaxbValueStore.getJAXBHelperContext().unwrap(newElements);
        }
        for(int i = 0; i < newElements.size(); i++) {
            if(!property.getType().isDataType()) {
                containerPolicy.addInto(elementsToAdd.get(i), this.elements, session);
            }
        }
        this.mapping.setAttributeValueInObject(jaxbValueStore.getEntity(), elements);
    }

    public void undoChanges(SDOChangeSummary cs) {
        // ignore logging state  
        if (null == cs) {
            return;
        }
        if (cs.isDirty(this)) {
            // swap elements, discard current state
            setCurrentElements((List)cs.getOriginalElements().get(this));
            cs.getOriginalElements().remove(this);
        }
    }

    public boolean remove(Object item, boolean fromDelete, boolean updateSequence) {
        // update element arrays before we modify original object
        copyElements();
        // pass the remove containment (fromDelete) flag back to the recursive delete/detach call to dataObject
        // fromDelete will always be false when called within ListWrapper
        removeContainment(item, fromDelete, updateSequence);
        // remove the first occurrence of any duplicates
        Object toRemove = item;
        if(!property.getType().isDataType()) {
            toRemove = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)item);
        }
        return containerPolicy.removeFrom(toRemove, elements, session);
    }

    public boolean addAll(Collection items, boolean updateSequence) {
        // Not allowed to add null if the property is non-nullable
        if (items.contains(null) && (property != null && !property.isNullable())) {
            throw new UnsupportedOperationException("Property [" + property.getName() + "] is non-nullable");
        }

        // update element arrays before we modify original object
        copyElements();

        // add new elements before we have updated containment on the items - duplicates will be removed 
        boolean modified = false;
        if(items != null && !(items.size() == 0)) {
            modified = true;
            Iterator itemsIterator = items.iterator();
            while(itemsIterator.hasNext()) {
                Object next = itemsIterator.next();
                if(!(property.getType().isDataType())) {
                    next = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)next);
                }
                containerPolicy.addInto(next, elements, session);
            }
        }

        dataObject._getCurrentValueStore().setManyProperty(property, this);

        /**
         * Corner case: Duplicate DataObjects
         * The effect of updateContainment on duplicates will be the removal of all but one of the duplicates
         * in the items collection that was added.
         * For sequences we must remove duplicates from items to match updateContainment for containment dataObjects
         */
        dataObject.updateContainment(property, items, updateSequence);

        // create new settings outside of updateContainment as we do earlier in currentElements.add
        updateSequence(property, items, updateSequence);

        return modified;
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

        boolean modified = true;
        // update element arrays before we modify original object
        copyElements();

        //need to use Vector for indexed operations
        Vector v = containerPolicy.vectorFor(elements, session);
        Collection unwrappedItems = items;
        if(!property.getType().isDataType()) {
            unwrappedItems = jaxbValueStore.getJAXBHelperContext().unwrap((Collection<DataObject>)items);
        }
        v.addAll(position, unwrappedItems);

        containerPolicy.clear(elements);
        for(Object next:v) {
            containerPolicy.addInto(next, elements, session);
        }

        // update containment
        dataObject.updateContainment(property, items);

        // create new settings outside of updateContainment as we do earlier in currentElements.add
        updateSequence(property, items, updateSequence);

        return modified;
    }

    public int size() {
        return containerPolicy.sizeFor(elements);
    }

    public boolean isEmpty() {
        return containerPolicy.isEmpty(elements);
    }

    public boolean contains(Object item) {
        if(property.getType().isDataType()) {
            return containerPolicy.contains(item, elements, session);
        } else {
            return containerPolicy.contains(jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)item), elements, session);
        }
    }

    public boolean containsAll(Collection items) {
        for(Object next:items) {
            if(!(contains(next))) {
                return false;
            }
        }
        return true;
    }

    public Object remove(int index, boolean updateSequence) {
        // fail-fast range checking
        if ((index < 0) || (index >= size())) {
            return null;
        }

        // update element arrays before we modify original object
        copyElements();

        // Update containment of object and container, do not recursively remove containment
        Object toRemove = get(index);
        int occurrence = getOccurrenceIndex(toRemove);
        removeContainment(occurrence, toRemove, false, updateSequence);

        // may need to unwrap
        if(!property.getType().isDataType()) {
            toRemove = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)toRemove);
        }
        return containerPolicy.removeFrom(toRemove, elements, session);
    }

    public Object get(int index) {
        Object toReturn = containerPolicy.vectorFor(elements, session).elementAt(index);
        if(!(property.getType().isDataType())) {
            if(property.isContainment()) {
                toReturn = jaxbValueStore.getJAXBHelperContext().wrap(toReturn, property, jaxbValueStore.getDataObject());
            } else {
                toReturn = jaxbValueStore.getJAXBHelperContext().wrap(toReturn);                
            }
        }
        return toReturn;
    }

    private int getOccurrenceIndex(Object element) {
        int occurrence = 0;
        boolean skipFirstOccurrence = true;
        Vector currentElements = containerPolicy.vectorFor(elements, session);
        for (int position = 0, size = size(); position < size; position++) {
            Object searchIndexObject = currentElements.get(position);
            // match only objects that are duplicates of this current one
            if (element == searchIndexObject) {
                //skip counting the first occurrence
                if (skipFirstOccurrence) {
                    skipFirstOccurrence = false;
                } else {
                    occurrence++;
                }
            }
        }
        return occurrence;
    }

    public ListIterator listIterator() {
        List currentElements = containerPolicy.vectorFor(elements, session);
        if(!property.getType().isDataType()) {
            currentElements = jaxbValueStore.getJAXBHelperContext().wrap(currentElements);
        }
        return currentElements.listIterator();
    }

    public ListIterator listIterator(int position) {
        List currentElements = containerPolicy.vectorFor(elements, session);
        if(!property.getType().isDataType()) {
            currentElements = jaxbValueStore.getJAXBHelperContext().wrap(currentElements);
        }
        return currentElements.listIterator(position);
    }

    public int indexOf(Object item) {
        if(property.getType().isDataType()) {
            return containerPolicy.vectorFor(elements, session).indexOf(item);
        } else {
            return containerPolicy.vectorFor(elements, session).indexOf(jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)item));
        }
    }

    public int lastIndexOf(Object item) {
        if(property.getType().isDataType()) {
            return containerPolicy.vectorFor(elements, session).lastIndexOf(item);
        } else {
            return containerPolicy.vectorFor(elements, session).lastIndexOf(jaxbValueStore.getJAXBHelperContext().unwrap((DataObject)item));
        }
    }

    public Object writeReplace() {
        if(property.getType().isDataType()) {
            return containerPolicy.vectorFor(elements, session);
        } else {
            return jaxbValueStore.getJAXBHelperContext().wrap(containerPolicy.vectorFor(elements, session));
        }
    }

    public Iterator iterator() {
        if(property.getType().isDataType()) {
            return containerPolicy.vectorFor(elements, session).iterator();
        } else {
            return jaxbValueStore.getJAXBHelperContext().wrap(containerPolicy.vectorFor(elements, session)).iterator();
        }
    }

    protected void updateContainment(Object item, boolean updateSequence) {
        if(mapping.isAbstractCompositeCollectionMapping() && null != item) {
            XMLCompositeCollectionMapping compositeMapping = (XMLCompositeCollectionMapping) mapping;
            if(compositeMapping.getContainerAccessor() != null) {
                Object itemEntity = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) item);
                if (itemEntity instanceof ChangeTracker) {
                    PropertyChangeListener listener = ((ChangeTracker) itemEntity)._persistence_getPropertyChangeListener();
                    if (listener != null) {
                        Object itemEntityOldContainer = compositeMapping.getContainerAccessor().getAttributeValueFromObject(itemEntity);
                        listener.propertyChange(new PropertyChangeEvent(itemEntity, compositeMapping.getContainerAttributeName(), jaxbValueStore.getEntity(), itemEntityOldContainer));
                    }
                }
                compositeMapping.getContainerAccessor().setAttributeValueInObject(itemEntity, jaxbValueStore.getEntity());
            }
        }
        super.updateContainment(item, updateSequence);
    }

}