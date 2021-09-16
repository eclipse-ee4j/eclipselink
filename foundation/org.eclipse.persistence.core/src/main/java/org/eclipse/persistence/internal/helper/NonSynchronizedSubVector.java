/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.util.*;

/**
 * SubList that implements Vector.
 */
public class NonSynchronizedSubVector<E> extends NonSynchronizedVector<E> {
    private Vector<E> l;
    private int offset;
    private int size;

    public NonSynchronizedSubVector(Vector<E> list, int fromIndex, int toIndex) {
        super(0);
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
        l = list;
        offset = fromIndex;
        size = toIndex - fromIndex;
    }

    @Override
    public E set(int index, E element) {
        return l.set(index+offset, element);
    }

    @Override
    public void setElementAt(E obj, int index) {
        set(index, obj);
    }

    @Override
    public E elementAt(int index) {
        return get(index);
    }

    @Override
    public E firstElement() {
        return get(0);
    }

    @Override
    public E lastElement() {
        return get(size() - 1);
    }

    @Override
    public int indexOf(Object elem, int index) {
        int size = size();
        if (elem == null) {
            for (int i = index ; i < size ; i++)
            if (get(i)==null)
                return i;
        } else {
            for (int i = index ; i < size ; i++)
            if (elem.equals(get(i)))
                return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object elem, int index) {
        int size = size();
        if (index >= size)
            throw new IndexOutOfBoundsException(index + " >= "+ size);
        if (elem == null) {
            for (int i = index; i >= 0; i--)
            if (get(i)==null)
                return i;
        } else {
            for (int i = index; i >= 0; i--)
            if (elem.equals(get(i)))
                return i;
        }
        return -1;
    }

    @Override
    public E get(int index) {
        return l.get(index+offset);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException();
        l.add(index+offset, element);
        size++;
        modCount++;
    }

    @Override
    public E remove(int index) {
        E result = l.remove(index+offset);
        size--;
        modCount++;
        return result;
    }

    /*protected void removeRange(int fromIndex, int toIndex) {
        l.removeRange(fromIndex+offset, toIndex+offset);
        size -= (toIndex-fromIndex);
        modCount++;
    }*/

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);
        int cSize = c.size();
        if (cSize==0)
            return false;
        l.addAll(offset+index, c);
        size += cSize;
        modCount++;
        return true;
    }

    @Override
    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;

            @Override
            public boolean hasMoreElements() {
                return count < size();
            }

            @Override
            public E nextElement() {
                if (count < elementCount) {
                    return get(count++);
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(l.toArray(), offset, result, 0, size);
        return result;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            return (T[]) Arrays.copyOfRange(elementData, offset, size, a.getClass());

        System.arraycopy(l.toArray(), offset, a, 0, size);

        if (a.length > size)
            a[size] = null;

        return a;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);

        return new ListIterator<E>() {
            private ListIterator<E> i = l.listIterator(index+offset);

            @Override
            public boolean hasNext() {
                return nextIndex() < size;
            }

            @Override
            public E next() {
                if (hasNext())
                    return i.next();
                else
                    throw new NoSuchElementException();
            }

            @Override
            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            @Override
            public E previous() {
                if (hasPrevious())
                    return i.previous();
                else
                    throw new NoSuchElementException();
            }

            @Override
            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            @Override
            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            @Override
            public void remove() {
                i.remove();
                size--;
                modCount++;
            }

            @Override
            public void set(E o) {
                i.set(o);
            }

            @Override
            public void add(E o) {
                i.add(o);
                size++;
                modCount++;
            }
        };
    }
}

