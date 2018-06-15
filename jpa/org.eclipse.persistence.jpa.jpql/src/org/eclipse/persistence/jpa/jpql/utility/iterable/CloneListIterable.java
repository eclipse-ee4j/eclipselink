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
package org.eclipse.persistence.jpa.jpql.utility.iterable;

import org.eclipse.persistence.jpa.jpql.utility.iterator.CloneListIterator;

/**
 * Pull together mutator state and behavior for subclasses.
 *
 * @param <E> the type of elements returned by the list iterable's list iterator
 *
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/LiveCloneListIterable.java">LiveCloneListIterable</a>
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/SnapshotCloneListIterable.java">SnapshotCloneListIterable</a>
 */
public abstract class CloneListIterable<E>
    implements ListIterable<E>
{
    final CloneListIterator.Mutator<E> mutator;


    // ********** constructors **********

    protected CloneListIterable() {
        super();
        this.mutator = this.buildDefaultMutator();
    }

    protected CloneListIterable(CloneListIterator.Mutator<E> mutator) {
        super();
        if (mutator == null) {
            throw new NullPointerException();
        }
        this.mutator = mutator;
    }

    protected CloneListIterator.Mutator<E> buildDefaultMutator() {
        return new DefaultMutator();
    }


    // ********** default mutations **********

    /**
     * At the specified index, add the specified element to the original list.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a
     * {@link CloneListIterator.Mutator}.
     */
    protected void add(@SuppressWarnings("unused") int index, @SuppressWarnings("unused") E element) {
        throw new RuntimeException("This method was not overridden."); //$NON-NLS-1$
    }

    /**
     * Remove the element at the specified index from the original list.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a
     * {@link CloneListIterator.Mutator}.
     */
    protected void remove(@SuppressWarnings("unused") int index) {
        throw new RuntimeException("This method was not overridden."); //$NON-NLS-1$
    }

    /**
     * At the specified index, set the specified element in the original list.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a
     * {@link CloneListIterator.Mutator}.
     */
    protected void set(@SuppressWarnings("unused") int index, @SuppressWarnings("unused") E element) {
        throw new RuntimeException("This method was not overridden."); //$NON-NLS-1$
    }


    //********** default mutator **********

    protected class DefaultMutator implements CloneListIterator.Mutator<E> {
        @Override
        public void add(int index, E element) {
            CloneListIterable.this.add(index, element);
        }
        @Override
        public void remove(int index) {
            CloneListIterable.this.remove(index);
        }
        @Override
        public void set(int index, E element) {
            CloneListIterable.this.set(index, element);
        }
    }
}
