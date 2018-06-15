/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.eclipse.persistence.jpa.jpql.AbstractValidator.JPQLQueryBNFValidator;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration.Type;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractOrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTripleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseOperandBNF;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.GroupByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.InExpressionItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.InternalBetweenExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalJoinBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LogicalExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.jpql.parser.NewValueBNF;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.OnClause;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.QueryPosition;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclarationBNF;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubqueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals.ClassType;
import org.eclipse.persistence.jpa.jpql.tools.resolver.Declaration;
import org.eclipse.persistence.jpa.jpql.tools.resolver.Resolver;
import org.eclipse.persistence.jpa.jpql.tools.resolver.StateFieldResolver;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.utility.filter.AndFilter;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.eclipse.persistence.jpa.jpql.utility.filter.Filter;
import org.eclipse.persistence.jpa.jpql.utility.filter.NullFilter;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * The visitor provides support for finding the possible proposals within a JPQL query at a certain
 * position.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public abstract class AbstractContentAssistVisitor extends AnonymousExpressionVisitor {

    /**
     * This is used to change the position of the cursor in order to add possible proposals
     */
    protected Stack<Integer> corrections;

    /**
     * The cached helpers that are used by this visitor to add valid content assist proposals.
     */
    protected Map<Class<?>, Object> helpers;

    /**
     * This map contains the {@link Filter} that is used to determine when an identifier is a valid
     * proposal, some of them depends on the expression's type.
     */
    protected Map<String, Filter<Expression>> identifierFilters;

    /**
     * Used to prevent and infinite recursion when one of the visit method is virtually asking a
     * child expression to be visited.
     */
    protected Stack<Expression> lockedExpressions;

    /**
     * The set of possible proposals gathered based on the position in the query.
     */
    protected DefaultContentAssistProposals proposals;

    /**
     * The context used to query information about the JPQL query.
     */
    protected final JPQLQueryContext queryContext;

    /**
     * Contains the position of the cursor within the parsed {@link Expression} from the root node
     * up to the deepest leaf node.
     */
    protected QueryPosition queryPosition;

    /**
     * A virtual space is used to move the position of the cursor by adding an extra space in order
     * to find some proposals within an expression. This is usually used when the trailing whitespace
     * is not owned by the child expression but by one of its parents.
     */
    protected Stack<Integer> virtualSpaces;

    /**
     * The current word, which was retrieved from the JPQL query based on the position of the cursor.
     * The word is the partial string found to the left of the cursor and up to the cursor.
     */
    protected String word;

    /**
     * This is used to retrieve words from the actual JPQL query.
     */
    protected WordParser wordParser;

    /**
     * This {@link Filter} is used to say the {@link Expression} is invalid without doing anything.
     */
    protected static final Filter<Expression> INVALID_IDENTIFIER_FILTER = new Filter<Expression>() {
        @Override
        public boolean accept(Expression expression) {
            return false;
        }
    };

    /**
     * A constant for the length of a whitespace, which is 1.
     */
    protected static final int SPACE_LENGTH = 1;

    /**
     * This {@link Filter} is used to say the {@link Expression} is valid without doing anything.
     */
    protected static final Filter<Expression> VALID_IDENTIFIER_FILTER = new Filter<Expression>() {
        @Override
        public boolean accept(Expression expression) {
            return true;
        }
    };

    /**
     * Creates a new <code>AbstractContentAssistVisitor</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     * @exception NullPointerException The {@link JPQLQueryContext} cannot be <code>null</code>
     */
    protected AbstractContentAssistVisitor(JPQLQueryContext queryContext) {
        super();
        Assert.isNotNull(queryContext, "The JPQLQueryContext cannot be null");
        this.queryContext = queryContext;
        initialize();
    }

    /**
     * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#AGGREGATE}
     * and the beginning starts with the current word.
     *
     * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
     */
    protected void addAggregateIdentifier(String identifier) {
        if (isAggregate(identifier)) {
            addIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#AGGREGATE} and the beginning starts with the
     * current word.
     *
     * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
     * added as proposals if they pass the checks
     */
    protected void addAggregateIdentifiers(JPQLQueryBNF queryBNF) {
        for (String identifier : queryBNF.getIdentifiers()) {
            addAggregateIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#AGGREGATE} and the beginning starts with the
     * current word.
     *
     * @param queryBNFId The unique of the {@link JPQLQueryBNF} for which the registered JPQL
     * identifiers will be added as proposals if they pass the checks
     */
    protected void addAggregateIdentifiers(String queryBNFId) {
        addAggregateIdentifiers(getQueryBNF(queryBNFId));
    }

    /**
     * Adds the JPQL identifiers which correspond to the arithmetic operators as valid proposals. The
     * word has to be an empty string.
     */
    protected void addArithmeticIdentifiers() {
        if (word.length() == 0) {
            addExpressionFactoryIdentifiers(ArithmeticExpressionFactory.ID);
        }
    }

    /**
     * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#CLAUSE}
     * and the beginning starts with the current word.
     *
     * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
     */
    protected void addClauseIdentifier(String identifier) {
        if (isClause(identifier)) {
            addIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#CLAUSE} and the beginning starts with the
     * current word.
     *
     * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
     * added as proposals if they pass the checks
     */
    protected void addClauseIdentifiers(JPQLQueryBNF queryBNF) {
        for (String identifier : queryBNF.getIdentifiers()) {
            addClauseIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#CLAUSE} and the beginning starts with the
     * current word.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
     * JPQL identifiers will be
     * added as proposals if they pass the checks
     */
    protected void addClauseIdentifiers(String queryBNFId) {
        addClauseIdentifiers(getQueryBNF(queryBNFId));
    }

    /**
     * Adds the JPQL identifiers which correspond to the comparison operators as valid proposals. The
     * word has to be an empty string.
     *
     * @param expression The {@link Expression} is preceding the position of the cursor and will help
     * to determine which of the comparison operators are valid
     */
    protected void addComparisonIdentifiers(Expression expression) {

        if (word.length() == 0) {

            for (String identifier : getExpressionFactory(ComparisonExpressionFactory.ID).identifiers()) {
                Filter<Expression> filter = getFilter(identifier);
                if (filter.accept(expression)) {
                    addIdentifier(identifier);
                }
            }
        }
    }

    /**
     * Adds the composite JPQL identifier by following the given rules:
     *
     * <ul>
     * <li>If the word is empty and the offset is -1, then use the rule used by {@link
     * #addIdentifier(String)};</li>
     * <li>Otherwise checks the ending of the JPQL query with a portion of the identifier from the
     * entire length of the identifier to the given offset by cutting off the trailing characters.</li>
     * </ul>
     *
     * @param identifier The composite JPQL identifier to add as valid proposal. The composite part
     * means it is an identifier composed of more than one word, example: <code><b>GROUP BY</b></code>
     * @param offset The smallest length of the given identifier to test against the ending of the
     * JPQL query based on the position of the cursor. The offset is exclusive
     */
    protected void addCompositeIdentifier(String identifier, int offset) {

        // No need to check by fragmenting the composite identifier
        if ((word.length() == 0) && (offset == -1)) {
            addIdentifier(identifier);
        }
        else if (isValidVersion(identifier)) {

            // Make sure the offset is not -1
            offset = Math.max(offset, 0);

            // Cut the composite identifier by removing the ending portion one character at a time
            // and see if the JPQL query is ending with that same text
            // Example: "SELECT e FROM Employee e ORDER B" and the JPQL identifier is "ORDER BY", the
            //          test will do "ORDER B", "ORDER ", "ORDER", "ORD", "OR", "O" and if it does
            //          match then we have to make sure there is a whitespace before to make sure it's
            //          a complete word
            int position = queryPosition.getPosition();

            for (int index = identifier.length(); --index > offset; ) {
                String fragment = identifier.substring(0, index);

                // The text ends with the beginning portion of the JPQL identifier,
                // make sure it has a whitespace before it. There is no case a non-alphanumeric
                // character should also be checked, like '(' or '+'
                if (wordParser.endsWithIgnoreCase(position, fragment)) {

                    char character = wordParser.character(position - fragment.length() - 1);

                    if (Character.isWhitespace(character)) {
                        proposals.addIdentifier(identifier);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#COMPOUND_FUNCTION}
     * and the beginning starts with the current word.
     *
     * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
     * @param expression The {@link Expression} represents the fragment that is before the current
     * word and it helps to filter out some of the compound identifiers
     * @param hasIs Flag indicating if the <code><b>IS</b></code> identifier was found before the word,
     * which would also be before the <code><b>NOT</b></code> identifier if it was also found
     * @param hasNot Flag indicating if the <code><b>NOT</b></code> identifier was found before the word
     */
    protected void addCompoundIdentifier(String identifier,
                                         Expression expression,
                                         boolean hasIs,
                                         boolean hasNot) {

        if (isCompoundFunction(identifier)) {

            Filter<Expression> filter = getFilter(identifier);

            // "IS <identifier>" (examples of possible identifiers: IS NULL, IS NOT NULL)
            if (hasIs && !hasNot) {
                if (identifier.startsWith("IS ") && filter.accept(expression)) {
                    addCompositeIdentifier(identifier, 0);
                }
            }
            // "NOT <identifier>" (examples of possible identifiers: NOT EMPTY, NOT BETWEEN)
            else if (!hasIs && hasNot) {
                if (identifier.startsWith("NOT ") && filter.accept(expression)) {
                    addCompositeIdentifier(identifier, 0);
                }
            }
            // "IS NOT <identifier>" (examples of possible identifiers: IS NOT NULL, IS NOT EMPTY)
            else if (hasIs && hasNot) {
                if (identifier.startsWith("IS NOT ") && filter.accept(expression)) {
                    addCompositeIdentifier(identifier, 0);
                }
            }
            // "<identifier>" (examples of possible identifiers: BETWEEN, MEMBER, MEMBER OF)
            else if (filter.accept(expression)) {
                addIdentifier(identifier);
            }
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#COMPOUND_FUNCTION} and the beginning starts
     * with the current word.
     *
     * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
     * added as proposals if they pass the checks
     * @param expression The {@link Expression} represents the fragment that is before the current
     * word and it helps to filter out some of the compound identifiers
     * @param hasIs Flag indicating if the <code><b>IS</b></code> identifier was found before the word,
     * which would also be before the <code><b>NOT</b></code> identifier if it was also found
     * @param hasNot Flag indicating if the <code><b>NOT</b></code> identifier was found before the word
     */
    protected void addCompoundIdentifiers(JPQLQueryBNF queryBNF,
                                          Expression expression,
                                          boolean hasIs,
                                          boolean hasNot) {

        for (String identifier : queryBNF.getIdentifiers()) {
            addCompoundIdentifier(identifier, expression, hasIs, hasNot);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#COMPOUND_FUNCTION} and the beginning starts
     * with the current word.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
     * JPQL identifiers will be added as proposals if they pass the checks
     * @param expression The {@link Expression} represents the fragment that is before the current
     * word and it helps to filter out some of the compound identifiers
     * @param hasIs Flag indicating if the <code><b>IS</b></code> identifier was found before the word,
     * which would also be before the <code><b>NOT</b></code> identifier if it was also found
     * @param hasNot Flag indicating if the <code><b>NOT</b></code> identifier was found before the word
     */
    protected void addCompoundIdentifiers(String queryBNFId,
                                          Expression expression,
                                          boolean hasIs,
                                          boolean hasNot) {

        addCompoundIdentifiers(getQueryBNF(queryBNFId), expression, hasIs, hasNot);
    }

    /**
     * Adds the entities as possible content assist proposals but will be filtered using the current word.
     */
    protected void addEntities() {
        for (IEntity entity : queryContext.getProvider().entities()) {
            if (isValidProposal(entity.getName(), word)) {
                proposals.addEntity(entity);
            }
        }
    }

    /**
     * Adds the entities as possible content assist proposals but will be filtered using the current
     * word and the entity's type will have to be assignable from the given {@link IType}.
     *
     * @param type The {@link IType} used to filter the abstract schema types
     */
    protected void addEntities(IType type) {

        for (IEntity entity : queryContext.getProvider().entities()) {

            if (isValidProposal(entity.getName(), word) &&
                type.isAssignableTo(entity.getType())) {

                proposals.addEntity(entity);
            }
        }
    }

    /**
     * Adds the given enum constant as a valid proposal.
     *
     * @param enumType The {@link IType} of the {@link Enum}
     * @param enumConstant The enum constant to be added as a valid proposal
     */
    protected void addEnumConstant(IType enumType, String enumConstant) {
        proposals.addEnumConstant(enumType, enumConstant);
    }

    /**
     * Adds the constants of the given enum type as valid proposals if the beginning starts with the
     * given word.
     *
     * @param enumType The {@link IType} of the enum type
     * @param word The word is used to filter the enum constants, which can be <code>null</code> or
     * an empty string
     */
    protected void addEnumConstants(IType enumType, String word) {

        for (String enumConstant : enumType.getEnumConstants()) {
            if (ExpressionTools.startWithIgnoreCase(enumConstant, word)) {
                addEnumConstant(enumType, enumConstant);
            }
        }
    }

    /**
     * Adds the JPQL identifiers that were registered with the given {@link ExpressionFactory}.
     *
     * @param expressionFactory The factory for which its registered JPQL identifiers are added as
     * valid proposals, if they meet the checks
     */
    protected void addExpressionFactoryIdentifiers(ExpressionFactory expressionFactory) {
        for (String identifier : expressionFactory.identifiers()) {
            addIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers that were registered with the {@link ExpressionFactory} with the
     * given unique identifier.
     *
     * @param expressionFactoryId The unique identifier of the factory for which its registered JPQL
     * identifiers are added as valid proposals, if they meet the checks
     */
    protected void addExpressionFactoryIdentifiers(String expressionFactoryId) {
        addExpressionFactoryIdentifiers(getExpressionFactory(expressionFactoryId));
    }

    /**
     * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#FUNCTION}
     * and the beginning starts with the current word.
     *
     * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
     */
    protected void addFunctionIdentifier(String identifier) {
        if (isFunction(identifier)) {
            addIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers for which their role is {@link IdentifierRole#FUNCTION FUNCTION} by
     * determining the {@link JPQLQueryBNF} that represents the fragment for which the given {@link
     * Expression} was parsed.
     * <p>
     * For instance: "<code>SELECT e, AVG(e.name) FROM Employee e</code>" and the given expression is
     * "<code>AVG(e.name)</code>", then the BNF should be the select item BNF.
     * <p>
     * If the BNF allows for a subquery and the expression is encapsulated by parenthesis, then
     * <code><b>SELECT</b></code> will also be added.
     * <p>
     * The identification variables will also be added.
     *
     * @param expression The {@link Expression} is used to determine the {@link JPQLQueryBNF} to use
     * when retrieving the JPQL identifiers representing a function
     */
    protected void addFunctionIdentifiers(Expression expression) {

        JPQLQueryBNF queryBNF = expression.getParent().findQueryBNF(expression);

        addIdentificationVariables();
        addFunctionIdentifiers(queryBNF);

        if (isValid(queryBNF, SubqueryBNF.ID, true) && isEncapsulated(expression)) {
            addIdentifier(SELECT);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#FUNCTION} and the beginning starts with the
     * current word.
     *
     * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
     * added as proposals if they pass the checks
     */
    protected void addFunctionIdentifiers(JPQLQueryBNF queryBNF) {
        for (String identifier : queryBNF.getIdentifiers()) {
            addFunctionIdentifier(identifier);
        }
    }

    /**
     * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
     * proposals if their role is {@link IdentifierRole#FUNCTION} and the beginning starts with the
     * current word.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
     * JPQL identifiers will be added as proposals if they pass the checks
     */
    protected void addFunctionIdentifiers(String queryBNFId) {
        addFunctionIdentifiers(getQueryBNF(queryBNFId));
    }

    /**
     * Adds the given identification variable as a valid proposal.
     *
     * @param identificationVariable The identification variable to add as a proposal if it passes the checks
     */
    protected void addIdentificationVariable(String identificationVariable) {

        if (ExpressionTools.stringIsNotEmpty(identificationVariable) &&
            isValidProposal(identificationVariable, word)) {

            proposals.addIdentificationVariable(identificationVariable);
        }
    }

    /**
     * Adds all the identification variables defined in the current query's <b>FROM</b> clause.
     */
    protected void addIdentificationVariables() {
        addIdentificationVariables(null, IdentificationVariableType.ALL);
    }

    /**
     * Adds the possible identification variables as valid proposals but filter them based on the
     * given type.
     * <p>
     * For instance, if the type is {@link IdentificationVariableType#LEFT}, then any identification
     * variables that have been defined before the given {@link Expression} are valid proposals, but
     * those defined after are not valid proposals.
     *
     * @param expression The {@link Expression} where the content assist was invoked, which helps to
     * determine how to stop adding identification variables
     * @param type Which type of identification variables to add as valid proposals
     */
    protected void addIdentificationVariables(Expression expression, IdentificationVariableType type) {

        for (Declaration declaration : queryContext.getDeclarations()) {
            boolean stop = type.add(this, declaration, expression);
            if (stop) {
                break;
            }
        }
    }

    /**
     * Adds the given identifier as a proposal if it passes the checks.
     *
     * @param identifier The JPQL identifier to add as a proposal
     */
    protected void addIdentifier(String identifier) {

        if (isValidVersion(identifier) &&
            isValidProposal(identifier, word)) {

            proposals.addIdentifier(identifier);
        }
    }

    /**
     * Adds the join specification identifiers as proposals.
     */
    protected void addJoinIdentifiers() {
        addIdentifier(INNER_JOIN);
        addIdentifier(INNER_JOIN_FETCH);
        addIdentifier(JOIN);
        addIdentifier(JOIN_FETCH);
        addIdentifier(LEFT_JOIN);
        addIdentifier(LEFT_JOIN_FETCH);
        addIdentifier(LEFT_OUTER_JOIN);
        addIdentifier(LEFT_OUTER_JOIN_FETCH);
    }

    /**
     * Adds the identification variables defined in the current query's <b>FROM</b> clause that are
     * declared before the given {@link Expression}.
     *
     * @param expression The {@link Expression} used to determine at which declaration to stop
     */
    protected void addLeftIdentificationVariables(Expression expression) {
        addIdentificationVariables(expression, IdentificationVariableType.LEFT);
    }

    /**
     * Adds the logical identifiers, which are <code><b>AND</b></code> and <code><b>OR</b></code>.
     */
    protected void addLogicalIdentifiers() {
        addIdentifier(AND);
        addIdentifier(OR);
    }

    /**
     * Adds the given identification variable as a proposal if it passes the checks. If an entity is
     * found, then it will be registered.
     *
     * @param identificationVariable An identification variable that is
     */
    protected void addRangeIdentificationVariable(String identificationVariable) {

        if (ExpressionTools.stringIsNotEmpty(identificationVariable) &&
            isValidProposal(identificationVariable, word)) {

            // Resolve the identification variable
            Resolver resolver = queryContext.getResolver(identificationVariable);
            IEntity entity = queryContext.getProvider().getEntity(resolver.getType());

            // The identification variable identifies an entity, add the extra information
            if (entity != null) {
                proposals.addRangeIdentificationVariable(identificationVariable, entity);
            }
            else {
                proposals.addIdentificationVariable(identificationVariable);
            }
        }
    }

    /**
     * Adds the result variables defined in the <code>SELECT</code> clause as valid proposals.
     */
    protected void addResultVariables() {
        for (String resultVariable : queryContext.getResultVariables()) {
            addIdentificationVariable(resultVariable);
        }
    }

    /**
     * Adds a virtual space on the stack.
     */
    protected final void addVirtualSpace() {
        virtualSpaces.add(SPACE_LENGTH);
    }

    /**
     * Determines whether the given {@link Expression} can be followed by an arithmetic operator.
     *
     * @param expression The {@link Expression} that found left of the cursor, which determines if
     * the arithmetic operators are appendable or not
     * @return <code>true</code> if the operators are appendable; <code>false</code> otherwise
     */
    protected boolean areArithmeticSymbolsAppendable(Expression expression) {
        return isAppendable(expression, AppendableType.ARITHMETIC);
    }

    /**
     * Determines whether the given {@link Expression} can be followed by a comparison operator.
     *
     * @param expression The {@link Expression} that found left of the cursor, which determines if
     * the comparison operators are appendable or not
     * @return <code>true</code> if the operators are appendable; <code>false</code> otherwise
     */
    protected boolean areComparisonSymbolsAppendable(Expression expression) {
        return isAppendable(expression, AppendableType.COMPARISON);
    }

    /**
     * Determines whether the given {@link Expression} can be followed by a logical operator.
     *
     * @param expression The {@link Expression} that found left of the cursor, which determines if
     * the logical operators are appendable or not
     * @return <code>true</code> if the logical identifiers are appendable; <code>false</code> otherwise
     */
    protected boolean areLogicalSymbolsAppendable(Expression expression) {
        return isAppendable(expression, AppendableType.LOGICAL);
    }

    protected abstract AcceptableTypeVisitor buildAcceptableTypeVisitor();

    protected AppendableExpressionVisitor buildAppendableExpressionVisitor() {
        return new AppendableExpressionVisitor(this);
    }

    protected Filter<Expression> buildCollectionCompoundTypeFilter() {
        return new Filter<Expression>() {
            @Override
            public boolean accept(Expression expression) {
                IType type = queryContext.getType(expression);
                TypeHelper typeHelper = queryContext.getTypeHelper();
                return typeHelper.isCollectionType(type) ||
                       typeHelper.isMapType(type);
            }
        };
    }

    protected CollectionExpressionVisitor buildCollectionExpressionVisitor() {
        return new CollectionExpressionVisitor();
    }

    protected CollectionMappingFilter buildCollectionMappingFilter() {
        return new CollectionMappingFilter();
    }

    protected ConcatExpressionCollectionHelper buildConcatExpressionCollectionHelper() {
        return new ConcatExpressionCollectionHelper(this);
    }

    protected ConditionalClauseCollectionHelper buildConditionalClauseCollectionHelper() {
        return new ConditionalClauseCollectionHelper(this);
    }

    protected ConstrutorCollectionHelper buildConstrutorCollectionHelper() {
        return new ConstrutorCollectionHelper(this);
    }

    protected DeclarationVisitor buildDeclarationVisitor() {
        return new DeclarationVisitor();
    }

    protected DefaultMappingCollector buildDefaultMappingCollector() {
        return new DefaultMappingCollector();
    }

    protected DeleteClauseCollectionHelper buildDeleteClauseCollectionHelper() {
        return new DeleteClauseCollectionHelper(this);
    }

    protected DeleteClauseStatementHelper buildDeleteClauseStatementHelper() {
        return new DeleteClauseStatementHelper(this);
    }

    protected Filter<Expression> buildDifferentComparisonFilter() {
        return new DifferentComparisonFilter();
    }

    protected DoubleEncapsulatedCollectionHelper buildDoubleEncapsulatedCollectionHelper() {
        return new DoubleEncapsulatedCollectionHelper(this);
    }

    protected EncapsulatedExpressionVisitor buildEncapsulatedExpressionVisitor() {
        return new EncapsulatedExpressionVisitor();
    }

    /**
     * Creates a new {@link QueryPosition} containing the corrected positions starting at the given
     * {@link Expression} and traversing the children at is always at the right side of the tree.
     *
     * @param invalidExpression The invalid {@link Expression} for which a new {@link QueryPosition}
     * will be calculated
     * @param startingPointExpression The {@link Expression} from which the calculation of the positions will start
     * @param virtualSpace Single element array that will be used to store the flag indicating if a
     * virtual space should be added or not
     * @return The new {@link QueryPosition} that contains the position of the cursor starting from
     * the given {@link Expression} down to the right leaf
     */
    protected QueryPosition buildEndingPositionFromInvalidExpression(Expression invalidExpression,
                                                                     Expression startingPointExpression,
                                                                     boolean[] virtualSpace) {

        EndingQueryPositionBuilder visitor = getEndingQueryPositionBuilder();

        try {

            visitor.prepare(invalidExpression);
            startingPointExpression.accept(visitor);

            virtualSpace[0] = visitor.hasVirtualSpace();
            return visitor.getQueryPosition();
        }
        finally {
            visitor.dispose();
        }
    }

    protected EndingQueryPositionBuilder buildEndingQueryPositionBuilder() {
        return new EndingQueryPositionBuilder(this);
    }

    protected EnumVisitor buildEnumVisitor() {
        return new EnumVisitor();
    }

    protected FilteringMappingCollector buildFilteringMappingCollector(AbstractPathExpression expression,
                                                                       Resolver resolver,
                                                                       Filter<IMapping> filter,
                                                                       String pattern) {

        return new FilteringMappingCollector(
            resolver,
            buildMappingFilter(expression, filter),
            pattern
        );
    }

    protected FollowingClausesVisitor buildFollowingClausesVisitor() {
        return new FollowingClausesVisitor();
    }

    protected FollowingInvalidExpressionVisitor buildFollowingInvalidExpressionVisitor() {
        return new FollowingInvalidExpressionVisitor(this);
    }

    protected FromClauseCollectionHelper buildFromClauseCollectionHelper() {
        return new FromClauseCollectionHelper(this);
    }

    protected FromClauseStatementHelper buildFromClauseStatementHelper() {
        return new FromClauseStatementHelper(this);
    }

    protected GroupByClauseCollectionHelper buildGroupByClauseCollectionHelper() {
        return new GroupByClauseCollectionHelper(this);
    }

    protected GroupByClauseStatementHelper buildGroupByClauseStatementHelper() {
        return new GroupByClauseStatementHelper(this);
    }

    protected HavingClauseStatementHelper buildHavingClauseStatementHelper() {
        return new HavingClauseStatementHelper(this);
    }

    protected IncompleteCollectionExpressionVisitor buildIncompleteCollectionExpressionVisitor() {
        return new IncompleteCollectionExpressionVisitor();
    }

    protected InvalidExpressionVisitor buildInvalidExpressionVisitor() {
        return new InvalidExpressionVisitor();
    }

    protected JoinCollectionHelper buildJoinCollectionHelper() {
        return new JoinCollectionHelper(this);
    }

    /**
     * Returns the {@link JPQLQueryBNFValidator} that can be used to validate an {@link Expression}
     * by making sure its BNF is part of the given BNF.
     *
     * @param queryBNF The BNF used to determine the validity of an {@link Expression}
     * @return A {@link JPQLQueryBNFValidator} that can determine if an {@link Expression} follows
     * the given BNF
     */
    protected JPQLQueryBNFValidator buildJPQLQueryBNFValidator(JPQLQueryBNF queryBNF) {
        return new JPQLQueryBNFValidator(queryBNF);
    }

    protected MappingCollector buildMappingCollector(AbstractPathExpression expression,
                                                     Resolver resolver,
                                                     Filter<IMapping> filter) {

        return buildFilteringMappingCollector(
            expression,
            resolver,
            filter,
            ExpressionTools.EMPTY_STRING
        );
    }

    protected Filter<IMapping> buildMappingFilter(AbstractPathExpression expression,
                                                  Filter<IMapping> filter) {

        // Wrap the filter with another Filter that will make sure only the
        // mappings with the right type will be accepted, for instance, AVG(e.|
        // can only accept state fields with a numeric type
        IType type = getAcceptableType(expression.getParent());

        // No need to filter
        if (type == null) {
            return filter;
        }

        // This will filter the property mappings
        return new AndFilter<IMapping>(new MappingTypeFilter(type, this), filter);
    }

    protected Filter<IMapping> buildMappingFilter(Expression expression) {
        MappingFilterBuilder visitor = getMappingFilterBuilder();
        try {
            expression.accept(visitor);
            return visitor.filter;
        }
        finally {
            visitor.dispose();
        }
    }

    protected MappingFilterBuilder buildMappingFilterBuilder() {
        return new MappingFilterBuilder(this);
    }

    protected Filter<Expression> buildNonCollectionCompoundTypeFilter() {
        return new Filter<Expression>() {
            @Override
            public boolean accept(Expression expression) {
                IType type = queryContext.getType(expression);
                TypeHelper typeHelper = queryContext.getTypeHelper();
                return !typeHelper.isCollectionType(type) &&
                       !typeHelper.isMapType(type);
            }
        };
    }

    protected NotExpressionVisitor buildNotExpressionVisitor() {
        return new NotExpressionVisitor();
    }

    protected OrderByClauseCollectionHelper buildOrderByClauseCollectionHelper() {
        return new OrderByClauseCollectionHelper(this);
    }

    protected OrderByClauseStatementHelper buildOrderByClauseStatementHelper() {
        return new OrderByClauseStatementHelper(this);
    }

    protected PropertyMappingFilter buildPropertyMappingFilter() {
        return new PropertyMappingFilter();
    }

    /**
     * Prepares this visitor by prepopulating it with the necessary data that is required to properly
     * gather the list of proposals based on the given caret position.
     *
     * @param position The position of the cursor within the JPQL query
     * @return The proposals that are valid choices for the given position
     */
    public ContentAssistProposals buildProposals(int position) {
        return buildProposals(position, ContentAssistExtension.NULL_HELPER);
    }

    /**
     * Prepares this visitor by prepopulating it with the necessary data that is required to properly
     * gather the list of proposals based on the given caret position.
     *
     * @param position The position of the cursor within the JPQL query
     * @param extension This extension can be used to provide additional support to JPQL content assist
     * that is outside the scope of providing proposals related to JPA metadata. It adds support for
     * providing suggestions related to class names, enum constants, table names, column names
     * @return The proposals that are valid choices for the given position
     */
    public ContentAssistProposals buildProposals(int position, ContentAssistExtension extension) {

        try {

            JPQLExpression jpqlExpression = queryContext.getJPQLExpression();
            String jpqlQuery = queryContext.getJPQLQuery();

            // Calculate the position of the cursor within the parsed tree
            queryPosition = jpqlExpression.buildPosition(jpqlQuery, position);

            // Retrieve the word from the JPQL query (which is the text before the position of the cursor)
            wordParser = new WordParser(jpqlQuery);
            wordParser.setPosition(position);
            word = wordParser.partialWord();

            // Now visit the deepest leaf first and calculate the possible proposals
            proposals = new DefaultContentAssistProposals(queryContext.getGrammar(), extension);
            queryPosition.getExpression().accept(this);

            return proposals;
        }
        finally {
            dispose();
        }
    }

    protected RangeVariableDeclarationVisitor buildRangeVariableDeclarationVisitor() {
        return new RangeVariableDeclarationVisitor();
    }

    protected ResultVariableVisitor buildResultVariableVisitor() {
        return new ResultVariableVisitor();
    }

    protected SelectClauseCollectionHelper buildSelectClauseCollectionHelper() {
        return new SelectClauseCollectionHelper(this);
    }

    protected SelectClauseStatementHelper buildSelectClauseStatementHelper() {
        return new SelectClauseStatementHelper(this);
    }

    protected SimpleFromClauseStatementHelper buildSimpleFromClauseStatementHelper() {
        return new SimpleFromClauseStatementHelper(this);
    }

    protected SimpleGroupByClauseStatementHelper buildSimpleGroupByClauseStatementHelper() {
        return new SimpleGroupByClauseStatementHelper(this);
    }

    protected SimpleHavingClauseStatementHelper buildSimpleHavingClauseStatementHelper() {
        return new SimpleHavingClauseStatementHelper(this);
    }

    protected SimpleSelectClauseCollectionHelper buildSimpleSelectClauseCollectionHelper() {
        return new SimpleSelectClauseCollectionHelper(this);
    }

    protected SimpleSelectClauseStatementHelper buildSimpleSelectClauseStatementHelper() {
        return new SimpleSelectClauseStatementHelper(this);
    }

    protected SimpleWhereClauseSelectStatementHelper buildSimpleWhereClauseSelectStatementHelper() {
        return new SimpleWhereClauseSelectStatementHelper(this);
    }

    protected SubqueryAppendableExpressionVisitor buildSubqueryAppendableExpressionVisitor() {
        return new SubqueryAppendableExpressionVisitor();
    }

    protected SubqueryVisitor buildSubqueryVisitor() {
        return new SubqueryVisitor();
    }

    protected TripleEncapsulatedCollectionHelper buildTripleEncapsulatedCollectionHelper() {
        return new TripleEncapsulatedCollectionHelper(this);
    }

    protected UpdateClauseStatementHelper buildUpdateClauseStatementHelper() {
        return new UpdateClauseStatementHelper(this);
    }

    protected UpdateItemCollectionHelper buildUpdateItemCollectionHelper() {
        return new UpdateItemCollectionHelper(this);
    }

    protected VisitParentVisitor buildVisitParentVisitor() {
        return new VisitParentVisitor(this);
    }

    protected WhenClauseConditionalClauseCollectionHelper buildWhenClauseConditionalClauseCollectionHelper() {
        return new WhenClauseConditionalClauseCollectionHelper(this);
    }

    protected WhereClauseDeleteStatementHelper buildWhereClauseDeleteStatementHelper() {
        return new WhereClauseDeleteStatementHelper(this);
    }

    protected WhereClauseSelectStatementHelper buildWhereClauseSelectStatementHelper() {
        return new WhereClauseSelectStatementHelper(this);
    }

    protected WhereClauseUpdateStatementHelper buildWhereClauseUpdateStatementHelper() {
        return new WhereClauseUpdateStatementHelper(this);
    }

    protected WithinInvalidExpressionVisitor buildWithinInvalidExpressionVisitor() {
        return new WithinInvalidExpressionVisitor();
    }

    /**
     * Disposes of the internal data.
     */
    public void dispose() {

        word          = null;
        proposals     = null;
        wordParser    = null;
        queryPosition = null;

        // Not required but during debugging, this is important to be reset
        corrections.setSize(1);
        virtualSpaces.setSize(1);
        lockedExpressions.clear();
    }

    /**
     * Retrieves the {@link RangeVariableDeclaration} that defines the entity name and identification
     * variable for the given {@link UpdateClause}.
     *
     * @return Either the {@link RangeVariableDeclaration} if the JPQL query defines it or <code>null</code>
     * if it was not defined
     */
    protected RangeVariableDeclaration findRangeVariableDeclaration(UpdateClause expression) {
        RangeVariableDeclarationVisitor visitor = getRangeVariableDeclarationVisitor();
        try {
            expression.getRangeVariableDeclaration().accept(visitor);
            return visitor.expression;
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines the root {@link IType} that any type should be assignable. If the {@link IType} is
     * {@link Number}, than any subclasses will be allowed.
     *
     * @param expression The {@link Expression} to visit, including its parent hierarchy until an
     * {@link Expression} requires a certain {@link IType}
     * @return The root {@link IType} allowed or <code>null</code> if anything is allowed
     */
    protected IType getAcceptableType(Expression expression) {
        AcceptableTypeVisitor visitor = getExpressionTypeVisitor();
        try {
            expression.accept(visitor);
            return visitor.type;
        }
        finally {
            visitor.dispose();
        }
    }

    protected AppendableExpressionVisitor getAppendableExpressionVisitor() {
        AppendableExpressionVisitor helper = getHelper(AppendableExpressionVisitor.class);
        if (helper == null) {
            helper = buildAppendableExpressionVisitor();
            registerHelper(AppendableExpressionVisitor.class, helper);
        }
        return helper;
    }

    /**
     * Casts the given {@link Expression} to a {@link CollectionExpression} if it is actually an
     * object of that type.
     *
     * @param expression The {@link Expression} to cast
     * @return The given {@link Expression} if it is a {@link CollectionExpression} or <code>null</code>
     * if it is any other object
     */
    protected CollectionExpression getCollectionExpression(Expression expression) {
        CollectionExpressionVisitor visitor = getCollectionExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.expression;
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Returns the visitor that collects the {@link CollectionExpression} if it's been visited.
     *
     * @return The {@link CollectionExpressionVisitor}
     * @see #buildCollectionExpressionVisitor()
     */
    protected CollectionExpressionVisitor getCollectionExpressionVisitor() {
        CollectionExpressionVisitor visitor = getHelper(CollectionExpressionVisitor.class);
        if (visitor == null) {
            visitor = buildCollectionExpressionVisitor();
            registerHelper(CollectionExpressionVisitor.class, visitor);
        }
        return visitor;
    }

    protected ConcatExpressionCollectionHelper getConcatExpressionCollectionHelper() {
        ConcatExpressionCollectionHelper helper = getHelper(ConcatExpressionCollectionHelper.class);
        if (helper == null) {
            helper = buildConcatExpressionCollectionHelper();
            registerHelper(ConcatExpressionCollectionHelper.class, helper);
        }
        return helper;
    }

    protected ConditionalClauseCollectionHelper getConditionalClauseCollectionHelper() {
        ConditionalClauseCollectionHelper helper = getHelper(ConditionalClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildConditionalClauseCollectionHelper();
            registerHelper(ConditionalClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected ConstrutorCollectionHelper getConstructorCollectionHelper() {
        ConstrutorCollectionHelper helper = getHelper(ConstrutorCollectionHelper.class);
        if (helper == null) {
            helper = buildConstrutorCollectionHelper();
            registerHelper(ConstrutorCollectionHelper.class, helper);
        }
        return helper;
    }

    protected DeclarationVisitor getDeclarationVisitor() {
        DeclarationVisitor helper = getHelper(DeclarationVisitor.class);
        if (helper == null) {
            helper = buildDeclarationVisitor();
            registerHelper(DeclarationVisitor.class, helper);
        }
        return helper;
    }

    protected MappingCollector getDefaultMappingCollector() {
        DefaultMappingCollector helper = getHelper(DefaultMappingCollector.class);
        if (helper == null) {
            helper = buildDefaultMappingCollector();
            registerHelper(DefaultMappingCollector.class, helper);
        }
        return helper;
    }

    protected DeleteClauseCollectionHelper getDeleteClauseCollectionHelper() {
        DeleteClauseCollectionHelper helper = getHelper(DeleteClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildDeleteClauseCollectionHelper();
            registerHelper(DeleteClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected DeleteClauseStatementHelper getDeleteClauseStatementHelper() {
        DeleteClauseStatementHelper helper = getHelper(DeleteClauseStatementHelper.class);
        if (helper == null) {
            helper = buildDeleteClauseStatementHelper();
            registerHelper(DeleteClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected DoubleEncapsulatedCollectionHelper getDoubleEncapsulatedCollectionHelper() {
        DoubleEncapsulatedCollectionHelper helper = getHelper(DoubleEncapsulatedCollectionHelper.class);
        if (helper == null) {
            helper = buildDoubleEncapsulatedCollectionHelper();
            registerHelper(DoubleEncapsulatedCollectionHelper.class, helper);
        }
        return helper;
    }

    protected EncapsulatedExpressionVisitor getEncapsulatedExpressionVisitor() {
        EncapsulatedExpressionVisitor helper = getHelper(EncapsulatedExpressionVisitor.class);
        if (helper == null) {
            helper = buildEncapsulatedExpressionVisitor();
            registerHelper(EncapsulatedExpressionVisitor.class, helper);
        }
        return helper;
    }

    protected EndingQueryPositionBuilder getEndingQueryPositionBuilder() {
        EndingQueryPositionBuilder visitor = getHelper(EndingQueryPositionBuilder.class);
        if (visitor == null) {
            visitor = buildEndingQueryPositionBuilder();
            registerHelper(EndingQueryPositionBuilder.class, visitor);
        }
        return visitor;
    }

    protected EnumVisitor getEnumVisitor() {
        EnumVisitor helper = getHelper(EnumVisitor.class);
        if (helper == null) {
            helper = buildEnumVisitor();
            helpers.put(EnumVisitor.class, helper);
        }
        return helper;
    }

    /**
     * Retrieves the registered {@link ExpressionFactory} that was registered for the given unique identifier.
     *
     * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to retrieve
     * @return The {@link ExpressionFactory} mapped with the given unique identifier
     */
    protected ExpressionFactory getExpressionFactory(String expressionFactoryId) {
        return queryContext.getExpressionRegistry().getExpressionFactory(expressionFactoryId);
    }

    protected AcceptableTypeVisitor getExpressionTypeVisitor() {
        AcceptableTypeVisitor helper = getHelper(AcceptableTypeVisitor.class);
        if (helper == null) {
            helper = buildAcceptableTypeVisitor();
            registerHelper(AcceptableTypeVisitor.class, helper);
        }
        return helper;
    }

    protected Filter<Expression> getFilter(String identifier) {
        Filter<Expression> filter = identifierFilters.get(identifier);
        return (filter != null) ? filter : INVALID_IDENTIFIER_FILTER;
    }

    protected FollowingClausesVisitor getFollowingClausesVisitor() {
        FollowingClausesVisitor helper = getHelper(FollowingClausesVisitor.class);
        if (helper == null) {
            helper = buildFollowingClausesVisitor();
            helpers.put(FollowingClausesVisitor.class, helper);
        }
        return helper;
    }

    protected FollowingInvalidExpressionVisitor getFollowingInvalidExpressionVisitor() {
        FollowingInvalidExpressionVisitor helper = getHelper(FollowingInvalidExpressionVisitor.class);
        if (helper == null) {
            helper = buildFollowingInvalidExpressionVisitor();
            registerHelper(FollowingInvalidExpressionVisitor.class, helper);
        }
        return helper;
    }

    protected FromClauseCollectionHelper getFromClauseCollectionHelper() {
        FromClauseCollectionHelper helper = getHelper(FromClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildFromClauseCollectionHelper();
            registerHelper(FromClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected FromClauseStatementHelper getFromClauseStatementHelper() {
        FromClauseStatementHelper helper = getHelper(FromClauseStatementHelper.class);
        if (helper == null) {
            helper = buildFromClauseStatementHelper();
            registerHelper(FromClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected GroupByClauseCollectionHelper getGroupByClauseCollectionHelper() {
        GroupByClauseCollectionHelper helper = getHelper(GroupByClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildGroupByClauseCollectionHelper();
            registerHelper(GroupByClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected GroupByClauseStatementHelper getGroupByClauseStatementHelper() {
        GroupByClauseStatementHelper helper = getHelper(GroupByClauseStatementHelper.class);
        if (helper == null) {
            helper = buildGroupByClauseStatementHelper();
            registerHelper(GroupByClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected HavingClauseStatementHelper getHavingClauseStatementHelper() {
        HavingClauseStatementHelper helper = getHelper(HavingClauseStatementHelper.class);
        if (helper == null) {
            helper = buildHavingClauseStatementHelper();
            registerHelper(HavingClauseStatementHelper.class, helper);
        }
        return helper;
    }

    /**
     * Retrieves the helper associated with the given helper class.
     *
     * @param helperClass The Java class of the helper to retrieve
     * @return The helper being requested
     */
    @SuppressWarnings("unchecked")
    protected final <T> T getHelper(Class<T> helperClass) {
        return (T) helpers.get(helperClass);
    }

    /**
     * Retrieves the role of the given identifier. A role helps to describe the purpose of the
     * identifier in a query.
     *
     * @param identifier The identifier for which its role is requested
     * @return The role of the given identifier
     */
    protected IdentifierRole getIdentifierRole(String identifier) {
        return queryContext.getExpressionRegistry().getIdentifierRole(identifier);
    }

    protected IncompleteCollectionExpressionVisitor getIncompleteCollectionExpressionVisitor() {
        IncompleteCollectionExpressionVisitor helper = getHelper(IncompleteCollectionExpressionVisitor.class);
        if (helper == null) {
            helper = buildIncompleteCollectionExpressionVisitor();
            registerHelper(IncompleteCollectionExpressionVisitor.class, helper);
        }
        return helper;
    }

    protected InvalidExpressionVisitor getInvalidExpressionVisitor() {
        InvalidExpressionVisitor visitor = getHelper(InvalidExpressionVisitor.class);
        if (visitor == null) {
            visitor = buildInvalidExpressionVisitor();
            registerHelper(InvalidExpressionVisitor.class, visitor);
        }
        return visitor;
    }

    protected JoinCollectionHelper getJoinCollectionHelper() {
        JoinCollectionHelper helper = getHelper(JoinCollectionHelper.class);
        if (helper == null) {
            helper = buildJoinCollectionHelper();
            registerHelper(JoinCollectionHelper.class, helper);
        }
        return helper;
    }

    /**
     * Returns the latest supported JPQL grammar, which is required when checking the validity of a
     * JPQL identifier. This is due to the fact that any JPQL identifiers defined in a more recent
     * spec will be seen to be registered in version 1.0 of the spec. By returning the latest version,
     * any JPQL identifiers should have a version registered.
     *
     * @return The latest {@link JPQLGrammar} supported by the provider
     */
    protected abstract JPQLGrammar getLatestGrammar();

    protected Filter<IMapping> getMappingCollectionFilter() {
        CollectionMappingFilter helper = getHelper(CollectionMappingFilter.class);
        if (helper == null) {
            helper = buildCollectionMappingFilter();
            registerHelper(CollectionMappingFilter.class, helper);
        }
        return helper;
    }

    protected MappingFilterBuilder getMappingFilterBuilder() {
        MappingFilterBuilder helper = getHelper(MappingFilterBuilder.class);
        if (helper == null) {
            helper = buildMappingFilterBuilder();
            helpers.put(MappingFilterBuilder.class, helper);
        }
        return helper;
    }

    protected Filter<IMapping> getMappingPropertyFilter() {
        PropertyMappingFilter helper = getHelper(PropertyMappingFilter.class);
        if (helper == null) {
            helper = buildPropertyMappingFilter();
            helpers.put(PropertyMappingFilter.class, helper);
        }
        return helper;
    }

    protected NotExpressionVisitor getNotExpressionVisitor() {
        NotExpressionVisitor helper = getHelper(NotExpressionVisitor.class);
        if (helper == null) {
            helper = buildNotExpressionVisitor();
            registerHelper(NotExpressionVisitor.class, helper);
        }
        return helper;
    }

    protected OrderByClauseCollectionHelper getOrderByClauseCollectionHelper() {
        OrderByClauseCollectionHelper helper = getHelper(OrderByClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildOrderByClauseCollectionHelper();
            registerHelper(OrderByClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected OrderByClauseStatementHelper getOrderByClauseStatementHelper() {
        OrderByClauseStatementHelper helper = getHelper(OrderByClauseStatementHelper.class);
        if (helper == null) {
            helper = buildOrderByClauseStatementHelper();
            registerHelper(OrderByClauseStatementHelper.class, helper);
        }
        return helper;
    }

    /**
     * Retrieves the {@link JPQLQueryBNF} that was registered for the given unique identifier.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} to retrieve
     * @return The {@link JPQLQueryBNF} representing a section of the grammar
     */
    protected JPQLQueryBNF getQueryBNF(String queryBNFId) {
        return queryContext.getExpressionRegistry().getQueryBNF(queryBNFId);
    }

    protected RangeVariableDeclarationVisitor getRangeVariableDeclarationVisitor() {
        RangeVariableDeclarationVisitor helper = getHelper(RangeVariableDeclarationVisitor.class);
        if (helper == null) {
            helper = buildRangeVariableDeclarationVisitor();
            registerHelper(RangeVariableDeclarationVisitor.class, helper);
        }
        return helper;
    }

    protected ResultVariableVisitor getResultVariableVisitor() {
        ResultVariableVisitor helper = getHelper(ResultVariableVisitor.class);
        if (helper == null) {
            helper = buildResultVariableVisitor();
            registerHelper(ResultVariableVisitor.class, helper);
        }
        return helper;
    }

    protected SelectClauseCollectionHelper getSelectClauseCollectionHelper() {
        SelectClauseCollectionHelper helper = getHelper(SelectClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildSelectClauseCollectionHelper();
            registerHelper(SelectClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected SelectClauseStatementHelper getSelectClauseStatementHelper() {
        SelectClauseStatementHelper helper = getHelper(SelectClauseStatementHelper.class);
        if (helper == null) {
            helper = buildSelectClauseStatementHelper();
            registerHelper(SelectClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected SimpleFromClauseStatementHelper getSimpleFromClauseStatementHelper() {
        SimpleFromClauseStatementHelper helper = getHelper(SimpleFromClauseStatementHelper.class);
        if (helper == null) {
            helper = buildSimpleFromClauseStatementHelper();
            registerHelper(SimpleFromClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected SimpleGroupByClauseStatementHelper getSimpleGroupByClauseStatementHelper() {
        SimpleGroupByClauseStatementHelper helper = getHelper(SimpleGroupByClauseStatementHelper.class);
        if (helper == null) {
            helper = buildSimpleGroupByClauseStatementHelper();
            registerHelper(SimpleGroupByClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected SimpleHavingClauseStatementHelper getSimpleHavingClauseStatementHelper() {
        SimpleHavingClauseStatementHelper helper = getHelper(SimpleHavingClauseStatementHelper.class);
        if (helper == null) {
            helper = buildSimpleHavingClauseStatementHelper();
            registerHelper(SimpleHavingClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected SimpleSelectClauseCollectionHelper getSimpleSelectClauseCollectionHelper() {
        SimpleSelectClauseCollectionHelper helper = getHelper(SimpleSelectClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildSimpleSelectClauseCollectionHelper();
            registerHelper(SimpleSelectClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected SimpleSelectClauseStatementHelper getSimpleSelectClauseStatementHelper() {
        SimpleSelectClauseStatementHelper helper = getHelper(SimpleSelectClauseStatementHelper.class);
        if (helper == null) {
            helper = buildSimpleSelectClauseStatementHelper();
            registerHelper(SimpleSelectClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected SimpleWhereClauseSelectStatementHelper getSimpleWhereClauseSelectStatementHelper() {
        SimpleWhereClauseSelectStatementHelper helper = getHelper(SimpleWhereClauseSelectStatementHelper.class);
        if (helper == null) {
            helper = buildSimpleWhereClauseSelectStatementHelper();
            registerHelper(SimpleWhereClauseSelectStatementHelper.class, helper);
        }
        return helper;
    }

    protected SubqueryAppendableExpressionVisitor getSubqueryAppendableExpressionVisitor() {
        SubqueryAppendableExpressionVisitor helper = getHelper(SubqueryAppendableExpressionVisitor.class);
        if (helper == null) {
            helper = buildSubqueryAppendableExpressionVisitor();
            registerHelper(SubqueryAppendableExpressionVisitor.class, helper);
        }
        return helper;
    }

    protected SubqueryVisitor getSubqueryVisitor() {
        SubqueryVisitor helper = getHelper(SubqueryVisitor.class);
        if (helper == null) {
            helper = buildSubqueryVisitor();
            registerHelper(SubqueryVisitor.class, helper);
        }
        return helper;
    }

    protected TripleEncapsulatedCollectionHelper getTripleEncapsulatedCollectionHelper() {
        TripleEncapsulatedCollectionHelper helper = getHelper(TripleEncapsulatedCollectionHelper.class);
        if (helper == null) {
            helper = buildTripleEncapsulatedCollectionHelper();
            registerHelper(TripleEncapsulatedCollectionHelper.class, helper);
        }
        return helper;
    }

    protected UpdateClauseStatementHelper getUpdateClauseStatementHelper() {
        UpdateClauseStatementHelper helper = getHelper(UpdateClauseStatementHelper.class);
        if (helper == null) {
            helper = buildUpdateClauseStatementHelper();
            registerHelper(UpdateClauseStatementHelper.class, helper);
        }
        return helper;
    }

    protected UpdateItemCollectionHelper getUpdateItemCollectionHelper() {
        UpdateItemCollectionHelper helper = getHelper(UpdateItemCollectionHelper.class);
        if (helper == null) {
            helper = buildUpdateItemCollectionHelper();
            registerHelper(UpdateItemCollectionHelper.class, helper);
        }
        return helper;
    }

    protected VisitParentVisitor getVisitParentVisitor() {
        VisitParentVisitor helper = getHelper(VisitParentVisitor.class);
        if (helper == null) {
            helper = buildVisitParentVisitor();
            registerHelper(VisitParentVisitor.class, helper);
        }
        return helper;
    }

    protected WhenClauseConditionalClauseCollectionHelper getWhenClauseConditionalClauseCollectionHelper() {
        WhenClauseConditionalClauseCollectionHelper helper = getHelper(WhenClauseConditionalClauseCollectionHelper.class);
        if (helper == null) {
            helper = buildWhenClauseConditionalClauseCollectionHelper();
            registerHelper(WhenClauseConditionalClauseCollectionHelper.class, helper);
        }
        return helper;
    }

    protected WhereClauseDeleteStatementHelper getWhereClauseDeleteStatementHelper() {
        WhereClauseDeleteStatementHelper helper = getHelper(WhereClauseDeleteStatementHelper.class);
        if (helper == null) {
            helper = buildWhereClauseDeleteStatementHelper();
            registerHelper(WhereClauseDeleteStatementHelper.class, helper);
        }
        return helper;
    }

    protected WhereClauseSelectStatementHelper getWhereClauseSelectStatementHelper() {
        WhereClauseSelectStatementHelper helper = getHelper(WhereClauseSelectStatementHelper.class);
        if (helper == null) {
            helper = buildWhereClauseSelectStatementHelper();
            registerHelper(WhereClauseSelectStatementHelper.class, helper);
        }
        return helper;
    }

    protected WhereClauseUpdateStatementHelper getWhereClauseUpdateStatementHelper() {
        WhereClauseUpdateStatementHelper helper = getHelper(WhereClauseUpdateStatementHelper.class);
        if (helper == null) {
            helper = buildWhereClauseUpdateStatementHelper();
            registerHelper(WhereClauseUpdateStatementHelper.class, helper);
        }
        return helper;
    }

    protected WithinInvalidExpressionVisitor getWithinInvalidExpressionVisitor() {
        WithinInvalidExpressionVisitor helper = getHelper(WithinInvalidExpressionVisitor.class);
        if (helper == null) {
            helper = buildWithinInvalidExpressionVisitor();
            registerHelper(WithinInvalidExpressionVisitor.class, helper);
        }
        return helper;
    }

    protected boolean hasClausesDefinedBetween(Expression expression,
                                               String afterIdentifier,
                                               String beforeIdentifier) {

        FollowingClausesVisitor visitor = getFollowingClausesVisitor();

        try {
            visitor.afterIdentifier  = afterIdentifier;
            visitor.beforeIdentifier = beforeIdentifier;

            expression.accept(visitor);

            return visitor.hasFollowUpClauses;
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether there is a virtual space to be considered or not.
     *
     * @return <code>true</code> if there is a virtual space to count as a real one or not
     */
    protected final boolean hasVirtualSpace() {
        return virtualSpaces.peek() > 0;
    }

    /**
     * Initializes this visitor.
     */
    protected void initialize() {

        helpers = new HashMap<Class<?>, Object>();
        lockedExpressions = new Stack<Expression>();

        virtualSpaces = new Stack<Integer>();
        virtualSpaces.add(0);

        corrections = new Stack<Integer>();
        corrections.add(0);

        identifierFilters = new HashMap<String, Filter<Expression>>();
        identifierFilters.put(DIFFERENT,             VALID_IDENTIFIER_FILTER);
        identifierFilters.put(EQUAL,                 VALID_IDENTIFIER_FILTER);
        identifierFilters.put(MEMBER,                VALID_IDENTIFIER_FILTER);
        identifierFilters.put(MEMBER_OF,             VALID_IDENTIFIER_FILTER);
        identifierFilters.put(NOT_MEMBER,            VALID_IDENTIFIER_FILTER);
        identifierFilters.put(NOT_MEMBER_OF,         VALID_IDENTIFIER_FILTER);
        identifierFilters.put(IS_NULL,               VALID_IDENTIFIER_FILTER);
        identifierFilters.put(IS_NOT_NULL,           VALID_IDENTIFIER_FILTER);
        identifierFilters.put(IN,                    VALID_IDENTIFIER_FILTER);
        identifierFilters.put(NOT_IN,                VALID_IDENTIFIER_FILTER);

        // 'EMPTY', 'NOT EMPTY'
        Filter<Expression> filter = buildCollectionCompoundTypeFilter();
        identifierFilters.put(IS_EMPTY,     filter);
        identifierFilters.put(IS_NOT_EMPTY, filter);

        // 'BETWEEN', 'NOT BETWEEN'
        filter = buildNonCollectionCompoundTypeFilter();
        identifierFilters.put(BETWEEN,     filter);
        identifierFilters.put(NOT_BETWEEN, filter);
        identifierFilters.put(LIKE,        filter);
        identifierFilters.put(NOT_LIKE,    filter);

        // '<', '<=', '>=', '>'
        filter = buildDifferentComparisonFilter();
        identifierFilters.put(GREATER_THAN,          filter);
        identifierFilters.put(GREATER_THAN_OR_EQUAL, filter);
        identifierFilters.put(LOWER_THAN,            filter);
        identifierFilters.put(LOWER_THAN_OR_EQUAL,   filter);
    }

    /**
     * Determines whether the given JPQL identifier used in an aggregate expression; for instance
     * <b>AND</b>.
     *
     * @param identifier The identifier to validate
     * @return <code>true</code> if the given identifier is used in an aggregate expression;
     * <code>false</code> otherwise
     */
    protected boolean isAggregate(String identifier) {
        return getIdentifierRole(identifier) == IdentifierRole.AGGREGATE;
    }

    /**
     * Determines whether a certain type of JPQL identifiers can be appended to the JPQL query based
     * on the given {@link Expression}.
     *
     * @param expression The {@link Expression} that determines what can be appended
     * @param appendableType The type of identifiers to append to the JPQL query
     * @return <code>true</code> if the JPQL identifiers with the given type can be appended to the
     * JPQL query; <code>false</code> if they cannot
     */
    protected boolean isAppendable(Expression expression, AppendableType appendableType) {
        AppendableExpressionVisitor visitor = getAppendableExpressionVisitor();
        try {
            visitor.appendableType = appendableType;
            expression.accept(visitor);
            return visitor.isAppendable();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether
     *
     * @param expression
     * @return
     */
    protected boolean isAppendableToCollection(Expression expression) {
        IncompleteCollectionExpressionVisitor visitor = getIncompleteCollectionExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.isComplete();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given JPQL identifier used in a clause; for instance <b>SELECT</b>.
     *
     * @param identifier The identifier to validate
     * @return <code>true</code> if the given identifier is a clause; <code>false</code> otherwise
     */
    protected boolean isClause(String identifier) {
        return getIdentifierRole(identifier) == IdentifierRole.CLAUSE;
    }

    /**
     * Determines whether the identifiers identifying clauses can be appended to the JPQL query based
     * on the given {@link Expression}.
     *
     * @param expression The {@link Expression} that determines what can be appended
     * @return <code>true</code> if the following clauses can be appended to the JPQL query;
     * <code>false</code> if they cannot
     */
    protected boolean isClauseAppendable(Expression expression) {
        return isAppendable(expression, AppendableType.CLAUSE);
    }

    /**
     * Determines whether the given {@link Expression} is grammatically complete.
     *
     * @param expression The {@link Expression} to verify its grammar integrity
     * @return <code>true</code> if the {@link Expression} is grammatically complete;
     * <code>false</code> otherwise
     */
    protected boolean isComplete(Expression expression) {
        return isAppendable(expression, AppendableType.COMPLETE);
    }

    /**
     * Determines whether the given {@link Expression} can be used as the left side of a compound
     * expression.
     *
     * @param expression The {@link Expression} that determines if the JPQL identifiers with
     * {@link IdentifierRole#COMPOUND_FUNCTION} can be used to create a compound expression
     * @return <code>true</code> if the compound identifiers can be appended to the JPQL query;
     * <code>false</code> if they cannot
     */
    protected boolean isCompoundable(Expression expression) {
        return isAppendable(expression, AppendableType.COMPOUNDABLE);
    }

    /**
     * Determines whether the given JPQL identifier used in a compound expression; an example would
     * be <b>BETWEEN</b> or <b>MEMBER</b>.
     *
     * @param identifier The identifier to validate
     * @return <code>true</code> if the given identifier is used in a compound expression;
     * <code>false</code> otherwise
     */
    protected boolean isCompoundFunction(String identifier) {

        // Only the full JPQL identifier is valid
        if (identifier == IS || identifier == OF) {
            return false;
        }

        return getIdentifierRole(identifier) == IdentifierRole.COMPOUND_FUNCTION;
    }

    /**
     * Determines whether the given {@link AbstractPathExpression} is found within a declaration expression.
     *
     * @param expression The {@link AbstractPathExpression} to visit
     * @return <code>true</code> if the visited {@link CollectionValuedPathExpression} is owned by
     * a {@link RangeVariableDeclaration}, which indicates it is used to define the "root" object;
     * <code>false</code> if it is not
     */
    protected boolean isDeclaration(AbstractPathExpression expression) {
        DeclarationVisitor visitor = getDeclarationVisitor();
        try {
            expression.accept(visitor);
            return visitor.isDeclaration();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given {@link Expression} is being encapsulated or not.
     *
     * @param expression The {@link Expression} to scan for encapsulation
     * @return <code>true</code> if the given {@link Expression} is within parenthesis;
     * <code>false</code> otherwise
     */
    protected boolean isEncapsulated(Expression expression) {
        EncapsulatedExpressionVisitor visitor = getEncapsulatedExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.isEncapsulated();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given {@link AbstractPathExpression} could potentially represent a
     * fully qualified enum constant, which is dictated by the location of the path expression within
     * the query. Only a few location allows an enum constant.
     *
     * @param expression The {@link AbstractPathExpression} to visit
     * @return <code>true</code> if the path expression represents a enum constant;
     * <code>false</code> otherwise
     */
    protected boolean isEnumAllowed(AbstractPathExpression expression) {
        EnumVisitor visitor = getEnumVisitor();
        try {
            visitor.pathExpression = expression;
            expression.accept(visitor);
            return visitor.isValid();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given {@link Expression} is preceded by an invalid expression.
     *
     * @param expression The {@link Expression} to visit
     * @return <code>true</code> if the visited {@link Expression} is part of a collection of
     * expressions and an invalid expression precede it; <code>false</code> otherwise
     */
    protected boolean isFollowingInvalidExpression(Expression expression) {
        FollowingInvalidExpressionVisitor visitor = getFollowingInvalidExpressionVisitor();
        try {
            visitor.expression = expression;
            expression.accept(visitor);
            return visitor.isFollowingInvalidExpression();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given JPQL identifier is a function, an example would be <b>AVG</b>.
     *
     * @param identifier The identifier to validate
     * @return <code>true</code> if the given identifier is a function; <code>false</code> otherwise
     */
    protected boolean isFunction(String identifier) {
        return getIdentifierRole(identifier) == IdentifierRole.FUNCTION;
    }

    /**
     * Determines whether the given {@link Expression} is in a subquery or in the top-level query.
     *
     * @param expression The {@link Expression} to visit its parent hierarchy
     * @return <code>true</code> if the owning query is a subquery; <code>false</code> if it's the
     * top-level query
     */
    protected boolean isInSubquery(Expression expression) {
        SubqueryVisitor visitor = getSubqueryVisitor();
        try {
            expression.accept(visitor);
            return visitor.isInSubquery();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given {@link Expression} represents an invalid fragment.
     *
     * @param expression The {@link Expression} to visit
     * @return <code>true</code> if the {@link Expression} is an invalid fragment;
     * <code>false</code> otherwise
     */
    protected boolean isInvalidExpression(Expression expression) {
        InvalidExpressionVisitor visitor = getInvalidExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.isInvalid();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether a <code><b>JOIN FETCH</b></code> expression can be identified by with an
     * identification variable or not.
     *
     * @return <code>true</code> if the expression can have an identification variable;
     * <code>false</code> otherwise
     */
    protected abstract boolean isJoinFetchIdentifiable();

    /**
     * Determines whether the given {@link Expression} has been set has the lock to prevent an
     * infinite recursion.
     *
     * @param expression The {@link Expression} to check if it is locked
     * @return <code>true</code> if the given {@link Expression} has been marked as locked;
     * <code>false</code> otherwise
     */
    protected boolean isLocked(Expression expression) {
        return !lockedExpressions.empty() && (lockedExpressions.peek() == expression);
    }

    /**
     * Determines whether the given {@link Expression} represents the negated expression.
     *
     * @param expression The {@link Expression} to visit
     * @return <code>true</code> if the {@link Expression} is {@link NotExpression};
     * <code>false</code> otherwise
     */
    protected boolean isNotExpression(Expression expression) {
        NotExpressionVisitor visitor = getNotExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.isNotExpression();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given position is within the given word.
     * <p>
     * Example: position=0, word="JPQL" {@literal =>} true
     * Example: position=1, word="JPQL" {@literal =>} true
     * Example: position=4, word="JPQL" {@literal =>} true
     * Example: position=5, word="JPQL" {@literal =>} true
     * Example: position=5, offset 2, (actual cursor position is 3), word="JPQL" {@literal =>} true
     *
     * @param position The position of the cursor
     * @param offset The offset to adjust the position
     * @param word The word to check if the cursor is positioned in it
     * @return <code>true</code> if the given position is within the given word;
     * <code>false</code> otherwise
     */
    protected boolean isPositionWithin(int position, int offset, String word) {
        return (position >= offset) && (position - offset <= word.length());
    }

    /**
     * Determines whether the given position is within the given word.
     * <p>
     * Example: position=0, word="JPQL" {@literal =>} true
     * Example: position=1, word="JPQL" {@literal =>} true
     * Example: position=4, word="JPQL" {@literal =>} true
     * Example: position=5, word="JPQL" {@literal =>} true
     *
     * @param position The position of the cursor
     * @param word The word to check if the cursor is positioned in it
     * @return <code>true</code> if the given position is within the given word;
     * <code>false</code> otherwise
     */
    protected boolean isPositionWithin(int position, String word) {
        return isPositionWithin(position, 0, word);
    }

    /**
     * Determines whether the JPQL identifier starting a subquery (<code><b>SELECT</b></code>) can
     * be appended based on the given {@link Expression} which is preceding the position of the cursor.
     *
     * @param expression The {@link Expression} that precedes the position of the cursor
     * @return <code>true</code> if a subquery can be appended; <code>false</code> otherwise
     */
    protected boolean isSubqueryAppendable(Expression expression) {
        AbstractAppendableExpressionVisitor visitor = getSubqueryAppendableExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.isAppendable();
        }
        finally {
            visitor.dispose();
        }
    }

    /**
     * Determines whether the given {@link Expression} part is an expression of the given query BNF.
     *
     * @param expression The {@link Expression} to validate based on the query BNF
     * @param queryBNF
     * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
     * <code>false</code> otherwise
     */
    protected boolean isValid(Expression expression, JPQLQueryBNF queryBNF) {
        JPQLQueryBNFValidator validator = buildJPQLQueryBNFValidator(queryBNF);
        try {
            expression.accept(validator);
            return validator.isValid();
        }
        finally {
            validator.dispose();
        }
    }

    /**
     * Determines whether the given {@link Expression} part is an expression of the given query BNF.
     *
     * @param expression The {@link Expression} to validate based on the query BNF
     * @param queryBNFId
     * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
     * <code>false</code> otherwise
     */
    protected boolean isValid(Expression expression, String queryBNFId) {
        return isValid(expression, getQueryBNF(queryBNFId));
    }

    /**
     * Determines whether the given {@link JPQLQueryBNF} part is the given parent {@link JPQLQueryBNF}.
     *
     * @param parentQueryBNF The "root" of the BNF used to determine if the other is a descendant
     * @param queryBNF The BNF to check if it is a descendant of the parent
     * @return <code>true</code> if the {@link JPQLQueryBNF} is a descendant of the given parent
     * {@link JPQLQueryBNF}; <code>false</code> otherwise
     */
    protected boolean isValid(JPQLQueryBNF parentQueryBNF, JPQLQueryBNF queryBNF) {
        return isValid(parentQueryBNF, queryBNF, false);
    }

    /**
     * Determines whether the given {@link JPQLQueryBNF} part is the given parent {@link JPQLQueryBNF}.
     *
     * @param parentQueryBNF The "root" of the BNF used to determine if the other is a descendant
     * @param queryBNF The BNF to check if it is a descendant of the parent
     * @param bypassCompound Indicates whether a {@link JPQLQueryBNF} representing a compound
     * expression should be considered when doing the validation
     * @return <code>true</code> if the {@link JPQLQueryBNF} is a descendant of the given parent
     * {@link JPQLQueryBNF}; <code>false</code> otherwise
     */
    protected boolean isValid(JPQLQueryBNF parentQueryBNF, JPQLQueryBNF queryBNF, boolean bypassCompound) {
        JPQLQueryBNFValidator validator = buildJPQLQueryBNFValidator(parentQueryBNF);
        try {
            validator.setBypassCompound(bypassCompound);
            validator.validate(queryBNF);
            return validator.isValid();
        }
        finally {
            validator.dispose();
        }
    }

    /**
     * Determines whether the given {@link JPQLQueryBNF} part is the given parent {@link JPQLQueryBNF}.
     *
     * @param parentQueryBNF The "root" of the BNF used to determine if the other is a descendant
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} to check if it is a
     * descendant of the parent
     * @param bypassCompound Indicates whether a {@link JPQLQueryBNF} representing a compound
     * expression should be considered when doing the validation
     * @return <code>true</code> if the {@link JPQLQueryBNF} is a descendant of the given parent
     * {@link JPQLQueryBNF}; <code>false</code> otherwise
     */
    protected boolean isValid(JPQLQueryBNF parentQueryBNF, String queryBNFId, boolean bypassCompound) {
        return isValid(parentQueryBNF, getQueryBNF(queryBNFId), bypassCompound);
    }

    /**
     * Determines whether the given proposal is a valid, which is based on the content of the given
     * word. If the word is not an empty string, the proposal must start with the content of the word.
     *
     * @param proposal The proposal to validate
     * @param word The word, which is what was parsed before the position of the cursor
     * @return <code>true</code> if the proposal is valid; <code>false</code> otherwise
     */
    protected boolean isValidProposal(String proposal, String word) {

        // There is no word to match the first letters
        if (word.length() == 0) {
            return true;
        }

        char character = word.charAt(0);

        if (character == '+' ||
            character == '-' ||
            character == '*' ||
            character == '/') {

            return true;
        }

        // The word is longer than the proposal
        if (word.length() > proposal.length()) {
            return false;
        }

        // Check to see if the proposal starts with the word
        for (int index = 0, length = word.length(); index < length; index++) {

            char character1 = proposal.charAt(index);
            char character2 = word  .charAt(index);

            // If characters don't match but case may be ignored, try converting
            // both characters to uppercase. If the results match, then the
            // comparison scan should continue
            char upperCase1 = Character.toUpperCase(character1);
            char upperCase2 = Character.toUpperCase(character2);

            if (upperCase1 != upperCase2) {
                return false;
            }

            // Unfortunately, conversion to uppercase does not work properly for
            // the Georgian alphabet, which has strange rules about case
            // conversion. So we need to make one last check before exiting
            if (Character.toLowerCase(upperCase1) != Character.toLowerCase(upperCase2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether the given JPQL identifier can be a valid proposal, i.e. if it's part of the
     * grammar of the JPA version that was used to parse the JPQL query.
     *
     * @param identifier The JPQL identifier to validate
     * @return <code>true</code> if the given identifier is part of the current JPA version or was
     * defined in previous release; <code>false</code> otherwise
     */
    protected boolean isValidVersion(String identifier) {
        JPAVersion identifierVersion = getLatestGrammar().getExpressionRegistry().getIdentifierVersion(identifier);
        return queryContext.getJPAVersion().isNewerThanOrEqual(identifierVersion);
    }

    /**
     * Determines whether the given {@link Expression} is part of an invalid fragment
     *
     * @param expression The {@link Expression} to verify its location within the JPQL query
     * @return <code>true</code> if the given {@link Expression} is within an invalid fragment;
     * <code>false</code> if it is not
     */
    protected boolean isWithinInvalidExpression(Expression expression) {
        WithinInvalidExpressionVisitor validator = getWithinInvalidExpressionVisitor();
        try {
            expression.accept(validator);
            return validator.isWithinInvalidExpression();
        }
        finally {
            validator.dispose();
        }
    }

    /**
     * Registers the given helper associated with the given helper class.
     *
     * @param helperClass The Java class of the helper to retrieve
     * @param helper The helper being registered
     * @see #getHelper(Class)
     */
    protected final <T> void registerHelper(Class<T> helperClass, T helper) {
        helpers.put(helperClass, helper);
    }

    /**
     * Removes the last virtual space from the stack.
     */
    protected final void removeVirtualSpace() {
        virtualSpaces.pop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return proposals.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AbsExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AbstractSchemaName expression) {

        // First visit with the adjustment
        corrections.add(queryPosition.getPosition(expression));
        super.visit(expression);
        corrections.pop();

        // Now visit without the adjustment
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AdditionExpression expression) {
        super.visit(expression);
        visitArithmeticExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AllOrAnyExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.NONE, ALL, ANY, SOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AndExpression expression) {
        super.visit(expression);
        visitLogicalExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ArithmeticFactor expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 1;

        if (position == length) {
            addIdentificationVariables();
            addFunctionIdentifiers(ArithmeticPrimaryBNF.ID);
        }
        else if (expression.hasSpaceAfterArithmeticOperator() || hasVirtualSpace()) {
            length++;

            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(ArithmeticPrimaryBNF.ID);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AvgFunction expression) {
        super.visit(expression);
        visitAggregateFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BadExpression expression) {
        super.visit(expression);
        visitInvalidExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BetweenExpression expression) {
        super.visit(expression);

        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        if (expression.hasExpression()) {
            length += expression.getExpression().getLength() + SPACE_LENGTH;
        }

        // Within "NOT BETWEEN" or "BETWEEN"
        if (expression.hasNot() && isPositionWithin(position, length, NOT_BETWEEN) ||
           !expression.hasNot() && isPositionWithin(position, length, BETWEEN)) {

            proposals.addIdentifier(BETWEEN);
            proposals.addIdentifier(NOT_BETWEEN);
        }
        // After "BETWEEN "
        else if (expression.hasSpaceAfterBetween()) {
            length += expression.getIdentifier().length() + SPACE_LENGTH;

            // TODO: Check for the BETWEEN's expression type
            // Right after "BETWEEN "
            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(expression.getBoundExpressionQueryBNFId());
            }

            // After lower bound
            if (expression.hasLowerBoundExpression()) {

                // Check for something like "<lower bound> <word>"
                int lowerBoundLength = expression.getLowerBoundExpression().getLength();

                if (!expression.hasAnd() &&
                    (position > length) && (position < length + lowerBoundLength) &&
                    isAppendableToCollection(expression.getLowerBoundExpression())) {

                    addIdentifier(AND);
                }

                length += lowerBoundLength;

                if (expression.hasSpaceAfterLowerBound()) {
                    length++;

                    // Right before "AND"
                    if (position == length) {
                        proposals.addIdentifier(AND);
                    }
                    else {
                        // Within "AND"
                        if (expression.hasAnd() && isPositionWithin(position, length, AND)) {
                            proposals.addIdentifier(AND);
                        }
                        // After "AND "
                        else if (expression.hasSpaceAfterAnd()) {
                            length += AND.length() + SPACE_LENGTH;

                            // TODO: Check for the BETWEEN's expression type
                            if (position == length) {
                                addIdentificationVariables();
                                addFunctionIdentifiers(InternalBetweenExpressionBNF.ID);
                            }
                        }
                        else if (!expression.hasAnd() &&
                                  expression.hasUpperBoundExpression()) {

                            length += expression.getUpperBoundExpression().getLength();

                            if (position == length) {
                                addIdentifier(AND);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CaseExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within "CASE"
        if (isPositionWithin(position, CASE)) {
            addIdentifier(CASE);
            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
        // After "CASE "
        else if (expression.hasSpaceAfterCase()) {
            int length = CASE.length() + SPACE_LENGTH;

            // Right after "CASE "
            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(CaseOperandBNF.ID);
                proposals.addIdentifier(WHEN);
            }

            // After "<case operand> "
            if (expression.hasCaseOperand() &&
                expression.hasSpaceAfterCaseOperand()) {

                length += expression.getCaseOperand().getLength() + SPACE_LENGTH;

                // Right after "<case operand> "
                if (position == length) {
                    proposals.addIdentifier(WHEN);
                }
            }

            // After "<when clauses> "
            if (expression.hasWhenClauses() &&
                expression.hasSpaceAfterWhenClauses()) {

                length += expression.getWhenClauses().getLength() + SPACE_LENGTH;

                // Right after "<when clauses> "
                if (isPositionWithin(position, length, ELSE)) {
                    proposals.addIdentifier(ELSE);
                }

                // After "ELSE "
                if (expression.hasElse() &&
                    expression.hasSpaceAfterElse()) {

                    length += ELSE.length() + SPACE_LENGTH;

                    // Right after "ELSE "
                    if (position == length) {
                        addIdentificationVariables();
                        addFunctionIdentifiers(ScalarExpressionBNF.ID);
                    }

                    // After "<else expression> "
                    if (expression.hasElseExpression() &&
                        expression.hasSpaceAfterElseExpression()) {

                        length += expression.getElseExpression().getLength() + SPACE_LENGTH;

                        // Right after "<else expression> "
                        if (isPositionWithin(position, length, END)) {
                            proposals.addIdentifier(END);
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CoalesceExpression expression) {
        super.visit(expression);

        if (isFollowingInvalidExpression(expression)) {
            return;
        }

        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within the identifier
        if (isPositionWithin(position, COALESCE)) {
            addIdentifier(COALESCE);
            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
        // Right after "<identifier>("
        else if (expression.hasLeftParenthesis()) {
            int length = COALESCE.length() + 1 /* '(' */;

            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(expression.getEncapsulatedExpressionQueryBNFId());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionExpression expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionMemberDeclaration expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within "IN"
        if (isPositionWithin(position, IN)) {

            if (!isWithinInvalidExpression(expression)) {
                proposals.addIdentifier(IN);
            }

            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }

        // In a subquery only
        // After "IN "
        if (isInSubquery(expression) && expression.hasSpaceAfterIn()) {
            int length = IN.length() + SPACE_LENGTH;

            // Right after "IN "
            if (position == length) {
                // TODO: Type.SuperQueryIdentificationVariable
                addLeftIdentificationVariables(expression);
            }
        }
        // In a top-level query or subquery
        // After "IN("
        else if (expression.hasLeftParenthesis()) {
            int length = IN.length() + 1 /* '(' */;

            // Right after "IN("
            if (position == length) {
                addLeftIdentificationVariables(expression);
                addFunctionIdentifiers(CollectionValuedPathExpressionBNF.ID);
            }

            // After "<collection-valued path expression>)"
            if (expression.hasRightParenthesis()) {
                length += expression.getCollectionValuedPathExpression().getLength() + 1 /* ')' */;

                // Right after "<collection-valued path expression>)"
                if ((position == length) && !expression.hasSpaceAfterRightParenthesis()) {
                    proposals.addIdentifier(AS);
                }

                if (expression.hasSpaceAfterRightParenthesis()) {
                    length++;
                }

                // Within "AS"
                if (isPositionWithin(position, length, AS)) {
                    proposals.addIdentifier(AS);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionMemberExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        String identifier = expression.getIdentifier();
        int length = 0;

        if (expression.hasEntityExpression()) {
            length = expression.getEntityExpression().getLength() + SPACE_LENGTH;
        }

        // Within the <identifier>
        if (isPositionWithin(position, length, identifier)) {
            proposals.addIdentifier(NOT_MEMBER);
            proposals.addIdentifier(NOT_MEMBER_OF);
            proposals.addIdentifier(MEMBER);
            proposals.addIdentifier(MEMBER_OF);
        }
        // After the <identifier>
        else if (expression.hasOf() && expression.hasSpaceAfterOf() ||
                !expression.hasOf() && expression.hasSpaceAfterMember()) {

            length += identifier.length() + SPACE_LENGTH;

            // Right after the <identifier>
            if (position == length) {
                if (!expression.hasOf()) {
                    addIdentifier(OF);
                }
                addIdentificationVariables();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionValuedPathExpression expression) {
        super.visit(expression);

        if (!isFollowingInvalidExpression(expression)) {

            visitPathExpression(expression);

            if (isDeclaration(expression)) {
                proposals.setClassNamePrefix(word, ClassType.INSTANTIABLE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ComparisonExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        if (expression.hasLeftExpression()) {
            length += expression.getLeftExpression().getLength() + SPACE_LENGTH;
        }

        // Within the comparison operator
        if (isPositionWithin(position, length, expression.getComparisonOperator())) {
            addExpressionFactoryIdentifiers(ComparisonExpressionFactory.ID);
        }

        // After the comparison operator
        length += expression.getComparisonOperator().length();

        if (expression.hasSpaceAfterIdentifier()) {
            length++;
        }

        // Right after the comparison operator
        if (position == length) {
            addIdentificationVariables();
            addFunctionIdentifiers(expression.getRightExpressionQueryBNFId());
            addClauseIdentifiers(expression.getRightExpressionQueryBNFId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConcatExpression expression) {
        super.visit(expression);
        visitCollectionExpression(expression, CONCAT, getConcatExpressionCollectionHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConstructorExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // NEW
        if (isPositionWithin(position, NEW)) {
            proposals.addIdentifier(NEW);
        }
        // After "NEW "
        else if (expression.hasSpaceAfterNew()) {
            int length = NEW.length() + SPACE_LENGTH;
            String className = expression.getClassName();
            int classNameLength = className.length();

            // Right after "NEW " or within the fully qualified class name
            if ((position >= length) && (position <= length + classNameLength)) {
                proposals.setClassNamePrefix(className.substring(0, position - length), ClassType.INSTANTIABLE);
            }
            // After "("
            else if (expression.hasLeftParenthesis()) {
                length += classNameLength + SPACE_LENGTH;

                // Right after "("
                if (position == length) {
                    addIdentificationVariables();
                    addFunctionIdentifiers(ConstructorItemBNF.ID);
                }
                else {
                    visitCollectionExpression(expression, NEW, getConstructorCollectionHelper());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CountFunction expression) {
        super.visit(expression);
        visitAggregateFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DateTime expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within the identifier
        if (expression.isCurrentDate()      && isPositionWithin(position, CURRENT_DATE) ||
            expression.isCurrentTime()      && isPositionWithin(position, CURRENT_TIME) ||
            expression.isCurrentTimestamp() && isPositionWithin(position, CURRENT_TIMESTAMP)) {

            proposals.addIdentifier(CURRENT_DATE);
            proposals.addIdentifier(CURRENT_TIME);
            proposals.addIdentifier(CURRENT_TIMESTAMP);

            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, DELETE_FROM, getDeleteClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteStatement expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitStatement(expression, getDeleteClauseStatementHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DivisionExpression expression) {
        super.visit(expression);
        visitArithmeticExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EmptyCollectionComparisonExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        if (expression.hasExpression()) {
            length = expression.getExpression().getLength() + SPACE_LENGTH;
        }

        // Within the <identifier>
        if (isPositionWithin(position, length, expression.getIdentifier())) {
            proposals.addIdentifier(IS_EMPTY);
            proposals.addIdentifier(IS_NOT_EMPTY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EntityTypeLiteral expression) {

        // Adjust the position to be the "beginning" of the expression by adding a "correction"
        corrections.add(queryPosition.getPosition(expression));
        super.visit(expression);
        corrections.pop();

        // Add the possible abstract schema names
        addEntities();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EntryExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.COLLECTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExistsExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.NONE, EXISTS, NOT_EXISTS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void visit(Expression expression) {
        expression.getParent().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FromClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, FROM, getFromClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FunctionExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(GroupByClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, GROUP_BY, getGroupByClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HavingClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, expression.getIdentifier(), getConditionalClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariable expression) {

        // First visit with the adjustment
        corrections.add(queryPosition.getPosition(expression));
        super.visit(expression);
        corrections.pop();

        // Now visit without the adjustment
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariableDeclaration expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        if ((position > 0) && (expression.hasSpace() || hasVirtualSpace())) {

            Expression rangeVariableDeclaration = expression.getRangeVariableDeclaration();
            int length = rangeVariableDeclaration.getLength() + SPACE_LENGTH;

            if (position == length) {
                if (isComplete(rangeVariableDeclaration)) {
                    addJoinIdentifiers();
                }
            }
            else {
                visitCollectionExpression(expression, ExpressionTools.EMPTY_STRING, getJoinCollectionHelper());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IndexExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(InExpression expression) {
        expression.accept(getVisitParentVisitor());
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        if (expression.hasExpression()) {
            length = expression.getExpression().getLength() + SPACE_LENGTH;
        }

        // Within "IN"
        if (isPositionWithin(position, length, expression.getIdentifier())) {

            // Make sure there is an expression left of 'IN'
            if (length > 2) {
                proposals.addIdentifier(IN);
                proposals.addIdentifier(NOT_IN);
            }

            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
        // After "IN("
        else if (expression.hasLeftParenthesis()) {
            length += expression.getIdentifier().length() + SPACE_LENGTH;

            // Right after "IN("
            if (position == length) {
                addFunctionIdentifiers(InExpressionItemBNF.ID);
                proposals.addIdentifier(SELECT);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(InputParameter expression) {
        // No content assist can be provider for an input parameter
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Join expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        String identifier = expression.getIdentifier();
        boolean joinFetch = expression.hasFetch();

        // Within "<join>"
        if (isPositionWithin(position, identifier)) {

            // Add JOIN identifiers
            proposals.addIdentifier(JOIN);
            proposals.addIdentifier(INNER_JOIN);
            proposals.addIdentifier(LEFT_JOIN);
            proposals.addIdentifier(LEFT_OUTER_JOIN);

            // Add JOIN FETCH identifiers if allowed or
            // if there is no 'AS identification_variable'
            if (isJoinFetchIdentifiable() ||
               !expression.hasAs() && !expression.hasIdentificationVariable()) {

                proposals.addIdentifier(JOIN_FETCH);
                proposals.addIdentifier(INNER_JOIN_FETCH);
                proposals.addIdentifier(LEFT_JOIN_FETCH);
                proposals.addIdentifier(LEFT_OUTER_JOIN_FETCH);
            }
        }
        // After "<join> "
        else if (expression.hasSpaceAfterJoin()) {
            int length = identifier.length() + SPACE_LENGTH;

            // Right after "<join> "
            if (position == length) {

                // Only add some JOIN identifiers if the actual identifier is shorter or incomplete
                if (identifier == LEFT) {
                    addIdentifier(LEFT_JOIN);
                    addIdentifier(LEFT_OUTER_JOIN);

                    if (isJoinFetchIdentifiable() ||
                        !expression.hasAs() && !expression.hasIdentificationVariable()) {

                        addIdentifier(LEFT_JOIN_FETCH);
                        addIdentifier(LEFT_OUTER_JOIN_FETCH);
                    }
                }
                else if (identifier == INNER) {
                    addIdentifier(INNER_JOIN);

                    if (isJoinFetchIdentifiable() ||
                        !expression.hasAs() && !expression.hasIdentificationVariable()) {

                        addIdentifier(INNER_JOIN_FETCH);
                    }
                }
                else if (identifier.equals("LEFT_OUTER")) {
                    addIdentifier(LEFT_OUTER_JOIN);

                    if (isJoinFetchIdentifiable() ||
                        !expression.hasAs() && !expression.hasIdentificationVariable()) {

                        addIdentifier(LEFT_OUTER_JOIN_FETCH);
                    }
                }
                else {
                    addLeftIdentificationVariables(expression);
                }
            }

            // After "join association path expression "
            if (expression.hasJoinAssociationPath() &&
                expression.hasSpaceAfterJoinAssociation()) {

                length += expression.getJoinAssociationPath().getLength() + SPACE_LENGTH;

                // Right after "join association path expression "
                // Make sure to verify if AS can be added if it's a JOIN FETCH expression
                if (!joinFetch || joinFetch && isJoinFetchIdentifiable()) {

                    if (isPositionWithin(position, length, AS)) {
                        addIdentifier(AS);
                    }

                    if (expression.hasAs()) {
                        length += 2;

                        if (expression.hasSpaceAfterAs()) {
                            length++;
                        }
                    }
                }

                if (joinFetch || expression.hasIdentificationVariable()) {

                    length += expression.getIdentificationVariable().getLength();

                    if (expression.hasSpaceAfterIdentificationVariable()) {
                        length += SPACE_LENGTH;
                    }

                    // After "join association path expression [AS] identification variable"
                    if (position == length) {
                        addIdentifier(ON);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(JPQLExpression expression) {

        int position = queryPosition.getPosition(expression) - corrections.peek();

        // At the beginning of the query
        if (position == 0) {
            addIdentifier(DELETE_FROM);
            addIdentifier(SELECT);
            addIdentifier(UPDATE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(KeyExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.LEFT_COLLECTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(KeywordExpression expression) {
        super.visit(expression);

        int position = queryPosition.getPosition(expression) - corrections.peek();
        String keyword = expression.getText();

        // Within the identifier
        if ((keyword == TRUE)  && isPositionWithin(position, TRUE)  ||
            (keyword == FALSE) && isPositionWithin(position, FALSE) ||
            (keyword == NULL)  && isPositionWithin(position, NULL)) {

            proposals.addIdentifier(TRUE);
            proposals.addIdentifier(FALSE);
            proposals.addIdentifier(NULL);

            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LengthExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LikeExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        if (expression.hasStringExpression()) {
            int length = expression.getStringExpression().getLength() + SPACE_LENGTH;

            // Within "LIKE" or "NOT LIKE"
            if (isPositionWithin(position, length, expression.getIdentifier())) {
                proposals.addIdentifier(LIKE);
                proposals.addIdentifier(NOT_LIKE);
            }
            // After "LIKE " or "NOT LIKE "
            else if (expression.hasSpaceAfterLike()) {
                length += expression.getIdentifier().length() + SPACE_LENGTH;

                // After "<pattern value> "
                if (expression.hasPatternValue() &&
                    expression.hasSpaceAfterPatternValue()) {

                    length += expression.getPatternValue().getLength() + SPACE_LENGTH;

                    // Within "ESCAPE"
                    if (isPositionWithin(position, length, ESCAPE)) {
                        proposals.addIdentifier(ESCAPE);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LocateExpression expression) {
        super.visit(expression);
        visitCollectionExpression(expression, LOCATE, getTripleEncapsulatedCollectionHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LowerExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MaxFunction expression) {
        super.visit(expression);
        visitAggregateFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MinFunction expression) {
        super.visit(expression);
        visitAggregateFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ModExpression expression) {
        super.visit(expression);
        visitCollectionExpression(expression, MOD, getDoubleEncapsulatedCollectionHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MultiplicationExpression expression) {
        super.visit(expression);
        visitArithmeticExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NotExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within "NOT"
        if (isPositionWithin(position, NOT)) {
            proposals.addIdentifier(NOT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullComparisonExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        if (expression.hasExpression()) {
            length += expression.getExpression().getLength() + SPACE_LENGTH;
        }

        // Within "IS NULL" or "IS NOT NULL"
        if (isPositionWithin(position, length, expression.getIdentifier())) {
            proposals.addIdentifier(IS_NULL);
            proposals.addIdentifier(IS_NOT_NULL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullExpression expression) {
        // No content assist can be provider
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullIfExpression expression) {
        super.visit(expression);
        visitCollectionExpression(expression, NULLIF, getDoubleEncapsulatedCollectionHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NumericLiteral expression) {
        // No content assist can be provider for a numerical value
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ObjectExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OnClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, expression.getIdentifier(), getConditionalClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderByClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, ORDER_BY, getOrderByClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderByItem expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // After the order by item
        if (expression.hasExpression()) {
            int length = expression.getExpression().getLength();

            // Within the expression
            if ((position > -1) && (position <= length)) {

                if (!expression.hasNulls() &&
                    !expression.hasOrdering()) {

                    addIdentifier(ASC);
                    addIdentifier(DESC);
                }
            }
            // After the expression
            else if (expression.hasSpaceAfterExpression()) {
                length++;

                // Right before "ASC" or "DESC"
                if (position == length) {
                    proposals.addIdentifier(ASC);
                    proposals.addIdentifier(DESC);
                }
                else {
                    // Right after the space
                    Ordering ordering = expression.getOrdering();

                    // Within "ASC" or "DESC"
                    if ((ordering != Ordering.DEFAULT) &&
                        isPositionWithin(position, length, ordering.name())) {

                        proposals.addIdentifier(ASC);
                        proposals.addIdentifier(DESC);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrExpression expression) {
        super.visit(expression);
        visitLogicalExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RangeVariableDeclaration expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // After "<abstract schema name> "
        if (expression.hasRootObject()) {
            int length = expression.getRootObject().getLength();

            // After "<abstract schema name> "
            if (expression.hasSpaceAfterRootObject()) {
                length++;

                // Right after "<abstract schema name> "
                if (position == length) {
                    addIdentifier(AS);
                }
                // Within 'AS'
                else if (expression.hasAs() && isPositionWithin(position, length, AS)) {
                    addIdentifier(AS);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ResultVariable expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        if (expression.hasSelectExpression()) {
            Expression selectItem = expression.getSelectExpression();
            int length = selectItem.getLength() + SPACE_LENGTH;

            // Right before "AS"
            if (position == length) {

                if (isComplete(selectItem)) {
                    addIdentifier(AS);
                }

                if (areArithmeticSymbolsAppendable(selectItem)) {
                    addArithmeticIdentifiers();
                }
            }
            // Within "AS"
            else if (expression.hasAs() && isPositionWithin(position, length, AS)) {

                if (isComplete(selectItem)) {
                    addIdentifier(AS);
                }

                addFunctionIdentifiers(selectItem);
            }
        }
        // Now add other functions as well, example " A|S e" could become "AVG e"
        else if (expression.hasAs() && isPositionWithin(position, AS)) {
            addFunctionIdentifiers(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, SELECT, getSelectClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectStatement expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitStatement(expression, getSelectClauseStatementHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleFromClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, FROM, getFromClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleSelectClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, SELECT, getSimpleSelectClauseCollectionHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleSelectStatement expression) {
        if (!isLocked(expression)) {
            // Don't continue traversing the parent hierarchy because a subquery
            // will handle all the possible proposals
            visitStatement(expression, getSimpleSelectClauseStatementHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SizeExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SqrtExpression expression) {
        expression.accept(getVisitParentVisitor());
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StateFieldPathExpression expression) {
        super.visit(expression);
        if (!isFollowingInvalidExpression(expression)) {
            visitPathExpression(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StringLiteral expression) {
        // No content assist required
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        if (position == 1) {
            addFunctionIdentifiers(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubstringExpression expression) {
        super.visit(expression);
        visitCollectionExpression(expression, SUBSTRING, getTripleEncapsulatedCollectionHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubtractionExpression expression) {
        super.visit(expression);
        visitArithmeticExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SumFunction expression) {
        super.visit(expression);
        visitAggregateFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TreatExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within "TREAT"
        if (isPositionWithin(position, TREAT)) {
            addIdentifier(TREAT);
            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
        // After "TREAT("
        else if (expression.hasLeftParenthesis()) {
            int length = TREAT.length() + 1;

            // Right after "TREAT("
            if (position == length) {
                addLeftIdentificationVariables(expression);
            }

            // After "<collection-valued path expression> "
            if (expression.hasCollectionValuedPathExpression() &&
                expression.hasSpaceAfterCollectionValuedPathExpression()) {

                Expression collectionValuedPathExpression = expression.getCollectionValuedPathExpression();
                length += collectionValuedPathExpression.getLength() + SPACE_LENGTH;

                // Within "AS"
                if (isPositionWithin(position, length, AS)) {
                    addIdentifier(AS);

                    // If the entity type is not specified, then we can add
                    // the possible abstract schema names
                    if (!expression.hasEntityType()) {

                        // If the type of the path expression is resolvable,
                        // then filter the abstract schema types
                        IType type = queryContext.getType(collectionValuedPathExpression);

                        if (type.isResolvable()) {
                            addEntities(type);
                        }
                        else {
                            addEntities();
                        }
                    }
                }
            }

            // After "AS "
            if (expression.hasAs() &&
                expression.hasSpaceAfterAs()) {

                length += AS.length() + SPACE_LENGTH;

                // Right after "AS "
                if (position == length) {
                    // If the type of the path expression is resolvable,
                    // then filter the abstract schema types
                    IType type = queryContext.getType(expression.getCollectionValuedPathExpression());

                    if (type.isResolvable()) {
                        addEntities(type);
                    }
                    else {
                        addEntities();
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TrimExpression expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        // Within "TRIM"
        if (isPositionWithin(position, TRIM)) {
            addIdentifier(TRIM);
            addIdentificationVariables();
            addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }
        // After "TRIM("
        else if (expression.hasLeftParenthesis()) {
            length += TRIM.length() + 1;

            // Right after "TRIM("
            if (position == length) {
                addIdentifier(BOTH);
                addIdentifier(LEADING);
                addIdentifier(TRAILING);

                if (!expression.hasTrimCharacter() &&
                    !expression.hasFrom()) {

                    addIdentificationVariables();
                    addFunctionIdentifiers(StringPrimaryBNF.ID);
                }
            }

            // Within the trim specification
            if (expression.hasSpecification()) {
                String specification = expression.getSpecification().name();

                if (isPositionWithin(position, length, specification)) {
                    addIdentifier(BOTH);
                    addIdentifier(LEADING);
                    addIdentifier(TRAILING);

                    if (!expression.hasTrimCharacter() &&
                        !expression.hasFrom()) {

                        addIdentificationVariables();
                        addFunctionIdentifiers(StringPrimaryBNF.ID);
                    }
                }

                length += specification.length();
            }

            if (expression.hasSpaceAfterSpecification()) {
                length += SPACE_LENGTH;
            }

            // Trim character
            if (expression.hasTrimCharacter()) {
                length += expression.getTrimCharacter().getLength();
            }

            if (expression.hasSpaceAfterTrimCharacter()) {
                length += SPACE_LENGTH;
            }

            // Right after "<trim_character> "
            if (position == length) {
                addIdentifier(FROM);

                if (!expression.hasFrom()) {
                    addIdentificationVariables();
                    addFunctionIdentifiers(StringPrimaryBNF.ID);
                }
            }

            if (expression.hasFrom()) {

                // Within "FROM"
                if (isPositionWithin(position, length, FROM)) {
                    addIdentifier(FROM);
                }

                length += FROM.length();
            }

            if (expression.hasSpaceAfterFrom()) {
                length += SPACE_LENGTH;
            }

            // Right after "FROM "
            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(StringPrimaryBNF.ID);
            }

            // Right after the string literal but there is no trim character,
            // nor FROM and there is a virtual space
            if (expression.hasExpression()) {
                length += expression.getExpression().getLength();

                if ((position == length + virtualSpaces.peek()) &&
                    !expression.hasTrimCharacter() &&
                    !expression.hasFrom()) {

                    addIdentifier(FROM);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TypeExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnknownExpression expression) {
        super.visit(expression);
        visitInvalidExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateClause expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within "UPDATE"
        if (isPositionWithin(position, UPDATE)) {
            proposals.addIdentifier(UPDATE);
        }
        // After "UPDATE "
        else if (expression.hasSpaceAfterUpdate()) {
            int length = UPDATE.length() + SPACE_LENGTH;

            // Right after "UPDATE "
            if (position == length) {
                addEntities();
            }
            // After "<range variable declaration> "
            else if (expression.hasRangeVariableDeclaration()) {

                RangeVariableDeclaration rangeVariableDeclaration = findRangeVariableDeclaration(expression);

                if ((rangeVariableDeclaration != null) &&
                     rangeVariableDeclaration.hasRootObject() &&
                     rangeVariableDeclaration.hasSpaceAfterRootObject()) {

                    length += rangeVariableDeclaration.getRootObject().getLength() + SPACE_LENGTH;

                    // Example: "UPDATE System s"
                    if (!expression.hasSet()        &&
                        !rangeVariableDeclaration.hasAs() &&
                        isPositionWithin(position, length, SET)) {

                        addIdentifier(SET);
                    }
                    // Example: "UPDATE System s "
                    // Example: "UPDATE System AS s "
                    else {

                        if (rangeVariableDeclaration.hasAs()) {
                            length += 2;
                        }

                        if (rangeVariableDeclaration.hasSpaceAfterAs()) {
                            length++;
                        }

                        if (rangeVariableDeclaration.hasIdentificationVariable()) {
                            length += rangeVariableDeclaration.getIdentificationVariable().getLength();
                        }

                        if (expression.hasSpaceAfterRangeVariableDeclaration()) {
                            length++;
                        }

                        // Within "SET"
                        if ((rangeVariableDeclaration.hasAs() && rangeVariableDeclaration.hasIdentificationVariable() ||
                            !rangeVariableDeclaration.hasAs() && rangeVariableDeclaration.hasIdentificationVariable()) &&
                            isPositionWithin(position, length, SET)) {

                            addIdentifier(SET);
                        }
                        // After "SET "
                        else if (expression.hasSet() &&
                                 expression.hasSpaceAfterSet()) {

                            length += SET.length() + SPACE_LENGTH;

                            // Right after "SET "
                            if (position == length) {
                                addIdentificationVariables();
                            }
                            // Within the new value expressions
                            else {
                                visitCollectionExpression(expression, ExpressionTools.EMPTY_STRING, getUpdateItemCollectionHelper());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateItem expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        // At the beginning
        if (position == length) {
            addIdentificationVariables();
        }
        else if (expression.hasStateFieldPathExpression() &&
                 expression.hasSpaceAfterStateFieldPathExpression()) {

            length += expression.getStateFieldPathExpression().getLength() + SPACE_LENGTH;

            // Within "="
            if (position == length) {
                proposals.addIdentifier(EQUAL);
            }
            // After "="
            else if (expression.hasEqualSign()) {
                length++;

                // Right after "="
                if (position == length) {
                    proposals.addIdentifier(EQUAL);
                    addIdentificationVariables();
                    addFunctionIdentifiers(NewValueBNF.ID);
                }
                else if (expression.hasSpaceAfterEqualSign()) {
                    length++;

                    // Right after "= "
                    if (position == length) {
                        addIdentificationVariables();
                        addFunctionIdentifiers(NewValueBNF.ID);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateStatement expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitStatement(expression, getUpdateClauseStatementHelper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpperExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ValueExpression expression) {
        super.visit(expression);
        visitSingleEncapsulatedExpression(expression, IdentificationVariableType.LEFT_COLLECTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(WhenClause expression) {
        super.visit(expression);
        int position = queryPosition.getPosition(expression) - corrections.peek();

        // Within "WHEN"
        if (isPositionWithin(position, WHEN)) {
            if (!isWithinInvalidExpression(expression)) {
                proposals.addIdentifier(WHEN);
            }
        }
        // After "WHEN "
        else if (expression.hasSpaceAfterWhen()) {
            int length = 5 /* WHEN + space */;

            // Right after "WHEN "
            if (position == length) {
                visitCollectionExpression(expression, WHEN, getWhenClauseConditionalClauseCollectionHelper());
            }
            else {
                length += expression.getWhenExpression().getLength();

                // After "WHEN <expression> " => THEN
                if (expression.hasSpaceAfterWhenExpression()) {
                    length++;

                    // Right after "WHEN <expression> " => THEN
                    if (position == length) {

                        // Check an incomplete WHEN expression
                        visitCollectionExpression(expression, WHEN, getWhenClauseConditionalClauseCollectionHelper());

                        // If the expression is complete, then "THEN" is a valid proposal
                        if (isComplete(expression)) {
                            proposals.addIdentifier(THEN);
                        }
                    }
                    else if (expression.hasThen()) {

                        // Within "THEN"
                        if (isPositionWithin(position, length, THEN)) {
                            proposals.addIdentifier(THEN);
                        }
                        else {
                            length += 4 /* THEN */;

                            // After "WHEN <expression> THEN "
                            if (expression.hasSpaceAfterThen()) {
                                length++;

                                // Right after "WHEN <expression> THEN "
                                if (position == length) {
                                    addIdentificationVariables();
                                    addFunctionIdentifiers(ScalarExpressionBNF.ID);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(WhereClause expression) {
        if (!isLocked(expression)) {
            super.visit(expression);
            visitCollectionExpression(expression, expression.getIdentifier(), getConditionalClauseCollectionHelper());
        }
    }

    /**
     * Visits the given {@link AggregateFunction} and attempts to find valid proposals.
     *
     * @param expression The {@link AggregateFunction} to inspect
     */
    protected void visitAggregateFunction(AggregateFunction expression) {

        if (isFollowingInvalidExpression(expression)) {
            return;
        }

        int position = queryPosition.getPosition(expression) - corrections.peek();
        String identifier = expression.getIdentifier();

        // Within "<identifier>"
        if (isPositionWithin(position, identifier)) {
            addIdentifier(identifier);
            addFunctionIdentifiers(expression);
        }
        // After "<identifier>("
        else if (expression.hasLeftParenthesis()) {
            int length = identifier.length() + 1 /* '(' */;
            boolean hasDistinct = expression.hasDistinct();

            // Within "DISTINCT"
            if (hasDistinct && isPositionWithin(position, length, DISTINCT) ) {
                addIdentifier(DISTINCT);
            }
            // After "("
            else {
                if (hasDistinct && expression.hasSpaceAfterDistinct()) {
                    length += DISTINCT.length() + SPACE_LENGTH;
                }

                // Right after "(" or right after "(DISTINCT "
                if (position == length) {
                    if (!hasDistinct) {
                        addIdentifier(DISTINCT);
                    }
                    addIdentificationVariables();
                    addFunctionIdentifiers(expression.getEncapsulatedExpressionQueryBNFId());
                }
            }
        }
    }

    /**
     * Visits the given {@link ArithmeticExpression} and attempts to find valid proposals.
     *
     * @param expression The {@link ArithmeticExpression} to inspect
     */
    protected void visitArithmeticExpression(ArithmeticExpression expression) {

        int position = queryPosition.getPosition(expression) - corrections.peek();
        int length = 0;

        if (expression.hasLeftExpression()) {
            length += expression.getLeftExpression().getLength() + SPACE_LENGTH;
        }

        // Within the arithmetic sign
        if (isPositionWithin(position, length, PLUS)) {
            addAggregateIdentifiers(expression.getQueryBNF());
        }
        // After the arithmetic sign, with or without the space
        else if (expression.hasSpaceAfterIdentifier()) {
            length += 2;

            // Right after the space
            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(expression.getRightExpressionQueryBNFId());
            }
        }
    }

    /**
     * Adds the possible proposals for the given {@link Expression expression} based on the location of
     * the cursor and the content of the expression.
     *
     * @param expression The {@link Expression expression} being visited
     * @param identifier The JPQL identifier of the {@link Expression} being visited, if the {@link
     * Expression} does not have an identifier, than an empty string should be passed
     * @param helper This helper completes the behavior of this method by retrieving the information
     * from the given {@link Expression}
     */
    protected <T extends Expression> void visitCollectionExpression(T expression,
                                                                    String identifier,
                                                                    CollectionExpressionHelper<T> helper) {

        if (isFollowingInvalidExpression(expression)) {
            return;
        }

        int position = queryPosition.getPosition(expression) - corrections.peek();
        boolean hasIdentifier = (identifier.length() > 0);
        boolean virtualSpace = hasVirtualSpace();

        // Within the identifier
        if (hasIdentifier && isPositionWithin(position, identifier)) {
            helper.addIdentifier(expression, identifier);
        }
        // After "<identifier>(" or "<identifier> "
        else if (helper.hasDelimiterAfterIdentifier(expression)) {
            int length = !hasIdentifier ? 0 : identifier.length() + 1 /* delimiter, either space or ( */;
            length += helper.preExpressionLength(expression);

            // Right after "<identifier>(" or "<identifier> "
            if (position == length) {
                helper.addTheBeginningOfChild(expression, null, 0, false);
            }
            // Within the encapsulated expressions
            else {

                // Create a collection representation of the encapsulated expression(s)
                CollectionExpression collectionExpression = helper.buildCollectionExpression(expression);
                boolean hasComma = false;
                boolean previousHasComma = false;

                // Determine the maximum children count, it is possible the query contains more children
                // than the expession's grammar would actually allow. The content assist will only
                // provide assistance from the first child to the last allowed child
                int childrenCount = collectionExpression.childrenSize();
                int count = Math.min(childrenCount, helper.maxCollectionSize(expression));

                // Iterate through each child of the collection
                for (int index = 0; index < count; index++) {

                    Expression child = collectionExpression.getChild(index);
                    int childLength = 0;

                    // At the beginning of the child
                    if (position == length) {
                        helper.addTheBeginningOfChild(expression, collectionExpression, index, hasComma);
                        break;
                    }
                    // Each expression within the collection has to be separated by a comma, the previous
                    // expression and the expression at the current index are not separated by a comma
                    // Example: "SELECT e FROM Employee e GROUP" <- [ "Employee e", "GROUP" ]
                    else if ((index > 0) && !hasComma) {

                        length += child.getLength();

                        // At the end of the child
                        if (position == length) {
                            helper.addAtTheEndOfChild(expression, collectionExpression, index, hasComma, false);
                            break;
                        }

                        // To be valid, each child has to be separated by a comma,
                        // ask the helper if it should continue with the next child
                        if (!helper.canContinue(expression, collectionExpression, index)) {
                            break;
                        }
                        // Special case when reaching the end of the collection
                        else if (index + 1 == count) {
                            helper.addAtTheEndOfChild(expression, collectionExpression, index, hasComma, false);
                        }
                    }
                    else {
                        childLength = child.getLength();

                        // At the end of the child
                        if ((position == length + childLength) ||
                            (virtualSpace && (position == length + childLength + SPACE_LENGTH))) {

                            helper.addAtTheEndOfChild(expression, collectionExpression, index, hasComma, virtualSpace);
                            break;
                        }
                    }

                    // Now add the child's length and length used by the comma and space
                    length += childLength;

                    // Move after the comma
                    previousHasComma = hasComma;
                    hasComma = collectionExpression.hasComma(index);

                    if (hasComma) {

                        // Two items were not separated by a comma and the next one is, this is invalid
                        if ((index > 0) && !previousHasComma) {
                            return;
                        }

                        length++;

                        // After the comma, add the proposals
                        if (position == length) {
                            helper.addTheBeginningOfChild(expression, collectionExpression, index + 1, hasComma);
                            break;
                        }
                    }

                    // Move after the space that follows the comma
                    if (collectionExpression.hasSpace(index)) {
                        length++;

                        // At the end of the child
                        if (!hasComma && (position == length)) {
                            helper.addAtTheEndOfChild(expression, collectionExpression, index, hasComma, true);
                        }
                    }

                    // Nothing more can be looked at
                    if (position < length) {
                        break;
                    }
                }
            }
        }
    }

    protected void visitEndingExpression(Expression expression) {

        // Keep track of the original QueryPosition, the new one will differ
        // to properly accommodate for the invalid portion of the query
        QueryPosition oldQueryPosition = queryPosition;

        //
        // Step 1
        //
        // Create a new QueryPosition for which the positions is set to be at the end
        // of the valid fragment of the JPQL query, which is up to right before the
        // beginning of the invalid fragment
        // Example: "SELECT e FROM Employee e S| WHERE e.name = 'JPQL'" <- | is the position of the cursor
        //          Valid fragment: "SELECT e FROM Employee e "
        //          Invalid fragment: "S WHERE e.name = 'JPQL'"
        queryPosition = buildEndingPositionFromInvalidExpression(expression, expression, new boolean[1]);

        // Adjust the position to include the whitespace owned by the parent expression
        expression = queryPosition.getExpression();

        while (expression != null) {

            int position = queryPosition.getPosition(expression);

            if (position == -1) {
                expression = null;
            }
            else {
                queryPosition.addPosition(expression, position + 1);
                expression = expression.getParent();
            }
        }

        //
        // Step 2
        //
        // Now make sure the correction is reset temporarily
        corrections.add(0);
        addVirtualSpace();

        // Now traverse the tree starting at the leaf from the valid fragment
        queryPosition.getExpression().accept(this);

        // Revert the data
        queryPosition = oldQueryPosition;
        corrections.pop();
        removeVirtualSpace();
    }

    protected void visitEnumConstant(AbstractPathExpression expression) {

        int position = queryPosition.getPosition(expression);
        String text = expression.toActualText();
        int lastDotIndex = text.lastIndexOf(DOT);

        // Check to see if an enum constant can be used at the expression's location
        if (isEnumAllowed(expression)) {
            boolean enumConstant = false;

            // The position is after the last dot, check for enum constants
            if (position > lastDotIndex) {

                // Retrieve the enum type if the path up to the last dot is a fully qualified enum type
                String enumType = expression.toParsedText().substring(0, lastDotIndex);
                IType type = queryContext.getType(enumType);

                // The path expression before the last dot is an enum type
                if (type.isResolvable() && type.isEnum()) {
                    enumConstant = true;

                    // Now retrieve the portion of the enum constant based on the cursor position
                    String word = text.substring(lastDotIndex + 1, position);

                    // Add the enum constants and filter them based on what's already proposed
                    addEnumConstants(type, word);
                }
            }

            // Enum type
            if (!enumConstant) {

                // Now retrieve the portion of the enum constant based on the cursor position
                text = text.substring(0, position);

                // Set the possible starting of a fully qualified enum type
                proposals.setClassNamePrefix(text, ClassType.ENUM);
            }
        }
    }

    protected void visitInvalidExpression(Expression expression) {

        if (!isLocked(expression)) {
            int position = queryPosition.getPosition(expression) - corrections.peek();
            boolean virtualSpace = (position == 1) && (word.length() == 0);

            // 1. Within the first word of the invalid fragment
            // 2. Or after the ending whitespace
            // Otherwise no need to do anything if beyond it
            if (isPositionWithin(position, word) || virtualSpace) {

                lockedExpressions.add(expression);

                // Keep track of the original QueryPosition, the new one will differ
                // to properly accommodate for the invalid portion of the query
                QueryPosition oldQueryPosition = queryPosition;

                boolean[] spaces = { false };

                //
                // Step 1
                //
                // Create a new QueryPosition for which the positions is set to be at the end
                // of the valid fragment of the JPQL query, which is up to right before the
                // beginning of the invalid fragment
                // Example: "SELECT e FROM Employee e S| WHERE e.name = 'JPQL'" <- | is the position of the cursor
                //          Valid fragment: "SELECT e FROM Employee e "
                //          Invalid fragment: "S WHERE e.name = 'JPQL'"
                queryPosition = buildEndingPositionFromInvalidExpression(expression, expression.getRoot(), spaces);

                //
                // Step 2
                //
                // Now make sure the correction is reset temporarily
                corrections.add(0);

                // This means the valid fragment ends with a whitespace, keep track of it
                if (spaces[0] || virtualSpace) {
                    addVirtualSpace();
                }

                // Now traverse the tree starting at the leaf from the valid fragment
                queryPosition.getExpression().accept(this);

                // Revert the data
                queryPosition = oldQueryPosition;
                corrections.pop();
                lockedExpressions.pop();

                if (spaces[0] || virtualSpace) {
                    removeVirtualSpace();
                }
            }
        }
    }

    /**
     * Visits the given {@link LogicalExpression} and attempts to find valid proposals.
     *
     * @param expression The {@link LogicalExpression} to inspect
     */
    protected void visitLogicalExpression(LogicalExpression expression) {

        if (isFollowingInvalidExpression(expression)) {
            return;
        }

        int position = queryPosition.getPosition(expression) - corrections.peek();
        String identifier = expression.getIdentifier();
        int length = 0;

        if (expression.hasLeftExpression()) {
            length += expression.getLeftExpression().getLength() + SPACE_LENGTH;
        }

        // Within "AND" or "OR"
        if (isPositionWithin(position, length, identifier)) {
            proposals.addIdentifier(identifier);
        }
        // After "AND " or "OR "
        else if (expression.hasSpaceAfterIdentifier()) {
            length += identifier.length() + SPACE_LENGTH;

            // Right after "AND " or "OR "
            if (position == length) {
                addIdentificationVariables();
                addFunctionIdentifiers(expression.getRightExpressionQueryBNFId());
            }
        }
    }

    /**
     * Visits the given {@link AbstractPathExpression} and attempts to find valid proposals.
     * <p>
     * Note: A path expression can represent many things: state field, relationship field, collection
     * field, enum constant, etc. This will consider all variations.
     *
     * @param expression The {@link AbstractPathExpression} to inspect
     */
    protected void visitPathExpression(AbstractPathExpression expression) {

        int position = queryPosition.getPosition(expression);
        String text = expression.toActualText();
        int dotIndex = text.indexOf(DOT);

        if (position > -1) {

            String variableName = queryContext.literal(
                expression.getIdentificationVariable(),
                LiteralType.IDENTIFICATION_VARIABLE
            );

            boolean variable = ExpressionTools.stringIsNotEmpty(variableName);

            // The position if after the identification variable
            if ((dotIndex > -1) && (position > dotIndex)) {

                // Retrieve the filter based on the location of the state field path, for instance, in
                // a JOIN or IN expression, the filter has to filter out the property and accept the
                // fields of collection type
                visitPathExpression(expression, buildMappingFilter(expression));

                // Don't do anything if the first path is not an identification variable,
                // which means it's either KEY() or VALUE()
                if (variable) {

                    // Attempts to resolve a possible fully qualified enum constant
                    visitEnumConstant(expression);

                    // Attempts to resolve third party option
                    visitThirdPartyPathExpression(expression, variableName);
                }
            }
            // The position is within the identification variable but don't do anything if the
            // identification variable is either KEY() or VALUE()
            else if (variable) {
                corrections.add(queryPosition.getPosition(expression));
                visit(expression);
                corrections.pop();
            }
        }
    }

    /**
     * Visits the given {@link AbstractPathExpression} by attempting to resolve the path.
     *
     * @param expression The {@link AbstractPathExpression} to inspect
     * @param filter The {@link Filter} is used to filter out {@link IMapping} that are not valid
     * based on their type and the type that is allowed
     */
    protected void visitPathExpression(AbstractPathExpression expression, Filter<IMapping> filter) {

        MappingCollector mappingCollector = getDefaultMappingCollector();
        int position = queryPosition.getPosition(expression);
        boolean mappingCollectorCreated = false;
        Resolver resolver = null;
        int length = 0;

        for (int index = 0, count = expression.pathSize(); index < count; index++) {
            String path = expression.getPath(index);

            // We're at the position, create the ChoiceBuilder
            if (position <= length + path.length()) {

                if (length == position) {
                    path = ExpressionTools.EMPTY_STRING;
                }
                else if (position - length > -1) {
                    path = path.substring(0, position - length);
                }

                // Special case where the path expression only has the
                // identification variable set
                if (resolver == null) {
                    break;
                }

                mappingCollector = buildFilteringMappingCollector(expression, resolver, filter, path);
                mappingCollectorCreated = true;
                break;
            }
            // The path is entirely before the position of the cursor
            else {
                // The first path is always an identification variable
                if (resolver == null) {
                    resolver = queryContext.getResolver(expression.getIdentificationVariable());
                }
                // Any other path is a property or collection-valued path
                else if ((index + 1 < count) || expression.endsWithDot()) {
                    Resolver childResolver = resolver.getChild(path);
                    if (childResolver == null) {
                        childResolver = new StateFieldResolver(resolver, path);
                        resolver.addChild(path, childResolver);
                        resolver = childResolver;
                    }
                }

                // Move the cursor after the path and dot
                length += path.length() + 1;
            }
        }

        if (!mappingCollectorCreated && (resolver != null)) {
            mappingCollector = buildMappingCollector(expression, resolver, filter);
        }

        proposals.addMappings(mappingCollector.buildProposals());
    }

    /**
     * Adds the possible proposals for the given {@link AbstractSingleEncapsulatedExpression expression}
     * based on the location of the cursor and the content of the expression.
     *
     * @param expression The {@link AbstractSingleEncapsulatedExpression expression} being visited
     * @param identificationVariableType The type of identification variables that can be added as
     * possible proposals
     */
    protected void visitSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression,
                                                     IdentificationVariableType identificationVariableType) {

        visitSingleEncapsulatedExpression(
            expression,
            identificationVariableType,
            expression.getIdentifier()
        );
    }

    /**
     * Adds the possible proposals for the given {@link AbstractSingleEncapsulatedExpression expression}
     * based on the location of the cursor and the content of the expression.
     *
     * @param expression The {@link AbstractSingleEncapsulatedExpression expression} being visited
     * @param identificationVariableType The type of identification variables that can be added as
     * possible proposals
     * @param expressionIdentifiers Sometimes the expression may have more than one possible identifier,
     * such as <b>ALL</b>, <b>ANY</b> and <b>SOME</b> are a possible JPQL identifier for a single
     * expression ({@link AllOrAnyExpression}
     */
    protected void visitSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression,
                                                     IdentificationVariableType identificationVariableType,
                                                     String... expressionIdentifiers) {

        if (isFollowingInvalidExpression(expression)) {
            return;
        }

        int position = queryPosition.getPosition(expression) - corrections.peek();
        String actualIdentifier = expression.getIdentifier();
        boolean added = false;

        for (String identifier : expressionIdentifiers) {

            // Within the identifier
            if (isPositionWithin(position, actualIdentifier)) {

                addFunctionIdentifiers(expression);

                // If the expression is marked as bad, then the identifiers
                // for the encapsulated expression are no added
                if (!isWithinInvalidExpression(expression)) {
                    for (String jpqlIdentifier : expressionIdentifiers) {
                        proposals.addIdentifier(identifier);
                    }
                }
                // Remove any identifier that got added by addAdditionalFunctions()
                else {
                    for (String jpqlIdentifier : expressionIdentifiers) {
                        proposals.removeIdentifier(identifier);
                    }
                }
            }
            // Right after "<identifier>("
            else if (expression.hasLeftParenthesis()) {
                int length = identifier.length() + 1 /* '(' */;

                if (!added && (position == length)) {
                    added = true;

                    addIdentificationVariables(expression, identificationVariableType);

                    String queryBNF = expression.getEncapsulatedExpressionQueryBNFId();
                    addFunctionIdentifiers(queryBNF);
                    addClauseIdentifiers(queryBNF);
                }
            }
        }
    }

    /**
     * Visits the given {@link AbstractSelectStatement} and checks to see if the identifiers of the
     * following clauses can be added a valid proposals.
     *
     * @param expression
     * @param helper This helper handles one clause from the given <code><b>SELECT</b></code> statement
     */
    @SuppressWarnings("unchecked")
    protected <T extends Expression> void visitStatement(T expression, StatementHelper<T> helper) {

        lockedExpressions.add(expression);

        try {
            int position = queryPosition.getPosition(expression);
            int length = 0;

            while (helper != null) {

                // Add the length of the clause to the current length
                Expression clause = helper.getClause(expression);
                length += clause.getLength();

                // Within the clause, not handled here
                if (position < length) {
                    break;
                }

                // Now check if the clause is complete
                boolean complete = false;

                // At the end of the clause
                if (position == length) {
                    // Check to see if the following clause identifiers can be appended
                    // Example: "SELECT e f|"                 <- FROM can be a valid proposal
                    // Example: "SELECT AVG(e.age)|"          <- No clause identifiers can be added
                    // Example: "SELECT e FROM Employee e|"   <- No clause identifiers can be added
                    // Example: "SELECT e FROM Employee e H|" <- HAVING can be a valid proposal
                    complete = isClauseAppendable(clause);
                }
                // Check whether a whitespace is after the clause (owned by the select statement)
                else if (helper.hasSpaceAfterClause(expression)) {
                    length++;

                    if (position == length) {

                        // Ask the helper to visit the clause's expression
                        // Example: "SELECT e FROM Employee e WHERE e.name = 'JPQL' |"
                        //           AND|OR can be added as valid proposals
                        visitEndingExpression(clause);

                        // Check to see if the following clause identifiers can be appended
                        // Example: "SELECT e |"                  <- FROM can be a valid proposal
                        // Example: "SELECT AVG(e.age) |"         <- FROM can be a valid proposal
                        // Example: "SELECT e FROM Employee e |"  <- The following clause identifiers can be valid proposals
                        complete = helper.isClauseComplete(expression);
                    }
                }

                // Continue to with the next helper
                if (!complete) {
                    helper = (StatementHelper<T>) helper.getNextHelper();
                }
                // Add the following clause identifiers until one is defined
                else {

                    // Append any internal clause identifiers
                    helper.addInternalClauseProposals(expression);

                    // Iterate through the helpers for the following clauses
                    do {
                        helper = (StatementHelper<T>) helper.getNextHelper();

                        if (helper != null) {

                            // Add the clause identifiers
                            helper.addClauseProposals();

                            // The following clauses cannot be added because either
                            // the clause is required or defined
                            if (helper.isRequired() || helper.hasClause(expression)) {
                                helper = null;
                            }
                        }

                    }
                    while (helper != null);
                }
            }
        }
        finally {
            lockedExpressions.pop();
        }
    }

    /**
     * Visits the given {@link AbstractPathExpression} and attempts to find valid proposals that is
     * not provided by the default implementation. Subclasses can add additional proposals that is
     * outside of the scope of generic JPA metadata.
     *
     * @param expression The {@link AbstractPathExpression} to inspect
     * @param variableName The beginning of the path expression
     */
    protected void visitThirdPartyPathExpression(AbstractPathExpression expression,
                                                 String variableName) {
    }

    // Made static for performance reasons.
    protected static class AbstractAppendableExpressionVisitor extends AnonymousExpressionVisitor {

        /**
         * Flag used to determine if JPQL identifiers can be appended to the expression.
         */
        protected boolean appendable;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            appendable = false;
        }

        /**
         * Determines whether JPQL identifiers can be appended to the expression.
         *
         * @return <code>true</code>
         */
        public boolean isAppendable() {
            return appendable;
        }
    }

    // Made static for performance reasons.
    /**
     * Common helper with visitor reference.
     */
    private static abstract class AbstractVisitorHelper {

        /**
         * Enclosing visitor instance.
         */
        protected final AbstractContentAssistVisitor visitor;

        AbstractVisitorHelper(AbstractContentAssistVisitor visitor) {
            this.visitor = visitor;
        }

    }

    // Made static for performance reasons.
    /**
     * This helper handles adding proposals within a conditional expression that might be parsed as
     * a single expression or has a collection of expression, which means the fragment is either
     * incomplete or invalid.
     */
    protected static abstract class AbstractConditionalClauseCollectionHelper<T extends Expression>
            extends AbstractVisitorHelper implements CollectionExpressionHelper<T> {

        protected AbstractConditionalClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(Expression expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // Coming from addTheBeginningOfChild(), require to bypass the above check
            if (index < 0) {
                index = (-index / 10) - 1;
            }

            // The only thing that is appendable is an arithmetic operator
            // Example: "SELECT e FROM Employee e WHERE e.name|"
            // Example: "SELECT e FROM Employee e WHERE I|"
            if ((index == 0) && !virtualSpace) {

                Expression child = collectionExpression.getChild(0);

                if (visitor.areArithmeticSymbolsAppendable(child)) {
                    visitor.addArithmeticIdentifiers();
                }
            }
            else {

                Object[] result = findChild(collectionExpression, index);

                if (result == null) {
                    return;
                }

                Expression child = (Expression) result[0];
                boolean hasIs  = (Boolean) result[1];
                boolean hasNot = (Boolean) result[2];

                // If 'IS' or 'IS NOT' is present, then none of the following are valid proposals
                if (!hasIs && !hasNot) {

                    if (visitor.areLogicalSymbolsAppendable(child)) {
                        visitor.addLogicalIdentifiers();
                    }

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }

                    if (visitor.areComparisonSymbolsAppendable(child)) {
                        visitor.addComparisonIdentifiers(child);
                    }
                }

                if (visitor.isCompoundable(child)) {
                    visitor.addCompoundIdentifiers(ConditionalExpressionBNF.ID, child, hasIs, hasNot);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(Expression expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(Expression expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            if (index == 0) {

                visitor.addIdentificationVariables();
                visitor.addFunctionIdentifiers(ConditionalExpressionBNF.ID);

                if ((collectionExpression != null) &&
                        visitor.isSubqueryAppendable(collectionExpression.getChild(index))) {

                    visitor.addIdentifier(SELECT);
                }
            }
            else {
                addAtTheEndOfChild(expression, collectionExpression, index * -10, hasComma, true);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(Expression expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            Expression child = collectionExpression.getChild(index);

            if (visitor.isNotExpression(child)) {
                return true;
            }

            String text = child.toParsedText();

            return text.equalsIgnoreCase(IS)  ||
                   text.equalsIgnoreCase(NOT) ||
                   text.equalsIgnoreCase("IS NOT");
        }

        /**
         * Handles a special case for a compound identifier like 'IS EMPTY' or 'IS NOT EMPTY'.
         *
         * @param collectionExpression The {@link CollectionExpression} is used to find the {@link
         * Expression} to use when filtering out compound identifier
         * @param index The position to start the search, which goes from that index to the beginning
         * @return An array of three elements. The first one is the child {@link Expression} that can
         * be used when filtering out compound identifier. The second boolean element indicates if
         * <code><b>IS</b></code> was detected after the child. The third boolean element indicates if
         * <code><b>NOT</b></code> was detected after the child, which would also be after <code><b>IS</b></code>
         * if it was detected. <code>null</code> is returned if nothing could be found
         */
        protected Object[] findChild(CollectionExpression collectionExpression, int index) {

            boolean notFound = false;
            boolean isFound = false;
            boolean scanPrevious = false;

            for (; index > -1; index--) {

                Expression child = collectionExpression.getChild(index);
                String text = child.toParsedText();

                // Handle 'NOT'
                if (text.equalsIgnoreCase(NOT) || visitor.isNotExpression(child)) {

                    // Two consecutive 'NOT' or 'IS' is invalid or 'NOT IS' is not valid
                    if (isFound || notFound) {
                        break;
                    }

                    notFound = true;
                }
                // Handle 'IS'
                else if (text.equalsIgnoreCase(IS)) {

                    // Two consecutive 'IS' is invalid
                    if (isFound) {
                        break;
                    }

                    isFound = true;
                }
                else if ("IS NOT".equalsIgnoreCase(text)) {

                    // Two consecutive 'NOT' or 'IS' is invalid or 'NOT IS' is not valid
                    if (isFound || notFound) {
                        break;
                    }

                    isFound  = true;
                    notFound = true;
                }
                // Anything else
                else {

                    // Make sure the previous item is not 'IS', this can happen
                    // when the correction value is changed (happens with IdentificationVariable)
                    if (index > 0) {
                        Object[] result = findChild(collectionExpression, index - 1);
                        isFound  |= (Boolean) result[1];
                        notFound |= (Boolean) result[2];
                    }

                    return new Object[] { child, isFound, notFound };
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(Expression expression) {
            // The actual number is 0 but an incomplete fragment like "WHERE e.phoneNumbers IS N"
            // is a collection of 3 expressions
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(Expression expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(Expression expression, int index) {
            return visitor.getQueryBNF(ConditionalExpressionBNF.ID);
        }
    }

    // Made static for performance reasons.
    protected static abstract class AbstractFromClauseStatementHelper<T extends AbstractSelectStatement>
            extends AbstractVisitorHelper implements StatementHelper<T> {

        protected AbstractFromClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(FROM);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(T expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(T expression) {
            return expression.getFromClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(T expression) {
            return expression.hasFromClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(T expression) {
            return expression.hasSpaceAfterFrom();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(T expression) {
            return visitor.isComplete(expression.getFromClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return true;
        }
    }

    // Made static for performance reasons.
    protected static abstract class AbstractGroupByClauseStatementHelper<T extends AbstractSelectStatement>
            extends AbstractVisitorHelper implements StatementHelper<T> {

        protected AbstractGroupByClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addCompositeIdentifier(GROUP_BY, -1);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(T expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(T expression) {
            return expression.getGroupByClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(T expression) {
            return expression.hasGroupByClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(T expression) {
            return expression.hasSpaceAfterGroupBy();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(T expression) {
            return visitor.isComplete(expression.getGroupByClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return false;
        }
    }

    // Made static for performance reasons.
    protected static abstract class AbstractHavingClauseStatementHelper<T extends AbstractSelectStatement>
            extends AbstractVisitorHelper implements StatementHelper<T> {

        protected AbstractHavingClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(HAVING);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(T expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(T expression) {
            return expression.getHavingClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(T expression) {
            return expression.hasHavingClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(T expression) {
            return  visitor.isComplete(expression.getHavingClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return false;
        }
    }

    // Made static for performance reasons.
    protected static abstract class AbstractSelectClauseCollectionHelper<T extends AbstractSelectClause>
            extends AbstractVisitorHelper implements CollectionExpressionHelper<T> {

        protected AbstractSelectClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(T expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // The only thing that is appendable is an arithmetic operator
            // Example: "SELECT e.name|"
            // Example: "SELECT e|"
            if (queryBNF(expression, index).handleAggregate()) {

                Expression child = collectionExpression.getChild(index);

                if (visitor.areArithmeticSymbolsAppendable(child)) {
                    visitor.addArithmeticIdentifiers();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(T expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(T expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            if ((index == 0) || hasComma) {
                visitor.addIdentificationVariables();
                visitor.addFunctionIdentifiers(expression.getSelectItemQueryBNFId());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(T expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getSelectExpression());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(T expression, CollectionExpression collectionExpression, int index) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(T expression) {
            return expression.hasSpaceAfterSelect();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(T expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(T expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(T expression, int index) {
            return visitor.getQueryBNF(expression.getSelectItemQueryBNFId());
        }
    }

    // Made static for performance reasons.
    protected static abstract class AbstractSelectClauseStatementHelper
            extends AbstractVisitorHelper implements StatementHelper<AbstractSelectStatement> {

        protected AbstractSelectClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(SELECT);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(AbstractSelectStatement expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(AbstractSelectStatement expression) {
            return expression.getSelectClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(AbstractSelectStatement expression) {
            return expression.hasSelectClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(AbstractSelectStatement expression) {
            return expression.hasSpaceAfterSelect();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(AbstractSelectStatement expression) {
            return visitor.isComplete(expression.getSelectClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return true;
        }

    }

    // Made static for performance reasons.
    protected static abstract class AbstractWhereClauseSelectStatementHelper<T extends AbstractSelectStatement>
            extends AbstractVisitorHelper implements StatementHelper<T> {

        protected AbstractWhereClauseSelectStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(WHERE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(T expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(T expression) {
            return expression.getWhereClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(T expression) {
            return expression.hasWhereClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(T expression) {
            return expression.hasSpaceAfterWhere();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(T expression) {
            return visitor.isComplete(expression.getWhereClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return false;
        }
    }

    // Made static for performance reasons.
    /**
     * This visitor retrieves the permitted type from the path expression's parent. For instance,
     * <b>SUM</b> or <b>AVG</b> only accepts state fields that have a numeric type.
     */
    protected static abstract class AcceptableTypeVisitor extends AbstractExpressionVisitor {

        /**
         * The type that is retrieved based on the expression, it determines what is acceptable.
         */
        protected IType type;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            type = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {
            expression.getParent().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {
            expression.getParent().accept(this);
        }
    }

    // Made static for performance reasons.
    /**
     * This visitor scans the visited {@link Expression} and determines if a JPQL identifier can be
     * added ("appended") when the position of the cursor is at the end of the expression.
     * <p>
     * For instance:
     * <ul>
     * <li>In "<code>SELECT e, AVG(e.age) F|</code>", F is parsed as a result variable but
     * can also be seen as the first letter for <b>FROM</b>;</li>
     * <li>In "<code>SELECT e FROM Employee e WHERE e.name |</code>", the compound identifiers can be
     * added, eg: 'IS NOT NULL', or '=', etc</li>
     * <li>In "<code>SELECT e FROM Employee e WHERE e.name NOT B|</code>", only the composite
     * identifier "BETWEEN" and "NOT BETWEEN" can be added because the <code>NOT</code> expression
     * does not have a valid expression: "B" is not a valid expression.</li>
     * <li>In "<code>SELECT e FROM Employee e FROM e.age|</code>", the arithmetic and comparison
     * identifiers are allowed, but the logical and compound identifiers.</li>
     * </ul>
     */
    protected static class AppendableExpressionVisitor extends AbstractAppendableExpressionVisitor {

        /**
         * Enclosing visitor instance.
         */
        protected final AbstractContentAssistVisitor visitor;

        /**
         * The type of the JPQL identifiers can can be possible proposals.
         */
        protected AppendableType appendableType;

        /**
         * Internal flag indicating if a clause is being visited which can have a collection of children.
         */
        protected boolean clauseOfItems;

        /**
         * Caches the visited {@link CollectionExpression} so a child could use it.
         */
        protected CollectionExpression collectionExpression;

        /**
         * Internal flag indicating the {@link Expression} being visited is a conditional expression.
         */
        protected boolean conditionalExpression;

        /**
         * When visiting a {@link CollectionExpression}, this indicates if there is a comma before the
         * child being visited.
         */
        protected boolean hasComma;

        /**
         * When visiting a {@link CollectionExpression}, this indicates the position within that
         * collection of the child being visited.
         */
        protected int positionInCollection;

        /**
         * Internal flag indicating the {@link Expression} being visited is encapsulated by parenthesis.
         */
        protected boolean subExpression;

        /**
         * Creates a new <code>AppendableExpressionVisitor</code>.
         */
        AppendableExpressionVisitor(AbstractContentAssistVisitor visitor) {
            super();
            this.visitor = visitor;
            this.positionInCollection = -1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbsExpression expression) {
            appendable = !conditionalExpression &&
                         expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbstractSchemaName expression) {
            appendable = !conditionalExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AdditionExpression expression) {
            if (expression.hasRightExpression()) {
                expression.getRightExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AllOrAnyExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AndExpression expression) {
            if (expression.hasRightExpression()) {
                expression.getRightExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ArithmeticFactor expression) {
            if (expression.hasExpression()) {
                expression.getExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AvgFunction expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BadExpression expression) {
            appendable = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BetweenExpression expression) {

            // Example 2: "x BETWEEN y A" is not complete
            // Example 1: "x between y SQRT(e.age)" is seen as complete
            if (!expression.hasAnd()) {

                String variable = visitor.queryContext.literal(
                    expression.getUpperBoundExpression(),
                    LiteralType.IDENTIFICATION_VARIABLE
                );

                if (variable != ExpressionTools.EMPTY_STRING) {
                    appendable = false;
                }
                else {
                    expression.getUpperBoundExpression().accept(this);
                }
            }
            else {
                boolean oldConditionalExpression = conditionalExpression;
                conditionalExpression = false;
                expression.getUpperBoundExpression().accept(this);
                conditionalExpression = oldConditionalExpression;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CaseExpression expression) {
            appendable = !conditionalExpression &&
                         expression.hasEnd();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CoalesceExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {

            collectionExpression = expression;
            positionInCollection = expression.childrenSize() - 1;
            hasComma = expression.hasComma(positionInCollection - 1);

            if ((appendableType == AppendableType.CLAUSE) &&
                (positionInCollection + 1 < collectionExpression.childrenSize())) {

                appendable = false;
            }
            else {
                expression.accept(positionInCollection, this);
            }

            hasComma = false;
            positionInCollection = -1;
            collectionExpression = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberDeclaration expression) {

            if (appendableType == AppendableType.COMPLETE) {
                if (expression.hasIdentificationVariable()) {
                    expression.getIdentificationVariable().accept(this);
                }
            }
            else {
                // Only the identifier is parsed
                appendable = !expression.hasAs()                         &&
                             !expression.hasSpaceAfterIn()               &&
                             !expression.hasLeftParenthesis()            &&
                             !expression.hasRightParenthesis()           &&
                             !expression.hasIdentificationVariable()     &&
                             !expression.hasSpaceAfterRightParenthesis() &&
                             !expression.hasCollectionValuedPathExpression();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberExpression expression) {

            // Only AND, OR, NOT or clause identifiers can be followed this expression
            appendable = (appendableType == AppendableType.LOGICAL   ||
                          appendableType == AppendableType.COMPLETE) &&
                          expression.hasCollectionValuedPathExpression();

            if (appendable) {
                boolean oldConditionalExpression = conditionalExpression;
                conditionalExpression = false;
                expression.getCollectionValuedPathExpression().accept(this);
                conditionalExpression = oldConditionalExpression;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            appendable = !conditionalExpression &&
                      !expression.endsWithDot();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ComparisonExpression expression) {

            if (appendableType == AppendableType.ARITHMETIC ||
                appendableType == AppendableType.COMPARISON ||
                appendableType == AppendableType.COMPOUNDABLE) {

                appendable = false;
            }
            // "x = y |" <- AND and OR are valid proposals
            // "(x = y) |" <- AND and OR are valid proposals
            else if (appendableType == AppendableType.LOGICAL) {
                AppendableType oldAppendableType = appendableType;
                appendableType = AppendableType.COMPLETE;
                expression.getRightExpression().accept(this);
                appendableType = oldAppendableType;
            }
            else if (expression.hasRightExpression()) {
                boolean oldConditionalExpression = conditionalExpression;
                conditionalExpression = false;
                expression.getRightExpression().accept(this);
                conditionalExpression = oldConditionalExpression;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConcatExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConstructorExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CountFunction expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DateTime expression) {

            if (conditionalExpression && (appendableType == AppendableType.COMPLETE)) {
                appendable = false;
            }
            else {
                appendable = // Valid ending
                             (!expression.isJDBCDate()                    ||
                              expression.toActualText().endsWith("}"))    &&
                             // Valid AppendableType
                             (appendableType == AppendableType.ARITHMETIC ||
                              appendableType == AppendableType.COMPARISON ||
                              appendableType == AppendableType.COMPLETE   ||
                              appendableType == AppendableType.COMPOUNDABLE);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DeleteClause expression) {
            if (expression.hasRangeVariableDeclaration()) {
                expression.getRangeVariableDeclaration().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DeleteStatement expression) {

            if (expression.hasWhereClause()) {
                expression.getWhereClause().accept(this);
            }
            else {
                expression.getDeleteClause().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DivisionExpression expression) {
            if (expression.hasRightExpression()) {
                expression.getRightExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EmptyCollectionComparisonExpression expression) {

            // Only AND, OR, NOT or clause identifiers can follow this expression
            // when the type is LOGICAL or CLAUSE
            appendable = appendableType == AppendableType.COMPLETE ||
                         appendableType == AppendableType.LOGICAL;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EntityTypeLiteral expression) {
            appendable = !conditionalExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EntryExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ExistsExpression expression) {

            // Only AND, OR, NOT or clause identifiers can follow this expression
            // when the type is LOGICAL or CLAUSE
            appendable = (appendableType == AppendableType.COMPLETE ||
                          appendableType == AppendableType.LOGICAL) &&
                          expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {

            // Anything other than what's a valid conditional expression is not valid
            // "... WHERE AVG(a.ge) " <- Invalid and the following clauses cannot be appended
            appendable = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FromClause expression) {

            if (expression.hasAsOfClause()) {
                expression.getAsOfClause().accept(this);
            }
            else if (expression.hasHierarchicalQueryClause()) {
                expression.getHierarchicalQueryClause().accept(this);
            }
            else if (expression.hasDeclaration()) {
                clauseOfItems = true;
                expression.getDeclaration().accept(this);
                clauseOfItems = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FunctionExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(GroupByClause expression) {

            if (expression.hasGroupByItems()) {
                clauseOfItems = true;
                expression.getGroupByItems().accept(this);
                clauseOfItems = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(HavingClause expression) {
            if (expression.hasConditionalExpression()) {
                conditionalExpression = true;
                expression.getConditionalExpression().accept(this);
                conditionalExpression = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {

            if (appendableType == AppendableType.COMPLETE) {
                appendable = true;
            }
            // The WHERE/HAVING clauses do not have a collection of expressions but has an
            // aggregation of expressions. If the appendable type is CLASUE, then the only
            // possible way for this to be valid is to have a collection of expressions where
            // the first child is the conditional expression and the second child is the
            // beginning of the following clause.
            // Example: "SELECT e FROM Employee e G|" <- appendable
            // Example: "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT E|" <- Not appendable
            else if (clauseOfItems || (!clauseOfItems && (appendableType == AppendableType.CLAUSE))) {
                appendable = !hasComma                   &&
                             (positionInCollection > -1) &&
                             !visitor.isFollowingInvalidExpression(expression);
            }
            else {

                // Special case if what's before is 'IS' or 'IS NOT', then it's not appendable
                if (positionInCollection > 1) {
                    Expression child = collectionExpression.getChild(positionInCollection - 1);
                    String text = child.toActualText();
                    appendable = !text.equals(IS) && !text.equals("IS NOT");
                }
                else {
                    switch (appendableType) {
                        case ARITHMETIC: {
                            appendable = false;
                            break;
                        }
                        case SUBQUERY:
                        case LOGICAL:
                        case COMPOUNDABLE: {
                            appendable = true;
//                            appendable = (positionInCollection > -1) || subExpression;
                            break;
                        }
                        case CLAUSE: {
                            appendable = false;
                            break;
                        }
                        case COMPARISON:
                        case COMPLETE: {
                            appendable = true;
                            break;
                        }
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariableDeclaration expression) {

            // 1) The FROM clause needs to have more than one identification variable declaration
            //    before the next clauses identifiers can be added. Example: "SELECT e FROM E" where
            //    'E' cannot be the beginning of a clause identifier
            // 2) The next clause identifiers cannot be added if there is a comma before the last
            //    item. Example: "SELECT e FROM Employee e, I" where 'I' cannot be the beginning of
            //    a clause identifier
            if ((appendableType != AppendableType.COMPLETE) && ((positionInCollection == -1) || hasComma)) {
                appendable = false;
            }
            else if (expression.hasJoins()) {
                expression.getJoins().accept(this);
            }
            else {
                expression.getRangeVariableDeclaration().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IndexExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(InExpression expression) {

            // Only AND, OR, NOT or clause identifiers can follow this expression
            // when the type is LOGICAL or CLAUSE
            appendable = (appendableType == AppendableType.COMPLETE ||
                          appendableType == AppendableType.LOGICAL) &&
                          expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(InputParameter expression) {
            appendable = !conditionalExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(Join expression) {

            if (expression.hasOnClause()) {
                expression.getOnClause().accept(this);
            }
            else if (expression.hasFetch()) {
                if (expression.hasAs()) {
                    appendable = expression.hasIdentificationVariable();
                }
                else {
                    appendable = expression.hasJoinAssociationPath();
                }
            }
            else {
                appendable = expression.hasIdentificationVariable();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(KeyExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(KeywordExpression expression) {
            // Nothing can be added right after NULL, TRUE, FALSE
            appendable = !conditionalExpression &&
                      (appendableType == AppendableType.LOGICAL ||
                       appendableType == AppendableType.COMPLETE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LengthExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LikeExpression expression) {

            // Only AND, OR, NOT or clause identifiers can follow this expression
            // when the type is LOGICAL or CLAUSE
            appendable = appendableType == AppendableType.COMPLETE ||
                         appendableType == AppendableType.LOGICAL;

            if (appendable) {

                boolean oldConditionalExpression = conditionalExpression;
                conditionalExpression = false;

                if (expression.hasEscape()) {
                    expression.getEscapeCharacter().accept(this);
                }
                else {
                    expression.getPatternValue().accept(this);
                }

                conditionalExpression = oldConditionalExpression;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LocateExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LowerExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MaxFunction expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MinFunction expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ModExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MultiplicationExpression expression) {
            if (expression.hasRightExpression()) {
                expression.getRightExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NotExpression expression) {

            // Example: "NOT B" can only have compound identifiers like 'NOT BETWEEN'
            if (expression.hasExpression()) {

                String variable = visitor.queryContext.literal(
                    expression.getExpression(),
                    LiteralType.IDENTIFICATION_VARIABLE
                );

                if (variable != ExpressionTools.EMPTY_STRING) {
                    appendable = (appendableType == AppendableType.COMPOUNDABLE);
                }
                else {
                    boolean oldConditionalExpression = conditionalExpression;
                    conditionalExpression = false;
                    expression.getExpression().accept(this);
                    conditionalExpression = oldConditionalExpression;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullComparisonExpression expression) {

            // Only AND, OR, NOT or clause identifiers can follow this expression
            // when the type is LOGICAL or CLAUSE
            appendable = appendableType == AppendableType.COMPLETE ||
                         appendableType == AppendableType.LOGICAL;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullIfExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NumericLiteral expression) {
            appendable = !conditionalExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ObjectExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OnClause expression) {
            if (expression.hasConditionalExpression()) {
                conditionalExpression = true;
                expression.getConditionalExpression().accept(this);
                conditionalExpression = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrderByClause expression) {
            if (expression.hasOrderByItems()) {
                clauseOfItems = true;
                expression.getOrderByItems().accept(this);
                clauseOfItems = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrderByItem expression) {
            appendable = expression.hasSpaceAfterExpression() &&
                         expression.isDefault() ||
                         expression.hasSpaceAfterOrdering() &&
                        !expression.isNullsFirst() && !expression.isNullsLast();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrExpression expression) {
            if (expression.hasRightExpression()) {
                expression.getRightExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {

            if (appendableType == AppendableType.COMPLETE) {
                if (expression.hasIdentificationVariable()) {
                    expression.getIdentificationVariable().accept(this);
                }
            }
            else {

                // Only the abstract schema name is parsed
                appendable = !expression.hasSpaceAfterRootObject() &&
                             !expression.hasAs() &&
                             !expression.hasIdentificationVariable();

                if (!appendable &&
                    expression.hasRootObject() &&
                    expression.hasIdentificationVariable()) {

                    expression.getIdentificationVariable().accept(this);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ResultVariable expression) {
            // The result variable is parsed without AS
            appendable = !expression.hasAs() &&
                         !expression.hasSpaceAfterAs() &&
                          expression.hasResultVariable();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectClause expression) {
            if (expression.hasSelectExpression()) {
                clauseOfItems = true;
                expression.getSelectExpression().accept(this);
                clauseOfItems = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectStatement expression) {

            if (expression.hasUnionClauses()) {
                expression.getUnionClauses().accept(this);
            }
            else if (expression.hasOrderByClause()) {
                expression.getOrderByClause().accept(this);
            }
            else if (expression.hasHavingClause()) {
                expression.getHavingClause().accept(this);
            }
            else if (expression.hasGroupByClause()) {
                expression.getGroupByClause().accept(this);
            }
            else if (expression.hasWhereClause()) {
                expression.getWhereClause().accept(this);
            }
            else if (expression.hasFromClause()) {
                expression.getFromClause().accept(this);
            }
            else {
                expression.getSelectClause().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleFromClause expression) {

            if (expression.hasAsOfClause()) {
                expression.getAsOfClause().accept(this);
            }
            else if (expression.hasHierarchicalQueryClause()) {
                expression.getHierarchicalQueryClause().accept(this);
            }
            else if (expression.hasDeclaration()) {
                clauseOfItems = true;
                expression.getDeclaration().accept(this);
                clauseOfItems = true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectClause expression) {

            if (expression.hasSelectExpression()) {
                clauseOfItems = true;
                expression.getSelectExpression().accept(this);
                clauseOfItems = true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {

            if (subExpression && (appendableType == AppendableType.ARITHMETIC)) {
                appendable = true;
            }
            else {

                if (expression.hasHavingClause()) {
                    expression.getHavingClause().accept(this);
                }
                else if (expression.hasGroupByClause()) {
                    expression.getGroupByClause().accept(this);
                }
                else if (expression.hasWhereClause()) {
                    expression.getWhereClause().accept(this);
                }
                else if (expression.hasFromClause()) {
                    expression.getFromClause().accept(this);
                }
                else {
                    expression.getSelectClause().accept(this);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SizeExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SqrtExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StateFieldPathExpression expression) {

            // "e.employee." cannot be appended with something
            if (expression.endsWithDot()) {
                appendable = false;
            }
            // "... WHERE e.age "    <- Invalid to be followed by another clause
            // "... GROUP BY e.age " <- Valid to be followed by another clause
            else if (appendableType == AppendableType.CLAUSE ||
                     appendableType == AppendableType.COMPLETE) {

                appendable = !conditionalExpression;
            }
            else {

                // Resolve the mapping so we can determine what is valid based on its type
                IMapping mapping = visitor.queryContext.getMapping(expression);

                if (mapping == null) {
                    appendable = false;
                }
                else {
                    IType type = mapping.getType();

                    switch (appendableType) {
                        case ARITHMETIC: {
                            // e.name (String) cannot be followed by +,-,/,*
                            // e.age (int) can be followed by an arithmetic operator
                            appendable = visitor.queryContext.getTypeHelper().isNumericType(type);
                            break;
                        }
                        case COMPARISON: {
                            TypeHelper typeHelper = visitor.queryContext.getTypeHelper();
                            appendable = !typeHelper.isCollectionType(type) &&
                                         !typeHelper.isMapType(type);
                            break;
                        }
                        case COMPOUNDABLE: {
                            // The type will be calculated later
                            appendable = true;
                            break;
                        }
                        case LOGICAL: {
                            appendable = visitor.queryContext.getTypeHelper().isBooleanType(type);
                            break;
                        }
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StringLiteral expression) {
            appendable = !conditionalExpression &&
                      expression.hasCloseQuote();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {

            if (appendableType == AppendableType.COMPLETE) {
                appendable = expression.hasRightParenthesis();
            }
            else if (expression.hasExpression()) {

                subExpression = true;
                boolean oldConditionalExpression = conditionalExpression;
                conditionalExpression = false;

                expression.getExpression().accept(this);
                appendable &= expression.hasRightParenthesis();

                subExpression = false;
                conditionalExpression = oldConditionalExpression;
            }
            else {
                // Only a subquery could be a valid proposal, everything
                // else cannot start an encapsulated expression
                appendable = (appendableType == AppendableType.SUBQUERY);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubstringExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubtractionExpression expression) {
            if (expression.hasRightExpression()) {
                expression.getRightExpression().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SumFunction expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TreatExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TrimExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TypeExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateClause expression) {
            if (expression.hasUpdateItems()) {
                expression.getUpdateItems().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateItem expression) {
            expression.getNewValue().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateStatement expression) {
            if (expression.hasWhereClause()) {
                expression.getWhereClause().accept(this);
            }
            else {
                expression.getUpdateClause().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpperExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ValueExpression expression) {
            appendable = !conditionalExpression &&
                      expression.hasRightParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhenClause expression) {
            if (expression.hasWhenExpression()) {
                conditionalExpression = true;
                expression.getWhenExpression().accept(this);
                conditionalExpression = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhereClause expression) {
            if (expression.hasConditionalExpression()) {
                conditionalExpression = true;
                expression.getConditionalExpression().accept(this);
                conditionalExpression = false;
            }
        }
    }

    // Made static for performance reasons.
    /**
     * This is used to determine how {@link AppendableExpressionVisitor} should perform the check.
     */
    protected static enum AppendableType {

        /**
         * Determines whether the arithmetic operators (+, -, *, /) can be appended as valid proposals.
         */
        ARITHMETIC,

        /**
         * Determines whether the JPQL identifiers identifying a clause (eg: <code><b>WHERE</b></code>)
         * can be appended as valid proposals.
         */
        CLAUSE,

        /**
         * Determines whether the comparison operators {@literal (<, <=, <>, >=, =)} can be appended as valid proposals.
         */
        COMPARISON,

        /**
         * Determines whether the visited {@link Expression} is grammatically complete by making sure
         * its ending is valid.
         */
        COMPLETE,

        /**
         * Determines whether the compound identifiers (eg: <code><b>IS NULL</b></code>) can be
         * appended as valid proposals.
         */
        COMPOUNDABLE,

        /**
         * Determines whether the logical identifiers (<code><b>AND</b></code> and <code><b>OR</b></code>)
         * can be appended as valid proposals.
         */
        LOGICAL,

        /**
         * Determines whether the JPQL identifiers identifying a subquery (eg: <code><b>SELECT</b></code>)
         * can be appended as valid proposals.
         */
        SUBQUERY
    }

    // Made static for performance reasons.
    /**
     * This helper is used to determine how to add proposals within a collection of expressions. Each
     * expression is usually separated by either a whitespace or by a comma.
     */
    protected static interface CollectionExpressionHelper<T extends Expression> {

        /**
         * Adds the proposals because the cursor is at the end of the child at the given position.
         *
         * @param expression The {@link Expression} being visited
         * @param collectionExpression The {@link CollectionExpression} is either the child of the
         * given {@link Expression} or a temporary generated one that usually contains a single item
         * @param index The position of that child in the collection of children
         * @param hasComma Indicates whether a comma is present before the child at the given position;
         * if the index is 0, then this is <code>false</code> by default
         * @param virtualSpace Indicates if this method is called because the cursor is at the end of
         * the child at the specified index but by considering there is a virtual space at the end of
         * that child
         */
        void addAtTheEndOfChild(T expression,
                                CollectionExpression collectionExpression,
                                int index,
                                boolean hasComma,
                                boolean virtualSpace);

        /**
         * Adds the given JPQL identifier as a valid proposal.
         *
         * @param expression The {@link Expression} being visited
         * @param identifier The JPQL identifier to add as a valid proposal
         */
        void addIdentifier(T expression, String identifier);

        /**
         * Adds the proposals because the cursor is at the beginning of the child {@link Expression}
         * at the given position.
         *
         * @param expression The {@link Expression} being visited
         * @param collectionExpression The {@link CollectionExpression} is either the child of the
         * given {@link Expression} or a temporary generated one that usually contains a single item.
         * This can be null if the position is at the beginning
         * @param index The position of the child that was scanned
         * @param hasComma Indicates whether a comma is present before the child at the given position;
         * if the index is 0, then this is <code>false</code> by default
         */
        void addTheBeginningOfChild(T expression,
                                    CollectionExpression collectionExpression,
                                    int index,
                                    boolean hasComma);

        /**
         * Either returns the given {@link Expression}'s child, which is already a {@link CollectionExpression}
         * or requests this helper to return a "virtual" {@link CollectionExpression} that is wrapping
         * the single element.
         *
         * @param expression The parent of the children to retrieve
         * @return The given expression's child or a "virtual" one
         */
        CollectionExpression buildCollectionExpression(T expression);

        /**
         * Asks this helper if the search can continue even though two child expressions are not
         * separated by a comma.
         *
         * @param expression The {@link Expression} being visited
         * @param collectionExpression The {@link CollectionExpression}
         * @param index The position of the child being scanned
         * @return <code>true</code> if the check can continue even though the previous child was not
         * separated by a comma; <code>false</code> to stop the check
         */
        boolean canContinue(T expression, CollectionExpression collectionExpression, int index);

        /**
         * Determines whether a delimiter like a whitespace or an open parenthesis was parsed after
         * the identifier.
         *
         * @param expression The {@link Expression} being visited
         * @return <code>true</code> if something is present; <code>false</code> otherwise
         */
        boolean hasDelimiterAfterIdentifier(T expression);

        /**
         * Returns the maximum number of encapsulated {@link Expression expressions} the {@link Expression}
         * allows. Some expression only allow 2, others 3 and others allow an unlimited number.
         *
         * @param expression The {@link Expression} for which its maximum number of children
         * @return The maximum number of children the expression can have
         */
        int maxCollectionSize(T expression);

        /**
         * Returns the length of anything that can be defined before the first child. An example can
         * be "<code>DISTINCT </code>" in "<code>AVG(DISTINCT e.name)</code>".
         *
         * @param expression The {@link Expression} being visited
         * @return The length of anything that was parsed before the first child or 0 if nothing was parsed
         */
        int preExpressionLength(T expression);

        /**
         * Returns the {@link JPQLQueryBNF} that defines the fragment at the given position.
         *
         * @param expression The {@link Expression} being visited
         * @param index The position of the element to retrieve the BNF defined in the JPQL grammar
         * @return The {@link JPQLQueryBNF} that defines the fragment at the given position
         */
        JPQLQueryBNF queryBNF(T expression, int index);
    }

    // Made static final for performance reasons.
    /**
     * This visitor retrieves the {@link CollectionExpression} if it is visited.
     */
    protected static final class CollectionExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link CollectionExpression} if it is the {@link Expression} that was visited.
         */
        protected CollectionExpression expression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class CollectionMappingFilter implements Filter<IMapping> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(IMapping value) {
            // Both association and collection field are accepted
            // Example: e.address is incomplete but it is not the entire path
            // Example: e.projects is the complete path
            return value.isRelationship();
        }
    }

    // Made static final for performance reasons.
    protected static final class ConcatExpressionCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<ConcatExpression> {

        protected ConcatExpressionCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(ConcatExpression expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // The only thing that is appendable is an arithmetic operator
            // Example: "SELECT e.name|"
            // Example: "SELECT e|"
            if (queryBNF(expression, index).handleAggregate()) {

                Expression child = collectionExpression.getChild(index);

                if ((index == 0) && !virtualSpace) {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }
                }
                else {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }

                    if (visitor.areComparisonSymbolsAppendable(child)) {
                        visitor.addComparisonIdentifiers(child);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(ConcatExpression expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
            visitor.addIdentificationVariables();
            visitor.addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(ConcatExpression expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            visitor.addIdentificationVariables();
            visitor.addFunctionIdentifiers(queryBNF(expression, index));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(ConcatExpression expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getExpression());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(ConcatExpression expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(ConcatExpression expression) {
            return expression.hasSpaceAfterIdentifier() ||
                   expression.hasLeftParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(ConcatExpression expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(ConcatExpression expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(ConcatExpression expression, int index) {
            return visitor.getQueryBNF(expression.getEncapsulatedExpressionQueryBNFId());
        }

    }

    // Made static final for performance reasons.
    /**
     * This helpers handles adding proposals for {@link AbstractConditionalClause}.
     */
    protected static final class ConditionalClauseCollectionHelper extends AbstractConditionalClauseCollectionHelper<AbstractConditionalClause> {

        protected ConditionalClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(AbstractConditionalClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getConditionalExpression());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(AbstractConditionalClause expression) {
            return expression.hasSpaceAfterIdentifier();
        }
    }

    // Made static final for performance reasons.
    protected static final class ConstrutorCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<ConstructorExpression> {

        protected ConstrutorCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(ConstructorExpression expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // The only thing that is appendable is an arithmetic operator
            // Example: "SELECT e.name|"
            // Example: "SELECT e|"
            if (queryBNF(expression, index).handleAggregate()) {

                Expression child = collectionExpression.getChild(index);

                if ((index == 0) && !virtualSpace) {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }
                }
                else {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }

                    if (visitor.areComparisonSymbolsAppendable(child)) {
                        visitor.addComparisonIdentifiers(child);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(ConstructorExpression expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(ConstructorExpression expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            visitor.addIdentificationVariables(expression, IdentificationVariableType.ALL);
            visitor.addFunctionIdentifiers(ConstructorItemBNF.ID);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(ConstructorExpression expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getConstructorItems());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(ConstructorExpression expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(ConstructorExpression expression) {
            return expression.hasLeftParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(ConstructorExpression expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(ConstructorExpression expression) {
            if (expression.hasSpaceAfterNew()) {
                return expression.getClassName().length() + SPACE_LENGTH;
            }
            return expression.getClassName().length();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(ConstructorExpression expression, int index) {
            return visitor.getQueryBNF(ConstructorItemBNF.ID);
        }
    }

    // Made static final for performance reasons.
    protected static final class DeclarationVisitor extends AnonymousExpressionVisitor {

        /**
         * Indicates if the visited {@link CollectionValuedPathExpression} is found within a
         * declaration expression.
         */
        protected boolean declaration;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            declaration = false;
        }

        /**
         * Determines whether the visited path expression is found within a declaration expression.
         *
         * @return <code>true</code> if the visited {@link CollectionValuedPathExpression} is owned by
         * a {@link RangeVariableDeclaration}, which indicates it is used to define the "root" object;
         * <code>false</code> if it is not
         */
        public boolean isDeclaration() {
            return declaration;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            expression.getParent().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {
            declaration = true;
        }
    }

    // Made static final for performance reasons.
    /**
     * The default implementation of {@link MappingCollector}, which simply returns an empty collection.
     */
    protected static final class DefaultMappingCollector implements MappingCollector {

        /**
         * {@inheritDoc}
         */
        @Override
        public Collection<IMapping> buildProposals() {
            return Collections.emptyList();
        }
    }

    // Made static final for performance reasons.
    protected static final class DeleteClauseCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<DeleteClause> {

        protected DeleteClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(DeleteClause expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(DeleteClause expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(DeleteClause expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            if (index == 0) {
                visitor.addEntities();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(DeleteClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getRangeVariableDeclaration());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(DeleteClause expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(DeleteClause expression) {
            return expression.hasSpaceAfterFrom();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(DeleteClause expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(DeleteClause expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(DeleteClause expression, int index) {
            return visitor.getQueryBNF(RangeVariableDeclarationBNF.ID);
        }
    }

    // Made static final for performance reasons.
    protected static final class DeleteClauseStatementHelper
            extends AbstractVisitorHelper implements StatementHelper<DeleteStatement> {

        protected DeleteClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(DELETE_FROM);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(DeleteStatement expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(DeleteStatement expression) {
            return expression.getDeleteClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WhereClauseDeleteStatementHelper getNextHelper() {
            return visitor.getWhereClauseDeleteStatementHelper();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(DeleteStatement expression) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(DeleteStatement expression) {
            return expression.hasSpaceAfterDeleteClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(DeleteStatement expression) {
            return visitor.isComplete(expression.getDeleteClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return true;
        }
    }

    // Made static final for performance reasons.
    protected static final class DifferentComparisonFilter
            extends AnonymousExpressionVisitor implements Filter<Expression> {

        /**
         * Determines whether {@literal '<', '<=', '>=', '>'} are valid comparison operators.
         */
        protected boolean valid;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(Expression expression) {
            try {
                expression.accept(this);
                return valid;
            }
            finally {
                valid = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            valid = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {
            valid = false;
        }
    }

    // Made static final for performance reasons.
    protected static final class DoubleEncapsulatedCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<AbstractDoubleEncapsulatedExpression> {

        protected DoubleEncapsulatedCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(AbstractDoubleEncapsulatedExpression expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // The only thing that is appendable is an arithmetic operator
            // Example: "SELECT e.name|"
            // Example: "SELECT e|"
            if (queryBNF(expression, index).handleAggregate()) {

                Expression child = collectionExpression.getChild(index);

                if ((index == 0) && !virtualSpace) {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }
                }
                else {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }

                    if (visitor.areComparisonSymbolsAppendable(child)) {
                        visitor.addComparisonIdentifiers(child);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(AbstractDoubleEncapsulatedExpression expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
            visitor.addIdentificationVariables();
            visitor.addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(AbstractDoubleEncapsulatedExpression expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            visitor.addIdentificationVariables();
            visitor.addFunctionIdentifiers(queryBNF(expression, index));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(AbstractDoubleEncapsulatedExpression expression) {
            return expression.buildCollectionExpression();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(AbstractDoubleEncapsulatedExpression expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(AbstractDoubleEncapsulatedExpression expression) {
            return expression.hasSpaceAfterIdentifier() ||
                   expression.hasLeftParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(AbstractDoubleEncapsulatedExpression expression) {
            // Both MOD and NULLIF allows a fixed 2 encapsulated expressions
            return 2;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(AbstractDoubleEncapsulatedExpression expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(AbstractDoubleEncapsulatedExpression expression, int index) {
            return visitor.getQueryBNF(expression.parameterExpressionBNF(index));
        }
    }

    // Made static final for performance reasons.
    protected static final class EncapsulatedExpressionVisitor extends AnonymousExpressionVisitor {

        /**
         * Determines whether the visited {@link Expression} is being encapsulated or not.
         */
        protected boolean encapsulated;

        /**
         * Internal flag that prevent infinite recursion.
         */
        protected boolean visited;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            encapsulated = false;
        }

        /**
         * Determines whether the visited {@link Expression} is being encapsulated or not.
         *
         * @return <code>true</code> if the visited {@link Expression} is within parenthesis;
         * <code>false</code> otherwise
         */
        public boolean isEncapsulated() {
            return encapsulated;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            if (!visited) {
                visited = true;
                expression.getParent().accept(this);
                visited = false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {
            encapsulated = true;
        }
    }

    // Made static for performance reasons.
    /**
     * This builder populates a {@link QueryPosition} by traversing the valid portion of the JPQL
     * query. The position is the end of each {@link Expression}.
     * <p>
     * For instance, "SELECT e FROM Employee e O WHERE e.name = 'JPQL'", the valid fragment is
     * "SELECT e FROM Employee e", the positions will be:
     * <ul>
     * <li>JPQLExpression = 24</li>
     * <li>SelectStatement = 24</li>
     * <li>FromClause = 15</li>
     * <li>IdentificationVariableDeclaration = 10</li>
     * <li>RangeVariableDeclaration = 10</li>
     * <li>IdentificationVariable = 1</li>
     * </ul>
     */
    protected static class EndingQueryPositionBuilder
            extends AbstractVisitorHelper implements ExpressionVisitor {

        /**
         * This internal flag helps to determine if the {@link Expression} where the cursor is located
         * was flagged to be invalid. If so, this helps to determine how to handle the calculation of
         * the new position within the query.
         * <p>
         * For instance: "SELECT e FROM Employee e AS" has a bad expression wrapping the identifier
         * "AS", which is parsed as a result variable. In this case, the position would actually be
         * 2 within the bad expression. If the bad expression was something more complex than just a
         * single word, then that expression should not be included in the position.
         */
        protected boolean badExpression;

        /**
         * This is used to correct the length of an {@link Expression}
         */
        protected int correction;

        /**
         * The {@link Expression} containing the invalid fragment.
         */
        protected Expression invalidExpression;

        /**
         * The position of the cursor within the invalid expression.
         */
        protected int positionWithinInvalidExpression;

        /**
         * This {@link QueryPosition} has the position of each {@link Expression} within the valid
         * fragment of the JPQL query.
         */
        public QueryPosition queryPosition;

        /**
         * Indicates whether a virtual space should be added to the stack or not.
         */
        public boolean virtualSpace;

        protected EndingQueryPositionBuilder(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * Disposes the internal data.
         */
        public void dispose() {
            correction                      = 0;
            virtualSpace                    = false;
            queryPosition                   = null;
            invalidExpression               = null;
            positionWithinInvalidExpression = -1;
        }

        /**
         * Returns the new {@link QueryPosition} that was created.
         *
         * @return
         */
        public QueryPosition getQueryPosition() {
            return queryPosition;
        }

        /**
         * Determines whether a virtual space should be added to the stack or not.
         *
         * @return <code>true</code> if a virtual space should be considered; <code>false</code> otherwise
         */
        public boolean hasVirtualSpace() {
            return virtualSpace;
        }

        /**
         * Prepares this visitor before visiting an {@link Expression}. {@link #dispose()} is called
         * after the visit operation is complete.
         *
         * @param invalidExpression The {@link Expression} containing the invalid fragment
         */
        public void prepare(Expression invalidExpression) {

            QueryPosition oldQueryPosition = visitor.queryPosition;

            this.invalidExpression = invalidExpression;
            this.positionWithinInvalidExpression = oldQueryPosition.getPosition(invalidExpression);
            this.queryPosition = new QueryPosition(oldQueryPosition.getPosition(invalidExpression.getParent()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbsExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbstractSchemaName expression) {

            if (badExpression) {
                correction = expression.getLength() - positionWithinInvalidExpression;
            }

            if (invalidExpression == expression) {
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AdditionExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AllOrAnyExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AndExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ArithmeticFactor expression) {

            if (!badExpression) {

                if (expression.hasExpression()) {
                    expression.getExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AvgFunction expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BadExpression expression) {
            badExpression = true;
            expression.getExpression().accept(this);
            badExpression = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BetweenExpression expression) {

            if (badExpression) {

                if (!expression.hasExpression() &&
                    !expression.hasNot()        &&
                    positionWithinInvalidExpression <= 7 /* BETWEEN */) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasUpperBoundExpression()) {
                    expression.getUpperBoundExpression().accept(this);
                }
                else if (expression.hasLowerBoundExpression() &&
                        !expression.hasAnd()) {

                    expression.getLowerBoundExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CaseExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 4 /* CASE */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (!expression.hasEnd()) {

                    if (expression.hasElseExpression()) {
                        expression.getElseExpression().accept(this);
                    }
                    else if (expression.hasWhenClauses()) {
                        expression.getWhenClauses();
                    }
                    else if (expression.hasCaseOperand()) {
                        expression.getCaseOperand().accept(this);
                    }
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CoalesceExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {

            if (!badExpression) {

                // First find the index of the child expression owning the invalid fragment
                int index = 0;

                for (Expression child : expression.children()) {
                    if (child.isAncestor(invalidExpression)) {
                        break;
                    }
                    index++;
                }

                // This visitor is not used for an invalid expression but for looking at the
                // end of an expression and the whitespace is owned by a parent expression
                if (index == expression.childrenSize()) {
                    index--;
                }

                // Now traverse the child
                Expression child = expression.getChild(index);
                child.accept(this);

                // Rather than marking the CollectionExpression as the Expression to visit
                // with the adjusted QueryPosition, the child that to the left of the invalid
                // expression will be used instead.
                // Example: "SELECT e FROM Employee e WHERE CONCAT(e.name, A|S a)" <- | is the cursor
                //          In this example, "CONCAT(e.name, AS a)" is wrapped with a BadExpression, but
                //          "CONCAT(e.name, " is actually valid so this will allow the new QueryPosition
                //          to be "inside" the CONCAT expression and the proposals will be available
                if (index > 0) {
                    Expression previousChild = expression.getChild(index - 1);

                    if (!visitor.isComplete(previousChild)) {
                        queryPosition.setExpression(previousChild);
                        queryPosition.addPosition(previousChild, previousChild.getLength());
                    }
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                // Adjust the length so it's within the collection expression
                // up to the position of the cursor
                int length = expression.toActualText(index + 1).length() - correction;
                queryPosition.addPosition(expression, length);

                // Now reset the correction so the parent expression does
                // not use the entire length of CollectionExpression
                correction = expression.getLength() - length;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberDeclaration expression) {

            if (badExpression) {
                if (positionWithinInvalidExpression <= 2 /* IN */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasIdentificationVariable()) {
                    expression.getIdentificationVariable().accept(this);
                }
                else if (expression.hasCollectionValuedPathExpression() &&
                        !expression.hasAs()) {

                    expression.getCollectionValuedPathExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberExpression expression) {

            if (badExpression) {

                if (!expression.hasEntityExpression() &&
                    !expression.hasNot()              &&
                    positionWithinInvalidExpression <= 6 /* MEMBER */) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasCollectionValuedPathExpression()) {
                    expression.getCollectionValuedPathExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {

            if (badExpression) {
                correction = expression.getLength() - positionWithinInvalidExpression;
            }

            if (invalidExpression == expression) {
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, positionWithinInvalidExpression);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ComparisonExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConcatExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConstructorExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 3 /* NEW */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasConstructorItems() &&
                   !expression.hasRightParenthesis()) {

                    expression.getConstructorItems().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CountFunction expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DateTime expression) {

            if (!expression.isJDBCDate()) {
                if (badExpression) {
                    correction = expression.getLength() - positionWithinInvalidExpression;
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
                else if (invalidExpression == expression) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DeleteClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 6/* DELETE */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasRangeVariableDeclaration()) {
                    expression.getRangeVariableDeclaration().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DeleteStatement expression) {

            if (!badExpression) {

                if (expression.hasWhereClause()) {
                    expression.getWhereClause().accept(this);
                }
                else {
                    expression.getDeleteClause().accept(this);
                    if (expression.hasSpaceAfterDeleteClause()) {
                        virtualSpace = true;
                    }
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DivisionExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EmptyCollectionComparisonExpression expression) {

            if (badExpression) {

                if (!expression.hasExpression() &&
                    !expression.hasNot()        &&
                    positionWithinInvalidExpression <= 5 /* EMPTY */) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {
                if (invalidExpression == expression) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EntityTypeLiteral expression) {

            if (badExpression) {
                correction = expression.getLength() - positionWithinInvalidExpression;
            }

            if (invalidExpression == expression) {
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, positionWithinInvalidExpression);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EntryExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ExistsExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FromClause expression) {
            visitAbstractFromClause(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FunctionExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(GroupByClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 5 /* GROUP */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasGroupByItems()) {
                    expression.getGroupByItems().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(HavingClause expression) {
            visitAbstractConditionalClause(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {

            if (badExpression) {
                correction = expression.getLength() - positionWithinInvalidExpression;
            }

            queryPosition.setExpression(expression);
            queryPosition.addPosition(expression, positionWithinInvalidExpression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariableDeclaration expression) {

            if (!badExpression) {

                if (expression.hasJoins()) {
                    expression.getJoins().accept(this);
                }
                else if (expression.hasRangeVariableDeclaration()) {
                    expression.getRangeVariableDeclaration().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IndexExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(InExpression expression) {

            if (badExpression) {

                if (!expression.hasExpression() &&
                    positionWithinInvalidExpression <= 2 /* IN */) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasInItems() &&
                   !expression.hasRightParenthesis()) {

                    expression.getInItems();
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(InputParameter expression) {

            if (!badExpression) {

                if (invalidExpression == expression) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(Join expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 4 /* JOIN */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasOnClause()) {
                    expression.getOnClause().accept(this);
                }
                else if (expression.hasIdentificationVariable()) {
                    expression.getIdentificationVariable().accept(this);
                    if (expression.hasSpaceAfterIdentificationVariable()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasJoinAssociationPath() &&
                        !expression.hasAs()) {

                    expression.getJoinAssociationPath().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(JPQLExpression expression) {

            if (expression.hasQueryStatement()) {
                Expression queryStatement = expression.getQueryStatement();
                queryStatement.accept(this);
                queryPosition.addPosition(expression, queryStatement.getLength() - correction);
            }
            else {
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, 0);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(KeyExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(KeywordExpression expression) {

            if (badExpression) {
                correction = expression.getLength() - positionWithinInvalidExpression;
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, positionWithinInvalidExpression);
            }
            else if (invalidExpression == expression) {
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, positionWithinInvalidExpression);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LengthExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LikeExpression expression) {

            if (badExpression) {

                if (!expression.hasStringExpression() &&
                    positionWithinInvalidExpression <= 4 /* LIKE */) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasEscapeCharacter()) {
                    expression.getEscapeCharacter().accept(this);
                }
                else if (expression.hasPatternValue() &&
                        !expression.hasEscape()) {

                    expression.getPatternValue().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LocateExpression expression) {
            visitAbstractTripleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LowerExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MaxFunction expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MinFunction expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ModExpression expression) {
            visitAbstractDoubleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MultiplicationExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NotExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 3 /* NOT */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasExpression()) {
                    expression.getExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullComparisonExpression expression) {

            if (badExpression) {

                if (!expression.hasExpression() &&
                   !expression.hasNot()         &&
                   positionWithinInvalidExpression <= 2) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasExpression()) {
                    expression.getExpression().accept(this);
                }

                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullExpression expression) {

            if (!badExpression) {

                if (invalidExpression == expression) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, 0);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullIfExpression expression) {
            visitAbstractDoubleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NumericLiteral expression) {

            if (!badExpression) {

                if (invalidExpression == expression) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ObjectExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OnClause expression) {
            visitAbstractConditionalClause(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrderByClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 5 /* ORDER */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasOrderByItems()) {
                    expression.getOrderByItems().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrderByItem expression) {

            if (!badExpression) {

                if (expression.hasExpression()) {
                    expression.getExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {

            if (!badExpression) {

                if (expression.hasIdentificationVariable()) {
                    expression.getIdentificationVariable().accept(this);
                }
                else if (!expression.hasAs()) {
                    expression.getRootObject().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ResultVariable expression) {

            if (badExpression) {

                // Special case for a result variable with nothing but the AS identifier
                if (!expression.hasResultVariable() &&
                     expression.hasAs() ||
                    !expression.hasSelectExpression()) {

                    correction = expression.getLength() - positionWithinInvalidExpression;

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasResultVariable()) {
                    expression.getResultVariable().accept(this);
                }
                else if (!expression.hasAs()) {
                    expression.getSelectExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 6 /* SELECT */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasSelectExpression()) {
                    expression.getSelectExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectStatement expression) {

            if (!badExpression) {

                if (expression.hasUnionClauses()) {
                    expression.getUnionClauses().accept(this);
                }
                else if (expression.hasOrderByClause()) {
                    expression.getOrderByClause().accept(this);
                    if (expression.hasSpaceBeforeUnion()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasHavingClause()) {
                    expression.getHavingClause().accept(this);
                    if (expression.hasSpaceBeforeOrderBy()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasGroupByClause()) {
                    expression.getGroupByClause().accept(this);
                    if (expression.hasSpaceAfterGroupBy()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasWhereClause()) {
                    expression.getWhereClause().accept(this);
                    if (expression.hasSpaceAfterWhere()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasFromClause()) {
                    expression.getFromClause().accept(this);
                    if (expression.hasSpaceAfterFrom()) {
                        virtualSpace = true;
                    }
                }
                else {
                    expression.getSelectClause().accept(this);
                    if (expression.hasSpaceAfterSelect()) {
                        virtualSpace = true;
                    }
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleFromClause expression) {
            visitAbstractFromClause(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectClause expression) {

            if (!badExpression) {

                if (expression.hasSelectExpression()) {
                    expression.getSelectExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {

            if (!badExpression) {

                if (expression.hasHavingClause()) {
                    expression.getHavingClause().accept(this);
                }
                else if (expression.hasGroupByClause()) {
                    expression.getGroupByClause().accept(this);
                    if (expression.hasSpaceAfterGroupBy()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasWhereClause()) {
                    expression.getWhereClause().accept(this);
                    if (expression.hasSpaceAfterWhere()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasFromClause()) {
                    expression.getFromClause().accept(this);
                    if (expression.hasSpaceAfterFrom()) {
                        virtualSpace = true;
                    }
                }
                else {
                    expression.getSelectClause().accept(this);
                    if (expression.hasSpaceAfterSelect()) {
                        virtualSpace = true;
                    }
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SizeExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SqrtExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StateFieldPathExpression expression) {

            if (badExpression) {
                correction = expression.getLength() - positionWithinInvalidExpression;
            }

            if (invalidExpression == expression) {
                queryPosition.setExpression(expression);
                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StringLiteral expression) {

            if (!badExpression) {

                if (invalidExpression == expression) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubstringExpression expression) {
            visitAbstractTripleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubtractionExpression expression) {
            visitCompoundExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SumFunction expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TreatExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 5 /* TREAT */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasEntityType() &&
                   !expression.hasRightParenthesis()) {

                    expression.getEntityType().accept(this);
                }
                else if (expression.hasCollectionValuedPathExpression() &&
                        !expression.hasAs()) {

                    expression.getCollectionValuedPathExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TrimExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TypeExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UnknownExpression expression) {
            // Nothing to do, this is the expression that needs
            // to be handled by the valid portion of the JPQL query
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 6 /* UPDATE */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasUpdateItems()) {
                    expression.getUpdateItems().accept(this);
                }
                else if (expression.hasRangeVariableDeclaration()) {
                    expression.getRangeVariableDeclaration().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateItem expression) {

            if (!badExpression) {

                if (expression.hasNewValue()) {
                    expression.getNewValue().accept(this);
                }
                else if (!expression.hasEqualSign() &&
                          expression.hasSpaceAfterStateFieldPathExpression()) {

                    expression.getStateFieldPathExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateStatement expression) {

            if (!badExpression) {

                if (expression.hasWhereClause()) {
                    expression.getWhereClause().accept(this);
                }
                else {
                    expression.getUpdateClause().accept(this);
                    if (expression.hasSpaceAfterUpdateClause()) {
                        virtualSpace = true;
                    }
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpperExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ValueExpression expression) {
            visitAbstractSingleEncapsulatedExpression(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhenClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 4 /* WHEN */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasThenExpression()) {
                    expression.getThenExpression().accept(this);
                }
                else if (expression.hasWhenExpression() &&
                         expression.hasThen()) {

                    expression.getWhenExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhereClause expression) {
            visitAbstractConditionalClause(expression);
        }

        protected void visitAbstractConditionalClause(AbstractConditionalClause expression) {

            if (!badExpression) {

                if (expression.hasConditionalExpression()) {
                    expression.getConditionalExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        protected void visitAbstractDoubleEncapsulatedExpression(AbstractDoubleEncapsulatedExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= expression.getIdentifier().length()) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasSecondExpression() &&
                   !expression.hasRightParenthesis()) {

                    expression.getSecondExpression().accept(this);
                }
                else if (expression.hasFirstExpression() &&
                        !expression.hasSpaceAfterComma()) {

                    expression.getFirstExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        protected void visitAbstractFromClause(AbstractFromClause expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= 4 /* FROM */) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasAsOfClause()) {
                    expression.getAsOfClause().accept(this);
                }
                else if (expression.hasHierarchicalQueryClause()) {

                    expression.getHierarchicalQueryClause().accept(this);

                    if (expression.hasSpaceAfterHierarchicalQueryClause()) {
                        virtualSpace = true;
                    }
                }
                else if (expression.hasDeclaration()) {

                    expression.getDeclaration().accept(this);

                    if (expression.hasSpaceAfterDeclaration()) {
                        virtualSpace = true;
                    }
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        protected void visitAbstractSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= expression.getIdentifier().length()) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasEncapsulatedExpression() &&
                   !expression.hasRightParenthesis()) {

                    expression.getExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        protected void visitAbstractTripleEncapsulatedExpression(AbstractTripleEncapsulatedExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= expression.getIdentifier().length()) {
                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasThirdExpression() &&
                   !expression.hasRightParenthesis()) {

                    expression.getThirdExpression().accept(this);
                }
                else if (expression.hasSecondExpression() &&
                        !expression.hasSecondComma()) {

                    expression.getSecondExpression().accept(this);
                }
                else if (expression.hasFirstExpression() &&
                        !expression.hasFirstComma()) {

                    expression.getFirstExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }

        protected void visitCompoundExpression(CompoundExpression expression) {

            if (badExpression) {

                if (positionWithinInvalidExpression <= expression.getIdentifier().length() &&
                    !expression.hasLeftExpression()) {

                    queryPosition.setExpression(expression);
                    queryPosition.addPosition(expression, positionWithinInvalidExpression);
                }
            }
            else {

                if (expression.hasRightExpression()) {
                    expression.getRightExpression().accept(this);
                }

                if (queryPosition.getExpression() == null) {
                    queryPosition.setExpression(expression);
                }

                queryPosition.addPosition(expression, expression.getLength() - correction);
            }
        }
    }

    // Made static final for performance reasons.
    /**
     * This visitor determines whether a path expression can be resolved as a fully qualified enum
     * type and an enum constant.
     * <p>
     * The valid locations are:
     * <ul>
     * <li>{@link CollectionMemberExpression} : entity_or_value_expression (before <code><b>MEMBER</b></code> identifier);</li>
     * <li>{@link InExpression} : One of the items;</li>
     * <li>{@link CaseExpression} : The <code><b>ELSE</b> expression</code>;</li>
     * <li>{@link WhenClause} : The <code><b>WHEN</b></code> or <code><b>THEN</b></code> expressions;</li>
     * <li>{@link FunctionExpression} : One of the function items;</li>
     * <li>{@link ComparisonExpression} : The left or right expression if the comparison identifier
     *     is either <code><b>=</b></code> or <code><b>&lt;&gt;</b></code>;</li>
     * <li>{@link UpdateItem} : The new value;</li>
     * <li>{@link ConstructorExpression} : One of the constructor items;</li>
     * <li>{@link CoalesceExpression} : The expression at index 1 or greater;</li>
     * <li>{@link NullIfExpression} : The second expression;</li>
     * </ul>
     */
    protected static final class EnumVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link AbstractPathExpression} being scanned for its location within the JPQL query.
         */
        protected AbstractPathExpression pathExpression;

        /**
         * Determines whether the path expression could potentially represent a fully qualified
         * enum constant, which is dictated by the location of the path expression within the query.
         * Only a few location allows an enum constant.
         */
        protected boolean valid;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            valid = false;
            pathExpression = null;
        }

        /**
         * Determines whether the path expression could potentially represent a fully qualified
         * enum constant, which is dictated by the location of the path expression within the query.
         * Only a few location allows an enum constant.
         *
         * @return <code>true</code> if the path expression represents a enum constant;
         * <code>false</code> otherwise
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CaseExpression expression) {
            valid = (pathExpression == expression.getElseExpression());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CoalesceExpression expression) {
            // TODO
            valid = (pathExpression == expression.getExpression());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberExpression expression) {
            valid = (pathExpression == expression.getEntityExpression());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            expression.getParent().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ComparisonExpression expression) {
            String identifier = expression.getComparisonOperator();
            valid = ((identifier == EQUAL) || (identifier == DIFFERENT));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConstructorExpression expression) {
            valid = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FunctionExpression expression) {
            valid = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(InExpression expression) {
            valid = (pathExpression != expression.getExpression());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullIfExpression expression) {
            valid = (pathExpression == expression.getSecondExpression());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StateFieldPathExpression expression) {
            expression.getParent().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateItem expression) {
            valid = (pathExpression == expression.getNewValue());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhenClause expression) {
            valid = (pathExpression == expression.getThenExpression() ||
                     pathExpression == expression.getWhenExpression());
        }
    }

    // Made static final for performance reasons.
    /**
     * This {@link MappingCollector} returns the possible mappings (non-collection type or
     * collection type) from a managed type.
     */
    protected static final class FilteringMappingCollector implements MappingCollector {

        /**
         * The {@link Filter} used to filter out either the collection type properties or the non-
         * collection type properties.
         */
        protected final Filter<IMapping> filter;

        /**
         * This resolver is used to retrieve the managed type, which is the parent path of this one.
         */
        protected final Resolver resolver;

        /**
         * The suffix is used to determine if the mapping name needs to be filtered out or not.
         */
        protected final String suffix;

        /**
         * Creates a new <code>FilteringMappingCollector</code>.
         *
         * @param resolver This resolver is used to retrieve the managed type, which is the parent
         * path of this one
         * @param filter The filter used to filter out either the collection type properties or the
         * non-collection type properties
         * @param suffix The suffix is used to determine if the property name needs to be filtered out
         * or not
         */
        FilteringMappingCollector(Resolver resolver, Filter<IMapping> filter, String suffix) {
            super();
            this.filter   = filter;
            this.suffix   = suffix;
            this.resolver = resolver;
        }

        protected void addFilteredMappings(IManagedType managedType, List<IMapping> mappings) {

            Filter<IMapping> filter = buildFilter(suffix);

            for (IMapping mapping : managedType.mappings()) {
                if (filter.accept(mapping)) {
                    mappings.add(mapping);
                }
            }
        }

        protected Filter<IMapping> buildFilter(String suffix) {
            if (suffix.length() == 0) {
                return filter;
            }
            return new AndFilter<IMapping>(filter, buildMappingNameFilter(suffix));
        }

        protected Filter<IMapping> buildMappingNameFilter(final String suffix) {
            return new Filter<IMapping>() {
                @Override
                public boolean accept(IMapping mapping) {
                    return mapping.getName().startsWith(suffix);
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Collection<IMapping> buildProposals() {

            IManagedType managedType = resolver.getManagedType();

            if (managedType == null) {
                return Collections.emptyList();
            }

            ArrayList<IMapping> mappings = new ArrayList<IMapping>();
            addFilteredMappings(managedType, mappings);
            return mappings;
        }
    }

    // Made static for performance reasons.
    protected static class FollowingClausesVisitor extends AbstractTraverseParentVisitor {

        /**
         * The JPQL identifier of the clause used to determine if there is any clause defined after it.
         */
        protected String afterIdentifier;

        /**
         * The JPQL identifier of the clause used to determine if there is any clause defined before it.
         */
        protected String beforeIdentifier;

        /**
         * Determines whether there is at least one clause defined after the clause defined by {@link #afterIdentifier}.
         */
        protected boolean hasFollowUpClauses;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            afterIdentifier    = null;
            beforeIdentifier   = null;
            hasFollowUpClauses = false;
        }

        /**
         * Determines if the <code><b>FROM</b></code> clause has been defined or not. The end limit
         * of the check is also taken into consideration.
         *
         * @param expression The <code><b>SELECT</b></code> expression being scanned for what has been
         * defined between the range of clauses
         * @return <code>true</code> if the <code><b>FROM</b></code> clause is defined;
         * <code>false</code> otherwise
         */
        protected boolean hasFromClause(AbstractSelectStatement expression) {
            // No need to check if the check is after the clause itself
            return (afterIdentifier != FROM) && expression.hasFromClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectStatement expression) {

            if (afterIdentifier == SELECT) {

                if (beforeIdentifier == WHERE) {
                    hasFollowUpClauses = hasFromClause(expression);
                }
                else if (beforeIdentifier == GROUP_BY) {
                    hasFollowUpClauses = hasFromClause(expression) ||
                                         expression.hasWhereClause();
                }
                else if (beforeIdentifier == HAVING) {
                    hasFollowUpClauses = hasFromClause(expression)   ||
                                         expression.hasWhereClause() ||
                                         expression.hasGroupByClause();
                }
                else if (beforeIdentifier == ORDER_BY) {
                    hasFollowUpClauses = hasFromClause(expression)     ||
                                         expression.hasWhereClause()   ||
                                         expression.hasGroupByClause() ||
                                         expression.hasHavingClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = hasFromClause(expression)     ||
                                         expression.hasWhereClause()   ||
                                         expression.hasGroupByClause() ||
                                         expression.hasHavingClause()  ||
                                         expression.hasOrderByClause();
                }
                else {
                    hasFollowUpClauses = hasFromClause(expression);
                }
            }
            else if (afterIdentifier == FROM) {

                if (beforeIdentifier == GROUP_BY) {
                    hasFollowUpClauses = hasFromClause(expression) ||
                                         expression.hasWhereClause();
                }
                else if (beforeIdentifier == HAVING) {
                    hasFollowUpClauses = hasFromClause(expression)   ||
                                         expression.hasWhereClause() ||
                                         expression.hasGroupByClause();
                }
                else if (beforeIdentifier == ORDER_BY) {
                    hasFollowUpClauses = hasFromClause(expression)     ||
                                         expression.hasWhereClause()   ||
                                         expression.hasGroupByClause() ||
                                         expression.hasHavingClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = hasFromClause(expression)     ||
                                         expression.hasWhereClause()   ||
                                         expression.hasGroupByClause() ||
                                         expression.hasHavingClause()  ||
                                         expression.hasOrderByClause();
                }
                else {
                    hasFollowUpClauses = hasFromClause(expression);
                }
            }
            else if (afterIdentifier == WHERE) {

                if (beforeIdentifier == HAVING) {
                    hasFollowUpClauses = expression.hasGroupByClause();
                }
                else if (beforeIdentifier == ORDER_BY) {
                    hasFollowUpClauses = expression.hasGroupByClause() ||
                                         expression.hasHavingClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = expression.hasGroupByClause() ||
                                         expression.hasHavingClause()  ||
                                         expression.hasOrderByClause();
                }
            }
            else if (afterIdentifier == GROUP_BY) {

                if (beforeIdentifier == ORDER_BY) {
                    hasFollowUpClauses = expression.hasHavingClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = expression.hasHavingClause() ||
                                         expression.hasOrderByClause();
                }
            }
            else if (afterIdentifier == HAVING) {
                hasFollowUpClauses = expression.hasOrderByClause();
            }
            else {
                hasFollowUpClauses = hasFromClause(expression);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleFromClause expression) {
            expression.getParent().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {

            if (afterIdentifier == SELECT) {

                if (beforeIdentifier == WHERE) {
                    hasFollowUpClauses = hasFromClause(expression);
                }
                else if (beforeIdentifier == GROUP_BY) {
                    hasFollowUpClauses = hasFromClause(expression) ||
                                         expression.hasWhereClause();
                }
                else if (beforeIdentifier == HAVING) {
                    hasFollowUpClauses = hasFromClause(expression)   ||
                                         expression.hasWhereClause() ||
                                         expression.hasGroupByClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = hasFromClause(expression)     ||
                                         expression.hasWhereClause()   ||
                                         expression.hasGroupByClause() ||
                                         expression.hasHavingClause();
                }
            }
            else if (afterIdentifier == FROM) {

                if (beforeIdentifier == GROUP_BY) {
                    hasFollowUpClauses = hasFromClause(expression) ||
                                         expression.hasWhereClause();
                }
                else if (beforeIdentifier == HAVING) {
                    hasFollowUpClauses = hasFromClause(expression)   ||
                                         expression.hasWhereClause() ||
                                         expression.hasGroupByClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = hasFromClause(expression)     ||
                                         expression.hasWhereClause()   ||
                                         expression.hasGroupByClause() ||
                                         expression.hasHavingClause();
                }
            }
            else if (afterIdentifier == WHERE) {

                if (beforeIdentifier == HAVING) {
                    hasFollowUpClauses = expression.hasGroupByClause();
                }
                else if (beforeIdentifier == null) {
                    hasFollowUpClauses = expression.hasGroupByClause() ||
                                         expression.hasHavingClause();
                }
            }
        }
    }

    // Made static final for performance reasons.
    protected static final class FollowingInvalidExpressionVisitor extends AbstractTraverseParentVisitor {

        /**
         * Enclosing visitor instance.
         */
        protected final AbstractContentAssistVisitor visitor;

        /**
         * The {@link Expression} used to determine if it follows an invalid fragment or not.
         */
        protected Expression expression;

        /**
         * Determines whether the visited {@link Expression} is preceded by an invalid expression.
         */
        protected boolean followingInvalidExpression;

        protected FollowingInvalidExpressionVisitor(AbstractContentAssistVisitor visitor) {
            super();
            this.visitor = visitor;
        }

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
            followingInvalidExpression = false;
        }

        /**
         * Determines whether the visited {@link Expression} is preceded by an invalid expression.
         *
         * @return <code>true</code> if the visited {@link Expression} is part of a collection of
         * expressions and an invalid expression precede it; <code>false</code> otherwise
         */
        public boolean isFollowingInvalidExpression() {
            return followingInvalidExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {

            int index = expression.childrenSize();

            // Find the index of the expression for which the tree is been visited
            while (--index >= 0) {
                Expression child = expression.getChild(index);

                if (child == this.expression) {
                    break;
                }
            }

            // Check to see if an expression before the index is invalid
            while (--index >= 0) {
                Expression child = expression.getChild(index);
                followingInvalidExpression = visitor.isInvalidExpression(child);

                if (followingInvalidExpression) {
                    index = -1;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            this.expression = expression;
            super.visit(expression);
        }
    }

    // Made static for performance reasons.
    protected static class FromClauseCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<AbstractFromClause> {

        protected FromClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(AbstractFromClause expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // At the end of a range variable declaration, JOIN clauses can be added
            // Example: "SELECT e FROM Employee e |"
            // Example: "SELECT e FROM Employee e, Address a |"
            if (((index == 0) || hasComma) && virtualSpace) {
                // "... JOIN e.mananager m |" <- Valid to add JOIN identifiers
                // "... JOIN e.mananager m ON e.name |" <- Invalid to add JOIN identifiers
                if (visitor.isComplete(collectionExpression.getChild(0))) {
                    visitor.addJoinIdentifiers();
                    visitor.addIdentifier(ON);
                }
            }
            // Special case to handle a range variable declaration that can also
            // be either the beginning of the following clauses
            else {
                 boolean end = (index + 1 == collectionExpression.childrenSize());

                 if ((index > 0) && end && !hasComma) {

                    int position = visitor.queryPosition.getPosition();

                    if (!visitor.hasClausesDefinedBetween(expression, FROM, HAVING)) {
                        visitor.addCompositeIdentifier(GROUP_BY, 4 /* GROUP - 1 */);
                    }

                    if (!visitor.hasClausesDefinedBetween(expression, FROM, ORDER_BY)) {
                        visitor.addCompositeIdentifier(ORDER_BY, 4 /* ORDER - 1 */);
                    }
                 }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(AbstractFromClause expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(AbstractFromClause expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            // 1. At the beginning of the FROM declaration, entities are valid proposals
            // 2. To be valid elsewhere, the declarations have to be separated by a comma
            //    "SELECT e FROM Employee e W|" <- entity names are not valid proposals, only 'WHERE' is
            if ((index == 0) || hasComma) {
                visitor.addEntities();
            }
            // In any other case, the JOIN identifiers are the only valid choices
            // "SELECT e FROM Employee e J|" <- only 'JOIN' and 'JOIN FETCH' are valid proposals
            else if ((index > 0) && !hasComma) {
                visitor.addJoinIdentifiers();
            }

            // The identifier for a collection member declaration can only be added
            // after the first declaration, as long as there is a comma before it
            // "SELECT e FROM Employee e, |" <- 'IN' is a valid proposal
            // "SELECT e FROM Employee e, I|" <- 'IN' is a valid proposal
            // "SELECT e FROM Employee e I|" <- 'IN' is NOT a valid proposal
            if ((index > 0) && hasComma) {
                visitor.addIdentifier(IN);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(AbstractFromClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getDeclaration());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(AbstractFromClause expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(AbstractFromClause expression) {
            return expression.hasSpaceAfterFrom();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(AbstractFromClause expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(AbstractFromClause expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(AbstractFromClause expression, int index) {
            return visitor.getQueryBNF(expression.getDeclarationQueryBNFId());
        }
    }

    // Made static for performance reasons.
    protected static class FromClauseStatementHelper extends AbstractFromClauseStatementHelper<SelectStatement> {

        protected FromClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WhereClauseSelectStatementHelper getNextHelper() {
            return visitor.getWhereClauseSelectStatementHelper();
        }
    }

    // Made static final for performance reasons.
    protected static final class GroupByClauseCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<GroupByClause> {

        protected GroupByClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(GroupByClause expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(GroupByClause expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(GroupByClause expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            if ((index == 0) || hasComma) {
                visitor.addFunctionIdentifiers(GroupByItemBNF.ID);
                visitor.addIdentificationVariables();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(GroupByClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getGroupByItems());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(GroupByClause expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(GroupByClause expression) {
            return expression.hasSpaceAfterGroupBy();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(GroupByClause expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(GroupByClause expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(GroupByClause expression, int index) {
            return visitor.getQueryBNF(GroupByItemBNF.ID);
        }
    }

    // Made static final for performance reasons.
    protected static final class GroupByClauseStatementHelper extends AbstractGroupByClauseStatementHelper<SelectStatement> {

        protected GroupByClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HavingClauseStatementHelper getNextHelper() {
            return visitor.getHavingClauseStatementHelper();
        }
    }

    // Made static final for performance reasons.
    protected static final class HavingClauseStatementHelper extends AbstractHavingClauseStatementHelper<SelectStatement> {

        protected HavingClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OrderByClauseStatementHelper getNextHelper() {
            return visitor.getOrderByClauseStatementHelper();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(SelectStatement expression) {
            return expression.hasSpaceBeforeOrderBy();
        }
    }

    // Made static for performance reasons.
    /**
     * The various ways of retrieving identification variables from the declaration expression.
     */
    protected static enum IdentificationVariableType {

        /**
         * Retrieves all the identification variables declared in the declaration portion of the
         * expression, which is either in the <b>FROM</b> clause of a <b>SELECT</b> query or
         * <b>DELETE</b> query or in the <b>UPDATE</b> query.
         */
        ALL {

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean add(AbstractContentAssistVisitor contentAssist,
                                  Declaration declaration,
                               Expression expression) {

                if (declaration.getType() == Type.RANGE) {
                    contentAssist.addRangeIdentificationVariable(declaration.getVariableName());
                }
                else {
                    contentAssist.addIdentificationVariable(declaration.getVariableName());
                }

                for (Join join : declaration.getJoins()) {

                    String variableName = contentAssist.queryContext.literal(
                        join.getIdentificationVariable(),
                        LiteralType.IDENTIFICATION_VARIABLE
                    );

                    contentAssist.addIdentificationVariable(variableName);
                }

                return false;
            }
        },

        /**
         * Only retrieves the identification variables that map a path expression defined in a
         * <b>JOIN</b> expression or in a <b>IN</b> expression.
         */
        COLLECTION {

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean add(AbstractContentAssistVisitor contentAssist,
                                  Declaration declaration,
                               Expression expression) {

                contentAssist.addIdentificationVariable(declaration.getVariableName());

                for (Join join : declaration.getJoins()) {

                    String variableName = contentAssist.queryContext.literal(
                        join.getIdentificationVariable(),
                        LiteralType.IDENTIFICATION_VARIABLE
                    );

                    contentAssist.addIdentificationVariable(variableName);
                }

                return false;
            }
        },

        /**
         * Only retrieves the identification variables that have been declared to the left of the
         * expression requested them, both range and collection type variables are collected.
         */
        LEFT {

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean add(AbstractContentAssistVisitor contentAssist,
                                  Declaration declaration,
                               Expression expression) {

                boolean shouldStop = declaration.getDeclarationExpression().isAncestor(expression);

                if (shouldStop && !declaration.getJoins().contains(expression)) {
                    return true;
                }

                if (declaration.getType() == Type.RANGE) {
                    contentAssist.addRangeIdentificationVariable(declaration.getVariableName());
                }
                else {
                    contentAssist.addIdentificationVariable(declaration.getVariableName());
                }

                for (Join join : declaration.getJoins()) {

                    if (join.isAncestor(expression)) {
                        return true;
                    }

                    String variableName = contentAssist.queryContext.literal(
                        join.getIdentificationVariable(),
                        LiteralType.IDENTIFICATION_VARIABLE
                    );

                    contentAssist.addIdentificationVariable(variableName);
                }

                return false;
            }
        },

        /**
         * Only retrieves the identification variables that map a path expression defined in a
         * <b>JOIN</b> expression or in a <b>IN</b> expression but that have been declared to the
         * left of the expression requested them.
         */
        LEFT_COLLECTION {

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean add(AbstractContentAssistVisitor contentAssist,
                                  Declaration declaration,
                               Expression expression) {

                boolean shouldStop = declaration.getDeclarationExpression().isAncestor(expression);

                if (shouldStop && declaration.getJoins().contains(expression)) {
                    return true;
                }

                if (!shouldStop && (declaration.getType() == Type.COLLECTION)) {
                    contentAssist.addIdentificationVariable(declaration.getVariableName());
                }
                else {

                    for (Join join : declaration.getJoins()) {

                        if (join.isAncestor(expression)) {
                            return true;
                        }

                        String variableName = contentAssist.queryContext.literal(
                            join.getIdentificationVariable(),
                            LiteralType.IDENTIFICATION_VARIABLE
                        );

                        contentAssist.addIdentificationVariable(variableName);
                    }
                }

                return false;
            }
        },

        /**
         * Simply indicates the identification variables should not be collected.
         */
        NONE {

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean add(AbstractContentAssistVisitor contentAssist,
                                  Declaration declaration,
                               Expression expression) {

                // Nothing to do, stop immediately
                return true;
            }
        };

        /**
         * Adds the identification variables defined in the given {@link Declaration}.
         *
         * @param contentAssist Backpointer to the content assist class
         * @param declaration The {@link Declaration} in the order they are declared in the declaration clause
         * @param expression The {@link Expression} being visited, which can help to determine when to
         * stop collecting identification variables
         */
        protected abstract boolean add(AbstractContentAssistVisitor contentAssist,
                                       Declaration declaration,
                                       Expression expression);
    }

    // Made static for performance reasons.
    /**
     * This visitor is used when a clause or a compound expression was parsed with a collection of
     * expressions representing an invalid fragment.
     * <p>
     * Example: <code>SELECT e FROM Employee e GROUP B</code>
     * <p>
     * In this example, the <code><b>FROM</b></code> clause contains a collection of two
     * identification variable declarations, in a valid query, it would be separated by a comma, but
     * this one just means it's incomplete and "GROUP B" is the beginning of the <code><b>GROUP BY</b></code>
     * clause.
     */
    protected static class IncompleteCollectionExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * The clause being visited, which is marked by its JPQL identifier.
         */
        protected String clause;

        /**
         * Determines whether an {@link Expression} that was visited is complete or if some part is missing.
         */
        protected boolean complete;

        /**
         * This flag is used to make sure only the last expression in a collection is tested. A single
         * expression cannot be used to check the "completeness".
         */
        protected boolean insideCollection;

        /**
         * Returns the list of identifiers for the clauses following the given identifier.
         *
         * @param afterIdentifier The JPQL identifier of the clause for which the list of following
         * clauses is built
         * @return The list of JPQL identifiers defining the clauses following the clause specified
         * by the given identifier
         */
        protected List<String> compositeIdentifiersAfter(String afterIdentifier) {

            if (clause == FROM) {
                return CollectionTools.list(GROUP_BY, ORDER_BY);
            }

            if (clause == WHERE) {
                return CollectionTools.list(GROUP_BY, ORDER_BY);
            }

            if (clause == HAVING) {
                return CollectionTools.list(ORDER_BY);
            }

            return new LinkedList<String>();
        }

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            complete = false;
        }

        /**
         * Determines whether an {@link Expression} that was visited is complete or if some part is missing.
         *
         * @return <code>true</code> if the visited {@link Expression} is grammatically complete;
         * <code>false</code> if it is incomplete
         */
        public boolean isComplete() {
            return complete;
        }

        /**
         * Determines whether the given JPQL fragment, which is the parsed text of the expression
         * invalid collection expression or the portion of it based on the cursor position within the
         * collection, is a composite identifier or not.
         *
         * @param clause The JPQL identifier of the clause having the collection of expressions
         * @param fragment The parsed text of the expression to determine if it's the beginning of the
         * composite identifier or not. The fragment is all lower case characters
         */
        protected boolean isPossibleCompositeIdentifier(String clause, String fragment) {

            for (String identifier : compositeIdentifiersAfter(clause)) {
                if (identifier.toLowerCase().startsWith(fragment)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {

            int lastIndex = expression.childrenSize() - 1;
            insideCollection = true;

            if (!expression.hasComma(lastIndex - 1)) {
                expression.getChild(lastIndex).accept(this);
            }

            insideCollection = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FromClause expression) {
            clause = FROM;
            expression.getDeclaration().accept(this);
            clause = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(GroupByClause expression) {
            clause = GROUP_BY;
            expression.getGroupByItems().accept(this);
            clause = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(HavingClause expression) {
            clause = HAVING;
            expression.getConditionalExpression().accept(this);
            clause = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {
            complete = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariableDeclaration expression) {
            if (insideCollection && !expression.hasJoins()) {
                expression.getRangeVariableDeclaration().accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrderByClause expression) {
            clause = ORDER_BY;
            expression.getOrderByItems().accept(this);
            clause = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {

            if (insideCollection) {

                // The "root" object could be the first identifier of a composite identifier
                // (eg: GROUP) and the identification variable is 'B'
                complete = !expression.hasAs() && !expression.hasIdentificationVariable();

                // Special case for composite identifiers
                if (!complete) {
                    String fragment = expression.toParsedText().toLowerCase();
                    complete = isPossibleCompositeIdentifier(clause, fragment);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhereClause expression) {
            clause = WHERE;
            expression.getConditionalExpression().accept(this);
            clause = null;
        }
    }

    // Made static final for performance reasons.
    /**
     * This visitor determines if the visited {@link Expression} is one of the two that represents
     * an invalid expression.
     */
    protected static final class InvalidExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * The invalid {@link Expression}, which is either {@link UnknownExpression} or {@link BadExpression}.
         */
        protected Expression expression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
        }

        /**
         * Determines whether the visited {@link Expression} represents an invalid fragment.
         *
         * @return <code>true</code> if the {@link Expression} is an invalid fragment;
         * <code>false</code> otherwise
         */
        public boolean isInvalid() {
            return expression != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BadExpression expression) {
            this.expression = expression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UnknownExpression expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class JoinCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<IdentificationVariableDeclaration> {

        protected JoinCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(IdentificationVariableDeclaration expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(IdentificationVariableDeclaration expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(IdentificationVariableDeclaration expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            visitor.addJoinIdentifiers();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(IdentificationVariableDeclaration expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getJoins());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(IdentificationVariableDeclaration expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(IdentificationVariableDeclaration expression) {
            return expression.hasSpace();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(IdentificationVariableDeclaration expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(IdentificationVariableDeclaration expression) {
            return expression.getRangeVariableDeclaration().getLength();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(IdentificationVariableDeclaration expression, int index) {
            return visitor.getQueryBNF(InternalJoinBNF.ID);
        }
    }

    // Made static for performance reasons.
    /**
     * A collector is responsible to retrieve the possible proposals by using the mappings that can
     * complete a path expression.
     */
    protected static interface MappingCollector {

        /**
         * Retrieves the possible proposals that can be used to complete a path expression based on
         * the position of the cursor.
         *
         * @return The possible proposals
         */
        Collection<IMapping> buildProposals();
    }

    // Made static final for performance reasons.
    /**
     * This visitor is responsible to create the right {@link Filter} based on the type of the {@link Expression}.
     */
    protected static final class MappingFilterBuilder extends AbstractTraverseParentVisitor {

        /**
         * Enclosing visitor instance.
         */
        protected final AbstractContentAssistVisitor visitor;

        /**
         * The {@link Filter} that will filter the various type of {@link IMapping IMappings} based
         * on the location of the of the path expression within the JPQL query.
         */
        protected Filter<IMapping> filter;

        protected MappingFilterBuilder(AbstractContentAssistVisitor visitor) {
            super();
            this.visitor = visitor;
        }

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            filter = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbsExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AvgFunction expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BetweenExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CoalesceExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberDeclaration expression) {
            filter = visitor.getMappingCollectionFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            filter = visitor.getMappingCollectionFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConcatExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CountFunction expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(EmptyCollectionComparisonExpression expression) {
            filter = visitor.getMappingCollectionFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FunctionExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(HavingClause expression) {
            // No need to filter, everything is allowed
            // Example: SELECT e FROM Employee e WHERE e.|
            //          1. Could become 'e.name = 'JPQL''
            //          2. Could become 'e.employees IS NOT NULL'
            //          3. Could become e.address.zipcode = 27519
            filter = NullFilter.instance();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(Join expression) {
            filter = visitor.getMappingCollectionFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(JPQLExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LengthExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LocateExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LowerExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MaxFunction expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MinFunction expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ModExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullComparisonExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OnClause expression) {
            // No need to filter, everything is allowed
            // Example: SELECT e FROM Employee e WHERE e.|
            //          1. Could become 'e.name = 'JPQL''
            //          2. Could become 'e.employees IS NOT NULL'
            //          3. Could become e.address.zipcode = 27519
            filter = NullFilter.instance();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SizeExpression expression) {
            filter = visitor.getMappingCollectionFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SqrtExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubstringExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SumFunction expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TreatExpression expression) {
            filter = visitor.getMappingCollectionFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TrimExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpperExpression expression) {
            filter = visitor.getMappingPropertyFilter();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhenClause expression) {
            // No need to filter, everything is allowed
            // Example: SELECT e FROM Employee e WHERE e.|
            //          1. Could become 'e.name = 'JPQL''
            //          2. Could become 'e.employees IS NOT NULL'
            //          3. Could become e.address.zipcode = 27519
            filter = NullFilter.instance();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(WhereClause expression) {
            // No need to filter, everything is allowed
            // Example: SELECT e FROM Employee e WHERE e.|
            //          1. Could become 'e.name = 'JPQL''
            //          2. Could become 'e.employees IS NOT NULL'
            //          3. Could become e.address.zipcode = 27519
            filter = NullFilter.instance();
        }
    }

    // Made static final for performance reasons.
    /**
     * This {@link Filter} is responsible to filter out the mappings that can't have their type
     * assignable to the one passed in.
     */
    protected static final class MappingTypeFilter
            extends AbstractVisitorHelper implements Filter<IMapping> {

        /**
         * The type used to determine if the mapping's type is a valid type.
         */
        protected final IType type;

        /**
         * Creates a new <code>MappingTypeFilter</code>.
         *
         * @param type The type used to determine if the mapping's type is a valid type
         */
        MappingTypeFilter(IType type, AbstractContentAssistVisitor visitor) {
            super(visitor);
            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(IMapping value) {

            // A reference mapping always can be used for further traversal
            if (value.isRelationship() &&
               !value.isCollection()) {

                return true;
            }

            // Determine if it's assignable to the desired type
            IType mappingType = value.getType();
            mappingType = visitor.queryContext.getTypeHelper().convertPrimitive(mappingType);
            return mappingType.isAssignableTo(type);
        }
    }

    // Made static final for performance reasons.
    protected static final class NotExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link NotExpression} if it is the {@link Expression} being visited otherwise <code>null</code>.
         */
        protected NotExpression expression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
        }

        /**
         * Determines whether the {@link Expression} being visited is {@link NotExpression} or not.
         *
         * @return <code>true</code> if the {@link Expression} is {@link NotExpression};
         *  <code>null</code> otherwise
         */
        public boolean isNotExpression() {
            return expression != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NotExpression expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class OrderByClauseCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<AbstractOrderByClause> {

        protected OrderByClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(AbstractOrderByClause expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            OrderByItem item = (OrderByItem) collectionExpression.getChild(index);

            if (item.getOrdering() == Ordering.DEFAULT) {
                visitor.addIdentifier(ASC);
                visitor.addIdentifier(DESC);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(AbstractOrderByClause expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(AbstractOrderByClause expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            if ((index == 0) || hasComma) {
                visitor.addIdentificationVariables();
                visitor.addResultVariables();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(AbstractOrderByClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getOrderByItems());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(AbstractOrderByClause expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(AbstractOrderByClause expression) {
            return expression.hasSpaceAfterIdentifier();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(AbstractOrderByClause expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(AbstractOrderByClause expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(AbstractOrderByClause expression, int index) {
            return visitor.getQueryBNF(OrderByItemBNF.ID);
        }
    }

    // Made static for performance reasons.
    protected static class OrderByClauseStatementHelper
            extends AbstractVisitorHelper implements StatementHelper<SelectStatement> {

        protected OrderByClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addCompositeIdentifier(ORDER_BY, -1);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(SelectStatement expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(SelectStatement expression) {
            return expression.getOrderByClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<SelectStatement> getNextHelper() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(SelectStatement expression) {
            return expression.hasOrderByClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(SelectStatement expression) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(SelectStatement expression) {
            return visitor.isComplete(expression.getOrderByClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return false;
        }
    }

    // Made static final for performance reasons.
    protected static final class PropertyMappingFilter implements Filter<IMapping> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(IMapping value) {
            return !value.isTransient() &&
                   !value.isCollection();
        }
    }

    // Made static final for performance reasons.
    protected static final class RangeVariableDeclarationVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link RangeVariableDeclaration} if it was visited otherwise <code>null</code>.
         */
        protected RangeVariableDeclaration expression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class ResultVariableVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link ResultVariable} if it was visited otherwise <code>null</code>.
         */
        protected ResultVariable expression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ResultVariable expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class SelectClauseCollectionHelper extends AbstractSelectClauseCollectionHelper<SelectClause> {

        protected SelectClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(SelectClause expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            super.addAtTheEndOfChild(expression, collectionExpression, index, hasComma, virtualSpace);

            Expression child = collectionExpression.getChild(index);

            // "SELECT e |" <- Valid to add AS
            // "SELECT AVG(e |" <- Invalid to add AS
            if (virtualSpace && visitor.isComplete(child)) {
                visitor.addIdentifier(AS);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(SelectClause expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            super.addTheBeginningOfChild(expression, collectionExpression, index, hasComma);

            if (index == 0) {
                visitor.addIdentifier(DISTINCT);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(SelectClause expression) {

            int length = 0;

            if (expression.hasDistinct()) {
                length = 8 /* DISTINCT */;

                if (expression.hasSpaceAfterDistinct()) {
                    length++;
                }
            }

            return length;
        }
    }

    // Made static final for performance reasons.
    protected static final class SelectClauseStatementHelper extends AbstractSelectClauseStatementHelper {

        protected SelectClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FromClauseStatementHelper getNextHelper() {
            return visitor.getFromClauseStatementHelper();
        }
    }

    // Made static for performance reasons.
    protected static class SimpleFromClauseStatementHelper extends AbstractFromClauseStatementHelper<SimpleSelectStatement> {

        protected SimpleFromClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SimpleWhereClauseSelectStatementHelper getNextHelper() {
            return visitor.getSimpleWhereClauseSelectStatementHelper();
        }
    }

    // Made static final for performance reasons.
    protected static final class SimpleGroupByClauseStatementHelper extends AbstractGroupByClauseStatementHelper<SimpleSelectStatement> {

        protected SimpleGroupByClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SimpleHavingClauseStatementHelper getNextHelper() {
            return visitor.getSimpleHavingClauseStatementHelper();
        }
    }

    // Made static final for performance reasons.
    protected static final class SimpleHavingClauseStatementHelper extends AbstractHavingClauseStatementHelper<SimpleSelectStatement> {

        protected SimpleHavingClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<SimpleSelectStatement> getNextHelper() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(SimpleSelectStatement expression) {
            return false;
        }
    }

    // Made static final for performance reasons.
    protected static final class SimpleSelectClauseCollectionHelper extends AbstractSelectClauseCollectionHelper<SimpleSelectClause> {

        protected SimpleSelectClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

    }

    // Made static final for performance reasons.
    protected static final class SimpleSelectClauseStatementHelper extends AbstractSelectClauseStatementHelper {

        protected SimpleSelectClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<SimpleSelectStatement> getNextHelper() {
            return visitor.getSimpleFromClauseStatementHelper();
        }
    }

    // Made static final for performance reasons.
    protected static final class SimpleWhereClauseSelectStatementHelper extends AbstractWhereClauseSelectStatementHelper<SimpleSelectStatement> {

        protected SimpleWhereClauseSelectStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<SimpleSelectStatement> getNextHelper() {
            return visitor.getSimpleGroupByClauseStatementHelper();
        }
    }

    // Made static for performance reasons.
    /**
     * This helper helps to add JPQL identifiers for the clauses that make up a query statement and
     * also chains the clauses within the query.
     */
    protected static interface StatementHelper<T extends Expression> {

        /**
         * Adds the JPQL identifier of the clause being scanned by this helper.
         * <p>
         * Note: The identifier should not be added directly to the list, it needs to be filtered out
         * based on the location of the cursor, it can be within a word.
         */
        void addClauseProposals();

        /**
         * Adds the JPQL identifier of the internal clause being scanned by this helper. For instance,
         * the <code><b>FROM</b></code> clause could add its own sub-clauses.
         * <p>
         * Note: The identifier should not be added directly to the list, it needs to be filtered out
         * based on the location of the cursor, it can be within a word.
         *
         * @param expression The {@link AbstractSelectStatement} being visited
         */
        void addInternalClauseProposals(T expression);

        /**
         * Returns the clause being scanned by this helper. It is safe to type cast the clause because
         * {@link #hasClause(Expression)} is called before this one.
         *
         * @param expression The {@link AbstractSelectStatement} being visited
         * @return The clause being scanned
         */
        Expression getClause(T expression);

        /**
         * Returns the {@link StatementHelper} that will scan the following clause, which is
         * based on the grammar and not on the actual existence of the clause in the parsed tree.
         *
         * @return The {@link StatementHelper} for the next clause
         */
        StatementHelper<? extends T> getNextHelper();

        /**
         * Determines whether the clause exists in the parsed tree.
         *
         * @param expression The {@link AbstractSelectStatement} being visited
         * @return <code>true</code> if the clause has been parsed; <code>false</code> otherwise
         */
        boolean hasClause(T expression);

        /**
         * Determines whether there is a space (owned by the <b>SELECT</b> statement) after the clause
         * being scanned by this helper.
         *
         * @param expression The {@link AbstractSelectStatement} being visited
         * @return <code>true</code> if a space follows the clause; <code>false</code> otherwise
         */
        boolean hasSpaceAfterClause(T expression);

        /**
         * Determines whether the clause being scanned is complete or not.
         *
         * @param expression The {@link AbstractSelectStatement} being visited
         * @return <code>true</code> if the clause is complete; <code>false</code> otherwise
         */
        boolean isClauseComplete(T expression);

        /**
         * Determines whether the clause is required in order to make the JPQL query grammatically valid.
         *
         * @return <code>true</code> if the clause has to be defined; <code>false</code> if the clause
         * is optional
         */
        boolean isRequired();
    }

    // Made static final for performance reasons.
    protected static final class SubqueryAppendableExpressionVisitor extends AbstractAppendableExpressionVisitor {

        /**
         * For a subquery <code><b>SELECT</b></code> clause identifier to be appendable, it has to be
         * encapsulated by a {@link SubExpression}.
         */
        protected boolean subExpression;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {
            this.appendable = subExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullExpression expression) {
            this.appendable = subExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {
            subExpression = true;
            expression.getExpression().accept(this);
            subExpression = false;
        }
    }

    // Made static final for performance reasons.
    /**
     * This visitor determines if an {@link Expression} is in a subquery.
     */
    protected static final class SubqueryVisitor extends AbstractTraverseParentVisitor {

        /**
         * The subquery {@link Expression} if it's the first clause visitor. Otherwise it will be
         * <code>null</code> if the {@link Expression} is in the top-level query.
         */
        protected SimpleSelectStatement expression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            expression = null;
        }

        /**
         * Determines whether the visited {@link Expression} is in a subquery or in the top-level query.
         *
         * @return <code>true</code> if the owning query is a subquery; <code>false</code> if it's the
         * top-level query
         */
        public boolean isInSubquery() {
            return expression != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class TripleEncapsulatedCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<AbstractTripleEncapsulatedExpression> {

        protected TripleEncapsulatedCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(AbstractTripleEncapsulatedExpression expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            // The only thing that is appendable is an arithmetic operator
            // Example: "SELECT e.name|"
            // Example: "SELECT e|"
            if (queryBNF(expression, index).handleAggregate()) {

                Expression child = collectionExpression.getChild(index);

                if ((index == 0) && !virtualSpace) {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }
                }
                else {

                    if (visitor.areArithmeticSymbolsAppendable(child)) {
                        visitor.addArithmeticIdentifiers();
                    }

                    if (visitor.areComparisonSymbolsAppendable(child)) {
                        visitor.addComparisonIdentifiers(child);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(AbstractTripleEncapsulatedExpression expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
            visitor.addFunctionIdentifiers(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(AbstractTripleEncapsulatedExpression expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            visitor.addIdentificationVariables();
            visitor.addFunctionIdentifiers(queryBNF(expression, index));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(AbstractTripleEncapsulatedExpression expression) {
            return expression.buildCollectionExpression();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(AbstractTripleEncapsulatedExpression expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(AbstractTripleEncapsulatedExpression expression) {
            return expression.hasSpaceAfterIdentifier() ||
                   expression.hasLeftParenthesis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(AbstractTripleEncapsulatedExpression expression) {
            // Both LOCATE and SUBSTRING can allow up to 3 encapsulated expressions
            return 3;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(AbstractTripleEncapsulatedExpression expression) {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(AbstractTripleEncapsulatedExpression expression, int index) {
            return visitor.getQueryBNF(expression.getParameterQueryBNFId(index));
        }
    }

    // Made static final for performance reasons.
    protected static final class UpdateClauseStatementHelper
            extends AbstractVisitorHelper implements StatementHelper<UpdateStatement> {

        protected UpdateClauseStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(UPDATE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(UpdateStatement expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(UpdateStatement expression) {
            return expression.getUpdateClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<UpdateStatement> getNextHelper() {
            return visitor.getWhereClauseUpdateStatementHelper();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(UpdateStatement expression) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(UpdateStatement expression) {
            return expression.hasSpaceAfterUpdateClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(UpdateStatement expression) {
            return visitor.isComplete(expression.getUpdateClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return true;
        }
    }

    // Made static final for performance reasons.
    protected static final class UpdateItemCollectionHelper
            extends AbstractVisitorHelper implements CollectionExpressionHelper<UpdateClause> {

        protected UpdateItemCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAtTheEndOfChild(UpdateClause expression,
                                       CollectionExpression collectionExpression,
                                       int index,
                                       boolean hasComma,
                                       boolean virtualSpace) {

            visitor.addAggregateIdentifiers(NewValueBNF.ID);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addIdentifier(UpdateClause expression, String identifier) {
            visitor.proposals.addIdentifier(identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addTheBeginningOfChild(UpdateClause expression,
                                           CollectionExpression collectionExpression,
                                           int index,
                                           boolean hasComma) {

            visitor.addIdentificationVariables();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(UpdateClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getUpdateItems());
            if (collectionExpression == null) {
                collectionExpression = expression.buildCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canContinue(UpdateClause expression,
                                   CollectionExpression collectionExpression,
                                   int index) {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(UpdateClause expression) {
            return expression.hasSpaceAfterUpdate();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int maxCollectionSize(UpdateClause expression) {
            return Integer.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int preExpressionLength(UpdateClause expression) {
            // There is a SPACE_LENGTH less, it's added automatically
            return UPDATE.length() +
                   SPACE_LENGTH    +
                   expression.getRangeVariableDeclaration().getLength() +
                   SPACE_LENGTH    +
                   SET.length();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JPQLQueryBNF queryBNF(UpdateClause expression, int index) {
            return visitor.getQueryBNF(NewValueBNF.ID);
        }
    }

    // Made static final for performance reasons.
    /**
     * This visitor is meant to adjust the corrections stack when traversing an {@link Expression} in
     * order to increase the list of valid proposals.
     * <p>
     * For instance, if the query is "<code>SELECT e FROM Employee e WHERE IN</code>" and the cursor
     * is at the end of the query, then <code>IN</code> would be parsed with {@link InExpression}.
     * However, due to how {@link AbstractContentAssistVisitor} works, the identifier <code>INDEX</code>
     * is not added as a valid proposal. This visitor adds that functionality.
     */
    protected static final class VisitParentVisitor extends AnonymousExpressionVisitor {

        /**
         * Enclosing visitor instance.
         */
        protected final AbstractContentAssistVisitor visitor;

        protected VisitParentVisitor(AbstractContentAssistVisitor visitor) {
            this.visitor = visitor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            expression.getParent().accept(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(InExpression expression) {

            int position = visitor.queryPosition.getPosition(expression) - visitor.corrections.peek();
            int length = 0;

            if (expression.hasExpression()) {
                length += expression.getExpression().getLength() + SPACE_LENGTH;
            }

            // Within "IN"
            if (visitor.isPositionWithin(position, length, expression.getIdentifier())) {

                boolean hasOnlyIdentifier = !expression.hasExpression() &&
                                            !expression.hasInItems();

                if (hasOnlyIdentifier) {
                    visitor.corrections.add(visitor.queryPosition.getPosition(expression));
                }

                super.visit(expression);

                if (hasOnlyIdentifier) {
                    visitor.corrections.pop();
                }
            }
            else {
                super.visit(expression);
            }
        }
    }

    // Made static final for performance reasons.
    /**
     * This helpers handles adding proposals for {@link WhenClause}.
     */
    protected static final class WhenClauseConditionalClauseCollectionHelper extends AbstractConditionalClauseCollectionHelper<WhenClause> {

        protected WhenClauseConditionalClauseCollectionHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CollectionExpression buildCollectionExpression(WhenClause expression) {
            CollectionExpression collectionExpression = visitor.getCollectionExpression(expression.getWhenExpression());
            if (collectionExpression == null) {
                collectionExpression = expression.buildWhenCollectionExpression();
            }
            return collectionExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasDelimiterAfterIdentifier(WhenClause expression) {
            return expression.hasSpaceAfterWhen();
        }
    }

    // Made static final for performance reasons.
    protected static final class WhereClauseDeleteStatementHelper
            extends AbstractVisitorHelper implements StatementHelper<DeleteStatement> {

        protected WhereClauseDeleteStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(WHERE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(DeleteStatement expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(DeleteStatement expression) {
            return expression.getWhereClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<? extends DeleteStatement> getNextHelper() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(DeleteStatement expression) {
            return expression.hasWhereClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(DeleteStatement expression) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(DeleteStatement expression) {
            return visitor.isComplete(expression.getWhereClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return false;
        }
    }

    // Made static final for performance reasons.
    protected static final class WhereClauseSelectStatementHelper extends AbstractWhereClauseSelectStatementHelper<SelectStatement> {

        protected WhereClauseSelectStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<SelectStatement> getNextHelper() {
            return visitor.getGroupByClauseStatementHelper();
        }
    }

    // Made static final for performance reasons.
    protected static final class WhereClauseUpdateStatementHelper
            extends AbstractVisitorHelper implements StatementHelper<UpdateStatement> {

        protected WhereClauseUpdateStatementHelper(AbstractContentAssistVisitor visitor) {
            super(visitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addClauseProposals() {
            visitor.addIdentifier(WHERE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInternalClauseProposals(UpdateStatement expression) {
            // Does not have internal clauses
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression getClause(UpdateStatement expression) {
            return expression.getWhereClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StatementHelper<? extends UpdateStatement> getNextHelper() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasClause(UpdateStatement expression) {
            return expression.hasWhereClause();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasSpaceAfterClause(UpdateStatement expression) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClauseComplete(UpdateStatement expression) {
            return visitor.isComplete(expression.getWhereClause());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRequired() {
            return false;
        }
    }

    // Made static final for performance reasons.
    protected static final class WithinInvalidExpressionVisitor extends AbstractTraverseParentVisitor {

        /**
         * Determines whether the visited {@link Expression} is an descendant of either a bad or
         * invalid expression.
         */
        protected boolean withinInvalidExpression;

        /**
         * Disposes of the internal data.
         */
        public void dispose() {
            withinInvalidExpression = false;
        }

        /**
         * Determines whether the visited {@link Expression} is part of an invalid fragment
         *
         * @return <code>true</code> if the visited {@link Expression} is within an invalid fragment;
         * <code>false</code> if it is not
         */
        public boolean isWithinInvalidExpression() {
            return withinInvalidExpression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BadExpression expression) {
            withinInvalidExpression = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UnknownExpression expression) {
            withinInvalidExpression = true;
        }
    }
}
