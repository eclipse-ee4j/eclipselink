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
package org.eclipse.persistence.jpa.jpql;

/**
 * This visitor traverses an {@link Expression} and retrieves the "literal" value. The literal to
 * retrieve depends on the {@link LiteralType type}. The literal is basically a string value like an
 * identification variable name, an input parameter, a path expression, an abstract schema name,
 * etc.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class DefaultLiteralVisitor extends LiteralVisitor {

	/**
	 * Creates a new <code>DefaultLiteralVisitor</code>.
	 */
	public DefaultLiteralVisitor() {
		super();
	}
}