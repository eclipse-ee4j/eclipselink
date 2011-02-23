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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This resolver is responsible to calculate the type based on the type of the <b>ABS</b>
 * expression. The valid type is a <code>Number</code> type.
 * <p>
 * The <b>ABS</b> function takes a numeric argument and returns a number (integer, float, or double)
 * of the same type as the argument to the function.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class AbsFunctionResolver extends AbstractTypeResolver {

	/**
	 * The resolver used to find the type of the state field path.
	 */
	private final TypeResolver typeResolver;

	/**
	 * Creates a new <code>AbsFunctionResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param typeResolver The resolver used to find the type of the state field path
	 */
	AbsFunctionResolver(TypeResolver parent, TypeResolver typeResolver) {
		super(parent);
		this.typeResolver = typeResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {

		IType type = typeResolver.getType();
		type = TypeHelper.convertPrimitive(type);

		// Anything else is an invalid type
		if (!TypeHelper.isNumericType(type)) {
			type = TypeHelper.objectType();
		}

		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration() {
		return typeResolver.getTypeDeclaration();
	}
}