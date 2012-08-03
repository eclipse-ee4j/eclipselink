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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import java.util.Arrays;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTripleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;
import org.eclipse.persistence.jpa.jpql.parser.Join;
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
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.NullOrdering;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.eclipse.persistence.jpa.tests.jpql.JPQLBasicTest;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBuilder.*;
import static org.junit.Assert.*;

/**
 * This abstract class provides the functionality to create an equivalent tree representation
 * ({@link ExpressionTester}) of the parsed tree representation ({@link Expression}) of a JPQL query.
 * <p>
 * Note: This class provides the {@link ExpressionTester} for all JPQL grammars (1.0, 2.0 and 2.1),
 * as well as for EclipseLink (all versions).
 *
 * @version 2.4.1
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused" /* For the extra import statement, see bug 330740 */ })
public abstract class JPQLParserTest extends JPQLBasicTest {

	@JPQLGrammarTestHelper
	private JPQLGrammar jpqlGrammar;

	protected static AbsExpressionTester abs(ExpressionTester expression) {
		return new AbsExpressionTester(expression);
	}

	protected static AbstractSchemaNameTester abstractSchemaName(String abstractSchemaName) {
		return new AbstractSchemaNameTester(abstractSchemaName);
	}

	protected static AdditionExpressionTester add(ExpressionTester leftExpression,
	                                              ExpressionTester rightExpression) {

		return new AdditionExpressionTester(leftExpression, rightExpression);
	}

	protected static AllOrAnyExpressionTester all(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(ALL, subquery);
	}

	protected static AndExpressionTester and(ExpressionTester leftExpression,
	                                         ExpressionTester rightExpression) {

		return new AndExpressionTester(leftExpression, rightExpression);
	}

	protected static AllOrAnyExpressionTester any(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(ANY, subquery);
	}

	protected static AllOrAnyExpressionTester anyExpression(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(ANY, subquery);
	}

	protected static AvgFunctionTester avg(ExpressionTester expression) {
		return new AvgFunctionTester(expression, false);
	}

	protected static AvgFunctionTester avg(String statefieldPathExpression) {
		return avg(path(statefieldPathExpression));
	}

	protected static AvgFunctionTester avgDistinct(String statefieldPathExpression) {
		return new AvgFunctionTester(path(statefieldPathExpression), true);
	}

	protected static BadExpressionTester bad(ExpressionTester expression) {
		return new BadExpressionTester(expression);
	}

	protected static BetweenExpressionTester between(ExpressionTester expression,
	                                                 ExpressionTester lowerBoundExpression,
	                                                 ExpressionTester upperBoundExpression) {

		return new BetweenExpressionTester(expression, false, lowerBoundExpression, upperBoundExpression);
	}

	protected static CaseExpressionTester case_(ExpressionTester... whenClauses) {

		ExpressionTester[] copy = new ExpressionTester[whenClauses.length - 1];
		System.arraycopy(whenClauses, 0, copy, 0, whenClauses.length - 1);

		return new CaseExpressionTester(
			nullExpression(),
			spacedCollection(copy),
			whenClauses[whenClauses.length - 1]
		);
	}

	protected static CaseExpressionTester case_(ExpressionTester caseOperand,
	                                            ExpressionTester[] whenClauses,
	                                            ExpressionTester elseExpression) {

		return new CaseExpressionTester(
			caseOperand,
			(whenClauses.length == 1) ? whenClauses[0] : spacedCollection(whenClauses),
			elseExpression
		);
	}

	protected static CaseExpressionTester case_(ExpressionTester[] whenClauses,
	                                            ExpressionTester elseExpression) {

		return case_(nullExpression(), whenClauses, elseExpression);
	}

	private static CastExpressionTester cast(ExpressionTester expression,
	                                         boolean hasAs,
	                                         ExpressionTester databaseType) {

		return new CastExpressionTester(expression, hasAs, databaseType);
	}

	protected static CastExpressionTester cast(ExpressionTester expression,
	                                           ExpressionTester databaseType) {

		return cast(expression, false, databaseType);
	}

	protected static CastExpressionTester cast(ExpressionTester expression, String databaseType) {
		return cast(expression, databaseType, -1, -1);
	}

	protected static CastExpressionTester cast(ExpressionTester expression,
	                                           String databaseType,
	                                           int size) {

		return cast(expression, databaseType, size, -1);
	}

	protected static CastExpressionTester cast(ExpressionTester expression,
	                                           String databaseType,
	                                           int size,
	                                           int precision) {

		return new CastExpressionTester(
			expression,
			false,
			databaseType(databaseType, size, precision)
		);
	}

	protected static CastExpressionTester cast(String pathExpression, ExpressionTester databaseType) {
		return cast(path(pathExpression), databaseType);
	}

	protected static CastExpressionTester cast(String pathExpression, String databaseType) {
		return cast(path(pathExpression), databaseType);
	}

	protected static CastExpressionTester cast(String pathExpression,
	                                           String databaseType,
	                                           int size) {

		return cast(path(pathExpression), databaseType, size, -1);
	}

	protected static CastExpressionTester cast(String pathExpression,
	                                           String databaseType,
	                                           int size,
	                                           int precision) {

		return cast(path(pathExpression), databaseType, size, precision);
	}

	protected static CastExpressionTester castAs(ExpressionTester expression,
	                                             ExpressionTester databaseType) {

		return cast(expression, true, databaseType);
	}

	protected static CastExpressionTester castAs(ExpressionTester expression, String databaseType) {
		return castAs(expression, databaseType, -1, -1);
	}

	protected static CastExpressionTester castAs(ExpressionTester pathExpression,
	                                             String databaseType,
	                                             int size) {

		return castAs(pathExpression, databaseType, size, -1);
	}

	protected static CastExpressionTester castAs(ExpressionTester expression,
	                                             String databaseType,
	                                             int size,
	                                             int precision) {

		return new CastExpressionTester(
			expression,
			true,
			databaseType(databaseType, size, precision)
		);
	}

	protected static CastExpressionTester castAs(String pathExpression, ExpressionTester databaseType) {
		return castAs(path(pathExpression), databaseType);
	}

	protected static CastExpressionTester castAs(String pathExpression,
	                                             String databaseType) {

		return castAs(path(pathExpression), databaseType, -1);
	}

	protected static CastExpressionTester castAs(String pathExpression,
	                                             String databaseType,
	                                             int size) {

		return castAs(path(pathExpression), databaseType, size);
	}

	protected static CastExpressionTester castAs(String pathExpression,
	                                             String databaseType,
	                                             int size,
	                                             int precision) {

		return castAs(path(pathExpression), databaseType, size, precision);
	}

	protected static CoalesceExpressionTester coalesce(ExpressionTester expression) {
		return new CoalesceExpressionTester(expression);
	}

	protected static CoalesceExpressionTester coalesce(ExpressionTester... expressions) {
		return new CoalesceExpressionTester(collection(expressions));
	}

	protected static CollectionExpressionTester collection(ExpressionTester... expressions) {

		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length - 1, Boolean.TRUE);

		spaces[expressions.length - 1] = Boolean.FALSE;
		commas[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, commas, spaces);
	}

	protected static CollectionExpressionTester collection(ExpressionTester[] expressions,
	                                                       Boolean[] commas,
	                                                       Boolean[] spaces) {

		return new CollectionExpressionTester(expressions, commas, spaces);
	}

	protected static CollectionValuedPathExpressionTester collectionPath(ExpressionTester identificationVariable,
	                                                                     boolean startsWithDot,
	                                                                     String collectionPath) {

		return new CollectionValuedPathExpressionTester(
			identificationVariable,
			startsWithDot ? "." + collectionPath : collectionPath
		);
	}

	protected static CollectionValuedPathExpressionTester collectionPath(ExpressionTester identificationVariable,
	                                                                     String collectionPath) {

		return collectionPath(identificationVariable, true, collectionPath);
	}

	protected static CollectionValuedPathExpressionTester collectionPath(String collectionPath) {

		int dotIndex = collectionPath.indexOf('.');

		if (dotIndex == 0) {
			return collectionPath(nullExpression(), false, collectionPath);
		}

		String variable = collectionPath.substring(0, dotIndex);
		String path = collectionPath.substring(dotIndex + 1);
		return collectionPath(variable(variable), false, path);
	}

	private static ComparisonExpressionTester comparison(ExpressionTester leftExpression,
	                                                     String comparator,
	                                                     ExpressionTester rightExpression) {

		return new ComparisonExpressionTester(comparator, leftExpression, rightExpression);
	}

	protected static ConcatExpressionTester concat(ExpressionTester... expressions) {
		if (expressions.length > 1) {
			return new ConcatExpressionTester(collection(expressions));
		}
		return new ConcatExpressionTester(expressions[0]);
	}

	protected static CountFunctionTester count(ExpressionTester statefieldPathExpression) {
		return new CountFunctionTester(statefieldPathExpression, false);
	}

	protected static CountFunctionTester count(String statefieldPathExpression) {
		return count(path(statefieldPathExpression));
	}

	protected static CountFunctionTester countDistinct(ExpressionTester statefieldPathExpression) {
		return new CountFunctionTester(statefieldPathExpression, true);
	}

	protected static DateTimeTester CURRENT_DATE() {
		return new DateTimeTester(CURRENT_DATE);
	}

	protected static DateTimeTester CURRENT_TIME() {
		return new DateTimeTester(CURRENT_TIME);
	}

	protected static DateTimeTester CURRENT_TIMESTAMP() {
		return new DateTimeTester(CURRENT_TIMESTAMP);
	}

	protected static DatabaseTypeTester databaseType(String databaseType) {
		return databaseType(databaseType, -1);
	}

	protected static DatabaseTypeTester databaseType(String databaseType, int length) {
		return databaseType(databaseType, length, -1);
	}

	protected static DatabaseTypeTester databaseType(String databaseType, int size, int precision) {

		DatabaseTypeTester expression = new DatabaseTypeTester(
			databaseType,
			(size == -1)       ? nullExpression() : numeric(size),
			(precision  == -1) ? nullExpression() : numeric(precision)
		);

		expression.hasComma            = (precision != -1);
		expression.hasSpaceAfterComma  = (precision != -1);
		expression.hasLeftParenthesis  = (size != -1 || precision != -1);
		expression.hasRightParenthesis = (size != -1 || precision != -1);

		return expression;
	}

	protected static DateTimeTester dateTime(String jdbcEscapeFormat) {
		return new DateTimeTester(jdbcEscapeFormat);
	}

	protected static DeleteClauseTester delete(ExpressionTester rangeVariableDeclaration) {
		return new DeleteClauseTester(rangeVariableDeclaration);
	}

	protected static DeleteClauseTester delete(String abstractSchemaName,
	                                           String identificationVariable) {

		return delete(rangeVariableDeclaration(abstractSchemaName, identificationVariable));
	}

	protected static DeleteClauseTester deleteAs(ExpressionTester abstractSchemaName,
	                                             ExpressionTester identificationVariable) {

		return delete(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	protected static DeleteClauseTester deleteAs(String abstractSchemaName,
	                                             String identificationVariable) {

		return delete(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	protected static DeleteStatementTester deleteStatement(ExpressionTester deleteClause) {
		return deleteStatement(deleteClause, nullExpression());
	}

	protected static DeleteStatementTester deleteStatement(ExpressionTester deleteClause,
	                                                       ExpressionTester whereClause) {

		return new DeleteStatementTester(deleteClause, whereClause);
	}

	protected static DeleteStatementTester deleteStatement(String abstractSchemaName,
	                                                       String identificationVariable) {

		return deleteStatement(delete(abstractSchemaName, identificationVariable));
	}

	protected static DeleteStatementTester deleteStatement(String abstractSchemaName,
	                                                       String identificationVariable,
	                                                       ExpressionTester whereClause) {

		return deleteStatement(delete(abstractSchemaName, identificationVariable), whereClause);
	}

	protected static ComparisonExpressionTester different(ExpressionTester leftExpression,
	                                                      ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.DIFFERENT, rightExpression);
	}

	protected static DivisionExpressionTester division(ExpressionTester leftExpression,
	                                                   ExpressionTester rightExpression) {

		return new DivisionExpressionTester(leftExpression, rightExpression);
	}

	protected static EntityTypeLiteralTester entity(String entity) {
		return new EntityTypeLiteralTester(entity);
	}

	protected static EntryExpressionTester entry(ExpressionTester identificationVariable) {
		return new EntryExpressionTester(identificationVariable);
	}

	protected static EntryExpressionTester entry(String identificationVariable) {
		return entry(variable(identificationVariable));
	}

	protected static ComparisonExpressionTester equal(ExpressionTester leftExpression,
	                                                  ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.EQUAL, rightExpression);
	}

	protected static UnionClauseTester except(ExpressionTester subquery) {
		return union(EXCEPT, false, subquery);
	}

	protected static UnionClauseTester except(ExpressionTester selectClause,
	                                          ExpressionTester fromClause) {

		return except(subquery(selectClause, fromClause));
	}

	protected static UnionClauseTester except(ExpressionTester selectClause,
	                                          ExpressionTester fromClause,
		                                       ExpressionTester whereClause) {

		return except(subquery(selectClause, fromClause, whereClause));
	}

	protected static UnionClauseTester except(ExpressionTester selectClause,
	                                          ExpressionTester fromClause,
		                                       ExpressionTester whereClause,
		                                       ExpressionTester groupByClause,
		                                       ExpressionTester havingClause) {

		return except(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
	}

	protected static UnionClauseTester exceptAll(ExpressionTester subquery) {
		return union(EXCEPT, true, subquery);
	}

	protected static UnionClauseTester exceptAll(ExpressionTester selectClause,
	                                             ExpressionTester fromClause) {

		return exceptAll(subquery(selectClause, fromClause));
	}

	protected static UnionClauseTester exceptAll(ExpressionTester selectClause,
	                                             ExpressionTester fromClause,
	 	                                          ExpressionTester whereClause) {

		return exceptAll(subquery(selectClause, fromClause, whereClause));
	}

	protected static UnionClauseTester exceptAll(ExpressionTester selectClause,
	                                             ExpressionTester fromClause,
	 	                                          ExpressionTester whereClause,
	 	                                          ExpressionTester groupByClause,
		                                          ExpressionTester havingClause) {

		return exceptAll(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
	}

	protected static ExistsExpressionTester exists(ExpressionTester subquery) {
		return new ExistsExpressionTester(subquery, false);
	}

	protected static ExtractExpressionTester extract(String part, ExpressionTester expression) {
		return new ExtractExpressionTester(part, false, expression);
	}

	protected static ExtractExpressionTester extractFrom(String part, ExpressionTester expression) {
		return new ExtractExpressionTester(part, true, expression);
	}

	protected static KeywordExpressionTester FALSE() {
		return new KeywordExpressionTester(FALSE);
	}

	protected static FromClauseTester from(ExpressionTester declaration) {
		return new FromClauseTester(declaration);
	}

	protected static FromClauseTester from(ExpressionTester... declarations) {
		return new FromClauseTester(collection(declarations));
	}

	/**
	 * Example: from("Employee", "e", "Product", "p")
	 */
	protected static FromClauseTester from(String... declarations) {

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
	protected static FromClauseTester from(String abstractSchemaName, String identificationVariable) {
		return from(fromEntity(abstractSchemaName, identificationVariable));
	}

	protected static FromClauseTester from(String abstractSchemaName,
	                                       String identificationVariable,
	                                       ExpressionTester... joins) {

		return from(identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins));
	}

	protected static FromClauseTester from(String abstractSchemaName,
	                                       String identificationVariable,
	                                       ExpressionTester joins) {

		return from(fromEntity(abstractSchemaName, identificationVariable, joins));
	}

	protected static FromClauseTester fromAs(String abstractSchemaName, String identificationVariable) {
		return from(identificationVariableDeclarationAs(abstractSchemaName, identificationVariable));
	}

	/**
	 * Example: from("e.employees", "e")
	 */
	protected static IdentificationVariableDeclarationTester fromCollection(String collectionPath,
	                                                                        String identificationVariable) {

		return identificationVariableDeclaration(
			rangeVariableDeclaration(
				collectionPath(collectionPath),
				variable(identificationVariable)
			),
			nullExpression()
		);
	}

	protected static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName,
	                                                                    String identificationVariable) {

		return identificationVariableDeclaration(abstractSchemaName, identificationVariable);
	}

	protected static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName,
	                                                                    String identificationVariable,
	                                                                    ExpressionTester... joins) {

		return identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins);
	}

	protected static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName,
	                                                                    String identificationVariable,
	                                                                    ExpressionTester join) {

		return identificationVariableDeclaration(abstractSchemaName, identificationVariable, join);
	}

	protected static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName,
	                                                                      String identificationVariable) {

		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable);
	}

	protected static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName,
	                                                                      String identificationVariable,
	                                                                      ExpressionTester... joins) {

		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, joins);
	}

	protected static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName,
	                                                                      String identificationVariable,
	                                                                      ExpressionTester join) {

		return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, join);
	}

	protected static CollectionMemberDeclarationTester fromIn(ExpressionTester collectionPath,
	                                                          ExpressionTester identificationVariable) {

		return new CollectionMemberDeclarationTester(
			collectionPath,
			false,
			identificationVariable
		);
	}

	protected static CollectionMemberDeclarationTester fromIn(String collectionPath,
	                                                          String identificationVariable) {

		return fromIn(collectionPath(collectionPath), variable(identificationVariable));
	}

	protected static CollectionMemberDeclarationTester fromInAs(ExpressionTester collectionPath,
	                                                            ExpressionTester identificationVariable) {

		return new CollectionMemberDeclarationTester(
			collectionPath,
			true,
			identificationVariable
		);
	}

	protected static CollectionMemberDeclarationTester fromInAs(String collectionPath,
	                                                            String identificationVariable) {

		return fromInAs(
			collectionPath(collectionPath),
			variable(identificationVariable)
		);
	}

	protected static FunctionExpressionTester function(String identifier,
	                                                   String functionName) {

		return new FunctionExpressionTester(identifier, functionName, nullExpression());
	}

	protected static FunctionExpressionTester function(String identifier,
	                                                   String functionName,
	                                                   ExpressionTester... funcItems) {

		return new FunctionExpressionTester(identifier, functionName, collection(funcItems));
	}

	protected static FunctionExpressionTester function(String identifier,
	                                                   String functionName,
	                                                   ExpressionTester funcItem) {

		return new FunctionExpressionTester(identifier, functionName, funcItem);
	}

	protected static ComparisonExpressionTester greaterThan(ExpressionTester leftExpression,
	                                                        ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.GREATER_THAN, rightExpression);
	}

	protected static ComparisonExpressionTester greaterThanOrEqual(ExpressionTester leftExpression,
	                                                               ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.GREATER_THAN_OR_EQUAL, rightExpression);
	}

	protected static GroupByClauseTester groupBy(ExpressionTester groupByItem) {
		return new GroupByClauseTester(groupByItem);
	}

	protected static GroupByClauseTester groupBy(ExpressionTester... groupByItems) {
		return new GroupByClauseTester(collection(groupByItems));
	}

	protected static HavingClauseTester having(ExpressionTester havingItem) {
		return new HavingClauseTester(havingItem);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration) {
		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, nullExpression());
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration,
	                                                                                           ExpressionTester... joins) {

		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, collection(joins));
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration,
	                                                                                           ExpressionTester joins) {

		return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, joins);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName,
	                                                                                           String identificationVariable) {

		return identificationVariableDeclaration(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName,
	                                                                                           String identificationVariable,
	                                                                                           ExpressionTester... joins) {

		return new IdentificationVariableDeclarationTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			spacedCollection(joins)
		);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName,
	                                                                                           String identificationVariable,
	                                                                                           ExpressionTester join) {

		return new IdentificationVariableDeclarationTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			join
		);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                                                             String identificationVariable) {

		return new IdentificationVariableDeclarationTester(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                                                             String identificationVariable,
	                                                                                             ExpressionTester join) {

		return new IdentificationVariableDeclarationTester(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			join
		);
	}

	protected static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                                                             String identificationVariable,
	                                                                                             ExpressionTester... joins) {

		return new IdentificationVariableDeclarationTester(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			spacedCollection(joins)
		);
	}

	protected static InExpressionTester in(ExpressionTester stateFieldPathExpression,
	                                       ExpressionTester... inItems) {

		return new InExpressionTester(stateFieldPathExpression, false, collection(inItems));
	}

	protected static InExpressionTester in(ExpressionTester stateFieldPathExpression,
	                                       ExpressionTester inItems) {

		return new InExpressionTester(stateFieldPathExpression, false, inItems);
	}

	protected static InExpressionTester in(ExpressionTester stateFieldPathExpression,
	                                       String inputParameter) {

		InExpressionTester in = in(stateFieldPathExpression, inputParameter(inputParameter));
		in.hasLeftParenthesis  = false;
		in.hasRightParenthesis = false;
		in.hasSpaceAfterIn     = true;
		return in;
	}

	protected static InExpressionTester in(String stateFieldPathExpression,
	                                       ExpressionTester... inItems) {

		return in(path(stateFieldPathExpression), inItems);
	}

	protected static InExpressionTester in(String stateFieldPathExpression,
	                                       ExpressionTester inItem) {

		return in(path(stateFieldPathExpression), inItem);
	}

	protected static InExpressionTester in(String pathExpression, String inputParameter) {
		return in(path(pathExpression), inputParameter);
	}

	protected static IndexExpressionTester index(ExpressionTester identificationVariable) {
		return new IndexExpressionTester(identificationVariable);
	}

	protected static IndexExpressionTester index(String identificationVariable) {
		return index(variable(identificationVariable));
	}

	protected static JoinTester innerJoin(ExpressionTester collectionPath,
	                                      ExpressionTester identificationVariable) {

		return innerJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoin(ExpressionTester collectionPath,
	                                      ExpressionTester identificationVariable,
	                                      ExpressionTester joinCondition) {

		return join(
			INNER_JOIN,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester innerJoin(ExpressionTester collectionPath,
	                                      String identificationVariable) {

		return innerJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoin(ExpressionTester collectionPath,
	                                      String identificationVariable,
	                                      ExpressionTester joinCondition) {

		return innerJoin(
			collectionPath,
			variable(identificationVariable),
			nullExpression()
		);
	}

	protected static JoinTester innerJoin(String collectionPath,
	                                      String identificationVariable) {

		return innerJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoin(String collectionPath,
	                                      String identificationVariable,
	                                      ExpressionTester joinCondition) {

		return innerJoin(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester innerJoinAs(ExpressionTester collectionPath,
	                                        ExpressionTester identificationVariable) {

		return innerJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinAs(ExpressionTester collectionPath,
	                                        ExpressionTester identificationVariable,
	                                        ExpressionTester joinCondition) {

		return join(
			INNER_JOIN,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester innerJoinAs(ExpressionTester collectionPath,
	                                        String identificationVariable) {

		return innerJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinAs(ExpressionTester collectionPath,
	                                        String identificationVariable,
	                                        ExpressionTester joinCondition) {

		return innerJoinAs(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester innerJoinAs(String collectionPath,
	                                        String identificationVariable) {

		return innerJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinAs(String collectionPath,
	                                        String identificationVariable,
	                                        ExpressionTester joinCondition) {

		return innerJoinAs(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester innerJoinFetch(ExpressionTester collectionPath) {
		return innerJoinFetch(
			collectionPath,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetch(ExpressionTester collectionPath,
	                                           ExpressionTester identificationVariable) {

		return innerJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetch(ExpressionTester collectionPath,
	                                           ExpressionTester identificationVariable,
	                                           ExpressionTester joinCondition) {

		return join(
			INNER_JOIN_FETCH,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester innerJoinFetch(ExpressionTester collectionPath,
	                                           String identificationVariable) {

		return innerJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetch(ExpressionTester collectionPath,
	                                           String identificationVariable,
	                                           ExpressionTester joinCondition) {

		return innerJoinFetch(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester innerJoinFetch(String collectionPath) {
		return innerJoinFetch(
			collectionPath(collectionPath)
		);
	}

	protected static JoinTester innerJoinFetch(String collectionPath,
	                                           String identificationVariable) {

		return innerJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetch(String collectionPath,
	                                           String identificationVariable,
	                                           ExpressionTester joinCondition) {

		return innerJoinFetch(
			collectionPath(collectionPath),
			variable(identificationVariable),
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetchAs(ExpressionTester collectionPath) {
		return innerJoinFetchAs(
			collectionPath,
			nullExpression(),
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
	                                             ExpressionTester identificationVariable) {

		return innerJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
	                                             ExpressionTester identificationVariable,
	                                             ExpressionTester joinCondition) {

		return join(
			INNER_JOIN_FETCH,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
	                                             String identificationVariable) {

		return innerJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
	                                             String identificationVariable,
	                                             ExpressionTester joinCondition) {

		return innerJoinFetchAs(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester innerJoinFetchAs(String collectionPath) {
		return innerJoinFetchAs(
			collectionPath(collectionPath)
		);
	}

	protected static JoinTester innerJoinFetchAs(String collectionPath,
	                                             String identificationVariable) {

		return innerJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester innerJoinFetchAs(String collectionPath,
	                                             String identificationVariable,
	                                             ExpressionTester joinCondition) {

		return innerJoinFetchAs(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static InputParameterTester inputParameter(String inputParameter) {
		return new InputParameterTester(inputParameter);
	}

	protected static UnionClauseTester intersect(ExpressionTester subquery) {
		return union(INTERSECT, false, subquery);
	}

	protected static UnionClauseTester intersect(ExpressionTester selectClause,
	                                             ExpressionTester fromClause) {

		return intersect(subquery(selectClause, fromClause));
	}

	protected static UnionClauseTester intersect(ExpressionTester selectClause,
	                                             ExpressionTester fromClause,
	 	                                          ExpressionTester whereClause) {

		return intersect(subquery(selectClause, fromClause, whereClause));
	}

	protected static UnionClauseTester intersect(ExpressionTester selectClause,
	                                             ExpressionTester fromClause,
	 	                                          ExpressionTester whereClause,
	 	                                          ExpressionTester groupByClause,
		                                          ExpressionTester havingClause) {

		return intersect(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
	}

	protected static UnionClauseTester intersectAll(ExpressionTester subquery) {
		return union(INTERSECT, true, subquery);
	}

	protected static UnionClauseTester intersectAll(ExpressionTester selectClause,
	                                                ExpressionTester fromClause) {

		return intersectAll(subquery(selectClause, fromClause));
	}

	protected static UnionClauseTester intersectAll(ExpressionTester selectClause,
	                                                ExpressionTester fromClause,
	    	                                          ExpressionTester whereClause) {

		return intersectAll(subquery(selectClause, fromClause, whereClause));
	}

	protected static UnionClauseTester intersectAll(ExpressionTester selectClause,
	                                                ExpressionTester fromClause,
	    	                                          ExpressionTester whereClause,
	    	                                          ExpressionTester groupByClause,
	   	                                          ExpressionTester havingClause) {

		return intersectAll(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
	}

	protected static EmptyCollectionComparisonExpressionTester isEmpty(ExpressionTester collectionPath) {
		return new EmptyCollectionComparisonExpressionTester(collectionPath, false);
	}

	protected static EmptyCollectionComparisonExpressionTester isEmpty(String collectionPath) {
		return isEmpty(collectionPath(collectionPath));
	}

	protected static EmptyCollectionComparisonExpressionTester isNotEmpty(ExpressionTester collectionPath) {
		return new EmptyCollectionComparisonExpressionTester(collectionPath, true);
	}

	protected static EmptyCollectionComparisonExpressionTester isNotEmpty(String collectionPath) {
		return isNotEmpty(collectionPath(collectionPath));
	}

	protected static NullComparisonExpressionTester isNotNull(ExpressionTester expression) {
		return new NullComparisonExpressionTester(expression, true);
	}

	protected static NullComparisonExpressionTester isNull(ExpressionTester expression) {
		return new NullComparisonExpressionTester(expression, false);
	}

	protected static JoinTester join(ExpressionTester collectionPath,
	                                 ExpressionTester identificationVariable) {

		return join(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester join(ExpressionTester collectionPath,
	                                 ExpressionTester identificationVariable,
	                                 ExpressionTester joinCondition) {

		return join(
			JOIN,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester join(ExpressionTester collectionPath,
	                                 String identificationVariable) {

		return join(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester join(ExpressionTester collectionPath,
	                                 String identificationVariable,
	                                 ExpressionTester joinCondition) {

		return join(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	private static JoinTester join(String joinType,
	                               ExpressionTester collectionPath,
	                               boolean hasAs,
	                               ExpressionTester identificationVariable,
	                               ExpressionTester joinCondition) {

		return new JoinTester(
			joinType,
			collectionPath,
			hasAs,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester join(String collectionPath,
	                                 String identificationVariable) {

		return join(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester join(String collectionPath,
	                                 String identificationVariable,
	                                 ExpressionTester joinCondition) {

		return join(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester joinAs(ExpressionTester collectionPath,
	                                   ExpressionTester identificationVariable) {

		return joinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinAs(ExpressionTester collectionPath,
	                                   ExpressionTester identificationVariable,
	                                   ExpressionTester joinCondition) {

		return join(
			JOIN,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester joinAs(ExpressionTester collectionPath,
	                                   String identificationVariable) {

		return joinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinAs(ExpressionTester collectionPath,
	                                   String identificationVariable,
	                                   ExpressionTester joinCondition) {

		return joinAs(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester joinAs(String collectionPath,
	                                   String identificationVariable) {

		return joinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinAs(String collectionPath,
	                                   String identificationVariable,
	                                   ExpressionTester joinCondition) {

		return joinAs(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester joinAsTreat(String collectionPath,
	                                        String entityTypeLiteral,
	                                        String variable) {

		return joinAs(treat(collectionPath, entityTypeLiteral), variable);
	}

	protected static JoinTester joinAsTreatAs(String collectionPath,
	                                          String entityTypeLiteral,
	                                          String variable) {

		return joinAs(treatAs(collectionPath, entityTypeLiteral), variable);
	}

	protected static JoinTester joinFetch(ExpressionTester collectionPath) {
		JoinTester join = joinFetch(
			collectionPath,
			nullExpression()
		);
		join.hasSpaceAfterJoinAssociation = false;
		return join;
	}

	protected static JoinTester joinFetch(ExpressionTester collectionPath,
	                                      ExpressionTester identificationVariable) {

		return joinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetch(ExpressionTester collectionPath,
	                                      ExpressionTester identificationVariable,
	                                      ExpressionTester joinCondition) {

		return join(
			JOIN_FETCH,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester joinFetch(ExpressionTester collectionPath,
	                                      String identificationVariable) {

		return joinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetch(ExpressionTester collectionPath,
	                                      String identificationVariable,
	                                      ExpressionTester joinCondition) {

		return joinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetch(String collectionPath) {
		return joinFetch(
			collectionPath(collectionPath)
		);
	}

	protected static JoinTester joinFetch(String collectionPath,
	                                      String identificationVariable) {

		return joinFetch(
			collectionPath(collectionPath),
			variable(identificationVariable)
		);
	}

	protected static JoinTester joinFetch(String collectionPath,
	                                      String identificationVariable,
	                                      ExpressionTester joinCondition) {

		return joinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetchAs(ExpressionTester collectionPath,
	                                        ExpressionTester identificationVariable) {

		return joinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetchAs(ExpressionTester collectionPath,
	                                        ExpressionTester identificationVariable,
	                                        ExpressionTester joinCondition) {

		return join(
			JOIN_FETCH,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester joinFetchAs(ExpressionTester collectionPath,
	                                        String identificationVariable) {

		return joinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetchAs(ExpressionTester collectionPath,
	                                        String identificationVariable,
	                                        ExpressionTester joinCondition) {

		return joinFetchAs(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester joinFetchAs(String collectionPath,
	                                        String identificationVariable) {

		return joinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester joinFetchAs(String collectionPath,
	                                        String identificationVariable,
	                                        ExpressionTester joinCondition) {

		return joinFetchAs(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester joinTreat(String collectionPath,
	                                      String entityTypeLiteral,
	                                      String variable) {

		return join(treat(collectionPath, entityTypeLiteral), variable);
	}

	protected static JoinTester joinTreatAs(String collectionPath,
	                                        String entityTypeLiteral,
	                                        String variable) {

		return join(treatAs(collectionPath, entityTypeLiteral), variable);
	}

	protected static JPQLExpressionTester jpqlExpression(ExpressionTester queryStatement) {
		return jpqlExpression(queryStatement, nullExpression());
	}

	protected static JPQLExpressionTester jpqlExpression(ExpressionTester queryStatement,
	                                                     ExpressionTester unknownExpression) {

		return new JPQLExpressionTester(queryStatement, unknownExpression);
	}

	protected static JoinTester leftJoin(ExpressionTester collectionPath,
	                                     ExpressionTester identificationVariable) {

		return leftJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoin(ExpressionTester collectionPath,
	                                     ExpressionTester identificationVariable,
	                                     ExpressionTester joinCondition) {

		return join(
			LEFT_JOIN,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoin(ExpressionTester collectionPath,
	                                     String identificationVariable) {

		return leftJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoin(ExpressionTester collectionPath,
	                                     String identificationVariable,
	                                     ExpressionTester joinCondition) {

		return leftJoin(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoin(String collectionPath,
	                                     String identificationVariable) {

		return leftJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoin(String collectionPath,
	                                     String identificationVariable,
	                                     ExpressionTester joinCondition) {

		return leftJoin(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester leftJoinAs(ExpressionTester collectionPath,
	                                       ExpressionTester identificationVariable) {

		return leftJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinAs(ExpressionTester collectionPath,
	                                       ExpressionTester identificationVariable,
	                                       ExpressionTester joinCondition) {

		return join(
			LEFT_JOIN,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoinAs(ExpressionTester collectionPath,
	                                       String identificationVariable) {

		return leftJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinAs(ExpressionTester collectionPath,
	                                       String identificationVariable,
	                                       ExpressionTester joinCondition) {

		return leftJoinAs(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoinAs(String collectionPath,
	                                       String identificationVariable) {

		return leftJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinAs(String collectionPath,
	                                       String identificationVariable,
	                                       ExpressionTester joinCondition) {

		return leftJoinAs(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoinFetch(ExpressionTester collectionPath) {
		JoinTester join = leftJoinFetch(
			collectionPath,
			nullExpression()
		);
		join.hasSpaceAfterJoinAssociation = false;
		return join;
	}

	protected static JoinTester leftJoinFetch(ExpressionTester collectionPath,
	                                          ExpressionTester identificationVariable) {

		return leftJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetch(ExpressionTester collectionPath,
	                                          ExpressionTester identificationVariable,
	                                          ExpressionTester joinCondition) {

		return join(
			LEFT_JOIN_FETCH,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoinFetch(ExpressionTester collectionPath,
	                                          String identificationVariable) {

		return leftJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetch(ExpressionTester collectionPath,
	                                          String identificationVariable,
	                                          ExpressionTester joinCondition) {

		return leftJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetch(String collectionPath) {
		return leftJoinFetch(
			collectionPath(collectionPath)
		);
	}

	protected static JoinTester leftJoinFetch(String collectionPath,
	                                          String identificationVariable) {

		return leftJoinFetch(
			collectionPath(collectionPath),
			variable(identificationVariable)
		);
	}

	protected static JoinTester leftJoinFetch(String collectionPath,
	                                          String identificationVariable,
	                                          ExpressionTester joinCondition) {

		return leftJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
	                                            ExpressionTester identificationVariable) {

		return leftJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
	                                            ExpressionTester identificationVariable,
	                                            ExpressionTester joinCondition) {

		return join(
			LEFT_JOIN_FETCH,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
	                                            String identificationVariable) {

		return leftJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
	                                            String identificationVariable,
	                                            ExpressionTester joinCondition) {

		return leftJoinFetchAs(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester leftJoinFetchAs(String collectionPath,
	                                            String identificationVariable) {

		return leftJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftJoinFetchAs(String collectionPath,
	                                            String identificationVariable,
	                                            ExpressionTester joinCondition) {

		return leftJoinFetchAs(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoin(ExpressionTester collectionPath,
	                                          ExpressionTester identificationVariable) {

		return leftOuterJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoin(ExpressionTester collectionPath,
	                                          ExpressionTester identificationVariable,
	                                          ExpressionTester joinCondition) {

		return join(
			LEFT_OUTER_JOIN,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoin(ExpressionTester collectionPath,
	                                          String identificationVariable) {

		return leftOuterJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoin(ExpressionTester collectionPath,
	                                          String identificationVariable,
	                                          ExpressionTester joinCondition) {

		return leftOuterJoin(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoin(String collectionPath,
	                                          String identificationVariable) {

		return leftOuterJoin(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoin(String collectionPath,
	                                          String identificationVariable,
	                                          ExpressionTester joinCondition) {

		return leftOuterJoin(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinAs(ExpressionTester collectionPath,
	                                            ExpressionTester identificationVariable) {

		return leftOuterJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinAs(ExpressionTester collectionPath,
	                                            ExpressionTester identificationVariable,
	                                            ExpressionTester joinCondition) {

		return join(
			LEFT_OUTER_JOIN,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinAs(String collectionPath,
	                                            String identificationVariable) {

		return leftOuterJoinAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinAs(String collectionPath,
	                                            String identificationVariable,
	                                            ExpressionTester joinCondition) {

		return leftOuterJoinAs(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath) {
		JoinTester join = leftOuterJoinFetch(
			collectionPath,
			nullExpression()
		);
		join.hasSpaceAfterJoinAssociation = false;
		return join;
	}

	protected static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
	                                               ExpressionTester identificationVariable) {

		return leftOuterJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
	                                               ExpressionTester identificationVariable,
	                                               ExpressionTester joinCondition) {

		return join(
			LEFT_OUTER_JOIN_FETCH,
			collectionPath,
			false,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
	                                               String identificationVariable) {

		return leftOuterJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
	                                               String identificationVariable,
	                                               ExpressionTester joinCondition) {

		return leftOuterJoinFetch(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinFetch(String collectionPath) {
		return leftOuterJoinFetch(
			collectionPath(collectionPath)
		);
	}

	protected static JoinTester leftOuterJoinFetch(String collectionPath,
	                                               String identificationVariable) {

		return leftOuterJoinFetch(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetch(String collectionPath,
	                                               String identificationVariable,
	                                               ExpressionTester joinCondition) {

		return leftOuterJoinFetch(
			collectionPath,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath) {
		return leftOuterJoinFetchAs(
			collectionPath,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
	                                                 ExpressionTester identificationVariable) {

		return leftOuterJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
	                                                 ExpressionTester identificationVariable,
	                                                 ExpressionTester joinCondition) {

		return join(
			LEFT_OUTER_JOIN_FETCH,
			collectionPath,
			true,
			identificationVariable,
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
	                                                 String identificationVariable) {

		return leftOuterJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
	                                                 String identificationVariable,
	                                                 ExpressionTester joinCondition) {

		return leftOuterJoinFetchAs(
			collectionPath,
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(String collectionPath,
	                                                 String identificationVariable) {

		return leftOuterJoinFetchAs(
			collectionPath,
			identificationVariable,
			nullExpression()
		);
	}

	protected static JoinTester leftOuterJoinFetchAs(String collectionPath,
	                                                 String identificationVariable,
	                                                 ExpressionTester joinCondition) {

		return leftOuterJoinFetchAs(
			collectionPath(collectionPath),
			variable(identificationVariable),
			joinCondition
		);
	}

	protected static LengthExpressionTester length(ExpressionTester stringPrimary) {
		return new LengthExpressionTester(stringPrimary);
	}

	protected static LikeExpressionTester like(ExpressionTester stringExpression,
	                                           ExpressionTester patternValue) {

		return like(stringExpression, patternValue, nullExpression());
	}

	protected static LikeExpressionTester like(ExpressionTester stringExpression,
	                                           ExpressionTester patternValue,
	                                           char escapeCharacter) {

		return like(stringExpression, patternValue, string(quote(escapeCharacter)));
	}

	protected static LikeExpressionTester like(ExpressionTester stringExpression,
	                                           ExpressionTester patternValue,
	                                           ExpressionTester escapeCharacter) {

		return new LikeExpressionTester(stringExpression, false, patternValue, escapeCharacter);
	}

	protected static LocateExpressionTester locate(ExpressionTester firstExpression,
	                                               ExpressionTester secondExpression) {

		return locate(firstExpression, secondExpression, nullExpression());
	}

	protected static LocateExpressionTester locate(ExpressionTester firstExpression,
	                                               ExpressionTester secondExpression,
	                                               ExpressionTester thirdExpression) {

		return new LocateExpressionTester(firstExpression, secondExpression, thirdExpression);
	}

	protected static LowerExpressionTester lower(ExpressionTester stringPrimary) {
		return new LowerExpressionTester(stringPrimary);
	}

	protected static ComparisonExpressionTester lowerThan(ExpressionTester leftExpression,
	                                                      ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.LOWER_THAN, rightExpression);
	}

	protected static ComparisonExpressionTester lowerThanOrEqual(ExpressionTester leftExpression,
	                                                             ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.LOWER_THAN_OR_EQUAL, rightExpression);
	}

	protected static MaxFunctionTester max(ExpressionTester expression) {
		return new MaxFunctionTester(expression, false);
	}

	protected static MaxFunctionTester max(String statefieldPathExpression) {
		return max(path(statefieldPathExpression));
	}

	protected static ExpressionTester maxDistinct(String statefieldPathExpression) {
		return new MaxFunctionTester(path(statefieldPathExpression), true);
	}

	protected static CollectionMemberExpressionTester member(ExpressionTester entityExpression,
	                                                         ExpressionTester collectionPath) {

		return new CollectionMemberExpressionTester(
			entityExpression,
			false,
			false,
			collectionPath
		);
	}

	protected static CollectionMemberExpressionTester member(ExpressionTester entityExpression,
	                                                         String collectionPath) {

		return member(entityExpression, collectionPath(collectionPath));
	}

	protected static CollectionMemberExpressionTester member(String identificationVariable,
	                                                         String collectionPath) {

		return member(variable(identificationVariable), collectionPath);
	}

	protected static CollectionMemberExpressionTester memberOf(ExpressionTester entityExpression,
	                                                           ExpressionTester collectionPath) {

		return new CollectionMemberExpressionTester(
			entityExpression,
			false,
			true,
			collectionPath
		);
	}

	protected static CollectionMemberExpressionTester memberOf(ExpressionTester entityExpression,
	                                                           String collectionPath) {

		return memberOf(entityExpression, collectionPath(collectionPath));
	}

	protected static CollectionMemberExpressionTester memberOf(String identificationVariable,
	                                                           String collectionPath) {

		return memberOf( variable(identificationVariable), collectionPath);
	}

	protected static MinFunctionTester min(ExpressionTester expression) {
		return new MinFunctionTester(expression, false);
	}

	protected static MinFunctionTester min(String statefieldPathExpression) {
		return min(path(statefieldPathExpression));
	}

	protected static MinFunctionTester minDistinct(String statefieldPathExpression) {
		return new MinFunctionTester(path(statefieldPathExpression), true);
	}

	protected static ArithmeticFactorTester minus(ExpressionTester expression) {
		return new ArithmeticFactorTester(MINUS, expression);
	}

	protected static ModExpressionTester mod(ExpressionTester simpleArithmeticExpression1,
	                                         ExpressionTester simpleArithmeticExpression2) {

		return new ModExpressionTester(simpleArithmeticExpression1, simpleArithmeticExpression2);
	}

	protected static MultiplicationExpressionTester multiplication(ExpressionTester leftExpression,
	                                                               ExpressionTester rightExpression) {

		return new MultiplicationExpressionTester(leftExpression, rightExpression);
	}

	protected static ConstructorExpressionTester new_(String className,
	                                                  ExpressionTester constructorItem) {

		return new ConstructorExpressionTester(className, constructorItem);
	}

	protected static ConstructorExpressionTester new_(String className,
	                                                  ExpressionTester... constructorItems) {

		return new ConstructorExpressionTester(className, collection(constructorItems));
	}

	protected static NotExpressionTester not(ExpressionTester expression) {
		return new NotExpressionTester(expression);
	}

	protected static BetweenExpressionTester notBetween(ExpressionTester expression,
	                                                    ExpressionTester lowerBoundExpression,
	                                                    ExpressionTester upperBoundExpression) {

		return new BetweenExpressionTester(
			expression,
			true,
			lowerBoundExpression,
			upperBoundExpression
		);
	}

	protected static ComparisonExpressionTester notEqual(ExpressionTester leftExpression,
	                                                     ExpressionTester rightExpression) {

		return comparison(leftExpression, Expression.NOT_EQUAL, rightExpression);
	}

	protected static ExistsExpressionTester notExists(ExpressionTester subquery) {
		return new ExistsExpressionTester(subquery, true);
	}

	protected static InExpressionTester notIn(ExpressionTester stateFieldPathExpression,
	                                          ExpressionTester inItems) {

		return new InExpressionTester(stateFieldPathExpression, true, inItems);
	}

	protected static InExpressionTester notIn(ExpressionTester stateFieldPathExpression,
	                                          ExpressionTester... inItems) {

		return new InExpressionTester(stateFieldPathExpression, true, collection(inItems));
	}

	protected static InExpressionTester notIn(ExpressionTester stateFieldPathExpression,
	                                          String inputParameter) {

		InExpressionTester in = notIn(stateFieldPathExpression, inputParameter(inputParameter));
		in.hasLeftParenthesis  = false;
		in.hasRightParenthesis = false;
		in.hasSpaceAfterIn     = true;
		return in;
	}

	protected static InExpressionTester notIn(String stateFieldPathExpression,
	                                          ExpressionTester... inItems) {

		return notIn(path(stateFieldPathExpression), collection(inItems));
	}

	protected static InExpressionTester notIn(String stateFieldPathExpression,
	                                          ExpressionTester inItem) {

		return notIn(path(stateFieldPathExpression), inItem);
	}

	protected static InExpressionTester notIn(String pathExpression, String singleInputParameter) {
		return notIn(path(pathExpression), singleInputParameter);
	}

	protected static LikeExpressionTester notLike(ExpressionTester stringExpression,
	                                              ExpressionTester patternValue) {

		return notLike(stringExpression, patternValue, nullExpression());
	}

	protected static LikeExpressionTester notLike(ExpressionTester stringExpression,
	                                              ExpressionTester patternValue,
	                                              ExpressionTester escapeCharacter) {

		return new LikeExpressionTester(stringExpression, true, patternValue, escapeCharacter);
	}

	protected static CollectionMemberExpressionTester notMember(ExpressionTester entityExpression,
	                                                            ExpressionTester collectionPath) {

		return new CollectionMemberExpressionTester(
			entityExpression,
			true,
			false,
			collectionPath
		);
	}

	protected static CollectionMemberExpressionTester notMember(ExpressionTester entityExpression,
	                                                            String collectionPath) {

		return notMember(entityExpression, collectionPath);
	}

	protected static CollectionMemberExpressionTester notMember(String identificationVariable,
	                                                            String collectionPath) {

		return notMember(variable(identificationVariable), collectionPath);
	}

	protected static CollectionMemberExpressionTester notMemberOf(ExpressionTester entityExpression,
	                                                              ExpressionTester collectionPath) {

		return new CollectionMemberExpressionTester(
			entityExpression,
			true,
			true,
			collectionPath
		);
	}

	protected static ExpressionTester NULL() {
		return new KeywordExpressionTester(NULL);
	}

	protected static ExpressionTester nullExpression() {
		return new NullExpressionTester();
	}

	protected static NullIfExpressionTester nullIf(ExpressionTester expression1,
	                                               ExpressionTester expression2) {

		return new NullIfExpressionTester(expression1, expression2);
	}

	protected static NumericLiteralTester numeric(double number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralTester numeric(float number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralTester numeric(int number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralTester numeric(long number) {
		return numeric(String.valueOf(number));
	}

	protected static NumericLiteralTester numeric(String value) {
		return new NumericLiteralTester(value);
	}

	protected static ObjectExpressionTester object(ExpressionTester identificationVariable) {
		return new ObjectExpressionTester(identificationVariable);
	}

	protected static ObjectExpressionTester object(String identificationVariable) {
		return object(variable(identificationVariable));
	}

	protected static OnClauseTester on(ExpressionTester conditionalExpression) {
		return new OnClauseTester(conditionalExpression);
	}

	protected static OrExpressionTester or(ExpressionTester leftExpression,
	                                       ExpressionTester rightExpression) {

		return new OrExpressionTester(leftExpression, rightExpression);
	}

	protected static OrderByClauseTester orderBy(ExpressionTester orderByItem) {
		return new OrderByClauseTester(orderByItem);
	}

	protected static OrderByClauseTester orderBy(ExpressionTester... orderByItems) {
		return new OrderByClauseTester(collection(orderByItems));
	}

	protected static OrderByClauseTester orderBy(String stateFieldPathExpression) {
		return new OrderByClauseTester(orderByItem(stateFieldPathExpression));
	}

	protected static OrderByItemTester orderByItem(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DEFAULT, NullOrdering.DEFAULT);
	}

	private static OrderByItemTester orderByItem(ExpressionTester orderByItem,
	                                             Ordering ordering,
	                                             NullOrdering nullOrdering) {

		return new OrderByItemTester(orderByItem, ordering, nullOrdering);
	}

	protected static OrderByItemTester orderByItem(String stateFieldPathExpression) {
		return orderByItem(path(stateFieldPathExpression));
	}

	protected static OrderByItemTester orderByItemAsc(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.ASC, NullOrdering.DEFAULT);
	}

	protected static OrderByItemTester orderByItemAsc(String stateFieldPathExpression) {
		return orderByItemAsc(path(stateFieldPathExpression));
	}

	protected static OrderByItemTester orderByItemAscNullsFirst(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.ASC, NullOrdering.NULLS_FIRST);
	}

	protected static OrderByItemTester orderByItemAscNullsFirst(String pathExpression) {
		return orderByItemAscNullsFirst(path(pathExpression));
	}

	protected static OrderByItemTester orderByItemAscNullsLast(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.ASC, NullOrdering.NULLS_LAST);
	}

	protected static OrderByItemTester orderByItemAscNullsLast(String stateFieldPathExpression) {
		return orderByItemAscNullsLast(path(stateFieldPathExpression));
	}

	protected static OrderByItemTester orderByItemDesc(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DESC, NullOrdering.DEFAULT);
	}

	protected static OrderByItemTester orderByItemDesc(String stateFieldPathExpression) {
		return orderByItemDesc(path(stateFieldPathExpression));
	}

	protected static OrderByItemTester orderByItemDescNullsFirst(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DESC, NullOrdering.NULLS_FIRST);
	}

	protected static OrderByItemTester orderByItemDescNullsFirst(String pathExpression) {
		return orderByItemDescNullsFirst(path(pathExpression));
	}

	protected static OrderByItemTester orderByItemDescNullsLast(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DESC, NullOrdering.NULLS_LAST);
	}

	protected static OrderByItemTester orderByItemDescNullsLast(String pathExpression) {
		return orderByItemAscNullsLast(path(pathExpression));
	}

	protected static OrderByItemTester orderByItemNullsFirst(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DEFAULT, NullOrdering.NULLS_FIRST);
	}

	protected static OrderByItemTester orderByItemNullsFirst(String pathExpression) {
		return orderByItemNullsFirst(path(pathExpression));
	}

	protected static OrderByItemTester orderByItemNullsLast(ExpressionTester orderByItem) {
		return orderByItem(orderByItem, Ordering.DEFAULT, NullOrdering.NULLS_LAST);
	}

	protected static OrderByItemTester orderByItemNullsLast(String pathExpression) {
		return orderByItemNullsLast(path(pathExpression));
	}

	private static StateFieldPathExpressionTester path(ExpressionTester identificationVariable,
	                                                   boolean startsWithDot,
	                                                   String pathExpression) {

		return new StateFieldPathExpressionTester(
			identificationVariable,
			startsWithDot ? "." + pathExpression : pathExpression
		);
	}

	protected static StateFieldPathExpressionTester path(ExpressionTester identificationVariable,
	                                                     String pathExpression) {

		return path(identificationVariable, false, pathExpression);
	}

	protected static StateFieldPathExpressionTester path(String pathExpression) {

		int dotIndex = pathExpression.indexOf('.');

		if (dotIndex == 0) {
			return path(nullExpression(), false, pathExpression);
		}

		String variable = pathExpression.substring(0, dotIndex);
		String path = pathExpression.substring(dotIndex + 1);
		return path(variable(variable), false, path);
	}

	protected static ArithmeticFactorTester plus(ExpressionTester expression) {
		return new ArithmeticFactorTester(PLUS, expression);
	}

	protected static String quote(char character) {
		return new StringBuilder(3).append("'").append(character).append("'").toString();
	}

	private static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
	                                                                       boolean hasAs,
	                                                                       ExpressionTester identificationVariable) {

		return new RangeVariableDeclarationTester(abstractSchemaName, hasAs, identificationVariable);
	}

	protected static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
	                                                                         ExpressionTester identificationVariable) {

		return rangeVariableDeclaration(abstractSchemaName, false, identificationVariable);
	}

	protected static RangeVariableDeclarationTester rangeVariableDeclaration(String abstractSchemaName) {
		return rangeVariableDeclaration(
			abstractSchemaName(abstractSchemaName),
			false,
			nullExpression()
		);
	}

	protected static RangeVariableDeclarationTester rangeVariableDeclaration(String abstractSchemaName,
	                                                                         String identificationVariable) {

		return rangeVariableDeclaration(
			abstractSchemaName(abstractSchemaName),
			false,
			variable(identificationVariable)
		);
	}

	protected static RangeVariableDeclarationTester rangeVariableDeclarationAs(ExpressionTester abstractSchemaName,
	                                                                           ExpressionTester identificationVariable) {

		return rangeVariableDeclaration(abstractSchemaName, true, identificationVariable);
	}

	protected static RangeVariableDeclarationTester rangeVariableDeclarationAs(String abstractSchemaName,
	                                                                           String identificationVariable) {

		return rangeVariableDeclarationAs(
			abstractSchemaName(abstractSchemaName),
			variable(identificationVariable)
		);
	}

	protected static RegexpExpressionTester regexp(ExpressionTester stringExpression,
	                                               ExpressionTester patternValue) {

		return new RegexpExpressionTester(stringExpression, patternValue);
	}

	protected static RegexpExpressionTester regexp(ExpressionTester stringExpression,
	                                               String patternValue) {

		return regexp(stringExpression, string(patternValue));
	}

	private static ResultVariableTester resultVariable(ExpressionTester selectExpression,
	                                                   boolean hasAs,
	                                                   ExpressionTester resultVariable) {

		return new ResultVariableTester(selectExpression, hasAs, resultVariable);
	}

	protected static ResultVariableTester resultVariable(ExpressionTester selectExpression,
	                                                     ExpressionTester resultVariable) {

		return resultVariable(selectExpression, false, resultVariable);
	}

	protected static ResultVariableTester resultVariable(ExpressionTester selectExpression,
	                                                     String resultVariable) {

		return resultVariable(selectExpression, false, variable(resultVariable));
	}

	protected static ResultVariableTester resultVariableAs(ExpressionTester selectExpression,
	                                                       ExpressionTester resultVariable) {

		return resultVariable(selectExpression, true, resultVariable);
	}

	protected static ResultVariableTester resultVariableAs(ExpressionTester selectExpression,
	                                                       String resultVariable) {

		return resultVariableAs(selectExpression, variable(resultVariable));
	}

	protected static SelectClauseTester select(ExpressionTester selectExpression) {
		return select(selectExpression, false);
	}

	protected static SelectClauseTester select(ExpressionTester... selectExpressions) {
		return new SelectClauseTester(collection(selectExpressions), false);
	}

	private static SelectClauseTester select(ExpressionTester selectExpression, boolean hasDistinct) {
		return new SelectClauseTester(selectExpression, hasDistinct);
	}

	protected static SelectClauseTester selectDistinct(ExpressionTester... selectExpressions) {
		return new SelectClauseTester(collection(selectExpressions), true);
	}

	protected static SelectClauseTester selectDistinct(ExpressionTester selectExpression) {
		return new SelectClauseTester(selectExpression, true);
	}

	protected static SelectClauseTester selectDisting(ExpressionTester selectExpression) {
		return select(selectExpression, true);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause) {

		return selectStatement(selectClause, fromClause, nullExpression());
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       ExpressionTester whereClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       ExpressionTester whereClause,
	                                                       ExpressionTester groupByClause,
	                                                       ExpressionTester havingClause,
	                                                       ExpressionTester orderByClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause,
			orderByClause,
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       ExpressionTester whereClause,
	                                                       ExpressionTester groupByClause,
	                                                       ExpressionTester havingClause,
	                                                       ExpressionTester orderByClause,
	                                                       ExpressionTester... unionClauses) {

		return new SelectStatementTester(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause,
			orderByClause,
			spacedCollection(unionClauses)
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       ExpressionTester whereClause,
	                                                       ExpressionTester groupByClause,
	                                                       ExpressionTester havingClause,
	                                                       ExpressionTester orderByClause,
	                                                       ExpressionTester unionClause) {

		return new SelectStatementTester(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause,
			orderByClause,
			unionClause
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       ExpressionTester whereClause,
	                                                       UnionClauseTester... unionClauses) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression(),
			nullExpression(),
			unionClauses
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       GroupByClauseTester groupByClause) {

		return selectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			groupByClause,
			nullExpression(),
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       GroupByClauseTester groupByClause,
	                                                       HavingClauseTester havingClause) {

		return selectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			groupByClause,
			havingClause,
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       HavingClauseTester havingClause) {

		return selectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			havingClause,
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       OrderByClauseTester orderByClause) {

		return selectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderByClause
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       UnionClauseTester... unionClauses) {

		return selectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			unionClauses
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       UnionClauseTester unionClause) {

		return selectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			unionClause
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       WhereClauseTester whereClause,
	                                                       GroupByClauseTester groupByClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			nullExpression(),
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       WhereClauseTester whereClause,
	                                                       GroupByClauseTester groupByClause,
	                                                       HavingClauseTester havingClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause,
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       WhereClauseTester whereClause,
	                                                       HavingClauseTester havingClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			havingClause,
			nullExpression()
		);
	}

	protected static SelectStatementTester selectStatement(ExpressionTester selectClause,
	                                                       ExpressionTester fromClause,
	                                                       WhereClauseTester whereClause,
	                                                       OrderByClauseTester orderByClause) {

		return selectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression(),
			orderByClause
		);
	}

	protected static UpdateItemTester set(ExpressionTester stateFieldPathExpression,
	                                      ExpressionTester newValue) {

		return new UpdateItemTester(stateFieldPathExpression, newValue);
	}

	protected static UpdateItemTester set(String pathExpression, ExpressionTester newValue) {

		if (pathExpression.startsWith("{")) {

			int dotIndex = pathExpression.indexOf(".");

			return set(
				path(
					virtualVariable(pathExpression.substring(0, dotIndex)),
					false,
					pathExpression.substring(dotIndex + 1)
				),
				newValue
			);
		}

		return set(path(pathExpression), newValue);
	}

	protected static SizeExpressionTester size(ExpressionTester collectionPath) {
		return new SizeExpressionTester(collectionPath);
	}

	protected static SizeExpressionTester size(String collectionPath) {
		return size(collectionPath(collectionPath));
	}

	protected static AllOrAnyExpressionTester some(ExpressionTester subquery) {
		return new AllOrAnyExpressionTester(SOME, subquery);
	}

	protected static CollectionExpressionTester spacedCollection(ExpressionTester... expressions) {

		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length,     Boolean.FALSE);

		spaces[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, commas, spaces);
	}

	protected static SqrtExpressionTester sqrt(ExpressionTester simpleArithmeticExpression) {
		return new SqrtExpressionTester(simpleArithmeticExpression);
	}

	protected static StringLiteralTester string(char literal) {
		return new StringLiteralTester(quote(literal));
	}

	protected static StringLiteralTester string(String literal) {
		return new StringLiteralTester(literal);
	}

	protected static SubExpressionTester sub(ExpressionTester expression) {
		return new SubExpressionTester(expression);
	}

	protected static SimpleFromClauseTester subFrom(ExpressionTester declaration) {
		return new SimpleFromClauseTester(declaration);
	}

	protected static SimpleFromClauseTester subFrom(ExpressionTester... declarations) {
		return new SimpleFromClauseTester(collection(declarations));
	}

	protected static SimpleFromClauseTester subFrom(String abstractSchemaName,
	                                                String identificationVariable) {

		return subFrom(identificationVariableDeclaration(abstractSchemaName, identificationVariable));
	}

	protected static SimpleFromClauseTester subFrom(String abstractSchemaName,
	                                                String identificationVariable,
	                                                ExpressionTester... joins) {

		return subFrom(identificationVariableDeclaration(
			abstractSchemaName,
			identificationVariable,
			joins
		));
	}

	protected static SimpleFromClauseTester subFrom(String abstractSchemaName,
	                                                String identificationVariable,
	                                                ExpressionTester joins) {

		return subFrom(identificationVariableDeclaration(
			abstractSchemaName,
			identificationVariable,
			joins
		));
	}

	protected static CollectionMemberDeclarationTester subFromIn(ExpressionTester collectionPath) {

		CollectionMemberDeclarationTester in = new CollectionMemberDeclarationTester(
			collectionPath,
			false,
			nullExpression()
		);

		in.hasLeftParenthesis            = false;
		in.hasRightParenthesis           = false;
		in.hasSpaceAfterIn               = true;
		in.hasSpaceAfterRightParenthesis = false;

		return in;
	}

	protected static CollectionMemberDeclarationTester subFromIn(String collectionPath) {
		return subFromIn(collectionPath(collectionPath));
	}

	protected static SimpleSelectStatementTester subquery(ExpressionTester selectClause,
	                                                      ExpressionTester fromClause) {

		return subquery(selectClause, fromClause, nullExpression());
	}

	protected static SimpleSelectStatementTester subquery(ExpressionTester selectClause,
	                                                      ExpressionTester fromClause,
	                                                      ExpressionTester whereClause) {

		return subSelectStatement(selectClause, fromClause, whereClause);
	}

	protected static SimpleSelectStatementTester subquery(ExpressionTester selectClause,
	                                                      ExpressionTester fromClause,
	                                                      ExpressionTester whereClause,
	                                                      ExpressionTester groupByClause,
	                                                      ExpressionTester havingClause) {

		return subSelectStatement(selectClause, fromClause, whereClause, groupByClause, havingClause);
	}

	protected static SimpleSelectClauseTester subSelect(ExpressionTester selectExpression) {
		return subSelect(selectExpression, false);
	}

	protected static SimpleSelectClauseTester subSelect(ExpressionTester... selectExpressions) {
		return new SimpleSelectClauseTester(collection(selectExpressions), false);
	}

	private static SimpleSelectClauseTester subSelect(ExpressionTester selectExpression,
	                                                  boolean hasDistinct) {

		return new SimpleSelectClauseTester(selectExpression, hasDistinct);
	}

	protected static SimpleSelectClauseTester subSelectDistinct(ExpressionTester selectExpression) {
		return subSelect(selectExpression, true);
	}

	protected static SimpleSelectClauseTester subSelectDistinct(ExpressionTester... selectExpressions) {
		return new SimpleSelectClauseTester(collection(selectExpressions), true);
	}

	protected static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
	                                                                ExpressionTester fromClause) {

		return subSelectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	protected static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
	                                                                ExpressionTester fromClause,
	                                                                ExpressionTester whereClause) {

		return subSelectStatement(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression()
		);
	}

	protected static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
	                                                                ExpressionTester fromClause,
	                                                                ExpressionTester whereClause,
	                                                                ExpressionTester groupByClause,
	                                                                ExpressionTester havingClause) {

		return new SimpleSelectStatementTester(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause
		);
	}

	protected static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
	                                                                ExpressionTester fromClause,
	                                                                GroupByClauseTester groupByClause) {

		return subSelectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			groupByClause,
			nullExpression()
		);
	}

	protected static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
	                                                                ExpressionTester fromClause,
	                                                                HavingClauseTester havingClause) {

		return subSelectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			havingClause
		);
	}

	protected static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
	                                                                ExpressionTester fromClause,
	                                                                OrderByClauseTester orderByClause) {

		return subSelectStatement(
			selectClause,
			fromClause,
			nullExpression(),
			nullExpression(),
			orderByClause
		);
	}

	protected static SubstringExpressionTester substring(ExpressionTester firstExpression,
	                                                     ExpressionTester secondExpression) {

		return substring(firstExpression, secondExpression, nullExpression());
	}

	protected static SubstringExpressionTester substring(ExpressionTester firstExpression,
	                                                     ExpressionTester secondExpression,
	                                                     ExpressionTester thirdExpression) {

		return new SubstringExpressionTester(firstExpression, secondExpression, thirdExpression);
	}

	protected static SubtractionExpressionTester subtract(ExpressionTester leftExpression,
	                                                      ExpressionTester rightExpression) {

		return new SubtractionExpressionTester(leftExpression, rightExpression);
	}

	protected static SumFunctionTester sum(ExpressionTester expression) {
		return new SumFunctionTester(expression, false);
	}

	protected static SumFunctionTester sum(String statefieldPathExpression) {
		return sum(path(statefieldPathExpression));
	}

	protected static SumFunctionTester sumDistinct(String statefieldPathExpression) {
		return new SumFunctionTester(path(statefieldPathExpression), true);
	}

	protected static TableExpressionTester table(ExpressionTester tableName) {
		return new TableExpressionTester(tableName);
	}

	protected static TableExpressionTester table(String tableName) {
		return table(string(tableName));
	}

	protected static TableVariableDeclarationTester tableVariableDeclaration(String tableName,
	                                                                         String variable) {

		return tableVariableDeclaration(table(tableName), variable(variable));
	}

	protected static TableVariableDeclarationTester tableVariableDeclaration(TableExpressionTester tableName,
	                                                                         ExpressionTester variable) {

		return new TableVariableDeclarationTester(tableName, false, variable);
	}

	protected static TableVariableDeclarationTester tableVariableDeclaration(TableExpressionTester tableName,
	                                                                         String variable) {

		return tableVariableDeclaration(tableName, variable(variable));
	}

	protected static TableVariableDeclarationTester tableVariableDeclarationAs(String tableName,
	                                                                           String variable) {

		return tableVariableDeclarationAs(table(tableName), variable(variable));
	}

	protected static TableVariableDeclarationTester tableVariableDeclarationAs(TableExpressionTester tableName,
	                                                                           ExpressionTester variable) {

		return new TableVariableDeclarationTester(tableName, true, variable);
	}

	protected static TableVariableDeclarationTester tableVariableDeclarationAs(TableExpressionTester tableName,
	                                                                           String variable) {

		return tableVariableDeclarationAs(tableName, variable(variable));
	}

	protected static TreatExpressionTester treat(ExpressionTester pathExpression,
	                                             ExpressionTester entityTypeName) {

		return new TreatExpressionTester(pathExpression, false, entityTypeName);
	}

	protected static TreatExpressionTester treat(ExpressionTester pathExpression,
	                                             String entityTypeName) {

		return treat(pathExpression, entity(entityTypeName));
	}

	protected static TreatExpressionTester treat(String pathExpression,
	                                             String entityTypeName) {

		return treat(collectionPath(pathExpression), entity(entityTypeName));
	}

	protected static TreatExpressionTester treatAs(ExpressionTester pathExpression,
	                                               ExpressionTester entityTypeName) {

		return new TreatExpressionTester(pathExpression, true, entityTypeName);
	}

	protected static TreatExpressionTester treatAs(ExpressionTester pathExpression,
	                                               String entityTypeName) {

		return treatAs(pathExpression, entity(entityTypeName));
	}

	protected static TreatExpressionTester treatAs(String pathExpression, String entityTypeName) {
		return treatAs(collectionPath(pathExpression), entity(entityTypeName));
	}

	protected static TrimExpressionTester trim(char trimCharacter, ExpressionTester stringPrimary) {
		return trim(string(quote(trimCharacter)), stringPrimary);
	}

	protected static TrimExpressionTester trim(ExpressionTester stringPrimary) {
		return trim(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trim(ExpressionTester trimCharacter,
	                                           ExpressionTester stringPrimary) {

		return trim(Specification.DEFAULT, trimCharacter, false, stringPrimary);
	}

	private static TrimExpressionTester trim(Specification specification,
	                                         ExpressionTester trimCharacter,
	                                         boolean hasFrom,
	                                         ExpressionTester stringPrimary) {

		return new TrimExpressionTester(specification, stringPrimary, trimCharacter, hasFrom);
	}

	protected static TrimExpressionTester trimBoth(ExpressionTester stringPrimary) {
		return trim(Specification.BOTH, nullExpression(), false, stringPrimary);
	}

	protected static TrimExpressionTester trimBothFrom(char trimCharacter,
	                                                   ExpressionTester stringPrimary) {

		return trimBothFrom(string(quote(trimCharacter)), stringPrimary);
	}

	protected static TrimExpressionTester trimBothFrom(ExpressionTester stringPrimary) {
		return trimBothFrom(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trimBothFrom(ExpressionTester trimCharacter,
	                                                   ExpressionTester stringPrimary) {

		return trim(Specification.BOTH, trimCharacter, true, stringPrimary);
	}

	protected static TrimExpressionTester trimFrom(char trimCharacter, ExpressionTester stringPrimary) {
		return trimFrom(string(trimCharacter), stringPrimary);
	}

	protected static TrimExpressionTester trimFrom(ExpressionTester stringPrimary) {
		return trimFrom(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trimFrom(ExpressionTester trimCharacter,
	                                               ExpressionTester stringPrimary) {

		return trim(Specification.DEFAULT, trimCharacter, true, stringPrimary);
	}

	protected static TrimExpressionTester trimLeading(char trimCharacter,
	                                                  ExpressionTester stringPrimary) {

		return trimLeading(string(quote(trimCharacter)), stringPrimary);
	}

	protected static TrimExpressionTester trimLeading(ExpressionTester stringPrimary) {
		return trimLeading(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trimLeading(ExpressionTester trimCharacter,
	                                                  ExpressionTester stringPrimary) {

		return trim(Specification.LEADING, trimCharacter, false, stringPrimary);
	}

	protected static TrimExpressionTester trimLeadingFrom(char trimCharacter,
	                                                      ExpressionTester stringPrimary) {

		return trimLeadingFrom(string(quote(trimCharacter)), stringPrimary);
	}

	protected static TrimExpressionTester trimLeadingFrom(ExpressionTester stringPrimary) {
		return trimLeadingFrom(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trimLeadingFrom(ExpressionTester trimCharacter,
	                                                      ExpressionTester stringPrimary) {

		return trim(Specification.LEADING, trimCharacter, true, stringPrimary);
	}

	protected static TrimExpressionTester trimTrailing(char trimCharacter,
	                                                   ExpressionTester stringPrimary) {

		return trimTrailing(string(quote(trimCharacter)), stringPrimary);
	}

	protected static TrimExpressionTester trimTrailing(ExpressionTester stringPrimary) {
		return trimTrailing(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trimTrailing(ExpressionTester trimCharacter,
	                                                   ExpressionTester stringPrimary) {

		return trim(Specification.TRAILING, trimCharacter, false, stringPrimary);
	}

	protected static TrimExpressionTester trimTrailingFrom(char trimCharacter,
	                                                       ExpressionTester stringPrimary) {

		return trimTrailingFrom(string(quote(trimCharacter)), stringPrimary);
	}

	protected static TrimExpressionTester trimTrailingFrom(ExpressionTester stringPrimary) {
		return trimTrailingFrom(nullExpression(), stringPrimary);
	}

	protected static TrimExpressionTester trimTrailingFrom(ExpressionTester trimCharacter,
	                                                       ExpressionTester stringPrimary) {

		return trim(Specification.TRAILING, trimCharacter, true, stringPrimary);
	}

	protected static ExpressionTester TRUE() {
		return new KeywordExpressionTester(TRUE);
	}

	protected static TypeExpressionTester type(ExpressionTester identificationVariable) {
		return new TypeExpressionTester(identificationVariable);
	}

	protected static TypeExpressionTester type(String identificationVariable) {
		return type(variable(identificationVariable));
	}

	protected static UnionClauseTester union(ExpressionTester subquery) {
		return union(UNION, false, subquery);
	}

	protected static UnionClauseTester union(ExpressionTester selectClause,
	                                         ExpressionTester fromClause) {

		return union(subquery(selectClause, fromClause));
	}

	protected static UnionClauseTester union(ExpressionTester selectClause,
	                                         ExpressionTester fromClause,
	                                         ExpressionTester whereClause) {

		return union(subquery(selectClause, fromClause, whereClause));
	}

	protected static UnionClauseTester union(ExpressionTester selectClause,
	                                         ExpressionTester fromClause,
	                                         ExpressionTester whereClause,
	                                         ExpressionTester groupByClause,
	                                         ExpressionTester havingClause) {

		return union(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
	}

	protected static UnionClauseTester union(String identifier,
	                                         boolean hasAll,
	                                         ExpressionTester subquery) {

		return new UnionClauseTester(identifier, hasAll, subquery);
	}

	protected static UnionClauseTester unionAll(ExpressionTester subquery) {
		return union(UNION, true, subquery);
	}

	protected static UnionClauseTester unionAll(ExpressionTester selectClause,
	                                            ExpressionTester fromClause) {

		return unionAll(subquery(selectClause, fromClause));
	}

	protected static UnionClauseTester unionAll(ExpressionTester selectClause,
	                                            ExpressionTester fromClause,
		                                         ExpressionTester whereClause) {

		return unionAll(subquery(selectClause, fromClause, whereClause));
	}

	protected static UnionClauseTester unionAll(ExpressionTester selectClause,
	                                            ExpressionTester fromClause,
		                                         ExpressionTester whereClause,
		                                         ExpressionTester groupByClause,
		                                         ExpressionTester havingClause) {

		return unionAll(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
	}

	protected static UnknownExpressionTester unknown(String unknown) {
		return new UnknownExpressionTester(unknown);
	}

	protected static UpdateClauseTester update(ExpressionTester rangeVariableDeclaration,
	                                           ExpressionTester updateItem) {

		return new UpdateClauseTester(
			rangeVariableDeclaration,
			updateItem
		);
	}

	protected static UpdateClauseTester update(ExpressionTester rangeVariableDeclaration,
	                                           ExpressionTester... updateItems) {

		return new UpdateClauseTester(
			rangeVariableDeclaration,
			collection(updateItems)
		);
	}

	protected static UpdateClauseTester update(String abstractSchemaName,
	                                           ExpressionTester updateItem) {

		UpdateClauseTester updateClause = update(
			rangeVariableDeclaration(
				abstractSchemaName(abstractSchemaName),
				virtualVariable(abstractSchemaName.toLowerCase())
			),
			updateItem
		);
		updateClause.hasSpaceAfterRangeVariableDeclaration = false;
		return updateClause;
	}

	protected static UpdateClauseTester update(String abstractSchemaName,
	                                           ExpressionTester... updateItems) {

		UpdateClauseTester updateClause = update(
			rangeVariableDeclaration(
				abstractSchemaName(abstractSchemaName),
				virtualVariable(abstractSchemaName.toLowerCase())
			),
			updateItems
		);
		updateClause.hasSpaceAfterRangeVariableDeclaration = false;
		return updateClause;
	}

	protected static UpdateClauseTester update(String abstractSchemaName,
	                                           String identificationVariable,
	                                           ExpressionTester updateItem) {

		return new UpdateClauseTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			updateItem
		);
	}

	protected static UpdateClauseTester update(String abstractSchemaName,
	                                           String identificationVariable,
	                                           ExpressionTester... updateItems) {

		return new UpdateClauseTester(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			collection(updateItems)
		);
	}

	protected static UpdateClauseTester updateAs(String abstractSchemaName,
	                                             ExpressionTester updateItem) {

		return update(
			rangeVariableDeclarationAs(
				abstractSchemaName(abstractSchemaName),
				nullExpression()),
			updateItem
		);
	}

	protected static UpdateClauseTester updateAs(String abstractSchemaName,
	                                             String identificationVariable) {

		return update(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	protected static UpdateClauseTester updateAs(String abstractSchemaName,
	                                             String identificationVariable,
	                                             ExpressionTester updateItem) {

		return update(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			updateItem
		);
	}

	protected static UpdateClauseTester updateAs(String abstractSchemaName,
	                                             String identificationVariable,
	                                             ExpressionTester... updateItems) {

		return update(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			updateItems
		);
	}

	protected static UpdateStatementTester updateStatement(ExpressionTester updateClause) {
		return updateStatement(updateClause, nullExpression());
	}

	protected static UpdateStatementTester updateStatement(ExpressionTester updateClause,
	                                                       ExpressionTester whereClause) {

		return new UpdateStatementTester(updateClause, whereClause);
	}

	protected static UpperExpressionTester upper(ExpressionTester stringPrimary) {
		return new UpperExpressionTester(stringPrimary);
	}

	protected static IdentificationVariableTester variable(String identificationVariable) {
		if (identificationVariable.startsWith("{")) {
			return virtualVariable(identificationVariable);
		}
		return new IdentificationVariableTester(identificationVariable, false, nullExpression());
	}

	protected static IdentificationVariableTester virtualVariable(String identificationVariable) {
		if (identificationVariable.startsWith("{")) {
			identificationVariable = identificationVariable.substring(1, identificationVariable.length() - 1);
		}
		return new IdentificationVariableTester(identificationVariable, true, nullExpression());
	}

	protected static IdentificationVariableTester virtualVariable(String identificationVariable,
	                                                              String pathExpression) {

		StateFieldPathExpressionTester path = path(
			virtualVariable(identificationVariable),
			pathExpression
		);

		return new IdentificationVariableTester(pathExpression, true, path);
	}

	protected static WhenClauseTester when(ExpressionTester conditionalExpression,
	                                       ExpressionTester thenExpression) {

		return new WhenClauseTester(conditionalExpression, thenExpression);
	}

	protected static WhereClauseTester where(ExpressionTester conditionalExpression) {
		return new WhereClauseTester(conditionalExpression);
	}

	protected JPQLGrammar getGrammar() {
		return jpqlGrammar;
	}

	protected final boolean isEclipseLink2_0() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_0.VERSION;
	}

	protected final boolean isEclipseLink2_1() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_1.VERSION;
	}

	protected boolean isEclipseLink2_1OrLater() {
		return isEclipseLink2_1() ||
		       isEclipseLink2_2() ||
		       isEclipseLink2_3() ||
		       isEclipseLink2_4();
	}

	protected final boolean isEclipseLink2_2() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_2.VERSION;
	}

	protected final boolean isEclipseLink2_3() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_3.VERSION;
	}

	protected final boolean isEclipseLink2_4() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	protected boolean isEclipseLink2_4OrLater() {
		return isEclipseLink2_4();
	}

	protected final boolean isJPA1_0() {
		return jpqlGrammar.getJPAVersion() == JPAVersion.VERSION_1_0;
	}

	protected final boolean isJPA2_0() {
		return jpqlGrammar.getJPAVersion() == JPAVersion.VERSION_2_0;
	}

	protected final boolean isJPA2_1() {
		return jpqlGrammar.getJPAVersion() == JPAVersion.VERSION_2_1;
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned on.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 */
	protected void testInvalidQuery(String jpqlQuery, ExpressionTester expressionTester) {
		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, true);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned on.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param jpqlGrammar The {@link JPQLGrammar} used to determine how to parse the JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testInvalidQuery(String jpqlQuery,
	                                ExpressionTester expressionTester,
	                                JPQLGrammar jpqlGrammar) {

		testQuery(jpqlQuery, expressionTester, jpqlGrammar, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, true);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned on.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testInvalidQuery(String jpqlQuery,
	                                ExpressionTester expressionTester,
	                                JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, formatter, true);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned on.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 */
	protected void testInvalidQuery(String jpqlQuery,
	                                ExpressionTester expressionTester,
	                                String jpqlQueryBNFId) {

		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, JPQLQueryStringFormatter.DEFAULT, true);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned on.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testInvalidQuery(String jpqlQuery,
	                                ExpressionTester expressionTester,
	                                String jpqlQueryBNFId,
	                                JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, formatter, true);
	}

	/**
	 * Tests parsing the given JPQL query by comparing the parsed tree ({@link JPQLExpression}) with
	 * the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * This method test both parsing modes: tolerant and non-tolerant. When the tolerant mode is
	 * turned on, it means it will parse valid/complete and invalid/incomplete JPQL queries. When the
	 * tolerant mode is turned off, it means the JPQL query has to be complete and has to be
	 * grammatically valid.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 */
	protected void testQuery(String jpqlQuery, ExpressionTester expressionTester) {
		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, true);
		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, false);
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
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param jpqlGrammar The {@link JPQLGrammar} used to determine how to parse the JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         JPQLGrammar jpqlGrammar) {

		testQuery(jpqlQuery, expressionTester, jpqlGrammar, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, true);
		testQuery(jpqlQuery, expressionTester, jpqlGrammar, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, false);
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
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param jpqlGrammar The {@link JPQLGrammar} used to determine how to parse the JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         JPQLGrammar jpqlGrammar,
	                         JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, jpqlGrammar, JPQLStatementBNF.ID, formatter, true);
		testQuery(jpqlQuery, expressionTester, jpqlGrammar, JPQLStatementBNF.ID, formatter, false);
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
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         JPQLGrammar jpqlGrammar,
	                         String jpqlQueryBNFId,
	                         JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, jpqlGrammar, jpqlQueryBNFId, formatter, true);
		testQuery(jpqlQuery, expressionTester, jpqlGrammar, jpqlQueryBNFId, formatter, false);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param jpqlGrammar The {@link JPQLGrammar} used to determine how to parse the JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse grammatically invalid or incomplete queries
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         JPQLGrammar jpqlGrammar,
	                         String jpqlQueryBNFId,
	                         JPQLQueryStringFormatter formatter,
	                         boolean tolerant) {

		JPQLExpression jpqlExpression = buildQuery(jpqlQuery, jpqlGrammar, jpqlQueryBNFId, formatter, tolerant);

		if (expressionTester.getClass() != JPQLExpressionTester.class) {
			expressionTester = jpqlExpression(expressionTester);
		}

		expressionTester.test(jpqlExpression);
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
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, formatter, true);
		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, formatter, false);
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
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         String jpqlQueryBNFId) {

		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, JPQLQueryStringFormatter.DEFAULT, true);
		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, JPQLQueryStringFormatter.DEFAULT, false);
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
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         String jpqlQueryBNFId,
	                         JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, formatter, true);
		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, formatter, false);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse grammatically invalid or incomplete queries
	 */
	protected void testQuery(String jpqlQuery,
	                         ExpressionTester expressionTester,
	                         String jpqlQueryBNFId,
	                         JPQLQueryStringFormatter formatter,
	                         boolean tolerant) {

		testQuery(jpqlQuery, expressionTester, jpqlGrammar, jpqlQueryBNFId, formatter, tolerant);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned off.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 */
	protected void testValidQuery(String jpqlQuery, ExpressionTester expressionTester) {
		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, JPQLQueryStringFormatter.DEFAULT, false);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned off.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testValidQuery(String jpqlQuery,
	                              ExpressionTester expressionTester,
	                              JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, JPQLStatementBNF.ID, formatter, false);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned off.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 */
	protected void testValidQuery(String jpqlQuery,
	                              ExpressionTester expressionTester,
	                              String jpqlQueryBNFId) {

		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, JPQLQueryStringFormatter.DEFAULT, false);
	}

	/**
	 * Tests the parsing of the given JPQL query by comparing the parsed tree ({@link JPQLExpression})
	 * with the given tester, which is an equivalent representation of the parsed tree.
	 * <p>
	 * The parsing system will have the tolerance turned off.
	 *
	 * @param jpqlQuery The JPQL query to parse and to test the parsed tree representation
	 * @param expressionTester The tester used to verify the parsed tree is correctly representing the
	 * JPQL query
	 * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.
	 * JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 */
	protected void testValidQuery(String jpqlQuery,
	                              ExpressionTester expressionTester,
	                              String jpqlQueryBNFId,
	                              JPQLQueryStringFormatter formatter) {

		testQuery(jpqlQuery, expressionTester, jpqlQueryBNFId, formatter, false);
	}

	protected static final class AbsExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected AbsExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return AbsExpression.class;
		}

		@Override
		protected String identifier() {
			return ABS;
		}
	}

	protected static abstract class AbstractConditionalClauseTester extends AbstractExpressionTester {

		private ExpressionTester conditionalExpression;
		public boolean hasSpaceAfterIdentifier;

		protected AbstractConditionalClauseTester(ExpressionTester conditionalExpression) {
			super();
			this.conditionalExpression   = conditionalExpression;
			this.hasSpaceAfterIdentifier = !conditionalExpression.isNull();
		}

		protected abstract Class<? extends AbstractConditionalClause> expressionType();

		protected abstract String identifier();

		public void test(Expression expression) {

			assertInstance(expression, expressionType());

			AbstractConditionalClause conditionalClause = (AbstractConditionalClause) expression;
			assertEquals(toString(),                      conditionalClause.toParsedText());
			assertEquals(!conditionalExpression.isNull(), conditionalClause.hasConditionalExpression());
			assertEquals(hasSpaceAfterIdentifier,         conditionalClause.hasSpaceAfterIdentifier());

			conditionalExpression.test(conditionalClause.getConditionalExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());
			if (hasSpaceAfterIdentifier) {
				sb.append(SPACE);
			}
			sb.append(conditionalExpression);
			return sb.toString();
		}
	}

	protected static abstract class AbstractDoubleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {

		private ExpressionTester firstExpression;
		public boolean hasComma;
		public boolean hasSpaceAfterComma;
		private ExpressionTester secondExpression;

		protected AbstractDoubleEncapsulatedExpressionTester(ExpressionTester firstExpression,
		                                                     ExpressionTester secondExpression) {

			super();
			this.hasComma           = true;
			this.hasSpaceAfterComma = true;
			this.firstExpression    = firstExpression;
			this.secondExpression   = secondExpression;
		}

		@Override
		protected abstract Class<? extends AbstractDoubleEncapsulatedExpression> expressionType();

		@Override
		protected boolean hasEncapsulatedExpression() {
			return !firstExpression.isNull() || hasComma || !secondExpression.isNull();
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			AbstractDoubleEncapsulatedExpression abstractDoubleEncapsulatedExpression = (AbstractDoubleEncapsulatedExpression) expression;
			assertEquals(!firstExpression.isNull(),  abstractDoubleEncapsulatedExpression.hasFirstExpression());
			assertEquals(!secondExpression.isNull(), abstractDoubleEncapsulatedExpression.hasSecondExpression());
			assertEquals(hasComma,                   abstractDoubleEncapsulatedExpression.hasComma());
			assertEquals(hasSpaceAfterComma,         abstractDoubleEncapsulatedExpression.hasSpaceAfterComma());

			firstExpression .test(abstractDoubleEncapsulatedExpression.getFirstExpression());
			secondExpression.test(abstractDoubleEncapsulatedExpression.getSecondExpression());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(firstExpression);
			if (hasComma) {
				sb.append(COMMA);
			}
			if (hasSpaceAfterComma) {
				sb.append(SPACE);
			}
			sb.append(secondExpression);
		}
	}

	protected static abstract class AbstractEncapsulatedExpressionTester extends AbstractExpressionTester {

		public boolean hasLeftParenthesis;
		public boolean hasRightParenthesis;

		protected AbstractEncapsulatedExpressionTester() {
			super();
			this.hasLeftParenthesis  = true;
			this.hasRightParenthesis = true;
		}

		protected abstract Class<? extends AbstractEncapsulatedExpression> expressionType();

		protected abstract boolean hasEncapsulatedExpression();

		protected abstract String identifier();

		public void test(Expression expression) {
			assertInstance(expression, expressionType());

			AbstractEncapsulatedExpression abstractEncapsulatedExpression = (AbstractEncapsulatedExpression) expression;
			assertEquals(toString(),                  abstractEncapsulatedExpression.toParsedText());
			assertEquals(hasLeftParenthesis,          abstractEncapsulatedExpression.hasLeftParenthesis());
			assertEquals(hasRightParenthesis,         abstractEncapsulatedExpression.hasRightParenthesis());
			assertEquals(hasEncapsulatedExpression(), abstractEncapsulatedExpression.hasEncapsulatedExpression());
		}

		@Override
		public final String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());
			if (hasLeftParenthesis) {
				sb.append(LEFT_PARENTHESIS);
			}
			else if (hasEncapsulatedExpression()) {
				sb.append(SPACE);
			}
			toStringEncapsulatedExpression(sb);
			if (hasRightParenthesis) {
				sb.append(RIGHT_PARENTHESIS);
			}
			return sb.toString();
		}

		protected abstract void toStringEncapsulatedExpression(StringBuilder sb);
	}

	/**
	 * The abstract definition of an {@link ExpressionTester}.
	 */
	protected static abstract class AbstractExpressionTester implements ExpressionTester {

		public final AdditionExpressionTester add(ExpressionTester expression) {
			return JPQLParserTest.add(this, expression);
		}

		public final AndExpressionTester and(ExpressionTester expression) {
			return JPQLParserTest.and(this, expression);
		}

		protected final void assertInstance(Expression expression,
		                                    Class<? extends Expression> expressionType) {

			Class<? extends Expression> expressionClass = expression.getClass();

			if (expressionClass != expressionType &&
			   !expressionType.isAssignableFrom(expressionClass)) {

				StringBuilder sb = new StringBuilder();
				sb.append("Expecting ");
				sb.append(expressionType.getSimpleName());
				sb.append(" but was ");
				sb.append(expressionClass.getSimpleName());
				sb.append(" for [");
				sb.append(expression.toParsedText());
				sb.append("]");

				fail(sb.toString());
			}
		}

		public final BetweenExpressionTester between(ExpressionTester lowerBoundExpression,
		                                             ExpressionTester upperBoundExpression) {

			return JPQLParserTest.between(this, lowerBoundExpression, upperBoundExpression);
		}

		public final ComparisonExpressionTester different(ExpressionTester expression) {
			return JPQLParserTest.different(this, expression);
		}

		public final DivisionExpressionTester divide(ExpressionTester expression) {
			return JPQLParserTest.division(this, expression);
		}

		public final ComparisonExpressionTester equal(ExpressionTester expression) {
			return JPQLParserTest.equal(this, expression);
		}

		public final ComparisonExpressionTester greaterThan(ExpressionTester expression) {
			return JPQLParserTest.greaterThan(this, expression);
		}

		public final ComparisonExpressionTester greaterThanOrEqual(ExpressionTester expression) {
			return JPQLParserTest.greaterThanOrEqual(this, expression);
		}

		public final InExpressionTester in(ExpressionTester... inItems) {
			if (inItems.length == 1) {
				return JPQLParserTest.in(this, inItems[0]);
			}
			return JPQLParserTest.in(this, inItems);
		}

		public final InExpressionTester in(String inputParameter) {
			return JPQLParserTest.in(this, inputParameter);
		}

		public final EmptyCollectionComparisonExpressionTester isEmpty() {
			return JPQLParserTest.isEmpty(this);
		}

		public final EmptyCollectionComparisonExpressionTester isNotEmpty() {
			return JPQLParserTest.isNotEmpty(this);
		}

		public boolean isNull() {
			return false;
		}

		public final LikeExpressionTester like(ExpressionTester patternValue) {
			return JPQLParserTest.like(this, patternValue);
		}

		public final LikeExpressionTester like(ExpressionTester patternValue,
		                                       ExpressionTester escapeCharacter) {

			return JPQLParserTest.like(this, patternValue, escapeCharacter);
		}

		public final ComparisonExpressionTester lowerThan(ExpressionTester expression) {
			return JPQLParserTest.lowerThan(this, expression);
		}

		public final ComparisonExpressionTester lowerThanOrEqual(ExpressionTester expression) {
			return JPQLParserTest.lowerThanOrEqual(this, expression);
		}

		public final CollectionMemberExpressionTester member(ExpressionTester collectionPath) {
			return JPQLParserTest.member(this, collectionPath);
		}

		public final CollectionMemberExpressionTester memberOf(ExpressionTester collectionPath) {
			return JPQLParserTest.memberOf(this, collectionPath);
		}

		public final MultiplicationExpressionTester multiply(ExpressionTester expression) {
			return JPQLParserTest.multiplication(this, expression);
		}

		public final BetweenExpressionTester notBetween(ExpressionTester lowerBoundExpression,
		                                                ExpressionTester upperBoundExpression) {

			return JPQLParserTest.notBetween(this, lowerBoundExpression, upperBoundExpression);
		}

		public final ComparisonExpressionTester notEqual(ExpressionTester expression) {
			return JPQLParserTest.notEqual(this, expression);
		}

		public final InExpressionTester notIn(ExpressionTester... inItems) {
			if (inItems.length == 1) {
				return JPQLParserTest.notIn(this, inItems[0]);
			}
			return JPQLParserTest.notIn(this, inItems);
		}

		public final InExpressionTester notIn(String inputParameter) {
			return JPQLParserTest.notIn(this, inputParameter);
		}

		public final LikeExpressionTester notLike(ExpressionTester expression) {
			return JPQLParserTest.notLike(this, expression);
		}

		public final LikeExpressionTester notLike(ExpressionTester expression,
		                                          ExpressionTester escapeCharacter) {

			return JPQLParserTest.notLike(this, expression, escapeCharacter);
		}

		public final ExpressionTester notMember(ExpressionTester collectionPath) {
			return JPQLParserTest.notMember(this, collectionPath);
		}

		public final ExpressionTester notMemberOf(ExpressionTester collectionPath) {
			return JPQLParserTest.notMemberOf(this, collectionPath);
		}

		public final OrExpressionTester or(ExpressionTester expression) {
			return JPQLParserTest.or(this, expression);
		}

		public final RegexpExpressionTester regexp(StringLiteralTester patternValue) {
			return JPQLParserTest.regexp(this, patternValue);
		}

		public final SubtractionExpressionTester subtract(ExpressionTester expression) {
			return JPQLParserTest.subtract(this, expression);
		}
	}

	protected static abstract class AbstractFromClauseTester extends AbstractExpressionTester {

		private ExpressionTester declaration;
		public boolean hasSpaceAfterFrom;

		protected AbstractFromClauseTester(ExpressionTester declaration) {
			super();
			this.hasSpaceAfterFrom = true;
			this.declaration       = declaration;
		}

		public void test(Expression expression) {

			assertInstance(expression, AbstractFromClause.class);

			AbstractFromClause fromClause = (AbstractFromClause) expression;
			assertEquals(toString(),            fromClause.toParsedText());
			assertEquals(!declaration.isNull(), fromClause.hasDeclaration());
			assertEquals(hasSpaceAfterFrom,     fromClause.hasSpaceAfterFrom());

			declaration.test(fromClause.getDeclaration());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(FROM);
			if (hasSpaceAfterFrom) {
				sb.append(SPACE);
			}
			sb.append(declaration);
			return sb.toString();
		}
	}

	protected static abstract class AbstractPathExpressionTester extends AbstractExpressionTester {

		private boolean endsWithDot;
		private ExpressionTester identificationVariable;
		private boolean startsWithDot;
		private String value;

		protected AbstractPathExpressionTester(ExpressionTester identificationVariable, String value) {

			super();

			this.value                  = value;
			this.identificationVariable = identificationVariable;
			this.startsWithDot          = (identificationVariable.toString().length() == 0) && (value.indexOf(DOT) > -1);

			if (value.length() > 1) {
				endsWithDot = value.charAt(value.length() - 1) == DOT;
			}
		}

		public void test(Expression expression) {

			assertInstance(expression, AbstractPathExpression.class);

			AbstractPathExpression abstractPathExpression = (AbstractPathExpression) expression;
			assertEquals(toString(),                       abstractPathExpression.toParsedText());
			assertEquals(!identificationVariable.isNull(), abstractPathExpression.hasIdentificationVariable());
			assertEquals(endsWithDot,                      abstractPathExpression.endsWithDot());
			assertEquals(startsWithDot,                    abstractPathExpression.startsWithDot());

			identificationVariable.test(abstractPathExpression.getIdentificationVariable());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identificationVariable);
			if (startsWithDot || (sb.length() > 0)) {
				sb.append(DOT);
			}
			sb.append(value);
			return sb.toString();
		}
	}

	protected static final class AbstractSchemaNameTester extends AbstractExpressionTester {

		private String abstractSchemaName;

		protected AbstractSchemaNameTester(String abstractSchemaName) {
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

	protected static abstract class AbstractSelectClauseTester extends AbstractExpressionTester {

		private boolean hasDistinct;
		public boolean hasSpaceAfterDistinct;
		public boolean hasSpaceAfterSelect;
		private ExpressionTester selectExpression;

		protected AbstractSelectClauseTester(ExpressionTester selectExpression, boolean hasDistinct) {
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
				sb.append(SPACE);
			}
			if (hasDistinct) {
				sb.append(DISTINCT);
			}
			if (hasSpaceAfterDistinct) {
				sb.append(SPACE);
			}
			sb.append(selectExpression);
			return sb.toString();
		}
	}

	protected static abstract class AbstractSelectStatementTester extends AbstractExpressionTester {

		private ExpressionTester fromClause;
		private ExpressionTester groupByClause;
		public boolean hasSpaceAfterFrom;
		public boolean hasSpaceAfterGroupBy;
		public boolean hasSpaceAfterSelect;
		public boolean hasSpaceAfterWhere;
		private ExpressionTester havingClause;
		private ExpressionTester selectClause;
		private ExpressionTester whereClause;

		protected AbstractSelectStatementTester(ExpressionTester selectClause,
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

		protected abstract Class<? extends AbstractSelectStatement> expressionType();

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
				sb.append(SPACE);
			}

			sb.append(fromClause);

			if (hasSpaceAfterFrom && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(SPACE);
			}

			sb.append(whereClause);

			if (hasSpaceAfterWhere && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(SPACE);
			}

			sb.append(groupByClause);

			if (hasSpaceAfterGroupBy && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(SPACE);
			}

			sb.append(havingClause);
			return sb.toString();
		}
	}

	protected static abstract class AbstractSingleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {

		private ExpressionTester expression;

		protected AbstractSingleEncapsulatedExpressionTester(ExpressionTester expression) {
			super();
			this.expression = expression;
		}

		@Override
		protected abstract Class<? extends AbstractSingleEncapsulatedExpression> expressionType();

		@Override
		protected boolean hasEncapsulatedExpression() {
			return !expression.isNull();
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			AbstractSingleEncapsulatedExpression encapsulatedExpression = (AbstractSingleEncapsulatedExpression) expression;
			this.expression.test(encapsulatedExpression.getExpression());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(expression);
		}
	}

	protected static abstract class AbstractTripleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {

		private ExpressionTester firstExpression;
		public boolean hasFirstComma;
		public boolean hasSecondComma;
		public boolean hasSpaceAfterFirstComma;
		public boolean hasSpaceAfterSecondComma;
		private ExpressionTester secondExpression;
		private ExpressionTester thirdExpression;

		protected AbstractTripleEncapsulatedExpressionTester(ExpressionTester firstExpression,
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
		protected abstract Class<? extends AbstractTripleEncapsulatedExpression> expressionType();

		@Override
		protected boolean hasEncapsulatedExpression() {
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
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(firstExpression);
			if (hasFirstComma) {
				sb.append(COMMA);
			}
			if (hasSpaceAfterFirstComma) {
				sb.append(SPACE);
			}
			sb.append(secondExpression);
			if (hasSecondComma) {
				sb.append(COMMA);
			}
			if (hasSpaceAfterSecondComma) {
				sb.append(SPACE);
			}
			sb.append(thirdExpression);
		}
	}

	protected static final class AdditionExpressionTester extends CompoundExpressionTester {

		protected AdditionExpressionTester(ExpressionTester leftExpression,
		                                   ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpression> expressionType() {
			return AdditionExpression.class;
		}

		@Override
		protected String identifier() {
			return PLUS;
		}
	}

	protected static abstract class AggregateFunctionTester extends AbstractSingleEncapsulatedExpressionTester {

		public boolean hasDistinct;
		public boolean hasSpaceAfterDistinct;

		protected AggregateFunctionTester(ExpressionTester expression, boolean hasDistinct) {
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

	protected static final class AllOrAnyExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private String identifier;

		protected AllOrAnyExpressionTester(String identifier, ExpressionTester expression) {
			super(expression);
			this.identifier = identifier;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return AllOrAnyExpression.class;
		}

		@Override
		protected String identifier() {
			return identifier;
		}
	}

	protected static final class AndExpressionTester extends LogicalExpressionTester {

		protected AndExpressionTester(ExpressionTester leftExpression,
		                              ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpression> expressionType() {
			return AndExpression.class;
		}

		@Override
		protected String identifier() {
			return AND;
		}
	}

	protected static final class ArithmeticFactorTester extends AbstractExpressionTester {

		private ExpressionTester expression;
		private String sign;

		protected ArithmeticFactorTester(String sign, ExpressionTester expression) {
			super();
			this.sign = sign;
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, ArithmeticFactor.class);

			ArithmeticFactor factor = (ArithmeticFactor) expression;
			assertEquals(toString(),    factor.toParsedText());
			assertEquals(sign == MINUS, factor.isMinusSign());
			assertEquals(sign == PLUS,  factor.isPlusSign());

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

	protected static final class AvgFunctionTester extends AggregateFunctionTester {

		protected AvgFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return AvgFunction.class;
		}

		@Override
		protected String identifier() {
			return AVG;
		}
	}

	protected static final class BadExpressionTester extends AbstractExpressionTester {

		private ExpressionTester expression;

		protected BadExpressionTester(ExpressionTester expression) {
			super();
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, BadExpression.class);

			BadExpression badExpression = (BadExpression) expression;
			assertEquals(toString(), badExpression.toParsedText());

			this.expression.test(badExpression.getExpression());
		}

		@Override
		public String toString() {
			return expression.toString();
		}
	}

	protected static final class BetweenExpressionTester extends AbstractExpressionTester {

		private ExpressionTester expression;
		public boolean hasAnd;
		private boolean hasNot;
		public boolean hasSpaceAfterAnd;
		public boolean hasSpaceAfterBetween;
		public boolean hasSpaceAfterLowerBound;
		private ExpressionTester lowerBoundExpression;
		private ExpressionTester upperBoundExpression;

		protected BetweenExpressionTester(ExpressionTester expression,
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
				sb.append(SPACE);
			}
			if (hasNot) {
				sb.append(NOT);
				sb.append(SPACE);
			}
			sb.append(BETWEEN);
			if (hasSpaceAfterBetween) {
				sb.append(SPACE);
			}
			sb.append(lowerBoundExpression);
			if (hasSpaceAfterLowerBound) {
				sb.append(SPACE);
			}
			if (hasAnd) {
				sb.append(AND);
			}
			if (hasSpaceAfterAnd) {
				sb.append(SPACE);
			}
			sb.append(upperBoundExpression);
			return sb.toString();
		}
	}

	protected static final class CaseExpressionTester extends AbstractExpressionTester {

		private ExpressionTester caseOperand;
		private ExpressionTester elseExpression;
		public boolean hasElse;
		public boolean hasEnd;
		public boolean hasSpaceAfterCase;
		public boolean hasSpaceAfterCaseOperand;
		public boolean hasSpaceAfterElse;
		public boolean hasSpaceAfterElseExpression;
		public boolean hasSpaceAfterWhenClauses;
		private ExpressionTester whenClauses;

		protected CaseExpressionTester(ExpressionTester caseOperand,
		                               ExpressionTester whenClauses,
		                               ExpressionTester elseExpression) {

			super();
			this.whenClauses                 = whenClauses;
			this.caseOperand                 = caseOperand;
			this.elseExpression              = elseExpression;
			this.hasElse                     = !elseExpression.isNull();
			this.hasEnd                      = true;
			this.hasSpaceAfterCase           = true;
			this.hasSpaceAfterElse           = hasElse;
			this.hasSpaceAfterElseExpression = hasElse;
			this.hasSpaceAfterCaseOperand    = !caseOperand.isNull();
			this.hasSpaceAfterWhenClauses    = !whenClauses.isNull();
		}

		public void test(Expression expression) {

			assertInstance(expression, CaseExpression.class);

			CaseExpression caseExpression = (CaseExpression) expression;
			assertEquals(toString(),                  caseExpression.toParsedText());
			assertEquals(!caseOperand.isNull(),       caseExpression.hasCaseOperand());
			assertEquals(hasElse,                     caseExpression.hasElse());
			assertEquals(hasEnd,                      caseExpression.hasEnd());
			assertEquals(hasSpaceAfterCase,           caseExpression.hasSpaceAfterCase());
			assertEquals(hasSpaceAfterCaseOperand,    caseExpression.hasSpaceAfterCaseOperand());
			assertEquals(hasSpaceAfterElse,           caseExpression.hasSpaceAfterElse());
			assertEquals(hasSpaceAfterElseExpression, caseExpression.hasSpaceAfterElseExpression());
			assertEquals(hasSpaceAfterWhenClauses,    caseExpression.hasSpaceAfterWhenClauses());
			assertEquals(!whenClauses.isNull(),       caseExpression.hasWhenClauses());

			caseOperand.test(caseExpression.getCaseOperand());
			whenClauses.test(caseExpression.getWhenClauses());
			elseExpression.test(caseExpression.getElseExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(CASE);
			if (hasSpaceAfterCase) {
				sb.append(SPACE);
			}
			sb.append(caseOperand);
			if (hasSpaceAfterCaseOperand) {
				sb.append(SPACE);
			}
			sb.append(whenClauses);
			if (hasSpaceAfterWhenClauses) {
				sb.append(SPACE);
			}
			if (hasElse) {
				sb.append(ELSE);
			}
			if (hasSpaceAfterElse) {
				sb.append(SPACE);
			}
			sb.append(elseExpression);
			if (hasSpaceAfterElseExpression) {
				sb.append(SPACE);
			}
			if (hasEnd) {
				sb.append(END);
			}
			return sb.toString();
		}
	}

	protected static class CastExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private ExpressionTester databaseType;
		private boolean hasAs;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterExpression;

		protected CastExpressionTester(ExpressionTester expression,
		                               boolean hasAs,
		                               ExpressionTester databaseType) {

			super(expression);
			this.hasAs                   = hasAs;
			this.databaseType            = databaseType;
			this.hasSpaceAfterAs         = hasAs && !databaseType.isNull();
			this.hasSpaceAfterExpression = !expression.isNull() && (hasAs || !databaseType.isNull());
		}

		@Override
		protected Class<CastExpression> expressionType() {
			return CastExpression.class;
		}

		@Override
		protected boolean hasEncapsulatedExpression() {
			return super.hasEncapsulatedExpression() || hasAs || !databaseType.isNull();
		}

		@Override
		protected String identifier() {
			return CAST;
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			super.toStringEncapsulatedExpression(sb);
			if (hasSpaceAfterExpression) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(databaseType);
		}
	}

	protected static class CoalesceExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected CoalesceExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return CoalesceExpression.class;
		}

		@Override
		protected String identifier(){
			return COALESCE;
		}
	}

	protected static final class CollectionExpressionTester extends AbstractExpressionTester {

		public Boolean[] commas;
		private ExpressionTester[] expressionTesters;
		public Boolean[] spaces;

		protected CollectionExpressionTester(ExpressionTester[] expressionTesters,
		                                     Boolean[] commas,
		                                     Boolean[] spaces) {

			super();
			this.expressionTesters = expressionTesters;
			this.spaces            = spaces;
			this.commas            = commas;
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
					sb.append(COMMA);
				}

				if ((index < spaces.length) && spaces[index]) {
					sb.append(SPACE);
				}
			}

			return sb.toString();
		}
	}

	protected static final class CollectionMemberDeclarationTester extends AbstractExpressionTester {

		private ExpressionTester collectionValuedPath;
		public boolean hasAs;
		public boolean hasLeftParenthesis;
		public boolean hasRightParenthesis;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterIn;
		public boolean hasSpaceAfterRightParenthesis;
		private ExpressionTester identificationVariable;

		protected CollectionMemberDeclarationTester(ExpressionTester collectionValuedPath,
		                                            boolean hasAs,
		                                            ExpressionTester identificationVariable) {

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
				sb.append(SPACE);
			}
			else if (hasLeftParenthesis) {
				sb.append(LEFT_PARENTHESIS);
			}
			sb.append(collectionValuedPath);
			if (hasRightParenthesis) {
				sb.append(RIGHT_PARENTHESIS);
			}
			if (hasSpaceAfterRightParenthesis) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	protected static final class CollectionMemberExpressionTester extends AbstractExpressionTester {

		private ExpressionTester collectionPath;
		private ExpressionTester entityExpression;
		private boolean hasNot;
		private boolean hasOf;
		public boolean hasSpaceAfterMember;
		public boolean hasSpaceAfterOf;

		protected CollectionMemberExpressionTester(ExpressionTester entityExpression,
		                                           boolean hasNot,
		                                           boolean hasOf,
		                                           ExpressionTester collectionPath) {

			super();
			this.hasNot                         = hasNot;
			this.hasOf                          = hasOf;
			this.hasSpaceAfterMember            = true;
			this.hasSpaceAfterOf                = hasOf;
			this.entityExpression               = entityExpression;
			this.collectionPath = collectionPath;
		}

		public void test(Expression expression) {
			assertInstance(expression, CollectionMemberExpression.class);

			CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;
			assertEquals(toString(),                               collectionMemberExpression.toParsedText());
			assertEquals(!collectionPath.isNull(), collectionMemberExpression.hasCollectionValuedPathExpression());
			assertEquals(!entityExpression.isNull(),               collectionMemberExpression.hasEntityExpression());
			assertEquals(hasNot,                                   collectionMemberExpression.hasNot());
			assertEquals(hasSpaceAfterMember,                      collectionMemberExpression.hasSpaceAfterMember());
			assertEquals(hasOf,                                    collectionMemberExpression.hasOf());
			assertEquals(hasSpaceAfterOf,                          collectionMemberExpression.hasSpaceAfterOf());

			entityExpression.test(collectionMemberExpression.getEntityExpression());
			collectionPath.test(collectionMemberExpression.getCollectionValuedPathExpression());
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
				if (hasSpaceAfterOf) {
					sb.append(SPACE);
				}
			}
			else {
				sb.append(MEMBER);
				if (hasSpaceAfterMember) {
					sb.append(SPACE);
				}
			}
			sb.append(collectionPath);
			return sb.toString();
		}
	}

	protected static final class CollectionValuedPathExpressionTester extends AbstractPathExpressionTester {

		protected CollectionValuedPathExpressionTester(ExpressionTester identificationVariable,
		                                               String value) {

			super(identificationVariable, value);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, CollectionValuedPathExpression.class);
		}
	}

	protected static final class ComparisonExpressionTester extends AbstractExpressionTester {

		private String comparator;
		public boolean hasSpaceAfterIdentifier;
		private ExpressionTester leftExpression;
		private ExpressionTester rightExpression;

		protected ComparisonExpressionTester(String comparator,
		                                     ExpressionTester leftExpression,
		                                     ExpressionTester rightExpression) {

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
				sb.append(SPACE);
			}
			sb.append(comparator);
			if (hasSpaceAfterIdentifier) {
				sb.append(SPACE);
			}
			sb.append(rightExpression);
			return sb.toString();
		}
	}

	protected static abstract class CompoundExpressionTester extends AbstractExpressionTester {

		public boolean hasSpaceAfterIdentifier;
		private ExpressionTester leftExpression;
		private ExpressionTester rightExpression;

		protected CompoundExpressionTester(ExpressionTester leftExpression,
		                                   ExpressionTester rightExpression) {

			super();
			this.leftExpression  = leftExpression;
			this.rightExpression = rightExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		protected abstract Class<? extends CompoundExpression> expressionType();

		protected abstract String identifier();

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
				sb.append(SPACE);
			}

			sb.append(identifier());

			if (hasSpaceAfterIdentifier) {
				sb.append(SPACE);
			}

			sb.append(rightExpression);
			return sb.toString();
		}
	}

	protected static final class ConcatExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected ConcatExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return ConcatExpression.class;
		}

		@Override
		protected String identifier() {
			return CONCAT;
		}
	}

	protected static final class ConstructorExpressionTester extends AbstractExpressionTester {

		private String className;
		private ExpressionTester constructorItems;
		public boolean hasLeftParenthesis;
		public boolean hasRightParenthesis;
		public boolean hasSpaceAfterNew;

		protected ConstructorExpressionTester(String className, ExpressionTester constructorItems) {
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
				sb.append(SPACE);
			}
			sb.append(className);
			if (hasLeftParenthesis) {
				sb.append(LEFT_PARENTHESIS);
			}
			sb.append(constructorItems);
			if (hasRightParenthesis) {
				sb.append(RIGHT_PARENTHESIS);
			}
			return sb.toString();
		}
	}

	protected static final class CountFunctionTester extends AggregateFunctionTester {

		protected CountFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return CountFunction.class;
		}

		@Override
		protected String identifier() {
			return COUNT;
		}
	}

	protected static class DatabaseTypeTester extends AbstractDoubleEncapsulatedExpressionTester {

		private String databaseType;

		protected DatabaseTypeTester(String databaseType,
		                             ExpressionTester size,
		                             ExpressionTester precision) {

			super(size, precision);
			this.databaseType = databaseType;
		}

		@Override
		protected Class<DatabaseType> expressionType() {
			return DatabaseType.class;
		}

		@Override
		protected String identifier() {
			return databaseType;
		}
	}

	protected static final class DateTimeTester extends AbstractExpressionTester {

		private String dateTime;

		protected DateTimeTester(String dateTime) {
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

	/**
	 * Tester for {@link DeleteClause}.
	 */
	protected static final class DeleteClauseTester extends AbstractExpressionTester {

		public boolean hasFrom;
		public boolean hasSpaceAfterDelete;
		public boolean hasSpaceAfterFrom;
		private ExpressionTester rangeVariableDeclaration;

		protected DeleteClauseTester(ExpressionTester rangeVariableDeclaration) {
			super();
			this.hasFrom                  = true;
			this.hasSpaceAfterFrom        = true;
			this.hasSpaceAfterDelete      = true;
			this.rangeVariableDeclaration = rangeVariableDeclaration;
		}

		public void test(Expression expression) {

			assertInstance(expression, DeleteClause.class);

			DeleteClause deleteClause = (DeleteClause) expression;
			assertEquals(toString(),                         deleteClause.toParsedText());
			assertEquals(hasSpaceAfterDelete,                deleteClause.hasSpaceAfterDelete());
			assertEquals(hasFrom,                            deleteClause.hasFrom());
			assertEquals(hasSpaceAfterFrom,                  deleteClause.hasSpaceAfterFrom());
			assertEquals(!rangeVariableDeclaration.isNull(), deleteClause.hasRangeVariableDeclaration());

			rangeVariableDeclaration.test(deleteClause.getRangeVariableDeclaration());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(DELETE);
			if (hasSpaceAfterDelete) {
				sb.append(SPACE);
			}
			if (hasFrom) {
				sb.append(FROM);
			}
			if (hasSpaceAfterFrom) {
				sb.append(SPACE);
			}
			sb.append(rangeVariableDeclaration);
			return sb.toString();
		}
	}

	protected static final class DeleteStatementTester extends AbstractExpressionTester {

		private ExpressionTester deleteClause;
		public boolean hasSpaceAfterDeleteClause;
		private ExpressionTester whereClause;

		protected DeleteStatementTester(ExpressionTester deleteClause, ExpressionTester whereClause) {
			super();
			this.deleteClause              = deleteClause;
			this.whereClause               = whereClause;
			this.hasSpaceAfterDeleteClause = !whereClause.isNull();
		}

		public void test(Expression expression) {

			assertInstance(expression, DeleteStatement.class);

			DeleteStatement deleteStatement = (DeleteStatement) expression;
			assertEquals(toString(),                deleteStatement.toParsedText());
			assertEquals(hasSpaceAfterDeleteClause, deleteStatement.hasSpaceAfterDeleteClause());
			assertEquals(!whereClause.isNull(),     deleteStatement.hasWhereClause());

			deleteClause.test(deleteStatement.getDeleteClause());
			whereClause .test(deleteStatement.getWhereClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(deleteClause);
			if (hasSpaceAfterDeleteClause) {
				sb.append(SPACE);
			}
			sb.append(whereClause);
			return sb.toString();
		}
	}

	protected static final class DivisionExpressionTester extends CompoundExpressionTester {

		protected DivisionExpressionTester(ExpressionTester leftExpression,
		                                   ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpression> expressionType() {
			return DivisionExpression.class;
		}

		@Override
		protected String identifier() {
			return DIVISION;
		}
	}

	protected static final class EmptyCollectionComparisonExpressionTester extends AbstractExpressionTester {

		private ExpressionTester collectionPath;
		private boolean hasNot;
		public boolean hasSpaceAfterIs;

		protected EmptyCollectionComparisonExpressionTester(ExpressionTester collectionPath,
		                                                    boolean hasNot) {

			super();
			this.hasNot = hasNot;
			this.hasSpaceAfterIs = true;
			this.collectionPath = collectionPath;
		}

		public void test(Expression expression) {
			assertInstance(expression, EmptyCollectionComparisonExpression.class);

			EmptyCollectionComparisonExpression emptyCollection = (EmptyCollectionComparisonExpression) expression;
			assertEquals(toString(),                               emptyCollection.toParsedText());
			assertEquals(!collectionPath.isNull(), emptyCollection.hasExpression());
			assertEquals(hasNot,                                   emptyCollection.hasNot());
			assertEquals(hasSpaceAfterIs,                          emptyCollection.hasSpaceAfterIs());

			collectionPath.test(emptyCollection.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(collectionPath);
			if (!collectionPath.isNull()) {
				sb.append(SPACE);
			}
			sb.append(IS);
			if (hasSpaceAfterIs) {
				sb.append(SPACE);
			}
			if (hasNot) {
				sb.append(NOT);
				sb.append(SPACE);
			}
			sb.append(EMPTY);
			return sb.toString();
		}
	}

	protected static final class EntityTypeLiteralTester extends AbstractExpressionTester {

		private String entityType;

		protected EntityTypeLiteralTester(String entityType) {
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

	protected static class EntryExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected EntryExpressionTester(ExpressionTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return EntryExpression.class;
		}

		@Override
		protected String identifier() {
			return ENTRY;
		}
	}

	protected static final class ExistsExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private boolean hasNot;

		protected ExistsExpressionTester(ExpressionTester subquery, boolean hasNot) {
			super(subquery);
			this.hasNot = hasNot;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return ExistsExpression.class;
		}

		@Override
		protected String identifier() {
			return hasNot ? NOT_EXISTS : EXISTS;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, ExistsExpression.class);

			ExistsExpression existsExpression = (ExistsExpression) expression;
			assertEquals(hasNot, existsExpression.hasNot());
		}
	}

	/**
	 * This tester tests an {@link Expression} information to make sure it correctly parsed a section
	 * of the query. This interface also adds helper method for easily creating a parsed tree
	 * representation of the actual query parsed tree.
	 */
	protected static interface ExpressionTester {

		AdditionExpressionTester add(ExpressionTester expression);
		AndExpressionTester and(ExpressionTester expression);
		BetweenExpressionTester between(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression);
		ComparisonExpressionTester different(ExpressionTester expression);
		DivisionExpressionTester divide(ExpressionTester expression);
		ComparisonExpressionTester equal(ExpressionTester expression);
		ComparisonExpressionTester greaterThan(ExpressionTester expression);
		ComparisonExpressionTester greaterThanOrEqual(ExpressionTester expression);
		InExpressionTester in(ExpressionTester... inItems);
		InExpressionTester in(String inputParameter);
		EmptyCollectionComparisonExpressionTester isEmpty();
		EmptyCollectionComparisonExpressionTester isNotEmpty();

		/**
		 * Determines whether this tester represents the {@link NullExpression}.
		 *
		 * @return <code>true</code> if this tester represents a <code>null</code> object;
		 * <code>false</code> otherwise
		 */
		boolean isNull();

		LikeExpressionTester like(ExpressionTester patternValue);
		LikeExpressionTester like(ExpressionTester patternValue, ExpressionTester escapeCharacter);
		ComparisonExpressionTester lowerThan(ExpressionTester expression);
		ComparisonExpressionTester lowerThanOrEqual(ExpressionTester expression);
		ExpressionTester member(ExpressionTester collectionPath);
		ExpressionTester memberOf(ExpressionTester collectionPath);
		MultiplicationExpressionTester multiply(ExpressionTester expression);
		BetweenExpressionTester notBetween(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression);
		ComparisonExpressionTester notEqual(ExpressionTester expression);
		InExpressionTester notIn(ExpressionTester... inItems);
		InExpressionTester notIn(String inputParameter);
		LikeExpressionTester notLike(ExpressionTester expression);
		LikeExpressionTester notLike(ExpressionTester expression, ExpressionTester escapeCharacter);
		ExpressionTester notMember(ExpressionTester collectionPath);
		ExpressionTester notMemberOf(ExpressionTester collectionPath);
		OrExpressionTester or(ExpressionTester expression);
		RegexpExpressionTester regexp(StringLiteralTester patternValue);
		SubtractionExpressionTester subtract(ExpressionTester expression);

		/**
		 * Tests the given {@link Expression} internal data.
		 */
		void test(Expression expression);
	}

	protected static final class ExtractExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private boolean hasFrom;
		public boolean hasSpaceAfterFrom;
		public boolean hasSpaceAfterPart;
		private String part;

		protected ExtractExpressionTester(String part, boolean hasFom, ExpressionTester expression) {
			super(expression);
			this.hasFrom           = hasFom;
			this.part              = (part != null) ? part : ExpressionTools.EMPTY_STRING;
			this.hasSpaceAfterPart = (part != null) && (hasFrom || !expression.isNull());
			this.hasSpaceAfterFrom = hasFom && !expression.isNull();
		}

		@Override
		protected Class<ExtractExpression> expressionType() {
			return ExtractExpression.class;
		}

		@Override
		protected boolean hasEncapsulatedExpression() {
			return super.hasEncapsulatedExpression() || hasFrom || ExpressionTools.stringIsNotEmpty(part);
		}

		@Override
		protected String identifier() {
			return EXTRACT;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			ExtractExpression extractExpression = (ExtractExpression) expression;
			assertEquals(part,              extractExpression.getDatePart());
			assertEquals(hasFrom,           extractExpression.hasFrom());
			assertSame  (hasSpaceAfterFrom, extractExpression.hasSpaceAfterFrom());
			assertSame  (hasSpaceAfterPart, extractExpression.hasSpaceAfterDatePart());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {

			sb.append(part);

			if (hasSpaceAfterPart) {
				sb.append(SPACE);
			}

			if (hasFrom) {
				sb.append(FROM);
			}

			if (hasSpaceAfterFrom) {
				sb.append(SPACE);
			}

			super.toStringEncapsulatedExpression(sb);
		}
	}

	protected static final class FromClauseTester extends AbstractFromClauseTester {

		protected FromClauseTester(ExpressionTester declarations) {
			super(declarations);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, FromClause.class);
		}
	}

	protected static final class FunctionExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private String functionName;
		public boolean hasComma;
		public boolean hasSpaceAfterComma;
		private String identifier;

		protected FunctionExpressionTester(String identifier,
		                                   String functionName,
		                                   ExpressionTester funcItems) {

			super(funcItems);
			this.identifier = identifier;
			this.functionName = functionName;
			this.hasSpaceAfterComma = !funcItems.isNull();
			this.hasComma = !funcItems.isNull();
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return FunctionExpression.class;
		}

		@Override
		protected boolean hasEncapsulatedExpression() {
			return functionName.length() > 0 ||
			       hasComma                  ||
			       hasSpaceAfterComma        ||
			       super.hasEncapsulatedExpression();
		}

		@Override
		protected String identifier() {
			return identifier;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			FunctionExpression funcExpression = (FunctionExpression) expression;
			assertEquals(identifier,         funcExpression.getIdentifier());
			assertEquals(functionName,       funcExpression.getFunctionName());
			assertEquals(hasComma,           funcExpression.hasComma());
			assertEquals(hasSpaceAfterComma, funcExpression.hasSpaceAfterComma());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			if (functionName != null) {
				sb.append(functionName);
			}
			if (hasComma) {
				sb.append(COMMA);
			}
			if (hasSpaceAfterComma) {
				sb.append(SPACE);
			}
			super.toStringEncapsulatedExpression(sb);
		}
	}

	protected static final class GroupByClauseTester extends AbstractExpressionTester {

		private ExpressionTester groupByItems;
		public boolean hasSpaceAfterGroupBy;

		protected GroupByClauseTester(ExpressionTester groupByItems) {
			super();
			this.groupByItems         = groupByItems;
			this.hasSpaceAfterGroupBy = !groupByItems.isNull();
		}

		public void test(Expression expression) {

			assertInstance(expression, GroupByClause.class);

			GroupByClause groupByClause = (GroupByClause) expression;
			assertEquals(toString(),             groupByClause.toParsedText());
			assertEquals(!groupByItems.isNull(), groupByClause.hasGroupByItems());
			assertEquals(hasSpaceAfterGroupBy,   groupByClause.hasSpaceAfterGroupBy());

			groupByItems.test(groupByClause.getGroupByItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(GROUP_BY);
			if (hasSpaceAfterGroupBy) {
				sb.append(SPACE);
			}
			sb.append(groupByItems);
			return sb.toString();
		}
	}

	protected static final class HavingClauseTester extends AbstractConditionalClauseTester {

		protected HavingClauseTester(ExpressionTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		protected Class<? extends AbstractConditionalClause> expressionType() {
			return HavingClause.class;
		}

		@Override
		protected String identifier() {
			return HAVING;
		}
	}

	protected static final class IdentificationVariableDeclarationTester extends AbstractExpressionTester {

		public boolean hasSpace;
		private ExpressionTester joins;
		private ExpressionTester rangeVariableDeclaration;

		protected IdentificationVariableDeclarationTester(ExpressionTester rangeVariableDeclaration,
		                                                  ExpressionTester joins) {

			super();
			this.rangeVariableDeclaration = rangeVariableDeclaration;
			this.hasSpace                 = !joins.isNull();
			this.joins                    = joins;
		}

		public void test(Expression expression) {

			assertInstance(expression, IdentificationVariableDeclaration.class);

			IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;
			assertEquals(toString(),                         identificationVariableDeclaration.toParsedText());
			assertEquals(!rangeVariableDeclaration.isNull(), identificationVariableDeclaration.hasRangeVariableDeclaration());
			assertEquals(hasSpace,                           identificationVariableDeclaration.hasSpace());
			assertEquals(!joins.isNull(),                    identificationVariableDeclaration.hasJoins());

			rangeVariableDeclaration.test(identificationVariableDeclaration.getRangeVariableDeclaration());
			joins.test(identificationVariableDeclaration.getJoins());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(rangeVariableDeclaration);
			if (hasSpace) {
				sb.append(SPACE);
			}
			sb.append(joins);
			return sb.toString();
		}
	}

	protected static final class IdentificationVariableTester extends AbstractExpressionTester {

		private String identificationVariable;
		private boolean virtual;
		private ExpressionTester virtualPathExpression;

		protected IdentificationVariableTester(String identificationVariable,
		                                       boolean virtual,
		                                       ExpressionTester virtualPathExpression) {
			super();
			this.virtual                = virtual;
			this.identificationVariable = identificationVariable;
			this.virtualPathExpression  = virtualPathExpression;
		}

		@Override
		public boolean isNull() {
			return virtual && virtualPathExpression.isNull();
		}

		public void test(Expression expression) {
			assertInstance(expression, IdentificationVariable.class);

			IdentificationVariable identificationVariable = (IdentificationVariable) expression;
			assertEquals(this.identificationVariable, identificationVariable.toParsedText());
			assertEquals(virtual,                     identificationVariable.isVirtual());

			if (virtual) {
				StateFieldPathExpression pathExpression = identificationVariable.getStateFieldPathExpression();
				assertEquals(virtualPathExpression.isNull(), pathExpression == null);
				if (pathExpression != null) {
					virtualPathExpression.test(pathExpression);
				}
			}
		}

		@Override
		public String toString() {
			return virtual ? virtualPathExpression.toString() : identificationVariable;
		}
	}

	protected static final class IndexExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected IndexExpressionTester(ExpressionTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return IndexExpression.class;
		}

		@Override
		protected String identifier() {
			return INDEX;
		}
	}

	protected static final class InExpressionTester extends AbstractExpressionTester {

		public boolean hasLeftParenthesis;
		private boolean hasNot;
		public boolean hasRightParenthesis;
		public boolean hasSpaceAfterIn;
		private ExpressionTester inItems;
		private ExpressionTester stateFieldPathExpression;

		protected InExpressionTester(ExpressionTester stateFieldPathExpression,
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
				sb.append(SPACE);
			}
			sb.append(hasNot ? NOT_IN : IN);
			if (hasSpaceAfterIn) {
				sb.append(SPACE);
			}
			if (hasLeftParenthesis) {
				sb.append(LEFT_PARENTHESIS);
			}
			sb.append(inItems);
			if (hasRightParenthesis) {
				sb.append(RIGHT_PARENTHESIS);
			}
			return sb.toString();
		}
	}

	protected static final class InputParameterTester extends AbstractExpressionTester {

		private String inputParameter;

		protected InputParameterTester(String inputParameter) {
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

	protected static final class JoinTester extends AbstractExpressionTester {

		private boolean hasAs;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterIdentificationVariable;
		public boolean hasSpaceAfterJoin;
		public boolean hasSpaceAfterJoinAssociation;
		private ExpressionTester identificationVariable;
		private ExpressionTester joinAssociationPath;
		private ExpressionTester joinCondition;
		private String joinType;

		protected JoinTester(String joinType,
		                     ExpressionTester joinAssociationPath,
		                     boolean hasAs,
		                     ExpressionTester identificationVariable,
		                     ExpressionTester joinCondition) {

			super();
			this.joinType                            = joinType;
			this.hasSpaceAfterJoin                   = true;
			this.joinAssociationPath                 = joinAssociationPath;
			this.hasAs                               = hasAs;
			this.hasSpaceAfterAs                     = hasAs;
			this.identificationVariable              = identificationVariable;
			this.hasSpaceAfterJoinAssociation        = !joinAssociationPath.isNull();
			this.hasSpaceAfterIdentificationVariable = !identificationVariable.isNull() && !joinCondition.isNull();
			this.joinCondition                       = joinCondition;
		}

		public void test(Expression expression) {
			assertInstance(expression, Join.class);

			Join join = (Join) expression;
			assertEquals(toString(), join.toParsedText());
			assertEquals(joinType, join.getIdentifier());

			assertEquals(hasSpaceAfterJoin,                   join.hasSpaceAfterJoin());
			assertEquals(!joinAssociationPath.isNull(),       join.hasJoinAssociationPath());
			assertEquals(hasAs,                               join.hasAs());
			assertEquals(hasSpaceAfterAs,                     join.hasSpaceAfterAs());
			assertEquals(hasSpaceAfterJoinAssociation,        join.hasSpaceAfterJoinAssociation());
			assertEquals(!identificationVariable.isNull(),    join.hasIdentificationVariable());
			assertEquals(hasSpaceAfterIdentificationVariable, join.hasSpaceAfterIdentificationVariable());

			joinAssociationPath.test(join.getJoinAssociationPath());
			identificationVariable.test(join.getIdentificationVariable());
			joinCondition.test(join.getOnClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			if (hasSpaceAfterJoin) {
				sb.append(SPACE);
			}
			sb.append(joinAssociationPath);
			if (hasSpaceAfterJoinAssociation) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			if (hasSpaceAfterIdentificationVariable) {
				sb.append(SPACE);
			}
			sb.append(joinCondition);
			return sb.toString();
		}
	}

	protected static final class JPQLExpressionTester extends AbstractExpressionTester {

		private ExpressionTester queryStatement;
		private ExpressionTester unknownExpression;

		protected JPQLExpressionTester(ExpressionTester queryStatement,
		                               ExpressionTester unknownExpression) {

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

	protected static final class KeywordExpressionTester extends AbstractExpressionTester {

		private String keyword;

		protected KeywordExpressionTester(String keyword) {
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

	protected static final class LengthExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected LengthExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return LengthExpression.class;
		}

		@Override
		protected String identifier() {
			return LENGTH;
		}
	}

	protected static final class LikeExpressionTester extends AbstractExpressionTester {

		private ExpressionTester escapeCharacter;
		public boolean hasEscape;
		private boolean hasNot;
		public boolean hasSpaceAfterEscape;
		public boolean hasSpaceAfterLike;
		public boolean hasSpaceAfterPatternValue;
		private ExpressionTester patternValue;
		private ExpressionTester stringExpression;

		protected LikeExpressionTester(ExpressionTester stringExpression,
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
			if (!stringExpression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(hasNot ? NOT_LIKE : LIKE);
			if (hasSpaceAfterLike) {
				sb.append(SPACE);
			}
			sb.append(patternValue);
			if (hasSpaceAfterPatternValue) {
				sb.append(SPACE);
			}
			if (hasEscape) {
				sb.append(ESCAPE);
			}
			if (hasSpaceAfterEscape) {
				sb.append(SPACE);
			}
			sb.append(escapeCharacter);
			return sb.toString();
		}
	}

	protected static final class LocateExpressionTester extends AbstractTripleEncapsulatedExpressionTester {

		protected LocateExpressionTester(ExpressionTester firstExpression,
		                                 ExpressionTester secondExpression,
		                                 ExpressionTester thirdExpression) {

			super(firstExpression, secondExpression, thirdExpression);
		}

		@Override
		protected Class<? extends AbstractTripleEncapsulatedExpression> expressionType() {
			return LocateExpression.class;
		}

		@Override
		protected String identifier() {
			return LOCATE;
		}
	}

	protected static abstract class LogicalExpressionTester extends CompoundExpressionTester {

		protected LogicalExpressionTester(ExpressionTester leftExpression,
		                                  ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, LogicalExpression.class);
		}
	}

	protected static final class LowerExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected LowerExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return LowerExpression.class;
		}

		@Override
		protected String identifier() {
			return LOWER;
		}
	}

	protected static final class MaxFunctionTester extends AggregateFunctionTester {

		protected MaxFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return MaxFunction.class;
		}

		@Override
		protected String identifier() {
			return MAX;
		}
	}

	protected static final class MinFunctionTester extends AggregateFunctionTester {

		protected MinFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return MinFunction.class;
		}

		@Override
		protected String identifier() {
			return MIN;
		}
	}

	protected static final class ModExpressionTester extends AbstractDoubleEncapsulatedExpressionTester {

		protected ModExpressionTester(ExpressionTester firstExpression, ExpressionTester secondExpression) {
			super(firstExpression, secondExpression);
		}

		@Override
		protected Class<? extends AbstractDoubleEncapsulatedExpression> expressionType() {
			return ModExpression.class;
		}

		@Override
		protected String identifier() {
			return MOD;
		}
	}

	protected static final class MultiplicationExpressionTester extends CompoundExpressionTester {

		protected MultiplicationExpressionTester(ExpressionTester leftExpression,
		                                         ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpression> expressionType() {
			return MultiplicationExpression.class;
		}

		@Override
		protected String identifier() {
			return MULTIPLICATION;
		}
	}

	protected static final class NotExpressionTester extends AbstractExpressionTester {

		private ExpressionTester expression;
		public boolean hasSpaceAfterNot;

		protected NotExpressionTester(ExpressionTester expression) {
			super();
			this.expression       = expression;
			this.hasSpaceAfterNot = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, NotExpression.class);

			NotExpression notExpression = (NotExpression) expression;
			assertEquals(toString(),                notExpression.toParsedText());
			assertEquals(hasSpaceAfterNot,          notExpression.hasSpaceAfterNot());
			assertEquals(!this.expression.isNull(), notExpression.hasExpression());

			this.expression.test(notExpression.getExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(NOT);
			if (hasSpaceAfterNot) {
				sb.append(SPACE);
			}
			sb.append(expression);
			return sb.toString();
		}
	}

	protected static final class NullComparisonExpressionTester extends AbstractExpressionTester {

		private ExpressionTester expression;
		private boolean hasNot;

		protected NullComparisonExpressionTester(ExpressionTester expression, boolean hasNot) {
			super();
			this.hasNot     = hasNot;
			this.expression = expression;
		}

		public void test(Expression expression) {
			assertInstance(expression, NullComparisonExpression.class);

			NullComparisonExpression nullComparisonExpression = (NullComparisonExpression) expression;
			assertEquals(toString(),                nullComparisonExpression.toParsedText());
			assertEquals(hasNot,                    nullComparisonExpression.hasNot());
			assertEquals(!this.expression.isNull(), nullComparisonExpression.hasExpression());

			this.expression.test(nullComparisonExpression.getExpression());
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

	protected static final class NullExpressionTester extends AbstractExpressionTester {

		@Override
		public boolean isNull() {
			return true;
		}

		public void test(Expression expression) {
			assertInstance(expression, NullExpression.class);
		}

		@Override
		public String toString() {
			return ExpressionTools.EMPTY_STRING;
		}
	}

	protected static final class NullIfExpressionTester extends AbstractDoubleEncapsulatedExpressionTester {

		protected NullIfExpressionTester(ExpressionTester firstExpression,
		                                 ExpressionTester secondExpression) {

			super(firstExpression, secondExpression);
      }

		@Override
		protected Class<? extends AbstractDoubleEncapsulatedExpression> expressionType() {
			return NullIfExpression.class;
		}

		@Override
		protected String identifier() {
			return NULLIF;
		}
	}

	protected static final class NumericLiteralTester extends AbstractExpressionTester {

		private String number;

		protected NumericLiteralTester(String number) {
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

	protected static final class ObjectExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected ObjectExpressionTester(ExpressionTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return ObjectExpression.class;
		}

		@Override
		protected String identifier() {
			return OBJECT;
		}
	}

	protected static final class OnClauseTester extends AbstractConditionalClauseTester {

		protected OnClauseTester(ExpressionTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		protected Class<? extends AbstractConditionalClause> expressionType() {
			return OnClause.class;
		}

		@Override
		protected String identifier() {
			return ON;
		}
	}

	protected static final class OrderByClauseTester extends AbstractExpressionTester {

		public boolean hasSpaceAfterOrderBy;
		private ExpressionTester orderByItems;

		protected OrderByClauseTester(ExpressionTester orderByItems) {
			super();
			this.orderByItems = orderByItems;
			this.hasSpaceAfterOrderBy = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, OrderByClause.class);

			OrderByClause orderByClause = (OrderByClause) expression;
			assertEquals(toString(),             orderByClause.toParsedText());
			assertEquals(!orderByItems.isNull(), orderByClause.hasOrderByItems());
			assertEquals(hasSpaceAfterOrderBy,   orderByClause.hasSpaceAfterOrderBy());

			orderByItems.test(orderByClause.getOrderByItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(ORDER_BY);
			if (hasSpaceAfterOrderBy) {
				sb.append(SPACE);
			}
			sb.append(orderByItems);
			return sb.toString();
		}
	}

	protected static final class OrderByItemTester extends AbstractExpressionTester {

		public boolean hasSpaceAfterNulls;
		private NullOrdering nullOrdering;
		public String nulls;
		private ExpressionTester orderByItem;
		private Ordering ordering;

		protected OrderByItemTester(ExpressionTester orderByItem,
		                            Ordering ordering,
		                            NullOrdering nullOrdering) {

			super();
			this.ordering     = ordering;
			this.orderByItem  = orderByItem;
			this.nullOrdering = nullOrdering;
		}

		public void test(Expression expression) {
			assertInstance(expression, OrderByItem.class);

			OrderByItem orderByItem = (OrderByItem) expression;

			boolean hasSpaceAfterExpression = ordering != Ordering.DEFAULT         ||
			                                  nullOrdering != NullOrdering.DEFAULT ||
			                                  orderByItem.hasNulls();

			assertEquals(toString(), orderByItem.toParsedText());
			assertEquals(!this.orderByItem.isNull(), orderByItem.hasExpression());
			assertEquals(!this.orderByItem.isNull() && hasSpaceAfterExpression, orderByItem.hasSpaceAfterExpression());
			assertEquals(ordering != Ordering.DEFAULT && nullOrdering != NullOrdering.DEFAULT, orderByItem.hasSpaceAfterOrdering());
			assertSame  (ordering, orderByItem.getOrdering());

			if (nulls != null) {
				assertEquals(nulls, orderByItem.getActualNullOrdering().toUpperCase());
			}
			else {
				assertSame(nullOrdering, orderByItem.getNullOrdering());
			}

			this.orderByItem.test(orderByItem.getExpression());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(orderByItem);

			if (ordering != Ordering.DEFAULT) {
				sb.append(SPACE);
				sb.append(ordering.name());
			}

			if (nulls != null) {
				sb.append(SPACE);
				sb.append(nulls);
				if (hasSpaceAfterNulls) {
					sb.append(SPACE);
				}
			}
			else if (nullOrdering != NullOrdering.DEFAULT) {
				sb.append(SPACE);
				sb.append(nullOrdering.getIdentifier());
			}

			return sb.toString();
		}
	}

	protected static final class OrExpressionTester extends LogicalExpressionTester {

		protected OrExpressionTester(ExpressionTester leftExpression,
		                             ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpression> expressionType() {
			return OrExpression.class;
		}

		@Override
		protected String identifier() {
			return OR;
		}
	}

	protected static final class RangeVariableDeclarationTester extends AbstractExpressionTester {

		private ExpressionTester abstractSchemaName;
		private boolean hasAs;
		public boolean hasSpaceAfterAbstractSchemaName;
		public boolean hasSpaceAfterAs;
		private ExpressionTester identificationVariable;

		protected RangeVariableDeclarationTester(ExpressionTester abstractSchemaName,
		                                         boolean hasAs,
		                                         ExpressionTester identificationVariable) {

			super();
			this.hasAs                           = hasAs;
			this.abstractSchemaName              = abstractSchemaName;
			this.identificationVariable          = identificationVariable;
			this.hasSpaceAfterAbstractSchemaName = true;
			this.hasSpaceAfterAs                 = hasAs;
		}

		public void test(Expression expression) {
			assertInstance(expression, RangeVariableDeclaration.class);

			RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;
			assertEquals(toString(),                       rangeVariableDeclaration.toParsedText());
			assertEquals(hasAs,                            rangeVariableDeclaration.hasAs());
			assertEquals(hasSpaceAfterAs,                  rangeVariableDeclaration.hasSpaceAfterAs());
			assertEquals(!identificationVariable.isNull(), rangeVariableDeclaration.hasIdentificationVariable());
			assertEquals(hasSpaceAfterAbstractSchemaName,  rangeVariableDeclaration.hasSpaceAfterRootObject());

			abstractSchemaName.test(rangeVariableDeclaration.getRootObject());
			identificationVariable.test(rangeVariableDeclaration.getIdentificationVariable());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(abstractSchemaName);
			if (hasSpaceAfterAbstractSchemaName) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	protected static class RegexpExpressionTester extends AbstractExpressionTester {

		public boolean hasSpaceAfterIdentifier;
		private ExpressionTester patternValue;
		private ExpressionTester stringExpression;

		protected RegexpExpressionTester(ExpressionTester stringExpression,
		                                 ExpressionTester patternValue) {

			super();
			this.stringExpression        = stringExpression;
			this.patternValue            = patternValue;
			this.hasSpaceAfterIdentifier = !patternValue.isNull();
		}

		public void test(Expression expression) {
			assertInstance(expression, RegexpExpression.class);

			RegexpExpression regexpExpression = (RegexpExpression) expression;
			assertEquals(toString(),                 regexpExpression.toParsedText());
			assertEquals(!patternValue.isNull(),     regexpExpression.hasPatternValue());
			assertEquals(hasSpaceAfterIdentifier,    regexpExpression.hasSpaceAfterIdentifier());
			assertEquals(!stringExpression.isNull(), regexpExpression.hasSpaceAfterStringExpression());
			assertEquals(!stringExpression.isNull(), regexpExpression.hasStringExpression());

			stringExpression.test(regexpExpression.getStringExpression());
			patternValue    .test(regexpExpression.getPatternValue());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(stringExpression);
			if (!stringExpression.isNull()) {
				sb.append(SPACE);
			}
			sb.append(REGEXP);
			if (hasSpaceAfterIdentifier) {
				sb.append(SPACE);
			}
			sb.append(patternValue);
			return sb.toString();
		}
	}

	protected static final class ResultVariableTester extends AbstractExpressionTester {

		private boolean hasAs;
		public boolean hasSpaceAfterAs;
		private ExpressionTester resultVariable;
		private ExpressionTester selectExpression;

		protected ResultVariableTester(ExpressionTester selectExpression,
		                               boolean hasAs,
		                               ExpressionTester resultVariable) {

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
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(resultVariable);
			return sb.toString();
		}
	}

	protected static final class SelectClauseTester extends AbstractSelectClauseTester {

		protected SelectClauseTester(ExpressionTester selectExpressions, boolean hasDistinct) {
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, SelectClause.class);
		}
	}

	protected static final class SelectStatementTester extends AbstractSelectStatementTester {

		public boolean hasSpaceBeforeOrderByClause;
		public boolean hasSpaceBeforeUnionClauses;
		private ExpressionTester orderByClause;
		private ExpressionTester unionClauses;

		protected SelectStatementTester(ExpressionTester selectClause,
		                                ExpressionTester fromClause,
		                                ExpressionTester whereClause,
		                                ExpressionTester groupByClause,
		                                ExpressionTester havingClause,
		                                ExpressionTester orderByClause,
		                                ExpressionTester unionClauses) {

			super(selectClause, fromClause, whereClause, groupByClause, havingClause);

			this.orderByClause = orderByClause;
			this.unionClauses  = unionClauses;

			this.hasSpaceBeforeOrderByClause = !orderByClause.isNull();
			this.hasSpaceBeforeUnionClauses  = !unionClauses .isNull();

			hasSpaceAfterFrom    |= (!fromClause   .isNull() && whereClause  .isNull() &&  groupByClause.isNull() &&  havingClause .isNull() && !orderByClause.isNull() && !unionClauses.isNull());
			hasSpaceAfterWhere   |= (!whereClause  .isNull() && groupByClause.isNull() &&  havingClause .isNull() && !orderByClause.isNull() && !unionClauses.isNull());
			hasSpaceAfterGroupBy |= (!groupByClause.isNull() && havingClause .isNull() && !orderByClause.isNull() && !unionClauses.isNull());
		}

		@Override
		protected Class<? extends AbstractSelectStatement> expressionType() {
			return SelectStatement.class;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			SelectStatement selectStatement = (SelectStatement) expression;
			assertEquals(!orderByClause.isNull(), selectStatement.hasOrderByClause());
//			assertEquals(hasSpaceBeforeOrderByClause, selectStatement.hasSpaceBeforeOrderBy());

			orderByClause.test(selectStatement.getOrderByClause());
			unionClauses .test(selectStatement.getUnionClauses());
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			if (hasSpaceBeforeOrderByClause && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(SPACE);
			}
			sb.append(orderByClause);
			if (hasSpaceBeforeUnionClauses && sb.charAt(sb.length() - 1) != ' ') {
				sb.append(SPACE);
			}
			sb.append(unionClauses);
			return sb.toString();
		}
	}

	protected static final class SimpleFromClauseTester extends AbstractFromClauseTester {

		protected SimpleFromClauseTester(ExpressionTester declaration) {
			super(declaration);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, SimpleFromClause.class);
		}
	}

	protected static final class SimpleSelectClauseTester extends AbstractSelectClauseTester {

		protected SimpleSelectClauseTester(ExpressionTester selectExpressions, boolean hasDistinct) {
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, SimpleSelectClause.class);
		}
	}

	protected static final class SimpleSelectStatementTester extends AbstractSelectStatementTester {

		protected SimpleSelectStatementTester(ExpressionTester selectClause,
		                                      ExpressionTester fromClause,
		                                      ExpressionTester whereClause,
		                                      ExpressionTester groupByClause,
		                                      ExpressionTester havingClause) {

			super(selectClause, fromClause, whereClause, groupByClause, havingClause);
		}

		@Override
		protected Class<? extends AbstractSelectStatement> expressionType() {
			return SimpleSelectStatement.class;
		}
	}

	protected static final class SizeExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected SizeExpressionTester(ExpressionTester collectionPath) {
			super(collectionPath);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return SizeExpression.class;
		}

		@Override
		protected String identifier() {
			return SIZE;
		}
	}

	protected static final class SqrtExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected SqrtExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return SqrtExpression.class;
		}

		@Override
		protected String identifier() {
			return SQRT;
		}
	}

	protected static final class StateFieldPathExpressionTester extends AbstractPathExpressionTester {

		protected StateFieldPathExpressionTester(ExpressionTester identificationVariable,
                                               String value) {

			super(identificationVariable, value);
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);
			assertInstance(expression, StateFieldPathExpression.class);
		}
	}

	protected static final class StringLiteralTester extends AbstractExpressionTester {

		private boolean hasCloseQuote;
		private String literal;

		protected StringLiteralTester(String literal) {

			super();
			this.literal = literal;

			if (literal.length() > 1) {
				char lastChar = literal.charAt(literal.length() - 1);
				hasCloseQuote = (lastChar == SINGLE_QUOTE) || (lastChar == DOUBLE_QUOTE);
			}
		}

		public void test(Expression expression) {
			assertInstance(expression, StringLiteral.class);

			StringLiteral stringLiteral = (StringLiteral) expression;
			assertEquals(toString(),    stringLiteral.toString());
			assertEquals(hasCloseQuote, stringLiteral.hasCloseQuote());
		}

		@Override
		public String toString() {
			return literal;
		}
	}

	protected static final class SubExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected SubExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return SubExpression.class;
		}

		@Override
		protected String identifier() {
			return ExpressionTools.EMPTY_STRING;
		}
	}

	protected static final class SubstringExpressionTester extends AbstractTripleEncapsulatedExpressionTester {

		protected SubstringExpressionTester(ExpressionTester firstExpression,
		                                    ExpressionTester firstArithmeticExpression,
		                                    ExpressionTester secondArithmeticExpression) {

			super(firstExpression, firstArithmeticExpression, secondArithmeticExpression);
		}

		@Override
		protected Class<? extends AbstractTripleEncapsulatedExpression> expressionType() {
			return SubstringExpression.class;
		}

		@Override
		protected String identifier() {
			return SubstringExpression.SUBSTRING;
		}
	}

	protected static final class SubtractionExpressionTester extends CompoundExpressionTester {

		protected SubtractionExpressionTester(ExpressionTester leftExpression,
		                                      ExpressionTester rightExpression) {

			super(leftExpression, rightExpression);
		}

		@Override
		protected Class<? extends CompoundExpression> expressionType() {
			return SubtractionExpression.class;
		}

		@Override
		protected String identifier() {
			return MINUS;
		}
	}

	protected static final class SumFunctionTester extends AggregateFunctionTester {

		protected SumFunctionTester(ExpressionTester expression, boolean hasDistinct) {
			super(expression, hasDistinct);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return SumFunction.class;
		}

		@Override
		protected String identifier() {
			return SUM;
		}
	}

	protected static class TableExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected TableExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<TableExpression> expressionType() {
			return TableExpression.class;
		}

		@Override
		protected String identifier() {
			return TABLE;
		}
	}

	protected static class TableVariableDeclarationTester extends AbstractExpressionTester {


		private boolean hasAs;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterTableExpression;
		private ExpressionTester identificationVariable;
		private TableExpressionTester tableExpression;

		protected TableVariableDeclarationTester(TableExpressionTester tableExpression,
		                                         boolean hasAs,
		                                         ExpressionTester identificationVariable) {
			super();
			this.tableExpression              = tableExpression;
			this.hasAs                        = hasAs;
			this.identificationVariable       = identificationVariable;
			this.hasSpaceAfterTableExpression = hasAs || !identificationVariable.isNull();
			this.hasSpaceAfterAs              = hasAs && !identificationVariable.isNull();
		}

		public void test(Expression expression) {
			assertInstance(expression, Expression.class);

			TableVariableDeclaration tableVariableDeclaration = (TableVariableDeclaration) expression;
			assertEquals(toString(), tableVariableDeclaration.toParsedText());
			assertEquals(!identificationVariable.isNull(), tableVariableDeclaration.hasIdentificationVariable());
			assertEquals(hasAs,                            tableVariableDeclaration.hasAs());
			assertEquals(hasSpaceAfterAs,                  tableVariableDeclaration.hasSpaceAfterAs());
			assertEquals(hasSpaceAfterTableExpression,     tableVariableDeclaration.hasSpaceAfterTableExpression());

			tableExpression       .test(tableVariableDeclaration.getTableExpression());
			identificationVariable.test(tableVariableDeclaration.getIdentificationVariable());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(tableExpression);
			if (hasSpaceAfterTableExpression) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	protected static final class TreatExpressionTester extends AbstractEncapsulatedExpressionTester {

		private ExpressionTester collectionValuedPathExpression;
		private ExpressionTester entityTypeName;
		private boolean hasAs;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterCollectionValuedPathExpression;

		protected TreatExpressionTester(ExpressionTester collectionValuedPathExpression,
		                                boolean hasAs,
		                                ExpressionTester entityTypeName) {
			super();
			this.hasAs           = hasAs;
			this.entityTypeName  = entityTypeName;
			this.hasSpaceAfterAs = hasAs;
			this.hasSpaceAfterCollectionValuedPathExpression = true;
			this.collectionValuedPathExpression              = collectionValuedPathExpression;
		}

		@Override
		protected Class<? extends AbstractEncapsulatedExpression> expressionType() {
			return TreatExpression.class;
		}

		@Override
		protected boolean hasEncapsulatedExpression() {
			return !collectionValuedPathExpression.isNull() || !entityTypeName.isNull() || hasAs;
		}

		@Override
		protected String identifier() {
			return TREAT;
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(collectionValuedPathExpression);
			if (hasSpaceAfterCollectionValuedPathExpression) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(entityTypeName);
		}
	}

	protected static final class TrimExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private boolean hasFrom;
		public boolean hasSpaceAfterFrom;
		public boolean hasSpaceAfterSpecification;
		public boolean hasSpaceAfterTrimCharacter;
		private Specification specification;
		private ExpressionTester trimCharacter;

		protected TrimExpressionTester(Specification specification,
		                               ExpressionTester stringPrimary,
		                               ExpressionTester trimCharacter,
		                               boolean hasFrom) {

			super(stringPrimary);
			this.specification              = specification;
			this.trimCharacter              = trimCharacter;
			this.hasFrom                    = hasFrom;
			this.hasSpaceAfterFrom          = hasFrom;
			this.hasSpaceAfterTrimCharacter = !trimCharacter.isNull();
			this.hasSpaceAfterSpecification = specification != Specification.DEFAULT;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return TrimExpression.class;
		}

		@Override
		protected String identifier() {
			return TRIM;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			TrimExpression trimExpression = (TrimExpression) expression;
			assertEquals(hasFrom,                                trimExpression.hasFrom());
			assertEquals(hasSpaceAfterFrom,                      trimExpression.hasSpaceAfterFrom());
			assertEquals(hasSpaceAfterTrimCharacter,             trimExpression.hasSpaceAfterTrimCharacter());
			assertEquals(!trimCharacter.isNull(),                trimExpression.hasTrimCharacter());
			assertEquals(specification != Specification.DEFAULT, trimExpression.hasSpecification());
			assertEquals(hasSpaceAfterSpecification,             trimExpression.hasSpaceAfterSpecification());

			trimCharacter.test(trimExpression.getTrimCharacter());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			if (specification != Specification.DEFAULT) {
				sb.append(specification);
			}
			if (hasSpaceAfterSpecification) {
				sb.append(SPACE);
			}
			sb.append(trimCharacter);
			if (hasSpaceAfterTrimCharacter) {
				sb.append(SPACE);
			}
			if (hasFrom) {
				sb.append(FROM);
			}
			if (hasSpaceAfterFrom) {
				sb.append(SPACE);
			}
			super.toStringEncapsulatedExpression(sb);
		}
	}

	protected static final class TypeExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected TypeExpressionTester(ExpressionTester identificationVariable) {
			super(identificationVariable);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return TypeExpression.class;
		}

		@Override
		protected String identifier() {
			return TYPE;
		}
	}

	protected static final class UnionClauseTester extends AbstractExpressionTester {

		private boolean hasAll;
		public boolean hasSpaceAfterAll;
		public boolean hasSpaceAfterIdentifier;
		private String identifier;
		private ExpressionTester subquery;

		protected UnionClauseTester(String identifier, boolean hasAll, ExpressionTester subquery) {
			super();
			this.identifier = identifier;
			this.hasAll     = hasAll;
			this.subquery   = subquery;
			this.hasSpaceAfterIdentifier = hasAll || !subquery.isNull();
			this.hasSpaceAfterAll        = hasAll && !subquery.isNull();
		}

		public void test(Expression expression) {
			assertInstance(expression, UnionClause.class);

			UnionClause unionClause = (UnionClause) expression;
			assertEquals(toString(),              unionClause.toParsedText());
			assertEquals(identifier,              unionClause.getIdentifier());
			assertEquals(hasSpaceAfterIdentifier, unionClause.hasSpaceAfterIdentifier());
			assertEquals(hasAll,                  unionClause.hasAll());
			assertEquals(!subquery.isNull(),      unionClause.hasQuery());
			assertEquals(hasSpaceAfterAll,        unionClause.hasSpaceAfterAll());

			subquery.test(unionClause.getQuery());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier);
			if (hasSpaceAfterIdentifier) {
				sb.append(SPACE);
			}
			if (hasAll) {
				sb.append(ALL);
			}
			if (hasSpaceAfterAll) {
				sb.append(SPACE);
			}
			sb.append(subquery);
			return sb.toString();
		}
	}

	protected static final class UnknownExpressionTester extends AbstractExpressionTester {

		private final String unknownText;

		protected UnknownExpressionTester(String unknownText) {
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

	protected static final class UpdateClauseTester extends AbstractExpressionTester {

		public boolean hasSet;
		public boolean hasSpaceAfterRangeVariableDeclaration;
		public boolean hasSpaceAfterSet;
		public boolean hasSpaceAfterUpdate;
		private ExpressionTester rangeVariableDeclaration;
		private ExpressionTester updateItems;

		protected UpdateClauseTester(ExpressionTester rangeVariableDeclaration,
		                             ExpressionTester updateItems) {

			super();
			this.hasSet                                = true;
			this.hasSpaceAfterSet                      = true;
			this.hasSpaceAfterUpdate                   = true;
			this.updateItems                           = updateItems;
			this.rangeVariableDeclaration              = rangeVariableDeclaration;
			this.hasSpaceAfterRangeVariableDeclaration = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, UpdateClause.class);

			UpdateClause updateClause = (UpdateClause) expression;
			assertEquals(toString(),                            updateClause.toParsedText());
			assertEquals(!rangeVariableDeclaration.isNull(),    updateClause.hasRangeVariableDeclaration());
			assertEquals(hasSet,                                updateClause.hasSet());
			assertEquals(hasSpaceAfterSet,                      updateClause.hasSpaceAfterSet());
			assertEquals(hasSpaceAfterUpdate,                   updateClause.hasSpaceAfterUpdate());
			assertEquals(!updateItems.isNull(),                 updateClause.hasUpdateItems());
			assertEquals(hasSpaceAfterRangeVariableDeclaration, updateClause.hasSpaceAfterRangeVariableDeclaration());

			rangeVariableDeclaration.test(updateClause.getRangeVariableDeclaration());
			updateItems.test(updateClause.getUpdateItems());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(UPDATE);
			if (hasSpaceAfterUpdate) {
				sb.append(SPACE);
			}
			sb.append(rangeVariableDeclaration);
			if (hasSpaceAfterRangeVariableDeclaration) {
				sb.append(SPACE);
			}
			if (hasSet) {
				sb.append(SET);
			}
			if (hasSpaceAfterSet) {
				sb.append(SPACE);
			}
			sb.append(updateItems);
			return sb.toString();
		}
	}

	protected static final class UpdateItemTester extends AbstractExpressionTester {

		public boolean hasEqualSign;
		public boolean hasSpaceAfterEqualSign;
		public boolean hasSpaceAfterStateFieldPathExpression;
		private ExpressionTester newValue;
		private ExpressionTester stateFieldPathExpression;

		protected UpdateItemTester(ExpressionTester stateFieldPathExpression,
		                           ExpressionTester newValue) {

			super();
			this.stateFieldPathExpression = stateFieldPathExpression;
			this.newValue = newValue;
			this.hasSpaceAfterStateFieldPathExpression = true;
			this.hasSpaceAfterEqualSign = true;
			this.hasEqualSign = true;
		}

		public void test(Expression expression) {
			assertInstance(expression, UpdateItem.class);

			UpdateItem updateItem = (UpdateItem) expression;
			assertEquals(toString(),                            updateItem.toParsedText());
			assertEquals(hasEqualSign,                          updateItem.hasEqualSign());
			assertEquals(!newValue.isNull(),                    updateItem.hasNewValue());
			assertEquals(hasSpaceAfterEqualSign,                updateItem.hasSpaceAfterEqualSign());
			assertEquals(hasSpaceAfterStateFieldPathExpression, updateItem.hasSpaceAfterStateFieldPathExpression());
			assertEquals(!stateFieldPathExpression.isNull(),    updateItem.hasStateFieldPathExpression());

			stateFieldPathExpression.test(updateItem.getStateFieldPathExpression());
			newValue.test(updateItem.getNewValue());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPathExpression);
			if (hasSpaceAfterStateFieldPathExpression) {
				sb.append(SPACE);
			}
			if (hasEqualSign) {
				sb.append(EQUAL);
			}
			if (hasSpaceAfterEqualSign) {
				sb.append(SPACE);
			}
			sb.append(newValue);
			return sb.toString();
		}
	}

	protected static final class UpdateStatementTester extends AbstractExpressionTester {

		public boolean hasSpaceAfterUpdateClause;
		private ExpressionTester updateClause;
		private ExpressionTester whereClause;

		protected UpdateStatementTester(ExpressionTester updateClause, ExpressionTester whereClause) {
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
			assertEquals(!whereClause.isNull(),     updateStatement.hasWhereClause());

			updateClause.test(updateStatement.getUpdateClause());
			whereClause .test(updateStatement.getWhereClause());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(updateClause);

			if (hasSpaceAfterUpdateClause) {
				sb.append(SPACE);
			}

			sb.append(whereClause);
			return sb.toString();
		}
	}

	protected static final class UpperExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		protected UpperExpressionTester(ExpressionTester expression) {
			super(expression);
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return UpperExpression.class;
		}

		@Override
		protected String identifier() {
			return UPPER;
		}
	}

	protected static final class WhenClauseTester extends AbstractExpressionTester {

		public boolean hasSpaceAfterThen;
		public boolean hasSpaceAfterWhen;
		public boolean hasSpaceAfterWhenExpression;
		public boolean hasThen;
		private ExpressionTester thenExpression;
		private ExpressionTester whenExpression;

		protected WhenClauseTester(ExpressionTester whenExpression, ExpressionTester thenExpression) {
			super();
			this.hasSpaceAfterWhenExpression = true;
			this.hasSpaceAfterThen           = true;
			this.hasSpaceAfterWhen           = true;
			this.hasThen                     = true;
			this.thenExpression              = thenExpression;
			this.whenExpression              = whenExpression;
		}

		public void test(Expression expression) {
			assertInstance(expression, WhenClause.class);

			WhenClause whenClause = (WhenClause) expression;
			assertEquals(toString(),                  whenClause.toParsedText());
			assertEquals(!whenExpression.isNull(),    whenClause.hasWhenExpression());
			assertEquals(hasSpaceAfterWhenExpression, whenClause.hasSpaceAfterWhenExpression());
			assertEquals(hasSpaceAfterThen,           whenClause.hasSpaceAfterThen());
			assertEquals(hasSpaceAfterWhen,           whenClause.hasSpaceAfterWhen());
			assertEquals(hasThen,                     whenClause.hasThen());
			assertEquals(!thenExpression.isNull(),    whenClause.hasThenExpression());

			whenExpression.test(whenClause.getWhenExpression());
			thenExpression.test(whenClause.getThenExpression());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(WHEN);
			if (hasSpaceAfterWhen) {
				sb.append(SPACE);
			}
			sb.append(whenExpression);
			if (hasSpaceAfterWhenExpression) {
				sb.append(SPACE);
			}
			if (hasThen) {
				sb.append(THEN);
			}
			if (hasSpaceAfterWhen) {
				sb.append(SPACE);
			}
			sb.append(thenExpression);
			return sb.toString();
		}
	}

	protected static final class WhereClauseTester extends AbstractConditionalClauseTester {

		protected WhereClauseTester(ExpressionTester conditionalExpression) {
			super(conditionalExpression);
		}

		@Override
		protected Class<? extends AbstractConditionalClause> expressionType() {
			return WhereClause.class;
		}

		@Override
		protected String identifier() {
			return WHERE;
		}
	}
}