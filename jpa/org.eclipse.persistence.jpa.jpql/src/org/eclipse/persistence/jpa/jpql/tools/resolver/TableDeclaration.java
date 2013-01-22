/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This {@link Declaration} uses a database table as the "root" object.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class TableDeclaration extends Declaration {

	/**
	 * Creates a new <code>TableDeclaration</code>.
	 */
	public TableDeclaration() {
		super();
	}

	/**
	 * Returns the unquoted table name.
	 *
	 * @return The name of the table specified in the <code><b>TABLE</b></code> expression
	 */
	public String getTableName() {
		return ExpressionTools.unquote(rootPath);
	}

	/**
	 * {@inheritDoc}
	 */
	public Type getType() {
		return Type.TABLE;
	}
}