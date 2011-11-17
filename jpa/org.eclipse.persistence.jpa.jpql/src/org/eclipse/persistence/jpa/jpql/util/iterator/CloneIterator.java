/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.util.iterator;

import java.util.Collection;
import java.util.Iterator;

/**
 * A <code>CloneIterator</code> iterates over a copy of a collection, allowing for concurrent access
 * to the original collection.
 * <p>
 * The original collection passed to the <code>CloneIterator</code>'s constructor should be
 * synchronized; otherwise you run the risk of a corrupted collection.
 * <p>
 * By default, a <code>CloneIterator</code> does not support the {@link #remove()} operation; this
 * is because it does not have access to the original collection. But if the <code>CloneIterator</code>
 * is supplied with an <code>Mutator</code> it will delegate the {@link #remove()} operation to the
 * {@link Mutator}. Alternatively, a subclass can override the {@link #remove(Object)} method.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public class CloneIterator<E> implements IterableIterator<E> {

	/**
	 * The current element of the iteration.
	 */
	private E current;

	/**
	 * This {@link Mutator} is used to remove the item from the original collection.
	 */
	private Mutator<E> mutator;

	/**
	 * The nested <code>Iterator</code>, which is iterating over a copy of the collection.
	 */
	private Iterator<E> nestedIterator;

	/**
	 * An internal object used to determine if the
	 */
	private static final Object UNKNOWN = new Object();

	/**
	 * Creates a new <code>CloneIterator</code> using a copy of the specified collection. The {@link
	 * #remove()} method will not be supported, unless a subclass overrides the {@link #remove(Object)}.
	 *
	 * @param collection The collection that is copied in order to iterate over its items without
	 * being changed concurrently
	 */
	public CloneIterator(Collection<? extends E> collection) {
		this(collection, NullMutator.<E>instance());
	}

	/**
	 * Creates a new <code>CloneIterator</code> using a copy of the specified collection. Use the
	 * specified mutator to remove objects from the original collection.
	 *
	 * @param collection The collection that is copied in order to iterate over its items without
	 * being changed concurrently
	 * @param mutator This <code>Mutator</code> is used to remove the item from the original collection
	 */
	@SuppressWarnings("unchecked")
	public CloneIterator(Collection<? extends E> collection, Mutator<? extends E> mutator) {
		super();
		this.nestedIterator = new ArrayIterator<E>((E[]) collection.toArray());
		this.mutator        = (Mutator<E>) mutator;
		this.current        = (E) UNKNOWN;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return nestedIterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> iterator() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public E next() {
		current = nestedIterator.next();
		return current;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void remove() {

		if (current == UNKNOWN) {
			throw new IllegalStateException("No more item can be removed.");
		}

		remove(current);
		current = (E) UNKNOWN;
	}

	/**
	 * Removes the specified element from the original collection.
	 * <p>
	 * This method can be overridden by a subclass as an alternative to building a {@link Mutator}.
	 *
	 * @param item The element to remove from the original collection
	 */
	protected void remove(E item) {
		mutator.remove(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}