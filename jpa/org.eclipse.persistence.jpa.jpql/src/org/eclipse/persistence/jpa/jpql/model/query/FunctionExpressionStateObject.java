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
package org.eclipse.persistence.jpa.jpql.model.query;

import java.util.List;

import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;

/**
 * This expression adds support to call a native database function.
 * <p>
 * New to EclipseLink 2.4, JPA 2.1.
 * <p>
 *
 * <div nowrap><b>BNF:</b> <code>function_expression ::= FUNCTION('function_name' {, func_item}*)</code><p>
 *
 * @see FunctionExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
@SuppressWarnings("nls")
public class FunctionExpressionStateObject extends FuncExpressionStateObject {

	/**
	 * Creates a new <code>FunctionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public FunctionExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>FunctionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param functionName The name of the native database function
	 * @param arguments The list of {@link StateObject} representing the arguments to pass to the
	 * native database function
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public FunctionExpressionStateObject(StateObject parent,
	                                 String functionName,
	                                 List<? extends StateObject> arguments) {
		super(parent, functionName, arguments);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FunctionExpression getExpression() {
		return (FunctionExpression) super.getExpression();
	}
}