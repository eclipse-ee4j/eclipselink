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
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public final class ExpressionToolsTest {

	private int adjustPosition(String query, int position) {
		return JPQLQueryBuilder.formatQuery(query.substring(0, position)).length();
	}

	private JPQLQueryStringFormatter buildQueryFormatter_5() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("+", " + ");
			}
		};
	}

	@Test
	public void testRepositionCursor_1() {

		String actualQuery = "  SELECT  AVG ( mag )    FROM  Magazine   mag";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		int position = 0;
		int expectedPosition = adjustPosition(actualQuery, position);
		int newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 2;
		expectedPosition = 0;
		newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 4;
		expectedPosition = adjustPosition(actualQuery, position);
		newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 10;
		expectedPosition = adjustPosition(actualQuery, position);
		newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 31;
		expectedPosition = adjustPosition(actualQuery, position);
		newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_2() {

		String actualQuery = "  SELECT  AVG ( mag ) , " + // 23, 16
		                     "          NEW oracle.toplink.test.MyEntity(e)  " + // 70, 59
		                     "FROM  Employee e "; // 87, 68

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		// 23 = SELECT AVG(mag)|, ...
		int position = 23;
		int expectedPosition = 16;
		int newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		// 78 = ... FROM EM|PLOYEE
		position = 78;
		expectedPosition = 59;
		newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_3() {

		String query = "SELECT  e ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		int extraWhitespacesCount = 1;

		// Test 1
		int expectedPosition = 0;
		int actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition, actualPosition);

		// Test 2
		expectedPosition = query.indexOf("e");
		actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		// Test 3
		expectedPosition = query.length();
		actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
	}

	@Test
	public void testRepositionCursor_4() {

		String query = " SELECT  e   FROM";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		int extraWhitespacesCount = 1;

		// Test 1
		int expectedPosition = 0;
		int actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition, actualPosition);

		extraWhitespacesCount = 2;

		// Test 2
		expectedPosition = query.indexOf("e");
		actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		extraWhitespacesCount = 4;

		// Test 3
		expectedPosition = query.indexOf("FROM");
		actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		// Test 4
		expectedPosition = query.length();
		actualPosition   = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
	}

	@Test
	public void testRepositionCursor_5() {

		String actualQuery = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, buildQueryFormatter_5());

		int position = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG(".length();
		int expectedPosition = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn + AVG(".length();
		int newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_6() {

		String actualQuery = "SELECT AVG(mag) FROM Magazine mag WHERE mag.isbn+AVG()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, buildQueryFormatter_5());

		int position = "SELECT AVG(mag) ".length();
		int expectedPosition = "SELECT AVG(mag) ".length();
		int newPosition = ExpressionTools.repositionCursor(jpqlExpression.toParsedText(), actualQuery, position);
		assertEquals(expectedPosition, newPosition);
	}
}