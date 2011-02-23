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

import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This {@link TypeResolver} tries to resolve the fully path and see if it's a valid type. In that
 * case, it would mean the path is a fully qualified enum type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class EnumTypeResolver extends AbstractTypeResolver {

	/**
	 * The entire path that could be an enum type or a state field path expression.
	 */
	private final String path;

	/**
	 * Creates a new <code>EnumTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param path The entire path that could be an enum type or a state field path expression
	 */
	EnumTypeResolver(TypeResolver parent, String path) {
		super(parent);
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping getMapping() {

		IType type = getTypeRepository().getEnumType(path);

		// The path is not an enum constant, then use the parent,
		// which will resolve the mapping
		if (type == null) {
			return getParentMapping();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {

		IType type = getTypeRepository().getEnumType(path);

		// The path is not an enum constant, then use the parent,
		// which will resolve the state field path expression
		if (type == null) {
			type = getParentType();
		}

		return type;
	}
}