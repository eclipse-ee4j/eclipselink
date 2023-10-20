/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.indirection;

/**
 * A linear collection that has a well-defined order.
 * Provides methods for a convenient access to elements at both ends.
 *
 * @param <E> the type of elements in this collection
 * @since XXX
 */
public interface IndirectSequencedCollection<E> {

    /**
     * Adds an element as the first element of this collection.
     * After this operation completes normally, the given element will be a member of
     * this collection, and it will be the first element in encounter order.
     *
     * @param e the element to be added
     */
    void addFirst(E e);

    /**
     * Adds an element as the last element of this collection.
     * After this operation completes normally, the given element will be a member of
     * this collection, and it will be the last element in encounter order.
     *
     * @param e the element to be added.
     */
    void addLast(E e);

    /**
     * Gets the first element of this collection.
     *
     * @return the retrieved element
     */
    E getFirst();

    /**
     * Gets the last element of this collection.
     *
     * @return the retrieved element
     */
    E getLast();

    /**
     * Removes and returns the first element of this collection.
     *
     * @return the removed element
     */
    E removeFirst();

    /**
     * Removes and returns the last element of this collection.
     *
     * @return the removed element
     */
    E removeLast();
}
