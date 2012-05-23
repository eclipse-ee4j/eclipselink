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

import org.eclipse.persistence.jpa.jpql.parser.LogicalExpression;

/**
 * This expression represents a logical expression, which means the first and second expressions are
 * aggregated with either the <code><b>AND</b></code> or the <code><b>OR</b></code> operator.
 *
 * @see AndExpressionStateObject
 * @see OrExpressionStateObject
 *
 * @see LogicalExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class LogicalExpressionStateObject extends CompoundExpressionStateObject {

	/**
	 * Creates a new <code>LogicalExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected LogicalExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>LogicalExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftStateObject The {@link StateObject} representing the left expression
	 * @param rightStateObject The {@link StateObject} representing the right expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected LogicalExpressionStateObject(StateObject parent,
	                                       StateObject leftStateObject,
	                                       StateObject rightStateObject) {

		super(parent, leftStateObject, rightStateObject);
	}

	/**
	 * Creates a new <code>LogicalExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftJpqlFragment The string representation of the left expression to parse and to
	 * convert into a {@link StateObject}
	 * @param rightJpqlFragment The string representation of the right expression to parse and to
	 * convert into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected LogicalExpressionStateObject(StateObject parent,
	                                       String leftJpqlFragment,
	                                       String rightJpqlFragment) {

		super(parent, leftJpqlFragment, rightJpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogicalExpression getExpression() {
		return (LogicalExpression) super.getExpression();
	}
}