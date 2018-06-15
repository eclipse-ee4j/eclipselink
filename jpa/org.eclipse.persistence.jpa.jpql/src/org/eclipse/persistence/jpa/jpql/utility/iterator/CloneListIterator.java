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
package org.eclipse.persistence.jpa.jpql.utility.iterator;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;

/**
 * A <code>CloneListIterator</code> iterates over a copy of a list,
 * allowing for concurrent access to the original list.
 * <p>
 * The original list passed to the <code>CloneListIterator</code>'s
 * constructor should be synchronized; otherwise you run the risk of
 * a corrupted list (e.g. {@link java.util.Vector}.
 * <p>
 * By default, a <code>CloneListIterator</code> does not support the
 * modification operations; this is because it does not have
 * access to the original list. But if the <code>CloneListIterator</code>
 * is supplied with a {@link Mutator} it will delegate the
 * modification operations to the {@link Mutator}.
 * Alternatively, a subclass can override the modification methods.
 *
 * @param <E> the type of elements returned by the iterator
 *
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/LiveCloneListIterable.java">LiveCloneListIterable</a>
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/SnapshotCloneListIterable.java">SnapshotCloneListIterable</a>
 */
public class CloneListIterator<E>
    implements ListIterator<E>
{
    private int cursor;
    private final ListIterator<Object> listIterator;
    private final Mutator<E> mutator;
    private State state;

    /**
     * Construct a list iterator on a copy of the specified array.
     * The modification methods will not be supported,
     * unless a subclass overrides them.
     */
    public CloneListIterator(E[] array) {
        this(array, Mutator.ReadOnly.<E>instance());
    }

    /**
     * Construct a list iterator on a copy of the specified array.
     * Use the specified list mutator to modify the original list.
     */
    public CloneListIterator(E[] array, Mutator<E> mutator) {
        this(mutator, array.clone());
    }

    /**
     * Construct a list iterator on a copy of the specified list.
     * The modification methods will not be supported,
     * unless a subclass overrides them.
     */
    public CloneListIterator(List<? extends E> list) {
        this(list, Mutator.ReadOnly.<E>instance());
    }

    /**
     * Construct a list iterator on a copy of the specified list.
     * Use the specified list mutator to modify the original list.
     */
    public CloneListIterator(List<? extends E> list, Mutator<E> mutator) {
        this(mutator, list.toArray());
    }

    /**
     * Internal constructor used by subclasses.
     * Swap order of arguments to prevent collision with other constructor.
     * The passed in array will *not* be cloned.
     */
    protected CloneListIterator(Mutator<E> mutator, Object... array) {
        super();
        // build a copy of the list and keep it in sync with original (if the mutator allows changes)
        // that way the nested list iterator will maintain some of our state
        this.listIterator = CollectionTools.list(array).listIterator();
        this.mutator = mutator;
        this.cursor = 0;
        this.state = State.UNKNOWN;
    }

    @Override
    public void add(E o) {
        // allow the nested iterator to throw an exception before we modify the original list
        this.listIterator.add(o);
        this.add(this.cursor, o);
        this.cursor++;
    }

    /**
     * Add the specified element to the original list.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a {@link Mutator}.
     */
    protected void add(int index, E o) {
        this.mutator.add(index, o);
    }

    @Override
    public boolean hasNext() {
        return this.listIterator.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return this.listIterator.hasPrevious();
    }

    /**
     * The list passed in during construction held elements of type <code>E</code>,
     * so this cast is not a problem. We need this cast because
     * all the elements of the original collection were copied into
     * an object array (<code>Object[]</code>).
     */
    @SuppressWarnings("unchecked")
    protected E nestedNext() {
        return (E) this.listIterator.next();
    }

    /**
     * The list passed in during construction held elements of type <code>E</code>,
     * so this cast is not a problem. We need this cast because
     * all the elements of the original collection were copied into
     * an object array (<code>Object[]</code>).
     */
    @SuppressWarnings("unchecked")
    protected E nestedPrevious() {
        return (E) this.listIterator.previous();
    }

    @Override
    public E next() {
        // allow the nested iterator to throw an exception before we modify the index
        E next = this.nestedNext();
        this.cursor++;
        this.state = State.NEXT;
        return next;
    }

    @Override
    public int nextIndex() {
        return this.listIterator.nextIndex();
    }

    @Override
    public E previous() {
        // allow the nested iterator to throw an exception before we modify the index
        E previous = this.nestedPrevious();
        this.cursor--;
        this.state = State.PREVIOUS;
        return previous;
    }

    @Override
    public int previousIndex() {
        return this.listIterator.previousIndex();
    }

    @Override
    public void remove() {
        // allow the nested iterator to throw an exception before we modify the original list
        this.listIterator.remove();
        if (this.state == State.PREVIOUS) {
            this.remove(this.cursor);
        } else {
            this.cursor--;
            this.remove(this.cursor);
        }
    }

    /**
     * Remove the specified element from the original list.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a {@link Mutator}.
     */
    protected void remove(int index) {
        this.mutator.remove(index);
    }

    @Override
    public void set(E o) {
        // allow the nested iterator to throw an exception before we modify the original list
        this.listIterator.set(o);
        if (this.state == State.PREVIOUS) {
            this.set(this.cursor, o);
        } else {
            this.set(this.cursor - 1, o);
        }
    }

    /**
     * Set the specified element in the original list.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a {@link Mutator}.
     */
    protected void set(int index, E o) {
        this.mutator.set(index, o);
    }

    /**
     * Used by {@link CloneListIterator} to remove
     * elements from the original list; since the list iterator
     * does not have direct access to the original list.
     */
    public interface Mutator<T> {

        /**
         * Add the specified object to the original list.
         */
        void add(int index, T o);

        /**
         * Remove the specified object from the original list.
         */
        void remove(int index);

        /**
         * Set the specified object in the original list.
         */
        void set(int index, T o);


        final class ReadOnly<S>
            implements Mutator<S>, Serializable
        {
            @SuppressWarnings("rawtypes")
            public static final Mutator INSTANCE = new ReadOnly();
            private static final long serialVersionUID = 1L;
            // ensure single instance
            private ReadOnly() {
                super();
            }
            @SuppressWarnings("unchecked")
            public static <R> Mutator<R> instance() {
                return INSTANCE;
            }
            // add is not supported
            @Override
            public void add(int index, Object o) {
                throw new UnsupportedOperationException();
            }
            private Object readResolve() {
                // replace this object with the singleton
                return INSTANCE;
            }
            // remove is not supported
            @Override
            public void remove(int index) {
                throw new UnsupportedOperationException();
            }
            // set is not supported
            @Override
            public void set(int index, Object o) {
                throw new UnsupportedOperationException();
            }
        }
    }

    private enum State {
        NEXT,
        PREVIOUS,
        UNKNOWN
    }
}
