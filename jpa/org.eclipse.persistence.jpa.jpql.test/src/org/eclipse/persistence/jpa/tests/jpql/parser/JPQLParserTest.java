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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractOrderByClause;
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
import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
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
import org.eclipse.persistence.jpa.jpql.parser.ConnectByClause;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.DefaultJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
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
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
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
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
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
import org.eclipse.persistence.jpa.jpql.parser.StartWithClause;
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
import org.eclipse.persistence.jpa.tests.jpql.JPQLBasicTest;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBuilder.*;
import static org.junit.Assert.*;

/**
 * This abstract class provides the functionality to test the parsed tree representation of a JPQL
 * query by traversing the tree and comparing each node.
 * <p>
 * Note: This class provides the {@link ExpressionTester} for all JPQL grammars (1.0, 2.0 and 2.1),
 * as well as for EclipseLink (all versions).
 *
 * @version 2.6
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused" /* For the extra import statement, see bug 330740 */ })
public abstract class JPQLParserTest extends JPQLBasicTest {

    @JPQLGrammarTestHelper
    private JPQLGrammar jpqlGrammar;

    protected JPQLGrammar getGrammar() {
        return jpqlGrammar;
    }

    protected final boolean isEclipseLinkProvider() {
        return DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME == jpqlGrammar.getProvider();
    }

    protected final boolean isGenericProvider() {
        return DefaultJPQLGrammar.PROVIDER_NAME == jpqlGrammar.getProvider();
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

    public static final class AbsExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static abstract class AbstractConditionalClauseTester extends AbstractExpressionTester {

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

    public static abstract class AbstractDoubleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {

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

    public static abstract class AbstractEncapsulatedExpressionTester extends AbstractExpressionTester {

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
    public static abstract class AbstractExpressionTester implements ExpressionTester {

        public final AdditionExpressionTester add(ExpressionTester expression) {
            return JPQLParserTester.add(this, expression);
        }

        public final AndExpressionTester and(ExpressionTester expression) {
            return JPQLParserTester.and(this, expression);
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

            return JPQLParserTester.between(this, lowerBoundExpression, upperBoundExpression);
        }

        public final ComparisonExpressionTester different(ExpressionTester expression) {
            return JPQLParserTester.different(this, expression);
        }

        public final DivisionExpressionTester divide(ExpressionTester expression) {
            return JPQLParserTester.division(this, expression);
        }

        public final ComparisonExpressionTester equal(ExpressionTester expression) {
            return JPQLParserTester.equal(this, expression);
        }

        public final ComparisonExpressionTester greaterThan(ExpressionTester expression) {
            return JPQLParserTester.greaterThan(this, expression);
        }

        public final ComparisonExpressionTester greaterThanOrEqual(ExpressionTester expression) {
            return JPQLParserTester.greaterThanOrEqual(this, expression);
        }

        public final InExpressionTester in(ExpressionTester... inItems) {
            if (inItems.length == 1) {
                return JPQLParserTester.in(this, inItems[0]);
            }
            return JPQLParserTester.in(this, inItems);
        }

        public final InExpressionTester in(String inputParameter) {
            return JPQLParserTester.in(this, inputParameter);
        }

        public final EmptyCollectionComparisonExpressionTester isEmpty() {
            return JPQLParserTester.isEmpty(this);
        }

        public final EmptyCollectionComparisonExpressionTester isNotEmpty() {
            return JPQLParserTester.isNotEmpty(this);
        }

        public boolean isNull() {
            return false;
        }

        public final LikeExpressionTester like(ExpressionTester patternValue) {
            return JPQLParserTester.like(this, patternValue);
        }

        public final LikeExpressionTester like(ExpressionTester patternValue,
                                               ExpressionTester escapeCharacter) {

            return JPQLParserTester.like(this, patternValue, escapeCharacter);
        }

        public final ComparisonExpressionTester lowerThan(ExpressionTester expression) {
            return JPQLParserTester.lowerThan(this, expression);
        }

        public final ComparisonExpressionTester lowerThanOrEqual(ExpressionTester expression) {
            return JPQLParserTester.lowerThanOrEqual(this, expression);
        }

        public final CollectionMemberExpressionTester member(ExpressionTester collectionPath) {
            return JPQLParserTester.member(this, collectionPath);
        }

        public final CollectionMemberExpressionTester memberOf(ExpressionTester collectionPath) {
            return JPQLParserTester.memberOf(this, collectionPath);
        }

        public final MultiplicationExpressionTester multiply(ExpressionTester expression) {
            return JPQLParserTester.multiplication(this, expression);
        }

        public final BetweenExpressionTester notBetween(ExpressionTester lowerBoundExpression,
                                                        ExpressionTester upperBoundExpression) {

            return JPQLParserTester.notBetween(this, lowerBoundExpression, upperBoundExpression);
        }

        public final ComparisonExpressionTester notEqual(ExpressionTester expression) {
            return JPQLParserTester.notEqual(this, expression);
        }

        public final InExpressionTester notIn(ExpressionTester... inItems) {
            if (inItems.length == 1) {
                return JPQLParserTester.notIn(this, inItems[0]);
            }
            return JPQLParserTester.notIn(this, inItems);
        }

        public final InExpressionTester notIn(String inputParameter) {
            return JPQLParserTester.notIn(this, inputParameter);
        }

        public final LikeExpressionTester notLike(ExpressionTester expression) {
            return JPQLParserTester.notLike(this, expression);
        }

        public final LikeExpressionTester notLike(ExpressionTester expression,
                                                  ExpressionTester escapeCharacter) {

            return JPQLParserTester.notLike(this, expression, escapeCharacter);
        }

        public final ExpressionTester notMember(ExpressionTester collectionPath) {
            return JPQLParserTester.notMember(this, collectionPath);
        }

        public final ExpressionTester notMemberOf(ExpressionTester collectionPath) {
            return JPQLParserTester.notMemberOf(this, collectionPath);
        }

        public final OrExpressionTester or(ExpressionTester expression) {
            return JPQLParserTester.or(this, expression);
        }

        public final RegexpExpressionTester regexp(StringLiteralTester patternValue) {
            return JPQLParserTester.regexp(this, patternValue);
        }

        public final SubtractionExpressionTester subtract(ExpressionTester expression) {
            return JPQLParserTester.subtract(this, expression);
        }
    }

    public static abstract class AbstractFromClauseTester extends AbstractExpressionTester {

        private ExpressionTester asOfClause;
        private ExpressionTester declaration;
        private boolean hasSpaceAfterDeclaration;
        public boolean hasSpaceAfterFrom;
        private boolean hasSpaceAfterHierarchicalQueryClause;
        private ExpressionTester hierarchicalQueryClause;

        protected AbstractFromClauseTester(ExpressionTester declaration,
                                           ExpressionTester hierarchicalQueryClause,
                                           ExpressionTester asOfClause) {
            super();
            this.hasSpaceAfterFrom                    = true;
            this.asOfClause                           = asOfClause;
            this.declaration                          = declaration;
            this.hierarchicalQueryClause              = hierarchicalQueryClause;
            this.hasSpaceAfterDeclaration             = !declaration.isNull() && (!hierarchicalQueryClause.isNull() || !asOfClause.isNull());
            this.hasSpaceAfterHierarchicalQueryClause = !hierarchicalQueryClause.isNull() && !asOfClause.isNull();
        }

        public void test(Expression expression) {
            assertInstance(expression, AbstractFromClause.class);

            AbstractFromClause fromClause = (AbstractFromClause) expression;
            assertEquals(toString(),                           fromClause.toParsedText());
            assertEquals(hasSpaceAfterFrom,                    fromClause.hasSpaceAfterFrom());
            assertEquals(!declaration.isNull(),                fromClause.hasDeclaration());
            assertEquals(hasSpaceAfterDeclaration,             fromClause.hasSpaceAfterDeclaration());
            assertEquals(!hierarchicalQueryClause.isNull(),    fromClause.hasHierarchicalQueryClause());
            assertEquals(hasSpaceAfterHierarchicalQueryClause, fromClause.hasSpaceAfterHierarchicalQueryClause());
            assertEquals(!asOfClause.isNull(),                 fromClause.hasAsOfClause());

            declaration.test(fromClause.getDeclaration());
            hierarchicalQueryClause.test(fromClause.getHierarchicalQueryClause());
            asOfClause.test(fromClause.getAsOfClause());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(FROM);
            if (hasSpaceAfterFrom) {
                sb.append(SPACE);
            }
            sb.append(declaration);
            if (hasSpaceAfterDeclaration) {
                sb.append(SPACE);
            }
            sb.append(hierarchicalQueryClause);
            if (hasSpaceAfterHierarchicalQueryClause) {
                sb.append(SPACE);
            }
            sb.append(asOfClause);
            return sb.toString();
        }
    }

    public static abstract class AbstractOrderByClauseTester extends AbstractExpressionTester {

        public boolean hasSpaceAfterIdentifier;
        private ExpressionTester orderByItems;

        protected AbstractOrderByClauseTester(ExpressionTester orderByItems) {
            super();
            this.orderByItems            = orderByItems;
            this.hasSpaceAfterIdentifier = !orderByItems.isNull();
        }

        protected abstract String identifier();

        public void test(Expression expression) {
            assertInstance(expression, AbstractOrderByClause.class);

            AbstractOrderByClause orderByClause = (AbstractOrderByClause) expression;
            assertEquals(toString(),              orderByClause.toParsedText());
            assertEquals(!orderByItems.isNull(),  orderByClause.hasOrderByItems());
            assertEquals(hasSpaceAfterIdentifier, orderByClause.hasSpaceAfterIdentifier());

            orderByItems.test(orderByClause.getOrderByItems());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(identifier());
            if (hasSpaceAfterIdentifier) {
                sb.append(SPACE);
            }
            sb.append(orderByItems);
            return sb.toString();
        }
    }

    public static abstract class AbstractPathExpressionTester extends AbstractExpressionTester {

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

    public static final class AbstractSchemaNameTester extends AbstractExpressionTester {

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

    public static abstract class AbstractSelectClauseTester extends AbstractExpressionTester {

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

    public static abstract class AbstractSelectStatementTester extends AbstractExpressionTester {

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

            this.selectClause            = selectClause;
            this.fromClause              = fromClause;
            this.whereClause             = whereClause;
            this.groupByClause           = groupByClause;
            this.havingClause            = havingClause;

            hasSpaceAfterSelect  = !fromClause.isNull();
            hasSpaceAfterFrom    = !fromClause.isNull() && (!whereClause.isNull() || !groupByClause.isNull() || !havingClause.isNull());
            hasSpaceAfterWhere   = !whereClause.isNull() && (!groupByClause.isNull() || !havingClause.isNull());
            hasSpaceAfterGroupBy = !groupByClause.isNull() && !havingClause.isNull();
        }

        protected abstract Class<? extends AbstractSelectStatement> expressionType();

        public void test(Expression expression) {
            assertInstance(expression, expressionType());

            AbstractSelectStatement selectStatement = (AbstractSelectStatement) expression;
            assertEquals(toString(),                        selectStatement.toParsedText());
            assertEquals(!selectClause .isNull(), selectStatement.hasSelectClause());
            assertEquals(!fromClause   .isNull(), selectStatement.hasFromClause());
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

            // SELECT
            sb.append(selectClause);

            if (hasSpaceAfterSelect && (sb.length() > 0) && (sb.charAt(sb.length() - 1) != ' ')) {
                sb.append(SPACE);
            }

            // FROM
            sb.append(fromClause);

            if (hasSpaceAfterFrom && sb.charAt(sb.length() - 1) != ' ') {
                sb.append(SPACE);
            }

            // WHERE
            sb.append(whereClause);

            if (hasSpaceAfterWhere && sb.charAt(sb.length() - 1) != ' ') {
                sb.append(SPACE);
            }

            // GROUP BY
            sb.append(groupByClause);

            if (hasSpaceAfterGroupBy && sb.charAt(sb.length() - 1) != ' ') {
                sb.append(SPACE);
            }

            // HAVING
            sb.append(havingClause);

            return sb.toString();
        }
    }

    public static abstract class AbstractSingleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {

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

    public static abstract class AbstractTripleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester {

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

    public static final class AdditionExpressionTester extends CompoundExpressionTester {

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

    public static abstract class AggregateFunctionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class AllOrAnyExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class AndExpressionTester extends LogicalExpressionTester {

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

    public static final class ArithmeticFactorTester extends AbstractExpressionTester {

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
            assertEquals(sign == MINUS, factor.isNegative());
            assertEquals(sign == PLUS,  factor.isPositive());

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

    protected final static class AsOfClauseTester extends AbstractExpressionTester {

        private String category;
        private ExpressionTester expression;
        public boolean hasSpaceAfterCategory;
        public boolean hasSpaceAfterIdentifier;

        protected AsOfClauseTester(String category, ExpressionTester expression) {
            super();
            this.category                = category;
            this.expression              = expression;
            this.hasSpaceAfterIdentifier = (category != null) || !expression.isNull();
            this.hasSpaceAfterCategory   = (category != null) && !expression.isNull();
        }

        public void test(Expression expression) {
            assertInstance(expression, AsOfClause.class);

            AsOfClause asOfClause = (AsOfClause) expression;
            assertEquals(toString(),                asOfClause.toParsedText());
            assertEquals(!this.expression.isNull(), asOfClause.hasExpression());
            assertEquals(hasSpaceAfterIdentifier,   asOfClause.hasSpaceAfterIdentifier());
            assertEquals(hasSpaceAfterCategory,     asOfClause.hasSpaceAfterCategory());

            this.expression.test(asOfClause.getExpression());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(AS_OF);
            if (hasSpaceAfterIdentifier) {
                sb.append(SPACE);
            }
            if (category != null) {
                sb.append(category);
            }
            if (hasSpaceAfterCategory) {
                sb.append(SPACE);
            }
            sb.append(expression);
            return sb.toString();
        }
    }

    public static final class AvgFunctionTester extends AggregateFunctionTester {

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

    public static final class BadExpressionTester extends AbstractExpressionTester {

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

    public static final class BetweenExpressionTester extends AbstractExpressionTester {

        private ExpressionTester expression;
        public boolean hasAnd;
        public boolean hasBetween;
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
            this.expression              = expression;
            this.hasSpaceAfterAnd        = true;
            this.hasSpaceAfterLowerBound = true;
            this.hasSpaceAfterBetween    = true;
            this.lowerBoundExpression    = lowerBoundExpression;
            this.upperBoundExpression    = upperBoundExpression;
        }

        public void test(Expression expression) {
            assertInstance(expression, BetweenExpression.class);

            BetweenExpression betweenExpression = (BetweenExpression) expression;
            assertEquals(toString(),                     betweenExpression.toParsedText());
            assertEquals(!this.expression.isNull(),      betweenExpression.hasExpression());
            assertEquals(hasNot,                         betweenExpression.hasNot());
            assertEquals(hasAnd,                         betweenExpression.hasAnd());
            assertEquals(!lowerBoundExpression.isNull(), betweenExpression.hasLowerBoundExpression());
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

    public static final class CaseExpressionTester extends AbstractExpressionTester {

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

    public static final class CastExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class CoalesceExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class CollectionExpressionTester extends AbstractExpressionTester {

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

    public static final class CollectionMemberDeclarationTester extends AbstractExpressionTester {

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

    public static final class CollectionMemberExpressionTester extends AbstractExpressionTester {

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

    public static final class CollectionValuedPathExpressionTester extends AbstractPathExpressionTester {

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

    public static final class ComparisonExpressionTester extends AbstractExpressionTester {

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

    public static abstract class CompoundExpressionTester extends AbstractExpressionTester {

        public boolean hasSpaceAfterIdentifier;
        private ExpressionTester leftExpression;
        private ExpressionTester rightExpression;

        protected CompoundExpressionTester(ExpressionTester leftExpression,
                                           ExpressionTester rightExpression) {

            super();
            this.leftExpression          = leftExpression;
            this.rightExpression         = rightExpression;
            this.hasSpaceAfterIdentifier = !rightExpression.isNull();
        }

        protected abstract Class<? extends CompoundExpression> expressionType();

        protected abstract String identifier();

        public void test(Expression expression) {
            assertInstance(expression, expressionType());

            CompoundExpression compoundExpression = (CompoundExpression) expression;
            assertEquals(toString(),                compoundExpression.toParsedText());
            assertEquals(!leftExpression.isNull(),  compoundExpression.hasLeftExpression());
            assertEquals(!rightExpression.isNull(), compoundExpression.hasRightExpression());
            assertEquals(hasSpaceAfterIdentifier,   compoundExpression.hasSpaceAfterIdentifier());

            leftExpression .test(compoundExpression.getLeftExpression());
            rightExpression.test(compoundExpression.getRightExpression());
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            if (!leftExpression.isNull()) {
                sb.append(leftExpression);
                if (sb.charAt(sb.length() - 1) != SPACE) {
                    sb.append(SPACE);
                }
            }

            sb.append(identifier());

            if (hasSpaceAfterIdentifier) {
                sb.append(SPACE);
            }

            sb.append(rightExpression);
            return sb.toString();
        }
    }

    public static final class ConcatExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class ConnectByClauseTester extends AbstractExpressionTester {

        private ExpressionTester expression;
        public boolean hasSpaceAfterConnectBy;

        protected ConnectByClauseTester(ExpressionTester expression) {
            super();
            this.expression             = expression;
            this.hasSpaceAfterConnectBy = !expression.isNull();
        }

        public void test(Expression expression) {
            assertInstance(expression, ConnectByClause.class);

            ConnectByClause priorExpression = (ConnectByClause) expression;
            assertEquals(toString(),                priorExpression.toParsedText());
            assertEquals(hasSpaceAfterConnectBy,    priorExpression.hasSpaceAfterConnectBy());
            assertEquals(!this.expression.isNull(), priorExpression.hasExpression());

            this.expression.test(priorExpression.getExpression());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(CONNECT_BY);
            if (hasSpaceAfterConnectBy) {
                sb.append(SPACE);
            }
            sb.append(expression);
            return sb.toString();
        }
    }

    public static final class ConstructorExpressionTester extends AbstractExpressionTester {

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

    public static final class CountFunctionTester extends AggregateFunctionTester {

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

    public static final class DatabaseTypeTester extends AbstractDoubleEncapsulatedExpressionTester {

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

    public static final class DateTimeTester extends AbstractExpressionTester {

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
    public static final class DeleteClauseTester extends AbstractExpressionTester {

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

    public static final class DeleteStatementTester extends AbstractExpressionTester {

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

    public static final class DivisionExpressionTester extends CompoundExpressionTester {

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

    public static final class EmptyCollectionComparisonExpressionTester extends AbstractExpressionTester {

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

    public static final class EntityTypeLiteralTester extends AbstractExpressionTester {

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

    public static final class EntryExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class ExistsExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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
    public static interface ExpressionTester {

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

    public static final class ExtractExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class FromClauseTester extends AbstractFromClauseTester {

        protected FromClauseTester(ExpressionTester declarations,
                                   ExpressionTester hierarchicalQueryClause,
                                   ExpressionTester asOfClause) {

            super(declarations, hierarchicalQueryClause, asOfClause);
        }

        @Override
        public void test(Expression expression) {
            super.test(expression);
            assertInstance(expression, FromClause.class);
        }
    }

    public static final class FunctionExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class GroupByClauseTester extends AbstractExpressionTester {

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

    public static final class HavingClauseTester extends AbstractConditionalClauseTester {

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

    public static final class HierarchicalQueryClauseTester extends AbstractExpressionTester {

        private ExpressionTester connectByClause;
        public boolean hasSpaceAfterConnectByClause;
        public boolean hasSpaceAfterStartWithClause;
        private ExpressionTester orderSiblingsByClause;
        private ExpressionTester startWithClause;

        protected HierarchicalQueryClauseTester(ExpressionTester startWithClause,
                                                ExpressionTester connectByClause,
                                                ExpressionTester orderSiblingsByClause) {
            super();
            this.startWithClause              = startWithClause;
            this.connectByClause              = connectByClause;
            this.orderSiblingsByClause        = orderSiblingsByClause;
            this.hasSpaceAfterStartWithClause = !startWithClause.isNull() && !connectByClause.isNull();
            this.hasSpaceAfterConnectByClause = !connectByClause.isNull() && !orderSiblingsByClause.isNull();
        }

        public void test(Expression expression) {
            assertInstance(expression, HierarchicalQueryClause.class);

            HierarchicalQueryClause clause = (HierarchicalQueryClause) expression;
            assertEquals(toString(),                      clause.toString());
            assertEquals(!startWithClause.isNull(),       clause.hasStartWithClause());
            assertEquals(!connectByClause.isNull(),       clause.hasConnectByClause());
            assertEquals(!orderSiblingsByClause.isNull(), clause.hasOrderSiblingsByClause());
            assertEquals(hasSpaceAfterStartWithClause,    clause.hasSpaceAfterStartWithClause());
            assertEquals(hasSpaceAfterConnectByClause,    clause.hasSpaceAfterConnectByClause());

            startWithClause.test(clause.getStartWithClause());
            connectByClause.test(clause.getConnectByClause());
            orderSiblingsByClause.test(clause.getOrderSiblingsByClause());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(startWithClause);
            if (hasSpaceAfterStartWithClause) {
                sb.append(SPACE);
            }
            sb.append(connectByClause);
            if (hasSpaceAfterConnectByClause) {
                sb.append(SPACE);
            }
            sb.append(orderSiblingsByClause);
            return sb.toString();
        }
    }

    public static final class IdentificationVariableDeclarationTester extends AbstractExpressionTester {

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

    public static final class IdentificationVariableTester extends AbstractExpressionTester {

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

    public static final class IndexExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class InExpressionTester extends AbstractExpressionTester {

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
            this.hasLeftParenthesis       = !inItems.isNull();
            this.hasRightParenthesis      = !inItems.isNull();
            this.hasNot                   = hasNot;
            this.inItems                  = inItems;
            this.hasSpaceAfterIn          = !hasLeftParenthesis && !inItems.isNull();
        }

        public void test(Expression expression) {
            assertInstance(expression, InExpression.class);

            InExpression inExpression = (InExpression) expression;
            assertEquals(toString(),                         inExpression.toParsedText());
            assertEquals(hasLeftParenthesis,                 inExpression.hasLeftParenthesis());
            assertEquals(hasRightParenthesis,                inExpression.hasRightParenthesis());
            assertEquals(hasNot,                             inExpression.hasNot());
            assertEquals(!inItems.isNull(),                  inExpression.hasInItems());
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

    public static final class InputParameterTester extends AbstractExpressionTester {

        private String inputParameter;

        protected InputParameterTester(String inputParameter) {
            super();
            this.inputParameter = inputParameter;
        }

        public void test(Expression expression) {
            assertInstance(expression, InputParameter.class);

            InputParameter inputParameter = (InputParameter) expression;
            assertEquals(toString(),                           inputParameter.toParsedText());
            assertEquals(this.inputParameter.charAt(0) == '?', inputParameter.isPositional());
            assertEquals(this.inputParameter.charAt(0) == ':', inputParameter.isNamed());
            assertEquals(this.inputParameter.substring(1),     inputParameter.getParameterName());
        }

        @Override
        public String toString() {
            return inputParameter;
        }
    }

    public static final class JoinTester extends AbstractExpressionTester {

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

    public static final class JPQLExpressionTester extends AbstractExpressionTester {

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

    public static final class KeywordExpressionTester extends AbstractExpressionTester {

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

    public static final class LengthExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class LikeExpressionTester extends AbstractExpressionTester {

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

    public static final class LocateExpressionTester extends AbstractTripleEncapsulatedExpressionTester {

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

    public static abstract class LogicalExpressionTester extends CompoundExpressionTester {

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

    public static final class LowerExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class MaxFunctionTester extends AggregateFunctionTester {

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

    public static final class MinFunctionTester extends AggregateFunctionTester {

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

    public static final class ModExpressionTester extends AbstractDoubleEncapsulatedExpressionTester {

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

    public static final class MultiplicationExpressionTester extends CompoundExpressionTester {

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

    public static final class NotExpressionTester extends AbstractExpressionTester {

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

    public static final class NullComparisonExpressionTester extends AbstractExpressionTester {

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

    public static final class NullExpressionTester extends AbstractExpressionTester {

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

    public static final class NullIfExpressionTester extends AbstractDoubleEncapsulatedExpressionTester {

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

    public static final class NumericLiteralTester extends AbstractExpressionTester {

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

    public static final class ObjectExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class OnClauseTester extends AbstractConditionalClauseTester {

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

    public static final class OrderByClauseTester extends AbstractOrderByClauseTester {

        protected OrderByClauseTester(ExpressionTester orderByItems) {
            super(orderByItems);
        }

        @Override
        protected String identifier() {
            return ORDER_BY;
        }

        @Override
        public void test(Expression expression) {
            super.test(expression);
            assertInstance(expression, OrderByClause.class);
        }
    }

    public static final class OrderByItemTester extends AbstractExpressionTester {

        public boolean hasSpaceAfterExpression;
        public boolean hasSpaceAfterNulls;
        public boolean hasSpaceAfterOrdering;
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
            this.hasSpaceAfterExpression = ordering != Ordering.DEFAULT ||
                                           nullOrdering != NullOrdering.DEFAULT;
            this.hasSpaceAfterOrdering = ordering != Ordering.DEFAULT &&
                                         nullOrdering != NullOrdering.DEFAULT;
        }

        public void test(Expression expression) {
            assertInstance(expression, OrderByItem.class);

            OrderByItem orderByItem = (OrderByItem) expression;
            assertEquals(toString(), orderByItem.toParsedText());
            assertEquals(!this.orderByItem.isNull(), orderByItem.hasExpression());
            assertEquals(hasSpaceAfterExpression,    orderByItem.hasSpaceAfterExpression());
            assertEquals(hasSpaceAfterOrdering,      orderByItem.hasSpaceAfterOrdering());
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

            if (hasSpaceAfterExpression) {
                sb.append(SPACE);
            }

            if (ordering != Ordering.DEFAULT) {
                sb.append(ordering.name());
            }

            if (hasSpaceAfterOrdering) {
                sb.append(SPACE);
            }

            if (nulls != null) {
                sb.append(nulls);
                if (hasSpaceAfterNulls) {
                    sb.append(SPACE);
                }
            }
            else if (nullOrdering != NullOrdering.DEFAULT) {
                sb.append(nullOrdering.getIdentifier());
            }

            return sb.toString();
        }
    }

    public static final class OrderSiblingsByClauseTester extends AbstractOrderByClauseTester {

        protected OrderSiblingsByClauseTester(ExpressionTester orderByItems) {
            super(orderByItems);
        }

        @Override
        protected String identifier() {
            return ORDER_SIBLINGS_BY;
        }

        @Override
        public void test(Expression expression) {
            super.test(expression);
            assertInstance(expression, OrderSiblingsByClause.class);
        }
    }

    public static final class OrExpressionTester extends LogicalExpressionTester {

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

    public static final class RangeVariableDeclarationTester extends AbstractExpressionTester {

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
            this.hasSpaceAfterAbstractSchemaName = hasAs || !identificationVariable.isNull();
            this.hasSpaceAfterAs                 = hasAs;

            if (identificationVariable instanceof IdentificationVariableTester &&
                identificationVariable.isNull()) {

                this.hasSpaceAfterAbstractSchemaName = true;
            }
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

    public static final class RegexpExpressionTester extends AbstractExpressionTester {

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

    public static final class ResultVariableTester extends AbstractExpressionTester {

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

    public static final class SelectClauseTester extends AbstractSelectClauseTester {

        protected SelectClauseTester(ExpressionTester selectExpressions, boolean hasDistinct) {
            super(selectExpressions, hasDistinct);
        }

        @Override
        public void test(Expression expression) {
            super.test(expression);
            assertInstance(expression, SelectClause.class);
        }
    }

    public static final class SelectStatementTester extends AbstractSelectStatementTester {

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

            super(selectClause,
                  fromClause,
                  whereClause,
                  groupByClause,
                  havingClause);

            this.orderByClause = orderByClause;
            this.unionClauses  = unionClauses;

            this.hasSpaceBeforeOrderByClause = !orderByClause.isNull();
            this.hasSpaceBeforeUnionClauses  = !unionClauses .isNull();

//            hasSpaceAfterFrom                    |= (!fromClause   .isNull() && (!whereClause.isNull() || !groupByClause.isNull() &&  havingClause .isNull() && !orderByClause.isNull() && !unionClauses.isNull());
//            hasSpaceAfterWhere                   |= (!whereClause  .isNull() && groupByClause.isNull() && !havingClause .isNull() && !orderByClause.isNull() && !unionClauses .isNull());
//            hasSpaceAfterHierarchicalQueryClause |= (!whereClause  .isNull() && groupByClause.isNull() && !havingClause .isNull() && !orderByClause.isNull() && !unionClauses .isNull());
//            hasSpaceAfterGroupBy                 |= (!groupByClause.isNull() && havingClause .isNull() && !orderByClause.isNull() && !unionClauses.isNull());
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
//            assertEquals(hasSpaceBeforeOrderByClause, selectStatement.hasSpaceBeforeOrderBy());

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

    public static final class SimpleFromClauseTester extends AbstractFromClauseTester {

        protected SimpleFromClauseTester(ExpressionTester declaration,
                                           ExpressionTester hierarchicalQueryClause,
                                           ExpressionTester asOfClause) {

            super(declaration, hierarchicalQueryClause, asOfClause);
        }

        @Override
        public void test(Expression expression) {
            super.test(expression);
            assertInstance(expression, SimpleFromClause.class);
        }
    }

    public static final class SimpleSelectClauseTester extends AbstractSelectClauseTester {

        protected SimpleSelectClauseTester(ExpressionTester selectExpressions, boolean hasDistinct) {
            super(selectExpressions, hasDistinct);
        }

        @Override
        public void test(Expression expression) {
            super.test(expression);
            assertInstance(expression, SimpleSelectClause.class);
        }
    }

    public static final class SimpleSelectStatementTester extends AbstractSelectStatementTester {

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

    public static final class SizeExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class SqrtExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class StartWithClauseTester extends AbstractConditionalClauseTester {

        protected StartWithClauseTester(ExpressionTester expression) {
            super(expression);
        }

        @Override
        protected Class<StartWithClause> expressionType() {
            return StartWithClause.class;
        }

        @Override
        protected String identifier() {
            return START_WITH;
        }
    }

    public static final class StateFieldPathExpressionTester extends AbstractPathExpressionTester {

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

    public static final class StringLiteralTester extends AbstractExpressionTester {

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

    public static final class SubExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class SubstringExpressionTester extends AbstractTripleEncapsulatedExpressionTester {

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

    public static final class SubtractionExpressionTester extends CompoundExpressionTester {

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

    public static final class SumFunctionTester extends AggregateFunctionTester {

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

    public static final class TableExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class TableVariableDeclarationTester extends AbstractExpressionTester {


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

    public static final class TreatExpressionTester extends AbstractEncapsulatedExpressionTester {

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

    public static final class TrimExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class TypeExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class UnionClauseTester extends AbstractExpressionTester {

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

    public static final class UnknownExpressionTester extends AbstractExpressionTester {

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

    public static final class UpdateClauseTester extends AbstractExpressionTester {

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
            this.hasSpaceAfterRangeVariableDeclaration = !rangeVariableDeclaration.isNull();
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

    public static final class UpdateItemTester extends AbstractExpressionTester {

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

    public static final class UpdateStatementTester extends AbstractExpressionTester {

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

    public static final class UpperExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

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

    public static final class WhenClauseTester extends AbstractExpressionTester {

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

    public static final class WhereClauseTester extends AbstractConditionalClauseTester {

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
