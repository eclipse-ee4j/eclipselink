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
 * The <b>SIZE</b> function returns an integer value, the number of elements of
 * the collection. If the collection is empty, the <b>SIZE</b> function
 * evaluates to zero.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= SIZE(collection_valued_path_expression)</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class SizeExpression extends AbstractSingleEncapsulatedExpression
{
	/**
	 * Creates a new <code>SizeExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	SizeExpression(AbstractExpression parent)
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
	JPQLQueryBNF encapsulatedExpressionBNF()
	{
		return queryBNF(CollectionValuedPathExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(FunctionsReturningNumericsBNF.ID);
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

		CollectionValuedPathExpression expression = new CollectionValuedPathExpression(this, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser)
	{
		return SIZE;
	}
}