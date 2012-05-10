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
 * The query BNF for a function expression returning a string value.
 *
 * <div nowrap><b>BNF:</b> <code>functions_returning_strings ::= CONCAT(string_primary, string_primary {, string_primary}*) |
 * SUBSTRING(string_primary, simple_arithmetic_expression [, simple_arithmetic_expression]) |
 * TRIM([[trim_specification] [trim_character] FROM] string_primary) |
 * LOWER(string_primary) | UPPER(string_primary)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class FunctionsReturningStringsBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "functions_returning_strings";

	/**
	 * Creates a new <code>FunctionsReturningStringsBNF</code>.
	 */
	public FunctionsReturningStringsBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();

		registerExpressionFactory(ConcatExpressionFactory.ID);
		registerExpressionFactory(SubstringExpressionFactory.ID);
		registerExpressionFactory(TrimExpressionFactory.ID);
		registerExpressionFactory(LowerExpressionFactory.ID);
		registerExpressionFactory(UpperExpressionFactory.ID);
	}
}