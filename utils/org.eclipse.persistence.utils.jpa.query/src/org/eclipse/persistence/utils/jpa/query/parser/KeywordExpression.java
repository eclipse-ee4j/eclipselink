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

import java.util.List;

/**
 * The expression representing some keywords: <code>TRUE</code>, <code>FALSE</code>
 * or <code>NULL</code>.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class KeywordExpression extends AbstractExpression
{
	/**
	 * Creates a new <code>KeywordExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	KeywordExpression(AbstractExpression parent)
	{
		super(parent);
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
		return queryBNF(BooleanLiteralBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		String word = parseIdentifier(wordParser);
		setText(word);

		wordParser.moveForward(word);
	}

	private String parseIdentifier(WordParser wordParser)
	{
		switch (wordParser.character())
		{
			case 'T': return TRUE;
			case 'F': return FALSE;
			default:  return NULL;
		}
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