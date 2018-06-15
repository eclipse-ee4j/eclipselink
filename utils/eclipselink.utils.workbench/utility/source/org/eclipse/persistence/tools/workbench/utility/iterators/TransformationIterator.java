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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;



/**
 * A <code>TransformationIterator</code> wraps another <code>Iterator</code>
 * and transforms its results for client consumption. To use, supply a
 * <code>Transformer</code> or subclass <code>TransformationIterator</code>
 * and override the <code>transform(Object)</code> method.
 */
public class TransformationIterator implements Iterator {
    private Iterator nestedIterator;
    private Transformer transformer;

    /**
     * Construct an iterator with the specified nested iterator
     * and a transformer that simply returns the object, unchanged.
     * Use this constructor if you want to override the
     * <code>transform(Object)</code> method instead of building
     * a <code>Transformer</code>.
     */
    public TransformationIterator(Iterator nestedIterator) {
        this(nestedIterator, Transformer.NULL_INSTANCE);
    }

    /**
     * Construct an iterator with the specified nested iterator
     * and transformer.
     */
    public TransformationIterator(Iterator nestedIterator, Transformer transformer) {
        super();
        this.nestedIterator = nestedIterator;
        this.transformer = transformer;
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
        // transform the object returned by the nested iterator before returning it
        return this.transform(this.nestedIterator.next());
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        // delegate to the nested iterator
        this.nestedIterator.remove();
    }

    /**
     * Transform the specified object and return the result.
     */
    protected Object transform(Object next) {
        return this.transformer.transform(next);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this) + '(' + this.nestedIterator + ')';
    }

}
