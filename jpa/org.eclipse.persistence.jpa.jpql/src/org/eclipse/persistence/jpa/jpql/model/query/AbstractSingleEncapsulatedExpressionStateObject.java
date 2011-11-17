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
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;

/**
 * This {@link StateObject} represents a JPQL expression that has a JPQL identifier followed by
 * an encapsulated expression with parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression AbstractSingleEncapsulatedExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSingleEncapsulatedExpressionStateObject extends AbstractEncapsulatedExpressionStateObject {

	/**
	 * The {@link StateObject} representing the encapsulated expression.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the encapsulated {@link StateObject} has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Creates a new <code>AbstractSingleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractSingleEncapsulatedExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractSingleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractSingleEncapsulatedExpressionStateObject(StateObject parent,
	                                                          StateObject stateObject) {

		super(parent);
		this.stateObject = parent(stateObject);
	}

	/**
	 * Creates a new <code>AbstractSingleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractSingleEncapsulatedExpressionStateObject(StateObject parent,
	                                                          String jpqlFragment) {

		super(parent);
		parse(jpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (stateObject != null) {
			children.add(stateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSingleEncapsulatedExpression getExpression() {
		return (AbstractSingleEncapsulatedExpression) super.getExpression();
	}

	/**
	 * Returns the unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF
	 * JPQLQueryBNF} that will determine how to parse the encapsulated expression.
	 *
	 * @return The non-<code>null</code> ID of the BNF
	 */
	protected abstract String getQueryBNFId();

	/**
	 * Returns the encapsulated {@link StateObject}.
	 *
	 * @return The encapsulated {@link StateObject}
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Determines whether the {@link StateObject} representing the encapsulated expression is present
	 * or not.
	 *
	 * @return <code>true</code> if the encapsulated {@link StateObject} is not <code>null</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasStateObject() {
		return getStateObject() != null;
	}

	/**
	 * Parses the given JPQL fragment, which represents the encapsulated expression, and creates the
	 * {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, getQueryBNFId());
		setStateObject(stateObject);
	}

	/**
	 * Sets the given {@link StateObject} to represent the new encapsulated expression.
	 *
	 * @param stateObject The new encapsulated {@link StateObject}
	 */
	protected void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextEncapsulatedExpression(Appendable writer) throws IOException {
		if (stateObject != null) {
			stateObject.toString(writer);
		}
	}
}