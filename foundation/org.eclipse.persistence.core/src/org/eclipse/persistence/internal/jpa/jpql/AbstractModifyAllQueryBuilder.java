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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.queries.ModifyAllQuery;

/**
 * This builder is responsible to populate a {@link ModifyAllQuery}.
 *
 * @see DeleteQueryVisitor
 * @see UpdateQueryVisitor
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
abstract class AbstractModifyAllQueryBuilder extends AbstractEclipseLinkExpressionVisitor {

	/**
	 * The {@link ModifyAllQuery} to update.
	 */
	final ModifyAllQuery query;

	/**
	 * The {@link ModifyAllQuery} to populate by using this visitor to visit the parsed tree
	 * representation of the JPQL query.
	 */
	final JPQLQueryContext queryContext;

	/**
	 * Creates a new <code>AbstractModifyAllQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param query The {@link ModifyAllQuery} to populate by using this visitor to visit the parsed
	 * tree representation of the JPQL query
	 */
	AbstractModifyAllQueryBuilder(JPQLQueryContext queryContext, ModifyAllQuery query) {
		super();
		this.query = query;
		this.queryContext = queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		// Set the ExpressionBuilder
		ExpressionBuilder expressionBuilder = queryContext.getBaseExpression().getBuilder();
		query.setExpressionBuilder(expressionBuilder);

		// Set the reference class if it's not set
		if (query.getReferenceClass() == null) {
			query.setReferenceClass(expressionBuilder.getQueryClass());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		query.setSelectionCriteria(queryContext.buildExpression(expression));
	}
}