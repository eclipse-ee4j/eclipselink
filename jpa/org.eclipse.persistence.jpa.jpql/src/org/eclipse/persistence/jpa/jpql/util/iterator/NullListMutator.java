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

/**
 * A <code>null</code> instance of a {@link ListMutator}.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public final class NullListMutator implements ListMutator<Object> {

	/**
	 * The singleton instance of this <code>NullListMutator</code>.
	 */
	private static final ListMutator<Object> INSTANCE = new NullListMutator();

	/**
	 * Creates a new <code>NullListMutator</code>.
	 */
	private NullListMutator() {
		super();
	}

	/**
	 * Returns the singleton instance of this <code>NullListMutator</code>.
	 *
	 * @return The singleton instance of this <code>NullListMutator</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> ListMutator<T> instance() {
		return (ListMutator<T>) INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(int index, Object item) {
		throw new UnsupportedOperationException("This ListMutator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(int index) {
		throw new UnsupportedOperationException("This ListMutator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void set(int index, Object item) {
		throw new UnsupportedOperationException("This ListMutator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}