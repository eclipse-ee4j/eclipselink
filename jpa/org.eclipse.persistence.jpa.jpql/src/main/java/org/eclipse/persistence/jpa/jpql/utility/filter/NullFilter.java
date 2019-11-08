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
package org.eclipse.persistence.jpa.jpql.utility.filter;

/**
 * A <code>null</code> implementation of a <code>Filter</code>. The singleton
 * instance can be typed cast properly when using generics.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class NullFilter implements Filter<Object> {

    /**
     * The singleton instance of this <code>NullFilter</code>.
     */
    private static final NullFilter INSTANCE = new NullFilter();

    /**
     * Creates a new <code>NullFilter</code>.
     */
    private NullFilter() {
        super();
    }

    /**
     * Returns the singleton instance of this <code>NullFilter</code>.
     *
     * @return The singleton instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Filter<T> instance() {
        return (Filter<T>) INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Object value) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
