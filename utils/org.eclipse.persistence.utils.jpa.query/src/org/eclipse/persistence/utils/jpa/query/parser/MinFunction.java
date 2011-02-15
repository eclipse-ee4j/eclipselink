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
 * One of the aggregate functions. The arguments must correspond to orderable
 * state-field types (i.e., numeric types, string types, character types, or
 * date types). The return type of this function is based on the state-field's
 * type.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= MIN([DISTINCT] state_field_path_expression)</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class MinFunction extends AggregateFunction
{
	/**
	 * Creates a new <code>MinFunction</code>.
	 *
	 * @param parent The parent of this expression
	 */
	MinFunction(AbstractExpression parent)
	{
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser)
	{
		return MIN;
	}
}