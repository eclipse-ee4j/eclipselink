/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * The query BNF for a entity or value expression.
 *
 * <div nowrap><b>BNF:</b> <code>entity_or_value_expression ::= single_valued_object_path_expression |
 * state_field_path_expression | simple_entity_or_value_expression</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class EntityOrValueExpressionBNF extends AbstractCompoundBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "entity_or_value_expression";

	/**
	 * Creates a new <code>EntityOrValueExpressionBNF</code>.
	 */
	EntityOrValueExpressionBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		super.initialize();

		registerChild(SingleValuedObjectPathExpressionBNF.ID);
		registerChild(StateFieldPathExpressionBNF.ID);
		registerChild(SimpleEntityOrValueExpressionBNF.ID);
	}
}