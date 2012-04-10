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
 * The <b>CAST</b> function cast value to a different type.
 * The database type is the 2nd parameter, and can be any valid database type including size and scale.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= CAST(value AS type)</code><p>
 * <p>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class CastExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
	 */
	private String asIdentifier;

	/**
	 * Determines whether the identifier <b>AS</b> was part of the query.
	 */
	private boolean hasAs;

	/**
	 * Determines whether a space was parsed after the identifier <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 * The database type to cast to.
	 */
	private String databaseType;

	/**
	 * Creates a new <code>CastExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public CastExpression(AbstractExpression parent) {
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
	protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {
                // Value
                super.addOrderedEncapsulatedExpressionTo(children);

		// 'AS'
		if (hasAs) {
			children.add(buildStringExpression(AS));
		}

		if (hasSpaceAfterAs) {
			children.add(buildStringExpression(SPACE));
		}

                if (hasDatabaseType()) {
                        children.add(buildStringExpression(databaseType));
                }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return ScalarExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(CastExpressionBNF.ID);
	}

	/**
	 * Returns the database type to cast to.
	 */
	public String getDatabaseType() {
		return databaseType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasDatabaseType() || hasAs || hasExpression();
	}

	/**
	 * Determines whether the identifier <b>AS</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return hasAs;
	}

	/**
	 * Determines whether a whitespace was found after <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAs() {
		return hasSpaceAfterAs;
	}

	/**
	 * Return if a database type was parsed.
	 */
	public boolean hasDatabaseType() {
		return databaseType.length() > 0;
	}

        /**
         * {@inheritDoc}
         */
        @Override
        protected String parseIdentifier(WordParser wordParser) {
                return CAST;
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

                // Parse the value
                super.parseEncapsulatedExpression(wordParser, tolerant);
                
                wordParser.skipLeadingWhitespace();
                
                // Parse 'AS'
                hasAs = wordParser.startsWithIdentifier(AS);

                if (hasAs) {
                        asIdentifier = wordParser.moveForward(AS);
                        hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
                }

		// Parse the database type
		databaseType = wordParser.word();
		wordParser.moveForward(databaseType);
		if (wordParser.startsWith(LEFT_PARENTHESIS)) {
		    while (!wordParser.isTail() && (wordParser.character() != RIGHT_PARENTHESIS)) {
		        databaseType = databaseType + wordParser.moveForward(1);
		    }
                    databaseType = databaseType + wordParser.moveForward(1);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {
                // Value
                super.toParsedTextEncapsulatedExpression(writer, actual);

		if (hasAs()) {
			writer.append(asIdentifier);
		}

		if (hasSpaceAfterAs) {
			writer.append(SPACE);
		}

		if (hasDatabaseType()) {
		        writer.append(databaseType);
		}
	}
}