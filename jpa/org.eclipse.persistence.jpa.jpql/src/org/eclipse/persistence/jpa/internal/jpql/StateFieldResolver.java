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
public final class StateFieldResolver extends AbstractPathResolver {

	/**
	 * Flag used to indicate the state field path expression is actually an enum type.
	 */
	private Boolean enumType;

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
	public void accept(ResolverVisitor visitor) {
		visitor.visit(this);
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
			enumType = Boolean.TRUE;
			return type.getTypeDeclaration();
		}
		else {
			enumType = Boolean.FALSE;
			return super.buildTypeDeclaration();
		}
	}

	private IType getEnumType() {
		return (stateFieldPath != null) ? getTypeRepository().getEnumType(stateFieldPath) : null;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public String getStateFieldPath() {
		return stateFieldPath;
	}

	/**
	 * Determines whether the state field path expression is actually an enum type.
	 *
	 * @return <code>true</code> if the path represents the fully qualified enum type with the enum
	 * constant; <code>false</code> to indicate it's a real state field path expression
	 */
	public boolean isEnumType() {
		// If this is called before the type was calculated,
		// then do so in order to set the enum type flag
		if (enumType == null) {
			getType();
		}
		return enumType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return stateFieldPath;
	}
}