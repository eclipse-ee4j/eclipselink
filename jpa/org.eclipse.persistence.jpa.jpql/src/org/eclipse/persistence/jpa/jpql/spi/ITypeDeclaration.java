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
 * The external representation of a type declaration, which is used to give more information about
 * the type, i.e. if it's an array and if it has parameter types.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface ITypeDeclaration extends IExternalForm {

	/**
	 * Returns the {@link Type} for the array type if ({@link #isArray()} is <code>true</code>) or
	 * 0 if the type is not an array.
	 *
	 * @return The array dimensionality if the {@link IType} is an array; otherwise 0
	 */
	int getDimensionality();

	/**
	 * Returns the type defined for the Java member.
	 *
	 * @return The type defined for the Java member
	 */
	IType getType();

	/**
	 * Returns the {@link ITypeDeclaration ITypeDeclarations} that represent the variables declared
	 * by the generic declaration represented by this {@link ITypeDeclaration}.
	 *
	 * @return The array over the {@link ITypeDeclaration ITypeDeclarations}
	 */
	ITypeDeclaration[] getTypeParameters();

	/**
	 * Determines whether this type represents an array or not.
	 *
	 * @return <code>true</code> if this type is an array; <code>false</code> otherwise
	 */
	boolean isArray();
}