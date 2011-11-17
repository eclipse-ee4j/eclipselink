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
 * The query BNF for a entity expression.
 * <p>
 * JPA 1.0:
 * <div nowrap><b>BNF:</b> <code>entity_expression ::= single_valued_association_path_expression |
 *                                                     simple_entity_expression</code><p>
 *
 * JPA 2.0:
 * <div nowrap><b>BNF:</b> <code>entity_expression ::= single_valued_object_path_expression |
 *                                                     simple_entity_expression</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EntityExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "entity_expression";

	/**
	 * Creates a new <code>EntityExpressionBNF</code>.
	 */
	public EntityExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		registerChild(SimpleEntityExpressionBNF.ID);
	}
}