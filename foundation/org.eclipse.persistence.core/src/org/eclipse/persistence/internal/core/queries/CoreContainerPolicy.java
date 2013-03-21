/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.core.queries;

import java.util.Vector;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

public interface CoreContainerPolicy<ABSTRACT_SESSION extends CoreAbstractSession> {

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of JDK 1.1 and 1.2.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed
     */
    public boolean addInto(Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of type.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed.
     */
    public boolean addInto(Object key, Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Remove all the elements from the specified container.
     * Valid only for certain subclasses.
     */
    public void clear(Object container);

    /**
     * INTERNAL:
     * Return an instance of the container class.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    public Object containerInstance();

    /**
     * INTERNAL:
     * Check if the object is contained in the collection.
     * This is used to check contains in a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public boolean contains(Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return whether the iterator has more objects.
     * The iterator is the one returned from #iteratorFor().
     * Valid for some subclasses only.
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    public boolean hasNext(Object iterator);

    /**
     * INTERNAL:
     * Return whether the container is empty.
     */
    public boolean isEmpty(Object container);

    public boolean isListPolicy();

    /**
     * INTERNAL:
     * Return an iterator for the given container.
     * This iterator can then be used as a parameter to #hasNext()
     * and #next().
     *
     * @see ContainerPolicy#hasNext(java.lang.Object)
     * @see ContainerPolicy#next(java.lang.Object)
     */
    public Object iteratorFor(Object container);

    /**
     * INTERNAL:
     * Return the next object from the iterator.
     * This is used to stream over a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public Object next(Object iterator, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * 
     * In the case of a Map, this will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    public Object nextEntry(Object iterator);

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * 
     * In the case of a Map, this will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(Object iterator, AbstractSession session)
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    public Object nextEntry(Object iterator, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Remove the object from the collection.
     * This is used to remove from a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public boolean removeFrom(Object element, Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Set the class used for the container.
     */
    public void setContainerClass(Class containerClass);

    /**
     * INTERNAL:
     * Return the size of container.
     */
    public int sizeFor(Object container);

    /**
     * INTERNAL:
     * Return a Vector populated with the contents of container.
     * Added for bug 2766379, must implement a version of vectorFor that
     * handles wrapped objects.
     */
    public Vector vectorFor(Object container, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return an instance of the container class with the specified initial capacity.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    public Object containerInstance(int initialCapacity);

}
