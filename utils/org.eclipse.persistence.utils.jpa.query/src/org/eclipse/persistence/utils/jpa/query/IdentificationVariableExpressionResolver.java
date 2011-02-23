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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.expressions.Expression;

/**
 * The {@link PathExpressionResolver} is responsible to retrieve the {@link Expression} for an
 * identification variable, which can map an abstract schema name or a collection-valued path.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class IdentificationVariableExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * The context used to query information about the application metadata.
	 */
	private final QueryBuilderContext queryContext;

	/**
	 * The name of the identification variable.
	 */
	private final String variableName;

	/**
	 * Creates a new <code>IdentificationVariableExpressionResolver</code>.
	 *
	 * @param parent The parent resolver responsible for the parent path of the given path
	 * @param variableName The name of the identification variable
	 * @param queryContext The context used to query information about the application metadata
	 */
	IdentificationVariableExpressionResolver(PathExpressionResolver parent,
	                                         String variableName,
	                                         QueryBuilderContext queryContext) {
		super(parent);
		this.variableName = variableName;
		this.queryContext = queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariableExpressionResolver clone() {
		return new IdentificationVariableExpressionResolver(
			getParent().clone(),
			variableName,
			queryContext
		);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {

		Expression expression = queryContext.getExpression(variableName);

		if (expression == null) {
			expression = getExpression(variableName);
			queryContext.addExpression(variableName, expression);
			queryContext.addIdentificationVariable(variableName);
		}

		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getParent().getExpression(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNullAllowed() {
		return getParent().isNullAllowed(variableName);
	}
}