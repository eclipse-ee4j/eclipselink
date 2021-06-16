/*
 * Copyright (c) 2009, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.utility.iterable;

import org.eclipse.persistence.jpa.jpql.tools.utility.iterator.CloneIterator;

/**
 * Pull together remover state and behavior for subclasses.
 *
 * @param <E> the type of elements returned by the iterable's iterator
 *
 * @see SnapshotCloneIterable
 * @see <a href="http://git.eclipse.org/c/dali/webtools.dali.git/tree/common/plugins/org.eclipse.jpt.common.utility/src/org/eclipse/jpt/common/utility/internal/iterable/LiveCloneIterable.java">LiveCloneIterable</a>
 */
@SuppressWarnings("nls")
public abstract class CloneIterable<E> implements Iterable<E> {

    final CloneIterator.Remover<E> remover;


    // ********** constructors **********

    protected CloneIterable() {
        super();
        this.remover = this.buildDefaultRemover();
    }

    protected CloneIterable(CloneIterator.Remover<E> remover) {
        super();
        if (remover == null) {
            throw new NullPointerException();
        }
        this.remover = remover;
    }

    protected CloneIterator.Remover<E> buildDefaultRemover() {
        return new DefaultRemover();
    }


    // ********** default removal **********

    /**
     * Remove the specified element from the original collection.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a
     * {@link org.eclipse.persistence.jpa.jpql.utility.iterator.CloneListIterator.Mutator}.
     */
    protected void remove(@SuppressWarnings("unused") E element) {
        throw new RuntimeException("This method was not overridden.");
    }


    //********** default mutator **********

    protected class DefaultRemover implements CloneIterator.Remover<E> {
        @Override
        public void remove(E element) {
            CloneIterable.this.remove(element);
        }
    }
}
