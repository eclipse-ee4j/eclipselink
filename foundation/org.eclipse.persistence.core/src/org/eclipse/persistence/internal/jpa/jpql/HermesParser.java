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
import org.eclipse.persistence.jpa.internal.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.internal.jpql.JPQLQueryProblemResourceBundle;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.JPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ModifyAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * The default implementation used to parse a JPQL query into a {@link DatabaseQuery}. It uses
 * {@link JPQLExpression} to parse the JPQL query.
 *
 * @see DefaultJPQLQueryHelper
 * @see JPQLExpression
 *
 * @version 2.3
 * @since 2.3
 * @author John Bracken
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class HermesParser /*implements JPQLQueryParser*/ {

	/**
	 * This visitor is responsible to create and populate a {@link DeleteAllQuery}.
	 */
	private DeleteQueryVisitor deleteQueryVisitor;

	/**
	 * The visitor used to populate an existing {@link DatabaseQuery}.
	 */
	private ExistingQueryBuilderVisitor existingQueryBuilderVisitor;

	/**
	 * The visitor used to create a new {@link DatabaseQuery}.
	 */
	private NewQueryBuilderVisitor newQueryBuilderVisitor;

	/**
	 * The contextual information for building a {@link DatabaseQuery}.
	 */
	private DefaultJPQLQueryContext queryContext;

	/**
	 * This helper is used to invoke validation on a JPQL query if it is turned on.
	 */
	private JPQLQueryHelper queryHelper;

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
	 * @param validateQueries Determines whether the JPQL queries should be validated before creating
	 * the {@link Expression Expression}
	 */
	public HermesParser(boolean validateQueries) {
		super();
		initialize(validateQueries);
	}

	/**
	 * Registers the input parameters derived from the jpql expression with the {@link DatabaseQuery}.
	 *
	 * @param databaseQuery The EclipseLink {@link DatabaseQuery} where the input parameter types
	 * are added
	 */
	private void addArguments(DatabaseQuery databaseQuery) {
		for (Map.Entry<String, Class<?>> inputParameters : queryContext.inputParameters()) {
			databaseQuery.addArgument(inputParameters.getKey().substring(1), inputParameters.getValue());
		}
	}

	/**
	 * Registers the input parameters derived from the jpql expression with the {@link DatabaseQuery}.
	 *
	 * @param arguments The map used to register the input parameters and their types
	 */
	private void addArguments(Map<String, Class<?>> arguments) {
		for (Map.Entry<String, Class<?>> inputParameters : queryContext.inputParameters()) {
			arguments.put(inputParameters.getKey().substring(1), inputParameters.getValue());
		}
	}

	/**
	 * Parses the given string representation of the Java Persistence query into a parsed tree using
	 * the optimized parsing algorithm (non-tolerant).
	 *
	 * @param query The string representation of the Java Persistence query to parse
	 * @return The parsed tree representation of the query
	 */
	private JPQLExpression buildExpression(String query) {
		return new JPQLExpression(query, IJPAVersion.DEFAULT_VERSION, validateQueries);
	}

	/**
	 * Creates the root of the {@link DatabaseQuery} hierarchy by visiting the given {@link
	 * org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression} in order to determine
	 * what type of query to create.
	 *
	 * @return Either {@link UpdateAllQuery}, {@link DeleteAllQuery}, {@link
	 * org.eclipse.persistence.queries.ReportQuery ReportQuery} or {@link
	 * org.eclipse.persistence.queries.ReadAllQuery ReadAllQuery}
	 */
	private DatabaseQuery buildQuery(JPQLExpression jpqlExpression) {
		NewQueryBuilderVisitor visitor = newQueryVisitor();
		try {
			jpqlExpression.accept(visitor);
			queryContext.setQuery(visitor.query);
			return visitor.query;
		}
		finally {
			visitor.query = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseQuery buildQuery(String jpqlQuery, AbstractSession session) {

		// Create the parsed tree representation of the query
		JPQLExpression jpqlExpression = buildExpression(jpqlQuery);
		queryContext.setJPQLExpression(jpqlExpression);

		// Now set the Session and JPQL query so the SPI is implemented
		queryContext.setQuery(session, jpqlQuery);

		// Validate the query
		validate(jpqlExpression);

		// Create the query
		DatabaseQuery query = buildQuery(jpqlExpression);
		query.setEJBQLString(jpqlQuery);

		try {
			// Now populate it
			jpqlExpression.accept(existingQueryVisitor());

			// Store the input parameter types
			addArguments(query);
		}
		finally {
			dispose();
		}

		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression buildSelectionCriteria(String abstractSchemaName,
	                                         String selectionCriteria,
	                                         AbstractSession session,
	                                         Map<String, Class<?>> arguments) {

		try {
			// Create the parsed tree representation of the selection criteria
			org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression = JPQLExpression.parseConditionalExpression(
				abstractSchemaName,
				selectionCriteria,
				IJPAVersion.DEFAULT_VERSION,
				validateQueries
			);
			queryContext.setJPQLExpression(expression.getRoot());

			// Now set the Session and JPQL query so the SPI is implemented
			queryContext.setQuery(session, selectionCriteria);

			// Validate the query
			validate(expression);

			// Create the visitor that converts the parsed expression into an Expression
			Expression queryExpression = queryContext.buildQueryExpression(expression);

			// Add the additional arguments and their types
			addArguments(arguments);

			return queryExpression;
		}
		finally {
			dispose();
		}
	}

	private DeleteQueryVisitor deleteQueryVisitor() {
		if (deleteQueryVisitor == null) {
			deleteQueryVisitor = new DeleteQueryVisitor(queryContext);
		}
		return deleteQueryVisitor;
	}

	private void dispose() {
		queryContext.dispose();
	}

	/**
	 * Creates a new {@link QueryBuilderVisitor} that visits an existing {@link DatabaseQuery}.
	 *
	 * @return The visitor used to populate the given query by traversing the JPQL parsed tree
	 */
	private ExistingQueryBuilderVisitor existingQueryVisitor() {
		if (existingQueryBuilderVisitor == null) {
			existingQueryBuilderVisitor = new ExistingQueryBuilderVisitor();
		}
		return existingQueryBuilderVisitor;
	}

	private void initialize(boolean validateQueries) {
		this.validateQueries = validateQueries;
		this.queryContext    = new DefaultJPQLQueryContext();
		this.queryHelper     = new JPQLQueryHelper(queryContext);
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
	 * Creates a new {@link QueryBuilderVisitor} that creates a new {@link DatabaseQuery}.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 * @return The visitor used to create a {@link DatabaseQuery} by traversing the JPQL parsed tree
	 */
	private NewQueryBuilderVisitor newQueryVisitor() {
		if (newQueryBuilderVisitor == null) {
			newQueryBuilderVisitor = new NewQueryBuilderVisitor();
		}
		return newQueryBuilderVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void populateQuery(String jpqlQuery, DatabaseQuery query, AbstractSession session) {

		try {
			// Parse the JPQL query
			JPQLExpression jpqlExpression = buildExpression(jpqlQuery);
			queryContext.setJPQLExpression(jpqlExpression);

			// Cache the DatabaseQuery
			queryContext.setQuery(query);

			// Now set the Session and JPQL query so the SPI is implemented
			queryContext.setQuery(session, jpqlQuery);

			// Validate the query
			validate(jpqlExpression);

			// Populate the DatabaseQuery with the information from the string representation
			jpqlExpression.accept(existingQueryVisitor());
		}
		finally {
			dispose();
		}
	}

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
	private void validate(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {

		if (validateQueries) {

			List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();

			// First validate the query using the grammar
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
	private class ExistingQueryBuilderVisitor extends AbstractExpressionVisitor {

		private void updateQuery(ModifyAllQuery query) {
			query.setJPQLString(queryContext.getJPQLQuery());
			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);
		}

		private void updateQuery(ObjectLevelReadQuery query) {
			query.setJPQLString(queryContext.getJPQLQuery());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {
			updateQuery(queryContext.<DeleteAllQuery>getDatabaseQuery());
			expression.accept(deleteQueryVisitor());
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
			updateQuery(queryContext.<ObjectLevelReadQuery>getDatabaseQuery());
			expression.accept(queryContext.readQueryVisitor());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {
			updateQuery(queryContext.<UpdateAllQuery>getDatabaseQuery());
			expression.accept(updateQueryVisitor());
		}
	}

	/**
	 * Visitor that handles traversing the expression of a new query that builds the query in the
	 * process.
	 */
	private class NewQueryBuilderVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link DatabaseQuery} that got created by traversing the root clause.
		 */
		DatabaseQuery query;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {
			query = new DeleteAllQuery();
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
			ReadQueryBuilder visitor = queryContext.readQueryBuilder();
			try {
				expression.accept(visitor);
				query = visitor.query;
			}
			finally {
				visitor.query = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {
			query = new UpdateAllQuery();
		}
	}
}