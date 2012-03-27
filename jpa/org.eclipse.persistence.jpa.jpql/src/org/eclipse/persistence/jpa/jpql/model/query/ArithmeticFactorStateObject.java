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
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactorBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This state object simply adds a plus or minus sign to the arithmetic primary expression.
 *
 * <div nowrap><b>BNF:</b> <code>arithmetic_factor ::= [{+|-}] arithmetic_primary</code><p>
 *
 * @see ArithmeticFactor
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class ArithmeticFactorStateObject extends AbstractStateObject {

	/**
	 * <code>true</code> if the arithmetic sign is the plus sign; <code>false</code> otherwise.
	 */
	private boolean plusSign;

	/**
	 * The {@link StateObject} that represents the arithmetic primary expression.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the arithmetic sign property has changed.
	 */
	public static final String ARITHMETIC_SIGN_PROPERTY = "arithmeticSign";

	/**
	 * Notifies the state object property has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Creates a new <code>ArithmeticFactorStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ArithmeticFactorStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>ArithmeticFactorStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} that represents the arithmetic primary expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ArithmeticFactorStateObject(StateObject parent, boolean plusSign, StateObject stateObject) {
		super(parent);
		this.plusSign    = plusSign;
		this.stateObject = parent(stateObject);
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
	}

	/**
	 * Makes sure the <code><b>-</b></code> sign is used.
	 */
	public void addMinus() {
		if (plusSign) {
			setArithmeticSign(false);
		}
	}

	/**
	 * Makes sure the <code><b>+</b></code> sign is used.
	 */
	public void addPlus() {
		if (!plusSign) {
			setArithmeticSign(true);
		}
	}

	/**
	 * Returns the arithmetic sign this expression is actually representing.
	 *
	 * @return The single character value of the arithmetic sign: '+' or '-'
	 */
	public String getArithmeticSign() {
		return plusSign ? PLUS : MINUS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArithmeticFactor getExpression() {
		return (ArithmeticFactor) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} that represents the arithmetic primary expression.
	 *
	 * @return The {@link StateObject} that represents the arithmetic primary expression or
	 * <code>null</code> if none was specified
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Determines whether the arithmetic sign is the minus sign.
	 *
	 * @return <code>true</code> if the arithmetic sign is the minus sign; <code>false</code> if it
	 * is the plus sign
	 */
	public boolean hasMinusSign() {
		return !plusSign;
	}

	/**
	 * Determines whether the arithmetic sign is the plus sign.
	 *
	 * @return <code>true</code> if the arithmetic sign is the plus sign; <code>false</code> if it
	 * is the minus sign
	 */
	public boolean hasPlusSign() {
		return plusSign;
	}

	/**
	 * Determines whether an {@link StateObject} representing the arithmetic primary expression
	 * exists.
	 *
	 * @return <code>true</code> if there is the arithmetic primary expression exists; <code>false</code>
	 * otherwise
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			ArithmeticFactorStateObject arithmeticFactor = (ArithmeticFactorStateObject) stateObject;
			return plusSign == arithmeticFactor.plusSign &&
			       areEquivalent(stateObject, arithmeticFactor.stateObject);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment to become the new arithmetic primary.
	 *
	 * @param jpqlFragment A portion of a query that will be parsed and the {@link StateObject} that
	 * was created will become the new arithmetic primary of this {@link ArithmeticFactorStateObject}
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, ArithmeticFactorBNF.ID);
		setStateObject(stateObject);
	}

	/**
	 * Sets the arithmetic sign this expression is actually representing.
	 *
	 * @param plusSign The single character value of the arithmetic sign: '+' (<code>true</code>) or
	 * '-' (<code>false</code>)
	 */
	public void setArithmeticSign(boolean plusSign) {
		boolean oldPlusSign = plusSign;
		this.plusSign = !oldPlusSign;
		firePropertyChanged(ARITHMETIC_SIGN_PROPERTY, oldPlusSign, plusSign);
	}

	/**
	 * Keeps a reference of the {@link ArithmeticFactor parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link ArithmeticFactor parsed object} representing an arithmetic
	 * factor expression
	 */
	public void setExpression(ArithmeticFactor expression) {
		super.setExpression(expression);
	}

	/**
	 * Returns the {@link StateObject} that represents the arithmetic primary expression.
	 *
	 * @param stateObject The {@link StateObject} that represents the arithmetic primary expression
	 * or <code>null</code> if none was specified
	 */
	public void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
	}

	/**
	 * Toggles the arithmetic sign, plus becomes minus and vice versa.
	 */
	public void toggleArithmeticSign() {
		setArithmeticSign(!plusSign);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(getArithmeticSign());
		if (stateObject != null) {
			writer.append(SPACE);
			stateObject.toString(writer);
		}
	}
}