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
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to return the numeric type for a list of {@link Resolver
 * Resolvers}.
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
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class NumericResolver extends Resolver {

	/**
	 * The {@link Resolver resolvers} used to calculate the numeric type.
	 */
	private final Collection<Resolver> resolvers;

	/**
	 * Creates a new <code>NumericResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param resolvers The {@link Resolver resolvers} used to calculate the numeric type
	 */
	public NumericResolver(Resolver parent, Collection<Resolver> typeResolvers) {
		super(parent);
		this.resolvers = typeResolvers;
	}

	/**
	 * Creates a new <code>NumericResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param resolver The {@link Resolver} used to calculate the numeric type
	 */
	public NumericResolver(Resolver parent, Resolver resolver) {
		this(parent, Collections.singleton(resolver));
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
	protected IType buildType() {

		List<IType> types = new ArrayList<IType>(resolvers.size());
		TypeHelper helper = getTypeHelper();
		IType unresolvableType = helper.unknownType();

		// Convert any primitive types into its Class type and any non-number into Object
		for (Resolver typeResolver : resolvers) {

			IType type = typeResolver.getType();

			// Only a resolvable type will be added to the list
			if (type != unresolvableType) {
				// Non-numeric type cannot be added
				if (!helper.isNumericType(type)) {
					type = helper.objectType();
				}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ITypeDeclaration buildTypeDeclaration() {
		return getType().getTypeDeclaration();
	}
}