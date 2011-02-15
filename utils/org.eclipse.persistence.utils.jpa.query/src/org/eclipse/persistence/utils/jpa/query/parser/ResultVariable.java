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
 * A result variable may be used to name a select item in the query result.
 * <p>
 * <div nowrap><b>BNF:</b> <code>select_item ::= select_expression [[AS] result_variable]</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class ResultVariable extends AbstractExpression
{
	/**
	 * Determines whether the identifier <b>AS</b> was parsed or not.
	 */
	private boolean hasAs;

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 * The {@link Expression} used
	 */
	private AbstractExpression resultVariable;

	/**
	 * The {@link Expression} representing the select expression.
	 */
	private AbstractExpression selectExpression;

	/**
	 * Creates a new <code>ResultVariable</code>.
	 *
	 * @param parent The parent of this expression
	 * @param selectExpression The expression that represents the select
	 * expression, which will have a variable assigned to it
	 */
	ResultVariable(AbstractExpression parent, AbstractExpression selectExpression)
	{
		super(parent);

		if (selectExpression != null)
		{
			this.selectExpression = selectExpression;
			this.selectExpression.setParent(this);
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
		children.add(getSelectExpression());
		children.add(getResultVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// Select expression
		if (selectExpression != null)
		{
			children.add(selectExpression);
			children.add(buildStringExpression(SPACE));
		}

		// 'AS'
		if (hasAs)
		{
			children.add(buildStringExpression(AS));
		}

		if (hasSpaceAfterAs)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Result variable
		if (resultVariable != null)
		{
			children.add(resultVariable);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(ResultVariableBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the result variable.
	 *
	 * @return The expression for the result variable
	 */
	public Expression getResultVariable()
	{
		if (resultVariable == null)
		{
			resultVariable = buildNullExpression();
		}

		return resultVariable;
	}

	/**
	 * Returns the {@link Expression} representing the select expression.
	 *
	 * @return The expression for the select expression
	 */
	public Expression getSelectExpression()
	{
		if (selectExpression == null)
		{
			selectExpression = buildNullExpression();
		}

		return selectExpression;
	}

	/**
	 * Determines whether the identifier <b>AS</b> was parsed or not.
	 *
	 * @return <code>true</code> if the identifier <b>AS</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasAs()
	{
		return hasAs;
	}

	/**
	 * Determines whether the result variable was parsed.
	 *
	 * @return <code>true</code> if the result variable was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasResultVariable()
	{
		return resultVariable != null &&
		      !resultVariable.isNull();
	}

	/**
	 * Determines whether a select expression was defined for this result variable.
	 *
	 * @return <code>true</code> if the select expression was parsed; <code>false</code>
	 * if the result variable was parsed without one
	 */
	public boolean hasSelectExpression()
	{
		return selectExpression != null &&
		      !selectExpression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAs()
	{
		return hasSpaceAfterAs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'AS'
		if (wordParser.startsWithIdentifier(AS))
		{
			hasAs = true;
			wordParser.moveForward(2);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the result variable
		if (tolerant)
		{
			resultVariable = parse
			(
				wordParser,
				queryBNF(IdentificationVariableBNF.ID),
				tolerant
			);
		}
		else
		{
			resultVariable = new IdentificationVariable(this, wordParser.word());
			resultVariable.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// Select expression
		if (selectExpression != null)
		{
			selectExpression.toParsedText(writer);
			writer.append(SPACE);
		}

		// 'AS'
		if (hasAs)
		{
			writer.append(AS);
		}

		if (hasSpaceAfterAs)
		{
			writer.append(SPACE);
		}

		// Result variable
		if (resultVariable != null)
		{
			resultVariable.toParsedText(writer);
		}
	}
}