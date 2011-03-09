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
 * The query BNF for a select expression.
 *
 * <div nowrap><b>BNF:</b> <code>select_expression ::= single_valued_path_expression |
 * scalar_expression | aggregate_expression | identification_variable |
 * OBJECT(identification_variable) | constructor_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class SelectExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "select_expression";

	/**
	 * Creates a new <code>SelectExpressionBNF</code>.
	 */
	SelectExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(SingleValuedPathExpressionBNF.ID);
		registerChild(ScalarExpressionBNF.ID);
		registerChild(AggregateExpressionBNF.ID);
		registerChild(IdentificationVariableBNF.ID);
		registerChild(ObjectExpressionBNF.ID);
		registerChild(ConstructorExpressionBNF.ID);
	}
}