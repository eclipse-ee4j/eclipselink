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
 * Stand-alone identification variables in the <b>SELECT</b> clause may
 * optionally be qualified by the <b>OBJECT</b> operator. The <b>SELECT</b>
 * clause must not use the <b>OBJECT</b> operator to qualify path expressions.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= OBJECT(identification_variable)</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class ObjectExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>ObjectExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	ObjectExpression(AbstractExpression parent) {
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
		return queryBNF(PreLiteralExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(ObjectExpressionBNF.ID);
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

		String word = wordParser.word();

		IdentificationVariable expression = new IdentificationVariable(this, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return OBJECT;
	}
}