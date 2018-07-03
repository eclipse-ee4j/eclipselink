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
package org.eclipse.persistence.tools.workbench.utility.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This implementation of StringHolder associates a collection of objects
 * with the string.
 */
public class CollectionStringHolder extends AbstractStringHolder {
    /** The objects associated with the string holder's string. */
    protected final Collection collection;

    /** The string associated with the string holder's objects. */
    protected final String string;


    public CollectionStringHolder(String string) {
        super();
        this.collection = new ArrayList();
        this.string = string;
    }

    public final String getString() {
        return this.string;
    }

    public boolean add(Object o) {
        return this.collection.add(o);
    }

    public boolean addAll(Collection c) {
        return this.collection.addAll(c);
    }

    public void clear() {
        this.collection.clear();
    }

    public boolean contains(Object o) {
        return this.collection.contains(o);
    }

    public boolean containsAll(Collection c) {
        return this.collection.containsAll(c);
    }

    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    public Iterator iterator() {
        return this.collection.iterator();
    }

    public boolean remove(Object o) {
        return this.collection.remove(o);
    }

    public boolean removeAll(Collection c) {
        return this.collection.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return this.collection.retainAll(c);
    }

    public int size() {
        return this.collection.size();
    }

    public Object[] toArray() {
        return this.collection.toArray();
    }

    public Object[] toArray(Object[] a) {
        return this.collection.toArray(a);
    }

}
