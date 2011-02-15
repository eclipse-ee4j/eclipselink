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

/**
 * The query BNF for a simple arithmetic expression.
 *
 * <div nowrap><b>BNF:</b> <code>simple_arithmetic_expression ::= arithmetic_term | simple_arithmetic_expression { + | - } arithmetic_term</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class SimpleArithmeticExpressionBNF extends AbstractCompoundBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "simple_arithmetic_expression";

	/**
	 * Creates a new <code>SimpleArithmeticExpressionBNF</code>.
	 */
	SimpleArithmeticExpressionBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId()
	{
		return ArithmeticTermBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handleAggregate()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		super.initialize();

		registerExpressionFactory(ArithmeticExpressionFactory.ID);

		registerChild(ArithmeticTermBNF.ID);
	}
}