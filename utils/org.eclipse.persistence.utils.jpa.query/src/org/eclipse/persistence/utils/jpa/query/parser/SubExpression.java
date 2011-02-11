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

import java.util.Collection;
import java.util.List;

/**
 * This expression wraps a sub-expression within parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= (sub_expression)</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class SubExpression extends AbstractExpression
{
	/**
	 * The encapsulated expression, i.e. wrapped with parenthesis.
	 */
	private AbstractExpression expression;

	/**
	 * Flag used to determine if the closing parenthesis is present in the query.
	 */
	private boolean hasRightParenthesis;

	/**
	 * The BNF coming from the parent that is used to parse the next portion of
	 * the query.
	 */
	private JPQLQueryBNF queryBNF;

	/**
	 * Creates a new <code>SubExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param queryBNF The BNF coming from the parent that is used to parse the
	 * next portion of the query
	 */
	SubExpression(AbstractExpression parent, JPQLQueryBNF queryBNF)
	{
		super(parent);
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
		// '('
		children.add(buildStringExpression(LEFT_PARENTHESIS));

		// The sub-expression
		if (expression != null)
		{
			children.add(expression);
		}

		// ')'
		if (hasRightParenthesis)
		{
			children.add(buildStringExpression(RIGHT_PARENTHESIS));
		}
	}

	/**
	 * Returns the encapsulated <code>Expression</code>.
	 *
	 * @return The <code>Expression</code> encapsulated with parenthesis
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
	 * Determines whether the encapsulated expression of the query was parsed.
	 *
	 * @return <code>true</code> if the encapsulated expression was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public boolean hasExpression()
	{
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether the close parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the close parenthesis was present in the
	 * string version of the query; <code>false</code> otherwise
	 */
	public boolean hasRightParenthesis()
	{
		return hasRightParenthesis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word)
	{
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       super.isParsingComplete(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse '('
		wordParser.moveForward(1);

		int count = wordParser.skipLeadingWhitespace();

		// Parse the sub-expression
		expression = parse
		(
			wordParser,
			getQueryBNF(),
			tolerant
		);

		if (hasExpression())
		{
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse ')'
		hasRightParenthesis = wordParser.character() == RIGHT_PARENTHESIS;

		if (hasRightParenthesis)
		{
			wordParser.moveForward(1);
		}
		else
		{
			wordParser.moveBackward(count);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// '('
		writer.append(LEFT_PARENTHESIS);

		// Sub-expression
		if (expression != null)
		{
			expression.toParsedText(writer);
		}

		// ')'
		if (hasRightParenthesis)
		{
			writer.append(RIGHT_PARENTHESIS);
		}
	}
}