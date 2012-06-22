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
package org.eclipse.persistence.jpa.internal.jpql.parser;

/**
 * The query BNF for a date/time primary expression.
 *
 * <div nowrap><b>BNF:</b> <code>datetime_primary ::= state_field_path_expression |
 * input_parameter | functions_returning_datetime | aggregate_expression |
 * case_expression | date_time_timestamp_literal</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DateTimePrimaryBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "datetime_primary";

	/**
	 * Creates a new <code>DatetimePrimaryBNF</code>.
	 */
	DateTimePrimaryBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(InputParameterBNF.ID);
		registerChild(FunctionsReturningDatetimeBNF.ID);
		registerChild(AggregateExpressionBNF.ID);
		registerChild(CaseExpressionBNF.ID);
		registerChild(DateTimeTimestampLiteralBNF.ID);
		registerChild(StateFieldPathExpressionBNF.ID);
	}
}