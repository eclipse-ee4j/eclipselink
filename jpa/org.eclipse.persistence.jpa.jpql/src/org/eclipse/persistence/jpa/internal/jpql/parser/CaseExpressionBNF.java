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
 * The query BNF for a case expression.
 *
 * <div nowrap><b>BNF:</b> <code>case_expression ::= general_case_expression |
 * simple_case_expression | coalesce_expression | nullif_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class CaseExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "case_expression";

	/**
	 * Creates a new <code>CaseExpressionBNF</code>.
	 */
	CaseExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(GeneralCaseExpressionBNF.ID);
		registerChild(SimpleCaseExpressionBNF.ID);
		registerChild(CoalesceExpressionBNF.ID);
		registerChild(NullIfExpressionBNF.ID);
	}
}