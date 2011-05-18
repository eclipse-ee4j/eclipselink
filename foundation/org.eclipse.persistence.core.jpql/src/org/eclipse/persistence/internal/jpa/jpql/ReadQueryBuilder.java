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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor visits the select expression in order to create the correct query, which is either
 * a {@link ReportQuery} or a {@link ReadAllQuery}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ReadQueryBuilder extends AnonymousExpressionVisitor {

	/**
	 * The read query that was created based on the type of select clause.
	 */
	ReadAllQuery query;

	/**
	 * The context used to query information about the application metadata.
	 */
	private final DefaultJPQLQueryContext queryContext;

	/**
	 * Creates a new <code>ReadQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	ReadQueryBuilder(DefaultJPQLQueryContext queryContext) {
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
	public void visit(IdentificationVariable expression) {

		// Use ReadAllQuery if the variable of the SELECT clause expression is the base variable
		// Example: ReadAllQuery = SELECT e FROM Employee e
		// Example: ReportQuery  = SELECT e FROM Department d JOIN d.employees e
		if (queryContext.isRangeIdentificationVariable(expression.getText())) {
			initializeReadAllQuery();
		}
		else {
			initializeReportQuery();
		}
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
	protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
      // Does not select an identification variable
		// (e.g. projection or aggregate function) => ReportQuery
		initializeReportQuery();
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
		expression.getSelectClause().accept(this);
	}
}