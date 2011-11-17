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
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * This {@link StateObject} represents a JPQL expression that has a JPQL identifier followed by
 * two an encapsulated expression with parenthesis, the two expression are separated by a comma.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(first_expression, second_expression)</code><p>
 *
 * @see ModExpressionStateObject
 * @see NullIfExpressionStateObject
 *
 * @see AbstractDoubleEncapsulatedExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractDoubleEncapsulatedExpressionStateObject extends AbstractEncapsulatedExpressionStateObject {

	/**
	 * The {@link StateObject} representing the first expression.
	 */
	private StateObject firstStateObject;

	/**
	 * The {@link StateObject} representing the second expression.
	 */
	private StateObject secondStateObject;

	/**
	 * Notifies the first {@link StateObject} property has changed.
	 */
	public static final String FIRST_STATE_OBJECT_PROPERTY = "firstStateObject";

	/**
	 * Notifies the second {@link StateObject} property has changed.
	 */
	public static final String SECOND_STATE_OBJECT_PROPERTY = "secondStateObject";

	/**
	 * Creates a new <code>AbstractDoubleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractDoubleEncapsulatedExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractDoubleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} representing the first expression
	 * @param secondStateObject The {@link StateObject} representing the second expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractDoubleEncapsulatedExpressionStateObject(StateObject parent,
	                                                          StateObject firstStateObject,
	                                                          StateObject secondStateObject) {

		super(parent);
		this.firstStateObject  = parent(firstStateObject);
		this.secondStateObject = parent(secondStateObject);
	}

	/**
	 * Creates a new <code>AbstractDoubleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstJpqlFragment The string representation of the first encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @param secondJpqlFragment The string representation of the second encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractDoubleEncapsulatedExpressionStateObject(StateObject parent,
	                                                       String firstJpqlFragment,
	                                                       String secondJpqlFragment) {

		super(parent);
		parseFirst(firstJpqlFragment);
		parseSecond(secondJpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {

		super.addChildren(children);

		if (firstStateObject != null) {
			children.add(firstStateObject);
		}

		if (secondStateObject != null) {
			children.add(secondStateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractDoubleEncapsulatedExpression getExpression() {
		return (AbstractDoubleEncapsulatedExpression) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} representing the first expression.
	 *
	 * @return The first encapsulated {@link StateObject} or <code>null</code> if none exists
	 */
	public StateObject getFirst() {
		return firstStateObject;
	}

	/**
	 * Returns the unique identifier of the BNF that will be used to parse a JPQL fragment as the
	 * first encapsulated expression.
	 *
	 * @return The query BNF ID for the first encapsulated expression
	 */
	protected abstract String getFirstQueryBNFId();

	/**
	 * Returns the {@link StateObject} representing the second expression.
	 *
	 * @return The second encapsulated {@link StateObject} or <code>null</code> if none exists
	 */
	public StateObject getSecond() {
		return secondStateObject;
	}

	/**
	 * Returns the unique identifier of the BNF that will be used to parse a JPQL fragment as the
	 * second encapsulated expression.
	 *
	 * @return The query BNF ID for the second encapsulated expression
	 */
	protected abstract String getSecondQueryBNFId();

	/**
	 * Determines whether the {@link StateObject} representing the first encapsulated expression is
	 * present or not.
	 *
	 * @return <code>true</code> if the first {@link StateObject} is not <code>null</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasFirst() {
		return firstStateObject != null;
	}

	/**
	 * Determines whether the {@link StateObject} representing the second encapsulated expression is
	 * present or not.
	 *
	 * @return <code>true</code> if the second {@link StateObject} is not <code>null</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSecond() {
		return secondStateObject != null;
	}

	/**
	 * Parses the given JPQL fragment, which will represent the first encapsulated expression.
	 *
	 * @param jpqlFragment The string representation of the first encapsulated expression to parse and
	 * to convert into a {@link StateObject} representation
	 */
	public void parseFirst(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, getFirstQueryBNFId());
		setFirst(stateObject);
	}

	/**
	 * Parses the given JPQL fragment, which will represent the second encapsulated expression.
	 *
	 * @param jpqlFragment The string representation of the second encapsulated expression to parse and
	 * to convert into a {@link StateObject} representation
	 */
	public void parseSecond(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, getSecondQueryBNFId());
		setSecond(stateObject);
	}

	/**
	 * Sets the given {@link StateObject} to represent the first encapsulated expression.
	 *
	 * @param firstStateObject The new encapsulated {@link StateObject} representing the first
	 * expression
	 */
	public void setFirst(StateObject firstStateObject) {
		StateObject oldFirstStateObject = this.firstStateObject;
		this.firstStateObject = parent(firstStateObject);
		firePropertyChanged(FIRST_STATE_OBJECT_PROPERTY, oldFirstStateObject, firstStateObject);
	}

	/**
	 * Sets the given {@link StateObject} to represent the second encapsulated expression.
	 *
	 * @param secondStateObject The new encapsulated {@link StateObject} representing the second
	 * expression
	 */
	public void setSecond(StateObject secondStateObject) {
		StateObject oldSecondStateObject = this.firstStateObject;
		this.secondStateObject = parent(secondStateObject);
		firePropertyChanged(SECOND_STATE_OBJECT_PROPERTY, oldSecondStateObject, secondStateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextEncapsulatedExpression(Appendable writer) throws IOException {

		if (firstStateObject != null) {
			firstStateObject.toString(writer);
			writer.append(SPACE);
		}

		writer.append(COMMA);

		if (secondStateObject != null) {
			writer.append(SPACE);
			secondStateObject.toString(writer);
		}
	}
}