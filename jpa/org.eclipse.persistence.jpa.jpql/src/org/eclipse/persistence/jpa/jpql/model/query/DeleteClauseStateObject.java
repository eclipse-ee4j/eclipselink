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

import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This is the <code><b>DELETE</b></code> clause of the <code><b>DELETE</b></code> statement.
 * <p>
 * A <code><b>DELETE</b></code> statement provides bulk operations over sets of entities of a single
 * entity class (together with its subclasses, if any). Only one entity abstract schema type may be
 * specified in the <code><b>UPDATE</b></code> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>delete_clause ::= DELETE FROM abstract_schema_name [[AS] identification_variable]</code><p>
 *
 * @see DeleteClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DeleteClauseStateObject extends AbstractModifyClauseStateObject {

	/**
	 * Creates a new <code>DeleteClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DeleteClauseStateObject(DeleteStatementStateObject parent) {
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
	public DeleteClause getExpression() {
		return (DeleteClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return DELETE_FROM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeleteStatementStateObject getParent() {
		return (DeleteStatementStateObject) super.getParent();
	}

	/**
	 * Keeps a reference of the {@link DeleteClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link DeleteClause parsed object} representing a <code><b>DELETE</b></code>
	 * clause
	 */
	public void setExpression(DeleteClause expression) {
		super.setExpression(expression);
	}
}