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
package org.eclipse.persistence.jpa.jpql.spi;

import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

/**
 * The external representation of the provider of managed types, which provide access to the JPA
 * domain model.
 *
 * @see IManagedType
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface IManagedTypeProvider extends IExternalForm {

	/**
	 * Returns the collection of possible abstract schema types.
	 *
	 * @return The {@link IEntity entities} defined in the persistence context
	 * @version 2.4
	 */
	IterableIterator<IEntity> entities();

	/**
	 * Retrieves the {@link IEmbeddable} with the given {@link IType}.
	 *
	 * @param type The {@link IType} of the {@link IEmbeddable} to retrieve
	 * @return The {@link IEmbeddable} for the given {@link IType} if it's representing an embeddable;
	 * otherwise <code>null</code>
	 * @since 2.4
	 */
	IEmbeddable getEmbeddable(IType type);

	/**
	 * Retrieves the {@link IEmbeddable} for the given fully qualified type name.
	 *
	 * @param typeName The fully qualified type name of the {@link IEmbeddable} to retrieve
	 * @return The {@link IEmbeddable} that has the given type name; otherwise <code>null</code>
	 * @since 2.4
	 */
	IEmbeddable getEmbeddable(String typeName);

	/**
	 * Retrieves the {@link IEntity} with the given {@link IType}.
	 *
	 * @param type The {@link IType} of the {@link IEntity} to retrieve
	 * @return The {@link IEntity} for the given {@link IType} if it's representing an entity;
	 * otherwise <code>null</code>
	 * @since 2.4
	 */
	IEntity getEntity(IType type);

	/**
	 * Retrieves the {@link IEntity} with the given name.
	 *
	 * @param typeName The fully qualified type name of the {@link IEntity} to retrieve
	 * @return The {@link IEntity} with the given name; otherwise <code>null</code>
	 * @see #getEntityName(String)
	 * @since 2.4
	 */
	IEntity getEntity(String typeName);

	/**
	 * Retrieves the {@link IEntity} with the given entity name.
	 *
	 * @param entityName The abstract schema name of the {@link IEntity} to retrieve
	 * @return The {@link IEntity} with the given abstract schema name; otherwise <code>null</code>
	 * @see #getEntity(String)
	 * @since 2.4
	 */
	IEntity getEntityNamed(String entityName);

	/**
	 * Retrieves the {@link IManagedType} for the given {@link IType}.
	 *
	 * @param type The type that is used as a managed type
	 * @return The {@link IManagedType} for the given type, if one exists, <code>null</code> otherwise
	 */
	IManagedType getManagedType(IType type);

	/**
	 * Retrieves the {@link IManagedType} for the given fully qualified type name.
	 *
	 * @param typeName The fully qualified type name of the {@link IManagedType} to retrieve
	 * @return The {@link IManagedType} for the given type, if one exists, <code>null</code> otherwise
	 * @version 2.4
	 */
	IManagedType getManagedType(String typeName);

	/**
	 * Retrieves the {@link IEmbeddable} with the given {@link IType}.
	 *
	 * @param type The {@link IType} of the {@link IMappedSuperclass} to retrieve
	 * @return The {@link IMappedSuperclass} for the given {@link IType} if it's representing a
	 * mapped superclass; otherwise <code>null</code>
	 * @since 2.4
	 */
	IMappedSuperclass getMappedSuperclass(IType type);

	/**
	 * Retrieves the {@link IMappedSuperclass} for the given fully qualified type name.
	 *
	 * @param typeName The fully qualified type name of the {@link IMappedSuperclass} to retrieve
	 * @return The {@link IMappedSuperclass} that has the given type name; otherwise <code>null</code>
	 * @since 2.4
	 */
	IMappedSuperclass getMappedSuperclass(String typeName);

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	ITypeRepository getTypeRepository();

	/**
	 * Returns the managed types available within the context of this provider.
	 *
	 * @return The managed types owned by this provider
	 */
	IterableIterator<IManagedType> managedTypes();
}