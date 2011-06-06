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
 * The query BNF for a comparison expression.
 *
 * <div nowrap><b>BNF:</b> <code>comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 * boolean_expression { = | <> } {boolean_expression | all_or_any_expression} |
 * enum_expression { = | <> } {enum_expression | all_or_any_expression} |
 * datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 * entity_expression { = | <> } {entity_expression | all_or_any_expression} |
 * arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression} |
 * entity_type_expression { = | <> } entity_type_expression}</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ComparisonExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "comparison_expression";

	/**
	 * Creates a new <code>ComparisonExpressionBNF</code>.
	 */
	public ComparisonExpressionBNF() {
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

		registerExpressionFactory(ComparisonExpressionFactory.ID);

		registerChild(AllOrAnyExpressionBNF.ID);
		registerChild(ArithmeticExpressionBNF.ID);
		registerChild(BooleanExpressionBNF.ID);
		registerChild(DatetimeExpressionBNF.ID);
		registerChild(EntityExpressionBNF.ID);
		registerChild(EntityTypeExpressionBNF.ID);
		registerChild(EnumExpressionBNF.ID);
		registerChild(StringExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompound() {
		return true;
	}
}