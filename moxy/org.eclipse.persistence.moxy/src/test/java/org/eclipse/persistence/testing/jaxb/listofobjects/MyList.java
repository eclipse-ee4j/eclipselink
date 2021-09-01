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
//     Denise Smith  November 16, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public class MyList<T> implements java.util.List<T>{
    private List internalList;

    public MyList(){
        internalList = new Stack();
    }

    @Override
    public boolean add(Object e) {
        return internalList.add(e);
    }

    @Override
    public void add(int index, Object element) {
        internalList.add(index, element);
    }

    @Override
    public boolean addAll(Collection c) {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return internalList.addAll(index, c);
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    @Override
    public boolean contains(Object o) {
        return internalList.contains(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return internalList.containsAll(c);
    }

    @Override
    public T get(int index) {
        return (T) internalList.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return internalList.indexOf(o);

    }

    @Override
    public boolean isEmpty() {
        return internalList.isEmpty();
    }

    @Override
    public Iterator iterator() {
        return internalList.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return internalList.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return internalList.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return internalList.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        return internalList.remove(o);
    }

    @Override
    public T remove(int index) {
        return (T) internalList.remove(index);
    }

    @Override
    public boolean removeAll(Collection c) {
        return internalList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return internalList.retainAll(c);
    }

    @Override
    public Object set(int index, Object element) {
        return internalList.set(index, element);
    }

    @Override
    public int size() {
        return internalList.size();
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return internalList.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return internalList.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return internalList.toArray(a);
    }

}
