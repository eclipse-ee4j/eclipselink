/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Mar 27/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.jaxb;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

import commonj.sdo.DataObject;

/**
 * JAXBList wraps the POJOs collection in order to provide additional SDO information.
 */
public class JAXBList implements List {

    private JAXBValueStore jaxbValueStore;
    private SDOProperty property;
    private ContainerPolicy containerPolicy;
    private AbstractSession session;

    public JAXBList(JAXBValueStore aJAXBValueStore, SDOProperty aProperty) {
        this.jaxbValueStore = aJAXBValueStore;
        this.property = aProperty;

        JAXBContext jaxbContext = (JAXBContext) jaxbValueStore.getJAXBHelperContext().getJAXBContext();
        this.session = jaxbContext.getXMLContext().getSession(aJAXBValueStore.getEntity().getClass());
        DatabaseMapping mapping = aJAXBValueStore.getJAXBMappingForProperty(property);
        this.containerPolicy = mapping.getContainerPolicy();
    }

    void setValueStore(JAXBValueStore aJAXBValueStore) {
        this.jaxbValueStore = aJAXBValueStore;
    }

    public void add(int index, Object element) {
        Object container = getContainer();
        if(container instanceof List) {
            List list = (List) container;
            if(!property.getType().isDataType()) {
                JAXBHelperContext jaxbHelperContext = jaxbValueStore.getJAXBHelperContext();
                Object unwrappedElement = jaxbHelperContext.unwrap((DataObject) element);
                jaxbHelperContext.putWrapperDataObject(unwrappedElement, (SDODataObject) element);
                list.add(index, unwrappedElement);
            } else {
                list.add(index, element);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public boolean add(Object e) {
        if(!property.getType().isDataType()) {
            e = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) e);
        }
        return containerPolicy.addInto(e, getContainer(), session);
    }

    public boolean addAll(Collection c) {
        boolean modified = false;
        for(Object o : c) {
            modified = add(o) || modified;
        }
        return modified;
    }

    public boolean addAll(int index, Collection c) {
        Object container = getContainer();
        if(container instanceof List) {
            List list = (List) container;
            if(!property.getType().isDataType()) {
                c = jaxbValueStore.getJAXBHelperContext().unwrap(c);
            }
            return list.addAll(index, c);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void clear() {
        containerPolicy.clear(getContainer());
    }

    public boolean contains(Object o) {
        if(!property.getType().isDataType()) {
            o = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) o);
        }
        return containerPolicy.contains(o, getContainer(), session);
    }

    public boolean containsAll(Collection collection) {
        for(Object o : collection) {
            if(!property.getType().isDataType()) {
                o = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) o);
            }
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public Object get(int index) {
        return getList().get(index);
    }

    public int indexOf(Object o) {
        return getList().indexOf(o);
    }

    public boolean isEmpty() {
        return containerPolicy.isEmpty(getContainer());
    }

    public Iterator iterator() {
        return getList().iterator();
    }

    public int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return getList().listIterator();
    }

    public ListIterator listIterator(int index) {
        return getList().listIterator(index);
    }

    public Object remove(int index) {
        Object container = getContainer();
        if(container instanceof List) {
            List list = (List) container;
            return list.remove(index);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public boolean remove(Object o) {
        if(!property.getType().isDataType()) {
            o = jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) o);
        }
        return containerPolicy.removeFrom(o, getContainer(), session);
    }

    public boolean removeAll(Collection c) {
        boolean modified = false;
        for(Object o : c) {
            modified = remove(o) || modified;
        }
        return modified;
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return containerPolicy.sizeFor(getContainer());
    }

    public List subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return getList().toArray();
    }

    public Object[] toArray(Object[] a) {
        return getList().toArray(a);
    }

    private Object getContainer() {
        DatabaseMapping mapping = jaxbValueStore.getJAXBMappingForProperty(property);
        Object container = mapping.getAttributeValueFromObject(jaxbValueStore.getEntity());
        if(null == container) {
            container = containerPolicy.containerInstance();
            mapping.setAttributeValueInObject(jaxbValueStore.getEntity(), container);
        }
        return container;
    }

    private List getList() {
        List list;
        if(containerPolicy.isListPolicy()) {
            list = (List) getContainer();
        } else {
            list = containerPolicy.vectorFor(getContainer(), session);
        }
        if(property.getType().isDataType()) {
            return list;
        } 
        if(property.isContainment()) {
            return jaxbValueStore.getJAXBHelperContext().wrap(list, property, jaxbValueStore.getDataObject());
        } else {
            return jaxbValueStore.getJAXBHelperContext().wrap(list);
        }
    }

}
