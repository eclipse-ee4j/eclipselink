/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeRepository;

/**
 * This helper can perform the following operations over a JPQL query:
 * <ul>
 * <li>Calculates the result type of a query: {@link #getResultType()};</li>
 * <li>Calculates the type of an input parameter: {@link #getParameterType(String)}.</li>
 * <li>Calculates the possible choices to complete the query from a given
 *     position (used for content assist): {@link #buildContentAssistProposals(int)}.</li>
 * <li>Validates the query by introspecting it grammatically and semantically:
 *     <ul>
 *     <li>{@link #validate()},</li>
 *     <li>{@link #validateGrammar()},</li>
 *     <li>{@link #validateSemantic()}.</li>
 *     </ul>
 * </li>
 * <li>Refactoring support:
 *     <ul>
 *     <li>{@link #buildBasicRefactoringTool()} provides support for generating the delta of the
 *     refactoring operation through a collection of {@link TextEdit} objects.</li>
 *     <li>{@link #buildRefactoringTool()} provides support for refactoring the JPQL query through
 *     the editable {@link org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject}
 *     and once all refactoring operations have been executed, the {@link
 *     org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter IJPQLQueryFormatter} will
 *     generate a new string representation of the JPQL query.</li>
 *     </ul>
 * </li>
 * </ul>
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.<p>
 *
 * @version 2.5
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
     * The {@link JPQLGrammar} that will determine how to parse JPQL queries.
     */
    private JPQLGrammar jpqlGrammar;

    /**
     * The context used to query information about the JPQL query.
     */
    private JPQLQueryContext queryContext;

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
        Assert.isNotNull(jpqlGrammar, "The JPQLGrammar cannot be null");
        this.jpqlGrammar = jpqlGrammar;
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
        this.jpqlGrammar  = queryContext.getGrammar();
    }

    /**
     * Creates the concrete instance of the tool that can refactor the content of a JPQL query. This
     * version simply provides the delta of the refactoring operations.
     *
     * @return The concrete instance of {@link RefactoringTool}
     * @see #buildRefactoringTool
     * @since 2.5
     */
    public abstract BasicRefactoringTool buildBasicRefactoringTool();

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
        return buildContentAssistProposals(position, ContentAssistExtension.NULL_HELPER);
    }

    /**
     * Retrieves the possibles choices that can complete the query from the given position within
     * the query.
     * <p>
     * <b>Note:</b> Disposing of the internal data is not done automatically.
     *
     * @param position The position within the query for which a list of possible choices are created
     * for completing the query
     * @param extension This extension can be used to provide additional information that is outside
     * the scope of simply providing JPA metadata information, such as table names, column names,
     * class names
     * @return The list of valid proposals regrouped by categories
     * @since 2.5
     */
    public ContentAssistProposals buildContentAssistProposals(int position, ContentAssistExtension extension) {
        return getContentAssistVisitor().buildProposals(position, extension);
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
     * @param jpqlGrammar The context used to query information about the JPQL query
     * @return A new concrete instance of {@link AbstractGrammarValidator}
     */
    protected abstract AbstractGrammarValidator buildGrammarValidator(JPQLGrammar jpqlGrammar);

    /**
     * Creates a context that will be used to store and retrieve information about the JPQL query.
     *
     * @param jpqlGrammar The JPQL grammar that is required for dictating how the JPQL query will be
     * parsed. It is also used by validation and by the content assist
     * @return A new {@link JPQLQueryContext}
     */
    protected abstract JPQLQueryContext buildJPQLQueryContext(JPQLGrammar jpqlGrammar);

    /**
     * Creates the {@link Comparator} that can sort {@link IType ITypes} based on the numerical
     * priority.
     *
     * @return {@link NumericTypeComparator}
     */
    protected Comparator<IType> buildNumericTypeComparator() {
        return new NumericTypeComparator(getTypeHelper());
    }

    /**
     * Creates the concrete instance of the tool that can refactor the content of a JPQL query. This
     * version provides a way to manipulate the editable version of the JPQL query ({@link
     * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} and simply
     * outputs the result of the refactoring operations, i.e. the updated JPQL query).
     *
     * @return The concrete instance of {@link RefactoringTool}
     * @see #buildBasicRefactoringTool()
     * @since 2.4
     */
    public abstract RefactoringTool buildRefactoringTool();

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

        if (queryContext != null) {
            queryContext.dispose();
        }

        // Not required but during debugging, this is important to be reset
        if (contentAssistVisitor != null) {
            contentAssistVisitor.dispose();
        }
    }

    /**
     * Returns the visitor that can visit a JPQL query and based on the position of the cursor within
     * the JPQL query and determines the valid proposals.
     *
     * @return A concrete instance of {@link AbstractContentAssistVisitor}
     * @see #buildContentAssistVisitor(JPQLQueryContext)
     */
    protected AbstractContentAssistVisitor getContentAssistVisitor() {
        if (contentAssistVisitor == null) {
            contentAssistVisitor = buildContentAssistVisitor(getQueryContext());
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
        return jpqlGrammar;
    }

    protected AbstractGrammarValidator getGrammarValidator() {
        if (grammarValidator == null) {
            grammarValidator = buildGrammarValidator(jpqlGrammar);
        }
        return grammarValidator;
    }

    /**
     * Returns the root of the parsed tree representation of the JPQL query.
     *
     * @return The parsed JPQL query
     */
    public JPQLExpression getJPQLExpression() {
        return getQueryContext().getJPQLExpression();
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
        Collection<InputParameter> inputParameters = getQueryContext().findInputParameters(parameterName);

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
        return getQueryContext().getQuery();
    }

    /**
     * Returns the {@link JPQLQueryContext} that contains information about the JPQL query.
     *
     * @return The {@link JPQLQueryContext} that contains information contained in the JPQL query
     */
    public JPQLQueryContext getQueryContext() {
        if (queryContext == null) {
            queryContext = buildJPQLQueryContext(jpqlGrammar);
        }
        return queryContext;
    }

    /**
     * Calculates the type of the query result of the JPQL query.
     * <p>
     * See {@link org.eclipse.persistence.jpa.jpql.tools.resolver.Resolver Resolver}
     * to understand how the type is calculated.
     *
     * @return The result type of the JPQL query if it could accurately be calculated or the
     * {@link IType} for <code>Object</code> if it could not be calculated
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
            semanticValidator = buildSemanticValidator(getQueryContext());
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
     * Sets the parsed tree representation of the JPQL query. If the expression was parsed outside of
     * the scope of this context, then this method has to be invoked before {@link #setQuery(IQuery)}
     * because the JPQL query is automatically parsed by that method.
     *
     * @param jpqlExpression The parsed representation of the JPQL query to manipulate
     * @see #setQuery(IQuery)
     */
    public void setJPQLExpression(JPQLExpression jpqlExpression) {
        getQueryContext().setJPQLExpression(jpqlExpression);
    }

    /**
     * Sets the external form of the JPQL query, which will be parsed and information will be
     * extracted for later access.
     *
     * @param query The external form of the JPQL query
     */
    public void setQuery(IQuery query) {
        getQueryContext().setQuery(query);
    }

    /**
     * Validates the query by introspecting it grammatically and semantically.
     *
     * @return The non-<code>null</code> list that will be used to store the {@link JPQLQueryProblem
     * problems} if any was found
     */
    public List<JPQLQueryProblem> validate() {
        List<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();
        validate(getJPQLExpression(), problems);
        return problems;
    }

    /**
     * Validates the query by introspecting it grammatically and semantically.
     *
     * @param expression The parsed tree representation of the JPQL fragment to validate
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
        List<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();
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
        List<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();
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
