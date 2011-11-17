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

import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * <code><b>NULLIF</b></code> returns the first expression if the two expressions are not equal. If
 * the expressions are equal, <code><b>NULLIF</b></code> returns a null value of the type of the
 * first expression.
 * <p>
 * <code><b>NULLIF</b></code> is equivalent to a searched <code><b>CASE</b></code> expression in
 * which the two expressions are equal and the resulting expression is <code><b>NULL</b></code>.
 * <p>
 * Returns the same type as the first expression.
 *
 * <div nowrap><b>BNF:</b> <code>nullif_expression::= NULLIF(scalar_expression, scalar_expression)</code><p>
 *
 * @see NullIfExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class NullIfExpressionStateObject extends AbstractDoubleEncapsulatedExpressionStateObject {

	/**
	 * Creates a new <code>NullIfExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullIfExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>NullIfExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstStateObject The {@link StateObject} representing the first expression
	 * @param secondStateObject The {@link StateObject} representing the second expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullIfExpressionStateObject(StateObject parent,
	                                   StateObject firstStateObject,
	                                   StateObject secondStateObject) {

		super(parent, firstStateObject, secondStateObject);
	}

	/**
	 * Creates a new <code>NullIfExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param firstJpqlFragment  The string representation of the first encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @param secondJpqlFragment  The string representation of the second encapsulated expression to
	 * parse and to convert into a {@link StateObject} representation
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullIfExpressionStateObject(StateObject parent,
	                                   String firstJpqlFragment,
	                                   String secondJpqlFragment) {

		super(parent, firstJpqlFragment, secondJpqlFragment);
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
	public NullIfExpression getExpression() {
		return (NullIfExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getFirstQueryBNFId() {
		return ScalarExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return NULLIF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSecondQueryBNFId() {
		return ScalarExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link NullIfExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link NullIfExpression parsed object} representing a <code><b>NULLIF</b></code>
	 * expression
	 */
	public void setExpression(NullIfExpression expression) {
		super.setExpression(expression);
	}
}