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

import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor visits the select expression in order to create the correct {@link ReadAllQuery},
 * which is either a {@link ReportQuery} or a {@link ReadAllQuery}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ReadAllQueryBuilder extends EclipseLinkAnonymousExpressionVisitor {

	/**
	 * The query that was created based on the type of select clause.
	 */
	ReadAllQuery query;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * The {@link Expression} being visited.
	 */
	private SelectStatement selectStatement;

	/**
	 * Creates a new <code>ReadAllQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ReadAllQueryBuilder(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	private void initializeReadAllQuery() {
		ReadAllQuery query = new ReadAllQuery();
		query.dontUseDistinct();
		this.query = query;
	}

	private void initializeReportQuery() {
		ReportQuery query = new ReportQuery();
		query.returnWithoutReportQueryResult();
		query.dontUseDistinct();
		this.query = query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		// Multiple expressions in the select clause => ReportQuery
		initializeReportQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void visit(Expression expression) {
      // Does not select an identification variable
		// (e.g. projection or aggregate function) => ReportQuery
		initializeReportQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		// Use ReadAllQuery if the variable of the SELECT clause expression is the base variable
		// Example: ReadAllQuery = SELECT e FROM Employee e
		// Example: ReportQuery  = SELECT e FROM Department d JOIN d.employees e
		String variableName = expression.getVariableName();

		if (queryContext.isRangeIdentificationVariable(variableName)) {

			if (selectStatement.hasGroupByClause() ||
			    selectStatement.hasHavingClause()  ||
			    variableName != queryContext.getFirstDeclaration().getVariableName()) {

				initializeReportQuery();
			}
			else {
				initializeReadAllQuery();
			}
		}
		else {
			initializeReportQuery();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression) {
		// For from clause only JPQL the full object is always selected, so is ReadAllQuery.
		initializeReadAllQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression) {
		// Visit the identification variable directly
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {
		// Make sure to traverse the select expression since
		// it has a result variable assigned to it
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		this.selectStatement = expression;
		expression.getSelectClause().accept(this);
	}
}