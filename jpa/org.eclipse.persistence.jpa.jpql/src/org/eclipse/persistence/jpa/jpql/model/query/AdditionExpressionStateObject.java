/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * TODO:
 *
 * <div nowrap><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression + arithmetic_term</code><p>
 *
 * @see AdditionExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class AdditionExpressionStateObject extends ArithmeticExpressionStateObject {

	/**
	 * Creates a new <code>AdditionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AdditionExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AdditionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftStateObject The {@link StateObject} representing the left expression
	 * @param rightStateObject The {@link StateObject} representing the right expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AdditionExpressionStateObject(StateObject parent,
	                                     StateObject leftStateObject,
	                                     StateObject rightStateObject) {

		super(parent, leftStateObject, rightStateObject);
	}

	/**
	 * Creates a new <code>AdditionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftJpqlFragment The string representation of the left expression to parse and to
	 * convert into a {@link StateObject}
	 * @param rightJpqlFragment The string representation of the right expression to parse and to
	 * convert into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AdditionExpressionStateObject(StateObject parent,
	                                     String leftJpqlFragment,
	                                     String rightJpqlFragment) {

		super(parent, leftJpqlFragment, rightJpqlFragment);
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
	public AdditionExpression getExpression() {
		return (AdditionExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return PLUS;
	}

	/**
	 * Keeps a reference of the {@link AdditionExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link AdditionExpression parsed object} representing an addition
	 * expression
	 */
	public void setExpression(AdditionExpression expression) {
		super.setExpression(expression);
	}
}