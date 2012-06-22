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
 * The query BNF for a simple conditional expression.
 *
 * <div nowrap><b>BNF:</b> <code>simple_cond_expression ::= comparison_expression |
 * between_expression | in_expression | like_expression | null_comparison_expression |
 * empty_collection_comparison_expression | collection_member_expression |
 * exists_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SimpleConditionalExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "simple_cond_expression";

	/**
	 * Creates a new <code>SimpleCondExpressionBNF</code>.
	 */
	SimpleConditionalExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(ComparisonExpressionBNF.ID);
		registerChild(BetweenExpressionBNF.ID);
		registerChild(InExpressionBNF.ID);
		registerChild(LikeExpressionBNF.ID);
		registerChild(NullComparisonExpressionBNF.ID);
		registerChild(EmptyCollectionComparisonExpressionBNF.ID);
		registerChild(CollectionMemberExpressionBNF.ID);
		registerChild(ExistsExpressionBNF.ID);
	}
}