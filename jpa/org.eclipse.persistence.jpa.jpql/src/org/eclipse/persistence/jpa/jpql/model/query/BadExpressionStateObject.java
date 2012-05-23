/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.parser.BadExpression;

/**
 * This wraps an invalid portion of the JPQL query that could not be parsed.
 *
 * @see BadExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class BadExpressionStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>BadExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public BadExpressionStateObject(StateObject parent, String text) {
		super(parent, text);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BadExpression getExpression() {
		return (BadExpression) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link BadExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link BadExpression parsed object} representing a bad expression
	 */
	public void setExpression(BadExpression expression) {
		super.setExpression(expression);
	}
}