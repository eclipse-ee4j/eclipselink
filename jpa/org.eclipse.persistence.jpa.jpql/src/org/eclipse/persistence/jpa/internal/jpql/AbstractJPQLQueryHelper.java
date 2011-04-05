/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse protected License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse protected License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.internal.jpql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.QueryPosition;
import org.eclipse.persistence.jpa.jpql.ContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * This helper can perform the following operations over a JPQL query:
 * <ul>
 * <li>Calculates the result type of a query: {@link #getResultType()};</li>
 * <li>Calculates the type of an input parameter: {@link #getParameterType(String)}.</li>
 * <li>Calculates the possible choices to complete the query from a given
 *     position (used for content assist): {@link #buildContentAssistItems(int)}.</li>
 * <li>Validates the query by introspecting it grammatically and semantically:
 *     <ul>
 *     <li>{@link #validate()},</li>
 *     <li>{@link #validateGrammar()},</li>
 *     <li>{@link #validateSemantic()}.</li>
 *     </ul></li>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractJPQLQueryHelper {

	/**
	 * This visitor is responsible to gather the possible proposals based on the position of the
	 * caret within the JPQL query.
	 */
	private ContentAssistVisitor contentAssistVisitor;

	/**
	 * This visitor is responsible to visit the entire parsed tree representation of the JPQL query
	 * and to validate its content based on the JPQL grammar.
	 */
	private GrammarValidator grammarValidator;

	/**
	 * The context used to query information about the query.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * This visitor is responsible to visit the entire parsed tree representation of the JPQL query
	 * and to validate the semantic of the information.
	 */
	private SemanticValidator semanticValidator;

	/**
	 * Creates a new <code>AbstractJPQLQueryHelper</code>.
	 */
	protected AbstractJPQLQueryHelper() {
		super();
		queryContext = new JPQLQueryContext();
	}

	/**
	 * Retrieves the possibles choices that can complete the query from the given position within
	 * the query.
	 * <p>
	 * <b>Note:</b> Disposing of the internal data is not done automatically.
	 *
	 * @param position The position within the query for which a list of possible choices are created
	 * for completing the query
	 * @return The list of valid proposals regrouped by categories
	 */
	protected ContentAssistProposals buildContentAssistProposals(int position) {

		// Create a map of the positions within the parsed tree
		QueryPosition queryPosition = getJPQLExpression().buildPosition(
			queryContext.getQuery().getExpression(),
			position
		);

		// Visit the expression, which will collect the possible proposals
		ContentAssistVisitor visitor = contentAssistVisitor();

		try {
			visitor.prepare(queryPosition);
			queryPosition.getExpression().accept(visitor);
			return visitor.getProposals();
		}
		finally {
			visitor.dispose();
		}
	}

	private ContentAssistVisitor contentAssistVisitor() {
		if (contentAssistVisitor == null) {
			contentAssistVisitor = new ContentAssistVisitor(queryContext);
		}
		return contentAssistVisitor;
	}

	/**
	 * Disposes of the internal data.
	 */
	protected void dispose() {
		queryContext.dispose();
	}

	private JPQLExpression getJPQLExpression() {
		return queryContext.getJPQLExpression();
	}

	/**
	 * Retrieves, if it can be determined, the type of the given input parameter with the given name.
	 * The type will be guessed based on its location within expression.
	 * <p>
	 * Note: Both named and positional input parameter can be used.
	 *
	 * @param expression The parsed tree representation of the query
	 * @param parameterName The name of the input parameter to retrieve its type, which needs to be
	 * prepended by ':' or '?'
	 * @return Either the closest type of the input parameter or <code>null</code> if the type
	 * couldn't be determined
	 */
	protected IType getParameterType(JPQLExpression expression, String parameterName) {

		// Retrieve the input parameter's qualifier (':' or '?')
		char character = parameterName.length() > 0 ? parameterName.charAt(0) : '\0';

		// Does not begin with either ':' or '?'
		if ((character != ':') && (character != '?')) {
			return getTypeHelper().objectType();
		}

		// Find all the location of the input parameters
		InputParameterVisitor visitor1 = new InputParameterVisitor(parameterName);
		expression.accept(visitor1);
		Set<InputParameter> inputParameters = visitor1.inputParameters;

		// The input parameter is not part of the query
		if (inputParameters.isEmpty()) {
			return getTypeHelper().objectType();
		}

		// Now find the closest type for each location
		TreeSet<IType> types = new TreeSet<IType>(new NumericTypeComparator(getTypeHelper()));

		for (InputParameter inputParameter : inputParameters) {
			IType type = queryContext.getParameterType(inputParameter);

			// A type is ignored if it cannot be determined and it can't affect the calculation
			// if the same input parameter is used elsewhere. Example:
			// SELECT e FROM Employee e WHERE :name IS NOT NULL AND e.name = 'JPQL'
			// The first :name cannot be used to calculate the type
			if (type.isResolvable()) {
				types.add(type);
			}
		}

		return types.isEmpty() ? getTypeHelper().objectType() : types.first();
	}

	/**
	 * Retrieves, if it can be determined, the type of the given input parameter with the given name.
	 * The type will be guessed based on its location within expression.
	 * <p>
	 * Note: Both named and positional input parameter can be used.
	 *
	 * @param parameterName The name of the input parameter to retrieve its type, which needs to be
	 * prepended by ':' or '?'
	 * @return Either the closest type of the input parameter or <code>null</code> if the type
	 * couldn't be determined
	 */
	protected IType getParameterType(String parameterName) {
		return getParameterType(getJPQLExpression(), parameterName);
	}

	/**
	 * Returns the string representation of the parsed tree.
	 *
	 * @return The string created from the parsed tree representation of the original JPQL query
	 */
	protected String getParsedJPQLQuery() {
		return getJPQLExpression().toParsedText();
	}

	/**
	 * Returns the provider for managed types (entities, embeddables, mapped superclasses).
	 *
	 * @return The container of managed types
	 */
	protected IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * Returns the external form representing a named query.
	 *
	 * @return The external form representing a named query
	 */
	protected IQuery getQuery() {
		return queryContext.getQuery();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	protected JPQLQueryContext getQueryContext() {
		return queryContext;
	}

	/**
	 * Calculates the type of the query result of the JPQL query.
	 * <p>
	 * See {@link TypeVisitor} to understand how the type is calculated.
	 *
	 * @return The result type of the JPQL query if it could accurately be calculated or the
	 * {@link IClass} for <code>Object</code> if it could not be calculated
	 */
	protected IType getResultType() {

		IType type = queryContext.getType(getJPQLExpression());

		if (!type.isResolvable()) {
			type = getTypeHelper().objectType();
		}

		return type;
	}

	/**
	 * Returns the {@link IType} representing the given Java type.
	 *
	 * @param type The Java type for which its external form is requested
	 * @return The external form for the given Java type
	 */
	protected IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	protected TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the repository that gives access to the application's types.
	 *
	 * @return The repository for classes, interfaces, enum types and annotations
	 */
	protected ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	private GrammarValidator grammarValidator() {
		if (grammarValidator == null) {
			grammarValidator = new GrammarValidator(queryContext);
		}
		return grammarValidator;
	}

	private SemanticValidator semanticValidator() {
		if (semanticValidator == null) {
			semanticValidator = new SemanticValidator(queryContext);
		}
		return semanticValidator;
	}

	/**
	 * Sets
	 *
	 * @param jpqlExpression
	 */
	public final void setJPQLExpression(JPQLExpression jpqlExpression) {
		queryContext.setJPQLExpression(jpqlExpression);
	}

	/**
	 * Sets the external form of the JPQL query, which will be parsed and information will be
	 * extracted for later access.
	 *
	 * @param query The external form of the JPQL query
	 */
	protected void setQuery(IQuery query) {
		queryContext.setQuery(query);
	}

	/**
	 * Validates the query by introspecting it grammatically and semantically.
	 *
	 * @return The non-<code>null</code> list that will be used to store the {@link JPQLQueryProblem
	 * problems} if any was found
	 */
	protected List<JPQLQueryProblem> validate() {
		List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();
		validate(getJPQLExpression(), problems);
		return problems;
	}

	/**
	 * Validates the query by introspecting it grammatically and semantically.
	 *
	 * @param jpqlExpression The parsed tree representation of the query
	 * @param problems A non-<code>null</code> list that will be used to store the {@link
	 * JPQLQueryProblem problems} if any was found
	 */
	protected void validate(Expression expression, List<JPQLQueryProblem> problems) {
		validateGrammar(expression, problems);
		validateSemantic(expression, problems);
	}

	/**
	 * Validates the query by only introspecting it grammatically.
	 *
	 * @return The non-<code>null</code> list that will be used to store the {@link JPQLQueryProblem
	 * problems} if any was found
	 */
	protected List<JPQLQueryProblem> validateGrammar() {
		List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();
		validateGrammar(getJPQLExpression(), problems);
		return problems;
	}

	/**
	 * Validates the query by only introspecting it grammatically.
	 *
	 * @param expression The parsed tree representation of the query
	 * @param problems A non-<code>null</code> list that will be used to store the {@link
	 * JPQLQueryProblem problems} if any was found
	 */
	protected void validateGrammar(Expression expression, List<JPQLQueryProblem> problems) {
		GrammarValidator visitor = grammarValidator();
		try {
			visitor.setProblems(problems);
			expression.accept(visitor);
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Validates the query by only introspecting it semantically.
	 *
	 * @return The non-<code>null</code> list that will be used to store the {@link JPQLQueryProblem
	 * problems} if any was found
	 */
	protected List<JPQLQueryProblem> validateSemantic() {
		List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();
		validateSemantic(getJPQLExpression(), problems);
		return problems;
	}

	/**
	 * Validates the query by only introspecting it semantically.
	 *
	 * @param expression The parsed tree representation of the query
	 * @param problems A non-<code>null</code> list that will be used to store the {@link
	 * JPQLQueryProblem problems} if any was found
	 */
	protected void validateSemantic(Expression expression, List<JPQLQueryProblem> problems) {
		SemanticValidator visitor = semanticValidator();
		try {
			visitor.setProblems(problems);
			expression.accept(visitor);
		}
		finally {
			visitor.dispose();
		}
	}
}