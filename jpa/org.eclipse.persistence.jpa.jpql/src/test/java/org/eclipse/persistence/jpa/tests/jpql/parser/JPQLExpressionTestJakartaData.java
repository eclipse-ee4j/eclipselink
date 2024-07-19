/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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

import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.abstractSchemaName;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.equal;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.from;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.identificationVariableDeclaration;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.numeric;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.rangeVariableDeclaration;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.select;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.selectStatement;
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
        checkAliasFrom("SELECT this FROM NoAliasEntity", Expression.THIS);
    }

    @Test
    public void testNoAliasOBJECT() {
        checkAliasFrom("SELECT OBJECT(this) FROM NoAliasEntity", Expression.THIS);
    }

    @Test
    public void testNoAliasCOUNT() {
        checkAliasFrom("SELECT COUNT(this) FROM NoAliasEntity", Expression.THIS);
    }

    @Test
    public void testNoAliasCASTCOUNT() {
        checkAliasFrom("SELECT CAST(COUNT(this) AS SMALLINT) FROM NoAliasEntity", Expression.THIS);
    }

    @Test
    public void testNoAliasCASTCASTCOUNT() {
        checkAliasFrom("SELECT CAST(CAST(COUNT(this) AS SMALLINT) AS BIGINT) FROM NoAliasEntity", Expression.THIS);
    }

    @Test
    public void testCorrectAliases() {
        JPQLExpression jpqlExpression = checkAliasFrom("SELECT this FROM NoAliasEntity this WHERE this.id1 = :id1", Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasWhere() {
        JPQLExpression jpqlExpression = checkAliasFrom("SELECT this FROM NoAliasEntity this WHERE id1 = :id1", Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhere() {
        JPQLExpression jpqlExpression = checkAliasFrom("SELECT this FROM NoAliasEntity WHERE id1 = :id1", Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhereAnd() {
        JPQLExpression jpqlExpression = checkAliasFrom("SELECT this FROM NoAliasEntity WHERE id1 = :id1 AND name = 'Name 1'", Expression.THIS);
        checkAliasesWhere(jpqlExpression, Expression.THIS);
    }

    @Test
    public void testNoAliasFromWhereAndUPPER() {
        JPQLExpression jpqlExpression = checkAliasFrom("SELECT this FROM NoAliasEntity WHERE id1 = :id1 AND UPPER(name) = 'NAME 1'", Expression.THIS);
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
    public void testFunctionNameAsStateFieldWithImplicitVariable() {

        String inputJPQLQuery = "SELECT this FROM Order WHERE length = 1";
//        String inputJPQLQuery = "UPDATE Box SET length = length + 1";

        SelectStatementTester selectStatement = selectStatement(
                select(variable("this")),
                from(identificationVariableDeclaration(
                        rangeVariableDeclaration(abstractSchemaName("Order"), virtualVariable("this")))),
                where(equal(virtualVariable("this", "length"), numeric(1)))
        );

        testJakartaDataQuery(inputJPQLQuery, selectStatement);
    }

    private JPQLExpression checkAliasFrom(String actualQuery, String expectedAlias) {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, true);
        SelectStatement selectStatement = (SelectStatement)jpqlExpression.getQueryStatement();
        FromClause fromClause = (FromClause)selectStatement.getFromClause();
        IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration)fromClause.getDeclaration();
        RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration)identificationVariableDeclaration.getRangeVariableDeclaration();
        IdentificationVariable identificationVariable = (IdentificationVariable)rangeVariableDeclaration.getIdentificationVariable();
        String alias = identificationVariable.getText();
        assertEquals(expectedAlias, alias);
        return jpqlExpression;
    }

    private void checkAliasesWhere(JPQLExpression jpqlExpression, String expectedAlias) {
        SelectStatement selectStatement = (SelectStatement)jpqlExpression.getQueryStatement();
        WhereClause whereClause = (WhereClause)selectStatement.getWhereClause();
        ListIterable<Expression> expressions = whereClause.children();
        List<IdentificationVariable> identificationVariableList = new ArrayList<>();
        collectIdentificationVariables(expressions, identificationVariableList);
        for (IdentificationVariable identificationVariable: identificationVariableList) {
            if (expectedAlias.equals(identificationVariable.getText())) {
                assertEquals(expectedAlias, identificationVariable.getText());
            } else {
                String alias = identificationVariable.getStateFieldPathExpression().getIdentificationVariable().toActualText();
                assertEquals("No expected alias: " + expectedAlias + " in: " + identificationVariable.getText(), expectedAlias, alias);
            }
        }
    }

    private void collectIdentificationVariables(ListIterable<Expression> expressions, List<IdentificationVariable> identificationVariableList) {
        for (Expression expression: expressions) {
            if (expression.children() != null) {
                collectIdentificationVariables(expression.children(), identificationVariableList);
            }
            if (expression instanceof IdentificationVariable identificationVariable) {
                identificationVariableList.add(identificationVariable);
            }
        }
    }
}
