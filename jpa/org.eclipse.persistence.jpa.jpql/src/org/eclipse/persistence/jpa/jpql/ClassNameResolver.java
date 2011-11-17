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
 * This {@link Resolver} simply holds onto the fully qualified class name of the {@link IType}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class ClassNameResolver extends Resolver {

	/**
	 * The fully qualified name of the type.
	 */
	private final String className;

	/**
	 * Creates a new <code>ClassNameResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param type The fully qualified name of the type
	 */
	public ClassNameResolver(Resolver parent, String className) {
		super(parent);
		this.className = className;
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
		return getType(className);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ITypeDeclaration buildTypeDeclaration() {
		return getType().getTypeDeclaration();
	}
}