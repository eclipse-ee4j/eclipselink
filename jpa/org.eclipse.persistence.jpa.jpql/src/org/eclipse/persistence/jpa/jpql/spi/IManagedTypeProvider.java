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
package org.eclipse.persistence.jpa.jpql.spi;

/**
 * The external representation of the provider of managed types.
 *
 * @see IManagedType
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface IManagedTypeProvider extends IExternalForm {

	/**
	 * Returns the collection of possible abstract schema types.
	 *
	 * @return The {@link IEntity entities} defined in the persistence context
	 */
	Iterable<IEntity> abstractSchemaTypes();

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code> otherwise
	 */
	IManagedType getManagedType(IType type);

	/**
	 * Retrieves the entity with the given abstract schema name, which can also be the entity class
	 * name.
	 *
	 * @param abstractSchemaName The abstract schema name, which can be different from the entity
	 * class name but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if none could be found
	 */
	IManagedType getManagedType(String abstractSchemaName);

	/**
	 * Returns the platform to which the JPA artifacts are deployed.
	 *
	 * @return The application's platform
	 */
	IPlatform getPlatform();

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	ITypeRepository getTypeRepository();

	/**
	 * Returns the version of the Java Persistence this entity for which it was defined.
	 *
	 * @return The version of the Java Persistence being used
	 */
	IJPAVersion getVersion();

	/**
	 * Returns the managed types available within the context of this provider.
	 *
	 * @return The managed types owned by this provider
	 */
	Iterable<IManagedType> managedTypes();
}