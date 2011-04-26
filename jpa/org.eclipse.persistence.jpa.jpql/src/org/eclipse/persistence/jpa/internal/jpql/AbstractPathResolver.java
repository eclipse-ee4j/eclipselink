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
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to resolve a single path of a path expression (state field
 * path expression, singled valued object field, or a collection-valued path expression).
 *
 * @see CollectionValuedFieldResolver
 * @see SingleValuedObjectFieldResolver
 * @see StateFieldResolver
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
abstract class AbstractPathResolver extends Resolver {

	/**
	 * The mapping of the last path of the path expression or <code>null</code> if it could not be
	 * found.
	 */
	private IMapping mapping;

	/**
	 * This flag is used to prevent resolving the {@link IMapping} more than once and no mapping
	 * could be found the first time.
	 */
	private boolean mappingResolved;

	/**
	 * The name of the path for which its type will be retrieved.
	 */
	final String path;

	/**
	 * Creates a new <code>AbstractPathResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param path The name of the path
	 */
	AbstractPathResolver(Resolver parent, String path) {
		super(parent);
		this.path = path;
		parent.addChild(path, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ITypeDeclaration buildTypeDeclaration() {
		IMapping mapping = getMapping();
		if (mapping != null) {
			return mapping.getTypeDeclaration();
		}
		return getTypeHelper().unknownTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IMapping getMapping() {
		if ((mapping == null) && !mappingResolved) {
			mapping = resolveMapping();
			mappingResolved = true;
		}
		return mapping;
	}

	/**
	 * Returns the single path represented by this {@link Resolver}.
	 *
	 * @return The single path represented by this {@link Resolver}
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * Retrieves the {@link IMapping} for the given property name.
	 *
	 * @param propertyName The name of the {@link IMapping} to retrieve
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	private IMapping resolveMapping() {
		IManagedType managedType = getParentManagedType();
		if (managedType != null) {
			return managedType.getMappingNamed(path);
		}
		return null;
	}
}