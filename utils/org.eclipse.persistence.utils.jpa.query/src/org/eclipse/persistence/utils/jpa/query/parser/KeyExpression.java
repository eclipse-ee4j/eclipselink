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

/**
 * This is part of JPA 2.0.
 * <p>
 * <div nowrap><b>BNF:</b> <code>KEY(identification_variable)</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class KeyExpression extends EncapsulatedIdentificationVariableExpression
{
	/**
	 * Creates a new <code>KeyExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	KeyExpression(AbstractExpression parent)
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
	public String getIdentifier()
	{
		return KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression parse(WordParser wordParser,
	                         JPQLQueryBNF queryBNF,
	                         boolean tolerant)
	{
		if (tolerant)
		{
			return super.parse(wordParser, queryBNF, tolerant);
		}

		String word = wordParser.word();

		IdentificationVariable expression = new IdentificationVariable(this, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}