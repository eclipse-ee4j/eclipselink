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

import org.junit.Test;

@SuppressWarnings("nls")
public final class CollectionMemberExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e, f FROM Employee e, IN(e.employees) f WHERE e.name MEMBER f.offices";

		ExpressionTester selectStatement = selectStatement(
			select(
				variable("e"),
				variable("f")
			),
			from(
				identificationVariableDeclaration("Employee", "e"),
				fromIn("e.employees", "f")
			),
			where(
					path("e.name")
				.member(
					collectionValuedPath("f.offices")
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e, f FROM Employee e, IN(e.employees) f WHERE e.name MEMBER OF e.employees";

		ExpressionTester selectStatement = selectStatement(
			select(
				variable("e"),
				variable("f")
			),
			from(
				identificationVariableDeclaration("Employee", "e"),
				fromIn("e.employees", "f")
			),
			where(
					path("e.name")
				.memberOf(
					collectionValuedPath("e.employees")
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e, f FROM Employee e, IN(e.employees) f WHERE e.name NOT MEMBER OF e.employees";

		ExpressionTester selectStatement = selectStatement(
			select(
				variable("e"),
				variable("f")
			),
			from(
				identificationVariableDeclaration("Employee", "e"),
				fromIn("e.employees", "f")
			),
			where(
					path("e.name")
				.notMemberOf(
					collectionValuedPath("e.employees")
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE MEMBER";

		CollectionMemberExpressionTester member = member(nullExpression(), nullExpression());
		member.hasSpaceAfterMember = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(member)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE NOT MEMBER";

		CollectionMemberExpressionTester member = notMember(nullExpression(), nullExpression());
		member.hasSpaceAfterMember = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(member)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE MEMBER OF";

		CollectionMemberExpressionTester member = memberOf(nullExpression(), nullExpression());
		member.hasSpaceAfterOf = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(member)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE NOT MEMBER OF";

		CollectionMemberExpressionTester member = notMemberOf(nullExpression(), nullExpression());
		member.hasSpaceAfterOf = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(member)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE NOT MEMBER OF ";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notMemberOf(nullExpression(), nullExpression()))
		);

		testInvalidQuery(query, selectStatement);
	}
}