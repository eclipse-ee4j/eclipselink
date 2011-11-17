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

import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>ABS</b></code> function removes the minus sign from a specified argument and returns
 * the absolute value, which is always a positive number or zero.
 * <p>
 * This is one of the JPQL arithmetic functions. The <code><b>ABS</b></code> function takes a
 * numeric argument and returns a number (integer, float, or double) of the same type as the
 * argument to the function.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= ABS(simple_arithmetic_expression)</code>
 *
 * @see AbsExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class AbsExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Creates a new <code>AbsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbsExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbsExpressionStateObject(StateObject parent, StateObject stateObject) {
		super(parent, stateObject);
	}

	/**
	 * Creates a new <code>AbsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbsExpressionStateObject(StateObject parent, String jpqlFragment) {
		super(parent, jpqlFragment);
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
	public AbsExpression getExpression() {
		return (AbsExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return ABS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return SimpleArithmeticExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link AbsExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link AbsExpression parsed object} representing a <code><b>ABS</b></code>
	 * expression
	 */
	public void setExpression(AbsExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}
}