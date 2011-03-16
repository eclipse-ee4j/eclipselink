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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * This resolver is responsible to return the numeric type for a list of {@link TypeResolver
 * TypeResolvers}.
 * <p>
 * The result of a <b>CASE</b> expression, <b>COALESCE</b> expression, <b>NULLIF</b> expression, or
 * arithmetic expression (+, -, *, /) is determined by applying the following rule to its operands.
 * <p>
 * <ul>
 * <li>If there is an operand of type <code>Double</code> or <code>double</code>, the result of the
 *     operation is of type <code>Double</code>;
 * <li>otherwise, if there is an operand of type <code>Float</code> or <code>float</code>, the
 *     result of the operation is of type <code>Float</code>;
 * <li>otherwise, if there is an operand of type <code>BigDecimal</code>, the result of the
 *     operation is of type <code>BigDecimal</code>;
 * <li>otherwise, if there is an operand of type <code>BigInteger</code>, the result of the
 *     operation is of type <code>BigInteger</code>, unless the operator is / (division), in which
 *     case the numeric result type is not further defined;
 * <li>otherwise, if there is an operand of type <code>Long</code> or <code>long</code>, the result
 *     of the operation is of type <code>Long</code>, unless the operator is / (division), in which
 *     case the numeric result type is not further defined;
 * <li>otherwise, if there is an operand of integral type, the result of the operation is of type
 *     <code>Integer</code>, unless the operator is / (division), in which case the numeric result
 *     type is not further defined.
 * </ul>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class NumericTypeResolver extends AbstractTypeResolver {

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
	NumericTypeResolver(TypeResolver parent, Collection<TypeResolver> typeResolvers) {
		super(parent);
		this.typeResolvers = typeResolvers;
	}

	/**
	 * Creates a new <code>NumericTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param typeResolver The resolver used to calculate the numeric type
	 */
	NumericTypeResolver(TypeResolver parent, TypeResolver typeResolver) {
		this(parent, Collections.singleton(typeResolver));
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {

		List<IType> types = new ArrayList<IType>(typeResolvers.size());
		TypeHelper helper = getTypeHelper();
		IType unresolvableType = getType(IType.UNRESOLVABLE_TYPE);

		// Convert any primitive types into its Class type and any non-number into Object
		for (TypeResolver typeResolver : typeResolvers) {

			IType type = typeResolver.getType();

			if (type != unresolvableType) {
				type = helper.convertPrimitive(type);
				type = helper.convertNotNumberType(type);
				types.add(type);
			}
		}

		if (types.isEmpty()) {
			return helper.unknownType();
		}

		// Comparing the types will result in putting the
		// result at the beginning of the list
		Collections.sort(types, new NumericTypeComparator(helper));

		return types.get(0);
	}
}