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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import org.eclipse.persistence.config.ParserValidationType;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemResourceBundle;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DatabaseQuery.ParameterType;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * This class compiles a JPQL query into a {@link DatabaseQuery}. If validation is not turned off,
 * then the JPQL query will be validated based on the grammar related to the validation level and
 * will also be validated based on the semantic (context).
 * <p>
 * The validation level determines how to validate the JPQL query. It checks if any specific feature
 * is allowed. For instance, if the JPQL query has functions defined for EclipseLink grammar but
 * the validation level is set for generic JPA, then an exception will be thrown indicating the
 * function cannot be used.
 *
 * @see JPQLExpression
 *
 * @version 2.5
 * @since 2.3
 * @author John Bracken
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class HermesParser implements JPAQueryBuilder {

	/**
	 * Determines how to validate the JPQL query grammatically.
	 */
	private String validationLevel;

	/**
	 * Creates a new <code>HermesParser</code>.
	 */
	public HermesParser() {
		super();
		validationLevel = ParserValidationType.DEFAULT;
	}

	/**
	 * Registers the input parameters derived from the JPQL expression with the {@link DatabaseQuery}.
	 *
	 * @param queryContext The {@link JPQLQueryContext} containing the information about the JPQL query
	 * @param databaseQuery The EclipseLink {@link DatabaseQuery} where the input parameter types are added
	 */
	private void addArguments(JPQLQueryContext queryContext, DatabaseQuery databaseQuery) {

		if (queryContext.inputParameters != null) {

			for (Map.Entry<InputParameter, Expression> entry : queryContext.inputParameters.entrySet()) {
				ParameterExpression parameter = (ParameterExpression) entry.getValue();

				databaseQuery.addArgument(
					parameter.getField().getName(),
					(Class<?>) parameter.getType(),
					entry.getKey().isPositional() ? ParameterType.POSITIONAL : ParameterType.NAMED
				);
			}
		}
	}

	/**
	 * Creates a {@link JPQLException} indicating the problems with the JPQL query.
	 *
	 * @param queryContext The {@link JPQLQueryContext} containing the information about the JPQL query
	 * @param problems The {@link JPQLQueryProblem problems} found in the JPQL query that are
	 * translated into an exception
	 * @param messageKey The key used to retrieve the localized message
	 * @return The {@link JPQLException} indicating the problems with the JPQL query
	 */
	private JPQLException buildException(JPQLQueryContext queryContext,
	                                     Collection<JPQLQueryProblem> problems,
	                                     String messageKey) {

		ResourceBundle bundle = resourceBundle();
		StringBuilder sb = new StringBuilder();

		for (JPQLQueryProblem problem : problems) {

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
		return new JPQLException(errorMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatabaseQuery buildQuery(CharSequence jpqlQuery, AbstractSession session) {
		return populateQueryImp(jpqlQuery, null, session);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Expression buildSelectionCriteria(String entityName,
	                                         String selectionCriteria,
	                                         AbstractSession session) {

		try {
			// Create the parsed tree representation of the selection criteria
			JPQLExpression jpqlExpression = new JPQLExpression(
				selectionCriteria,
				DefaultEclipseLinkJPQLGrammar.instance(),
				ConditionalExpressionBNF.ID,
				isTolerant()
			);

			// Caches the info and add a virtual range variable declaration
			JPQLQueryContext queryContext = new JPQLQueryContext(jpqlGrammar());
			queryContext.cache(session, null, jpqlExpression, selectionCriteria);
			queryContext.addRangeVariableDeclaration(entityName, "this");

			// Validate the JPQL query, which will use the JPQL grammar matching the validation
			// level, for now, only validate the query statement because there could be an unknown
			// ending that is an order by clause
			validate(queryContext, jpqlExpression.getQueryStatement());

			// Create the Expression representing the selection criteria
			return queryContext.buildExpression(jpqlExpression.getQueryStatement());
		}
		catch (JPQLException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw buildUnexpectedException(selectionCriteria, exception);
		}
	}

	private JPQLException buildUnexpectedException(CharSequence jpqlQuery, Exception exception) {
		String errorMessage = resourceBundle().getString(HermesParser_UnexpectedException_ErrorMessage);
		errorMessage = MessageFormat.format(errorMessage, jpqlQuery);
		return new JPQLException(errorMessage, exception);
	}

	/**
	 * Determines whether the JPQL query should be parsed with tolerance turned on or off, i.e. if
	 * validation is turned off, then it's assumed the JPQL query is grammatically valid and complete.
	 * In this case, it will be parsed with tolerance turned off resulting in better performance.
	 *
	 * @return <code>true</code> if the query might be incomplete or invalid; <code>false</code> if
	 * the query is complete and grammatically valid
	 */
	private boolean isTolerant() {
		return validationLevel != ParserValidationType.None;
	}

	/**
	 * Returns the {@link JPQLGrammar} that will help to validate the JPQL query grammatically and
	 * semantically (contextually). It will also checks if any specific feature added to that grammar
	 * is allowed. For instance, if the JPQL query has functions defined for EclipseLink grammar but
	 * the validation level is set for generic JPA, then an exception will be thrown.
	 *
	 * @return The {@link JPQLGrammar} written for a specific JPA version or for the current version
	 * of EclipseLink
	 */
	private JPQLGrammar jpqlGrammar() {

		if (validationLevel == ParserValidationType.EclipseLink) {
			return DefaultEclipseLinkJPQLGrammar.instance();
		}

		if (validationLevel == ParserValidationType.JPA10) {
			return JPQLGrammar1_0.instance();
		}

		if (validationLevel == ParserValidationType.JPA20) {
			return JPQLGrammar2_0.instance();
		}

		if (validationLevel == ParserValidationType.JPA21) {
			return JPQLGrammar2_1.instance();
		}

		return DefaultEclipseLinkJPQLGrammar.instance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateQuery(CharSequence jpqlQuery, DatabaseQuery query, AbstractSession session) {
		populateQueryImp(jpqlQuery, query, session);
	}

	private DatabaseQuery populateQueryImp(CharSequence jpqlQuery,
	                                       DatabaseQuery query,
	                                       AbstractSession session) {

		try {
			// Parse the JPQL query with the most recent JPQL grammar
			JPQLExpression jpqlExpression = new JPQLExpression(
				jpqlQuery,
				DefaultEclipseLinkJPQLGrammar.instance(),
				isTolerant()
			);

			// Create a context that caches the information contained in the JPQL query
			// (especially from the FROM clause)
			JPQLQueryContext queryContext = new JPQLQueryContext(jpqlGrammar());
			queryContext.cache(session, query, jpqlExpression, jpqlQuery);

			// Validate the JPQL query, which will use the JPQL grammar matching the validation level
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
		catch (JPQLException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw buildUnexpectedException(jpqlQuery, exception);
		}
	}

	private ResourceBundle resourceBundle() {
		return ResourceBundle.getBundle(JPQLQueryProblemResourceBundle.class.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValidationLevel(String validationLevel) {
		this.validationLevel = validationLevel;
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

		if (validationLevel != ParserValidationType.None) {

			Collection<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();

			// Validate the JPQL query grammatically (based on the JPQL grammar)
			EclipseLinkGrammarValidator grammar = new EclipseLinkGrammarValidator(jpqlGrammar());
			grammar.setProblems(problems);
			expression.accept(grammar);

			if (!problems.isEmpty()) {
				throw buildException(
					queryContext,
					problems,
					HermesParser_GrammarValidator_ErrorMessage
				);
			}

			// Validate the JPQL query semantically (contextually)
			EclipseLinkSemanticValidator semantic = new EclipseLinkSemanticValidator(queryContext);
			semantic.setProblems(problems);
			expression.accept(semantic);

			if (!problems.isEmpty()) {
				throw buildException(
					queryContext,
					problems,
					HermesParser_SemanticValidator_ErrorMessage
				);
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
			this.jpqlQuery = jpqlQuery.toString();
			this.queryContext = queryContext;
		}

		private ReadAllQuery buildReadAllQuery(SelectStatement expression) {
			ReadAllQueryBuilder visitor = new ReadAllQueryBuilder(queryContext);
			expression.accept(visitor);
			return visitor.query;
		}

		private AbstractObjectLevelReadQueryVisitor buildVisitor(ObjectLevelReadQuery query) {

			if (query.isReportQuery()) {
				return new ReportQueryVisitor(queryContext, (ReportQuery) query);
			}

			if (query.isReadAllQuery()) {
				return new ReadAllQueryVisitor(queryContext, (ReadAllQuery) query);
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
				((JPQLCallQueryMechanism) query.getQueryMechanism()).getJPQLCall().setIsParsed(true);
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
				((JPQLCallQueryMechanism) query.getQueryMechanism()).getJPQLCall().setIsParsed(true);
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
				((JPQLCallQueryMechanism) query.getQueryMechanism()).getJPQLCall().setIsParsed(true);
			}

			query.setSession(queryContext.getSession());
			query.setShouldDeferExecutionInUOW(false);

			// Now populate it
			UpdateQueryVisitor visitor = new UpdateQueryVisitor(queryContext, query);
			expression.accept(visitor);
		}
	}
}