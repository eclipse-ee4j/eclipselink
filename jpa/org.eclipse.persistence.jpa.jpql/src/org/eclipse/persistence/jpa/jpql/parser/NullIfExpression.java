/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
 * <b>NULLIF</b> returns the first expression if the two expressions are not equal. If the
 * expressions are equal, <b>NULLIF</b> returns a null value of the type of the first expression.
 * <p>
 * <b>NULLIF</b> is equivalent to a searched <b>CASE</b> expression in which the two expressions
 * are equal and the resulting expression is <b>NULL</b>.
 * <p>
 * Returns the same type as the first expression.
 *
 * <div nowrap><b>BNF:</b> <code>nullif_expression::= NULLIF(scalar_expression, scalar_expression)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class NullIfExpression extends AbstractDoubleEncapsulatedExpression {

	/**
	 * Creates a new <code>NullIfExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public NullIfExpression(AbstractExpression parent) {
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
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(NullIfExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String parameterExpressionBNF(int index) {
		return ScalarExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return NULLIF;
	}
}