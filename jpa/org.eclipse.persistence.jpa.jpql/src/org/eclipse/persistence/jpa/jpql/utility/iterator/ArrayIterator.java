/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.utility.iterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An <code>ArrayIterator</code> provides a {@link Iterator} for an array of objects.
 *
 * @version 2.5
 * @since 2.4
 */
@SuppressWarnings("nls")
public class ArrayIterator<E> implements Iterator<E> {

    /**
     * The object containing the items to iterate over.
     */
    E[] array;

    /**
     * The last index this {@link ArrayIterator} can iterate over.
     */
    private int maxIndex;

    /**
     * The position of the cursor.
     */
    int nextIndex;

    /**
     * Creates a new <code>ArrayIterator</code> for the specified array.
     *
     * @param array The object containing the items to iterate over
     */
    public <T extends E> ArrayIterator(T... array) {
        this(array, 0, array.length);
    }

    /**
     * Creates a new <code>ArrayIterator</code> for the specified array, starting at the specified
     * start index and continuing for the specified length.
     *
     * @param array The object containing the items to iterate over
     * @param start The beginning of the iteration
     * @param length The length of the iteration
     * @exception IllegalArgumentException The start index is either a negative value or greater than
     * the length of the array or the length to copy goes beyond the length of the array
     */
    public <T extends E> ArrayIterator(T[] array, int start, int length) {

        if ((start < 0) || (start > array.length)) {
            throw new IllegalArgumentException("The start index is either a negative value or greater than the length of the array: " + start);
        }

        if ((length < 0) || (length > array.length - start)) {
            throw new IllegalArgumentException("The length to copy goes beyond the length of the array: " + length);
        }

        this.array     = array;
        this.nextIndex = start;
        this.maxIndex  = start + length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return nextIndex < maxIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E next() {

        if (hasNext()) {
            return array[nextIndex++];
        }

        throw new NoSuchElementException("No more elements can be retrieved.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("An ArrayIterator is read-only.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("(");
        sb.append(Arrays.toString(array));
        sb.append(")");
        return sb.toString();
    }
}
