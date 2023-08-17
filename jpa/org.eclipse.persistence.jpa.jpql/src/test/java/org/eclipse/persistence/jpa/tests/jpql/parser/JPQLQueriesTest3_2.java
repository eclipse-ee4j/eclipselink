/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_0.query_013;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Select02;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Select_Chained;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ConcatPipes_Where;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Select01;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Select02;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Select03;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_2.query_ReplaceFunction_Where;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.concatPipes;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.from;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.numeric;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.path;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.replace;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.resultVariable;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.select;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.selectStatement;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.string;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.variable;
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

//    @Test
    public void test_Query_013() {

        // SELECT e.salary / 1000D n
        // From Employee e

        ExpressionTester selectStatement = selectStatement(
                select(resultVariable(path("e.salary").divide(numeric("1000D")), "n")),
                from("Employee", "e")
        );

        testQuery(query_013(), selectStatement);
    }

}
