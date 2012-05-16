/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * This expression represents the database specific data type, which may include size and scale.
 * <p>
 * <div nowrap><b>BNF:</b> <code>database_type ::= data_type_literal [( [numeric_literal [, numeric_literal]] )]</code>
 * <p>
 * <div nowrap><b>BNF:</b> <code>data_type_literal ::= [CHAR, VARCHAR, NUMERIC, INTEGER, DATE, TIME, TIMESTAMP, etc]</code>
 * <p>
 * Example: <code>CASE(e.name AS VARCHAR(20))</code>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class DatabaseType extends AbstractDoubleEncapsulatedExpression {

	/**
	 * Creates a new <code>DatabaseType</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The database type
	 */
	public DatabaseType(AbstractExpression parent, String databaseType) {
		super(parent);
		setText(databaseType);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		acceptUnknownVisitor(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(DatabaseTypeQueryBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSecondExpressionOptional() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String parameterExpressionBNF(int index) {
		return NumericLiteralBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldParseRightParenthesis(WordParser wordParser, boolean tolerant) {
		// If the database type uses parenthesis, then this expression will own the right
		// parenthesis,otherwise its parent expression should own it
		return hasLeftParenthesis() || hasFirstExpression() || hasComma() || hasSecondExpression();
	}
}