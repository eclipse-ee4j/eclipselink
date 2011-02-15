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

import java.util.List;

/**
 * An abstract schema name designates the abstract schema type over which the
 * query ranges.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class AbstractSchemaName extends AbstractExpression
{
	/**
	 * Creates a new <code>AbstractSchemaName</code>.
	 *
	 * @param parent The parent of this expression
	 * @param abstractSchemaName The abstract schema name
	 */
	AbstractSchemaName(AbstractExpression parent, String abstractSchemaName)
	{
		super(parent, abstractSchemaName);
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
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(AbstractSchemaNameBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText()
	{
		return super.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		wordParser.moveForward(getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		writer.append(getText());
	}
}