/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.util.*;

/**
 * SubList that implements Vector.
 */
public class NonSynchronizedSubVector extends NonSynchronizedVector {
    private Vector l;
    private int offset;
    private int size;

    public NonSynchronizedSubVector(Vector list, int fromIndex, int toIndex) {
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

    public Object set(int index, Object element) {
        return l.set(index+offset, element);
    }
    
    public void setElementAt(Object obj, int index) {
        set(index, obj);
    }
    
    public Object elementAt(int index) {
        return get(index);
    }
    
    public Object firstElement() {
        return get(0);
    }    
    
    public Object lastElement() {
        return get(size() - 1);
    }
    
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
    
    public Object get(int index) {
        return l.get(index+offset);
    }

    public int size() {
        return size;
    }

    public void add(int index, Object element) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException();
        l.add(index+offset, element);
        size++;
        modCount++;
    }

    public Object remove(int index) {
        Object result = l.remove(index+offset);
        size--;
        modCount++;
        return result;
    }

    /*protected void removeRange(int fromIndex, int toIndex) {
        l.removeRange(fromIndex+offset, toIndex+offset);
        size -= (toIndex-fromIndex);
        modCount++;
    }*/

    public boolean addAll(Collection c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection c) {
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

    public Enumeration elements() {
        return new Enumeration() {
            int count = 0;
    
            public boolean hasMoreElements() {
                return count < size();
            }
    
            public Object nextElement() {
                if (count < elementCount) {
                    return get(count++);
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }
        
    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(l.toArray(), offset, result, 0, size);
        return result;
    }

    public Object[] toArray(Object a[]) {
        if (a.length < size)
            a = (Object[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        
        System.arraycopy(l.toArray(), offset, a, 0, size);

        if (a.length > size)
            a[size] = null;

        return a;
    }

    public Iterator iterator() {
        return listIterator();
    }

    public ListIterator listIterator(final int index) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);

        return new ListIterator() {
            private ListIterator i = l.listIterator(index+offset);

            public boolean hasNext() {
                return nextIndex() < size;
            }

            public Object next() {
                if (hasNext())
                    return i.next();
                else
                    throw new NoSuchElementException();
            }

            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            public Object previous() {
                if (hasPrevious())
                    return i.previous();
                else
                    throw new NoSuchElementException();
            }

            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            public void remove() {
                i.remove();
                size--;
                modCount++;
            }

            public void set(Object o) {
                i.set(o);
            }

            public void add(Object o) {
                i.add(o);
                size++;
                modCount++;
            }
        };
    }
}

