/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
 * The external representation of a Java type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface IType
{
	/**
	 * Determines whether the given type represents the same Java type thank this
	 * one.
	 * <p>
	 * Note: {@link Object#hashCode()} needs to be overriden.
	 *
	 * @param type The type to compare with this one
	 * @return <code>true</code> if the given type and this one represents the
	 * same Java type; <code>false</code> otherwise
	 */
	boolean equals(IType type);

	/**
	 * Returns the fully qualified class name.
	 *
	 * @return The name of the class represented by this one
	 */
	String getName();

	/**
	 * Returns the declaration of the Java class, which gives the information about type parameters,
	 * dimensionality, etc.
	 *
	 * @return The external form of the class' type declaration
	 */
	ITypeDeclaration getTypeDeclaration();

	/**
	 * Determines whether this type is an instance of the given type.
	 *
	 * @param type The type used to determine if the class represented by this
	 * external form is an instance of with one
	 * @return <code>true</code> if this type is an instance of the given type;
	 * <code>false</code> otherwise
	 */
	boolean isAssignableTo(IType type);

	/**
	 * Determines whether this Java type actually exists.
	 *
	 * @return <code>true</code> if the actual Java type can be located on the application's
	 * class path; <code>false</code> if it could not be found
	 */
	boolean isResolvable();
}