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
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * A resolver is used to find the type for any JPQL expression that has a type.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
interface TypeResolver {

	/**
	 * Returns the managed type associated with the property or collection-valued property.
	 * <p>
	 * For example: "<code><b>SELECT</b> e <b>FROM</b> Employee e</code>", the {@link TypeResolver}
	 * for <i>e</i> would be returning the {@link IManagedType} for <i>Employee</i>
	 *
	 * @return Either the managed type, if it could be resolved, otherwise <code>null</code> is returned
	 */
	IManagedType getManagedType();

	/**
	 * Retrieves the mapping for the wrapped property.
	 *
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	IMapping getMapping();

	/**
	 * Returns the external form representing the JPQL query.
	 *
	 * @return The external form of the JPQL query
	 */
	IQuery getQuery();

	/**
	 * Returns the type of the wrapped property.
	 *
	 * @return Either the type that was resolved by this resolver or {@link Object} if it could be
	 * resolved
	 */
	IType getType();

	/**
	 * Returns the type declaration of the wrapped property.
	 *
	 * @return Either the type declaration that was resolved by this resolver
	 */
	ITypeDeclaration getTypeDeclaration();

	/**
	 * Retrieves the managed type for the given type.
	 *
	 * @param type The type for which a managed type could exist
	 * @return Either the managed type for the given type or <code>null</code> if none exists
	 */
	IManagedType resolveManagedType(IType type);

	/**
	 * Retrieves the managed type with the given abstract schema name.
	 *
	 * @param abstractSchemaName The abstract schema name of the managed type to retrieve
	 * @return Either the managed type with the given abstract schema name or <code>null</code> if
	 * none exists
	 */
	IManagedType resolveManagedType(String abstractSchemaName);

	/**
	 * Retrieves the mapping for the given variable name.
	 *
	 * @param variableName The name of the mapping to retrieve
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	IMapping resolveMapping(String variableName);

	/**
	 * Returns the resolved type for the given variable name, which could be an identification
	 * variable, a property, etc.
	 *
	 * @param variableName The name of the variable to resolve
	 * @return The type of the given variable or {@link Object} if it could not be resolved
	 */
	IType resolveType(String variableName);

	/**
	 * Returns the resolved type declaration for the given variable name, which
	 * could be an identification variable, a property, etc.
	 *
	 * @param variableName The name of the variable to resolve
	 * @return The type declaration of the given variable or {@link Object} if it could not be resolved
	 */
	ITypeDeclaration resolveTypeDeclaration(String variableName);
}