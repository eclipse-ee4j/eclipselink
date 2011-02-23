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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link RangeVariableDeclaration} creates a new {@link RangeVariableDeclaration}.
 *
 * @see RangeVariableDeclaration
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class RangeVariableDeclarationFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link RangeVariableDeclarationFactory}.
	 */
	static final String ID = "range_variable_declaration";

	/**
	 * Creates a new <code>RangeVariableDeclarationFactory</code>.
	 */
	RangeVariableDeclarationFactory() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant) {
		expression = new RangeVariableDeclaration(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}