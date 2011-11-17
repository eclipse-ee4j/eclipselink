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
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.QueryPosition;
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
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractJPQLQueryHelper {

	/**
	 * This visitor is responsible to gather the possible proposals based on the position of the
	 * caret within the JPQL query.
	 */
	private AbstractContentAssistVisitor contentAssistVisitor;

	/**
	 * This visitor is responsible to visit the entire parsed tree representation of the JPQL query
	 * and to validate its content based on the JPQL grammar.
	 */
	private AbstractGrammarValidator grammarValidator;

	/**
	 * The context used to query information about the JPQL query.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * This visitor is responsible to visit the entire parsed tree representation of the JPQL query
	 * and to validate the semantic of the information.
	 */
	private AbstractSemanticValidator semanticValidator;

	/**
	 * Creates a new <code>AbstractJPQLQueryHelper</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} that will determine how to parse JPQL queries
	 */
	public AbstractJPQLQueryHelper(JPQLGrammar jpqlGrammar) {
		super();
		queryContext = buildJPQLQueryContext(jpqlGrammar);
	}

	/**
	 * Creates a new <code>AbstractJPQLQueryHelper</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @exception NullPointerException The JPQLQueryContext cannot be <code>null</code>
	 */
	protected AbstractJPQLQueryHelper(JPQLQueryContext queryContext) {
		super();
		Assert.isNotNull(queryContext, "The JPQLQueryContext cannot be null");
		this.queryContext = queryContext;
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
	public ContentAssistProposals buildContentAssistProposals(int position) {

		// Create a map of the positions within the parsed tree
		QueryPosition queryPosition = getJPQLExpression().buildPosition(
			queryContext.getQuery().getExpression(),
			position
		);

		// Visit the expression, which will collect the possible proposals
		AbstractContentAssistVisitor visitor = getContentAssistVisitor();

		try {
			visitor.prepare(queryPosition);
			queryPosition.getExpression().accept(visitor);
			return visitor.getProposals();
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Creates the concrete instance of the content assist visitor that will give the possible
	 * choices based on the position of the cursor within the JPQL query.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @return A new concrete instance of {@link AbstractContentAssistVisitor}
	 */
	protected abstract AbstractContentAssistVisitor buildContentAssistVisitor(JPQLQueryContext queryContext);

	/**
	 * Creates the concrete instance of the validator that will grammatically validate the JPQL query.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @return A new concrete instance of {@link AbstractGrammarValidator}
	 */
	protected abstract AbstractGrammarValidator buildGrammarValidator(JPQLQueryContext queryContext);

	/**
	 * Creates
	 *
	 * @param jpqlGrammar
	 * @return
	 */
	protected abstract JPQLQueryContext buildJPQLQueryContext(JPQLGrammar jpqlGrammar);

	protected Comparator<IType> buildNumericTypeComparator() {
		return new NumericTypeComparator(getTypeHelper());
	}

	/**
	 * Creates the concrete instance of the validator that will semantically validate the JPQL query.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @return A new concrete instance of {@link AbstractSemanticValidator}
	 */
	protected abstract AbstractSemanticValidator buildSemanticValidator(JPQLQueryContext queryContext);

	/**
	 * Disposes of the internal data.
	 */
	public void dispose() {
		queryContext.dispose();
	}

	protected AbstractContentAssistVisitor getContentAssistVisitor() {
		if (contentAssistVisitor == null) {
			contentAssistVisitor = buildContentAssistVisitor(queryContext);
		}
		return contentAssistVisitor;
	}

	/**
	 * Returns the JPQL grammar that will be used to define how to parse a JPQL query.
	 *
	 * @return The grammar that was used to parse this {@link Expression}
	 * @since 2.4
	 */
	public JPQLGrammar getGrammar() {
		return queryContext.getGrammar();
	}

	protected AbstractGrammarValidator getGrammarValidator() {
		if (grammarValidator == null) {
			grammarValidator = buildGrammarValidator(queryContext);
		}
		return grammarValidator;
	}

	/**
	 * Returns the root of the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed JPQL query
	 */
	public JPQLExpression getJPQLExpression() {
		return queryContext.getJPQLExpression();
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
	public IType getParameterType(String parameterName) {

		// Retrieve the input parameter's qualifier (':' or '?')
		char character = parameterName.length() > 0 ? parameterName.charAt(0) : '\0';

		// Does not begin with either ':' or '?'
		if ((character != ':') && (character != '?')) {
			return getTypeHelper().objectType();
		}

		// Find the InputParameters with the given parameter name
		Collection<InputParameter> inputParameters = queryContext.findInputParameters(parameterName);

		// No InputParameter was found
		if (inputParameters.isEmpty()) {
			return getTypeHelper().objectType();
		}

		// Now find the closest type for each location
		TreeSet<IType> types = new TreeSet<IType>(buildNumericTypeComparator());

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
	 * Returns the string representation of the parsed tree.
	 *
	 * @return The string created from the parsed tree representation of the original JPQL query
	 */
	public String getParsedJPQLQuery() {
		return getJPQLExpression().toParsedText();
	}

	/**
	 * Returns the provider for managed types (entities, embeddables, mapped superclasses).
	 *
	 * @return The container of managed types
	 */
	public IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * Returns the external form representing a named query.
	 *
	 * @return The external form representing a named query
	 */
	public IQuery getQuery() {
		return queryContext.getQuery();
	}

	/**
	 * Returns the {@link JPQLQueryContext} that contains information about the JPQL query.
	 *
	 * @return The {@link JPQLQueryContext} that contains information contained in the JPQL query
	 */
	public JPQLQueryContext getQueryContext() {
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
	public IType getResultType() {

		IType type = queryContext.getType(getJPQLExpression());

		if (!type.isResolvable()) {
			type = getTypeHelper().objectType();
		}

		return type;
	}

	protected AbstractSemanticValidator getSemanticValidator() {
		if (semanticValidator == null) {
			semanticValidator = buildSemanticValidator(queryContext);
		}
		return semanticValidator;
	}

	/**
	 * Returns the {@link IType} representing the given Java type.
	 *
	 * @param type The Java type for which its external form is requested
	 * @return The external form for the given Java type
	 */
	public IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	public TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the repository that gives access to the application's types.
	 *
	 * @return The repository for classes, interfaces, enum types and annotations
	 */
	public ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	/**
	 * Sets
	 *
	 * @param jpqlExpression
	 */
	public void setJPQLExpression(JPQLExpression jpqlExpression) {
		queryContext.setJPQLExpression(jpqlExpression);
	}

	/**
	 * Sets the external form of the JPQL query, which will be parsed and information will be
	 * extracted for later access.
	 *
	 * @param query The external form of the JPQL query
	 */
	public void setQuery(IQuery query) {
		queryContext.setQuery(query);
	}

	/**
	 * Validates the query by introspecting it grammatically and semantically.
	 *
	 * @return The non-<code>null</code> list that will be used to store the {@link JPQLQueryProblem
	 * problems} if any was found
	 */
	public List<JPQLQueryProblem> validate() {
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
	public void validate(Expression expression, List<JPQLQueryProblem> problems) {
		validateGrammar(expression, problems);
		validateSemantic(expression, problems);
	}

	/**
	 * Validates the query by only introspecting it grammatically.
	 *
	 * @return The non-<code>null</code> list that will be used to store the {@link JPQLQueryProblem
	 * problems} if any was found
	 */
	public List<JPQLQueryProblem> validateGrammar() {
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
	public void validateGrammar(Expression expression, List<JPQLQueryProblem> problems) {
		AbstractGrammarValidator visitor = getGrammarValidator();
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
	public List<JPQLQueryProblem> validateSemantic() {
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
	public void validateSemantic(Expression expression, List<JPQLQueryProblem> problems) {
		AbstractSemanticValidator visitor = getSemanticValidator();
		try {
			visitor.setProblems(problems);
			expression.accept(visitor);
		}
		finally {
			visitor.dispose();
		}
	}
}