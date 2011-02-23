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
package org.eclipse.persistence.utils.jpa.query;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

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
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractJPQLQueryHelper<T> {

	/**
	 * The external form wrapping the object representation of the Java Persistence query.
	 */
	private IQuery externalQuery;

	/**
	 * The parsed tree representation of the JPQL query.
	 */
	private JPQLExpression jpqlExpression;

	/**
	 * The external form representing the Java Persistence query.
	 */
	private T query;

	/**
	 * The visitor used to calculate the type of an {@link Expression}.
	 */
	private TypeVisitor typeVisitor;

	/**
	 * Creates a new <code>AbstractJPQLQueryHelper</code>.
	 *
	 * @param query The query object to wrap with the external form
	 * @exception NullPointerException If the given query is <code>null</code>
	 */
	protected AbstractJPQLQueryHelper(T query) {
		super();
		initialize(query);
	}

	/**
	 * Retrieves the possibles choices that can complete the query from the given position within
	 * the query.
	 *
	 * @param position The position within the query for which a list of possible choices are created
	 * for completing the query
	 * @return The list of choices regrouped by categories
	 */
	public ContentAssistItems buildContentAssistItems(int position) {
		ContentAssistProvider provider = new ContentAssistProvider(getQuery(), position);
		return provider.items();
	}

	/**
	 * Creates the external form wrapping the given query object.
	 *
	 * @param query The query to wrap with the external form
	 * @return A new concrete implementation representing the given query object
	 */
	protected abstract IQuery buildQuery(T query);

	/**
	 * Creates a new parsed representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 * @see Expression
	 * @see JPQLExpression
	 */
	public JPQLExpression getJPQLExpression() {
		if (jpqlExpression == null) {
				jpqlExpression = new JPQLExpression(
				getQuery().getExpression(),
				IJPAVersion.DEFAULT_VERSION,
				true
			);
		}
		return jpqlExpression;
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
			return TypeHelper.objectType();
		}

		// Find all the location of the input parameters
		InputParameterVisitor visitor1 = new InputParameterVisitor(parameterName);
		expression.accept(visitor1);
		Set<InputParameter> inputParameters = visitor1.inputParameters;

		// The input parameter is not part of the query
		if (inputParameters.isEmpty()) {
			return TypeHelper.objectType();
		}

		// Now find the closest type for each location
		TreeSet<IType> types = new TreeSet<IType>(NumericTypeComparator.instance());

		for (InputParameter inputParameter : inputParameters) {

			// Determine the closest type
			ParameterTypeVisitor visitor2 = new ParameterTypeVisitor(getQuery(), inputParameter);
			inputParameter.accept(visitor2);

			// A type is ignored if it cannot be determined and it can't affect the calculation
			// if the same input parameter is used elsewhere. Example:
			// SELECT e FROM Employee e WHERE :name IS NOT NULL AND e.name = 'JPQL'
			// The first :name cannot be used to calculate the type
			IType type = visitor2.getType(getTypeVisitor());

			if (type.isResolvable()) {
				types.add(type);
			}
		}

		return types.isEmpty() ? TypeHelper.objectType() : types.first();
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
		return getParameterType(getJPQLExpression(), parameterName);
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
	public IQuery getQuery() {
		if (externalQuery == null) {
			externalQuery = buildQuery(query);
		}
		return externalQuery;
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

		TypeVisitor visitor = getTypeVisitor();
		getJPQLExpression().accept(visitor);
		IType type = visitor.getType();

		if (!type.isResolvable()) {
			type = TypeHelper.objectType();
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
	 * Returns the repository that gives access to the application's types.
	 *
	 * @return The repository for classes, interfaces, enum types and annotations
	 */
	protected ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	/**
	 * Returns the visitor used to calculate the type of an {@link Expression}.
	 *
	 * @return The visitor used to calculate the type of an {@link Expression}
	 */
	protected TypeVisitor getTypeVisitor() {
		if (typeVisitor == null) {
			typeVisitor = new TypeVisitor(getQuery());
		}
		return typeVisitor;
	}

	/**
	 * Initializes this helper and creates the external form of the named query.
	 *
	 * @param query The query to wrap with the external form
	 * @exception NullPointerException If the given query is <code>null</code>
	 */
	protected void initialize(T query) {
		if (query == null) {
			throw new NullPointerException("The query cannot be null");
		}
		this.query = query;
	}

	/**
	 * Sets the visitor used to calculate the type of an {@link Expression}.
	 *
	 * @param typeVisitor The visitor used to calculate the type of an {@link Expression}
	 */
	protected void setTypeVisitor(TypeVisitor typeVisitor) {
		this.typeVisitor = typeVisitor;
	}

	/**
	 * Validates the query by introspecting it grammatically and semantically.
	 *
	 * @return The list of {@link QueryProblem QueryProblems} describing grammatical and semantic
	 * issues found in the query
	 */
	public List<JPQLQueryProblem> validate() {
		return validate(getJPQLExpression());
	}

	/**
	 * Validates the query by introspecting it grammatically and semantically.
	 *
	 * @param jpqlExpression The parsed tree representation of the query
	 * @return The list of {@link QueryProblem QueryProblems} describing grammatical and semantic
	 * issues found in the query
	 */
	public List<JPQLQueryProblem> validate(Expression expression) {
		List<JPQLQueryProblem> problems = validateGrammar(expression);
		problems.addAll(validateSemantic(expression));
		return problems;
	}

	/**
	 * Validates the query by only introspecting it grammatically.
	 *
	 * @return The list of {@link QueryProblem QueryProblems} describing grammatical issues found
	 * in the query
	 */
	public List<JPQLQueryProblem> validateGrammar() {
		return validateGrammar(getJPQLExpression());
	}

	/**
	 * Validates the query by only introspecting it grammatically.
	 *
	 * @param expression The parsed tree representation of the query
	 * @return The list of {@link QueryProblem QueryProblems} describing grammatical issues found
	 * in the query
	 */
	public List<JPQLQueryProblem> validateGrammar(Expression expression) {
		GrammarValidator visitor = new GrammarValidator(getQuery());
		expression.accept(visitor);
		return visitor.problems();
	}

	/**
	 * Validates the query by only introspecting it semantically.
	 *
	 * @return The list of {@link QueryProblem QueryProblems} describing semantic issues found
	 * in the query
	 */
	public List<JPQLQueryProblem> validateSemantic() {
		return validateSemantic(getJPQLExpression());
	}

	/**
	 * Validates the query by only introspecting it semantically.
	 *
	 * @param expression The parsed tree representation of the query
	 * @return The list of {@link QueryProblem QueryProblems} describing semantic issues found
	 * in the query
	 */
	public List<JPQLQueryProblem> validateSemantic(Expression expression) {
		SemanticValidator visitor = new SemanticValidator(getTypeVisitor());
		expression.accept(visitor);
		return visitor.problems();
	}
}