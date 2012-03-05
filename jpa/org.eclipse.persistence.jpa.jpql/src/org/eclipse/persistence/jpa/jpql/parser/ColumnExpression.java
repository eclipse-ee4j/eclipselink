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
 * This expression adds support for field expressions.
 * <p>
 * New to EclipseLink 2.4.
 *
 * <div nowrap><b>BNF:</b> <code>column_expression ::= COLUMN('ADDRESS_ID'(, path}*)</code><p>
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public class ColumnExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The SQL.
	 */
	private String column;

        /**
         *
         */
        private boolean hasComma;

        /**
         *
         */
        private boolean hasSpaceAFterComma;

	/**
	 * Creates a new <code>ColumnExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public ColumnExpression(AbstractExpression parent) {
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

		if (column != null) {
			children.add(buildStringExpression(column));
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
		// TOD: get this to work, return SingleValuedPathExpressionBNF.ID;
                return FunctionItemBNF.ID;		
	}

	/**
	 * Returns the column name.
	 *
	 * @return The column name.
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ColumnExpressionBNF.ID);
	}

	/**
	 * Returns the column name.
	 *
	 * @return The column name
	 */
	public String getUnquotedColumn() {
		return ExpressionTools.unquote(column);
	}

        public boolean hasComma() {
                return hasComma;
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasColumn() || hasComma ||  super.hasEncapsulatedExpression();
	}

        public boolean hasSpaceAFterComma() {
                return hasSpaceAFterComma;
        }

	/**
	 * Determines whether the SQL was parsed.
	 *
	 * @return <code>true</code> if the SQL was parsed; <code>false</code> otherwise
	 */
	public boolean hasColumn() {
		return ExpressionTools.stringIsNotEmpty(column);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		// Parse the column name outside of a CollectionExpression so we can retrieve it
		// with getColumn()
		if (wordParser.startsWith(SINGLE_QUOTE)) {
			column = ExpressionTools.parseLiteral(wordParser);
			wordParser.moveForward(column);
			wordParser.skipLeadingWhitespace();
		}
		else {
		    column = ExpressionTools.EMPTY_STRING;
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
		return COLUMN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

		if (column != null) {
			writer.append(column);
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