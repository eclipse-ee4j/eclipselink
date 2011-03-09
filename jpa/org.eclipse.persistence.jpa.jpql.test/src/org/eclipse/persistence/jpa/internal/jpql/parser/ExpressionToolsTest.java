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
package org.eclipse.persistence.jpa.internal.jpql.parser;

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

		String actualQuery = "  SELECT  AVG ( mag ) , " + // 23, 16
		                     "          NEW oracle.toplink.test.MyEntity(e)  " + // 70, 59
		                     "FROM  Employee e "; // 87, 68

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		// 23 = SELECT AVG(mag)|, ...
		int position = 23;
		int expectedPosition = 16;
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);

		// 78 = ... FROM EM|PLOYEE
		position = 78;
		expectedPosition = 59;
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
		int expectedPosition = "SELECT".length();
		int newPosition = ExpressionTools.repositionCursor(actualQuery, position, jpqlExpression.toParsedText());
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_08() {

		String actualQuery = "select       o, AVG(o.addressId)   from Address o";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = "select     ".length();
		int expectedPosition = "SELECT".length();
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
}