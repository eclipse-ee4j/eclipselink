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

import java.io.IOException;
import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * In the <code><b>SELECT</b></code> clause the result of a query may be the result of an aggregate
 * function applied to a path expression.
 * <p>
 * <pre><code>BNF: aggregate_expression ::= {AVG|MAX|MIN|SUM}([DISTINCT] state_field_path_expression) |
 *                              COUNT([DISTINCT] identification_variable | state_field_path_expression | single_valued_association_path_expression)</code></pre>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.AggregateFunction AggregateFunction
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public abstract class AggregateFunctionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Determines whether the <code><b>DISTINCT</b></code> keyword is part of the query, which is
	 * used to return only distinct (different) values.
	 */
	private boolean distinct;

	/**
	 * Notifies the visibility of the <code><b>DISTINCT</b></code> identifier has changed.
	 */
	public static final String DISTINCT_PROPERTY = "distinct";

	/**
	 * Creates a new <code>AggregateFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AggregateFunctionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AggregateStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AggregateFunctionStateObject(StateObject parent,
	                                       boolean distinct,
	                                       StateObject stateObject) {

		super(parent, stateObject);
		this.distinct = distinct;
	}

	/**
	 * Creates a new <code>AggregateFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 * @param path Either the identification variable or the state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AggregateFunctionStateObject(StateObject parent, boolean distinct, String path) {
		super(parent, path);
		this.distinct = distinct;
	}

	/**
	 * Creates a new <code>AggregateStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AggregateFunctionStateObject(StateObject parent, StateObject stateObject) {
		this(parent, false, stateObject);
	}

	/**
	 * Creates a new <code>AggregateFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path Either the identification variable or the state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AggregateFunctionStateObject(StateObject parent, String path) {
		super(parent, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AggregateFunction getExpression() {
		return (AggregateFunction) super.getExpression();
	}

	/**
	 * Sets whether the <code><b>DISTINCT</b></code> keyword should be part of the query, which is
	 * used to return only distinct (different) values.
	 *
	 * @return <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to have
	 * distinct values; <code>false</code> if it is not required
	 */
	public boolean hasDistinct() {
		return distinct;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			AggregateFunctionStateObject function = (AggregateFunctionStateObject) stateObject;
			return distinct == function.distinct;
		}

		return false;
	}

	/**
	 * Sets whether the <code><b>DISTINCT</b></code> keyword should be part of the query, which is
	 * used to return only distinct (different) values
	 *
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 */
	public void setDistinct(boolean distinct) {
		boolean oldDistinct = this.distinct;
		this.distinct = distinct;
		firePropertyChanged(DISTINCT_PROPERTY, oldDistinct, distinct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}

	/**
	 * Reverses the visibility of the <code><b>DISTINCT</b></code> identifier.
	 */
	public void toggleDistinct() {
		setDistinct(!hasDistinct());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextEncapsulatedExpression(Appendable writer) throws IOException {

		if (hasDistinct()) {
			writer.append(DISTINCT);
			writer.append(SPACE);
		}

		super.toTextEncapsulatedExpression(writer);
	}
}