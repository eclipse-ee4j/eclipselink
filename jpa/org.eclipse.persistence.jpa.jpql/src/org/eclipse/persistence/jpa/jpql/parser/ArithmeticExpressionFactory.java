/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
 * This {@link ExpressionFactory} creates a new expression when the portion of the query to parse
 * starts with an arithmetic identifier. It is possible the expression to parse is also a {@link
 * NumericLiteral} or an {@link ArithmeticFactor}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ArithmeticExpressionFactory extends ExpressionFactory {

	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
	 * factory is an {@link AdditionExpression} or {@link SubtractionExpression}.
	 */
	private ArithmeticExpressionVisitor visitor;

	/**
	 * The unique identifier of this {@link ArithmeticExpressionFactory}.
	 */
	public static final String ID = "*/-+";

	/**
	 * Creates a new <code>AbstractArithmeticExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this <code>ExpressionFactory</code>
	 * @param identifier The JPQL identifier handled by this factory
	 */
	public ArithmeticExpressionFactory() {
		super(ID, Expression.PLUS,
		          Expression.MINUS,
		          Expression.DIVISION,
		          Expression.MULTIPLICATION);
	}

	/**
	 * Creates the {@link Expression} this factory for which it is responsible.
	 *
	 * @param parent The parent of the new {@link Expression}
	 * @param character
	 * @return A new {@link CompoundExpression}
	 */
	private CompoundExpression buildExpression(AbstractExpression parent, char character) {
		if (character == '*') {
			return new MultiplicationExpression(parent);
		}
		return new DivisionExpression(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("null")
	protected final AbstractExpression buildExpression(AbstractExpression parent,
	                                                   WordParser wordParser,
	                                                   String word,
	                                                   JPQLQueryBNF queryBNF,
	                                                   AbstractExpression expression,
	                                                   boolean tolerant) {

		Boolean type = wordParser.startsWithDigit();

		// Return right away the number literal
		// "1 + 1" can't be parsed as "1, +1"
		if ((type == Boolean.TRUE) && (expression == null)) {
			expression = new NumericLiteral(parent, wordParser.word());
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// When the text starts with either '+' or '-' and the expression passed
		// is null, then it means an ArithmeticFactor has to be used.
		// In any other cases, the expression would be for instance an AND
		// expression, a function, etc
		if ((type == Boolean.FALSE) && (expression == null)) {
			expression = new ArithmeticFactor(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		char character = word.charAt(0);// wordParser.character();

		// Subtraction
		if (character == '-') {
			SubtractionExpression substractionExpression = new SubtractionExpression(parent);
			substractionExpression.setLeftExpression(expression);
			substractionExpression.parse(wordParser, tolerant);
			return substractionExpression;
		}

		// Addition
		if (character == '+') {
			AdditionExpression additionExpression = new AdditionExpression(parent);
			additionExpression.setLeftExpression(expression);
			additionExpression.parse(wordParser, tolerant);
			return additionExpression;
		}

		if (expression != null) {
			expression.accept(visitor());
		}

		if ((visitor != null) && visitor.found) {
			visitor.found = false;
			ArithmeticExpression arithmeticException = (ArithmeticExpression) expression;

			CompoundExpression compoundExpression = buildExpression(parent, character);
			compoundExpression.setLeftExpression((AbstractExpression) arithmeticException.getRightExpression());
			compoundExpression.parse(wordParser, tolerant);
			arithmeticException.setRightExpression(compoundExpression);

			return arithmeticException;
		}
		else {
			CompoundExpression compoundExpression = buildExpression(parent, character);
			compoundExpression.setLeftExpression(expression);
			compoundExpression.parse(wordParser, tolerant);
			return compoundExpression;
		}
	}

	private ArithmeticExpressionVisitor visitor() {
		if (visitor == null) {
			visitor = new ArithmeticExpressionVisitor();
		}
		return visitor;
	}

	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
	 * factory is an {@link AdditionExpression} or {@link SubtractionExpression}.
	 */
	private class ArithmeticExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * This flag is turned on if the {@link Expression} visited is {@link OrExpression}.
		 */
		boolean found;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression) {
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubtractionExpression expression) {
			found = true;
		}
	}
}