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
package org.eclipse.persistence.jpa.internal.jpql;

import java.util.Comparator;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * This {@link Comparator} is used to sort {@link IType ITypes} based on the numerical priority.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class NumericTypeComparator implements Comparator<IType> {

	/**
	 * The helper that gives access to the most common {@link IType types}.
	 */
	private final TypeHelper typeHelper;

	/**
	 * Creates a new <code>NumericTypeComparator</code>.
	 *
	 * @param typeHelper The helper that gives access to the most common {@link IType types}
	 */
	public NumericTypeComparator(TypeHelper typeHelper) {
		super();
		this.typeHelper = typeHelper;
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
		IType type = typeHelper.objectType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// Double
		type = typeHelper.doubleType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// Float
		type = typeHelper.floatType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// BigDecimal
		type = typeHelper.bigDecimal();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// BigInteger
		type = typeHelper.bigInteger();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		// Long
		type = typeHelper.longType();
		if (type1.equals(type)) return -1;
		if (type2.equals(type)) return  1;

		return 1;
	}
}