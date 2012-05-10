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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * An <b>EXISTS</b> expression is a predicate that is <code>true</code> only if the result of the
 * subquery consists of one or more values and that is <code>false</code> otherwise.
 * <p>
 * <div nowrap><b>BNF:</b> <code>exists_expression ::= [NOT] EXISTS(subquery)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class ExistsExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The actual <b>NOT</b> identifier found in the string representation of the JPQL query.
	 */
	private String notIdentifier;

	/**
	 * Creates a new <code>ExistsExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public ExistsExpression(AbstractExpression parent) {
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
	public String encapsulatedExpressionBNF() {
		return SubqueryBNF.ID;
	}

	/**
	 * Returns the actual <b>NOT</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualNotIdentifier() {
		return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ExistsExpressionBNF.ID);
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return (notIdentifier != null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression parse(WordParser wordParser, String queryBNFId, boolean tolerant) {

		if (tolerant) {
			return super.parse(wordParser, queryBNFId, tolerant);
		}

		SimpleSelectStatement expression = new SimpleSelectStatement(this);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		if (wordParser.startsWithIgnoreCase('N')) {
			int position = wordParser.position();
			notIdentifier = wordParser.substring(position, position + 3);
			return NOT_EXISTS;
		}
		return EXISTS;
	}
}