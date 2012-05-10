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
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ArithmeticExpressionTest extends JPQLParserTest {

	@Test
	public void testOrderOfOperations_01() {

		String query = "select e from Employee e where 10 + 2 * 10 / 2 = 20";

		// (10) + ((2 * 10) / (2))
		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					numeric(10)
				.add(
						numeric(2).multiply(numeric(10))
					.divide(
						numeric(2)
					)
				)
				.equal(numeric(20))
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testOrderOfOperations_02() {

		String query = "select e from Employee e where 10 + 2 * 10 / 2 - 2 = 18";

		// (10 + ((2 * 10) / 2)) - (2)
		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					numeric(10)
				.add(
							numeric(2)
						.multiply(
							numeric(10)
						)
					.divide(
						numeric(2)
					)
					.subtract(
						numeric(2)
					)
				)
				.equal(numeric(18))
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testOrderOfOperations_03() {

		String query = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary + 10000 - 10000 / ?1 * ?2 >= 50000";

		// (emp.salary) + ((10000) - ((10000 / ?1) * (?2))))
		ExpressionTester selectStatement = selectStatement(
			select(object("emp")),
			from("Employee", "emp"),
			where(
					path("emp.salary")
				.add(
						numeric(10000)
					.subtract(
								numeric(10000)
							.divide(
								inputParameter("?1")
							)
						.multiply(
							inputParameter("?2")
						)
					)
				)
				.greaterThanOrEqual(numeric(50000))
			)
		);

		testQuery(query, selectStatement);
	}
}