/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ExtendedList6<FOO extends Foo> implements List<FOO> {

    private ArrayList<FOO> foos = new ArrayList<FOO>();

    @Override
    public int size() {
        return foos.size();
    }

    @Override
    public boolean isEmpty() {
        return foos.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return foos.contains(o);
    }

    @Override
    public Iterator<FOO> iterator() {
        return foos.iterator();
    }

    @Override
    public Object[] toArray() {
        return foos.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return foos.toArray(a);
    }

    @Override
    public boolean add(FOO e) {
        return foos.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return foos.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return foos.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends FOO> c) {
        return foos.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends FOO> c) {
        return foos.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return foos.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return foos.removeAll(c);
    }

    @Override
    public void clear() {
        foos.clear();
    }

    @Override
    public FOO get(int index) {
        return foos.get(index);
    }

    @Override
    public FOO set(int index, FOO element) {
        return foos.set(index, element);
    }

    @Override
    public void add(int index, FOO element) {
        foos.add(index, element);
    }

    @Override
    public FOO remove(int index) {
        return foos.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return foos.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return foos.lastIndexOf(o);
    }

    @Override
    public ListIterator<FOO> listIterator() {
        return foos.listIterator();
    }

    @Override
    public ListIterator<FOO> listIterator(int index) {
        return foos.listIterator(index);
    }

    @Override
    public List<FOO> subList(int fromIndex, int toIndex) {
        return foos.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        ExtendedList6 test = (ExtendedList6) obj;
        if(size() != test.size()) {
            return false;
        }
        for(int x=0; x<size(); x++) {
            if(!get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
