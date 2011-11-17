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
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * A compound {@link StateObject} has a left and right expressions combined by an identifier.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= left_expression &lt;identifier&gt; right_expression</code><p>
 *
 * @see CompoundExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class CompoundExpressionStateObject extends AbstractStateObject {

	/**
	 * The {@link StateObject} representing the left expression.
	 */
	private StateObject leftStateObject;

	/**
	 * The {@link StateObject} representing the right expression.
	 */
	private StateObject rightStateObject;

	/**
	 * Notifies the left state object property has changed.
	 */
	public static final String LEFT_STATE_OBJECT_PROPERTY = "leftStateObject";

	/**
	 * Notifies the right state object property has changed.
	 */
	public static final String RIGHT_STATE_OBJECT_PROPERTY = "rightStateObject";

	/**
	 * Creates a new <code>CompoundExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected CompoundExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>ArithmeticExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftStateObject The {@link StateObject} representing the left expression
	 * @param rightStateObject The {@link StateObject} representing the right expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected CompoundExpressionStateObject(StateObject parent,
	                                        StateObject leftStateObject,
	                                        StateObject rightStateObject) {
		super(parent);
		this.leftStateObject  = parent(leftStateObject);
		this.rightStateObject = parent(rightStateObject);
	}

	/**
	 * Creates a new <code>ArithmeticExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftJpqlFragment The string representation of the left expression to parse and to
	 * convert into a {@link StateObject}
	 * @param rightJpqlFragment The string representation of the right expression to parse and to
	 * convert into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected CompoundExpressionStateObject(StateObject parent,
	                                        String leftJpqlFragment,
	                                        String rightJpqlFragment) {

		super(parent);
		parseLeft (leftJpqlFragment);
		parseRight(rightJpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (leftStateObject != null) {
			children.add(leftStateObject);
		}
		if (rightStateObject != null) {
			children.add(rightStateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompoundExpression getExpression() {
		return (CompoundExpression) super.getExpression();
	}

	/**
	 * Returns the identifier joining the two {@link StateObject StateObjects}.
	 *
	 * @return The JPQL identifier join two expressions
	 */
	public abstract String getIdentifier();

	/**
	 * Returns the {@link StateObject} that represents the left expression.
	 *
	 * @return The {@link StateObject} representing the left expression
	 */
	public StateObject getLeft() {
		return leftStateObject;
	}

	/**
	 * Returns the unique identifier of the BNF that will be used to parse a JPQL fragment as the
	 * left side of the expression.
	 *
	 * @return The query BNF ID for the left side of the expression
	 */
	protected abstract String getLeftQueryBNFId();

	/**
	 * Returns the {@link StateObject} that represents the right expression.
	 *
	 * @return The {@link StateObject} representing the right expression
	 */
	public StateObject getRight() {
		return rightStateObject;
	}

	/**
	 * Returns the unique identifier of the BNF that will be used to parse a JPQL fragment as the
	 * right side of the expression.
	 *
	 * @return The query BNF ID for the right side of the expression
	 */
	protected abstract String getRightQueryBNFId();

	/**
	 * Determines whether there is a {@link StateObject} that represents the left expression.
	 *
	 * @return <code>true</code> if there is a left {@link StateObject}; <code>false</code> if it is
	 * <code>null</code>
	 */
	public boolean hasLeft() {
		return leftStateObject != null;
	}

	/**
	 * Determines whether there is a {@link StateObject} that represents the right expression.
	 *
	 * @return <code>true</code> if there is a right {@link StateObject}; <code>false</code> if it
	 * is <code>null</code>
	 */
	public boolean hasRight() {
		return rightStateObject != null;
	}

	/**
	 * Parses the given JPQL fragment and update the left side of the compound expression.
	 *
	 * @param jpqlFragment The portion of the query to become the left side of the compound expression
	 */
	public void parseLeft(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, getLeftQueryBNFId());
		setLeft(stateObject);
	}

	/**
	 * Parses the given JPQL fragment and update the right side of the compound expression.
	 *
	 * @param jpqlFragment The portion of the query to become the right side of the compound expression
	 */
	public void parseRight(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, getRightQueryBNFId());
		setLeft(stateObject);
	}

	/**
	 * Sets the left {@link StateObject} to become the given object.
	 *
	 * @param leftStateObject The {@link StateObject} representing the left expression
	 */
	public void setLeft(StateObject leftStateObject) {
		StateObject oldLeftStateObject = this.leftStateObject;
		this.leftStateObject = parent(leftStateObject);
		firePropertyChanged(LEFT_STATE_OBJECT_PROPERTY, oldLeftStateObject, leftStateObject);
	}

	/**
	 * Sets the right {@link StateObject} to become the given object.
	 *
	 * @param rightStateObject The {@link StateObject} representing the right expression
	 */
	public void setRight(StateObject rightStateObject) {
		StateObject oldRightStateObject = this.rightStateObject;
		this.rightStateObject = parent(rightStateObject);
		firePropertyChanged(RIGHT_STATE_OBJECT_PROPERTY, oldRightStateObject, rightStateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		if (leftStateObject != null) {
			leftStateObject.toString(writer);
		}

		writer.append(SPACE);
		writer.append(getIdentifier());
		writer.append(SPACE);

		if (rightStateObject != null) {
			rightStateObject.toString(writer);
		}
	}
}