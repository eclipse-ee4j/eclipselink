/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class ExpressionToolsTest {

	private int adjustPosition(String query, int position) {
		return JPQLQueryBuilder.formatQuery(query.substring(0, position)).length();
	}

	private JPQLQueryStringFormatter buildQueryFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("+", " + ");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryFormatter_2() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",", ", ");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryFormatter_3() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",A", ", A");
			}
		};
	}

	@Test
	public void testEscape_01() {

		String actualQuery = "SELECT e FROM Employee e";
		String result = ExpressionTools.escape(actualQuery, new int[1]);
		assertEquals(actualQuery, result);
	}

	@Test
	public void testEscape_02() {

		String actualQuery = "SELECT\r\ne FROM Employee \"";
		String expectedQuery = "SELECT\\r\\ne FROM Employee \\\"";
		String result = ExpressionTools.escape(actualQuery, new int[1]);
		assertEquals(expectedQuery, result);
	}

	@Test
	public void testEscape_03() {

		String actualQuery = "\\\r \u00E9 \0";
		String expectedQuery = "\\\\\\r \u00E9 \\0";
		String result = ExpressionTools.escape(actualQuery, new int[1]);
		assertEquals(expectedQuery, result);
	}

	@Test
	public void testEscapeWithPosition_01() {

		int[] position = { 12 };
		String actualQuery = "SELECT e FROM Employee e";
		ExpressionTools.escape(actualQuery, position);
		assertEquals(12, position[0]);
	}

	@Test
	public void testEscapeWithPosition_02() {

		int[] position = { 12 };
		String actualQuery = "SELECT\r\ne FROM Employee \"";
		ExpressionTools.escape(actualQuery, position);
		assertEquals(14, position[0]);
	}

	@Test
	public void testEscapeWithPosition_03() {

		int[] position = { 7 };
		String actualQuery = "SELECT\r\ne FROM Employee \"";
		ExpressionTools.escape(actualQuery, position);
		assertEquals(8, position[0]);
	}

	@Test
	public void testEscapeWithPosition_04() {

		int[] position = { 8 };
		String actualQuery = "SELECT\r\ne FROM Employee \"";
		ExpressionTools.escape(actualQuery, position);
		assertEquals(10, position[0]);
	}

	@Test
	public void testEscapeWithPosition_05() {

		int[] position = { 6 };
		String actualQuery = "\\\r \u00E9 \0";
		ExpressionTools.escape(actualQuery, position);
		assertEquals(9, position[0]);
	}

	@Test
	public void testRepositionCursor_01() {

		String actualQuery = "  SELECT  AVG ( mag )    FROM  Magazine   mag";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = 0;
		int expectedPosition = adjustPosition(actualQuery, position);
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);

		position = 2;
		expectedPosition = 0;
		newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);

		position = 4;
		expectedPosition = adjustPosition(actualQuery, position);
		newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);

		position = 10;
		expectedPosition = adjustPosition(actualQuery, position);
		newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);

		position = 31;
		expectedPosition = adjustPosition(actualQuery, position);
		newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_02() {

		String actualQuery = "  SELECT  AVG ( mag ) , " +
		                     "          NEW oracle.toplink.test.MyEntity(e)  " +
		                     "FROM  Employee e ";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		// Test 1
		int position = "  SELECT  AVG ( mag ) ,".length();
		int expectedPosition = "SELECT AVG(mag),".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);

		// Test 2
		position = "  SELECT  AVG ( mag ) , ".length() +
		           "          NEW oracle.toplink.test.MyEntity(e".length();
		expectedPosition = "SELECT AVG(mag), NEW oracle.toplink.test.MyEntity(e".length();
		newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_03() {

		String query = "SELECT  e ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		int extraWhitespacesCount = 1;

		// Test 1
		int expectedPosition = 0;
		int actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, actualPosition);

		// Test 2
		expectedPosition = query.indexOf("e");
		actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		// Test 3
		expectedPosition = query.length();
		actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
	}

	@Test
	public void testRepositionCursor_04() {

		String query = " SELECT  e   FROM";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		int extraWhitespacesCount = 1;

		// Test 1
		int expectedPosition = 0;
		int actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, actualPosition);

		extraWhitespacesCount = 2;

		// Test 2
		expectedPosition = query.indexOf("e");
		actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		extraWhitespacesCount = 4;

		// Test 3
		expectedPosition = query.indexOf("FROM");
		actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		// Test 4
		expectedPosition = query.length();
		actualPosition   = ExpressionTools.repositionCursor(query, expectedPosition, jpqlExpression.toParsedText());
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
	}

	@Test
	public void testRepositionCursor_05() {

		String actualQuery = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, buildQueryFormatter_1());

		int position = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG(".length();
		int expectedPosition = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn + AVG(".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_06() {

		String actualQuery = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, buildQueryFormatter_1());

		int position = "SELECT AVG(mag) ".length();
		int expectedPosition = "SELECT AVG(mag) ".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_07() {

		String actualQuery = "select   o, AVG(o.addressId)   from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = "select ".length();
		int expectedPosition = "SELECT ".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_08() {

		String actualQuery = "select       o, AVG(o.addressId)   from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = "select     ".length();
		int expectedPosition = "SELECT ".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_09() {

		String actualQuery = "select   o,    o, AVG(o.addressId)   from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = "select   o,    o,".length();
		int expectedPosition = "select o, o,".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_10() {

		String actualQuery = "select o,o,AVG(o.addressId) from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, buildQueryFormatter_2());

		int position = "select o,o,AVG(o.addressId) ".length();
		int expectedPosition = "select o, o, AVG(o.addressId) ".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_11() {

		String actualQuery = "select o,\r\no,AVG(o.addressId) from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, buildQueryFormatter_3());

		int position = "select o,\r\no,AVG(o.addressId) ".length();
		int expectedPosition = "select o, o, AVG(o.addressId) ".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_12() {

		String actualQuery = "select  \r\n o, AVG(o.addressId)  \r\n from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = "select  \r\n o, AVG(o.addressId)  \r\n ".length();
		int expectedPosition = "select o, AVG(o.addressId) ".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testUnescape_01() {

		String actualQuery = "SELECT e FROM Employee e";
		String result = ExpressionTools.unescape(actualQuery, new int[1]);
		assertEquals(actualQuery, result);
	}

	@Test
	public void testUnescape_02() {

		String actualQuery = "SELECT\\r\\ne FROM Employee \\\"";
		String expectedQuery = "SELECT\r\ne FROM Employee \"";
		String result = ExpressionTools.unescape(actualQuery, new int[1]);
		assertEquals(expectedQuery, result);
	}

	@Test
	public void testUnescape_03() {

		String actualQuery = "\\r \\u00E9 \\0";
		String expectedQuery = "\r \u00E9 \0";
		String result = ExpressionTools.unescape(actualQuery, new int[1]);
		assertEquals(expectedQuery, result);
	}

	@Test
	public void testUnescapeWithPosition_01() {

		int[] position = { 0 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(0, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_02() {

		int[] position = { 1 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(0, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_03() {

		int[] position = { 3 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_04() {

		int[] position = { 4 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_05() {

		int[] position = { 5 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_06() {

		int[] position = { 6 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_07() {

		int[] position = { 7 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_08() {

		int[] position = { 8 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_09() {

		int[] position = { 9 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(2, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_10() {

		int[] position = { 10 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(4, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_11() {

		int[] position = { 11 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(4, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_12() {

		int[] position = { 12 };
		String actualQuery = "\\r \\u00E9 \\0";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(5, position[0]);
	}

	@Test
	public void testUnescapeWithPosition_13() {

		int[] position = { 12 };
		String actualQuery = "SELECT e\\r\\n";
		ExpressionTools.unescape(actualQuery, position);

		assertEquals(10, position[0]);
	}
}