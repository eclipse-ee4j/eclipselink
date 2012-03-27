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

import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;

/**
 * Exact numeric literals support the use of Java integer literal syntax as well as SQL exact
 * numeric literal syntax. Approximate literals support the use of Java floating point literal
 * syntax as well as SQL approximate numeric literal
 * syntax.
 * <p>
 * Appropriate suffixes may be used to indicate the specific type of a numeric literal in accordance
 * with the Java Language Specification.
 *
 * @see NumericLiteral
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class NumericLiteralStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>NumericLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NumericLiteralStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>NumericLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param numeric The actual number
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NumericLiteralStateObject(StateObject parent, Number numeric) {
		super(parent, numeric.toString());
	}

	/**
	 * Creates a new <code>NumericLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param numeric The string representation of the numeric literal
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NumericLiteralStateObject(StateObject parent, String numeric) {
		super(parent, numeric);
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
	public NumericLiteral getExpression() {
		return (NumericLiteral) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link NumericLiteral parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link NumericLiteral parsed object} representing a numeric literal
	 */
	public void setExpression(NumericLiteral expression) {
		super.setExpression(expression);
	}
}