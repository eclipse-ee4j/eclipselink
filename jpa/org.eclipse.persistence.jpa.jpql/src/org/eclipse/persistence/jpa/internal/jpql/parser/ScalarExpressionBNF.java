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
 * The query BNF for a scalar expression.
 *
 * <div nowrap><b>BNF:</b> <code>scalar_expression ::= simple_arithmetic_expression |
 * string_primary | enum_primary | datetime_primary | boolean_primary |
 * case_expression | entity_type_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class ScalarExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "scalar_expression";

	/**
	 * Creates a new <code>SelectExpressionBNF</code>.
	 */
	ScalarExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackExpressionFactoryId() {
		return PreLiteralExpressionFactory.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(SimpleArithmeticExpressionBNF.ID);
		registerChild(StringPrimaryBNF.ID);
		registerChild(EnumPrimaryBNF.ID);
		registerChild(DateTimePrimaryBNF.ID);
		registerChild(BooleanPrimaryBNF.ID);
		registerChild(CaseExpressionBNF.ID);
		registerChild(EntityTypeExpressionBNF.ID);
	}
}