/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
public final class SelectClauseTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_11() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replaceAll("-1", "- 1");
			}
		};
	}

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT AVG(mag.price) FROM Magazine mag";

		SelectStatementTester selectStatement = selectStatement(
			select(avg("mag.price")),
			from("Magazine", "mag")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e.firstName, e.lastName, e.address FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(collection(path("e.firstName"), path("e.lastName"), path("e.address"))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT OBJECT(e), COUNT(DISTINCT e) FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(collection(object("e"), countDistinct(variable("e")))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT COUNT(DISTINCT mag.price) FROM Magazine mag";

		SelectStatementTester selectStatement = selectStatement(
			select(countDistinct(path("mag.price"))),
			from("Magazine", "mag")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT COUNT  ( DISTINCT mag.price  ) FROM Magazine mag";

		SelectStatementTester selectStatement = selectStatement(
			select(countDistinct(path("mag.price"))),
			from("Magazine", "mag")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT DISTINCT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			selectDistinct(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT AVG(e.age), e, COUNT(e.name), NEW test(e), OBJECT(e), SUM(e.age) FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(collection(
				avg("e.age"),
				variable("e"),
				count("e.name"),
				new_("test", variable("e")),
				object("e"),
				sum("e.age")
			)),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT OBJECT(e) FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(object("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT DISTINCT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			selectDistinct(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		if (!isEclipseLink2_1OrLater()) {
			return;
		}

		String query = "SELECT activityDef.ACTIVITY_NAME, " +
		               "AVG(activity.ACTIVITY_RUNNING_TIME), " +
		               "COUNT( activity.ACTIVITY_RUNNING_TIME ) , " +
		               "(SUM(activity.ACTIVITY_RUNNING_TIME * activity.ACTIVITY_RUNNING_TIME) - " +
		               "(SUM(activity.ACTIVITY_RUNNING_TIME) / COUNT(activityDef.ACTIVITY_NAME) * " +
		               "SUM(activity.ACTIVITY_RUNNING_TIME) / COUNT(activityDef.ACTIVITY_NAME))* " +
		               "COUNT(activityDef.ACTIVITY_NAME)) / (COUNT(activityDef.ACTIVITY_NAME) -1) " +
		               "FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(
				path("activityDef.ACTIVITY_NAME"),
				avg("activity.ACTIVITY_RUNNING_TIME"),
		      count("activity.ACTIVITY_RUNNING_TIME"),
		      sub(
	      		sum(
	      				path("activity.ACTIVITY_RUNNING_TIME")
	      			.multiply(
	      				path("activity.ACTIVITY_RUNNING_TIME")
	      			)
	      		)
		      	.subtract(
		      		sub(
		      				sum("activity.ACTIVITY_RUNNING_TIME")
		      			.divide(
		      				count("activityDef.ACTIVITY_NAME")
		      			)
		      			.multiply(
		      				sum("activity.ACTIVITY_RUNNING_TIME")
		      			)
		      			.divide(
		      				count("activityDef.ACTIVITY_NAME")
		      			)
		      		)
		      		.multiply(
		      			count("activityDef.ACTIVITY_NAME")
		      		)
		      	)
		      )
		      .divide(
		      	sub(
		      			count("activityDef.ACTIVITY_NAME")
		      		.subtract(
		      			numeric(1)
		      		)
		      	)
		      )
			),
			from("Employee", "e")
		);

		testQuery(query, selectStatement, buildQueryFormatter_11());
	}

	@Test
	public void testBuildExpression_12() {

		if (isJPA1_0()) {
			return;
		}

		String query = "SELECT e.name - e.age + 18, " +
		               "       e.age * 18, " +
		               "       (avg(e.age) * 18) - e.age, " +
		               "       e.salary / 12 + avg(e.salary) " +
		               "FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(
				/* Item 1 */
					path("e.name")
				.subtract(
						path("e.age")
					.add(
						numeric(18)
					)
				),
				/* Item 2 */
					path("e.age")
				.multiply(
					numeric(18)
				),
				/* Item 3 */
				sub(
						avg("e.age")
					.multiply(
						numeric(18)
					)
				)
				.subtract(
					path("e.age")
				),
				/* Item 4 */
					path("e.salary")
				.divide(
					numeric(12)
				)
				.add(avg("e.salary")
				)
			),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}
}