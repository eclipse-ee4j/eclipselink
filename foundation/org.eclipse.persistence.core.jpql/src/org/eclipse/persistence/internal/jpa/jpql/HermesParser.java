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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.jpa.jpql.spi.JavaManagedTypeProvider;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.EclipseLinkJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemResourceBundle;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * The default implementation used to parse a JPQL query into a {@link DatabaseQuery}. It uses
 * {@link JPQLExpression} to parse the JPQL query.
 *
 * @see EclipseLinkJPQLQueryHelper
 * @see JPQLExpression
 *
 * @version 2.4
 * @since 2.3
 * @author John Bracken
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class HermesParser implements JPAQueryBuilder {

	/**
	 * Indicates whether this query builder should have validation mode on for JPQL queries.
	 */
	private boolean validateQueries;

	/**
	 * Creates a new <code>HermesParser</code>.
	 */
	public HermesParser() {
		this(true);
	}

	/**
	 * Creates a new {@link HermesParser}.
	 *
	 * @param validateQueries Determines whether JPQL queries should be validated before creating the
	 * {@link Expression Expression}
	 */
	public HermesParser(boolean validateQueries) {
		super();
		this.validateQueries = validateQueries;
	}

	/**
	 * Registers the input parameters derived from the JPQL expression with the {@link DatabaseQuery}.
	 *
	 * @param queryContext The {@link JPQLQueryContext} containing the information about the JPQL query
	 * @param databaseQuery The EclipseLink {@link DatabaseQuery} where the input parameter types are added
	 */
	private void addArguments(JPQLQueryContext queryContext, DatabaseQuery databaseQuery) {
		for (Map.Entry<String, Class<?>> inputParameters : queryContext.inputParameters()) {
			databaseQuery.addArgument(inputParameters.getKey().substring(1), inputParameters.getValue());
		}
	}

	private IManagedTypeProvider buildProvider(AbstractSession session) {
		return new JavaManagedTypeProvider(session);
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseQuery buildQuery(String jpqlQuery, AbstractSession session) {
		return populateQueryImp(jpqlQuery, null, session);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression buildSelectionCriteria(String entityName,
	                                         String selectionCriteria,
	                                         AbstractSession session) {

		// Create the parsed tree representation of the selection criteria
		JPQLExpression jpqlExpression = new JPQLExpression(
			selectionCriteria,
			jpqlGrammar(),
			ConditionalExpressionBNF.ID,
			validateQueries
		);

		// Caches the info and add a virtual range variable declaration
		JPQLQueryContext queryContext = new JPQLQueryContext();
		queryContext.cache(session, null, jpqlExpression, selectionCriteria);
		queryContext.addRangeVariableDeclaration(entityName, "this");

		// Validate the query
		// Note: Currently turned off, I need to tweak validation to support JPQL fragment
//		validate(jpqlExpression, session, selectionCriteria);

		// Create the Expression representing the selection criteria
		return queryContext.buildExpression(jpqlExpression.getQueryStatement());
	}

	private JPQLGrammar jpqlGrammar() {
		return DefaultEclipseLinkJPQLGrammar.instance();
	}

	/**
	 * Creates and throws a {@link JPQLException} indicating the problems with the JPQL query.
	 *
	 * @param queryContext The {@link JPQLQueryContext} containing the information about the JPQL query
	 * @param problems The {@link JPQLQueryProblem problems} found in the JPQL query that are
	 * translated into an exception
	 * @param messageKey The key used to retrieve the localized message
	 */
	private void logProblems(JPQLQueryContext queryContext,
	                         List<JPQLQueryProblem> problems,
	                         String messageKey) {

		ResourceBundle bundle = ResourceBundle.getBundle(JPQLQueryProblemResourceBundle.class.getName());
		StringBuilder sb = new StringBuilder();

		for (int index = 0, count = problems.size(); index < count; index++)  {

			JPQLQueryProblem problem = problems.get(index);

			// Create the localized message
			String message = bundle.getString(problem.getMessageKey());
			message = MessageFormat.format(message, (Object[]) problem.getMessageArguments());

			// Append the description
			sb.append("\n");
			sb.append("[");
			sb.append(problem.getStartPosition());
			sb.append(", ");
			sb.append(problem.getEndPosition());
			sb.append("] ");
			sb.append(message);
		}

		String errorMessage = bundle.getString(messageKey);
		errorMessage = MessageFormat.format(errorMessage, queryContext.getJPQLQuery(), sb);

		throw new JPQLException(errorMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	public void populateQuery(String jpqlQuery, DatabaseQuery query, AbstractSession session) {
		populateQueryImp(jpqlQuery, query, session);
	}

	private DatabaseQuery populateQueryImp(String jpqlQuery,
	                                       DatabaseQuery query,
	                                       AbstractSession session) {

		// Parse the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, jpqlGrammar(), validateQueries);

		// Create the context
		JPQLQueryContext queryContext = new JPQLQueryContext();
		queryContext.cache(session, query, jpqlExpression, jpqlQuery);

		// Validate the query
		validate(queryContext, jpqlExpression, session, jpqlQuery);

		// Create the DatabaseQuery
		DatabaseQueryVisitor visitor = new DatabaseQueryVisitor(queryContext);
		jpqlExpression.accept(visitor);

		// Add the input parameter types to the DatabaseQuery
		if (query == null) {
			query = queryContext.getDatabaseQuery();
			addArguments(queryContext, query);
		}

		return query;
	}

	/**
	 * Grammatically and semantically validates the JPQL query. If the query is not valid, then an
	 * exception will be thrown.
	 */
	private void validate(JPQLQueryContext queryContext,
	                      JPQLExpression expression,
	                      AbstractSession session,
	                      String jpqlQuery) {

		if (validateQueries) {

			List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();

			// Create the helper
			EclipseLinkJPQLQueryHelper queryHelper = new EclipseLinkJPQLQueryHelper(jpqlGrammar());
			queryHelper.setJPQLExpression(expression);
			queryHelper.setQuery(new JavaQuery(buildProvider(session), jpqlQuery));

			// Validate the query using the grammar
			queryHelper.validateGrammar(expression, problems);

			if (!problems.isEmpty()) {
				logProblems(queryContext, problems, JPQLQueryProblemMessages.HermesParser_GrammarValidator_ErrorMessage);
				problems.clear();
			}

			// Now validate the semantic of the query
			queryHelper.validateSemantic(expression, problems);

			if (!problems.isEmpty()) {
				logProblems(queryContext, problems, JPQLQueryProblemMessages.HermesParser_SemanticValidator_ErrorMessage);
			}
		}
	}

	/**
	 * This visitor traverses the parsed tree and create the right EclipseLink query and populates it.
	 */
	private class DatabaseQueryVisitor extends AbstractExpressionVisitor {

		private final JPQLQueryContext queryContext;

		DatabaseQueryVisitor(JPQLQueryContext queryContext) {
			super();
			this.queryContext = queryContext;
		}

		private ReadAllQuery buildReadAllQuery(SelectStatement expression) {
			ReadAllQueryBuilder visitor = new ReadAllQueryBuilder(queryContext);
			expression.accept(visitor);
			return visitor.query;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {

			DeleteAllQuery query = queryContext.getDatabaseQuery();

			// Create and prepare the query
			if (query == null) {
				query = new DeleteAllQuery();
				queryContext.setDatabasQuery(query);
	                        query.setJPQLString(queryContext.getJPQLQuery());
			}
			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			DeleteQueryVisitor visitor = new DeleteQueryVisitor(queryContext);
			visitor.query = query;
			expression.accept(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLExpression expression) {
			expression.getQueryStatement().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression) {

			ObjectLevelReadQuery query = queryContext.getDatabaseQuery();

			// Create and prepare the query
			if (query == null) {
				query = buildReadAllQuery(expression);
				queryContext.setDatabasQuery(query);
	                        query.setJPQLString(queryContext.getJPQLQuery());
			}

			// Now populate it
			if (query.isReportQuery()) {
				queryContext.populateReportQuery(expression, (ReportQuery) query);
			}
			else {
				ObjectLevelReadQueryVisitor visitor = new ObjectLevelReadQueryVisitor(queryContext);
				visitor.query = query;
				expression.accept(visitor);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {

			UpdateAllQuery query = queryContext.getDatabaseQuery();

			// Create and prepare the query
			if (query == null) {
				query = new UpdateAllQuery();
				queryContext.setDatabasQuery(query);
	                        query.setJPQLString(queryContext.getJPQLQuery());
			}
			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			UpdateQueryVisitor visitor = new UpdateQueryVisitor(queryContext);
			visitor.query = query;
			expression.accept(visitor);
		}
	}
}