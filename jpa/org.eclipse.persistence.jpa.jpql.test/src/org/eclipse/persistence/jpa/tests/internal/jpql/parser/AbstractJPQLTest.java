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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import java.util.Arrays;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTripleEncapsulatedExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LogicalExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.TrimExpression.Specification;
import org.eclipse.persistence.jpa.internal.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;

import static org.eclipse.persistence.jpa.internal.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * The root of the JPQL unit-tests, which has all the method to test the parsed tree representation
 * of the JPQL query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
abstract class AbstractJPQLTest {

	static AbsExpressionTester abs(ExpressionTester expression) {
		return new AbsExpressionTester(expression);
	}

	static AbstractSchemaNameTester abstractSchemaName(String abstractSchemaName) {
		return new AbstractSchemaNameTester(abstractSchemaName);
	}

	static AdditionExpressionTester add(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return new AdditionExpressionTester(leftExpression, rightExpression);
	}

	static AllOrAnyExpressionTester all(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(ALL, subquery);
	}

	static AndExpressionTester and(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return new AndExpressionTester(leftExpression, rightExpression);
	}

	static AllOrAnyExpressionTester any(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(ANY, subquery);
	}

	static AllOrAnyExpressionTester anyExpression(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(ANY, subquery);
	}

	static AvgFunctionTester avg(ExpressionTester expression) {
		return new AvgFunctionTester(expression, false);
	}

	static AvgFunctionTester avg(String statefieldPathExpression) {
		return avg(path(statefieldPathExpression));
	}

	static AvgFunctionTester avgDistinct(String statefieldPathExpression) {
		return new AvgFunctionTester(path(statefieldPathExpression), true);
	}

	static BetweenExpressionTester between(ExpressionTester expression, ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression) {
		return new BetweenExpressionTester(expression, false, lowerBoundExpression, upperBoundExpression);
	}

	static CaseExpressionTester case_(ExpressionTester... caseOperands) {
		ExpressionTester[] copy = new ExpressionTester[caseOperands.length - 1];
		System.arraycopy(caseOperands, 0, copy, 0, caseOperands.length - 1);
		return new CaseExpressionTester(
			nullExpression(),
			spacedCollection(copy),
			caseOperands[caseOperands.length - 1]
		);
	}

	static CaseExpressionTester case_(ExpressionTester caseOperand, ExpressionTester[] whenClauses, ExpressionTester elseExpression) {
		return new CaseExpressionTester(
			caseOperand,
			spacedCollection(whenClauses),
			elseExpression
		);
	}

	static CaseExpressionTester case_(ExpressionTester[] whenClauses, ExpressionTester elseExpression) {
		return case_(nullExpression(), whenClauses, elseExpression);
	}

	static CoalesceExpressionTester coalesce(ExpressionTester expression) {
		return new CoalesceExpressionTester(expression);
	}

	static CoalesceExpressionTester coalesce(ExpressionTester... expressions) {
		return new CoalesceExpressionTester(collection(expressions));
	}

	static CollectionExpressionTester collection(ExpressionTester... expressions) {

		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length - 1, Boolean.TRUE);

		spaces[expressions.length - 1] = Boolean.FALSE;
		commas[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, commas, spaces);
	}

	static CollectionExpressionTester collection(ExpressionTester[] expressions, Boolean[] commas, Boolean[] spaces) {
		return new CollectionExpressionTester(expressions, commas, spaces);
	}

	static CollectionValuedPathExpressionTester collectionValuedPath(String collectionValuedPathExpression) {
		return new CollectionValuedPathExpressionTester(collectionValuedPathExpression);
	}

	private static ComparisonExpressionTester comparison(ExpressionTester leftExpression, String comparator, ExpressionTester rightExpression) {
		return new ComparisonExpressionTester(comparator, leftExpression, rightExpression);
	}

	static ConcatExpressionTester concat(ExpressionTester... expressions) {
		if (expressions.length > 1) {
			return new ConcatExpressionTester(collection(expressions));
		}
		return new ConcatExpressionTester(expressions[0]);
	}

	static CountFunctionTester count(ExpressionTester statefieldPathExpression) {
		return new CountFunctionTester(statefieldPathExpression, false);
	}

	static CountFunctionTester count(String statefieldPathExpression) {
		return count(path(statefieldPathExpression));
	}

	static CountFunctionTester countDistinct(ExpressionTester statefieldPathExpression) {
		return new CountFunctionTester(statefieldPathExpression, true);
	}

	static DateTimeTester CURRENT_DATE() {
		return new DateTimeTester(CURRENT_DATE);
	}

	static DateTimeTester CURRENT_TIME() {
		return new DateTimeTester(CURRENT_TIME);
	}

	static DateTimeTester CURRENT_TIMESTAMP() {
		return new DateTimeTester(CURRENT_TIMESTAMP);
	}

	static DateTimeTester dateTime(String jdbcEscapeFormat) {
		return new DateTimeTester(jdbcEscapeFormat);
	}

	static DeleteClauseTester delete(String abstractSchemaName, String identificationVariable) {
		return new DeleteClauseTester(rangeVariableDeclaration(abstractSchemaName, identificationVariable));
	}

	static DeleteStatementTester deleteStatement(ExpressionTester updateClause) {
		return deleteStatement(updateClause, nullExpression());
	}

	static DeleteStatementTester deleteStatement(ExpressionTester updateClause, ExpressionTester whereClause) {
		return new DeleteStatementTester(updateClause, whereClause);
	}

	static DeleteStatementTester deleteStatement(String abstractSchemaName, String identificationVariable) {
		return deleteStatement(delete(abstractSchemaName, identificationVariable));
	}

	static DeleteStatementTester deleteStatement(String abstractSchemaName, String identificationVariable, ExpressionTester whereClause) {
		return deleteStatement(delete(abstractSchemaName, identificationVariable), whereClause);
	}

	static ComparisonExpressionTester different(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return comparison(leftExpression, Expression.DIFFERENT, rightExpression);
	}

	static DivisionExpressionTester division(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return new DivisionExpressionTester(leftExpression, rightExpression);
	}

	static EntityTypeLiteralTester entity(String entity) {
		return new EntityTypeLiteralTester(entity);
	}

	static ComparisonExpressionTester equal(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return comparison(leftExpression, Expression.EQUAL, rightExpression);
	}

	static ExistsExpressionTester exists(ExpressionTester subquery) {
		return new ExistsExpressionTester(subquery, false);
	}

	static KeywordExpressionTester FALSE() {
		return new KeywordExpressionTester(FALSE);
	}

	static FromClauseTester from(ExpressionTester declaration) {
		return new FromClauseTester(declaration);
	}

	static FromClauseTester from(ExpressionTester... declarations) {
		return new FromClauseTester(collection(declarations));
	}

	/**
	 * Example: from("Employee", "e", "Product", "p")
	 */
	static FromClauseTester from(String... declarations) {
		ExpressionTester[] identificationVariableDeclarations = new ExpressionTester[declarations.length / 2];

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
	static FromClauseTester from(String abstractSchemaName, String identificationVariable) {
		return from(fromEntity(abstractSchemaName, identificationVariable));
	}

	static FromClauseTester from(String abstractSchemaName, String identificationVariable, ExpressionTester... joins) {
		return from(identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins));
	}

	static FromClauseTester from(String abstractSchemaName, String identificationVariable, ExpressionTester joins) {
		return from(fromEntity(abstractSchemaName, identificationVariable, joins));
	}

	static FromClauseTester fromAs(String abstractSchemaName, String identificationVariable) {
		return from(identificationVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	/**
	 * Example: from("e.employees", "e")
	 */
	static IdentificationVariableDeclarationTester fromCollection(String collectionValuedPathExpression, String identificationVariable) {
		return identificationVariableDeclaration(
			rangeVariableDeclaration(
				collectionValuedPath(collectionValuedPathExpression),
				variable(identificationVariable)
			),
			nullExpression()
		);
	}

	static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName, String identificationVariable) {
		return identificationVariableDeclaration(abstractSchemaName, identificationVariable);
	}

	static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName, String identificationVariable, ExpressionTester... joins) {
		return identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins);
	}

	static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName, String identificationVariable, ExpressionTester join) {
		return identificationVariableDeclaration(abstractSchemaName, identificationVariable, join);
	}

	static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName, String identificationVariable) {
		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable);
	}

	static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName, String identificationVariable, ExpressionTester... joins) {
		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, joins);
	}

	static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName, String identificationVariable, ExpressionTester join) {
		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, join);
	}

	static CollectionMemberDeclarationTester fromIn(ExpressionTester collectionValuedPathExpression, ExpressionTester identificationVariable) {
		return new CollectionMemberDeclarationTester(collectionValuedPathExpression, false, identificationVariable);
	}

	static CollectionMemberDeclarationTester fromIn(String collectionValuedPathExpression, String identificationVariable) {
		return fromIn(collectionValuedPath(collectionValuedPathExpression), variable(identificationVariable));
	}

	static CollectionMemberDeclarationTester fromInAs(ExpressionTester collectionValuedPathExpression, ExpressionTester identificationVariable) {
		return new CollectionMemberDeclarationTester(collectionValuedPathExpression, true, identificationVariable);
	}

	static CollectionMemberDeclarationTester fromInAs(String collectionValuedPathExpression, String identificationVariable) {
		return fromInAs(collectionValuedPath(collectionValuedPathExpression), variable(identificationVariable));
	}

	static FuncExpressionTester func(String functionName, ExpressionTester... funcItems) {
		return new FuncExpressionTester(functionName, collection(funcItems));
	}

	static FuncExpressionTester func(String functionName, ExpressionTester funcItem) {
		return new FuncExpressionTester(functionName, funcItem);
	}

	static ComparisonExpressionTester greaterThan(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return comparison(leftExpression, Expression.GREATER_THAN, rightExpression);
	}

	static ComparisonExpressionTester greaterThanOrEqual(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return comparison(leftExpression, Expression.GREATER_THAN_OR_EQUAL, rightExpression);
	}

	static GroupByClauseTester groupBy(ExpressionTester groupByItem) {
		return new GroupByClauseTester(groupByItem);
	}

	static GroupByClauseTester groupBy(ExpressionTester... groupByItems) {
		return new GroupByClauseTester(collection(groupByItems));
	}

	static HavingClauseTester having(ExpressionTester havingItem) {
		return new HavingClauseTester(havingItem);
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, nullExpression());
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration, ExpressionTester... joins) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, collection(joins));
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration, ExpressionTester joins) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, joins);
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName, String identificationVariable) {
		return identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName, identificationVariable), nullExpression());
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName, String identificationVariable, ExpressionTester... joins) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration(abstractSchemaName, identificationVariable), spacedCollection(joins));
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName, String identificationVariable, ExpressionTester join) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration(abstractSchemaName, identificationVariable), join);
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName, String identificationVariable) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable), nullExpression());
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName, String identificationVariable, ExpressionTester join) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable), join);
	}

	static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName, String identificationVariable, ExpressionTester... joins) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable), spacedCollection(joins));
	}

	static InExpressionTester in(ExpressionTester stateFieldPathExpression, ExpressionTester... inItems) {
		return new InExpressionTester(stateFieldPathExpression, false, collection(inItems));
	}

	static InExpressionTester in(ExpressionTester stateFieldPathExpression, ExpressionTester inItems) {
		return new InExpressionTester(stateFieldPathExpression, false, inItems);
	}

	static InExpressionTester in(String stateFieldPathExpression, ExpressionTester... inItems) {
		return in(path(stateFieldPathExpression), inItems);
	}

	static InExpressionTester in(String stateFieldPathExpression, ExpressionTester inItem) {
		return in(path(stateFieldPathExpression), inItem);
	}

	static IndexExpressionTester index(ExpressionTester identificationVariable) {
		return new IndexExpressionTester(identificationVariable);
	}

	static IndexExpressionTester index(String identificationVariable) {
		return index(variable(identificationVariable));
	}

	static JoinTester innerJoin(String collectionValuedPathExpression, String identificationVariable) {
		return join(Expression.INNER_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	static JoinFetchTester innerJoinFetch(String collectionValuedPathExpression) {
		return joinFetch(Expression.INNER_JOIN_FETCH, collectionValuedPathExpression);
	}

	static InputParameterTester inputParameter(String inputParameter) {
		return new InputParameterTester(inputParameter);
	}

	static EmptyCollectionComparisonExpressionTester isEmpty(String collectionValuedPathExpression) {
		return new EmptyCollectionComparisonExpressionTester(collectionValuedPath(collectionValuedPathExpression), false);
	}

	static EmptyCollectionComparisonExpressionTester isNotEmpty(String collectionValuedPathExpression) {
		return new EmptyCollectionComparisonExpressionTester(collectionValuedPath(collectionValuedPathExpression), true);
	}

	static NullComparisonExpressionTester isNotNull(ExpressionTester expression) {
		return new NullComparisonExpressionTester(expression, true);
	}

	static NullComparisonExpressionTester isNull(ExpressionTester expression) {
		return new NullComparisonExpressionTester(expression, false);
	}

	static JoinTester join(String collectionValuedPathExpression,String identificationVariable) {
		return join(Expression.JOIN, collectionValuedPathExpression, identificationVariable);
	}

	private static JoinTester join(String joinType, String collectionValuedPathExpression, String identificationVariable) {
		return new JoinTester(joinType, collectionValuedPath(collectionValuedPathExpression), false, variable(identificationVariable));
	}

	static JoinTester joinAs(String collectionValuedPathExpression, String identificationVariable) {
		return new JoinTester(Expression.JOIN, collectionValuedPath(collectionValuedPathExpression), true, variable(identificationVariable));
	}

	static JoinFetchTester joinFetch(String collectionValuedPathExpression) {
		return joinFetch(Expression.JOIN_FETCH, collectionValuedPathExpression);
	}

	private static JoinFetchTester joinFetch(String joinType, String collectionValuedPathExpression) {
		return new JoinFetchTester(joinType, collectionValuedPath(collectionValuedPathExpression));
	}

	static JPQLExpressionTester jpqlExpression(ExpressionTester queryStatement) {
		return jpqlExpression(queryStatement, nullExpression());
	}

	static JPQLExpressionTester jpqlExpression(ExpressionTester queryStatement, ExpressionTester unknownExpression) {
		return new JPQLExpressionTester(queryStatement, unknownExpression);
	}

	static JoinTester leftJoin(String collectionValuedPathExpression, String identificationVariable) {
		return join(Expression.LEFT_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	static JoinFetchTester leftJoinFetch(String collectionValuedPathExpression) {
		return joinFetch(Expression.LEFT_JOIN_FETCH, collectionValuedPathExpression);
	}

	static JoinTester leftOuterJoin(String collectionValuedPathExpression, String identificationVariable) {
		return join(Expression.LEFT_OUTER_JOIN, collectionValuedPathExpression, identificationVariable);
	}

	static JoinFetchTester leftOuterJoinFetch(String collectionValuedPathExpression) {
		return joinFetch(Expression.LEFT_OUTER_JOIN_FETCH, collectionValuedPathExpression);
	}

	static LengthExpressionTester length(ExpressionTester stringPrimary) {
		return new LengthExpressionTester(stringPrimary);
	}

	static LikeExpressionTester like(ExpressionTester stringExpression, ExpressionTester patternValue) {
		return like(stringExpression, patternValue, nullExpression());
	}

	static LikeExpressionTester like(ExpressionTester stringExpression, ExpressionTester patternValue, char escapeCharacter) {
		return like(stringExpression, patternValue, string("'" + escapeCharacter + "'"));
	}

	static LikeExpressionTester like(ExpressionTester stringExpression, ExpressionTester patternValue, ExpressionTester escapeCharacter) {
		return new LikeExpressionTester(stringExpression, false, patternValue, escapeCharacter);
	}

	static LocateExpressionTester locate(ExpressionTester firstExpression, ExpressionTester secondExpression) {
		return locate(firstExpression, secondExpression, nullExpression());
	}

	static LocateExpressionTester locate(ExpressionTester firstExpression, ExpressionTester secondExpression, ExpressionTester thirdExpression) {
		return new LocateExpressionTester(firstExpression, secondExpression, thirdExpression);
	}

	static LowerExpressionTester lower(ExpressionTester stringPrimary) {
		return new LowerExpressionTester(stringPrimary);
	}

	static ComparisonExpressionTester lowerThan(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return comparison(leftExpression, Expression.LOWER_THAN, rightExpression);
	}

	static ComparisonExpressionTester lowerThanOrEqual(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return comparison(leftExpression, Expression.LOWER_THAN_OR_EQUAL, rightExpression);
	}

	static MaxFunctionTester max(ExpressionTester expression) {
		return new MaxFunctionTester(expression, false);
	}

	static MaxFunctionTester max(String statefieldPathExpression) {
		return max(path(statefieldPathExpression));
	}

	static ExpressionTester maxDistinct(String statefieldPathExpression) {
		return new MaxFunctionTester(path(statefieldPathExpression), true);
	}

	static CollectionMemberExpressionTester member(ExpressionTester entityExpression, ExpressionTester collectionValuedPathExpression) {
		return new CollectionMemberExpressionTester(entityExpression, false, false, collectionValuedPathExpression);
	}

	static CollectionMemberExpressionTester member(ExpressionTester entityExpression, String collectionValuedPathExpression) {
		return member(entityExpression, collectionValuedPath(collectionValuedPathExpression));
	}

	static CollectionMemberExpressionTester member(String identificationVariable, String collectionValuedPathExpression) {
		return member(variable(identificationVariable), collectionValuedPathExpression);
	}

	static CollectionMemberExpressionTester memberOf(ExpressionTester entityExpression, ExpressionTester collectionValuedPathExpression) {
		return new CollectionMemberExpressionTester(entityExpression, false, true, collectionValuedPathExpression);
	}

	static CollectionMemberExpressionTester memberOf(ExpressionTester entityExpression, String collectionValuedPathExpression) {
		return memberOf(entityExpression, collectionValuedPath(collectionValuedPathExpression));
	}

	static CollectionMemberExpressionTester memberOf(String identificationVariable, String collectionValuedPathExpression) {
		return memberOf( variable(identificationVariable), collectionValuedPathExpression);
	}

	static MinFunctionTester min(ExpressionTester expression) {
		return new MinFunctionTester(expression, false);
	}

	static MinFunctionTester min(String statefieldPathExpression) {
		return min(path(statefieldPathExpression));
	}

	static MinFunctionTester minDistinct(String statefieldPathExpression) {
		return new MinFunctionTester(path(statefieldPathExpression), true);
	}

	static ArithmeticFactorTester minus(ExpressionTester expression) {
		return new ArithmeticFactorTester(MINUS, expression);
	}

	static ModExpressionTester mod(ExpressionTester simpleArithmeticExpression1, ExpressionTester simpleArithmeticExpression2) {
		return new ModExpressionTester(simpleArithmeticExpression1, simpleArithmeticExpression2);
	}

	static MultiplicationExpressionTester multiplication(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return new MultiplicationExpressionTester(leftExpression, rightExpression);
	}

	static ConstructorExpressionTester new_(String className, ExpressionTester constructorItem) {
		return new ConstructorExpressionTester(className, constructorItem);
	}

	static ConstructorExpressionTester new_(String className, ExpressionTester... constructorItems) {
		return new ConstructorExpressionTester(className, collection(constructorItems));
	}

	static NotExpressionTester not(ExpressionTester expression) {
		return new NotExpressionTester(expression);
	}

	static BetweenExpressionTester notBetween(ExpressionTester expression, ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression) {
		return new BetweenExpressionTester(expression, true, lowerBoundExpression, upperBoundExpression);
	}

	static ExistsExpressionTester notExists(ExpressionTester subquery) {
		return new ExistsExpressionTester(subquery, true);
	}

	static InExpressionTester notIn(ExpressionTester stateFieldPathExpression, ExpressionTester inItems) {
		return new InExpressionTester(stateFieldPathExpression, true, inItems);
	}

	static InExpressionTester notIn(ExpressionTester stateFieldPathExpression, ExpressionTester... inItems) {
		return new InExpressionTester(stateFieldPathExpression, true, collection(inItems));
	}

	static InExpressionTester notIn(String stateFieldPathExpression, ExpressionTester... inItems) {
		return notIn(path(stateFieldPathExpression), collection(inItems));
	}

	static InExpressionTester notIn(String stateFieldPathExpression, ExpressionTester inItem) {
		return notIn(path(stateFieldPathExpression), inItem);
	}

	static LikeExpressionTester notLike(ExpressionTester stringExpression, ExpressionTester patternValue) {
		return notLike(stringExpression, patternValue, nullExpression());
	}

	static LikeExpressionTester notLike(ExpressionTester stringExpression, ExpressionTester patternValue, ExpressionTester escapeCharacter) {
		return new LikeExpressionTester(stringExpression, true, patternValue, escapeCharacter);
	}

	static CollectionMemberExpressionTester notMember(ExpressionTester entityExpression, ExpressionTester collectionValuedPathExpression) {
		return new CollectionMemberExpressionTester(entityExpression, true, false, collectionValuedPathExpression);
	}

	static CollectionMemberExpressionTester notMember(ExpressionTester entityExpression, String collectionValuedPathExpression) {
		return notMember(entityExpression, collectionValuedPathExpression);
	}

	static CollectionMemberExpressionTester notMember(String identificationVariable, String collectionValuedPathExpression) {
		return notMember(variable(identificationVariable), collectionValuedPathExpression);
	}

	static CollectionMemberExpressionTester notMemberOf(ExpressionTester entityExpression, ExpressionTester collectionValuedPathExpression) {
		return new CollectionMemberExpressionTester(entityExpression, true, true, collectionValuedPathExpression);
	}

	static ExpressionTester NULL() {
		return new KeywordExpressionTester(NULL);
	}

	static ExpressionTester nullExpression() {
		return new NullExpressionTester();
	}

	static NullIfExpressionTester nullIf(ExpressionTester expression1, ExpressionTester expression2) {
		return new NullIfExpressionTester(expression1, expression2);
	}

	static NumericLiteralTester numeric(double number) {
		return numeric(String.valueOf(number));
	}

	static NumericLiteralTester numeric(float number) {
		return numeric(String.valueOf(number));
	}

	static NumericLiteralTester numeric(int number) {
		return numeric(String.valueOf(number));
	}

	static NumericLiteralTester numeric(long number) {
		return numeric(String.valueOf(number));
	}

	static NumericLiteralTester numeric(String value) {
		return new NumericLiteralTester(value);
	}

	static ObjectTester object(String identificationVariable) {
		return new ObjectTester(variable(identificationVariable));
	}

	static OrExpressionTester or(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return new OrExpressionTester(leftExpression, rightExpression);
	}

	static OrderByClauseTester orderBy(ExpressionTester orderByItem) {
		return new OrderByClauseTester(orderByItem);
	}

	static OrderByClauseTester orderBy(ExpressionTester... orderByItems) {
		return new OrderByClauseTester(collection(orderByItems));
	}

	static OrderByClauseTester orderBy(String stateFieldPathExpression) {
		return new OrderByClauseTester(orderByItem(stateFieldPathExpression));
	}

	static OrderByItemTester orderByItem(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DEFAULT);
	}

	private static OrderByItemTester orderByItem(ExpressionTester orderByItem, Ordering ordering) {
		return new OrderByItemTester(orderByItem, ordering);
	}

	static OrderByItemTester orderByItem(String stateFieldPathExpression) {
		return orderByItem(path(stateFieldPathExpression));
	}

	static OrderByItemTester orderByItemAsc(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.ASC);
	}

	static OrderByItemTester orderByItemAsc(String stateFieldPathExpression) {
		return orderByItemAsc(path(stateFieldPathExpression));
	}

	static OrderByItemTester orderByItemDesc(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DESC);
	}

	static OrderByItemTester orderByItemDesc(String stateFieldPathExpression) {
		return orderByItemDesc(path(stateFieldPathExpression));
	}

	static StateFieldPathExpressionTester path(String stateFieldPathExpression) {
		return new StateFieldPathExpressionTester(stateFieldPathExpression);
	}

	static ArithmeticFactorTester plus(ExpressionTester expression) {
		return new ArithmeticFactorTester(PLUS, expression);
	}

	private static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName, boolean hasAs, ExpressionTester identificationVariable) {
		return new RangeVariableDeclarationTester(abstractSchemaName, hasAs, identificationVariable);
	}

	static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName, ExpressionTester identificationVariable) {
		return rangeVariableDeclaration(abstractSchemaName, false, identificationVariable);
	}

	static RangeVariableDeclarationTester rangeVariableDeclaration(String abstractSchemaName, String identificationVariable) {
		if (identificationVariable != null) {
			return rangeVariableDeclaration(abstractSchemaName(abstractSchemaName), false, variable(identificationVariable));
		}
		return rangeVariableDeclaration(abstractSchemaName(abstractSchemaName), false, nullExpression());
	}

	static RangeVariableDeclarationTester rangeVariableDeclarationAs(String abstractSchemaName, String identificationVariable) {
		return rangeVariableDeclaration(abstractSchemaName(abstractSchemaName), true, variable(identificationVariable));
	}

	static ResultVariableTester ResultVariableTester(ExpressionTester selectExpression, String resultVariable) {
		return new ResultVariableTester(selectExpression, false, variable(resultVariable));
	}

	static SelectClauseTester select(ExpressionTester selectExpression) {
		return select(selectExpression, false);
	}

	static SelectClauseTester select(ExpressionTester... selectExpressions) {
		return new SelectClauseTester(collection(selectExpressions), false);
	}

	private static SelectClauseTester select(ExpressionTester selectExpression, boolean hasDistinct) {
		return new SelectClauseTester(selectExpression, hasDistinct);
	}

	static SelectClauseTester selectDistinct(ExpressionTester... selectExpressions) {
		return new SelectClauseTester(collection(selectExpressions), true);
	}

	static SelectClauseTester selectDistinct(ExpressionTester selectExpression) {
		return new SelectClauseTester(selectExpression, true);
	}

	static SelectClauseTester selectDisting(ExpressionTester selectExpression) {
		return select(selectExpression, true);
	}

	private static ResultVariableTester selectItem(ExpressionTester selectExpression, boolean hasAs, ExpressionTester resultVariable) {
		return new ResultVariableTester(selectExpression, hasAs, resultVariable);
	}

	static ResultVariableTester selectItem(ExpressionTester selectExpression, ExpressionTester resultVariable) {
		return selectItem(selectExpression, false, resultVariable);
	}

	static ResultVariableTester selectItem(ExpressionTester selectExpression, String resultVariable) {
		return selectItem(selectExpression, false, variable(resultVariable));
	}

	static ResultVariableTester selectItemAs(ExpressionTester selectExpression, ExpressionTester resultVariable) {
		return selectItem(selectExpression, true, resultVariable);
	}

	static ResultVariableTester selectItemAs(ExpressionTester selectExpression, String resultVariable) {
		return selectItemAs(selectExpression, variable(resultVariable));
	}

	static SelectStatementTester selectStatement(ExpressionTester selectClause, ExpressionTester fromClause) {
		return selectStatement(selectClause, fromClause, nullExpression());
	}

	static SelectStatementTester selectStatement(ExpressionTester selectClause, ExpressionTester fromClause, ExpressionTester whereClause) {
		return selectStatement(selectClause, fromClause, whereClause, nullExpression(), nullExpression(), nullExpression());
	}

	static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                             ExpressionTester fromClause,
	                                             ExpressionTester whereClause,
	                                             ExpressionTester groupByClause,
	                                             ExpressionTester havingClause,
	                                             ExpressionTester orderByClause) {

		return new SelectStatementTester(selectClause, fromClause, whereClause, groupByClause, havingClause, orderByClause);
	}

	static UpdateItemTester set(ExpressionTester stateFieldPathExpression, ExpressionTester newValue) {
		return new UpdateItemTester(stateFieldPathExpression, newValue);
	}

	static UpdateItemTester set(String stateFieldPathExpression, ExpressionTester newValue) {
		if (stateFieldPathExpression.indexOf(".") == -1) {
			StateFieldPathExpressionTester pathExpression = path(stateFieldPathExpression);
			pathExpression.hasIdentificationVariable = false;
			return set(pathExpression, newValue);
		}
		return set(path(stateFieldPathExpression), newValue);
	}

	static SizeExpressionTester size(String collectionValuedPathExpression) {
		return new SizeExpressionTester(collectionValuedPath(collectionValuedPathExpression));
	}

	static AllOrAnyExpressionTester some(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(SOME, subquery);
	}

	static ExpressionTester spacedCollection(ExpressionTester... expressions) {

		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length,     Boolean.FALSE);

		spaces[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, commas, spaces);
	}

	static SqrtExpressionTester sqrt(ExpressionTester simpleArithmeticExpression) {
		return new SqrtExpressionTester(simpleArithmeticExpression);
	}

	static StringLiteralTester string(char literal) {
		return new StringLiteralTester("\'" + literal + "\'");
	}

	static StringLiteralTester string(String literal) {
		return new StringLiteralTester(literal);
	}

	static SubExpressionTester sub(ExpressionTester expression) {
		return new SubExpressionTester(expression);
	}

	static SimpleFromClauseTester subFrom(ExpressionTester declaration) {
		return new SimpleFromClauseTester(declaration);
	}

	static SimpleFromClauseTester subFrom(ExpressionTester... declarations) {
		return new SimpleFromClauseTester(collection(declarations));
	}

	static SimpleFromClauseTester subFrom(String abstractSchemaName, String identificationVariable) {
		return subFrom(identificationVariableDeclaration(abstractSchemaName, identificationVariable));
	}

	static SimpleFromClauseTester subFrom(String abstractSchemaName, String identificationVariable, ExpressionTester... joins) {
		return subFrom(identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins));
	}

	static SimpleFromClauseTester subFrom(String abstractSchemaName, String identificationVariable, ExpressionTester joins) {
		return subFrom(identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins));
	}

	static SimpleSelectStatementTester subquery(ExpressionTester selectClause, ExpressionTester fromClause) {
		return subquery(selectClause, fromClause, nullExpression());
	}

	static SimpleSelectStatementTester subquery(ExpressionTester selectClause, ExpressionTester fromClause, ExpressionTester whereClause) {
		return subSelectStatement(selectClause, fromClause, whereClause);
	}

	static SimpleSelectClauseTester subSelect(ExpressionTester selectExpression) {
		return subSelect(selectExpression, false);
	}

	static SimpleSelectClauseTester subSelect(ExpressionTester... selectExpressions) {
		return new SimpleSelectClauseTester(collection(selectExpressions), false);
	}

	private static SimpleSelectClauseTester subSelect(ExpressionTester selectExpression, boolean hasDistinct) {
		return new SimpleSelectClauseTester(selectExpression, hasDistinct);
	}

	static SimpleSelectClauseTester subSelectDistinct(ExpressionTester selectExpression) {
		return subSelect(selectExpression, true);
	}

	static SimpleSelectClauseTester subSelectDistinct(ExpressionTester... selectExpressions) {
		return new SimpleSelectClauseTester(collection(selectExpressions), true);
	}

	static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause, ExpressionTester fromClause, ExpressionTester whereClause) {
		return subSelectStatement(selectClause, fromClause, whereClause, nullExpression(), nullExpression());
	}

	static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause, ExpressionTester fromClause, ExpressionTester whereClause, ExpressionTester groupByClause, ExpressionTester havingClause) {
		return new SimpleSelectStatementTester(selectClause, fromClause, whereClause, groupByClause, havingClause);
	}

	static SubstractionExpressionTester substract(ExpressionTester leftExpression, ExpressionTester rightExpression) {
		return new SubstractionExpressionTester(leftExpression, rightExpression);
	}

	static SubstringExpressionTester substring(ExpressionTester firstExpression, ExpressionTester secondExpression) {
		return substring(firstExpression, secondExpression, nullExpression());
	}

	static SubstringExpressionTester substring(ExpressionTester firstExpression, ExpressionTester secondExpression, ExpressionTester thirdExpression) {
		return new SubstringExpressionTester(firstExpression, secondExpression, thirdExpression);
	}

	static SumFunctionTester sum(ExpressionTester expression) {
		return new SumFunctionTester(expression, false);
	}

	static SumFunctionTester sum(String statefieldPathExpression) {
		return sum(path(statefieldPathExpression));
	}

	static SumFunctionTester sumDistinct(String statefieldPathExpression) {
		return new SumFunctionTester(path(statefieldPathExpression), true);
	}

	static TrimExpressionTester trim(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.DEFAULT, stringPrimary, nullExpression(), false);
	}

	static TrimExpressionTester trimBothFrom(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.BOTH, stringPrimary, nullExpression(), true);
	}

	static TrimExpressionTester trimFrom(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.DEFAULT, stringPrimary, nullExpression(), true);
	}

	static TrimExpressionTester trimLeading(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.LEADING, stringPrimary, nullExpression(), false);
	}

	static TrimExpressionTester trimLeadingFrom(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.LEADING, stringPrimary, nullExpression(), true);
	}

	static TrimExpressionTester trimTrailing(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.TRAILING, stringPrimary, nullExpression(), false);
	}

	static TrimExpressionTester trimTrailingFrom(ExpressionTester stringPrimary) {
		return new TrimExpressionTester(Specification.TRAILING, stringPrimary, nullExpression(), true);
	}

	static ExpressionTester TRUE() {
		return new KeywordExpressionTester(TRUE);
	}

	static TypeExpressionTester type(String identificationVariable) {
		return new TypeExpressionTester(variable(identificationVariable));
	}

	static UnknownExpressionTester unknown(String unknown) {
		return new UnknownExpressionTester(unknown);
	}

	static UpdateClauseTester update(String abstractSchemaName, ExpressionTester updateItem) {
		UpdateClauseTester updateClause = new UpdateClauseTester(rangeVariableDeclaration(abstractSchemaName, null), updateItem);
		updateClause.hasSpaceAfterRangeVariableDeclaration = false;
		return updateClause;
	}

	static UpdateClauseTester update(String abstractSchemaName, String identificationVariable, ExpressionTester updateItem) {
		return new UpdateClauseTester(rangeVariableDeclaration(abstractSchemaName, identificationVariable), updateItem);
	}

	static UpdateClauseTester update(String abstractSchemaName, String identificationVariable, ExpressionTester... updateItems) {
		return new UpdateClauseTester(rangeVariableDeclaration(abstractSchemaName, identificationVariable), collection(updateItems));
	}

	static UpdateStatementTester updateStatement(ExpressionTester updateClause) {
		return updateStatement(updateClause, nullExpression());
	}

	static UpdateStatementTester updateStatement(ExpressionTester updateClause, ExpressionTester whereClause) {
		return new UpdateStatementTester(updateClause, whereClause);
	}

	static UpperExpressionTester upper(ExpressionTester stringPrimary) {
		return new UpperExpressionTester(stringPrimary);
	}

	static IdentificationVariableTester variable(String identificationVariable) {
		return new IdentificationVariableTester(identificationVariable);
	}

	static WhenClauseTester when(ExpressionTester conditionalExpression, ExpressionTester thenExpression) {
		return new WhenClauseTester(conditionalExpression, thenExpression);
	}

	static WhereClauseTester where(ExpressionTester conditionalExpression) {
		return new WhereClauseTester(conditionalExpression);
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
	final void testInvalidQuery(String query, ExpressionTester queryStatement) {
		testInvalidQuery(query, queryStatement, JPQLQueryStringFormatter.DEFAULT);
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
	 * @param version The JPA version used for parsing the query
	 */
	final void testInvalidQuery(String query, ExpressionTester queryStatement, IJPAVersion version) {
		testInvalidQuery(query, queryStatement, version, JPQLQueryStringFormatter.DEFAULT);
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
	 * @param version The JPA version used for parsing the query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	final void testInvalidQuery(String query,
	                            ExpressionTester queryStatement,
	                            IJPAVersion version,
	                            JPQLQueryStringFormatter formatter) {

		testQuery(query, queryStatement, version, formatter, true);
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
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	final void testInvalidQuery(String query,
	                            ExpressionTester queryStatement,
	                            JPQLQueryStringFormatter formatter) {

		testInvalidQuery(query, queryStatement, IJPAVersion.DEFAULT_VERSION, formatter);
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
	final void testQuery(String query, ExpressionTester queryStatement) {
		testQuery(query, queryStatement, JPQLQueryStringFormatter.DEFAULT);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 *
	 * @param query The JPQL query to parse and to test the parsed tree representation
	 * @param queryStatement The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param version The JPA version used for parsing the query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse grammatically invalid or incomplete queries
	 */
	private final void testQuery(String query,
	                             ExpressionTester queryStatement,
	                             IJPAVersion version,
	                             JPQLQueryStringFormatter formatter,
	                             boolean tolerant) {

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query, version, tolerant, formatter);

		if (queryStatement.getClass() != JPQLExpressionTester.class) {
			queryStatement = jpqlExpression(queryStatement);
		}

		queryStatement.test(jpqlExpression);
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
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	final void testQuery(String query,
	                     ExpressionTester queryStatement,
	                     JPQLQueryStringFormatter formatter) {

		testValidQuery(query, queryStatement, formatter);
		testInvalidQuery(query, queryStatement, formatter);
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
	final void testValidQuery(String query, ExpressionTester queryStatement) {
		testValidQuery(query, queryStatement, JPQLQueryStringFormatter.DEFAULT);
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
	final void testValidQuery(String query, ExpressionTester queryStatement, IJPAVersion version) {
		testValidQuery(query, queryStatement, version, JPQLQueryStringFormatter.DEFAULT);
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
	 * @param version The JPA version used to parse the query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	final void testValidQuery(String query,
	                          ExpressionTester queryStatement,
	                          IJPAVersion version,
	                          JPQLQueryStringFormatter formatter) {

		testQuery(query, queryStatement, version, formatter, false);
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
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	final void testValidQuery(String query,
	                          ExpressionTester queryStatement,
	                          JPQLQueryStringFormatter formatter) {

		testValidQuery(query, queryStatement, IJPAVersion.DEFAULT_VERSION, formatter);
	}

	static final class AbsExpressionTester extends AbstractSingleEncapsulatedExpressionTester {
		AbsExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return AbsExpression.class;
		}

		@Override
		String identifier() {
			return ABS;
		}
	}

	static abstract class AbstractConditionalClauseTester extends AbstractExpressionTester {
		private ExpressionTester conditionalExpression;
		boolean hasSpaceAfterIdentifier;

		AbstractConditionalClauseTester(ExpressionTester conditionalExpression) {
			super();

			this.conditionalExpression   = conditionalExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		abstract Class<? extends AbstractConditionalClause> expressionType();

		abstract String identifier();

		public void test(Expression expression) {
			assertInstance(expression, expressionType());

			AbstractConditionalClause conditionalClause = (AbstractConditionalClause) expression;
			assertEquals(toString(), conditionalClause.toParsedText());
			assertTrue(conditionalClause.hasConditionalExpression());
			assertEquals(hasSpaceAfterIdentifier, conditionalClause.hasSpaceAfterIdentifier());

			conditionalExpression.test(conditionalClause.getConditionalExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());

			if (hasSpaceAfterIdentifier) {
				sb.append(" ");
			}

			sb.append(conditionalExpression);
			return sb.toString();
		}
	}

	static abstract class AbstractDoubleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {
		private ExpressionTester firstExpression;
		boolean hasComma;
		boolean hasSpaceAfterComma;
		private ExpressionTester secondExpression;

		AbstractDoubleEncapsulatedExpressionTester(ExpressionTester firstExpression,
		                                           ExpressionTester secondExpression) {
			super();

			this.hasComma = true;
			this.hasSpaceAfterComma = true;
			this.firstExpression = firstExpression;
			this.secondExpression = secondExpression;
		}

		@Override
		abstract Class<? extends AbstractDoubleEncapsulatedExpression> expressionType();

		@Override
		boolean hasEncapsulatedExpression() {
			return !firstExpression.isNull() || hasComma || !secondExpression.isNull();
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			AbstractDoubleEncapsulatedExpression abstractDoubleEncapsulatedExpression = (AbstractDoubleEncapsulatedExpression) expression;
			assertEquals(!firstExpression.isNull(),  abstractDoubleEncapsulatedExpression.hasFirstExpression());
			assertEquals(!secondExpression.isNull(), abstractDoubleEncapsulatedExpression.hasSecondExpression());
			assertEquals(hasComma,           abstractDoubleEncapsulatedExpression.hasComma());
			assertEquals(hasSpaceAfterComma, abstractDoubleEncapsulatedExpression.hasSpaceAfterComma());

			firstExpression .test(abstractDoubleEncapsulatedExpression.getFirstExpression());
			secondExpression.test(abstractDoubleEncapsulatedExpression.getSecondExpression());
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(firstExpression);

			if (hasComma) {
				sb.append(",");
			}

			if (hasSpaceAfterComma) {
				sb.append(" ");
			}

			sb.append(secondExpression);
		}
	}

	static abstract class AbstractEncapsulatedExpressionTester extends AbstractExpressionTester {
		boolean hasLeftParenthesis;
		boolean hasRightParenthesis;

		AbstractEncapsulatedExpressionTester() {
			super();

			this.hasLeftParenthesis  = true;
			this.hasRightParenthesis = true;
		}

		abstract Class<? extends AbstractEncapsulatedExpression> expressionType();

		abstract boolean hasEncapsulatedExpression();

		abstract String identifier();

		public void test(Expression expression) {
			assertInstance(expression, expressionType());

			AbstractEncapsulatedExpression abstractEncapsulatedExpression = (AbstractEncapsulatedExpression) expression;
			assertEquals(toString(), abstractEncapsulatedExpression.toParsedText());
			assertEquals(hasLeftParenthesis, abstractEncapsulatedExpression.hasLeftParenthesis());
			assertEquals(hasRightParenthesis, abstractEncapsulatedExpression.hasRightParenthesis());
			assertEquals(hasEncapsulatedExpression(), abstractEncapsulatedExpression.hasEncapsulatedExpression());
		}

		@Override
		public final String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());

			if (hasLeftParenthesis) {
				sb.append("(");
			}
			else if (hasEncapsulatedExpression()) {
				sb.append(" ");
			}

			toStringEncapsulatedExpression(sb);

			if (hasRightParenthesis) {
				sb.append(")");
			}

			return sb.toString();
		}

		abstract void toStringEncapsulatedExpression(StringBuilder sb);
	}

	/**
	 * The abstract definition of an {@link ExpressionTester}.
	 */
	static abstract class AbstractExpressionTester implements ExpressionTester {
		public final AdditionExpressionTester add(ExpressionTester expression) {
			return AbstractJPQLTest.add(this, expression);
		}

		public final AndExpressionTester and(ExpressionTester expression) {
			return AbstractJPQLTest.and(this, expression);
		}

		final void assertInstance(Expression expression, Class<? extends Expression> expressionType) {

			Class<? extends Expression> expressionClass = expression.getClass();

			if (expressionClass != expressionType &&
			   !expressionType.isAssignableFrom(expressionClass)) {

				fail(String.format (
					"Expecting %s but was %s for %s",
					expressionType.getSimpleName(),
					expressionClass.getSimpleName(),
					expression.toParsedText()
				));
			}
		}

		public final BetweenExpressionTester between(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression) {
			return AbstractJPQLTest.between(this, lowerBoundExpression, upperBoundExpression);
		}

		public final ComparisonExpressionTester different(ExpressionTester expression) {
			return AbstractJPQLTest.different(this, expression);
		}

		public final DivisionExpressionTester division(ExpressionTester expression) {
			return AbstractJPQLTest.division(this, expression);
		}

		public final ComparisonExpressionTester equal(ExpressionTester expression) {
			return AbstractJPQLTest.equal(this, expression);
		}

		public final ComparisonExpressionTester greaterThan(ExpressionTester expression) {
			return AbstractJPQLTest.greaterThan(this, expression);
		}

		public final ComparisonExpressionTester greaterThanOrEqual(ExpressionTester expression) {
			return AbstractJPQLTest.greaterThanOrEqual(this, expression);
		}

		public final InExpressionTester in(ExpressionTester... inItems) {
			if (inItems.length == 1) {
				return AbstractJPQLTest.in(this, inItems[0]);
			}
			return AbstractJPQLTest.in(this, inItems);
		}

		public boolean isNull() {
			return false;
		}

		public final LikeExpressionTester like(ExpressionTester patternValue) {
			return AbstractJPQLTest.like(this, patternValue);
		}

		public final LikeExpressionTester like(ExpressionTester patternValue, ExpressionTester escapeCharacter) {
			return AbstractJPQLTest.like(this, patternValue, escapeCharacter);
		}

		public final ComparisonExpressionTester lowerThan(ExpressionTester expression) {
			return AbstractJPQLTest.lowerThan(this, expression);
		}

		public final ComparisonExpressionTester lowerThanOrEqual(ExpressionTester expression) {
			return AbstractJPQLTest.lowerThanOrEqual(this, expression);
		}

		public final CollectionMemberExpressionTester member(ExpressionTester collectionValuedPathExpression) {
			return AbstractJPQLTest.member(this, collectionValuedPathExpression);
		}

		public final CollectionMemberExpressionTester memberOf(ExpressionTester collectionValuedPathExpression) {
			return AbstractJPQLTest.memberOf(this, collectionValuedPathExpression);
		}

		public final MultiplicationExpressionTester multiplication(ExpressionTester expression) {
			return AbstractJPQLTest.multiplication(this, expression);
		}

		public final BetweenExpressionTester notBetween(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression) {
			return AbstractJPQLTest.notBetween(this, lowerBoundExpression, upperBoundExpression);
		}

		public final InExpressionTester notIn(ExpressionTester... inItems) {
			if (inItems.length == 1) {
				return AbstractJPQLTest.notIn(this, inItems[0]);
			}
			return AbstractJPQLTest.notIn(this, inItems);
		}

		public LikeExpressionTester notLike(ExpressionTester expression) {
			return AbstractJPQLTest.notLike(this, expression);
		}

		public LikeExpressionTester notLike(ExpressionTester expression, ExpressionTester escapeCharacter) {
			return AbstractJPQLTest.notLike(this, expression, escapeCharacter);
		}

		public final ExpressionTester notMember(ExpressionTester collectionValuedPathExpression) {
			return AbstractJPQLTest.notMember(this, collectionValuedPathExpression);
		}

		public final ExpressionTester notMemberOf(ExpressionTester collectionValuedPathExpression) {
			return AbstractJPQLTest.notMemberOf(this, collectionValuedPathExpression);
		}

		public final OrExpressionTester or(ExpressionTester expression) {
			return AbstractJPQLTest.or(this, expression);
		}

		public final SubstractionExpressionTester substract(ExpressionTester expression) {
			return AbstractJPQLTest.substract(this, expression);
		}
	}

	static abstract class AbstractFromClauseTester extends AbstractExpressionTester {
		private ExpressionTester declaration;

		AbstractFromClauseTester(ExpressionTester declaration) {
			super();
			this.declaration = declaration;
		}

		public void test(Expression expression) {
			assertInstance(expression, AbstractFromClause.class);

			AbstractFromClause fromClause = (AbstractFromClause) expression;
			assertEquals(toString(), fromClause.toParsedText());
			assertTrue  (fromClause.hasDeclaration());
			assertTrue  (fromClause.hasSpaceAfterFrom());

			declaration.test(fromClause.getDeclaration());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(FROM);
			sb.append(" ");
			sb.append(declaration);
			return sb.toString();
		}
	}

	static abstract class AbstractPathExpressionTester extends AbstractExpressionTester {
		boolean hasIdentificationVariable;
		private String value;

		AbstractPathExpressionTester(String value) {
			super();
			this.value = value;
			this.hasIdentificationVariable = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, AbstractPathExpression.class);

			AbstractPathExpression abstractPathExpression = (AbstractPathExpression) expression;
			assertEquals(value, abstractPathExpression.toParsedText());
			assertEquals(hasIdentificationVariable, abstractPathExpression.hasIdentificationVariable());
			assertFalse (abstractPathExpression.endsWithDot());
			assertFalse (abstractPathExpression.startsWithDot());
		}
		@Override
		public String toString() {
			return value;
		}
	}

	static final class AbstractSchemaNameTester extends AbstractExpressionTester {
		private String abstractSchemaName;

		AbstractSchemaNameTester(String abstractSchemaName) {
			super();
			this.abstractSchemaName = abstractSchemaName;
		}

		public void test(Expression expression) {
			assertInstance(expression, AbstractSchemaName.class);

			AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;
			assertEquals(toString(), abstractSchemaName.toParsedText());
		}
		@Override
		public String toString() {
			return abstractSchemaName;
		}
	}

	static abstract class AbstractSelectClauseTester extends AbstractExpressionTester {

		private boolean hasDistinct;
		boolean hasSpaceAfterDistinct;
		boolean hasSpaceAfterSelect;
		private ExpressionTester selectExpression;

		AbstractSelectClauseTester(ExpressionTester selectExpression, boolean hasDistinct) {
			super();
			this.hasDistinct           = hasDistinct;
			this.hasSpaceAfterDistinct = hasDistinct;
			this.hasSpaceAfterSelect   = hasDistinct || !selectExpression.isNull();
			this.selectExpression      = selectExpression;
		}

		public void test(Expression expression) {

			assertInstance(expression, AbstractSelectClause.class);
			AbstractSelectClause selectClause = (AbstractSelectClause) expression;

			assertEquals(toString(),                 selectClause.toParsedText());
			assertEquals(hasSpaceAfterSelect,        selectClause.hasSpaceAfterSelect());
			assertEquals(hasDistinct,                selectClause.hasDistinct());
			assertEquals(hasSpaceAfterDistinct,      selectClause.hasSpaceAfterDistinct());
			assertEquals(!selectExpression.isNull(), selectClause.hasSelectExpression());

			selectExpression.test(selectClause.getSelectExpression());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(SELECT);

			if (hasSpaceAfterSelect) {
				sb.append(" ");
			}

			if (hasDistinct) {
				sb.append(DISTINCT);
			}

			if (hasSpaceAfterDistinct) {
				sb.append(" ");
			}

			sb.append(selectExpression);
			return sb.toString();
		}
	}

	static abstract class AbstractSelectStatementTester extends AbstractExpressionTester {
		private ExpressionTester fromClause;
		private ExpressionTester groupByClause;
		boolean hasSpaceAfterFrom;
		boolean hasSpaceAfterGroupBy;
		boolean hasSpaceAfterSelect;
		boolean hasSpaceAfterWhere;
		private ExpressionTester havingClause;
		private ExpressionTester selectClause;
		private ExpressionTester whereClause;

		AbstractSelectStatementTester(ExpressionTester selectClause,
		                              ExpressionTester fromClause,
		                              ExpressionTester whereClause,
		                              ExpressionTester groupByClause,
		                              ExpressionTester havingClause) {
			super();

			this.selectClause  = selectClause;
			this.fromClause    = fromClause;
			this.whereClause   = whereClause;
			this.groupByClause = groupByClause;
			this.havingClause  = havingClause;

			hasSpaceAfterSelect  = !fromClause   .isNull();
			hasSpaceAfterFrom    = !fromClause   .isNull() && !whereClause  .isNull() || !groupByClause.isNull() || !havingClause.isNull();
			hasSpaceAfterWhere   = !whereClause  .isNull() && !groupByClause.isNull() || !havingClause .isNull();
			hasSpaceAfterGroupBy = !groupByClause.isNull() && !havingClause .isNull();
		}

		abstract Class<? extends AbstractSelectStatement> expressionType();

		public void test(Expression expression) {
			assertInstance(expression, expressionType());

			AbstractSelectStatement selectStatement = (AbstractSelectStatement) expression;
			assertEquals(toString(), selectStatement.toParsedText());

			assertEquals(!fromClause   .isNull(), selectStatement.hasFromClause());
			assertEquals(!whereClause  .isNull(), selectStatement.hasWhereClause());
			assertEquals(!groupByClause.isNull(), selectStatement.hasGroupByClause());
			assertEquals(!havingClause .isNull(), selectStatement.hasHavingClause());

//			assertEquals(hasSpaceAfterFrom,    selectStatement.hasSpaceAfterFrom());
//			assertEquals(hasSpaceAfterGroupBy, selectStatement.hasSpaceAfterGroupBy());
//			assertEquals(hasSpaceAfterSelect,  selectStatement.hasSpaceAfterSelect());
//			assertEquals(hasSpaceAfterWhere,   selectStatement.hasSpaceAfterWhere());

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

			if (hasSpaceAfterSelect && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(" ");
			}

			sb.append(fromClause);

			if (hasSpaceAfterFrom && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(" ");
			}

			sb.append(whereClause);

			if (hasSpaceAfterWhere && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(" ");
			}

			sb.append(groupByClause);

			if (hasSpaceAfterGroupBy && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(" ");
			}

			sb.append(havingClause);
			return sb.toString();
		}
	}

	static abstract class AbstractSingleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {
		private ExpressionTester expression;

		AbstractSingleEncapsulatedExpressionTester(ExpressionTester expression) {
			super();
			this.expression = expression;
		}

		@Override
		abstract Class<? extends AbstractSingleEncapsulatedExpression> expressionType();

		@Override
		final boolean hasEncapsulatedExpression() {
			return !expression.isNull();
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			AbstractSingleEncapsulatedExpression encapsulatedExpression = (AbstractSingleEncapsulatedExpression) expression;
			this.expression.test(encapsulatedExpression.getExpression());
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(expression);
		}
	}

	static abstract class AbstractTripleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {
		private ExpressionTester firstExpression;
		boolean hasFirstComma;
		boolean hasSecondComma;
		boolean hasSpaceAfterFirstComma;
		boolean hasSpaceAfterSecondComma;
		private ExpressionTester secondExpression;
		private ExpressionTester thirdExpression;

		AbstractTripleEncapsulatedExpressionTester(ExpressionTester firstExpression,
		                                           ExpressionTester secondExpression,
		                                           ExpressionTester thirdExpression) {
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
		abstract Class<? extends AbstractTripleEncapsulatedExpression> expressionType();

		@Override
		boolean hasEncapsulatedExpression() {
			return !firstExpression.isNull() || hasFirstComma || !secondExpression.isNull() || hasSecondComma || !thirdExpression.isNull();
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, AbstractTripleEncapsulatedExpression.class);

			AbstractTripleEncapsulatedExpression tripleExpression = (AbstractTripleEncapsulatedExpression) expression;
			assertEquals(toString(), tripleExpression.toParsedText());

			assertEquals(hasFirstComma,  tripleExpression.hasFirstComma());
			assertEquals(hasSecondComma, tripleExpression.hasSecondComma());

			assertEquals(hasSpaceAfterFirstComma,  tripleExpression.hasSpaceAfterFirstComma());
			assertEquals(hasSpaceAfterSecondComma, tripleExpression.hasSpaceAfterSecondComma());

			assertEquals(!firstExpression.isNull(),  tripleExpression.hasFirstExpression());
			assertEquals(!secondExpression.isNull(), tripleExpression.hasSecondExpression());
			assertEquals(!thirdExpression.isNull(),  tripleExpression.hasThirdExpression());

			firstExpression.test(tripleExpression.getFirstExpression());
			secondExpression.test(tripleExpression.getSecondExpression());
			thirdExpression.test(tripleExpression.getThirdExpression());
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb) {

			sb.append(firstExpression);

			if (hasFirstComma) {
				sb.append(",");
			}

			if (hasSpaceAfterFirstComma) {
				sb.append(" ");
			}

			sb.append(secondExpression);

			if (hasSecondComma) {
				sb.append(",");
			}

			if (hasSpaceAfterSecondComma) {
				sb.append(" ");
			}

			sb.append(thirdExpression);
		}
	}

	static final class AdditionExpressionTester extends CompoundExpressionTester {
		AdditionExpressionTester(ExpressionTester leftExpression,
		                         ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType() {
			return AdditionExpression.class;
		}

		@Override
		String identifier() {
			return PLUS;
		}
	}

	static abstract class AggregateFunctionTester extends AbstractSingleEncapsulatedExpressionTester {
		boolean hasDistinct;
		boolean hasSpaceAfterDistinct;

		AggregateFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression);
			this.hasDistinct = hasDistinct;
			this.hasSpaceAfterDistinct = hasDistinct;
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb) {

			if (hasDistinct) {
				sb.append(DISTINCT);
			}

			if (hasSpaceAfterDistinct) {
				sb.append(" ");
			}

			super.toStringEncapsulatedExpression(sb);
		}
	}

	static final class AllOrAnyExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private String identifier;

		AllOrAnyExpressionTester(String identifier, ExpressionTester expression) {
			super(expression);
			this.identifier = identifier;
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return AllOrAnyExpression.class;
		}

		@Override
		String identifier() {
			return identifier;
		}
	}

	static final class AndExpressionTester extends LogicalExpressionTester {

		AndExpressionTester(ExpressionTester leftExpression, ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType() {
			return AndExpression.class;
		}

		@Override
		String identifier() {
			return AND;
		}
	}

	static final class ArithmeticFactorTester extends AbstractExpressionTester {

		private ExpressionTester expression;
		private String sign;

		ArithmeticFactorTester(String sign, ExpressionTester expression) {
			super();
			this.sign = sign;
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, ArithmeticFactor.class);

			ArithmeticFactor factor = (ArithmeticFactor) expression;
			assertEquals(toString(),    factor.toParsedText());
			assertEquals(sign == MINUS, factor.hasMinusSign());
			assertEquals(sign == PLUS,  factor.hasPlusSign());

			this.expression.test(factor.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(sign);
			sb.append(expression);
			return sb.toString();
		}
	}

	static final class AvgFunctionTester extends AggregateFunctionTester {
		AvgFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return AvgFunction.class;
		}

		@Override
		String identifier() {
			return AVG;
		}
	}

	static final class BetweenExpressionTester extends AbstractExpressionTester {
		private ExpressionTester expression;
		boolean hasAnd;
		private boolean hasNot;
		boolean hasSpaceAfterAnd;
		boolean hasSpaceAfterBetween;
		boolean hasSpaceAfterLowerBound;
		private ExpressionTester lowerBoundExpression;
		private ExpressionTester upperBoundExpression;

		BetweenExpressionTester(ExpressionTester expression,
		                        boolean hasNot,
		                        ExpressionTester lowerBoundExpression,
		                        ExpressionTester upperBoundExpression) {
			super();

			this.hasNot                  = hasNot;
			this.hasAnd                  = true;
			this.hasSpaceAfterAnd        = true;
			this.hasSpaceAfterLowerBound = true;
			this.hasSpaceAfterBetween    = true;
			this.expression              = expression;
			this.lowerBoundExpression    = lowerBoundExpression;
			this.upperBoundExpression    = upperBoundExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, BetweenExpression.class);

			BetweenExpression betweenExpression = (BetweenExpression) expression;
			assertEquals(toString(),                     betweenExpression.toParsedText());
			assertEquals(hasAnd,                         betweenExpression.hasAnd());
			assertEquals(!this.expression.isNull(),      betweenExpression.hasExpression());
			assertEquals(!lowerBoundExpression.isNull(), betweenExpression.hasLowerBoundExpression());
			assertEquals(hasNot,                         betweenExpression.hasNot());
			assertEquals(hasSpaceAfterAnd,               betweenExpression.hasSpaceAfterAnd());
			assertEquals(hasSpaceAfterBetween,           betweenExpression.hasSpaceAfterBetween());
			assertEquals(hasSpaceAfterLowerBound,        betweenExpression.hasSpaceAfterLowerBound());
			assertEquals(!upperBoundExpression.isNull(), betweenExpression.hasUpperBoundExpression());

			this.expression.test(betweenExpression.getExpression());
			lowerBoundExpression.test(betweenExpression.getLowerBoundExpression());
			upperBoundExpression.test(betweenExpression.getUpperBoundExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(expression);
			if (!expression.isNull()) {
				sb.append(" ");
			}
			if (hasNot) {
				sb.append("NOT ");
			}
			sb.append(BETWEEN);
			if (hasSpaceAfterBetween) {
				sb.append(" ");
			}
			sb.append(lowerBoundExpression);
			if (hasSpaceAfterLowerBound) {
				sb.append(" ");
			}
			if (hasAnd) {
				sb.append(AND);
			}
			if (hasSpaceAfterAnd) {
				sb.append(" ");
			}
			sb.append(upperBoundExpression);
			return sb.toString();
		}
	}

	static final class CaseExpressionTester extends AbstractExpressionTester {

		private ExpressionTester caseOperand;
		private ExpressionTester elseExpression;
		boolean hasElse;
		boolean hasEnd;
		private ExpressionTester whenClauses;

		CaseExpressionTester(ExpressionTester caseOperand, ExpressionTester whenClauses, ExpressionTester elseExpression) {
			super();
			this.whenClauses    = whenClauses;
			this.caseOperand    = caseOperand;
			this.elseExpression = elseExpression;
			this.hasElse        = !elseExpression.isNull();
			this.hasEnd         = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, CaseExpression.class);

			CaseExpression caseExpression = (CaseExpression) expression;
			assertEquals(toString(), caseExpression.toParsedText());
			assertEquals(!caseOperand.isNull(), caseExpression.hasCaseOperand());
			assertEquals(hasElse, caseExpression.hasElse());
			assertEquals(hasEnd,  caseExpression.hasEnd());
			assertTrue  (caseExpression.hasSpaceAfterCase());
			assertTrue  (caseExpression.hasSpaceAfterElse());
			assertTrue  (caseExpression.hasSpaceAfterElseExpression());
			assertTrue  (caseExpression.hasSpaceAfterWhenClauses());
			assertTrue  (caseExpression.hasWhenClauses());

			caseOperand.test(caseExpression.getCaseOperand());
			whenClauses.test(caseExpression.getWhenClauses());
			elseExpression.test(caseExpression.getElseExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(CASE);
			sb.append(" ");

			if (!caseOperand.isNull()) {
				sb.append(caseOperand);
				sb.append(" ");
			}

			sb.append(whenClauses);
			sb.append(" ");
			sb.append(ELSE);
			sb.append(" ");
			sb.append(elseExpression);
			sb.append(" ");
			sb.append(END);
			return sb.toString();
		}
	}

	protected static class CoalesceExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		CoalesceExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return CoalesceExpression.class;
		}

		@Override
		String identifier(){
			return COALESCE;
		}
	}

	static final class CollectionExpressionTester extends AbstractExpressionTester {

		private Boolean[] commas;
		private ExpressionTester[] expressionTesters;
		private Boolean[] spaces;

		CollectionExpressionTester(ExpressionTester[] expressionTesters, Boolean[] commas, Boolean[] spaces) {
			super();
			this.expressionTesters = expressionTesters;
			this.spaces = spaces;
			this.commas = commas;
		}

		public void test(Expression expression) {
			assertInstance(expression, CollectionExpression.class);

			CollectionExpression collectionExpression = (CollectionExpression) expression;
			assertEquals(toString(), collectionExpression.toParsedText());
			assertEquals(spaces.length, collectionExpression.childrenSize());
			assertEquals(commas.length, collectionExpression.childrenSize());
			assertEquals(expressionTesters.length, collectionExpression.childrenSize());

			// Expressions
			for (int index = expressionTesters.length; --index >= 0; ) {
				expressionTesters[index].test(collectionExpression.getChild(index));
			}

			// Spaces
			for (int index = 0, count = spaces.length; index < count; index++) {
				assertEquals(
					"The flag for a space at " + index + " does not match",
					spaces[index],
					collectionExpression.hasSpace(index)
				);
			}

			// Commas
			for (int index = 0, count = commas.length; index < count; index++) {
				assertEquals(
					"The flag for a comma at " + index + " does not match",
					commas[index],
					collectionExpression.hasComma(index)
				);
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			for (int index = 0, count = expressionTesters.length; index < count; index++) {
				sb.append(expressionTesters[index]);

				if ((index < commas.length) && commas[index]) {
					sb.append(",");
				}

				if ((index < spaces.length) && spaces[index]) {
					sb.append(" ");
				}
			}

			return sb.toString();
		}
	}

	static final class CollectionMemberDeclarationTester extends AbstractExpressionTester {

		private ExpressionTester collectionValuedPath;
		public boolean hasAs;
		public boolean hasLeftParenthesis;
		public boolean hasRightParenthesis;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterIn;
		public boolean hasSpaceAfterRightParenthesis;
		private ExpressionTester identificationVariable;

		CollectionMemberDeclarationTester(ExpressionTester collectionValuedPath, boolean hasAs, ExpressionTester identificationVariable) {
			super();
			this.hasAs                         = hasAs;
			this.hasSpaceAfterAs               = hasAs;
			this.hasLeftParenthesis            = true;
			this.hasRightParenthesis           = true;
			this.collectionValuedPath          = collectionValuedPath;
			this.identificationVariable        = identificationVariable;
			this.hasSpaceAfterRightParenthesis = hasAs || !identificationVariable.isNull();
		}

		public void test(Expression expression) {
			assertInstance(expression, CollectionMemberDeclaration.class);

			CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;
			assertEquals(toString(),                       collectionMemberDeclaration.toParsedText());
			assertEquals(hasAs,                            collectionMemberDeclaration.hasAs());
			assertEquals(!collectionValuedPath.isNull(),   collectionMemberDeclaration.hasCollectionValuedPathExpression());
			assertEquals(!identificationVariable.isNull(), collectionMemberDeclaration.hasIdentificationVariable());
			assertEquals(hasLeftParenthesis,               collectionMemberDeclaration.hasLeftParenthesis());
			assertEquals(hasRightParenthesis,              collectionMemberDeclaration.hasRightParenthesis());
			assertEquals(hasSpaceAfterAs,                  collectionMemberDeclaration.hasSpaceAfterAs());
			assertEquals(hasSpaceAfterIn,                  collectionMemberDeclaration.hasSpaceAfterIn());
			assertEquals(hasSpaceAfterRightParenthesis,    collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

			collectionValuedPath.test(collectionMemberDeclaration.getCollectionValuedPathExpression());
			identificationVariable.test(collectionMemberDeclaration.getIdentificationVariable());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(IN);
			if (hasSpaceAfterIn) {
				sb.append(" ");
			}
			else if (hasLeftParenthesis) {
				sb.append("(");
			}
			sb.append(collectionValuedPath);
			if (hasRightParenthesis) {
				sb.append(")");
			}
			if (hasSpaceAfterRightParenthesis) {
				sb.append(" ");
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(" ");
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	static final class CollectionMemberExpressionTester extends AbstractExpressionTester {

		private ExpressionTester collectionValuedPathExpression;
		private ExpressionTester entityExpression;
		private boolean hasNot;
		private boolean hasOf;
		public boolean hasSpaceAfterMember;
		public boolean hasSpaceAfterOf;

		CollectionMemberExpressionTester(ExpressionTester entityExpression, boolean hasNot, boolean hasOf, ExpressionTester collectionValuedPathExpression) {
			super();
			this.hasNot                         = hasNot;
			this.hasOf                          = hasOf;
			this.hasSpaceAfterMember            = true;
			this.hasSpaceAfterOf                = hasOf;
			this.entityExpression               = entityExpression;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, CollectionMemberExpression.class);

			CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;
			assertEquals(toString(),                               collectionMemberExpression.toParsedText());
			assertEquals(!collectionValuedPathExpression.isNull(), collectionMemberExpression.hasCollectionValuedPathExpression());
			assertEquals(!entityExpression.isNull(),               collectionMemberExpression.hasEntityExpression());
			assertEquals(hasNot,                                   collectionMemberExpression.hasNot());
			assertEquals(hasSpaceAfterMember,                      collectionMemberExpression.hasSpaceAfterMember());
			assertEquals(hasOf,                                    collectionMemberExpression.hasOf());
			assertEquals(hasSpaceAfterOf,                          collectionMemberExpression.hasSpaceAfterOf());

			entityExpression.test(collectionMemberExpression.getEntityExpression());
			collectionValuedPathExpression.test(collectionMemberExpression.getCollectionValuedPathExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(entityExpression);
			if (!entityExpression.isNull()) {
				sb.append(" ");
			}
			if (hasNot) {
				sb.append("NOT ");
			}
			if (hasOf) {
				sb.append(MEMBER_OF);
				if (hasSpaceAfterOf) {
					sb.append(" ");
				}
			}
			else {
				sb.append(MEMBER);
				if (hasSpaceAfterMember) {
					sb.append(" ");
				}
			}
			sb.append(collectionValuedPathExpression);
			return sb.toString();
		}
	}

	static final class CollectionValuedPathExpressionTester extends AbstractPathExpressionTester {
		CollectionValuedPathExpressionTester(String value) {
			super(value);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, CollectionValuedPathExpression.class);
		}
	}

	static final class ComparisonExpressionTester extends AbstractExpressionTester {

		private String comparator;
		public boolean hasSpaceAfterIdentifier;
		private ExpressionTester leftExpression;
		private ExpressionTester rightExpression;

		ComparisonExpressionTester(String comparator, ExpressionTester leftExpression, ExpressionTester rightExpression) {
			super();
			this.comparator              = comparator;
			this.leftExpression          = leftExpression;
			this.rightExpression         = rightExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, ComparisonExpression.class);

			ComparisonExpression comparisonExpression = (ComparisonExpression) expression;
			assertEquals(toString(),                comparisonExpression.toParsedText());
			assertEquals(!leftExpression.isNull(),  comparisonExpression.hasLeftExpression());
			assertEquals(!rightExpression.isNull(), comparisonExpression.hasRightExpression());
			assertEquals(hasSpaceAfterIdentifier,   comparisonExpression.hasSpaceAfterIdentifier());

			leftExpression .test(comparisonExpression.getLeftExpression());
			rightExpression.test(comparisonExpression.getRightExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(leftExpression);
			if (!leftExpression.isNull()) {
				sb.append(" ");
			}
			sb.append(comparator);
			if (hasSpaceAfterIdentifier) {
				sb.append(" ");
			}
			sb.append(rightExpression);
			return sb.toString();
		}
	}

	static abstract class CompoundExpressionTester extends AbstractExpressionTester {
		boolean hasSpaceAfterIdentifier;
		private ExpressionTester leftExpression;
		private ExpressionTester rightExpression;

		CompoundExpressionTester(ExpressionTester leftExpression,
		                         ExpressionTester rightExpression) {
			super();

			this.leftExpression  = leftExpression;
			this.rightExpression = rightExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		abstract Class<? extends CompoundExpression> expressionType();

		abstract String identifier();

		public void test(Expression expression) {
			assertInstance(expression, expressionType());

			CompoundExpression compoundExpression = (CompoundExpression) expression;
			assertEquals(toString(), compoundExpression.toParsedText());
			assertEquals(!leftExpression.isNull(), compoundExpression.hasLeftExpression());
			assertEquals(!rightExpression.isNull(), compoundExpression.hasRightExpression());
			assertEquals(hasSpaceAfterIdentifier, compoundExpression.hasSpaceAfterIdentifier());

			leftExpression.test(compoundExpression.getLeftExpression());
			rightExpression.test(compoundExpression.getRightExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(leftExpression);

			if (!leftExpression.isNull()) {
				sb.append(" ");
			}

			sb.append(identifier());

			if (hasSpaceAfterIdentifier) {
				sb.append(" ");
			}

			sb.append(rightExpression);
			return sb.toString();
		}
	}

	static final class ConcatExpressionTester extends AbstractSingleEncapsulatedExpressionTester {
		ConcatExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return ConcatExpression.class;
		}

		@Override
		String identifier() {
			return CONCAT;
		}
	}

	static final class ConstructorExpressionTester extends AbstractExpressionTester {

		private String className;
		private ExpressionTester constructorItems;
		boolean hasLeftParenthesis;
		boolean hasRightParenthesis;
		boolean hasSpaceAfterNew;

		ConstructorExpressionTester(String className, ExpressionTester constructorItems) {
			super();
			this.className           = className;
			this.constructorItems    = constructorItems;
			this.hasSpaceAfterNew    = true;
			this.hasLeftParenthesis  = true;
			this.hasRightParenthesis = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, ConstructorExpression.class);

			ConstructorExpression constructorExpression = (ConstructorExpression) expression;
			assertEquals(toString(),                 constructorExpression.toParsedText());
			assertEquals(!constructorItems.isNull(), constructorExpression.hasConstructorItems());
			assertEquals(hasSpaceAfterNew,           constructorExpression.hasSpaceAfterNew());
			assertEquals(hasLeftParenthesis,         constructorExpression.hasLeftParenthesis());
			assertEquals(hasRightParenthesis,        constructorExpression.hasRightParenthesis());

			constructorItems.test(constructorExpression.getConstructorItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(NEW);
			if (hasSpaceAfterNew) {
				sb.append(" ");
			}
			sb.append(className);
			if (hasLeftParenthesis) {
				sb.append("(");
			}
			sb.append(constructorItems);
			if (hasRightParenthesis) {
				sb.append(")");
			}
			return sb.toString();
		}
	}

	static final class CountFunctionTester extends AggregateFunctionTester {
		CountFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return CountFunction.class;
		}

		@Override
		String identifier() {
			return COUNT;
		}
	}

	static final class DateTimeTester extends AbstractExpressionTester {
		private String dateTime;

		DateTimeTester(String dateTime) {
			super();
			this.dateTime = dateTime;
		}

		public void test(Expression expression) {
			assertInstance(expression, DateTime.class);

			DateTime dateTime = (DateTime) expression;
			assertEquals(toString(), dateTime.toParsedText());
		}

		@Override
		public String toString() {
			return dateTime;
		}
	}

	static final class DeleteClauseTester extends AbstractExpressionTester {
		private ExpressionTester rangeVariableDeclaration;

		DeleteClauseTester(ExpressionTester rangeVariableDeclaration) {
			super();
			this.rangeVariableDeclaration = rangeVariableDeclaration;
		}

		public void test(Expression expression) {
			assertInstance(expression, DeleteClause.class);

			DeleteClause deleteClause = (DeleteClause) expression;
			assertEquals(toString(), deleteClause.toParsedText());
			assertTrue(deleteClause.hasFrom());
			assertTrue(deleteClause.hasRangeVariableDeclaration());
			assertTrue(deleteClause.hasSpaceAfterDelete());
			assertTrue(deleteClause.hasSpaceAfterFrom());

			rangeVariableDeclaration.test(deleteClause.getRangeVariableDeclaration());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(DELETE_FROM);
			sb.append(" ");
			sb.append(rangeVariableDeclaration);
			return sb.toString();
		}
	}

	static final class DeleteStatementTester extends AbstractExpressionTester {
		private ExpressionTester deleteClause;
		private ExpressionTester whereClause;

		DeleteStatementTester(ExpressionTester deleteClause,
		                      ExpressionTester whereClause) {
			super();

			this.deleteClause = deleteClause;
			this.whereClause = whereClause;
		}

		public void test(Expression expression) {
			assertInstance(expression, DeleteStatement.class);

			DeleteStatement deleteStatement = (DeleteStatement) expression;
			assertEquals(toString(), deleteStatement.toParsedText());
			assertTrue(deleteStatement.hasSpaceAfterDeleteClause());
			assertTrue(deleteStatement.hasWhereClause());

			deleteClause.test(deleteStatement.getDeleteClause());
			whereClause.test(deleteStatement.getWhereClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(deleteClause);

			if (!whereClause.isNull()) {
				sb.append(" ");
				sb.append(whereClause);
			}

			return sb.toString();
		}
	}

	static final class DivisionExpressionTester extends CompoundExpressionTester {
		DivisionExpressionTester(ExpressionTester leftExpression,
		                         ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType() {
			return DivisionExpression.class;
		}

		@Override
		String identifier() {
			return DIVISION;
		}
	}

	static final class EmptyCollectionComparisonExpressionTester extends AbstractExpressionTester {
		private ExpressionTester collectionValuedPathExpression;
		private boolean hasNot;

		EmptyCollectionComparisonExpressionTester(ExpressionTester collectionValuedPathExpression,
		                                          boolean hasNot) {
			super();

			this.hasNot = hasNot;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, EmptyCollectionComparisonExpression.class);

			EmptyCollectionComparisonExpression emptyCollection = (EmptyCollectionComparisonExpression) expression;
			assertEquals(toString(), emptyCollection.toParsedText());
			assertTrue  (emptyCollection.hasExpression());
			assertEquals(hasNot, emptyCollection.hasNot());
			assertTrue  (emptyCollection.hasSpaceAfterIs());

			collectionValuedPathExpression.test(emptyCollection.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(collectionValuedPathExpression);
			sb.append(hasNot ? " IS NOT EMPTY" : " IS EMPTY");
			return sb.toString();
		}
	}

	static final class EntityTypeLiteralTester extends AbstractExpressionTester {
		private String entityType;

		EntityTypeLiteralTester(String entityType) {
			super();
			this.entityType = entityType;
		}

		public void test(Expression expression) {
			assertInstance(expression, EntityTypeLiteral.class);

			EntityTypeLiteral entityTypeLiteral = (EntityTypeLiteral) expression;
			assertEquals(toString(), entityTypeLiteral.toParsedText());
		}

		@Override
		public String toString() {
			return entityType;
		}
	}

	static final class ExistsExpressionTester extends AbstractExpressionTester {
		private boolean hasNot;
		private ExpressionTester subquery;

		ExistsExpressionTester(ExpressionTester subquery, boolean hasNot) {
			super();
			this.hasNot = hasNot;
			this.subquery = subquery;
		}

		public void test(Expression expression) {
			assertInstance(expression, ExistsExpression.class);

			ExistsExpression existsExpression = (ExistsExpression) expression;
			assertEquals(toString(), expression.toParsedText());
			assertTrue  (existsExpression.hasLeftParenthesis());
			assertTrue  (existsExpression.hasRightParenthesis());
			assertTrue  (existsExpression.hasExpression());
			assertEquals(hasNot, existsExpression.hasNot());

			subquery.test(existsExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(hasNot ? NOT_EXISTS : EXISTS);
			sb.append("(");
			sb.append(subquery);
			sb.append(")");
			return sb.toString();
		}
	}

	/**
	 * This tester tests an {@link Expression} information to make sure it correctly parsed a section
	 * of the query. This interface also adds helper method for easily creating a parsed tree
	 * representation of the actual query parsed tree.
	 */
	static interface ExpressionTester {

		AdditionExpressionTester add(ExpressionTester expression);
		AndExpressionTester and(ExpressionTester expression);
		BetweenExpressionTester between(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression);
		ComparisonExpressionTester different(ExpressionTester expression);
		DivisionExpressionTester division(ExpressionTester expression);
		ComparisonExpressionTester equal(ExpressionTester expression);
		ComparisonExpressionTester greaterThan(ExpressionTester expression);
		ComparisonExpressionTester greaterThanOrEqual(ExpressionTester expression);
		InExpressionTester in(ExpressionTester... inItems);

		/**
		 * Determines whether this tester represents the {@link NullExpression}.
		 *
		 * @return <code>true</code> if this tester represents a <code>null</code> object; false
		 * otherwise
		 */
		boolean isNull();

		LikeExpressionTester like(ExpressionTester patternValue);
		LikeExpressionTester like(ExpressionTester patternValue, ExpressionTester escapeCharacter);
		ComparisonExpressionTester lowerThan(ExpressionTester expression);
		ComparisonExpressionTester lowerThanOrEqual(ExpressionTester expression);
		ExpressionTester member(ExpressionTester collectionValuedPathExpression);
		ExpressionTester memberOf(ExpressionTester collectionValuedPathExpression);
		MultiplicationExpressionTester multiplication(ExpressionTester expression);
		BetweenExpressionTester notBetween(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression);
		InExpressionTester notIn(ExpressionTester... inItems);
		LikeExpressionTester notLike(ExpressionTester expression);
		LikeExpressionTester notLike(ExpressionTester expression, ExpressionTester escapeCharacter);
		ExpressionTester notMember(ExpressionTester collectionValuedPathExpression);
		ExpressionTester notMemberOf(ExpressionTester collectionValuedPathExpression);
		OrExpressionTester or(ExpressionTester expression);
		SubstractionExpressionTester substract(ExpressionTester expression);

		/**
		 * Tests the given {@link Expression} internal data.
		 */
		void test(Expression expression);
	}

	static final class FromClauseTester extends AbstractFromClauseTester {
		FromClauseTester(ExpressionTester declarations) {
			super(declarations);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, FromClause.class);
		}
	}

	static final class FuncExpressionTester extends AbstractSingleEncapsulatedExpressionTester {
		private String functionName;
		boolean hasComma;
		boolean hasSpaceAfterComma;

		FuncExpressionTester(String functionName, ExpressionTester funcItems) {
			super(funcItems);
			this.functionName = functionName;
			this.hasSpaceAfterComma = true;
			this.hasComma = true;
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return FuncExpression.class;
		}

		@Override
		String identifier() {
			return FUNC;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			FuncExpression funcExpression = (FuncExpression) expression;
			assertEquals(functionName,       funcExpression.getFunctionName());
			assertEquals(hasComma,           funcExpression.hasComma());
			assertEquals(hasSpaceAfterComma, funcExpression.hasSpaceAFterComma());
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb) {
			if (functionName != null) {
				sb.append(functionName);
			}
			if (hasComma) {
				sb.append(",");
			}
			if (hasSpaceAfterComma) {
				sb.append(" ");
			}
			super.toStringEncapsulatedExpression(sb);
		}
	}

	static final class GroupByClauseTester extends AbstractExpressionTester {
		private ExpressionTester groupByItems;

		GroupByClauseTester(ExpressionTester groupByItems) {
			super();
			this.groupByItems = groupByItems;
		}

		public void test(Expression expression) {
			assertInstance(expression, GroupByClause.class);

			GroupByClause groupByClause = (GroupByClause) expression;
			assertEquals(toString(), groupByClause.toParsedText());
			assertTrue  (groupByClause.hasGroupByItems());
			assertTrue  (groupByClause.hasSpaceAfterGroupBy());

			groupByItems.test(groupByClause.getGroupByItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(GROUP_BY);
			sb.append(" ");
			sb.append(groupByItems);
			return sb.toString();
		}
	}

	static final class HavingClauseTester extends AbstractConditionalClauseTester {
		HavingClauseTester(ExpressionTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		Class<? extends AbstractConditionalClause> expressionType() {
			return HavingClause.class;
		}

		@Override
		String identifier() {
			return HAVING;
		}
	}

	static final class IdentificationVariableDeclarationTester extends AbstractExpressionTester {
		boolean hasSpace;
		private ExpressionTester joins;
		private ExpressionTester rangeVariableDeclaration;

		IdentificationVariableDeclarationTester(ExpressionTester rangeVariableDeclaration,
		                                        ExpressionTester joins) {
			super();

			this.rangeVariableDeclaration = rangeVariableDeclaration;
			this.hasSpace = !joins.isNull();
			this.joins = joins;
		}

		public void test(Expression expression) {
			assertInstance(expression, IdentificationVariableDeclaration.class);

			IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;
			assertEquals(toString(), identificationVariableDeclaration.toParsedText());
			assertTrue  (identificationVariableDeclaration.hasRangeVariableDeclaration());

			assertEquals(hasSpace,        identificationVariableDeclaration.hasSpace());
			assertEquals(!joins.isNull(), identificationVariableDeclaration.hasJoins());

			rangeVariableDeclaration.test(identificationVariableDeclaration.getRangeVariableDeclaration());
			joins.test(identificationVariableDeclaration.getJoins());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(rangeVariableDeclaration);
			if (hasSpace) {
				sb.append(" ");
			}
			sb.append(joins);
			return sb.toString();
		}
	}

	static final class IdentificationVariableTester extends AbstractExpressionTester {
		private String identificationVariable;

		IdentificationVariableTester(String identificationVariable) {
			super();
			this.identificationVariable = identificationVariable;
		}

		public void test(Expression expression) {
			assertInstance(expression, IdentificationVariable.class);

			IdentificationVariable identificationVariable = (IdentificationVariable) expression;
			assertEquals(toString(), identificationVariable.toParsedText());
		}

		@Override
		public String toString() {
			return identificationVariable;
		}
	}

	static final class IndexExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		IndexExpressionTester(ExpressionTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return IndexExpression.class;
		}

		@Override
		String identifier() {
			return INDEX;
		}
	}

	static final class InExpressionTester extends AbstractExpressionTester {
		boolean hasLeftParenthesis;
		private boolean hasNot;
		boolean hasRightParenthesis;
		boolean hasSpaceAfterIn;
		private ExpressionTester inItems;
		private ExpressionTester stateFieldPathExpression;

		InExpressionTester(ExpressionTester stateFieldPathExpression,
		                   boolean hasNot,
		                   ExpressionTester inItems) {
			super();

			this.stateFieldPathExpression = stateFieldPathExpression;
			this.hasLeftParenthesis = true;
			this.hasRightParenthesis = true;
			this.hasNot = hasNot;
			this.inItems = inItems;
			this.hasSpaceAfterIn = !hasLeftParenthesis;
		}

		public void test(Expression expression) {
			assertInstance(expression, InExpression.class);

			InExpression inExpression = (InExpression) expression;
			assertEquals(toString(), inExpression.toParsedText());
			assertEquals(hasLeftParenthesis, inExpression.hasLeftParenthesis());
			assertEquals(hasRightParenthesis, inExpression.hasRightParenthesis());
			assertEquals(hasNot, inExpression.hasNot());
			assertEquals(!inItems.isNull(), inExpression.hasInItems());
			assertEquals(!stateFieldPathExpression.isNull(), inExpression.hasExpression());

			stateFieldPathExpression.test(inExpression.getExpression());
			inItems.test(inExpression.getInItems());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPathExpression);

			if (!stateFieldPathExpression.isNull()) {
				sb.append(" ");
			}

			sb.append(hasNot ? NOT_IN : IN);

			if (hasSpaceAfterIn) {
				sb.append(" ");
			}

			if (hasLeftParenthesis) {
				sb.append("(");
			}

			sb.append(inItems);

			if (hasRightParenthesis) {
				sb.append(")");
			}

			return sb.toString();
		}
	}

	static final class InputParameterTester extends AbstractExpressionTester {
		private String inputParameter;

		InputParameterTester(String inputParameter) {
			super();
			this.inputParameter = inputParameter;
		}

		public void test(Expression expression) {
			assertInstance(expression, InputParameter.class);

			InputParameter inputParameter = (InputParameter) expression;
			assertEquals(toString(), inputParameter.toParsedText());
			assertEquals(this.inputParameter.charAt(0) == '?', inputParameter.isPositional());
			assertEquals(this.inputParameter.charAt(0) == ':', inputParameter.isNamed());
		}

		@Override
		public String toString() {
			return inputParameter;
		}
	}

	static final class JoinFetchTester extends AbstractExpressionTester {

		private ExpressionTester collectionValuedPathExpression;
		private String joinType;

		JoinFetchTester(String joinType, ExpressionTester collectionValuedPathExpression) {
			super();
			this.joinType = joinType;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, JoinFetch.class);

			JoinFetch join = (JoinFetch) expression;
			assertEquals(toString(), join.toParsedText());
			assertEquals(joinType, join.getIdentifier());
			assertTrue  (join.hasJoinAssociationPath());
			assertTrue  (join.hasSpaceAfterFetch());

			collectionValuedPathExpression.test(join.getJoinAssociationPath());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			sb.append(" ");
			sb.append(collectionValuedPathExpression);
			return sb.toString();
		}
	}

	static final class JoinTester extends AbstractExpressionTester {

		private ExpressionTester collectionValuedPathExpression;
		private boolean hasAs;
		private ExpressionTester identificationVariable;
		private String joinType;

		JoinTester(String joinType, ExpressionTester collectionValuedPathExpression, boolean hasAs, ExpressionTester identificationVariable) {
			super();
			this.collectionValuedPathExpression = collectionValuedPathExpression;
			this.identificationVariable = identificationVariable;
			this.joinType = joinType;
			this.hasAs = hasAs;
		}

		public void test(Expression expression) {
			assertInstance(expression, Join.class);

			Join join = (Join) expression;
			assertEquals(toString(), join.toParsedText());
			assertEquals(joinType, join.getIdentifier());

			assertEquals(hasAs, join.hasAs());
			assertTrue  (join.hasIdentificationVariable());
			assertTrue  (join.hasJoinAssociationPath());

			assertEquals(hasAs, join.hasSpaceAfterAs());
			assertTrue  (join.hasSpaceAfterJoin());
			assertTrue  (join.hasSpaceAfterJoinAssociation());

			collectionValuedPathExpression.test(join.getJoinAssociationPath());
			identificationVariable.test(join.getIdentificationVariable());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			sb.append(" ");
			sb.append(collectionValuedPathExpression);
			sb.append(hasAs ? " AS " : " ");
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	static final class JPQLExpressionTester extends AbstractExpressionTester {

		private ExpressionTester queryStatement;
		private ExpressionTester unknownExpression;

		JPQLExpressionTester(ExpressionTester queryStatement, ExpressionTester unknownExpression) {

			super();
			this.queryStatement    = queryStatement;
			this.unknownExpression = unknownExpression;
		}

		public void test(Expression expression) {

			JPQLExpression jpqlExpression = (JPQLExpression) expression;

			assertEquals(toString(),                  jpqlExpression.toParsedText());
			assertEquals(!queryStatement.isNull(),    jpqlExpression.hasQueryStatement());
			assertEquals(!unknownExpression.isNull(), jpqlExpression.hasUnknownEndingStatement());

			queryStatement   .test(jpqlExpression.getQueryStatement());
			unknownExpression.test(jpqlExpression.getUnknownEndingStatement());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(queryStatement);
			sb.append(unknownExpression);
			return sb.toString();
		}
	}

	static final class KeywordExpressionTester extends AbstractExpressionTester {
		private String keyword;

		KeywordExpressionTester(String keyword) {
			super();
			this.keyword = keyword;
		}

		public void test(Expression expression) {
			assertInstance(expression, KeywordExpression.class);

			KeywordExpression keywordExpression = (KeywordExpression) expression;
			assertEquals(toString(), keywordExpression.toParsedText());
		}

		@Override
		public String toString() {
			return keyword;
		}
	}

	static final class LengthExpressionTester extends AbstractSingleEncapsulatedExpressionTester {
		LengthExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return LengthExpression.class;
		}

		@Override
		String identifier() {
			return LENGTH;
		}
	}

	static final class LikeExpressionTester extends AbstractExpressionTester {

		private ExpressionTester escapeCharacter;
		boolean hasEscape;
		private boolean hasNot;
		boolean hasSpaceAfterEscape;
		public boolean hasSpaceAfterLike;
		public boolean hasSpaceAfterPatternValue;
		private ExpressionTester patternValue;
		private ExpressionTester stringExpression;

		LikeExpressionTester(ExpressionTester stringExpression,
		                     boolean hasNot,
		                     ExpressionTester patternValue,
		                     ExpressionTester escapeCharacter) {
			super();

			this.hasNot              = hasNot;
			this.hasSpaceAfterLike   = true;
			this.stringExpression    = stringExpression;
			this.patternValue        = patternValue;
			this.escapeCharacter     = escapeCharacter;
			this.hasEscape           = !escapeCharacter.isNull();
			this.hasSpaceAfterEscape = hasEscape;
			this.hasSpaceAfterPatternValue = !patternValue.isNull() && hasEscape;
		}

		public void test(Expression expression) {
			assertInstance(expression, LikeExpression.class);

			LikeExpression likeExpression = (LikeExpression) expression;
			assertEquals(toString(),                likeExpression.toParsedText());
			assertEquals(hasNot,                    likeExpression.hasNot());
			assertEquals(hasSpaceAfterLike,         likeExpression.hasSpaceAfterLike());
			assertEquals(hasEscape,                 likeExpression.hasEscape());
			assertEquals(!escapeCharacter.isNull(), likeExpression.hasEscapeCharacter());
			assertEquals(hasSpaceAfterEscape,       likeExpression.hasSpaceAfterEscape());
			assertEquals(hasSpaceAfterPatternValue, likeExpression.hasSpaceAfterPatternValue());
			assertEquals(!patternValue.isNull(),    likeExpression.hasPatternValue());

			stringExpression.test(likeExpression.getStringExpression());
			patternValue.test(likeExpression.getPatternValue());
			escapeCharacter.test(likeExpression.getEscapeCharacter());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(stringExpression);
			sb.append(hasNot ? " NOT LIKE" : " LIKE");

			if (hasSpaceAfterLike) {
				sb.append(" ");
			}

			sb.append(patternValue);

			if (hasSpaceAfterPatternValue) {
				sb.append(" ");
			}

			if (hasEscape) {
				sb.append(ESCAPE);
			}

			if (hasSpaceAfterEscape) {
				sb.append(" ");
			}

			sb.append(escapeCharacter);

			return sb.toString();
		}
	}

	static final class LocateExpressionTester extends AbstractTripleEncapsulatedExpressionTester {

		LocateExpressionTester(ExpressionTester firstExpression, ExpressionTester secondExpression, ExpressionTester thirdExpression) {
			super(firstExpression, secondExpression, thirdExpression);
		}

		@Override
		Class<? extends AbstractTripleEncapsulatedExpression> expressionType() {
			return LocateExpression.class;
		}

		@Override
		String identifier() {
			return LOCATE;
		}
	}

	static abstract class LogicalExpressionTester extends CompoundExpressionTester {

		LogicalExpressionTester(ExpressionTester leftExpression, ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, LogicalExpression.class);
		}
	}

	static final class LowerExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		LowerExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return LowerExpression.class;
		}

		@Override
		String identifier() {
			return LOWER;
		}
	}

	static final class MaxFunctionTester extends AggregateFunctionTester {

		MaxFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return MaxFunction.class;
		}

		@Override
		String identifier() {
			return MAX;
		}
	}

	static final class MinFunctionTester extends AggregateFunctionTester {

		MinFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return MinFunction.class;
		}

		@Override
		String identifier() {
			return MIN;
		}
	}

	static final class ModExpressionTester extends AbstractDoubleEncapsulatedExpressionTester {
		ModExpressionTester(ExpressionTester firstExpression,
		                    ExpressionTester secondExpression) {
			super(firstExpression, secondExpression);
		}

		@Override
		Class<? extends AbstractDoubleEncapsulatedExpression> expressionType() {
			return ModExpression.class;
		}

		@Override
		String identifier() {
			return MOD;
		}
	}

	static final class MultiplicationExpressionTester extends CompoundExpressionTester {
		MultiplicationExpressionTester(ExpressionTester leftExpression,
		                               ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType() {
			return MultiplicationExpression.class;
		}

		@Override
		String identifier() {
			return MULTIPLICATION;
		}
	}

	static final class NotExpressionTester extends AbstractExpressionTester {
		private ExpressionTester expression;

		NotExpressionTester(ExpressionTester expression) {
			super();
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, NotExpression.class);

			NotExpression notExpression = (NotExpression) expression;
			assertEquals(toString(), notExpression.toParsedText());
			assertEquals(!this.expression.isNull(), notExpression.hasExpression());

			this.expression.test(notExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(NOT);
			sb.append(" ");
			sb.append(expression);
			return sb.toString();
		}
	}

	static final class NullComparisonExpressionTester extends AbstractExpressionTester {
		private ExpressionTester expression;
		private boolean hasNot;

		NullComparisonExpressionTester(ExpressionTester expression,
		                               boolean hasNot) {
			super();

			this.hasNot = hasNot;
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, NullComparisonExpression.class);

			NullComparisonExpression nullComparisonExpression = (NullComparisonExpression) expression;
			assertEquals(toString(), nullComparisonExpression.toParsedText());
			assertEquals(hasNot, nullComparisonExpression.hasNot());
			assertEquals(!this.expression.isNull(), nullComparisonExpression.hasExpression());

			this.expression.test(nullComparisonExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(expression);

			if (expression.isNull()) {
				sb.append(hasNot ? "IS NOT NULL" : "IS NULL");
			}
			else {
				sb.append(hasNot ? " IS NOT NULL" : " IS NULL");
			}

			return sb.toString();
		}
	}

	static final class NullExpressionTester extends AbstractExpressionTester {

		@Override
		public boolean isNull() {
			return true;
		}

		public void test(Expression expression) {
			if (!((AbstractExpression) expression).isVirtual()) {
				assertInstance(expression, NullExpression.class);
			}
		}

		@Override
		public String toString() {
			return ExpressionTools.EMPTY_STRING;
		}
	}

	static final class NullIfExpressionTester extends AbstractDoubleEncapsulatedExpressionTester {
		NullIfExpressionTester(ExpressionTester firstExpression,
                             ExpressionTester secondExpression) {
			super(firstExpression, secondExpression);
      }

		@Override
		Class<? extends AbstractDoubleEncapsulatedExpression> expressionType() {
			return NullIfExpression.class;
		}

		@Override
		String identifier() {
			return NULLIF;
		}
	}

	static final class NumericLiteralTester extends AbstractExpressionTester {
		private String number;

		NumericLiteralTester(String number) {
			super();
			this.number = number;
		}

		public void test(Expression expression) {
			assertInstance(expression, NumericLiteral.class);

			assertEquals(toString(), expression.toParsedText());
		}

		@Override
		public String toString() {
			return number;
		}
	}

	static final class ObjectTester extends AbstractExpressionTester {
		private ExpressionTester identificationVariable;

		ObjectTester(ExpressionTester identificationVariable) {
			super();
			this.identificationVariable = identificationVariable;
		}

		public void test(Expression expression) {
			assertInstance(expression, ObjectExpression.class);

			ObjectExpression objectExpression = (ObjectExpression) expression;
			assertEquals(toString(), objectExpression.toParsedText());
			assertTrue  (objectExpression.hasExpression());
			assertTrue  (objectExpression.hasLeftParenthesis());
			assertTrue  (objectExpression.hasRightParenthesis());

			identificationVariable.test(objectExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(OBJECT);
			sb.append("(");
			sb.append(identificationVariable);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class OrderByClauseTester extends AbstractExpressionTester {

		boolean hasSpaceAfterOrderBy;
		private ExpressionTester orderByItems;

		OrderByClauseTester(ExpressionTester orderByItems) {
			super();
			this.orderByItems = orderByItems;
			this.hasSpaceAfterOrderBy = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, OrderByClause.class);

			OrderByClause orderByClause = (OrderByClause) expression;
			assertEquals(toString(), orderByClause.toParsedText());
			assertTrue  (orderByClause.hasOrderByItems());
			assertTrue  (orderByClause.hasSpaceAfterOrderBy());

			orderByItems.test(orderByClause.getOrderByItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(ORDER_BY);
			if (hasSpaceAfterOrderBy) {
				sb.append(" ");
			}
			sb.append(orderByItems);
			return sb.toString();
		}
	}

	static final class OrderByItemTester extends AbstractExpressionTester {
		private ExpressionTester orderByItem;
		private Ordering ordering;

		OrderByItemTester(ExpressionTester orderByItem, Ordering ordering) {
			super();

			this.ordering = ordering;
			this.orderByItem = orderByItem;
		}

		public void test(Expression expression) {
			assertInstance(expression, OrderByItem.class);

			OrderByItem orderByItem = (OrderByItem) expression;
			assertEquals(toString(), orderByItem.toParsedText());
			assertEquals(!this.orderByItem.isNull(), orderByItem.hasExpression());
			assertEquals(!this.orderByItem.isNull() && ordering != Ordering.DEFAULT, orderByItem.hasSpaceAfterExpression());
			assertSame  (ordering, orderByItem.getOrdering());

			this.orderByItem.test(orderByItem.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(orderByItem);

			if (!orderByItem.isNull() && (ordering != Ordering.DEFAULT)) {
				sb.append(" ");
			}

			if (ordering != Ordering.DEFAULT) {
				sb.append(ordering.name());
			}

			return sb.toString();
		}
	}

	static final class OrExpressionTester extends LogicalExpressionTester {
		OrExpressionTester(ExpressionTester leftExpression,
		                   ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType() {
			return OrExpression.class;
		}

		@Override
		String identifier() {
			return OR;
		}
	}

	static final class RangeVariableDeclarationTester extends AbstractExpressionTester {
		private ExpressionTester abstractSchemaName;
		private boolean hasAs;
		boolean hasSpaceAfterAbstractSchemaName;
		boolean hasSpaceAfterAs;
		private ExpressionTester identificationVariable;

		RangeVariableDeclarationTester(ExpressionTester abstractSchemaName,
		                               boolean hasAs,
		                               ExpressionTester identificationVariable) {
			super();

			this.hasAs = hasAs;
			this.abstractSchemaName = abstractSchemaName;
			this.identificationVariable = identificationVariable;
			this.hasSpaceAfterAbstractSchemaName = true;
			this.hasSpaceAfterAs = hasAs;
		}

		public void test(Expression expression) {
			assertInstance(expression, RangeVariableDeclaration.class);

			RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;
			assertEquals(toString(), rangeVariableDeclaration.toParsedText());
			assertEquals(hasAs, rangeVariableDeclaration.hasAs());
			assertEquals(hasSpaceAfterAs, rangeVariableDeclaration.hasSpaceAfterAs());
			assertEquals(!identificationVariable.isNull(), rangeVariableDeclaration.hasIdentificationVariable());
			assertEquals(hasSpaceAfterAbstractSchemaName,  rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());

			abstractSchemaName.test(rangeVariableDeclaration.getAbstractSchemaName());
			identificationVariable.test(rangeVariableDeclaration.getIdentificationVariable());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(abstractSchemaName);
			if (hasSpaceAfterAbstractSchemaName) {
				sb.append(" ");
			}
			if (hasAs) {
				sb.append("AS");
			}
			if (hasSpaceAfterAs) {
				sb.append(" ");
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	static final class ResultVariableTester extends AbstractExpressionTester {

		private boolean hasAs;
		boolean hasSpaceAfterAs;
		private ExpressionTester resultVariable;
		private ExpressionTester selectExpression;

		ResultVariableTester(ExpressionTester selectExpression, boolean hasAs, ExpressionTester resultVariable) {
			super();
			this.hasAs            = hasAs;
			this.hasSpaceAfterAs  = hasAs;
			this.selectExpression = selectExpression;
			this.resultVariable   = resultVariable;
		}

		public void test(Expression expression) {

			assertInstance(expression, ResultVariable.class);
			ResultVariable resultVariable = (ResultVariable) expression;

			assertEquals(toString(),                    resultVariable.toParsedText());
			assertEquals(hasAs,                         resultVariable.hasAs());
			assertEquals(hasSpaceAfterAs,               resultVariable.hasSpaceAfterAs());
			assertEquals(!this.resultVariable.isNull(), resultVariable.hasResultVariable());
			assertEquals(!selectExpression.isNull(),    resultVariable.hasSelectExpression());

			this.selectExpression.test(resultVariable.getSelectExpression());
			this.resultVariable  .test(resultVariable.getResultVariable());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(selectExpression);

			if (!selectExpression.isNull()) {
				sb.append(" ");
			}

			if (hasAs) {
				sb.append(AS);
			}

			if (hasSpaceAfterAs) {
				sb.append(" ");
			}

			sb.append(resultVariable);
			return sb.toString();
		}
	}

	static final class SelectClauseTester extends AbstractSelectClauseTester {

		SelectClauseTester(ExpressionTester selectExpressions, boolean hasDistinct) {
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, SelectClause.class);
		}
	}

	static final class SelectStatementTester extends AbstractSelectStatementTester {

		boolean hasSpaceBeforeOrderBy;
		private ExpressionTester orderByClause;

		SelectStatementTester(ExpressionTester selectClause,
		                      ExpressionTester fromClause,
		                      ExpressionTester whereClause,
		                      ExpressionTester groupByClause,
		                      ExpressionTester havingClause,
		                      ExpressionTester orderByClause) {

			super(selectClause, fromClause, whereClause, groupByClause, havingClause);

			this.orderByClause = orderByClause;
			this.hasSpaceBeforeOrderBy = !orderByClause.isNull();

			hasSpaceAfterFrom    |= (!fromClause   .isNull() && whereClause  .isNull() &&  groupByClause.isNull() &&  havingClause .isNull() && !orderByClause.isNull());
			hasSpaceAfterWhere   |= (!whereClause  .isNull() && groupByClause.isNull() &&  havingClause .isNull() && !orderByClause.isNull());
			hasSpaceAfterGroupBy |= (!groupByClause.isNull() && havingClause .isNull() && !orderByClause.isNull());
		}

		@Override
		Class<? extends AbstractSelectStatement> expressionType() {
			return SelectStatement.class;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			SelectStatement selectStatement = (SelectStatement) expression;
			assertEquals(!orderByClause.isNull(), selectStatement.hasOrderByClause());
//			assertEquals(hasSpaceBeforeOrderBy, selectStatement.hasSpaceBeforeOrderBy());

			orderByClause.test(selectStatement.getOrderByClause());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());

			if (hasSpaceBeforeOrderBy && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(" ");
			}

			sb.append(orderByClause);
			return sb.toString();
		}
	}

	static final class SimpleFromClauseTester extends AbstractFromClauseTester {
		SimpleFromClauseTester(ExpressionTester declaration) {
			super(declaration);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, SimpleFromClause.class);
		}
	}

	static final class SimpleSelectClauseTester extends AbstractSelectClauseTester {

		SimpleSelectClauseTester(ExpressionTester selectExpressions, boolean hasDistinct) {
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, SimpleSelectClause.class);
		}
	}

	static final class SimpleSelectStatementTester extends AbstractSelectStatementTester {

		SimpleSelectStatementTester(ExpressionTester selectClause,
		                            ExpressionTester fromClause,
		                            ExpressionTester whereClause,
		                            ExpressionTester groupByClause,
		                            ExpressionTester havingClause) {

			super(selectClause, fromClause, whereClause, groupByClause, havingClause);
		}

		@Override
		Class<? extends AbstractSelectStatement> expressionType() {
			return SimpleSelectStatement.class;
		}
	}

	static final class SizeExpressionTester extends AbstractExpressionTester {

		private ExpressionTester collectionValuedPathExpression;

		SizeExpressionTester(ExpressionTester collectionValuedPathExpression) {
			super();
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, SizeExpression.class);

			SizeExpression sizeExpression = (SizeExpression) expression;
			assertTrue(sizeExpression.hasExpression());
			assertTrue(sizeExpression.hasLeftParenthesis());
			assertTrue(sizeExpression.hasRightParenthesis());

			collectionValuedPathExpression.test(sizeExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(SIZE);
			sb.append("(");
			sb.append(collectionValuedPathExpression);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class SqrtExpressionTester extends AbstractSingleEncapsulatedExpressionTester {
		SqrtExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return SqrtExpression.class;
		}

		@Override
		String identifier() {
			return SQRT;
		}
	}

	static final class StateFieldPathExpressionTester extends AbstractPathExpressionTester {
		StateFieldPathExpressionTester(String value) {
			super(value);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, StateFieldPathExpression.class);
		}
	}

	static final class StringLiteralTester extends AbstractExpressionTester {
		private String literal;

		StringLiteralTester(String literal) {
			super();
			this.literal = literal;
		}

		public void test(Expression expression) {
			assertInstance(expression, StringLiteral.class);

			StringLiteral stringLiteral = (StringLiteral) expression;
			assertEquals(toString(), stringLiteral.toString());
			assertTrue(stringLiteral.hasCloseQuote());
		}

		@Override
		public String toString() {
			return literal;
		}
	}

	static final class SubExpressionTester extends AbstractExpressionTester {
		private ExpressionTester expression;

		SubExpressionTester(ExpressionTester expression) {
			super();
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, SubExpression.class);

			SubExpression subExpression = (SubExpression) expression;
			assertEquals(toString(), subExpression.toParsedText());
			assertTrue(subExpression.hasExpression());
			assertTrue(subExpression.hasRightParenthesis());

			this.expression.test(subExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(expression);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class SubstractionExpressionTester extends CompoundExpressionTester {
		SubstractionExpressionTester(ExpressionTester leftExpression,
		                             ExpressionTester rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType() {
			return SubtractionExpression.class;
		}

		@Override
		String identifier() {
			return MINUS;
		}
	}

	static final class SubstringExpressionTester extends AbstractTripleEncapsulatedExpressionTester {

		SubstringExpressionTester(ExpressionTester firstExpression,
		                          ExpressionTester firstArithmeticExpression,
		                          ExpressionTester secondArithmeticExpression) {

			super(firstExpression, firstArithmeticExpression, secondArithmeticExpression);
		}

		@Override
		Class<? extends AbstractTripleEncapsulatedExpression> expressionType() {
			return SubstringExpression.class;
		}

		@Override
		String identifier() {
			return SubstringExpression.SUBSTRING;
		}
	}

	static final class SumFunctionTester extends AggregateFunctionTester {
		SumFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return SumFunction.class;
		}

		@Override
		String identifier() {
			return SUM;
		}
	}

	static final class TrimExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private boolean hasFrom;
		boolean hasSpaceAfterFrom;
		private Specification specification;
		private ExpressionTester trimCharacter;

		TrimExpressionTester(Specification specification, ExpressionTester stringPrimary, ExpressionTester trimCharacter, boolean hasFrom) {
			super(stringPrimary);
			this.specification = specification;
			this.trimCharacter = trimCharacter;
			this.hasFrom = hasFrom;
			this.hasSpaceAfterFrom = hasFrom;
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return TrimExpression.class;
		}

		@Override
		String identifier() {
			return TRIM;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			TrimExpression trimExpression = (TrimExpression) expression;
			assertEquals(hasFrom, trimExpression.hasFrom());
			assertEquals(hasFrom, trimExpression.hasSpaceAfterFrom());
			assertEquals(!trimCharacter.isNull(), trimExpression.hasSpaceAfterTrimCharacter());
			assertEquals(!trimCharacter.isNull(), trimExpression.hasTrimCharacter());
			assertEquals(specification != Specification.DEFAULT, trimExpression.hasSpecification());
			assertEquals(specification != Specification.DEFAULT, trimExpression.hasSpaceAfterSpecification());

			trimCharacter.test(trimExpression.getTrimCharacter());
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb) {

			if (specification != Specification.DEFAULT) {
				sb.append(specification);
				sb.append(" ");
			}

			if (!trimCharacter.isNull()) {
				sb.append(" ");
				sb.append(trimCharacter);
			}

			if (hasFrom) {
				sb.append(FROM);
			}

			if (hasSpaceAfterFrom) {
				sb.append(" ");
			}

			super.toStringEncapsulatedExpression(sb);
		}
	}

	static final class TypeExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		TypeExpressionTester(ExpressionTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return TypeExpression.class;
		}

		@Override
		String identifier() {
			return TYPE;
		}
	}

	static final class UnknownExpressionTester extends AbstractExpressionTester {

		private final String unknownText;

		UnknownExpressionTester(String unknownText) {
			super();
			this.unknownText = unknownText;
		}

		public void test(Expression expression) {
			assertInstance(expression, UnknownExpression.class);

			UnknownExpression unknownExpression = (UnknownExpression) expression;
			assertEquals(toString(), unknownExpression.toParsedText());
		}

		@Override
		public String toString() {
			return unknownText;
		}
	}

	static final class UpdateClauseTester extends AbstractExpressionTester {

		boolean hasSet;
		boolean hasSpaceAfterRangeVariableDeclaration;
		boolean hasSpaceAfterSet;
		boolean hasSpaceAfterUpdate;
		private ExpressionTester rangeVariableDeclaration;
		private ExpressionTester updateItems;

		UpdateClauseTester(ExpressionTester rangeVariableDeclaration, ExpressionTester updateItems) {
			super();
			this.hasSet                   = true;
			this.hasSpaceAfterSet         = true;
			this.hasSpaceAfterUpdate      = true;
			this.updateItems              = updateItems;
			this.rangeVariableDeclaration = rangeVariableDeclaration;
			this.hasSpaceAfterRangeVariableDeclaration = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, UpdateClause.class);

			UpdateClause updateClause = (UpdateClause) expression;
			assertEquals(toString(), updateClause.toParsedText());
			assertEquals(!rangeVariableDeclaration.isNull(), updateClause.hasRangeVariableDeclaration());
			assertEquals(hasSet,                updateClause.hasSet());
			assertEquals(hasSpaceAfterSet,      updateClause.hasSpaceAfterSet());
			assertEquals(hasSpaceAfterUpdate,   updateClause.hasSpaceAfterUpdate());
			assertEquals(!updateItems.isNull(), updateClause.hasUpdateItems());
			assertEquals(hasSpaceAfterRangeVariableDeclaration, updateClause.hasSpaceAfterRangeVariableDeclaration());

			rangeVariableDeclaration.test(updateClause.getRangeVariableDeclaration());
			updateItems.test(updateClause.getUpdateItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(UPDATE);
			if (hasSpaceAfterUpdate) {
				sb.append(" ");
			}
			sb.append(rangeVariableDeclaration);
			if (hasSpaceAfterRangeVariableDeclaration) {
				sb.append(" ");
			}
			sb.append("SET");
			if (hasSpaceAfterSet) {
				sb.append(" ");
			}
			sb.append(updateItems);
			return sb.toString();
		}
	}

	static final class UpdateItemTester extends AbstractExpressionTester {

		private ExpressionTester newValue;
		private ExpressionTester stateFieldPathExpression;

		UpdateItemTester(ExpressionTester stateFieldPathExpression, ExpressionTester newValue) {
			super();
			this.stateFieldPathExpression = stateFieldPathExpression;
			this.newValue = newValue;
		}

		public void test(Expression expression) {
			assertInstance(expression, UpdateItem.class);

			UpdateItem updateItem = (UpdateItem) expression;
			assertEquals(toString(), updateItem.toParsedText());
			assertTrue(updateItem.hasEqualSign());
			assertTrue(updateItem.hasNewValue());
			assertTrue(updateItem.hasSpaceAfterEqualSign());
			assertTrue(updateItem.hasSpaceAfterStateFieldPathExpression());
			assertTrue(updateItem.hasStateFieldPathExpression());

			stateFieldPathExpression.test(updateItem.getStateFieldPathExpression());
			newValue.test(updateItem.getNewValue());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPathExpression);
			sb.append(" = ");
			sb.append(newValue);
			return sb.toString();
		}
	}

	static final class UpdateStatementTester extends AbstractExpressionTester {

		boolean hasSpaceAfterUpdateClause;
		private ExpressionTester updateClause;
		private ExpressionTester whereClause;

		UpdateStatementTester(ExpressionTester updateClause, ExpressionTester whereClause) {
			super();
			this.updateClause = updateClause;
			this.whereClause  = whereClause;
			this.hasSpaceAfterUpdateClause = !whereClause.isNull();
		}

		public void test(Expression expression) {
			assertInstance(expression, UpdateStatement.class);

			UpdateStatement updateStatement = (UpdateStatement) expression;
			assertEquals(toString(), updateStatement.toParsedText());
			assertEquals(hasSpaceAfterUpdateClause, updateStatement.hasSpaceAfterUpdateClause());
			assertEquals(!whereClause.isNull(), updateStatement.hasWhereClause());

			updateClause.test(updateStatement.getUpdateClause());
			whereClause .test(updateStatement.getWhereClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(updateClause);

			if (hasSpaceAfterUpdateClause) {
				sb.append(" ");
			}

			sb.append(whereClause);
			return sb.toString();
		}
	}

	static final class UpperExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		UpperExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return UpperExpression.class;
		}

		@Override
		String identifier() {
			return UPPER;
		}
	}

	static final class WhenClauseTester extends AbstractExpressionTester {

		private ExpressionTester conditionalExpression;
		private ExpressionTester thenExpression;

		WhenClauseTester(ExpressionTester conditionalExpression,
		                 ExpressionTester thenExpression) {
			super();

			this.conditionalExpression = conditionalExpression;
			this.thenExpression = thenExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, WhenClause.class);

			WhenClause whenClause = (WhenClause) expression;
			assertEquals(toString(), whenClause.toParsedText());
			assertTrue  (whenClause.hasWhenExpression());
			assertTrue  (whenClause.hasSpaceAfterWhenExpression());
			assertTrue  (whenClause.hasSpaceAfterThen());
			assertTrue  (whenClause.hasSpaceAfterWhen());
			assertTrue  (whenClause.hasThen());
			assertTrue  (whenClause.hasThenExpression());

			conditionalExpression.test(whenClause.getWhenExpression());
			thenExpression.test(whenClause.getThenExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(WHEN);
			sb.append(" ");
			sb.append(conditionalExpression);
			sb.append(" ");
			sb.append(THEN);
			sb.append(" ");
			sb.append(thenExpression);
			return sb.toString();
		}
	}

	static final class WhereClauseTester extends AbstractConditionalClauseTester {

		WhereClauseTester(ExpressionTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		Class<? extends AbstractConditionalClause> expressionType() {
			return WhereClause.class;
		}

		@Override
		String identifier() {
			return WHERE;
		}
	}
}