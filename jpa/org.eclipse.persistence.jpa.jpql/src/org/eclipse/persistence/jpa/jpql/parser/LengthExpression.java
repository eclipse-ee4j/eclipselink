/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
 * The <b>LENGTH</b> function returns the length of the string in characters as an integer.
 * <p>
 * JPA 1.0, 2.0:
 * <div nowrap><b>BNF:</b> <code>expression ::= LENGTH(string_primary)</code>
 * <p>
 * JPA 2.1:
 * <div nowrap><b>BNF:</b> <code>expression ::= LENGTH(string_expression)</code>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class LengthExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>LengthExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public LengthExpression(AbstractExpression parent) {
		super(parent, LENGTH);
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
	public String getEncapsulatedExpressionQueryBNFId() {
		return InternalLengthExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(FunctionsReturningNumericsBNF.ID);
	}
}