/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidator;
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

		Map<String, Class<?>> inputParameters = queryContext.inputParameters();

		if (inputParameters != null) {

			for (String inputParameter : inputParameters.keySet()) {
				databaseQuery.addArgument(
					inputParameter.substring(1),
					inputParameters.get(inputParameter)
				);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseQuery buildQuery(CharSequence jpqlQuery, AbstractSession session) {
		return populateQueryImp(jpqlQuery, null, session);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression buildSelectionCriteria(String entityName,
	                                         String selectionCriteria,
	                                         AbstractSession session) {

		JPQLGrammar jpqlGrammar = jpqlGrammar();

		// Create the parsed tree representation of the selection criteria
		JPQLExpression jpqlExpression = new JPQLExpression(
			selectionCriteria,
			jpqlGrammar,
			ConditionalExpressionBNF.ID,
			validateQueries
		);

		// Caches the info and add a virtual range variable declaration
		JPQLQueryContext queryContext = new JPQLQueryContext(jpqlGrammar);
		queryContext.cache(session, null, jpqlExpression, selectionCriteria);
		queryContext.addRangeVariableDeclaration(entityName, "this");

		// Validate the query
		validate(queryContext, jpqlExpression.getQueryStatement());

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
	                         Collection<JPQLQueryProblem> problems,
	                         String messageKey) {

		ResourceBundle bundle = ResourceBundle.getBundle(JPQLQueryProblemResourceBundle.class.getName());
		StringBuilder sb = new StringBuilder();

		for (JPQLQueryProblem problem : problems)  {

			// Retrieve the localized message
			String message;

			try {
				message = bundle.getString(problem.getMessageKey());
			}
			catch (NullPointerException e) {
				// In case the resource bundle was not updated
				message = problem.getMessageKey();
			}

			// Now format the localized message
			String[] arguments = problem.getMessageArguments();

			if (arguments.length > 0) {
				message = MessageFormat.format(message, (Object[]) arguments);
			}

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
	public void populateQuery(CharSequence jpqlQuery, DatabaseQuery query, AbstractSession session) {
		populateQueryImp(jpqlQuery, query, session);
	}

	private DatabaseQuery populateQueryImp(CharSequence jpqlQuery,
	                                       DatabaseQuery query,
	                                       AbstractSession session) {

		JPQLGrammar jpqlGrammar = jpqlGrammar();

		// Parse the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, jpqlGrammar(), validateQueries);

		// Create a context that caches the information contained in the JPQL query
		// (especially from the FROM clause)
		JPQLQueryContext queryContext = new JPQLQueryContext(jpqlGrammar);
		queryContext.cache(session, query, jpqlExpression, jpqlQuery);

		// Validate the JPQL query
		validate(queryContext, jpqlExpression);

		// Create the DatabaseQuery by visiting the parsed tree
		DatabaseQueryVisitor visitor = new DatabaseQueryVisitor(queryContext, jpqlQuery);
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
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to
	 * validate grammatically and semantically
	 */
	private void validate(JPQLQueryContext queryContext,
	                      org.eclipse.persistence.jpa.jpql.parser.Expression expression) {

		if (validateQueries) {

			Collection<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();

			// Validate the JPQL query grammatically
			EclipseLinkGrammarValidator grammar = new EclipseLinkGrammarValidator(jpqlGrammar());
			grammar.setProblems(problems);
			expression.accept(grammar);

			if (!problems.isEmpty()) {
				logProblems(queryContext, problems, JPQLQueryProblemMessages.HermesParser_GrammarValidator_ErrorMessage);
				problems = new LinkedList<JPQLQueryProblem>();
			}

			// Validate the JPQL query semantically
			EclipseLinkSemanticValidatorHelper helper = new EclipseLinkSemanticValidatorHelper(queryContext);
			EclipseLinkSemanticValidator semantic = new EclipseLinkSemanticValidator(helper);
			semantic.setProblems(problems);
			expression.accept(semantic);

			if (!problems.isEmpty()) {
				logProblems(queryContext, problems, JPQLQueryProblemMessages.HermesParser_SemanticValidator_ErrorMessage);
			}
		}
	}

	/**
	 * This visitor traverses the parsed tree and create the right EclipseLink query and populates it.
	 */
	private static class DatabaseQueryVisitor extends AbstractExpressionVisitor {

		private final String jpqlQuery;
		private final JPQLQueryContext queryContext;

		DatabaseQueryVisitor(JPQLQueryContext queryContext, CharSequence jpqlQuery) {
			super();
			this.jpqlQuery    = jpqlQuery.toString();
			this.queryContext = queryContext;
		}

		private ReadAllQuery buildReadAllQuery(SelectStatement expression) {
			ReadAllQueryBuilder visitor = new ReadAllQueryBuilder(queryContext);
			expression.accept(visitor);
			return visitor.query;
		}

		private AbstractReadAllQueryVisitor buildVisitor(ObjectLevelReadQuery query) {

			if (query.isReportQuery()) {
				return new ReportQueryVisitor(queryContext, (ReportQuery) query);
			}

			return new ObjectLevelReadQueryVisitor(queryContext, query);
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
				query.setJPQLString(jpqlQuery);
			}

			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			DeleteQueryVisitor visitor = new DeleteQueryVisitor(queryContext, query);
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
				query.setJPQLString(jpqlQuery);
			}

			// Now populate it
			expression.accept(buildVisitor(query));
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
				query.setJPQLString(jpqlQuery);
			}

			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			UpdateQueryVisitor visitor = new UpdateQueryVisitor(queryContext, query);
			expression.accept(visitor);
		}
	}
}