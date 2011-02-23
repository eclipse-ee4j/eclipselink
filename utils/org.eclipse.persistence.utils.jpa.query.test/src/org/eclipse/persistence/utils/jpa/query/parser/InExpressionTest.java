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

@SuppressWarnings("nls")
public final class InExpressionTest extends AbstractJPQLTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE e.name IN (e.address.street)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").in(path("e.address.street")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE e NOT IN(e.address.street)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(variable("e").notIn(path("e.address.street")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE e IN (e.address.street, e.name)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					variable("e")
				.in(
					path("e.address.street"),
					path("e.name")
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE e IN (SELECT m FROM Manager m)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					variable("e")
				.in(
					subquery(
						subSelect(variable("m")),
						subFrom("Manager", "m")
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE e IN";

		InExpressionTester inExpression = in(variable("e"), nullExpression());
		inExpression.hasSpaceAfterIn     = false;
		inExpression.hasLeftParenthesis  = false;
		inExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(inExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE e IN(";

		InExpressionTester inExpression = in(variable("e"), nullExpression());
		inExpression.hasLeftParenthesis  = true;
		inExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(inExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE e IN()";

		InExpressionTester inExpression = in(variable("e"), nullExpression());
		inExpression.hasLeftParenthesis  = true;
		inExpression.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(inExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE e NOT IN()";

		InExpressionTester notInExpression = notIn(variable("e"), nullExpression());
		notInExpression.hasLeftParenthesis  = true;
		notInExpression.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notInExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT e FROM Employee e WHERE e NOT IN(";

		InExpressionTester notInExpression = notIn(variable("e"), nullExpression());
		notInExpression.hasLeftParenthesis  = true;
		notInExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notInExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e " +
		               "WHERE e IN ((SELECT e2 FROM Employee e2 WHERE e2 = e), " +
		               "            (SELECT e3 FROM Employee e3 WHERE e3 = e))";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					variable("e")
				.in(
					subExpression(subquery(
						subSelect(variable("e2")),
						subFrom("Employee", "e2"),
						where(variable("e2").equal(variable("e")))
					)),
					subExpression(subquery(
						subSelect(variable("e3")),
						subFrom("Employee", "e3"),
						where(variable("e3").equal(variable("e")))
					))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String query = "SELECT e FROM Employee e WHERE NOT IN('JPQL', 'JPA')";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					nullExpression()
				.notIn(
					string("'JPQL'"),
					string("'JPA'")
				)
			)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String query = "SELECT e FROM Employee e WHERE IN('JPQL', 'JPA')";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					nullExpression()
				.in(
					string("'JPQL'"),
					string("'JPA'")
				)
			)
		);

		testInvalidQuery(query, selectStatement);
	}
}