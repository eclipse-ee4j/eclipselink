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
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.DefaultJPQLQueryHelper;
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
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * The default implementation used to parse a JPQL query into a {@link DatabaseQuery}. It uses
 * {@link JPQLExpression} to parse the JPQL query.
 *
 * @see DefaultJPQLQueryHelper
 * @see JPQLExpression
 *
 * @version 2.4
 * @since 2.3
 * @author John Bracken
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class HermesParser /*implements JPAQueryBuilder*/ {

	/**
	 * The visitor used to populate an existing {@link DatabaseQuery}.
	 */
	private DatabaseQueryVisitor databaseQueryVisitor;

	/**
	 * This visitor is responsible to create and populate a {@link DeleteAllQuery}.
	 */
	private DeleteQueryVisitor deleteQueryVisitor;

	/**
	 * The contextual information for building a {@link DatabaseQuery}.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * This helper is used to invoke validation on a JPQL query if it is turned on.
	 */
	private DefaultJPQLQueryHelper queryHelper;

	/**
	 * This visitor is responsible to create the right read query based on the select expression.
	 */
	private ReadAllQueryBuilder readAllQueryBuilder;

	/**
	 * This visitor is responsible to create and populate a {@link ObjectLevelReadQuery}.
	 */
	private ObjectLevelReadQueryVisitor readAllQueryVisitor;

	/**
	 * This visitor is responsible to create and populate a {@link UpdateAllQuery}.
	 */
	private UpdateQueryVisitor updateQueryVisitor;

	/**
	 * Indicates whether this query builder should have validation mode on for JPQL queries.
	 */
	private boolean validateQueries;

	/**
	 * Creates a new {@link HermesParser}.
	 *
	 * @param validateQueries Determines whether JPQL queries should be validated before creating the
	 * {@link Expression Expression}
	 */
	public HermesParser(boolean validateQueries) {
		super();
		this.queryContext    = new JPQLQueryContext();
		this.validateQueries = validateQueries;
	}

	/**
	 * Registers the input parameters derived from the jpql expression with the {@link DatabaseQuery}.
	 *
	 * @param databaseQuery The EclipseLink {@link DatabaseQuery} where the input parameter types are added
	 */
	private void addArguments(DatabaseQuery databaseQuery) {
		for (Map.Entry<String, Class<?>> inputParameters : queryContext.inputParameters()) {
			databaseQuery.addArgument(inputParameters.getKey().substring(1), inputParameters.getValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseQuery buildQuery(String jpqlQuery, AbstractSession session) {
		return populateQueryImp(jpqlQuery, null, session);
	}

	private ReadAllQuery buildReadAllQuery(SelectStatement expression) {
		ReadAllQueryBuilder visitor = readAllQueryBuilder();
		try {
			expression.accept(visitor);
			return visitor.query;
		}
		finally {
			visitor.query = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression buildSelectionCriteria(String entityName,
	                                         String selectionCriteria,
	                                         AbstractSession session) {

		try {
			// Create the parsed tree representation of the selection criteria
			JPQLExpression jpqlExpression = new JPQLExpression(
				selectionCriteria,
				jpqlGrammar(),
				ConditionalExpressionBNF.ID,
				validateQueries
			);

			// Caches the info and add a virtual range variable declaration
			queryContext.cache(session, null, jpqlExpression, selectionCriteria);
			queryContext.addRangeVariableDeclaration(entityName, "this");

			// Validate the query
			validate(jpqlExpression.getQueryStatement());

			// Create the Expression representing the selection criteria
			return queryContext.buildExpression(jpqlExpression.getQueryStatement());
		}
		// Note: Used for debugging purposes
//		catch (Exception e) {
//			queryContext.dispose();
//			e.printStackTrace();
//			return null;
//		}
		finally {
			queryContext.dispose();
		}
	}

	/**
	 * Returns the visitor that will populate the {@link DatabaseQuery} by traversing the parsed tree
	 * representation of the JPQL query.
	 *
	 * @return The visitor used to populate the given query by traversing the JPQL parsed tree
	 */
	private DatabaseQueryVisitor databaseQueryVisitor() {
		if (databaseQueryVisitor == null) {
			databaseQueryVisitor = new DatabaseQueryVisitor();
		}
		return databaseQueryVisitor;
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate a {@link DeleteAllQuery}.
	 *
	 * @return The visitor used for a query of delete query type
	 */
	private DeleteQueryVisitor deleteQueryVisitor() {
		if (deleteQueryVisitor == null) {
			deleteQueryVisitor = new DeleteQueryVisitor(queryContext);
		}
		return deleteQueryVisitor;
	}

	private JPQLGrammar jpqlGrammar() {
		return DefaultEclipseLinkJPQLGrammar.instance();
	}

	/**
	 * Creates and throws a {@link JPQLException} indicating the problems with the JPQL query.
	 *
	 * @param problems the errors with the jpql query that are translated into an exception.
	 */
	private void logProblems(List<JPQLQueryProblem> problems, String messageKey) {

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

		// TODO - needs patch to core before uncommenting
		// throw new JPQLException(errorMessage);
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate an {@link
	 * ObjectLevelReadQuery}.
	 *
	 * @return The visitor used for a query of object level read query type
	 */
	private ObjectLevelReadQueryVisitor objectLevelReadQueryVisitor() {
		if (readAllQueryVisitor == null) {
			readAllQueryVisitor = new ObjectLevelReadQueryVisitor(queryContext);
		}
		return readAllQueryVisitor;
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

		// Create the parsed tree representation of the query
		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, jpqlGrammar(), validateQueries);

		// Now set the Session and JPQL query so the SPI is implemented
		queryContext.cache(session, query, jpqlExpression, jpqlQuery);

		try {
			// Validate the query
			validate(jpqlExpression);

			// Now populate it
			jpqlExpression.accept(databaseQueryVisitor());

			// Store the input parameter types
			if (query == null) {
				query = queryContext.getDatabaseQuery();
				addArguments(query);
			}

			return query;
		}
		// Note: Used for debugging purposes
//		catch (Exception e) {
//			queryContext.dispose();
//			e.printStackTrace();
//			return null;
//		}
		finally {
			queryContext.dispose();
		}
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate a {@link ReadAllQuery}.
	 *
	 * @return The visitor used for a query of read query type
	 */
	private ReadAllQueryBuilder readAllQueryBuilder() {
		if (readAllQueryBuilder == null) {
			readAllQueryBuilder = new ReadAllQueryBuilder(queryContext);
		}
		return readAllQueryBuilder;
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate a {@link UpdateAllQuery}.
	 *
	 * @return The visitor used for a query of update query type
	 */
	private UpdateQueryVisitor updateQueryVisitor() {
		if (updateQueryVisitor == null) {
			updateQueryVisitor = new UpdateQueryVisitor(queryContext);
		}
		return updateQueryVisitor;
	}

	/**
	 * Grammatically and semantically validates the JPQL query. If the query is not valid, then an
	 * exception will be thrown.
	 *
	 * @param expression The JPQL expression to validate
	 */
	private void validate(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {

		if (validateQueries) {

			List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();

			// Validate the query using the grammar
			queryHelper.validateGrammar(expression, problems);

			if (!problems.isEmpty()) {
				logProblems(problems, JPQLQueryProblemMessages.HermesParser_GrammarValidator_ErrorMessage);
				problems.clear();
			}

			// Now validate the semantic of the query
			queryHelper.validateSemantic(expression, problems);

			if (!problems.isEmpty()) {
				logProblems(problems, JPQLQueryProblemMessages.HermesParser_SemanticValidator_ErrorMessage);
			}
		}
	}

	/**
	 * This visitor is responsible to handle traversing the expression of an existing query and to
	 * complete that query.
	 */
	private class DatabaseQueryVisitor extends AbstractExpressionVisitor {

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
			}

			query.setJPQLString(queryContext.getJPQLQuery());
			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			DeleteQueryVisitor visitor = deleteQueryVisitor();
			try {
				visitor.query = query;
				expression.accept(visitor);
			}
			finally {
				visitor.query = null;
			}
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
			}

			query.setJPQLString(queryContext.getJPQLQuery());

			// Now populate it
			if (query.isReportQuery()) {
				queryContext.populateReportQuery(expression, (ReportQuery) query);
			}
			else {
				ObjectLevelReadQueryVisitor visitor = objectLevelReadQueryVisitor();
				try {
					visitor.query = query;
					expression.accept(visitor);
				}
				finally {
					visitor.query = null;
				}
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
			}

			query.setJPQLString(queryContext.getJPQLQuery());
			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			UpdateQueryVisitor visitor = updateQueryVisitor();
			try {
				visitor.query = query;
				expression.accept(visitor);
			}
			finally {
				visitor.query = null;
			}
		}
	}
}