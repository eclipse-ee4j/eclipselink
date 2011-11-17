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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for a constructor item expression.
 *
 * <div nowrap><b>BNF:</b> <code>constructor_item ::= single_valued_path_expression |
 *                                                    scalar_expression |
 *                                                    aggregate_expression |
 *                                                    identification_variable</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ConstructorItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "constructor_item";

	/**
	 * Creates a new <code>ConstructorItemBNF</code>.
	 */
	public ConstructorItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleCollection(true);
		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
		registerChild(SingleValuedPathExpressionBNF.ID);
		registerChild(ScalarExpressionBNF.ID);
		registerChild(AggregateExpressionBNF.ID);
		registerChild(IdentificationVariableBNF.ID);
	}
}