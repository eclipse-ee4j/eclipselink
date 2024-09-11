/*
 * Copyright (c) 2006, 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries1_0.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * This unit-tests tests the parsed tree representation of a JPQL query.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueriesTest1_0 extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryFormatter_1() {
        return new JPQLQueryStringFormatter() {
            @Override
            public String format(String query) {
                return query.replace("1+(", "1 + (");
            }
        };
    }

    private JPQLQueryStringFormatter buildStringFormatter_1() {
        return new JPQLQueryStringFormatter() {
            @Override
            public String format(String query) {
                return query.replace("AVG", "avg");
            }
        };
    }

    private JPQLQueryStringFormatter buildStringFormatter_2() {
        return new JPQLQueryStringFormatter() {
            @Override
            public String format(String query) {
                return query.replace("CURRENT_TIMESTAMP", "current_timestamp");
            }
        };
    }

    private JPQLQueryStringFormatter buildStringFormatter_3() {
        return new JPQLQueryStringFormatter() {
            @Override
            public String format(String query) {
                return query.replace("END", "end");
            }
        };
    }

    @Test
    public final void test_Query_001() {
        test_Query_001(getGrammar());
    }

    final void test_Query_001(JPQLGrammar jpqlGrammar) {

        // SELECT e FROM Employee e

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e")
        );

        testQuery(query_001(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_002() {
        test_Query_002(getGrammar());
    }

    final void test_Query_002(JPQLGrammar jpqlGrammar) {

        // SELECT e\nFROM Employee e

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e")
        );

        testQuery(query_002(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_003() {
        test_Query_003(getGrammar());
    }

    final void test_Query_003(JPQLGrammar jpqlGrammar) {

        // SELECT e
      // FROM Employee e
      // WHERE e.department.name = 'NA42' AND
      //       e.address.state IN ('NY', 'CA')

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    path("e.department.name").equal(string("'NA42'"))
                .and(
                    path("e.address.state").in(string("'NY'"), string("'CA'"))
                )
            )
        );

        testQuery(query_003(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_004() {
        test_Query_004(getGrammar());
    }

    final void test_Query_004(JPQLGrammar jpqlGrammar) {

        // SELECT p.number
        // FROM Employee e, Phone p
        // WHERE     e = p.employee
        //       AND e.department.name = 'NA42'
        //       AND p.type = 'Cell'

        ExpressionTester selectStatement = selectStatement(
            select(path("p.number")),
            from("Employee", "e", "Phone", "p"),
            where(
                    variable("e").equal(path("p.employee"))
                .and(
                    path("e.department.name").equal(string("'NA42'"))
                )
                .and(
                    path("p.type").equal(string("'Cell'"))
                )
            )
        );

        testQuery(query_004(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_005() {
        test_Query_005(getGrammar());
    }

    final void test_Query_005(JPQLGrammar jpqlGrammar) {

        // SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
        // FROM Department d JOIN d.employees e
        // GROUP BY d
        // HAVING COUNT(e) >= 5

        ExpressionTester selectStatement = selectStatement(
            select(
                variable("d"),
                count(variable("e")),
                max("e.salary"),
                avg("e.salary")),
            from("Department", "d", join("d.employees", "e")),
            groupBy(variable("d")),
            having(count(variable("e")).greaterThanOrEqual(numeric(5)))
        );

        testQuery(query_005(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_006() {
        test_Query_006(getGrammar());
    }

    final void test_Query_006(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE     e.department = ?1
        //       AND e.salary > ?2

        ExpressionTester andExpression =
                path("e.department").equal(inputParameter("?1"))
            .and(
                path("e.salary").greaterThan(inputParameter("?2")));

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(andExpression)
        );

        testQuery(query_006(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_007() {
        test_Query_007(getGrammar());
    }

    final void test_Query_007(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE     e.department = :dept
        //       AND e.salary > :base

        ExpressionTester andExpression =
                path("e.department").equal(inputParameter(":dept"))
            .and(
                path("e.salary").greaterThan(inputParameter(":base")));

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(andExpression)
        );

        testQuery(query_007(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_008() {
        test_Query_008(getGrammar());
    }

    final void test_Query_008(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE     e.department = 'NA65'
        //       AND e.name = 'UNKNOWN'' OR e.name = ''Roberts'

        ExpressionTester andExpression =
                path("e.department").equal(string("'NA65'"))
            .and(
                path("e.name").equal(string("'UNKNOWN'' OR e.name = ''Roberts'")));

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(andExpression)
        );

        testQuery(query_008(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_009() {
        test_Query_009(getGrammar());
    }

    final void test_Query_009(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.startDate BETWEEN ?1 AND ?2

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.startDate").between(inputParameter("?1"), inputParameter("?2")))
        );

        testQuery(query_009(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_010() {
        test_Query_010(getGrammar());
    }

    final void test_Query_010(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.department = :dept AND
        //      e.salary = (SELECT MAX(e.salary)
        //                  FROM Employee e
        //                  WHERE e.department = :dept)

        ExpressionTester subquery = subquery(
            subSelect(max("e.salary")),
            subFrom("Employee", "e"),
            where(path("e.department").equal(inputParameter(":dept")))
        );

        ExpressionTester andExpression =
                path("e.department").equal(inputParameter(":dept"))
            .and(
                path("e.salary").equal(sub(subquery)));

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(andExpression)
        );

        testQuery(query_010(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_011() {
        test_Query_011(getGrammar());
    }

    final void test_Query_011(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Project p JOIN p.employees e
        // WHERE p.name = ?1
        // ORDER BY e.name

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Project", "p", join("p.employees", "e")),
            where(path("p.name").equal(inputParameter("?1"))),
            orderBy("e.name")
        );

        testQuery(query_011(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_012() {
        test_Query_012(getGrammar());
    }

    final void test_Query_012(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.projects IS EMPTY";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(isEmpty("e.projects"))
        );

        testQuery(query_012(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_013() {
        test_Query_013(getGrammar());
    }

    final void test_Query_013(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.projects IS NOT EMPTY";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(isNotEmpty("e.projects"))
        );

        testQuery(query_013(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_014() {
        test_Query_014(getGrammar());
    }

    final void test_Query_014(JPQLGrammar jpqlGrammar) {

        // UPDATE Employee e
        // SET e.manager = ?1
        // WHERE e.department = ?2

        ExpressionTester updateStatement = updateStatement(
            update("Employee", "e", set("e.manager", inputParameter("?1"))),
            where(path("e.department").equal(inputParameter("?2")))
        );

        testQuery(query_014(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_015() {
        test_Query_015(getGrammar());
    }

    final void test_Query_015(JPQLGrammar jpqlGrammar) {

        // DELETE FROM Project p
      // WHERE p.employees IS EMPTY

        ExpressionTester deleteStatement = deleteStatement(
            "Project",
            "p",
            where(isEmpty("p.employees"))
        );

        testQuery(query_015(), deleteStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_016() {
        test_Query_016(getGrammar());
    }

    final void test_Query_016(JPQLGrammar jpqlGrammar) {

        // DELETE FROM Department d
        // WHERE d.name IN ('CA13', 'CA19', 'NY30')

        ExpressionTester inExpression = in(
            "d.name",
            string("'CA13'"),
            string("'CA19'"),
            string("'NY30'")
        );

        ExpressionTester deleteStatement = deleteStatement(
            "Department",
            "d",
            where(inExpression)
        );

        testQuery(query_016(), deleteStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_017() {
        test_Query_017(getGrammar());
    }

    final void test_Query_017(JPQLGrammar jpqlGrammar) {

        // UPDATE Employee e
        // SET e.department = null
        // WHERE e.department.name IN ('CA13', 'CA19', 'NY30')

        ExpressionTester updateStatement = updateStatement(
            update("Employee", "e", set("e.department", NULL())),
            where(path("e.department.name").in(string("'CA13'"), string("'CA19'"), string("'NY30'")))
        );

        testQuery(query_017(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_018() {
        test_Query_018(getGrammar());
    }

    final void test_Query_018(JPQLGrammar jpqlGrammar) {

        // SELECT d
        // FROM Department d
        // WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'

        ExpressionTester likeExpression = like(
            path("d.name"),
            string("'QA\\_%'"),
            '\\'
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("d")),
            from("Department", "d"),
            where(likeExpression)
        );

        testQuery(query_018(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_019() {
        test_Query_019(getGrammar());
    }

    final void test_Query_019(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)

        ExpressionTester subQuery = subquery(
            subSelect(max("e2.salary")),
            subFrom("Employee", "e2")
        );

        ExpressionTester comparisonExpression = equal(
            path("e.salary"),
            sub(subQuery)
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(comparisonExpression)
        );

        testQuery(query_019(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_020() {
        test_Query_020(getGrammar());
    }

    final void test_Query_020(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')

        ExpressionTester comparisonExpression1 = equal(
            path("p.employee"),
            variable("e")
        );

        ExpressionTester comparisonExpression2 = equal(
            path("p.type"),
            string("'Cell'")
        );

        ExpressionTester subQuery = subquery(
            subSelect(variable("p")),
            subFrom("Phone", "p"),
            where(and(comparisonExpression1, comparisonExpression2))
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists(subQuery))
        );

        testQuery(query_020(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_021() {
        test_Query_021(getGrammar());
    }

    final void test_Query_021(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                exists(
                    subquery(
                        subSelect(variable("p")),
                        subFrom(fromCollection("e.phones", "p")),
                        where(path("p.type").equal(string("'Cell'")))
                    )
                )
            )
        );

        testQuery(query_021(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_022() {
        test_Query_022(getGrammar());
    }

    final void test_Query_022(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.department IN (SELECT DISTINCT d
        //                        FROM Department d JOIN d.employees de JOIN de.projects p
        //                        WHERE p.name LIKE 'QA%')

        ExpressionTester subquery = subquery(
            subSelectDistinct(variable("d")),
            subFrom("Department", "d", join("d.employees", "de"), join("de.projects", "p")),
            where(like(path("p.name"), string("'QA%'")))
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(in("e.department", subquery))
        );

        testQuery(query_022(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_023() {
        test_Query_023(getGrammar());
    }

    final void test_Query_023(JPQLGrammar jpqlGrammar) {

        // SELECT p
        // FROM Phone p
        // WHERE p.type NOT IN ('Office', 'Home')

        ExpressionTester notInExpression = notIn(
            "p.type",
            string("'Office'"),
            string("'Home'")
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("p")),
            from("Phone", "p"),
            where(notInExpression)
        );

        testQuery(query_023(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_024() {
        test_Query_024(getGrammar());
    }

    final void test_Query_024(JPQLGrammar jpqlGrammar) {

        // SELECT m
        // FROM Employee m
        // WHERE (SELECT COUNT(e)
        //        FROM Employee e
        //        WHERE e.manager = m) > 0

        ExpressionTester comparisonExpression1 = equal(
            path("e.manager"),
            variable("m")
        );

        ExpressionTester subquery = subquery(
            subSelect(count(variable("e"))),
            subFrom("Employee", "e"),
            where(comparisonExpression1)
        );

        ExpressionTester comparisonExpression2 = greaterThan(
            sub(subquery),
            numeric(0L)
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("m")),
            from("Employee", "m"),
            where(comparisonExpression2)
        );

        testQuery(query_024(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_025() {
        test_Query_025(getGrammar());
    }

    final void test_Query_025(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e MEMBER OF e.directs

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(memberOf("e", "e.directs"))
        );

        testQuery(query_025(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_026() {
        test_Query_026(getGrammar());
    }

    final void test_Query_026(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE NOT EXISTS (SELECT p
        //                   FROM e.phones p
        //                   WHERE p.type = 'Cell')

        ExpressionTester comparisonExpression = equal(
            path("p.type"),
            string("'Cell'")
        );

        ExpressionTester subquery = subquery(
            subSelect(variable("p")),
            subFrom(fromCollection("e.phones", "p")),
            where(comparisonExpression)
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notExists(subquery))
        );

        testQuery(query_026(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_027() {
        test_Query_027(getGrammar());
    }

    final void test_Query_027(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.directs IS NOT EMPTY AND
        //       e.salary < ALL (SELECT d.salary FROM e.directs d)

        ExpressionTester subquery = subquery(
            subSelect(path("d.salary")),
            subFrom(fromCollection("e.directs", "d"))
        );

        ExpressionTester comparisonExpression = lowerThan(
            path("e.salary"),
            all(subquery)
        );

        ExpressionTester andExpression = and(
            isNotEmpty("e.directs"),
            comparisonExpression
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(andExpression)
        );

        testQuery(query_027(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_028() {
        test_Query_028(getGrammar());
    }

    final void test_Query_028(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p
        //                           WHERE p.name LIKE 'QA%')

        ExpressionTester subquery = subquery(
            subSelectDistinct(variable("d")),
            subFrom("Department", "d", join("d.employees", "de"), join("de.projects", "p")),
            where(like(path("p.name"), string("'QA%'")))
        );

        ExpressionTester comparisonExpression = equal(
            path("e.department"),
            anyExpression(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(comparisonExpression)
        );

        testQuery(query_028(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_029() {
        test_Query_029(getGrammar());
    }

    final void test_Query_029(JPQLGrammar jpqlGrammar) {

        // SELECT d
        // FROM Department d
        // WHERE SIZE(d.employees) = 2

        ExpressionTester selectStatement = selectStatement(
            select(variable("d")),
            from("Department", "d"),
            where(equal(size("d.employees"), numeric(2L)))
        );

        testQuery(query_029(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_030() {
        test_Query_030(getGrammar());
    }

    final void test_Query_030(JPQLGrammar jpqlGrammar) {

        // SELECT d
      // FROM Department d
      // WHERE (SELECT COUNT(e)
      //        FROM d.employees e) = 2

        ExpressionTester subquery = subquery(
            subSelect(count(variable("e"))),
            subFrom(fromCollection("d.employees", "e"))
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("d")),
            from("Department", "d"),
            where(equal(sub(subquery), numeric(2)))
        );

        testQuery(query_030(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_031() {
        test_Query_031(getGrammar());
    }

    final void test_Query_031(JPQLGrammar jpqlGrammar) {

        // SELECT e
        // FROM Employee e
        // ORDER BY e.name DESC

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(orderByItemDesc("e.name"))
        );

        testQuery(query_031(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_032() {
        test_Query_032(getGrammar());
    }

    final void test_Query_032(JPQLGrammar jpqlGrammar) {

        // SELECT e
      // FROM Employee e JOIN e.department d
      // ORDER BY d.name, e.name DESC

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join("e.department", "d")),
            orderBy(
                orderByItem("d.name"),
                orderByItemDesc("e.name")
            )
        );

        testQuery(query_032(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_033() {
        test_Query_033(getGrammar());
    }

    final void test_Query_033(JPQLGrammar jpqlGrammar) {

        // SELECT AVG(e.salary) FROM Employee e
        ExpressionTester selectStatement = selectStatement(
            select(avg("e.salary")),
            from("Employee", "e")
        );

        testQuery(query_033(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_034() {
        test_Query_034(getGrammar());
    }

    final void test_Query_034(JPQLGrammar jpqlGrammar) {

        // SELECT d.name, AVG(e.salary)
        // FROM Department d JOIN d.employees e
        // GROUP BY d.name

        ExpressionTester selectStatement = selectStatement(
            select(path("d.name"), avg("e.salary")),
            from("Department", "d", join("d.employees", "e")),
            groupBy(path("d.name"))
        );

        testQuery(query_034(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_035() {
        test_Query_035(getGrammar());
    }

    final void test_Query_035(JPQLGrammar jpqlGrammar) {

        // SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name

        ExpressionTester selectStatement = selectStatement(
            select(path("d.name"), avg("e.salary")),
            from("Department", "d", join("d.employees", "e")),
            where(isEmpty("e.directs")),
            groupBy(path("d.name"))
        );

        testQuery(query_035(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_036() {
        test_Query_036(getGrammar());
    }

    final void test_Query_036(JPQLGrammar jpqlGrammar) {

        // SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name
      // HAVING AVG(e.salary) > 50000

        ExpressionTester selectStatement = selectStatement(
            select(path("d.name"), avg("e.salary")),
            from("Department", "d", join("d.employees", "e")),
            where(isEmpty("e.directs")),
            groupBy(path("d.name")),
            having(greaterThan(avg("e.salary"), numeric(50000)))
        );

        testQuery(query_036(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_037() {
        test_Query_037(getGrammar());
    }

    final void test_Query_037(JPQLGrammar jpqlGrammar) {

        // SELECT e, COUNT(p), COUNT(DISTINCT p.type)
        // FROM Employee e JOIN e.phones p
        // GROUP BY e

        ExpressionTester selectStatement = selectStatement(
            select(variable("e"),
                   count(variable("p")),
                   countDistinct(path("p.type"))),
            from("Employee", "e", join("e.phones", "p")),
            groupBy(variable("e"))
        );

        testQuery(query_037(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_038() {
        test_Query_038(getGrammar());
    }

    final void test_Query_038(JPQLGrammar jpqlGrammar) {

        // SELECT d.name, e.salary, COUNT(p)
        // FROM Department d JOIN d.employees e JOIN e.projects p
        // GROUP BY d.name, e.salary

        ExpressionTester selectStatement = selectStatement(
            select(path("d.name"),
                   path("e.salary"),
                   count(variable("p"))),
            from(
                "Department", "d",
                join("d.employees", "e"),
                join("e.projects", "p")
            ),
            groupBy(path("d.name"), path("e.salary"))
        );

        testQuery(query_038(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_039() {
        test_Query_039(getGrammar());
    }

    final void test_Query_039(JPQLGrammar jpqlGrammar) {

        // SELECT e, COUNT(p)
        // FROM Employee e JOIN e.projects p
        // GROUP BY e
        // HAVING COUNT(p) >= 2

        ExpressionTester selectStatement = selectStatement(
            select(variable("e"), count(variable("p"))),
            from("Employee", "e", join("e.projects", "p")),
            groupBy(variable("e")),
            having(greaterThanOrEqual(count(variable("p")), numeric(2)))
        );

        testQuery(query_039(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_040() {
        test_Query_040(getGrammar());
    }

    final void test_Query_040(JPQLGrammar jpqlGrammar) {

        // UPDATE Employee e
        // SET e.salary = 60000
        // WHERE e.salary = 55000

        ExpressionTester updateStatement = updateStatement(
            update("Employee", "e", set("e.salary", numeric(60000))),
            where(equal(path("e.salary"), numeric(55000)))
        );

        testQuery(query_040(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_041() {
        test_Query_041(getGrammar());
    }

    final void test_Query_041(JPQLGrammar jpqlGrammar) {

        // UPDATE Employee e
        // SET e.salary = e.salary + 5000
        // WHERE EXISTS (SELECT p
        //               FROM e.projects p
        //               WHERE p.name = 'Release1')

        ExpressionTester additionExpression = add(
            path("e.salary"),
            numeric(5000)
        );

        ExpressionTester subquery = subquery(
            subSelect(variable("p")),
            subFrom(fromCollection("e.projects", "p")),
            where(equal(path("p.name"), string("'Release1'")))
        );

        ExpressionTester updateStatement = updateStatement(
            update("Employee", "e", set("e.salary", additionExpression)),
            where(exists(subquery))
        );

        testQuery(query_041(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_042() {
        test_Query_042(getGrammar());
    }

    final void test_Query_042(JPQLGrammar jpqlGrammar) {

        // UPDATE Phone p
        // SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)),
        //     p.type = 'Business'
        // WHERE p.employee.address.city = 'New York' AND p.type = 'Office'

        ExpressionTester concatExpression = concat(
            string("'288'"),
            substring(
                path("p.number"),
                locate(path("p.number"), string("'-'")),
                numeric(4L)
            )
        );

        ExpressionTester andExpression = and(
            equal(path("p.employee.address.city"), string("'New York'")),
            equal(path("p.type"), string("'Office'"))
        );

        ExpressionTester updateStatement = updateStatement(
            update(
                "Phone", "p",
                set("p.number", concatExpression),
                set("p.type", string("'Business'"))
            ),
            where(andExpression)
        );

        testQuery(query_042(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_043() {
        test_Query_043(getGrammar());
    }

    final void test_Query_043(JPQLGrammar jpqlGrammar) {

        // DELETE FROM Employee e
        // WHERE e.department IS NULL";

        ExpressionTester deleteStatement = deleteStatement(
            "Employee", "e",
            where(isNull(path("e.department")))
        );

        testQuery(query_043(), deleteStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_044() {
        test_Query_044(getGrammar());
    }

    final void test_Query_044(JPQLGrammar jpqlGrammar) {

        // Select Distinct object(c)
        // From Customer c, In(c.orders) co
        // Where co.totalPrice >= Some (Select o.totalPrice
        //                              From Order o, In(o.lineItems) l
        //                              Where l.quantity = 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(equal(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = greaterThanOrEqual(
            path("co.totalPrice"),
            some(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_044(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_045() {
        test_Query_045(getGrammar());
    }

    final void test_Query_045(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice <= SOME (Select o.totalPrice
        //                              FROM Order o, IN(o.lineItems) l
        //                              WHERE l.quantity = 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(equal(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = lowerThanOrEqual(
            path("co.totalPrice"),
            some(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_045(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_046() {
        test_Query_046(getGrammar());
    }

    final void test_Query_046(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)

        ExpressionTester subquery = subquery(
            subSelect(max("o.totalPrice")),
            subFrom("Order", "o")
        );

        ExpressionTester comparisonExpression = equal(
            path("co.totalPrice"),
            any(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_046(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_047() {
        test_Query_047(getGrammar());
    }

    final void test_Query_047(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice < ANY (Select o.totalPrice
        //                            FROM Order o, IN(o.lineItems) l
        //                            WHERE l.quantity = 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(equal(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = lowerThan(
            path("co.totalPrice"),
            any(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_047(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_048() {
        test_Query_048(getGrammar());
    }

    final void test_Query_048(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice > ANY (Select o.totalPrice
        //                            FROM Order o, IN(o.lineItems) l
        //                            WHERE l.quantity = 3)

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(
                    path("co.totalPrice")
                .greaterThan(
                    any(subquery(
                        subSelect(path("o.totalPrice")),
                        subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
                        where(equal(path("l.quantity"), numeric(3L)))
                    ))
                )
            )
        );

        testQuery(query_048(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_049() {
        test_Query_049(getGrammar());
    }

    final void test_Query_049(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)

        ExpressionTester subquery = subquery(
            subSelect(min("o.totalPrice")),
            subFrom("Order", "o")
        );

        ExpressionTester comparisonExpression = different(
            path("co.totalPrice"),
            all(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_049(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_050() {
        test_Query_050(getGrammar());
    }

    final void test_Query_050(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct object(c)
      // FROM Customer c, IN(c.orders) co
      // WHERE co.totalPrice >= ALL (Select o.totalPrice
        //                             FROM Order o, IN(o.lineItems) l
        //                             WHERE l.quantity >= 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(greaterThanOrEqual(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = greaterThanOrEqual(
            path("co.totalPrice"),
            all(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_050(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_051() {
        test_Query_051(getGrammar());
    }

    final void test_Query_051(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice <= ALL (Select o.totalPrice
        //                             FROM Order o, IN(o.lineItems) l
        //                             WHERE l.quantity > 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(greaterThan(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = lowerThanOrEqual(
            path("co.totalPrice"),
            all(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_051(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_052() {
        test_Query_052(getGrammar());
    }

    final void test_Query_052(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)

        ExpressionTester subquery = subquery(
            subSelect(min("o.totalPrice")),
            subFrom("Order", "o")
        );

        ExpressionTester comparisonExpression = equal(
            path("co.totalPrice"),
            all(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_052(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_053() {
        test_Query_053(getGrammar());
    }

    final void test_Query_053(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice < ALL (Select o.totalPrice
        //                            FROM Order o, IN(o.lineItems) l
        //                            WHERE l.quantity > 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(greaterThan(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = lowerThan(
            path("co.totalPrice"),
            all(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_053(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_054() {
        test_Query_054(getGrammar());
    }

    final void test_Query_054(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT object(c)
        // FROM Customer c, IN(c.orders) co
        // WHERE co.totalPrice > ALL (Select o.totalPrice
        //                            FROM Order o, IN(o.lineItems) l
        //                            WHERE l.quantity > 3)

        ExpressionTester subquery = subquery(
            subSelect(path("o.totalPrice")),
            subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(greaterThan(path("l.quantity"), numeric(3L)))
        );

        ExpressionTester comparisonExpression = greaterThan(
            path("co.totalPrice"),
            all(subquery)
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
            where(comparisonExpression)
        );

        testQuery(query_054(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_055() {
        test_Query_055(getGrammar());
    }

    final void test_Query_055(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // FROM Customer c JOIN c.orders o
        // WHERE EXISTS (SELECT l
        //               FROM o.lineItems l
        //               where l.quantity > 3)

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c", join("c.orders", "o")),
            where(
                exists(
                    subquery(
                        subSelect(variable("l")),
                        subFrom(fromCollection("o.lineItems", "l")),
                        where(path("l.quantity").greaterThan(numeric(3L)))
                    )
                )
            )
        );

        testQuery(query_055(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_056() {
        test_Query_056(getGrammar());
    }

    final void test_Query_056(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // FROM Customer c JOIN c.orders o
        // WHERE EXISTS (SELECT o
        //               FROM c.orders o
        //               where o.totalPrice BETWEEN 1000 AND 1200)

        ExpressionTester subquery = subquery(
            subSelect(variable("o")),
            subFrom(fromCollection("c.orders", "o")),
            where(between(path("o.totalPrice"), numeric(1000L), numeric(1200L)))
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c", join("c.orders", "o")),
            where(exists(subquery))
        );

        testQuery(query_056(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_057() {
        test_Query_057(getGrammar());
    }

    final void test_Query_057(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // from Customer c
        // WHERE c.home.state IN(Select distinct w.state
        //                       from c.work w
        //                       where w.state = :state)

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.home.state")
                .in(
                    subquery(
                        subSelectDistinct(path("w.state")),
                        subFrom(fromCollection("c.work", "w")),
                        where(path("w.state").equal(inputParameter(":state")))
                    )
                )
            )
        );

        testQuery(query_057(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_058() {
        test_Query_058(getGrammar());
    }

    final void test_Query_058(JPQLGrammar jpqlGrammar) {

        // Select Object(o)
        // from Order o
        // WHERE EXISTS (Select c
        //               From o.customer c
        //               WHERE c.name LIKE '%Caruso')

        ExpressionTester subquery = subquery(
            subSelect(variable("c")),
            subFrom(fromCollection("o.customer", "c")),
            where(like(path("c.name"), string("'%Caruso'")))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            from("Order", "o"),
            where(exists(subquery))
        );

        testQuery(query_058(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_059() {
        test_Query_059(getGrammar());
    }

    final void test_Query_059(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // FROM Customer c
        // WHERE EXISTS (SELECT o
        //               FROM c.orders o
        //               where o.totalPrice > 1500)

        ExpressionTester subquery = subquery(
            subSelect(variable("o")),
            subFrom(fromCollection("c.orders", "o")),
            where(greaterThan(path("o.totalPrice"), numeric(1500)))
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c"),
            where(exists(subquery))
        );

        testQuery(query_059(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_060() {
        test_Query_060(getGrammar());
    }

    final void test_Query_060(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer c
        // WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)

        ExpressionTester subquery = subquery(
            subSelect(variable("o1")),
            subFrom(fromCollection("c.orders", "o1"))
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(notExists(subquery))
        );

        testQuery(query_060(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_061() {
        test_Query_061(getGrammar());
    }

    final void test_Query_061(JPQLGrammar jpqlGrammar) {

        // select object(o)
        // FROM Order o
        // Where SQRT(o.totalPrice) > :doubleValue

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            from("Order", "o"),
            where(greaterThan(sqrt(path("o.totalPrice")), inputParameter(":doubleValue")))
        );

        testQuery(query_061(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_062() {
        test_Query_062(getGrammar());
    }

    final void test_Query_062(JPQLGrammar jpqlGrammar) {

        // select sum(o.totalPrice)
        // FROM Order o
        // GROUP BY o.totalPrice
        // HAVING ABS(o.totalPrice) = :doubleValue

        ExpressionTester selectStatement = selectStatement(
            select(sum("o.totalPrice")),
            from("Order", "o"),
            groupBy(path("o.totalPrice")),
            having(equal(abs(path("o.totalPrice")), inputParameter(":doubleValue")))
        );

        testQuery(query_062(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_063() {
        test_Query_063(getGrammar());
    }

    final void test_Query_063(JPQLGrammar jpqlGrammar) {

        // select c.name
        // FROM Customer c
        // Group By c.name
        // HAVING trim(TRAILING from c.name) = ' David R. Vincent'

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c"),
            groupBy(path("c.name")),
            having(equal(trimTrailingFrom(path("c.name")), string("' David R. Vincent'")))
        );

        testQuery(query_063(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_064() {
        test_Query_064(getGrammar());
    }

    final void test_Query_064(JPQLGrammar jpqlGrammar) {

        // select c.name
        // FROM  Customer c
        // Group By c.name
        // Having trim(LEADING from c.name) = 'David R. Vincent '

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c"),
            groupBy(path("c.name")),
            having(equal(trimLeadingFrom(path("c.name")), string("'David R. Vincent '")))
        );

        testQuery(query_064(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_065() {
        test_Query_065(getGrammar());
    }

    final void test_Query_065(JPQLGrammar jpqlGrammar) {

        // select c.name
        // FROM  Customer c
        // Group by c.name
        // HAVING trim(BOTH from c.name) = 'David R. Vincent'

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c"),
            groupBy(path("c.name")),
            having(equal(trimBothFrom(path("c.name")), string("'David R. Vincent'")))
        );

        testQuery(query_065(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_066() {
        test_Query_066(getGrammar());
    }

    final void test_Query_066(JPQLGrammar jpqlGrammar) {

        // select c.name
        // FROM  Customer c
        // GROUP BY c.name
        // HAVING LOCATE('Frechette', c.name) > 0

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c"),
            groupBy(path("c.name")),
            having(greaterThan(locate(string("'Frechette'"), path("c.name")), numeric(0L)))
        );

        testQuery(query_066(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_067() {
        test_Query_067(getGrammar());
    }

    final void test_Query_067(JPQLGrammar jpqlGrammar) {

        // select a.city
        // FROM  Customer c JOIN c.home a
        // GROUP BY a.city
        // HAVING LENGTH(a.city) = 10

        ExpressionTester selectStatement = selectStatement(
            select(path("a.city")),
            from("Customer", "c", join("c.home", "a")),
            groupBy(path("a.city")),
            having(equal(length(path("a.city")), numeric(10L)))
        );

        testQuery(query_067(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_068() {
        test_Query_068(getGrammar());
    }

    final void test_Query_068(JPQLGrammar jpqlGrammar) {

        // select count(cc.country)
        // FROM  Customer c JOIN c.country cc
        // GROUP BY cc.country
        // HAVING UPPER(cc.country) = 'ENGLAND'

        ExpressionTester selectStatement = selectStatement(
            select(count("cc.country")),
            from("Customer", "c", join("c.country", "cc")),
            groupBy(path("cc.country")),
            having(equal(upper(path("cc.country")), string("'ENGLAND'")))
        );

        testQuery(query_068(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_069() {
        test_Query_069(getGrammar());
    }

    final void test_Query_069(JPQLGrammar jpqlGrammar) {

        // select count(cc.country)
        // FROM  Customer c JOIN c.country cc
        // GROUP BY cc.code
        // HAVING LOWER(cc.code) = 'gbr'

        ExpressionTester selectStatement = selectStatement(
            select(count("cc.country")),
            from("Customer", "c", join("c.country", "cc")),
            groupBy(path("cc.code")),
            having(equal(lower(path("cc.code")), string("'gbr'")))
        );

        testQuery(query_069(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_070() {
        test_Query_070(getGrammar());
    }

    final void test_Query_070(JPQLGrammar jpqlGrammar) {

        // select c.name
        // FROM  Customer c
        // Group By c.name
        // HAVING c.name = concat(:fmname, :lname)

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c"),
            groupBy(path("c.name")),
            having(equal(path("c.name"), concat(inputParameter(":fmname"), inputParameter(":lname"))))
        );

        testQuery(query_070(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_071() {
        test_Query_071(getGrammar());
    }

    final void test_Query_071(JPQLGrammar jpqlGrammar) {

        // select count(c)
        // FROM  Customer c JOIN c.aliases a
        // GROUP BY a.alias
        // HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

        ExpressionTester selectStatement = selectStatement(
            select(count(variable("c"))),
            from("Customer", "c", join("c.aliases", "a")),
            groupBy(path("a.alias")),
            having(equal(
                path("a.alias"),
                substring(inputParameter(":string1"), inputParameter(":int1"), inputParameter(":int2"))
            ))
        );

        testQuery(query_071(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_072() {
        test_Query_072(getGrammar());
    }

    final void test_Query_072(JPQLGrammar jpqlGrammar) {

        // select c.country.country
        // FROM  Customer c
        // GROUP BY c.country.country

        ExpressionTester selectStatement = selectStatement(
            select(path("c.country.country")),
            from("Customer", "c"),
            groupBy(path("c.country.country"))
        );

        testQuery(query_072(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_073() {
        test_Query_073(getGrammar());
    }

    final void test_Query_073(JPQLGrammar jpqlGrammar) {

        // select Count(c)
        // FROM  Customer c JOIN c.country cc
        // GROUP BY cc.code
        // HAVING cc.code IN ('GBR', 'CHA')

        ExpressionTester selectStatement = selectStatement(
            select(count(variable("c"))),
            from("Customer", "c", join("c.country", "cc")),
            groupBy(path("cc.code")),
            having(in(path("cc.code"), string("'GBR'"), string("'CHA'")))
        );

        testQuery(query_073(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_074() {
        test_Query_074(getGrammar());
    }

    final void test_Query_074(JPQLGrammar jpqlGrammar) {

        // select c.name
        // FROM  Customer c JOIN c.orders o
        // WHERE o.totalPrice BETWEEN 90 AND 160
        // GROUP BY c.name

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c", join("c.orders", "o")),
            where(between(path("o.totalPrice"), numeric(90), numeric(160))),
            groupBy(path("c.name"))
        );

        testQuery(query_074(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_075() {
        test_Query_075(getGrammar());
    }

    final void test_Query_075(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // FROM Order AS o
        // WHERE o.customer.id = '1001' OR o.totalPrice > 10000

        ExpressionTester orExpression = or(
            equal(path("o.customer.id"), string("'1001'")),
            greaterThan(path("o.totalPrice"), numeric(10000))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            fromAs("Order", "o"),
            where(orExpression)
        );

        testQuery(query_075(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_076() {
        test_Query_076(getGrammar());
    }

    final void test_Query_076(JPQLGrammar jpqlGrammar) {

        // select Distinct Object(o)
        // FROM Order AS o
        // WHERE o.customer.id = '1001' OR o.totalPrice < 1000

        ExpressionTester orExpression = or(
            equal(path("o.customer.id"), string("'1001'")),
            lowerThan(path("o.totalPrice"), numeric(1000))
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            fromAs("Order", "o"),
            where(orExpression)
        );

        testQuery(query_076(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_077() {
        test_Query_077(getGrammar());
    }

    final void test_Query_077(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // FROM Order AS o
        // WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

        ExpressionTester orExpression = or(
            equal(path("o.customer.name"), string("'Karen R. Tegan'")),
            greaterThan(path("o.totalPrice"), numeric(10000))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            fromAs("Order", "o"),
            where(orExpression)
        );

        testQuery(query_077(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_078() {
        test_Query_078(getGrammar());
    }

    final void test_Query_078(JPQLGrammar jpqlGrammar) {

        // select DISTINCT o
        // FROM Order AS o
        // WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

        ExpressionTester orExpression = or(
            equal(path("o.customer.name"), string("'Karen R. Tegan'")),
            greaterThan(path("o.totalPrice"), numeric(5000))
        );

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("o")),
            fromAs("Order", "o"),
            where(orExpression)
        );

        testQuery(query_078(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_079() {
        test_Query_079(getGrammar());
    }

    final void test_Query_079(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // FROM Order AS o
        // WHERE o.customer.id = '1001' AND o.totalPrice > 10000

        ExpressionTester andExpression = and(
            equal(path("o.customer.id"), string("'1001'")),
            greaterThan(path("o.totalPrice"), numeric(10000))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            fromAs("Order", "o"),
            where(andExpression)
        );

        testQuery(query_079(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_080() {
        test_Query_080(getGrammar());
    }

    final void test_Query_080(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // FROM Order AS o
        // WHERE o.customer.id = '1001' AND o.totalPrice < 1000

        ExpressionTester andExpression = and(
            equal(path("o.customer.id"), string("'1001'")),
            lowerThan(path("o.totalPrice"), numeric(1000))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            fromAs("Order", "o"),
            where(andExpression)
        );

        testQuery(query_080(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_081() {
        test_Query_081(getGrammar());
    }

    final void test_Query_081(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // FROM Order AS o
        // WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

        ExpressionTester andExpression = and(
            equal(path("o.customer.name"), string("'Karen R. Tegan'")),
            greaterThan(path("o.totalPrice"), numeric(10000))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            fromAs("Order", "o"),
            where(andExpression)
        );

        testQuery(query_081(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_082() {
        test_Query_082(getGrammar());
    }

    final void test_Query_082(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // FROM Order AS o
        // WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

        ExpressionTester andExpression = and(
            equal(path("o.customer.name"), string("'Karen R. Tegan'")),
            greaterThan(path("o.totalPrice"), numeric(500))
        );

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            fromAs("Order", "o"),
            where(andExpression)
        );

        testQuery(query_082(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_083() {
        test_Query_083(getGrammar());
    }

    final void test_Query_083(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT p
        // From Product p
        // where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("p")),
            from("Product", "p"),
            where(notBetween(path("p.shelfLife.soldDate"), inputParameter(":date1"), inputParameter(":newdate")))
        );

        testQuery(query_083(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_084() {
        test_Query_084(getGrammar());
    }

    final void test_Query_084(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT o
        // From Order o
        // where o.totalPrice NOT BETWEEN 1000 AND 1200

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("o")),
            from("Order", "o"),
            where(notBetween(path("o.totalPrice"), numeric(1000), numeric(1200)))
        );

        testQuery(query_084(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_085() {
        test_Query_085(getGrammar());
    }

    final void test_Query_085(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT p
        // From Product p
        // where p.shelfLife.soldDate BETWEEN :date1 AND :date6

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("p")),
            from("Product", "p"),
            where(between(path("p.shelfLife.soldDate"), inputParameter(":date1"), inputParameter(":date6")))
        );

        testQuery(query_085(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_086() {
        test_Query_086(getGrammar());
    }

    final void test_Query_086(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT a
        // from Alias a LEFT JOIN FETCH a.customers
        // where a.alias LIKE 'a%'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("a")),
            from("Alias", "a", leftJoinFetch("a.customers")),
            where(like(path("a.alias"), string("'a%'")))
        );

        testQuery(query_086(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_087() {
        test_Query_087(getGrammar());
    }

    final void test_Query_087(JPQLGrammar jpqlGrammar) {

        // select Object(o)
        // from Order o LEFT JOIN FETCH o.customer
        // where o.customer.name LIKE '%Caruso'

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            from("Order", "o", leftJoinFetch("o.customer")),
            where(like(path("o.customer.name"), string("'%Caruso'")))
        );

        testQuery(query_087(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_088() {
        test_Query_088(getGrammar());
    }

    final void test_Query_088(JPQLGrammar jpqlGrammar) {

        // select o
        // from Order o LEFT JOIN FETCH o.customer
        // where o.customer.home.city='Lawrence'

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o", leftJoinFetch("o.customer")),
            where(equal(path("o.customer.home.city"), string("'Lawrence'")))
        );

        testQuery(query_088(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_089() {
        test_Query_089(getGrammar());
    }

    final void test_Query_089(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // from Customer c LEFT JOIN FETCH c.orders
        // where c.home.state IN('NY','RI')

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c", leftJoinFetch("c.orders")),
            where(in(path("c.home.state"), string("'NY'"), string("'RI'")))
        );

        testQuery(query_089(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_090() {
        test_Query_090(getGrammar());
    }

    final void test_Query_090(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // from Customer c JOIN FETCH c.spouse

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c", joinFetch("c.spouse"))
        );

        testQuery(query_090(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_091() {
        test_Query_091(getGrammar());
    }

    final void test_Query_091(JPQLGrammar jpqlGrammar) {

        // SELECT Object(c)
        // from Customer c INNER JOIN c.aliases a
        // where a.alias = :aName

        ExpressionTester selectStatement = selectStatement(
            select(object("c")),
            from("Customer", "c", innerJoin("c.aliases", "a")),
            where(equal(path("a.alias"), inputParameter(":aName")))
        );

        testQuery(query_091(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_092() {
        test_Query_092(getGrammar());
    }

    final void test_Query_092(JPQLGrammar jpqlGrammar) {

        // SELECT Object(o)
        // from Order o INNER JOIN o.customer cust
        // where cust.name = ?1

        ExpressionTester selectStatement = selectStatement(
            select(object("o")),
            from("Order", "o", innerJoin("o.customer", "cust")),
            where(equal(path("cust.name"), inputParameter("?1")))
        );

        testQuery(query_092(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_093() {
        test_Query_093(getGrammar());
    }

    final void test_Query_093(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT object(c)
        // from Customer c INNER JOIN c.creditCards cc
        // where cc.type='VISA'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c", innerJoin("c.creditCards", "cc")),
            where(equal(path("cc.type"), string("'VISA'")))
        );

        testQuery(query_093(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_094() {
        test_Query_094(getGrammar());
    }

    final void test_Query_094(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // from Customer c INNER JOIN c.spouse s

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c", innerJoin("c.spouse", "s"))
        );

        testQuery(query_094(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_095() {
        test_Query_095(getGrammar());
    }

    final void test_Query_095(JPQLGrammar jpqlGrammar) {

        // select cc.type
        // FROM CreditCard cc JOIN cc.customer cust
        // GROUP BY cc.type

        ExpressionTester selectStatement = selectStatement(
            select(path("cc.type")),
            from("CreditCard", "cc", join("cc.customer", "cust")),
            groupBy(path("cc.type"))
        );

        testQuery(query_095(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_096() {
        test_Query_096(getGrammar());
    }

    final void test_Query_096(JPQLGrammar jpqlGrammar) {

        // select cc.code
        // FROM Customer c JOIN c.country cc
        // GROUP BY cc.code

        ExpressionTester selectStatement = selectStatement(
            select(path("cc.code")),
            from("Customer", "c", join("c.country", "cc")),
            groupBy(path("cc.code"))
        );

        testQuery(query_096(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_097() {
        test_Query_097(getGrammar());
    }

    final void test_Query_097(JPQLGrammar jpqlGrammar) {

        // select Object(c)
        // FROM Customer c JOIN c.aliases a
        // where LOWER(a.alias)='sjc'

        ExpressionTester selectStatement = selectStatement(
            select(object("c")),
            from("Customer", "c", join("c.aliases", "a")),
            where(equal(lower(path("a.alias")), string("'sjc'")))
        );

        testQuery(query_097(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_098() {
        test_Query_098(getGrammar());
    }

    final void test_Query_098(JPQLGrammar jpqlGrammar) {

        // select Object(c)
        // FROM Customer c JOIN c.aliases a
        // where UPPER(a.alias)='SJC'

        ExpressionTester selectStatement = selectStatement(
            select(object("c")),
            from("Customer", "c", join("c.aliases", "a")),
            where(equal(upper(path("a.alias")), string("'SJC'")))
        );

        testQuery(query_098(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_099() {
        test_Query_099(getGrammar());
    }

    final void test_Query_099(JPQLGrammar jpqlGrammar) {

        // SELECT c.id, a.alias
        // from Customer c LEFT OUTER JOIN c.aliases a
        // where c.name LIKE 'Ste%'
        // ORDER BY a.alias, c.id

        ExpressionTester selectStatement = selectStatement(
            select(path("c.id"), path("a.alias")),
            from("Customer", "c", leftOuterJoin("c.aliases", "a")),
            where(path("c.name").like(string("'Ste%'"))),
            orderBy(
                orderByItem(path("a.alias")),
                orderByItem(path("c.id"))
            )
        );

        testQuery(query_099(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_100() {
        test_Query_100(getGrammar());
    }

    final void test_Query_100(JPQLGrammar jpqlGrammar) {

        // SELECT o.id, cust.id
        // from Order o LEFT OUTER JOIN o.customer cust
        // where cust.name=?1
        // ORDER BY o.id

        ExpressionTester selectStatement = selectStatement(
            select(path("o.id"), path("cust.id")),
            from("Order", "o", leftOuterJoin("o.customer", "cust")),
            where(path("cust.name").equal(inputParameter("?1"))),
            orderBy("o.id")
        );

        testQuery(query_100(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_101() {
        test_Query_101(getGrammar());
    }

    final void test_Query_101(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // from Customer c LEFT OUTER JOIN c.creditCards cc
        // where c.name LIKE '%Caruso'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c", leftOuterJoin("c.creditCards", "cc")),
            where(path("c.name").like(string("'%Caruso'")))
        );

        testQuery(query_101(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_102() {
        test_Query_102(getGrammar());
    }

    final void test_Query_102(JPQLGrammar jpqlGrammar) {

        // SELECT Sum(p.quantity)
        // FROM Product p

        ExpressionTester selectStatement = selectStatement(
            select(sum("p.quantity")),
            from("Product", "p")
        );

        testQuery(query_102(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_103() {
        test_Query_103(getGrammar());
    }

    final void test_Query_103(JPQLGrammar jpqlGrammar) {

        // Select Count(c.home.city)
        // from Customer c

        ExpressionTester selectStatement = selectStatement(
            select(count("c.home.city")),
            from("Customer", "c")
        );

        testQuery(query_103(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_104() {
        test_Query_104(getGrammar());
    }

    final void test_Query_104(JPQLGrammar jpqlGrammar) {

        // SELECT Sum(p.price)
        // FROM Product p

        ExpressionTester selectStatement = selectStatement(
            select(sum("p.price")),
            from("Product", "p")
        );

        testQuery(query_104(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_105() {
        test_Query_105(getGrammar());
    }

    final void test_Query_105(JPQLGrammar jpqlGrammar) {

        // SELECT AVG(o.totalPrice)
        // FROM Order o

        ExpressionTester selectStatement = selectStatement(
            select(avg("o.totalPrice")),
            from("Order", "o")
        );

        testQuery(query_105(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_106() {
        test_Query_106(getGrammar());
    }

    final void test_Query_106(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT MAX(l.quantity)
        // FROM LineItem l

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(max("l.quantity")),
            from("LineItem", "l")
        );

        testQuery(query_106(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_107() {
        test_Query_107(getGrammar());
    }

    final void test_Query_107(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT MIN(o.id)
        // FROM Order o
        // where o.customer.name = 'Robert E. Bissett'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(min("o.id")),
            from("Order", "o"),
            where(path("o.customer.name").equal(string("'Robert E. Bissett'")))
        );

        testQuery(query_107(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_108() {
        test_Query_108(getGrammar());
    }

    final void test_Query_108(JPQLGrammar jpqlGrammar) {

        // SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name)
        // FROM Customer c
        // where c.work.city = :workcity

        ExpressionTester selectStatement = selectStatement(
            select(new_(
                "com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer",
                path("c.id"),
                path("c.name")
            )),
            from("Customer", "c"),
            where(path("c.work.city").equal(inputParameter(":workcity")))
        );

        testQuery(query_108(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_109() {
        test_Query_109(getGrammar());
    }

    final void test_Query_109(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // FROM Customer c
        // WHERE SIZE(c.orders) > 100

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c"),
            where(size("c.orders").greaterThan(numeric(100)))
        );

        testQuery(query_109(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_110() {
        test_Query_110(getGrammar());
    }

    final void test_Query_110(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // FROM Customer c
        // WHERE SIZE(c.orders) >= 2

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c"),
            where(size("c.orders").greaterThanOrEqual(numeric(2)))
        );

        testQuery(query_110(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_111() {
        test_Query_111(getGrammar());
    }

    final void test_Query_111(JPQLGrammar jpqlGrammar) {

        // select Distinct c
        // FROM Customer c LEFT OUTER JOIN c.work workAddress
        // where workAddress.zip IS NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c", leftOuterJoin("c.work", "workAddress")),
            where(isNull(path("workAddress.zip")))
        );

        testQuery(query_111(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_112() {
        test_Query_112(getGrammar());
    }

    final void test_Query_112(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // FROM Customer c, IN(c.orders) o

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from(
                fromEntity("Customer", "c"),
                fromIn("c.orders", "o"))
        );

        testQuery(query_112(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_113() {
        test_Query_113(getGrammar());
    }

    final void test_Query_113(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // from Customer c
        // where c.name is null

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(isNull(path("c.name")))
        );

        testQuery(query_113(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_114() {
        test_Query_114(getGrammar());
    }

    final void test_Query_114(JPQLGrammar jpqlGrammar) {

        // Select c.name
        // from Customer c
        // where c.home.street = '212 Edgewood Drive'

        ExpressionTester selectStatement = selectStatement(
            select(path("c.name")),
            from("Customer", "c"),
            where(path("c.home.street").equal(string("'212 Edgewood Drive'")))
        );

        testQuery(query_114(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_115() {
        test_Query_115(getGrammar());
    }

    final void test_Query_115(JPQLGrammar jpqlGrammar) {

        // Select s.customer
        // from Spouse s
        // where s.id = '6'

        ExpressionTester selectStatement = selectStatement(
            select(path("s.customer")),
            from("Spouse", "s"),
            where(path("s.id").equal(string("'6'")))
        );

        testQuery(query_115(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_116() {
        test_Query_116(getGrammar());
    }

    final void test_Query_116(JPQLGrammar jpqlGrammar) {

        // Select c.work.zip
        // from Customer c

        ExpressionTester selectStatement = selectStatement(
            select(path("c.work.zip")),
            from("Customer", "c")
        );

        testQuery(query_116(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_117() {
        test_Query_117(getGrammar());
    }

    final void test_Query_117(JPQLGrammar jpqlGrammar) {

        // SELECT Distinct Object(c)
        // From Customer c, IN(c.home.phones) p
        // where p.area LIKE :area

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(
                fromEntity("Customer", "c"),
                fromIn("c.home.phones", "p")),
            where(
                    path("p.area")
                .like(
                    inputParameter(":area")
                )
            )
        );

        testQuery(query_117(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_118() {
        test_Query_118(getGrammar());
    }

    final void test_Query_118(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT Object(c)
        // from Customer c, in(c.aliases) a
        // where NOT a.customerNoop IS NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(not(isNull(path("a.customerNoop"))))
        );

        testQuery(query_118(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_119() {
        test_Query_119(getGrammar());
    }

    final void test_Query_119(JPQLGrammar jpqlGrammar) {

        // select distinct object(c)
        // fRoM Customer c, IN(c.aliases) a
        // where c.name = :cName OR a.customerNoop IS NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(path("c.name").equal(inputParameter(":cName")).or(isNull(path("a.customerNoop"))))
        );

        testQuery(query_119(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_120() {
        test_Query_120(getGrammar());
    }

    final void test_Query_120(JPQLGrammar jpqlGrammar) {

        // select Distinct Object(c)
        // from Customer c, in(c.aliases) a
        // where c.name = :cName AND a.customerNoop IS NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(path("c.name").equal(inputParameter(":cName")).and(isNull(path("a.customerNoop"))))
        );

        testQuery(query_120(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_121() {
        test_Query_121(getGrammar());
    }

    final void test_Query_121(JPQLGrammar jpqlGrammar) {

        // sElEcT Distinct oBJeCt(c)
        // FROM Customer c, IN(c.aliases) a
        // WHERE a.customerNoop IS NOT NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(isNotNull(path("a.customerNoop")))
        );

        testQuery(query_121(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_122() {
        test_Query_122(getGrammar());
    }

    final void test_Query_122(JPQLGrammar jpqlGrammar) {

        // select distinct Object(c)
        // FROM Customer c, in(c.aliases) a
        // WHERE a.alias LIKE '%\\_%' escape '\\'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(path("a.alias").like(string("'%\\_%'"), string('\\')))
        );

        testQuery(query_122(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_123() {
        test_Query_123(getGrammar());
    }

    final void test_Query_123(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c, in(c.aliases) a
        // WHERE a.customerNoop IS NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(isNull(path("a.customerNoop")))
        );

        testQuery(query_123(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_124() {
        test_Query_124(getGrammar());
    }

    final void test_Query_124(JPQLGrammar jpqlGrammar) {

        // Select Distinct o.creditCard.balance
        // from Order o
        // ORDER BY o.creditCard.balance ASC

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(path("o.creditCard.balance")),
            from("Order", "o"),
            orderBy(orderByItemAsc("o.creditCard.balance"))
        );

        testQuery(query_124(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_125() {
        test_Query_125(getGrammar());
    }

    final void test_Query_125(JPQLGrammar jpqlGrammar) {

        // Select c.work.zip
        // from Customer c
        // where c.work.zip IS NOT NULL
        // ORDER BY c.work.zip ASC

        ExpressionTester selectStatement = selectStatement(
            select(path("c.work.zip")),
            from("Customer", "c"),
            where(isNotNull(path("c.work.zip"))),
            orderBy(orderByItemAsc("c.work.zip"))
        );

        testQuery(query_125(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_126() {
        test_Query_126(getGrammar());
    }

    final void test_Query_126(JPQLGrammar jpqlGrammar) {

        // SELECT a.alias
        // FROM Alias AS a
        // WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1

        ExpressionTester orExpression =
                sub(
                        isNull(path("a.alias"))
                    .and(
                        isNull(inputParameter(":param1"))))
            .or(
                path("a.alias").equal(inputParameter(":param1")));

        ExpressionTester selectStatement = selectStatement(
            select(path("a.alias")),
            fromAs("Alias", "a"),
            where(orExpression)
        );

        testQuery(query_126(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_127() {
        test_Query_127(getGrammar());
    }

    final void test_Query_127(JPQLGrammar jpqlGrammar) {

        // Select Object(c)
        // from Customer c
        // where c.aliasesNoop IS NOT EMPTY or c.id <> '1'

        ExpressionTester orExpression =
                isNotEmpty("c.aliasesNoop")
            .or(
                path("c.id").different(string("'1'")));

        ExpressionTester selectStatement = selectStatement(
            select(object("c")),
            from("Customer", "c"),
            where(orExpression)
        );

        testQuery(query_127(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_128() {
        test_Query_128(getGrammar());
    }

    final void test_Query_128(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(p)
        // from Product p
        // where p.name = ?1

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("p")),
            from("Product", "p"),
            where(path("p.name").equal(inputParameter("?1")))
        );

        testQuery(query_128(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_129() {
        test_Query_129(getGrammar());
    }

    final void test_Query_129(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(p)
        // from Product p
        // where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)

        ExpressionTester addition =
                sub(numeric(500)
            .add(
                inputParameter(":int1")));

        ExpressionTester andExpression =
                sub(path("p.quantity").greaterThan(addition))
            .and(
                sub(isNull(path("p.partNumber"))));

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("p")),
            from("Product", "p"),
            where(andExpression)
        );

        testQuery(query_129(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_130() {
        test_Query_130(getGrammar());
    }

    final void test_Query_130(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(o)
        // from Order o
        // where o.customer.name IS NOT NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(isNotNull(path("o.customer.name")))
        );

        testQuery(query_130(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_131() {
        test_Query_131(getGrammar());
    }

    final void test_Query_131(JPQLGrammar jpqlGrammar) {

        // Select DISTINCT Object(p)
        // From Product p
        // where (p.quantity < 10) OR (p.quantity > 20)

        ExpressionTester orExpression =
                sub(path("p.quantity").lowerThan(numeric(10)))
            .or(
                sub(path("p.quantity").greaterThan(numeric(20))));

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("p")),
            from("Product", "p"),
            where(orExpression)
        );

        testQuery(query_131(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_132() {
        test_Query_132(getGrammar());
    }

    final void test_Query_132(JPQLGrammar jpqlGrammar) {

        // Select DISTINCT Object(p)
        // From Product p
        // where p.quantity NOT BETWEEN 10 AND 20

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("p")),
            from("Product", "p"),
            where(path("p.quantity").notBetween(numeric(10), numeric(20)))
        );

        testQuery(query_132(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_133() {
        test_Query_133(getGrammar());
    }

    final void test_Query_133(JPQLGrammar jpqlGrammar) {

        // Select DISTINCT OBJECT(p)
        // From Product p
        // where (p.quantity >= 10) AND (p.quantity <= 20)

        ExpressionTester andExpression =
                sub(path("p.quantity").greaterThanOrEqual(numeric(10)))
            .and(
                sub(path("p.quantity").lowerThanOrEqual(numeric(20))));

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("p")),
            from("Product", "p"),
            where(andExpression)
        );

        testQuery(query_133(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_134() {
        test_Query_134(getGrammar());
    }

    final void test_Query_134(JPQLGrammar jpqlGrammar) {

        // Select DISTINCT OBJECT(p)
        // From Product p
        // where p.quantity BETWEEN 10 AND 20

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("p")),
            from("Product", "p"),
            where(path("p.quantity").between(numeric(10), numeric(20)))
        );

        testQuery(query_134(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_135() {
        test_Query_135(getGrammar());
    }

    final void test_Query_135(JPQLGrammar jpqlGrammar) {

        // Select Distinct OBJECT(c)
        // from Customer c, IN(c.creditCards) b
        // where SQRT(b.balance) = :dbl

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.creditCards", "b")),
            where(sqrt(path("b.balance")).equal(inputParameter(":dbl")))
        );

        testQuery(query_135(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_136() {
        test_Query_136(getGrammar());
    }

    final void test_Query_136(JPQLGrammar jpqlGrammar) {

        // Select Distinct OBJECT(c)
        // From Product p
        // where MOD(550, 100) = p.quantity

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Product", "p"),
            where(mod(numeric(550), numeric(100)).equal(path("p.quantity")))
        );

        testQuery(query_136(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_137() {
        test_Query_137(getGrammar());
    }

    final void test_Query_137(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT Object(c)
        // from Customer c
        // WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')

        ExpressionTester orExpression =
                sub(path("c.home.state").equal(string("'NH'")))
            .or(
                sub(path("c.home.state").equal(string("'RI'"))));

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(orExpression)
        );

        testQuery(query_137(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_138() {
        test_Query_138(getGrammar());
    }

    final void test_Query_138(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT Object(c)
        // from Customer c
        // where c.home.state IN('NH', 'RI')

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(path("c.home.state").in(string("'NH'"), string("'RI'")))
        );

        testQuery(query_138(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_139() {
        test_Query_139(getGrammar());
    }

    final void test_Query_139(JPQLGrammar jpqlGrammar) {

        // SELECT o
        // FROM Customer c JOIN c.orders o JOIN c.address a
        // WHERE a.state = 'CA'
        // ORDER BY o.quantity DESC, o.totalcost

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
            where(path("a.state").equal(string("'CA'"))),
            orderBy(orderByItemDesc("o.quantity"), orderByItem("o.totalcost"))
        );

        testQuery(query_139(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_140() {
        test_Query_140(getGrammar());
    }

    final void test_Query_140(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // from Customer c
        // where c.home.city IN(:city)

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(path("c.home.city").in(inputParameter(":city")))
        );

        testQuery(query_140(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_141() {
        test_Query_141(getGrammar());
    }

    final void test_Query_141(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(o)
        // from Order o, in(o.lineItems) l
        // where l.quantity NOT IN (1, 5)

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(path("l.quantity").notIn(numeric(1), numeric(5)))
        );

        testQuery(query_141(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_142() {
        test_Query_142(getGrammar());
    }

    final void test_Query_142(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(o)
        // FROM Order o
        // WHERE o.sampleLineItem MEMBER OF o.lineItems

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(path("o.sampleLineItem").memberOf(collectionPath("o.lineItems")))
        );

        testQuery(query_142(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_143() {
        test_Query_143(getGrammar());
    }

    final void test_Query_143(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(o)
        // FROM Order o
        // WHERE :param NOT MEMBER o.lineItems

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(inputParameter(":param").notMember(collectionPath("o.lineItems")))
        );

        testQuery(query_143(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_144() {
        test_Query_144(getGrammar());
    }

    final void test_Query_144(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(o)
        // FROM Order o, LineItem l
        // WHERE l MEMBER o.lineItems

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o", "LineItem", "l"),
            where(variable("l").member(collectionPath("o.lineItems")))
        );

        testQuery(query_144(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_145() {
        test_Query_145(getGrammar());
    }

    final void test_Query_145(JPQLGrammar jpqlGrammar) {

        // select distinct Object(c)
        // FROM Customer c, in(c.aliases) a
        // WHERE a.alias LIKE 'sh\\_ll' escape '\\'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(path("a.alias").like(string("'sh\\_ll'"), string('\\')))
        );

        testQuery(query_145(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_146() {
        test_Query_146(getGrammar());
    }

    final void test_Query_146(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(a)
        // FROM Alias a
        // WHERE a.customerNoop NOT MEMBER OF a.customersNoop

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("a")),
            from("Alias", "a"),
            where(path("a.customerNoop").notMemberOf(collectionPath("a.customersNoop")))
        );

        testQuery(query_146(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_147() {
        test_Query_147(getGrammar());
    }

    final void test_Query_147(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(a)
        // FROM Alias a
        // WHERE a.customerNoop MEMBER OF a.customersNoop

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("a")),
            from("Alias", "a"),
            where(path("a.customerNoop").memberOf(collectionPath("a.customersNoop")))
        );

        testQuery(query_147(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_148() {
        test_Query_148(getGrammar());
    }

    final void test_Query_148(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(a)
        // from Alias a
        // where LOCATE('ev', a.alias) = 3

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("a")),
            from("Alias", "a"),
            where(locate(string("'ev'"), path("a.alias")).equal(numeric(3)))
        );

        testQuery(query_148(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_149() {
        test_Query_149(getGrammar());
    }

    final void test_Query_149(JPQLGrammar jpqlGrammar) {

        // Select DISTINCT Object(o)
        // From Order o
        // WHERE o.totalPrice > ABS(:dbl)

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(path("o.totalPrice").greaterThan(abs(inputParameter(":dbl"))))
        );

        testQuery(query_149(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_150() {
        test_Query_150(getGrammar());
    }

    final void test_Query_150(JPQLGrammar jpqlGrammar) {

        // Select Distinct OBjeCt(a)
        // From Alias a
        // WHERE LENGTH(a.alias) > 4

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("a")),
            from("Alias", "a"),
            where(length(path("a.alias")).greaterThan(numeric(4)))
        );

        testQuery(query_150(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_151() {
        test_Query_151(getGrammar());
    }

    final void test_Query_151(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(a)
        // From Alias a
        // WHERE a.alias = SUBSTRING(:string1, :int2, :int3)

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("a")),
            from("Alias", "a"),
            where(
                    path("a.alias")
                .equal(
                    substring(inputParameter(":string1"), inputParameter(":int2"), inputParameter(":int3"))))
        );

        testQuery(query_151(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_152() {
        test_Query_152(getGrammar());
    }

    final void test_Query_152(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(a)
        // From Alias a
        // WHERE a.alias = CONCAT('ste', 'vie')

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("a")),
            from("Alias", "a"),
            where(
                    path("a.alias")
                .equal(
                    concat(string("'ste'"), string("'vie'"))))
        );

        testQuery(query_152(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_153() {
        test_Query_153(getGrammar());
    }

    final void test_Query_153(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c
        // WHERE c.work.zip IS NOT NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(isNotNull(path("c.work.zip")))
        );

        testQuery(query_153(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_154() {
        test_Query_154(getGrammar());
    }

    final void test_Query_154(JPQLGrammar jpqlGrammar) {

        // sELEct dIsTiNcT oBjEcT(c)
        // FROM Customer c
        // WHERE c.work.zip IS NULL

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(isNull(path("c.work.zip")))
        );

        testQuery(query_154(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_155() {
        test_Query_155(getGrammar());
    }

    final void test_Query_155(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c
        // WHERE c.aliases IS NOT EMPTY

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(isNotEmpty("c.aliases"))
        );

        testQuery(query_155(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_156() {
        test_Query_156(getGrammar());
    }

    final void test_Query_156(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c
        // WHERE c.aliases IS EMPTY

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(isEmpty("c.aliases"))
        );

        testQuery(query_156(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_157() {
        test_Query_157(getGrammar());
    }

    final void test_Query_157(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c
        // WHERE c.home.zip not like '%44_'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(path("c.home.zip").notLike(string("'%44_'")))
        );

        testQuery(query_157(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_158() {
        test_Query_158(getGrammar());
    }

    final void test_Query_158(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c
        // WHERE c.home.zip LIKE '%77'"

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c"),
            where(path("c.home.zip").like(string("'%77'")))
        );

        testQuery(query_158(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_159() {
        test_Query_159(getGrammar());
    }

    final void test_Query_159(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer c Left Outer Join c.home h
        // WHERE h.city Not iN ('Swansea', 'Brookline')

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from("Customer", "c", leftOuterJoin("c.home", "h")),
            where(path("h.city").notIn(string("'Swansea'"), string("'Brookline'")))
        );

        testQuery(query_159(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_160() {
        test_Query_160(getGrammar());
    }

    final void test_Query_160(JPQLGrammar jpqlGrammar) {

        // select distinct c
        // FROM Customer c
        // WHERE c.home.city IN ('Lexington')

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c"),
            where(path("c.home.city").in(string("'Lexington'")))
        );

        testQuery(query_160(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_161() {
        test_Query_161(getGrammar());
    }

    final void test_Query_161(JPQLGrammar jpqlGrammar) {

        // sElEcT c
        // FROM Customer c
        // Where c.name = :cName

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(path("c.name").equal(inputParameter(":cName")))
        );

        testQuery(query_161(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_162() {
        test_Query_162(getGrammar());
    }

    final void test_Query_162(JPQLGrammar jpqlGrammar) {

        // select distinct Object(o)
        // From Order o
        // WHERE o.creditCard.approved = FALSE

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(path("o.creditCard.approved").equal(FALSE()))
        );

        testQuery(query_162(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_163() {
        test_Query_163(getGrammar());
    }

    final void test_Query_163(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT Object(o)
        // From Order o
        // where o.totalPrice NOT bETwEeN 1000 AND 1200

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(path("o.totalPrice").notBetween(numeric(1000), numeric(1200)))
        );

        testQuery(query_163(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_164() {
        test_Query_164(getGrammar());
    }

    final void test_Query_164(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT Object(o)
        // From Order o
        // where o.totalPrice BETWEEN 1000 AND 1200

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(path("o.totalPrice").between(numeric(1000), numeric(1200)))
        );

        testQuery(query_164(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_165() {
        test_Query_165(getGrammar());
    }

    final void test_Query_165(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT Object(o)
        // FROM Order o, in(o.lineItems) l
        // WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
            where(    path("l.quantity")
                    .lowerThan(
                        numeric(2))
                .and(
                        path("o.customer.name")
                    .equal(
                        string("'Robert E. Bissett'"))))
        );

        testQuery(query_165(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_166() {
        test_Query_166(getGrammar());
    }

    final void test_Query_166(JPQLGrammar jpqlGrammar) {

        // select distinct Object(o)
        // FROM Order AS o, in(o.lineItems) l
        // WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from(fromEntityAs("Order", "o"), fromIn("o.lineItems", "l")),
            where(
                    sub(path("l.quantity").lowerThan(numeric(2)))
                .and(
                    sub(
                        sub(
                                path("o.totalPrice")
                            .lowerThan(
                                sub(
                                        numeric(3)
                                    .add(
                                            numeric(54).multiply(numeric(2))
                                        .add(
                                            numeric(-8)
                                        )
                                    )
                                )
                            )
                        )
                        .or(
                            sub(
                                path("o.customer.name").equal(string("'Robert E. Bissett'"))
                            )
                        )
                    )
                )
            )
        );

        testQuery(query_166(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_167() {
        test_Query_167(getGrammar());
    }

    final void test_Query_167(JPQLGrammar jpqlGrammar) {

        // SeLeCt DiStInCt oBjEcT(o)
        // FROM Order AS o
        // WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            fromAs("Order", "o"),
            where(
                    path("o.customer.name").equal(string("'Karen R. Tegan'"))
                .or(
                    path("o.totalPrice").lowerThan(numeric(100))
                )
            )
        );

        testQuery(query_167(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_168() {
        test_Query_168(getGrammar());
    }

    final void test_Query_168(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(o)
        // FROM Order o
        // WHERE NOT o.totalPrice < 4500

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("o")),
            from("Order", "o"),
            where(not(path("o.totalPrice").lowerThan(numeric(4500))))
        );

        testQuery(query_168(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_169() {
        test_Query_169(getGrammar());
    }

    final void test_Query_169(JPQLGrammar jpqlGrammar) {

        // Select DISTINCT Object(P)
        // From Product p

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("P")),
            from("Product", "p")
        );

        testQuery(query_169(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_170() {
        test_Query_170(getGrammar());
    }

    final void test_Query_170(JPQLGrammar jpqlGrammar) {

        // SELECT DISTINCT c
        // from Customer c
        // WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.home.street").equal(inputParameter(":street"))
                .or(
                    path("c.home.city").equal(inputParameter(":city"))
                )
                .or(
                    path("c.home.state").equal(inputParameter(":state"))
                )
                .or(
                    path("c.home.zip").equal(inputParameter(":zip"))
                )
            )
        );

        testQuery(query_170(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_171() {
        test_Query_171(getGrammar());
    }

    final void test_Query_171(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // from Customer c
        // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.home.street").equal(inputParameter(":street"))
                .and(
                    path("c.home.city").equal(inputParameter(":city"))
                )
                .and(
                    path("c.home.state").equal(inputParameter(":state"))
                )
                .and(
                    path("c.home.zip").equal(inputParameter(":zip"))
                )
            )
        );

        testQuery(query_172(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_172() {
        test_Query_172(getGrammar());
    }

    final void test_Query_172(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // from Customer c
      // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.home.street").equal(inputParameter(":street"))
                .and(
                    path("c.home.city").equal(inputParameter(":city"))
                )
                .and(
                    path("c.home.state").equal(inputParameter(":state"))
                )
                .and(
                    path("c.home.zip").equal(inputParameter(":zip"))
                )
            )
        );

        testQuery(query_172(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_173() {
        test_Query_173(getGrammar());
    }

    final void test_Query_173(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FrOm Customer c, In(c.aliases) a
        // WHERE a.alias = :aName

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
            where(path("a.alias").equal(inputParameter(":aName")))
        );

        testQuery(query_173(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_174() {
        test_Query_174(getGrammar());
    }

    final void test_Query_174(JPQLGrammar jpqlGrammar) {

        // Select Distinct Object(c)
        // FROM Customer AS c

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(object("c")),
            fromAs("Customer", "c")
        );

        testQuery(query_174(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_175() {
        test_Query_175(getGrammar());
    }

    final void test_Query_175(JPQLGrammar jpqlGrammar) {

        // Select Distinct o
        // from Order AS o
        // WHERE o.customer.name = :name

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(variable("o")),
            fromAs("Order", "o"),
            where(path("o.customer.name").equal(inputParameter(":name")))
        );

        testQuery(query_175(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_176() {
        test_Query_176(getGrammar());
    }

    final void test_Query_176(JPQLGrammar jpqlGrammar) {

        // UPDATE Customer c SET c.name = 'CHANGED'
        // WHERE c.orders IS NOT EMPTY

        ExpressionTester updateStatement = updateStatement(
            update("Customer", "c", set(path("c.name"), string("'CHANGED'"))),
            where(isNotEmpty("c.orders"))
        );

        testQuery(query_176(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_177() {
        test_Query_177(getGrammar());
    }

    final void test_Query_177(JPQLGrammar jpqlGrammar) {

        // UPDATE DateTime SET date = CURRENT_DATE

        ExpressionTester updateStatement = updateStatement(
            update("DateTime", "this", set("{this}.date", CURRENT_DATE()), false)
        );

        testQuery(query_177(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_178() {
        test_Query_178(getGrammar());
    }

    final void test_Query_178(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer c
        // WHERE c.firstName = :first AND
        //     c.lastName = :last

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.firstName").equal(inputParameter(":first"))
                .and(
                    path("c.lastName").equal(inputParameter(":last"))
                )
            )
        );

        testQuery(query_178(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_179() {
        test_Query_179(getGrammar());
    }

    final void test_Query_179(JPQLGrammar jpqlGrammar) {

        // SELECT OBJECT ( c ) FROM Customer AS c

        ExpressionTester selectStatement = selectStatement(
            select(object("c")),
            fromAs("Customer", "c")
        );

        testQuery(query_179(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_180() {
        test_Query_180(getGrammar());
    }

    final void test_Query_180(JPQLGrammar jpqlGrammar) {

        // SELECT c.firstName, c.lastName
        // FROM Customer AS c

        ExpressionTester selectStatement = selectStatement(
            select(path("c.firstName"), path("c.lastName")),
            fromAs("Customer", "c")
        );

        testQuery(query_180(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_181() {
        test_Query_181(getGrammar());
    }

    final void test_Query_181(JPQLGrammar jpqlGrammar) {

        // SELECT c.address.city
        // FROM Customer AS c

        ExpressionTester selectStatement = selectStatement(
            select(path("c.address.city")),
            fromAs("Customer", "c")
        );

        testQuery(query_181(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_182() {
        test_Query_182(getGrammar());
    }

    final void test_Query_182(JPQLGrammar jpqlGrammar) {

        // SELECT new com.titan.domain.Name(c.firstName, c.lastName)
        // FROM Customer c

        ExpressionTester selectStatement = selectStatement(
            select(new_("com.titan.domain.Name", path("c.firstName"), path("c.lastName"))),
            from("Customer", "c")
        );

        testQuery(query_182(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_183() {
        test_Query_183(getGrammar());
    }

    final void test_Query_183(JPQLGrammar jpqlGrammar) {

        // SELECT cbn.ship
        // FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn

        ExpressionTester selectStatement = selectStatement(
            select(path("cbn.ship")),
            from(
                fromEntityAs("Customer", "c"),
                fromIn("c.reservations", "r"),
                fromIn("r.cabins", "cbn")
            )
        );

        testQuery(query_183(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_184() {
        test_Query_184(getGrammar());
    }

    final void test_Query_184(JPQLGrammar jpqlGrammar) {

        // Select c.firstName, c.lastName, p.number
        // From Customer c Left Join c.phoneNumbers p

        ExpressionTester selectStatement = selectStatement(
            select(path("c.firstName"), path("c.lastName"), path("p.number")),
            from("Customer", "c", leftJoin("c.phoneNumbers", "p"))
        );

        testQuery(query_184(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_185() {
        test_Query_185(getGrammar());
    }

    final void test_Query_185(JPQLGrammar jpqlGrammar) {

        // SELECT r
        // FROM Reservation AS r
        // WHERE (r.amountPaid * .01) > 300.00

        ExpressionTester selectStatement = selectStatement(
            select(variable("r")),
            fromAs("Reservation", "r"),
            where(
                    sub(path("r.amountPaid").multiply(numeric(".01")))
                .greaterThan(
                    numeric("300.00")
                )
            )
        );

        testQuery(query_185(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_186() {
        test_Query_186(getGrammar());
    }

    final void test_Query_186(JPQLGrammar jpqlGrammar) {

        // SELECT s
        // FROM Ship AS s
        // WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00

        ExpressionTester selectStatement = selectStatement(
            select(variable("s")),
            fromAs("Ship", "s"),
            where(
                    path("s.tonnage").greaterThanOrEqual(numeric("80000.00"))
                .and(
                    path("s.tonnage").lowerThanOrEqual(numeric("130000.00"))
                )
            )
        );

        testQuery(query_186(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_187() {
        test_Query_187(getGrammar());
    }

    final void test_Query_187(JPQLGrammar jpqlGrammar) {

        // SELECT r
        // FROM Reservation r, IN ( r.customers ) AS cust
        // WHERE cust = :specificCustomer

        ExpressionTester selectStatement = selectStatement(
            select(variable("r")),
            from(fromEntity("Reservation", "r"), fromInAs("r.customers", "cust")),
            where(variable("cust").equal(inputParameter(":specificCustomer")))
        );

        testQuery(query_187(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_188() {
        test_Query_188(getGrammar());
    }

    final void test_Query_188(JPQLGrammar jpqlGrammar) {

        // SELECT s
        // FROM Ship AS s
        // WHERE s.tonnage BETWEEN 80000.00 AND 130000.00

        ExpressionTester selectStatement = selectStatement(
            select(variable("s")),
            fromAs("Ship", "s"),
            where(path("s.tonnage").between(numeric("80000.00"), numeric("130000.00")))
        );

        testQuery(query_188(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_189() {
        test_Query_189(getGrammar());
    }

    final void test_Query_189(JPQLGrammar jpqlGrammar) {

        // SELECT s
        // FROM Ship AS s
        // WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00

        ExpressionTester selectStatement = selectStatement(
            select(variable("s")),
            fromAs("Ship", "s"),
            where(path("s.tonnage").notBetween(numeric("80000.00"), numeric("130000.00")))
        );

        testQuery(query_189(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_190() {
        test_Query_190(getGrammar());
    }

    final void test_Query_190(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer AS c
        //  WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            fromAs("Customer", "c"),
            where(path("c.address.state").in(string("'FL'"), string("'TX'"), string("'MI'"), string("'WI'"), string("'MN'")))
        );

        testQuery(query_190(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_191() {
        test_Query_191(getGrammar());
    }

    final void test_Query_191(JPQLGrammar jpqlGrammar) {

        // SELECT cab
        // FROM Cabin AS cab
        // WHERE cab.deckLevel IN (1,3,5,7)

        ExpressionTester selectStatement = selectStatement(
            select(variable("cab")),
            fromAs("Cabin", "cab"),
            where(
                    path("cab.deckLevel")
                .in(
                    numeric(1),
                    numeric(3),
                    numeric(5),
                    numeric(7)
                )
            )
        );

        testQuery(query_191(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_192() {
        test_Query_192(getGrammar());
    }

    final void test_Query_192(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer c
        // WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.address.state")
                .in(
                    inputParameter("?1"),
                    inputParameter("?2"),
                    inputParameter("?3"),
                    string("'WI'"),
                    string("'MN'")
                )
            )
        );

        testQuery(query_192(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_193() {
        test_Query_193(getGrammar());
    }

    final void test_Query_193(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer c
        // WHERE c.address IS NULL

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(isNull(path("c.address")))
        );

        testQuery(query_193(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_194() {
        test_Query_194(getGrammar());
    }

    final void test_Query_194(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer c
        // WHERE c.address.state = 'TX' AND
        //       c.lastName = 'Smith' AND
        //       c.firstName = 'John'

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.address.state").equal(string("'TX'"))
                .and(
                    path("c.lastName").equal(string("'Smith'"))
                )
                .and(
                    path("c.firstName").equal(string("'John'"))
                )
            )
        );

        testQuery(query_194(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_195() {
        test_Query_195(getGrammar());
    }

    final void test_Query_195(JPQLGrammar jpqlGrammar) {

        // SELECT crs
        // FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust
        // WHERE
        //  cust = :myCustomer
        //  AND
        //  cust MEMBER OF res.customers

        ExpressionTester selectStatement = selectStatement(
            select(variable("crs")),
            from(
                fromEntityAs("Cruise", "crs"),
                fromInAs("crs.reservations", "res"),
                fromEntityAs("Customer", "cust")
            ),
            where(
                    variable("cust").equal(inputParameter(":myCustomer"))
                .and(
                    variable("cust").memberOf(collectionPath("res.customers"))
                )
            )
        );

        testQuery(query_195(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_196() {
        test_Query_196(getGrammar());
    }

    final void test_Query_196(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer AS c
        // WHERE    LENGTH(c.lastName) > 6
        //       AND
        //          LOCATE( c.lastName, 'Monson' ) > -1

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            fromAs("Customer", "c"),
            where(
                    length(path("c.lastName")).greaterThan(numeric(6))
                .and(
                        locate(path("c.lastName"), string("'Monson'"))
                    .greaterThan(
                        numeric(-1)
                    )
                )
            )
        );

        testQuery(query_196(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_197() {
        test_Query_197(getGrammar());
    }

    final void test_Query_197(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer AS C
        // ORDER BY c.lastName

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            fromAs("Customer", "C"),
            orderBy("c.lastName")
        );

        testQuery(query_197(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_198() {
        test_Query_198(getGrammar());
    }

    final void test_Query_198(JPQLGrammar jpqlGrammar) {

        // SELECT c
        // FROM Customer AS C
      // WHERE c.address.city = 'Boston' AND c.address.state = 'MA'
        // ORDER BY c.lastName DESC

        ExpressionTester selectStatement = selectStatement(
            select(variable("c")),
            fromAs("Customer", "C"),
            where(
                    path("c.address.city").equal(string("'Boston'"))
                .and(
                    path("c.address.state").equal(string("'MA'"))
                )
            ),
            orderBy(orderByItemDesc("c.lastName"))
        );

        testQuery(query_198(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_199() {
        test_Query_199(getGrammar());
    }

    final void test_Query_199(JPQLGrammar jpqlGrammar) {

        // SELECT cr.name, COUNT (res)
        // FROM Cruise cr LEFT JOIN cr.reservations res
        // GROUP BY cr.name

        ExpressionTester selectStatement = selectStatement(
            select(path("cr.name"), count(variable("res"))),
            from("Cruise", "cr", leftJoin("cr.reservations", "res")),
            groupBy(path("cr.name"))
        );

        testQuery(query_199(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_200() {
        test_Query_200(getGrammar());
    }

    final void test_Query_200(JPQLGrammar jpqlGrammar) {

        // SELECT cr.name, COUNT (res)
        // FROM Cruise cr LEFT JOIN cr.reservations res
        // GROUP BY cr.name
        // HAVING count(res) > 10

        ExpressionTester selectStatement = selectStatement(
            select(path("cr.name"), count(variable("res"))),
            from("Cruise", "cr", leftJoin("cr.reservations", "res")),
            groupBy(path("cr.name")),
            having(count(variable("res")).greaterThan(numeric(10)))
        );

        testQuery(query_200(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_201() {
        test_Query_201(getGrammar());
    }

    final void test_Query_201(JPQLGrammar jpqlGrammar) {

        // SELECT COUNT (res)
        // FROM Reservation res
        // WHERE res.amountPaid >
        //       (SELECT avg(r.amountPaid) FROM Reservation r)

        ExpressionTester selectStatement = selectStatement(
            select(count(variable("res"))),
            from("Reservation", "res"),
            where(
                    path("res.amountPaid")
                .greaterThan(
                    sub(
                        subquery(
                            subSelect(avg("r.amountPaid")),
                            subFrom("Reservation", "r")
                        )
                    )
                )
            )
        );

        testQuery(query_201(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_202() {
        test_Query_202(getGrammar());
    }

    final void test_Query_202(JPQLGrammar jpqlGrammar) {

        // SELECT cr
        // FROM Cruise cr
        // WHERE 100000 < (
        //    SELECT SUM(res.amountPaid) FROM cr.reservations res
        // )

        ExpressionTester selectStatement = selectStatement(
            select(variable("cr")),
            from("Cruise", "cr"),
            where(
                    numeric(100000)
                .lowerThan(
                    sub(
                        subquery(
                            subSelect(sum("res.amountPaid")),
                            subFrom(fromCollection("cr.reservations", "res"))
                        )
                    )
                )
            )
        );

        testQuery(query_202(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_203() {
        test_Query_203(getGrammar());
    }

    final void test_Query_203(JPQLGrammar jpqlGrammar) {

        // SELECT cr
        // FROM Cruise cr
        // WHERE 0 < ALL (
        //   SELECT res.amountPaid from cr.reservations res
        // )

        ExpressionTester selectStatement = selectStatement(
            select(variable("cr")),
            from("Cruise", "cr"),
            where(
                    numeric(0)
                .lowerThan(
                    all(
                        subquery(
                            subSelect(path("res.amountPaid")),
                            subFrom(fromCollection("cr.reservations", "res"))
                        )
                    )
                )
            )
        );

        testQuery(query_203(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_204() {
        test_Query_204(getGrammar());
    }

    final void test_Query_204(JPQLGrammar jpqlGrammar) {

        // UPDATE Reservation res
        // SET res.name = 'Pascal'
        // WHERE EXISTS (
        //    SELECT c
        //    FROM res.customers c
        //    WHERE c.firstName = 'Bill' AND c.lastName='Burke'
        // )

        ExpressionTester updateStatement = updateStatement(
            update("Reservation", "res", set(path("res.name"), string("'Pascal'"))),
            where(
                exists(
                    subquery(
                        subSelect(variable("c")),
                        subFrom(fromCollection("res.customers", "c")),
                        where(
                                path("c.firstName").equal(string("'Bill'"))
                            .and(
                                path("c.lastName").equal(string("'Burke'"))
                            )
                        )
                    )
                )
            )
        );

        testQuery(query_204(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_205() {
        test_Query_205(getGrammar());
    }

    final void test_Query_205(JPQLGrammar jpqlGrammar) {

        // SELECT o.quantity, a.zipcode
        // FROM Customer c JOIN c.orders o JOIN c.address a
        // WHERE a.state = 'CA'
        // ORDER BY o.quantity, a.zipcode

        ExpressionTester selectStatement = selectStatement(
            select(path("o.quantity"), path("a.zipcode")),
            from(
                "Customer", "c",
                join("c.orders", "o"),
                join("c.address", "a")
            ),
            where(path("a.state").equal(string("'CA'"))),
            orderBy(
                orderByItem("o.quantity"),
                orderByItem("a.zipcode")
            )
        );

        testQuery(query_205(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_206() {
        test_Query_206(getGrammar());
    }

    final void test_Query_206(JPQLGrammar jpqlGrammar) {

        // DELETE
        // FROM Customer c
        // WHERE c.status = 'inactive'

        ExpressionTester deleteStatement = deleteStatement(
            "Customer", "c",
            where(path("c.status").equal(string("'inactive'")))
        );

        testQuery(query_206(), deleteStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_207() {
        test_Query_207(getGrammar());
    }

    final void test_Query_207(JPQLGrammar jpqlGrammar) {

        // DELETE
        // FROM Customer c
        // WHERE c.status = 'inactive'
        //       AND
        //       c.orders IS EMPTY

        ExpressionTester deleteStatement = deleteStatement(
            "Customer", "c",
            where(
                    path("c.status").equal(string("'inactive'"))
                .and(
                    isEmpty("c.orders"))
            )
        );

        testQuery(query_207(), deleteStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_208() {
        test_Query_208(getGrammar());
    }

    final void test_Query_208(JPQLGrammar jpqlGrammar) {

        // UPDATE customer c
        // SET c.status = 'outstanding'
        // WHERE c.balance < 10000

        ExpressionTester updateStatement = updateStatement(
            update("customer", "c", set("c.status", string("'outstanding'"))),
            where(path("c.balance").lowerThan(numeric(10000)))
        );

        testQuery(query_208(), updateStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_209() {
        test_Query_209(getGrammar());
    }

    final void test_Query_209(JPQLGrammar jpqlGrammar) {

        // Select e
        // from Employee e join e.phoneNumbers p
        // where    e.firstName = 'Bob'
        //      and e.lastName like 'Smith%'
        //      and e.address.city = 'Toronto'
        //      and p.areaCode <> '2'

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join("e.phoneNumbers", "p")),
            where(
                    path("e.firstName").equal(string("'Bob'"))
                .and(
                    path("e.lastName").like(string("'Smith%'"))
                )
                .and(
                    path("e.address.city").equal(string("'Toronto'"))
                )
                .and(
                    path("p.areaCode").different(string("'2'"))
                )
            )
        );

        testQuery(query_209(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_210() {
        test_Query_210(getGrammar());
    }

    final void test_Query_210(JPQLGrammar jpqlGrammar) {

        // Select e
        // From Employee e
        // Where Exists(Select a From e.address a Where a.zipCode = 27519)

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists(
                subquery(
                    subSelect(variable("a")),
                    subFrom(identificationVariableDeclaration(rangeVariableDeclaration(
                        collectionPath("e.address"),
                        variable("a")
                    ))),
                    where(path("a.zipCode").equal(numeric(27519)))
                )
            ))
        );

        testQuery(query_210(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_211() {
        test_Query_211(getGrammar());
    }

    final void test_Query_211(JPQLGrammar jpqlGrammar) {

        // Select e
        // From Employee e
        // Where Exists(Where Exists(Select e.name From In e.phoneNumbers Where e.zipCode = 27519))

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists(
                subquery(
                    subSelect(path("e.name")),
                    subFrom(subFromIn("e.phoneNumbers")),
                    where(path("e.zipCode").equal(numeric(27519)))
                )
            ))
        );

        testQuery(query_211(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_212() {
        test_Query_212(getGrammar());
    }

    final void test_Query_212(JPQLGrammar jpqlGrammar) {

        // UPDATE Employee e SET e.salary = e.salary*(1+(:percent/100))
        // WHERE EXISTS (SELECT p
        //               FROM e.projects p
        //               WHERE p.name LIKE :projectName)

        ExpressionTester updateStatement = updateStatement(
            update(
                "Employee", "e",
                set(
                    "e.salary",
                        path("e.salary")
                    .multiply(
                        sub(
                                numeric(1)
                            .add(
                                sub(inputParameter(":percent").divide(numeric(100))
                                )
                            )
                        )
                    )
                )
            ),
            where(exists(
                subquery(
                    subSelect(variable("p")),
                    subFrom(
                        identificationVariableDeclaration(
                            rangeVariableDeclaration(collectionPath("e.projects"), variable("p"))
                        )
                    ),
                    where(path("p.name").like(inputParameter(":projectName")))
                )
            ))
        );

        testQuery(query_212(), updateStatement, jpqlGrammar, buildQueryFormatter_1());
    }

    @Test
    public final void test_Query_213() {
        test_Query_213(getGrammar());
    }

    final void test_Query_213(JPQLGrammar jpqlGrammar) {

        // select e_0
      // from Sellexpect e_0
      // where e_0.iSellexpectnr IN (select e_1.iSellexpectnr
      //                             from Sellexpectline e_1
      //                             where e_1.iStandversionnr IN (select e_2.iStandversionnr
      //                                                           from Standversion e_2
      //                                                           where e_2.iStandnr IN (select e_3.iStandnr
      //                                                                                  from Stand e_3
      //                                                                                  where lower(e_3.iStandid) like :e_3_iStandid)
      //                                                                                 )
        //                                                          )
      //                            )

        ExpressionTester selectStatement = selectStatement(
            select(variable("e_0")),
            from("Sellexpect", "e_0"),
            where(
                    path("e_0.iSellexpectnr")
                .in(
                    subquery(
                        subSelect(path("e_1.iSellexpectnr")),
                        subFrom("Sellexpectline", "e_1"),
                        where(
                                path("e_1.iStandversionnr")
                            .in(
                                subquery(
                                    subSelect(path("e_2.iStandversionnr")),
                                    subFrom("Standversion", "e_2"),
                                    where(
                                            path("e_2.iStandnr")
                                        .in(
                                            subquery(
                                                subSelect(path("e_3.iStandnr")),
                                                subFrom("Stand", "e_3"),
                                                where(
                                                        lower(path("e_3.iStandid"))
                                                    .like(
                                                        inputParameter(":e_3_iStandid")
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );

        testQuery(query_213(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_214() {
        test_Query_214(getGrammar());
    }

    final void test_Query_214(JPQLGrammar jpqlGrammar) {

        // SELECT r
        // FROM RuleCondition r
        // WHERE     r.ruleType = :ruleType
        //       AND r.operator = :operator
        //       AND (SELECT Count(rcc) FROM r.components rcc ) = :componentCount
        //       AND (SELECT Count(rc2) FROM r.components rc2 WHERE rc2.componentId IN :componentIds) = :componentCount

        ExpressionTester selectStatement = selectStatement(
            select(variable("r")),
            from("RuleCondition", "r"),
            where(
                    path("r.ruleType").equal(inputParameter(":ruleType"))
                .and(
                    path("r.operator").equal(inputParameter(":operator"))
                )
                .and(
                    sub(subquery(
                        subSelect(count(variable("rcc"))),
                        subFrom(fromCollection("r.components", "rcc"))
                    ))
                    .equal(
                        inputParameter(":componentCount")
                    )
                )
                .and(
                    sub(subquery(
                        subSelect(count(variable("rc2"))),
                        subFrom(fromCollection("r.components", "rc2")),
                        where(
                            path("rc2.componentId").in(":componentIds")
                        )
                    ))
                    .equal(
                        inputParameter(":componentCount")
                    )
                )
            )
        );

        testQuery(query_214(), selectStatement, jpqlGrammar);
    }

    @Test
    public final void test_Query_215() {
        test_Query_215(getGrammar());
    }

    final void test_Query_215(JPQLGrammar jpqlGrammar) {

        String jpqlQuery = "SELECT NEW pkg.TableMetadata(" +
                           "            table_oir," +
                           "            (SELECT pi.value FROM table_oir.propertyInstances pi WHERE pi.property.name = 'x'), " +
                           "            description_pi.value" +
                           "        ) " +
                           "FROM ObjectInstanceRevision table_oir," +
                           "     IN (table_oir.propertyInstances) description_pi " +
                           "WHERE table_oir.objectInstance.objectClass.objectClassId = 'y'";

        boolean valid = jpqlGrammar.getJPAVersion() == JPAVersion.VERSION_2_1;

        if (jpqlGrammar.getProvider() == DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME) {
            EclipseLinkVersion version = EclipseLinkVersion.value(jpqlGrammar.getProviderVersion());
            valid |= version.isNewerThanOrEqual(EclipseLinkJPQLGrammar2_4.VERSION);
        }

        ExpressionTester subquery = subquery(
            subSelect(path("pi.value")),
            subFrom(
                identificationVariableDeclaration(
                    rangeVariableDeclaration(
                        collectionPath("table_oir.propertyInstances"),
                        variable("pi")
                    )
                )
            ),
            where(
                path("pi.property.name").equal(string("'x'"))
            )
        );

        ExpressionTester selectStatement = selectStatement(
            select(
                new_(
                    "pkg.TableMetadata",
                    variable("table_oir"),
                    sub(valid ? subquery : bad(subquery)),
                    path("description_pi.value")
                )
            ),
            from(
                identificationVariableDeclaration("ObjectInstanceRevision", "table_oir"),
                fromIn("table_oir.propertyInstances", "description_pi")
            ),
            where(
                path("table_oir.objectInstance.objectClass.objectClassId").equal(string("'y'"))
            )
        );

        if (valid) {
            testQuery(jpqlQuery, selectStatement, jpqlGrammar);
        }
        else {
            testInvalidQuery(jpqlQuery, selectStatement, jpqlGrammar);
        }
    }

    @Test
    public final void test_Query_216() {
        test_Query_216(getGrammar());
    }

    final void test_Query_216(JPQLGrammar jpqlGrammar) {

        String jpqlQuery = "UPDATE Employee SET avg = 'JPQL'";

        UpdateStatementTester updateStatement = updateStatement(
            update("Employee", "this", set("{this}.avg", string("'JPQL'")), false)
        );

        testQuery(jpqlQuery, updateStatement, jpqlGrammar, buildStringFormatter_1());
    }

    @Test
    public final void test_Query_217() {
        test_Query_217(getGrammar());
    }

    final void test_Query_217(JPQLGrammar jpqlGrammar) {

        String jpqlQuery = "UPDATE Employee SET current_timestamp = 'JPQL'";

        UpdateStatementTester updateStatement = updateStatement(
            update("Employee", "this", set("{this}.current_timestamp", string("'JPQL'")), false)
        );

        testQuery(jpqlQuery, updateStatement, jpqlGrammar, buildStringFormatter_2());
    }

    @Test
    public final void test_Query_218() {
        test_Query_218(getGrammar());
    }

    final void test_Query_218(JPQLGrammar jpqlGrammar) {

        String jpqlQuery = "UPDATE Employee SET end = 'JPQL'";

        UpdateStatementTester updateStatement = updateStatement(
            update("Employee", "this", set("{this}.end", string("'JPQL'")), false)
        );

        testQuery(jpqlQuery, updateStatement, jpqlGrammar, buildStringFormatter_3());
    }
}
