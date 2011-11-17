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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A <code>null</code> instance of an {@link IterableIterator}.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public final class NullIterator implements IterableIterator<Object> {

	/**
	 * The singleton instance this <code>NullIterator</code>.
	 */
	private static NullIterator INSTANCE = new NullIterator();

	/**
	 * Ensure non-instantiability.
	 */
	private NullIterator() {
		super();
	}

	/**
	 * Returns the singleton instance this <code>NullIterator</code>.
	 *
	 * @return The singleton instance this <code>NullIterator</code>
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> IterableIterator<T> instance() {
		return (IterableIterator<T>) INSTANCE;
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
	public Iterator<Object> iterator() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object next() {
		throw new NoSuchElementException("A NullIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException("A NullIterator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}