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

import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalTermBNF;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>OR</b></code> logical operator chains multiple criteria together. A valid operand
 * of an <code><b>OR</b></code> operator must be one of: <code><b>TRUE</b></code>,
 * <code><b>FALSE</b></code>, and <code><b>NULL</b></code>. The <code><b>OR</b></code> operator has
 * a lower precedence than the <code><b>AND</b></code> operator.
 * <p>
 * <code><b>NULL</b></code> represents unknown. Therefore, if one operand is <code><b>NULL</b></code>
 * and the other operand is <code><b>TRUE</b></code> the result is <code><b>TRUE</b></code>, because
 * one <code><b>TRUE</b></code> operand is sufficient for a <code><b>TRUE</b></code> result. If one
 * operand is <code><b>NULL</b></code> and the other operand is either <code><b>FALSE</b></code> or
 * <code><b>NULL</b></code>, the result is <code><b>NULL</b></code> (unknown).
 * <p>
 * The following table shows how the <b>OR</b> operator is evaluated based on its two operands:
 * <p>
 * <code>
 * <style type="text/css">td {border-top:solid 1px; border-bottom:solid 0px; border-left:solid 1px; border-right:solid 0px}</style>
 * <table cellspacing="0" cellpadding="2" border="1" width="250" style="border:1px outset darkgrey;">
 * <tr><td></td><td style="border-top="0""><b>TRUE</b></td><td style="border-top="0""><b>FALSE</b><td style="border-top="0""><b>NULL</b></td></tr>
 * <tr><td style="border-left="0""><b>TRUE</b></td><td>TRUE</td><td>TRUE<td>TRUE</td></tr>
 * <tr><td style="border-left="0""><b>FALSE</b></td><td>TRUE</td><td>FALSE<td>NULL</td></tr>
 * <tr><td style="border-left="0""><b>NULL</b></td><td>TRUE</td><td>NULL<td>NULL</td></tr>
 * </table>
 * </code>
 *
 * <div nowrap><b>BNF:</b> <code>conditional_expression ::= conditional_expression OR conditional_term</code><p>
 *
 * @see OrExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class OrExpressionStateObject extends LogicalExpressionStateObject {

	/**
	 * Creates a new <code>OrExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>OrExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftStateObject The {@link StateObject} representing the left expression
	 * @param rightStateObject The {@link StateObject} representing the right expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrExpressionStateObject(StateObject parent,
	                               StateObject leftStateObject,
	                               StateObject rightStateObject) {

		super(parent, leftStateObject, rightStateObject);
	}

	/**
	 * Creates a new <code>OrExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftJpqlFragment The string representation of the left expression to parse and to
	 * convert into a {@link StateObject}
	 * @param rightJpqlFragment The string representation of the right expression to parse and to
	 * convert into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrExpressionStateObject(StateObject parent,
	                               String leftJpqlFragment,
	                               String rightJpqlFragment) {

		super(parent, leftJpqlFragment, rightJpqlFragment);
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
	public OrExpression getExpression() {
		return (OrExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return OR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getLeftQueryBNFId() {
		return ConditionalExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRightQueryBNFId() {
		return ConditionalTermBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link OrExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link OrExpression parsed object} representing a logical
	 * <code><b>OR</b></code> expression
	 */
	public void setExpression(OrExpression expression) {
		super.setExpression(expression);
	}
}