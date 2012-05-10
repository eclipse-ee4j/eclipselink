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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * This {@link KeyExpressionFactory} creates a new {@link KeyExpression} when the portion of the
 * query to parse starts with <b>KEY</b>.
 *
 * @see KeyExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class KeyExpressionFactory extends GeneralIdentificationExpressionFactory {

	/**
	 * The unique identifier of this {@link KeyExpressionFactory}.
	 */
	public static final String ID = Expression.KEY;

	/**
	 * Creates a new <code>KeyExpressionFactory</code>.
	 */
	public KeyExpressionFactory() {
		super(ID, Expression.KEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression buildExpression(AbstractExpression parent) {
		return new KeyExpression(parent);
	}
}