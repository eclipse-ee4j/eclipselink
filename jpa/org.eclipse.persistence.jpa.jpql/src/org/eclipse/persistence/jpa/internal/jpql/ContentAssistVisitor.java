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
package org.eclipse.persistence.jpa.internal.jpql;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.eclipse.persistence.jpa.internal.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.*;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.internal.jpql.util.AndFilter;
import org.eclipse.persistence.jpa.internal.jpql.util.Filter;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.MappingTypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;

import static org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.internal.jpql.parser.Expression.*;

/**
 * This visitor traverses the JPQL parsed tree and gathers the possible proposals at a given position.
 * <p>
 * Example:
 * <pre><code> // Have the external form of an IQuery
 * {@link org.eclipse.persistence.jpa.jpql.spi.IQuery IQuery} query = ...
 *
 * // Create a JPQLQueryContext
 * {@link JPQLQueryContext} context = new JPQLQueryContext();
 * context.{@link JPQLQueryContext#setQuery(org.eclipse.persistence.jpa.jpql.spi.IQuery) setQuery(query)};
 *
 * // Create a map of the positions within the parsed tree
 * {@link QueryPosition} queryPosition = context.getJPQLExpression().buildPosition(query.getExpression(), position);
 *
 * // Create the visitor and visit the parsed tree
 * ContentAssistVisitor visitor = new ContentAssistVisitor(context);
 * visitor.{@link #prepare(QueryPosition) prepare(queryPosition)};
 * queryPosition.getExpression().accept(visitor);
 *
 * // Retrieve the proposals
 * {@link org.eclipse.persistence.jpa.jpql.ContentAssistProposals ContentAssistProposals} proposals = visitor.getProposals();
 *
 * // Only required if the visitor is cached
 * visitor.dispose();
 *
 * // Only required if the context is cached
 * context.dispose();
 * </code></pre>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ContentAssistVisitor extends AbstractVisitor {

	/**
	 * This is used to change the position of the cursor in order to add possible proposals
	 */
	private Stack<Integer> corrections;

	/**
	 * The cached helpers that are used by this visitor to add valid content assist proposals.
	 */
	private Map<Class<?>, Object> helpers;

	/**
	 * Used to prevent and infinite recursion when one of the visit method is virtually asking a
	 * child expression to be visited.
	 */
	private Stack<Expression> lockedExpressions;

	/**
	 * Used to determine if the cursor is an expression contained in a collection, if not, then this
	 * value is set to -1.
	 */
	private Stack<Integer> positionInCollections;

	/**
	 * The set of possible proposals gathered based on the position in the query.
	 */
	private DefaultContentAssistProposals proposals;

	/**
	 * Contains the position of the cursor within the parsed {@link Expression}.
	 */
	private QueryPosition queryPosition;

	/**
	 * A virtual space is used to move the position by an amount of space in order to find some
	 * proposals within an expression. This is usually used when the trailing whitespace is not owned
	 * by the child expression but by one of its parents.
	 */
	private Stack<Integer> virtualSpaces;

	/**
	 * The current word, which was retrieved from the JPQL based on the position of the cursor.
	 * The word is what is on the left side of the cursor.
	 */
	private String word;

	/**
	 * This is used to retrieve words from the actual JPQL query.
	 */
	private WordParser wordParser;

	/**
	 * A constant for the length of a whitespace, which is 1.
	 */
	private static final int SPACE_LENGTH = 1;

	/**
	 * Creates a new <code>ContentAssistVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 */
	public ContentAssistVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * Returns the collection of possible abstract schema types.
	 *
	 * @return The {@link IEntity entities} defined in the persistence context
	 */
	private Iterable<IEntity> abstractSchemaTypes() {
		return getProvider().abstractSchemaTypes();
	}

	/**
	 * Determines the root {@link IType} that any type should be assignable. If the {@link IType} is
	 * {@link Number}, than any subclasses will be allowed.
	 *
	 * @param expression The {@link Expression} to visit, including its parent hierarchy until an
	 * {@link Expression} requires a certain {@link IType}
	 * @return The root {@link IType} allowed or <code>null</code> if anything is allowed
	 */
	private IType acceptableType(Expression expression) {
		AcceptableTypeVisitor visitor = expressionTypeVisitor();
		try {
			expression.accept(visitor);
			return visitor.type;
		}
		finally {
			visitor.type = null;
		}
	}

	/**
	 * Adds the abstract schema types as possible content assist proposals but will be filtered using
	 * the current word.
	 */
	private void addAbstractSchemaTypes() {
		for (IEntity abstractSchemaType : abstractSchemaTypes()) {
			if (isValidProposal(abstractSchemaType.getName(), word)) {
				proposals.addAbstractSchemaType(abstractSchemaType);
			}
		}
	}

	/**
	 * Adds the abstract schema types as possible content assist proposals but will be filtered using
	 * the current word and the entity's type will have to match the one from the given {@link IType}.
	 *
	 * @param type The {@link IType} used to filter the abstract schema types
	 */
	private void addAbstractSchemaTypes(IType type) {

		for (IEntity abstractSchemaType : abstractSchemaTypes()) {

			if (isValidProposal(abstractSchemaType.getName(), word) &&
			    type.isAssignableTo(abstractSchemaType.getType())) {

				proposals.addAbstractSchemaType(abstractSchemaType);
			}
		}
	}

	private void addAggregate(String identifier) {
		if (isAggregate(identifier)) {
			addProposal(identifier);
		}
	}

	private void addAllAggregates(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.identifiers()) {
			addAggregate(identifier);
		}
	}

	private void addAllAggregates(String queryBNFId) {
		addAllAggregates(queryBNF(queryBNFId));
	}

	private void addAllClauses(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.identifiers()) {
			addClause(identifier);
		}
	}

	private void addAllClauses(String queryBNF) {
		addAllClauses(queryBNF(queryBNF));
	}

	private void addAllCompounds(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.identifiers()) {
			addCompound(identifier);
		}
	}

	private void addAllCompounds(String queryBNFId) {
		addAllCompounds(queryBNF(queryBNFId));
	}

	private void addAllFunctions(JPQLQueryBNF queryBNF) {
		addAllFunctions(queryBNF, queryPosition.getPosition());
	}

	private void addAllFunctions(JPQLQueryBNF queryBNF, int position) {
		for (String identifier : queryBNF.identifiers()) {
			addFunction(identifier, position);
		}
	}

	private void addAllFunctions(String queryBNFId) {
		addAllFunctions(queryBNF(queryBNFId), queryPosition.getPosition());
	}

	private void addAllIdentificationVariables(Expression expression) {
		addIdentificationVariables(IdentificationVariableType.ALL, expression);
	}

	private void addAllIdentifiers(JPQLQueryBNF queryBNF) {
		for (String identifier : queryBNF.identifiers()) {
			addProposal(identifier);
		}
	}

	private void addAllIdentifiers(String queryBNFId) {
		addAllIdentifiers(queryBNF(queryBNFId));
	}

	private void addClause(String identifier) {
		if (isClause(identifier)) {
			addProposal(identifier);
		}
	}

	private void addCompound(String identifier) {
		if (isCompoundFunction(identifier)) {
			addIdentifier(identifier, queryPosition.getPosition());
		}
	}

	private void addFunction(String identifier, int position) {
		if (isFunction(identifier)) {
			addIdentifier(identifier, position);
		}
	}

	private void addIdentificationVariable(String identificationVariable) {

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
	private void addIdentificationVariables(IdentificationVariableType type, Expression expression) {

		if (type == IdentificationVariableType.RESULT_VARIABLE) {
			for (String resultVariable : queryContext.getResultVariables()) {
				addProposal(resultVariable);
			}
		}
		else if (type != IdentificationVariableType.NONE) {
			boolean stop = false;

			for (Declaration declaration : queryContext.getDeclarations()) {

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

						if (shouldStop && !declaration.getJoins().contains(expression) &&
						                  !declaration.getJoinFetches().contains(expression)) {

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

						if (shouldStop && (declaration.getJoins().contains(expression) &&
						                   declaration.getJoinFetches().contains(expression))) {

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

	private void addIdentifier(String identifier, int position) {

		position -= word.length();
		boolean found = addIdentifier(identifier, position, "IS NOT ", 2);

		if (!found) {
			found = addIdentifier(identifier, position, "NOT ", 3);

			if (!found) {
				if (isValidProposal(identifier, word) &&
				    isValidVersion(identifier)) {

					proposals.addIdentifier(identifier);
				}
			}
		}
	}

	private boolean addIdentifier(String identifier,
	                              int position,
	                              String partialEnding,
	                              int endIndex) {

		// Check to see if the previous words are "IS", "IS NOT" and "NOT",
		// they are special case since the only allowed identifiers are
		// those starting with them
		for (int index = partialEnding.length(); index > endIndex; index--) {
			String partial = partialEnding.substring(0, index);

			if (wordParser.endsWith(position, partial)) {
				addProposal(identifier, partial + word);
				return true;
			}
		}

		return false;
	}

	private void addJoinIdentifiers() {
		proposals.addIdentifier(INNER_JOIN);
		proposals.addIdentifier(INNER_JOIN_FETCH);
		proposals.addIdentifier(JOIN);
		proposals.addIdentifier(JOIN_FETCH);
		proposals.addIdentifier(LEFT_JOIN);
		proposals.addIdentifier(LEFT_JOIN_FETCH);
		proposals.addIdentifier(LEFT_OUTER_JOIN);
		proposals.addIdentifier(LEFT_OUTER_JOIN_FETCH);
	}

	private void addLeftIdentificationVariables(Expression expression) {
		addIdentificationVariables(IdentificationVariableType.LEFT, expression);
	}

	private void addProposal(String proposal) {
		addProposal(proposal, word);
	}

	private void addProposal(String identifier, String word) {

		if (isValidProposal(identifier, word) &&
		    isValidVersion(identifier)) {

			proposals.addIdentifier(identifier);
		}
	}

	private void addRangeIdentificationVariable(String identificationVariable) {

		if (ExpressionTools.stringIsNotEmpty(identificationVariable) &&
		    isValidProposal(identificationVariable, word)) {

			Resolver resolver = queryContext.getResolver(identificationVariable);
			IEntity entity = entity(resolver.getManagedType());

			if (entity != null) {
				proposals.addRangeIdentificationVariable(identificationVariable, entity);
			}
			else {
				proposals.addIdentificationVariable(identificationVariable);
			}
		}
	}

	private void addResultVariables(Expression expression) {
		addIdentificationVariables(IdentificationVariableType.RESULT_VARIABLE, expression);
	}

	private void addScalarExpressionProposals(Expression expression) {
		addAllIdentificationVariables(expression);
		addAbstractSchemaTypes();
		addAllFunctions(ScalarExpressionBNF.ID);
	}

	private void addSelectExpressionProposals(AbstractSelectClause expression, int length) {

		int position = position(expression) - corrections.peek();

		// Now check for inside the select expression
		CollectionExpression collectionExpression = collectionExpression(expression.getSelectExpression());

		if (collectionExpression != null) {

			for (int index = 0, count = collectionExpression.childrenSize(); index < count; index++) {
				Expression child = collectionExpression.getChild(index);

				// At the beginning of the child expression
				if (position == length) {
					addAllIdentificationVariables(expression);
					addAllFunctions(SelectItemBNF.ID);
					break;
				}
				else {
					boolean withinChild = addSelectExpressionProposals(child, length, index, index + 1 == count);

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
					addAllIdentificationVariables(expression);
					addAllFunctions(SelectItemBNF.ID);
					break;
				}

				if (collectionExpression.hasSpace(index)) {
					length++;
				}
			}
		}
		else {
			addSelectExpressionProposals(expression.getSelectExpression(), length, 0, true);
		}
	}

	private boolean addSelectExpressionProposals(Expression expression,
	                                             int length,
	                                             int index,
	                                             boolean last) {

		int position = position(expression) - corrections.peek();

		// At the beginning of the child expression
		if (position > 0) {

			if (position == 0) {
				if (index == 0) {
					addProposal(DISTINCT);
				}
				addAllIdentificationVariables(expression);
				addAllFunctions(SelectItemBNF.ID);
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

						addAllAggregates(SelectItemBNF.ID);
					}

					return true;
				}
			}
		}

		return false;
	}

	private AppendableExpressionVisitor appendableExpressionVisitor() {
		return getHelper(AppendableExpressionVisitor.class);
	}

	private FilteringMappingCollector buildFilteringMappingCollector(AbstractPathExpression expression,
	                                                                 Resolver resolver,
	                                                                 Filter<IMapping> filter,
	                                                                 String pattern) {

		return new FilteringMappingCollector(
			resolver,
			buildMappingFilter(expression, filter),
			pattern
		);
	}

	private Object buildHelper(Class<?> helperClass) {
		try {
			Constructor<?> constructor = helperClass.getDeclaredConstructor(ContentAssistVisitor.class);
			constructor.setAccessible(true);
			Object helper = constructor.newInstance(this);
			helpers.put(helperClass, helper);
			return helper;
		}
		catch (Exception e) {
			/* Never happens */
			return null;
		}
	}

	private MappingCollector buildMappingCollector(AbstractPathExpression expression,
	                                               Resolver resolver,
	                                               Filter<IMapping> filter) {

		return buildFilteringMappingCollector(
			expression,
			resolver,
			filter,
			ExpressionTools.EMPTY_STRING
		);
	}

	private Filter<IMapping> buildMappingFilter(AbstractPathExpression expression,
	                                            Filter<IMapping> filter) {

		// Wrap the filter with another Filter that will make sure only the
		// mappings with the right type will be accepted, for instance, AVG(e.|
		// can only accept state fields with a numeric type
		IType type = acceptableType(expression.getParent());

		// No need to filter
		if (type == null) {
			return filter;
		}

		// This will filter the property mappings
		return new AndFilter<IMapping>(new MappingTypeFilter(type), filter);
	}

	@SuppressWarnings("unchecked")
	private SelectStatementHelper<AbstractSelectStatement, Expression> cast(
		SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> helper) {

		return (SelectStatementHelper<AbstractSelectStatement, Expression>) helper;
	}

	private CompletenessVisitor completenessVisitor() {
		return getHelper(TrailingCompletenessVisitor.class);
	}

	private CompoundExpressionHelper compoundExpressionHelper() {
		return getHelper(CompoundExpressionHelper.class);
	}

	private CompletenessVisitor conditionalExpressionCompletenessVisitor() {
		return getHelper(ConditionalExpressionCompletenessVisitor.class);
	}

	private ConstrutorCollectionHelper constructorCollectionHelper() {
		return getHelper(ConstrutorCollectionHelper.class);
	}

	private MappingCollector defaultMappingCollector() {
		return getHelper(DefaultMappingCollector.class);
	}

	private ClauseHelper<DeleteClause> deleteClauseHelper() {
		return getHelper(DeleteClauseHelper.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		word          = null;
		proposals     = null;
		wordParser    = null;
		queryPosition = null;
	}

	private DoubleEncapsulatedCollectionHelper doubleEncapsulatedCollectionHelper() {
		return getHelper(DoubleEncapsulatedCollectionHelper.class);
	}

	private AcceptableTypeVisitor expressionTypeVisitor() {
		return getHelper(AcceptableTypeVisitor.class);
	}

	private int findExpressionPosition(CollectionExpression expression) {

		Expression leafExpression = queryPosition.getExpression();

		if (leafExpression != expression) {
			for (int index = 0, count = expression.childrenSize(); index < count; index++) {
				Expression child = expression.getChild(index);

				if (child.isAncestor(leafExpression)) {
					return index;
				}
			}
		}

		int position = position(expression);

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

	private RangeVariableDeclaration findRangeVariableDeclaration(UpdateClause expression) {
		RangeVariableDeclarationVisitor visitor = rangeVariableDeclarationVisitor();
		try {
			expression.getRangeVariableDeclaration().accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	private FromClauseCollectionHelper fromClauseCollectionHelper() {
		return getHelper(FromClauseCollectionHelper.class);
	}

	private ClauseHelper<AbstractFromClause> fromClauseHelper() {
		return getHelper(FromClauseHelper.class);
	}

	private FromClauseSelectStatementHelper fromClauseSelectStatementHelper() {
		return getHelper(FromClauseSelectStatementHelper.class);
	}

	/**
	 * Retrieves the helper associated with the given helper class. If not cached, the helper will be
	 * automatically created.
	 *
	 * @param helperClass The Java class of the helper to retrieve
	 * @return The helper being requested
	 */
	@SuppressWarnings("unchecked")
	private <T> T getHelper(Class<T> helperClass) {
		Object helper = helpers.get(helperClass);
		if (helper == null) {
			helper = buildHelper(helperClass);
		}
		return (T) helper;
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

	private GroupByClauseCollectionHelper groupByClauseCollectionHelper() {
		return getHelper(GroupByClauseCollectionHelper.class);
	}

	private GroupByClauseSelectStatementHelper groupByClauseSelectStatementHelper() {
		return getHelper(GroupByClauseSelectStatementHelper.class);
	}

	private ClauseHelper<HavingClause> havingClauseHelper() {
		return getHelper(HavingClauseHelper.class);
	}

	private HavingClauseSelectStatementHelper havingClauseSelectStatementHelper() {
		return getHelper(HavingClauseSelectStatementHelper.class);
	}

	private CompletenessVisitor incompleteCollectionExpressionVisitor() {
		return getHelper(IncompleteCollectionExpressionVisitor.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();

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
	private boolean isAggregate(String proposal) {
		return identifierRole(proposal) == IdentifierRole.AGGREGATE;
	}

	private boolean isAppendable(Expression expression) {
		AppendableExpressionVisitor visitor = appendableExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.appendable;
		}
		finally {
			visitor.appendable = false;
		}
	}

	private boolean isAppendableToCollection(Expression expression) {
		CompletenessVisitor visitor = incompleteCollectionExpressionVisitor();
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
	private boolean isClause(String identifier) {
		return JPQLExpression.identifierRole(identifier) == IdentifierRole.CLAUSE;
	}

	private boolean isComplete(Expression expression) {
		CompletenessVisitor visitor = completenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
	}

	private boolean isCompoundable(Expression expression) {
		CompoundExpressionHelper visitor = compoundExpressionHelper();
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
	private boolean isCompoundFunction(String identifier) {

		// Only the s full JPQL identifier is valid
		if (identifier == IS || identifier == OF) {
			return false;
		}

		return identifierRole(identifier) == IdentifierRole.COMPOUND_FUNCTION;
	}

	private boolean isConditionalExpressionComplete(Expression expression) {
		CompletenessVisitor visitor = conditionalExpressionCompletenessVisitor();
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
	private boolean isFunction(String identifier) {
		return identifierRole(identifier) == IdentifierRole.FUNCTION;
	}

	private boolean isGroupByComplete(Expression expression) {
		// TODO
		CompletenessVisitor visitor = completenessVisitor();
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
	private boolean isInSubquery(Expression expression) {
		SubqueryVisitor visitor = subqueryVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	private boolean isLocked(Expression expression) {
		return !lockedExpressions.empty() && (lockedExpressions.peek() == expression);
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
	private boolean isPositionWithin(int position, int offset, String word) {
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
	private boolean isPositionWithin(int position, String word) {
		return isPositionWithin(position, 0, word);
	}

	private boolean isPreviousClauseComplete(AbstractSelectStatement expression,
	                                         SelectStatementHelper<AbstractSelectStatement, Expression> helper) {

		helper = cast(helper.getPreviousHelper());

		if ((helper == null) || !helper.hasClause(expression)) {
			return false;
		}

		Expression clause = helper.getClause(expression);
		Expression clauseExpression = helper.getClauseExpression(clause);
		return helper.isClauseExpressionComplete(clauseExpression);
	}

	private boolean isResultVariable(Expression expression) {
		ResultVariableVisitor visitor = resultVariableVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	private boolean isSelectExpressionComplete(Expression expression) {
		CompletenessVisitor visitor = selectClauseCompletenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
	}

	/**
	 * Determines whether the given proposal is a valid, which is based on the content of the given
	 * word. If the word is not an empty string, the proposal must start with the content of the word.
	 *
	 * @param proposal The proposal to validate
	 * @param word The word, which is what was parsed before the position of the cursor
	 * @return <code>true</code> if the proposal is valid; <code>false</code> otherwise
	 */
	private boolean isValidProposal(String proposal, String word) {

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
	private boolean isValidVersion(String identifier) {
		IJPAVersion identifierVersion = JPQLExpression.identifierVersion(identifier);
		return getJPAVersion().isNewerThanOrEqual(identifierVersion);
	}

	private JoinCollectionHelper joinCollectionHelper() {
		return getHelper(JoinCollectionHelper.class);
	}

	/**
	 * Returns the length of the string representation for the given {@link Expression}. The text
	 * containing any virtual text will be used.
	 *
	 * @param expression The {@link Expression} used to calculate the length of its string
	 * representation
	 * @return The length of the text, which may contain virtual text
	 */
	private int length(Expression expression) {
		return expression.toActualText().length();
	}

	private Filter<IMapping> mappingCollectionFilter() {
		return getHelper(CollectionMappingFilter.class);
	}

	private Filter<IMapping> mappingFilter(Expression expression) {
		MappingFilterBuilder visitor = mappingFilterBuilder();
		try {
			expression.accept(visitor);
			return visitor.filter;
		}
		finally {
			visitor.filter = null;
		}
	}

	private MappingFilterBuilder mappingFilterBuilder() {
		return getHelper(MappingFilterBuilder.class);
	}

	private Filter<IMapping> mappingPropertyFilter() {
		return getHelper(PropertyMappingFilter.class);
	}

	private OrderByClauseCollectionHelper orderByClauseCollectionHelper() {
		return getHelper(OrderByClauseCollectionHelper.class);
	}

	private OrderByClauseSelectStatementHelper orderByClauseSelectStatementHelper() {
		return getHelper(OrderByClauseSelectStatementHelper.class);
	}

	/**
	 * Returns the position of the {@link Expression} within the parsed tree representation of the
	 * JPQL query. The beginning of the string representation is the position returned.
	 *
	 * @param expression The {@link Expression} to find its position in the tree based on the string
	 * representation
	 * @return The position within the parsed tree of the given {@link Expression}
	 */
	private int position(Expression expression) {
		return queryPosition.getPosition(expression);
	}

	/**
	 * Prepares this visitor by prepopulating it with the necessary data that is required to properly
	 * gather the list of proposals based on the caret position.
	 *
	 * @param proposals The object used to store the possible proposals gathered based on the
	 * position in the query
	 * @param queryPosition Contains the position of the cursor within the parsed {@link Expression}
	 */
	public void prepare(QueryPosition queryPosition) {

		this.queryPosition = queryPosition;
		this.proposals     = new DefaultContentAssistProposals();

		wordParser = new WordParser(queryContext.getJPQLExpression().toActualText());
		wordParser.setPosition(queryPosition.getPosition());
		word = wordParser.partialWord();
	}

	private RangeVariableDeclarationVisitor rangeVariableDeclarationVisitor() {
		return getHelper(RangeVariableDeclarationVisitor.class);
	}

	private ResultVariableVisitor resultVariableVisitor() {
		return getHelper(ResultVariableVisitor.class);
	}

	private CompletenessVisitor selectClauseCompletenessVisitor() {
		return getHelper(SelectClauseCompletenessVisitor.class);
	}

	private SelectClauseSelectStatementHelper selectClauseSelectStatementHelper() {
		return getHelper(SelectClauseSelectStatementHelper.class);
	}

	private SimpleFromClauseSelectStatementHelper simpleFromClauseSelectStatementHelper() {
		return getHelper(SimpleFromClauseSelectStatementHelper.class);
	}

	private SimpleGroupByClauseSelectStatementHelper simpleGroupByClauseSelectStatementHelper() {
		return getHelper(SimpleGroupByClauseSelectStatementHelper.class);
	}

	private SimpleHavingClauseSelectStatementHelper simpleHavingClauseSelectStatementHelper() {
		return getHelper(SimpleHavingClauseSelectStatementHelper.class);
	}

	private SimpleSelectClauseSelectStatementHelper simpleSelectClauseSelectStatementHelper() {
		return getHelper(SimpleSelectClauseSelectStatementHelper.class);
	}

	private SimpleWhereClauseSelectStatementHelper simpleWhereClauseSelectStatementHelper() {
		return getHelper(SimpleWhereClauseSelectStatementHelper.class);
	}

	private SubqueryVisitor subqueryVisitor() {
		return getHelper(SubqueryVisitor.class);
	}

	private TripleEncapsulatedCollectionHelper tripleEncapsulatedCollectionHelper() {
		return getHelper(TripleEncapsulatedCollectionHelper.class);
	}

	private UpdateItemCollectionHelper updateItemCollectionHelper() {
		return getHelper(UpdateItemCollectionHelper.class);
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
		corrections.add(position(expression));
		super.visit(expression);
		corrections.pop();

		// Add the possible abstract schema names
		addAbstractSchemaTypes();
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
		int position = position(expression) - corrections.peek();

		// After the arithmetic factor
		if (position == 1) {
			addAllIdentificationVariables(expression);
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
		corrections.add(position(expression));
		super.visit(expression);
		corrections.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {
		super.visit(expression);

		int position = position(expression) - corrections.peek();
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
				addAllIdentificationVariables(expression);
				addAllFunctions(InternalBetweenExpressionBNF.ID);
			}

			// After lower bound
			if (expression.hasLowerBoundExpression()) {

				// Check for something like "<lower bound> <word>"
				int lowerBoundLength = length(expression.getLowerBoundExpression());

				if (!expression.hasAnd() &&
				    (position > length) && (position < length + lowerBoundLength) &&
				    isAppendableToCollection(expression.getLowerBoundExpression())) {

					addProposal(AND);
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
								addAllIdentificationVariables(expression);
								addAllFunctions(InternalBetweenExpressionBNF.ID);
							}
						}
						else if (!expression.hasAnd() &&
						          expression.hasUpperBoundExpression()) {

							length += length(expression.getUpperBoundExpression());

							if (position == length) {
								addProposal(AND);
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
		int position = position(expression) - corrections.peek();

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
				addAllIdentificationVariables(expression);
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
						addAllIdentificationVariables(expression);
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
		int position = position(expression) - corrections.peek();

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
		int position = position(expression) - corrections.peek();
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
					addProposal(OF);
				}
				addAllIdentificationVariables(expression);
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
		int position = position(expression) - corrections.peek();
		int length = 0;

		if (expression.hasLeftExpression()) {
			length += length(expression.getLeftExpression()) + SPACE_LENGTH;
		}

		// Within the comparison operator
		if (isPositionWithin(position, length, expression.getComparisonOperator())) {
			proposals.addIdentifier(LOWER_THAN);
			proposals.addIdentifier(LOWER_THAN_OR_EQUAL);
			proposals.addIdentifier(DIFFERENT);
			proposals.addIdentifier(EQUAL);
			proposals.addIdentifier(GREATER_THAN);
			proposals.addIdentifier(GREATER_THAN_OR_EQUAL);
		}

		// After the comparison operator
		length += expression.getComparisonOperator().length();

		if (expression.hasSpaceAfterIdentifier()) {
			length++;
		}

		// Right after the comparison operator
		if (position == length) {
			addAllIdentificationVariables(expression);
			addAllFunctions(expression.rightExpressionBNF());
			addAllClauses(expression.rightExpressionBNF().getId());
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
		int position = position(expression) - corrections.peek();

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
					addAllIdentificationVariables(expression);
					addAllFunctions(ConstructorItemBNF.ID);
				}
				else {
					visitCollectionExpression(expression, NEW, constructorCollectionHelper());
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
		int position = position(expression) - corrections.peek();;

		// Within the identifier
		if (isPositionWithin(position, CURRENT_DATE) ||
		    isPositionWithin(position, CURRENT_TIME) ||
		    isPositionWithin(position, CURRENT_TIMESTAMP)) {

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
			visitClause(expression, DELETE_FROM, expression.hasSpaceAfterFrom(), deleteClauseHelper());
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
		int position = position(expression) - corrections.peek();
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
		corrections.add(position(expression));
		super.visit(expression);
		corrections.pop();

		// Add the possible abstract schema names
		addAbstractSchemaTypes();
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
			visitCollectionExpression(expression, FROM, fromClauseCollectionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression) {
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
			visitCollectionExpression(expression, GROUP_BY, groupByClauseCollectionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitClause(expression, HAVING, expression.hasSpaceAfterIdentifier(), havingClauseHelper());
			visitCompoundableExpression(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {
		corrections.add(position(expression));
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
			int position = position(expression) - corrections.peek();
			int length = length(expression.getRangeVariableDeclaration()) + SPACE_LENGTH;

			// Right after the range variable declaration
			if (position == length) {
				addJoinIdentifiers();
			}
			else {
				visitCollectionExpression(expression, ExpressionTools.EMPTY_STRING, joinCollectionHelper());
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

		int position = position(expression) - corrections.peek();
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
				addAllFunctions(InItemBNF.ID);
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
		int position = position(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within "<join>"
		if (isPositionWithin(position, identifier)) {

			// Add JOIN identifiers
			proposals.addIdentifier(JOIN);
			proposals.addIdentifier(INNER_JOIN);
			proposals.addIdentifier(LEFT_JOIN);
			proposals.addIdentifier(LEFT_OUTER_JOIN);

			// Only add the JOIN FETCH identifiers if there is no AS or identification variable
			// otherwise the expression would become invalid
			if (!expression.hasAs() &&
			    !expression.hasIdentificationVariable()) {

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
					addProposal(LEFT_JOIN);
					addProposal(LEFT_OUTER_JOIN);

					if (!expression.hasAs() &&
					    !expression.hasIdentificationVariable()) {

						addProposal(LEFT_JOIN_FETCH);
						addProposal(LEFT_OUTER_JOIN_FETCH);
					}
				}
				else if (identifier == INNER) {
					      addProposal(INNER_JOIN);

					if (!expression.hasAs() &&
					    !expression.hasIdentificationVariable()) {

						addProposal(INNER_JOIN_FETCH);
					}
				}
				else if (identifier.equals("LEFT_OUTER")) {
					addProposal(LEFT_OUTER_JOIN);

					if (!expression.hasAs() &&
					    !expression.hasIdentificationVariable()) {

						addProposal(LEFT_OUTER_JOIN_FETCH);
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
				if (isPositionWithin(position, length, AS)) {
					addProposal(AS);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression) {
		super.visit(expression);
		int position = position(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within "<join fetch>"
		if (isPositionWithin(position, identifier)) {
			addJoinIdentifiers();
		}
		// After "<join fetch> "
		else if (expression.hasSpaceAfterFetch()) {
			int length = identifier.length() + SPACE_LENGTH;

			// Right after "<join fetch> "
			if (position == length) {
				addLeftIdentificationVariables(expression);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {

		if (!isLocked(expression)) {
			int position = position(expression);
			boolean hasQueryStatement = expression.hasQueryStatement();
			int length = hasQueryStatement ? length(expression.getQueryStatement()) : 0;

			// At the beginning of the query
			if (position == 0) {
				addProposal(SELECT);
				addProposal(UPDATE);
				addProposal(DELETE_FROM);
			}
			// After the query, inside the invalid query (or ending whitespace)
			else if (position > length) {

				String text = expression.getUnknownEndingStatement().toActualText();

				addProposal(SELECT,      text);
				addProposal(DELETE_FROM, text);
				addProposal(UPDATE,      text);

				if (hasQueryStatement) {
					lockedExpressions.add(expression);
					corrections.add(-length - 1);

					expression.getQueryStatement().accept(this);

					corrections.pop();
					lockedExpressions.pop();
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

		corrections.add(position(expression));
		super.visit(expression);
		corrections.pop();

		int position = position(expression) - corrections.peek();

		// Within the identifier
		if (isPositionWithin(position, TRUE)  ||
		    isPositionWithin(position, FALSE) ||
		    isPositionWithin(position, NULL)) {

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
		int position = position(expression) - corrections.peek();
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
		visitCollectionExpression(expression, LOCATE, tripleEncapsulatedCollectionHelper());
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
		visitCollectionExpression(expression, MOD, doubleEncapsulatedCollectionHelper());
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
		int position = position(expression) - corrections.peek();

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
					String variableName = queryContext.literal(
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
		int position = position(expression) - corrections.peek();
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
		visitCollectionExpression(expression, NULLIF, doubleEncapsulatedCollectionHelper());
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
			visitCollectionExpression(expression, ORDER_BY, orderByClauseCollectionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {
		super.visit(expression);
		int position = position(expression) - corrections.peek();

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
		int position = position(expression) - corrections.peek();

		// After "<abstract schema name> "
		if (expression.hasAbstractSchemaName() &&
		    expression.hasSpaceAfterAbstractSchemaName()) {

			int length = length(expression.getAbstractSchemaName()) + SPACE_LENGTH;

			// Right after "<abstract schema name> "
			if (isPositionWithin(position, length, AS)) {
				addProposal(AS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {
		super.visit(expression);
		int position = position(expression) - corrections.peek();
		int length = 0;

		if (expression.hasSelectExpression()) {
			length += length(expression.getSelectExpression()) + SPACE_LENGTH;
		}

		// Within "AS"
		if (isPositionWithin(position, length, AS)) {
			addProposal(AS);
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
			visitSelectStatement(expression, selectClauseSelectStatementHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		if (!isLocked(expression)) {
			super.visit(expression);
			visitClause(expression, FROM, expression.hasSpaceAfterFrom(), fromClauseHelper());
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
			visitSelectStatement(expression, simpleSelectClauseSelectStatementHelper());
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
		corrections.add(position(expression));
		super.visit(expression);
		corrections.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression) {
		super.visit(expression);
		visitCollectionExpression(expression, SUBSTRING, tripleEncapsulatedCollectionHelper());
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
		int position = position(expression) - corrections.peek();

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
					proposals.addIdentifier(AS);

					// If the entity type is not specified, then we can add
					// the possible abstract schema names
					if (!expression.hasEntityType()) {

						// If the type of the path expression is resolvable,
						// then filter the abstract schema types
						IType type = getType(collectionValuedPathExpression);

						if (type.isResolvable()) {
							addAbstractSchemaTypes(type);
						}
						else {
							addAbstractSchemaTypes();
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
						addAbstractSchemaTypes(type);
					}
					else {
						addAbstractSchemaTypes();
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
		int position = position(expression) - corrections.peek();
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
				addProposal(BOTH);
				addProposal(LEADING);
				addProposal(TRAILING);

				if (!expression.hasTrimCharacter() &&
				    !expression.hasFrom()) {

					addAllIdentificationVariables(expression);
					addAllFunctions(StringPrimaryBNF.ID);
				}
			}

			// Within the trim specification
			if (expression.hasSpecification()) {
				String specification = expression.getSpecification().name();

				if (isPositionWithin(position, length, specification)) {
					addProposal(BOTH);
					addProposal(LEADING);
					addProposal(TRAILING);

					if (!expression.hasTrimCharacter() &&
					    !expression.hasFrom()) {

						addAllIdentificationVariables(expression);
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
				addProposal(FROM);

				if (!expression.hasFrom()) {
					addAllIdentificationVariables(expression);
					addAllFunctions(StringPrimaryBNF.ID);
				}
			}

			if (expression.hasFrom()) {

				// Within "FROM"
				if (isPositionWithin(position, length, FROM)) {
					addProposal(FROM);
				}

				length += FROM.length();
			}

			if (expression.hasSpaceAfterFrom()) {
				length += SPACE_LENGTH;
			}

			// Right after "FROM "
			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllFunctions(StringPrimaryBNF.ID);
			}

			// Right after the string literal but there is no trim character,
			// nor FROM and there is a virtual space
			if (expression.hasExpression()) {
				length += length(expression.getExpression());

				if ((position == length + virtualSpaces.peek()) &&
				    !expression.hasTrimCharacter() &&
				    !expression.hasFrom()) {

					addProposal(FROM);
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
		corrections.add(position(expression));
		super.visit(expression);
		corrections.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		super.visit(expression);
		int position = position(expression) - corrections.peek();

		// Within "UPDATE"
		if (isPositionWithin(position, UPDATE)) {
			proposals.addIdentifier(UPDATE);
		}
		// After "UPDATE "
		else if (expression.hasSpaceAfterUpdate()) {
			int length = UPDATE.length() + SPACE_LENGTH;

			// Right after "UPDATE "
			if (position == length) {
				addAbstractSchemaTypes();
			}
			// After "<range variable declaration> "
			else if (expression.hasRangeVariableDeclaration()) {

				RangeVariableDeclaration rangeVariableDeclaration = findRangeVariableDeclaration(expression);

				if ((rangeVariableDeclaration != null) &&
				     rangeVariableDeclaration.hasAbstractSchemaName() &&
				     rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName()) {

					length += length(rangeVariableDeclaration.getAbstractSchemaName()) + SPACE_LENGTH;

					// Example: "UPDATE System s"
					if (!expression.hasSet()        &&
					    !rangeVariableDeclaration.hasAs() &&
					    isPositionWithin(position, length, SET)) {

						addProposal(SET);
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

							addProposal(SET);
						}
						// After "SET "
						else if (expression.hasSet() &&
						         expression.hasSpaceAfterSet()) {

							length += SET.length() + SPACE_LENGTH;

							// Right after "SET "
							if (position == length) {
								addAllIdentificationVariables(expression);
							}
							// Within the new value expressions
							else {
								visitCollectionExpression(expression, ExpressionTools.EMPTY_STRING, updateItemCollectionHelper());
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
		int position = position(expression) - corrections.peek();
		int length = 0;

		// At the beginning
		if (position == length) {
			addAllIdentificationVariables(expression);
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
					addAllIdentificationVariables(expression);
					addAllFunctions(NewValueBNF.ID);
				}
				else if (expression.hasSpaceAfterEqualSign()) {
					length++;

					// Right after "= "
					if (position == length) {
						addAllIdentificationVariables(expression);
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
		int position = position(expression) - corrections.peek();

		// Within "WHEN"
		if (isPositionWithin(position, WHEN)) {
			proposals.addIdentifier(WHEN);
		}
		// After "WHEN "
		else if (expression.hasSpaceAfterWhen()) {
			int length = WHEN.length() + SPACE_LENGTH;

			// Right after "WHEN "
			if (position == length) {
				addAllIdentificationVariables(expression);
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
									addScalarExpressionProposals(expression);
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

	private void visitAggregateFunction(AggregateFunction expression) {

		int position = position(expression) - corrections.peek();
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
				addProposal(DISTINCT);
			}
			// After "("
			else {
				if (hasDistinct && expression.hasSpaceAfterDistinct()) {
					length += DISTINCT.length() + SPACE_LENGTH;
				}

				// Right after "(" or right after "(DISTINCT "
				if (position == length) {
					if (!hasDistinct) {
						addProposal(DISTINCT);
					}
					addAllIdentificationVariables(expression);
					addAllFunctions(expression.encapsulatedExpressionBNF());
				}
			}
		}
	}

	private void visitArithmeticExpression(ArithmeticExpression expression) {

		int position = position(expression) - corrections.peek();
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
				addAllIdentificationVariables(expression);
				addAllFunctions(expression.rightExpressionBNF(), position);
			}
		}
	}

	private <T extends AbstractExpression> void visitClause(T expression,
	                                                        String identifier,
	                                                        boolean hasSpaceAfterIdentifier,
	                                                        ClauseHelper<T> helper) {

		lockedExpressions.add(expression);
		int position = position(expression) - corrections.peek();

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
	private <T extends Expression> void visitCollectionExpression(T expression,
	                                                              String identifier,
	                                                              CollectionExpressionHelper<T> helper) {

		int position = position(expression) - corrections.peek();

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

	private void visitCompoundableExpression(AbstractConditionalClause expression) {

		if (expression.hasConditionalExpression()) {

			// Get the position and start length
			int position = position(expression);
			int length = expression.getIdentifier().length() + SPACE_LENGTH;

			CompoundExpressionHelper helper = compoundExpressionHelper();

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

	private void visitCompoundableExpression(CompoundExpressionHelper helper,
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

	private void visitDeleteStatement(DeleteStatement expression) {

		lockedExpressions.add(expression);
		int position = position(expression);

		//
		// DELETE clause
		//
		DeleteClause deleteClause = expression.getDeleteClause();
		int length = length(deleteClause);

		// At the end of the DELETE clause, check for adding proposals based
		// on possible incomplete information
		if ((position == length) && isAppendable(deleteClause)) {
			addProposal(WHERE);
		}
		// Right after the DELETE clause, the space is owned by the select statement
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

				addProposal(WHERE);
			}
		}

		if (expression.hasWhereClause()) {
			AbstractConditionalClause whereClause = (AbstractConditionalClause) expression.getWhereClause();

			// Check for within the WHERE clause
			if (position > length) {
				int whereClauseLength = length(whereClause);
				length += whereClauseLength;

				// Right after the WHERE clause, the space is owned by the select statement
				if ((position == length + SPACE_LENGTH) && true) {

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
	private void visitEncapsulatedExpression(AbstractEncapsulatedExpression expression,
	                                         String identifier,
	                                         String jpqlQueryBNF) {

		int position = position(expression) - corrections.peek();

		// Within the identifier
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(identifier);
		}
		// Right after "<identifier>("
		else if (expression.hasLeftParenthesis()) {
			int length = identifier.length() + 1 /* '(' */;

			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllFunctions(jpqlQueryBNF);
			}
		}
	}

	private void visitLogicalExpression(LogicalExpression expression, String identifier) {

		int position = position(expression) - corrections.peek();
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
				addAllIdentificationVariables(expression);
				addAllFunctions(expression.rightExpressionBNF());
			}
		}
	}

	private VisitParentVisitor visitParentVisitor() {
		return getHelper(VisitParentVisitor.class);
	}

	private void visitPathExpression(AbstractPathExpression expression) {

		int position = position(expression);
		String text = expression.toActualText();
		int dotIndex = text.indexOf(AbstractExpression.DOT);

		if (position > -1) {
			if ((dotIndex > -1) && (position > dotIndex)) {
				// Retrieve the filter based on the location of the state field path, for instance, in a
				// JOIN or IN expression, the filter has to filter out the property and accept the fields
				// of collection type
				visitPathExpression(expression, mappingFilter(expression));
			}
			else {
				String variableName = queryContext.literal(
					expression.getIdentificationVariable(),
					LiteralType.IDENTIFICATION_VARIABLE
				);

				// Don't do anything if the identification variable is either KEY() or VALUE()
				if (ExpressionTools.stringIsNotEmpty(variableName)) {
					corrections.add(position(expression));
					visit(expression);
					corrections.pop();
				}
			}
		}
	}

	private void visitPathExpression(AbstractPathExpression expression, Filter<IMapping> helper) {

		MappingCollector mappingCollector = defaultMappingCollector();
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
					resolver = queryContext.getResolver(expression.getIdentificationVariable());
				}
				// Any other path is a property or collection-valued path
				else if ((index + 1 < count) || expression.endsWithDot()) {
					Resolver childResolver = resolver.getChild(path);
					if (childResolver == null) {
						childResolver = new SingleValuedObjectFieldResolver(resolver, path);
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

	private void visitSelectClause(AbstractSelectClause expression) {

		int position = position(expression) - corrections.peek();

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
						addProposal(DISTINCT);
					}
					addAllIdentificationVariables(expression);
					addAllFunctions(SelectItemBNF.ID);
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

	private SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression>
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

	private void visitSelectStatement(AbstractSelectStatement expression,
	                                  SelectStatementHelper<? extends AbstractSelectStatement, ? extends Expression> helper) {

		lockedExpressions.add(expression);

		try {
			int position = position(expression);
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
	private void visitSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression,
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
	private void visitSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression,
	                                               IdentificationVariableType identificationVariableType,
	                                               String... expressionIdentifiers) {

		int position = position(expression) - corrections.peek();
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

	private void visitUpdateStatement(UpdateStatement expression) {

		lockedExpressions.add(expression);
		int position = position(expression);

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

				addProposal(WHERE);
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

	private ClauseHelper<WhereClause> whereClauseHelper() {
		return getHelper(WhereClauseHelper.class);
	}

	private WhereClauseSelectStatementHelper whereClauseSelectStatementHelper() {
		return getHelper(WhereClauseSelectStatementHelper.class);
	}

	private abstract class AbstractFromClauseSelectStatementHelper<T extends AbstractSelectStatement>
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
			addProposal(FROM);
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

	private abstract class AbstractGroupByClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                 implements SelectStatementHelper<T, GroupByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addProposal(GROUP_BY);
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

	private abstract class AbstractHavingClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                 implements SelectStatementHelper<T, HavingClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addProposal(HAVING);
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

	private abstract class AbstractSelectClauseSelectStatementHelper implements SelectStatementHelper<AbstractSelectStatement, AbstractSelectClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addProposal(SELECT);
		}

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(AbstractSelectStatement expression,
		                                      AbstractSelectClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addProposal(FROM);
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

	private abstract class AbstractWhereClauseSelectStatementHelper<T extends AbstractSelectStatement>
	                 implements SelectStatementHelper<T, WhereClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addProposal(WHERE);
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
	private class AcceptableTypeVisitor extends AbstractExpressionVisitor {

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
	private class AppendableExpressionVisitor extends AbstractTraverseChildrenVisitor {

		/**
		 *
		 */
		boolean appendable;

		/**
		 *
		 */
		int positionInCollection;

		/**
		 * Creates a new <code>AppendableExpressionVisitor</code>.
		 */
		@SuppressWarnings("unused")
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
			expression.getChild(positionInCollection).accept(this);
			positionInCollection = -1;
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
		public void visit(IdentificationVariable expression) {
			appendable = (positionInCollection > -1);
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
		public void visit(MultiplicationExpression expression) {
			super.visit(expression);
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
			appendable = !expression.hasSpaceAfterAbstractSchemaName() &&
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
	private interface ClauseHelper<T extends Expression> {

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
	private interface CollectionExpressionHelper<T extends Expression> {

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

	private class CollectionMappingFilter implements Filter<IMapping> {

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(IMapping value) {
			// Both association and collection field are accepted
			// Example: e.address is incomplete but it is not the entire path
			// Example: e.projects is the complete path
			return MappingTypeHelper.isRelationshipMapping(value);
		}
	}

	private abstract class CompletenessVisitor extends AbstractExpressionVisitor {

		/**
		 * Determines whether an {@link Expression} that was visited is complete or if some part is
		 * missing.
		 */
		boolean complete;

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
	private class CompoundExpressionHelper extends AnonymousExpressionVisitor {

		private Expression leftExpression;
		private LogicalExpression logicalExpression;
		private Expression rightExpression;

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
				return ContentAssistVisitor.this.length(leftExpression);
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

		private void visitLogicalExpression(LogicalExpression expression) {

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
	private class ConditionalExpressionCompletenessVisitor extends CompletenessVisitor {

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

	private class ConstrutorCollectionHelper implements CollectionExpressionHelper<ConstructorExpression> {

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
			CollectionExpression collectionExpression = collectionExpression(expression.getConstructorItems());
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
			return AbstractExpression.queryBNF(ConstructorItemBNF.ID);
		}
	}

	/**
	 * The default implementation of {@link MappingCollector}, which simply returns an empty
	 * collection.
	 */
	private class DefaultMappingCollector implements MappingCollector {

		/**
		 * {@inheritDoc}
		 */
		public Collection<IMapping> buildProposals() {
			return Collections.emptyList();
		}
	}

	private class DeleteClauseHelper implements ClauseHelper<DeleteClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(DeleteClause expression) {
			// TODO?
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(DeleteClause expression) {
			addAbstractSchemaTypes();
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(DeleteClause expression) {
			return expression.getRangeVariableDeclaration();
		}
	}

	private class DoubleEncapsulatedCollectionHelper implements CollectionExpressionHelper<AbstractDoubleEncapsulatedExpression> {

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
			addAllIdentificationVariables(expression);
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
			return expression.parameterExpressionBNF(index);
		}
	}

	/**
	 * This {@link MappingCollector} returns the possible mappings (non-collection type or
	 * collection type) from a managed type.
	 */
	private class FilteringMappingCollector implements MappingCollector {

		/**
		 * The {@link Filter} used to filter out either the collection type properties or the non-
		 * collection type properties.
		 */
		private final Filter<IMapping> filter;

		/**
		 * This resolver is used to retrieve the managed type, which is the parent path of this one.
		 */
		private final Resolver resolver;

		/**
		 * The suffix is used to determine if the mapping name needs to be filtered out or not.
		 */
		private final String suffix;

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

		private void addFilteredMappings(IManagedType managedType, List<IMapping> mappings) {

			Filter<IMapping> filter = buildFilter(suffix);

			for (IMapping mapping : managedType.mappings()) {
				if (filter.accept(mapping)) {
					mappings.add(mapping);
				}
			}
		}

		private Filter<IMapping> buildFilter(String suffix) {
			if (suffix.length() == 0) {
				return filter;
			}
			return new AndFilter<IMapping>(filter, buildMappingNameFilter(suffix));
		}

		private Filter<IMapping> buildMappingNameFilter(final String suffix) {
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

	private class FromClauseCollectionHelper implements CollectionExpressionHelper<AbstractFromClause> {

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
			addAbstractSchemaTypes();
			if (index > 0) {
				addProposal(IN);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(AbstractFromClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getDeclaration());
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
			return expression.declarationBNF();
		}
	}

	private class FromClauseHelper implements ClauseHelper<AbstractFromClause> {

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

			addAbstractSchemaTypes();

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

	private class FromClauseSelectStatementHelper extends AbstractFromClauseSelectStatementHelper<SelectStatement> {

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

			addProposal(WHERE);

			if (!expression.hasWhereClause()) {
				addProposal(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addProposal(HAVING);

					if (!expression.hasHavingClause()) {
						addProposal(ORDER_BY);
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
			return selectClauseSelectStatementHelper();
		}
	}

	private class GroupByClauseCollectionHelper implements CollectionExpressionHelper<GroupByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(GroupByClause expression, Expression child, int index) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(GroupByClause expression, int index) {
			addAllIdentificationVariables(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(GroupByClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getGroupByItems());
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
			return AbstractExpression.queryBNF(GroupByItemBNF.ID);
		}
	}

	private class GroupByClauseSelectStatementHelper extends AbstractGroupByClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      GroupByClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addProposal(HAVING);

				if (!expression.hasHavingClause()) {
					addProposal(ORDER_BY);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public HavingClauseSelectStatementHelper getNextHelper() {
			return havingClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public WhereClauseSelectStatementHelper getPreviousHelper() {
			return whereClauseSelectStatementHelper();
		}
	}

	private class HavingClauseHelper implements ClauseHelper<HavingClause> {

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
			addAllIdentificationVariables(expression);
			addAllFunctions(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(HavingClause expression) {
			return expression.getConditionalExpression();
		}
	}

	private class HavingClauseSelectStatementHelper extends AbstractHavingClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      HavingClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addProposal(ORDER_BY);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public OrderByClauseSelectStatementHelper getNextHelper() {
			return orderByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public GroupByClauseSelectStatementHelper getPreviousHelper() {
			return groupByClauseSelectStatementHelper();
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
	private enum IdentificationVariableType {

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

	private class IncompleteCollectionExpressionVisitor extends CompletenessVisitor {

		/**
		 * This flag is used to make sure only the last expression in a collection is tested. A single
		 * expression cannot be used to check the "completeness".
		 */
		private boolean insideCollection;

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

	private class JoinCollectionHelper implements CollectionExpressionHelper<IdentificationVariableDeclaration> {

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
			CollectionExpression collectionExpression = collectionExpression(expression.getJoins());
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
			return AbstractExpression.queryBNF(InternalJoinBNF.ID);
		}
	}

	/**
	 * A collector is responsible to retrieve the possible proposals by using the mappings that can
	 * complete a path expression.
	 */
	private interface MappingCollector {

		/**
		 * Retrieves the possible proposals that can be used to complete a path expression based on
		 * the position of the cursor.
		 *
		 * @return The possible proposals
		 */
		Collection<IMapping> buildProposals();
	}

	/**
	 * This visitor is responsible to create the right {@link Filter<IMapping>} based on the type
	 * of the {@link Expression}.
	 */
	private class MappingFilterBuilder extends AbstractTraverseParentVisitor {

		/**
		 * The {@link Filter} that will filter the various type of {@link IMapping IMappings} based
		 * on the location of the of the path expression within the JPQL query.
		 */
		Filter<IMapping> filter;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			filter = mappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			filter = mappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			filter = mappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JoinFetch expression) {
			filter = mappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLExpression expression) {
			filter = mappingPropertyFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression) {
			filter = mappingCollectionFilter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TreatExpression expression) {
			filter = mappingCollectionFilter();
		}
	}

	/**
	 * This {@link Filter} is responsible to filter out the mappings that can't have their type
	 * assignable to the one passed in.
	 */
	private class MappingTypeFilter implements Filter<IMapping> {

		/**
		 * The type used to determine if the mapping's type is a valid type.
		 */
		private final IType type;

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
			if (MappingTypeHelper.isRelationshipMapping(value) &&
			   !MappingTypeHelper.isCollectionMapping(value)) {

				return true;
			}

			// Determine if it's assignable to the desired type
			IType mappingType = value.getType();
			mappingType = getTypeHelper().convertPrimitive(mappingType);
			return mappingType.isAssignableTo(type);
		}
	}

	private class OrderByClauseCollectionHelper implements CollectionExpressionHelper<OrderByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(OrderByClause expression, Expression child, int index) {
			addProposal(ASC);
			addProposal(DESC);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(OrderByClause expression, int index) {
			addAllIdentificationVariables(expression);
			addResultVariables(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(OrderByClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getOrderByItems());
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
			return AbstractExpression.queryBNF(OrderByItemBNF.ID);
		}
	}

	private class OrderByClauseSelectStatementHelper implements SelectStatementHelper<SelectStatement, OrderByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposal() {
			addProposal(ORDER_BY);
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
			return havingClauseSelectStatementHelper();
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

	private class PropertyMappingFilter implements Filter<IMapping> {

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(IMapping value) {
			return !MappingTypeHelper.isTransientMapping(value) &&
			       !MappingTypeHelper.isCollectionMapping(value);
		}
	}

	private class RangeVariableDeclarationVisitor extends AbstractExpressionVisitor {

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

	private class ResultVariableVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link ResultVariable} if it was visited otherwise <code>null</code>.
		 */
		private ResultVariable expression;

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
	private class SelectClauseCompletenessVisitor extends CompletenessVisitor {

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
		public void visit(FuncExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
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

			int position = position(expression);

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

		private void visitAbstractDoubleEncapsulatedExpression(AbstractDoubleEncapsulatedExpression expression) {
			visitAbstractEncapsulatedExpression(expression);
		}

		private void visitAbstractEncapsulatedExpression(AbstractEncapsulatedExpression expression) {
			// If ')' is present, then anything can be added after
			complete = expression.hasRightParenthesis();
		}

		private void visitAbstractSingleEncapsulatedExpression(AbstractSingleEncapsulatedExpression expression) {
			visitAbstractEncapsulatedExpression(expression);
		}

		private void visitAbstractTripleEncapsulatedExpression(AbstractTripleEncapsulatedExpression expression) {
			visitAbstractEncapsulatedExpression(expression);
		}

		private void visitAggregateFunction(AggregateFunction expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		private void visitArithmeticExpression(ArithmeticExpression expression) {
			expression.getRightExpression().accept(this);
		}

		private void visitEncapsulatedIdentificationVariableExpression(EncapsulatedIdentificationVariableExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}
	}

	private class SelectClauseSelectStatementHelper extends AbstractSelectClauseSelectStatementHelper {

		/**
		 * {@inheritDoc}
		 */
		public FromClauseSelectStatementHelper getNextHelper() {
			return fromClauseSelectStatementHelper();
		}
	}

	private interface SelectStatementHelper<T extends AbstractSelectStatement, C extends Expression> {

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
		 * @param expression The {@link AbstractSelectStatement} being visited
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

	private class SimpleFromClauseSelectStatementHelper extends AbstractFromClauseSelectStatementHelper<SimpleSelectStatement> {

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

			addProposal(WHERE);

			if (!expression.hasWhereClause()) {
				addProposal(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addProposal(HAVING);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleWhereClauseSelectStatementHelper getNextHelper() {
			return simpleWhereClauseSelectStatementHelper();
		}


		/**
		 * {@inheritDoc}
		 */
		public SimpleSelectClauseSelectStatementHelper getPreviousHelper() {
			return simpleSelectClauseSelectStatementHelper();
		}
	}

	private class SimpleGroupByClauseSelectStatementHelper extends AbstractGroupByClauseSelectStatementHelper<SimpleSelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SimpleSelectStatement expression,
		                                      GroupByClause clause,
		                                      int position,
		                                      boolean complete) {

			if (complete || isAppendable(clause)) {
				addProposal(HAVING);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleHavingClauseSelectStatementHelper getNextHelper() {
			return simpleHavingClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleWhereClauseSelectStatementHelper getPreviousHelper() {
			return simpleWhereClauseSelectStatementHelper();
		}
	}

	private class SimpleHavingClauseSelectStatementHelper extends AbstractHavingClauseSelectStatementHelper<SimpleSelectStatement> {

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
			return simpleGroupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(SimpleSelectStatement expression) {
			return false;
		}
	}

	private class SimpleSelectClauseSelectStatementHelper extends AbstractSelectClauseSelectStatementHelper {

		/**
		 * {@inheritDoc}
		 */
		public SimpleFromClauseSelectStatementHelper getNextHelper() {
			return simpleFromClauseSelectStatementHelper();
		}
	}

	private class SimpleWhereClauseSelectStatementHelper extends AbstractWhereClauseSelectStatementHelper<SimpleSelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SimpleSelectStatement expression,
		                                      WhereClause clause,
		                                      int position,
		                                      boolean complete) {


			if (complete || isAppendable(clause)) {
				addProposal(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addProposal(HAVING);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleGroupByClauseSelectStatementHelper getNextHelper() {
			return simpleGroupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public SimpleFromClauseSelectStatementHelper getPreviousHelper() {
			return simpleFromClauseSelectStatementHelper();
		}
	}

	/**
	 * This visitor determines if an {@link Expression} is in a subquery.
	 */
	private class SubqueryVisitor extends AbstractTraverseParentVisitor {

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

	private class TrailingCompletenessVisitor extends CompletenessVisitor {

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
		public void visit(FuncExpression expression) {
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
			complete = expression.hasIdentificationVariable();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JoinFetch expression) {
			complete = expression.hasJoinAssociationPath();
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

	private class TripleEncapsulatedCollectionHelper implements CollectionExpressionHelper<AbstractTripleEncapsulatedExpression> {

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
			addAllIdentificationVariables(expression);
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
			return expression.parameterExpressionBNF(index);
		}
	}

	private class UpdateItemCollectionHelper implements CollectionExpressionHelper<UpdateClause> {

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
			addAllIdentificationVariables(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(UpdateClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getUpdateItems());
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
			return AbstractExpression.queryBNF(NewValueBNF.ID);
		}
	}

	/**
	 * This visitor is meant to adjust the corrections stack when traversing an {@link Expression} in
	 * order to increase the list of valid proposals.
	 * <p>
	 * For instance, if the query is "<code>SELECT e FROM Employee e WHERE IN</code>" and the cursor
	 * is at the end of the query, then <code>IN</code> would be parsed with {@link InExpression}.
	 * However, due to how {@link ContentAssistVisitor} works, the identifier <code>INDEX</code>
	 * is not added as a valid proposal. This visitor adds that functionality.
	 */
	private class VisitParentVisitor extends AnonymousExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expression.getParent().accept(ContentAssistVisitor.this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InExpression expression) {

			int position = position(expression) - corrections.peek();
			int length = 0;

			if (expression.hasExpression()) {
				length += length(expression.getExpression()) + SPACE_LENGTH;
			}

			// Within "IN"
			if (isPositionWithin(position, length, expression.getIdentifier())) {

				boolean hasOnlyIdentifier = !expression.hasExpression() &&
				                            !expression.hasInItems();

				if (hasOnlyIdentifier) {
					corrections.add(position(expression));
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

	private class WhereClauseHelper implements ClauseHelper<WhereClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(WhereClause expression) {

			addAllAggregates(ConditionalExpressionBNF.ID);

			if (isCompoundable(expression.getConditionalExpression())) {
				addAllCompounds(ConditionalExpressionBNF.ID);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addProposals(WhereClause expression) {
			addAllIdentificationVariables(expression);
			addAllFunctions(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(WhereClause expression) {
			return expression.getConditionalExpression();
		}
	}

	private class WhereClauseSelectStatementHelper extends AbstractWhereClauseSelectStatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void appendNextClauseProposals(SelectStatement expression,
		                                      WhereClause clause,
		                                      int position,
		                                      boolean complete) {


			if (complete || isAppendable(clause)) {
				addProposal(GROUP_BY);

				if (!expression.hasGroupByClause()) {
					addProposal(HAVING);

					if (!expression.hasHavingClause()) {
						addProposal(ORDER_BY);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public GroupByClauseSelectStatementHelper getNextHelper() {
			return groupByClauseSelectStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		public FromClauseSelectStatementHelper getPreviousHelper() {
			return fromClauseSelectStatementHelper();
		}
	}
}