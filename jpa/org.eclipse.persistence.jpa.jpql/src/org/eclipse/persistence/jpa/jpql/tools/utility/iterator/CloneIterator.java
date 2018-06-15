/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.utility.iterator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.persistence.jpa.jpql.utility.iterator.ArrayIterator;

/**
 * A <code>CloneIterator</code> iterates over a copy of a collection,
 * allowing for concurrent access to the original collection.
 * <p>
 * The original collection passed to the <code>CloneIterator</code>'s
 * constructor should be synchronized (e.g. {@link java.util.Vector});
 * otherwise you run the risk of a corrupted collection.
 * <p>
 * By default, a <code>CloneIterator</code> does not support the
 * {@link #remove()} operation; this is because it does not have
 * access to the original collection. But if the <code>CloneIterator</code>
 * is supplied with an {@link Remover} it will delegate the
 * {@link #remove()} operation to the {@link Remover}.
 * Alternatively, a subclass can override the {@link #remove(Object)}
 * method.
 *
 * @param <E> the type of elements returned by the iterator
 *
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/LiveCloneIterable.java">LiveCloneIterable</a>
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/SnapshotCloneIterable.java">SnapshotCloneIterable</a>
 */
public class CloneIterator<E>
    implements Iterator<E>
{
    private final Iterator<Object> iterator;
    private E current;
    private final Remover<E> remover;
    private boolean removeAllowed;


    // ********** constructors **********

    /**
     * Construct an iterator on a copy of the specified collection.
     * The {@link #remove()} method will not be supported,
     * unless a subclass overrides the {@link #remove(Object)}.
     */
    public CloneIterator(Collection<? extends E> collection) {
        this(collection, Remover.ReadOnly.<E>instance());
    }

    /**
     * Construct an iterator on a copy of the specified array.
     * The {@link #remove()} method will not be supported,
     * unless a subclass overrides the {@link #remove(Object)}.
     */
    public CloneIterator(E[] array) {
        this(array, Remover.ReadOnly.<E>instance());
    }

    /**
     * Construct an iterator on a copy of the specified collection.
     * Use the specified remover to remove objects from the
     * original collection.
     */
    public CloneIterator(Collection<? extends E> collection, Remover<E> remover) {
        this(remover, collection.toArray());
    }

    /**
     * Construct an iterator on a copy of the specified array.
     * Use the specified remover to remove objects from the
     * original array.
     */
    public CloneIterator(E[] array, Remover<E> remover) {
        this(remover, array.clone());
    }

    /**
     * Internal constructor used by subclasses.
     * Swap order of arguments to prevent collision with other constructor.
     * The passed in array will *not* be cloned.
     */
    protected CloneIterator(Remover<E> remover, Object... array) {
        super();
        this.iterator = new ArrayIterator<Object>(array);
        this.current = null;
        this.remover = remover;
        this.removeAllowed = false;
    }


    // ********** Iterator implementation **********

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public E next() {
        this.current = this.nestedNext();
        this.removeAllowed = true;
        return this.current;
    }

    @Override
    public void remove() {
        if ( ! this.removeAllowed) {
            throw new IllegalStateException();
        }
        this.remove(this.current);
        this.removeAllowed = false;
    }


    // ********** internal methods **********

    /**
     * The collection passed in during construction held elements of type <code>E</code>,
     * so this cast is not a problem. We need this cast because
     * all the elements of the original collection were copied into
     * an object array (<code>Object[]</code>).
     */
    @SuppressWarnings("unchecked")
    protected E nestedNext() {
        return (E) this.iterator.next();
    }

    /**
     * Remove the specified element from the original collection.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a {@link Remover}.
     */
    protected void remove(E e) {
        this.remover.remove(e);
    }


    //********** member interface **********

    /**
     * Used by {@link CloneIterator} to remove
     * elements from the original collection; since the iterator
     * does not have direct access to the original collection.
     */
    public interface Remover<T> {

        /**
         * Remove the specified object from the original collection.
         */
        void remove(T element);


        final class ReadOnly<S>
            implements Remover<S>, Serializable
        {
            @SuppressWarnings("rawtypes")
            public static final Remover INSTANCE = new ReadOnly();
            @SuppressWarnings("unchecked")
            public static <R> Remover<R> instance() {
                return INSTANCE;
            }
            // ensure single instance
            private ReadOnly() {
                super();
            }
            // remove is not supported
            @Override
            public void remove(Object element) {
                throw new UnsupportedOperationException();
            }
            private static final long serialVersionUID = 1L;
            private Object readResolve() {
                // replace this object with the singleton
                return INSTANCE;
            }
        }
    }
}
