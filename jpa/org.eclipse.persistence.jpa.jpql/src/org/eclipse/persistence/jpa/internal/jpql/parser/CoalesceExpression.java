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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * A <b>COALESCE</b> expression returns <code>null</code> if all its arguments evaluate to
 * <code>null</code>, and the value of the first non-<code>null</code> argument otherwise.
 * <p>
 * The return type is the type returned by the arguments if they are all of the same type, otherwise
 * it is undetermined.
 *
 * <div nowrap><b>BNF:</b> <code>coalesce_expression::= COALESCE(scalar_expression {, scalar_expression}+)</code>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class CoalesceExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>CoalesceExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	CoalesceExpression(AbstractExpression parent) {
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
		return InternalCoalesceExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(CoalesceExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return COALESCE;
	}
}