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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression adds support to in-line literals or SQL into the query.
 * <p>
 * New to EclipseLink 2.4.
 *
 * <div nowrap><b>BNF:</b> <code>sql_expression ::= SQL('sql'(, sql_item}*)</code><p>
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public class SQLExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The SQL.
	 */
	private String sql;

        /**
         *
         */
        private boolean hasComma;

        /**
         *
         */
        private boolean hasSpaceAFterComma;

	/**
	 * Creates a new <code>SQLExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public SQLExpression(AbstractExpression parent) {
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
	@Override
	protected void addOrderedEncapsulatedExpressionTo(List<StringExpression> children) {

		if (sql != null) {
			children.add(buildStringExpression(sql));
		}

                if (hasComma) {
                        children.add(buildStringExpression(COMMA));
                }

                if (hasSpaceAFterComma) {
                        children.add(buildStringExpression(SPACE));
                }

                super.addOrderedEncapsulatedExpressionTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return SQLItemBNF.ID;
	}

	/**
	 * Returns the SQL.
	 *
	 * @return The SQL.
	 */
	public String getSQL() {
		return sql;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(SQLExpressionBNF.ID);
	}

	/**
	 * Returns the SQL.
	 *
	 * @return The SQL
	 */
	public String getUnquotedSQL() {
		return ExpressionTools.unquote(sql);
	}

        public boolean hasComma() {
                return hasComma;
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasSQL() || hasComma ||  super.hasEncapsulatedExpression();
	}

        public boolean hasSpaceAFterComma() {
                return hasSpaceAFterComma;
        }

	/**
	 * Determines whether the SQL was parsed.
	 *
	 * @return <code>true</code> if the SQL was parsed; <code>false</code> otherwise
	 */
	public boolean hasSQL() {
		return ExpressionTools.stringIsNotEmpty(sql);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		// Parse the function name outside of a CollectionExpression so we can retrieve it
		// with getFunctionName()
		if (wordParser.startsWith(SINGLE_QUOTE)) {
			sql = ExpressionTools.parseLiteral(wordParser);
			wordParser.moveForward(sql);
			wordParser.skipLeadingWhitespace();
		}
		else {
		    sql = ExpressionTools.EMPTY_STRING;
		}

                hasComma = wordParser.startsWith(COMMA);

                if (hasComma) {
                        wordParser.moveForward(1);
                }

                hasSpaceAFterComma = wordParser.skipLeadingWhitespace() > 0;

                super.parseEncapsulatedExpression(wordParser, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return SQL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

		if (sql != null) {
			writer.append(sql);
		}

                if (hasComma) {
                        writer.append(COMMA);
                }

                if (hasSpaceAFterComma) {
                        writer.append(SPACE);
                }

                super.toParsedTextEncapsulatedExpression(writer, actual);
	}
}