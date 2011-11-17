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
 * The query BNF for a new value expression.
 *
 * <div nowrap><b>BNF:</b> <code>new_value ::= scalar_expression | simple_entity_expression | NULL</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class NewValueBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "new_value";

	/**
	 * Creates a new <code>NewValueBNF</code>.
	 */
	public NewValueBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(PreLiteralExpressionFactory.ID);
		registerExpressionFactory(KeywordExpressionFactory.ID);
		registerChild(ScalarExpressionBNF.ID);
		registerChild(SimpleEntityExpressionBNF.ID);
	}
}