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

import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.tests.jpql.EclipseLinkVersionTools;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class ResultVariableTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e AS n FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(variable("e"), "n")),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e n FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable(variable("e"), "n")),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT AVG(e.age) AS g FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(avg("e.age"), "g")),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT AVG(e.age) g FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable(avg("e.age"), "g")),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT AVG(e.age) + 2 AS g FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(avg("e.age").add(numeric(2)), "g")),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT AVG(e.age) + 2 AS g FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(avg("e.age").add(numeric(2)), "g")),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT AVG(e.age) AS g, e.name AS n FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(
                resultVariableAs(avg("e.age"), "g"),
                resultVariableAs(path("e.name"), "n")
            ),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String jpqlQuery = "SELECT AVG(e.age) g, e.name n FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(
                resultVariable(avg("e.age"), "g"),
                resultVariable(path("e.name"), "n")
            ),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String jpqlQuery = "SELECT AVG(e.age) AS";

        ResultVariableTester resultVariable = resultVariableAs(avg(path("e.age")), nullExpression());
        resultVariable.hasSpaceAfterAs = false;

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String jpqlQuery = "SELECT AVG(e.age) AS ";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(avg(path("e.age")), nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String jpqlQuery = "SELECT AS";

        ResultVariableTester resultVariable = resultVariableAs(nullExpression(), nullExpression());
        resultVariable.hasSpaceAfterAs = false;

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String jpqlQuery = "SELECT AS ";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(nullExpression(), nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String jpqlQuery = "SELECT AS n";

        ExpressionTester selectStatement = selectStatement(
            select(resultVariableAs(nullExpression(), "n"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String jpqlQuery = "SELECT e AS emp FROM Employee e ORDER BY emp";

        SelectStatementTester selectStatement = selectStatement(
            select(resultVariableAs(variable("e"), "emp")),
            from("Employee", "e"),
            orderBy(orderByItem(variable("emp")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() {

        String jpqlQuery = "SELECT e.name, AVG(e.age) AS age FROM Employee e ORDER BY age";

        SelectStatementTester selectStatement = selectStatement(
            select(path("e.name"), resultVariableAs(avg("e.age"), "age")),
            from("Employee", "e"),
            orderBy(orderByItem(variable("age")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_16() {

        String jpqlQuery = "SELECT e.name, e.dept, e.UUID, e.salary AS salary, AVG(e.age) AS age FROM Employee e ORDER BY age, salary ASC";

        SelectStatementTester selectStatement = selectStatement(
            select(
                path("e.name"),
                path("e.dept"),
                path("e.UUID"),
                resultVariableAs(path("e.salary"), "salary"),
                resultVariableAs(avg("e.age"), "age")
            ),
            from("Employee", "e"),
            orderBy(
                orderByItem(variable("age")),
                orderByItemAsc(variable("salary"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_17() {

        if (getGrammar().getJPAVersion().isOlderThan(JPAVersion.VERSION_2_1)) {
            return;
        }

        String jpqlQuery = "SELECT e.name, " +
                           "       UPPER(e.dept) dept, " +
                           "       e.UUID, " +
                           "       e.salary AS salary, " +
                           "       AVG(e.age) + e.age AS age " +
                           "FROM Employee e " +
                           "ORDER BY age, salary ASC";

        SelectStatementTester selectStatement = selectStatement(
            select(
                path("e.name"),
                resultVariable(upper(path("e.dept")), "dept"),
                path("e.UUID"),
                resultVariableAs(path("e.salary"), "salary"),
                resultVariableAs(avg("e.age").add(path("e.age")), "age")
            ),
            from("Employee", "e"),
            orderBy(
                orderByItem(variable("age")),
                orderByItemAsc(variable("salary"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_18() {

        String jpqlQuery = "SELECT a.name, " +
                           "       a.UUID, " +
                           "       a.typeUUID AS assetTypeUUID, " +
                           "       p.name AS projectName, " +
                           "       ap.usageType " +
                           "FROM Asset a, " +
                           "     UsedAssetUsingProject ap, " +
                           "     Project p " +
                           "WHERE a.UUID = ap.usedAsset AND ap.usingProject = p.UUID";

        SelectStatementTester selectStatement = selectStatement(
            select(
                path("a.name"),
                path("a.UUID"),
                resultVariableAs(path("a.typeUUID"), "assetTypeUUID"),
                resultVariableAs(path("p.name"), "projectName"),
                path("ap.usageType")
            ),
            from(
                "Asset", "a",
                "UsedAssetUsingProject", "ap",
                "Project", "p"
            ),
            where(
                        path("a.UUID")
                    .equal(
                        path("ap.usedAsset")
                    )
                .and(
                        path("ap.usingProject")
                    .equal(
                        path("p.UUID")
                    )
                )
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_19() {

        String jpqlQuery = "select f.id as id, f.name as name, f.description as description from Foo f";

        SelectStatementTester selectStatement = selectStatement(
            select(
                resultVariableAs(path("f.id"), "id"),
                resultVariableAs(path("f.name"), "name"),
                resultVariableAs(path("f.description"), "description")
            ),
            from("Foo", "f")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_20() {

        if (!isEclipseLinkProvider() || EclipseLinkVersionTools.isOlderThan2_1(getGrammar())) {
            return;
        }

        String jpqlQuery = "SELECT FUNC('MONTH', o.dateOperation) mois, " +
                           "       FUNC('YEAR', o.dateOperation) annee, " +
                           "       o.category.name categ, " +
                           "       SUM(o.amount) " +
                           "FROM BBankOperation o " +
                           "GROUP BY annee, mois, categ " +
                           "ORDER BY annee ASC, mois ASC, categ ASC";

        SelectStatementTester selectStatement = selectStatement(
            select(
                resultVariable(function(FUNC, "'MONTH'", path("o.dateOperation")), "mois"),
                resultVariable(function(FUNC, "'YEAR'",  path("o.dateOperation")), "annee"),
                resultVariable(path("o.category.name"), "categ"),
                sum("o.amount")
            ),
            from("BBankOperation", "o"),
            groupBy(
                variable("annee"),
                variable("mois"),
                variable("categ")
            ),
            orderBy(
                orderByItemAsc(variable("annee")),
                orderByItemAsc(variable("mois")),
                orderByItemAsc(variable("categ"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
