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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * This {@link TypeResolver} compares each {@link IType} retrieves from the {@link TypeResolver type
 * resolvers} that were gathered for a given {@link Expression} and returns that type if they are
 * all the same type otherwise the {@link IType} for <code>Object</code> is returned.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class CollectionEquivalentTypeResolver extends AbstractTypeResolver {

	/**
	 * The list of {@link TypeResolver resolvers} that were created for each of the encapsulated
	 * expressions.
	 */
	private List<TypeResolver> resolvers;

	/**
	 * Creates a new <code>CollectionEquivalentTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver
	 * @param resolvers The list of {@link TypeResolver resolvers} that were created for each of
	 * the encapsulated expressions
	 */
	CollectionEquivalentTypeResolver(TypeVisitor parent, List<TypeResolver> resolvers) {
		super(parent);
		this.resolvers = resolvers;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {

		// Retrieve the first type so it can be compared with the others
		IType type = resolvers.get(0).getType();

		for (int index = 1, count = resolvers.size(); index < count; index++) {
			IType anotherType = resolvers.get(index).getType();

			// Two types are not the same, then the type is Object
			if (!type.equals(anotherType)) {
				return getType(Object.class);
			}
		}

		return type;
	}
}