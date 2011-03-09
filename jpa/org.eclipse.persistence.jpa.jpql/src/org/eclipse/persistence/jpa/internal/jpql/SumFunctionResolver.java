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
package org.eclipse.persistence.jpa.internal.jpql;

import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This resolver is responsible to calculate the type based on the type of the state field path.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class SumFunctionResolver extends AbstractTypeResolver {

	/**
	 * The resolver used to find the type of the state field path.
	 */
	private final TypeResolver typeResolver;

	/**
	 * Creates a new <code>SumFunctionResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param typeResolver The resolver used to find the type of the state field path
	 */
	SumFunctionResolver(TypeResolver parent, TypeResolver typeResolver) {
		super(parent);
		this.typeResolver = typeResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {

		IType type = getTypeDeclaration().getType();

		// Integral types: int/Integer, long/Long => the result is a Long
		if (getTypeHelper().isIntegralType(type)) {
			return getTypeHelper().longType();
		}

		// Floating types: float/Float, double/Double => the result is a Double
		if (getTypeHelper().isFloatingType(type)) {
			return getTypeHelper().doubleType();
		}

		// BigInteger, the result is the same
		IType bigIntegerType = getTypeHelper().bigInteger();

		if (type.equals(bigIntegerType)) {
			return bigIntegerType;
		}

		// BigDecimal, the result is the same
		IType bigDecimalType = getTypeHelper().bigDecimal();

		if (type.equals(bigDecimalType)) {
			return bigDecimalType;
		}

		// Anything else is an invalid type
		return getTypeHelper().objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration() {
		return typeResolver.getTypeDeclaration();
	}
}