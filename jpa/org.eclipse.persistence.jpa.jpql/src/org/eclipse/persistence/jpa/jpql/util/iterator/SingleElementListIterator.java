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

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A <code>SingleElementListIterator</code> holds a single element and returns it with the first
 * call to {@link #next()}, at which point it will return <code>false</code> to any subsequent call
 * to {@link #hasNext()}. Likewise, it will return <code>false</code> to a call to {@link #hasPrevious()}<
 * until a call to {@link #next()}, at which point a call to {@link #previous()} will return the
 * single element.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public class SingleElementListIterator<E> implements IterableListIterator<E> {

	/**
	 * The only element of this <code>Iterator</code>.
	 */
	private E element;

	/**
	 * The element that can be returned by {@link #next()}.
	 */
	private E next;

	/**
	 * A constant used to determine if we are at the end of the iteration.
	 */
	private static final Object END = new Object();

	/**
	 * Creates a new <code>SingleElementListIterator</code> that returns only the specified element.
	 *
	 * @param element The only element of this <code>Iterator</code>
	 */
	public SingleElementListIterator(E element) {
		super();
		this.next    = element;
		this.element = element;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(E item) {
		throw new UnsupportedOperationException("This SingleElementListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return next == element;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasPrevious() {
		return next == END;
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
	@SuppressWarnings("unchecked")
	public E next() {

		if (next == END) {
			throw new NoSuchElementException("No more elements can be retrieved.");
		}

		next = (E) END;
		return element;
	}

	/**
	 * {@inheritDoc}
	 */
	public int nextIndex() {
		return (next == element) ? 0 : 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public E previous() {

		if (next == element) {
			throw new NoSuchElementException("No more elements can be retrieved.");
		}

		next = element;
		return element;
	}

	/**
	 * {@inheritDoc}
	 */
	public int previousIndex() {
		return (next == END) ? 0 : -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException("This SingleElementListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void set(E item) {
		throw new UnsupportedOperationException("This SingleElementListIterator is read-only.");
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("(");
		sb.append(element);
		sb.append(")");
		return sb.toString();
	}
}