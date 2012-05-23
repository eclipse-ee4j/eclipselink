/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * One of the four binary operators. A subtraction is the inverse of addition, it is a mathematical
 * operation representing the removal of the second operand from the first operand.
 * <p>
 * <div nowrap><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression - arithmetic_term</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class SubtractionExpression extends ArithmeticExpression {

	/**
	 * Creates a new <code>SubtractionExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public SubtractionExpression(AbstractExpression parent) {
		super(parent, MINUS);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}