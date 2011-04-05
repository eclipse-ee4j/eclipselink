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
 * This {@link Resolver} is responsible to resolve the type of a state field, which is the leaf of
 * the state field path expression.
 * <p>
 * It is possible the state field path expression is actually an enum type, which will be
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class StateFieldResolver extends AbstractPathResolver {

	/**
	 * The full state field path expression, which is used to determine if it's an enum type.
	 */
	private String stateFieldPath;

	/**
	 * Creates a new <code>StateFieldResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param path The state field path, which is the last path of the state field path expression
	 * @param stateFieldPath The full state field path expression, which is used to determine if it's
	 * an enum type
	 */
	StateFieldResolver(Resolver parent, String path, String stateFieldPath) {
		super(parent, path);
		this.stateFieldPath = stateFieldPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IType buildType() {
		return getTypeHelper().convertPrimitive(super.buildType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ITypeDeclaration buildTypeDeclaration() {
		IType type = getEnumType();
		if (type != null) {
			return type.getTypeDeclaration();
		}
		return super.buildTypeDeclaration();
	}

	private IType getEnumType() {
		return (stateFieldPath != null) ? getTypeRepository().getEnumType(stateFieldPath) : null;
	}
}