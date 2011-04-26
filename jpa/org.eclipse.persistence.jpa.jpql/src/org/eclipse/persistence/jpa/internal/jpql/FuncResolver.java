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

import java.util.Collection;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to calculate the type of a <b>FUNC</b> function by
 * calculating the type of each parameters.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class FuncResolver extends Resolver {

	/**
	 * The collection of {@link Resolver Resolvers} that will be used to
	 * calculate the actual type.
	 */
	private final Collection<Resolver> resolvers;

	/**
	 * Creates a new <code>FuncResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param resolvers The collection of {@link Resolver Resolvers} that will be used to
	 * calculate the actual type
	 */
	FuncResolver(Resolver parent, Collection<Resolver> resolvers) {
		super(parent);
		this.resolvers = resolvers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ResolverVisitor visitor) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IType buildType() {

		IType stringType   = null;
		IType numericType  = null;
		IType dateTimeType = null;
		IType numericClass = getTypeHelper().numberType();
		IType dateClass    = getTypeHelper().dateType();

		for (Resolver resolver : resolvers) {
			IType parameterType = resolver.getType();

			// String parameter type
			if (getTypeHelper().isStringType(parameterType)) {
				stringType = parameterType;
			}
			// Numeric parameter type
			else if (parameterType.isAssignableTo(numericClass)) {
				numericType = numericClass;
			}
			// Date/Time parameter type
			else if (parameterType.isAssignableTo(dateClass)) {
				dateTimeType = dateClass;
			}
		}

		// String type
		if ((stringType != null) && (numericType == null) && (dateTimeType == null)) {
			return stringType;
		}

		// Numeric type
		if ((stringType == null) && (numericType != null) && (dateTimeType == null)) {
			return numericType;
		}

		// Date/Time type
		if ((stringType == null) && (numericType == null) && (dateTimeType != null)) {
			return dateTimeType;
		}

		// Unknown type
		return getTypeHelper().objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ITypeDeclaration buildTypeDeclaration() {
		return getType().getTypeDeclaration();
	}
}