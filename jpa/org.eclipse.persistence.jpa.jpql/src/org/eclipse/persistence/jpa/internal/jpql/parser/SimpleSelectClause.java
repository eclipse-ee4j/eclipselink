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
package org.eclipse.persistence.jpa.internal.jpql.parser;

/**
 * The <b>SELECT</b> statement queries data from entities. This version simply does not handle
 * <b>OBJECT</b> and <b>NEW</b> identifiers. It is used from within another expression.
 * <p>
 * <div nowrap><b>BNF:</b> <code>simple_select_clause ::= SELECT [DISTINCT] simple_select_expression</code><p>
 *
 * @see AbstractSelectClause
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class SimpleSelectClause extends AbstractSelectClause {

	/**
	 * Creates a new <code>SimpleSelectClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	SimpleSelectClause(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(SimpleSelectClauseBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF selectItemBNF() {
		return queryBNF(SimpleSelectExpressionBNF.ID);
	}
}