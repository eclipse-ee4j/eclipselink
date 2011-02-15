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
 * A compound expression has a left and right expressions combined by an
 * identifier.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= left_expression identifier right_expression</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class CompoundExpression extends AbstractExpression
{
	/**
	 * Determines whether a whitespace is present after the identifier.
	 */
	private boolean hasSpaceAfterIdentifier;

	/**
	 * The left side of the arithmetic expression.
	 */
	private AbstractExpression leftExpression;

	/**
	 * The right side of the arithmetic expression.
	 */
	private AbstractExpression rightExpression;

	/**
	 * Creates a new <code>CompoundExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The identifier of this expression
	 */
	CompoundExpression(AbstractExpression parent, String identifier)
	{
		super(parent, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addChildrenTo(Collection<Expression> children)
	{
		children.add(getLeftExpression());
		children.add(getRightExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addOrderedChildrenTo(List<StringExpression> children)
	{
		// Left expression
		if (leftExpression != null)
		{
			children.add(leftExpression);
		}

		if (hasLeftExpression())
		{
			children.add(buildStringExpression(SPACE));
		}

		// Identifier
		children.add(buildStringExpression(getText()));

		if (hasSpaceAfterIdentifier)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Right expression
		if (rightExpression != null)
		{
			children.add(rightExpression);
		}
	}

	/**
	 * Returns the {@link Expression} that represents the first expression, which
	 * is before the identifier.
	 *
	 * @return The expression that was parsed representing the first expression
	 */
	public final Expression getLeftExpression()
	{
		if (leftExpression == null)
		{
			leftExpression = buildNullExpression();
		}

		return leftExpression;
	}

	/**
	 * Returns the {@link Expression} that represents the second expression,
	 * which is after the identifier.
	 *
	 * @return The expression that was parsed representing the second expression
	 */
	public final Expression getRightExpression()
	{
		if (rightExpression == null)
		{
			rightExpression = buildNullExpression();
		}

		return rightExpression;
	}

	/**
	 * Determines whether the first expression of the query was parsed.
	 *
	 * @return <code>true</code> if the first expression was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public final boolean hasLeftExpression()
	{
		return leftExpression != null &&
		      !leftExpression.isNull();
	}

	/**
	 * Determines whether the second expression of the query was parsed.
	 *
	 * @return <code>true</code> if the second expression was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public final boolean hasRightExpression()
	{
		return rightExpression != null &&
		      !rightExpression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the identifier.
	 *
	 * @return <code>true</code> if there was a whitespace after the identifier;
	 * <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterIdentifier()
	{
		return hasSpaceAfterIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse the identifier
		String identifier = parseIdentifier(wordParser);
		wordParser.moveForward(identifier);
		setText(identifier);

		hasSpaceAfterIdentifier = wordParser.skipLeadingWhitespace() > 0;

		// Parse the right expression
		rightExpression = parse
		(
			wordParser,
			rightExpressionBNF(),
			tolerant
		);

		if (!hasSpaceAfterIdentifier && hasRightExpression())
		{
			hasSpaceAfterIdentifier = true;
		}
	}

	/**
	 * Parses the identifier of this expression.
	 *
	 * @param text The text to parse, which starts with the identifier
	 * @return The identifier for this expression
	 */
	abstract String parseIdentifier(WordParser wordParser);

	/**
	 * Returns the BNF used to determine how to parse the right expression.
	 *
	 * @return The BNF used when parsing the expression after the identifier
	 */
	abstract JPQLQueryBNF rightExpressionBNF();

	/**
	 * Sets the given {@link Expression} to be the first expression of this
	 * compound one.
	 *
	 * @param leftExpression The expression that was parsed before the identifier
	 */
	final void setLeftExpression(AbstractExpression leftExpression)
	{
		this.leftExpression = leftExpression;
		this.leftExpression.setParent(this);
	}

	/**
	 * Sets the given {@link Expression} to be the second expression of this
	 * compound one.
	 *
	 * @param rightExpression The expression that was parsed after the identifier
	 */
	final void setRightExpression(AbstractExpression rightExpression)
	{
		this.rightExpression = rightExpression;
		this.rightExpression.setParent(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void toParsedText(StringBuilder writer)
	{
		// Left expression
		if (leftExpression != null)
		{
			leftExpression.toParsedText(writer);
		}

		// Make sure the whitespace is not part of the left expression
		if (hasLeftExpression() && (writer.charAt(writer.length() - 1) != ' '))
		{
			writer.append(SPACE);
		}

		// Identifier
		writer.append(getText());

		if (hasSpaceAfterIdentifier)
		{
			writer.append(SPACE);
		}

		// Right expression
		if (rightExpression != null)
		{
			rightExpression.toParsedText(writer);
		}
	}
}