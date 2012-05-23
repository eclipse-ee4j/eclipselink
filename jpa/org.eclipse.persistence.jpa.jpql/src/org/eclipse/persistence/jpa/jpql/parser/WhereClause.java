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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>WHERE</b> clause of a query consists of a conditional expression used to select objects or
 * values that satisfy the expression. The <b>WHERE</b> clause restricts the result of a select
 * statement or the scope of an update or delete operation.
 * <p>
 * <div nowrap><b>BNF:</b> <code>where_clause ::= WHERE conditional_expression</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhereClause extends AbstractConditionalClause {

	/**
	 * Creates a new <code>WhereClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	WhereClause(AbstractExpression parent) {
		super(parent, WHERE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		if (word.equalsIgnoreCase(SELECT) ||
		    word.equalsIgnoreCase(FROM)) {

			return false;
		}

		return super.isParsingComplete(wordParser, word, expression);
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
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(WhereClauseBNF.ID);
	}
}