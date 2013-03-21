/*******************************************************************************
 * Copyright (c) 2012, 2013, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.resolver;


/**
 * This {@link Declaration} represents an unknown (invalid/incomplete) declaration.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class UnknownDeclaration extends Declaration {

	/**
	 * Creates a new <code>UnknownDeclaration</code>.
	 */
	public UnknownDeclaration() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public Type getType() {
		return Type.UNKNOWN;
	}
}