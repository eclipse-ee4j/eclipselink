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

/**
 * The external representation of a mapping, which represents a single persistence property
 * of a managed type.
 *
 * @see IManagedType
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface IMapping
{
	/**
	 * Returns the type of this mapping.
	 *
	 * @return One of the supported mapping type
	 */
	IMappingType getMappingType();

	/**
	 * Returns the name of the persistence property represented by this mapping.
	 *
	 * @return The name of this mapping
	 */
	String getName();

	/**
	 * Returns the parent managed type owning this mapping.
	 *
	 * @return The parent of this mapping
	 */
	IManagedType getParent();

	/**
	 * Returns the type of this mapping.
	 *
	 * @return The external form representing the type of this mapping
	 */
	IType getType();

	/**
	 * Returns the declaration of the Java class, which gives the information about type parameters,
	 * dimensionality, etc.
	 *
	 * @return The external form of the class' type declaration
	 */
	ITypeDeclaration getTypeDeclaration();
}