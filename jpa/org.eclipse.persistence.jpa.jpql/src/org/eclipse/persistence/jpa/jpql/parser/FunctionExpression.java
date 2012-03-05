/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * This expression adds support to call native database functions.
 * <p>
 * New to EclipseLink 2.4, JPA 2.1.
 *
 * <div nowrap><b>BNF:</b> <code>func_expression ::= FUNCTION('function_name' {, func_item}*)</code><p>
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class FunctionExpression extends FuncExpression {

	/**
	 * Creates a new <code>FuncExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public FunctionExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return FunctionItemBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(FunctionExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return FUNCTION;
	}
}