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
 * The <code>state_field_path_expression</code> must have a string, numeric, or
 * enum value. The literal and/or <code>input_parameter</code> values must be
 * like the same abstract schema type of the <code>state_field_path_expression</code>
 * in type.
 * <p>
 * The results of the <code>subquery</code> must be like the same abstract
 * schema type of the <code>state_field_path_expression</code> in type.
 * <p>
 * There must be at least one element in the comma separated list that defines
 * the set of values for the <b>IN</b> expression. If the value of a
 * state_field_path_expression in an <b>IN</b> or <b>NOT IN</b> expression is
 * <b>NULL</b> or unknown, the value of the expression is unknown.
 * <p>
 * <div nowrap><b>BNF:</b> <code>in_expression ::= state_field_path_expression [NOT] IN(in_item {, in_item}* | subquery)</code><p>
 * <p>
 * <div nowrap>Example: </code><b>SELECT</b> c <b>FROM</b> Customer c <b>WHERE</b> c.home.city <b>IN</b>(:city)</p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class InExpression extends AbstractExpression
{
	/**
	 * Flag used to determine if the closing parenthesis is present in the query.
	 */
	private boolean hasLeftParenthesis;

	/**
	 * Determines whether this expression is negated or not.
	 */
	private boolean hasNot;

	/**
	 * Flag used to determine if the opening parenthesis is present in the query.
	 */
	private boolean hasRightParenthesis;

	/**
	 * The expression within parenthesis, which can be one or many expressions.
	 */
	private AbstractExpression inItems;

	/**
	 * The expression before the 'IN' identifier used for identification.
	 */
	private AbstractExpression stateFieldPathExpression;

	/**
	 * Creates a new <code>InExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The state field path expression that was parsed prior of
	 * parsing this expression
	 */
	InExpression(AbstractExpression parent,
	             AbstractExpression expression)
	{
		super(parent, IN);

		if (expression != null)
		{
			this.stateFieldPathExpression = expression;
			this.stateFieldPathExpression.setParent(this);
		}
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
		children.add(getStateFieldPathExpression());
		children.add(getInItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// State field path expression
		if (stateFieldPathExpression != null)
		{
			children.add(stateFieldPathExpression);
		}

		// 'NOT'
		if (hasNot)
		{
			children.add(buildStringExpression(SPACE));
			children.add(buildStringExpression(NOT));
		}
		else if (hasStateFieldPathExpression())
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'IN'
		children.add(buildStringExpression(IN));

		// '('
		if (hasLeftParenthesis)
		{
			children.add(buildStringExpression(LEFT_PARENTHESIS));
		}
		else if (hasInItems())
		{
			children.add(buildStringExpression(SPACE));
		}

		// In items
		if (inItems != null)
		{
			children.add(inItems);
		}

		// ')'
		if (hasRightParenthesis)
		{
			children.add(buildStringExpression(RIGHT_PARENTHESIS));
		}
	}

	/**
	 * Returns the {@link Expression} that represents the list if items.
	 *
	 * @return The expression that was parsed representing the list of items
	 */
	public Expression getInItems()
	{
		if (inItems == null)
		{
			inItems = buildNullExpression();
		}

		return inItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(InExpressionBNF.ID);
	}

	/**
	 * Returns the {@link Expression} that represents the state field path
	 * expression.
	 *
	 * @return The expression that was parsed representing the state field path
	 * expression
	 */
	public Expression getStateFieldPathExpression()
	{
		if (stateFieldPathExpression == null)
		{
			stateFieldPathExpression = buildNullExpression();
		}

		return stateFieldPathExpression;
	}

	/**
	 * Determines whether the list of items was parsed.
	 *
	 * @return <code>true</code> if at least one item was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasInItems()
	{
		return inItems != null &&
		      !inItems.isNull();
	}

	/**
	 * Determines whether the open parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the open parenthesis was present in the
	 * string version of the query; <code>false</code> otherwise
	 */
	public boolean hasLeftParenthesis()
	{
		return hasLeftParenthesis;
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot()
	{
		return hasNot;
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
	 * Determines whether the state field path expression was parsed.
	 *
	 * @return <code>true</code> if the state field path expression was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public boolean hasStateFieldPathExpression()
	{
		return stateFieldPathExpression != null &&
		      !stateFieldPathExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'NOT'
		hasNot = wordParser.startsWithIgnoreCase('N');

		if (hasNot)
		{
			wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'IN'
		wordParser.moveForward(IN);

		int count = wordParser.skipLeadingWhitespace();

		// Parse '('
		hasLeftParenthesis = wordParser.character() == LEFT_PARENTHESIS;

		if (hasLeftParenthesis)
		{
			wordParser.moveForward(1);
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse the in items or sub-query
		inItems = parse
		(
			wordParser,
			queryBNF(InItemBNF.ID),
			tolerant
		);

		if (hasInItems())
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
		// State field path expression
		if (stateFieldPathExpression != null)
		{
			stateFieldPathExpression.toParsedText(writer);
		}

		if (hasStateFieldPathExpression())
		{
			writer.append(SPACE);
		}

		// 'IN'
		if (hasNot)
		{
			writer.append(NOT);
			writer.append(SPACE);
		}

		// 'IN'
		writer.append(IN);

		// '('
		if (hasLeftParenthesis)
		{
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasInItems())
		{
			writer.append(SPACE);
		}

		// IN items
		if (inItems != null)
		{
			inItems.toParsedText(writer);
		}

		// ')'
		if (hasRightParenthesis)
		{
			writer.append(RIGHT_PARENTHESIS);
		}
	}
}