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

/**
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class UnionClauseTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() throws Exception {

		String jpqlQuery = "select e from Employee e union";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union(nullExpression())
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() throws Exception {

		String jpqlQuery = "select e from Employee e union ";

		UnionClauseTester union = union(nullExpression());
		union.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() throws Exception {

		String jpqlQuery = "select e from Employee e union all";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			unionAll(nullExpression())
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() throws Exception {

		String jpqlQuery = "select e from Employee e union all ";

		UnionClauseTester union = unionAll(nullExpression());
		union.hasSpaceAfterAll = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() throws Exception {

		String jpqlQuery = "select e from Employee e intersect";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			intersect(nullExpression())
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() throws Exception {

		String jpqlQuery = "select e from Employee e intersect ";

		UnionClauseTester union = intersect(nullExpression());
		union.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() throws Exception {

		String jpqlQuery = "select e from Employee e intersect all";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			intersectAll(nullExpression())
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() throws Exception {

		String jpqlQuery = "select e from Employee e intersect all ";

		UnionClauseTester union = intersectAll(nullExpression());
		union.hasSpaceAfterAll = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_09() throws Exception {

		String jpqlQuery = "select e from Employee e except";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			except(nullExpression())
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_10() throws Exception {

		String jpqlQuery = "select e from Employee e except ";

		UnionClauseTester union = except(nullExpression());
		union.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_11() throws Exception {

		String jpqlQuery = "select e from Employee e except all";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			exceptAll(nullExpression())
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_12() throws Exception {

		String jpqlQuery = "select e from Employee e except all ";

		UnionClauseTester union = exceptAll(nullExpression());
		union.hasSpaceAfterAll = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			union
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_13() throws Exception {

		String jpqlQuery = "select e from Employee e except select a from Alias a";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			except(
				subSelect(variable("a")),
				subFrom("Alias", "a")
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_14() throws Exception {

		String jpqlQuery = "select e from Employee e " +
		                   "except select a from Alias a " +
		                   "union " +
		                   "intersect all select p from Product p where p.id <> 2";

		UnionClauseTester union = union(nullExpression());
		union.hasSpaceAfterIdentifier = true;

		CollectionExpressionTester unionClauses = spacedCollection(
			except(
				subSelect(variable("a")),
				subFrom("Alias", "a")
			),
			union,
			intersectAll(
				subSelect(variable("p")),
				subFrom("Product", "p"),
				where(path("p.id").different(numeric(2)))
			)
		);

		unionClauses.spaces[1] = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			unionClauses
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_15() throws Exception {

		String jpqlQuery = "select e from Employee e " +
		                   "except select a " +
		                   "intersect all select p from Product p where p.id <> " +
		                   "union select d from Department d";

		ComparisonExpressionTester comparison = path("p.id").different(nullExpression());
		comparison.hasSpaceAfterIdentifier = true;

		SimpleSelectStatementTester subquery = subquery(
			subSelect(variable("a")),
			nullExpression()
		);

		subquery.hasSpaceAfterFrom = true;

		CollectionExpressionTester unionClauses = spacedCollection(
			except(subquery),
			intersectAll(
				subSelect(variable("p")),
				subFrom("Product", "p"),
				where(comparison)
			),
			union(
				subSelect(variable("d")),
				subFrom("Department", "d")
			)
		);

		unionClauses.spaces[0] = false;
		unionClauses.spaces[1] = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			unionClauses
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}