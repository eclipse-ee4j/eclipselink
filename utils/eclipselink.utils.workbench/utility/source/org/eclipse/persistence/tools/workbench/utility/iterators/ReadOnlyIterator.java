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

import java.util.Iterator;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>ReadOnlyIterator</code> wraps another <code>Iterator</code>
 * and removes support for #remove().
 */
public class ReadOnlyIterator implements Iterator {
    private Iterator nestedIterator;

    /**
     * Construct an iterator on the specified collection that
     * disallows removes.
     */
    public ReadOnlyIterator(Collection c) {
        this(c.iterator());
    }

    /**
     * Construct an iterator with the specified nested iterator
     * and disallow removes.
     */
    public ReadOnlyIterator(Iterator nestedIterator) {
        super();
        this.nestedIterator = nestedIterator;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        // delegate to the nested iterator
        return this.nestedIterator.hasNext();
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        // delegate to the nested iterator
        return this.nestedIterator.next();
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this) + '(' + this.nestedIterator + ')';
    }

}
