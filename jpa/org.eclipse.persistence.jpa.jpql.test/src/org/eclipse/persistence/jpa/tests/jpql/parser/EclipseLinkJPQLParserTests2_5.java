/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_5.*;

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in JPA 2.1 and the additional support provider by EclipseLink version 2.5.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLParserTests2_5 extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(" TIMESTAMP", " timestamp");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryFormatter_2() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("SCN", "scn");
			}
		};
	}

	@Test
	public void test_HQL_Query_001() {

		String jpqlQuery = "FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			from("Employee", "e")
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_HQL_Query_002() {

		String jpqlQuery = "FROM Employee e WHERE e.name = 'JPQL'";

		ExpressionTester selectStatement = selectStatement(
			from("Employee", "e"),
			where(path("e.name").equal(string("'JPQL'")))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_Query_006() {

		// UPDATE DateTime SET timestamp = CURRENT_TIMESTAMP

		ExpressionTester updateStatement = updateStatement(
			update("DateTime", set("{datetime}.timestamp", CURRENT_TIMESTAMP()))
		);

		testQuery(query_006(), updateStatement, buildQueryFormatter_1());
	}

	@Test
	public void test_Query_007() {

		// UPDATE DateTime SET scn = CURRENT_TIMESTAMP

		ExpressionTester updateStatement = updateStatement(
			update("DateTime", set("{datetime}.scn", CURRENT_TIMESTAMP()))
		);

		testQuery(query_007(), updateStatement, buildQueryFormatter_2());
	}
}