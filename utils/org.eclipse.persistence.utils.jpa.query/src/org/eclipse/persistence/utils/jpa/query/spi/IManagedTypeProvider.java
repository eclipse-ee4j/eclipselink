/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.spi;

import java.util.Iterator;

/**
 * The external representation of the provider of managed types.
 *
 * @see IManagedType
 *
 * @version 12.0.0
 * @since 12.0.0
 * @author Pascal Filion
 */
public interface IManagedTypeProvider
{
	/**
	 * Returns the abstract schema names for each entity owned by this container.
	 *
	 * @return The abstract schema names, which may differ from the entity class
	 * name
	 */
	Iterator<String> entityNames();

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code>
	 * otherwise
	 */
	IManagedType getManagedType(IType type);

	/**
	 * Retrieves the entity with the given abstract schema name, which can also
	 * be the entity class name.
	 *
	 * @param abstractSchemaName The abstract schema name, which can be different
	 * from the entity class name but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if
	 * none could be found
	 */
	IManagedType getManagedType(String abstractSchemaName);

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	ITypeRepository getTypeRepository();

	/**
	 * Returns the version of the Java Persistence this entity for which it was
	 * defined.
	 *
	 * @return The version of the Java Persistence being used
	 */
	IJPAVersion getVersion();

	/**
	 * Returns the managed types available within the context of this provider.
	 *
	 * @return The managed types owned by this provider
	 */
	Iterator<IManagedType> managedTypes();
}