/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.utility.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A <code>null</code> instance of an {@link Iterator}.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public final class EmptyIterator implements Iterator<Object> {

    /**
     * The singleton instance this <code>EmptyIterator</code>.
     */
    private static EmptyIterator INSTANCE = new EmptyIterator();

    /**
     * Ensure non-instantiability.
     */
    private EmptyIterator() {
        super();
    }

    /**
     * Returns the singleton instance this <code>EmptyIterator</code>.
     *
     * @return The singleton instance this <code>EmptyIterator</code>
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> Iterator<T> instance() {
        return (Iterator<T>) INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Object next() {
        throw new NoSuchElementException("A NullIterator is read-only.");
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        throw new UnsupportedOperationException("A NullIterator is read-only.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
