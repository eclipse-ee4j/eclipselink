/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for an aggregate expression.
 * <p>
 * JPA 1.0, 2.0:
 * <div nowrap><b>BNF:</b> <code>aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                                                        COUNT ([DISTINCT] identification_variable |
 *                                                                          state_field_path_expression |
 *                                                                          single_valued_object_path_expression)</code>
 * <p>
 * JPA 2.1:
 * <div nowrap><b>BNF:</b> <code>aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                                                        COUNT ([DISTINCT] identification_variable |
 *                                                                          state_field_path_expression |
 *                                                                          single_valued_object_path_expression) |
 *                                                        function_invocation</code>
 * <p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class AggregateExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "aggregate_expression";

	/**
	 * Creates a new <code>AggregateExpressionBNF</code>.
	 */
	public AggregateExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();

		registerExpressionFactory(AvgFunctionFactory.ID);
		registerExpressionFactory(MaxFunctionFactory.ID);
		registerExpressionFactory(MinFunctionFactory.ID);
		registerExpressionFactory(SumFunctionFactory.ID);
		registerExpressionFactory(CountFunctionFactory.ID);
	}
}