/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class BadExpressionTest extends JPQLParserTest {

//    private void buildQuery(CharSequence query, String[] result) {
//        JPQLExpression jpqlExpression = new JPQLExpression(query, DefaultJPQLGrammar.instance());
//        result[0] = jpqlExpression.toParsedText();
//    }

//    private JPQLQueryStringFormatter buildQueryStringFormatter_10() {
//        return new JPQLQueryStringFormatter() {
//            public String format(String query) {
//                return query.replace(">a", "> a");
//            }
//        };
//    }

//    private JPQLQueryStringFormatter buildStringQueryFormatter_09() {
//        return new JPQLQueryStringFormatter() {
//            public String format(String query) {
//                return query.replace("AND ", "AND       ").replace("IN(", "IN (");
//            }
//        };
//    }

//    private Collection<String> queries() throws Exception {
//        Method[] methods = JPQLQueries1_0.class.getDeclaredMethods();
//        Collection<String> queries = new ArrayList<String>(methods.length / 2);
//
//        for (Method method : methods) {
//            if (!method.getName().startsWith("query_")) {
//                continue;
//            }
//
//            String query = (String) method.invoke(null, new Object[0]);
//            queries.add(query);
//        }
//
//        return queries;
//    }

    @Test
    public void testBadExpression_01() {

        String query = "SELECT e, FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(collection(variable("e"), nullExpression())),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void testBadExpression_02() {

        String query = "SELECT FROM Employee e";

        SelectClauseTester selectClause = select(nullExpression());
        selectClause.hasSpaceAfterSelect = true;

        ExpressionTester selectStatement = selectStatement(
            selectClause,
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void testBadExpression_03() {

        String query = "SELECT DISTINCT FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(nullExpression()),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void testBadExpression_04() {

        String query = "SELECT e e FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(spacedCollection(variable("e"), variable("e"))),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void testBadExpression_05() {

//        String query = "SELECT IS FROM Employee e";
    }

    @Test
    public void testBadExpression_06() {

//        String query = "SELECT e " +
//                       "FROM Employee e " +
//                       "WHERE e.department.name = 'NA42' AND " +
//                       "      e.address.state IN ('NY' 'CA')";
    }

    @Test
    public void testBadExpression_07() {

//        String query = "SELECT e " +
//                       "FROM Employee e " +
//                       "WHERE e.department.name = 'NA42' AND " +
//                       "      e.address.state I ('NY', 'CA')";
    }

    @Test
    public void testBadExpression_08() {

//        String query = "SELECT e " +
//                       "FROM Employee e " +
//                       "WHERE e.department.name = 'NA42' AN " +
//                       "      e.address.state IN ('NY', 'CA')";
    }

    @Test
    public void testBadExpression_09() {

//        String query = "SELECT e " +
//                       "FROM Employee e " +
//                       "WHERE e.department.name = 'NA42 AND " +
//                       "      e.address.state IN ('NY', 'CA')";
    }

    @Test
    public void testBadExpression_10() {

//        String query = "SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary) " +
//                       "FROM Department d JOIN d.employees e " +
//                       "GROUP BY d " +
//                       "HAVING COUNT(e) >a = 5";
    }

    @Test
    public void testBadExpression_11() {

//        String query = "SELECT a.city " +
//                       "FROM Address a " +
//                       "WHERE " +
//                       "   a.city = 'Cary'" +
//                       "     AND" +
//                       "   e.name = 'Pascal' ANY(a,)";
    }

    private void testQueries(QueryModifier queryModifier) throws Exception {

//        Collection<String> queries = queries();
//        int queryIndex = 0;
//
//        for (String query : queries) {
//            for (int index = query.length(); --index >= 0;) {
//                StringBuilder sb = new StringBuilder(query);
//                queryModifier.modify(sb, index);
//
////                String expectedQuery = JPQLQueryBuilder.toParsedText(sb.toString());
////                String actualQuery = testQuery(expectedQuery);
//
//                // The query was built, check the generated string
////                if (actualQuery != null) {
//                    // This is valid, tweak the expected query
////                    expectedQuery = expectedQuery.replace("','", "', '");
////                    expectedQuery = expectedQuery.replace("IN(", "IN (");
////                    expectedQuery = expectedQuery.replace("EXISTS(", "EXISTS (");
////                    expectedQuery = expectedQuery.replace("'AND", "' AND");
////
////                    // Perform the check
////                    assertEquals("Query (" + queryIndex + ", " + index + ")", expectedQuery, actualQuery);
//                }
////                else {
////                    fail("Timeout: [" + queryIndex + ", " + index + "] = " + expectedQuery);
////                }
////            }
//
//            queryIndex++;
//        }
    }

//    @SuppressWarnings("deprecation")
//    private String testQuery(final CharSequence query) throws Exception {
//        final String[] result = new String[1];
//        final Boolean[] terminated = { false };
//
//        // Start a thread that will parse the query
//        Thread thread = new Thread(query.toString()) {
//            @Override
//            public void run() {
//                buildQuery(query, result);
//                terminated[0] = true;
//            }
//        };
//        thread.start();
//
//        int count = 0;
//
//        // Sleep for a maximum of 10 seconds or until the test completed
//        while (count < 100 && result[0] == null) {
//            Thread.sleep(10);
//            count++;
//        }
//
//        if (!terminated[0]) {
//            thread.stop();
//        }
//
//        return result[0];
//    }

//    @Test // No Timeout: done internally
    public void testRemoveCharacter() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                // Skip espaced character otherwise the string becomes invalid
                if ((query.charAt(position) == '\\') ||
                    (position > 0) && (query.charAt(position - 1) == '\\')) {
                    return;
                }

                query.delete(position, position + 1);
            }
        });
    }

//    @Test // No Timeout: done internally
    public void testZAddCharacter() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                query.insert(position, 'a');
            }
        });
    }

//    @Test // No Timeout: done internally
    public void testZAddWhitespace() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                // Skip espaced character otherwise the string becomes invalid
                if ((query.charAt(position) == '\\') ||
                    (position > 0) && (query.charAt(position - 1) == '\\')) {
                    return;
                }

                query.insert(position, ' ');
            }
        });
    }

//    @Test // No Timeout: done internally
    public void testZChangeCharacterToAnything() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                // Skip espaced character otherwise the string becomes invalid
                if ((query.charAt(position) == '\\') ||
                    (position > 0) && (query.charAt(position - 1) == '\\')) {
                    return;
                }

                query.replace(position, position + 1, "a");
            }
        });
    }

//    @Test // No Timeout: done internally
    public void testZChangeCharacterToCloseParenthesis() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                // Skip espaced character otherwise the string becomes invalid
                if ((query.charAt(position) == '\\') ||
                    (position > 0) && (query.charAt(position - 1) == '\\')) {
                    return;
                }

                query.replace(position, position + 1, ")");
            }
        });
    }

//    @Test // No Timeout: done internally
    public void testZChangeCharacterToOpenParenthesis() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                // Skip espaced character otherwise the string becomes invalid
                if ((query.charAt(position) == '\\') ||
                    (position > 0) && (query.charAt(position - 1) == '\\')) {
                    return;
                }

                query.replace(position, position + 1, "(");
            }
        });
    }

//    @Test // No Timeout: done internally
    public void testZChangeCharacterToSingleQuote() throws Exception {
        testQueries(new QueryModifier() {
            public void modify(StringBuilder query, int position) {
                // Skip espaced character otherwise the string becomes invalid
                if ((query.charAt(position) == '\\') ||
                    (position > 0) && (query.charAt(position - 1) == '\\')) {
                    return;
                }

                query.replace(position, position + 1, "'");
            }
        });
    }

    private interface QueryModifier {
        void modify(StringBuilder query, int position);
    }
}
