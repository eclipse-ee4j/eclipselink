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

import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * One of the aggregate functions. The arguments must be numeric.
 * <p>
 * <code><b>SUM</b></code> returns <code>Long</code> when applied to state-fields of integral types
 * (other than <code>BigInteger</code>); <code>Double</code> when applied to state-fields of
 * floating point types; <code>BigInteger</code> when applied to state-fields of type
 * <code>BigInteger</code>; and <code>BigDecimal</code> when applied to state-fields of type
 * <code>BigDecimal</code>. If <code><b>SUM</b></code>, <code><b>AVG</b></code>, <code><b>MAX</b></code>,
 * or <code><b>MIN</b></code> is used, and there are no values to which the aggregate function can
 * be applied, the result of the aggregate function is <code>null</code>. If <code><b>COUNT</code></b>
 * is used, and there are no values to which <code><b>COUNT</b></code> can be applied, the result of
 * the aggregate function is 0.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= SUM([DISTINCT] state_field_path_expression)</code><p>
 *
 * @see SumFunction
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SumFunctionStateObject extends AggregateFunctionStateObject {

	/**
	 * Creates a new <code>SumFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SumFunctionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>SumFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SumFunctionStateObject(StateObject parent, boolean distinct, StateObject stateObject) {
		super(parent, distinct, stateObject);
	}

	/**
	 * Creates a new <code>SumFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 * @param path Either the identification variable or the state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SumFunctionStateObject(StateObject parent, boolean distinct, String path) {
		super(parent, distinct, path);
	}

	/**
	 * Creates a new <code>SumFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SumFunctionStateObject(StateObject parent, StateObject stateObject) {
		super(parent, stateObject);
	}

	/**
	 * Creates a new <code>SumFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path Either the identification variable or the state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SumFunctionStateObject(StateObject parent, String path) {
		super(parent, path);
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
	public SumFunction getExpression() {
		return (SumFunction) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return MAX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return StateFieldPathExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link SumFunction parsed object} object, which should only be done
	 * when this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link SumFunction parsed object} representing a <code><b>SUM</b></code>
	 * expression
	 */
	public void setExpression(SumFunction expression) {
		super.setExpression(expression);
	}
}