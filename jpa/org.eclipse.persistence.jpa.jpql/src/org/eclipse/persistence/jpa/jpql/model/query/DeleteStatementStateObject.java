/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;

/**
 * Bulk delete operation apply to entities of a single entity class (together with its subclasses,
 * if any).
 * <p>
 * <div nowrap><b>BNF:</b> <code>delete_statement ::= delete_clause [where_clause]</code><p>
 *
 * @see DeleteStatement
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DeleteStatementStateObject extends AbstractModifyStatementStateObject {

	/**
	 * Creates a new <code>DeleteStatementStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DeleteStatementStateObject(JPQLQueryStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractModifyClauseStateObject buildModifyClause() {
		return new DeleteClauseStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeleteStatement getExpression() {
		return (DeleteStatement) super.getExpression();
	}

	/**
	 * Returns the state object representing the <code><b>DELETE</b></code> clause.
	 *
	 * @return The state object representing the <code><b>DELETE</b></code> clause, which is never
	 * <code>null</code>
	 */
	@Override
	public DeleteClauseStateObject getModifyClause() {
		return (DeleteClauseStateObject) super.getModifyClause();
	}

	/**
	 * Keeps a reference of the {@link DeleteStatement parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link DeleteStatement parsed object} representing a <code><b>DELETE</b></code>
	 * statement
	 */
	public void setExpression(DeleteStatement expression) {
		super.setExpression(expression);
	}
}