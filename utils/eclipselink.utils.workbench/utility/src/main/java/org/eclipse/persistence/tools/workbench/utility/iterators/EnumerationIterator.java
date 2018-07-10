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
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * An <code>EnumerationIterator</code> wraps an
 * <code>Enumeration</code> so that it can be treated like an
 * <code>Iterator</code>.
 */
public class EnumerationIterator implements Iterator {
    private Enumeration enumeration;

    /**
     * Construct an iterator that wraps the specified enumeration.
     */
    public EnumerationIterator(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    /**
     * @see Iterator#hasNext()
     */
    public boolean hasNext() {
        return this.enumeration.hasMoreElements();
    }

    /**
     * @see Iterator#next()
     */
    public Object next() {
        return this.enumeration.nextElement();
    }

    /**
     * @see Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this) + '(' + this.enumeration + ')';
    }

}
