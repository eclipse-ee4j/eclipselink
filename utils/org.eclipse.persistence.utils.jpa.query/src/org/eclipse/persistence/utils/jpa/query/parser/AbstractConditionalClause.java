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
 * Conditional expressions are composed of other conditional expressions,
 * comparison operations, logical operations, path expressions that evaluate to
 * boolean values, boolean literals, and boolean input parameters. Arithmetic
 * expressions can be used in comparison expressions. Arithmetic expressions are
 * composed of other arithmetic expressions, arithmetic operations, path
 * expressions that evaluate to numeric values, numeric literals, and numeric
 * input parameters. Arithmetic operations use numeric promotion. Standard
 * bracketing () for ordering expression evaluation is supported.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= identifier conditional_expression</code><p>
 *
 * @see HavingClause
 * @see WhereClause
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class AbstractConditionalClause extends AbstractExpression
{
	/**
	 * The expression representing the composition of the conditional expressions.
	 */
	private AbstractExpression conditionalExpression;

	/**
	 * Determines whether a whitespace was parsed after the identifier.
	 */
	private boolean hasSpace;

	/**
	 * Creates a new <code>AbstractConditionalClause</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The identifier of this conditional clause
	 */
	AbstractConditionalClause(AbstractExpression parent, String identifier)
	{
		super(parent, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addChildrenTo(Collection<Expression> children)
	{
		children.add(getConditionalExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addOrderedChildrenTo(List<StringExpression> children)
	{
		// Identifier
		children.add(buildStringExpression(getText()));

		// Space
		if (hasSpace)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Conditional Expression
		if (conditionalExpression != null)
		{
			children.add(conditionalExpression);
		}
	}

	/**
	 * Returns the expression representing the composition of the conditional
	 * expressions.
	 *
	 * @return The actual conditional expression
	 */
	public final Expression getConditionalExpression()
	{
		if (conditionalExpression == null)
		{
			conditionalExpression = buildNullExpression();
		}

		return conditionalExpression;
	}

	/**
	 * Determines whether the conditional expression was parsed.
	 *
	 * @return <code>true</code> if there is a conditional expression;
	 * <code>false</code> otherwise
	 */
	public final boolean hasConditionalExpression()
	{
		return conditionalExpression != null &&
		      !conditionalExpression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the identifier or not.
	 *
	 * @return <code>true</code> if there was a whitespace after the identifier;
	 * <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterIdentifier()
	{
		return hasSpace;
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
	final void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse the identifier
		wordParser.moveForward(getText());

		hasSpace = wordParser.skipLeadingWhitespace() > 0;

		// Parse the conditional expression
		conditionalExpression = parse
		(
			wordParser,
			queryBNF(ConditionalExpressionBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void toParsedText(StringBuilder writer)
	{
		writer.append(getText());

		if (hasSpace)
		{
			writer.append(SPACE);
		}

		if (conditionalExpression != null)
		{
			conditionalExpression.toParsedText(writer);
		}
	}
}