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

import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to calculate the {@link IType} based on the type of the
 * <b>ABS</b> expression. The valid type is a <code>Number</code> type.
 * <p>
 * The <b>ABS</b> function takes a numeric argument and returns a number (integer, float, or double)
 * of the same type as the argument to the function.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class AbsFunctionResolver extends Resolver {

	/**
	 * Creates a new <code>AbsFunctionResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 */
	public AbsFunctionResolver(Resolver parent) {
		super(parent);
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

		IType type = getParentType();

		// Anything else is an invalid type
		if (!getTypeHelper().isNumericType(type)) {
			type = getTypeHelper().objectType();
		}

		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ITypeDeclaration buildTypeDeclaration() {
		return getParentTypeDeclaration();
	}
}