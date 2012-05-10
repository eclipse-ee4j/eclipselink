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

@SuppressWarnings("nls")
public final class EclipseLinkLikeExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%')";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					upper(path("e.firstName"))
				.like(
					upper(string("'b%'"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%') ESCAPE ' '";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					upper(path("e.firstName"))
				.like(
					upper(string("'b%'")),
					string("' '")
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%') + 2";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					upper(path("e.firstName"))
				.like(
					upper(string("'b%'")).add(numeric(2))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%') + 2 ESCAPE ' '";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					upper(path("e.firstName"))
				.like(
					upper(string("'b%'")).add(numeric(2)),
					string("' '")
				)
			)
		);

		testQuery(query, selectStatement);
	}
}