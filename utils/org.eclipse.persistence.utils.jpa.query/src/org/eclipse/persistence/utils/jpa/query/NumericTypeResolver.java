/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This resolver is responsible to return the numeric type for a list of
 * {@link TypeResolver TypeResolvers}.
 * <p>
 * The result of a case expression, coalesce expression, nullif expression, or
 * arithmetic expression (+, -, *, /) is determined by applying the following
 * rule to its operands.
 * <p>
 * <ul>
 * <li>If there is an operand of type <code>Double</code> or <code>double</code>,
 * the result of the operation is of type <code>Double</code>;
 * <li>otherwise, if there is an operand of type <code>Float</code> or
 * <code>float</code>, the result of the operation is of type <code>Float</code>;
 * <li>otherwise, if there is an operand of type <code>BigDecimal</code>, the
 * result of the operation is of type <code>BigDecimal</code>;
 * <li>otherwise, if there is an operand of type <code>BigInteger</code>, the
 * result of the operation is of type <code>BigInteger</code>, unless the
 * operator is / (division), in which case the numeric result type is not
 * further defined;
 * <li>otherwise, if there is an operand of type <code>Long</code> or
 * <code>long</code>, the result of the operation is of type <code>Long</code>,
 * unless the operator is / (division), in which case the numeric result type is
 * not further defined;
 * <li>otherwise, if there is an operand of integral type, the result of the
 * operation is of type <code>Integer</code>, unless the operator is / (division),
 * in which case the numeric result type is not further defined.
 * </ul>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class NumericTypeResolver extends AbstractTypeResolver
{
	/**
	 * The resolvers used to calculate the numeric type.
	 */
	private final Collection<TypeResolver> typeResolvers;

	/**
	 * Creates a new <code>NumericTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param typeResolvers The resolvers used to calculate the numeric type
	 */
	NumericTypeResolver(TypeResolver parent, Collection<TypeResolver> typeResolvers)
	{
		super(parent);
		this.typeResolvers = typeResolvers;
	}

	/**
	 * Creates a new <code>NumericTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param typeResolver The resolver used to calculate the numeric type
	 */
	NumericTypeResolver(TypeResolver parent, TypeResolver typeResolver)
	{
		this(parent, Collections.singleton(typeResolver));
	}

	private Comparator<IType> buildComparator()
	{
		return new Comparator<IType>()
		{
			@Override
			public int compare(IType type1, IType type2)
			{
				if (type1.equals(type2))
				{
					return 0;
				}

				// Object type
				IType type = objectType();
				if (type1.equals(type)) return -1;
				if (type2.equals(type)) return  1;

				// Double
				type = doubleType();
				if (type1.equals(type)) return -1;
				if (type2.equals(type)) return  1;

				// Float
				type = floatType();
				if (type1.equals(type)) return -1;
				if (type2.equals(type)) return  1;

				// BigDecimal
				type = bigDecimal();
				if (type1.equals(type)) return -1;
				if (type2.equals(type)) return  1;

				// BigInteger
				type = bigInteger();
				if (type1.equals(type)) return -1;
				if (type2.equals(type)) return  1;

				// Long
				type = longType();
				if (type1.equals(type)) return -1;
				if (type2.equals(type)) return  1;

				return 1;
			}
		};
	}

	private IType convertNotNumberType(IType type)
	{
		if (!type.isAssignableTo(numberType()))
		{
			type = objectType();
		}

		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		List<IType> types = new ArrayList<IType>(typeResolvers.size());

		for (TypeResolver typeResolver : typeResolvers)
		{
			IType type = typeResolver.getType();

			type = convertPrimitive(type);
			type = convertNotNumberType(type);

			types.add(type);
		}

		Collections.sort(types, buildComparator());
		return types.get(0);
	}
}