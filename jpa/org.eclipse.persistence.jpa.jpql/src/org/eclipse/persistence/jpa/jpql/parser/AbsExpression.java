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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>ABS</b> function removes the minus sign from a specified argument and returns the absolute
 * value, which is always a positive number or zero.
 * <p>
 * This is one of the JPQL arithmetic functions. The <b>ABS</b> function takes a numeric argument
 * and returns a number (integer, float, or double) of the same type as the argument to the function.
 * <p>
 * JPA 1.0, 2.0:
 * <div nowrap><b>BNF:</b> <code>expression ::= ABS(simple_arithmetic_expression)</code>
 * <p>
 * JPA 2.1:
 * <div nowrap><b>BNF:</b> <code>expression ::= ABS(arithmetic_expression)</code>
 * <p>
 * Example: <code>SELECT DISTINCT o FROM Order o WHERE o.totalPrice > ABS(:dbl)</code>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class AbsExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>AbsExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public AbsExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return SimpleArithmeticExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(FunctionsReturningNumericsBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return ABS;
	}
}