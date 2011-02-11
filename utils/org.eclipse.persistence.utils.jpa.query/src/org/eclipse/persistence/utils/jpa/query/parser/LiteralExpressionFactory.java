/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;

/**
 * This {@link LiteralExpressionFactory} is responsible to return the right
 * literal expression.
 *
 * @see StringLiteral
 * @see InputParameter
 * @see NumericLiteral
 * @see KeywordExpression
 * @see StateFieldPathExpression
 * @see IdentificationVariable
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class LiteralExpressionFactory extends AbstractLiteralExpressionFactory
{
	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression}
	 * passed to this factory is an a literal type expression.
	 */
	private LiteralExpressionVisitor visitor;

	/**
	 * The unique identifier of this {@link LiteralExpressionFactory}.
	 */
	static final String ID = "literal";

	/**
	 * Creates a new <code>LiteralExpressionFactory</code>.
	 */
	LiteralExpressionFactory()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   AbstractExpression expression,
	                                   boolean tolerant)
	{
		expression = new IdentificationVariable(parent, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean shouldSkip(AbstractExpression expression)
	{
		if ((expression == null) ||
		    (expression.getJPAVersion() == IJPAVersion.VERSION_1_0))
		{
			return false;
		}

		expression.accept(visitor());

		if (visitor.found)
		{
			visitor.found = false;
			return true;
		}

		return false;
	}

	private LiteralExpressionVisitor visitor()
	{
		if (visitor == null)
		{
			visitor = new LiteralExpressionVisitor();
		}

		return visitor;
	}

	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression}
	 * passed to this factory is an a literal type expression.
	 */
	private class LiteralExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * This flag is turned on if the {@link Expression} visited is a literal
		 * type expression.
		 */
		private boolean found;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression)
		{
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression)
		{
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeywordExpression expression)
		{
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NumericLiteral expression)
		{
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression)
		{
			found = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression)
		{
			found = true;
		}
	}
}