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
package org.eclipse.persistence.jpa.internal.jpql.util;

/**
 * A <code>null</code> implementation of a <code>Filter</code>. The singleton
 * instance can be typed cast properly when using generics.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class NullFilter implements Filter<Object> {

	/**
	 * The singleton instance of this <code>NullFilter</code>.
	 */
	private static final NullFilter INSTANCE = new NullFilter();

	/**
	 * Creates a new <code>NullFilter</code>.
	 */
	private NullFilter() {
		super();
	}

	/**
	 * Returns the singleton instance of this <code>NullFilter</code>.
	 *
	 * @return The singleton instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> Filter<T> instance() {
		return (Filter<T>) INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean accept(Object value) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}