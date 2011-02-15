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
 * This expression handles parsing a JPQL identifier followed by an expression
 * encapsulated within parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AbstractSingleEncapsulatedExpression extends AbstractEncapsulatedExpression
{
	/**
	 * The sub-expression encapsulated within parenthesis.
	 */
	private AbstractExpression expression;

	/**
	 * Creates a new <code>EncapsulatedExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	AbstractSingleEncapsulatedExpression(AbstractExpression parent)
	{
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addChildrenTo(Collection<Expression> children)
	{
		super.addChildrenTo(children);
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedEncapsulatedExpressionTo(List<StringExpression> children)
	{
		children.add(getExpression());
	}

	/**
	 * Returns the BNF used to parse the encapsulated expression.
	 *
	 * @return The BNF used to parse the encapsulated expression
	 */
	abstract JPQLQueryBNF encapsulatedExpressionBNF();

	/**
	 * Returns the {@link Expression} that is encapsulated within parenthesis.
	 *
	 * @return The {@link Expression} that is encapsulated within parenthesis
	 */
	public final AbstractExpression getExpression()
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
	boolean hasEncapsulatedExpression()
	{
		return hasExpression();
	}

	/**
	 * Determines whether the encapsulated expression of the query was parsed.
	 *
	 * @return <code>true</code> if the encapsulated expression was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public final boolean hasExpression()
	{
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant)
	{
		expression = parse
		(
			wordParser,
			encapsulatedExpressionBNF(),
			tolerant
		);
	}

	/**
	 * Sets
	 *
	 * @param expression
	 */
	final void setExpression(AbstractExpression expression)
	{
		this.expression = expression;
		this.expression.setParent(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedTextEncapsulatedExpression(StringBuilder writer)
	{
		if (expression != null)
		{
			expression.toParsedText(writer);
		}
	}
}