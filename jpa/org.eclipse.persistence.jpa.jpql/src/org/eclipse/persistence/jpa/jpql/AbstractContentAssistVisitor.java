/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.eclipse.persistence.jpa.jpql.AbstractValidator.JPQLQueryBNFValidator;
import org.eclipse.persistence.jpa.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseChildrenVisitor;
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
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticTermBNF;
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
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionFactory;
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
import org.eclipse.persistence.jpa.jpql.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
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
import org.eclipse.persistence.jpa.jpql.parser.InternalFromClauseBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalJoinBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalWhenClauseBNF;
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
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.QueryPosition;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
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
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.eclipse.persistence.jpa.jpql.util.filter.AndFilter;
import org.eclipse.persistence.jpa.jpql.util.filter.Filter;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The abstract definition that provides support for finding the possible proposals within a JPQL
 * query at a certain position.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractContentAssistVisitor extends AnonymousExpressionVisitor {

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link CollectionExpression}.
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 * The context used to query information about the JPQL query.
	 */
	protected final JPQLQueryContext context;

	/**
	 * This is used to change the position of the cursor in order to add possible proposals
	 */
	protected Stack<Integer> corrections;

	/**
	 * The cached helpers that are used by this visitor to add valid content assist proposals.
	 */
	protected Map<Class<?>, Object> helpers;

	/**
	 * Used to prevent and infinite recursion when one of the visit method is virtually asking a
	 * child expression to be visited.
	 */
	protected Stack<Expression> lockedExpressions;

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link NullExpression}.
	 */
	private NullExpressionVisitor nullExpressionVisitor;

	/**
	 * Used to determine if the cursor is an expression contained in a collection, if not, then this
	 * value is set to -1.
	 */
	protected Stack<Integer> positionInCollections;

	/**
	 * The set of possible proposals gathered based on the position in the query.
	 */
	protected DefaultContentAssistProposals proposals;

	/**
	 * Contains the position of the cursor within the parsed {@link Expression}.
	 */
	protected QueryPosition queryPosition;

	/**
	 * A virtual space is used to move the position by an amount of space in order to find some
	 * proposals within an expression. This is usually used when the trailing whitespace is not owned
	 * by the child expression but by one of its parents.
	 */
	protected Stack<Integer> virtualSpaces;

	/**
	 * The current word, which was retrieved from the JPQL based on the position of the cursor.
	 * The word is what is on the left side of the cursor.
	 */
	protected String word;

	/**
	 * This is used to retrieve words from the actual JPQL query.
	 */
	protected WordParser wordParser;

	/**
	 * A constant for the length of a whitespace, which is 1.
	 */
	protected static final int SPACE_LENGTH = 1;

	/**
	 * Creates a new <code>AbstractContentAssistVisitor</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception NullPointerException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	protected AbstractContentAssistVisitor(JPQLQueryContext context) {
		super();
		Assert.isNotNull(context, "The JPQLQueryContext cannot be null");
		this.context = context;
		initialize();
	}

	/**
	 * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#AGGREGATE}
	 * and the beginning starts with the current word.
	 *
	 * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
	 */
	protected void addAggregate(String identifier) {
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
	protected void addAllAggregates(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.getIdentifiers()) {
			addAggregate(identifier);
		}
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#AGGREGATE} and the beginning starts with the
	 * current word.
	 *
	 * @param queryBNFId The unique of the {@link JPQLQueryBNF} for which the registered JPQL
	 * identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllAggregates(String queryBNFId) {
		addAllAggregates(getQueryBNF(queryBNFId));
	}

	protected void addAllArithmeticIdentifiers() {
		addAllExpressionFactoryIdentifiers(ArithmeticExpressionFactory.ID);
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#CLAUSE} and the beginning starts with the
	 * current word.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllClauses(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.getIdentifiers()) {
			addClause(identifier);
		}
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#CLAUSE} and the beginning starts with the
	 * current word.
	 *
	 * @param queryBNF The unique identifier of the {@link JPQLQueryBNF} for which the registered
	 * JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllClauses(String queryBNF) {
		addAllClauses(getQueryBNF(queryBNF));
	}

	protected void addAllComparisonIdentifiers() {
		addAllExpressionFactoryIdentifiers(ComparisonExpressionFactory.ID);
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#COMPOUND_FUNCTION} and the beginning starts
	 * with the current word.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllCompounds(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.getIdentifiers()) {
			addCompound(identifier);
		}
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#COMPOUND_FUNCTION} and the beginning starts
	 * with the current word.
	 *
	 * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
	 * JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllCompounds(String queryBNFId) {
		addAllCompounds(getQueryBNF(queryBNFId));
	}

	protected void addAllExpressionFactoryIdentifiers(ExpressionFactory expressionFactory) {
		for (String identifier : expressionFactory.identifiers()) {
			proposals.addIdentifier(identifier);
		}
	}

	protected void addAllExpressionFactoryIdentifiers(String expressionFactoryId) {
		addAllExpressionFactoryIdentifiers(
			getExpressionRegistry().getExpressionFactory(expressionFactoryId)
		);
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#FUNCTION} and the beginning starts with the
	 * current word.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllFunctions(JPQLQueryBNF queryBNF) {
		addAllFunctions(queryBNF, queryPosition.getPosition());
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#FUNCTION} and the beginning starts with the
	 * current word.
	 *
	 * @param position The position of the cursor to use for determining if the given JPQL identifier
	 * is a valid proposal
	 * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllFunctions(JPQLQueryBNF queryBNF, int position) {
		for (String identifier : queryBNF.getIdentifiers()) {
			addFunction(identifier, position);
		}
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#FUNCTION} and the beginning starts with the
	 * current word.
	 *
	 * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
	 * JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllFunctions(String queryBNFId) {
		addAllFunctions(getQueryBNF(queryBNFId), queryPosition.getPosition());
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if their role is {@link IdentifierRole#FUNCTION} and the beginning starts with the
	 * current word.
	 *
	 * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
	 * JPQL identifiers will be
	 * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllFunctions(String queryBNFId, int position) {
		addAllFunctions(getQueryBNF(queryBNFId), position);
	}

	/**
	 * Adds all the identification variables defined in the current query's <b>FROM</b> clause.
	 */
	protected void addAllIdentificationVariables() {
		addIdentificationVariables(IdentificationVariableType.ALL, null);
	}

	/**
	 * Adds the JPQL identifiers that are registered with the given {@link JPQLQueryBNF} as valid
	 * proposals if the beginning starts with the current word.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} for which the registered JPQL identifiers will be
	 * added as proposals if they pass the checks
	 */
	protected void addAllIdentifiers(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.getIdentifiers()) {
			addIdentifier(identifier);
		}
	}

	/**
	 * Adds the unique identifier of the JPQL identifiers that are registered with the given {@link
	 * JPQLQueryBNF} as valid proposals if the beginning starts with the current word.
	 *
	 * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} for which the registered
	 * JPQL identifiers will be added as proposals if they pass the checks
	 */
	protected void addAllIdentifiers(String queryBNFId) {
		addAllIdentifiers(getQueryBNF(queryBNFId));
	}

	protected void addAllLogicIdentifiers() {
		addIdentifier(AND);
		addIdentifier(OR);
	}

	/**
	 * Adds the result variables defined in the <code>SELECT</code> clause as valid proposals.
	 */
	protected void addAllResultVariables() {
		addIdentificationVariables(IdentificationVariableType.RESULT_VARIABLE, null);
	}

	/**
	 * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#CLAUSE}
	 * and the beginning starts with the current word.
	 *
	 * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
	 */
	protected void addClause(String identifier) {
		if (isClause(identifier)) {
			addIdentifier(identifier);
		}
	}

	/**
	 * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#COMPOUND_FUNCTION}
	 * and the beginning starts with the current word.
	 *
	 * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
	 */
	protected void addCompound(String identifier) {
		if (isCompoundFunction(identifier)) {
			addIdentifier(identifier, queryPosition.getPosition());
		}
	}

	/**
	 * Adds the abstract schema types as possible content assist proposals but will be filtered using
	 * the current word.
	 */
	protected void addEntities() {
		for (IEntity entity : entities()) {
			if (isValidProposal(entity.getName(), word)) {
				proposals.addAbstractSchemaType(entity);
			}
		}
	}

	/**
	 * Adds the abstract schema types as possible content assist proposals but will be filtered using
	 * the current word and the entity's type will have to match the one from the given {@link IType}.
	 *
	 * @param type The {@link IType} used to filter the abstract schema types
	 */
	protected void addEntities(IType type) {

		for (IEntity entity : entities()) {

			if (isValidProposal(entity.getName(), word) &&
			    type.isAssignableTo(entity.getType())) {

				proposals.addAbstractSchemaType(entity);
			}
		}
	}

	/**
	 * Adds the given JPQL identifier as a valid proposal if its role is {@link IdentifierRole#FUNCTION}
	 * and the beginning starts with the current word.
	 *
	 * @param position The position of the cursor to use for determining if the given JPQL identifier
	 * is a valid proposal
	 * @param identifier The JPQL identifier to add as a valid proposal if it passes the checks
	 */
	protected void addFunction(String identifier, int position) {
		if (isFunction(identifier)) {
			addIdentifier(identifier, position);
		}
	}

	/**
	 * Adds the given identification variable as a valid proposal.
	 *
	 * @param identificationVariable The identification variable to add as a proposal if it passes
	 * the checks
	 */
	protected void addIdentificationVariable(String identificationVariable) {

		if (ExpressionTools.stringIsNotEmpty(identificationVariable) &&
		    isValidProposal(identificationVariable, word)) {

			proposals.addIdentificationVariable(identificationVariable);
		}
	}

	/**
	 * Adds the possible identifier variables as valid proposals but filter them based on the given
	 * type.
	 * <p>
	 * For instance, if the type is {@link IdentificationVariableType#LEFT}, then any identification
	 * variables that have been defined before the given {@link Expression} are valid proposals, but
	 * those defined after are not valid proposals.
	 *
	 * @param type Which type of identification variables to add as valid proposals
	 * @param expression The {@link Expression} where the content assist was invoked, which helps to
	 * determine how to stop adding identification variable
	 */
	protected void addIdentificationVariables(IdentificationVariableType type, Expression expression) {

		// Result variable
		if (type == IdentificationVariableType.RESULT_VARIABLE) {
			for (String resultVariable : context.getResultVariables()) {
				addIdentificationVariable(resultVariable);
			}
		}
		else if (type != IdentificationVariableType.NONE) {
			boolean stop = false;

			for (Declaration declaration : context.getDeclarations()) {

				if (stop) {
					break;
				}

				switch (type) {

					// Add the entire list of identification variables (range and collection)
					case ALL: {

						if (declaration.rangeDeclaration) {
							addRangeIdentificationVariable(declaration.getVariableName());
						}
						else {
							addIdentificationVariable(declaration.getVariableName());
						}

						for (String joinIdentificationVariable : declaration.getJoinIdentificationVariables()) {
							addIdentificationVariable(joinIdentificationVariable);
						}

						break;
					}

					// Add only the collection identification variables
					case COLLECTION: {

						if (!declaration.rangeDeclaration) {
							addIdentificationVariable(declaration.getVariableName());

							for (String joinIdentificationVariable : declaration.getJoinIdentificationVariables()) {
								addIdentificationVariable(joinIdentificationVariable);
							}
						}

						break;
					}

					// Add the entire list of identification variables (range and collection) that are
					// defined to the left of the expression
					case LEFT: {

						boolean shouldStop = declaration.declarationExpression.isAncestor(expression);

						if (shouldStop && !declaration.getJoins().contains(expression)) {
							stop = true;
							break;
						}

						if (declaration.rangeDeclaration) {
							addRangeIdentificationVariable(declaration.getVariableName());
						}
						else if (!shouldStop) {
							addIdentificationVariable(declaration.getVariableName());
						}

						for (Map.Entry<Join, String> join : declaration.getJoinEntries()) {
							if (join.getKey().isAncestor(expression)) {
								stop = true;
								break;
							}
							addIdentificationVariable(join.getValue());
						}

						break;
					}

					// Add only the collection identification variables that are
					// defined to the left of the expression
					case LEFT_COLLECTION: {

						boolean shouldStop = declaration.declarationExpression.isAncestor(expression);

						if (shouldStop && (declaration.getJoins().contains(expression))) {
							stop = true;
							break;
						}

						if (!shouldStop && !declaration.rangeDeclaration) {
							addIdentificationVariable(declaration.getVariableName());
						}
						else {
							for (Map.Entry<Join, String> join : declaration.getJoinEntries()) {
								if (join.getKey().isAncestor(expression)) {
									stop = true;
									break;
								}
								addIdentificationVariable(join.getValue());
							}
						}

						break;
					}
				}
			}
		}
	}

	/**
	 * Adds the given identifier as a proposal if it passes the checks.
	 *
	 * @param identifier The JPQL identifier to add as a proposal
	 */
	protected void addIdentifier(String identifier) {
		addIdentifier(identifier, word);
	}

	/**
	 * Adds the given identifier as a proposal. If the JPQL identifier has more than one word, what
	 * precedes the given position will be checked in order to filter out some identifiers. For
	 * example: "<b>... WHERE IS NOT |</b>" has <code>IS NOT</code> already defined, that means
	 * <b>IS NOT EMPTY</b>, <b>IS NOT NULL</b> will be filtered out. The only valid proposals in this
	 * case is <b>EMPTY</b> and <b>NULL</b>.
	 *
	 * @param identifier The JPQL identifier to add as a proposal
	 * @param position The position of the cursor to use for determining if the given JPQL identifier
	 * is a valid proposal
	 */
	protected void addIdentifier(String identifier, int position) {

		position -= word.length();
		boolean found = addIdentifier(identifier, position, "IS NOT ", 2);

		if (!found) {
			found = addIdentifier(identifier, position, "NOT ", 3);

			if (!found) {
				addIdentifier(identifier);
			}
		}
	}

	/**
	 * Adds the given identifier as a proposal. If the JPQL identifier has more than one word, what
	 * precedes the given position will be checked in order to filter out some identifiers. For
	 * example: "<b>... WHERE IS NOT |</b>" has <code>IS NOT</code> already defined, that means
	 * <b>IS NOT EMPTY</b>, <b>IS NOT NULL</b> will be filtered out. The only valid proposals in this
	 * case is <b>EMPTY</b> and <b>NULL</b>.
	 *
	 * @param identifier The JPQL identifier to add as a proposal
	 * @param position The position of the cursor to use for determining if the given JPQL identifier
	 * is a valid proposal
	 * @param partialEnding The possible characters that could be identifiers and might be part of
	 * a JPQL identifier with more than one word
	 * @param endIndex The position used to stop matching the partial ending with what is found
	 * before the position of the cursor
	 * @return <code>true</code> if the given JPQL identifier was added as a proposal; <code>false</code>
	 * otherwise
	 */
	protected boolean addIdentifier(String identifier,
	                                int position,
	                                String partialEnding,
	                                int endIndex) {

		for (int index = partialEnding.length(); index > endIndex; index--) {
			String partial = partialEnding.substring(0, index);

			if (wordParser.endsWith(position, partial)) {
				addIdentifier(identifier, partial + word);
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds the given proposal as a proposal if it passes the checks.
	 *
	 * @param identifier The JPQL identifier to add as a proposal
	 * @param word The string used to determine if the identifier starts with it
	 */
	protected void addIdentifier(String identifier, String word) {

		if (isValidProposal(identifier, word) &&
		    isValidVersion(identifier)) {

			proposals.addIdentifier(identifier);
		}
	}

	/**
	 * Adds the join specification identifiers as proposals without validation.
	 */
	protected void addJoinIdentifiers() {
		proposals.addIdentifier(INNER_JOIN);
		proposals.addIdentifier(INNER_JOIN_FETCH);
		proposals.addIdentifier(JOIN);
		proposals.addIdentifier(JOIN_FETCH);
		proposals.addIdentifier(LEFT_JOIN);
		proposals.addIdentifier(LEFT_JOIN_FETCH);
		proposals.addIdentifier(LEFT_OUTER_JOIN);
		proposals.addIdentifier(LEFT_OUTER_JOIN_FETCH);
	}

	/**
	 * Adds the identification variables defined in the current query's <b>FROM</b> clause that are
	 * declared before the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} used to determine at which declaration to stop
	 */
	protected void addLeftIdentificationVariables(Expression expression) {
		addIdentificationVariables(IdentificationVariableType.LEFT, expression);
	}

	/**
	 * Adds the given identification variable as a proposal if it passes the checks. If an entity is
	 * found, then it will be registered.
	 */
	protected void addRangeIdentificationVariable(String identificationVariable) {

		if (ExpressionTools.stringIsNotEmpty(identificationVariable) &&
		    isValidProposal(identificationVariable, word)) {

			Resolver resolver = context.getResolver(identificationVariable);
			IEntity entity = getEntity(resolver.getType());

			if (entity != null) {
				proposals.addRangeIdentificationVariable(identificationVariable, entity);
			}
			else {
				proposals.addIdentificationVariable(identificationVariable);
			}
		}
	}

	/**
	 * Adds the proposals that are valid for a scalar expression.
	 */
	protected void addScalarExpressionProposals() {
		addAllIdentificationVariables();
		addEntities();
		addAllFunctions(ScalarExpressionBNF.ID);
	}

	protected void addSelectExpressionProposals(AbstractSelectClause expression, int length) {

		int position = getPosition(expression) - corrections.peek();

		// Now check for inside the select expression
		CollectionExpression collectionExpression = getCollectionExpression(expression.getSelectExpression());

		if (collectionExpression != null) {

			for (int index = 0, count = collectionExpression.childrenSize(); index < count; index++) {
				Expression child = collectionExpression.getChild(index);

				// At the beginning of the child expression
				if (position == length) {
					addAllIdentificationVariables();
					addAllFunctions(expression.selectItemBNF());
					break;
				}
				else {
					boolean withinChild = addSelectExpressionProposals(
						child,
						expression.selectItemBNF(),
						length,
						index,
						index + 1 == count
					);

					if (withinChild) {
						break;
					}
				}

				length += length(child);

				if (collectionExpression.hasComma(index)) {
					length++;
				}
				// Cannot do anything more since a comma is missing,
				// which means the query is not valid
				else {
					break;
				}

				// After ',', the proposals can be added
				if (position == length) {
					addAllIdentificationVariables();
					addAllFunctions(expression.selectItemBNF());
					break;
				}

				if (collectionExpression.hasSpace(index)) {
					length++;
				}
			}
		}
		else {
			addSelectExpressionProposals(
				expression.getSelectExpression(),
				expression.selectItemBNF(),
				length,
				0,
				true
			);
		}
	}

	protected boolean addSelectExpressionProposals(Expression expression,
	                                               String queryBNFId,
	                                               int length,
	                                               int index,
	                                               boolean last) {

		int position = getPosition(expression) - corrections.peek();

		// At the beginning of the child expression
		if (position > 0) {

			if (position == 0) {
				if (index == 0) {
					addIdentifier(DISTINCT);
				}
				addAllIdentificationVariables();
				addAllFunctions(queryBNFId);
			}
			else {
				int childLength = length(expression);

				// At the end of the child expression
				if ((position == length + childLength + virtualSpaces.peek() ||
				     position == childLength) &&
				    isSelectExpressionComplete(expression)) {

					// Proposals cannot be added if the expression is a result variable
					if (!isResultVariable(expression)) {

						// There is a "virtual" space after the expression, we can add "AS"
						// or the cursor is at the end of the child expression
						if ((virtualSpaces.peek() > 0) || (position == childLength)) {
							proposals.addIdentifier(AS);
						}

						addAllAggregates(queryBNFId);
					}

					return true;
				}
			}
		}

		return false;
	}

	protected boolean areArithmeticSymbolsAppendable(Expression expression) {
		return isValid(expression, ArithmeticTermBNF.ID);
	}

	protected boolean areComparisonSymbolsAppendable(Expression expression) {
		JPQLQueryBNF queryBNF = getQueryBNF(ComparisonExpressionBNF.ID);
		queryBNF.setCompound(false);
		try {
			return isValid(expression, queryBNF);
		}
		finally {
			queryBNF.setCompound(true);
		}
	}

	protected boolean areLogicSymbolsAppendable(Expression expression) {
		return isValid(expression, ConditionalExpressionBNF.ID);
	}

	protected AcceptableTypeVisitor buildAcceptableTypeVisitor() {
		return new AcceptableTypeVisitor();
	}

	protected AppendableExpressionVisitor buildAppendableExpressionVisitor() {
		return new AppendableExpressionVisitor();
	}

	/**
	 * Creates a visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return A new {@link CollectionExpressionVisitor}
	 */
	protected CollectionExpressionVisitor buildCollectionExpressionVisitor() {
		return new CollectionExpressionVisitor();
	}

	protected CollectionMappingFilter buildCollectionMappingFilter() {
		return new CollectionMappingFilter();
	}

	protected CompoundExpressionHelper buildCompoundExpressionHelper() {
		return new CompoundExpressionHelper();
	}

	protected ConditionalExpressionCompletenessVisitor buildConditionalExpressionCompletenessVisitor() {
		return new ConditionalExpressionCompletenessVisitor();
	}

	protected ConstrutorCollectionHelper buildConstrutorCollectionHelper() {
		return new ConstrutorCollectionHelper();
	}

	protected DefaultMappingCollector buildDefaultMappingCollector() {
		return new DefaultMappingCollector();
	}

	protected DeleteClauseHelper buildDeleteClauseHelper() {
		return new DeleteClauseHelper();
	}

	protected DoubleEncapsulatedCollectionHelper buildDoubleEncapsulatedCollectionHelper() {
		return new DoubleEncapsulatedCollectionHelper();
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

	protected FromClauseCollectionHelper buildFromClauseCollectionHelper() {
		return new FromClauseCollectionHelper();
	}

	protected FromClauseHelper buildFromClauseHelper() {
		return new FromClauseHelper();
	}

	protected FromClauseSelectStatementHelper buildFromClauseSelectStatementHelper() {
		return new FromClauseSelectStatementHelper();
	}

	protected GroupByClauseCollectionHelper buildGroupByClauseCollectionHelper() {
		return new GroupByClauseCollectionHelper();
	}

	protected GroupByClauseSelectStatementHelper buildGroupByClauseSelectStatementHelper() {
		return new GroupByClauseSelectStatementHelper();
	}

	protected HavingClauseHelper buildHavingClauseHelper() {
		return new HavingClauseHelper();
	}

	protected HavingClauseSelectStatementHelper buildHavingClauseSelectStatementHelper() {
		return new HavingClauseSelectStatementHelper();
	}

	protected IncompleteCollectionExpressionVisitor buildIncompleteCollectionExpressionVisitor() {
		return new IncompleteCollectionExpressionVisitor();
	}

	protected JoinCollectionHelper buildJoinCollectionHelper() {
		return new JoinCollectionHelper();
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

	/**
	 * Returns the {@link JPQLQueryBNFValidator} that can be used to validate an {@link Expression}
	 * by making sure its BNF is part of the given BNF.
	 *
	 * @param queryBNFId The unique identifier of the BNF used to determine the validity of an
	 * {@link Expression}
	 * @return A {@link JPQLQueryBNFValidator} that can determine if an {@link Expression} follows
	 * the given BNF
	 */
	protected JPQLQueryBNFValidator buildJPQLQueryBNFValidator(String queryBNFId) {
		return buildJPQLQueryBNFValidator(getQueryBNF(queryBNFId));
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
		return new AndFilter<IMapping>(new MappingTypeFilter(type), filter);
	}

	protected Filter<IMapping> buildMappingFilter(Expression expression) {
		MappingFilterBuilder visitor = getMappingFilterBuilder();
		try {
			expression.accept(visitor);
			return visitor.filter;
		}
		finally {
			visitor.filter = null;
		}
	}

	protected MappingFilterBuilder buildMappingFilterBuilder() {
		return new MappingFilterBuilder();
	}

	/**
	 * Creates a visitor that collects the {@link NullExpression} if it's been visited.
	 *
	 * @return A new {@link NullExpressionVisitor}
	 */
	protected NullExpressionVisitor buildNullExpressionVisitor() {
		return new NullExpressionVisitor();
	}

	protected OrderByClauseCollectionHelper buildOrderByClauseCollectionHelper() {
		return new OrderByClauseCollectionHelper();
	}

	protected OrderByClauseSelectStatementHelper buildOrderByClauseSelectStatementHelper() {
		return new OrderByClauseSelectStatementHelper();
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

		JPQLExpression jpqlExpression = getJPQLExpression();

		QueryPosition queryPosition = jpqlExpression.buildPosition(
			getQueryExpression(),
			position
		);

		prepare(queryPosition);
		jpqlExpression.accept(this);
		return proposals;
	}

	protected RangeVariableDeclarationVisitor buildRangeVariableDeclarationVisitor() {
		return new RangeVariableDeclarationVisitor();
	}

	protected ResultVariableVisitor buildResultVariableVisitor() {
		return new ResultVariableVisitor();
	}

	protected SelectClauseCompletenessVisitor buildSelectClauseCompleteness() {
		return new SelectClauseCompletenessVisitor();
	}

	protected SelectClauseSelectStatementHelper buildSelectClauseSelectStatementHelper() {
		return new SelectClauseSelectStatementHelper();
	}

	protected SimpleFromClauseSelectStatementHelper buildSimpleFromClauseSelectStatementHelper() {
		return new SimpleFromClauseSelectStatementHelper();
	}

	protected SimpleGroupByClauseSelectStatementHelper buildSimpleGroupByClauseSelectStatementHelper() {
		return new SimpleGroupByClauseSelectStatementHelper();
	}

	protected SimpleHavingClauseSelectStatementHelper buildSimpleHavingClauseSelectStatementHelper() {
		return new SimpleHavingClauseSelectStatementHelper();
	}

	protected SimpleSelectClauseSelectStatementHelper buildSimpleSelectClauseSelectStatementHelper() {
		return new SimpleSelectClauseSelectStatementHelper();
	}

	protected SimpleWhereClauseSelectStatementHelper buildSimpleWhereClauseSelectStatementHelper() {
		return new SimpleWhereClauseSelectStatementHelper();
	}

	protected SubqueryVisitor buildSubqueryVisitor() {
		return new SubqueryVisitor();
	}

	protected TrailingCompletenessVisitor buildTrailingCompleteness() {
		return new TrailingCompletenessVisitor();
	}

	protected TripleEncapsulatedCollectionHelper buildTripleEncapsulatedCollectionHelper() {
		return new TripleEncapsulatedCollectionHelper();
	}

	protected UpdateItemCollectionHelper buildUpdateItemCollectionHelper() {
		return new UpdateItemCollectionHelper();
	}

	protected VisitParentVisitor buildVisitParentVisitor() {
		return new VisitParentVisitor();
	}

	protected WhereClauseHelper buildWhereClauseHelper() {
		return new WhereClauseHelper();
	}

	protected WhereClauseSelectStatementHelper buildWhereClauseSelectStatementHelper() {
		return new WhereClauseSelectStatementHelper();
	}

	@SuppressWarnings("unchecked")
	protected SelectStatementHelper<AbstractSelectStatement, Expression> cast(
		SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> helper) {

		return (SelectStatementHelper<AbstractSelectStatement, Expression>) helper;
	}

	/**
	 * Disposes this visitor.
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
		positionInCollections.setSize(1);
	}

	/**
	 * Returns the collection of possible abstract schema types.
	 *
	 * @return The {@link IEntity entities} defined in the persistence context
	 */
	protected IterableIterator<IEntity> entities() {
		return getProvider().entities();
	}

	protected int findExpressionPosition(CollectionExpression expression) {

		Expression leafExpression = queryPosition.getExpression();

		if (leafExpression != expression) {
			for (int index = 0, count = expression.childrenSize(); index < count; index++) {
				Expression child = expression.getChild(index);

				if (child.isAncestor(leafExpression)) {
					return index;
				}
			}
		}

		int position = getPosition(expression);

		if (position > -1) {
			for (int index = 0, count = expression.childrenSize(); index < count; index++) {
				Expression child = expression.getChild(index);
				String text = child.toActualText();

				if (position <= text.length()) {
					return index;
				}

				position -= text.length();

				if (expression.hasComma(index)) {
					position--;
				}

				if (expression.hasSpace(index)) {
					position--;
				}
			}
		}

		if (position == 0 && (expression.endsWithComma() || expression.endsWithSpace())) {
			return expression.childrenSize();
		}

		return -1;
	}

	protected RangeVariableDeclaration findRangeVariableDeclaration(UpdateClause expression) {
		RangeVariableDeclarationVisitor visitor = getRangeVariableDeclarationVisitor();
		try {
			expression.getRangeVariableDeclaration().accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
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
			visitor.type = null;
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
			visitor.expression = null;
		}
	}

	/**
	 * Returns the visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return The {@link CollectionExpressionVisitor}
	 * @see #buildCollectionExpressionVisitor()
	 */
	protected CollectionExpressionVisitor getCollectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = buildCollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	protected CompletenessVisitor getCompletenessVisitor() {
		TrailingCompletenessVisitor helper = (TrailingCompletenessVisitor) helpers.get(TrailingCompletenessVisitor.class);
		if (helper == null) {
			helper = buildTrailingCompleteness();
			helpers.put(TrailingCompletenessVisitor.class, helper);
		}
		return helper;
	}

	protected CompoundExpressionHelper getCompoundExpressionHelper() {
		CompoundExpressionHelper helper = getHelper(CompoundExpressionHelper.class);
		if (helper == null) {
			helper = buildCompoundExpressionHelper();
			registerHelper(CompoundExpressionHelper.class, helper);
		}
		return helper;
	}

	protected CompletenessVisitor getConditionalExpressionCompletenessVisitor() {
		ConditionalExpressionCompletenessVisitor helper = getHelper(ConditionalExpressionCompletenessVisitor.class);
		if (helper == null) {
			helper = buildConditionalExpressionCompletenessVisitor();
			registerHelper(ConditionalExpressionCompletenessVisitor.class, helper);
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

	/**
	 * Returns
	 *
	 * @return
	 */
	protected final Stack<Integer> getCorrections() {
		return corrections;
	}

	protected MappingCollector getDefaultMappingCollector() {
		DefaultMappingCollector helper = getHelper(DefaultMappingCollector.class);
		if (helper == null) {
			helper = buildDefaultMappingCollector();
			registerHelper(DefaultMappingCollector.class, helper);
		}
		return helper;
	}

	protected ClauseHelper<DeleteClause> getDeleteClauseHelper() {
		DeleteClauseHelper helper = getHelper(DeleteClauseHelper.class);
		if (helper == null) {
			helper = buildDeleteClauseHelper();
			registerHelper(DeleteClauseHelper.class, helper);
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

	/**
	 * Retrieves the {@link IEmbeddable} for the given {@link IType} if one exists.
	 *
	 * @param type The {@link IType} that is mapped as an embeddable
	 * @return The given {@link IType} mapped as an embeddable; <code>null</code> if none exists or
	 * if it's mapped as a different managed type
	 */
	protected IEmbeddable getEmbeddable(IType type) {
		return getProvider().getEmbeddable(type);
	}

	/**
	 * Retrieves the {@link IEntity} for the given {@link IType} if one exists.
	 *
	 * @param type The {@link IType} that is mapped as an entity
	 * @return The given {@link IType} mapped as an entity; <code>null</code> if none exists or if
	 * it's mapped as a different managed type
	 */
	protected IEntity getEntity(IType type) {
		return getProvider().getEntity(type);
	}

	/**
	 * Retrieves the entity with the given abstract schema name, which can also be the entity class
	 * name.
	 *
	 * @param entityName The abstract schema name, which can be different from the entity class name
	 * but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if none could be found
	 */
	protected IEntity getEntityNamed(String entityName) {
		return getProvider().getEntityNamed(entityName);
	}

	/**
	 * Retrieves the registered {@link ExpressionFactory} that was registered for the given unique identifier.
	 *
	 * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to retrieve
	 * @return The {@link ExpressionFactory} mapped with the given unique identifier
	 */
	protected final ExpressionFactory getExpressionFactory(String expressionFactoryId) {
		return getExpressionRegistry().getExpressionFactory(expressionFactoryId);
	}

	/**
	 * Returns the registry containing the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link
	 * org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory ExpressionFactories} that are used
	 * to properly parse a JPQL query.
	 *
	 * @return The registry containing the information related to the JPQL grammar
	 */
	protected ExpressionRegistry getExpressionRegistry() {
		return getQueryContext().getExpressionRegistry();
	}

	protected AcceptableTypeVisitor getExpressionTypeVisitor() {
		AcceptableTypeVisitor helper = getHelper(AcceptableTypeVisitor.class);
		if (helper == null) {
			helper = buildAcceptableTypeVisitor();
			registerHelper(AcceptableTypeVisitor.class, helper);
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

	protected ClauseHelper<AbstractFromClause> getFromClauseHelper() {
		FromClauseHelper helper = getHelper(FromClauseHelper.class);
		if (helper == null) {
			helper = buildFromClauseHelper();
			registerHelper(FromClauseHelper.class, helper);
		}
		return helper;
	}

	protected FromClauseSelectStatementHelper getFromClauseSelectStatementHelper() {
		FromClauseSelectStatementHelper helper = getHelper(FromClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildFromClauseSelectStatementHelper();
			registerHelper(FromClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	/**
	 * Returns the JPQL grammar that will be used to define how to parse a JPQL query.
	 *
	 * @return The grammar that was used to parse this {@link Expression}
	 */
	protected JPQLGrammar getGrammar() {
		return getQueryContext().getGrammar();
	}

	protected GroupByClauseCollectionHelper getGroupByClauseCollectionHelper() {
		GroupByClauseCollectionHelper helper = getHelper(GroupByClauseCollectionHelper.class);
		if (helper == null) {
			helper = buildGroupByClauseCollectionHelper();
			registerHelper(GroupByClauseCollectionHelper.class, helper);
		}
		return helper;
	}

	protected GroupByClauseSelectStatementHelper getGroupByClauseSelectStatementHelper() {
		GroupByClauseSelectStatementHelper helper = getHelper(GroupByClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildGroupByClauseSelectStatementHelper();
			registerHelper(GroupByClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	protected ClauseHelper<HavingClause> getHavingClauseHelper() {
		HavingClauseHelper helper = getHelper(HavingClauseHelper.class);
		if (helper == null) {
			helper = buildHavingClauseHelper();
			registerHelper(HavingClauseHelper.class, helper);
		}
		return helper;
	}

	protected HavingClauseSelectStatementHelper getHavingClauseSelectStatementHelper() {
		HavingClauseSelectStatementHelper helper = getHelper(HavingClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildHavingClauseSelectStatementHelper();
			registerHelper(HavingClauseSelectStatementHelper.class, helper);
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
		return getExpressionRegistry().getIdentifierRole(identifier);
	}

	/**
	 * Retrieves the JPA version in which the identifier was first introduced.
	 *
	 * @return The version in which the identifier was introduced
	 */
	protected JPAVersion getIdentifierVersion(String identifier) {
		return getExpressionRegistry().getIdentifierVersion(identifier);
	}

	protected CompletenessVisitor getIncompleteCollectionExpressionVisitor() {
		IncompleteCollectionExpressionVisitor helper = getHelper(IncompleteCollectionExpressionVisitor.class);
		if (helper == null) {
			helper = buildIncompleteCollectionExpressionVisitor();
			registerHelper(IncompleteCollectionExpressionVisitor.class, helper);
		}
		return helper;
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
	 * Returns the version of the Java Persistence this entity for which it was defined.
	 *
	 * @return The version of the Java Persistence being used
	 */
	protected JPAVersion getJPAVersion() {
		return getQueryContext().getJPAVersion();
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	protected JPQLExpression getJPQLExpression() {
		return context.getJPQLExpression();
	}

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code> otherwise
	 */
	protected IManagedType getManagedType(IType type) {
		return getProvider().getManagedType(type);
	}

	/**
	 * Retrieves the {@link IMappedSuperclass} for the given {@link IType} if one exists.
	 *
	 * @param type The {@link IType} that is mapped as an embeddable
	 * @return The given {@link IType} mapped as an mapped super class; <code>null</code> if none
	 * exists or if it's mapped as a different managed type
	 */
	protected IMappedSuperclass getMappedSuperclass(IType type) {
		return getProvider().getMappedSuperclass(type);
	}

	/**
	 * Returns the {@link IMapping} for the field represented by the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} representing a state field path expression or a
	 * collection-valued path expression
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	protected IMapping getMapping(Expression expression) {
		return context.getMapping(expression);
	}

	protected Filter<IMapping> getMappingCollectionFilter() {
		CollectionMappingFilter helper = getHelper(CollectionMappingFilter.class);
		if (helper == null) {
			helper = buildCollectionMappingFilter();
			registerHelper(CollectionMappingFilter.class, helper);
		}
		return helper;
	}

	protected MappingFilterBuilder getMappingFilterBuilder() {
		MappingFilterBuilder helper = (MappingFilterBuilder) helpers.get(MappingFilterBuilder.class);
		if (helper == null) {
			helper = buildMappingFilterBuilder();
			helpers.put(MappingFilterBuilder.class, helper);
		}
		return helper;
	}

	protected Filter<IMapping> getMappingPropertyFilter() {
		PropertyMappingFilter helper = (PropertyMappingFilter) helpers.get(PropertyMappingFilter.class);
		if (helper == null) {
			helper = buildPropertyMappingFilter();
			helpers.put(PropertyMappingFilter.class, helper);
		}
		return helper;
	}

	/**
	 * Returns the visitor that collects the {@link NullExpression} if it's been visited.
	 *
	 * @return The {@link NullExpressionVisitor}
	 * @see #buildNullExpressionVisitor()
	 */
	protected NullExpressionVisitor getNullExpressionVisitor() {
		if (nullExpressionVisitor == null) {
			nullExpressionVisitor = buildNullExpressionVisitor();
		}
		return nullExpressionVisitor;
	}

	protected OrderByClauseCollectionHelper getOrderByClauseCollectionHelper() {
		OrderByClauseCollectionHelper helper = getHelper(OrderByClauseCollectionHelper.class);
		if (helper == null) {
			helper = buildOrderByClauseCollectionHelper();
			registerHelper(OrderByClauseCollectionHelper.class, helper);
		}
		return helper;
	}

	protected OrderByClauseSelectStatementHelper getOrderByClauseSelectStatementHelper() {
		OrderByClauseSelectStatementHelper helper = getHelper(OrderByClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildOrderByClauseSelectStatementHelper();
			registerHelper(OrderByClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	/**
	 * Returns the position of the {@link Expression} within the parsed tree representation of the
	 * JPQL query. The beginning of the string representation is the position returned.
	 *
	 * @param expression The {@link Expression} to find its position in the tree based on the string
	 * representation
	 * @return The position within the parsed tree of the given {@link Expression}
	 */
	protected int getPosition(Expression expression) {
		return queryPosition.getPosition(expression);
	}

	/**
	 * Returns the object that contains the valid proposals based on the position of the cursor
	 * within the JPQL query.
	 *
	 * @return The list of proposals
	 */
	public DefaultContentAssistProposals getProposals() {
		return proposals;
	}

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	protected IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * Returns the external form of the JPQL query.
	 *
	 * @return The external form of the JPQL query
	 */
	protected IQuery getQuery() {
		return context.getQuery();
	}

	/**
	 * Retrieves the BNF object that was registered for the given unique identifier.
	 *
	 * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} to retrieve
	 * @return The {@link JPQLQueryBNF} representing a section of the grammar
	 */
	protected JPQLQueryBNF getQueryBNF(String queryBNFId) {
		return getExpressionRegistry().getQueryBNF(queryBNFId);
	}

	/**
	 * Returns the {@link JPQLQueryContext} that is used by this visitor.
	 *
	 * @return The {@link JPQLQueryContext} holding onto the JPQL query and the cached information
	 */
	protected JPQLQueryContext getQueryContext() {
		return context;
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return A non-<code>null</code> string representation of the JPQL query
	 */
	protected String getQueryExpression() {
		return getQuery().getExpression();
	}

	protected RangeVariableDeclarationVisitor getRangeVariableDeclarationVisitor() {
		RangeVariableDeclarationVisitor helper = getHelper(RangeVariableDeclarationVisitor.class);
		if (helper == null) {
			helper = buildRangeVariableDeclarationVisitor();
			registerHelper(RangeVariableDeclarationVisitor.class, helper);
		}
		return helper;
	}

	/**
	 * Creates or retrieved the cached {@link Resolver} for the given {@link Expression}. The
	 * {@link Resolver} can return the {@link IType} and {@link ITypeDeclaration} of the {@link
	 * Expression} and either the {@link IManagedType} or the {@link IMapping}.
	 *
	 * @param expression The {@link Expression} for which its {@link Resolver} will be retrieved
	 * @return {@link Resolver} for the given {@link Expression}
	 */
	protected Resolver getResolver(Expression expression) {
		return context.getResolver(expression);
	}

	protected ResultVariableVisitor getResultVariableVisitor() {
		ResultVariableVisitor helper = getHelper(ResultVariableVisitor.class);
		if (helper == null) {
			helper = buildResultVariableVisitor();
			registerHelper(ResultVariableVisitor.class, helper);
		}
		return helper;
	}

	protected CompletenessVisitor getSelectClauseCompletenessVisitor() {
		SelectClauseCompletenessVisitor helper = (SelectClauseCompletenessVisitor) helpers.get(SelectClauseCompletenessVisitor.class);
		if (helper == null) {
			helper = buildSelectClauseCompleteness();
			helpers.put(SelectClauseCompletenessVisitor.class, helper);
		}
		return helper;
	}

	protected SelectClauseSelectStatementHelper getSelectClauseSelectStatementHelper() {
		SelectClauseSelectStatementHelper helper = getHelper(SelectClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildSelectClauseSelectStatementHelper();
			registerHelper(SelectClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	protected SimpleFromClauseSelectStatementHelper getSimpleFromClauseSelectStatementHelper() {
		SimpleFromClauseSelectStatementHelper helper = getHelper(SimpleFromClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildSimpleFromClauseSelectStatementHelper();
			registerHelper(SimpleFromClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	protected SimpleGroupByClauseSelectStatementHelper getSimpleGroupByClauseSelectStatementHelper() {
		SimpleGroupByClauseSelectStatementHelper helper = getHelper(SimpleGroupByClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildSimpleGroupByClauseSelectStatementHelper();
			registerHelper(SimpleGroupByClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	protected SimpleHavingClauseSelectStatementHelper getSimpleHavingClauseSelectStatementHelper() {
		SimpleHavingClauseSelectStatementHelper helper = getHelper(SimpleHavingClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildSimpleHavingClauseSelectStatementHelper();
			registerHelper(SimpleHavingClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	protected SimpleSelectClauseSelectStatementHelper getSimpleSelectClauseSelectStatementHelper() {
		SimpleSelectClauseSelectStatementHelper helper = getHelper(SimpleSelectClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildSimpleSelectClauseSelectStatementHelper();
			registerHelper(SimpleSelectClauseSelectStatementHelper.class, helper);
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

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	protected IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Returns the {@link IType} of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} for which its type will be calculated
	 * @return Either the {@link IType} that was resolved by this {@link Resolver} or the
	 * {@link IType} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected IType getType(Expression expression) {
		return context.getType(expression);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	protected IType getType(String typeName) {
		return getTypeRepository().getType(typeName);
	}

	/**
	 * Returns the {@link ITypeDeclaration} of the field handled by this {@link Resolver}.
	 *
	 * @param expression The {@link Expression} for which its type declaration will be calculated
	 * @return Either the {@link ITypeDeclaration} that was resolved by this {@link Resolver} or the
	 * {@link ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected ITypeDeclaration getTypeDeclaration(Expression expression) {
		return context.getTypeDeclaration(expression);
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
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	protected ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	protected UpdateItemCollectionHelper getUpdateItemCollectionHelper() {
		UpdateItemCollectionHelper helper = getHelper(UpdateItemCollectionHelper.class);
		if (helper == null) {
			helper = buildUpdateItemCollectionHelper();
			registerHelper(UpdateItemCollectionHelper.class, helper);
		}
		return helper;
	}

	/**
	 * Initializes this visitor.
	 */
	protected void initialize() {

		helpers = new HashMap<Class<?>, Object>();
		lockedExpressions = new Stack<Expression>();

		virtualSpaces = new Stack<Integer>();
		virtualSpaces.add(0);

		positionInCollections = new Stack<Integer>();
		positionInCollections.add(-1);

		corrections = new Stack<Integer>();
		corrections.add(0);
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

	protected boolean isAppendable(Expression expression) {
		AppendableExpressionVisitor visitor = getAppendableExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.appendable;
		}
		finally {
			visitor.appendable = false;
		}
	}

	protected boolean isAppendableToCollection(Expression expression) {
		CompletenessVisitor visitor = getIncompleteCollectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
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

	protected boolean isComplete(Expression expression) {
		CompletenessVisitor visitor = getCompletenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
	}

	protected boolean isCompoundable(Expression expression) {
		CompoundExpressionHelper visitor = getCompoundExpressionHelper();
		try {
			expression.accept(visitor);
			return visitor.isCompoundable();
		}
		finally {
			visitor.dispose();
		}
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

		// Only the s full JPQL identifier is valid
		if (identifier == IS || identifier == OF) {
			return false;
		}

		return getIdentifierRole(identifier) == IdentifierRole.COMPOUND_FUNCTION;
	}

	protected boolean isConditionalExpressionComplete(Expression expression) {
		CompletenessVisitor visitor = getConditionalExpressionCompletenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
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

	protected boolean isGroupByComplete(Expression expression) {
		// TODO
		CompletenessVisitor visitor = getCompletenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
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
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * Determines whether a <code><b>JOIN FETCH</b></code> expression can be identified by with an
	 * identification variable or not.
	 *
	 * @return <code>true</code> if the expression can have an identification variable; false
	 * otherwise
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
	 * Determines whether the given {@link Expression} is the {@link NullExpression}.
	 *
	 * @param expression The {@link Expression} to visit
	 * @return <code>true</code> if the given {@link Expression} is {@link NullExpression};
	 * <code>false</code> otherwise
	 */
	protected boolean isNull(Expression expression) {
		NullExpressionVisitor visitor = getNullExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * Determines whether the given position is within the given word.
	 * <p>
	 * Example: position=0, word="JPQL" => true
	 * Example: position=1, word="JPQL" => true
	 * Example: position=4, word="JPQL" => true
	 * Example: position=5, word="JPQL" => true
	 * Example: position=5, offset 2, (actual cursor position is 3), word="JPQL" => true
	 *
	 * @param position The position of the cursor
	 * @param offset The offset to adjust the position
	 * @param word The word to check if the cursor is positioned in it
	 * @return <code>true</code> if the given position is within the given word; <code>false</code>
	 * otherwise
	 */
	protected boolean isPositionWithin(int position, int offset, String word) {
		return (position >= offset) && (position - offset <= word.length());
	}

	/**
	 * Determines whether the given position is within the given word.
	 * <p>
	 * Example: position=0, word="JPQL" => true
	 * Example: position=1, word="JPQL" => true
	 * Example: position=4, word="JPQL" => true
	 * Example: position=5, word="JPQL" => true
	 *
	 * @param position The position of the cursor
	 * @param word The word to check if the cursor is positioned in it
	 * @return <code>true</code> if the given position is within the given word; <code>false</code>
	 * otherwise
	 */
	protected boolean isPositionWithin(int position, String word) {
		return isPositionWithin(position, 0, word);
	}

	protected boolean isPreviousClauseComplete(AbstractSelectStatement expression,
	                                           SelectStatementHelper<AbstractSelectStatement, Expression> helper) {

		helper = cast(helper.getPreviousHelper());

		if ((helper == null) || !helper.hasClause(expression)) {
			return false;
		}

		Expression clause = helper.getClause(expression);
		Expression clauseExpression = helper.getClauseExpression(clause);
		return helper.isClauseExpressionComplete(clauseExpression);
	}

	protected boolean isResultVariable(Expression expression) {
		ResultVariableVisitor visitor = getResultVariableVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	protected boolean isSelectExpressionComplete(Expression expression) {
		CompletenessVisitor visitor = getSelectClauseCompletenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
	}

	/**
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	protected boolean isValid(Expression expression, JPQLQueryBNF queryBNF) {
		JPQLQueryBNFValidator validator = buildJPQLQueryBNFValidator(queryBNF);
		try {
			expression.accept(validator);
			return validator.valid;
		}
		finally {
			validator.valid = false;
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
		JPAVersion identifierVersion = getIdentifierVersion(identifier);
		return getJPAVersion().isNewerThanOrEqual(identifierVersion);
	}

	/**
	 * Returns the length of the string representation for the given {@link Expression}. The text
	 * containing any virtual text will be used.
	 *
	 * @param expression The {@link Expression} used to calculate the length of its string
	 * representation
	 * @return The length of the text, which may contain virtual text
	 */
	protected int length(Expression expression) {
		return expression.getLength();
	}

	/**
	 * Prepares this visitor by pre-populating it with the necessary data that is required to properly
	 * gather the list of proposals based on the caret position.
	 *
	 * @param queryPosition Contains the position of the cursor within the parsed {@link Expression}
	 */
	public void prepare(QueryPosition queryPosition) {

		this.queryPosition = queryPosition;
		this.proposals     = new DefaultContentAssistProposals(getGrammar());

		wordParser = new WordParser(context.getJPQLExpression().toActualText());
		wordParser.setPosition(queryPosition.getPosition());
		word = wordParser.partialWord();
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

		// Adjust the position to be the "beginning" of the expression by adding a "correction"
		corrections.add(getPosition(expression));
		super.visit(expression);
		corrections.pop();

		// Add the possible abstract schema names
		addEntities();
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
		visitLogicalExpression(expression, AND);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();

		// After the arithmetic factor
		if (position == 1) {
			addAllIdentificationVariables();
			addAllFunctions(expression.getQueryBNF());
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
		corrections.add(getPosition(expression));
		super.visit(expression);
		corrections.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {
		super.visit(expression);

		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasExpression()) {
			length += length(expression.getExpression()) + SPACE_LENGTH;
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
				addAllIdentificationVariables();
				addAllFunctions(InternalBetweenExpressionBNF.ID);
			}

			// After lower bound
			if (expression.hasLowerBoundExpression()) {

				// Check for something like "<lower bound> <word>"
				int lowerBoundLength = length(expression.getLowerBoundExpression());

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
								addAllIdentificationVariables();
								addAllFunctions(InternalBetweenExpressionBNF.ID);
							}
						}
						else if (!expression.hasAnd() &&
						          expression.hasUpperBoundExpression()) {

							length += length(expression.getUpperBoundExpression());

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
		int position = getPosition(expression) - corrections.peek();

		// Within "CASE"
		if (isPositionWithin(position, CASE)) {
			if (isValidVersion(CASE)) {
				proposals.addIdentifier(CASE);
			}
		}
		// After "CASE "
		else if (expression.hasSpaceAfterCase()) {
			int length = CASE.length() + SPACE_LENGTH;

			// Right after "CASE "
			if (position == length) {
				addAllIdentificationVariables();
				addAllFunctions(CaseOperandBNF.ID);
				proposals.addIdentifier(WHEN);
			}

			// After "<case operand> "
			if (expression.hasCaseOperand() &&
			    expression.hasSpaceAfterCaseOperand()) {

				length += length(expression.getCaseOperand()) + SPACE_LENGTH;

				// Right after "<case operand> "
				if (position == length) {
					proposals.addIdentifier(WHEN);
				}
			}

			// After "<when clauses> "
			if (expression.hasWhenClauses() &&
			    expression.hasSpaceAfterWhenClauses()) {

				length += length(expression.getWhenClauses()) + SPACE_LENGTH;

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
						addAllIdentificationVariables();
						addAllFunctions(CaseOperandBNF.ID);
					}

					// After "<else expression> "
					if (expression.hasElseExpression() &&
					    expression.hasSpaceAfterElseExpression()) {

						length += length(expression.getElseExpression()) + SPACE_LENGTH;

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
		visitEncapsulatedExpression(expression, COALESCE, expression.encapsulatedExpressionBNF());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		// Adjust the index within the collection
		positionInCollections.add(findExpressionPosition(expression));
		super.visit(expression);
		positionInCollections.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();

		// Within "IN"
		if (isPositionWithin(position, IN)) {
			proposals.addIdentifier(IN);
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
				addAllFunctions(CollectionValuedPathExpressionBNF.ID);
			}

			// After "<collection-valued path expression>)"
			if (expression.hasRightParenthesis()) {
				length += length(expression.getCollectionValuedPathExpression()) + 1 /* ')' */;

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
		int position = getPosition(expression) - corrections.peek();
		String identifier = expression.getIdentifier();
		int length = 0;

		if (expression.hasEntityExpression()) {
			length = length(expression.getEntityExpression()) + SPACE_LENGTH;
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
				addAllIdentificationVariables();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		visitPathExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasLeftExpression()) {
			length += length(expression.getLeftExpression()) + SPACE_LENGTH;
		}

		// Within the comparison operator
		if (isPositionWithin(position, length, expression.getComparisonOperator())) {
			addAllExpressionFactoryIdentifiers(ComparisonExpressionFactory.ID);
		}

		// After the comparison operator
		length += expression.getComparisonOperator().length();

		if (expression.hasSpaceAfterIdentifier()) {
			length++;
		}

		// Right after the comparison operator
		if (position == length) {
			addAllIdentificationVariables();
			addAllFunctions(expression.rightExpressionBNF());
			addAllClauses(expression.rightExpressionBNF());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {
		super.visit(expression);
		visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();

		// NEW
		if (isPositionWithin(position, NEW)) {
			proposals.addIdentifier(NEW);
		}
		// After "NEW "
		else if (expression.hasSpaceAfterNew()) {
			int length = NEW.length() + SPACE_LENGTH;

			// Right after "NEW "
			if (position == length) {
				// TODO: Show all the instantiable classes
			}

			// After "("
			if (expression.hasLeftParenthesis()) {
				String className = expression.getClassName();
				length += className.length() + SPACE_LENGTH;

				// Right after "("
				if (position == length) {
					addAllIdentificationVariables();
					addAllFunctions(ConstructorItemBNF.ID);
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
		int position = getPosition(expression) - corrections.peek();

		// Within the identifier
		if (expression.isCurrentDate()      && isPositionWithin(position, CURRENT_DATE) ||
		    expression.isCurrentTime()      && isPositionWithin(position, CURRENT_TIME) ||
		    expression.isCurrentTimestamp() && isPositionWithin(position, CURRENT_TIMESTAMP)) {

			proposals.addIdentifier(CURRENT_DATE);
			proposals.addIdentifier(CURRENT_TIME);
			proposals.addIdentifier(CURRENT_TIMESTAMP);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitClause(expression, DELETE_FROM, expression.hasSpaceAfterFrom(), getDeleteClauseHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitDeleteStatement(expression);
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
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasExpression()) {
			length = length(expression.getExpression()) + SPACE_LENGTH;
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
		corrections.add(getPosition(expression));
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
			visitClause(expression, HAVING, expression.hasSpaceAfterIdentifier(), getHavingClauseHelper());
			visitCompoundableExpression(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {
		corrections.add(getPosition(expression));
		super.visit(expression);
		corrections.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {
		super.visit(expression);

		// After the range variable declaration
		if (expression.hasSpace()) {
			int position = getPosition(expression) - corrections.peek();
			int length = length(expression.getRangeVariableDeclaration()) + SPACE_LENGTH;

			// Right after the range variable declaration
			if (position == length) {
				addJoinIdentifiers();
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
		expression.accept(visitParentVisitor());

		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasExpression()) {
			length += length(expression.getExpression()) + SPACE_LENGTH;
		}

		// Within "IN"
		if (isPositionWithin(position, length, expression.getIdentifier())) {
			proposals.addIdentifier(IN);
			proposals.addIdentifier(NOT_IN);
		}
		// After "IN("
		else if (expression.hasLeftParenthesis()) {
			length += expression.getIdentifier().length() + SPACE_LENGTH;

			// Right after "IN("
			if (position == length) {
				addAllFunctions(InExpressionItemBNF.ID);
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
		int position = getPosition(expression) - corrections.peek();
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

				length += length(expression.getJoinAssociationPath()) + SPACE_LENGTH;

				// Right after "join association path expression "
				// Make sure to verify if AS can be added if it's a JOIN FETCH expression
				if (!joinFetch || joinFetch && isJoinFetchIdentifiable()) {
					if (isPositionWithin(position, length, AS)) {
						addIdentifier(AS);
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

		if (!isLocked(expression)) {

			int position = getPosition(expression);

			// At the beginning of the query
			if (position == 0) {
				addIdentifier(SELECT);
				addIdentifier(UPDATE);
				addIdentifier(DELETE_FROM);
			}
			else {
				Expression queryStatement = expression.getQueryStatement();
				boolean hasQueryStatement = expression.hasQueryStatement();
				int length = hasQueryStatement ? length(queryStatement) : 0;

				// After the query, inside the invalid query (or ending whitespace)
				if (position > length) {
					String text = expression.getUnknownEndingStatement().toActualText();

					// Only try to add the identifiers if there is no query statement,
					// they cannot be added at the end of a query
					if (!hasQueryStatement) {
						addIdentifier(SELECT,      text);
						addIdentifier(DELETE_FROM, text);
						addIdentifier(UPDATE,      text);
					}
					else {

						lockedExpressions.add(expression);
						corrections.add(-length - 2); // -2 because the position of any Expression is -1

						queryStatement.accept(this);

						corrections.pop();
						lockedExpressions.pop();
					}
				}
			}
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

		corrections.add(getPosition(expression));
		super.visit(expression);
		corrections.pop();

		int position = getPosition(expression) - corrections.peek();
		String keyword = expression.getText();

		// Within the identifier
		if ((keyword == TRUE)  && isPositionWithin(position, TRUE)  ||
		    (keyword == FALSE) && isPositionWithin(position, FALSE) ||
		    (keyword == NULL)  && isPositionWithin(position, NULL)) {

			proposals.addIdentifier(TRUE);
			proposals.addIdentifier(FALSE);
			proposals.addIdentifier(NULL);
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
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasStringExpression()) {
			length += length(expression.getStringExpression()) + SPACE_LENGTH;
		}

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

				length += length(expression.getPatternValue()) + SPACE_LENGTH;

				// Within "ESCAPE"
				if (isPositionWithin(position, length, ESCAPE)) {
					proposals.addIdentifier(ESCAPE);
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
		int position = getPosition(expression) - corrections.peek();

		// Within "NOT
		if (isPositionWithin(position, NOT)) {
			proposals.addIdentifier(NOT);

			// Also add the negated JPQL identifiers
			if (!expression.hasExpression()) {
				int currentPosition = queryPosition.getPosition();
				addIdentifier(NOT_BETWEEN,   currentPosition);
				addIdentifier(NOT_EXISTS,    currentPosition);
				addIdentifier(NOT_IN,        currentPosition);
				addIdentifier(NOT_LIKE,      currentPosition);
				addIdentifier(NOT_MEMBER,    currentPosition);
				addIdentifier(NOT_MEMBER_OF, currentPosition);

				// In case IS is in the query right before NOT
				addIdentifier(IS_NOT_EMPTY,  currentPosition);
				addIdentifier(IS_NOT_NULL,   currentPosition);
			}
		}
		// After "NOT "
		else if (expression.hasSpaceAfterNot()) {
			int length = NOT.length() + SPACE_LENGTH;

			// Right after "NOT "
			if (position == length) {
				boolean canAddCompoundIdentifiers = !expression.hasExpression();

				if (!canAddCompoundIdentifiers) {
					String variableName = context.literal(
						expression.getExpression(),
						LiteralType.IDENTIFICATION_VARIABLE
					);
					canAddCompoundIdentifiers = ExpressionTools.stringIsNotEmpty(variableName);
				}

				if (canAddCompoundIdentifiers) {
					int currentPosition = queryPosition.getPosition();
					addIdentifier(NOT_BETWEEN,   currentPosition);
					addIdentifier(NOT_EXISTS,    currentPosition);
					addIdentifier(NOT_IN,        currentPosition);
					addIdentifier(NOT_LIKE,      currentPosition);
					addIdentifier(NOT_MEMBER,    currentPosition);
					addIdentifier(NOT_MEMBER_OF, currentPosition);

					// In case IS is in the query right before NOT
					addIdentifier(IS_NOT_EMPTY,  currentPosition);
					addIdentifier(IS_NOT_NULL,   currentPosition);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasExpression()) {
			length += length(expression.getExpression()) + SPACE_LENGTH;
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
		int position = getPosition(expression) - corrections.peek();

		// After the order by item
		if (expression.hasExpression()) {
			int length = length(expression.getExpression());

			if (expression.hasSpaceAfterExpression()) {
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
		visitLogicalExpression(expression, OR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();

		// After "<abstract schema name> "
		if (expression.hasRootObject() &&
		    expression.hasSpaceAfterRootObject()) {

			int length = length(expression.getRootObject()) + SPACE_LENGTH;

			// Right after "<abstract schema name> "
			if (isPositionWithin(position, length, AS)) {
				addIdentifier(AS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasSelectExpression()) {
			length += length(expression.getSelectExpression()) + SPACE_LENGTH;
		}

		// Within "AS"
		if (isPositionWithin(position, length, AS)) {
			addIdentifier(AS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitSelectClause(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitSelectStatement(expression, getSelectClauseSelectStatementHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitClause(expression, FROM, expression.hasSpaceAfterFrom(), getFromClauseHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitSelectClause(expression);
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
			visitSelectStatement(expression, getSimpleSelectClauseSelectStatementHelper());
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
		expression.accept(visitParentVisitor());
		visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		visitPathExpression(expression);
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
		corrections.add(getPosition(expression));
		super.visit(expression);
		corrections.pop();
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
		int position = getPosition(expression) - getCorrections().peek();

		// Within "TREAT"
		if (isPositionWithin(position, TREAT)) {
			if (isValidVersion(TREAT)) {
				proposals.addIdentifier(TREAT);
			}
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
				length += length(collectionValuedPathExpression) + SPACE_LENGTH;

				// Within "AS"
				if (isPositionWithin(position, length, AS)) {
					getProposals().addIdentifier(AS);

					// If the entity type is not specified, then we can add
					// the possible abstract schema names
					if (!expression.hasEntityType()) {

						// If the type of the path expression is resolvable,
						// then filter the abstract schema types
						IType type = getType(collectionValuedPathExpression);

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
					IType type = getType(expression.getCollectionValuedPathExpression());

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
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		// Within "TRIM"
		if (isPositionWithin(position, TRIM)) {
			proposals.addIdentifier(TRIM);
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

					addAllIdentificationVariables();
					addAllFunctions(StringPrimaryBNF.ID);
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

						addAllIdentificationVariables();
						addAllFunctions(StringPrimaryBNF.ID);
					}
				}

				length += specification.length();
			}

			if (expression.hasSpaceAfterSpecification()) {
				length += SPACE_LENGTH;
			}

			// Trim character
			if (expression.hasTrimCharacter()) {
				length += length(expression.getTrimCharacter());
			}

			if (expression.hasSpaceAfterTrimCharacter()) {
				length += SPACE_LENGTH;
			}

			// Right after "<trim_character> "
			if (position == length) {
				addIdentifier(FROM);

				if (!expression.hasFrom()) {
					addAllIdentificationVariables();
					addAllFunctions(StringPrimaryBNF.ID);
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
				addAllIdentificationVariables();
				addAllFunctions(StringPrimaryBNF.ID);
			}

			// Right after the string literal but there is no trim character,
			// nor FROM and there is a virtual space
			if (expression.hasExpression()) {
				length += length(expression.getExpression());

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

		corrections.add(getPosition(expression));
		super.visit(expression);
		corrections.pop();

		int position = queryPosition.getPosition();
		String word = this.word;
		this.word = wordParser.substring(position - expression.getLength(), position);

		// Special cases
		addIdentifier(IS_EMPTY,     position);
		addIdentifier(IS_NOT_EMPTY, position);
		addIdentifier(IS_NOT_NULL,  position);
		addIdentifier(IS_NULL,      position);

		this.word = word;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		super.visit(expression);
		int position = getPosition(expression) - corrections.peek();

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

					length += length(rangeVariableDeclaration.getRootObject()) + SPACE_LENGTH;

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
							length += length(rangeVariableDeclaration.getIdentificationVariable());
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
								addAllIdentificationVariables();
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
		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		// At the beginning
		if (position == length) {
			addAllIdentificationVariables();
		}
		else if (expression.hasStateFieldPathExpression() &&
		         expression.hasSpaceAfterStateFieldPathExpression()) {

			length += length(expression.getStateFieldPathExpression()) + SPACE_LENGTH;

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
					addAllIdentificationVariables();
					addAllFunctions(NewValueBNF.ID);
				}
				else if (expression.hasSpaceAfterEqualSign()) {
					length++;

					// Right after "= "
					if (position == length) {
						addAllIdentificationVariables();
						addAllFunctions(NewValueBNF.ID);
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
			visitUpdateStatement(expression);
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
		int position = getPosition(expression) - corrections.peek();

		// Within "WHEN"
		if (isPositionWithin(position, WHEN)) {
			proposals.addIdentifier(WHEN);
		}
		// After "WHEN "
		else if (expression.hasSpaceAfterWhen()) {
			int length = WHEN.length() + SPACE_LENGTH;

			// Right after "WHEN "
			if (position == length) {
				addAllIdentificationVariables();
				addAllFunctions(InternalWhenClauseBNF.ID);
			}
			else {
				length += length(expression.getWhenExpression());

				// After "WHEN <expression> " => THEN
				if (expression.hasSpaceAfterWhenExpression()) {
					length++;

					// Right after "WHEN <expression> " => THEN
					if (position == length) {
						proposals.addIdentifier(THEN);
					}
					else if (expression.hasThen()) {
						// Within "THEN"
						if (isPositionWithin(position, length, THEN)) {
							proposals.addIdentifier(THEN);
						}
						else {
							length += THEN.length();

							// After "WHEN <expression> THEN "
							if (expression.hasSpaceAfterThen()) {
								length++;

								// Right after "WHEN <expression> THEN "
								if (position == length) {
									addScalarExpressionProposals();
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
			visitClause(expression, WHERE, expression.hasSpaceAfterIdentifier(), whereClauseHelper());
			visitCompoundableExpression(expression);
		}
	}

	protected void visitAggregateFunction(AggregateFunction expression) {

		int position = getPosition(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within "<identifier>"
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(identifier);
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
					addAllIdentificationVariables();
					addAllFunctions(expression.encapsulatedExpressionBNF());
				}
			}
		}
	}

	protected void visitArithmeticExpression(ArithmeticExpression expression) {

		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasLeftExpression()) {
			length += length(expression.getLeftExpression()) + SPACE_LENGTH;
		}

		// Within the arithmetic sign
		if (isPositionWithin(position, length, PLUS)) {
			addAllAggregates(expression.getQueryBNF());
		}
		// After the arithmetic sign, with or without the space
		else if (expression.hasSpaceAfterIdentifier()) {
			length += 2;

			// Right after the space
			if ((position == length) && (positionInCollections.peek() == -1)) {
				addAllIdentificationVariables();
				addAllFunctions(expression.rightExpressionBNF(), position);
			}
		}
	}

	protected <T extends AbstractExpression> void visitClause(T expression,
	                                                          String identifier,
	                                                          boolean hasSpaceAfterIdentifier,
	                                                          ClauseHelper<T> helper) {

		lockedExpressions.add(expression);
		int correction = corrections.peek();
		int position = getPosition(expression) - correction;

		// Within "<identifier>"
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(identifier);
		}
		// After "<identifier> "
		else if (hasSpaceAfterIdentifier) {
			int length = identifier.length() + SPACE_LENGTH;

			// Right after "<identifier> "
			if (position == length) {
				helper.addProposals(expression);
			}
			// Somewhere in the clause's expression
			else {
				Expression clauseExpression = helper.getClauseExpression(expression);
				int clauseExpressionLength = length(clauseExpression);

				// At the end of the clause's expression
				if (position == length + clauseExpressionLength + virtualSpaces.peek()) {

					virtualSpaces.add(SPACE_LENGTH);
					corrections.add(-clauseExpressionLength - 2);

					clauseExpression.accept(this);

					// Now ask the helper to add possible identifiers at the end of its expression
					if (isComplete(clauseExpression)) {
						helper.addAtTheEndOfExpression(expression);
					}

					virtualSpaces.pop();
					corrections.pop();
				}
				// At the end of the clause's expression, check to see if the identifier can be appended
				else if (position == length + clauseExpressionLength + virtualSpaces.peek() - correction) {

					// Now ask the helper to add possible identifiers at the end of its expression
					if (isAppendable(clauseExpression)) {
						helper.addAtTheEndOfExpression(expression);
					}
				}
			}
		}

		lockedExpressions.pop();
	}

	/**
	 * Adds the possible proposals for the given {@link Expression expression} based on the location of
	 * the cursor and the content of the expression.
	 *
	 * @param expression The {@link Expression expression} being visited
	 * @param identifier
	 * @param helper
	 */
	protected <T extends Expression> void visitCollectionExpression(T expression,
	                                                                String identifier,
	                                                                CollectionExpressionHelper<T> helper) {

		int position = getPosition(expression) - corrections.peek();

		// Within the identifier
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(identifier);
		}
		// After "<identifier>(" or "<identifier> "
		else if (helper.hasDelimiterAfterIdentifier(expression)) {
			int length = identifier.length() + 1 /* delimiter, either space or ( */;
			length += helper.preExpressionLength(expression);

			// Right after "<identifier>(" or "<identifier> "
			if (position == length) {
				helper.addProposals(expression, 0);
			}
			// Within the encapsulated expressions
			else {
				// Create a collection representation of the encapsulated expression(s)
				CollectionExpression collectionExpression = helper.buildCollectionExpression(expression);
				boolean hasComma = false;

				// Determine the maximum children count, it is possible the query contains more children
				// than the expession's grammar would actually allow. The content assist will only
				// provide assistance from the first child to the last allowed child
				int count = Math.min(collectionExpression.childrenSize(), helper.maxCollectionSize(expression));

				for (int index = 0; index < count; index++) {
					Expression child = collectionExpression.getChild(index);
					int childLength = 0;

					// At the beginning of the child expression
					if (position == length) {
						helper.addProposals(expression, index);
						break;
					}
					else {
						childLength = length(child);

						// At the end of the child expression
						if ((position == length + childLength + virtualSpaces.peek()) &&
						     isComplete(child)) {

							helper.addAtTheEndOfChild(expression, child, index);
							break;
						}
					}

					// Now add the child's length and length used by the comma and space
					length += childLength;

					// Move after the comma
					hasComma = collectionExpression.hasComma(index);

					if (hasComma) {
						length++;

						// After ',', the proposals can be added
						if (position == length) {
							helper.addProposals(expression, index + 1);
							break;
						}
					}

					// Move after the space that follows the comma
					if (collectionExpression.hasSpace(index)) {
						length++;
					}

					// Nothing more can be looked at
					if (position < length) {
						break;
					}
				}
			}
		}
	}

	protected void visitCompoundableExpression(AbstractConditionalClause expression) {

		if (expression.hasConditionalExpression()) {

			// Get the position and start length
			int position = getPosition(expression);
			int length = expression.getIdentifier().length() + SPACE_LENGTH;

			CompoundExpressionHelper helper = getCompoundExpressionHelper();

			try {
				// Start scanning the conditional expression
				expression.getConditionalExpression().accept(helper);

				// Visit the conditional expression in order to determine if identifiers defined has
				// compound (example: BETWEEN) can be added as possible proposals
				visitCompoundableExpression(helper, position, length);
			}
			finally {
				helper.dispose();
			}
		}
	}

	protected void visitCompoundableExpression(CompoundExpressionHelper helper,
	                                           int position,
	                                           int length) {

		length += helper.length();

		// At the end of an expression
		if (position == length) {
			if (helper.isCompoundable()) {
				addAllCompounds(ConditionalExpressionBNF.ID);
			}
		}
		// Continue inside of the conditional expression
		else if (helper.hasIdentifier()) {
			length += helper.identifierLength();

			if (helper.hasNext()) {
				helper.next();
				visitCompoundableExpression(helper, position, length);
			}
		}
	}

	protected void visitDeleteStatement(DeleteStatement expression) {

		lockedExpressions.add(expression);
		int position = getPosition(expression) - corrections.peek();

		//
		// DELETE clause
		//
		DeleteClause deleteClause = expression.getDeleteClause();
		int length = length(deleteClause);

		// At the end of the DELETE clause, check for adding proposals based
		// on possible incomplete information
		if ((position == length) && isAppendable(deleteClause)) {
			addIdentifier(WHERE);
		}
		// Right after the DELETE clause, the space is owned by JPQLExpression
		else if ((position == length + SPACE_LENGTH) && expression.hasSpaceAfterDeleteClause()) {

			virtualSpaces.add(SPACE_LENGTH);
			corrections.add(-length - 2);

			deleteClause.accept(this);

			corrections.pop();
			virtualSpaces.pop();
		}

		// Nothing else to do
		if ((position == length) && !expression.hasSpaceAfterDeleteClause()) {
			return;
		}

		if (expression.hasSpaceAfterDeleteClause()) {
			length++;
		}

		// Nothing else to do
		if ((position == length) && !deleteClause.hasRangeVariableDeclaration()) {
			return;
		}

		//
		// WHERE clause
		//
		// Right before "WHERE"
		if (position == length) {

			if (expression.hasSpaceAfterDeleteClause() &&
			    isComplete(deleteClause.getRangeVariableDeclaration())) {

				addIdentifier(WHERE);
			}
		}

		if (expression.hasWhereClause()) {
			AbstractConditionalClause whereClause = (AbstractConditionalClause) expression.getWhereClause();

			// Check for within the WHERE clause
			if (position > length) {
				int whereClauseLength = length(whereClause);
				length += whereClauseLength;

				// Right after the WHERE clause
				if (position == length + SPACE_LENGTH) {

					virtualSpaces.add(SPACE_LENGTH);
					corrections.add(-whereClauseLength - 2);

					whereClause.accept(this);

					corrections.pop();
					virtualSpaces.pop();
				}
			}
		}
	}

	/**
	 * Adds the possible proposals for the given {@link AbstractEncapsulatedExpression expression}
	 * based on the location of the cursor and the content of the expression.
	 *
	 * @param expression The {@link AbstractEncapsulatedExpression expression} being visited
	 * @param identifier
	 * @param jpqlQueryBNF
	 */
	protected void visitEncapsulatedExpression(AbstractEncapsulatedExpression expression,
	                                           String identifier,
	                                           String jpqlQueryBNF) {

		int position = getPosition(expression) - corrections.peek();

		// Within the identifier
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(identifier);
		}
		// Right after "<identifier>("
		else if (expression.hasLeftParenthesis()) {
			int length = identifier.length() + 1 /* '(' */;

			if (position == length) {
				addAllIdentificationVariables();
				addAllFunctions(jpqlQueryBNF);
			}
		}
	}

	protected void visitLogicalExpression(LogicalExpression expression, String identifier) {

		int position = getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasLeftExpression()) {
			length += length(expression.getLeftExpression()) + SPACE_LENGTH;
		}

		// Within "AND" or "OR"
		if (isPositionWithin(position, length, expression.getIdentifier())) {
			proposals.addIdentifier(identifier);
		}
		// After "AND " or "OR "
		else if (expression.hasSpaceAfterIdentifier()) {
			length += identifier.length() + SPACE_LENGTH;

			// Right after "AND " or "OR "
			if (position == length) {
				addAllIdentificationVariables();
				addAllFunctions(expression.rightExpressionBNF());
			}
		}
	}

	protected VisitParentVisitor visitParentVisitor() {
		VisitParentVisitor helper = getHelper(VisitParentVisitor.class);
		if (helper == null) {
			helper = buildVisitParentVisitor();
			registerHelper(VisitParentVisitor.class, helper);
		}
		return helper;
	}

	protected void visitPathExpression(AbstractPathExpression expression) {

		int position = getPosition(expression);
		String text = expression.toActualText();
		int dotIndex = text.indexOf(AbstractExpression.DOT);

		if (position > -1) {
			if ((dotIndex > -1) && (position > dotIndex)) {
				// Retrieve the filter based on the location of the state field path, for instance, in a
				// JOIN or IN expression, the filter has to filter out the property and accept the fields
				// of collection type
				visitPathExpression(expression, buildMappingFilter(expression));
			}
			else {
				String variableName = context.literal(
					expression.getIdentificationVariable(),
					LiteralType.IDENTIFICATION_VARIABLE
				);

				// Don't do anything if the identification variable is either KEY() or VALUE()
				if (ExpressionTools.stringIsNotEmpty(variableName)) {
					corrections.add(getPosition(expression));
					visit(expression);
					corrections.pop();
				}
			}
		}
	}

	protected void visitPathExpression(AbstractPathExpression expression, Filter<IMapping> helper) {

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

				mappingCollector = buildFilteringMappingCollector(expression, resolver, helper, path);
				mappingCollectorCreated = true;
				break;
			}
			// The path is entirely before the position of the cursor
			else {
				// The first path is always an identification variable
				if (resolver == null) {
					resolver = context.getResolver(expression.getIdentificationVariable());
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
			mappingCollector = buildMappingCollector(expression, resolver, helper);
		}

		proposals.addMappings(mappingCollector.buildProposals());
	}

	protected void visitSelectClause(AbstractSelectClause expression) {

		int position = getPosition(expression) - corrections.peek();

		// Within "SELECT"
		if (isPositionWithin(position, SELECT)) {
			proposals.addIdentifier(SELECT);
		}
		// After "SELECT "
		else if (expression.hasSpaceAfterSelect()) {
			int length = SELECT.length() + SPACE_LENGTH;

			// Within "DISTINCT"
			if (expression.hasDistinct() &&
			    isPositionWithin(position, length, DISTINCT)) {

				proposals.addIdentifier(DISTINCT);
			}
			// After "DISTINCT "
			else {
				if (expression.hasDistinct()) {
					length += DISTINCT.length();

					if (expression.hasSpaceAfterDistinct()) {
						length++;
					}
				}

				// Right after "SELECT " or after "DISTINCT "
				if (position == length) {
					if (!expression.hasDistinct()) {
						addIdentifier(DISTINCT);
					}
					addAllIdentificationVariables();
					addAllFunctions(expression.selectItemBNF());
				}
				// Somewhere in the clause's expression
				else {
					int selectExpressionLength = length(expression.getSelectExpression());

					// At the end of the clause's expression
					if (position <= length + selectExpressionLength + virtualSpaces.peek()) {
						addSelectExpressionProposals(expression, length);
					}
				}
			}
		}
	}

	protected SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression>
	        visitSelectStatement(AbstractSelectStatement expression,
	                             int position,
	                             int[] length,
	                             SelectStatementHelper<AbstractSelectStatement, Expression> helper) {

		// Right before the identifier
		if (position == length[0]) {

			if (helper.hasSpaceBeforeClause(expression) &&
			    isPreviousClauseComplete(expression, helper)) {

				helper.addClauseProposal();
			}

			return null;
		}

		if (helper.hasClause(expression)) {
			Expression clause = helper.getClause(expression);

			// Check for within the clause
			if (position > length[0]) {
				int clauseLength = length(clause);
				length[0] += clauseLength;
				boolean hasSpaceAfterIdentifier = helper.hasSpaceAfterClause(expression);
				Expression clauseExpression = helper.getClauseExpression(clause);

				// At the end of the clause, check for adding proposals based
				// on possible incomplete information
				if (position == length[0]) {
					helper.appendNextClauseProposals(expression, clause, position, false);
				}
				// Right after the clause, the space is owned by the select statement
				else if ((position == length[0] + SPACE_LENGTH) && hasSpaceAfterIdentifier) {

					virtualSpaces.add(SPACE_LENGTH);
					corrections.add(-clauseLength - 2);

					clause.accept(this);

					corrections.pop();
					virtualSpaces.pop();

					// Now add the following clause identifiers
					if (helper.isClauseExpressionComplete(clauseExpression)) {
						helper.appendNextClauseProposals(expression, clause, position, true);
					}
				}

				// Nothing else to do
				if ((position < length[0]) || (position == length[0]) && !hasSpaceAfterIdentifier) {
					return null;
				}

				if (hasSpaceAfterIdentifier) {
					length[0]++;
				}

				// Nothing else to do
				if ((position < length[0]) || (position == length[0]) && !helper.hasClauseExpression(clause)) {
					return null;
				}

				// Right before the next clause
				if (position == length[0]) {

					if (hasSpaceAfterIdentifier && helper.isClauseExpressionComplete(clauseExpression)) {
						helper.appendNextClauseProposals(expression, clause, position, true);
					}

					return null;
				}
			}
		}

		return helper.getNextHelper();
	}

	protected void visitSelectStatement(AbstractSelectStatement expression,
	                                    SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> helper) {

		lockedExpressions.add(expression);

		try {
			int position = getPosition(expression);
			int[] length = new int[1];

			while (helper != null) {
				helper = visitSelectStatement(expression, position, length, cast(helper));
			}
		}
		finally {
			lockedExpressions.pop();
		}
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

		int position = getPosition(expression) - corrections.peek();
		String actualIdentifier = expression.getIdentifier();
		boolean added = false;

		for (String identifier : expressionIdentifiers) {

			// Within the identifier
			if (isPositionWithin(position, actualIdentifier)) {
				proposals.addIdentifier(identifier);
			}
			// Right after "<identifier>("
			else if (expression.hasLeftParenthesis()) {
				int length = identifier.length() + 1 /* '(' */;

				if (!added && (position == length)) {
					added = true;

					addIdentificationVariables(identificationVariableType, expression);

					String queryBNF = expression.encapsulatedExpressionBNF();
					addAllFunctions(queryBNF);
					addAllClauses(queryBNF);
				}
			}
		}
	}

	protected void visitUpdateStatement(UpdateStatement expression) {

		lockedExpressions.add(expression);
		int position = getPosition(expression);

		//
		// UPDATE clause
		//
		UpdateClause updateClause = expression.getUpdateClause();
		int length = length(updateClause);

		// Right after the UPDATE clause, the space is owned by the select statement
		if ((position == length + SPACE_LENGTH) && expression.hasSpaceAfterUpdateClause()) {

			virtualSpaces.add(SPACE_LENGTH);
			corrections.add(-length - 2);

			updateClause.accept(this);

			corrections.pop();
			virtualSpaces.pop();
		}

		// Nothing else to do
		if ((position == length) && !expression.hasSpaceAfterUpdateClause()) {
			return;
		}

		if (expression.hasSpaceAfterUpdateClause()) {
			length++;
		}

		// Nothing else to do
		if ((position == length) && !updateClause.hasRangeVariableDeclaration()) {
			return;
		}

		//
		// WHERE clause
		//
		// Right before "WHERE"
		if (position == length) {

			if (expression.hasSpaceAfterUpdateClause() &&
			    isComplete(updateClause.getUpdateItems())) {

				addIdentifier(WHERE);
			}

			return;
		}

		if (expression.hasWhereClause()) {
			AbstractConditionalClause whereClause = (AbstractConditionalClause) expression.getWhereClause();

			// Check for within the WHERE clause
			if (position > length) {
				int whereClauseLength = length(whereClause);
				length += whereClauseLength;

				// Right after the WHERE clause, the space is owned by the select statement
				if (position == length + SPACE_LENGTH) {

					virtualSpaces.add(SPACE_LENGTH);
					corrections.add(-whereClauseLength - 2);

					whereClause.accept(this);

					corrections.pop();
					virtualSpaces.pop();
				}
			}
		}
	}

	protected ClauseHelper<WhereClause> whereClauseHelper() {
		WhereClauseHelper helper = getHelper(WhereClauseHelper.class);
		if (helper == null) {
			helper = buildWhereClauseHelper();
			registerHelper(WhereClauseHelper.class, helper);
		}
		return helper;
	}

	protected WhereClauseSelectStatementHelper whereClauseSelectStatementHelper() {
		WhereClauseSelectStatementHelper helper = getHelper(WhereClauseSelectStatementHelper.class);
		if (helper == null) {
			helper = buildWhereClauseSelectStatementHelper();
			registerHelper(WhereClauseSelectStatementHelper.class, helper);
		}
		return helper;
	}

	protected abstract class AbstractFromClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                   implements SelectStatementHelper<T, AbstractFromClause> {

		protected boolean addAppendableToCollection(T expression, int position) {

			if (wordParser.endsWith(position, "GROUP") ||
			    wordParser.endsWith(position, "GROUP B")) {

				if (!expression.hasWhereClause()) {
					proposals.addIdentifier(GROUP_BY);
				}

				return true;
			}
			else if (wordParser.endsWith(position, "ORDER") ||
			         wordParser.endsWith(position, "ORDER B")) {

				if (!expression.hasWhereClause() &&
					 !expression.hasHavingClause()) {

					proposals.addIdentifier(ORDER_BY);
				}

				return true;
			}

			return false;
		}

		/**
		 * Requests this helper to add the JPQL identifiers for the clauses that follows the <b>FROM</b>
		 * clause.
		 *
		 * @param expression The {@link AbstractSelectStatement} being visited
		 */
		protected abstract void addClauseIdentifierProposals(T expression);

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addIdentifier(FROM);
		}

		/**
		 * {@inheritDoc}
		 */
		public final void appendNextClauseProposals(T expression,
		                                            AbstractFromClause clause,
		                                            int position,
		                                            boolean complete) {

			if (complete || isAppendable(clause)) {
				addClauseIdentifierProposals(expression);
			}
			else if (isAppendableToCollection(clause)) {
				boolean skip = addAppendableToCollection(expression, position);

				if (!skip) {
					addClauseIdentifierProposals(expression);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public AbstractFromClause getClause(T expression) {
			return (AbstractFromClause) expression.getFromClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(AbstractFromClause clause) {
			return clause.getDeclaration();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(AbstractSelectStatement expression) {
			return expression.hasFromClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClauseExpression(AbstractFromClause clause) {
			return clause.hasDeclaration();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(T expression) {
			return expression.hasSpaceAfterFrom();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceBeforeClause(T expression) {
			return expression.hasSpaceAfterSelect();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseExpressionComplete(Expression expression) {
			return isComplete(expression);
		}
	}

	protected abstract class AbstractGroupByClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                   implements SelectStatementHelper<T, GroupByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addIdentifier(GROUP_BY);
		}

		/**
		 * {@inheritDoc}
		 */
		public GroupByClause getClause(AbstractSelectStatement expression) {
			return (GroupByClause) expression.getGroupByClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(GroupByClause clause) {
			return clause.getGroupByItems();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(AbstractSelectStatement expression) {
			return expression.hasGroupByClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClauseExpression(GroupByClause clause) {
			return clause.hasGroupByItems();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(AbstractSelectStatement expression) {
			return expression.hasSpaceAfterGroupBy();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceBeforeClause(AbstractSelectStatement expression) {
			return expression.hasSpaceAfterWhere();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseExpressionComplete(Expression expression) {
			return isGroupByComplete(expression);
		}
	}

	protected abstract class AbstractHavingClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                   implements SelectStatementHelper<T, HavingClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addIdentifier(HAVING);
		}

		/**
		 * {@inheritDoc}
		 */
		public HavingClause getClause(AbstractSelectStatement expression) {
			return (HavingClause) expression.getHavingClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(HavingClause clause) {
			return clause.getConditionalExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(AbstractSelectStatement expression) {
			return expression.hasHavingClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClauseExpression(HavingClause clause) {
			return clause.hasConditionalExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceBeforeClause(AbstractSelectStatement expression) {
			return expression.hasSpaceAfterGroupBy();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseExpressionComplete(Expression expression) {
			return isConditionalExpressionComplete(expression);
		}
	}

	protected abstract class AbstractSelectClauseSelectStatementHelper
	                   implements SelectStatementHelper<AbstractSelectStatement, AbstractSelectClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addIdentifier(SELECT);
		}

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(AbstractSelectStatement expression,
		                                      AbstractSelectClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addIdentifier(FROM);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public AbstractSelectClause getClause(AbstractSelectStatement expression) {
			return expression.getSelectClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(AbstractSelectClause clause) {
			return clause.getSelectExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public SelectStatementHelper<AbstractSelectStatement, Expression> getPreviousHelper() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(AbstractSelectStatement expression) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClauseExpression(AbstractSelectClause clause) {
			return clause.hasSelectExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(AbstractSelectStatement expression) {
			return expression.hasSpaceAfterSelect();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceBeforeClause(AbstractSelectStatement expression) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseExpressionComplete(Expression expression) {
			return isSelectExpressionComplete(expression);
		}
	}

	protected abstract class AbstractWhereClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                   implements SelectStatementHelper<T, WhereClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addIdentifier(WHERE);
		}

		/**
		 * {@inheritDoc}
		 */
		public WhereClause getClause(AbstractSelectStatement expression) {
			return (WhereClause) expression.getWhereClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(WhereClause clause) {
			return clause.getConditionalExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(AbstractSelectStatement expression) {
			return expression.hasWhereClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClauseExpression(WhereClause clause) {
			return clause.hasConditionalExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(AbstractSelectStatement expression) {
			return expression.hasSpaceAfterWhere();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceBeforeClause(AbstractSelectStatement expression) {
			return expression.hasSpaceAfterFrom();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseExpressionComplete(Expression expression) {
			return isConditionalExpressionComplete(expression);
		}
	}

	/**
	 * This visitor retrieves the permitted type from the path expression's parent. For instance,
	 * <b>SUM<b></b> or <b>AVG</b> only accepts state fields that have a numeric type.
	 */
	protected class AcceptableTypeVisitor extends AbstractExpressionVisitor {

		/**
		 * The type that is retrieved based on the expression, it determines what is acceptable.
		 */
		IType type;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbsExpression expression) {
			type = getType(Number.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ArithmeticFactor expression) {
			type = getType(Number.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression) {
			type = getType(Number.class);
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
		public void visit(ConcatExpression expression) {
			type = getType(CharSequence.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LengthExpression expression) {
			type = getType(CharSequence.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LocateExpression expression) {
			// TODO: Handle the position
			type = getType(CharSequence.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LowerExpression expression) {
			type = getType(CharSequence.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ModExpression expression) {
			// In theory we would only allow Long and Integer
			type = getType(Number.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SqrtExpression expression) {
			type = getType(Number.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			expression.getParent().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstringExpression expression) {
			// TODO: Handle the position
			type = getType(CharSequence.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression) {
			type = getType(Number.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TrimExpression expression) {
			type = getType(CharSequence.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpperExpression expression) {
			type = getType(CharSequence.class);
		}
	}

	/**
	 * This visitor scans the visited {@link Expression} and determines if a JPQL identifier can be
	 * added when the position is at the end of a clause and the ending of the clause can be seen as
	 * the beginning of an identifier.
	 * <p>
	 * For instance, in "<code>SELECT e, AVG(e.age) F</code>", F is parsed as a result variable but
	 * can also be seen as the first letter for <b>FROM</b>.
	 */
	protected class AppendableExpressionVisitor extends AbstractTraverseChildrenVisitor {

		/**
		 *
		 */
		protected boolean appendable;

		/**
		 *
		 */
		protected boolean clauseOfItems;

		/**
		 *
		 */
		protected boolean hasComma;

		/**
		 *
		 */
		protected int positionInCollection;

		/**
		 * Creates a new <code>AppendableExpressionVisitor</code>.
		 */
		AppendableExpressionVisitor() {
			super();
			this.positionInCollection = -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression) {
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression) {
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {

			positionInCollection = expression.childrenSize() - 1;
			hasComma = expression.hasComma(positionInCollection - 1);

			try {
				expression.accept(positionInCollection, this);
			}
			finally {
				hasComma = false;
				positionInCollection = -1;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DivisionExpression expression) {
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(GroupByClause expression) {
			clauseOfItems = true;
			super.visit(expression);
			clauseOfItems = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			if (clauseOfItems) {
				appendable = (positionInCollection > -1) && !hasComma;
			}
			else {
				appendable = (positionInCollection > -1);
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
			// 2) The next clauses identifiers cannot be added if there is a comma before the last
			//    item. Example: "SELECT e FROM Employee e, I" where 'I' cannot be the beginning of
			//    a clause identifier
			if ((positionInCollection == -1) || hasComma) {
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
		public void visit(MultiplicationExpression expression) {
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression) {
			clauseOfItems = true;
			super.visit(expression);
			clauseOfItems = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItem expression) {
			appendable = expression.hasSpaceAfterExpression() &&
			             expression.isDefault() ||
			             expression.hasSpaceAfterOrdering() &&
			             (!expression.isNullsFirst() && !expression.isNullsLast());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression) {
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			// Only the abstract schema name is parsed
			appendable = !expression.hasSpaceAfterRootObject() &&
			             !expression.hasAs() &&
			             !expression.hasIdentificationVariable();
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
		public void visit(SubtractionExpression expression) {
			super.visit(expression);
		}
	}

	/**
	 * This helper is responsible to add the proposals
	 */
	protected interface ClauseHelper<T extends Expression> {

		/**
		 * Adds the proposals because the cursor is at the end of the {@link Expression}.
		 *
		 * @param expression The clause for which proposals can be added after the expression
		 */
		void addAtTheEndOfExpression(T expression);

		/**
		 * Adds the possible proposals.
		 *
		 * @param expression The clause for which proposals can be added
		 */
		void addProposals(T expression);

		/**
		 * Returns the expression from the given clause.
		 *
		 * @param expression The clause for which its expression is needed
		 * @return The clause's expression
		 */
		Expression getClauseExpression(T expression);
	}

	/**
	 *
	 */
	protected interface CollectionExpressionHelper<T extends Expression> {

		/**
		 * Adds the proposals because the cursor is at the end of the given child {@link Expression}.
		 *
		 * @param expression The {@link Expression} being visited
		 * @param child The child of the parent {@link Expression} for which proposals can be added
		 * at the end
		 * @param index The position of that child in the collection of children
		 */
		void addAtTheEndOfChild(T expression, Expression child, int index);

		/**
		 * Adds
		 *
		 * @param expression
		 * @param index
		 */
		void addProposals(T expression, int index);

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
		 * Determines whether
		 *
		 * @param expression
		 * @return
		 */
		boolean hasDelimiterAfterIdentifier(T expression);

		/**
		 * Returns the maximum number of encapsulated {@link Expression expressions} the {@link
		 * Expression} allows. Some expression only allow 2, others 3 and others allow an unlimited
		 * number.
		 *
		 * @param expression The {@link Expression} for which its maximum number of children
		 * @return The maximum number of children the expression can have
		 */
		int maxCollectionSize(T expression);

		/**
		 * Returns the length to add to
		 */
		int preExpressionLength(T expression);

		/**
		 * Returns the
		 *
		 * @param expression
		 * @return
		 */
		JPQLQueryBNF queryBNF(T expression, int index);
	}

	/**
	 * This visitor retrieves the {@link CollectionExpression} if it is visited.
	 */
	protected static class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} if it is the {@link Expression} that was visited.
		 */
		protected CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		protected CollectionExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			this.expression = expression;
		}
	}

	protected class CollectionMappingFilter implements Filter<IMapping> {

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(IMapping value) {
			// Both association and collection field are accepted
			// Example: e.address is incomplete but it is not the entire path
			// Example: e.projects is the complete path
			return value.isRelationship();
		}
	}

	protected abstract class CompletenessVisitor extends AbstractExpressionVisitor {

		/**
		 * Determines whether an {@link Expression} that was visited is complete or if some part is missing.
		 */
		protected boolean complete;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			int lastIndex = expression.childrenSize() - 1;
			AbstractExpression child = (AbstractExpression) expression.getChild(lastIndex);
			child.accept(this);
		}
	}

	/**
	 * This helper is responsible to traverse the parsed tree and to determine if JPQL identifiers
	 * with a compound role can be appended after an {@link Expression}, which is based on the
	 * location of the cursor.
	 */
	protected class CompoundExpressionHelper extends AnonymousExpressionVisitor {

		protected Expression leftExpression;
		protected LogicalExpression logicalExpression;
		protected Expression rightExpression;

		void dispose() {
			leftExpression    = null;
			rightExpression   = null;
			logicalExpression = null;
		}

		boolean hasIdentifier() {
			if (logicalExpression != null) {
				return true;
			}
			return false;
		}

		boolean hasNext() {
			return rightExpression != null;
		}

		int identifierLength() {
			if (logicalExpression != null) {
				int length = logicalExpression.getIdentifier().length();
				length += logicalExpression.hasLeftExpression()       ? 1 : 0;
				length += logicalExpression.hasSpaceAfterIdentifier() ? 1 : 0;
				return length;
			}
			return 0;
		}

		boolean isCompoundable() {
			return isComplete(leftExpression);
		}

		int length() {
			if (leftExpression != null) {
				return AbstractContentAssistVisitor.this.length(leftExpression);
			}
			return 0;
		}

		void next() {
			rightExpression.accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression) {
			visitLogicalExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Expression expression) {
			this.leftExpression = expression;
			this.rightExpression = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression) {
			visitLogicalExpression(expression);
		}

		protected void visitLogicalExpression(LogicalExpression expression) {

			this.logicalExpression = expression;

			if (expression.hasLeftExpression()) {
				this.leftExpression = expression.getLeftExpression();
			}
			else {
				this.leftExpression = null;
			}

			if (expression.hasRightExpression()) {
				this.rightExpression = expression.getRightExpression();
			}
			else {
				this.rightExpression = null;
			}
		}
	}

	/**
	 * This visitor checks to see if the conditional expression is complete or not. To be complete,
	 * the expression's ending has to be complete.
	 * <p>
	 * For instance:<br>
	 * "<code>WHERE e.name</code>" is not complete because <code>e.name</code> is not one of the
	 * possible expressions allowed in a conditional expression.<br>
	 * "<code>HAVING e.age BETWEEN 5 AND 18</code>" is complete.<br>
	 * "<code>HAVING e.age BETWEEN 5 AND</code>" is not complete.
	 */
	protected class ConditionalExpressionCompletenessVisitor extends CompletenessVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression) {
			complete = expression.hasRightExpression();
			if (complete) {
				complete = isComplete(expression.getRightExpression());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BetweenExpression expression) {
			complete = expression.hasUpperBoundExpression();
			if (complete) {
				complete = isComplete(expression.getUpperBoundExpression());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberExpression expression) {
			complete = expression.hasCollectionValuedPathExpression();
			if (complete) {
				complete = isComplete(expression.getCollectionValuedPathExpression());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression) {
			complete = expression.hasRightExpression();
			if (complete) {
				complete = isComplete(expression.getRightExpression());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EmptyCollectionComparisonExpression expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ExistsExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InExpression expression) {
			complete = expression.hasInItems();
			if (complete) {
				complete = isComplete(expression.getInItems());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LikeExpression expression) {
			complete = isComplete(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullComparisonExpression expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression) {
			complete = expression.hasRightExpression();
			if (complete) {
				complete = isComplete(expression.getRightExpression());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			// It is the first conditional expression, make sure it is a valid conditional expression
			if (!complete) {
				complete = expression.hasRightParenthesis();
				if (complete) {
					expression.getExpression().accept(this);
				}
			}
			// It is not the first conditional expression, simply make sure it has ')'
			else {
				complete = expression.hasRightParenthesis();
			}
		}
	}

	protected class ConstrutorCollectionHelper implements CollectionExpressionHelper<ConstructorExpression> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(ConstructorExpression expression, Expression child, int index) {
			addAllAggregates(ConstructorItemBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(ConstructorExpression expression, int index) {
			addIdentificationVariables(IdentificationVariableType.ALL, expression);
			addAllFunctions(ConstructorItemBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(ConstructorExpression expression) {
			CollectionExpression collectionExpression = getCollectionExpression(expression.getConstructorItems());
			if (collectionExpression == null) {
				collectionExpression = expression.buildCollectionExpression();
			}
			return collectionExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(ConstructorExpression expression) {
			return expression.hasLeftParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(ConstructorExpression expression) {
			return Integer.MAX_VALUE;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(ConstructorExpression expression) {
			if (expression.hasSpaceAfterNew()) {
				return expression.getClassName().length() + SPACE_LENGTH;
			}
			return expression.getClassName().length();
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(ConstructorExpression expression, int index) {
			return AbstractContentAssistVisitor.this.getQueryBNF(ConstructorItemBNF.ID);
		}
	}

	/**
	 * The default implementation of {@link MappingCollector}, which simply returns an empty
	 * collection.
	 */
	protected class DefaultMappingCollector implements MappingCollector {

		/**
		 * Creates a new <code>DefaultMappingCollector</code>.
		 */
		protected DefaultMappingCollector() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public Collection<IMapping> buildProposals() {
			return Collections.emptyList();
		}
	}

	protected class DeleteClauseHelper implements ClauseHelper<DeleteClause> {

		/**
		 * Creates a new <code>DeleteClauseHelper</code>.
		 */
		protected DeleteClauseHelper() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(DeleteClause expression) {
			addIdentifier(WHERE);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(DeleteClause expression) {
			addEntities();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(DeleteClause expression) {
			return expression.getRangeVariableDeclaration();
		}
	}

	protected class DoubleEncapsulatedCollectionHelper implements CollectionExpressionHelper<AbstractDoubleEncapsulatedExpression> {

		/**
		 * Creates a new <code>DoubleEncapsulatedCollectionHelper</code>.
		 */
		protected DoubleEncapsulatedCollectionHelper() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(AbstractDoubleEncapsulatedExpression expression,
		                               Expression child,
		                               int index) {

			if (queryBNF(expression, index).handleAggregate()) {
				addAllAggregates(queryBNF(expression, index));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(AbstractDoubleEncapsulatedExpression expression, int index) {
			addAllIdentificationVariables();
			addAllFunctions(queryBNF(expression, index));
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(AbstractDoubleEncapsulatedExpression expression) {
			return expression.buildCollectionExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(AbstractDoubleEncapsulatedExpression expression) {
			return expression.hasLeftParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(AbstractDoubleEncapsulatedExpression expression) {
			// Both MOD and NULLIF allows a fixed 2 encapsulated expressions
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(AbstractDoubleEncapsulatedExpression expression) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(AbstractDoubleEncapsulatedExpression expression, int index) {
			return getQueryBNF(expression.parameterExpressionBNF(index));
		}
	}

	/**
	 * This {@link MappingCollector} returns the possible mappings (non-collection type or
	 * collection type) from a managed type.
	 */
	protected class FilteringMappingCollector implements MappingCollector {

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
				public boolean accept(IMapping mapping) {
					return mapping.getName().startsWith(suffix);
				}
			};
		}

		/**
		 * {@inheritDoc}
		 */
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

	protected class FromClauseCollectionHelper implements CollectionExpressionHelper<AbstractFromClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(AbstractFromClause expression, Expression child, int index) {
			addJoinIdentifiers();
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(AbstractFromClause expression, int index) {
			addEntities();
			if (index > 0) {
				addIdentifier(IN);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(AbstractFromClause expression) {
			CollectionExpression collectionExpression = getCollectionExpression(expression.getDeclaration());
			if (collectionExpression == null) {
				collectionExpression = expression.buildCollectionExpression();
			}
			return collectionExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(AbstractFromClause expression) {
			return expression.hasSpaceAfterFrom();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(AbstractFromClause expression) {
			return Integer.MAX_VALUE;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(AbstractFromClause expression) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(AbstractFromClause expression, int index) {
			return getQueryBNF(expression.declarationBNF());
		}
	}

	protected class FromClauseHelper implements ClauseHelper<AbstractFromClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(AbstractFromClause expression) {
			// TODO?
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(AbstractFromClause expression) {

			addEntities();

			// With the correction, check to see if the possible identifiers (IN)
			// can be added and only if it's not the first item in the list
			if (positionInCollections.peek() > 0) {
				addAllIdentifiers(InternalFromClauseBNF.ID);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(AbstractFromClause expression) {
			return expression.getDeclaration();
		}
	}

	protected class FromClauseSelectStatementHelper extends AbstractFromClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean addAppendableToCollection(SelectStatement expression, int position) {

			boolean skip = super.addAppendableToCollection(expression, position);

			if (!skip && (wordParser.endsWith(position, "ORDER") ||
			              wordParser.endsWith(position, "ORDER B"))) {

				if (!expression.hasWhereClause() &&
					 !expression.hasHavingClause()) {

					proposals.addIdentifier(ORDER_BY);
				}

				return true;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void addClauseIdentifierProposals(SelectStatement expression) {

			addIdentifier(WHERE);

			if (!expression.hasWhereClause()) {
				addIdentifier(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addIdentifier(HAVING);

					if (!expression.hasHavingClause()) {
						addIdentifier(ORDER_BY);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public WhereClauseSelectStatementHelper getNextHelper() {
			return whereClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public SelectClauseSelectStatementHelper getPreviousHelper() {
			return getSelectClauseSelectStatementHelper();
		}
	}

	protected class GroupByClauseCollectionHelper implements CollectionExpressionHelper<GroupByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(GroupByClause expression, Expression child, int index) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(GroupByClause expression, int index) {
			addAllFunctions(GroupByItemBNF.ID);
			addAllIdentificationVariables();
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(GroupByClause expression) {
			CollectionExpression collectionExpression = getCollectionExpression(expression.getGroupByItems());
			if (collectionExpression == null) {
				collectionExpression = expression.buildCollectionExpression();
			}
			return collectionExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(GroupByClause expression) {
			return expression.hasSpaceAfterGroupBy();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(GroupByClause expression) {
			return Integer.MAX_VALUE;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(GroupByClause expression) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(GroupByClause expression, int index) {
			return AbstractContentAssistVisitor.this.getQueryBNF(GroupByItemBNF.ID);
		}
	}

	protected class GroupByClauseSelectStatementHelper extends AbstractGroupByClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      GroupByClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addIdentifier(HAVING);

				if (!expression.hasHavingClause()) {
					addIdentifier(ORDER_BY);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public HavingClauseSelectStatementHelper getNextHelper() {
			return getHavingClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public WhereClauseSelectStatementHelper getPreviousHelper() {
			return whereClauseSelectStatementHelper();
		}
	}

	protected class HavingClauseHelper implements ClauseHelper<HavingClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(HavingClause expression) {
			addAllAggregates(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(HavingClause expression) {
			addAllIdentificationVariables();
			addAllFunctions(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(HavingClause expression) {
			return expression.getConditionalExpression();
		}
	}

	protected class HavingClauseSelectStatementHelper extends AbstractHavingClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      HavingClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addIdentifier(ORDER_BY);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public OrderByClauseSelectStatementHelper getNextHelper() {
			return getOrderByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public GroupByClauseSelectStatementHelper getPreviousHelper() {
			return getGroupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(SelectStatement expression) {
			return expression.hasSpaceBeforeOrderBy();
		}
	}

	/**
	 * The various ways of retrieving identification variables from the declaration expression.
	 */
	protected enum IdentificationVariableType {

		/**
		 * Retrieves all the identification variables declared in the declaration portion of the
		 * expression, which is either in the <b>FROM</b> clause of a <b>SELECT</b> query or
		 * <b>DELETE</b> query or in the <b>UPDATE</b> query.
		 */
		ALL,

		/**
		 * Only retrieves the identification variables that map a path expression defined in a
		 * <b>JOIN</b> expression or in a <b>IN</b> expression.
		 */
		COLLECTION,

		/**
		 * Only retrieves the identification variables that have been declared to the left of the
		 * expression requested them, both range and collection type variables are collected.
		 */
		LEFT,

		/**
		 * Only retrieves the identification variables that map a path expression defined in a
		 * <b>JOIN</b> expression or in a <b>IN</b> expression but that have been declared to the
		 * left of the expression requested them.
		 */
		LEFT_COLLECTION,

		/**
		 * Simply indicate the identification variables should not be collected.
		 */
		NONE,

		/**
		 * Retrieves the result variables that have been defined in the <b>SELECT</b> clause.
		 */
		RESULT_VARIABLE
	}

	protected class IncompleteCollectionExpressionVisitor extends CompletenessVisitor {

		/**
		 * This flag is used to make sure only the last expression in a collection is tested. A single
		 * expression cannot be used to check the "completeness".
		 */
		protected boolean insideCollection;

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
			expression.getDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(GroupByClause expression) {
			expression.getGroupByItems().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(HavingClause expression) {
			expression.getConditionalExpression().accept(this);
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
		public void visit(RangeVariableDeclaration expression) {

			if (insideCollection) {

				// The abstract schema name could be "GROUP" or "ORDER" and as the last declaration,
				// GROUP BY and ORDER BY becomes valid proposals
				complete = !expression.hasAs() && !expression.hasIdentificationVariable();

				// Special cases
				if (!complete) {
					complete = expression.toParsedText().equalsIgnoreCase("GROUP B") ||
					           expression.toParsedText().equalsIgnoreCase("ORDER B");
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {
			expression.getConditionalExpression().accept(this);
		}
	}

	protected class JoinCollectionHelper implements CollectionExpressionHelper<IdentificationVariableDeclaration> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(IdentificationVariableDeclaration expression,
		                               Expression child,
		                               int index) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(IdentificationVariableDeclaration expression, int index) {
			addJoinIdentifiers();
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(IdentificationVariableDeclaration expression) {
			CollectionExpression collectionExpression = getCollectionExpression(expression.getJoins());
			if (collectionExpression == null) {
				collectionExpression = expression.buildCollectionExpression();
			}
			return collectionExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(IdentificationVariableDeclaration expression) {
			return expression.hasSpace();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(IdentificationVariableDeclaration expression) {
			return Integer.MAX_VALUE;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(IdentificationVariableDeclaration expression) {
			return length(expression.getRangeVariableDeclaration());
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(IdentificationVariableDeclaration expression, int index) {
			return AbstractContentAssistVisitor.this.getQueryBNF(InternalJoinBNF.ID);
		}
	}

	/**
	 * A collector is responsible to retrieve the possible proposals by using the mappings that can
	 * complete a path expression.
	 */
	protected interface MappingCollector {

		/**
		 * Retrieves the possible proposals that can be used to complete a path expression based on
		 * the position of the cursor.
		 *
		 * @return The possible proposals
		 */
		Collection<IMapping> buildProposals();
	}

	/**
	 * This visitor is responsible to create the right {@link Filter} based on the type of the {@link
	 * Expression}.
	 */
	protected class MappingFilterBuilder extends AbstractTraverseParentVisitor {

		/**
		 * The {@link Filter} that will filter the various type of {@link IMapping IMappings} based
		 * on the location of the of the path expression within the JPQL query.
		 */
		protected Filter<IMapping> filter;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			filter = getMappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			filter = getMappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			filter = getMappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLExpression expression) {
			filter = getMappingPropertyFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression) {
			filter = getMappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TreatExpression expression) {
			filter = getMappingCollectionFilter();
		}
	}

	/**
	 * This {@link Filter} is responsible to filter out the mappings that can't have their type
	 * assignable to the one passed in.
	 */
	protected class MappingTypeFilter implements Filter<IMapping> {

		/**
		 * The type used to determine if the mapping's type is a valid type.
		 */
		protected final IType type;

		/**
		 * Creates a new <code>MappingTypeFilter</code>.
		 *
		 * @param type The type used to determine if the mapping's type is a valid type
		 */
		MappingTypeFilter(IType type) {
			super();
			this.type = type;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(IMapping value) {

			// A reference mapping always can be used for further traversal
			if (value.isRelationship() &&
			   !value.isCollection()) {

				return true;
			}

			// Determine if it's assignable to the desired type
			IType mappingType = value.getType();
			mappingType = getTypeHelper().convertPrimitive(mappingType);
			return mappingType.isAssignableTo(type);
		}
	}

	/**
	 * This visitor checks to see if the visited expression is {@link NullExpression}.
	 */
	protected static class NullExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link NullExpression} if it is the {@link Expression} that was visited.
		 */
		protected NullExpression expression;

		/**
		 * Creates a new <code>NullExpressionVisitor</code>.
		 */
		protected NullExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			this.expression = expression;
		}
	}

	protected class OrderByClauseCollectionHelper implements CollectionExpressionHelper<OrderByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(OrderByClause expression, Expression child, int index) {

			OrderByItem item = (OrderByItem) child;

			if (item.getOrdering() == Ordering.DEFAULT) {
				addIdentifier(ASC);
				addIdentifier(DESC);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(OrderByClause expression, int index) {
			addAllIdentificationVariables();
			addAllResultVariables();
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(OrderByClause expression) {
			CollectionExpression collectionExpression = getCollectionExpression(expression.getOrderByItems());
			if (collectionExpression == null) {
				collectionExpression = expression.buildCollectionExpression();
			}
			return collectionExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(OrderByClause expression) {
			return expression.hasSpaceAfterOrderBy();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(OrderByClause expression) {
			return Integer.MAX_VALUE;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(OrderByClause expression) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(OrderByClause expression, int index) {
			return AbstractContentAssistVisitor.this.getQueryBNF(OrderByItemBNF.ID);
		}
	}

	protected class OrderByClauseSelectStatementHelper implements SelectStatementHelper<SelectStatement, OrderByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addIdentifier(ORDER_BY);
		}

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      OrderByClause clause,
		                                      int position,
		                                      boolean complete) {

			// Nothing to add
		}

		/**
		 * {@inheritDoc}
		 */
		public OrderByClause getClause(SelectStatement expression) {
			return (OrderByClause) expression.getOrderByClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(OrderByClause clause) {
			return clause.getOrderByItems();
		}

		/**
		 * {@inheritDoc}
		 */
		public SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> getNextHelper() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public HavingClauseSelectStatementHelper getPreviousHelper() {
			return getHavingClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(SelectStatement expression) {
			return expression.hasOrderByClause();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClauseExpression(OrderByClause clause) {
			return clause.hasOrderByItems();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(SelectStatement expression) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceBeforeClause(SelectStatement expression) {
			return expression.hasSpaceBeforeOrderBy();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseExpressionComplete(Expression expression) {
			return false;
		}
	}

	protected class PropertyMappingFilter implements Filter<IMapping> {

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(IMapping value) {
			return !value.isTransient() &&
			       !value.isCollection();
		}
	}

	protected class RangeVariableDeclarationVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link RangeVariableDeclaration} if it was visited otherwise <code>null</code>.
		 */
		RangeVariableDeclaration expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			this.expression = expression;
		}
	}

	protected class ResultVariableVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link ResultVariable} if it was visited otherwise <code>null</code>.
		 */
		protected ResultVariable expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor checks the integrity of the select items.
	 * <p>
	 * The possible JPQL identifiers allowed in a SELECT expression as of JPA 2.0
	 * are:
	 * <ul>
	 * <li> *, /, +, -
	 * <li> ABS
	 * <li> AVG
	 * <li> CASE
	 * <li> COALESCE
	 * <li> CONCAT
	 * <li> COUNT
	 * <li> CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP, SQL date
	 * <li> ENTRY
	 * <li> FUNC
	 * <li> INDEX
	 * <li> KEY
	 * <li> LENGTH
	 * <li> LOCATE
	 * <li> LOWER
	 * <li> MAX
	 * <li> MIN
	 * <li> MOD
	 * <li> NEW
	 * <li> NULL
	 * <li> NULLIF
	 * <li> OBJECT
	 * <li> SIZE
	 * <li> SQRT
	 * <li> SUBSTRING
	 * <li> SUM
	 * <li> TRIM
	 * <li> TRUE, FALSE
	 * <li> TYPE
	 * <li> UPPER
	 * <li> VALUE
	 * </ul>
	 */
	protected class SelectClauseCompletenessVisitor extends CompletenessVisitor {

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
		public void visit(AdditionExpression expression) {
			visitArithmeticExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression) {
			visitAggregateFunction(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CaseExpression expression) {
			complete = expression.hasEnd();
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

			// "SELECT e," is complete
			if (expression.endsWithComma()) {
				complete = true;
			}

			int lastIndex = expression.childrenSize() - 1;
			AbstractExpression child = (AbstractExpression) expression.getChild(lastIndex);

			// The collection ends with an empty element, that's not complete
			if (isNull(child)) {
				complete = false;
			}
			else {
				int length = expression.toActualText(positionInCollections.peek()).length();

				// The position is at the beginning of the child expression, that means
				// it's complete because we don't have to verify the child expression
				if (corrections.peek() == length) {
					int index = Math.max(0, positionInCollections.peek() - 1);
					complete = expression.hasComma(index);
				}
				// Dig into the child expression to check its status
				else {
					child.accept(this);
				}
			}
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
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression) {
			visitAggregateFunction(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DateTime expression) {
			// Always complete if CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP
			// or if the JDBC escape syntax ends with '}'
			complete = expression.isJDBCDate() ? expression.toActualText().endsWith("}") : true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DivisionExpression expression) {
			visitArithmeticExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression) {
			visitEncapsulatedIdentificationVariableExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			// Always complete
			complete = true;
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
		public void visit(KeyExpression expression) {
			visitEncapsulatedIdentificationVariableExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeywordExpression expression) {
			// Always complete
			complete = true;
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
			visitAggregateFunction(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MinFunction expression) {
			visitAggregateFunction(expression);
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
			visitArithmeticExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			complete = true;
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
		public void visit(ObjectExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			complete = expression.hasResultVariable();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression) {

			int position = getPosition(expression);

			if (position == SELECT.length() + SPACE_LENGTH) {
				complete = true;
			}
			else {
				expression.getSelectExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression) {
			expression.getSelectExpression().accept(this);
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
			// Always complete, even if it ends with a dot
			complete = true;
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
			visitArithmeticExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression) {
			visitAggregateFunction(expression);
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
		public void visit(UpperExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression) {
			visitEncapsulatedIdentificationVariableExpression(expression);
		}

		protected void visitAbstractDoubleEncapsulatedExpression(AbstractDoubleEncapsulatedExpression expression) {
			visitAbstractEncapsulatedExpression(expression);
		}

		protected void visitAbstractEncapsulatedExpression(AbstractEncapsulatedExpression expression) {
			// If ')' is present, then anything can be added after
			complete = expression.hasRightParenthesis();
		}

		protected void visitAbstractSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression) {
			visitAbstractEncapsulatedExpression(expression);
		}

		protected void visitAbstractTripleEncapsulatedExpression(AbstractTripleEncapsulatedExpression expression) {
			visitAbstractEncapsulatedExpression(expression);
		}

		protected void visitAggregateFunction(AggregateFunction expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		protected void visitArithmeticExpression(ArithmeticExpression expression) {
			expression.getRightExpression().accept(this);
		}

		protected void visitEncapsulatedIdentificationVariableExpression(EncapsulatedIdentificationVariableExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}
	}

	protected class SelectClauseSelectStatementHelper extends AbstractSelectClauseSelectStatementHelper {

		/**
		 * {@inheritDoc}
		 */
		public FromClauseSelectStatementHelper getNextHelper() {
			return getFromClauseSelectStatementHelper();
		}
	}

	protected interface SelectStatementHelper<T extends AbstractSelectStatement, C extends Expression> {

		/**
		 * Adds the JPQL identifier of the clause being scanned by this helper.
		 */
		void addClauseProposal();

		/**
		 * The position of the cursor is at the end of the given clause, requests to add the clauses'
		 * identifiers that can be added as proposals.
		 *
		 * @param expression The {@link AbstractSelectStatement} being visited
		 * @param clause The clause being scanned
		 * @param position The position of the cursor within the {@link AbstractSelectStatement}
		 * @param complete Determines whether the clause's expression is complete or not
		 */
		void appendNextClauseProposals(T expression, C clause, int position, boolean complete);

		/**
		 * Returns the clause being scanned by this helper. It is safe to type cast the clause because
		 * {@link #hasClause(AbstractSelectStatement)} is called before this one.
		 *
		 * @param expression The {@link AbstractSelectStatement} being visited
		 * @return The clause being scanned
		 */
		C getClause(T expression);

		/**
		 * Returns the clause's expression.
		 *
		 * @param clause The {@link AbstractSelectStatement} being visited
		 * @return The clause's expression
		 */
		Expression getClauseExpression(C clause);

		/**
		 * Returns the {@link SelectStatementHelper} that will scan the following clause, which is
		 * based on the grammar and not on the actual existence of the clause in the parsed tree.
		 *
		 * @return The {@link SelectStatementHelper} for the next clause
		 */
		SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> getNextHelper();

		/**
		 * Returns the {@link SelectStatementHelper} that will scan the previous clause, which is
		 * based on the grammar and not on the actual existence of the clause in the parsed tree.
		 *
		 * @return The {@link SelectStatementHelper} for the previous clause
		 */
		SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> getPreviousHelper();

		/**
		 * Determines whether the clause exists in the parsed tree.
		 *
		 * @param expression The {@link AbstractSelectStatement} being visited
		 * @return <code>true</code> if the clause has been parsed; <code>false</code> otherwise
		 */
		boolean hasClause(T expression);

		/**
		 * Determines whether the clause's expression exists in the parsed tree.
		 *
		 * @param clause The clause being scanned
		 * @return <code>true</code> if the clause has its expression or a portion of it parsed;
		 * <code>false</code> if nothing was parsed
		 */
		boolean hasClauseExpression(C clause);

		/**
		 * Determines whether there is a space (owned by the <b>SELECT</b> statement) after the clause
		 * being scanned by this helper.
		 *
		 * @param expression The {@link AbstractSelectStatement} being visited
		 * @return <code>true</code> if a space follows the clause; <code>false</code> otherwise
		 */
		boolean hasSpaceAfterClause(T expression);

		/**
		 * Determines whether there is a space (owned by the <b>SELECT</b> statement) before the
		 * clause being scanned by this helper.
		 *
		 * @param expression The {@link AbstractSelectStatement} being visited
		 * @return <code>true</code> if a space precedes the clause; <code>false</code> otherwise
		 */
		boolean hasSpaceBeforeClause(T expression);

		/**
		 * Determines whether the clause's expression is complete or incomplete.
		 *
		 * @param expression The clause's expression to verify its completeness
		 * @return <code>true</code> if the {@link Expression} is complete based on its content versus
		 * what the grammar expects; <code>false</code> otherwise
		 */
		boolean isClauseExpressionComplete(Expression expression);
	}

	protected class SimpleFromClauseSelectStatementHelper extends AbstractFromClauseSelectStatementHelper<SimpleSelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean addAppendableToCollection(SimpleSelectStatement expression, int position) {

			if (wordParser.endsWith(position, "GROUP") ||
			    wordParser.endsWith(position, "GROUP B")) {

				if (!expression.hasWhereClause()) {
					proposals.addIdentifier(GROUP_BY);
				}

				return true;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void addClauseIdentifierProposals(SimpleSelectStatement expression) {

			addIdentifier(WHERE);

			if (!expression.hasWhereClause()) {
				addIdentifier(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addIdentifier(HAVING);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleWhereClauseSelectStatementHelper getNextHelper() {
			return getSimpleWhereClauseSelectStatementHelper();
		}


		/**
		 * {@inheritDoc}
		 */
		public SimpleSelectClauseSelectStatementHelper getPreviousHelper() {
			return getSimpleSelectClauseSelectStatementHelper();
		}
	}

	protected class SimpleGroupByClauseSelectStatementHelper extends AbstractGroupByClauseSelectStatementHelper<SimpleSelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SimpleSelectStatement expression,
		                                      GroupByClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addIdentifier(HAVING);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleHavingClauseSelectStatementHelper getNextHelper() {
			return getSimpleHavingClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleWhereClauseSelectStatementHelper getPreviousHelper() {
			return getSimpleWhereClauseSelectStatementHelper();
		}
	}

	protected class SimpleHavingClauseSelectStatementHelper extends AbstractHavingClauseSelectStatementHelper<SimpleSelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SimpleSelectStatement expression,
		                                      HavingClause clause,
		                                      int position,
		                                      boolean complete) {
		}

		/**
		 * {@inheritDoc}
		 */
		public SelectStatementHelper<AbstractSelectStatement, Expression> getNextHelper() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleGroupByClauseSelectStatementHelper getPreviousHelper() {
			return getSimpleGroupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(SimpleSelectStatement expression) {
			return false;
		}
	}

	protected class SimpleSelectClauseSelectStatementHelper extends AbstractSelectClauseSelectStatementHelper {

		/**
		 * {@inheritDoc}
		 */
		public SimpleFromClauseSelectStatementHelper getNextHelper() {
			return getSimpleFromClauseSelectStatementHelper();
		}
	}

	protected class SimpleWhereClauseSelectStatementHelper extends AbstractWhereClauseSelectStatementHelper<SimpleSelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SimpleSelectStatement expression,
		                                      WhereClause clause,
		                                      int position,
		                                      boolean complete) {


			if (complete || isAppendable(clause)) {
				addIdentifier(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addIdentifier(HAVING);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleGroupByClauseSelectStatementHelper getNextHelper() {
			return getSimpleGroupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleFromClauseSelectStatementHelper getPreviousHelper() {
			return getSimpleFromClauseSelectStatementHelper();
		}
	}

	/**
	 * This visitor determines if an {@link Expression} is in a subquery.
	 */
	protected class SubqueryVisitor extends AbstractTraverseParentVisitor {

		/**
		 * The subquery {@link Expression} if it's the first clause visitor. Otherwise it will be
		 * <code>null</code> if the {@link Expression} is in the top-level query.
		 */
		SimpleSelectStatement expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			this.expression = expression;
		}
	}

	protected class TrailingCompletenessVisitor extends CompletenessVisitor {

 		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbsExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AllOrAnyExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ArithmeticFactor expression) {

			complete = expression.hasExpression();

			if (complete) {
				expression.getExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BetweenExpression expression) {

			complete = expression.hasAnd() && expression.hasUpperBoundExpression();

			if (complete) {
				expression.getUpperBoundExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CaseExpression expression) {
			complete = expression.hasEnd();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CoalesceExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			complete = expression.hasIdentificationVariable();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberExpression expression) {

			complete = expression.hasCollectionValuedPathExpression();

			if (complete) {
				expression.getCollectionValuedPathExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			complete = !expression.endsWithDot();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConcatExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConstructorExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DateTime expression) {
			if (expression.isJDBCDate()) {
				complete = expression.toActualText().endsWith("}");
			}
			else {
				complete = true;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DivisionExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EmptyCollectionComparisonExpression expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntityTypeLiteral expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ExistsExpression expression) {
			complete = expression.hasRightParenthesis();
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
			if (expression.hasJoins()) {
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
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			if (expression.hasFetch()) {
				if (expression.hasAs()) {
					complete = expression.hasIdentificationVariable();
				}
				else {
					complete = expression.hasJoinAssociationPath();
				}
			}
			else {
				complete = expression.hasIdentificationVariable();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeyExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeywordExpression expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LengthExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LikeExpression expression) {
			if (expression.hasEscape()) {
				complete = expression.hasEscapeCharacter();
			}
			else {
				complete = expression.hasPatternValue();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LocateExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LowerExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MaxFunction expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MinFunction expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ModExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MultiplicationExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NotExpression expression) {
			complete = expression.hasExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullComparisonExpression expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullIfExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NumericLiteral expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ObjectExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItem expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			complete = expression.hasIdentificationVariable();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			complete = expression.hasResultVariable();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SqrtExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			complete = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression) {
			complete = expression.hasCloseQuote();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstringExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubtractionExpression expression) {

			complete = expression.hasRightExpression();

			if (complete) {
				expression.getRightExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TreatExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TrimExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TypeExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateItem expression) {

			complete = expression.hasNewValue();

			if (complete) {
				expression.getNewValue().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpperExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhenClause expression) {

			complete = expression.hasThenExpression();

			if (complete) {
				expression.getThenExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {

			complete = expression.hasConditionalExpression();

			if (complete) {
				expression.getConditionalExpression().accept(this);
			}
		}
	}

	protected class TripleEncapsulatedCollectionHelper implements CollectionExpressionHelper<AbstractTripleEncapsulatedExpression> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(AbstractTripleEncapsulatedExpression expression,
		                               Expression child,
		                               int index) {

			if (queryBNF(expression, index).handleAggregate()) {
				addAllAggregates(queryBNF(expression, index));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(AbstractTripleEncapsulatedExpression expression, int index) {
			addAllIdentificationVariables();
			addAllFunctions(queryBNF(expression, index));
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(AbstractTripleEncapsulatedExpression expression) {
			return expression.buildCollectionExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(AbstractTripleEncapsulatedExpression expression) {
			return expression.hasLeftParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(AbstractTripleEncapsulatedExpression expression) {
			// Both LOCATE and SUBSTRING can allow up to 3 encapsulated expressions
			return 3;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(AbstractTripleEncapsulatedExpression expression) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(AbstractTripleEncapsulatedExpression expression, int index) {
			return getQueryBNF(expression.parameterExpressionBNF(index));
		}
	}

	protected class UpdateItemCollectionHelper implements CollectionExpressionHelper<UpdateClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(UpdateClause expression, Expression child, int index) {
			addAllAggregates(NewValueBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(UpdateClause expression, int index) {
			addAllIdentificationVariables();
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(UpdateClause expression) {
			CollectionExpression collectionExpression = getCollectionExpression(expression.getUpdateItems());
			if (collectionExpression == null) {
				collectionExpression = expression.buildCollectionExpression();
			}
			return collectionExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasDelimiterAfterIdentifier(UpdateClause expression) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		public int maxCollectionSize(UpdateClause expression) {
			return Integer.MAX_VALUE;
		}

		/**
		 * {@inheritDoc}
		 */
		public int preExpressionLength(UpdateClause expression) {
			// There is a SPACE_LENGTH less, it's added automatically
			return UPDATE.length() +
			       SPACE_LENGTH    +
			       length(expression.getRangeVariableDeclaration()) +
			       SPACE_LENGTH    +
			       SET.length();
		}

		/**
		 * {@inheritDoc}
		 */
		public JPQLQueryBNF queryBNF(UpdateClause expression, int index) {
			return AbstractContentAssistVisitor.this.getQueryBNF(NewValueBNF.ID);
		}
	}

	/**
	 * This visitor is meant to adjust the corrections stack when traversing an {@link Expression} in
	 * order to increase the list of valid proposals.
	 * <p>
	 * For instance, if the query is "<code>SELECT e FROM Employee e WHERE IN</code>" and the cursor
	 * is at the end of the query, then <code>IN</code> would be parsed with {@link InExpression}.
	 * However, due to how {@link AbstractContentAssistVisitor} works, the identifier <code>INDEX</code>
	 * is not added as a valid proposal. This visitor adds that functionality.
	 */
	protected class VisitParentVisitor extends AnonymousExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expression.getParent().accept(AbstractContentAssistVisitor.this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InExpression expression) {

			int position = getPosition(expression) - corrections.peek();
			int length = 0;

			if (expression.hasExpression()) {
				length += length(expression.getExpression()) + SPACE_LENGTH;
			}

			// Within "IN"
			if (isPositionWithin(position, length, expression.getIdentifier())) {

				boolean hasOnlyIdentifier = !expression.hasExpression() &&
				                            !expression.hasInItems();

				if (hasOnlyIdentifier) {
					corrections.add(getPosition(expression));
				}

				super.visit(expression);

				if (hasOnlyIdentifier) {
					corrections.pop();
				}
			}
			else {
				super.visit(expression);
			}
		}
	}

	protected class WhereClauseHelper implements ClauseHelper<WhereClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(WhereClause expression) {

			Expression conditional = expression.getConditionalExpression();

			if (areLogicSymbolsAppendable(conditional)) {
				addAllLogicIdentifiers();
			}

			if (areArithmeticSymbolsAppendable(conditional)) {
				addAllArithmeticIdentifiers();
			}

			if (areComparisonSymbolsAppendable(conditional)) {
				addAllComparisonIdentifiers();
			}

			if (isCompoundable(conditional)) {
				addAllCompounds(ConditionalExpressionBNF.ID);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(WhereClause expression) {
			addAllIdentificationVariables();
			addAllFunctions(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(WhereClause expression) {
			return expression.getConditionalExpression();
		}
	}

	protected class WhereClauseSelectStatementHelper extends AbstractWhereClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      WhereClause clause,
		                                      int position,
		                                      boolean complete) {


			if (complete || isAppendable(clause)) {
				addIdentifier(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addIdentifier(HAVING);

					if (!expression.hasHavingClause()) {
						addIdentifier(ORDER_BY);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public GroupByClauseSelectStatementHelper getNextHelper() {
			return getGroupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public FromClauseSelectStatementHelper getPreviousHelper() {
			return getFromClauseSelectStatementHelper();
		}
	}
}