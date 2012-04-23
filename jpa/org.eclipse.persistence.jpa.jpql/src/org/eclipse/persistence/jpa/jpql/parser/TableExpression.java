/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Define a table expression. This allow a non-mapped table to be used in a query.
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class TableExpression extends AbstractExpression {

	/**
	 *
	 */
	private String table;

	/**
	 * Creates a new <code>TableExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public TableExpression(AbstractExpression parent) {
		super(parent);
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
	public void acceptChildren(ExpressionVisitor visitor) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {
		children.add(buildStringExpression(table));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(TableExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {
		wordParser.moveForward(TABLE);
		wordParser.skipLeadingWhitespace();
		wordParser.moveForward(LEFT_PARENTHESIS);
		table = wordParser.word();
		wordParser.moveForward(table);
		wordParser.skipLeadingWhitespace();
		wordParser.moveForward(RIGHT_PARENTHESIS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {
		writer.append(TABLE);
		writer.append(SPACE);
		writer.append(LEFT_PARENTHESIS);
		writer.append(table);
		writer.append(RIGHT_PARENTHESIS);
	}
}