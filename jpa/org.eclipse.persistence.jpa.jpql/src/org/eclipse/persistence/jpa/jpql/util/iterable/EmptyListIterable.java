/*******************************************************************************
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.util.iterable;

import java.io.Serializable;
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.EmptyListIterator;

/**
 * An <code>EmptyListIterable</code> is just that. Maybe just a touch better-performing than {@link
 * java.util.Collections#EMPTY_LIST} since we don't create a new {@link java.util.Iterator} every
 * time {@link #iterator()} is called. (Not sure why they do that....)
 *
 * @param <E> the type of elements returned by the list iterable's list iterator
 *
 * @see EmptyListIterator
 * @see EmptyIterable
 *
 * @version 2.5
 * @since 2.5
 */
@SuppressWarnings("nls")
public final class EmptyListIterable<E> implements ListIterable<E>, Serializable {

	/**
	 * The singleton instance of this <code>EmptyIterable</code>.
	 */
	private static final ListIterable<Object> INSTANCE = new EmptyListIterable<Object>();

	/**
	 * The serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>EmptyIterable</code> and insures single instance.
	 */
	private EmptyListIterable() {
		super();
	}

	/**
	 * Return the singleton instance of this {@link ListIterable}.
	 *
	 * @return The singleton instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> ListIterable<T> instance() {
		return (ListIterable<T>) INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public ListIterator<E> iterator() {
		return EmptyListIterator.instance();
	}

	private Object readResolve() {
		// Replace this object with the singleton
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[]";
	}
}