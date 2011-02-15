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
 * The query BNF for an aggregate expression.
 *
 * <div nowrap><b>BNF:</b> <code>aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 * COUNT ([DISTINCT] identification_variable | state_field_path_expression | single_valued_object_path_expression)</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class AggregateExpressionBNF extends JPQLQueryBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "aggregate_expression";

	/**
	 * Creates a new <code>AggregateExpressionBNF</code>.
	 */
	AggregateExpressionBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		registerExpressionFactory(AvgFunctionFactory.ID);
		registerExpressionFactory(MaxFunctionFactory.ID);
		registerExpressionFactory(MinFunctionFactory.ID);
		registerExpressionFactory(SumFunctionFactory.ID);
		registerExpressionFactory(CountFunctionFactory.ID);
	}
}