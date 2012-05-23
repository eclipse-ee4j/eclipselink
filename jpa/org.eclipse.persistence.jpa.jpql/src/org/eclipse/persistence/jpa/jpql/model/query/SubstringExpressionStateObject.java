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

import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The second and third arguments of the <code><b>SUBSTRING</b></code> function denote the starting
 * position and length of the substring to be returned. These arguments are integers. The first
 * position of a string is denoted by 1. The <code><b>SUBSTRING</b></code> function returns a string.
 * <p>
 * JPA 1.0:
 * <div nowrap><b>BNF</b> ::= SUBSTRING(string_primary, simple_arithmetic_expression, simple_arithmetic_expression)<p>
 * JPA 2.0:
 * <div nowrap><b>BNF</b> ::= SUBSTRING(string_primary, simple_arithmetic_expression [, simple_arithmetic_expression])<p>
 *
 * @see SubstringExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SubstringExpressionStateObject extends AbstractTripleEncapsulatedExpressionStateObject {

	/**
	 * Creates a new <code>SubstringExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubstringExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>SubstringExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} of the first expression
	 * @param secondStateObject The {@link StateObject} of the second expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubstringExpressionStateObject(StateObject parent,
	                                      StateObject firstStateObject,
	                                      StateObject secondStateObject) {

		super(parent, firstStateObject, secondStateObject, null);
	}

	/**
	 * Creates a new <code>SubstringExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} of the first expression
	 * @param secondStateObject The {@link StateObject} of the second expression
	 * @param thirdStateObject The {@link StateObject} of the third encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubstringExpressionStateObject(StateObject parent,
	                                      StateObject firstStateObject,
	                                      StateObject secondStateObject,
	                                      StateObject thirdStateObject) {

		super(parent, firstStateObject, secondStateObject, thirdStateObject);
	}

	/**
	 * Creates a new <code>SubstringExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstJpqlFragment The string representation of the first encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @param secondJpqlFragment The string representation of the second encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubstringExpressionStateObject(StateObject parent,
	                                      String firstJpqlFragment,
	                                      String secondJpqlFragment) {

		super(parent, firstJpqlFragment, secondJpqlFragment, null);
	}

	/**
	 * Creates a new <code>SubstringExpressionStateObject</code>.
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
	public SubstringExpressionStateObject(StateObject parent,
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
	public SubstringExpression getExpression() {
		return (SubstringExpression) super.getExpression();
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
		return SUBSTRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSecondQueryBNFId() {
		return SimpleArithmeticExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getThirdQueryBNFId() {
		return SimpleArithmeticExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link SubstringExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SubstringExpression parsed object} representing a <code><b>SUBSTRING</b></code>
	 * expression
	 */
	public void setExpression(SubstringExpression expression) {
		super.setExpression(expression);
	}
}