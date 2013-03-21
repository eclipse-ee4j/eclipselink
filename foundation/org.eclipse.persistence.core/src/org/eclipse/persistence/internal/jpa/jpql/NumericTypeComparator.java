/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * This {@link Comparator} compares two {@link Class} values and returned the appropriate numeric
 * type that takes precedence.
 *
 * @since 2.4
 * @since 2.5
 * @author Pascal Filion
 */
final class NumericTypeComparator implements Comparator<Class<?>> {

	/**
	 * The singleton instance of this {@link NumericTypeComparator}.
	 */
	private static final Comparator<Class<?>> INSTANCE = new NumericTypeComparator();

	/**
	 * Creates a new <code>NumericTypeComparator</code>.
	 */
	private NumericTypeComparator() {
		super();
	}

	/**
	 * Returns the singleton instance of this {@link NumericTypeComparator}.
	 *
	 * @return The singleton instance of this {@link NumericTypeComparator}
	 */
	public static Comparator<Class<?>> instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compare(Class<?> type1, Class<?> type2) {

		// Same type
		if (type1 == type2) {
			return 0;
		}

		// Object type
		if (type1 == Object.class) return -1;
		if (type2 == Object.class) return  1;

		// Double
		if (type1 == Double.TYPE || type1 == Double.class) return -1;
		if (type2 == Double.TYPE || type2 == Double.class) return  1;

		// Float
		if (type1 == Float.TYPE || type1 == Float.class) return -1;
		if (type2 == Float.TYPE || type2 == Float.class) return  1;

		// BigDecimal
		if (type1 == BigDecimal.class) return -1;
		if (type2 == BigDecimal.class) return  1;

		// BigInteger
		if (type1 == BigInteger.class) return -1;
		if (type2 == BigInteger.class) return  1;

		// Long
		if (type1 == Long.TYPE || type1 == Long.class) return -1;
		if (type2 == Long.TYPE || type2 == Long.class) return  1;

		return 1;
	}
}