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
package org.eclipse.persistence.internal.helper;

import java.util.*;

/**
 * Vector subclass that removes the synchronization.
 */
public class NonSynchronizedVector extends Vector {
    public static NonSynchronizedVector newInstance(int initialCapacity, int capacityIncrement) {
        return new NonSynchronizedVector(initialCapacity, capacityIncrement);
    }

    public static NonSynchronizedVector newInstance(int initialCapacity) {
        return new NonSynchronizedVector(initialCapacity);
    }

    public static NonSynchronizedVector newInstance() {
        return new NonSynchronizedVector();
    }

    public static NonSynchronizedVector newInstance(Collection c) {
        return new NonSynchronizedVector(c);
    }

    public NonSynchronizedVector(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public NonSynchronizedVector(int initialCapacity) {
        super(initialCapacity);
    }

    public NonSynchronizedVector() {
        super();
    }

    public NonSynchronizedVector(Collection c) {
        super(c);
    }

    public void copyInto(Object anArray[]) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    public void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (elementCount < oldCapacity) {
            Object oldData[] = elementData;
            elementData = new Object[elementCount];
            System.arraycopy(oldData, 0, elementData, 0, elementCount);
        }
    }

    public void ensureCapacity(int minCapacity) {
        modCount++;
        ensureCapacityHelper(minCapacity);
    }

    private void ensureCapacityHelper(int minCapacity) {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            Object oldData[] = elementData;
            int newCapacity = (capacityIncrement > 0) ?
            (oldCapacity + capacityIncrement) : (oldCapacity * 2);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = new Object[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, elementCount);
        }
    }

    public void setSize(int newSize) {
        modCount++;
        if (newSize > elementCount) {
            ensureCapacityHelper(newSize);
        } else {
            for (int i = newSize ; i < elementCount ; i++) {
            elementData[i] = null;
            }
        }
        elementCount = newSize;
    }

    public int capacity() {
        return elementData.length;
    }

    public Object clone() {
        return new NonSynchronizedVector(this);
    }

    public int size() {
        return elementCount;
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }

    public Enumeration elements() {
        return new Enumeration() {
            int count = 0;

            public boolean hasMoreElements() {
                return count < elementCount;
            }

            public Object nextElement() {
                if (count < elementCount) {
                    return elementData[count++];
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

    public int indexOf(Object elem, int index) {
        if (elem == null) {
            for (int i = index ; i < elementCount ; i++)
            if (elementData[i]==null)
                return i;
        } else {
            for (int i = index ; i < elementCount ; i++)
            if (elem.equals(elementData[i]))
                return i;
        }
        return -1;
    }

    public int lastIndexOf(Object elem) {
        return lastIndexOf(elem, elementCount-1);
    }

    public int lastIndexOf(Object elem, int index) {
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);
        if (elem == null) {
            for (int i = index; i >= 0; i--)
            if (elementData[i]==null)
                return i;
        } else {
            for (int i = index; i >= 0; i--)
            if (elem.equals(elementData[i]))
                return i;
        }
        return -1;
    }

    public Object elementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        }
        return elementData[index];
    }

    public Object firstElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return elementData[0];
    }

    public Object lastElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return elementData[elementCount - 1];
    }

    public void setElementAt(Object obj, int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                                 elementCount);
        }
        elementData[index] = obj;
    }

    public void removeElementAt(int index) {
        modCount++;
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                                 elementCount);
        } else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        elementCount--;
        elementData[elementCount] = null; /* to let gc do its work */
    }

    public void insertElementAt(Object obj, int index) {
        modCount++;
        if (index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index
                                 + " > " + elementCount);
        }
        ensureCapacityHelper(elementCount + 1);
        System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
        elementData[index] = obj;
        elementCount++;
    }

    public void addElement(Object obj) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = obj;
    }

    public boolean removeElement(Object obj) {
        modCount++;
        int i = indexOf(obj);
        if (i >= 0) {
            removeElementAt(i);
            return true;
        }
        return false;
    }

    public void removeAllElements() {
        modCount++;
        // Let gc do its work
        for (int i = 0; i < elementCount; i++)
            elementData[i] = null;

        elementCount = 0;
    }

    public Object[] toArray() {
        Object[] result = new Object[elementCount];
        System.arraycopy(elementData, 0, result, 0, elementCount);
        return result;
    }

    public Object[] toArray(Object a[]) {
        if (a.length < elementCount)
            a = (Object[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), elementCount);

        System.arraycopy(elementData, 0, a, 0, elementCount);

        if (a.length > elementCount)
            a[elementCount] = null;

        return a;
    }

    public Object get(int index) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        return elementData[index];
    }

    public Object set(int index, Object element) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        Object oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    public boolean add(Object o) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = o;
        return true;
    }

    public Object remove(int index) {
        modCount++;
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        Object oldValue = elementData[index];

        int numMoved = elementCount - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                     numMoved);
        elementData[--elementCount] = null; // Let gc do its work

        return oldValue;
    }

    public boolean containsAll(Collection c) {
        Iterator e = c.iterator();
        while (e.hasNext())
            if(!contains(e.next()))
                return false;

        return true;
    }

    public boolean addAll(Collection c) {
        modCount++;
            Object[] a = c.toArray();
            int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);
            System.arraycopy(a, 0, elementData, elementCount, numNew);
            elementCount += numNew;
        return numNew != 0;
    }

    public boolean removeAll(Collection c) {
        boolean modified = false;
        Iterator e = iterator();
        while (e.hasNext()) {
            if(c.contains(e.next())) {
            e.remove();
            modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(Collection c)  {
        boolean modified = false;
        Iterator e = iterator();
        while (e.hasNext()) {
            if(!c.contains(e.next())) {
            e.remove();
            modified = true;
            }
        }
        return modified;
    }

    public boolean addAll(int index, Collection c) {
        modCount++;
        if (index < 0 || index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

            Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);

        int numMoved = elementCount - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                     numMoved);

            System.arraycopy(a, 0, elementData, index, numNew);
        elementCount += numNew;
        return numNew != 0;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        List list = (List) o;
        int size = size();
        if (list.size() != size) {
            return false;
        }
        for (int index = 0; index < size; index++) {
            Object left = get(index);
            Object right = list.get(index);
            if ((left != right) && ((left == null) || (right == null) || (!left.equals(right)))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        int size = size();
        for (int index = 0; index < size; index++) {
            Object obj = get(index);
            hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        Iterator e = iterator();
        buf.append("[");
        int maxIndex = size() - 1;
        for (int i = 0; i <= maxIndex; i++) {
            buf.append(String.valueOf(e.next()));
            if (i < maxIndex)
            buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = elementCount - toIndex;
            System.arraycopy(elementData, toIndex, elementData, fromIndex,
                             numMoved);

        // Let gc do its work
        int newElementCount = elementCount - (toIndex-fromIndex);
        while (elementCount != newElementCount)
            elementData[--elementCount] = null;
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {
        s.defaultWriteObject();
    }

    public List subList(int fromIndex, int toIndex) {
        return new NonSynchronizedSubVector(this, fromIndex, toIndex);
    }

}

