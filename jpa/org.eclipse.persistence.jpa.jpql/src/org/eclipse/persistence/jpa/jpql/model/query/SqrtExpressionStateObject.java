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

import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>SQRT</b></code> function takes a numeric argument and returns a <code>Double</code>.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= SQRT(simple_arithmetic_expression)</code><p>
 *
 * @see SqrtExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SqrtExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Creates a new <code>SqrtExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SqrtExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>SqrtExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SqrtExpressionStateObject(StateObject parent, StateObject stateObject) {
		super(parent, stateObject);
	}

	/**
	 * Creates a new <code>SqrtExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SqrtExpressionStateObject(StateObject parent, String jpqlFragment) {
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
	public SqrtExpression getExpression() {
		return (SqrtExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return SQRT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return SimpleArithmeticExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link SqrtExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SqrtExpression parsed object} representing a <code><b>SQRT</b></code>
	 * expression
	 */
	public void setExpression(SqrtExpression expression) {
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