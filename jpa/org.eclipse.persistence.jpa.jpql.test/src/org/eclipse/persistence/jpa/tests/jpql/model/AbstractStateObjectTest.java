/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.model;

import java.util.Arrays;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.AbstractActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.BaseJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractConditionalClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractDoubleEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractFromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractListHolderStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractRangeVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSchemaNameStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSingleEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractTripleEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AdditionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AllOrAnyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AndExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ArithmeticFactorStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AvgFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.BetweenExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CaseExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CoalesceExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionValuedPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CompoundExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConstructorExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CountFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DateTimeStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DeleteClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DeleteStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DerivedPathIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DerivedPathVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DivisionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EmptyCollectionComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EncapsulatedIdentificationVariableExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntityTypeLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntryExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ExistsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.FromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.GroupByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.HavingClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IndexExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InputParameterStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JoinFetchStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JoinStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeywordExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LengthExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LikeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LocateExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LogicalExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LowerExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MaxFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MinFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ModExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MultiplicationExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NotExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullIfExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NumericLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ObjectExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.RangeVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ResultVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleFromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SizeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SqrtExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubstringExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubtractionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SumFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TrimExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TypeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UnknownExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpperExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ValueExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhenClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhereClauseStateObject;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;
import org.eclipse.persistence.jpa.tests.jpql.JPQLCoreTest;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBuilder;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests the {@link StateObject} API.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused" /* For the extra import statement, see bug 330740 */})
public abstract class AbstractStateObjectTest extends JPQLCoreTest {

	@IJPQLQueryBuilderTestHelper
	private IJPQLQueryBuilder queryBuilder;

	@IJPQLQueryFormatterTestHelper
	private IJPQLQueryFormatter queryFormatter;

	protected static AbsExpressionStateObjectTester abs(StateObjectTester expression) {
		return new AbsExpressionStateObjectTester(expression);
	}

	protected static AbstractSchemaNameStateObjectTester abstractSchemaName(String abstractSchemaName) {
		return new AbstractSchemaNameStateObjectTester(abstractSchemaName);
	}

	protected static AdditionExpressionStateObjectTester add(StateObjectTester leftExpression,
	                                                         StateObjectTester rightExpression) {

		return new AdditionExpressionStateObjectTester(leftExpression, rightExpression);
	}

	protected static AllOrAnyExpressionStateObjectTester all(StateObjectTester subquery) {
		return new AllOrAnyExpressionStateObjectTester(ALL, subquery);
	}

	protected static AndExpressionStateObjectTester and(StateObjectTester leftExpression,
	                                                    StateObjectTester rightExpression) {

		return new AndExpressionStateObjectTester(leftExpression, rightExpression);
	}

	protected static AllOrAnyExpressionStateObjectTester any(StateObjectTester subquery) {
		return new AllOrAnyExpressionStateObjectTester(ANY, subquery);
	}

	protected static AllOrAnyExpressionStateObjectTester anyExpression(StateObjectTester subquery) {
		return new AllOrAnyExpressionStateObjectTester(ANY, subquery);
	}

	protected static AvgFunctionStateObjectTester avg(StateObjectTester expression) {
		return new AvgFunctionStateObjectTester(expression, false);
	}

	protected static AvgFunctionStateObjectTester avg(String statefieldPathExpression) {
		return avg(path(statefieldPathExpression));
	}

	protected static AvgFunctionStateObjectTester avgDistinct(String statefieldPathExpression) {
		return new AvgFunctionStateObjectTester(path(statefieldPathExpression), true);
	}

	protected static BetweenExpressionStateObjectTester between(StateObjectTester expression,
	                                                            StateObjectTester lowerBoundExpression,
	                                                            StateObjectTester upperBoundExpression) {

		return new BetweenExpressionStateObjectTester(expression, false, lowerBoundExpression, upperBoundExpression);
	}

	protected static CaseExpressionStateObjectTester case_(StateObjectTester... caseOperands) {

		StateObjectTester[] copy = new StateObjectTester[caseOperands.length - 1];
		System.arraycopy(caseOperands, 0, copy, 0, caseOperands.length - 1);

		return new CaseExpressionStateObjectTester(
			nullExpression(),
			spacedCollection(copy),
			caseOperands[caseOperands.length - 1]
		);
	}

	protected static CaseExpressionStateObjectTester case_(StateObjectTester caseOperand,
	                                                       StateObjectTester[] whenClauses,
	                                                       StateObjectTester elseExpression) {

		return new CaseExpressionStateObjectTester(
			caseOperand,
			spacedCollection(whenClauses),
			elseExpression
		);
	}

	protected static CaseExpressionStateObjectTester case_(StateObjectTester[] whenClauses,
	                                                       StateObjectTester elseExpression) {

		return case_(nullExpression(), whenClauses, elseExpression);
	}

	protected static CoalesceExpressionStateObjectTester coalesce(StateObjectTester expression) {
		return new CoalesceExpressionStateObjectTester(expression);
	}

	protected static CoalesceExpressionStateObjectTester coalesce(StateObjectTester... expressions) {
		return new CoalesceExpressionStateObjectTester(collection(expressions));
	}

	protected static CollectionExpressionStateObjectTester collection(StateObjectTester... expressions) {

		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length - 1, Boolean.TRUE);

		spaces[expressions.length - 1] = Boolean.FALSE;
		commas[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, commas, spaces);
	}

	protected static CollectionExpressionStateObjectTester collection(StateObjectTester[] expressions,
	                                                                  Boolean[] commas,
	                                                                  Boolean[] spaces) {

		return new CollectionExpressionStateObjectTester(expressions, commas, spaces);
	}

	protected static CollectionValuedPathExpressionStateObjectTester collectionPath(StateObjectTester identificationVariable,
	                                                                                String collectionValuedPathExpression) {

		return new CollectionValuedPathExpressionStateObjectTester(
			identificationVariable,
			collectionValuedPathExpression
		);
	}

	protected static CollectionValuedPathExpressionStateObjectTester collectionPath(String collectionValuedPathExpression) {
		return collectionPath(nullExpression(), collectionValuedPathExpression);
	}

	private static ComparisonExpressionStateObjectTester comparison(StateObjectTester leftExpression,
	                                                                String comparator,
	                                                                StateObjectTester rightExpression) {

		return new ComparisonExpressionStateObjectTester(comparator, leftExpression, rightExpression);
	}

	protected static ConcatExpressionStateObjectTester concat(StateObjectTester... expressions) {
		if (expressions.length > 1) {
			return new ConcatExpressionStateObjectTester(collection(expressions));
		}
		return new ConcatExpressionStateObjectTester(expressions[0]);
	}

	protected static CountFunctionStateObjectTester count(StateObjectTester statefieldPathExpression) {
		return new CountFunctionStateObjectTester(statefieldPathExpression, false);
	}

	protected static CountFunctionStateObjectTester count(String statefieldPathExpression) {
		return count(path(statefieldPathExpression));
	}

	protected static CountFunctionStateObjectTester countDistinct(StateObjectTester statefieldPathExpression) {
		return new CountFunctionStateObjectTester(statefieldPathExpression, true);
	}

	protected static DateTimeStateObjectTester CURRENT_DATE() {
		return new DateTimeStateObjectTester(CURRENT_DATE);
	}

	protected static DateTimeStateObjectTester CURRENT_TIME() {
		return new DateTimeStateObjectTester(CURRENT_TIME);
	}

	protected static DateTimeStateObjectTester CURRENT_TIMESTAMP() {
		return new DateTimeStateObjectTester(CURRENT_TIMESTAMP);
	}

	protected static DateTimeStateObjectTester dateTime(String jdbcEscapeFormat) {
		return new DateTimeStateObjectTester(jdbcEscapeFormat);
	}

	protected static DeleteClauseStateObjectTester delete(StateObjectTester rangeVariableDeclaration) {
		return new DeleteClauseStateObjectTester(rangeVariableDeclaration);
	}

	protected static DeleteClauseStateObjectTester delete(String abstractSchemaName,
	                                                      String identificationVariable) {

		return delete(rangeVariableDeclaration(abstractSchemaName, identificationVariable));
	}

	protected static DeleteClauseStateObjectTester deleteAs(StateObjectTester abstractSchemaName,
	                                                        StateObjectTester identificationVariable) {

		return delete(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	protected static DeleteClauseStateObjectTester deleteAs(String abstractSchemaName,
	                                                        String identificationVariable) {

		return delete(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	protected static DeleteStatementStateObjectTester deleteStatement(StateObjectTester deleteClause) {
		return deleteStatement(deleteClause, nullExpression());
	}

	protected static DeleteStatementStateObjectTester deleteStatement(StateObjectTester deleteClause,
	                                                                  StateObjectTester whereClause) {

		return new DeleteStatementStateObjectTester(deleteClause, whereClause);
	}

	protected static DeleteStatementStateObjectTester deleteStatement(String abstractSchemaName,
	                                                                  String identificationVariable) {

		return deleteStatement(delete(abstractSchemaName, identificationVariable));
	}

	protected static DeleteStatementStateObjectTester deleteStatement(String abstractSchemaName,
	                                                                  String identificationVariable,
	                                                                  StateObjectTester whereClause) {

		return deleteStatement(delete(abstractSchemaName, identificationVariable), whereClause);
	}

	protected static DerivedPathIdentificationVariableDeclarationStateObjectTester derivedIdentificationVariableDeclaration(StateObjectTester derivedRangeVariableDeclaration,
	                                                                                                                        StateObjectTester joins) {

		return new DerivedPathIdentificationVariableDeclarationStateObjectTester(
			derivedRangeVariableDeclaration,
			joins
		);
	}

	private static DerivedPathVariableDeclarationStateObjectTester derivedPathVariableDeclaration(StateObjectTester path,
	                                                                                              boolean hasAs,
	                                                                                              StateObjectTester identificationVariable) {

		return new DerivedPathVariableDeclarationStateObjectTester(path, hasAs, identificationVariable);
	}

	protected static DerivedPathVariableDeclarationStateObjectTester derivedPathVariableDeclaration(StateObjectTester path,
	                                                                                                StateObjectTester identificationVariable) {

		return derivedPathVariableDeclaration(path, false, identificationVariable);
	}

	protected static ComparisonExpressionStateObjectTester different(StateObjectTester leftExpression,
	                                                                 StateObjectTester rightExpression) {

		return comparison(leftExpression, Expression.DIFFERENT, rightExpression);
	}

	protected static DivisionExpressionStateObjectTester division(StateObjectTester leftExpression,
	                                                              StateObjectTester rightExpression) {

		return new DivisionExpressionStateObjectTester(leftExpression, rightExpression);
	}

	protected static EntityTypeLiteralStateObjectTester entity(String entity) {
		return new EntityTypeLiteralStateObjectTester(entity);
	}

	protected static EntryExpressionStateObjectTester entry(String identificationVariable) {
		return new EntryExpressionStateObjectTester(identificationVariable);
	}

	protected static ComparisonExpressionStateObjectTester equal(StateObjectTester leftExpression,
	                                                             StateObjectTester rightExpression) {

		return comparison(leftExpression, Expression.EQUAL, rightExpression);
	}

	protected static ExistsExpressionStateObjectTester exists(StateObjectTester subquery) {
		return new ExistsExpressionStateObjectTester(subquery, false);
	}

	protected static KeywordExpressionStateObjectTester FALSE() {
		return new KeywordExpressionStateObjectTester(FALSE);
	}

	protected static FromClauseStateObjectTester from(StateObjectTester declaration) {
		return new FromClauseStateObjectTester(declaration);
	}

	protected static FromClauseStateObjectTester from(StateObjectTester... declarations) {
		return new FromClauseStateObjectTester(collection(declarations));
	}

	/**
	 * Example: from("Employee", "e", "Product", "p")
	 */
	protected static FromClauseStateObjectTester from(String... declarations) {

		StateObjectTester[] identificationVariableDeclarations = new StateObjectTester[declarations.length / 2];

		for (int index = 0, count = declarations.length; index + 1 < count; index += 2) {
			identificationVariableDeclarations[index / 2] = identificationVariableDeclaration(
				declarations[index],
				declarations[index + 1]
			);
		}

		return from(identificationVariableDeclarations);
	}

	/**
	 * Example: from("Employee", "e")
	 */
	protected static FromClauseStateObjectTester from(String abstractSchemaName,
	                                                  String identificationVariable) {

		return from(fromEntity(abstractSchemaName, identificationVariable));
	}

	protected static FromClauseStateObjectTester from(String abstractSchemaName,
	                                                  String identificationVariable,
	                                                  StateObjectTester... joins) {

		return from(identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins));
	}

	protected static FromClauseStateObjectTester from(String abstractSchemaName,
	                                                  String identificationVariable,
	                                                  StateObjectTester joins) {

		return from(fromEntity(abstractSchemaName, identificationVariable, joins));
	}

	protected static FromClauseStateObjectTester fromAs(String abstractSchemaName, String identificationVariable) {
		return from(identificationVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	protected static DerivedPathIdentificationVariableDeclarationStateObjectTester fromDerivedPath(String path,
	                                                                                               String identificationVariable) {

		return derivedIdentificationVariableDeclaration(
			derivedPathVariableDeclaration(
				collectionPath(path),
				variable(identificationVariable)
			),
			nullExpression()
		);
	}

	protected static IdentificationVariableDeclarationStateObjectTester fromEntity(String abstractSchemaName,
	                                                                               String identificationVariable) {

		return identificationVariableDeclaration(abstractSchemaName, identificationVariable);
	}

	protected static IdentificationVariableDeclarationStateObjectTester fromEntity(String abstractSchemaName,
	                                                                               String identificationVariable,
	                                                                               StateObjectTester... joins) {

		return identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins);
	}

	protected static IdentificationVariableDeclarationStateObjectTester fromEntity(String abstractSchemaName,
	                                                                               String identificationVariable,
	                                                                               StateObjectTester join) {

		return identificationVariableDeclaration(abstractSchemaName, identificationVariable, join);
	}

	protected static IdentificationVariableDeclarationStateObjectTester fromEntityAs(String abstractSchemaName,
	                                                                                 String identificationVariable) {

		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable);
	}

	protected static IdentificationVariableDeclarationStateObjectTester fromEntityAs(String abstractSchemaName,
	                                                                                 String identificationVariable,
	                                                                                 StateObjectTester... joins) {

		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, joins);
	}

	protected static IdentificationVariableDeclarationStateObjectTester fromEntityAs(String abstractSchemaName,
	                                                                                 String identificationVariable,
	                                                                                 StateObjectTester join) {

		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, join);
	}

	protected static CollectionMemberDeclarationStateObjectTester fromIn(StateObjectTester collectionValuedPathExpression,
	                                                                     StateObjectTester identificationVariable) {

		return new CollectionMemberDeclarationStateObjectTester(
			collectionValuedPathExpression,
			false,
			identificationVariable,
			false
		);
	}

	protected static CollectionMemberDeclarationStateObjectTester fromIn(String collectionValuedPathExpression,
	                                                                     String identificationVariable) {

		return fromIn(collectionPath(collectionValuedPathExpression), variable(identificationVariable));
	}

	protected static CollectionMemberDeclarationStateObjectTester fromInAs(StateObjectTester collectionValuedPathExpression,
	                                                                       StateObjectTester identificationVariable) {

		return new CollectionMemberDeclarationStateObjectTester(
			collectionValuedPathExpression,
			true,
			identificationVariable,
			false
		);
	}

	protected static CollectionMemberDeclarationStateObjectTester fromInAs(String collectionValuedPathExpression,
	                                                                       String identificationVariable) {

		return fromInAs(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static ComparisonExpressionStateObjectTester greaterThan(StateObjectTester leftExpression,
	                                                                   StateObjectTester rightExpression) {

		return comparison(leftExpression, Expression.GREATER_THAN, rightExpression);
	}

	protected static ComparisonExpressionStateObjectTester greaterThanOrEqual(StateObjectTester leftExpression,
	                                                                          StateObjectTester rightExpression) {

		return comparison(leftExpression, Expression.GREATER_THAN_OR_EQUAL, rightExpression);
	}

	protected static GroupByClauseStateObjectTester groupBy(StateObjectTester groupByItem) {
		return new GroupByClauseStateObjectTester(groupByItem);
	}

	protected static GroupByClauseStateObjectTester groupBy(StateObjectTester... groupByItems) {
		return new GroupByClauseStateObjectTester(collection(groupByItems));
	}

	protected static HavingClauseStateObjectTester having(StateObjectTester havingItem) {
		return new HavingClauseStateObjectTester(havingItem);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclaration(StateObjectTester rangeVariableDeclaration) {
		return new IdentificationVariableDeclarationStateObjectTester(rangeVariableDeclaration, nullExpression());
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclaration(StateObjectTester rangeVariableDeclaration,
	                                                                                                      StateObjectTester... joins) {

		return new IdentificationVariableDeclarationStateObjectTester(rangeVariableDeclaration, collection(joins));
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclaration(StateObjectTester rangeVariableDeclaration,
	                                                                                                      StateObjectTester joins) {

		return new IdentificationVariableDeclarationStateObjectTester(rangeVariableDeclaration, joins);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclaration(String abstractSchemaName,
	                                                                                                      String identificationVariable) {

		return identificationVariableDeclaration(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclaration(String abstractSchemaName,
	                                                                                                      String identificationVariable,
	                                                                                                      StateObjectTester... joins) {

		return new IdentificationVariableDeclarationStateObjectTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			spacedCollection(joins)
		);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclaration(String abstractSchemaName,
	                                                                                                      String identificationVariable,
	                                                                                                      StateObjectTester join) {

		return new IdentificationVariableDeclarationStateObjectTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			join
		);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                                                                        String identificationVariable) {

		return new IdentificationVariableDeclarationStateObjectTester(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                                                                        String identificationVariable,
	                                                                                                        StateObjectTester join) {

		return new IdentificationVariableDeclarationStateObjectTester(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			join
		);
	}

	protected static IdentificationVariableDeclarationStateObjectTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                                                                        String identificationVariable,
	                                                                                                        StateObjectTester... joins) {

		return new IdentificationVariableDeclarationStateObjectTester(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			spacedCollection(joins)
		);
	}

	private static InExpressionStateObjectTester in(StateObjectTester stateFieldPathExpression,
	                                                StateObjectTester inItems) {

		return new InExpressionStateObjectTester(stateFieldPathExpression, false, inItems);
	}

	protected static InExpressionStateObjectTester in(StateObjectTester stateFieldPathExpression,
	                                                  StateObjectTester... inItems) {

		return in(stateFieldPathExpression, collection(inItems));
	}

	protected static InExpressionStateObjectTester in(String stateFieldPathExpression,
	                                                  StateObjectTester... inItems) {

		return in(path(stateFieldPathExpression), collection(inItems));
	}

	private static InExpressionStateObjectTester in(String stateFieldPathExpression,
	                                                StateObjectTester inItems) {

		return in(path(stateFieldPathExpression), inItems);
	}

	protected static IndexExpressionStateObjectTester index(String identificationVariable) {
		return new IndexExpressionStateObjectTester(identificationVariable);
	}

	protected static JoinStateObjectTester innerJoin(StateObjectTester collectionValuedPathExpression,
	                                      StateObjectTester identificationVariable) {

		return join(INNER_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester innerJoin(String collectionValuedPathExpression,
	                                      String identificationVariable) {

		return innerJoin(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinStateObjectTester innerJoinAs(StateObjectTester collectionValuedPathExpression,
	                                        StateObjectTester identificationVariable) {

		return joinAs(INNER_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester innerJoinAs(String collectionValuedPathExpression,
	                                        String identificationVariable) {

		return innerJoinAs(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinFetchStateObjectTester innerJoinFetch(StateObjectTester collectionValuedPathExpression) {
		return joinFetch(INNER_JOIN_FETCH, collectionValuedPathExpression);
	}

	protected static JoinFetchStateObjectTester innerJoinFetch(String collectionValuedPathExpression) {
		return innerJoinFetch(collectionPath(collectionValuedPathExpression));
	}

	protected static InputParameterStateObjectTester inputParameter(String inputParameter) {
		return new InputParameterStateObjectTester(inputParameter);
	}

	protected static EmptyCollectionComparisonExpressionStateObjectTester isEmpty(StateObjectTester collectionValuedPathExpression) {
		return new EmptyCollectionComparisonExpressionStateObjectTester(collectionValuedPathExpression, false);
	}

	protected static EmptyCollectionComparisonExpressionStateObjectTester isEmpty(String collectionValuedPathExpression) {
		return isEmpty(collectionPath(collectionValuedPathExpression));
	}

	protected static EmptyCollectionComparisonExpressionStateObjectTester isNotEmpty(StateObjectTester collectionValuedPathExpression) {
		return new EmptyCollectionComparisonExpressionStateObjectTester(collectionValuedPathExpression, true);
	}

	protected static EmptyCollectionComparisonExpressionStateObjectTester isNotEmpty(String collectionValuedPathExpression) {
		return isNotEmpty(collectionPath(collectionValuedPathExpression));
	}

	protected static NullComparisonExpressionStateObjectTester isNotNull(StateObjectTester expression) {
		return new NullComparisonExpressionStateObjectTester(expression, true);
	}

	protected static NullComparisonExpressionStateObjectTester isNull(StateObjectTester expression) {
		return new NullComparisonExpressionStateObjectTester(expression, false);
	}

	protected static JoinStateObjectTester join(StateObjectTester collectionValuedPathExpression,
	                                            StateObjectTester identificationVariable) {

		return join(JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester join(StateObjectTester collectionValuedPathExpression,
	                                            String identificationVariable) {

		return join(
			collectionValuedPathExpression,
			variable(identificationVariable)
		);
	}

	private static JoinStateObjectTester join(String joinType,
	                                          boolean hasAs,
	                                          StateObjectTester collectionValuedPathExpression,
	                                          StateObjectTester identificationVariable) {

		return new JoinStateObjectTester(
			joinType,
			collectionValuedPathExpression,
			hasAs,
			identificationVariable
		);
	}

	private static JoinStateObjectTester join(String joinType,
	                                          StateObjectTester collectionValuedPathExpression,
	                                          StateObjectTester identificationVariable) {

		return join(
			joinType,
			false,
			collectionValuedPathExpression,
			identificationVariable
		);
	}

	protected static JoinStateObjectTester join(String collectionValuedPathExpression,
	                                            String identificationVariable) {

		return join(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinStateObjectTester joinAs(StateObjectTester collectionValuedPathExpression,
	                                              StateObjectTester identificationVariable) {

		return joinAs(JOIN, collectionValuedPathExpression, identificationVariable);
	}

	private static JoinStateObjectTester joinAs(String joinType,
	                                            StateObjectTester collectionValuedPathExpression,
	                                            StateObjectTester identificationVariable) {

		return join(
			joinType,
			true,
			collectionValuedPathExpression,
			identificationVariable
		);
	}

	protected static JoinStateObjectTester joinAs(String collectionValuedPathExpression,
	                                              String identificationVariable) {

		return joinAs(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinFetchStateObjectTester joinFetch(StateObjectTester collectionValuedPathExpression) {
		return joinFetch(JOIN_FETCH, collectionValuedPathExpression);
	}

	protected static JoinFetchStateObjectTester joinFetch(String collectionValuedPathExpression) {
		return joinFetch(collectionPath(collectionValuedPathExpression));
	}

	private static JoinFetchStateObjectTester joinFetch(String joinFetchType,
	                                                    StateObjectTester collectionValuedPathExpression) {

		return new JoinFetchStateObjectTester(
			joinFetchType,
			collectionValuedPathExpression
		);
	}

	protected static JPQLQueryStateObjectTester jpqlQuery(StateObjectTester queryStatement) {
		return new JPQLQueryStateObjectTester(queryStatement);
	}

	protected static KeyExpressionStateObjectTester key(String identificationVariable) {
		return new KeyExpressionStateObjectTester(identificationVariable);
	}

	protected static JoinStateObjectTester leftJoin(StateObjectTester collectionValuedPathExpression,
	                                                StateObjectTester identificationVariable) {

		return join(LEFT_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester leftJoin(String collectionValuedPathExpression,
	                                                String identificationVariable) {

		return leftJoin(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinStateObjectTester leftJoinAs(StateObjectTester collectionValuedPathExpression,
	                                                  StateObjectTester identificationVariable) {

		return joinAs(LEFT_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester leftJoinAs(String collectionValuedPathExpression,
	                                                  String identificationVariable) {

		return leftJoinAs(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinFetchStateObjectTester leftJoinFetch(StateObjectTester collectionValuedPathExpression) {
		return joinFetch(LEFT_JOIN_FETCH, collectionValuedPathExpression);
	}

	protected static JoinFetchStateObjectTester leftJoinFetch(String collectionValuedPathExpression) {
		return leftJoinFetch(collectionPath(collectionValuedPathExpression));
	}

	protected static JoinStateObjectTester leftOuterJoin(StateObjectTester collectionValuedPathExpression,
	                                                     StateObjectTester identificationVariable) {

		return join(LEFT_OUTER_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester leftOuterJoin(String collectionValuedPathExpression,
	                                                     String identificationVariable) {

		return leftOuterJoin(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinStateObjectTester leftOuterJoinAs(StateObjectTester collectionValuedPathExpression,
	                                                       StateObjectTester identificationVariable) {

		return joinAs(LEFT_OUTER_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	protected static JoinStateObjectTester leftOuterJoinAs(String collectionValuedPathExpression,
	                                                       String identificationVariable) {

		return leftOuterJoinAs(
			collectionPath(collectionValuedPathExpression),
			variable(identificationVariable)
		);
	}

	protected static JoinFetchStateObjectTester leftOuterJoinFetch(StateObjectTester collectionValuedPathExpression) {
		return joinFetch(LEFT_OUTER_JOIN_FETCH, collectionValuedPathExpression);
	}

	protected static JoinFetchStateObjectTester leftOuterJoinFetch(String collectionValuedPathExpression) {
		return leftOuterJoinFetch(collectionPath(collectionValuedPathExpression));
	}

	protected static LengthExpressionStateObjectTester length(StateObjectTester stringPrimary) {
		return new LengthExpressionStateObjectTester(stringPrimary);
	}

	protected static LikeExpressionStateObjectTester like(StateObjectTester stringExpression,
	                                                      StateObjectTester patternValue) {

		return like(stringExpression, patternValue, null);
	}

	protected static LikeExpressionStateObjectTester like(StateObjectTester stringExpression,
	                                                      StateObjectTester patternValue,
	                                                      char escapeCharacter) {

		return like(stringExpression, patternValue, quote(escapeCharacter));
	}

	protected static LikeExpressionStateObjectTester like(StateObjectTester stringExpression,
	                                                      StateObjectTester patternValue,
	                                                      String escapeCharacter) {

		return new LikeExpressionStateObjectTester(
			stringExpression,
			false,
			patternValue,
			escapeCharacter
		);
	}

	protected static LocateExpressionStateObjectTester locate(StateObjectTester firstExpression,
	                                                          StateObjectTester secondExpression) {

		return locate(firstExpression, secondExpression, nullExpression());
	}

	protected static LocateExpressionStateObjectTester locate(StateObjectTester firstExpression,
	                                                          StateObjectTester secondExpression,
	                                                          StateObjectTester thirdExpression) {

		return new LocateExpressionStateObjectTester(firstExpression, secondExpression, thirdExpression);
	}

	protected static LowerExpressionStateObjectTester lower(StateObjectTester stringPrimary) {
		return new LowerExpressionStateObjectTester(stringPrimary);
	}

	protected static ComparisonExpressionStateObjectTester lowerThan(StateObjectTester leftExpression,
	                                                                 StateObjectTester rightExpression) {

		return comparison(leftExpression, Expression.LOWER_THAN, rightExpression);
	}

	protected static ComparisonExpressionStateObjectTester lowerThanOrEqual(StateObjectTester leftExpression,
	                                                                        StateObjectTester rightExpression) {

		return comparison(leftExpression, Expression.LOWER_THAN_OR_EQUAL, rightExpression);
	}

	protected static MaxFunctionStateObjectTester max(StateObjectTester expression) {
		return new MaxFunctionStateObjectTester(expression, false);
	}

	protected static MaxFunctionStateObjectTester max(String statefieldPathExpression) {
		return max(path(statefieldPathExpression));
	}

	protected static StateObjectTester maxDistinct(String statefieldPathExpression) {
		return new MaxFunctionStateObjectTester(path(statefieldPathExpression), true);
	}

	protected static CollectionMemberExpressionStateObjectTester member(StateObjectTester entityExpression,
	                                                                    StateObjectTester collectionValuedPathExpression) {

		return new CollectionMemberExpressionStateObjectTester(
			entityExpression,
			false,
			false,
			collectionValuedPathExpression
		);
	}

	protected static CollectionMemberExpressionStateObjectTester member(StateObjectTester entityExpression,
	                                                                    String collectionValuedPathExpression) {

		return member(entityExpression, collectionPath(collectionValuedPathExpression));
	}

	protected static CollectionMemberExpressionStateObjectTester member(String identificationVariable,
	                                                                    String collectionValuedPathExpression) {

		return member(variable(identificationVariable), collectionValuedPathExpression);
	}

	protected static CollectionMemberExpressionStateObjectTester memberOf(StateObjectTester entityExpression,
	                                                                      StateObjectTester collectionValuedPathExpression) {

		return new CollectionMemberExpressionStateObjectTester(
			entityExpression,
			false,
			true,
			collectionValuedPathExpression
		);
	}

	protected static CollectionMemberExpressionStateObjectTester memberOf(StateObjectTester entityExpression,
	                                                                      String collectionValuedPathExpression) {

		return memberOf(entityExpression, collectionPath(collectionValuedPathExpression));
	}

	protected static CollectionMemberExpressionStateObjectTester memberOf(String identificationVariable,
	                                                                      String collectionValuedPathExpression) {

		return memberOf( variable(identificationVariable), collectionValuedPathExpression);
	}

	protected static MinFunctionStateObjectTester min(StateObjectTester expression) {
		return new MinFunctionStateObjectTester(expression, false);
	}

	protected static MinFunctionStateObjectTester min(String statefieldPathExpression) {
		return min(path(statefieldPathExpression));
	}

	protected static MinFunctionStateObjectTester minDistinct(String statefieldPathExpression) {
		return new MinFunctionStateObjectTester(path(statefieldPathExpression), true);
	}

	protected static ArithmeticFactorStateObjectTester minus(StateObjectTester expression) {
		return new ArithmeticFactorStateObjectTester(MINUS, expression);
	}

	protected static ModExpressionStateObjectTester mod(StateObjectTester simpleArithmeticExpression1,
	                                                    StateObjectTester simpleArithmeticExpression2) {

		return new ModExpressionStateObjectTester(simpleArithmeticExpression1, simpleArithmeticExpression2);
	}

	protected static MultiplicationExpressionStateObjectTester multiplication(StateObjectTester leftExpression,
	                                                                          StateObjectTester rightExpression) {

		return new MultiplicationExpressionStateObjectTester(leftExpression, rightExpression);
	}

	protected static ConstructorExpressionStateObjectTester new_(String className,
	                                                             StateObjectTester constructorItem) {

		return new ConstructorExpressionStateObjectTester(className, constructorItem);
	}

	protected static ConstructorExpressionStateObjectTester new_(String className,
	                                                             StateObjectTester... constructorItems) {

		return new ConstructorExpressionStateObjectTester(className, collection(constructorItems));
	}

	protected static NotExpressionStateObjectTester not(StateObjectTester expression) {
		return new NotExpressionStateObjectTester(expression);
	}

	protected static BetweenExpressionStateObjectTester notBetween(StateObjectTester expression,
	                                                               StateObjectTester lowerBoundExpression,
	                                                               StateObjectTester upperBoundExpression) {

		return new BetweenExpressionStateObjectTester(
			expression,
			true,
			lowerBoundExpression,
			upperBoundExpression
		);
	}

	protected static ExistsExpressionStateObjectTester notExists(StateObjectTester subquery) {
		return new ExistsExpressionStateObjectTester(subquery, true);
	}

	protected static InExpressionStateObjectTester notIn(StateObjectTester stateFieldPathExpression,
	                                                     StateObjectTester inItems) {

		return new InExpressionStateObjectTester(stateFieldPathExpression, true, inItems);
	}

	protected static InExpressionStateObjectTester notIn(StateObjectTester stateFieldPathExpression,
	                                                     StateObjectTester... inItems) {

		return notIn(stateFieldPathExpression, collection(inItems));
	}

	protected static InExpressionStateObjectTester notIn(String stateFieldPathExpression,
	                                                     StateObjectTester inItems) {

		return notIn(path(stateFieldPathExpression), inItems);
	}

	protected static InExpressionStateObjectTester notIn(String stateFieldPathExpression,
	                                                     StateObjectTester... inItems) {

		return notIn(path(stateFieldPathExpression), inItems);
	}

	protected static LikeExpressionStateObjectTester notLike(StateObjectTester stringExpression,
	                                                         StateObjectTester patternValue) {

		return notLike(stringExpression, patternValue, null);
	}

	protected static LikeExpressionStateObjectTester notLike(StateObjectTester stringExpression,
	                                                         StateObjectTester patternValue,
	                                                         String escapeCharacter) {

		return new LikeExpressionStateObjectTester(
			stringExpression,
			true,
			patternValue,
			escapeCharacter
		);
	}

	protected static CollectionMemberExpressionStateObjectTester notMember(StateObjectTester entityExpression,
	                                                                       StateObjectTester collectionValuedPathExpression) {

		return new CollectionMemberExpressionStateObjectTester(
			entityExpression,
			true,
			false,
			collectionValuedPathExpression
		);
	}

	protected static CollectionMemberExpressionStateObjectTester notMember(StateObjectTester entityExpression,
	                                                                       String collectionValuedPathExpression) {

		return notMember(entityExpression, collectionValuedPathExpression);
	}

	protected static CollectionMemberExpressionStateObjectTester notMember(String identificationVariable,
	                                                                       String collectionValuedPathExpression) {

		return notMember(variable(identificationVariable), collectionValuedPathExpression);
	}

	protected static CollectionMemberExpressionStateObjectTester notMemberOf(StateObjectTester entityExpression,
	                                                                         StateObjectTester collectionValuedPathExpression) {

		return new CollectionMemberExpressionStateObjectTester(
			entityExpression,
			true,
			true,
			collectionValuedPathExpression
		);
	}

	protected static StateObjectTester NULL() {
		return new KeywordExpressionStateObjectTester(NULL);
	}

	protected static StateObjectTester nullExpression() {
		return new NullExpressionStateObjectTester();
	}

	protected static NullIfExpressionStateObjectTester nullIf(StateObjectTester expression1,
	                                                          StateObjectTester expression2) {

		return new NullIfExpressionStateObjectTester(expression1, expression2);
	}

	protected static NumericLiteralStateObjectTester numeric(double number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralStateObjectTester numeric(float number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralStateObjectTester numeric(int number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralStateObjectTester numeric(long number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralStateObjectTester numeric(String value) {
		return new NumericLiteralStateObjectTester(value);
	}

	protected static ObjectExpressionStateObjectTester object(String identificationVariable) {
		return new ObjectExpressionStateObjectTester(identificationVariable);
	}

	protected static OrExpressionStateObjectTester or(StateObjectTester leftExpression,
	                                                  StateObjectTester rightExpression) {

		return new OrExpressionStateObjectTester(leftExpression, rightExpression);
	}

	protected static OrderByClauseStateObjectTester orderBy(StateObjectTester orderByItem) {
		return new OrderByClauseStateObjectTester(orderByItem);
	}

	protected static OrderByClauseStateObjectTester orderBy(StateObjectTester... orderByItems) {
		return new OrderByClauseStateObjectTester(collection(orderByItems));
	}

	protected static OrderByClauseStateObjectTester orderBy(String stateFieldPathExpression) {
		return new OrderByClauseStateObjectTester(orderByItem(stateFieldPathExpression));
	}

	protected static OrderByItemStateObjectTester orderByItem(StateObjectTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DEFAULT);
	}

	private static OrderByItemStateObjectTester orderByItem(StateObjectTester orderByItem,
	                                                        Ordering ordering) {

		return new OrderByItemStateObjectTester(orderByItem, ordering);
	}

	protected static OrderByItemStateObjectTester orderByItem(String stateFieldPathExpression) {
		return orderByItem(path(stateFieldPathExpression));
	}

	protected static OrderByItemStateObjectTester orderByItemAsc(StateObjectTester orderByItem) {
		return orderByItem(orderByItem, Ordering.ASC);
	}

	protected static OrderByItemStateObjectTester orderByItemAsc(String stateFieldPathExpression) {
		return orderByItemAsc(path(stateFieldPathExpression));
	}

	protected static OrderByItemStateObjectTester orderByItemDesc(StateObjectTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DESC);
	}

	protected static OrderByItemStateObjectTester orderByItemDesc(String stateFieldPathExpression) {
		return orderByItemDesc(path(stateFieldPathExpression));
	}

	protected static StateFieldPathExpressionStateObjectTester path(StateObjectTester identificationVariable,
	                                                                String stateFieldPathExpression) {

		return new StateFieldPathExpressionStateObjectTester(identificationVariable, stateFieldPathExpression);
	}

	protected static StateFieldPathExpressionStateObjectTester path(String stateFieldPathExpression) {
		return path(nullExpression(), stateFieldPathExpression);
	}

	protected static ArithmeticFactorStateObjectTester plus(StateObjectTester expression) {
		return new ArithmeticFactorStateObjectTester(PLUS, expression);
	}

	protected static String quote(char character) {
		return new StringBuilder(3).append("'").append(character).append("'").toString();
	}

	private static RangeVariableDeclarationStateObjectTester rangeVariableDeclaration(StateObjectTester abstractSchemaName,
	                                                                                  boolean hasAs,
	                                                                                  StateObjectTester identificationVariable) {

		return new RangeVariableDeclarationStateObjectTester(abstractSchemaName, hasAs, identificationVariable);
	}

	protected static RangeVariableDeclarationStateObjectTester rangeVariableDeclaration(StateObjectTester abstractSchemaName,
	                                                                                    StateObjectTester identificationVariable) {

		return rangeVariableDeclaration(abstractSchemaName, false, identificationVariable);
	}

	protected static RangeVariableDeclarationStateObjectTester rangeVariableDeclaration(String abstractSchemaName) {
		return rangeVariableDeclaration(
			abstractSchemaName(abstractSchemaName),
			false,
			nullExpression()
		);
	}

	protected static RangeVariableDeclarationStateObjectTester rangeVariableDeclaration(String abstractSchemaName,
	                                                                                    String identificationVariable) {

		return rangeVariableDeclaration(
			abstractSchemaName(abstractSchemaName),
			false,
			variable(identificationVariable)
		);
	}

	protected static RangeVariableDeclarationStateObjectTester rangeVariableDeclarationAs(StateObjectTester abstractSchemaName,
	                                                                                      StateObjectTester identificationVariable) {

		return rangeVariableDeclaration(abstractSchemaName, true, identificationVariable);
	}

	protected static RangeVariableDeclarationStateObjectTester rangeVariableDeclarationAs(String abstractSchemaName,
	                                                                                      String identificationVariable) {

		return rangeVariableDeclarationAs(
			abstractSchemaName(abstractSchemaName),
			variable(identificationVariable)
		);
	}

	protected static SelectClauseStateObjectTester select(StateObjectTester selectExpression) {
		return select(selectExpression, false);
	}

	protected static SelectClauseStateObjectTester select(StateObjectTester... selectExpressions) {
		return new SelectClauseStateObjectTester(collection(selectExpressions), false);
	}

	private static SelectClauseStateObjectTester select(StateObjectTester selectExpression,
	                                                    boolean hasDistinct) {

		return new SelectClauseStateObjectTester(selectExpression, hasDistinct);
	}

	protected static SelectClauseStateObjectTester selectDistinct(StateObjectTester... selectExpressions) {
		return new SelectClauseStateObjectTester(collection(selectExpressions), true);
	}

	protected static SelectClauseStateObjectTester selectDistinct(StateObjectTester selectExpression) {
		return new SelectClauseStateObjectTester(selectExpression, true);
	}

	protected static SelectClauseStateObjectTester selectDisting(StateObjectTester selectExpression) {
		return select(selectExpression, true);
	}

	private static ResultVariableStateObjectTester selectItem(StateObjectTester selectExpression,
	                                                          boolean hasAs,
	                                                          String resultVariable) {

		return new ResultVariableStateObjectTester(selectExpression, hasAs, resultVariable);
	}

	protected static ResultVariableStateObjectTester selectItem(StateObjectTester selectExpression,
	                                                            String resultVariable) {

		return selectItem(selectExpression, false, resultVariable);
	}

	protected static ResultVariableStateObjectTester selectItemAs(StateObjectTester selectExpression,
	                                                              String resultVariable) {

		return selectItem(selectExpression, true, resultVariable);
	}

	protected static SelectStatementStateObjectTester selectStatement(StateObjectTester selectClause,
	                                                                  StateObjectTester fromClause) {

		return selectStatement(selectClause, fromClause, nullExpression());
	}

	protected static SelectStatementStateObjectTester selectStatement(StateObjectTester selectClause,
	                                                                  StateObjectTester fromClause,
	                                                                  StateObjectTester whereClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	protected static SelectStatementStateObjectTester selectStatement(StateObjectTester selectClause,
	                                                                  StateObjectTester fromClause,
	                                                                  StateObjectTester whereClause,
	                                                                  StateObjectTester groupByClause,
	                                                                  StateObjectTester havingClause,
	                                                                  StateObjectTester orderByClause) {

		return new SelectStatementStateObjectTester(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause,
			orderByClause
		);
	}

	protected static UpdateItemStateObjectTester set(StateObjectTester stateFieldPathExpression,
	                                                 StateObjectTester newValue) {

		return new UpdateItemStateObjectTester(stateFieldPathExpression, newValue);
	}

	protected static UpdateItemStateObjectTester set(String stateFieldPathExpression,
	                                                 StateObjectTester newValue) {

		return set(path(stateFieldPathExpression), newValue);
	}

	protected static SizeExpressionStateObjectTester size(StateObjectTester collectionValuedPathExpression) {
		return new SizeExpressionStateObjectTester(collectionValuedPathExpression);
	}

	protected static SizeExpressionStateObjectTester size(String collectionValuedPathExpression) {
		return size(collectionPath(collectionValuedPathExpression));
	}

	protected static AllOrAnyExpressionStateObjectTester some(StateObjectTester subquery) {
		return new AllOrAnyExpressionStateObjectTester(SOME, subquery);
	}

	protected static StateObjectTester spacedCollection(StateObjectTester... expressions) {

		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length,     Boolean.FALSE);

		spaces[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, commas, spaces);
	}

	protected static SqrtExpressionStateObjectTester sqrt(StateObjectTester simpleArithmeticExpression) {
		return new SqrtExpressionStateObjectTester(simpleArithmeticExpression);
	}

	protected static StringLiteralStateObjectTester string(char literal) {
		return new StringLiteralStateObjectTester(quote(literal));
	}

	protected static StringLiteralStateObjectTester string(String literal) {
		return new StringLiteralStateObjectTester(literal);
	}

	protected static SubExpressionStateObjectTester sub(StateObjectTester expression) {
		return new SubExpressionStateObjectTester(expression);
	}

	protected static SimpleFromClauseStateObjectTester subFrom(StateObjectTester declaration) {
		return new SimpleFromClauseStateObjectTester(declaration);
	}

	protected static SimpleFromClauseStateObjectTester subFrom(StateObjectTester... declarations) {
		return new SimpleFromClauseStateObjectTester(collection(declarations));
	}

	protected static SimpleFromClauseStateObjectTester subFrom(String abstractSchemaName,
	                                                           String identificationVariable) {

		return subFrom(identificationVariableDeclaration(abstractSchemaName, identificationVariable));
	}

	protected static SimpleFromClauseStateObjectTester subFrom(String abstractSchemaName,
	                                                           String identificationVariable,
	                                                           StateObjectTester... joins) {

		return subFrom(identificationVariableDeclaration(
			abstractSchemaName,
			identificationVariable,
			joins
		));
	}

	protected static SimpleFromClauseStateObjectTester subFrom(String abstractSchemaName,
	                                                String identificationVariable,
	                                                StateObjectTester joins) {

		return subFrom(identificationVariableDeclaration(
			abstractSchemaName,
			identificationVariable,
			joins
		));
	}

	protected static CollectionMemberDeclarationStateObjectTester subFromDerivedIn(StateObjectTester collectionValuedPathExpression) {

		return new CollectionMemberDeclarationStateObjectTester(
			collectionValuedPathExpression,
			false,
			null,
			true
		);
	}

	protected static CollectionMemberDeclarationStateObjectTester subFromDerivedIn(String collectionValuedPathExpression) {
		return subFromDerivedIn(collectionPath(collectionValuedPathExpression));
	}

	protected static SimpleSelectStatementStateObjectTester subquery(StateObjectTester selectClause,
	                                                                 StateObjectTester fromClause) {

		return subquery(selectClause, fromClause, nullExpression());
	}

	protected static SimpleSelectStatementStateObjectTester subquery(StateObjectTester selectClause,
	                                                                 StateObjectTester fromClause,
	                                                                 StateObjectTester whereClause) {

		return subSelectStatement(selectClause, fromClause, whereClause);
	}

	protected static SimpleSelectStatementStateObjectTester subquery(StateObjectTester selectClause,
	                                                                 StateObjectTester fromClause,
	                                                                 StateObjectTester whereClause,
	                                                                 StateObjectTester groupByClause,
	                                                                 StateObjectTester havingClause) {

		return subSelectStatement(selectClause, fromClause, whereClause, groupByClause, havingClause);
	}

	protected static SimpleSelectClauseStateObjectTester subSelect(StateObjectTester selectExpression) {
		return subSelect(selectExpression, false);
	}

	protected static SimpleSelectClauseStateObjectTester subSelect(StateObjectTester... selectExpressions) {
		return new SimpleSelectClauseStateObjectTester(collection(selectExpressions), false);
	}

	private static SimpleSelectClauseStateObjectTester subSelect(StateObjectTester selectExpression,
	                                                             boolean hasDistinct) {

		return new SimpleSelectClauseStateObjectTester(selectExpression, hasDistinct);
	}

	protected static SimpleSelectClauseStateObjectTester subSelectDistinct(StateObjectTester selectExpression) {
		return subSelect(selectExpression, true);
	}

	protected static SimpleSelectClauseStateObjectTester subSelectDistinct(StateObjectTester... selectExpressions) {
		return new SimpleSelectClauseStateObjectTester(collection(selectExpressions), true);
	}

	protected static SimpleSelectStatementStateObjectTester subSelectStatement(StateObjectTester selectClause,
	                                                                           StateObjectTester fromClause,
	                                                                           StateObjectTester whereClause) {

		return subSelectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression()
		);
	}

	protected static SimpleSelectStatementStateObjectTester subSelectStatement(StateObjectTester selectClause,
	                                                                           StateObjectTester fromClause,
	                                                                           StateObjectTester whereClause,
	                                                                           StateObjectTester groupByClause,
	                                                                           StateObjectTester havingClause) {

		return new SimpleSelectStatementStateObjectTester(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause
		);
	}

	protected static SubstractionExpressionStateObjectTester substract(StateObjectTester leftExpression,
	                                                                   StateObjectTester rightExpression) {

		return new SubstractionExpressionStateObjectTester(leftExpression, rightExpression);
	}

	protected static SubstringExpressionStateObjectTester substring(StateObjectTester firstExpression,
	                                                                StateObjectTester secondExpression) {

		return substring(firstExpression, secondExpression, nullExpression());
	}

	protected static SubstringExpressionStateObjectTester substring(StateObjectTester firstExpression,
	                                                                StateObjectTester secondExpression,
	                                                                StateObjectTester thirdExpression) {

		return new SubstringExpressionStateObjectTester(firstExpression, secondExpression, thirdExpression);
	}

	protected static SumFunctionStateObjectTester sum(StateObjectTester expression) {
		return new SumFunctionStateObjectTester(expression, false);
	}

	protected static SumFunctionStateObjectTester sum(String statefieldPathExpression) {
		return sum(path(statefieldPathExpression));
	}

	protected static SumFunctionStateObjectTester sumDistinct(String statefieldPathExpression) {
		return new SumFunctionStateObjectTester(path(statefieldPathExpression), true);
	}

	protected static TrimExpressionStateObjectTester trim(char trimCharacter,
	                                                      StateObjectTester stringPrimary) {

		return trim(quote(trimCharacter), stringPrimary);
	}

	private static TrimExpressionStateObjectTester trim(Specification specification,
	                                                    String trimCharacter,
	                                                    boolean hasFrom,
	                                                    StateObjectTester stringPrimary) {

		return new TrimExpressionStateObjectTester(specification, stringPrimary, trimCharacter, hasFrom);
	}

	protected static TrimExpressionStateObjectTester trim(StateObjectTester stringPrimary) {
		return trim(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trim(String trimCharacter,
	                                                      StateObjectTester stringPrimary) {

		return trim(Specification.DEFAULT, trimCharacter, false, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimBoth(StateObjectTester stringPrimary) {
		return trim(Specification.BOTH, null, false, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimBothFrom(char trimCharacter,
	                                                              StateObjectTester stringPrimary) {

		return trimBothFrom(quote(trimCharacter), stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimBothFrom(StateObjectTester stringPrimary) {
		return trimBothFrom(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimBothFrom(String trimCharacter,
	                                                              StateObjectTester stringPrimary) {

		return trim(Specification.BOTH, trimCharacter, true, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimFrom(char trimCharacter,
	                                                          StateObjectTester stringPrimary) {

		return trimFrom(trimCharacter, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimFrom(StateObjectTester stringPrimary) {
		return trimFrom(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimFrom(String trimCharacter,
	                                                          StateObjectTester stringPrimary) {

		return trim(Specification.DEFAULT, trimCharacter, true, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimLeading(char trimCharacter,
	                                                             StateObjectTester stringPrimary) {

		return trimLeading(quote(trimCharacter), stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimLeading(StateObjectTester stringPrimary) {
		return trimLeading(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimLeading(String trimCharacter,
	                                                             StateObjectTester stringPrimary) {

		return trim(Specification.LEADING, trimCharacter, false, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimLeadingFrom(char trimCharacter,
	                                                                 StateObjectTester stringPrimary) {

		return trimLeadingFrom(quote(trimCharacter), stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimLeadingFrom(StateObjectTester stringPrimary) {
		return trimLeadingFrom(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimLeadingFrom(String trimCharacter,
	                                                                 StateObjectTester stringPrimary) {

		return trim(Specification.LEADING, trimCharacter, true, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimTrailing(char trimCharacter,
	                                                              StateObjectTester stringPrimary) {

		return trimTrailing(quote(trimCharacter), stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimTrailing(StateObjectTester stringPrimary) {
		return trimTrailing(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimTrailing(String trimCharacter,
	                                                              StateObjectTester stringPrimary) {

		return trim(Specification.TRAILING, trimCharacter, false, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimTrailingFrom(char trimCharacter,
	                                                                  StateObjectTester stringPrimary) {

		return trimTrailingFrom(quote(trimCharacter), stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimTrailingFrom(StateObjectTester stringPrimary) {
		return trimTrailingFrom(null, stringPrimary);
	}

	protected static TrimExpressionStateObjectTester trimTrailingFrom(String trimCharacter,
	                                                                  StateObjectTester stringPrimary) {

		return trim(Specification.TRAILING, trimCharacter, true, stringPrimary);
	}

	protected static StateObjectTester TRUE() {
		return new KeywordExpressionStateObjectTester(TRUE);
	}

	protected static TypeExpressionStateObjectTester type(StateObjectTester identificationVariable) {
		return new TypeExpressionStateObjectTester(identificationVariable);
	}

	protected static TypeExpressionStateObjectTester type(String identificationVariable) {
		return type(variable(identificationVariable));
	}

	protected static UnknownExpressionStateObjectTester unknown(String unknown) {
		return new UnknownExpressionStateObjectTester(unknown);
	}

	protected static UpdateClauseStateObjectTester update(StateObjectTester rangeVariableDeclaration,
	                                                      StateObjectTester updateItem) {

		return new UpdateClauseStateObjectTester(
			rangeVariableDeclaration,
			updateItem
		);
	}

	protected static UpdateClauseStateObjectTester update(StateObjectTester rangeVariableDeclaration,
	                                                      StateObjectTester... updateItems) {

		return new UpdateClauseStateObjectTester(
			rangeVariableDeclaration,
			collection(updateItems)
		);
	}

	protected static UpdateClauseStateObjectTester update(String abstractSchemaName,
	                                                      StateObjectTester updateItem) {

		return update(
			rangeVariableDeclaration(
				abstractSchemaName(abstractSchemaName),
				virtualVariable()
			),
			updateItem
		);
	}

	protected static UpdateClauseStateObjectTester update(String abstractSchemaName,
	                                                      StateObjectTester... updateItems) {

		return update(
			rangeVariableDeclaration(
				abstractSchemaName(abstractSchemaName),
				virtualVariable()
			),
			updateItems
		);
	}

	protected static UpdateClauseStateObjectTester update(String abstractSchemaName,
	                                                      String identificationVariable,
	                                                      StateObjectTester updateItem) {

		return new UpdateClauseStateObjectTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			updateItem
		);
	}

	protected static UpdateClauseStateObjectTester update(String abstractSchemaName,
	                                                      String identificationVariable,
	                                                      StateObjectTester... updateItems) {

		return new UpdateClauseStateObjectTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			collection(updateItems)
		);
	}

	protected static UpdateClauseStateObjectTester updateAs(String abstractSchemaName,
	                                                        StateObjectTester updateItem) {

		return update(
			rangeVariableDeclarationAs(
				abstractSchemaName(abstractSchemaName),
				nullExpression()),
			updateItem
		);
	}

	protected static UpdateClauseStateObjectTester updateAs(String abstractSchemaName,
	                                                        String identificationVariable,
	                                                        StateObjectTester updateItem) {

		return update(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			updateItem
		);
	}

	protected static UpdateClauseStateObjectTester updateAs(String abstractSchemaName,
	                                                        String identificationVariable,
	                                                        StateObjectTester... updateItems) {

		return update(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			updateItems
		);
	}

	protected static UpdateStatementStateObjectTester updateStatement(StateObjectTester updateClause) {
		return updateStatement(updateClause, nullExpression());
	}

	protected static UpdateStatementStateObjectTester updateStatement(StateObjectTester updateClause,
	                                                                  StateObjectTester whereClause) {

		return new UpdateStatementStateObjectTester(updateClause, whereClause);
	}

	protected static UpperExpressionStateObjectTester upper(StateObjectTester stringPrimary) {
		return new UpperExpressionStateObjectTester(stringPrimary);
	}

	protected static ValueExpressionStateObjectTester value(String identificationVariable) {
		return new ValueExpressionStateObjectTester(identificationVariable);
	}

	protected static IdentificationVariableStateObjectTester variable(String identificationVariable) {
		return new IdentificationVariableStateObjectTester(identificationVariable);
	}

	private static IdentificationVariableStateObjectTester virtualVariable() {
		IdentificationVariableStateObjectTester variable = variable(null);
		variable.virtual = true;
		return variable;
	}

	protected static WhenClauseStateObjectTester when(StateObjectTester conditionalExpression,
	                                                  StateObjectTester thenExpression) {

		return new WhenClauseStateObjectTester(conditionalExpression, thenExpression);
	}

	protected static WhereClauseStateObjectTester where(StateObjectTester conditionalExpression) {
		return new WhereClauseStateObjectTester(conditionalExpression);
	}

	protected JPQLQueryStateObject buildStateObject(String jpqlQuery, boolean tolerant) throws Exception {
		return queryBuilder.buildStateObject(getPersistenceUnit(), jpqlQuery, tolerant);
	}

	protected JPQLGrammar getGrammar() {
		return queryBuilder.getGrammar();
	}

	protected IJPQLQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	protected void test(StateObjectTester tester, JPQLQueryStateObject stateObject, String jpqlQuery) {

		// Test the creation of the StateObject
		jpqlQuery(tester).test(stateObject);

		// Test the IJPQLQueryFormatter
		testFormatter(stateObject, jpqlQuery);
	}

	/**
	 * Tests the injected {@link IJPQLQueryFormatter} and make sure the generated string will be
	 * the same as the given JPQL query.
	 *
	 * @param jpqlStateObject
	 * @param jpqlQuery
	 */
	protected void testFormatter(JPQLQueryStateObject jpqlStateObject, String jpqlQuery) {

		if (queryFormatter != null) {

			if (queryFormatter instanceof DefaultActualJPQLQueryFormatter &&
			    ((DefaultActualJPQLQueryFormatter) queryFormatter).getIdentifierStyle() == IdentifierStyle.CAPITALIZE_EACH_WORD &&
			    ((DefaultActualJPQLQueryFormatter) queryFormatter).isUsingExactMatch()) {

				System.out.println();
			}

			boolean exactMatch = false;
			IdentifierStyle stye = ((BaseJPQLQueryFormatter) queryFormatter).getIdentifierStyle();

			if (queryFormatter instanceof AbstractActualJPQLQueryFormatter) {
				AbstractActualJPQLQueryFormatter formatter = (AbstractActualJPQLQueryFormatter) queryFormatter;
				exactMatch = formatter.isUsingExactMatch();
			}

			jpqlQuery = JPQLQueryBuilder.toText(jpqlQuery, jpqlStateObject.getGrammar(), exactMatch, stye);

			assertEquals(
				"The JPQL query was not generated correctly.",
				jpqlQuery,
				queryFormatter.toString(jpqlStateObject)
			);
		}
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned on.
	 *
	 * @param query The JPQL query to parse and to test the parsed tree representation
	 * @param queryStatement The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 */
	protected void testInvalidQuery(String query, StateObjectTester queryStatement) throws Exception {
		testQuery(query, queryStatement, true);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * This method test both parsing modes: tolerant and non-tolerant. When the tolerant mode is
	 * turned on, it means it will parse valid/complete and invalid/incomplete JPQL queries. When the
	 * tolerant mode is turned off, it means the JPQL query has to be complete and has to be
	 * grammatically valid.
	 *
	 * @param query The JPQL query to parse and to test the parsed tree representation
	 * @param queryStatement The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 */
	protected void testQuery(String query, StateObjectTester queryStatement) throws Exception {
		testValidQuery(query, queryStatement);
		testInvalidQuery(query, queryStatement);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param queryStatement The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse grammatically invalid or incomplete queries
	 */
	protected void testQuery(String jpqlQuery, StateObjectTester queryStatement, boolean tolerant) throws Exception {

		// Create the StateObject
		JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, tolerant);

		if (queryStatement.getClass() != JPQLQueryStateObjectTester.class) {
			queryStatement = jpqlQuery(queryStatement);
		}

		// Compare the StateObject with the equivalent tree
		queryStatement.test(stateObject);

		// Now test the formatter
		testFormatter(stateObject, jpqlQuery);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned off.
	 *
	 * @param query The JPQL query to parse and to test the parsed tree representation
	 * @param queryStatement The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 */
	protected void testValidQuery(String query, StateObjectTester queryStatement) throws Exception {
		testQuery(query, queryStatement, false);
	}

	protected static final class AbsExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected AbsExpressionStateObjectTester(StateObjectTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return AbsExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return ABS;
		}
	}

	protected static abstract class AbstractConditionalClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester conditionalExpression;

		protected AbstractConditionalClauseStateObjectTester(StateObjectTester conditionalExpression) {
			super();
			this.conditionalExpression = conditionalExpression;
		}

		protected abstract Class<? extends AbstractConditionalClauseStateObject> expressionType();

		protected abstract String identifier();

		public void test(StateObject stateObject) {

			assertInstance(stateObject, expressionType());

			AbstractConditionalClauseStateObject conditionalClause = (AbstractConditionalClauseStateObject) stateObject;
			assertEquals(!conditionalExpression.isNull(), conditionalClause.hasConditional());

			conditionalExpression.test(conditionalClause.getConditional());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());
			sb.append(SPACE);
			sb.append(conditionalExpression);
			return sb.toString();
		}
	}

	protected static abstract class AbstractDoubleEncapsulatedExpressionStateObjectTester extends AbstractEncapsulatedExpressionStateObjectTester {

		private StateObjectTester firstExpression;
		private StateObjectTester secondExpression;

		protected AbstractDoubleEncapsulatedExpressionStateObjectTester(StateObjectTester firstExpression,
		                                                                StateObjectTester secondExpression) {

			super();
			this.firstExpression  = firstExpression;
			this.secondExpression = secondExpression;
		}

		@Override
		protected abstract Class<? extends AbstractDoubleEncapsulatedExpressionStateObject> expressionType();

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);

			AbstractDoubleEncapsulatedExpressionStateObject abstractDoubleEncapsulatedExpressionStateObject = (AbstractDoubleEncapsulatedExpressionStateObject) stateObject;
			assertEquals(!firstExpression.isNull(),  abstractDoubleEncapsulatedExpressionStateObject.hasFirst());
			assertEquals(!secondExpression.isNull(), abstractDoubleEncapsulatedExpressionStateObject.hasSecond());

			firstExpression .test(abstractDoubleEncapsulatedExpressionStateObject.getFirst());
			secondExpression.test(abstractDoubleEncapsulatedExpressionStateObject.getSecond());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(firstExpression);
			sb.append(COMMA);
			sb.append(SPACE);
			sb.append(secondExpression);
		}
	}

	protected static abstract class AbstractEncapsulatedExpressionStateObjectTester extends AbstractStateObjectTester {

		protected AbstractEncapsulatedExpressionStateObjectTester() {
			super();
		}

		protected abstract Class<? extends AbstractEncapsulatedExpressionStateObject> expressionType();

		protected abstract String identifier();

		public void test(StateObject stateObject) {
			assertInstance(stateObject, expressionType());
		}

		@Override
		public final String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());
			sb.append(LEFT_PARENTHESIS);
			toStringEncapsulatedExpression(sb);
			sb.append(RIGHT_PARENTHESIS);
			return sb.toString();
		}

		protected abstract void toStringEncapsulatedExpression(StringBuilder sb);
	}

	protected static abstract class AbstractFromClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester declaration;

		protected AbstractFromClauseStateObjectTester(StateObjectTester declaration) {
			super();
			this.declaration = declaration;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, AbstractFromClauseStateObject.class);

			AbstractFromClauseStateObject fromClause = (AbstractFromClauseStateObject) stateObject;
			assertEquals(!declaration.isNull(), fromClause.hasItems());

			declaration.test(fromClause.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(FROM);
			sb.append(SPACE);
			sb.append(declaration);
			return sb.toString();
		}
	}

	protected static abstract class AbstractIdentificationVariableDeclarationStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester joins;
		private StateObjectTester rangeVariableDeclaration;

		protected AbstractIdentificationVariableDeclarationStateObjectTester(StateObjectTester rangeVariableDeclaration,
		                                                                     StateObjectTester joins) {

			super();
			this.rangeVariableDeclaration = rangeVariableDeclaration;
			this.joins                    = joins;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, AbstractIdentificationVariableDeclarationStateObject.class);

			AbstractIdentificationVariableDeclarationStateObject identificationVariableDeclaration = (AbstractIdentificationVariableDeclarationStateObject) stateObject;
			assertEquals(!joins.isNull(), identificationVariableDeclaration.hasItems());

			rangeVariableDeclaration.test(identificationVariableDeclaration.getRangeVariableDeclaration());
			joins.test(identificationVariableDeclaration.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(rangeVariableDeclaration);
			sb.append(SPACE);
			sb.append(joins);
			return sb.toString();
		}
	}

	protected static abstract class AbstractPathExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester identificationVariable;
		private String value;

		protected AbstractPathExpressionStateObjectTester(StateObjectTester identificationVariable,
		                                                  String value) {
			super();

			this.value                  = value;
			this.identificationVariable = identificationVariable;

			if (identificationVariable.isNull() && (value.indexOf(DOT) > -1)) {
				this.identificationVariable = variable(value.substring(0, value.indexOf(DOT)));
			}
			else {
				this.identificationVariable = virtualVariable();
			}
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, AbstractPathExpressionStateObject.class);

			AbstractPathExpressionStateObject abstractPathExpressionStateObject = (AbstractPathExpressionStateObject) stateObject;

			identificationVariable.test(abstractPathExpressionStateObject.getIdentificationVariable());
		}

		@Override
		public String toString() {
			return value;
		}
	}

	protected static abstract class AbstractRangeVariableDeclarationStateObjectTester extends AbstractStateObjectTester {

		private boolean hasAs;
		private StateObjectTester identificationVariable;
		private StateObjectTester root;

		protected AbstractRangeVariableDeclarationStateObjectTester(StateObjectTester root,
		                                                            boolean hasAs,
		                                                            StateObjectTester identificationVariable) {

			super();
			this.hasAs                  = hasAs;
			this.root                   = root;
			this.identificationVariable = identificationVariable;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, AbstractRangeVariableDeclarationStateObject.class);

			AbstractRangeVariableDeclarationStateObject rangeVariableDeclaration = (AbstractRangeVariableDeclarationStateObject) stateObject;
			assertEquals(hasAs, rangeVariableDeclaration.hasAs());

			root.test(rangeVariableDeclaration.getRootStateObject());

			if (!identificationVariable.isNull() || !rangeVariableDeclaration.isIdentificationVariableOptional()) {
				identificationVariable.test(rangeVariableDeclaration.getIdentificationVariableStateObject());
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(root);
			sb.append(SPACE);
			if (hasAs) {
				sb.append(AS);
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	protected static final class AbstractSchemaNameStateObjectTester extends AbstractStateObjectTester {

		private String abstractSchemaName;

		protected AbstractSchemaNameStateObjectTester(String abstractSchemaName) {
			super();
			this.abstractSchemaName = abstractSchemaName;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, AbstractSchemaNameStateObject.class);
		}

		@Override
		public String toString() {
			return abstractSchemaName;
		}
	}

	protected static abstract class AbstractSelectClauseStateObjectTester extends AbstractStateObjectTester {

		private boolean hasDistinct;
		protected StateObjectTester selectExpression;

		protected AbstractSelectClauseStateObjectTester(StateObjectTester selectExpression, boolean hasDistinct) {
			super();
			this.hasDistinct      = hasDistinct;
			this.selectExpression = selectExpression;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, AbstractSelectClauseStateObject.class);
			AbstractSelectClauseStateObject selectClause = (AbstractSelectClauseStateObject) stateObject;
			assertEquals(hasDistinct,                selectClause.hasDistinct());
			assertEquals(!selectExpression.isNull(), selectClause.hasSelectItem());

			testSelectExpression(selectClause, selectExpression);
		}

		protected abstract void testSelectExpression(AbstractSelectClauseStateObject selectClause,
		                                             StateObjectTester selectExpression);

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(SELECT);
			sb.append(SPACE);
			if (hasDistinct) {
				sb.append(DISTINCT);
				sb.append(SPACE);
			}
			sb.append(selectExpression);
			return sb.toString();
		}
	}

	protected static abstract class AbstractSelectStatementStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester fromClause;
		private StateObjectTester groupByClause;
		private StateObjectTester havingClause;
		private StateObjectTester selectClause;
		private StateObjectTester whereClause;

		protected AbstractSelectStatementStateObjectTester(StateObjectTester selectClause,
		                                        StateObjectTester fromClause,
		                                        StateObjectTester whereClause,
		                                        StateObjectTester groupByClause,
		                                        StateObjectTester havingClause) {

			super();

			this.selectClause  = selectClause;
			this.fromClause    = fromClause;
			this.whereClause   = whereClause;
			this.groupByClause = groupByClause;
			this.havingClause  = havingClause;
		}

		protected abstract Class<? extends AbstractSelectStatementStateObject> expressionType();

		public void test(StateObject stateObject) {
			assertInstance(stateObject, expressionType());

			AbstractSelectStatementStateObject selectStatement = (AbstractSelectStatementStateObject) stateObject;

			assertEquals(!whereClause  .isNull(), selectStatement.hasWhereClause());
			assertEquals(!groupByClause.isNull(), selectStatement.hasGroupByClause());
			assertEquals(!havingClause .isNull(), selectStatement.hasHavingClause());

			selectClause .test(selectStatement.getSelectClause());
			fromClause   .test(selectStatement.getFromClause());
			whereClause  .test(selectStatement.getWhereClause());
			groupByClause.test(selectStatement.getGroupByClause());
			havingClause .test(selectStatement.getHavingClause());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();

			sb.append(selectClause);
			sb.append(SPACE);
			sb.append(fromClause);

			if (whereClause != null) {
				sb.append(SPACE);
				sb.append(whereClause);
			}

			if (groupByClause != null) {
				sb.append(SPACE);
				sb.append(groupByClause);
			}

			if (havingClause != null) {
				sb.append(SPACE);
				sb.append(havingClause);
			}

			return sb.toString();
		}
	}

	protected static abstract class AbstractSingleEncapsulatedExpressionStateObjectTester extends AbstractEncapsulatedExpressionStateObjectTester {

		private StateObjectTester expression;

		protected AbstractSingleEncapsulatedExpressionStateObjectTester(StateObjectTester expression) {
			super();
			this.expression = expression;
		}

		@Override
		protected abstract Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType();

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);

			AbstractSingleEncapsulatedExpressionStateObject encapsulatedExpressionStateObject = (AbstractSingleEncapsulatedExpressionStateObject) stateObject;
			this.expression.test(encapsulatedExpressionStateObject.getStateObject());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(expression);
		}
	}

	/**
	 * The abstract definition of an {@link ExpressionStateObjectTester}.
	 */
	protected static abstract class AbstractStateObjectTester implements StateObjectTester {

		public final AdditionExpressionStateObjectTester add(StateObjectTester expression) {
			return AbstractStateObjectTest.add(this, expression);
		}

		public final AndExpressionStateObjectTester and(StateObjectTester expression) {
			return AbstractStateObjectTest.and(this, expression);
		}

		protected final void assertInstance(StateObject stateObject,
		                                    Class<? extends StateObject> expressionType) {

			assertNotNull("The StateObject cannot be null", stateObject);
			Class<? extends StateObject> expressionClass = stateObject.getClass();

			if (expressionClass != expressionType &&
			   !expressionType.isAssignableFrom(expressionClass)) {

				fail(String.format(
					"Expecting %s but was %s for %s",
					expressionType.getSimpleName(),
					expressionClass.getSimpleName(),
					stateObject.toString()
				));
			}
		}

		public final BetweenExpressionStateObjectTester between(StateObjectTester lowerBoundExpression,
		                                                        StateObjectTester upperBoundExpression) {

			return AbstractStateObjectTest.between(this, lowerBoundExpression, upperBoundExpression);
		}

		public final ComparisonExpressionStateObjectTester different(StateObjectTester expression) {
			return AbstractStateObjectTest.different(this, expression);
		}

		public final DivisionExpressionStateObjectTester division(StateObjectTester expression) {
			return AbstractStateObjectTest.division(this, expression);
		}

		public final ComparisonExpressionStateObjectTester equal(StateObjectTester expression) {
			return AbstractStateObjectTest.equal(this, expression);
		}

		public final ComparisonExpressionStateObjectTester greaterThan(StateObjectTester expression) {
			return AbstractStateObjectTest.greaterThan(this, expression);
		}

		public final ComparisonExpressionStateObjectTester greaterThanOrEqual(StateObjectTester expression) {
			return AbstractStateObjectTest.greaterThanOrEqual(this, expression);
		}

		public final InExpressionStateObjectTester in(StateObjectTester... inItems) {
			if (inItems.length == 1) {
				return AbstractStateObjectTest.in(this, inItems[0]);
			}
			return AbstractStateObjectTest.in(this, collection(inItems));
		}

		public final EmptyCollectionComparisonExpressionStateObjectTester isEmpty() {
			return AbstractStateObjectTest.isEmpty(this);
		}

		public final EmptyCollectionComparisonExpressionStateObjectTester isNotEmpty() {
			return AbstractStateObjectTest.isNotEmpty(this);
		}

		public boolean isNull() {
			return false;
		}

		public final LikeExpressionStateObjectTester like(StateObjectTester patternValue) {
			return AbstractStateObjectTest.like(this, patternValue);
		}

		public final LikeExpressionStateObjectTester like(StateObjectTester patternValue,
		                                                  String escapeCharacter) {

			return AbstractStateObjectTest.like(this, patternValue, escapeCharacter);
		}

		public final ComparisonExpressionStateObjectTester lowerThan(StateObjectTester expression) {
			return AbstractStateObjectTest.lowerThan(this, expression);
		}

		public final ComparisonExpressionStateObjectTester lowerThanOrEqual(StateObjectTester expression) {
			return AbstractStateObjectTest.lowerThanOrEqual(this, expression);
		}

		public final CollectionMemberExpressionStateObjectTester member(StateObjectTester collectionValuedPathExpression) {
			return AbstractStateObjectTest.member(this, collectionValuedPathExpression);
		}

		public final CollectionMemberExpressionStateObjectTester memberOf(StateObjectTester collectionValuedPathExpression) {
			return AbstractStateObjectTest.memberOf(this, collectionValuedPathExpression);
		}

		public final MultiplicationExpressionStateObjectTester multiplication(StateObjectTester expression) {
			return AbstractStateObjectTest.multiplication(this, expression);
		}

		public final BetweenExpressionStateObjectTester notBetween(StateObjectTester lowerBoundExpression,
		                                                StateObjectTester upperBoundExpression) {

			return AbstractStateObjectTest.notBetween(this, lowerBoundExpression, upperBoundExpression);
		}

		public final InExpressionStateObjectTester notIn(StateObjectTester... inItems) {
			if (inItems.length == 1) {
				return AbstractStateObjectTest.notIn(this, inItems[0]);
			}
			return AbstractStateObjectTest.notIn(this, collection(inItems));
		}

		public final LikeExpressionStateObjectTester notLike(StateObjectTester expression) {
			return AbstractStateObjectTest.notLike(this, expression);
		}

		public final LikeExpressionStateObjectTester notLike(StateObjectTester expression,
		                                                     String escapeCharacter) {

			return AbstractStateObjectTest.notLike(this, expression, escapeCharacter);
		}

		public final StateObjectTester notMember(StateObjectTester collectionValuedPathExpression) {
			return AbstractStateObjectTest.notMember(this, collectionValuedPathExpression);
		}

		public final StateObjectTester notMemberOf(StateObjectTester collectionValuedPathExpression) {
			return AbstractStateObjectTest.notMemberOf(this, collectionValuedPathExpression);
		}

		public final OrExpressionStateObjectTester or(StateObjectTester expression) {
			return AbstractStateObjectTest.or(this, expression);
		}

		public final SubstractionExpressionStateObjectTester substract(StateObjectTester expression) {
			return AbstractStateObjectTest.substract(this, expression);
		}

		public void test(IterableListIterator<? extends StateObject> items) {
			if (!items.hasNext()) {
				fail("State objects should be present.");
			}
			else {
				test(items.next());
				if (items.hasNext()) {
					fail("Only one StateObject child should be present");
				}
			}
		}
	}

	protected static abstract class AbstractTripleEncapsulatedExpressionStateObjectTester extends AbstractEncapsulatedExpressionStateObjectTester {

		private StateObjectTester firstExpression;
		public boolean hasFirstComma;
		public boolean hasSecondComma;
		public boolean hasSpaceAfterFirstComma;
		public boolean hasSpaceAfterSecondComma;
		private StateObjectTester secondExpression;
		private StateObjectTester thirdExpression;

		protected AbstractTripleEncapsulatedExpressionStateObjectTester(StateObjectTester firstExpression,
		                                                     StateObjectTester secondExpression,
		                                                     StateObjectTester thirdExpression) {

			super();

			this.firstExpression  = firstExpression;
			this.secondExpression = secondExpression;
			this.thirdExpression  = thirdExpression;

			hasFirstComma  = !secondExpression.isNull();
			hasSecondComma = !thirdExpression.isNull();

			hasSpaceAfterFirstComma  = hasFirstComma;
			hasSpaceAfterSecondComma = hasSecondComma;
		}

		@Override
		protected abstract Class<? extends AbstractTripleEncapsulatedExpressionStateObject> expressionType();

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, AbstractTripleEncapsulatedExpressionStateObject.class);

			AbstractTripleEncapsulatedExpressionStateObject tripleExpressionStateObject = (AbstractTripleEncapsulatedExpressionStateObject) stateObject;

			assertEquals(!firstExpression.isNull(),  tripleExpressionStateObject.hasFirst());
			assertEquals(!secondExpression.isNull(), tripleExpressionStateObject.hasSecond());
			assertEquals(!thirdExpression.isNull(),  tripleExpressionStateObject.hasThird());

			firstExpression.test(tripleExpressionStateObject.getFirst());
			secondExpression.test(tripleExpressionStateObject.getSecond());
			thirdExpression.test(tripleExpressionStateObject.getThird());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(firstExpression);
			sb.append(COMMA);
			sb.append(SPACE);
			sb.append(secondExpression);
			sb.append(COMMA);
			sb.append(SPACE);
			sb.append(thirdExpression);
		}
	}

	protected static final class AdditionExpressionStateObjectTester extends CompoundExpressionStateObjectTester {

		protected AdditionExpressionStateObjectTester(StateObjectTester leftExpression,
		                                   StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpressionStateObject> expressionType() {
			return AdditionExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return PLUS;
		}
	}

	protected static abstract class AggregateFunctionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		public boolean hasDistinct;
		public boolean hasSpaceAfterDistinct;

		protected AggregateFunctionStateObjectTester(StateObjectTester expression, boolean hasDistinct) {
			super(expression);
			this.hasDistinct = hasDistinct;
			this.hasSpaceAfterDistinct = hasDistinct;
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {

			if (hasDistinct) {
				sb.append(DISTINCT);
			}

			if (hasSpaceAfterDistinct) {
				sb.append(SPACE);
			}

			super.toStringEncapsulatedExpression(sb);
		}
	}

	protected static final class AllOrAnyExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		private String identifier;

		protected AllOrAnyExpressionStateObjectTester(String identifier, StateObjectTester expression) {
			super(expression);
			this.identifier = identifier;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return AllOrAnyExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return identifier;
		}
	}

	protected static final class AndExpressionStateObjectTester extends LogicalExpressionStateObjectTester {

		protected AndExpressionStateObjectTester(StateObjectTester leftExpression,
		                              StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpressionStateObject> expressionType() {
			return AndExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return AND;
		}
	}

	protected static final class ArithmeticFactorStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester expression;
		private String sign;

		protected ArithmeticFactorStateObjectTester(String sign, StateObjectTester expression) {
			super();
			this.sign = sign;
			this.expression = expression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, ArithmeticFactorStateObject.class);

			ArithmeticFactorStateObject factor = (ArithmeticFactorStateObject) expression;
			assertEquals(sign == MINUS, factor.hasMinusSign());
			assertEquals(sign == PLUS,  factor.hasPlusSign());

			this.expression.test(factor.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(sign);
			sb.append(expression);
			return sb.toString();
		}
	}

	protected static final class AvgFunctionStateObjectTester extends AggregateFunctionStateObjectTester {

		protected AvgFunctionStateObjectTester(StateObjectTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return AvgFunctionStateObject.class;
		}

		@Override
		protected String identifier() {
			return AVG;
		}
	}

	protected static final class BetweenExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester expression;
		private boolean hasNot;
		private StateObjectTester lowerBoundExpression;
		private StateObjectTester upperBoundExpression;

		protected BetweenExpressionStateObjectTester(StateObjectTester expression,
		                                  boolean hasNot,
		                                  StateObjectTester lowerBoundExpression,
		                                  StateObjectTester upperBoundExpression) {

			super();
			this.hasNot                  = hasNot;
			this.expression              = expression;
			this.lowerBoundExpression    = lowerBoundExpression;
			this.upperBoundExpression    = upperBoundExpression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, BetweenExpressionStateObject.class);

			BetweenExpressionStateObject betweenExpressionStateObject = (BetweenExpressionStateObject) stateObject;
			assertEquals(!this.expression.isNull(),      betweenExpressionStateObject.hasStateObject());
			assertEquals(!lowerBoundExpression.isNull(), betweenExpressionStateObject.hasLowerBound());
			assertEquals(hasNot,                         betweenExpressionStateObject.hasNot());
			assertEquals(!upperBoundExpression.isNull(), betweenExpressionStateObject.hasUpperBound());

			this.expression.test(betweenExpressionStateObject.getStateObject());
			lowerBoundExpression.test(betweenExpressionStateObject.getLowerBound());
			upperBoundExpression.test(betweenExpressionStateObject.getUpperBound());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(expression);
			if (!expression.isNull()) {
				sb.append(SPACE);
			}
			if (hasNot) {
				sb.append(NOT);
				sb.append(SPACE);
			}
			sb.append(BETWEEN);
			sb.append(SPACE);
			sb.append(lowerBoundExpression);
			sb.append(SPACE);
			sb.append(AND);
			sb.append(SPACE);
			sb.append(upperBoundExpression);
			return sb.toString();
		}
	}

	protected static final class CaseExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester caseOperand;
		private StateObjectTester elseExpression;
		private StateObjectTester whenClauses;

		protected CaseExpressionStateObjectTester(StateObjectTester caseOperand,
		                               StateObjectTester whenClauses,
		                               StateObjectTester elseExpression) {

			super();
			this.whenClauses    = whenClauses;
			this.caseOperand    = caseOperand;
			this.elseExpression = elseExpression;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, CaseExpressionStateObject.class);

			CaseExpressionStateObject caseExpressionStateObject = (CaseExpressionStateObject) stateObject;
			assertEquals(!whenClauses.isNull(), caseExpressionStateObject.hasItems());

			caseOperand.test(caseExpressionStateObject.getCaseOperand());
			whenClauses.test(caseExpressionStateObject.items());
			elseExpression.test(caseExpressionStateObject.getElse());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(CASE);
			sb.append(SPACE);
			sb.append(caseOperand);
			sb.append(SPACE);
			sb.append(whenClauses);
			sb.append(SPACE);
			sb.append(ELSE);
			sb.append(SPACE);
			sb.append(elseExpression);
			sb.append(SPACE);
			sb.append(END);
			return sb.toString();
		}
	}

	protected static class CoalesceExpressionStateObjectTester extends AbstractEncapsulatedExpressionStateObjectTester {

		private StateObjectTester stateObject;

		protected CoalesceExpressionStateObjectTester(StateObjectTester stateObject) {
			super();
			this.stateObject = stateObject;
		}

		@Override
		protected Class<? extends AbstractEncapsulatedExpressionStateObject> expressionType() {
			return CoalesceExpressionStateObject.class;
		}

		@Override
		protected String identifier(){
			return COALESCE;
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);

			CoalesceExpressionStateObject coalesceExpressionStateObject = (CoalesceExpressionStateObject) stateObject;
			assertEquals(!this.stateObject.isNull(), coalesceExpressionStateObject.hasItems());

			this.stateObject.test(coalesceExpressionStateObject.items());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(stateObject);
		}
	}

	protected static final class CollectionExpressionStateObjectTester extends AbstractStateObjectTester {

		private Boolean[] commas;
		private StateObjectTester[] expressionStateObjectTesters;
		private Boolean[] spaces;

		protected CollectionExpressionStateObjectTester(StateObjectTester[] expressionStateObjectTesters,
		                                                Boolean[] commas,
		                                                Boolean[] spaces) {

			super();
			this.expressionStateObjectTesters = expressionStateObjectTesters;
			this.spaces            = spaces;
			this.commas            = commas;
		}

		@Override
		public void test(IterableListIterator<? extends StateObject> items) {
			int index = 0;
			for (StateObject item : items) {
				if (index == expressionStateObjectTesters.length) {
					fail("The children size is different: " + expressionStateObjectTesters.length);
				}
				expressionStateObjectTesters[index++].test(item);
			}
			if (index < expressionStateObjectTesters.length) {
				fail("The children size is different: " + expressionStateObjectTesters.length);
			}
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, AbstractListHolderStateObject.class);

			AbstractListHolderStateObject<?> collectionExpressionStateObject = (AbstractListHolderStateObject<?>) stateObject;
			assertEquals(spaces.length,                       collectionExpressionStateObject.items());
			assertEquals(commas.length,                       collectionExpressionStateObject.itemsSize());
			assertEquals(expressionStateObjectTesters.length, collectionExpressionStateObject.itemsSize());

			// Expressions
			for (int index = expressionStateObjectTesters.length; --index >= 0; ) {
				expressionStateObjectTesters[index].test(collectionExpressionStateObject.getItem(index));
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			for (int index = 0, count = expressionStateObjectTesters.length; index < count; index++) {
				sb.append(expressionStateObjectTesters[index]);

				if ((index < commas.length) && commas[index]) {
					sb.append(COMMA);
				}

				if ((index < spaces.length) && spaces[index]) {
					sb.append(SPACE);
				}
			}

			return sb.toString();
		}
	}

	protected static final class CollectionMemberDeclarationStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester collectionValuedPath;
		private boolean derived;
		public boolean hasAs;
		public boolean hasLeftParenthesis;
		public boolean hasRightParenthesis;
		public boolean hasSpaceAfterIn;
		private StateObjectTester identificationVariable;

		protected CollectionMemberDeclarationStateObjectTester(StateObjectTester collectionValuedPath,
		                                                       boolean hasAs,
		                                                       StateObjectTester identificationVariable,
		                                                       boolean derived) {

			super();
			this.derived                = derived;
			this.hasAs                  = hasAs;
			this.collectionValuedPath   = collectionValuedPath;
			this.identificationVariable = identificationVariable;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, CollectionMemberDeclarationStateObject.class);

			CollectionMemberDeclarationStateObject collectionMemberDeclaration = (CollectionMemberDeclarationStateObject) stateObject;
			assertEquals(hasAs,   collectionMemberDeclaration.hasAs());
			assertEquals(derived, collectionMemberDeclaration.isDerived());

			collectionValuedPath.test(collectionMemberDeclaration.getCollectionValuedPath());

			if (!derived) {
				identificationVariable.test(collectionMemberDeclaration.getIdentificationVariable());
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(IN);
			if (hasSpaceAfterIn) {
				sb.append(SPACE);
			}
			else if (hasLeftParenthesis) {
				sb.append(LEFT_PARENTHESIS);
			}
			sb.append(collectionValuedPath);
			if (hasRightParenthesis) {
				sb.append(RIGHT_PARENTHESIS);
			}
			else {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	protected static final class CollectionMemberExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester collectionValuedPathExpression;
		private StateObjectTester entityExpression;
		private boolean hasNot;
		private boolean hasOf;

		protected CollectionMemberExpressionStateObjectTester(StateObjectTester entityExpression,
		                                           boolean hasNot,
		                                           boolean hasOf,
		                                           StateObjectTester collectionValuedPathExpression) {

			super();
			this.hasNot                         = hasNot;
			this.hasOf                          = hasOf;
			this.entityExpression               = entityExpression;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, CollectionMemberExpressionStateObject.class);

			CollectionMemberExpressionStateObject collectionMemberExpressionStateObject = (CollectionMemberExpressionStateObject) stateObject;
			assertEquals(!entityExpression.isNull(), collectionMemberExpressionStateObject.hasEntityStateObject());
			assertEquals(hasNot,                     collectionMemberExpressionStateObject.hasNot());
			assertEquals(hasOf,                      collectionMemberExpressionStateObject.hasOf());

			entityExpression.test(collectionMemberExpressionStateObject.getEntityStateObject());
			collectionValuedPathExpression.test(collectionMemberExpressionStateObject.getCollectionValuedPath());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(entityExpression);
			if (!entityExpression.isNull()) {
				sb.append(SPACE);
			}
			if (hasNot) {
				sb.append(NOT);
				sb.append(SPACE);
			}
			if (hasOf) {
				sb.append(MEMBER_OF);
				sb.append(SPACE);
			}
			else {
				sb.append(MEMBER);
				sb.append(SPACE);
			}
			sb.append(collectionValuedPathExpression);
			return sb.toString();
		}
	}

	protected static final class CollectionValuedPathExpressionStateObjectTester extends AbstractPathExpressionStateObjectTester {

		protected CollectionValuedPathExpressionStateObjectTester(StateObjectTester identificationVariable,
		                                                          String value) {

			super(identificationVariable, value);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, CollectionValuedPathExpressionStateObject.class);
		}
	}

	protected static final class ComparisonExpressionStateObjectTester extends AbstractStateObjectTester {

		private String comparator;
		private StateObjectTester leftExpression;
		private StateObjectTester rightExpression;

		protected ComparisonExpressionStateObjectTester(String comparator,
		                                                StateObjectTester leftExpression,
		                                                StateObjectTester rightExpression) {

			super();
			this.comparator      = comparator;
			this.leftExpression  = leftExpression;
			this.rightExpression = rightExpression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, ComparisonExpressionStateObject.class);

			ComparisonExpressionStateObject comparisonExpressionStateObject = (ComparisonExpressionStateObject) stateObject;
			assertEquals(!leftExpression.isNull(),  comparisonExpressionStateObject.hasLeft());
			assertEquals(!rightExpression.isNull(), comparisonExpressionStateObject.hasRight());

			leftExpression .test(comparisonExpressionStateObject.getLeft());
			rightExpression.test(comparisonExpressionStateObject.getRight());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(leftExpression);
			if (!leftExpression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(comparator);
			sb.append(SPACE);
			sb.append(rightExpression);
			return sb.toString();
		}
	}

	protected static abstract class CompoundExpressionStateObjectTester extends AbstractStateObjectTester {

		public boolean hasSpaceAfterIdentifier;
		private StateObjectTester leftExpression;
		private StateObjectTester rightExpression;

		protected CompoundExpressionStateObjectTester(StateObjectTester leftExpression,
		                                              StateObjectTester rightExpression) {

			super();
			this.leftExpression          = leftExpression;
			this.rightExpression         = rightExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		protected abstract Class<? extends CompoundExpressionStateObject> expressionType();

		protected abstract String identifier();

		public void test(StateObject stateObject) {
			assertInstance(stateObject, expressionType());

			CompoundExpressionStateObject compoundExpressionStateObject = (CompoundExpressionStateObject) stateObject;
			assertEquals(!leftExpression .isNull(), compoundExpressionStateObject.hasLeft());
			assertEquals(!rightExpression.isNull(), compoundExpressionStateObject.hasRight());

			leftExpression .test(compoundExpressionStateObject.getLeft());
			rightExpression.test(compoundExpressionStateObject.getRight());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(leftExpression);
			if (!leftExpression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(identifier());
			sb.append(SPACE);
			sb.append(rightExpression);
			return sb.toString();
		}
	}

	protected static final class ConcatExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester stateObject;

		protected ConcatExpressionStateObjectTester(StateObjectTester stateObject) {
			super();
			this.stateObject = stateObject;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, ConcatExpressionStateObject.class);

			ConcatExpressionStateObject concatExpressionStateObject = (ConcatExpressionStateObject) stateObject;
			assertEquals(!this.stateObject.isNull(), concatExpressionStateObject.hasItems());

			this.stateObject.test(concatExpressionStateObject.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(CONCAT);
			sb.append(LEFT_PARENTHESIS);
			sb.append(stateObject);
			sb.append(RIGHT_PARENTHESIS);
			return sb.toString();
		}
	}

	protected static final class ConstructorExpressionStateObjectTester extends AbstractStateObjectTester {

		private String className;
		private StateObjectTester constructorItems;

		protected ConstructorExpressionStateObjectTester(String className,
		                                                 StateObjectTester constructorItems) {

			super();
			this.className           = className;
			this.constructorItems    = constructorItems;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, ConstructorExpressionStateObject.class);

			ConstructorExpressionStateObject constructorExpressionStateObject = (ConstructorExpressionStateObject) stateObject;
			assertEquals(!constructorItems.isNull(), constructorExpressionStateObject.hasItems());

			constructorItems.test(constructorExpressionStateObject.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(NEW);
			sb.append(SPACE);
			sb.append(className);
			sb.append(LEFT_PARENTHESIS);
			sb.append(constructorItems);
			sb.append(RIGHT_PARENTHESIS);
			return sb.toString();
		}
	}

	protected static final class CountFunctionStateObjectTester extends AggregateFunctionStateObjectTester {

		protected CountFunctionStateObjectTester(StateObjectTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return CountFunctionStateObject.class;
		}

		@Override
		protected String identifier() {
			return COUNT;
		}
	}

	protected static final class DateTimeStateObjectTester extends AbstractStateObjectTester {

		private String dateTime;

		protected DateTimeStateObjectTester(String dateTime) {
			super();
			this.dateTime = dateTime;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, DateTimeStateObject.class);
		}

		@Override
		public String toString() {
			return dateTime;
		}
	}

	/**
	 * StateObjectTester for {@link DeleteClause}.
	 */
	protected static final class DeleteClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester rangeVariableDeclaration;

		protected DeleteClauseStateObjectTester(StateObjectTester rangeVariableDeclaration) {
			super();
			this.rangeVariableDeclaration = rangeVariableDeclaration;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, DeleteClauseStateObject.class);

			DeleteClauseStateObject deleteClause = (DeleteClauseStateObject) stateObject;
			rangeVariableDeclaration.test(deleteClause.getRangeVariableDeclaration());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(DELETE);
			sb.append(SPACE);
			sb.append(FROM);
			sb.append(SPACE);
			sb.append(rangeVariableDeclaration);
			return sb.toString();
		}
	}

	protected static final class DeleteStatementStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester deleteClause;
		private StateObjectTester whereClause;

		protected DeleteStatementStateObjectTester(StateObjectTester deleteClause, StateObjectTester whereClause) {
			super();
			this.deleteClause = deleteClause;
			this.whereClause  = whereClause;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, DeleteStatementStateObject.class);

			DeleteStatementStateObject deleteStatement = (DeleteStatementStateObject) stateObject;
			assertEquals(!whereClause.isNull(), deleteStatement.hasWhereClause());

			deleteClause.test(deleteStatement.getModifyClause());
			whereClause .test(deleteStatement.getWhereClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(deleteClause);
			sb.append(SPACE);
			sb.append(whereClause);
			return sb.toString();
		}
	}

	protected static final class DerivedPathIdentificationVariableDeclarationStateObjectTester extends AbstractIdentificationVariableDeclarationStateObjectTester {

		public DerivedPathIdentificationVariableDeclarationStateObjectTester(StateObjectTester derivedRangeVariableDeclaration,
		                                                                     StateObjectTester joins) {

			super(derivedRangeVariableDeclaration, joins);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, DerivedPathIdentificationVariableDeclarationStateObject.class);
		}
	}

	protected static final class DerivedPathVariableDeclarationStateObjectTester extends AbstractRangeVariableDeclarationStateObjectTester {

		public DerivedPathVariableDeclarationStateObjectTester(StateObjectTester path,
		                                                       boolean hasAs,
		                                                       StateObjectTester identificationVariable) {

			super(path, hasAs, identificationVariable);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, DerivedPathVariableDeclarationStateObject.class);
		}
	}

	protected static final class DivisionExpressionStateObjectTester extends CompoundExpressionStateObjectTester {

		protected DivisionExpressionStateObjectTester(StateObjectTester leftExpression,
		                                   StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpressionStateObject> expressionType() {
			return DivisionExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return DIVISION;
		}
	}

	protected static final class EmptyCollectionComparisonExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester collectionValuedPathExpression;
		private boolean hasNot;

		protected EmptyCollectionComparisonExpressionStateObjectTester(StateObjectTester collectionValuedPathExpression,
		                                                    boolean hasNot) {

			super();
			this.hasNot = hasNot;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, EmptyCollectionComparisonExpressionStateObject.class);

			EmptyCollectionComparisonExpressionStateObject emptyCollection = (EmptyCollectionComparisonExpressionStateObject) stateObject;
			assertEquals(hasNot, emptyCollection.hasNot());

			collectionValuedPathExpression.test(emptyCollection.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(collectionValuedPathExpression);
			if (!collectionValuedPathExpression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(IS);
			sb.append(SPACE);
			if (hasNot) {
				sb.append(NOT);
				sb.append(SPACE);
			}
			sb.append(EMPTY);
			return sb.toString();
		}
	}

	protected static abstract class EncapsulatedIdentificationVariableExpressionStateObjectTester extends AbstractEncapsulatedExpressionStateObjectTester {

		private String identificationVariable;

		protected EncapsulatedIdentificationVariableExpressionStateObjectTester(String identificationVariable) {
			super();
			this.identificationVariable = identificationVariable;
		}

		@Override
		protected abstract Class<? extends EncapsulatedIdentificationVariableExpressionStateObject> expressionType();

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);

			EncapsulatedIdentificationVariableExpressionStateObject object = (EncapsulatedIdentificationVariableExpressionStateObject) stateObject;
			assertEquals(identificationVariable, object.getIdentificationVariable());;
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			if (identificationVariable != null) {
				sb.append(identificationVariable);
			}
		}
	}

	protected static final class EntityTypeLiteralStateObjectTester extends AbstractStateObjectTester {

		private String entityType;

		protected EntityTypeLiteralStateObjectTester(String entityType) {
			super();
			this.entityType = entityType;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, EntityTypeLiteralStateObject.class);
		}

		@Override
		public String toString() {
			return entityType;
		}
	}

	protected static final class EntryExpressionStateObjectTester extends EncapsulatedIdentificationVariableExpressionStateObjectTester {

		protected EntryExpressionStateObjectTester(String identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<EntryExpressionStateObject> expressionType() {
			return EntryExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return ENTRY;
		}
	}

	protected static final class ExistsExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		private boolean hasNot;

		protected ExistsExpressionStateObjectTester(StateObjectTester subquery, boolean hasNot) {
			super(subquery);
			this.hasNot = hasNot;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return ExistsExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return hasNot ? NOT_EXISTS : EXISTS;
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, ExistsExpressionStateObject.class);

			ExistsExpressionStateObject existsExpressionStateObject = (ExistsExpressionStateObject) stateObject;
			assertEquals(hasNot, existsExpressionStateObject.hasNot());
		}
	}

	protected static final class FromClauseStateObjectTester extends AbstractFromClauseStateObjectTester {

		protected FromClauseStateObjectTester(StateObjectTester declarations) {
			super(declarations);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, FromClauseStateObject.class);
		}
	}

	protected static final class GroupByClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester groupByItems;

		protected GroupByClauseStateObjectTester(StateObjectTester groupByItems) {
			super();
			this.groupByItems = groupByItems;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, GroupByClauseStateObject.class);

			GroupByClauseStateObject groupByClause = (GroupByClauseStateObject) stateObject;
			assertEquals(!groupByItems.isNull(), groupByClause.hasItems());

			groupByItems.test(groupByClause.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(GROUP_BY);
			sb.append(SPACE);
			sb.append(groupByItems);
			return sb.toString();
		}
	}

	protected static final class HavingClauseStateObjectTester extends AbstractConditionalClauseStateObjectTester {

		protected HavingClauseStateObjectTester(StateObjectTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		protected Class<? extends AbstractConditionalClauseStateObject> expressionType() {
			return HavingClauseStateObject.class;
		}

		@Override
		protected String identifier() {
			return HAVING;
		}
	}

	protected static final class IdentificationVariableDeclarationStateObjectTester extends AbstractIdentificationVariableDeclarationStateObjectTester {

		public IdentificationVariableDeclarationStateObjectTester(StateObjectTester rangeVariableDeclaration,
		                                                          StateObjectTester joins) {

			super(rangeVariableDeclaration, joins);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, IdentificationVariableDeclarationStateObject.class);
		}
	}

	protected static final class IdentificationVariableStateObjectTester extends AbstractStateObjectTester {

		private String identificationVariable;
		public boolean virtual;

		protected IdentificationVariableStateObjectTester(String identificationVariable) {
			super();
			this.identificationVariable = identificationVariable;
		}

		public void test(StateObject stateObject) {

			if (virtual && (stateObject == null)) {
				return;
			}

			assertInstance(stateObject, IdentificationVariableStateObject.class);

			IdentificationVariableStateObject identificationVariableStateObject = (IdentificationVariableStateObject) stateObject;
			assertEquals(virtual, identificationVariableStateObject.isVirtual());

			if (!virtual) {
				assertEquals(identificationVariable, identificationVariableStateObject.getText());
			}
		}

		@Override
		public String toString() {
			return identificationVariable;
		}
	}

	protected static final class IndexExpressionStateObjectTester extends AbstractEncapsulatedExpressionStateObjectTester {

		private String identificationVariable;

		protected IndexExpressionStateObjectTester(String identificationVariable) {
			super();
			this.identificationVariable = identificationVariable;
		}

		@Override
		protected Class<? extends AbstractEncapsulatedExpressionStateObject> expressionType() {
			return IndexExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return INDEX;
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			if (identificationVariable != null) {
				sb.append(identificationVariable);
			}
		}
	}

	protected static final class InExpressionStateObjectTester extends AbstractStateObjectTester {

		private boolean hasNot;
		private StateObjectTester inItems;
		private StateObjectTester stateFieldPath;

		protected InExpressionStateObjectTester(StateObjectTester stateFieldPath,
		                                        boolean hasNot,
		                                        StateObjectTester inItems) {

			super();
			this.stateFieldPath = stateFieldPath;
			this.hasNot         = hasNot;
			this.inItems        = inItems;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, InExpressionStateObject.class);

			InExpressionStateObject inExpressionStateObject = (InExpressionStateObject) stateObject;
			assertEquals(hasNot,            inExpressionStateObject.hasNot());
			assertEquals(!inItems.isNull(), inExpressionStateObject.hasItems());

			inItems.test(inExpressionStateObject.items());
			stateFieldPath.test(inExpressionStateObject.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPath);
			sb.append(hasNot ? NOT_IN : IN);
			sb.append(SPACE);
			sb.append(LEFT_PARENTHESIS);
			sb.append(inItems);
			sb.append(RIGHT_PARENTHESIS);
			return sb.toString();
		}
	}

	protected static final class InputParameterStateObjectTester extends AbstractStateObjectTester {

		private String inputParameter;

		protected InputParameterStateObjectTester(String inputParameter) {
			super();
			this.inputParameter = inputParameter;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, InputParameterStateObject.class);

			InputParameterStateObject inputParameter = (InputParameterStateObject) stateObject;
			assertEquals(this.inputParameter.charAt(0) == '?', inputParameter.isPositional());
			assertEquals(this.inputParameter.charAt(0) == ':', inputParameter.isNamed());
		}

		@Override
		public String toString() {
			return inputParameter;
		}
	}

	protected static final class JoinFetchStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester joinAssociationPath;
		private String joinType;

		protected JoinFetchStateObjectTester(String joinType, StateObjectTester joinAssociationPath) {
			super();
			this.joinType            = joinType;
			this.joinAssociationPath = joinAssociationPath;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, JoinFetchStateObject.class);

			JoinFetchStateObject join = (JoinFetchStateObject) stateObject;
			assertEquals(joinType, join.getJoinType());

			joinAssociationPath.test(join.getJoinAssociationPathStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			sb.append(SPACE);
			sb.append(joinAssociationPath);
			return sb.toString();
		}
	}

	protected static final class JoinStateObjectTester extends AbstractStateObjectTester {

		private boolean hasAs;
		private StateObjectTester identificationVariable;
		private StateObjectTester joinAssociationPath;
		private String joinType;

		protected JoinStateObjectTester(String joinType,
		                     StateObjectTester joinAssociationPath,
		                     boolean hasAs,
		                     StateObjectTester identificationVariable) {

			super();
			this.joinType               = joinType;
			this.joinAssociationPath    = joinAssociationPath;
			this.hasAs                  = hasAs;
			this.identificationVariable = identificationVariable;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, JoinStateObject.class);

			JoinStateObject join = (JoinStateObject) stateObject;
			assertEquals(joinType, join.getJoinType());
			assertEquals(hasAs,    join.hasAs());

			joinAssociationPath.test(join.getJoinAssociationPathStateObject());
			identificationVariable.test(join.getIdentificationVariableStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			sb.append(SPACE);
			sb.append(joinAssociationPath);
			sb.append(SPACE);
			if (hasAs) {
				sb.append(AS);
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	protected static final class JPQLQueryStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester queryStatement;

		protected JPQLQueryStateObjectTester(StateObjectTester queryStatement) {
			super();
			this.queryStatement = queryStatement;
		}

		public void test(StateObject stateObject) {

			JPQLQueryStateObject jpqlQueryStateObject = (JPQLQueryStateObject) stateObject;
			queryStatement.test(jpqlQueryStateObject.getQueryStatement());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(queryStatement);
			return sb.toString();
		}
	}

	protected static final class KeyExpressionStateObjectTester extends EncapsulatedIdentificationVariableExpressionStateObjectTester {

		protected KeyExpressionStateObjectTester(String identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<KeyExpressionStateObject> expressionType() {
			return KeyExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return VALUE;
		}
	}

	protected static final class KeywordExpressionStateObjectTester extends AbstractStateObjectTester {

		private String keyword;

		protected KeywordExpressionStateObjectTester(String keyword) {
			super();
			this.keyword = keyword;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, KeywordExpressionStateObject.class);
		}

		@Override
		public String toString() {
			return keyword;
		}
	}

	protected static final class LengthExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected LengthExpressionStateObjectTester(StateObjectTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return LengthExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return LENGTH;
		}
	}

	protected static final class LikeExpressionStateObjectTester extends AbstractStateObjectTester {

		private String escapeCharacter;
		private boolean hasNot;
		private StateObjectTester patternValue;
		private StateObjectTester stringExpression;

		protected LikeExpressionStateObjectTester(StateObjectTester stringExpression,
		                                          boolean hasNot,
		                                          StateObjectTester patternValue,
		                                          String escapeCharacter) {

			super();
			this.hasNot           = hasNot;
			this.stringExpression = stringExpression;
			this.patternValue     = patternValue;
			this.escapeCharacter  = escapeCharacter;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, LikeExpressionStateObject.class);

			LikeExpressionStateObject likeExpressionStateObject = (LikeExpressionStateObject) stateObject;
			assertEquals(hasNot,                 likeExpressionStateObject.hasNot());
			assertEquals(!patternValue.isNull(), likeExpressionStateObject.hasPatternValue());
			assertEquals(escapeCharacter,        likeExpressionStateObject.getEscapeCharacter());

			stringExpression.test(likeExpressionStateObject.getStringStateObject());
			patternValue    .test(likeExpressionStateObject.getPatternValue());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(stringExpression);
			if (!stringExpression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(hasNot ? NOT_LIKE : LIKE);
			sb.append(SPACE);
			sb.append(patternValue);
			sb.append(SPACE);
			if (escapeCharacter != null) {
				sb.append(ESCAPE);
				sb.append(SPACE);
				sb.append(escapeCharacter);
			}
			return sb.toString();
		}
	}

	protected static final class LocateExpressionStateObjectTester extends AbstractTripleEncapsulatedExpressionStateObjectTester {

		protected LocateExpressionStateObjectTester(StateObjectTester firstExpression,
		                                 StateObjectTester secondExpression,
		                                 StateObjectTester thirdExpression) {

			super(firstExpression, secondExpression, thirdExpression);
		}

		@Override
		protected Class<? extends AbstractTripleEncapsulatedExpressionStateObject> expressionType() {
			return LocateExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return LOCATE;
		}
	}

	protected static abstract class LogicalExpressionStateObjectTester extends CompoundExpressionStateObjectTester {

		protected LogicalExpressionStateObjectTester(StateObjectTester leftExpression,
		                                             StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, LogicalExpressionStateObject.class);
		}
	}

	protected static final class LowerExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected LowerExpressionStateObjectTester(StateObjectTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return LowerExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return LOWER;
		}
	}

	protected static final class MaxFunctionStateObjectTester extends AggregateFunctionStateObjectTester {

		protected MaxFunctionStateObjectTester(StateObjectTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return MaxFunctionStateObject.class;
		}

		@Override
		protected String identifier() {
			return MAX;
		}
	}

	protected static final class MinFunctionStateObjectTester extends AggregateFunctionStateObjectTester {

		protected MinFunctionStateObjectTester(StateObjectTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return MinFunctionStateObject.class;
		}

		@Override
		protected String identifier() {
			return MIN;
		}
	}

	protected static final class ModExpressionStateObjectTester extends AbstractDoubleEncapsulatedExpressionStateObjectTester {

		protected ModExpressionStateObjectTester(StateObjectTester firstExpression, StateObjectTester secondExpression) {
			super(firstExpression, secondExpression);
		}

		@Override
		protected Class<? extends AbstractDoubleEncapsulatedExpressionStateObject> expressionType() {
			return ModExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return MOD;
		}
	}

	protected static final class MultiplicationExpressionStateObjectTester extends CompoundExpressionStateObjectTester {

		protected MultiplicationExpressionStateObjectTester(StateObjectTester leftExpression,
		                                         StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpressionStateObject> expressionType() {
			return MultiplicationExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return MULTIPLICATION;
		}
	}

	protected static final class NotExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester stateObject;

		protected NotExpressionStateObjectTester(StateObjectTester stateObject) {
			super();
			this.stateObject = stateObject;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, NotExpressionStateObject.class);

			NotExpressionStateObject notExpressionStateObject = (NotExpressionStateObject) stateObject;
			assertEquals(!this.stateObject.isNull(), notExpressionStateObject.hasStateObject());

			this.stateObject.test(notExpressionStateObject.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(NOT);
			sb.append(SPACE);
			sb.append(stateObject);
			return sb.toString();
		}
	}

	protected static final class NullComparisonExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester expression;
		private boolean hasNot;

		protected NullComparisonExpressionStateObjectTester(StateObjectTester expression,
		                                                    boolean hasNot) {

			super();
			this.hasNot     = hasNot;
			this.expression = expression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, NullComparisonExpressionStateObject.class);

			NullComparisonExpressionStateObject nullComparisonExpressionStateObject = (NullComparisonExpressionStateObject) stateObject;
			assertEquals(hasNot,                    nullComparisonExpressionStateObject.hasNot());
			assertEquals(!this.expression.isNull(), nullComparisonExpressionStateObject.hasStateObject());

			this.expression.test(nullComparisonExpressionStateObject.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(expression);
			if (!expression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(hasNot ? IS_NOT_NULL : IS_NULL);
			return sb.toString();
		}
	}

	protected static final class NullExpressionStateObjectTester extends AbstractStateObjectTester {

		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public void test(IterableListIterator<? extends StateObject> items) {
			assertFalse(items.hasNext());
		}

		public void test(StateObject stateObject) {
			assertNull(stateObject);
		}

		@Override
		public String toString() {
			return ExpressionTools.EMPTY_STRING;
		}
	}

	protected static final class NullIfExpressionStateObjectTester extends AbstractDoubleEncapsulatedExpressionStateObjectTester {

		protected NullIfExpressionStateObjectTester(StateObjectTester firstExpression,
		                                            StateObjectTester secondExpression) {

			super(firstExpression, secondExpression);
      }

		@Override
		protected Class<? extends AbstractDoubleEncapsulatedExpressionStateObject> expressionType() {
			return NullIfExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return NULLIF;
		}
	}

	protected static final class NumericLiteralStateObjectTester extends AbstractStateObjectTester {

		private String number;

		protected NumericLiteralStateObjectTester(String number) {
			super();
			this.number = number;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, NumericLiteralStateObject.class);
		}

		@Override
		public String toString() {
			return number;
		}
	}

	protected static final class ObjectExpressionStateObjectTester extends EncapsulatedIdentificationVariableExpressionStateObjectTester {

		protected ObjectExpressionStateObjectTester(String identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<ObjectExpressionStateObject> expressionType() {
			return ObjectExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return OBJECT;
		}
	}

	protected static final class OrderByClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester orderByItems;

		protected OrderByClauseStateObjectTester(StateObjectTester orderByItems) {
			super();
			this.orderByItems = orderByItems;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, OrderByClauseStateObject.class);

			OrderByClauseStateObject orderByClause = (OrderByClauseStateObject) stateObject;
			assertEquals(!orderByItems.isNull(), orderByClause.hasItems());

			orderByItems.test(orderByClause.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(ORDER_BY);
			sb.append(SPACE);
			sb.append(orderByItems);
			return sb.toString();
		}
	}

	protected static final class OrderByItemStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester orderByItem;
		private Ordering ordering;

		protected OrderByItemStateObjectTester(StateObjectTester orderByItem, Ordering ordering) {
			super();
			this.ordering    = ordering;
			this.orderByItem = orderByItem;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, OrderByItemStateObject.class);

			OrderByItemStateObject orderByItem = (OrderByItemStateObject) stateObject;
			assertEquals(!this.orderByItem.isNull(), orderByItem.hasStateObject());
			assertSame  (ordering, orderByItem.getOrdering());

			this.orderByItem.test(orderByItem.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(orderByItem);

			if (!orderByItem.isNull() && (ordering != Ordering.DEFAULT)) {
				sb.append(SPACE);
			}

			if (ordering != Ordering.DEFAULT) {
				sb.append(ordering.name());
			}

			return sb.toString();
		}
	}

	protected static final class OrExpressionStateObjectTester extends LogicalExpressionStateObjectTester {

		protected OrExpressionStateObjectTester(StateObjectTester leftExpression,
		                             StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpressionStateObject> expressionType() {
			return OrExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return OR;
		}
	}

	protected static final class RangeVariableDeclarationStateObjectTester extends AbstractRangeVariableDeclarationStateObjectTester {

		public RangeVariableDeclarationStateObjectTester(StateObjectTester abstractSchemaName,
                                                       boolean hasAs,
                                                       StateObjectTester identificationVariable) {

			super(abstractSchemaName, hasAs, identificationVariable);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, RangeVariableDeclarationStateObject.class);
		}
	}

	protected static final class ResultVariableStateObjectTester extends AbstractStateObjectTester {

		private boolean hasAs;
		private String resultVariable;
		private StateObjectTester selectStateObject;

		protected ResultVariableStateObjectTester(StateObjectTester selectStateObject,
		                                          boolean hasAs,
		                                          String resultVariable) {

			super();
			this.hasAs             = hasAs;
			this.selectStateObject = selectStateObject;
			this.resultVariable    = resultVariable;
		}

		public void test(StateObject stateObject) {

			assertInstance(stateObject, ResultVariableStateObject.class);
			ResultVariableStateObject resultVariable = (ResultVariableStateObject) stateObject;

			assertEquals(hasAs,                       resultVariable.hasAs());
			assertEquals(this.resultVariable,         resultVariable.getResultVariable());
			assertEquals(!selectStateObject.isNull(), resultVariable.hasStateObject());

			selectStateObject.test(resultVariable.getStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(selectStateObject);
			sb.append(SPACE);
			if (hasAs) {
				sb.append(AS);
				sb.append(SPACE);
			}
			sb.append(resultVariable);
			return sb.toString();
		}
	}

	protected static final class SelectClauseStateObjectTester extends AbstractSelectClauseStateObjectTester {

		protected SelectClauseStateObjectTester(StateObjectTester selectExpressions, boolean hasDistinct) {
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, SelectClauseStateObject.class);
		}

		@Override
		protected void testSelectExpression(AbstractSelectClauseStateObject selectClause,
		                                    StateObjectTester selectExpression) {

			SelectClauseStateObject selectClauseStateObject = (SelectClauseStateObject) selectClause;
			selectExpression.test(selectClauseStateObject.items());
		}
	}

	protected static final class SelectStatementStateObjectTester extends AbstractSelectStatementStateObjectTester {

		private StateObjectTester orderByClause;

		protected SelectStatementStateObjectTester(StateObjectTester selectClause,
		                                StateObjectTester fromClause,
		                                StateObjectTester whereClause,
		                                StateObjectTester groupByClause,
		                                StateObjectTester havingClause,
		                                StateObjectTester orderByClause) {

			super(selectClause, fromClause, whereClause, groupByClause, havingClause);
			this.orderByClause = orderByClause;
		}

		@Override
		protected Class<? extends AbstractSelectStatementStateObject> expressionType() {
			return SelectStatementStateObject.class;
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);

			SelectStatementStateObject selectStatement = (SelectStatementStateObject) stateObject;
			assertEquals(!orderByClause.isNull(), selectStatement.hasOrderByClause());

			orderByClause.test(selectStatement.getOrderByClause());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());

			if (orderByClause != null) {
				sb.append(SPACE);
				sb.append(orderByClause);
			}

			return sb.toString();
		}
	}

	protected static final class SimpleFromClauseStateObjectTester extends AbstractFromClauseStateObjectTester {

		protected SimpleFromClauseStateObjectTester(StateObjectTester declaration) {
			super(declaration);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, SimpleFromClauseStateObject.class);
		}
	}

	protected static final class SimpleSelectClauseStateObjectTester extends AbstractSelectClauseStateObjectTester {

		protected SimpleSelectClauseStateObjectTester(StateObjectTester selectExpressions, boolean hasDistinct) {
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, SimpleSelectClauseStateObject.class);
		}

		@Override
		protected void testSelectExpression(AbstractSelectClauseStateObject selectClause,
		                                    StateObjectTester selectExpression) {

			SimpleSelectClauseStateObject selectClauseStateObject = (SimpleSelectClauseStateObject) selectClause;
			selectExpression.test(selectClauseStateObject.getSelectItem());
		}
	}

	protected static final class SimpleSelectStatementStateObjectTester extends AbstractSelectStatementStateObjectTester {

		protected SimpleSelectStatementStateObjectTester(StateObjectTester selectClause,
		                                      StateObjectTester fromClause,
		                                      StateObjectTester whereClause,
		                                      StateObjectTester groupByClause,
		                                      StateObjectTester havingClause) {

			super(selectClause, fromClause, whereClause, groupByClause, havingClause);
		}

		@Override
		protected Class<? extends AbstractSelectStatementStateObject> expressionType() {
			return SimpleSelectStatementStateObject.class;
		}
	}

	protected static final class SizeExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected SizeExpressionStateObjectTester(StateObjectTester collectionValuedPathExpression) {
			super(collectionValuedPathExpression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return SizeExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return SIZE;
		}
	}

	protected static final class SqrtExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected SqrtExpressionStateObjectTester(StateObjectTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return SqrtExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return SQRT;
		}
	}

	protected static final class StateFieldPathExpressionStateObjectTester extends AbstractPathExpressionStateObjectTester {

		protected StateFieldPathExpressionStateObjectTester(StateObjectTester identificationVariable,
		                                                    String value) {

			super(identificationVariable, value);
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);
			assertInstance(stateObject, StateFieldPathExpressionStateObject.class);
		}
	}

	/**
	 * This tester tests an {@link Expression} information to make sure it correctly parsed a section
	 * of the query. This interface also adds helper method for easily creating a parsed tree
	 * representation of the actual query parsed tree.
	 */
	protected static interface StateObjectTester {

		AdditionExpressionStateObjectTester add(StateObjectTester expression);
		AndExpressionStateObjectTester and(StateObjectTester expression);
		BetweenExpressionStateObjectTester between(StateObjectTester lowerBoundExpression, StateObjectTester upperBoundExpression);
		ComparisonExpressionStateObjectTester different(StateObjectTester expression);
		DivisionExpressionStateObjectTester division(StateObjectTester expression);
		ComparisonExpressionStateObjectTester equal(StateObjectTester expression);
		ComparisonExpressionStateObjectTester greaterThan(StateObjectTester expression);
		ComparisonExpressionStateObjectTester greaterThanOrEqual(StateObjectTester expression);
		InExpressionStateObjectTester in(StateObjectTester... inItems);
		EmptyCollectionComparisonExpressionStateObjectTester isEmpty();
		EmptyCollectionComparisonExpressionStateObjectTester isNotEmpty();

		/**
		 * Determines whether this tester represents the {@link NullExpression}.
		 *
		 * @return <code>true</code> if this tester represents a <code>null</code> object; false
		 * otherwise
		 */
		boolean isNull();

		LikeExpressionStateObjectTester like(StateObjectTester patternValue);
		LikeExpressionStateObjectTester like(StateObjectTester patternValue, String escapeCharacter);
		ComparisonExpressionStateObjectTester lowerThan(StateObjectTester expression);
		ComparisonExpressionStateObjectTester lowerThanOrEqual(StateObjectTester expression);
		StateObjectTester member(StateObjectTester collectionValuedPathExpression);
		StateObjectTester memberOf(StateObjectTester collectionValuedPathExpression);
		MultiplicationExpressionStateObjectTester multiplication(StateObjectTester expression);
		BetweenExpressionStateObjectTester notBetween(StateObjectTester lowerBoundExpression, StateObjectTester upperBoundExpression);
		InExpressionStateObjectTester notIn(StateObjectTester... inItems);
		LikeExpressionStateObjectTester notLike(StateObjectTester expression);
		LikeExpressionStateObjectTester notLike(StateObjectTester expression, String escapeCharacter);
		StateObjectTester notMember(StateObjectTester collectionValuedPathExpression);
		StateObjectTester notMemberOf(StateObjectTester collectionValuedPathExpression);
		OrExpressionStateObjectTester or(StateObjectTester expression);
		SubstractionExpressionStateObjectTester substract(StateObjectTester expression);

		void test(IterableListIterator<? extends StateObject> items);
		/**
		 * Tests the given {@link Expression} internal data.
		 */
		void test(StateObject stateObject);
	}

	protected static final class StringLiteralStateObjectTester extends AbstractStateObjectTester {

		private boolean hasCloseQuote;
		private String literal;

		protected StringLiteralStateObjectTester(String literal) {

			super();
			this.literal = literal;

			if (literal.length() > 1) {
				char lastChar = literal.charAt(literal.length() - 1);
				hasCloseQuote = (lastChar == SINGLE_QUOTE) || (lastChar == DOUBLE_QUOTE);
			}
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, StringLiteralStateObject.class);

			StringLiteralStateObject stringLiteral = (StringLiteralStateObject) stateObject;
			assertEquals(toString(),    stringLiteral.toString());
			assertEquals(hasCloseQuote, stringLiteral.hasCloseQuote());
		}

		@Override
		public String toString() {
			return literal;
		}
	}

	protected static final class SubExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected SubExpressionStateObjectTester(StateObjectTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return SubExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return ExpressionTools.EMPTY_STRING;
		}
	}

	protected static final class SubstractionExpressionStateObjectTester extends CompoundExpressionStateObjectTester {

		protected SubstractionExpressionStateObjectTester(StateObjectTester leftExpression,
		                                                  StateObjectTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpressionStateObject> expressionType() {
			return SubtractionExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return MINUS;
		}
	}

	protected static final class SubstringExpressionStateObjectTester extends AbstractTripleEncapsulatedExpressionStateObjectTester {

		protected SubstringExpressionStateObjectTester(StateObjectTester firstExpression,
		                                               StateObjectTester firstArithmeticExpression,
		                                               StateObjectTester secondArithmeticExpression) {

			super(firstExpression, firstArithmeticExpression, secondArithmeticExpression);
		}

		@Override
		protected Class<? extends AbstractTripleEncapsulatedExpressionStateObject> expressionType() {
			return SubstringExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return SUBSTRING;
		}
	}

	protected static final class SumFunctionStateObjectTester extends AggregateFunctionStateObjectTester {

		protected SumFunctionStateObjectTester(StateObjectTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return SumFunctionStateObject.class;
		}

		@Override
		protected String identifier() {
			return SUM;
		}
	}

	protected static final class TrimExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		private boolean hasFrom;
		private Specification specification;
		private String trimCharacter;

		protected TrimExpressionStateObjectTester(Specification specification,
		                                          StateObjectTester stringPrimary,
		                                          String trimCharacter,
		                                          boolean hasFrom) {

			super(stringPrimary);
			this.specification = specification;
			this.trimCharacter = trimCharacter;
			this.hasFrom       = hasFrom;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return TrimExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return TRIM;
		}

		@Override
		public void test(StateObject stateObject) {
			super.test(stateObject);

			TrimExpressionStateObject trimExpressionStateObject = (TrimExpressionStateObject) stateObject;
			assertEquals(trimCharacter != null,                  trimExpressionStateObject.hasTrimCharacter());
			assertEquals(specification != Specification.DEFAULT, trimExpressionStateObject.hasSpecification());

			assertEquals(trimCharacter, trimExpressionStateObject.getTrimCharacter());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			if (specification != Specification.DEFAULT) {
				sb.append(specification);
			}
			sb.append(SPACE);
			sb.append(trimCharacter);
			sb.append(SPACE);
			if (hasFrom) {
				sb.append(FROM);
			}
			sb.append(SPACE);
			super.toStringEncapsulatedExpression(sb);
		}
	}

	protected static final class TypeExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected TypeExpressionStateObjectTester(StateObjectTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return TypeExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return TYPE;
		}
	}

	protected static final class UnknownExpressionStateObjectTester extends AbstractStateObjectTester {

		private final String unknownText;

		protected UnknownExpressionStateObjectTester(String unknownText) {
			super();
			this.unknownText = unknownText;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, UnknownExpressionStateObject.class);
		}

		@Override
		public String toString() {
			return unknownText;
		}
	}

	protected static final class UpdateClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester rangeVariableDeclaration;
		private StateObjectTester updateItems;

		protected UpdateClauseStateObjectTester(StateObjectTester rangeVariableDeclaration,
		                                        StateObjectTester updateItems) {

			super();
			this.updateItems              = updateItems;
			this.rangeVariableDeclaration = rangeVariableDeclaration;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, UpdateClauseStateObject.class);

			UpdateClauseStateObject updateClause = (UpdateClauseStateObject) stateObject;
			assertEquals(!updateItems.isNull(), updateClause.hasItems());

			rangeVariableDeclaration.test(updateClause.getRangeVariableDeclaration());
			updateItems.test(updateClause.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(UPDATE);
			sb.append(SPACE);
			sb.append(rangeVariableDeclaration);
			sb.append(SPACE);
			sb.append(SET);
			sb.append(SPACE);
			sb.append(updateItems);
			return sb.toString();
		}
	}

	protected static final class UpdateItemStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester newValue;
		private StateObjectTester stateFieldPathExpression;

		protected UpdateItemStateObjectTester(StateObjectTester stateFieldPathExpression,
		                                      StateObjectTester newValue) {

			super();
			this.stateFieldPathExpression = stateFieldPathExpression;
			this.newValue = newValue;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, UpdateItemStateObject.class);

			UpdateItemStateObject updateItem = (UpdateItemStateObject) stateObject;
			assertEquals(!newValue.isNull(), updateItem.hasNewValue());

			stateFieldPathExpression.test(updateItem.getStateFieldPath());
			newValue.test(updateItem.getNewValue());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPathExpression);
			sb.append(SPACE);
			sb.append(EQUAL);
			sb.append(SPACE);
			sb.append(newValue);
			return sb.toString();
		}
	}

	protected static final class UpdateStatementStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester updateClause;
		private StateObjectTester whereClause;

		protected UpdateStatementStateObjectTester(StateObjectTester updateClause,
		                                           StateObjectTester whereClause) {

			super();
			this.updateClause = updateClause;
			this.whereClause  = whereClause;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, UpdateStatementStateObject.class);

			UpdateStatementStateObject updateStatement = (UpdateStatementStateObject) stateObject;
			assertEquals(!whereClause.isNull(), updateStatement.hasWhereClause());

			updateClause.test(updateStatement.getModifyClause());
			whereClause .test(updateStatement.getWhereClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(updateClause);
			sb.append(SPACE);
			sb.append(whereClause);
			return sb.toString();
		}
	}

	protected static final class UpperExpressionStateObjectTester extends AbstractSingleEncapsulatedExpressionStateObjectTester {

		protected UpperExpressionStateObjectTester(StateObjectTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpressionStateObject> expressionType() {
			return UpperExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return UPPER;
		}
	}

	protected static final class ValueExpressionStateObjectTester extends EncapsulatedIdentificationVariableExpressionStateObjectTester {

		protected ValueExpressionStateObjectTester(String identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<ValueExpressionStateObject> expressionType() {
			return ValueExpressionStateObject.class;
		}

		@Override
		protected String identifier() {
			return VALUE;
		}
	}

	protected static final class WhenClauseStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester thenExpression;
		private StateObjectTester whenExpression;

		protected WhenClauseStateObjectTester(StateObjectTester whenExpression,
		                                      StateObjectTester thenExpression) {

			super();
			this.thenExpression = thenExpression;
			this.whenExpression = whenExpression;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, WhenClauseStateObject.class);

			WhenClauseStateObject whenClause = (WhenClauseStateObject) stateObject;
			assertEquals(!whenExpression.isNull(), whenClause.hasThen());
			assertEquals(!thenExpression.isNull(), whenClause.hasThen());

			whenExpression.test(whenClause.getConditional());
			thenExpression.test(whenClause.getThen());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(WHEN);
			sb.append(SPACE);
			sb.append(whenExpression);
			sb.append(SPACE);
			sb.append(THEN);
			sb.append(SPACE);
			sb.append(thenExpression);
			return sb.toString();
		}
	}

	protected static final class WhereClauseStateObjectTester extends AbstractConditionalClauseStateObjectTester {

		protected WhereClauseStateObjectTester(StateObjectTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		protected Class<? extends AbstractConditionalClauseStateObject> expressionType() {
			return WhereClauseStateObject.class;
		}

		@Override
		protected String identifier() {
			return WHERE;
		}
	}
}