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
 * The <b>CONCAT</b> function returns a string that is a concatenation of its
 * arguments.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary)</code><p>
 * <p>
 * <div nowrap>Example: <b>SELECT</b> c.firstName <b>FROM</b> Customer c <b>HAVING</b> c.firstName = <b>CONCAT</b>(:fname, :lname)</p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class ConcatExpression extends AbstractDoubleEncapsulatedExpression
{
	/**
	 * Creates a new <code>ConcatExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	ConcatExpression(AbstractExpression parent)
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
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(FunctionsReturningStringsBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF parameterExpressionBNF(int index)
	{
		return queryBNF(StringPrimaryBNF.ID);
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

		return parseWithoutCollection(wordParser, queryBNF, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser)
	{
		return CONCAT;
	}
}