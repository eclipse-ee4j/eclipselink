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

/**
 * A <code>null</code> instance of a {@link Mutator}.
 *
 * @version 2.4
 * @since 2.4
 */
@SuppressWarnings("nls")
public final class NullMutator implements Mutator<Object> {

	/**
	 * The singleton instance of this <code>NullMutator</code>.
	 */
	private static final Mutator<Object> INSTANCE = new NullMutator();

	/**
	 * Creates a new <code>NullMutator</code>.
	 */
	private NullMutator() {
		super();
	}

	/**
	 * Returns the singleton instance of this <code>NullMutator</code>.
	 *
	 * @return The singleton instance of this <code>NullMutator</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Mutator<T> instance() {
		return (Mutator<T>) INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(Object current) {
		throw new UnsupportedOperationException("This Mutator is read-only.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}