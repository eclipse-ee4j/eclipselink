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

import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This default resolver simply holds onto the actual type since it is already determined.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class ClassTypeResolver extends AbstractTypeResolver {

	/**
	 * The actual Java type for which its {@link IType} will be returned.
	 */
	private final Class<?> type;

	/**
	 * Creates a new <code>ClassTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param type The actual Java type for which its {@link IType} will be returned
	 */
	ClassTypeResolver(TypeResolver parent, Class<?> type) {
		super(parent);
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		return resolveManagedType(getType(type));
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return getType(type);
	}
}