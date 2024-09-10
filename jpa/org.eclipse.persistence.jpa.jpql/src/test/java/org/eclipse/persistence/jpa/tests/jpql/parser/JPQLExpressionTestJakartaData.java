/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.and;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.avg;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.division;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.equal;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.id;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.from;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.inputParameter;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.isNotNull;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.max;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.numeric;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.path;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.select;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.selectStatement;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.set;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.update;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.updateStatement;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.variable;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.virtualVariable;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.where;

import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_2;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public final class JPQLExpressionTestJakartaData extends JPQLParserTest {

    @Test
    public void testNoAlias() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasOBJECT() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT OBJECT(this) FROM NoAliasEntity", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasCOUNT() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT COUNT(this) FROM NoAliasEntity", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasCASTCOUNT() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT CAST(COUNT(this) AS SMALLINT) FROM NoAliasEntity", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasCASTCASTCOUNT() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT CAST(CAST(COUNT(this) AS SMALLINT) AS BIGINT) FROM NoAliasEntity", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testCorrectAliases() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity this WHERE this.id1 = :id1", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasWhere() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity this WHERE id1 = :id1", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhere01() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity WHERE id1 = :id1", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhere02() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity WHERE (id1 = :id1)", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhere03() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity WHERE (ID <= :idParam AND UPPER(name) NOT LIKE UPPER(:nameParam) AND UPPER(name) NOT LIKE 'dgdgs') ORDER BY ID DESC, name", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhere04() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity WHERE (:rate * ID <= :max AND :rate * ID >= :min) ORDER BY name", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhereAnd() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity WHERE id1 = :id1 AND name = 'Name 1'", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhereAndUPPER() {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery("SELECT this FROM NoAliasEntity WHERE id1 = :id1 AND UPPER(name) = 'NAME 1'", JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        checkAliasFrom(jpqlExpression, Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testGeneratedSelectNoAliasFromWhere() {
        String expectedJPQLQuery = "SELECT this FROM NoAliasEntity WHERE id1 = :id1";
        String inputJPQLQuery = "FROM NoAliasEntity WHERE id1 = :id1";
        JPQLExpression jpqlExpression = new JPQLExpression(inputJPQLQuery, JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        assertEquals(expectedJPQLQuery, jpqlExpression.toActualText());
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testGeneratedSelect() {
        String expectedJPQLQuery = "SELECT this FROM NoAliasEntity this";
        String inputJPQLQuery = "FROM NoAliasEntity this";
        JPQLExpression jpqlExpression = new JPQLExpression(inputJPQLQuery, JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        assertEquals(expectedJPQLQuery, jpqlExpression.toActualText());
    }

    @Test
    public void testFunctionNameAsImplicitStateField() {

        String inputJPQLQuery = "SELECT this FROM Order WHERE length = 1";

        SelectStatementTester selectStatement = selectStatement(
                select(variable("this")),
                from("Order", "{this}"),
                where(equal(virtualVariable("this", "length"), numeric(1)))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    @Test
    public void testFunctionNameAsImplicitStateFieldInNumericExpression() {

        String inputJPQLQuery = "SELECT this FROM Order WHERE id = length + 1";

        SelectStatementTester selectStatement = selectStatement(
                select(variable("this")),
                from("Order", "{this}"),
                where(equal(
                        virtualVariable("this", "id"),
                        virtualVariable("this", "length").add(numeric(1))
                ))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2182
    @Test
    public void testFunctionNameAsImplicitStateFieldInSelectWhereExpression() {

        String inputJPQLQuery = "SELECT name FROM Order WHERE name IS NOT NULL AND id = :idParam";

        SelectStatementTester selectStatement = selectStatement(
                select(virtualVariable("this", "name")),
                from("Order", "{this}"),
                where(and(isNotNull(virtualVariable("this", "name")),
                        equal(virtualVariable("this", "id"),
                                inputParameter(":idParam"))
                ))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2192
    @Test
    public void testFunctionNameAsImplicitStateFieldInSelectAggregateMaxExpression() {

        String inputJPQLQuery = "SELECT MAX(price) FROM Item WHERE AVG(price) = 100";

        SelectStatementTester selectStatement = selectStatement(
                select(max(virtualVariable("this", "price"))),
                from("Item", "{this}"),
                where(equal(avg(virtualVariable("this", "price")), numeric(100)))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2247
    @Test
    public void testFunctionNameAsImplicitStateFieldInSelectArithmeticExpression() {

        String inputJPQLQuery = "SELECT totalPrice / quantity FROM Order WHERE creationYear = :yearParam";

        SelectStatementTester selectStatement = selectStatement(
                select(division(virtualVariable("this", "totalPrice"), virtualVariable("this", "quantity"))),
                from("Order", "{this}"),
                where(equal(virtualVariable("this", "creationYear"), inputParameter(":yearParam")))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    @Test
    public void testUpdateFunctionNameAsImplicitStateFieldInNumericExpression() {

        String inputJPQLQuery = "UPDATE Order SET length = length + 1";

        UpdateStatementTester selectStatement = updateStatement(
                update(
                        "Order", "this",
                         set(path("{this}.length"),
                                virtualVariable("this", "length")
                                        .add(numeric(1))
                        ), false)
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2184
    @Test
    public void testUpdateFunctionNameAsImplicitStateFieldInIdFunction() {

        String inputJPQLQuery = "UPDATE Order SET length = length + 1 WHERE ID(this) = 1";

        UpdateStatementTester selectStatement = updateStatement(
                update(
                        "Order", "this",
                        set(path("{this}.length"),
                                virtualVariable("this", "length")
                                        .add(numeric(1))
                        ), false),
                where(equal(
                        id(virtualVariable("this", "this")),
                        numeric(1)
                ))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    private void checkAliasFrom(JPQLExpression jpqlExpression, String expectedAlias) {
        SelectStatement selectStatement = (SelectStatement) jpqlExpression.getQueryStatement();
        FromClause fromClause = (FromClause) selectStatement.getFromClause();
        IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) fromClause.getDeclaration();
        RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) identificationVariableDeclaration.getRangeVariableDeclaration();
        IdentificationVariable identificationVariable = (IdentificationVariable) rangeVariableDeclaration.getIdentificationVariable();
        String alias = identificationVariable.getText();
        assertEquals(expectedAlias, alias);
    }

    private void checkAliasesWhere(JPQLExpression jpqlExpression, String expectedAlias) {
        SelectStatement selectStatement = (SelectStatement) jpqlExpression.getQueryStatement();
        WhereClause whereClause = (WhereClause) selectStatement.getWhereClause();
        ListIterable<Expression> expressions = whereClause.children();
        List<IdentificationVariable> identificationVariableList = new ArrayList<>();
        collectIdentificationVariables(expressions, identificationVariableList);
        for (IdentificationVariable identificationVariable : identificationVariableList) {
            if (expectedAlias.equals(identificationVariable.getText())) {
                assertEquals(expectedAlias, identificationVariable.getText());
            } else {
                String alias = identificationVariable.getStateFieldPathExpression().getIdentificationVariable().toActualText();
                assertEquals("No expected alias: " + expectedAlias + " in: " + identificationVariable.getText(), expectedAlias, alias);
            }
        }
    }

    private void collectIdentificationVariables(ListIterable<Expression> expressions, List<IdentificationVariable> identificationVariableList) {
        for (Expression expression : expressions) {
            if (expression.children() != null) {
                collectIdentificationVariables(expression.children(), identificationVariableList);
            }
            if (expression instanceof IdentificationVariable identificationVariable) {
                identificationVariableList.add(identificationVariable);
            }
        }
    }
}
