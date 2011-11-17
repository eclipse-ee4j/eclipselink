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

import org.eclipse.persistence.jpa.jpql.parser.WhereClause;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>WHERE</b></code> clause of a query consists of a conditional expression used to
 * select objects or values that satisfy the expression. The <code><b>WHERE</b></code> clause
 * restricts the result of a select statement or the scope of an update or delete operation.
 * <p>
 * <div nowrap><b>BNF:</b> <code>where_clause ::= WHERE conditional_expression</code><p>
 *
 * @see WhereClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class WhereClauseStateObject extends AbstractConditionalClauseStateObject {

	/**
	 * Creates a new <code>WhereClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public WhereClauseStateObject(AbstractModifyStatementStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>WhereClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public WhereClauseStateObject(AbstractSelectStatementStateObject parent) {
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
	public WhereClause getExpression() {
		return (WhereClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return WHERE;
	}

	/**
	 * Keeps a reference of the {@link WhereClause parsed object} object, which should only be done
	 * when this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link WhereClause parsed object} representing a <code><b>Where</b></code>
	 * clause
	 */
	public void setExpression(WhereClause expression) {
		super.setExpression(expression);
	}
}