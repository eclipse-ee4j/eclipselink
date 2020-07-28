/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.queries;

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: A CollectionContainerPolicy is ContainerPolicy whose container class
 * implements the Collection interface.
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a Collection.
 *
 * @see ContainerPolicy
 * @see MapContainerPolicy
 */
public class CollectionContainerPolicy extends InterfaceContainerPolicy {

    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public CollectionContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public CollectionContainerPolicy(Class containerClass) {
        super(containerClass);
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public CollectionContainerPolicy(String containerClassName) {
        super(containerClassName);
    }

    /**
     * INTERNAL:
     * Add element into a container which implements the Collection interface.
     * @return boolean indicating whether the container changed.
     */
    public boolean addInto(Object key, Object element, Object container, AbstractSession session) {
        Object elementToAdd = element;
        // PERF: Using direct access.
        if (this.elementDescriptor != null) {
            elementToAdd = this.elementDescriptor.getObjectBuilder().wrapObject(element, session);
        }
        try {
            return ((Collection)container).add(elementToAdd);
        } catch (ClassCastException ex1) {
            throw QueryException.cannotAddElement(element, container, ex1);
        } catch (IllegalArgumentException ex2) {
            throw QueryException.cannotAddElement(element, container, ex2);
        } catch (UnsupportedOperationException ex3) {
            throw QueryException.cannotAddElement(element, container, ex3);
        }
    }

    /**
     * INTERNAL:
     * Return a container populated with the contents of the specified Vector.
     */
    public Object buildContainerFromVector(Vector vector, AbstractSession session) {
        if ((getContainerClass() == vector.getClass()) && (!hasElementDescriptor())) {
            return vector;
        } else {
            return super.buildContainerFromVector(vector, session);
        }
    }

    /**
     * INTERNAL:
     * Remove all the elements from container.
     *
     * @param container java.lang.Object
     */
    public void clear(Object container) {
        try {
            ((Collection)container).clear();
        } catch (UnsupportedOperationException ex) {
            throw QueryException.methodNotValid(container, "clear()");
        }
    }

    /**
     * INTERNAL:
     * Return a clone of the specified container.
     */
    @Override
    public Object cloneFor(Object container) {
        if (container == null) {
            return null;
        }

        if (container instanceof java.lang.Cloneable) {
            return super.cloneFor(container);
        }

        Collection original = (Collection)container;
        Collection clone = (Collection)containerInstance(original.size());
        clone.addAll(original);
        return clone;
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
        return ((Collection)container).contains(element);
    }

    public Class getInterfaceType() {
        return ClassConstants.Collection_Class;
    }

    /**
     * INTERNAL:
     * Return whether the collection has order.
     * SortedSets cannot be indexed, but they are order-sensitive.
     */
    public boolean hasOrder() {
        return Helper.classImplementsInterface(this.getContainerClass(), ClassConstants.SortedSet_Class);
    }

    /**
     * INTERNAL:
     * Validate the container type.
     */
    public boolean isValidContainer(Object container) {
        // PERF: Use instanceof which is inlined, not isAssignable which is very inefficent.
        return container instanceof Collection;
    }

    public boolean isCollectionPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     * Return an iterator for the given container.
     *
     * @param container java.lang.Object
     * @return java.util.Enumeration/java.util.Iterator
     */
    public Object iteratorFor(Object container) {
        return ((Collection)container).iterator();
    }

    /**
     * INTERNAL:
     * Remove element from container which implements the Collection interface.
     *
     * @param key java.lang.Object This param represents the key that would be used by this object in a map, may be null
     * @param element java.lang.Object
     * @param container java.lang.Object
     */
    protected boolean removeFrom(Object key, Object element, Object container) {
        try {
            return ((Collection)container).remove(element);
        } catch (UnsupportedOperationException ex) {
            throw QueryException.methodNotValid(element, "remove(Object element)");
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
        return ((Collection)container).size();
    }
}
