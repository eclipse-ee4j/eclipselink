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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.eclipse.persistence.jpa.internal.jpql.DeclarationExpressionLocator;
import org.eclipse.persistence.jpa.internal.jpql.DefaultContentAssistItems;
import org.eclipse.persistence.jpa.internal.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.internal.jpql.VariableNameType;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractValidator.CollectionExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

import static org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression.*;

/**
 * This visitor traverses the JPQL parsed tree and gather the possible choices at a given position.
 * <p>
 * Note: It does not retrieve the possible state fields and collection valued fields because this
 * validator does not access to the application.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ContentAssistVisitor extends AbstractTraverseParentVisitor {

	/**
	 *
	 */
	private AppendableExpressionVisitor appendableExpressionVisitor;

	/**
	 *
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 *
	 */
	private CompletenessVisitor completenessVisitor;

	/**
	 *
	 */
	private ConstrutorCollectionHelper construtorCollectionHelper;

	/**
	 * This is used to change the position of the cursor in order to add possible choices
	 */
	private Stack<Integer> corrections;

	/**
	 *
	 */
	private ClauseHelper<DeleteClause> deleteClauseHelper;

	/**
	 *
	 */
	private DoubleEncapsulatedCollectionHelper doubleEncapsulatedCollectionHelper;

	/**
	 *
	 */
	private CollectionExpressionHelper<AbstractFromClause> fromClauseCollectionHelper;

	/**
	 *
	 */
	private ClauseHelper<AbstractFromClause> fromClauseHelper;

	/**
	 *
	 */
	private GroupByClauseCollectionHelper groupByClauseCollectionHelper;

	/**
	 *
	 */
	private ClauseHelper<HavingClause> havingClauseHelper;

	/**
	 * The set of possible choices gathered based on the position in the query.
	 */
	private DefaultContentAssistItems items;

	private JoinCollectionHelper joinCollectionHelper;

	/**
	 * Used to prevent and infinite recursion when one of the visit method is virtually asking a
	 * child expression to be visited.
	 */
	private Stack<Expression> lockedExpressions;

	private OrderByClauseCollectionHelper orderByClauseCollectionHelper;

	/**
	 * Used to determine if the cursor is an expression contained in a collection, if not, then this
	 * value is set to -1.
	 */
	private Stack<Integer> positionInCollections;

	/**
	 * The external representation of the query used to retrieve the possible choices at a certain
	 * position.
	 */
	private IQuery query;

	/**
	 * The context where some information is cached.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * Contains the position of the cursor within the parsed {@link Expression}.
	 */
	private QueryPosition queryPosition;

	/**
	 *
	 */
	private SelectClauseCompletenessVisitor selectClauseCompletenessVisitor;

	private TripleEncapsulatedCollectionHelper tripleEncapsulatedCollectionHelper;

	/**
	 *
	 */
	private UpdateItemCollectionHelper updateItemCollectionHelper;

	/**
	 * A virtual space is used to move the position by an amount of space in order to find some
	 * choices within an expression. This is usually used when the trailing whitespace is not owned
	 * by the child expression but by one of its parents.
	 */
	private Stack<Integer> virtualSpaces;

	/**
	 *
	 */
	private ClauseHelper<WhereClause> whereClauseHelper;

	/**
	 * The current word based on the query and the position of the cursor. The word is what is on the
	 * left side of the cursor.
	 */
	private String word;

	/**
	 * This is used to retrieve words from the actual query.
	 */
	private WordParser wordParser;

	/**
	 * A constant for the length of a whitespace, which is 1.
	 */
	private static final int SPACE_LENGTH = 1;

	/**
	 * Creates a new <code>ContentAssistVisitor</code>.
	 *
	 * @param query The external representation of the JPQL query
	 * @param jpqlExpression The parsed tree representation of the JPQL query
	 * @param queryContext The context where some information is cached
	 * @param items The object used to store the possible choices gathered based on the position in
	 * the query
	 * @param queryPosition Contains the position of the cursor within the parsed {@link Expression}
	 * @param actualQuery The actual query is the text version of the query that may contain extra
	 * whitespace and different formatting than the trim down version generated by the parsed tree
	 */
	public ContentAssistVisitor(IQuery query,
	                            JPQLExpression jpqlExpression,
	                            JPQLQueryContext queryContext,
	                            DefaultContentAssistItems items,
	                            QueryPosition queryPosition) {

		super();
		initialize(query, jpqlExpression, queryContext, items, queryPosition);
	}

	/**
	 * Adds the abstract schema names as possible content assist items but will be filtered using
	 * the current word.
	 */
	private void addAbstractSchemaNames() {
		for (Iterator<String> iter = query.getProvider().entityNames(); iter.hasNext(); ) {
			String abstractSchemaName = iter.next();
			if (isValidChoice(abstractSchemaName, word)) {
				items.addAbstractSchemaName(abstractSchemaName);
			}
		}
	}

	private void addAllIdentificationVariables(Expression expression) {
		addIdentificationVariables(IdentificationVariableType.ALL, expression);
	}

	private void addAllPossibleAggregates(JPQLQueryBNF queryBNF) {
		for (Iterator<String> iter = identifiers(queryBNF); iter.hasNext(); ) {
			addPossibleAggregate(iter.next());
		}
	}

	private void addAllPossibleAggregates(String queryBNFId) {
		addAllPossibleAggregates(queryBNF(queryBNFId));
	}

	private void addAllPossibleClauses(JPQLQueryBNF queryBNF) {
		for (Iterator<String> iter = identifiers(queryBNF); iter.hasNext(); ) {
			addPossibleClause(iter.next());
		}
	}

	private void addAllPossibleCompounds(JPQLQueryBNF queryBNF) {
		for (Iterator<String> iter = identifiers(queryBNF); iter.hasNext(); ) {
			addPossibleCompound(iter.next());
		}
	}

	private void addAllPossibleCompounds(String queryBNFId) {
		addAllPossibleCompounds(queryBNF(queryBNFId));
	}

	private void addAllPossibleFunctions(JPQLQueryBNF queryBNF) {
		addAllPossibleFunctions(queryBNF, queryPosition.getPosition());
	}

	private void addAllPossibleFunctions(JPQLQueryBNF queryBNF, int position) {
		for (Iterator<String> iter = identifiers(queryBNF); iter.hasNext(); ) {
			addPossibleFunction(iter.next(), position);
		}
	}

	private void addAllPossibleFunctions(String queryBNFId) {
		addAllPossibleFunctions(queryBNF(queryBNFId), queryPosition.getPosition());
	}

	private void addAllPossibleIdentifiers(JPQLQueryBNF queryBNF) {
		for (Iterator<String> iter = identifiers(queryBNF); iter.hasNext(); ) {
			addPossibleChoice(iter.next());
		}
	}

	private void addAllPossibleIdentifiers(String queryBNFId) {
		addAllPossibleIdentifiers(queryBNF(queryBNFId));
	}

	private void addIdentificationVariables(IdentificationVariableType type, Expression expression) {

		// Nothing to do
		if (type == IdentificationVariableType.NONE) {
			return;
		}

		// Locate the declaration expressions
		DeclarationExpressionLocator locator = new DeclarationExpressionLocator();
		expression.accept(locator);

		IdentificationVariableFinder visitor = new IdentificationVariableFinder(type, expression);

		// First retrieve visit the expression and gather the identification variables
		for (Expression declaration : locator.declarationExpresions()) {
			declaration.accept(visitor);
			visitor.complete = false;
		}

		for (String identificationVariable : visitor.identificationVariables) {

			// If the identification variable matches the word, then it's added
			if (isValidChoice(identificationVariable, word)) {
				items.addIdentificationVariable(identificationVariable);

				// Now find the abstract schema name associated with the identification variable, if
				// it's a range identification variable and register the association
				String abstractSchemaName = visitor.rangeIdentificationVariables.get(identificationVariable);

				if (abstractSchemaName != null) {
					items.addRangeIdentificationVariable(identificationVariable, abstractSchemaName);
				}
			}
		}
	}

	private void addJoinIdentifiers() {
		items.addIdentifier(INNER_JOIN);
		items.addIdentifier(INNER_JOIN_FETCH);
		items.addIdentifier(JOIN);
		items.addIdentifier(JOIN_FETCH);
		items.addIdentifier(LEFT_JOIN);
		items.addIdentifier(LEFT_JOIN_FETCH);
		items.addIdentifier(LEFT_OUTER_JOIN);
		items.addIdentifier(LEFT_OUTER_JOIN_FETCH);
	}

	private void addLeftIdentificationVariables(Expression expression) {
		addIdentificationVariables(IdentificationVariableType.LEFT, expression);
	}

	private void addPossibleAggregate(String choice) {
		if (isAggregate(choice)) {
			addPossibleChoice(choice);
		}
	}

	private void addPossibleChoice(String choice) {
		addPossibleChoice(choice, word);
	}

	private void addPossibleChoice(String identifier, String word) {

		if (isValidChoice(identifier, word) &&
		    isValidVersion(identifier)) {

			items.addIdentifier(identifier);
		}
	}

	private void addPossibleClause(String identifier) {
		if (isClause(identifier)) {
			addPossibleChoice(identifier);
		}
	}

	private void addPossibleCompound(String choice) {
		if (isCompoundFunction(choice)) {
			addPossibleChoice(choice);
		}
	}

	private void addPossibleFunction(String choice, int position) {

		if (isFunction(choice)) {
			String currentWord = word;
			int endPosition = position - word.length();

			// Check to see if the previous words are "IS", "IS NOT" and "NOT",
			// they are special case since the only allowed identifiers are
			// those starting with them
			if (wordParser.endsWith(endPosition, "IS NOT")) {
				currentWord = new StringBuilder("IS NOT ").append(word).toString();
			}
			else if (wordParser.endsWith(endPosition, "IS")) {
				currentWord = new StringBuilder("IS ").append(word).toString();
			}
			else if (wordParser.endsWith(endPosition, "NOT")) {
				currentWord = new StringBuilder("NOT ").append(word).toString();
			}
			else {
				addPossibleChoice(choice);
				currentWord = null;
			}

			if (currentWord != null                &&
			    isValidChoice(choice, currentWord) &&
			    isValidVersion(choice)) {

				items.addIdentifier(choice);
			}
		}
	}

	private void addScalarExpressionChoices(Expression expression) {
		addAllIdentificationVariables(expression);
		addAbstractSchemaNames();
		addAllPossibleFunctions(ScalarExpressionBNF.ID);
	}

	private void addSelectExpressionChoices(AbstractSelectClause expression, int length) {

		int position = position(expression) - corrections.peek();

		// Now check for inside the select expression
		CollectionExpression collectionExpression = collectionExpression(expression.getSelectExpression());

		if (collectionExpression != null) {

			for (int index = 0, count = collectionExpression.childrenSize(); index < count; index++) {
				Expression child = collectionExpression.getChild(index);

				// At the beginning of the child expression
				if (position == length) {
					addAllIdentificationVariables(expression);
					addAllPossibleFunctions(SelectItemBNF.ID);
					break;
				}
				else {
					boolean withinChild = addSelectExpressionChoices(child, length, index, index + 1 == count);

					if (withinChild) {
						break;
					}
				}

				length += length(child);

				if (collectionExpression.hasComma(index)) {
					length++;
				}

				// After ',', the choices can be added
				if (position == length) {
					addAllIdentificationVariables(expression);
					addAllPossibleFunctions(SelectItemBNF.ID);
					break;
				}

				if (collectionExpression.hasSpace(index)) {
					length++;
				}
			}
		}
		else {
			addSelectExpressionChoices(expression.getSelectExpression(), length, 0, true);
		}
	}

	private boolean addSelectExpressionChoices(Expression expression,
	                                           int length,
	                                           int index,
	                                           boolean last) {

		int position = position(expression) - corrections.peek();

		// At the beginning of the child expression
		if (position > 0) {

			if (position == 0) {
				if (index == 0) {
					addPossibleChoice(DISTINCT);
				}
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(SelectItemBNF.ID);
			}
			else {
				int childLength = length(expression);

				// At the end of the child expression
				if ((position == length + childLength + virtualSpaces.peek() ||
				     position == childLength) &&
				    isSelectExpressionComplete(expression)) {

					// Choices cannot be added if the expression is a result variable
					ResultVariableVisitor resultVariableVisitor = new ResultVariableVisitor();
					expression.accept(resultVariableVisitor);

					if (resultVariableVisitor.expression == null) {

						// There is a "virtual" space after the expression, we can add "AS"
						// or the cursor is at the end of the child expression
						if ((virtualSpaces.peek() > 0) || (position == childLength)) {
							items.addIdentifier(AS);
						}

						addAllPossibleAggregates(SelectItemBNF.ID);
					}

					return true;
				}
			}
		}

		return false;
	}

	private AppendableExpressionVisitor appendableExpressionVisitor() {
		if (appendableExpressionVisitor == null) {
			appendableExpressionVisitor = new AppendableExpressionVisitor();
		}
		return appendableExpressionVisitor;
	}

	private CollectionExpression collectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = collectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	private CollectionExpressionVisitor collectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = new CollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	private CompletenessVisitor completenessVisitor() {
		if (completenessVisitor == null) {
			completenessVisitor = new TrailingCompletenessVisitor();
		}
		return completenessVisitor;
	}

	private ConstrutorCollectionHelper constructorCollectionHelper() {
		if (construtorCollectionHelper == null) {
			construtorCollectionHelper = new ConstrutorCollectionHelper();
		}
		return construtorCollectionHelper;
	}

	private ClauseHelper<DeleteClause> deleteClauseHelper() {
		if (deleteClauseHelper == null) {
			deleteClauseHelper = new DeleteClauseHelper();
		}
		return deleteClauseHelper;
	}

	private DoubleEncapsulatedCollectionHelper doubleEncapsulatedCollectionHelper() {
		if (doubleEncapsulatedCollectionHelper == null) {
			doubleEncapsulatedCollectionHelper = new DoubleEncapsulatedCollectionHelper();
		}
		return doubleEncapsulatedCollectionHelper;
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
				String text = child.toParsedText();

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

	private CollectionExpressionHelper<AbstractFromClause> fromClauseCollectionHelper() {
		if (fromClauseCollectionHelper == null) {
			fromClauseCollectionHelper = new FromClauseCollectionHelper();
		}
		return fromClauseCollectionHelper;
	}

	private ClauseHelper<AbstractFromClause> fromClauseHelper() {
		if (fromClauseHelper == null) {
			fromClauseHelper = new FromClauseHelper();
		}
		return fromClauseHelper;
	}

	private GroupByClauseCollectionHelper groupByClauseCollectionHelper() {
		if (groupByClauseCollectionHelper == null) {
			groupByClauseCollectionHelper = new GroupByClauseCollectionHelper();
		}
		return groupByClauseCollectionHelper;
	}

	private ClauseHelper<HavingClause> havingClauseHelper() {
		if (havingClauseHelper == null) {
			havingClauseHelper = new HavingClauseHelper();
		}
		return havingClauseHelper;
	}

	private Iterator<String> identifiers(JPQLQueryBNF queryBNF) {
		return queryBNF.identifiers();
	}

	private void initialize(IQuery query,
	                        JPQLExpression jpqlExpression,
	                        JPQLQueryContext queryContext,
	                        DefaultContentAssistItems items,
	                        QueryPosition queryPosition) {

		this.items                       = items;
		this.query                       = query;
		this.queryContext                = queryContext;
		this.queryPosition               = queryPosition;
		this.lockedExpressions           = new Stack<Expression>();

		virtualSpaces = new Stack<Integer>();
		virtualSpaces.add(0);

		positionInCollections = new Stack<Integer>();
		positionInCollections.add(-1);

		wordParser = new WordParser(jpqlExpression.toParsedText());
		wordParser.setPosition(queryPosition.getPosition());
		word = wordParser.partialWord();

		corrections = new Stack<Integer>();
		corrections.add(0);
	}

	private boolean isAggregate(String choice) {
		return identifierRole(choice) == IdentifierRole.AGGREGATE;
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

	private boolean isClause(String identifier) {
		return JPQLExpression.identifierRole(identifier) == IdentifierRole.CLAUSE;
	}

	private boolean isCompoundFunction(String choice) {
		return identifierRole(choice) == IdentifierRole.COMPOUND_FUNCTION;
	}

	private boolean isExpressionComplete(Expression expression) {
		CompletenessVisitor visitor = completenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
	}

	private boolean isFunction(String choice) {
		return identifierRole(choice) == IdentifierRole.FUNCTION;
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

	private boolean isSelectExpressionComplete(Expression expression) {
		SelectClauseCompletenessVisitor visitor = selectClauseCompletenessVisitor();
		try {
			expression.accept(visitor);
			return visitor.complete;
		}
		finally {
			visitor.complete = false;
		}
	}

	private boolean isValidChoice(String choice, String word) {

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

		// The word is longer than the choice
		if (word.length() > choice.length()) {
			return false;
		}

		// Check to see if the choice starts with the word
		for (int index = 0, length = word.length(); index < length; index++) {
			char character1 = choice.charAt(index);
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

	private boolean isValidVersion(String identifier) {
		IJPAVersion identifierVersion = JPQLExpression.identifierVersion(identifier);
		return version().isNewerThanOrEqual(identifierVersion);
	}

	private JoinCollectionHelper joinCollectionHelper() {
		if (joinCollectionHelper == null) {
			joinCollectionHelper = new JoinCollectionHelper();
		}
		return joinCollectionHelper;
	}

	private int length(Expression expression) {
		return expression.toParsedText().length();
	}

	private OrderByClauseCollectionHelper orderByClauseCollectionHelper() {
		if (orderByClauseCollectionHelper == null) {
			orderByClauseCollectionHelper = new OrderByClauseCollectionHelper();
		}
		return orderByClauseCollectionHelper;
	}

	private int position(Expression expression) {
		return queryPosition.getPosition(expression);
	}

	private int position(Expression expression, char character) {
		return expression.toParsedText().indexOf(character);
	}

	private SelectClauseCompletenessVisitor selectClauseCompletenessVisitor() {
		if (selectClauseCompletenessVisitor == null) {
			selectClauseCompletenessVisitor = new SelectClauseCompletenessVisitor();
		}
		return selectClauseCompletenessVisitor;
	}

	private TripleEncapsulatedCollectionHelper tripleEncapsulatedCollectionHelper() {
		if (tripleEncapsulatedCollectionHelper == null) {
			tripleEncapsulatedCollectionHelper = new TripleEncapsulatedCollectionHelper();
		}
		return tripleEncapsulatedCollectionHelper;
	}

	private UpdateItemCollectionHelper updateItemCollectionHelper() {
		if (updateItemCollectionHelper == null) {
			updateItemCollectionHelper = new UpdateItemCollectionHelper();
		}
		return updateItemCollectionHelper;
	}

	private IJPAVersion version() {
		return query.getProvider().getVersion();
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
		addAbstractSchemaNames();
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
			addAllPossibleFunctions(expression.getQueryBNF());
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

		// Within "NOT BETWEEN"
		if (expression.hasNot() && isPositionWithin(position, length, NOT_BETWEEN)) {
			items.addIdentifier(NOT_BETWEEN);
		}
		// Within "BETWEEN"
		else if (!expression.hasNot() && isPositionWithin(position, length, BETWEEN)) {
			items.addIdentifier(BETWEEN);
		}
		// After "BETWEEN "
		else if (expression.hasSpaceAfterBetween()) {
			length += expression.getIdentifier().length() + SPACE_LENGTH;

			// TODO: Check for the BETWEEN's expression type
			// Right after "BETWEEN "
			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(InternalBetweenExpressionBNF.ID);
			}

			if (expression.hasLowerBoundExpression()) {
				length += length(expression.getLowerBoundExpression());

				if (expression.hasSpaceAfterLowerBound()) {
					length++;

					// At the end of the lower bound expression, AND is the only viable choice
					if (isPositionWithin(position, length, AND)) {
						addPossibleChoice(AND);
					}

					if (expression.hasAnd() &&
					    expression.hasSpaceAfterAnd()) {

						length += AND.length() + SPACE_LENGTH;

						// TODO: Check for the BETWEEN's expression type
						if (position == length) {
							addAllIdentificationVariables(expression);
							addAllPossibleFunctions(InternalBetweenExpressionBNF.ID);
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
				items.addIdentifier(CASE);
			}
		}
		// After "CASE "
		else if (expression.hasSpaceAfterCase()) {
			int length = CASE.length() + SPACE_LENGTH;

			// Right after "CASE "
			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(CaseOperandBNF.ID);
				items.addIdentifier(WHEN);
			}

			// After "<case operand> "
			if (expression.hasCaseOperand() &&
			    expression.hasSpaceAfterCaseOperand()) {

				length += length(expression.getCaseOperand()) + SPACE_LENGTH;

				// Right after "<case operand> "
				if (position == length) {
					items.addIdentifier(WHEN);
				}
			}

			// After "<when clauses> "
			if (expression.hasWhenClauses() &&
			    expression.hasSpaceAfterWhenClauses()) {

				length += length(expression.getWhenClauses()) + SPACE_LENGTH;

				// Right after "<when clauses> "
				if (isPositionWithin(position, length, ELSE)) {
					items.addIdentifier(ELSE);
				}

				// After "ELSE "
				if (expression.hasElse() &&
				    expression.hasSpaceAfterElse()) {

					length += ELSE.length() + SPACE_LENGTH;

					// Right after "ELSE "
					if (position == length) {
						addAllIdentificationVariables(expression);
						addAllPossibleFunctions(CaseOperandBNF.ID);
					}

					// After "<else expression> "
					if (expression.hasElseExpression() &&
					    expression.hasSpaceAfterElseExpression()) {

						length += length(expression.getElseExpression()) + SPACE_LENGTH;

						// Right after "<else expression> "
						if (isPositionWithin(position, length, END)) {
							items.addIdentifier(END);
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
			items.addIdentifier(IN);
		}

		// In a subquery only
		SubqueryVisitor visitor = new SubqueryVisitor();
		expression.accept(visitor);

		// After "IN "
		if ((visitor.expression != null) && expression.hasSpaceAfterIn()) {
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
				addAllPossibleFunctions(CollectionValuedPathExpressionBNF.ID);
			}

			// After "<collection-valued path expression>)"
			if (expression.hasRightParenthesis()) {
				length += length(expression.getCollectionValuedPathExpression()) + 1 /* ')' */;

				// Right after "<collection-valued path expression>)"
				if ((position == length) && !expression.hasSpaceAfterRightParenthesis()) {
					items.addIdentifier(AS);
				}

				if (expression.hasSpaceAfterRightParenthesis()) {
					length++;
				}

				// Within "AS"
				if (isPositionWithin(position, length, AS)) {
					items.addIdentifier(AS);
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
			items.addIdentifier(NOT_MEMBER);
			items.addIdentifier(NOT_MEMBER_OF);
			items.addIdentifier(MEMBER);
			items.addIdentifier(MEMBER_OF);
		}
		// After the <identifier>
		else if (expression.hasOf() && expression.hasSpaceAfterOf() ||
		        !expression.hasOf() && expression.hasSpaceAfterMember()) {

			length += identifier.length() + SPACE_LENGTH;

			// Right after the <identifier>
			if (position == length) {
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
			items.addIdentifier(LOWER_THAN);
			items.addIdentifier(LOWER_THAN_OR_EQUAL);
			items.addIdentifier(DIFFERENT);
			items.addIdentifier(EQUAL);
			items.addIdentifier(GREATER_THAN);
			items.addIdentifier(GREATER_THAN_OR_EQUAL);
		}
		// After the comparison operator
		else {
			length += expression.getComparisonOperator().length();

			if (expression.hasSpaceAfterIdentifier()) {
				length++;
			}

			// Right after the comparison operator
			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(expression.rightExpressionBNF());
				addAllPossibleClauses(expression.rightExpressionBNF());
			}
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
			items.addIdentifier(NEW);
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
					addAllPossibleFunctions(ConstructorItemBNF.ID);
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

			items.addIdentifier(CURRENT_DATE);
			items.addIdentifier(CURRENT_TIME);
			items.addIdentifier(CURRENT_TIMESTAMP);
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
			items.addIdentifier(IS_EMPTY);
			items.addIdentifier(IS_NOT_EMPTY);
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
		addAbstractSchemaNames();
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
				visitCollectionExpression(expression, EMPTY_STRING, joinCollectionHelper());
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
		super.visit(expression);
		int position = position(expression) - corrections.peek();
		int length = 0;

		if (expression.hasExpression()) {
			length += length(expression.getExpression()) + SPACE_LENGTH;
		}

		// Within "IN"
		if (isPositionWithin(position, length, expression.getIdentifier())) {
			items.addIdentifier(IN);
			items.addIdentifier(NOT_IN);
		}
		// After "IN("
		else if (expression.hasLeftParenthesis()) {
			length += expression.getIdentifier().length() + SPACE_LENGTH;

			// Right after "IN("
			if (position == length) {
				addAllPossibleFunctions(InItemBNF.ID);
				items.addIdentifier(SELECT);
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

			// Add join identifiers
			items.addIdentifier(JOIN);
			items.addIdentifier(INNER_JOIN);
			items.addIdentifier(LEFT_JOIN);
			items.addIdentifier(LEFT_OUTER_JOIN);

			// Only add the join fetch identifiers if there is no AS or identification variable
			// otherwise the expression would become invalid
			if (!expression.hasAs() &&
			    !expression.hasIdentificationVariable()) {

				items.addIdentifier(JOIN_FETCH);
				items.addIdentifier(INNER_JOIN_FETCH);
				items.addIdentifier(LEFT_JOIN_FETCH);
				items.addIdentifier(LEFT_OUTER_JOIN_FETCH);
			}
		}
		// After "<join> "
		else if (expression.hasSpaceAfterJoin()) {
			int length = identifier.length() + SPACE_LENGTH;

			// Right after "<join> "
			if (position == length) {
				addLeftIdentificationVariables(expression);
			}

			// After "join association path expression "
			if (expression.hasJoinAssociationPath() &&
			    expression.hasSpaceAfterJoinAssociation()) {

				length += length(expression.getJoinAssociationPath()) + SPACE_LENGTH;

				// Right after "join association path expression "
				if (isPositionWithin(position, length, AS)) {
					addPossibleChoice(AS);
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
				addPossibleChoice(SELECT);
				addPossibleChoice(UPDATE);
				addPossibleChoice(DELETE_FROM);
			}
			// After the query, inside the invalid query (or ending whitespace)
			else if (position > length) {

				String text = expression.getUnknownEndingStatement().toParsedText();

				addPossibleChoice(SELECT,      text);
				addPossibleChoice(DELETE_FROM, text);
				addPossibleChoice(UPDATE,      text);

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

		int position = position(expression) - corrections.peek();;

		// Within the identifier
		if (isPositionWithin(position, TRUE)  ||
		    isPositionWithin(position, FALSE) ||
		    isPositionWithin(position, NULL)) {

			items.addIdentifier(TRUE);
			items.addIdentifier(FALSE);
			items.addIdentifier(NULL);
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
			items.addIdentifier(LIKE);
			items.addIdentifier(NOT_LIKE);
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
					items.addIdentifier(ESCAPE);
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
			items.addIdentifier(NOT);

			// Also add the negated JPQL identifiers
			if (!expression.hasExpression()) {
				items.addIdentifier(NOT_BETWEEN);
				items.addIdentifier(NOT_EXISTS);
				items.addIdentifier(NOT_IN);
				items.addIdentifier(NOT_LIKE);
				items.addIdentifier(NOT_MEMBER);
				items.addIdentifier(NOT_MEMBER_OF);
			}
		}
		// After "NOT "
		else if (expression.hasSpaceAfterNot()) {
			int length = NOT.length() + SPACE_LENGTH;

			// Right after "NOT "
			if (position == length) {
				items.addIdentifier(NOT_BETWEEN);
				items.addIdentifier(NOT_EXISTS);
				items.addIdentifier(NOT_IN);
				items.addIdentifier(NOT_LIKE);
				items.addIdentifier(NOT_MEMBER);
				items.addIdentifier(NOT_MEMBER_OF);
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
		if (isPositionWithin(position, length, expression.getIdentifier().name())) {
			items.addIdentifier(IS_NULL);
			items.addIdentifier(IS_NOT_NULL);
		}
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
					items.addIdentifier(ASC);
					items.addIdentifier(DESC);
				}
				else {
					// Right after the space
					Ordering ordering = expression.getOrdering();

					// Within "ASC" or "DESC"
					if ((ordering != Ordering.DEFAULT) &&
					    isPositionWithin(position, length, ordering.name())) {

						items.addIdentifier(ASC);
						items.addIdentifier(DESC);
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
				addPossibleChoice(AS);
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
			addPossibleChoice(AS);
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
			visitSelectStatement(expression);
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
			// will handle all the possible choices
			visitSelectStatement(expression);
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
		super.visit(expression);
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
				items.addIdentifier(TREAT);
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
			if (expression.hasExpression() &&
			    expression.hasSpaceAfterExpression()) {

				length += length(expression.getExpression()) + SPACE_LENGTH;

				// Within "AS"
				if (isPositionWithin(position, length, AS)) {
					items.addIdentifier(AS);

					// If the entity type is not specified, then we can add
					// the possible abstract schema names
					if (!expression.hasEntityType()) {
						// TODO: Filter to only have the valid abstract schema names
						addAbstractSchemaNames();
					}
				}
			}

			// After "AS "
			if (expression.hasAs() &&
			    expression.hasSpaceAfterAs()) {

				length += AS.length() + SPACE_LENGTH;

				// Right after "AS "
				if (position == length) {
					// TODO: Filter to only have the valid abstract schema names
					addAbstractSchemaNames();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {
		// TODO: Rewrite to use length instead of StringBuilder
		super.visit(expression);
		int position = position(expression);

		// TRIM
		if (isPositionWithin(position, TRIM)) {
			items.addIdentifier(TRIM);
		}
		// After '('
		else if (position - corrections.peek() == position(expression, LEFT_PARENTHESIS) + SPACE_LENGTH) {
			addPossibleChoice(BOTH);
			addPossibleChoice(LEADING);
			addPossibleChoice(TRAILING);
			addAllIdentificationVariables(expression);
			addAllPossibleFunctions(StringPrimaryBNF.ID);
		}
		else {
			StringBuilder writer = new StringBuilder();

			// 'TRIM'
			writer.append(TRIM);

			// '('
			if (expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// Trim specification
			if (expression.hasSpecification()) {
				writer.append(expression.getSpecification().name());
			}

			if (expression.hasSpaceAfterSpecification()) {
				writer.append(SPACE);
			}

			// Trim character
			if (expression.hasTrimCharacter()) {
				writer.append(expression.getTrimCharacter());
			}

			if (expression.hasSpaceAfterTrimCharacter()) {
				writer.append(SPACE);
			}

			// After the character
			if (position - corrections.peek() == writer.length()) {
				addPossibleChoice(FROM);

				if (!expression.hasFrom()) {
					addAllIdentificationVariables(expression);
					addAllPossibleFunctions(StringPrimaryBNF.ID);
				}
			}

			if (expression.hasFrom()) {
				writer.append(FROM);
			}

			if (expression.hasSpaceAfterFrom()) {
				writer.append(SPACE);
			}

			// After the FROM
			if (position - corrections.peek() == writer.length()) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(StringPrimaryBNF.ID);
			}

			if (expression.hasExpression()) {
				writer.append(expression.getExpression());

				if (position - corrections.peek() - virtualSpaces.peek() == writer.length() &&
				    !expression.hasTrimCharacter() &&
				    !expression.hasFrom()) {

					addPossibleChoice(FROM);
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
			items.addIdentifier(UPDATE);
		}
		// After "UPDATE "
		else if (expression.hasSpaceAfterUpdate()) {
			int length = UPDATE.length() + SPACE_LENGTH;

			// Right after "UPDATE "
			if (position == length) {
				addAbstractSchemaNames();
			}
			// After "<range variable declaration> "
			else if (expression.hasRangeVariableDeclaration()) {

				RangeVariableDeclarationVisitor visitor = new RangeVariableDeclarationVisitor();
				expression.getRangeVariableDeclaration().accept(visitor);

				if ((visitor.expression != null) &&
				     visitor.expression.hasAbstractSchemaName() &&
				     visitor.expression.hasSpaceAfterAbstractSchemaName()) {

					length += length(visitor.expression.getAbstractSchemaName()) + SPACE_LENGTH;

					// Example: "UPDATE System s"
					if (!expression.hasSet()        &&
					    !visitor.expression.hasAs() &&
					    isPositionWithin(position, length, SET)) {

						addPossibleChoice(SET);
					}
					// Example: "UPDATE System s "
					// Example: "UPDATE System AS s "
					else {

						if (visitor.expression.hasAs()) {
							length += 2;
						}

						if (visitor.expression.hasSpaceAfterAs()) {
							length++;
						}

						if (visitor.expression.hasIdentificationVariable()) {
							length += length(visitor.expression.getIdentificationVariable());
						}

						if (expression.hasSpaceAfterRangeVariableDeclaration()) {
							length++;
						}

						// Within "SET"
						if ((visitor.expression.hasAs() && visitor.expression.hasIdentificationVariable() ||
						    !visitor.expression.hasAs() && visitor.expression.hasIdentificationVariable()) &&
						    isPositionWithin(position, length, SET)) {

							addPossibleChoice(SET);
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
								visitCollectionExpression(expression, EMPTY_STRING, updateItemCollectionHelper());
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
				items.addIdentifier(EQUAL);
			}
			// After "="
			else if (expression.hasEqualSign()) {
				length++;

				// Right after "="
				if (position == length) {
					items.addIdentifier(EQUAL);
					addAllIdentificationVariables(expression);
					addAllPossibleFunctions(NewValueBNF.ID);
				}
				else if (expression.hasSpaceAfterEqualSign()) {
					length++;

					// Right after "= "
					if (position == length) {
						addAllIdentificationVariables(expression);
						addAllPossibleFunctions(NewValueBNF.ID);
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
			items.addIdentifier(WHEN);
		}
		// After "WHEN "
		else if (expression.hasSpaceAfterWhen()) {
			int length = WHEN.length() + SPACE_LENGTH;

			// Right after "WHEN "
			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(InternalWhenClauseBNF.ID);
			}
			else {
				length += length(expression.getWhenExpression());

				// After "WHEN <expression> " => THEN
				if (expression.hasSpaceAfterWhenExpression()) {
					length++;

					// Right after "WHEN <expression> " => THEN
					if (position == length) {
						items.addIdentifier(THEN);
					}
					else if (expression.hasThen()) {
						// Within "THEN"
						if (isPositionWithin(position, length, THEN)) {
							items.addIdentifier(THEN);
						}
						else {
							length += THEN.length();

							// After "WHEN <expression> THEN "
							if (expression.hasSpaceAfterThen()) {
								length++;

								// Right after "WHEN <expression> THEN "
								if (position == length) {
									addScalarExpressionChoices(expression);
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
		}
	}

	private void visitAggregateFunction(AggregateFunction expression) {

		int position = position(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within "<identifier>"
		if (isPositionWithin(position, identifier)) {
			items.addIdentifier(identifier);
		}
		// After "<identifier>("
		else if (expression.hasLeftParenthesis()) {
			int length = identifier.length() + 1 /* '(' */;
			boolean hasDistinct = expression.hasDistinct();

			// Within "DISTINCT"
			if (hasDistinct && isPositionWithin(position, length, DISTINCT) ) {
				addPossibleChoice(DISTINCT);
			}
			// After "("
			else {
				if (hasDistinct && expression.hasSpaceAfterDistinct()) {
					length += DISTINCT.length() + SPACE_LENGTH;
				}

				// Right after "(" or right after "(DISTINCT "
				if (position == length) {
					if (!hasDistinct) {
						addPossibleChoice(DISTINCT);
					}
					addAllIdentificationVariables(expression);
					addAllPossibleFunctions(expression.encapsulatedExpressionBNF());
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
			addAllPossibleAggregates(expression.getQueryBNF());
		}
		// After the arithmetic sign, with or without the space
		else if (expression.hasSpaceAfterIdentifier()) {
			length += 2;

			// Right after the space
			if ((position == length) && (positionInCollections.peek() == -1)) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(expression.rightExpressionBNF(), position);
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
			items.addIdentifier(identifier);
		}
		// After "<identifier> "
		else if (hasSpaceAfterIdentifier) {
			int length = identifier.length() + SPACE_LENGTH;

			// Right after "<identifier> "
			if (position == length) {
				helper.addChoices(expression);
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
					if (isExpressionComplete(clauseExpression)) {
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
	 * Adds the possible choices for the given {@link Expression expression} based on the location of
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
			items.addIdentifier(identifier);
		}
		// After "<identifier>(" or "<identifier> "
		else if (helper.hasDelimiterAfterIdentifier(expression)) {
			int length = identifier.length() + 1 /* delimiter, either space or ( */;
			length += helper.preExpressionLength(expression);

			// Right after "<identifier>(" or "<identifier> "
			if (position == length) {
				helper.addChoices(expression, 0);
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
						helper.addChoices(expression, index);
						break;
					}
					else {
						childLength = length(child);

						// At the end of the child expression
						if ((position == length + childLength + virtualSpaces.peek()) &&
						     isExpressionComplete(child)) {

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

						// After ',', the choices can be added
						if (position == length) {
							helper.addChoices(expression, index + 1);
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

	private void visitDeleteStatement(DeleteStatement expression) {

		lockedExpressions.add(expression);
		int position = position(expression);

		//
		// DELETE clause
		//
		DeleteClause deleteClause = expression.getDeleteClause();
		int length = length(deleteClause);

		// At the end of the DELETE clause, check for adding choices based
		// on possible incomplete information
		if ((position == length) && isAppendable(deleteClause)) {
			addPossibleChoice(WHERE);
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
			    isExpressionComplete(deleteClause.getRangeVariableDeclaration())) {

				addPossibleChoice(WHERE);
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
	 * Adds the possible choices for the given {@link AbstractEncapsulatedExpression expression}
	 * based on the location of the cursor and the content of the expression.
	 *
	 * @param expression The {@link AbstractEncapsulatedExpression expression} being visited
	 * @param identifier
	 * @param jpqlQueryBNF
	 */
	private void visitEncapsulatedExpression(AbstractEncapsulatedExpression expression,
	                                         String identifier,
	                                         JPQLQueryBNF jpqlQueryBNF) {

		int position = position(expression) - corrections.peek();

		// Within the identifier
		if (isPositionWithin(position, identifier)) {
			items.addIdentifier(identifier);
		}
		// Right after "<identifier>("
		else if (expression.hasLeftParenthesis()) {
			int length = identifier.length() + 1 /* '(' */;

			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(jpqlQueryBNF);
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
			items.addIdentifier(identifier);
		}
		// After "AND " or "OR "
		else if (expression.hasSpaceAfterIdentifier()) {
			length += identifier.length() + SPACE_LENGTH;

			// Right after "AND " or "OR "
			if (position == length) {
				addAllIdentificationVariables(expression);
				addAllPossibleFunctions(expression.rightExpressionBNF());
			}
		}
	}

	private void visitPathExpression(AbstractPathExpression expression) {

		int position = position(expression);
		String text = expression.toParsedText();
		int dotIndex = text.indexOf(".");

		// Don't do anything if the cursor is after a dot, it's handled by another visitor
		if ((position > -1) && ((dotIndex == -1) || (position <= dotIndex))) {
			corrections.add(position(expression));
			super.visit(expression);
			corrections.pop();
		}
	}

	private void visitSelectClause(AbstractSelectClause expression) {

		int position = position(expression) - corrections.peek();

		// Within "SELECT"
		if (isPositionWithin(position, SELECT)) {
			items.addIdentifier(SELECT);
		}
		// After "SELECT "
		else if (expression.hasSpaceAfterSelect()) {
			int length = SELECT.length() + SPACE_LENGTH;

			// Within "DISTINCT"
			if (expression.hasDistinct() &&
			    isPositionWithin(position, length, DISTINCT)) {

				items.addIdentifier(DISTINCT);
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
						addPossibleChoice(DISTINCT);
					}
					addAllIdentificationVariables(expression);
					addAllPossibleFunctions(SelectItemBNF.ID);
				}
				// Somewhere in the clause's expression
				else {
					int selectExpressionLength = length(expression.getSelectExpression());

					// At the end of the clause's expression
					if (position <= length + selectExpressionLength + virtualSpaces.peek()) {
						addSelectExpressionChoices(expression, length);
					}
				}
			}
		}
	}

	private void visitSelectStatement(AbstractSelectStatement expression) {

		lockedExpressions.add(expression);

		try {
			int position = position(expression);

			//
			// SELECT clause
			//
			AbstractSelectClause selectClause = expression.getSelectClause();
			int length = length(selectClause);

			// At the end of the FROM clause, check for adding choices based
			// on possible incomplete information
			if ((position == length) && isAppendable(selectClause)) {
				addPossibleChoice(FROM);
			}
			// Right after the SELECT expression, the space is owned by the select statement
			else if ((position == length + SPACE_LENGTH) &&  expression.hasSpaceAfterSelect()) {

				virtualSpaces.add(SPACE_LENGTH);
				corrections.add(-length - 2);

				selectClause.accept(this);

				corrections.pop();
				virtualSpaces.pop();
			}

			// Nothing else to do
			if ((position < length) || (position == length) && !expression.hasSpaceAfterSelect()) {
				return;
			}

			if (expression.hasSpaceAfterSelect()) {
				length++;
			}

			// Nothing else to do
			if ((position < length) || (position == length) && !selectClause.hasSelectExpression()) {
				return;
			}

			//
			// FROM clause
			//
			// Right before "FROM"
			if (position == length) {

				if (expression.hasSpaceAfterSelect() &&
				    isSelectExpressionComplete(selectClause.getSelectExpression())) {

					addPossibleChoice(FROM);
				}

				return;
			}

			if (expression.hasFromClause()) {
				AbstractFromClause fromClause = (AbstractFromClause) expression.getFromClause();

				// Check for within the FROM clause
				if (position > length) {
					int fromClauseLenght = length(fromClause);
					length += fromClauseLenght;

					// At the end of the FROM clause, check for adding choices based
					// on possible incomplete information
					if ((position == length) && isAppendable(fromClause)) {
						addPossibleChoice(WHERE);

						if (!expression.hasWhereClause()) {
							addPossibleChoice(GROUP_BY);

							if (!expression.hasGroupByClause()) {
								addPossibleChoice(HAVING);

								if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
									addPossibleChoice(ORDER_BY);
								}
							}
						}
					}
					// Right after the FROM clause, the space is owned by the select statement
					else if ((position == length + SPACE_LENGTH) && expression.hasSpaceAfterFrom()) {

						virtualSpaces.add(SPACE_LENGTH);
						corrections.add(-fromClauseLenght - 2);

						fromClause.accept(this);

						corrections.pop();
						virtualSpaces.pop();

						// Now add the following clause identifiers
						if (isExpressionComplete(fromClause.getDeclaration())) {
							addPossibleChoice(WHERE);

							if (!expression.hasWhereClause()) {
								addPossibleChoice(GROUP_BY);

								if (!expression.hasGroupByClause()) {
									addPossibleChoice(HAVING);

									if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
										addPossibleChoice(ORDER_BY);
									}
								}
							}
						}
					}

					// Nothing else to do
					if ((position < length) || (position == length) && !expression.hasSpaceAfterFrom()) {
						return;
					}

					if (expression.hasSpaceAfterFrom()) {
						length++;
					}

					// Nothing else to do
					if ((position < length) || (position == length) && !fromClause.hasDeclaration()) {
						return;
					}

					// Right before "WHERE"
					if (position == length) {

						if (expression.hasSpaceAfterFrom() &&
						    isExpressionComplete(fromClause.getDeclaration())) {

							addPossibleChoice(WHERE);

							// Now add the following clause identifiers
							if (!expression.hasWhereClause()) {
								addPossibleChoice(GROUP_BY);

								if (!expression.hasGroupByClause()) {
									addPossibleChoice(HAVING);

									if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
										addPossibleChoice(ORDER_BY);
									}
								}
							}
						}

						return;
					}
				}
			}

			//
			// WHERE clause
			//
			if (expression.hasWhereClause()) {
				AbstractConditionalClause whereClause = (AbstractConditionalClause) expression.getWhereClause();

				// Check for within the WHERE clause
				if (position > length) {
					int whereClauseLength = length(whereClause);
					length += whereClauseLength;

					// At the end of the WHERE clause, check for adding choices based
					// on possible incomplete information
					if ((position == length) && isAppendable(whereClause)) {
						addPossibleChoice(GROUP_BY);

						if (!expression.hasGroupByClause()) {
							addPossibleChoice(HAVING);

							if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
								addPossibleChoice(ORDER_BY);
							}
						}
					}
					// Right after the WHERE clause, the space is owned by the select statement
					else if ((position == length + SPACE_LENGTH) && expression.hasSpaceAfterWhere()) {

						virtualSpaces.add(SPACE_LENGTH);
						corrections.add(-whereClauseLength - 2);

						whereClause.accept(this);

						corrections.pop();
						virtualSpaces.pop();

						// Now add the following clause identifiers
						if (isExpressionComplete(whereClause.getConditionalExpression())) {
							addPossibleChoice(GROUP_BY);

							if (!expression.hasGroupByClause()) {
								addPossibleChoice(HAVING);

								if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
									addPossibleChoice(ORDER_BY);
								}
							}
						}
					}
				}

				// Nothing else to do
				if ((position < length) || (position == length) && !expression.hasSpaceAfterWhere()) {
					return;
				}

				if (expression.hasSpaceAfterWhere()) {
					length++;
				}

				// Nothing else to do
				if ((position < length) || (position == length) && !whereClause.hasConditionalExpression()) {
					return;
				}

				// Right before "GROUP BY"
				if (position == length) {

					if (expression.hasSpaceAfterWhere() &&
					    isExpressionComplete(whereClause.getConditionalExpression())) {

						addPossibleChoice(GROUP_BY);

						// Now add the following clause identifiers
						if (!expression.hasGroupByClause()) {
							addPossibleChoice(HAVING);

							if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
								addPossibleChoice(ORDER_BY);
							}
						}
					}

					return;
				}
			}

			//
			// GROUP BY clause
			//
			if (expression.hasGroupByClause()) {
				GroupByClause groupByClause = (GroupByClause) expression.getGroupByClause();

				// Check for within the GROUP BY clause
				if (position > length) {
					int groupByClauseLength = length(groupByClause);
					length += groupByClauseLength;

					// At the end of the GROUP BY clause, check for adding choices based
					// on possible incomplete information
					if ((position == length) && isAppendable(groupByClause)) {
						addPossibleChoice(HAVING);

						if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
							addPossibleChoice(ORDER_BY);
						}
					}
					// Right after the GROUP BY clause, the space is owned by the select statement
					else if ((position == length + SPACE_LENGTH) && expression.hasSpaceAfterGroupBy()) {

						virtualSpaces.add(SPACE_LENGTH);
						corrections.add(-groupByClauseLength - 2);

						groupByClause.accept(this);

						corrections.pop();
						virtualSpaces.pop();

						// Now add the following clause identifiers
						if (isExpressionComplete(groupByClause.getGroupByItems())) {
							addPossibleChoice(HAVING);

							if (!expression.hasHavingClause()) {
								addPossibleChoice(ORDER_BY);
							}
						}
					}
				}

				// Nothing else to do
				if ((position < length) || (position == length) && !expression.hasSpaceAfterGroupBy()) {
					return;
				}

				if (expression.hasSpaceAfterGroupBy()) {
					length++;
				}

				// Nothing else to do
				if ((position < length) || (position == length) && !groupByClause.hasGroupByItems()) {
					return;
				}

				// Right before "HAVING"
				if (position == length) {

					if (expression.hasSpaceAfterGroupBy() &&
					    isExpressionComplete(groupByClause.getGroupByItems())) {

						// Now add the following clause identifiers
						addPossibleChoice(HAVING);

						if (!expression.hasHavingClause() && (expression instanceof SelectStatement)) {
							addPossibleChoice(ORDER_BY);
						}
					}

					return;
				}
			}

			//
			// HAVING clause
			//
			HavingClause havingClause = expression.hasHavingClause() ? (HavingClause) expression.getHavingClause() : null;

			if (havingClause != null) {

				// Check for within the HAVING clause
				if (position > length) {
					int havingClauseLength = length(havingClause);
					length += havingClauseLength;

					// Right after the HAVING clause, the space is owned by the select statement
					if (expression instanceof SelectStatement) {

						// At the end of the HAVING clause, check for adding choices based
						// on possible incomplete information
						if ((position == length) && isAppendable(havingClause)) {
							addPossibleChoice(ORDER_BY);
						}
						// Right after the HAVING clause, the space is owned by the select statement
						else if ((position == length + SPACE_LENGTH) && ((SelectStatement) expression).hasSpaceBeforeOrderBy()) {

							virtualSpaces.add(SPACE_LENGTH);
							corrections.add(-havingClauseLength - 2);

							havingClause.accept(this);

							corrections.pop();
							virtualSpaces.pop();

							// Now add the following clause identifiers
							if (isExpressionComplete(havingClause.getConditionalExpression())) {
								addPossibleChoice(ORDER_BY);
							}
						}
					}
				}
			}

			if (expression instanceof SelectStatement) {

				SelectStatement selectStatement = (SelectStatement) expression;

				if (havingClause != null) {
					// Nothing else to do
					if ((position < length) || (position == length) && !selectStatement.hasSpaceBeforeOrderBy()) {
						return;
					}

					if (selectStatement.hasSpaceBeforeOrderBy()) {
						length++;
					}

					// Nothing else to do
					if ((position < length) || (position == length) && !havingClause.hasConditionalExpression()) {
						return;
					}

					// Right before "ORDER BY"
					if (position == length) {

						if (selectStatement.hasSpaceBeforeOrderBy() &&
						    isExpressionComplete(havingClause.getConditionalExpression())) {

							addPossibleChoice(ORDER_BY);
						}

						return;
					}
				}

				//
				// ORDER BY clause
				//
				if (selectStatement.hasOrderByClause()) {
					OrderByClause orderByClause = (OrderByClause) selectStatement.getOrderByClause();

					// Check for within the ORDER BY clause
					if (position > length) {
						int orderByClauseLength = length(orderByClause);
						length += orderByClauseLength;

						// Right after the ORDER BY clause, the space is owned by the select statement
						if (position == length + SPACE_LENGTH) {

							virtualSpaces.add(SPACE_LENGTH);
							corrections.add(-orderByClauseLength - 2);

							orderByClause.accept(this);

							corrections.pop();
							virtualSpaces.pop();
						}
					}
				}
			}
		}
		finally {
			lockedExpressions.pop();
		}
	}

	/**
	 * Adds the possible choices for the given {@link AbstractSingleEncapsulatedExpression expression}
	 * based on the location of the cursor and the content of the expression.
	 *
	 * @param expression The {@link AbstractSingleEncapsulatedExpression expression} being visited
	 * @param identificationVariableType TODO
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
	 * Adds the possible choices for the given {@link AbstractSingleEncapsulatedExpression expression}
	 * based on the location of the cursor and the content of the expression.
	 *
	 * @param expression The {@link AbstractSingleEncapsulatedExpression expression} being visited
	 * @param identificationVariableType TODO
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
				items.addIdentifier(identifier);
			}
			// Right after "<identifier>("
			else if (expression.hasLeftParenthesis()) {
				int length = identifier.length() + 1 /* '(' */;

				if (!added && (position == length)) {
					added = true;

					addIdentificationVariables(identificationVariableType, expression);

					JPQLQueryBNF queryBNF = expression.encapsulatedExpressionBNF();
					addAllPossibleFunctions(queryBNF);
					addAllPossibleClauses(queryBNF);
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
			    isExpressionComplete(updateClause.getUpdateItems())) {

				addPossibleChoice(WHERE);
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
		if (whereClauseHelper == null) {
			whereClauseHelper = new WhereClauseHelper();
		}
		return whereClauseHelper;
	}

	private class AppendableExpressionVisitor extends AbstractTraverseChildrenVisitor {

		/**
		 *
		 */
		boolean appendable;

		/**
		 *
		 */
		int positionInCollection;

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
	 * This helper is responsible to add the choices
	 */
	private interface ClauseHelper<T extends Expression> {

		/**
		 * Adds
		 *
		 * @param expression The clause for which choices can be added after the expression
		 */
		void addAtTheEndOfExpression(T expression);

		/**
		 * Adds the possible choices.
		 *
		 * @param expression The clause for which choices can be added
		 */
		void addChoices(T expression);

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
		 * Adds
		 *
		 * @param expression
		 * @param child
		 * @param index
		 */
		void addAtTheEndOfChild(T expression, Expression child, int index);

		/**
		 * Adds
		 *
		 * @param expression
		 * @param index
		 */
		void addChoices(T expression, int index);

		/**
		 * Creates
		 *
		 * @param expression
		 * @return
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

	private class ConstrutorCollectionHelper implements CollectionExpressionHelper<ConstructorExpression> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(ConstructorExpression expression, Expression child, int index) {
			addAllPossibleAggregates(ConstructorItemBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(ConstructorExpression expression, int index) {
			addIdentificationVariables(IdentificationVariableType.ALL, expression);
			addAllPossibleFunctions(ConstructorItemBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(ConstructorExpression expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getConstructorItems());
			if (collectionExpression != null) {
				return collectionExpression;
			}
			return expression.buildCollectionExpression();
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
		public void addChoices(DeleteClause expression) {
			addAbstractSchemaNames();
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
				addAllPossibleAggregates(queryBNF(expression, index));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(AbstractDoubleEncapsulatedExpression expression, int index) {
			addAllIdentificationVariables(expression);
			addAllPossibleFunctions(queryBNF(expression, index));
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
		public void addChoices(AbstractFromClause expression, int index) {
			addAbstractSchemaNames();
			if (index > 0) {
				addPossibleChoice(IN);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(AbstractFromClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getDeclaration());
			if (collectionExpression != null) {
				return collectionExpression;
			}
			return expression.buildCollectionExpression();
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
		public void addChoices(AbstractFromClause expression) {

			addAbstractSchemaNames();

			// With the correction, check to see if the possible identifiers (IN)
			// can be added and only if it's not the first item in the list
			if (positionInCollections.peek() > 0) {
				addAllPossibleIdentifiers(InternalFromClauseBNF.ID);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(AbstractFromClause expression) {
			return expression.getDeclaration();
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
		public void addChoices(GroupByClause expression, int index) {
			addAllIdentificationVariables(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(GroupByClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getGroupByItems());
			if (collectionExpression != null) {
				return collectionExpression;
			}
			return expression.buildCollectionExpression();
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

	private class HavingClauseHelper implements ClauseHelper<HavingClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(HavingClause expression) {
			addAllPossibleAggregates(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(HavingClause expression) {
			addAllIdentificationVariables(expression);
			addAllPossibleFunctions(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(HavingClause expression) {
			return expression.getConditionalExpression();
		}
	}

	private class IdentificationVariableFinder extends AbstractTraverseChildrenVisitor {

		boolean complete;

		/**
		 * The expression used to stop gathering the identification variables, which is only used if
		 * the type is {@link IdentificationVariableType#LEFT} or {@link IdentificationVariableType#
		 * LEFT_RANGE}.
		 */
		private Expression expression;

		/**
		 * The identification variable names defined in the declaration expression.
		 */
		private final Set<String> identificationVariables;

		/**
		 * The identification variable names mapped to their abstract schema names.
		 */
		private final Map<String, String> rangeIdentificationVariables;

		/**
		 * The type of identification variables to retrieve, which helps to filter out those from a
		 * different declaration.
		 */
		private final IdentificationVariableType type;

		/**
		 * Creates a new <code>IdentificationVariableFinder</code>.
		 *
		 * @param type The type of identification variables to retrieve, which helps to filter out
		 * those from a different declaration
		 * @param expression The expression used to stop gathering the identification variables, which
		 * is only used if the type is {@link IdentificationVariableType#LEFT} or
		 * {@link IdentificationVariableType#LEFT_RANGE}
		 */
		IdentificationVariableFinder(IdentificationVariableType type, Expression expression) {
			super();
			this.type                         = type;
			this.expression                   = expression;
			this.identificationVariables      = new HashSet<String>();
			this.rangeIdentificationVariables = new HashMap<String, String>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (Iterator<Expression> iter = expression.children(); iter.hasNext(); ) {
				Expression child = iter.next();
				if (!complete) {
					child.accept(this);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {

			if (expression.isAncestor(this.expression) &&
			   (type == IdentificationVariableType.LEFT ||
			    type == IdentificationVariableType.LEFT_COLLECTION)) {

				complete = true;
			}
			else {
				String variableName = queryContext.variableName(
					expression.getIdentificationVariable(),
					VariableNameType.IDENTIFICATION_VARIABLE
				);

				if (ExpressionTools.stringIsNotEmpty(variableName)) {
					identificationVariables.add(variableName);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {
			expression.getRangeVariableDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {
			expression.getDeleteClause().accept(this);
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
		public void visit(IdentificationVariableDeclaration expression) {

			if (this.expression != expression) {
				expression.getRangeVariableDeclaration().accept(this);
				expression.getJoins().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			if (this.expression == expression &&
			    type != IdentificationVariableType.ALL) {

				complete = true;
			}
			else {
				String variableName = queryContext.variableName(
					expression.getIdentificationVariable(),
					VariableNameType.IDENTIFICATION_VARIABLE
				);

				if (ExpressionTools.stringIsNotEmpty(variableName)) {
					identificationVariables.add(variableName);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {

			if (type == IdentificationVariableType.ALL ||
			    type == IdentificationVariableType.LEFT) {

				String variableName = queryContext.variableName(
					expression.getIdentificationVariable(),
					VariableNameType.IDENTIFICATION_VARIABLE
				);

				if (ExpressionTools.stringIsNotEmpty(variableName)) {
					identificationVariables.add(variableName);

					String abstractSchemaName = queryContext.variableName(
						expression.getAbstractSchemaName(),
						VariableNameType.ABSTRACT_SCHEMA_NAME
					);

					if (ExpressionTools.stringIsNotEmpty(abstractSchemaName)) {
						rangeIdentificationVariables.put(variableName, abstractSchemaName);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression) {
			expression.getFromClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			expression.getDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			expression.getFromClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			expression.getRangeVariableDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {
			expression.getUpdateClause().accept(this);
		}
	}

	/**
	 * The various ways of retrieving identification variables from the declaration expression.
	 */
	private enum IdentificationVariableType {
		ALL,
		COLLECTION,
		LEFT,
		LEFT_COLLECTION,
		NONE
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
		public void addChoices(IdentificationVariableDeclaration expression, int index) {
			addJoinIdentifiers();
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(IdentificationVariableDeclaration expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getJoins());
			if (collectionExpression != null) {
				return collectionExpression;
			}
			return expression.buildCollectionExpression();
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

	private class OrderByClauseCollectionHelper implements CollectionExpressionHelper<OrderByClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfChild(OrderByClause expression, Expression child, int index) {
			addPossibleChoice(ASC);
			addPossibleChoice(DESC);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(OrderByClause expression, int index) {
			addAllIdentificationVariables(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(OrderByClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getOrderByItems());
			if (collectionExpression != null) {
				return collectionExpression;
			}
			return expression.buildCollectionExpression();
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

	private class RangeVariableDeclarationVisitor extends AbstractExpressionVisitor {

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
			if (child.isNull()) {
				complete = false;
			}
			else {
				int length = expression.toParsedText(positionInCollections.peek()).length();

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
			complete = expression.isJDBCDate() ? expression.toParsedText().endsWith("}") : true;
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

	private class SubqueryVisitor extends AbstractTraverseParentVisitor {

		private SimpleSelectStatement expression;

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
				complete = expression.toParsedText().endsWith("}");
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
				addAllPossibleAggregates(queryBNF(expression, index));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(AbstractTripleEncapsulatedExpression expression, int index) {
			addAllIdentificationVariables(expression);
			addAllPossibleFunctions(queryBNF(expression, index));
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
			addAllPossibleAggregates(NewValueBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(UpdateClause expression, int index) {
			addAllIdentificationVariables(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public CollectionExpression buildCollectionExpression(UpdateClause expression) {
			CollectionExpression collectionExpression = collectionExpression(expression.getUpdateItems());
			if (collectionExpression != null) {
				return collectionExpression;
			}
			return expression.buildCollectionExpression();
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

	private class WhereClauseHelper implements ClauseHelper<WhereClause> {

		/**
		 * {@inheritDoc}
		 */
		public void addAtTheEndOfExpression(WhereClause expression) {
			addAllPossibleAggregates(ConditionalExpressionBNF.ID);
			addAllPossibleCompounds(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addChoices(WhereClause expression) {
			addAllIdentificationVariables(expression);
			addAllPossibleFunctions(ConditionalExpressionBNF.ID);
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClauseExpression(WhereClause expression) {
			return expression.getConditionalExpression();
		}
	}
}