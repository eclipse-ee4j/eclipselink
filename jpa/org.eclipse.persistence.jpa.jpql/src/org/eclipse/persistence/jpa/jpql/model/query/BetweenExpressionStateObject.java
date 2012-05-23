/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.InternalBetweenExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * Used in conditional expression to determine whether the result of an expression falls within an
 * inclusive range of values. Numeric, string and date expression can be evaluated in this way.
 *
 * <div nowrap><b>BNF:</b> <code>between_expression ::= arithmetic_expression [NOT] BETWEEN arithmetic_expression AND arithmetic_expression |<br>
 *                                                      string_expression [NOT] BETWEEN string_expression AND string_expression |<br>
 *                                                      datetime_expression [NOT] BETWEEN datetime_expression AND datetime_expression</code></div><p>
 *
 * @see BetweenExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class BetweenExpressionStateObject extends AbstractStateObject {

	/**
	 * The {@link StateObject} representing the lower bound expression.
	 */
	private StateObject lowerBoundStateObject;

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 * The {@link StateObject} representing the expression to determine if its result falls within
	 * the lower and upper bounds.
	 */
	private StateObject stateObject;

	/**
	 * The {@link StateObject} representing the upper bound expression.
	 */
	private StateObject upperBoundStateObject;

	/**
	 * Notifies the {@link StateObject} representing the lower bound expression has changed.
	 */
	public static final String LOWER_STATE_OBJECT_PROPERTY = "lowerBoundStateObject";

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static final String NOT_PROPERTY = "not";

	/**
	 * Notifies the state object property has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Notifies the {@link StateObject} representing the upper bound expression has changed.
	 */
	public static final String UPPER_STATE_OBJECT_PROPERTY = "upperBoundStateObject";

	/**
	 * Creates a new <code>BetweenExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public BetweenExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>BetweenExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the expression to compare its result
	 * to the lower and upper bounds
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param lowerBound The {@link StateObject} representing the lower bound expression
	 * @param upperBound The {@link StateObject} representing the upper bound expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public BetweenExpressionStateObject(StateObject parent,
	                                    StateObject stateObject,
	                                    boolean not,
	                                    StateObject lowerBound,
	                                    StateObject upperBound) {

		super(parent);
		this.not = not;
		this.stateObject           = parent(stateObject);
		this.lowerBoundStateObject = parent(lowerBound);
		this.upperBoundStateObject = parent(upperBound);
	}

	/**
	 * Creates a new <code>BetweenExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the expression to compare its result
	 * to the lower and upper bounds
	 * @param lowerBound The {@link StateObject} representing the lower bound expression
	 * @param upperBound The {@link StateObject} representing the upper bound expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public BetweenExpressionStateObject(StateObject parent,
	                                    StateObject stateObject,
	                                    StateObject lowerBound,
	                                    StateObject upperBound) {

		this(parent, stateObject, false, lowerBound, upperBound);
	}

	/**
	 * Creates a new <code>BetweenExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The JPQL fragment representing the expression to compare its result to the
	 * lower and upper bounds, the fragment will be parsed and converted into a {@link StateObject}
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param lowerBoundJpqlFragment The JPQL fragment representing the lower bound of the range, the
	 * fragment will be parsed and converted into a {@link StateObject}
	 * @param upperBoundJpqlFragment The JPQL fragment representing the upper bound of the range, the
	 * fragment will be parsed and converted into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public BetweenExpressionStateObject(StateObject parent,
	                                    String jpqlFragment,
	                                    boolean not,
	                                    String lowerBoundJpqlFragment,
	                                    String upperBoundJpqlFragment) {

		super(parent);
		this.not = not;
		parse(jpqlFragment);
		parseLowerBound(lowerBoundJpqlFragment);
		parseUpperBound(upperBoundJpqlFragment);
	}

	/**
	 * Creates a new <code>BetweenExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The JPQL fragment representing the expression to compare its result to the
	 * lower and upper bounds, the fragment will be parsed and converted into a {@link StateObject}
	 * @param lowerBoundJpqlFragment The JPQL fragment representing the lowe bound of the range, the
	 * fragment will be parsed and converted into a {@link StateObject}
	 * @param upperBoundJpqlFragment The JPQL fragment representing the upper bound of the range, the
	 * fragment will be parsed and converted into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public BetweenExpressionStateObject(StateObject parent,
	                                    String jpqlFragment,
	                                    String lowerBoundJpqlFragment,
	                                    String upperBoundJpqlFragment) {

		this(parent, jpqlFragment, false, lowerBoundJpqlFragment, upperBoundJpqlFragment);
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
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (stateObject != null) {
			children.add(stateObject);
		}
		if (lowerBoundStateObject != null) {
			children.add(lowerBoundStateObject);
		}
		if (upperBoundStateObject != null) {
			children.add(upperBoundStateObject);
		}
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public BetweenExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BetweenExpression getExpression() {
		return (BetweenExpression) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} representing the lower bound of the range.
	 *
	 * @return The expression representing the lower bound
	 */
	public StateObject getLowerBound() {
		return lowerBoundStateObject;
	}

	/**
	 * Returns the {@link StateObject} representing the expression to determine if its result falls
	 * within the lower and upper bounds.
	 *
	 * @return The expression to check if its result is in the range of the lower and upper bounds
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Returns the {@link StateObject} representing the upper bound of the range.
	 *
	 * @return The expression representing the upper bound
	 */
	public StateObject getUpperBound() {
		return upperBoundStateObject;
	}

	/**
	 * Determines whether the {@link StateObject} representing the lower bound is defined or not.
	 *
	 * @return <code>true</code> if the {@link StateObject} representing the expression to check if
	 * its result falls into a range has been defined; <code>false</code> otherwise
	 */
	public boolean hasLowerBound() {
		return lowerBoundStateObject != null;
	}

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>NOT</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return not;
	}

	/**
	 * Determines whether the {@link StateObject} representing the expression to determine if its
	 * result falls within the lower and upper bounds has been defined or not.
	 *
	 * @return <code>true</code> if the {@link StateObject} representing the lower bound expression
	 * has been defined; <code>false</code> otherwise
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * Determines whether the {@link StateObject} representing the upper bound is defined or not.
	 *
	 * @return <code>true</code> if the {@link StateObject} representing the upper bound expression
	 * has been defined; <code>false</code> otherwise
	 */
	public boolean hasUpperBound() {
		return upperBoundStateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			BetweenExpressionStateObject between = (BetweenExpressionStateObject) stateObject;
			return not == between.not &&
			       areEquivalent(stateObject,           between.stateObject)           &&
			       areEquivalent(lowerBoundStateObject, between.lowerBoundStateObject) &&
			       areEquivalent(upperBoundStateObject, between.upperBoundStateObject);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment, which will represent the expression to compare its result to
	 * the lower and upper bounds.
	 *
	 * @param jpqlFragment The JPQL fragment representing the expression to compare its result to the
	 * lower and upper bounds, the fragment will be parsed and converted into a {@link StateObject}
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, InternalBetweenExpressionBNF.ID);
		setStateObject(stateObject);
	}

	/**
	 * Parses the given JPQL fragment, which will represent the lower bound of the range.
	 *
	 * @param jpqlFragment The JPQL fragment representing the lower bound of the range, the fragment
	 * will be parsed and converted into a {@link StateObject}
	 */
	public void parseLowerBound(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, InternalBetweenExpressionBNF.ID);
		setLowerBound(stateObject);
	}

	/**
	 * Parses the given JPQL fragment, which will represent the upper bound of the range.
	 *
	 * @param jpqlFragment The JPQL fragment representing the upper bound of the range, the fragment
	 * will be parsed and converted into a {@link StateObject}
	 */
	public void parseUpperBound(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, InternalBetweenExpressionBNF.ID);
		setUpperBound(stateObject);
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (not) {
			setNot(false);
		}
	}

	/**
	 * Keeps a reference of the {@link BetweenExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link BetweenExpression parsed object} representing a <code><b>BETWEEN</b></code>
	 * expression
	 */
	public void setExpression(BetweenExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the {@link StateObject} representing the lower bound of the range.
	 *
	 * @param lowerBound The {@link StateObject} representing the lower bound expression
	 */
	public void setLowerBound(StateObject lowerBound) {
		StateObject oldLowerBoundStateObject = lowerBoundStateObject;
		lowerBoundStateObject = parent(lowerBound);
		firePropertyChanged(LOWER_STATE_OBJECT_PROPERTY, oldLowerBoundStateObject, lowerBoundStateObject);
	}

	/**
	 * Sets whether the <code><b>NOT</b></code> identifier should be part of the expression or not.
	 *
	 * @param not <code>true</code> if the <code><b>NOT</b></code> identifier should be part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setNot(boolean not) {
		boolean oldNot = this.not;
		this.not = not;
		firePropertyChanged(NOT_PROPERTY, oldNot, not);
	}

	/**
	 * Sets the {@link StateObject} representing the expression to determine if its result falls
	 * within the lower and upper bounds.
	 *
	 * @param stateObject The expression to check if its result is in the range of the lower and
	 * upper bounds
	 */
	public void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
	}

	/**
	 * Sets the {@link StateObject} representing the upper bound of the range.
	 *
	 * @param upperBound The {@link StateObject} representing the upper bound expression
	 */
	public void setUpperBound(StateObject upperBound) {
		StateObject oldUpperBoundStateObject = upperBoundStateObject;
		upperBoundStateObject = parent(upperBound);
		firePropertyChanged(UPPER_STATE_OBJECT_PROPERTY, oldUpperBoundStateObject, upperBoundStateObject);
	}

	/**
	 * Changes the visibility state of the <code><b>NOT</b></code> identifier.
	 */
	public void toggleNot() {
		setNot(!not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		if (stateObject != null) {
			stateObject.toString(writer);
			writer.append(SPACE);
		}

		writer.append(not ? NOT_BETWEEN : BETWEEN);

		if (lowerBoundStateObject != null) {
			writer.append(SPACE);
			lowerBoundStateObject.toString(writer);
		}

		writer.append(SPACE);
		writer.append(AND);

		if (upperBoundStateObject != null) {
			writer.append(SPACE);
			upperBoundStateObject.toString(writer);
		}
	}
}