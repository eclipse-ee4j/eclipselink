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
 * The query BNF for a date/time primary expression.
 *
 * <div nowrap><b>BNF:</b> <code>datetime_primary ::= state_field_path_expression |
 * input_parameter | functions_returning_datetime | aggregate_expression |
 * case_expression | date_time_timestamp_literal</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class DateTimePrimaryBNF extends AbstractCompoundBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "datetime_primary";

	/**
	 * Creates a new <code>DatetimePrimaryBNF</code>.
	 */
	DateTimePrimaryBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		super.initialize();

		registerChild(InputParameterBNF.ID);
		registerChild(FunctionsReturningDatetimeBNF.ID);
		registerChild(AggregateExpressionBNF.ID);
		registerChild(CaseExpressionBNF.ID);
		registerChild(DateTimeTimestampLiteralBNF.ID);
		registerChild(StateFieldPathExpressionBNF.ID);
	}
}