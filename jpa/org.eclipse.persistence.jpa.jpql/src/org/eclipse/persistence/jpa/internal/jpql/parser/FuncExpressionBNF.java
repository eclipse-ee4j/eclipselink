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
package org.eclipse.persistence.jpa.internal.jpql.parser;

/**
 * The query BNF for the EclipseLink's func expression.
 *
 * <div nowrap><b>BNF:</b> <code>func_expression ::= FUNC('function_name' {, func_item}*)</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class FuncExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier for this {@link FuncExpressionBNF}.
	 */
	static final String ID = FuncExpression.FUNC;

	/**
	 * Creates a new <code>FuncExpressionBNF</code>.
	 */
	FuncExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();
		registerExpressionFactory(FuncExpressionFactory.ID);
	}
}