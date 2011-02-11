/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.Collection;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This {@link TypeResolver} calculates the type of a <b>FUNC</b> function by calculating the type
 * of each parameters.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class FuncTypeResolver extends AbstractTypeResolver
{
	/**
	 * The collection of {@link TypeResolver TypeResolvers} that will be used to
	 * calculate the actual type.
	 */
	private final Collection<TypeResolver> resolvers;

	/**
	 * Creates a new <code>FuncTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver
	 * @param resolvers The collection of {@link TypeResolver TypeResolvers} that will be used to
	 * calculate the actual type
	 */
	FuncTypeResolver(TypeResolver parent, Collection<TypeResolver> resolvers)
	{
		super(parent);
		this.resolvers = resolvers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		IType stringType   = null;
		IType numericType  = null;
		IType dateTimeType = null;
		IType numericClass = numberType();
		IType dateClass    = dateType();

		for (TypeResolver resolver : resolvers)
		{
			IType parameterType = resolver.getType();

			// String parameter type
			if (isStringType(parameterType))
			{
				stringType = parameterType;
			}
			// Numeric parameter type
			else if (parameterType.isAssignableTo(numericClass))
			{
				numericType = numericClass;
			}
			// Date/Time parameter type
			else if (parameterType.isAssignableTo(dateClass))
			{
				dateTimeType = dateClass;
			}
		}

		// String type
		if ((stringType != null) && (numericType == null) && (dateTimeType == null))
		{
			return stringType;
		}

		// Numeric type
		if ((stringType == null) && (numericType != null) && (dateTimeType == null))
		{
			return numericType;
		}

		// Date/Time type
		if ((stringType == null) && (numericType == null) && (dateTimeType != null))
		{
			return dateTimeType;
		}

		// Unknown type
		return objectType();
	}
}