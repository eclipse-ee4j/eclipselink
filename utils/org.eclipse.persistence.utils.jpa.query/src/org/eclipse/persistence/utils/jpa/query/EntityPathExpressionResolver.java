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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

/**
 * This {@link PathExpressionResolver} is the root of a path expression where the identification
 * variable is mapped to an abstract schema name.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class EntityPathExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * The name of the entity.
	 */
	private final String abstractSchemaName;

	/**
	 * The builder of {@link Expression expressions} that touches the same Java type.
	 */
	private ExpressionBuilder expressionBuilder;

	/**
	 * The context used to query information about the application metadata.
	 */
	private final QueryBuilderContext queryContext;

	/**
	 * Creates a new <code>EntityPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver of this one
	 * @param queryContext The context used to query information about the application metadata
	 * @param abstractSchemaName The name of the entity
	 */
	EntityPathExpressionResolver(PathExpressionResolver parent,
	                             QueryBuilderContext queryContext,
	                             String abstractSchemaName) {
		super(parent);
		this.queryContext       = queryContext;
		this.abstractSchemaName = abstractSchemaName;
	}

	private ExpressionBuilder buildExpressionBuilder() {
		ClassDescriptor descriptor = queryContext.getDescriptor(abstractSchemaName);
		return new ExpressionBuilder(descriptor.getJavaClass());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityPathExpressionResolver clone() {
		return new EntityPathExpressionResolver(getParent().clone(), queryContext, abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public ExpressionBuilder getExpression() {
		if (expressionBuilder == null) {
			expressionBuilder = buildExpressionBuilder();
		}
		return expressionBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		throw new IllegalAccessError("EntityPathExpressionResolver.getExpression(" + variableName + ") cannot be invoked.");
	}
}