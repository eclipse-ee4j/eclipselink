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

import java.util.Iterator;

/**
 * The external representation of a type declaration, which is used to give more
 * information to the a type or attribute's type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface ITypeDeclaration
{
	/**
	 * Returns the type defined for the Java member.
	 *
	 * @return The type defined for the Java member
	 */
	IType getType();

	/**
	 * Returns the {@link IType} objects that represent the variables declared by the generic
	 * declaration represented	by this {@link ITypeDeclaration}
	 * @return The collection over the {@link IType type parameters}
	 */
	Iterator<IType> parameterTypes();
}