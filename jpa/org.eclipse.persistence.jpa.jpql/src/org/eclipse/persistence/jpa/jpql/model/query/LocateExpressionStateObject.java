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

import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>LOCATE</b></code> function returns the position of a given string within a string,
 * starting the search at a specified position. It returns the first position at which the string
 * was found as an integer. The first argument is the string to be located; the second argument is
 * the string to be searched; the optional third argument is an integer that represents the string
 * position at which the search is started (by default, the beginning of the string to be searched).
 * The first position in a string is denoted by 1. If the string is not found, 0 is returned. The
 * <code><b>LENGTH</b></code> function returns the length of the string in characters as an integer.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= LOCATE(string_primary, string_primary [, simple_arithmetic_expression])</code><p>
 *
 * @see LocateExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class LocateExpressionStateObject extends AbstractTripleEncapsulatedExpressionStateObject {

	/**
	 * Creates a new <code>LocateExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LocateExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>LocateExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} of the first expression
	 * @param secondStateObject The {@link StateObject} of the second expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LocateExpressionStateObject(StateObject parent,
	                                   StateObject firstStateObject,
	                                   StateObject secondStateObject) {

		super(parent, firstStateObject, secondStateObject, null);
	}

	/**
	 * Creates a new <code>LocateExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} of the first expression
	 * @param secondStateObject The {@link StateObject} of the second expression
	 * @param thirdStateObject The {@link StateObject} of the third encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LocateExpressionStateObject(StateObject parent,
	                                   StateObject firstStateObject,
	                                   StateObject secondStateObject,
	                                   StateObject thirdStateObject) {

		super(parent, firstStateObject, secondStateObject, thirdStateObject);
	}

	/**
	 * Creates a new <code>LocateExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstJpqlFragment The string representation of the first encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @param secondJpqlFragment The string representation of the second encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LocateExpressionStateObject(StateObject parent,
	                                   String firstJpqlFragment,
	                                   String secondJpqlFragment) {

		super(parent, firstJpqlFragment, secondJpqlFragment, null);
	}

	/**
	 * Creates a new <code>LocateExpressionStateObject</code>.
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
	public LocateExpressionStateObject(StateObject parent,
	                                   String firstJpqlFragment,
	                                   String secondJpqlFragment,
	                                   String thirdJpqlFragment) {

		super(parent, firstJpqlFragment, secondJpqlFragment, thirdJpqlFragment);
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
	public LocateExpression getExpression() {
		return (LocateExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFirstQueryBNFId() {
		return StringPrimaryBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return LOCATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSecondQueryBNFId() {
		return StringPrimaryBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getThirdQueryBNFId() {
		return SimpleArithmeticExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link LocateExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link LocateExpression parsed object} representing a <code><b>LOCATE</b></code>
	 * expression
	 */
	public void setExpression(LocateExpression expression) {
		super.setExpression(expression);
	}
}