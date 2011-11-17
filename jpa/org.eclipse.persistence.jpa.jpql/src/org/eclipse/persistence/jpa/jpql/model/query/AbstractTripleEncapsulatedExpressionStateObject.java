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
import org.eclipse.persistence.jpa.jpql.parser.AbstractTripleEncapsulatedExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * This {@link Expression} takes care of parsing an expression that encapsulates three expressions
 * separated by a comma.
 *
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(first_expression, second_expression, third_expression)</code><p>
 *
 * @see LocateExpressionStateObject
 * @see SubstringExpressionStateObject
 *
 * @see AbstractTripleEncapsulatedExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractTripleEncapsulatedExpressionStateObject extends AbstractEncapsulatedExpressionStateObject {

	/**
	 * The {@link StateObject} representing the first expression.
	 */
	private StateObject firstStateObject;

	/**
	 * The {@link StateObject} representing the second expression.
	 */
	private StateObject secondStateObject;

	/**
	 * The {@link StateObject} representing the third expression.
	 */
	private StateObject thirdStateObject;

	/**
	 * Notifies the first {@link StateObject} property has changed.
	 */
	public static final String FIRST_STATE_OBJECT_PROPERTY = "firstStateObject";

	/**
	 * Notifies the second {@link StateObject} property has changed.
	 */
	public static final String SECOND_STATE_OBJECT_PROPERTY = "secondStateObject";

	/**
	 * Notifies the third {@link StateObject} property has changed.
	 */
	public static final String THIRD_STATE_OBJECT_PROPERTY = "thirdStateObject";

	/**
	 * Creates a new <code>AbstractTripleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractTripleEncapsulatedExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractTripleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} of the first encapsulated expression
	 * @param secondStateObject The {@link StateObject} of the second encapsulated expression
	 * @param thirdStateObject The {@link StateObject} of the third encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractTripleEncapsulatedExpressionStateObject(StateObject parent,
	                                                          StateObject firstStateObject,
	                                                          StateObject secondStateObject,
	                                                          StateObject thirdStateObject) {

		super(parent);
		this.firstStateObject  = parent(firstStateObject);
		this.secondStateObject = parent(secondStateObject);
		this.thirdStateObject  = parent(thirdStateObject);
	}

	/**
	 * Creates a new <code>AbstractTripleEncapsulatedExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstJpqlFragment The string representation of the first encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @param secondJpqlFragment The string representation of the second encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @param thirdJpqlFragment The string representation of the third encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractTripleEncapsulatedExpressionStateObject(StateObject parent,
	                                                          String firstJpqlFragment,
	                                                          String secondJpqlFragment,
	                                                          String thirdJpqlFragment) {

		super(parent);
		parseFirst (firstJpqlFragment);
		parseSecond(secondJpqlFragment);
		parseThird (thirdJpqlFragment);
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

		if (thirdStateObject != null) {
			children.add(thirdStateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractTripleEncapsulatedExpression getExpression() {
		return (AbstractTripleEncapsulatedExpression) super.getExpression();
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
	 * Returns the {@link StateObject} representing the third expression.
	 *
	 * @return The third encapsulated {@link StateObject} or <code>null</code> if none exists
	 */
	public StateObject getThird() {
		return thirdStateObject;
	}

	/**
	 * Returns the unique identifier of the BNF that will be used to parse a JPQL fragment as the
	 * third encapsulated expression.
	 *
	 * @return The query BNF ID for the third encapsulated expression
	 */
	protected abstract String getThirdQueryBNFId();

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
	 * Determines whether the {@link StateObject} representing the third encapsulated expression is
	 * present or not.
	 *
	 * @return <code>true</code> if the third {@link StateObject} is not <code>null</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasThird() {
		return thirdStateObject != null;
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
	 * Parses the given JPQL fragment, which will represent the third encapsulated expression.
	 *
	 * @param jpqlFragment The string representation of the third encapsulated expression to parse and
	 * to convert into a {@link StateObject} representation
	 */
	public void parseThird(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, getThirdQueryBNFId());
		setThird(stateObject);
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
	 * Sets the given {@link StateObject} to represent the third encapsulated expression.
	 *
	 * @param thirdStateObject The new encapsulated {@link StateObject} representing the third
	 * expression
	 */
	public void setThird(StateObject thirdStateObject) {
		StateObject oldThirdStateObject = this.thirdStateObject;
		this.thirdStateObject = parent(thirdStateObject);
		firePropertyChanged(THIRD_STATE_OBJECT_PROPERTY, oldThirdStateObject, thirdStateObject);
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

		writer.append(COMMA);

		if (thirdStateObject != null) {
			writer.append(SPACE);
			thirdStateObject.toString(writer);
		}
	}
}