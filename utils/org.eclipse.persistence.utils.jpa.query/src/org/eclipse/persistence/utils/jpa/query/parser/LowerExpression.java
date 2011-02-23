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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * The <b>LOWER</b> function converts a string to lower case and it returns a string.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= LOWER(string_primary)</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class LowerExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>LowerExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	LowerExpression(AbstractExpression parent) {
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
	JPQLQueryBNF encapsulatedExpressionBNF() {
		return queryBNF(StringPrimaryBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(FunctionsReturningStringsBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression parse(WordParser wordParser,
	                         JPQLQueryBNF queryBNF,
	                         boolean tolerant) {
		if (tolerant) {
			return super.parse(wordParser, queryBNF, tolerant);
		}

		return parseWithoutCollection(wordParser, queryBNF, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return LOWER;
	}
}