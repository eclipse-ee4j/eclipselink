/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link DateTimeFactory} creates a new {@link DateTime} when the portion
 * of the query to parse starts with <b>CURRENT_DATE</b>, <b>CURRENT_TIME</b>,
 * <b>CURRENT_TIMESTAMP</b> or with the JDBC escape format used for date/time/
 * timestamp.
 *
 * @see DateTime
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class DateTimeFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link FunctionsReturningDatetimeFactory}.
	 */
	static final String ID = "functions_returning_datetime";

	/**
	 * Creates a new <code>DateTimeFactory</code>.
	 */
	DateTimeFactory() {
		super(ID, Expression.CURRENT_DATE,
		          Expression.CURRENT_TIME,
		          Expression.CURRENT_TIMESTAMP,
		          "{");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant) {
		expression = new DateTime(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}