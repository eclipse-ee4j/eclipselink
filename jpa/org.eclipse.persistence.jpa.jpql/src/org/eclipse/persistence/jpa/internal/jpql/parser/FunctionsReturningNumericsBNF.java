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
 * The query BNF for a function expression returning a numeric value.
 *
 * <div nowrap><b>BNF:</b> <code>functions_returning_numerics::= LENGTH(string_primary) |
 * LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 * ABS(simple_arithmetic_expression) | SQRT(simple_arithmetic_expression) |
 * MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 * SIZE(collection_valued_path_expression) |
 * INDEX(identification_variable)</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class FunctionsReturningNumericsBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "functions_returning_numerics";

	/**
	 * Creates a new <code>FunctionsReturningNumericsBNF</code>.
	 */
	FunctionsReturningNumericsBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerExpressionFactory(LengthExpressionFactory.ID);
		registerExpressionFactory(LocateExpressionFactory.ID);
		registerExpressionFactory(AbsExpressionFactory.ID);
		registerExpressionFactory(SqrtExpressionFactory.ID);
		registerExpressionFactory(ModExpressionFactory.ID);
		registerExpressionFactory(SizeExpressionFactory.ID);
		registerExpressionFactory(IndexExpressionFactory.ID);

		// EclipseLink's extension
		registerChild(FuncExpressionBNF.ID);
	}
}