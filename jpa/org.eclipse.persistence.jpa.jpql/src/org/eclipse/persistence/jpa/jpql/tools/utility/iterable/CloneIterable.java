/*******************************************************************************
 * Copyright (c) 2009, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.tools.utility.iterable;

import org.eclipse.persistence.jpa.jpql.tools.utility.iterator.CloneIterator;

/**
 * Pull together remover state and behavior for subclasses.
 *
 * @param <E> the type of elements returned by the iterable's iterator
 *
 * @see SnapshotCloneIterable
 * @see LiveCloneIterable
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
	 * {@link org.eclipse.jpt.common.utility.internal.iterators.Mutator}.
	 */
	protected void remove(@SuppressWarnings("unused") E element) {
		throw new RuntimeException("This method was not overridden.");
	}


	//********** default mutator **********

	protected class DefaultRemover implements CloneIterator.Remover<E> {
		public void remove(E element) {
			CloneIterable.this.remove(element);
		}
	}
}