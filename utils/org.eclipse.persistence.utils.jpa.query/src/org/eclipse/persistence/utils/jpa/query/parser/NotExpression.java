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

import java.util.Collection;
import java.util.List;

/**
 * <div nowrap><b>BNF:</b> <code>expression ::= NOT expression</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class NotExpression extends AbstractExpression
{
	/**
	 * The expression being negated by this expression.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether a space was parsed after <b>NOT</b>.
	 */
	private boolean hasSpaceAfterNot;

	/**
	 * The BNF coming from the parent expression that was used to parse the
	 * query.
	 */
	private JPQLQueryBNF queryBNF;

	/**
	 * Creates a new <code>NotExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param queryBNF The BNF coming from the parent expression that was used
	 * to parse the query
	 */
	NotExpression(AbstractExpression parent, JPQLQueryBNF queryBNF)
	{
		super(parent, NOT);
		this.queryBNF = queryBNF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		children.add(buildStringExpression(NOT));

		if (hasSpaceAfterNot)
		{
			children.add(buildStringExpression(SPACE));
		}

		if (hasExpression())
		{
			children.add(expression);
		}
	}

	/**
	 * Returns the {@link Expression} representing the expression that is negated.
	 *
	 * @return The expression representing the expression that is negated
	 */
	public Expression getExpression()
	{
		if (expression == null)
		{
			expression = buildNullExpression();
		}

		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF;
	}

	/**
	 * Determines whether the expression to negate was parsed.
	 *
	 * @return <code>true</code> if the expression to negate was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public boolean hasExpression()
	{
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>NOT</b>.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the identifier <b>NOT</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterNot()
	{
		return hasSpaceAfterNot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		wordParser.moveForward(NOT);
		hasSpaceAfterNot = wordParser.skipLeadingWhitespace() > 0;
		expression = parse(wordParser, getQueryBNF(), tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// NOT
		writer.append(getText());

		if (hasSpaceAfterNot)
		{
			writer.append(SPACE);
		}

		// Expression
		if (expression != null)
		{
			expression.toParsedText(writer);
		}
	}
}