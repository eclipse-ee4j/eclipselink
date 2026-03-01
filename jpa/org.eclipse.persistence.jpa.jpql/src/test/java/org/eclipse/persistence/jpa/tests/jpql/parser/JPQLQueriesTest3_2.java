/*
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Select02;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Select_Chained;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_IdFunction_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_IdFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_IdFunctionNestedInArithmeticFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_IdFunctionNestedInStringFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_LeftFunction_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_LeftFunction_Select02;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_LeftFunction_Select03;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_LeftFunction_Select04;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_LeftFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Select02;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Select03;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_RightFunction_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_RightFunction_Select02;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_RightFunction_Select03;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_RightFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_VersionFunction_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_VersionFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_VersionFunctionNestedInArithmeticFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_VersionFunctionNestedInStringFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_Union01;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.abs;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.castAs;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.concatPipes;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.count;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.except;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.from;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.groupBy;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.having;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.id;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.intersect;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.left;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.leftJoin;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.lower;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.numeric;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.path;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.replace;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.right;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.select;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.selectStatement;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.string;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.subFrom;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.subSelect;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.union;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.unionAll;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.variable;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.version;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.where;

public class JPQLQueriesTest3_2 extends JPQLParserTest {

    @Test
    public void test_Query_ConcatPipes_Select01() {

        // SELECT c.firstName || 'Smith' FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(concatPipes(path("c.firstName"), string("'Smith'"))),
                from("Customer", "c")
        );
        try {
            testQuery(query_ConcatPipes_Select01(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ConcatPipes_Select02() {

        // SELECT c.firstName || c.lastName FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(concatPipes(path("c.firstName"), path("c.lastName"))),
                from("Customer", "c")
        );
        try {
            testQuery(query_ConcatPipes_Select02(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ConcatPipes_Select_Chained() {

        // SELECT c.firstName || c.lastName FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(concatPipes(concatPipes(path("c.firstName"), string("'Francis'")), string("'Smith'"))),
                from("Customer", "c")
        );
        try {
            testQuery(query_ConcatPipes_Select_Chained(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ConcatPipes_Where() {

        // SELECT c FROM Customer c WHERE c.firstName || 'Smith' = 'JohnSmith'
        ExpressionTester selectStatement = selectStatement(
                select(variable("c")),
                from("Customer", "c"),
                where(concatPipes(path("c.firstName"), string("'Smith'")).equal(string("'JohnSmith'")))
        );
        try {
            testQuery(query_ConcatPipes_Where(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ReplaceFunction_Select01() {

        // SELECT REPLACE('Hello Vorld', 'V', 'W') FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(replace(string("'Hello Vorld'"), string("'V'"), string("'W'"))),
                from("Customer", "c")
        );
        try {
            testQuery(query_ReplaceFunction_Select01(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ReplaceFunction_Select02() {

        // SELECT REPLACE('Hella Warld', 'a', 'o') FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(replace(string("'Hella Warld'"), string("'a'"), string("'o'"))),
                from("Customer", "c")
        );
        try {
            testQuery(query_ReplaceFunction_Select02(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ReplaceFunction_Select03() {

        // SELECT REPLACE(c.firstName, 'a', 'o') FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(replace(path("c.firstName"), string("'a'"), string("'o'"))),
                from("Customer", "c")
        );
        try {
            testQuery(query_ReplaceFunction_Select03(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_ReplaceFunction_Where() {

        // SELECT c FROM Customer c WHERE REPLACE(c.firstName, 'o', 'a') = 'Jahn'
        ExpressionTester selectStatement = selectStatement(
                select(variable("c")),
                from("Customer", "c"),
                where(replace(path("c.firstName"), string("'o'"), string("'a'")).equal(string("'Jahn'")))
        );
        try {
            testQuery(query_ReplaceFunction_Where(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_LeftFunction_Select01() {

        // SELECT LEFT('Hello World', 5) FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(left(string("'Hello World'"), numeric(5))),
                from("Customer", "c")
        );
        try {
            testQuery(query_LeftFunction_Select01(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_LeftFunction_Select02() {

        // SELECT LEFT(LEFT('Hello World', 5), 2) FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(left(left(string("'Hello World'"), numeric(5)), numeric(2))),
                from("Customer", "c")
        );
        try {
            testQuery(query_LeftFunction_Select02(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_LeftFunction_Select03() {

        // SELECT LEFT(c.firstName, 2) FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(left(path("c.firstName"), numeric(2))),
                from("Customer", "c")
        );
        try {
            testQuery(query_LeftFunction_Select03(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_LeftFunction_Select04() {

        // SELECT LEFT(cr.name, 5), COUNT (res)
        // FROM Cruise cr LEFT JOIN cr.reservations res
        // GROUP BY cr.name
        // HAVING count(res) > 10
        ExpressionTester selectStatement = selectStatement(
                select(left(path("cr.name"), numeric(5)), count(variable("res"))),
                from("Cruise", "cr", leftJoin("cr.reservations", "res")),
                groupBy(path("cr.name")),
                having(count(variable("res")).greaterThan(numeric(10)))
        );
        try {
            testQuery(query_LeftFunction_Select04(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }


    @Test
    public void test_Query_LeftFunction_Where() {

        // SELECT c FROM Customer c WHERE LEFT(c.firstName, 4) = 'John'
        ExpressionTester selectStatement = selectStatement(
                select(variable("c")),
                from("Customer", "c"),
                where(left(path("c.firstName"), numeric(4)).equal(string("'John'")))
        );
        try {
            testQuery(query_LeftFunction_Where(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }


    @Test
    public void test_Query_RightFunction_Select01() {

        // SELECT RIGHT('Hello World', 5) FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(right(string("'Hello World'"), numeric(5))),
                from("Customer", "c")
        );
        try {
            testQuery(query_RightFunction_Select01(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_RightFunction_Select02() {

        // SELECT RIGHT(RIGHT('Hello World', 5), 2) FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(right(right(string("'Hello World'"), numeric(5)), numeric(2))),
                from("Customer", "c")
        );
        try {
            testQuery(query_RightFunction_Select02(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_RightFunction_Select03() {

        // SELECT RIGHT(c.firstName, 2) FROM Customer c
        ExpressionTester selectStatement = selectStatement(
                select(right(path("c.firstName"), numeric(2))),
                from("Customer", "c")
        );
        try {
            testQuery(query_RightFunction_Select03(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_RightFunction_Where() {

        // SELECT c FROM Customer c WHERE RIGHT(c.firstName, 4) = 'John'
        ExpressionTester selectStatement = selectStatement(
                select(variable("c")),
                from("Customer", "c"),
                where(right(path("c.firstName"), numeric(4)).equal(string("'John'")))
        );
        try {
            testQuery(query_RightFunction_Where(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_Union01() {

        // Select a from Address a where a.city = 'Ottawa'
        // union Select a2 from Address a2
        // union all Select a2 from Address a2
        // intersect Select a from Address a where a.city = 'Ottawa'
        // except Select a from Address a where a.city = 'Ottawa'

        SelectStatementTester selectStatement = selectStatement(
                select(variable("a")),
                from("Address", "a"),
                where(path("a.city").equal(string("'Ottawa'"))),
                union(
                        subSelect(variable("a2")),
                        subFrom("Address", "a2")
                ),
                unionAll(
                        subSelect(variable("a2")),
                        subFrom("Address", "a2")
                ),
                intersect(
                        subSelect(variable("a")),
                        subFrom("Address", "a"),
                        where(path("a.city").equal(string("'Ottawa'")))
                ),
                except(
                        subSelect(variable("a")),
                        subFrom("Address", "a"),
                        where(path("a.city").equal(string("'Ottawa'")))
                )
        );

        testQuery(query_Union01(), selectStatement);
    }

    @Test
    public final void test_Query_IdFunction_Select01() {
        // select id(c)
        // FROM Customer c

        ExpressionTester selectStatement = selectStatement(
            select(id("c")),
            from("Customer", "c"));

        testQuery(query_IdFunction_Select01(), selectStatement);
    }

    @Test
    public final void test_Query_IdFunction_Where() {
        // SELECT c
        // FROM Customer
        // c WHERE ID(c).id = 1

        ExpressionTester selectStatement = selectStatement(
                select(variable("c")),
                from("Customer", "c"),
                where(id("c").equal(numeric(1))));

        testQuery(query_IdFunction_Where(), selectStatement);
    }

    @Test
    public final void test_Query_IdFunctionNestedInArithmeticFunction_Where() {
        //SELECT this
        // FROM Customer this
        // WHERE ABS(CAST(ID(this) AS INTEGER)) = 1"

        ExpressionTester selectStatement = selectStatement(
                select(variable("this")),
                from("Customer", "this"),
                where(abs(castAs(id("this"), "INTEGER")).equal(numeric(1))));

        testQuery(query_IdFunctionNestedInArithmeticFunction_Where(), selectStatement);
    }

    @Test
    public final void test_Query_IdFunctionNestedInStringFunction_Where() {
        // SELECT this
        // FROM Customer this
        // WHERE LOWER(CAST(ID(this) AS STRING)) = 'a'

        ExpressionTester selectStatement = selectStatement(
                select(variable("this")),
                from("Customer", "this"),
                where(lower(castAs(id("this"), "STRING")).equal(string("'abc'"))));

        testQuery(query_IdFunctionNestedInStringFunction_Where(), selectStatement);
    }

    @Test
    public final void test_Query_VersionFunction_Select01() {
        // select version(c)
        // FROM Customer c

        ExpressionTester selectStatement = selectStatement(
                select(version("c")),
                from("Customer", "c"));

        testQuery(query_VersionFunction_Select01(), selectStatement);
    }

    @Test
    public final void test_Query_VersionFunction_Where() {
        // SELECT c
        // FROM Customer
        // c WHERE version(c).id = 1

        ExpressionTester selectStatement = selectStatement(
                select(variable("c")),
                from("Customer", "c"),
                where(version("c").equal(numeric(1))));

        testQuery(query_VersionFunction_Where(), selectStatement);
    }

    @Test
    public final void test_Query_VersionFunctionNestedInArithmeticFunction_Where() {
        //SELECT this
        // FROM Customer this
        // WHERE ABS(CAST(VERSION(this) AS INTEGER)) = 1"

        ExpressionTester selectStatement = selectStatement(
                select(variable("this")),
                from("Customer", "this"),
                where(abs(castAs(version("this"), "INTEGER")).equal(numeric(1))));

        testQuery(query_VersionFunctionNestedInArithmeticFunction_Where(), selectStatement);
    }

    @Test
    public final void test_Query_VersionFunctionNestedInStringFunction_Where() {
        // SELECT this
        // FROM Customer this
        // WHERE LOWER(CAST(VERSION(this) AS STRING)) = '1'

        ExpressionTester selectStatement = selectStatement(
                select(variable("this")),
                from("Customer", "this"),
                where(lower(castAs(version("this"), "STRING")).equal(string("'1'"))));

        testQuery(query_VersionFunctionNestedInStringFunction_Where(), selectStatement);
    }
}
