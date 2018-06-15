/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * An <code>ArrayListIterator</code> provides a <code>ListIterator</code>
 * for an array of objects.
 *
 * The name might be a bit confusing:
 * This is a <code>ListIterator</code> for an <code>Array</code>;
 * <em>not</em> an <code>Iterator</code> for an <code>ArrayList</code>.
 */
public class ArrayListIterator
    extends ArrayIterator
    implements ListIterator
{
    private int minIndex;

    /**
     * Construct a list iterator for the specified array.
     */
    public ArrayListIterator(Object[] array) {
        this(array, 0, array.length);
    }

    /**
     * Construct a list iterator for the specified array,
     * starting at the specified start index and continuing for
     * the specified length.
     */
    public ArrayListIterator(Object[] array, int start, int length) {
        super(array, start, length);
        this.minIndex = start;
    }

    /**
     * @see java.util.ListIterator#nextIndex()
     */
    public int nextIndex() {
        return this.nextIndex;
    }

    /**
     * @see java.util.ListIterator#previousIndex()
     */
    public int previousIndex() {
        return this.nextIndex - 1;
    }

    /**
     * @see java.util.ListIterator#hasPrevious()
     */
    public boolean hasPrevious() {
        return this.nextIndex > this.minIndex;
    }

    /**
     * @see java.util.ListIterator#previous()
     */
    public Object previous() {
        if (this.hasPrevious()) {
            return this.array[--this.nextIndex];
        }
        throw new NoSuchElementException();
    }

    /**
     * @see java.util.ListIterator#add(java.lang.Object)
     */
    public void add(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.ListIterator#set(java.lang.Object)
     */
    public void set(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString();
    }

}
