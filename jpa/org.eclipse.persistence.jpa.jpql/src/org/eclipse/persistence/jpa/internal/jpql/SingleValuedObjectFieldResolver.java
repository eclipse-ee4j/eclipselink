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

import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to resolve the type of a single valued object field, which
 * is usually part of a path expression. It is not the first path nor the last path.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class SingleValuedObjectFieldResolver extends AbstractPathResolver {

	/**
	 * The {@link IManagedType} representing this single valued object field path.
	 */
	private IManagedType managedType;

	/**
	 * This flag is used to prevent resolving the {@link IManagedType} more than once and no managed
	 * type could be found the first time.
	 */
	private boolean managedTypeResolved;

	/**
	 * Creates a new <code>SingleValuedObjectFieldResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param path The singled value object path, which is a path that is part of a state field path
	 * expression or a collection-valued path expression but that is not the root or leaf path
	 */
	SingleValuedObjectFieldResolver(Resolver parent, String path) {
		super(parent, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IManagedType getManagedType() {
		if ((managedType == null) && !managedTypeResolved) {
			managedType = resolveManagedType();
			managedTypeResolved = true;
		}
		return managedType;
	}

	private IManagedType resolveManagedType() {

		IMapping mapping = getMapping();

		if (mapping == null) {
			return null;
		}

		ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
		IType type = typeDeclaration.getType();

		// Collection type cannot be traversed
		// Example: SELECT e.employees. FROM Employee e where employees is a collection,
		// it cannot be traversed
		if (getTypeHelper().isCollectionType(type)) {
			return null;
		}

		// Retrieve the corresponding managed type for the mapping's type
		return getProvider().getManagedType(type);
	}
}