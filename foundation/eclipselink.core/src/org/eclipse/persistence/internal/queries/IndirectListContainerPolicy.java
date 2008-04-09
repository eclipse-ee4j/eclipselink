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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * A ContainerPolicy for jdk1.1 only (IndirectList implements Collection
 * in jdk1.2; so the CollectionContainerPolicy can be used.)
 *
 * @see ContainerPolicy
 * @author Big Country
 *    @since TOPLink/Java 2.5
 */
public class IndirectListContainerPolicy extends InterfaceContainerPolicy {

    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public IndirectListContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     * @param containerClass java.lang.Class
     */
    public IndirectListContainerPolicy(Class containerClass) {
        super(containerClass);
        DescriptorException.invalidContainerPolicy(this, containerClass);
    }

    /**
     * INTERNAL:
     * Add element into the container.
     *
     * @param element java.lang.Object
     * @param container java.lang.Object
     * @return boolean indicating whether the container changed
     */
    protected boolean addInto(Object key, Object element, Object container) {
        try {
            ((IndirectList)container).addElement(element);
            return true;
        } catch (ClassCastException ex) {
            throw QueryException.cannotAddElement(element, container, ex);
        }
    }

    /**
     * INTERNAL:
     * Add element into a container which implements the Collection interface.
     *
     * @param element java.lang.Object
     * @param container java.lang.Object
     * @return boolean indicating whether the container changed
     */
    public void addIntoWithOrder(Vector indexes, Hashtable elements, Object container) {
        Object object = null;
        try {
            Enumeration indexEnum = indexes.elements();
            while (indexEnum.hasMoreElements()) {
                Integer index = (Integer)indexEnum.nextElement();
                object = elements.get(index);
                if (index.intValue() >= (sizeFor(container) - 1)) {
                    ((IndirectList)container).addElement(object);
                } else {
                    ((IndirectList)container).setElementAt(object, index.intValue());
                }
            }
        } catch (ClassCastException ex1) {
            throw QueryException.cannotAddElement(object, container, ex1);
        }
    }

    /**
     * INTERNAL:
     * Remove all the elements from container.
     *
     * @param container java.lang.Object
     */
    public void clear(Object container) {
        ((IndirectList)container).clear();
    }

    /**
     * INTERNAL:
     * Return the true if element exists in container.
     *
     * @param element java.lang.Object
     * @param container java.lang.Object
     * @return boolean true if container 'contains' element
     */
    protected boolean contains(Object element, Object container) {
        return ((IndirectList)container).contains(element);
    }

    public Class getInterfaceType() {
        return ClassConstants.IndirectList_Class;
    }

    /**
     * INTERNAL:
     * Return whether the iterator has more objects,
     *
     * @param iterator java.lang.Object
     * @return boolean true if iterator has more objects
     */
    public boolean hasNext(Object iterator) {
        return ((Enumeration)iterator).hasMoreElements();
    }

    /**
     * INTERNAL:
     * Returns true if the collection has order
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    public boolean hasOrder() {
        return true;
    }

    /**
     * INTERNAL:
     * Return an Iterator for the given container.
     *
     * @param container java.lang.Object
     * @return java.lang.Object the iterator
     */
    public Object iteratorFor(Object container) {
        return ((IndirectList)container).elements();
    }

    /**
     * INTERNAL:
     * Return the next object on the queue.
     * Valid for some subclasses only.
     *
     * @param iterator java.lang.Object
     * @return java.lang.Object the next object in the queue
     */
    protected Object next(Object iterator) {
        return ((Enumeration)iterator).nextElement();
    }

    /**
     * INTERNAL:
     * Remove element from container which implements the Collection interface.
     *
     * @param element java.lang.Object
     * @param container java.lang.Object
     */
    protected boolean removeFrom(Object key, Object element, Object container) {
        return ((IndirectList)container).removeElement(element);
    }

    /**
     * INTERNAL:
     * Remove elements from this container starting with this index
     *
     * @param beginIndex int the point to start deleting values from the collection
     * @param container java.lang.Object
     * @return boolean indicating whether the container changed
     */
    public void removeFromWithOrder(int beginIndex, Object container) {
        int size = sizeFor(container) - 1;
        try {
            for (; size >= beginIndex; --size) {
                ((IndirectList)container).removeElementAt(size);
            }
        } catch (ClassCastException ex1) {
            throw QueryException.cannotRemoveFromContainer(new Integer(size), container, this);
        }
    }

    /**
     * INTERNAL:
     * Return the size of container.
     *
     * @param anObject java.lang.Object
     * @return int The size of the container.
     */
    public int sizeFor(Object container) {
        return ((IndirectList)container).size();
    }
}