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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.DefaultJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBuilder;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit-tests for {@link ExpressionTools}.
 *
 * @version 2.5
 * @since 2.3
 */
@SuppressWarnings("nls")
public final class ExpressionToolsTest {

    private int adjustPosition(String query, int position) {
        return JPQLQueryBuilder.toParsedText(
            query.substring(0, position),
            DefaultJPQLGrammar.instance()
        ).length();
    }

    private JPQLQueryStringFormatter buildQueryFormatter_1() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace("+", " + ");
            }
        };
    }

    private JPQLExpression parse(String jpqlQuery) {
        return JPQLQueryBuilder.buildQuery(
            jpqlQuery,
            DefaultJPQLGrammar.instance(),
            true
        );
    }

    private JPQLExpression parse(String jpqlQuery, JPQLQueryStringFormatter formatter) {
        return JPQLQueryBuilder.buildQuery(
            jpqlQuery,
            DefaultJPQLGrammar.instance(),
            formatter,
            true
        );
    }

    @Test
    public void test_escape_01() {

        String jpqlQuery = "SELECT e FROM Employee e";
        String result = ExpressionTools.escape(jpqlQuery, new int[1]);
        assertEquals(jpqlQuery, result);
    }

    @Test
    public void test_escape_02() {

        String jpqlQuery = "SELECT\r\ne FROM Employee \"";
        String expectedQuery = "SELECT\\r\\ne FROM Employee \\\"";
        String result = ExpressionTools.escape(jpqlQuery, new int[1]);
        assertEquals(expectedQuery, result);
    }

    @Test
    public void test_escape_03() {

        String jpqlQuery = "\\\r \u00E9 \0";
        String expectedQuery = "\\\\\\r \u00E9 \\0";
        String result = ExpressionTools.escape(jpqlQuery, new int[1]);
        assertEquals(expectedQuery, result);
    }

    @Test
    public void test_escape_04() {

        int[] position = { 12 };
        String jpqlQuery = "SELECT e FROM Employee e";
        ExpressionTools.escape(jpqlQuery, position);
        assertEquals(12, position[0]);
    }

    @Test
    public void test_escape_05() {

        int[] position = { 12 };
        String jpqlQuery = "SELECT\r\ne FROM Employee \"";
        ExpressionTools.escape(jpqlQuery, position);
        assertEquals(14, position[0]);
    }

    @Test
    public void test_escape_06() {

        int[] position = { 7 };
        String jpqlQuery = "SELECT\r\ne FROM Employee \"";
        ExpressionTools.escape(jpqlQuery, position);
        assertEquals(8, position[0]);
    }

    @Test
    public void test_escape_07() {

        int[] position = { 8 };
        String jpqlQuery = "SELECT\r\ne FROM Employee \"";
        ExpressionTools.escape(jpqlQuery, position);
        assertEquals(10, position[0]);
    }

    @Test
    public void test_escape_08() {

        int[] position = { 6 };
        String jpqlQuery = "\\\r \u00E9 \0";
        ExpressionTools.escape(jpqlQuery, position);
        assertEquals(9, position[0]);
    }

    @Test
    public void test_reposition_01() {

        String jpqlQuery = "  SELECT  AVG ( mag ) , " +
                             "          NEW oracle.toplink.test.MyEntity(e)  " +
                             "FROM  Employee e ";

        JPQLExpression jpqlExpression = parse(jpqlQuery);


        // Test 1.a
        int position1 = "  SELECT  ".length();
        int position2 = "  SELECT  AVG ( mag )".length();
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "SELECT ".length();
        int expectedPosition2 = "SELECT AVG(mag)".length();
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 1.b - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);


        // Test 2.a
        position1 = "  SELECT  AVG ( mag ) ,".length();
        position2 = position1;
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        expectedPosition1 = "SELECT AVG(mag),".length();
        expectedPosition2 = expectedPosition1;
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2.b - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);


        // Test 3.a
        position1 = "  SELECT  AVG ( mag ) , ".length() +
                    "          NEW oracle.toplink.test.MyEntity(".length();
        position2 = "  SELECT  AVG ( mag ) , ".length() +
                    "          NEW oracle.toplink.test.MyEntity(e".length();
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        expectedPosition1 = "SELECT AVG(mag), NEW oracle.toplink.test.MyEntity(".length();
        expectedPosition2 = "SELECT AVG(mag), NEW oracle.toplink.test.MyEntity(e".length();
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 3.b - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_02() {

        String jpqlQuery = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG()";
        JPQLExpression jpqlExpression = parse(jpqlQuery, buildQueryFormatter_1());

        // Test 1
        int position1 = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG(".length();
        int position2 = position1;
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn + AVG(".length();
        int expectedPosition2 = expectedPosition1;
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_03() {

        String jpqlQuery = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG()";
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(
            jpqlQuery,
            DefaultJPQLGrammar.instance(),
            buildQueryFormatter_1(),
            true
        );

        // Test 1
        int position1 = "SELECT AVG(mag) ".length();
        int position2 = position1;
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "SELECT AVG(mag) ".length();
        int expectedPosition2 = expectedPosition1;
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_04() {

        String jpqlQuery = "select   o, AVG(o.addressId)   from Address o";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1.a
        int position1 = "select   ".length();
        int position2 = "select   o".length();
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "SELECT ".length();
        int expectedPosition2 = "SELECT o".length();
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 1.b
        position1 = "select  ".length();
        position2 = position1;
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        expectedPosition1 = "SELECT ".length();
        expectedPosition2 = expectedPosition1;
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 1.c
        position1 = "select ".length();
        position2 = position1;
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        expectedPosition1 = "SELECT ".length();
        expectedPosition2 = expectedPosition1;
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 1.d
        position1 = "select".length();
        position2 = position1;
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        expectedPosition1 = "SELECT".length();
        expectedPosition2 = expectedPosition1;
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2.a - The reverse
        position1 = "select".length();
        position2 = position1;
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPosition1 = "SELECT".length();
        expectedPosition2 = expectedPosition1;
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2.b - The reverse
        position1 = "select ".length();
        position2 = position1;
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPosition1 = "select   ".length();
        expectedPosition2 = expectedPosition1;
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2.c - The reverse
        position1 = "select ".length();
        position2 = "select o".length();
        positions = new int[] { position1, position2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPosition1 = "select   ".length();
        expectedPosition2 = "select   o".length();
        expectedPositions = new int[] { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_05() {

        String jpqlQuery = "select       o, AVG(o.addressId)   from Address o";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1
        int position = "select     ".length();
        int[] positions = { position, position };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition = "SELECT ".length();
        int[] expectedPositions = { expectedPosition, expectedPosition };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        position = "SELECT ".length();
        positions = new int[] { position, position };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPosition = "select       ".length();
        expectedPositions = new int[] { expectedPosition, expectedPosition };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_06() {

        String jpqlQuery = "select   o,    o, AVG(o.addressId)   from Address o";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1
        int position1 = "select   o,    o,".length();
        int position2 = position1;
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "select o, o,".length();
        int expectedPosition2 = expectedPosition1;
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_08() {

        String jpqlQuery = "select o,\r\no,AVG(o.addressId) from Address o";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1
        int position1 = "select o,\r\no,AVG(o.addressId) ".length();
        int position2 = position1;
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "select o, o, AVG(o.addressId) ".length();
        int expectedPosition2 = expectedPosition1;
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_09() {

        String jpqlQuery = "select  \r\n o, AVG(o.addressId)  \r\n from Address o";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1
        int position1 = "select  \r\n o, AVG(o.addressId)  \r\n ".length();
        int position2 = position1;
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "select o, AVG(o.addressId) ".length();
        int expectedPosition2 = expectedPosition1;
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_10() {

        String jpqlQuery = "select e \rFROM  Employee e WHERE e.name = 'JPQL ";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1.a
        int position = "select e \rFROM  ".length();
        int[] positions = { position, position };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition = "select e FROM ".length();
        int[] expectedPositions = { expectedPosition, expectedPosition };
        assertArrayEquals(expectedPositions, positions);

        // Test 1.b
        position = "select e \rFROM ".length();
        positions = new int[] { position, position };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        expectedPosition = "select e FROM ".length();
        expectedPositions = new int[] { expectedPosition, expectedPosition };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        position = "select e FROM ".length();
        positions = new int[] { position, position };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPosition = "select e \rFROM  ".length();
        expectedPositions = new int[] { expectedPosition, expectedPosition };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_reposition_11() {

        String jpqlQuery = "select o,o,AVG(o.addressId) from Address o";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        // Test 1
        int position1 = "select o,o,AVG(o.addressId) ".length();
        int position2 = position1;
        int[] positions = { position1, position2 };

        ExpressionTools.reposition(jpqlQuery, positions, jpqlExpression.toParsedText());

        int expectedPosition1 = "select o, o, AVG(o.addressId) ".length();
        int expectedPosition2 = expectedPosition1;
        int[] expectedPositions = { expectedPosition1, expectedPosition2 };
        assertArrayEquals(expectedPositions, positions);

        // Test 2 - The reverse
        positions = new int[] { expectedPosition1, expectedPosition2 };

        ExpressionTools.reposition(jpqlExpression.toParsedText(), positions, jpqlQuery);

        expectedPositions = new int[] { position1, position2 };
        assertArrayEquals(expectedPositions, positions);
    }

    @Test
    public void test_repositionCursor_01() {

        String jpqlQuery = "  SELECT  AVG ( mag )    FROM  Magazine   mag";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        int position = 0;
        int expectedPosition = adjustPosition(jpqlQuery, position);
        int newPosition = ExpressionTools.repositionCursor(jpqlQuery, position, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, newPosition);

        position = 2;
        expectedPosition = 0;
        newPosition = ExpressionTools.repositionCursor(jpqlQuery, position, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, newPosition);

        position = 4;
        expectedPosition = adjustPosition(jpqlQuery, position);
        newPosition = ExpressionTools.repositionCursor(jpqlQuery, position, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, newPosition);

        position = 10;
        expectedPosition = adjustPosition(jpqlQuery, position);
        newPosition = ExpressionTools.repositionCursor(jpqlQuery, position, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, newPosition);

        position = 31;
        expectedPosition = adjustPosition(jpqlQuery, position);
        newPosition = ExpressionTools.repositionCursor(jpqlQuery, position, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, newPosition);
    }

    @Test
    public void test_repositionCursor_02() {

        String jpqlQuery = "SELECT  e ";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        int extraWhitespacesCount = 1;

        // Test 1
        int expectedPosition = 0;
        int actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, actualPosition);

        // Test 2
        expectedPosition = jpqlQuery.indexOf("e");
        actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

        // Test 3
        expectedPosition = jpqlQuery.length();
        actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
    }

    @Test
    public void test_repositionCursor_03() {

        String jpqlQuery = " SELECT  e   FROM";
        JPQLExpression jpqlExpression = parse(jpqlQuery);

        int extraWhitespacesCount = 1;

        // Test 1
        int expectedPosition = 0;
        int actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition, actualPosition);

        extraWhitespacesCount = 2;

        // Test 2
        expectedPosition = jpqlQuery.indexOf("e");
        actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

        extraWhitespacesCount = 4;

        // Test 3
        expectedPosition = jpqlQuery.indexOf("FROM");
        actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

        // Test 4
        expectedPosition = jpqlQuery.length();
        actualPosition   = ExpressionTools.repositionCursor(jpqlQuery, expectedPosition, jpqlExpression.toParsedText());
        assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
    }

    @Test
    public void test_repositionJava_01() {

        String jpqlQuery = "SELECT e FROM Employee e";

        int[] positions = { 0, 0 };
        ExpressionTools.repositionJava(jpqlQuery, positions);

        assertEquals(0, positions[0]);
        assertEquals(0, positions[1]);
    }

    @Test
    public void test_repositionJava_02() {

        String jpqlQuery = "SELECT e FROM Employee e";
        int position = jpqlQuery.length();

        int[] positions = { position, position };
        ExpressionTools.repositionJava(jpqlQuery, positions);

        assertEquals(position, positions[0]);
        assertEquals(position, positions[1]);
    }

    @Test
    public void test_repositionJava_03() {

        String jpqlQuery = "SELECT e FROM\r\nEmployee e WHERE e.name <> \"JPQL\"";
        int position = "SELECT e FROM\r\nEmployee e WHERE e.".length();

        int[] positions = { position, position };
        ExpressionTools.repositionJava(jpqlQuery, positions);

        int expectedPosition = "SELECT e FROM\\r\\nEmployee e WHERE e.".length();
        assertEquals(expectedPosition, positions[0]);
        assertEquals(expectedPosition, positions[1]);
    }

    @Test
    public void test_repositionJava_04() {

        String jpqlQuery = "SELECT e FROM\r\nEmployee e WHERE e.name <> \"JPQL\"";
        int position = "SELECT e FROM\r\nEmployee e WHERE e.name <".length();

        int[] positions = { position, position };
        ExpressionTools.repositionJava(jpqlQuery, positions);

        int expectedPosition = "SELECT e FROM\\r\\nEmployee e WHERE e.name <".length();
        assertEquals(expectedPosition, positions[0]);
        assertEquals(expectedPosition, positions[1]);
    }

    @Test
    public void test_repositionJava_05() {

        String jpqlQuery = "SELECT e FROM\r\nEmployee e WHERE e.name <> \"JPQL\"";
        int position1 = "SELECT e FROM\r\nEmployee e WHERE ".length();
        int position2 = "SELECT e FROM\r\nEmployee e WHERE e.name <> \"JPQL\"".length();

        int[] positions = { position1, position2 };
        ExpressionTools.repositionJava(jpqlQuery, positions);

        int expectedPosition1 = "SELECT e FROM\\r\\nEmployee e WHERE ".length();
        int expectedPosition2 = "SELECT e FROM\\r\\nEmployee e WHERE e.name <> \\\"JPQL\\\"".length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_repositionJava_06() {

        String jpqlQuery = "SELECT e FROM\r\nEmployee e WHERE e.name <> \"JPQL\"";
        int position1 = 0;
        int position2 = "SELECT e FROM\r\nEmployee e WHERE e.name <> \"JPQL\"".length();

        int[] positions = { position1, position2 };
        ExpressionTools.repositionJava(jpqlQuery, positions);

        int expectedPosition1 = 0;
        int expectedPosition2 = "SELECT e FROM\\r\\nEmployee e WHERE e.name <> \\\"JPQL\\\"".length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_unescape_01() {

        String jpqlQuery = "SELECT e FROM Employee e";
        String result = ExpressionTools.unescape(jpqlQuery, new int[1]);
        assertEquals(jpqlQuery, result);
    }

    @Test
    public void test_unescape_02() {

        String jpqlQuery = "SELECT\\r\\ne FROM Employee \\\"";
        String expectedQuery = "SELECT\r\ne FROM Employee \"";
        String result = ExpressionTools.unescape(jpqlQuery, new int[1]);
        assertEquals(expectedQuery, result);
    }

    @Test
    public void test_unescape_03() {

        String jpqlQuery = "\\r \\u00E9 \\0";
        String expectedQuery = "\r \u00E9 \0";
        String result = ExpressionTools.unescape(jpqlQuery, new int[1]);
        assertEquals(expectedQuery, result);
    }

    @Test
    public void test_unescape_04() {

        int[] position = { 0 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(0, position[0]);
    }

    @Test
    public void test_unescape_05() {

        int[] position = { 1 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(0, position[0]);
    }

    @Test
    public void test_unescape_06() {

        int[] position = { 3 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_07() {

        int[] position = { 4 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_08() {

        int[] position = { 5 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_09() {

        int[] position = { 6 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_10() {

        int[] position = { 7 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_11() {

        int[] position = { 8 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_12() {

        int[] position = { 9 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(2, position[0]);
    }

    @Test
    public void test_unescape_13() {

        int[] position = { 10 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(4, position[0]);
    }

    @Test
    public void test_unescape_14() {

        int[] position = { 11 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(4, position[0]);
    }

    @Test
    public void test_unescape_15() {

        int[] position = { 12 };
        String jpqlQuery = "\\r \\u00E9 \\0";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(5, position[0]);
    }

    @Test
    public void test_unescape_16() {

        int[] position = { 12 };
        String jpqlQuery = "SELECT e\\r\\n";
        ExpressionTools.unescape(jpqlQuery, position);

        assertEquals(10, position[0]);
    }

    @Test
    public void test_unquote_01() {

        String text = null;
        String result = ExpressionTools.unquote(text);
        assertNull(result);
    }

    @Test
    public void test_unquote_02() {

        String text = ExpressionTools.EMPTY_STRING;
        String result = ExpressionTools.unquote(text);
        assertEquals(text, result);
    }

    @Test
    public void test_unquote_03() {

        String text = " ";
        String result = ExpressionTools.unquote(text);
        assertEquals(text, result);
    }

    @Test
    public void test_unquote_04() {

        String text = " a ";
        String result = ExpressionTools.unquote(text);
        assertEquals(text, result);
    }

    @Test
    public void test_unquote_05() {

        String text = "' JPQL";
        String result = ExpressionTools.unquote(text);
        assertEquals(" JPQL", result);
    }

    @Test
    public void test_unquote_06() {

        String text = "'Pascal'";
        String result = ExpressionTools.unquote(text);
        assertEquals("Pascal", result);
    }

    @Test
    public void test_unquote_07() {

        String text = "'JPQL";
        String result = ExpressionTools.unquote(text);
        assertEquals("JPQL", result);
    }

    @Test
    public void test_unquote_08() {

        String text = "'";
        String result = ExpressionTools.unquote(text);
        assertEquals(ExpressionTools.EMPTY_STRING, result);
    }

    @Test
    public void test_unquote_09() {

        String text = "''";
        String result = ExpressionTools.unquote(text);
        assertEquals(ExpressionTools.EMPTY_STRING, result);
    }

    @Test
    public void test_unquote_10() {

        String text = "''''";
        String result = ExpressionTools.unquote(text);
        assertEquals("'", result);
    }

    @Test
    public void test_unquote_11() {

        String text = "'JPQL''s version'";
        String result = ExpressionTools.unquote(text);
        assertEquals("JPQL's version", result);
    }

    @Test
    public void test_unquote_12() {

        String text = "'''s version'";
        String result = ExpressionTools.unquote(text);
        assertEquals("'s version", result);
    }

    @Test
    public void test_unquote_13() {

        String text = "'''s version''";
        String result = ExpressionTools.unquote(text);
        assertEquals("'s version'", result);
    }
}
