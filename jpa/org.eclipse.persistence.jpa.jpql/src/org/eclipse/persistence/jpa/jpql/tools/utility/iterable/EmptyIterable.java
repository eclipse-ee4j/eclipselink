/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.utility.iterable;

import java.io.Serializable;
import java.util.Iterator;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterator.EmptyIterator;

/**
 * An <code>EmptyIterable</code> is just that. Maybe just a touch better-performing than {@link
 * java.util.Collections#EMPTY_SET} since we don't create a new {@link Iterator} every time {@link
 * #iterator()} is called. (Not sure why they do that....)
 *
 * @param <E> the type of elements returned by the iterable's iterator
 *
 * @see EmptyIterator
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/EmptyListIterable.java">EmptyListIterable</a>
 *
 * @version 2.5
 * @since 2.5
 */
@SuppressWarnings("nls")
public final class EmptyIterable<E> implements Iterable<E>, Serializable {

    /**
     * The singleton instance of this <code>EmptyIterable</code>.
     */
    private static final Iterable<Object> INSTANCE = new EmptyIterable<Object>();

    /**
     * The serial version UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>EmptyIterable</code> and insures single instance.
     */
    private EmptyIterable() {
        super();
    }

    /**
     * Return the singleton instance of this {@link Iterable}.
     *
     * @return The singleton instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> instance() {
        return (Iterable<T>) INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return EmptyIterator.instance();
    }

    private Object readResolve() {
        // Replace this object with the singleton
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[]";
    }
}
