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

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A <code>null</code> instance of an {@link IterableListIterator}.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public final class NullListIterator implements IterableListIterator<Object> {

	/**
	 * The singleton instance this <code>NullListIterator</code>.
	 */
	private static NullListIterator INSTANCE = new NullListIterator();

	/**
	 * Ensure non-instantiability.
	 */
	private NullListIterator() {
		super();
	}

	/**
	 * Returns the singleton instance this <code>NullListIterator</code>.
	 *
	 * @return The singleton instance this <code>NullListIterator</code>
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> IterableListIterator<T> instance() {
		return (IterableListIterator<T>) INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(Object item) {
		throw new UnsupportedOperationException("A NullListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasPrevious() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public ListIterator<Object> iterator() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object next() {
		throw new NoSuchElementException("A NullListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public int nextIndex() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object previous() {
		throw new NoSuchElementException("A NullListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public int previousIndex() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException("A NullListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void set(Object item) {
		throw new UnsupportedOperationException("A NullListIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}