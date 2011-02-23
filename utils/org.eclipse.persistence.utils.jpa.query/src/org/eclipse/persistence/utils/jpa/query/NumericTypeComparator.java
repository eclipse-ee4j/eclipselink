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
package org.eclipse.persistence.utils.jpa.query;

import java.util.Comparator;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This {@link Comparator} is used to sort {@link IType ITypes} based on the numerical priority.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class NumericTypeComparator implements Comparator<IType> {

	/**
	 * The singleton instance of this {@link NumericTypeComparator}.
	 */
	private static final Comparator<IType> INSTANCE = new NumericTypeComparator();

	/**
	 * Creates a new <code>NumericTypeComparator</code>.
	 */
	private NumericTypeComparator() {
		super();
	}

	/**
	 * Returns the singleton instance of this {@link NumericTypeComparator}.
	 *
	 * @return The {@link Comparator} of two {@link IType ITypes} based on the numerical priority.
	 */
	public static Comparator<IType> instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compare(IType type1, IType type2) {

		// Same type
		if (type1.equals(type2)) {
			return 0;
		}

		// Object type
		IType type = TypeHelper.objectType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// Double
		type = TypeHelper.doubleType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// Float
		type = TypeHelper.floatType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// BigDecimal
		type = TypeHelper.bigDecimal();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// BigInteger
		type = TypeHelper.bigInteger();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// Long
		type = TypeHelper.longType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		return 1;
	}
}