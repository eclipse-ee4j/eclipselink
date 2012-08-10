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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.junit.Test;

/**
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkComparisonExpressionTest extends JPQLParserTest {

	protected boolean isEclipseLink2_0() {
		return EclipseLinkVersion.value(getGrammar().getProviderVersion()) == EclipseLinkVersion.VERSION_2_0;
	}

	@Test
	public void test_BuildExpression_01() throws Exception {

		if (isEclipseLink2_0()) {
			return;
		}

		String query = "SELECT e FROM Employee e WHERE e.age !=";

		ComparisonExpressionTester comparison = path("e.age").notEqual(nullExpression());
		comparison.hasSpaceAfterIdentifier = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparison)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void test_BuildExpression_02() throws Exception {

		if (isEclipseLink2_0()) {
			return;
		}

		String query = "SELECT e FROM Employee e WHERE e.age != ";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").notEqual(nullExpression()))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void test_BuildExpression_03() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age != 'JPQL'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").notEqual(string("'JPQL'")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void test_BuildExpression_04() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age != 'JPQL' GROUP BY e.name";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").notEqual(string("'JPQL'"))),
			groupBy(path("e.name"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void test_BuildExpression_05() throws Exception {

		if (isEclipseLink2_0()) {
			return;
		}

		String query = "SELECT e FROM Employee e WHERE != 'JPQL' GROUP BY e.name";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(nullExpression().notEqual(string("'JPQL'"))),
			groupBy(path("e.name"))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void test_BuildExpression_06() throws Exception {

		if (isEclipseLink2_0()) {
			return;
		}

		String query = "SELECT e FROM Employee e WHERE != 'JPQL'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(nullExpression().notEqual(string("'JPQL'")))
		);

		testInvalidQuery(query, selectStatement);
	}
}