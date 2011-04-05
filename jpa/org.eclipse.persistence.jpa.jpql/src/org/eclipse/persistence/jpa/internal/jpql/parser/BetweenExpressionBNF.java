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
package org.eclipse.persistence.jpa.internal.jpql.parser;

/**
 * The query BNF for the between expression.
 *
 * <div nowrap><b>BNF:</b> <code>between_expression ::= arithmetic_expression [NOT] BETWEEN arithmetic_expression AND arithmetic_expression |
 * string_expression [NOT] BETWEEN string_expression AND string_expression |
 * datetime_expression [NOT] BETWEEN datetime_expression AND datetime_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class BetweenExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this {@link BetweenExpressionBNF}.
	 */
	public static final String ID = "between_expression";

	/**
	 * Creates a new <code>BetweenExpressionBNF</code>.
	 */
	BetweenExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerExpressionFactory(BetweenExpressionFactory.ID);

		registerChild(ArithmeticExpressionBNF.ID);
		registerChild(StringExpressionBNF.ID);
		registerChild(DatetimeExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompound() {
		return true;
	}
}