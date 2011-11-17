/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
 * <div nowrap><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression * arithmetic_term</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class MultiplicationExpression extends ArithmeticExpression {

	/**
	 * Creates a new <code>MultiplicationExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public MultiplicationExpression(AbstractExpression parent) {
		super(parent, MULTIPLICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}