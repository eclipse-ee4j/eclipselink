/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.List;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * This helper can perform the following operations over a JPQL query:
 * <ul>
 * <li>Calculate the result type of a query: {@link #getResultType()};</li>
 * <li>Calculate the type of an input parameter: {@link #getParameterType(String)}.</li>
 * <li>Calculate the possible choices to complete the query from a given
 *     position (used for content assist): {@link #buildContentAssistItems(int)}.</li>
 * <li>Validate the query by introspecting its grammar and semantic:
 *     <ul>
 *     <li>{@link #validate()},</li>
 *     <li>{@link #validateGrammar()},</li>
 *     <li>{@link #validateSemantic()}.</li>
 *     </ul>
 * </ul>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractQueryHelper<T>
{
	/**
	 * The external form representing the Java Persistence query.
	 */
	private IQuery query;

	/**
	 * Creates a new <code>AbstractQueryHelper</code>.
	 *
	 * @param query The query to wrap with the external form
	 * @exception NullPointerException If the given query is <code>null</code>
	 */
	protected AbstractQueryHelper(T query)
	{
		super();
		initialize(query);
	}

	/**
	 * Retrieves the possibles choices that can complete the query from the given
	 * position within the query.
	 *
	 * @param position The position within the query for which a list of possible
	 * choices are created for completing the query
	 * @return The list of choices regrouped by categories
	 */
	public ContentAssistItems buildContentAssistItems(int position)
	{
		ContentAssistProvider provider = buildProvider(position);
		return provider.items();
	}

	private ContentAssistProvider buildProvider(int position)
	{
		return new ContentAssistProvider(getQuery(), position);
	}

	/**
	 * Creates the external form of the provider of managed types.
	 *
	 * @param query The query to wrap with the external form that can be used to
	 * retrieve the actual provider of managed types
	 * @return A new {@link IManagedTypeProvider}
	 */
	protected abstract IManagedTypeProvider buildProvider(T query);

	/**
	 * Creates the external form wrapping the given query object.
	 *
	 * @param query The query to wrap with the external form
	 * @return A new concrete implementation representing the given query object
	 */
	protected abstract IQuery buildQuery(T query);

	/**
	 * Retrieves, if it can be determined, the type of the given input parameter
	 * with the given name. The type will be guessed based on its location within
	 * expression.
	 * <p>
	 * Note: Both named and positional input parameter can be used.
	 *
	 * @param parameterName The name of the input parameter to retrieve its type,
	 * which needs to be prepended by ':' or '?'
	 * @return Either the closest type of the input parameter or <code>null</code>
	 * if the type couldn't be determined
	 */
	public IType getParameterType(String parameterName)
	{
		char character = parameterName.length() > 0 ? parameterName.charAt(0) : '\0';

		// Does not begin with either ':' or '?'
		if ((character != ':') && (character != '?'))
		{
			return getType(Object.class);
		}

		// Find the input parameter
		InputParameterVisitor visitor1 = new InputParameterVisitor(parameterName);
		jpqlExpression().accept(visitor1);
		InputParameter inputParameter = visitor1.inputParameter();

		if (inputParameter == null)
		{
			return getType(Object.class);
		}

		// Now find the closest type
		ParameterTypeVisitor visitor2 = new ParameterTypeVisitor(query, inputParameter);
		inputParameter.accept(visitor2);
		return visitor2.type();
	}

	/**
	 * Returns the provider for managed types (entities, embeddables, mapped
	 * superclasses).
	 *
	 * @return The container of managed types
	 */
	protected IManagedTypeProvider getProvider()
	{
		return query.getProvider();
	}

	/**
	 * Returns the external form representing a named query.
	 *
	 * @return The external form representing a named query
	 */
	protected IQuery getQuery()
	{
		return query;
	}

	/**
	 * Calculates the type of the query result of the JPQL query.
	 * <p>
	 * See {@link SelectQueryTypeVisitor} to understand how the type is calculated.
	 *
	 * @return The result type of the JPQL query if it could accurately be
	 * calculated or the {@link IClass} for <code>Object</code> if it could not
	 * be calculated
	 */
	public IType getResultType()
	{
		SelectQueryTypeVisitor visitor = new SelectQueryTypeVisitor(query);
		jpqlExpression().accept(visitor);
		return visitor.getType();
	}

	/**
	 * Returns the {@link IType} representing the given Java type.
	 *
	 * @param type The Java type for which its external form is requested
	 * @return The external form for the given Java type
	 */
	protected IType getType(Class<?> type)
	{
		return getTypeRepository().getType(Object.class);
	}

	/**
	 * Returns the repository that gives access to the application's types.
	 *
	 * @return The repository for classes, interfaces, etc
	 */
	protected ITypeRepository getTypeRepository()
	{
		return getProvider().getTypeRepository();
	}

	/**
	 * Initializes this helper and creates the external form of the named query.
	 *
	 * @param query The query to wrap with the external form
	 * @exception NullPointerException If the given query is <code>null</code>
	 */
	protected void initialize(T query)
	{
		if (query == null)
		{
			throw new NullPointerException("The query cannot be null");
		}

		this.query = buildQuery(query);
	}

	private JPQLExpression jpqlExpression()
	{
		return new JPQLExpression(query.getExpression(), getProvider().getVersion(), true);
	}

	/**
	 * Populates the given EclipseLink query with the information contained in
	 * the query.
	 *
	 * @param query The EclipseLink query to populate with the
	 */
	public void populateQuery(DatabaseQuery query)
	{
		QueryBuilder queryBuilder = new QueryBuilder(query);
		jpqlExpression().accept(queryBuilder);
	}

	/**
	 * Validates the query by introspecting its grammar and its semantic.
	 *
	 * @return The list of {@link QueryProblem QueryProblems} describing
	 * grammatic and semantic issues found in the query
	 */
	public List<QueryProblem> validate()
	{
		JPQLExpression expression = jpqlExpression();

		List<QueryProblem> problems = validateGrammar(expression);
		problems.addAll(validateSemantic(expression));

		return problems;
	}

	/**
	 * Validates the query by only introspecting its grammar.
	 *
	 * @return The list of {@link QueryProblem QueryProblems} describing
	 * grammatic issues found in the query
	 */
	public List<QueryProblem> validateGrammar()
	{
		return validateGrammar(jpqlExpression());
	}

	/**
	 * Validates the query by only introspecting its grammar.
	 *
	 * @param jpqlExpression The parsed tree represention of the query
	 * @return The list of {@link QueryProblem QueryProblems} describing
	 * grammatic issues found in the query
	 */
	private List<QueryProblem> validateGrammar(JPQLExpression jpqlExpression)
	{
		GrammarValidator visitor = new GrammarValidator(query);
		jpqlExpression.accept(visitor);
		return visitor.problems();
	}

	/**
	 * Validates the query by only introspecting its semantic.
	 *
	 * @return The list of {@link QueryProblem QueryProblems} describing
	 * semantic issues found in the query
	 */
	public List<QueryProblem> validateSemantic()
	{
		return validateSemantic(jpqlExpression());
	}

	/**
	 * Validates the query by only introspecting its semantic.
	 *
	 * @param jpqlExpression The parsed tree represention of the query
	 * @return The list of {@link QueryProblem QueryProblems} describing
	 * semantic issues found in the query
	 */
	private List<QueryProblem> validateSemantic(JPQLExpression jpqlExpression)
	{
		SemanticValidator visitor = new SemanticValidator(query);
		jpqlExpression.accept(visitor);
		return visitor.problems();
	}
}