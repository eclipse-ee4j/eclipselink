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
 * An <code>IteratorEnumeration</code> wraps an
 * <code>Iterator</code> so that it can be treated like an
 * <code>Enumeration</code>.
 */
public class IteratorEnumeration implements Enumeration {
    private Iterator iterator;

    /**
     * Construct an enumeration that wraps the specified iterator.
     */
    public IteratorEnumeration(Iterator iterator) {
        super();
        this.iterator = iterator;
    }

    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    /**
     * @see java.util.Enumeration#nextElement()
     */
    public Object nextElement() {
        return this.iterator.next();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this) + '(' + this.iterator + ')';
    }

}
