/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.queries;

import java.util.Vector;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;

public interface CoreContainerPolicy<ABSTRACT_SESSION extends CoreAbstractSession> {

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of JDK 1.1 and 1.2.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed
     */
    boolean addInto(Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of type.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed.
     */
    boolean addInto(Object key, Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Remove all the elements from the specified container.
     * Valid only for certain subclasses.
     */
    void clear(Object container);

    /**
     * INTERNAL:
     * Return an instance of the container class.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    Object containerInstance();

    /**
     * INTERNAL:
     * Check if the object is contained in the collection.
     * This is used to check contains in a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    boolean contains(Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return whether the iterator has more objects.
     * The iterator is the one returned from #iteratorFor().
     * Valid for some subclasses only.
     *
     * @see ContainerPolicy#iteratorFor(Object)
     */
    boolean hasNext(Object iterator);

    /**
     * INTERNAL:
     * Return whether the container is empty.
     */
    boolean isEmpty(Object container);

    boolean isListPolicy();

    /**
     * INTERNAL:
     * Return an iterator for the given container.
     * This iterator can then be used as a parameter to #hasNext()
     * and #next().
     *
     * @see ContainerPolicy#hasNext(Object)
     * @see ContainerPolicy#next(Object, org.eclipse.persistence.internal.sessions.AbstractSession)
     */
    Object iteratorFor(Object container);

    /**
     * INTERNAL:
     * Return the next object from the iterator.
     * This is used to stream over a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    Object next(Object iterator, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     *
     * In the case of a Map, this will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(Object)
     * @see MapContainerPolicy#unwrapIteratorResult(Object)
     */
    Object nextEntry(Object iterator);

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     *
     * In the case of a Map, this will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(Object)
     * @see MapContainerPolicy#unwrapIteratorResult(Object)
     */
    Object nextEntry(Object iterator, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Remove the object from the collection.
     * This is used to remove from a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    boolean removeFrom(Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Set the class used for the container.
     */
    void setContainerClass(Class containerClass);

    /**
     * INTERNAL:
     * Return the size of container.
     */
    int sizeFor(Object container);

    /**
     * INTERNAL:
     * Return a Vector populated with the contents of container.
     * Added for bug 2766379, must implement a version of vectorFor that
     * handles wrapped objects.
     */
    Vector vectorFor(Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return an instance of the container class with the specified initial capacity.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    Object containerInstance(int initialCapacity);

}
