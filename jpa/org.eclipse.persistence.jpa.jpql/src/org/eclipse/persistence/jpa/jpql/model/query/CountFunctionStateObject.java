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

import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.InternalCountBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * One of the aggregate functions. The return type of this function is a <code>Long</code>.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= COUNT ([DISTINCT] identification_variable |
 *                                                                state_field_path_expression |
 *                                                                single_valued_object_path_expression)</code><p>
 *
 * @see CountFunction
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class CountFunctionStateObject extends AggregateFunctionStateObject {

	/**
	 * Creates a new <code>CountFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CountFunctionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>CountFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CountFunctionStateObject(StateObject parent, boolean distinct, StateObject stateObject) {
		super(parent, distinct, stateObject);
	}
	/**
	 * Creates a new <code>CountFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
	 * have distinct values; <code>false</code> if it is not required
	 * @param path Either the identification variable or the state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CountFunctionStateObject(StateObject parent, boolean distinct, String path) {
		super(parent, distinct, path);
	}

	/**
	 * Creates a new <code>CountFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CountFunctionStateObject(StateObject parent, StateObject stateObject) {
		super(parent, stateObject);
	}

	/**
	 * Creates a new <code>CountFunctionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path Either the identification variable or the state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CountFunctionStateObject(StateObject parent, String path) {
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
	public CountFunction getExpression() {
		return (CountFunction) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return COUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return InternalCountBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link CountFunction parsed object} object, which should only be done
	 * when this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link CountFunction parsed object} representing a <code><b>COUNT</b></code>
	 * expression
	 */
	public void setExpression(CountFunction expression) {
		super.setExpression(expression);
	}
}