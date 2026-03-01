/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 Contributors to the Eclipse Foundation. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.tests.jpql.parser;

import java.util.Arrays;

import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.NullOrdering;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AbsExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AbstractSchemaNameTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AdditionExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AllOrAnyExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AndExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ArithmeticFactorTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AsOfClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.AvgFunctionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.BadExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.BetweenExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CaseExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CastExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CeilingExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CoalesceExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CollectionExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CollectionMemberDeclarationTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CollectionMemberExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CollectionValuedPathExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ComparisonExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ConcatExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ConcatPipesExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ConnectByClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ConstructorExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.CountFunctionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.DatabaseTypeTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.DateTimeTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.DeleteClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.DeleteStatementTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.DivisionExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.EmptyCollectionComparisonExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.EntityTypeLiteralTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.EntryExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ExistsExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ExpExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ExtractExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.FloorExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.FromClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.FunctionExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.GroupByClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.HavingClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.HierarchicalQueryClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.IdentificationVariableDeclarationTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.IdentificationVariableTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.IdExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.InExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.IndexExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.InputParameterTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.JPQLExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.JoinTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.KeywordExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.LeftExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.LengthExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.LikeExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.LnExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.LocateExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.LowerExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.MaxFunctionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.MinFunctionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ModExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.MultiplicationExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.NotExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.NullComparisonExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.NullExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.NullIfExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.NumericLiteralTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ObjectExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.OnClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.OrExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.OrderByClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.OrderByItemTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.OrderSiblingsByClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.PowerExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.RangeVariableDeclarationTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.RegexpExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ReplaceExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ResultVariableTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.RightExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.RoundExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SelectClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SelectStatementTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SignExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SimpleFromClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SimpleSelectClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SimpleSelectStatementTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SizeExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SqrtExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.StartWithClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.StateFieldPathExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.StringLiteralTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SubExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SubstringExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SubtractionExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SumFunctionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.TableExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.TableVariableDeclarationTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.TreatExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.TrimExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.TypeExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UnionClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UnknownExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UpdateClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UpdateItemTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UpdateStatementTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UpperExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.VersionExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.WhenClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.WhereClauseTester;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * Utility class that creates an equivalent representation of any JPQL fragment, which then can be
 * used to test the actual hierarchical representation of a parsed JPQL query.
 *
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLParserTester {

    /**
     * Cannot instantiate <code>JPQLParserTester</code>.
     */
    private JPQLParserTester() {
        super();
    }

    public static AbsExpressionTester abs(ExpressionTester expression) {
        return new AbsExpressionTester(expression);
    }

    public static AbstractSchemaNameTester abstractSchemaName(String abstractSchemaName) {
        return new AbstractSchemaNameTester(abstractSchemaName);
    }

    public static AdditionExpressionTester add(ExpressionTester leftExpression,
                                                  ExpressionTester rightExpression) {

        return new AdditionExpressionTester(leftExpression, rightExpression);
    }

    public static AllOrAnyExpressionTester all(ExpressionTester subquery) {
        return new AllOrAnyExpressionTester(ALL, subquery);
    }

    public static AndExpressionTester and(ExpressionTester leftExpression,
                                             ExpressionTester rightExpression) {

        return new AndExpressionTester(leftExpression, rightExpression);
    }

    public static AllOrAnyExpressionTester any(ExpressionTester subquery) {
        return new AllOrAnyExpressionTester(ANY, subquery);
    }

    public static AllOrAnyExpressionTester anyExpression(ExpressionTester subquery) {
        return new AllOrAnyExpressionTester(ANY, subquery);
    }

    public static SubExpressionTester array(ExpressionTester... items) {
        return sub(collection(items));
    }

    public static AsOfClauseTester asOf(ExpressionTester expression) {
        return new AsOfClauseTester(null, expression);
    }

    public static AsOfClauseTester asOfScn(ExpressionTester expression) {
        return new AsOfClauseTester(SCN, expression);
    }

    public static AsOfClauseTester asOfScn(int changeNumber) {
        return asOfScn(numeric(changeNumber));
    }

    public static AsOfClauseTester asOfTimestamp(ExpressionTester expression) {
        return new AsOfClauseTester(TIMESTAMP, expression);
    }

    public static AvgFunctionTester avg(ExpressionTester expression) {
        return new AvgFunctionTester(expression, false);
    }

    public static AvgFunctionTester avg(String statefieldPathExpression) {
        return avg(path(statefieldPathExpression));
    }

    public static AvgFunctionTester avgDistinct(String statefieldPathExpression) {
        return new AvgFunctionTester(path(statefieldPathExpression), true);
    }

    public static BadExpressionTester bad(ExpressionTester expression) {
        return new BadExpressionTester(expression);
    }

    public static BetweenExpressionTester between(ExpressionTester expression,
                                                     ExpressionTester lowerBoundExpression,
                                                     ExpressionTester upperBoundExpression) {

        return new BetweenExpressionTester(expression, false, lowerBoundExpression, upperBoundExpression);
    }

    public static CaseExpressionTester case_(ExpressionTester... whenClauses) {

        ExpressionTester[] copy = new ExpressionTester[whenClauses.length - 1];
        System.arraycopy(whenClauses, 0, copy, 0, whenClauses.length - 1);

        return new CaseExpressionTester(
            nullExpression(),
            spacedCollection(copy),
            whenClauses[whenClauses.length - 1]
        );
    }

    public static CaseExpressionTester case_(ExpressionTester caseOperand,
                                                ExpressionTester[] whenClauses,
                                                ExpressionTester elseExpression) {

        return new CaseExpressionTester(
            caseOperand,
            (whenClauses.length == 1) ? whenClauses[0] : spacedCollection(whenClauses),
            elseExpression
        );
    }

    public static CaseExpressionTester case_(ExpressionTester[] whenClauses,
                                                ExpressionTester elseExpression) {

        return case_(nullExpression(), whenClauses, elseExpression);
    }

    private static CastExpressionTester cast(ExpressionTester expression,
                                             boolean hasAs,
                                             ExpressionTester databaseType) {

        return new CastExpressionTester(expression, hasAs, databaseType);
    }

    public static CastExpressionTester cast(ExpressionTester expression,
                                               ExpressionTester databaseType) {

        return cast(expression, false, databaseType);
    }

    public static CastExpressionTester cast(ExpressionTester expression, String databaseType) {
        return cast(expression, databaseType, -1, -1);
    }

    public static CastExpressionTester cast(ExpressionTester expression,
                                               String databaseType,
                                               int size) {

        return cast(expression, databaseType, size, -1);
    }

    public static CastExpressionTester cast(ExpressionTester expression,
                                               String databaseType,
                                               int size,
                                               int precision) {

        return new CastExpressionTester(
            expression,
            false,
            databaseType(databaseType, size, precision)
        );
    }

    public static CastExpressionTester cast(String pathExpression, ExpressionTester databaseType) {
        return cast(path(pathExpression), databaseType);
    }

    public static CastExpressionTester cast(String pathExpression, String databaseType) {
        return cast(path(pathExpression), databaseType);
    }

    public static CastExpressionTester cast(String pathExpression,
                                               String databaseType,
                                               int size) {

        return cast(path(pathExpression), databaseType, size, -1);
    }

    public static CastExpressionTester cast(String pathExpression,
                                               String databaseType,
                                               int size,
                                               int precision) {

        return cast(path(pathExpression), databaseType, size, precision);
    }

    public static CastExpressionTester castAs(ExpressionTester expression,
                                                 ExpressionTester databaseType) {

        return cast(expression, true, databaseType);
    }

    public static CastExpressionTester castAs(ExpressionTester expression, String databaseType) {
        return castAs(expression, databaseType, -1, -1);
    }

    public static CastExpressionTester castAs(ExpressionTester pathExpression,
                                                 String databaseType,
                                                 int size) {

        return castAs(pathExpression, databaseType, size, -1);
    }

    public static CastExpressionTester castAs(ExpressionTester expression,
                                                 String databaseType,
                                                 int size,
                                                 int precision) {

        return new CastExpressionTester(
            expression,
            true,
            databaseType(databaseType, size, precision)
        );
    }

    public static CastExpressionTester castAs(String pathExpression, ExpressionTester databaseType) {
        return castAs(path(pathExpression), databaseType);
    }

    public static CastExpressionTester castAs(String pathExpression,
                                                 String databaseType) {

        return castAs(path(pathExpression), databaseType, -1);
    }

    public static CastExpressionTester castAs(String pathExpression,
                                                 String databaseType,
                                                 int size) {

        return castAs(path(pathExpression), databaseType, size);
    }

    public static CastExpressionTester castAs(String pathExpression,
                                                 String databaseType,
                                                 int size,
                                                 int precision) {

        return castAs(path(pathExpression), databaseType, size, precision);
    }

    public static CeilingExpressionTester ceiling(ExpressionTester simpleArithmeticExpression) {
        return new CeilingExpressionTester(simpleArithmeticExpression);
    }

    public static CeilingExpressionTester ceiling(String statefieldPathExpression) {
        return ceiling(path(statefieldPathExpression));
    }

    public static CoalesceExpressionTester coalesce(ExpressionTester expression) {
        return new CoalesceExpressionTester(expression);
    }

    public static CoalesceExpressionTester coalesce(ExpressionTester... expressions) {
        return new CoalesceExpressionTester(collection(expressions));
    }

    public static CollectionExpressionTester collection(ExpressionTester... expressions) {

        Boolean[] spaces = new Boolean[expressions.length];
        Boolean[] commas = new Boolean[expressions.length];

        Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
        Arrays.fill(commas, 0, expressions.length - 1, Boolean.TRUE);

        spaces[expressions.length - 1] = Boolean.FALSE;
        commas[expressions.length - 1] = Boolean.FALSE;

        return collection(expressions, commas, spaces);
    }

    public static CollectionExpressionTester collection(ExpressionTester[] expressions,
                                                        Boolean[] commas,
                                                        Boolean[] spaces) {

        return new CollectionExpressionTester(expressions, commas, spaces);
    }

    public static CollectionValuedPathExpressionTester collectionPath(ExpressionTester identificationVariable,
                                                                      boolean startsWithDot,
                                                                      String collectionPath) {

        return new CollectionValuedPathExpressionTester(
            identificationVariable,
            startsWithDot ? "." + collectionPath : collectionPath
        );
    }

    public static CollectionValuedPathExpressionTester collectionPath(ExpressionTester identificationVariable,
                                                                      String collectionPath) {

        return collectionPath(identificationVariable, true, collectionPath);
    }

    public static CollectionValuedPathExpressionTester collectionPath(String collectionPath) {

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

    public static ConcatExpressionTester concat(ExpressionTester... expressions) {
        if (expressions.length > 1) {
            return new ConcatExpressionTester(collection(expressions));
        }
        return new ConcatExpressionTester(expressions[0]);
    }

    public static ConcatPipesExpressionTester concatPipes(ExpressionTester leftExpression,
                                                                 ExpressionTester rightExpression) {
        return new ConcatPipesExpressionTester(leftExpression, rightExpression);
    }

    public static ConnectByClauseTester connectBy(ExpressionTester expression) {
        return new ConnectByClauseTester(expression);
    }

    public static CountFunctionTester count(ExpressionTester statefieldPathExpression) {
        return new CountFunctionTester(statefieldPathExpression, false);
    }

    public static CountFunctionTester count(String statefieldPathExpression) {
        return count(path(statefieldPathExpression));
    }

    public static CountFunctionTester countDistinct(ExpressionTester statefieldPathExpression) {
        return new CountFunctionTester(statefieldPathExpression, true);
    }

    public static DateTimeTester CURRENT_DATE() {
        return new DateTimeTester(CURRENT_DATE);
    }

    public static DateTimeTester CURRENT_TIME() {
        return new DateTimeTester(CURRENT_TIME);
    }

    public static DateTimeTester CURRENT_TIMESTAMP() {
        return new DateTimeTester(CURRENT_TIMESTAMP);
    }

    public static DatabaseTypeTester databaseType(String databaseType) {
        return databaseType(databaseType, -1);
    }

    public static DatabaseTypeTester databaseType(String databaseType, int length) {
        return databaseType(databaseType, length, -1);
    }

    public static DatabaseTypeTester databaseType(String databaseType, int size, int precision) {

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

    public static DateTimeTester dateTime(String jdbcEscapeFormat) {
        return new DateTimeTester(jdbcEscapeFormat);
    }

    public static DeleteClauseTester delete(ExpressionTester rangeVariableDeclaration) {
        return new DeleteClauseTester(rangeVariableDeclaration);
    }

    public static DeleteClauseTester delete(String abstractSchemaName,
                                               String identificationVariable) {

        return delete(rangeVariableDeclaration(abstractSchemaName, identificationVariable));
    }

    public static DeleteClauseTester deleteAs(ExpressionTester abstractSchemaName,
                                                 ExpressionTester identificationVariable) {

        return delete(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable));
    }

    public static DeleteClauseTester deleteAs(String abstractSchemaName,
                                                 String identificationVariable) {

        return delete(rangeVariableDeclarationAs(abstractSchemaName, identificationVariable));
    }

    public static DeleteStatementTester deleteStatement(ExpressionTester deleteClause) {
        return deleteStatement(deleteClause, nullExpression());
    }

    public static DeleteStatementTester deleteStatement(ExpressionTester deleteClause,
                                                           ExpressionTester whereClause) {

        return new DeleteStatementTester(deleteClause, whereClause);
    }

    public static DeleteStatementTester deleteStatement(String abstractSchemaName,
                                                           String identificationVariable) {

        return deleteStatement(delete(abstractSchemaName, identificationVariable));
    }

    public static DeleteStatementTester deleteStatement(String abstractSchemaName,
                                                           String identificationVariable,
                                                           ExpressionTester whereClause) {

        return deleteStatement(delete(abstractSchemaName, identificationVariable), whereClause);
    }

    public static ComparisonExpressionTester different(ExpressionTester leftExpression,
                                                          ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.DIFFERENT, rightExpression);
    }

    public static DivisionExpressionTester division(ExpressionTester leftExpression,
                                                       ExpressionTester rightExpression) {

        return new DivisionExpressionTester(leftExpression, rightExpression);
    }

    public static EntityTypeLiteralTester entity(String entity) {
        return new EntityTypeLiteralTester(entity);
    }

    public static EntryExpressionTester entry(ExpressionTester identificationVariable) {
        return new EntryExpressionTester(identificationVariable);
    }

    public static EntryExpressionTester entry(String identificationVariable) {
        return entry(variable(identificationVariable));
    }

    public static ComparisonExpressionTester equal(ExpressionTester leftExpression,
                                                      ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.EQUAL, rightExpression);
    }

    public static UnionClauseTester except(ExpressionTester subquery) {
        return union(EXCEPT, false, subquery);
    }

    public static UnionClauseTester except(ExpressionTester selectClause,
                                              ExpressionTester fromClause) {

        return except(subquery(selectClause, fromClause));
    }

    public static UnionClauseTester except(ExpressionTester selectClause,
                                              ExpressionTester fromClause,
                                               ExpressionTester whereClause) {

        return except(subquery(selectClause, fromClause, whereClause));
    }

    public static UnionClauseTester except(ExpressionTester selectClause,
                                              ExpressionTester fromClause,
                                               ExpressionTester whereClause,
                                               ExpressionTester groupByClause,
                                               ExpressionTester havingClause) {

        return except(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
    }

    public static UnionClauseTester exceptAll(ExpressionTester subquery) {
        return union(EXCEPT, true, subquery);
    }

    public static UnionClauseTester exceptAll(ExpressionTester selectClause,
                                                 ExpressionTester fromClause) {

        return exceptAll(subquery(selectClause, fromClause));
    }

    public static UnionClauseTester exceptAll(ExpressionTester selectClause,
                                                 ExpressionTester fromClause,
                                                   ExpressionTester whereClause) {

        return exceptAll(subquery(selectClause, fromClause, whereClause));
    }

    public static UnionClauseTester exceptAll(ExpressionTester selectClause,
                                                 ExpressionTester fromClause,
                                                   ExpressionTester whereClause,
                                                   ExpressionTester groupByClause,
                                                  ExpressionTester havingClause) {

        return exceptAll(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
    }

    public static ExistsExpressionTester exists(ExpressionTester subquery) {
        return new ExistsExpressionTester(subquery, false);
    }

    public static ExtractExpressionTester extract(String part, ExpressionTester expression) {
        return new ExtractExpressionTester(part, false, expression);
    }

    public static ExpExpressionTester exp(ExpressionTester simpleArithmeticExpression) {
        return new ExpExpressionTester(simpleArithmeticExpression);
    }

    public static ExpExpressionTester exp(String statefieldPathExpression) {
        return exp(path(statefieldPathExpression));
    }

    public static ExtractExpressionTester extractFrom(String part, ExpressionTester expression) {
        return new ExtractExpressionTester(part, true, expression);
    }

    public static KeywordExpressionTester FALSE() {
        return new KeywordExpressionTester(FALSE);
    }

    public static FloorExpressionTester floor(ExpressionTester simpleArithmeticExpression) {
        return new FloorExpressionTester(simpleArithmeticExpression);
    }

    public static FloorExpressionTester floor(String statefieldPathExpression) {
        return floor(path(statefieldPathExpression));
    }

    public static FromClauseTester from(ExpressionTester declaration) {
        return new FromClauseTester(declaration, nullExpression(), nullExpression());
    }

    public static FromClauseTester from(ExpressionTester... declarations) {
        return new FromClauseTester(collection(declarations), nullExpression(), nullExpression());
    }

    public static FromClauseTester from(ExpressionTester declarations,
                                           HierarchicalQueryClauseTester hierarchicalQueryClause,
                                           AsOfClauseTester asOfClause) {

        return new FromClauseTester(declarations, hierarchicalQueryClause, asOfClause);
    }

    public static FromClauseTester from(ExpressionTester[] declarations,
                                           AsOfClauseTester asOfClause) {

        return from(collection(declarations), nullExpression(), asOfClause);
    }

    public static FromClauseTester from(ExpressionTester[] declarations,
                                           ExpressionTester hierarchicalQueryClause,
                                           ExpressionTester asOfClause) {

        return from(collection(declarations), hierarchicalQueryClause, asOfClause);
    }

    /**
     * Example: from("Employee", "e", "Product", "p")
     */
    public static FromClauseTester from(String... declarations) {

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
    public static FromClauseTester from(String abstractSchemaName, String identificationVariable) {
        return from(fromEntity(abstractSchemaName, identificationVariable));
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           AsOfClauseTester asOfClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            nullExpression(),
            asOfClause
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           ConnectByClauseTester connectByClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause(connectByClause),
            nullExpression()
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           ConnectByClauseTester connectByClause,
                                           OrderSiblingsByClauseTester orderSiblingsByClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause(nullExpression(), connectByClause, orderSiblingsByClause),
            nullExpression()
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           ExpressionTester... joins) {

        return from(
            identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins)
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           ExpressionTester joins) {

        return from(fromEntity(abstractSchemaName, identificationVariable, joins));
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           ExpressionTester joins,
                                           AsOfClauseTester asOfClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable, joins),
            nullExpression(),
            asOfClause
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           HierarchicalQueryClauseTester hierarchicalQueryClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause,
            nullExpression()
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           OrderSiblingsByClauseTester orderSiblingsByClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause(nullExpression(), nullExpression(), orderSiblingsByClause),
            nullExpression()
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           StartWithClauseTester startWithClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause(startWithClause, nullExpression()),
            nullExpression()
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           StartWithClauseTester startWithClause,
                                           ConnectByClauseTester connectByClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause(startWithClause, connectByClause),
            nullExpression()
        );
    }

    public static FromClauseTester from(String abstractSchemaName,
                                           String identificationVariable,
                                           StartWithClauseTester startWithClause,
                                           ConnectByClauseTester connectByClause,
                                           OrderSiblingsByClauseTester orderSiblingsByClause) {

        return new FromClauseTester(
            fromEntity(abstractSchemaName, identificationVariable),
            hierarchicalQueryClause(startWithClause, connectByClause, orderSiblingsByClause),
            nullExpression()
        );
    }

    public static FromClauseTester fromAs(String abstractSchemaName, String identificationVariable) {
        return from(identificationVariableDeclarationAs(abstractSchemaName, identificationVariable));
    }

    /**
     * Example: from("e.employees", "e")
     */
    public static IdentificationVariableDeclarationTester fromCollection(String collectionPath,
                                                                            String identificationVariable) {

        return identificationVariableDeclaration(
            rangeVariableDeclaration(
                collectionPath(collectionPath),
                variable(identificationVariable)
            ),
            nullExpression()
        );
    }

    public static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName,
                                                                        String identificationVariable) {

        return identificationVariableDeclaration(abstractSchemaName, identificationVariable);
    }

    public static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName,
                                                                        String identificationVariable,
                                                                        ExpressionTester... joins) {

        return identificationVariableDeclaration(abstractSchemaName, identificationVariable, joins);
    }

    public static IdentificationVariableDeclarationTester fromEntity(String abstractSchemaName,
                                                                        String identificationVariable,
                                                                        ExpressionTester join) {

        return identificationVariableDeclaration(abstractSchemaName, identificationVariable, join);
    }

    public static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName,
                                                                          String identificationVariable) {

        return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable);
    }

    public static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName,
                                                                          String identificationVariable,
                                                                          ExpressionTester... joins) {

        return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, joins);
    }

    public static IdentificationVariableDeclarationTester fromEntityAs(String abstractSchemaName,
                                                                          String identificationVariable,
                                                                          ExpressionTester join) {

        return identificationVariableDeclarationAs(abstractSchemaName, identificationVariable, join);
    }

    public static CollectionMemberDeclarationTester fromIn(ExpressionTester collectionPath,
                                                              ExpressionTester identificationVariable) {

        return new CollectionMemberDeclarationTester(
            collectionPath,
            false,
            identificationVariable
        );
    }

    public static CollectionMemberDeclarationTester fromIn(String collectionPath,
                                                              String identificationVariable) {

        return fromIn(collectionPath(collectionPath), variable(identificationVariable));
    }

    public static CollectionMemberDeclarationTester fromInAs(ExpressionTester collectionPath,
                                                                ExpressionTester identificationVariable) {

        return new CollectionMemberDeclarationTester(
            collectionPath,
            true,
            identificationVariable
        );
    }

    public static CollectionMemberDeclarationTester fromInAs(String collectionPath,
                                                                String identificationVariable) {

        return fromInAs(
            collectionPath(collectionPath),
            variable(identificationVariable)
        );
    }

    public static FunctionExpressionTester function(String identifier,
                                                       String functionName) {

        return new FunctionExpressionTester(identifier, functionName, nullExpression());
    }

    public static FunctionExpressionTester function(String identifier,
                                                       String functionName,
                                                       ExpressionTester... funcItems) {

        return new FunctionExpressionTester(identifier, functionName, collection(funcItems));
    }

    public static FunctionExpressionTester function(String identifier,
                                                       String functionName,
                                                       ExpressionTester funcItem) {

        return new FunctionExpressionTester(identifier, functionName, funcItem);
    }

    public static ComparisonExpressionTester greaterThan(ExpressionTester leftExpression,
                                                            ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.GREATER_THAN, rightExpression);
    }

    public static ComparisonExpressionTester greaterThanOrEqual(ExpressionTester leftExpression,
                                                                   ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.GREATER_THAN_OR_EQUAL, rightExpression);
    }

    public static GroupByClauseTester groupBy(ExpressionTester groupByItem) {
        return new GroupByClauseTester(groupByItem);
    }

    public static GroupByClauseTester groupBy(ExpressionTester... groupByItems) {
        return new GroupByClauseTester(collection(groupByItems));
    }

    public static HavingClauseTester having(ExpressionTester havingItem) {
        return new HavingClauseTester(havingItem);
    }

    public static HierarchicalQueryClauseTester hierarchicalQueryClause(ExpressionTester connectByClause) {
        return hierarchicalQueryClause(nullExpression(), connectByClause, nullExpression());
    }

    public static HierarchicalQueryClauseTester hierarchicalQueryClause(ExpressionTester startWithClause,
                                                                           ExpressionTester connectByClause) {

        return hierarchicalQueryClause(startWithClause, connectByClause, nullExpression());
    }

    public static HierarchicalQueryClauseTester hierarchicalQueryClause(ExpressionTester startWithClause,
                                                                        ExpressionTester connectByClause,
                                                                        ExpressionTester orderSiblingsByClause) {

        return new HierarchicalQueryClauseTester(startWithClause, connectByClause, orderSiblingsByClause);
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration) {
        return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, nullExpression());
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration,
                                                                                            ExpressionTester... joins) {

        if (joins.length == 0) {
            return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, nullExpression());
        }

        if (joins.length == 1) {
            return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, joins[0]);
        }

        return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, collection(joins));
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration,
                                                                                            ExpressionTester joins) {

        return new IdentificationVariableDeclarationTester(rangeVariableDeclaration, joins);
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName,
                                                                                            String identificationVariable) {

        return identificationVariableDeclaration(
            rangeVariableDeclaration(abstractSchemaName, identificationVariable),
            nullExpression()
        );
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName,
                                                                                            String identificationVariable,
                                                                                            ExpressionTester... joins) {

        return new IdentificationVariableDeclarationTester(
            rangeVariableDeclaration(abstractSchemaName, identificationVariable),
            spacedCollection(joins)
        );
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclaration(String abstractSchemaName,
                                                                                               String identificationVariable,
                                                                                               ExpressionTester join) {

        return new IdentificationVariableDeclarationTester(
            rangeVariableDeclaration(abstractSchemaName, identificationVariable),
            join
        );
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName,
                                                                                              String identificationVariable) {

        return new IdentificationVariableDeclarationTester(
            rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
            nullExpression()
        );
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName,
                                                                                              String identificationVariable,
                                                                                              ExpressionTester join) {

        return new IdentificationVariableDeclarationTester(
            rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
            join
        );
    }

    public static IdentificationVariableDeclarationTester identificationVariableDeclarationAs(String abstractSchemaName,
                                                                                              String identificationVariable,
                                                                                              ExpressionTester... joins) {

        return new IdentificationVariableDeclarationTester(
            rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
            spacedCollection(joins)
        );
    }

    public static IdExpressionTester id(ExpressionTester identificationVariable) {
        return new IdExpressionTester(identificationVariable);
    }

    public static IdExpressionTester id(String identificationVariable) {
        return id(variable(identificationVariable));
    }

    public static InExpressionTester in(ExpressionTester stateFieldPathExpression,
                                        ExpressionTester... inItems) {

        return new InExpressionTester(stateFieldPathExpression, false, collection(inItems));
    }

    public static InExpressionTester in(ExpressionTester stateFieldPathExpression,
                                        ExpressionTester inItems) {

        return new InExpressionTester(stateFieldPathExpression, false, inItems);
    }

    public static InExpressionTester in(ExpressionTester stateFieldPathExpression,
                                        String inputParameter) {

        InExpressionTester in = in(stateFieldPathExpression, inputParameter(inputParameter));
        in.hasLeftParenthesis  = false;
        in.hasRightParenthesis = false;
        in.hasSpaceAfterIn     = true;
        return in;
    }

    public static InExpressionTester in(String stateFieldPathExpression,
                                        ExpressionTester... inItems) {

        return in(path(stateFieldPathExpression), inItems);
    }

    public static InExpressionTester in(String stateFieldPathExpression,
                                        ExpressionTester inItem) {

        return in(path(stateFieldPathExpression), inItem);
    }

    public static InExpressionTester in(String pathExpression, String inputParameter) {
        return in(path(pathExpression), inputParameter);
    }

    public static IndexExpressionTester index(ExpressionTester identificationVariable) {
        return new IndexExpressionTester(identificationVariable);
    }

    public static IndexExpressionTester index(String identificationVariable) {
        return index(variable(identificationVariable));
    }

    public static JoinTester innerJoin(ExpressionTester collectionPath,
                                          ExpressionTester identificationVariable) {

        return innerJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoin(ExpressionTester collectionPath,
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

    public static JoinTester innerJoin(ExpressionTester collectionPath,
                                          String identificationVariable) {

        return innerJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoin(ExpressionTester collectionPath,
                                          String identificationVariable,
                                          ExpressionTester joinCondition) {

        return innerJoin(
            collectionPath,
            variable(identificationVariable),
            nullExpression()
        );
    }

    public static JoinTester innerJoin(String collectionPath,
                                          String identificationVariable) {

        return innerJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoin(String collectionPath,
                                          String identificationVariable,
                                          ExpressionTester joinCondition) {

        return innerJoin(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester innerJoinAs(ExpressionTester collectionPath,
                                            ExpressionTester identificationVariable) {

        return innerJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinAs(ExpressionTester collectionPath,
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

    public static JoinTester innerJoinAs(ExpressionTester collectionPath,
                                            String identificationVariable) {

        return innerJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinAs(ExpressionTester collectionPath,
                                            String identificationVariable,
                                            ExpressionTester joinCondition) {

        return innerJoinAs(
            collectionPath,
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester innerJoinAs(String collectionPath,
                                            String identificationVariable) {

        return innerJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinAs(String collectionPath,
                                            String identificationVariable,
                                            ExpressionTester joinCondition) {

        return innerJoinAs(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester innerJoinFetch(ExpressionTester collectionPath) {
        return innerJoinFetch(
            collectionPath,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetch(ExpressionTester collectionPath,
                                               ExpressionTester identificationVariable) {

        return innerJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetch(ExpressionTester collectionPath,
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

    public static JoinTester innerJoinFetch(ExpressionTester collectionPath,
                                               String identificationVariable) {

        return innerJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetch(ExpressionTester collectionPath,
                                               String identificationVariable,
                                               ExpressionTester joinCondition) {

        return innerJoinFetch(
            collectionPath,
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester innerJoinFetch(String collectionPath) {
        return innerJoinFetch(
            collectionPath(collectionPath)
        );
    }

    public static JoinTester innerJoinFetch(String collectionPath,
                                               String identificationVariable) {

        return innerJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetch(String collectionPath,
                                               String identificationVariable,
                                               ExpressionTester joinCondition) {

        return innerJoinFetch(
            collectionPath(collectionPath),
            variable(identificationVariable),
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetchAs(ExpressionTester collectionPath) {
        return innerJoinFetchAs(
            collectionPath,
            nullExpression(),
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
                                                 ExpressionTester identificationVariable) {

        return innerJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
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

    public static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
                                                 String identificationVariable) {

        return innerJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetchAs(ExpressionTester collectionPath,
                                                 String identificationVariable,
                                                 ExpressionTester joinCondition) {

        return innerJoinFetchAs(
            collectionPath,
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester innerJoinFetchAs(String collectionPath) {
        return innerJoinFetchAs(
            collectionPath(collectionPath)
        );
    }

    public static JoinTester innerJoinFetchAs(String collectionPath,
                                                 String identificationVariable) {

        return innerJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester innerJoinFetchAs(String collectionPath,
                                                 String identificationVariable,
                                                 ExpressionTester joinCondition) {

        return innerJoinFetchAs(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static InputParameterTester inputParameter(String inputParameter) {
        return new InputParameterTester(inputParameter);
    }

    public static UnionClauseTester intersect(ExpressionTester subquery) {
        return union(INTERSECT, false, subquery);
    }

    public static UnionClauseTester intersect(ExpressionTester selectClause,
                                                 ExpressionTester fromClause) {

        return intersect(subquery(selectClause, fromClause));
    }

    public static UnionClauseTester intersect(ExpressionTester selectClause,
                                                 ExpressionTester fromClause,
                                                   ExpressionTester whereClause) {

        return intersect(subquery(selectClause, fromClause, whereClause));
    }

    public static UnionClauseTester intersect(ExpressionTester selectClause,
                                                 ExpressionTester fromClause,
                                                   ExpressionTester whereClause,
                                                   ExpressionTester groupByClause,
                                                  ExpressionTester havingClause) {

        return intersect(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
    }

    public static UnionClauseTester intersectAll(ExpressionTester subquery) {
        return union(INTERSECT, true, subquery);
    }

    public static UnionClauseTester intersectAll(ExpressionTester selectClause,
                                                    ExpressionTester fromClause) {

        return intersectAll(subquery(selectClause, fromClause));
    }

    public static UnionClauseTester intersectAll(ExpressionTester selectClause,
                                                    ExpressionTester fromClause,
                                                      ExpressionTester whereClause) {

        return intersectAll(subquery(selectClause, fromClause, whereClause));
    }

    public static UnionClauseTester intersectAll(ExpressionTester selectClause,
                                                    ExpressionTester fromClause,
                                                      ExpressionTester whereClause,
                                                      ExpressionTester groupByClause,
                                                     ExpressionTester havingClause) {

        return intersectAll(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
    }

    public static EmptyCollectionComparisonExpressionTester isEmpty(ExpressionTester collectionPath) {
        return new EmptyCollectionComparisonExpressionTester(collectionPath, false);
    }

    public static EmptyCollectionComparisonExpressionTester isEmpty(String collectionPath) {
        return isEmpty(collectionPath(collectionPath));
    }

    public static EmptyCollectionComparisonExpressionTester isNotEmpty(ExpressionTester collectionPath) {
        return new EmptyCollectionComparisonExpressionTester(collectionPath, true);
    }

    public static EmptyCollectionComparisonExpressionTester isNotEmpty(String collectionPath) {
        return isNotEmpty(collectionPath(collectionPath));
    }

    public static NullComparisonExpressionTester isNotNull(ExpressionTester expression) {
        return new NullComparisonExpressionTester(expression, true);
    }

    public static NullComparisonExpressionTester isNull(ExpressionTester expression) {
        return new NullComparisonExpressionTester(expression, false);
    }

    public static JoinTester join(ExpressionTester collectionPath,
                                     ExpressionTester identificationVariable) {

        return join(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester join(ExpressionTester collectionPath,
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

    public static JoinTester join(ExpressionTester collectionPath,
                                     String identificationVariable) {

        return join(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester join(ExpressionTester collectionPath,
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

    public static JoinTester join(String joinType,
                                  ExpressionTester collectionPath,
                                  ExpressionTester identificationVariable) {

        return join(
            joinType,
            collectionPath,
            false,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester join(String joinType,
                                  ExpressionTester collectionPath,
                                  ExpressionTester identificationVariable,
                                  ExpressionTester joinCondition) {

        return join(
            joinType,
            collectionPath,
            false,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester join(String collectionPath, String identificationVariable) {

        return join(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester join(String collectionPath,
                                  String identificationVariable,
                                  ExpressionTester joinCondition) {

        return join(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester joinAs(ExpressionTester collectionPath,
                                    ExpressionTester identificationVariable) {

        return joinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinAs(ExpressionTester collectionPath,
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

    public static JoinTester joinAs(ExpressionTester collectionPath,
                                       String identificationVariable) {

        return joinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinAs(ExpressionTester collectionPath,
                                       String identificationVariable,
                                       ExpressionTester joinCondition) {

        return joinAs(
            collectionPath,
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester joinAs(String joinType,
                                    ExpressionTester collectionPath,
                                    ExpressionTester identificationVariable) {

        return join(
            joinType,
            collectionPath,
            true,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinAs(String joinType,
                                    ExpressionTester collectionPath,
                                    ExpressionTester identificationVariable,
                                    ExpressionTester joinCondition) {

        return join(
            joinType,
            collectionPath,
            true,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester joinAs(String collectionPath,
                                       String identificationVariable) {

        return joinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinAs(String collectionPath,
                                       String identificationVariable,
                                       ExpressionTester joinCondition) {

        return joinAs(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester joinAsTreat(String collectionPath,
                                            String entityTypeLiteral,
                                            String variable) {

        return joinAs(treat(collectionPath, entityTypeLiteral), variable);
    }

    public static JoinTester joinAsTreatAs(String collectionPath,
                                              String entityTypeLiteral,
                                              String variable) {

        return joinAs(treatAs(collectionPath, entityTypeLiteral), variable);
    }

    public static JoinTester joinFetch(ExpressionTester collectionPath) {
        JoinTester join = joinFetch(
            collectionPath,
            nullExpression()
        );
        join.hasSpaceAfterJoinAssociation = false;
        return join;
    }

    public static JoinTester joinFetch(ExpressionTester collectionPath,
                                          ExpressionTester identificationVariable) {

        return joinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetch(ExpressionTester collectionPath,
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

    public static JoinTester joinFetch(ExpressionTester collectionPath,
                                          String identificationVariable) {

        return joinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetch(ExpressionTester collectionPath,
                                          String identificationVariable,
                                          ExpressionTester joinCondition) {

        return joinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetch(String collectionPath) {
        return joinFetch(
            collectionPath(collectionPath)
        );
    }

    public static JoinTester joinFetch(String collectionPath,
                                          String identificationVariable) {

        return joinFetch(
            collectionPath(collectionPath),
            variable(identificationVariable)
        );
    }

    public static JoinTester joinFetch(String collectionPath,
                                          String identificationVariable,
                                          ExpressionTester joinCondition) {

        return joinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetchAs(ExpressionTester collectionPath,
                                            ExpressionTester identificationVariable) {

        return joinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetchAs(ExpressionTester collectionPath,
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

    public static JoinTester joinFetchAs(ExpressionTester collectionPath,
                                            String identificationVariable) {

        return joinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetchAs(ExpressionTester collectionPath,
                                            String identificationVariable,
                                            ExpressionTester joinCondition) {

        return joinFetchAs(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester joinFetchAs(String collectionPath,
                                            String identificationVariable) {

        return joinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester joinFetchAs(String collectionPath,
                                            String identificationVariable,
                                            ExpressionTester joinCondition) {

        return joinFetchAs(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester joinTreat(String collectionPath,
                                          String entityTypeLiteral,
                                          String variable) {

        return join(treat(collectionPath, entityTypeLiteral), variable);
    }

    public static JoinTester joinTreatAs(String collectionPath,
                                            String entityTypeLiteral,
                                            String variable) {

        return join(treatAs(collectionPath, entityTypeLiteral), variable);
    }

    public static JPQLExpressionTester jpqlExpression(ExpressionTester queryStatement) {
        return jpqlExpression(queryStatement, nullExpression());
    }

    public static JPQLExpressionTester jpqlExpression(ExpressionTester queryStatement,
                                                         ExpressionTester unknownExpression) {

        return new JPQLExpressionTester(queryStatement, unknownExpression);
    }

    public static JoinTester leftJoin(ExpressionTester collectionPath,
                                         ExpressionTester identificationVariable) {

        return leftJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoin(ExpressionTester collectionPath,
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

    public static JoinTester leftJoin(ExpressionTester collectionPath,
                                         String identificationVariable) {

        return leftJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoin(ExpressionTester collectionPath,
                                         String identificationVariable,
                                         ExpressionTester joinCondition) {

        return leftJoin(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftJoin(String collectionPath,
                                         String identificationVariable) {

        return leftJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoin(String collectionPath,
                                         String identificationVariable,
                                         ExpressionTester joinCondition) {

        return leftJoin(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester leftJoinAs(ExpressionTester collectionPath,
                                           ExpressionTester identificationVariable) {

        return leftJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinAs(ExpressionTester collectionPath,
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

    public static JoinTester leftJoinAs(ExpressionTester collectionPath,
                                           String identificationVariable) {

        return leftJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinAs(ExpressionTester collectionPath,
                                           String identificationVariable,
                                           ExpressionTester joinCondition) {

        return leftJoinAs(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftJoinAs(String collectionPath,
                                           String identificationVariable) {

        return leftJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinAs(String collectionPath,
                                           String identificationVariable,
                                           ExpressionTester joinCondition) {

        return leftJoinAs(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftJoinFetch(ExpressionTester collectionPath) {
        JoinTester join = leftJoinFetch(
            collectionPath,
            nullExpression()
        );
        join.hasSpaceAfterJoinAssociation = false;
        return join;
    }

    public static JoinTester leftJoinFetch(ExpressionTester collectionPath,
                                              ExpressionTester identificationVariable) {

        return leftJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetch(ExpressionTester collectionPath,
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

    public static JoinTester leftJoinFetch(ExpressionTester collectionPath,
                                              String identificationVariable) {

        return leftJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetch(ExpressionTester collectionPath,
                                              String identificationVariable,
                                              ExpressionTester joinCondition) {

        return leftJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetch(String collectionPath) {
        return leftJoinFetch(
            collectionPath(collectionPath)
        );
    }

    public static JoinTester leftJoinFetch(String collectionPath,
                                              String identificationVariable) {

        return leftJoinFetch(
            collectionPath(collectionPath),
            variable(identificationVariable)
        );
    }

    public static JoinTester leftJoinFetch(String collectionPath,
                                              String identificationVariable,
                                              ExpressionTester joinCondition) {

        return leftJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
                                                ExpressionTester identificationVariable) {

        return leftJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
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

    public static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
                                                String identificationVariable) {

        return leftJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetchAs(ExpressionTester collectionPath,
                                                String identificationVariable,
                                                ExpressionTester joinCondition) {

        return leftJoinFetchAs(
            collectionPath,
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester leftJoinFetchAs(String collectionPath,
                                                String identificationVariable) {

        return leftJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftJoinFetchAs(String collectionPath,
                                                String identificationVariable,
                                                ExpressionTester joinCondition) {

        return leftJoinFetchAs(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftOuterJoin(ExpressionTester collectionPath,
                                              ExpressionTester identificationVariable) {

        return leftOuterJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoin(ExpressionTester collectionPath,
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

    public static JoinTester leftOuterJoin(ExpressionTester collectionPath,
                                              String identificationVariable) {

        return leftOuterJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoin(ExpressionTester collectionPath,
                                              String identificationVariable,
                                              ExpressionTester joinCondition) {

        return leftOuterJoin(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftOuterJoin(String collectionPath,
                                              String identificationVariable) {

        return leftOuterJoin(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoin(String collectionPath,
                                              String identificationVariable,
                                              ExpressionTester joinCondition) {

        return leftOuterJoin(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester leftOuterJoinAs(ExpressionTester collectionPath,
                                                ExpressionTester identificationVariable) {

        return leftOuterJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinAs(ExpressionTester collectionPath,
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

    public static JoinTester leftOuterJoinAs(String collectionPath,
                                                String identificationVariable) {

        return leftOuterJoinAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinAs(String collectionPath,
                                                String identificationVariable,
                                                ExpressionTester joinCondition) {

        return leftOuterJoinAs(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath) {
        JoinTester join = leftOuterJoinFetch(
            collectionPath,
            nullExpression()
        );
        join.hasSpaceAfterJoinAssociation = false;
        return join;
    }

    public static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
                                                   ExpressionTester identificationVariable) {

        return leftOuterJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
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

    public static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
                                                   String identificationVariable) {

        return leftOuterJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetch(ExpressionTester collectionPath,
                                                   String identificationVariable,
                                                   ExpressionTester joinCondition) {

        return leftOuterJoinFetch(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftOuterJoinFetch(String collectionPath) {
        return leftOuterJoinFetch(
            collectionPath(collectionPath)
        );
    }

    public static JoinTester leftOuterJoinFetch(String collectionPath,
                                                   String identificationVariable) {

        return leftOuterJoinFetch(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetch(String collectionPath,
                                                   String identificationVariable,
                                                   ExpressionTester joinCondition) {

        return leftOuterJoinFetch(
            collectionPath,
            identificationVariable,
            joinCondition
        );
    }

    public static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath) {
        return leftOuterJoinFetchAs(
            collectionPath,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
                                                     ExpressionTester identificationVariable) {

        return leftOuterJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
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

    public static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
                                                     String identificationVariable) {

        return leftOuterJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetchAs(ExpressionTester collectionPath,
                                                     String identificationVariable,
                                                     ExpressionTester joinCondition) {

        return leftOuterJoinFetchAs(
            collectionPath,
            variable(identificationVariable),
            joinCondition
        );
    }

    public static JoinTester leftOuterJoinFetchAs(String collectionPath,
                                                     String identificationVariable) {

        return leftOuterJoinFetchAs(
            collectionPath,
            identificationVariable,
            nullExpression()
        );
    }

    public static JoinTester leftOuterJoinFetchAs(String collectionPath,
                                                     String identificationVariable,
                                                     ExpressionTester joinCondition) {

        return leftOuterJoinFetchAs(
            collectionPath(collectionPath),
            variable(identificationVariable),
            joinCondition
        );
    }

    public static LengthExpressionTester length(ExpressionTester stringPrimary) {
        return new LengthExpressionTester(stringPrimary);
    }

    public static LikeExpressionTester like(ExpressionTester stringExpression,
                                               ExpressionTester patternValue) {

        return like(stringExpression, patternValue, nullExpression());
    }

    public static LikeExpressionTester like(ExpressionTester stringExpression,
                                               ExpressionTester patternValue,
                                               char escapeCharacter) {

        return like(stringExpression, patternValue, string(quote(escapeCharacter)));
    }

    public static LikeExpressionTester like(ExpressionTester stringExpression,
                                               ExpressionTester patternValue,
                                               ExpressionTester escapeCharacter) {

        return new LikeExpressionTester(stringExpression, false, patternValue, escapeCharacter);
    }

    public static LnExpressionTester ln(ExpressionTester simpleArithmeticExpression) {
        return new LnExpressionTester(simpleArithmeticExpression);
    }

    public static LnExpressionTester ln(String statefieldPathExpression) {
        return ln(path(statefieldPathExpression));
    }

    public static LocateExpressionTester locate(ExpressionTester firstExpression,
                                                   ExpressionTester secondExpression) {

        return locate(firstExpression, secondExpression, nullExpression());
    }

    public static LocateExpressionTester locate(ExpressionTester firstExpression,
                                                   ExpressionTester secondExpression,
                                                   ExpressionTester thirdExpression) {

        return new LocateExpressionTester(firstExpression, secondExpression, thirdExpression);
    }

    public static LowerExpressionTester lower(ExpressionTester stringPrimary) {
        return new LowerExpressionTester(stringPrimary);
    }

    public static ComparisonExpressionTester lowerThan(ExpressionTester leftExpression,
                                                          ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.LOWER_THAN, rightExpression);
    }

    public static ComparisonExpressionTester lowerThanOrEqual(ExpressionTester leftExpression,
                                                                 ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.LOWER_THAN_OR_EQUAL, rightExpression);
    }

    public static MaxFunctionTester max(ExpressionTester expression) {
        return new MaxFunctionTester(expression, false);
    }

    public static MaxFunctionTester max(String statefieldPathExpression) {
        return max(path(statefieldPathExpression));
    }

    public static ExpressionTester maxDistinct(String statefieldPathExpression) {
        return new MaxFunctionTester(path(statefieldPathExpression), true);
    }

    public static CollectionMemberExpressionTester member(ExpressionTester entityExpression,
                                                             ExpressionTester collectionPath) {

        return new CollectionMemberExpressionTester(
            entityExpression,
            false,
            false,
            collectionPath
        );
    }

    public static CollectionMemberExpressionTester member(ExpressionTester entityExpression,
                                                             String collectionPath) {

        return member(entityExpression, collectionPath(collectionPath));
    }

    public static CollectionMemberExpressionTester member(String identificationVariable,
                                                             String collectionPath) {

        return member(variable(identificationVariable), collectionPath);
    }

    public static CollectionMemberExpressionTester memberOf(ExpressionTester entityExpression,
                                                               ExpressionTester collectionPath) {

        return new CollectionMemberExpressionTester(
            entityExpression,
            false,
            true,
            collectionPath
        );
    }

    public static CollectionMemberExpressionTester memberOf(ExpressionTester entityExpression,
                                                               String collectionPath) {

        return memberOf(entityExpression, collectionPath(collectionPath));
    }

    public static CollectionMemberExpressionTester memberOf(String identificationVariable,
                                                               String collectionPath) {

        return memberOf( variable(identificationVariable), collectionPath);
    }

    public static MinFunctionTester min(ExpressionTester expression) {
        return new MinFunctionTester(expression, false);
    }

    public static MinFunctionTester min(String statefieldPathExpression) {
        return min(path(statefieldPathExpression));
    }

    public static MinFunctionTester minDistinct(String statefieldPathExpression) {
        return new MinFunctionTester(path(statefieldPathExpression), true);
    }

    public static ArithmeticFactorTester minus(ExpressionTester expression) {
        return new ArithmeticFactorTester(MINUS, expression);
    }

    public static ModExpressionTester mod(ExpressionTester simpleArithmeticExpression1,
                                             ExpressionTester simpleArithmeticExpression2) {

        return new ModExpressionTester(simpleArithmeticExpression1, simpleArithmeticExpression2);
    }

    public static MultiplicationExpressionTester multiplication(ExpressionTester leftExpression,
                                                                   ExpressionTester rightExpression) {

        return new MultiplicationExpressionTester(leftExpression, rightExpression);
    }

    public static ConstructorExpressionTester new_(String className,
                                                      ExpressionTester constructorItem) {

        return new ConstructorExpressionTester(className, constructorItem);
    }

    public static ConstructorExpressionTester new_(String className,
                                                      ExpressionTester... constructorItems) {

        return new ConstructorExpressionTester(className, collection(constructorItems));
    }

    public static NotExpressionTester not(ExpressionTester expression) {
        return new NotExpressionTester(expression);
    }

    public static BetweenExpressionTester notBetween(ExpressionTester expression,
                                                        ExpressionTester lowerBoundExpression,
                                                        ExpressionTester upperBoundExpression) {

        return new BetweenExpressionTester(
            expression,
            true,
            lowerBoundExpression,
            upperBoundExpression
        );
    }

    public static ComparisonExpressionTester notEqual(ExpressionTester leftExpression,
                                                         ExpressionTester rightExpression) {

        return comparison(leftExpression, Expression.NOT_EQUAL, rightExpression);
    }

    public static ExistsExpressionTester notExists(ExpressionTester subquery) {
        return new ExistsExpressionTester(subquery, true);
    }

    public static InExpressionTester notIn(ExpressionTester stateFieldPathExpression,
                                              ExpressionTester inItems) {

        return new InExpressionTester(stateFieldPathExpression, true, inItems);
    }

    public static InExpressionTester notIn(ExpressionTester stateFieldPathExpression,
                                              ExpressionTester... inItems) {

        return new InExpressionTester(stateFieldPathExpression, true, collection(inItems));
    }

    public static InExpressionTester notIn(ExpressionTester stateFieldPathExpression,
                                              String inputParameter) {

        InExpressionTester in = notIn(stateFieldPathExpression, inputParameter(inputParameter));
        in.hasLeftParenthesis  = false;
        in.hasRightParenthesis = false;
        in.hasSpaceAfterIn     = true;
        return in;
    }

    public static InExpressionTester notIn(String stateFieldPathExpression,
                                              ExpressionTester... inItems) {

        return notIn(path(stateFieldPathExpression), collection(inItems));
    }

    public static InExpressionTester notIn(String stateFieldPathExpression,
                                              ExpressionTester inItem) {

        return notIn(path(stateFieldPathExpression), inItem);
    }

    public static InExpressionTester notIn(String pathExpression, String singleInputParameter) {
        return notIn(path(pathExpression), singleInputParameter);
    }

    public static LikeExpressionTester notLike(ExpressionTester stringExpression,
                                                  ExpressionTester patternValue) {

        return notLike(stringExpression, patternValue, nullExpression());
    }

    public static LikeExpressionTester notLike(ExpressionTester stringExpression,
                                                  ExpressionTester patternValue,
                                                  ExpressionTester escapeCharacter) {

        return new LikeExpressionTester(stringExpression, true, patternValue, escapeCharacter);
    }

    public static CollectionMemberExpressionTester notMember(ExpressionTester entityExpression,
                                                                ExpressionTester collectionPath) {

        return new CollectionMemberExpressionTester(
            entityExpression,
            true,
            false,
            collectionPath
        );
    }

    public static CollectionMemberExpressionTester notMember(ExpressionTester entityExpression,
                                                                String collectionPath) {

        return notMember(entityExpression, collectionPath);
    }

    public static CollectionMemberExpressionTester notMember(String identificationVariable,
                                                                String collectionPath) {

        return notMember(variable(identificationVariable), collectionPath);
    }

    public static CollectionMemberExpressionTester notMemberOf(ExpressionTester entityExpression,
                                                                  ExpressionTester collectionPath) {

        return new CollectionMemberExpressionTester(
            entityExpression,
            true,
            true,
            collectionPath
        );
    }

    public static ExpressionTester NULL() {
        return new KeywordExpressionTester(NULL);
    }

    public static ExpressionTester nullExpression() {
        return new NullExpressionTester();
    }

    public static NullIfExpressionTester nullIf(ExpressionTester expression1,
                                                   ExpressionTester expression2) {

        return new NullIfExpressionTester(expression1, expression2);
    }

    public static NumericLiteralTester numeric(double number) {
        return numeric(String.valueOf(number));
    }

    public static NumericLiteralTester numeric(float number) {
        return numeric(String.valueOf(number));
    }

    public static NumericLiteralTester numeric(int number) {
        return numeric(String.valueOf(number));
    }

    public static NumericLiteralTester numeric(long number) {
        return numeric(String.valueOf(number));
    }

    public static NumericLiteralTester numeric(String value) {
        return new NumericLiteralTester(value);
    }

    public static ObjectExpressionTester object(ExpressionTester identificationVariable) {
        return new ObjectExpressionTester(identificationVariable);
    }

    public static ObjectExpressionTester object(String identificationVariable) {
        return object(variable(identificationVariable));
    }

    public static OnClauseTester on(ExpressionTester conditionalExpression) {
        return new OnClauseTester(conditionalExpression);
    }

    public static OrExpressionTester or(ExpressionTester leftExpression,
                                           ExpressionTester rightExpression) {

        return new OrExpressionTester(leftExpression, rightExpression);
    }

    public static OrderByClauseTester orderBy(ExpressionTester orderByItem) {
        return new OrderByClauseTester(orderByItem);
    }

    public static OrderByClauseTester orderBy(ExpressionTester... orderByItems) {
        return new OrderByClauseTester(collection(orderByItems));
    }

    public static OrderByClauseTester orderBy(String stateFieldPathExpression) {
        return new OrderByClauseTester(orderByItem(stateFieldPathExpression));
    }

    public static OrderByItemTester orderByItem(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.DEFAULT, NullOrdering.DEFAULT);
    }

    private static OrderByItemTester orderByItem(ExpressionTester orderByItem,
                                                 Ordering ordering,
                                                 NullOrdering nullOrdering) {

        return new OrderByItemTester(orderByItem, ordering, nullOrdering);
    }

    public static OrderByItemTester orderByItem(String stateFieldPathExpression) {
        return orderByItem(path(stateFieldPathExpression));
    }

    public static OrderByItemTester orderByItemAsc(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.ASC, NullOrdering.DEFAULT);
    }

    public static OrderByItemTester orderByItemAsc(String stateFieldPathExpression) {
        return orderByItemAsc(path(stateFieldPathExpression));
    }

    public static OrderByItemTester orderByItemAscNullsFirst(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.ASC, NullOrdering.NULLS_FIRST);
    }

    public static OrderByItemTester orderByItemAscNullsFirst(String pathExpression) {
        return orderByItemAscNullsFirst(path(pathExpression));
    }

    public static OrderByItemTester orderByItemAscNullsLast(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.ASC, NullOrdering.NULLS_LAST);
    }

    public static OrderByItemTester orderByItemAscNullsLast(String stateFieldPathExpression) {
        return orderByItemAscNullsLast(path(stateFieldPathExpression));
    }

    public static OrderByItemTester orderByItemDesc(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.DESC, NullOrdering.DEFAULT);
    }

    public static OrderByItemTester orderByItemDesc(String stateFieldPathExpression) {
        return orderByItemDesc(path(stateFieldPathExpression));
    }

    public static OrderByItemTester orderByItemDescNullsFirst(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.DESC, NullOrdering.NULLS_FIRST);
    }

    public static OrderByItemTester orderByItemDescNullsFirst(String pathExpression) {
        return orderByItemDescNullsFirst(path(pathExpression));
    }

    public static OrderByItemTester orderByItemDescNullsLast(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.DESC, NullOrdering.NULLS_LAST);
    }

    public static OrderByItemTester orderByItemDescNullsLast(String pathExpression) {
        return orderByItemAscNullsLast(path(pathExpression));
    }

    public static OrderByItemTester orderByItemNullsFirst(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.DEFAULT, NullOrdering.NULLS_FIRST);
    }

    public static OrderByItemTester orderByItemNullsFirst(String pathExpression) {
        return orderByItemNullsFirst(path(pathExpression));
    }

    public static OrderByItemTester orderByItemNullsLast(ExpressionTester orderByItem) {
        return orderByItem(orderByItem, Ordering.DEFAULT, NullOrdering.NULLS_LAST);
    }

    public static OrderByItemTester orderByItemNullsLast(String pathExpression) {
        return orderByItemNullsLast(path(pathExpression));
    }

    public static OrderSiblingsByClauseTester orderSiblingsBy(ExpressionTester expression) {
        return new OrderSiblingsByClauseTester(expression);
    }

    public static OrderSiblingsByClauseTester orderSiblingsBy(ExpressionTester... expressions) {
        return new OrderSiblingsByClauseTester(collection(expressions));
    }

    private static StateFieldPathExpressionTester path(ExpressionTester identificationVariable,
                                                       boolean startsWithDot,
                                                       String pathExpression) {

        return new StateFieldPathExpressionTester(
            identificationVariable,
            startsWithDot ? "." + pathExpression : pathExpression
        );
    }

    public static StateFieldPathExpressionTester path(ExpressionTester identificationVariable,
                                                      String pathExpression) {

        return path(identificationVariable, false, pathExpression);
    }

    public static StateFieldPathExpressionTester path(String pathExpression) {

        int dotIndex = pathExpression.indexOf('.');

        if (dotIndex == 0) {
            return path(nullExpression(), false, pathExpression.substring(1));
        }

        String variable = pathExpression.substring(0, dotIndex);
        String path = pathExpression.substring(dotIndex + 1);
        return path(variable(variable), false, path);
    }

    public static ArithmeticFactorTester plus(ExpressionTester expression) {
        return new ArithmeticFactorTester(PLUS, expression);
    }

    public static PowerExpressionTester power(ExpressionTester simpleArithmeticExpression1, ExpressionTester simpleArithmeticExpression2) {
        return new PowerExpressionTester(simpleArithmeticExpression1, simpleArithmeticExpression2);
    }

    public static PowerExpressionTester power(String statefieldPathExpression1, String statefieldPathExpression2) {
        return power(path(statefieldPathExpression1), path(statefieldPathExpression2));
    }

    public static PowerExpressionTester power(String statefieldPathExpression1, ExpressionTester simpleArithmeticExpression2) {
        return power(path(statefieldPathExpression1), simpleArithmeticExpression2);
    }

    public static String quote(char character) {
        return new StringBuilder(3).append("'").append(character).append("'").toString();
    }

    public static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName) {
        return rangeVariableDeclaration(abstractSchemaName, nullExpression());
    }

    private static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
                                                                           boolean hasAs,
                                                                           ExpressionTester identificationVariable) {

        return new RangeVariableDeclarationTester(abstractSchemaName, hasAs, identificationVariable);
    }

    public static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
                                                                          ExpressionTester identificationVariable) {

        return rangeVariableDeclaration(abstractSchemaName, false, identificationVariable);
    }

    public static RangeVariableDeclarationTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
                                                                          String identificationVariable) {

        return rangeVariableDeclaration(abstractSchemaName, variable(identificationVariable));
    }

    public static RangeVariableDeclarationTester rangeVariableDeclaration(String abstractSchemaName) {
        return rangeVariableDeclaration(
            abstractSchemaName(abstractSchemaName),
            false,
            nullExpression()
        );
    }

    public static RangeVariableDeclarationTester rangeVariableDeclaration(String abstractSchemaName,
                                                                          String identificationVariable) {

        return rangeVariableDeclaration(
            abstractSchemaName(abstractSchemaName),
            false,
            variable(identificationVariable)
        );
    }

    public static RangeVariableDeclarationTester rangeVariableDeclarationAs(ExpressionTester abstractSchemaName,
                                                                            ExpressionTester identificationVariable) {

        return rangeVariableDeclaration(abstractSchemaName, true, identificationVariable);
    }

    public static RangeVariableDeclarationTester rangeVariableDeclarationAs(String abstractSchemaName,
                                                                            String identificationVariable) {

        return rangeVariableDeclarationAs(
            abstractSchemaName(abstractSchemaName),
            variable(identificationVariable)
        );
    }

    public static RegexpExpressionTester regexp(ExpressionTester stringExpression,
                                                ExpressionTester patternValue) {

        return new RegexpExpressionTester(stringExpression, patternValue);
    }

    public static RegexpExpressionTester regexp(ExpressionTester stringExpression,
                                                String patternValue) {

        return regexp(stringExpression, string(patternValue));
    }

    private static ResultVariableTester resultVariable(ExpressionTester selectExpression,
                                                       boolean hasAs,
                                                       ExpressionTester resultVariable) {

        return new ResultVariableTester(selectExpression, hasAs, resultVariable);
    }

    public static ResultVariableTester resultVariable(ExpressionTester selectExpression,
                                                      ExpressionTester resultVariable) {

        return resultVariable(selectExpression, false, resultVariable);
    }

    public static ResultVariableTester resultVariable(ExpressionTester selectExpression,
                                                      String resultVariable) {

        return resultVariable(selectExpression, false, variable(resultVariable));
    }

    public static ResultVariableTester resultVariableAs(ExpressionTester selectExpression,
                                                        ExpressionTester resultVariable) {

        return resultVariable(selectExpression, true, resultVariable);
    }

    public static ResultVariableTester resultVariableAs(ExpressionTester selectExpression,
                                                        String resultVariable) {

        return resultVariableAs(selectExpression, variable(resultVariable));
    }

    public static RoundExpressionTester round(ExpressionTester simpleArithmeticExpression1, ExpressionTester simpleArithmeticExpression2) {
        return new RoundExpressionTester(simpleArithmeticExpression1, simpleArithmeticExpression2);
    }

    public static RoundExpressionTester round(String statefieldPathExpression1, String statefieldPathExpression2) {
        return round(path(statefieldPathExpression1), path(statefieldPathExpression2));
    }

    public static RoundExpressionTester round(String statefieldPathExpression1, ExpressionTester simpleArithmeticExpression2) {
        return round(path(statefieldPathExpression1), simpleArithmeticExpression2);
    }

    public static SelectClauseTester select(ExpressionTester selectExpression) {
        return select(selectExpression, false);
    }

    public static SelectClauseTester select(ExpressionTester... selectExpressions) {
        return new SelectClauseTester(collection(selectExpressions), false);
    }

    private static SelectClauseTester select(ExpressionTester selectExpression, boolean hasDistinct) {
        return new SelectClauseTester(selectExpression, hasDistinct);
    }

    public static SelectClauseTester selectDistinct(ExpressionTester... selectExpressions) {
        return new SelectClauseTester(collection(selectExpressions), true);
    }

    public static SelectClauseTester selectDistinct(ExpressionTester selectExpression) {
        return new SelectClauseTester(selectExpression, true);
    }

    public static SelectClauseTester selectDisting(ExpressionTester selectExpression) {
        return select(selectExpression, true);
    }

    private static SelectStatementTester selectStatement(ExpressionTester selectClause,
                                                         ExpressionTester fromClause,
                                                         ExpressionTester whereClause,
                                                         ExpressionTester groupByClause,
                                                         ExpressionTester havingClause,
                                                         ExpressionTester orderByClause,
                                                         ExpressionTester unionClauses) {

        return new SelectStatementTester(
            selectClause,
            fromClause,
            whereClause,
            groupByClause,
            havingClause,
            orderByClause,
            unionClauses
        );
    }

    private static SelectStatementTester selectStatement(ExpressionTester selectClause,
                                                         ExpressionTester fromClause,
                                                         ExpressionTester whereClause,
                                                         ExpressionTester groupByClause,
                                                         ExpressionTester havingClause,
                                                         ExpressionTester orderByClause,
                                                         ExpressionTester... unionClauses) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            groupByClause,
            havingClause,
            orderByClause,
            spacedCollection(unionClauses)
        );
    }

    public static SelectStatementTester selectStatement(FromClauseTester fromClause) {

        return selectStatement(
            nullExpression(),
            fromClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(FromClauseTester fromClause,
                                                        WhereClauseTester whereClause) {

        return selectStatement(
            nullExpression(),
            fromClause,
            whereClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause) {

        return selectStatement(
            selectClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        CollectionExpressionTester unionClauses) {

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

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        GroupByClauseTester groupByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            groupByClause,
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        GroupByClauseTester groupByClause,
                                                        HavingClauseTester havingClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            groupByClause,
            havingClause,
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        GroupByClauseTester groupByClause,
                                                        HavingClauseTester havingClause,
                                                        OrderByClauseTester orderByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            groupByClause,
            havingClause,
            orderByClause,
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        GroupByClauseTester groupByClause,
                                                        OrderByClauseTester orderByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            groupByClause,
            nullExpression(),
            orderByClause,
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        HavingClauseTester havingClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            nullExpression(),
            havingClause,
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        OrderByClauseTester orderByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            orderByClause,
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
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

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        GroupByClauseTester groupByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            groupByClause,
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        GroupByClauseTester groupByClause,
                                                        HavingClauseTester havingClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            groupByClause,
            havingClause,
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        GroupByClauseTester groupByClause,
                                                        HavingClauseTester havingClause,
                                                        OrderByClauseTester orderByClause) {

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

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        GroupByClauseTester groupByClause,
                                                        OrderByClauseTester orderByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            groupByClause,
            nullExpression(),
            orderByClause,
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        HavingClauseTester havingClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            nullExpression(),
            havingClause,
            nullExpression(),
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        HavingClauseTester havingClause,
                                                        OrderByClauseTester orderByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            nullExpression(),
            havingClause,
            orderByClause,
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        OrderByClauseTester orderByClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            nullExpression(),
            nullExpression(),
            orderByClause,
            nullExpression()
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
                                                        UnionClauseTester unionClause) {

        return selectStatement(
            selectClause,
            fromClause,
            whereClause,
            nullExpression(),
            nullExpression(),
            nullExpression(),
            unionClause
        );
    }

    public static SelectStatementTester selectStatement(SelectClauseTester selectClause,
                                                        FromClauseTester fromClause,
                                                        WhereClauseTester whereClause,
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

    public static UpdateItemTester set(ExpressionTester stateFieldPathExpression,
                                       ExpressionTester newValue) {

        return new UpdateItemTester(stateFieldPathExpression, newValue);
    }

    public static UpdateItemTester set(String pathExpression, ExpressionTester newValue) {

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

    public static SignExpressionTester sign(ExpressionTester simpleArithmeticExpression) {
        return new SignExpressionTester(simpleArithmeticExpression);
    }

    public static SignExpressionTester sign(String statefieldPathExpression) {
        return sign(path(statefieldPathExpression));
    }

    public static SizeExpressionTester size(ExpressionTester collectionPath) {
        return new SizeExpressionTester(collectionPath);
    }

    public static SizeExpressionTester size(String collectionPath) {
        return size(collectionPath(collectionPath));
    }

    public static AllOrAnyExpressionTester some(ExpressionTester subquery) {
        return new AllOrAnyExpressionTester(SOME, subquery);
    }

    public static CollectionExpressionTester spacedCollection(ExpressionTester... expressions) {

        Boolean[] spaces = new Boolean[expressions.length];
        Boolean[] commas = new Boolean[expressions.length];

        Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
        Arrays.fill(commas, 0, expressions.length,     Boolean.FALSE);

        spaces[expressions.length - 1] = Boolean.FALSE;

        return collection(expressions, commas, spaces);
    }

    public static SqrtExpressionTester sqrt(ExpressionTester simpleArithmeticExpression) {
        return new SqrtExpressionTester(simpleArithmeticExpression);
    }

    public static StartWithClauseTester startWith(ExpressionTester expression) {
        return new StartWithClauseTester(expression);
    }

    public static StringLiteralTester string(char literal) {
        return new StringLiteralTester(quote(literal));
    }

    public static StringLiteralTester string(String literal) {
        return new StringLiteralTester(literal);
    }

    public static SubExpressionTester sub(ExpressionTester expression) {
        return new SubExpressionTester(expression);
    }

    public static SimpleFromClauseTester subFrom(ExpressionTester declaration) {
        return new SimpleFromClauseTester(declaration, nullExpression(), nullExpression());
    }

    public static SimpleFromClauseTester subFrom(ExpressionTester... declarations) {
        return new SimpleFromClauseTester(collection(declarations), nullExpression(), nullExpression());
    }

    public static SimpleFromClauseTester subFrom(ExpressionTester declarations,
                                                 HierarchicalQueryClauseTester hierarchicalQueryClause,
                                                 AsOfClauseTester asOfClause) {

        return new SimpleFromClauseTester(declarations, hierarchicalQueryClause, asOfClause);
    }

    public static SimpleFromClauseTester subFrom(ExpressionTester[] declarations,
                                                 AsOfClauseTester asOfClause) {

        return subFrom(collection(declarations), nullExpression(), asOfClause);
    }

    public static SimpleFromClauseTester subFrom(ExpressionTester[] declarations,
                                                 ExpressionTester hierarchicalQueryClause,
                                                 ExpressionTester asOfClause) {

        return subFrom(collection(declarations), hierarchicalQueryClause, asOfClause);
    }

    public static SimpleFromClauseTester subFrom(String abstractSchemaName,
                                                 String identificationVariable) {

        return subFrom(identificationVariableDeclaration(abstractSchemaName, identificationVariable));
    }

    public static SimpleFromClauseTester subFrom(String abstractSchemaName,
                                                 String identificationVariable,
                                                 ExpressionTester... joins) {

        return subFrom(identificationVariableDeclaration(
            abstractSchemaName,
            identificationVariable,
            joins
        ));
    }

    public static SimpleFromClauseTester subFrom(String abstractSchemaName,
                                                 String identificationVariable,
                                                 ExpressionTester joins) {

        return subFrom(identificationVariableDeclaration(
            abstractSchemaName,
            identificationVariable,
            joins
        ));
    }

    public static CollectionMemberDeclarationTester subFromIn(ExpressionTester collectionPath) {

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

    public static CollectionMemberDeclarationTester subFromIn(String collectionPath) {
        return subFromIn(collectionPath(collectionPath));
    }

    public static LeftExpressionTester left(ExpressionTester firstExpression,
                                                  ExpressionTester secondExpression) {

        return new LeftExpressionTester(firstExpression, secondExpression);
    }

    public static RightExpressionTester right(ExpressionTester firstExpression,
                                            ExpressionTester secondExpression) {

        return new RightExpressionTester(firstExpression, secondExpression);
    }

    public static ReplaceExpressionTester replace(ExpressionTester firstExpression,
                                                                   ExpressionTester secondExpression,
                                                                   ExpressionTester thirdExpression) {

        return new ReplaceExpressionTester(firstExpression, secondExpression, thirdExpression);
    }

    public static SimpleSelectStatementTester subquery(ExpressionTester selectClause,
                                                       ExpressionTester fromClause) {

        return subquery(selectClause, fromClause, nullExpression());
    }

    public static SimpleSelectStatementTester subquery(ExpressionTester selectClause,
                                                       ExpressionTester fromClause,
                                                       ExpressionTester whereClause) {

        return subSelectStatement(selectClause, fromClause, whereClause);
    }

    public static SimpleSelectStatementTester subquery(ExpressionTester selectClause,
                                                       ExpressionTester fromClause,
                                                       ExpressionTester whereClause,
                                                       ExpressionTester groupByClause,
                                                       ExpressionTester havingClause) {

        return subSelectStatement(selectClause, fromClause, whereClause, groupByClause, havingClause);
    }

    public static SimpleSelectClauseTester subSelect(ExpressionTester selectExpression) {
        return subSelect(selectExpression, false);
    }

    public static SimpleSelectClauseTester subSelect(ExpressionTester... selectExpressions) {
        return new SimpleSelectClauseTester(collection(selectExpressions), false);
    }

    private static SimpleSelectClauseTester subSelect(ExpressionTester selectExpression,
                                                      boolean hasDistinct) {

        return new SimpleSelectClauseTester(selectExpression, hasDistinct);
    }

    public static SimpleSelectClauseTester subSelectDistinct(ExpressionTester selectExpression) {
        return subSelect(selectExpression, true);
    }

    public static SimpleSelectClauseTester subSelectDistinct(ExpressionTester... selectExpressions) {
        return new SimpleSelectClauseTester(collection(selectExpressions), true);
    }

    public static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
                                                                 ExpressionTester fromClause) {

        return subSelectStatement(
            selectClause,
            fromClause,
            nullExpression(),
            nullExpression(),
            nullExpression()
        );
    }

    public static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
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

    public static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
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

    public static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
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

    public static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
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

    public static SimpleSelectStatementTester subSelectStatement(ExpressionTester selectClause,
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

    public static SubstringExpressionTester substring(ExpressionTester firstExpression,
                                                      ExpressionTester secondExpression) {

        return substring(firstExpression, secondExpression, nullExpression());
    }

    public static SubstringExpressionTester substring(ExpressionTester firstExpression,
                                                      ExpressionTester secondExpression,
                                                      ExpressionTester thirdExpression) {

        return new SubstringExpressionTester(firstExpression, secondExpression, thirdExpression);
    }

    public static SubtractionExpressionTester subtract(ExpressionTester leftExpression,
                                                       ExpressionTester rightExpression) {

        return new SubtractionExpressionTester(leftExpression, rightExpression);
    }

    public static SumFunctionTester sum(ExpressionTester expression) {
        return new SumFunctionTester(expression, false);
    }

    public static SumFunctionTester sum(String statefieldPathExpression) {
        return sum(path(statefieldPathExpression));
    }

    public static SumFunctionTester sumDistinct(String statefieldPathExpression) {
        return new SumFunctionTester(path(statefieldPathExpression), true);
    }

    public static TableExpressionTester table(ExpressionTester tableName) {
        return new TableExpressionTester(tableName);
    }

    public static TableExpressionTester table(String tableName) {
        return table(string(tableName));
    }

    public static TableVariableDeclarationTester tableVariableDeclaration(String tableName) {
        return tableVariableDeclaration(table(tableName), nullExpression());
    }

    public static TableVariableDeclarationTester tableVariableDeclaration(String tableName,
                                                                          String variable) {


        return tableVariableDeclaration(table(tableName), variable(variable));
    }

    public static TableVariableDeclarationTester tableVariableDeclaration(TableExpressionTester tableName,
                                                                          ExpressionTester variable) {

        return new TableVariableDeclarationTester(tableName, false, variable);
    }

    public static TableVariableDeclarationTester tableVariableDeclaration(TableExpressionTester tableName,
                                                                          String variable) {

        return tableVariableDeclaration(tableName, variable(variable));
    }

    public static TableVariableDeclarationTester tableVariableDeclarationAs(String tableName,
                                                                            String variable) {

        return tableVariableDeclarationAs(table(tableName), variable(variable));
    }

    public static TableVariableDeclarationTester tableVariableDeclarationAs(TableExpressionTester tableName,
                                                                            ExpressionTester variable) {

        return new TableVariableDeclarationTester(tableName, true, variable);
    }

    public static TableVariableDeclarationTester tableVariableDeclarationAs(TableExpressionTester tableName,
                                                                            String variable) {

        return tableVariableDeclarationAs(tableName, variable(variable));
    }

    public static TreatExpressionTester treat(ExpressionTester pathExpression,
                                              ExpressionTester entityTypeName) {

        return new TreatExpressionTester(pathExpression, false, entityTypeName);
    }

    public static TreatExpressionTester treat(ExpressionTester pathExpression,
                                              String entityTypeName) {

        return treat(pathExpression, entity(entityTypeName));
    }

    public static TreatExpressionTester treat(String pathExpression,
                                              String entityTypeName) {

        return treat(collectionPath(pathExpression), entity(entityTypeName));
    }

    public static TreatExpressionTester treatAs(ExpressionTester pathExpression,
                                                ExpressionTester entityTypeName) {

        return new TreatExpressionTester(pathExpression, true, entityTypeName);
    }

    public static TreatExpressionTester treatAs(ExpressionTester pathExpression,
                                                String entityTypeName) {

        return treatAs(pathExpression, entity(entityTypeName));
    }

    public static TreatExpressionTester treatAs(String pathExpression, String entityTypeName) {
        return treatAs(collectionPath(pathExpression), entity(entityTypeName));
    }

    public static TrimExpressionTester trim(char trimCharacter, ExpressionTester stringPrimary) {
        return trim(string(quote(trimCharacter)), stringPrimary);
    }

    public static TrimExpressionTester trim(ExpressionTester stringPrimary) {
        return trim(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trim(ExpressionTester trimCharacter,
                                            ExpressionTester stringPrimary) {

        return trim(Specification.DEFAULT, trimCharacter, false, stringPrimary);
    }

    private static TrimExpressionTester trim(Specification specification,
                                             ExpressionTester trimCharacter,
                                             boolean hasFrom,
                                             ExpressionTester stringPrimary) {

        return new TrimExpressionTester(specification, stringPrimary, trimCharacter, hasFrom);
    }

    public static TrimExpressionTester trimBoth(ExpressionTester stringPrimary) {
        return trim(Specification.BOTH, nullExpression(), false, stringPrimary);
    }

    public static TrimExpressionTester trimBothFrom(char trimCharacter,
                                                    ExpressionTester stringPrimary) {

        return trimBothFrom(string(quote(trimCharacter)), stringPrimary);
    }

    public static TrimExpressionTester trimBothFrom(ExpressionTester stringPrimary) {
        return trimBothFrom(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trimBothFrom(ExpressionTester trimCharacter,
                                                    ExpressionTester stringPrimary) {

        return trim(Specification.BOTH, trimCharacter, true, stringPrimary);
    }

    public static TrimExpressionTester trimFrom(char trimCharacter, ExpressionTester stringPrimary) {
        return trimFrom(string(trimCharacter), stringPrimary);
    }

    public static TrimExpressionTester trimFrom(ExpressionTester stringPrimary) {
        return trimFrom(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trimFrom(ExpressionTester trimCharacter,
                                                ExpressionTester stringPrimary) {

        return trim(Specification.DEFAULT, trimCharacter, true, stringPrimary);
    }

    public static TrimExpressionTester trimLeading(char trimCharacter,
                                                   ExpressionTester stringPrimary) {

        return trimLeading(string(quote(trimCharacter)), stringPrimary);
    }

    public static TrimExpressionTester trimLeading(ExpressionTester stringPrimary) {
        return trimLeading(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trimLeading(ExpressionTester trimCharacter,
                                                   ExpressionTester stringPrimary) {

        return trim(Specification.LEADING, trimCharacter, false, stringPrimary);
    }

    public static TrimExpressionTester trimLeadingFrom(char trimCharacter,
                                                       ExpressionTester stringPrimary) {

        return trimLeadingFrom(string(quote(trimCharacter)), stringPrimary);
    }

    public static TrimExpressionTester trimLeadingFrom(ExpressionTester stringPrimary) {
        return trimLeadingFrom(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trimLeadingFrom(ExpressionTester trimCharacter,
                                                       ExpressionTester stringPrimary) {

        return trim(Specification.LEADING, trimCharacter, true, stringPrimary);
    }

    public static TrimExpressionTester trimTrailing(char trimCharacter,
                                                    ExpressionTester stringPrimary) {

        return trimTrailing(string(quote(trimCharacter)), stringPrimary);
    }

    public static TrimExpressionTester trimTrailing(ExpressionTester stringPrimary) {
        return trimTrailing(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trimTrailing(ExpressionTester trimCharacter,
                                                    ExpressionTester stringPrimary) {

        return trim(Specification.TRAILING, trimCharacter, false, stringPrimary);
    }

    public static TrimExpressionTester trimTrailingFrom(char trimCharacter,
                                                        ExpressionTester stringPrimary) {

        return trimTrailingFrom(string(quote(trimCharacter)), stringPrimary);
    }

    public static TrimExpressionTester trimTrailingFrom(ExpressionTester stringPrimary) {
        return trimTrailingFrom(nullExpression(), stringPrimary);
    }

    public static TrimExpressionTester trimTrailingFrom(ExpressionTester trimCharacter,
                                                        ExpressionTester stringPrimary) {

        return trim(Specification.TRAILING, trimCharacter, true, stringPrimary);
    }

    public static ExpressionTester TRUE() {
        return new KeywordExpressionTester(TRUE);
    }

    public static TypeExpressionTester type(ExpressionTester identificationVariable) {
        return new TypeExpressionTester(identificationVariable);
    }

    public static TypeExpressionTester type(String identificationVariable) {
        return type(variable(identificationVariable));
    }

    public static UnionClauseTester union(ExpressionTester subquery) {
        return union(UNION, false, subquery);
    }

    public static UnionClauseTester union(ExpressionTester selectClause,
                                             ExpressionTester fromClause) {

        return union(subquery(selectClause, fromClause));
    }

    public static UnionClauseTester union(ExpressionTester selectClause,
                                          ExpressionTester fromClause,
                                          ExpressionTester whereClause) {

        return union(subquery(selectClause, fromClause, whereClause));
    }

    public static UnionClauseTester union(ExpressionTester selectClause,
                                          ExpressionTester fromClause,
                                          ExpressionTester whereClause,
                                          ExpressionTester groupByClause,
                                          ExpressionTester havingClause) {

        return union(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
    }

    public static UnionClauseTester union(String identifier,
                                          boolean hasAll,
                                          ExpressionTester subquery) {

        return new UnionClauseTester(identifier, hasAll, subquery);
    }

    public static UnionClauseTester unionAll(ExpressionTester subquery) {
        return union(UNION, true, subquery);
    }

    public static UnionClauseTester unionAll(ExpressionTester selectClause,
                                             ExpressionTester fromClause) {

        return unionAll(subquery(selectClause, fromClause));
    }

    public static UnionClauseTester unionAll(ExpressionTester selectClause,
                                             ExpressionTester fromClause,
                                             ExpressionTester whereClause) {

        return unionAll(subquery(selectClause, fromClause, whereClause));
    }

    public static UnionClauseTester unionAll(ExpressionTester selectClause,
                                             ExpressionTester fromClause,
                                             ExpressionTester whereClause,
                                             ExpressionTester groupByClause,
                                             ExpressionTester havingClause) {

        return unionAll(subquery(selectClause, fromClause, whereClause, groupByClause, havingClause));
    }

    public static UnknownExpressionTester unknown(String unknown) {
        return new UnknownExpressionTester(unknown);
    }

    public static UpdateClauseTester update(ExpressionTester rangeVariableDeclaration,
                                            ExpressionTester updateItem) {

        return new UpdateClauseTester(
            rangeVariableDeclaration,
            updateItem
        );
    }

    public static UpdateClauseTester update(ExpressionTester rangeVariableDeclaration,
                                            ExpressionTester... updateItems) {

        return new UpdateClauseTester(
            rangeVariableDeclaration,
            collection(updateItems)
        );
    }

    public static UpdateClauseTester update(String abstractSchemaName,
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

    public static UpdateClauseTester update(String abstractSchemaName,
                                            String virtualVariable,
                                            ExpressionTester updateItem,
                                            boolean dummy) {

        UpdateClauseTester updateClause = update(
                rangeVariableDeclaration(
                        abstractSchemaName(abstractSchemaName),
                        virtualVariable(virtualVariable)
                ),
                updateItem
        );
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;
        return updateClause;
    }

    public static UpdateClauseTester update(String abstractSchemaName,
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

    public static UpdateClauseTester update(String abstractSchemaName,
                                            String identificationVariable,
                                            ExpressionTester updateItem) {

        return new UpdateClauseTester(
            rangeVariableDeclaration(abstractSchemaName, identificationVariable),
            updateItem
        );
    }

    public static UpdateClauseTester update(String abstractSchemaName,
                                            String identificationVariable,
                                            ExpressionTester... updateItems) {

        return new UpdateClauseTester(
            rangeVariableDeclaration(abstractSchemaName, identificationVariable),
            collection(updateItems)
        );
    }

    public static UpdateClauseTester updateAs(String abstractSchemaName,
                                              ExpressionTester updateItem) {

        return update(
            rangeVariableDeclarationAs(
                abstractSchemaName(abstractSchemaName),
                nullExpression()),
            updateItem
        );
    }

    public static UpdateClauseTester updateAs(String abstractSchemaName,
                                              String identificationVariable) {

        return update(
            rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
            nullExpression()
        );
    }

    public static UpdateClauseTester updateAs(String abstractSchemaName,
                                              String identificationVariable,
                                              ExpressionTester updateItem) {

        return update(
            rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
            updateItem
        );
    }

    public static UpdateClauseTester updateAs(String abstractSchemaName,
                                              String identificationVariable,
                                              ExpressionTester... updateItems) {

        return update(
            rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
            updateItems
        );
    }

    public static UpdateStatementTester updateStatement(ExpressionTester updateClause) {
        return updateStatement(updateClause, nullExpression());
    }

    public static UpdateStatementTester updateStatement(ExpressionTester updateClause,
                                                        ExpressionTester whereClause) {

        return new UpdateStatementTester(updateClause, whereClause);
    }

    public static UpperExpressionTester upper(ExpressionTester stringPrimary) {
        return new UpperExpressionTester(stringPrimary);
    }

    public static IdentificationVariableTester variable(String identificationVariable) {

        if (identificationVariable.startsWith("{")) {
            return virtualVariable(identificationVariable);
        }

        return new IdentificationVariableTester(identificationVariable, false, nullExpression());
    }

    public static VersionExpressionTester version(ExpressionTester identificationVariable) {
        return new VersionExpressionTester(identificationVariable);
    }

    public static VersionExpressionTester version(String identificationVariable) {
        return version(variable(identificationVariable));
    }

    public static IdentificationVariableTester virtualVariable(String identificationVariable) {

        if (identificationVariable.startsWith("{")) {
            identificationVariable = identificationVariable.substring(1, identificationVariable.length() - 1);
        }

        return new IdentificationVariableTester(identificationVariable, true, nullExpression());
    }

    public static IdentificationVariableTester virtualVariable(String identificationVariable,
                                                               String pathExpression) {

        StateFieldPathExpressionTester path = path(
            virtualVariable(identificationVariable),
            pathExpression
        );

        return new IdentificationVariableTester(pathExpression, true, path);
    }

    public static WhenClauseTester when(ExpressionTester conditionalExpression,
                                        ExpressionTester thenExpression) {

        return new WhenClauseTester(conditionalExpression, thenExpression);
    }

    public static WhereClauseTester where(ExpressionTester conditionalExpression) {
        return new WhereClauseTester(conditionalExpression);
    }
}
