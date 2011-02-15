/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link ExpressionFactory} creates a new expression when the portion of
 * the query to parse starts with an arithmetic identifier. It is possible the
 * expression to parse is also a {@link NumericLiteral} or an {@link ArithmeticFactor}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class ArithmeticExpressionFactory extends ExpressionFactory
{
	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression}
	 * passed to this factory is an {@link AdditionExpression} or
	 * {@link SubstractionExpression}.
	 */
	private ArithmeticExpressionVisitor visitor;

	/**
	 * The unique identifier of this {@link ArithmeticExpressionFactory}.
	 */
	static final String ID = "*/-+";

	/**
	 * Creates a new <code>AbstractArithmeticExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this <code>ExpressionFactory</code>
	 * @param identifier The JPQL identifier handled by this factory
	 */
	ArithmeticExpressionFactory()
	{
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
	private CompoundExpression buildExpression(AbstractExpression parent,
	                                           char character)
	{
		if (character == '*')
		{
			return new MultiplicationExpression(parent);
		}

		return new DivisionExpression(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final AbstractExpression buildExpression(AbstractExpression parent,
	                                         WordParser wordParser,
	                                         String word,
	                                         JPQLQueryBNF queryBNF,
	                                         AbstractExpression expression,
	                                   boolean tolerant)
	{
		Boolean type = wordParser.startsWithDigit();

		// Return right away the number literal
		if (type == Boolean.TRUE)
		{
			expression = new NumericLiteral(parent, wordParser.word());
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// When the text starts with either '+' or '-' and the expression passed
		// is null, then it means an ArithmeticFactor has to be used.
		// In any other cases, the expression would be for instance an AND
		// expression, a function, etc
		if ((type == Boolean.FALSE) && (expression == null))
		{
			expression = new ArithmeticFactor(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		char character = wordParser.character();

		// Substraction
		if (character == '-')
		{
			SubstractionExpression substractionExpression = new SubstractionExpression(parent);
			substractionExpression.setLeftExpression(expression);
			substractionExpression.parse(wordParser, tolerant);
			return substractionExpression;
		}

		// Addition
		if (character == '+')
		{
			AdditionExpression additionExpression = new AdditionExpression(parent);
			additionExpression.setLeftExpression(expression);
			additionExpression.parse(wordParser, tolerant);
			return additionExpression;
		}

		expression.accept(visitor());

		if (visitor.found)
		{
			visitor.found = false;
			ArithmeticExpression arithmeticException = (ArithmeticExpression) expression;

			CompoundExpression compoundExpression = buildExpression(parent, character);
			compoundExpression.setLeftExpression((AbstractExpression) arithmeticException.getRightExpression());
			compoundExpression.parse(wordParser, tolerant);
			arithmeticException.setRightExpression(compoundExpression);

			return arithmeticException;
		}
		else
		{
			CompoundExpression compoundExpression = buildExpression(parent, character);
			compoundExpression.setLeftExpression(expression);
			compoundExpression.parse(wordParser, tolerant);
			return compoundExpression;
		}
	}

	private ArithmeticExpressionVisitor visitor()
	{
		if (visitor == null)
		{
			visitor = new ArithmeticExpressionVisitor();
		}

		return visitor;
	}

	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression}
	 * passed to this factory is an {@link AdditionExpression} or
	 * {@link SubstractionExpression}.
	 */
	private class ArithmeticExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * This flag is turned on if the {@link Expression} visited is
		 * {@link OrExpression}.
		 */
		boolean found;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression)
		{
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstractionExpression expression)
		{
			found = true;
		}
	}
}