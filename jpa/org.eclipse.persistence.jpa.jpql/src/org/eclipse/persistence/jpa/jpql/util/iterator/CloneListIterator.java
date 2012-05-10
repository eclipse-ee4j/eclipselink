/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import java.util.ListIterator;

/**
 * A <code>CloneListIterator</code> iterates over a copy of a list, allowing for concurrent access
 * to the original list.
 * <p>
 * The original list passed to the <code>CloneListIterator</code>'s constructor should be
 * synchronized; otherwise you run the risk of a corrupted list.
 * <p>
 * By default, a <code>CloneListIterator</code> does not support the modification operations; this
 * is because it does not have access to the original list. But if the <code>CloneListIterator</code>
 * is supplied with a {@link Mutator} it will delegate the modification operations to the {@link
 * Mutator}. Alternatively, a subclass can override the modification methods.
 *
 * @version 2.4
 * @since 2.4
 */
public class CloneListIterator<E> implements IterableListIterator<E> {

	/**
	 * The position where this iterator is over the original iterator.
	 */
	private int cursor;

	/**
	 * This {@link Mutator} is used to remove the item from the original collection.
	 */
	private ListMutator<E> mutator;

	/**
	 * The nested {@link ListIterator}, which is iterating over a copy of the collection.
	 */
	private ListIterator<E> nestedListIterator;

	/**
	 * Flag used to determine which item to remove or set when those methods are called.
	 */
	private State state;

	/**
	 * Creates a new <code>CloneListIterator</code> using a copy of the specified list. The
	 * modification methods will not be supported, unless a subclass overrides them.
	 *
	 * @param list The list that is copied in order to iterate over its items without being changed concurrently
	 */
	public CloneListIterator(List<? extends E> list) {
		this(list, NullListMutator.<E>instance());
	}

	/**
	 * Construct a list iterator on a copy of the specified list. Use the specified {@link ListMutator}
	 * to modify the original list.
	 *
	 * @param list The list that is copied in order to iterate over its items without being changed concurrently
	 * @param mutator This {@link ListMutator} is used to remove the item from the original list
	 */
	@SuppressWarnings("unchecked")
	public CloneListIterator(List<? extends E> list, ListMutator<E> mutator) {
		super();
		this.cursor             = 0;
		this.mutator            = mutator;
		this.state              = State.UNKNOWN;
		this.nestedListIterator = new ArrayListIterator<E>((E[]) list.toArray());
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(E item) {

		// Allow the nested iterator to throw an exception before we modify the
		// original list
		nestedListIterator.add(item);
		add(cursor, item);
		cursor++;
	}

	/**
	 * Adds the specified element to the original list.
	 * <p>
	 * This method can be overridden by a subclass as an alternative to building a {@link Mutator}.
	 *
	 * @param index The index of insertion
	 * @param item The element to insert into the list
	 */
	protected void add(int index, E item) {
		mutator.add(index, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return nestedListIterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasPrevious() {
		return nestedListIterator.hasPrevious();
	}

	/**
	 * {@inheritDoc}
	 */
	public ListIterator<E> iterator() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public E next() {

		// Allow the nested iterator to throw an exception before we modify the index
		E next = nestedListIterator.next();
		cursor++;
		state = State.NEXT;
		return next;
	}

	/**
	 * {@inheritDoc}
	 */
	public int nextIndex() {
		return nestedListIterator.nextIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	public E previous() {

		// Allow the nested iterator to throw an exception before we modify the index
		E previous = nestedListIterator.previous();
		cursor--;
		state = State.PREVIOUS;
		return previous;
	}

	/**
	 * {@inheritDoc}
	 */
	public int previousIndex() {
		return nestedListIterator.previousIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {

		// Allow the nested iterator to throw an exception before we modify the
		// original list
		nestedListIterator.remove();

		if (state == State.PREVIOUS) {
			remove(cursor);
		}
		else {
			cursor--;
			remove(cursor);
		}
	}

	/**
	 * Removes the specified element from the original list.
	 * <p>
	 * This method can be overridden by a subclass as an alternative to building a {@link ListMutator}.
	 *
	 * @param index The position of the item to remove from the original list
	 */
	protected void remove(int index) {
		mutator.remove(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public void set(E item) {

		// Allow the nested iterator to throw an exception before we modify the
		// original list
		nestedListIterator.set(item);

		if (state == State.PREVIOUS) {
			set(cursor, item);
		}
		else {
			set(cursor - 1, item);
		}
	}

	/**
	 * Sets the specified element in the original list.
	 * <p>
	 * This method can be overridden by a subclass as an alternative to building a {@link ListMutator}.
	 *
	 * @param index The index of replacement
	 * @param item The element to replace the existing one
	 */
	protected void set(int index, E item) {
		mutator.set(index, item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * A series of constants used to determine where to update the item when {@link
	 * CloneListIterator#set(Object)} is called.
	 */
	private enum State {

		/**
		 * The constant used when {@link CloneListIterator#next()} was called.
		 */
		NEXT,

		/**
		 * The constant used when {@link CloneListIterator#previous()} was called.
		 */
		PREVIOUS,

		/**
		 * The constant used when {@link CloneListIterator} was created.
		 */
		UNKNOWN
	}
}