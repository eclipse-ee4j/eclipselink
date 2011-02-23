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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.RangeVariableDeclaration;

/**
 * This visitor is used to retrieve the abstract schema name from a
 * {@link RangeVariableDeclaration}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class AbstractSchemaNameVisitor extends AbstractExpressionVisitor {

	/**
	 * Either the abstract schema name that was retrieved by visiting the query
	 * or <code>null</code> if it never got discovered.
	 */
	private String abstractSchemaName;

	/**
	 * Returns the abstract schema name from {@link RangeVariableDeclaration}.
	 *
	 * @return Either the abstract schema name that was retrieved by visiting the
	 * query or <code>null</code> if it never got discovered
	 */
	String getAbstractSchemaName() {
		return abstractSchemaName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		abstractSchemaName = expression.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {
		expression.getAbstractSchemaName().accept(this);
	}
}