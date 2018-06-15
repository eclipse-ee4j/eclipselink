/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterator.CloneIterator;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;

/**
 * A <code>SnapshotCloneIterable</code> returns an iterator on a "snapshot" of a
 * collection, allowing for concurrent access to the original collection. A
 * copy of the collection is created when the iterable is constructed.
 * As a result, the contents of the collection will be the same with
 * every call to {@link #iterator()}.
 * <p>
 * The original collection passed to the <code>SnapshotCloneIterable</code>'s
 * constructor should be thread-safe (e.g. {@link java.util.Vector});
 * otherwise you run the risk of a corrupted collection.
 * <p>
 * By default, the iterator returned by a <code>SnapshotCloneIterable</code> does not
 * support the {@link Iterator#remove()} operation; this is because it does not
 * have access to the original collection. But if the <code>SnapshotCloneIterable</code>
 * is supplied with a {@link CloneIterator.Remover}
 * it will delegate the
 * {@link Iterator#remove()} operation to the <code>Remover</code>.
 * Alternatively, a subclass can override the iterable's {@link #remove(Object)}
 * method.
 * <p>
 * This iterable is useful for multiple passes over a collection that should not
 * be changed (e.g. by another thread) between passes.
 *
 * @param <E> the type of elements returned by the iterable's iterator
 *
 * @see CloneIterator
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/LiveCloneIterable.java">LiveCloneIterable</a>
 * @see org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable SnapshotCloneListIterable
 */
public class SnapshotCloneIterable<E> extends CloneIterable<E> {

    private final Object[] array;


    // ********** constructors **********

    /**
     * Construct a "snapshot" iterable for the specified iterator.
     * The {@link Iterator#remove()} operation will not be supported
     * by the iterator returned by {@link #iterator()}
     * unless a subclass overrides the iterable's {@link #remove(Object)}
     * method.
     */
    public SnapshotCloneIterable(Iterator<? extends E> iterator) {
        super();
        this.array = CollectionTools.array(Object.class, iterator);
    }

    /**
     * Construct a "snapshot" iterable for the specified iterator.
     * The specified remover will be used by any generated iterators to
     * remove objects from the original collection.
     */
    public SnapshotCloneIterable(Iterator<? extends E> iterator, CloneIterator.Remover<E> remover) {
        super(remover);
        this.array =CollectionTools.array(Object.class, iterator);
    }

    /**
     * Construct a "snapshot" iterable for the specified collection.
     * The {@link Iterator#remove()} operation will not be supported
     * by the iterator returned by {@link #iterator()}
     * unless a subclass overrides the iterable's {@link #remove(Object)}
     * method.
     */
    public SnapshotCloneIterable(Collection<? extends E> collection) {
        super();
        this.array = collection.toArray();
    }

    /**
     * Construct a "snapshot" iterable for the specified collection.
     * The specified remover will be used by any generated iterators to
     * remove objects from the original collection.
     */
    public SnapshotCloneIterable(Collection<? extends E> collection, CloneIterator.Remover<E> remover) {
        super(remover);
        this.array = collection.toArray();
    }


    // ********** Iterable implementation **********

    @Override
    public Iterator<E> iterator() {
        return new LocalCloneIterator<E>(this.remover, this.array);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.array);
    }


    // ********** clone iterator **********

    /**
     * provide access to "internal" constructor
     */
    protected static class LocalCloneIterator<E> extends CloneIterator<E> {
        protected LocalCloneIterator(Remover<E> remover, Object[] array) {
            super(remover, array);
        }
    }
}
