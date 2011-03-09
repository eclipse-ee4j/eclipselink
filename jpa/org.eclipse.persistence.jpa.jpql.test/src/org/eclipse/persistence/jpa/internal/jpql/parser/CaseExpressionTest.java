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

import org.junit.Test;

@SuppressWarnings("nls")
public final class CaseExpressionTest extends AbstractJPQLTest {

	@Test
	public void testBuildExpression_01() throws Exception {
		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		               "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
		               "         ELSE e.salary * 1.01 " +
		               "    END";

		ExpressionTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set(
					path("e.salary"),
					case_(
						new ExpressionTester[] {
							when(path("e.rating").equal(numeric(1)), path("e.salary").multiplication(numeric("1.1"))),
							when(path("e.rating").equal(numeric(2)), path("e.salary").multiplication(numeric("1.05")))
						},
						path("e.salary").multiplication(numeric(1.01))
					)
				)
			)
		);

		testQuery(query, updateStatement);
	}

	@Test
	public void testBuildExpression_02() throws Exception {
		String query = "SELECT e.name, " +
		               "       f.name, " +
		               "       CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum ' " +
		               "                   WHEN f.annualMiles > 25000 THEN 'Gold ' " +
		               "                   ELSE '' " +
		               "                   END, " +
		               "              'Frequent Flyer') " +
		               "FROM Employee e JOIN e.frequentFlierPlan f";

		ExpressionTester selectStatement = selectStatement(
			select(
				path("e.name"),
				path("f.name"),
				concat(
					case_(
						new ExpressionTester[] {
							when(path("f.annualMiles").greaterThan(numeric(50000)), string("'Platinum '")),
							when(path("f.annualMiles").greaterThan(numeric(25000)), string("'Gold '"))
						},
						string("''")
					),
					string("'Frequent Flyer'")
				)
			),
			from("Employee", "e", join("e.frequentFlierPlan", "f"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() throws Exception {
		String query = "SELECT CASE WHEN f.annualMiles > 50000 THEN 'Platinum ' " +
		               "            WHEN f.annualMiles > 25000 THEN 'Gold ' " +
		               "            ELSE '' " +
		               "       END " +
		               "FROM Employee e JOIN e.frequentFlierPlan f";

		ExpressionTester selectStatement = selectStatement(
			select(
				case_(
					new ExpressionTester[] {
						when(path("f.annualMiles").greaterThan(numeric(50000)), string("'Platinum '")),
						when(path("f.annualMiles").greaterThan(numeric(25000)), string("'Gold '"))
					},
					string("''")
				)
			),
			from("Employee", "e", join("e.frequentFlierPlan", "f"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() throws Exception {
		String query = "SELECT CASE WHEN e.age > 17 THEN 0 " +
		               "            WHEN e.age > 39 THEN 1 " +
		               "            WHEN e.age > 64 THEN 2 " +
		               "            ELSE 3 " +
		               "       END " +
		               "       + :input " +
		               "FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(
					case_(
						new ExpressionTester[] {
							when(path("e.age").greaterThan(numeric(17)), numeric(0)),
							when(path("e.age").greaterThan(numeric(39)), numeric(1)),
							when(path("e.age").greaterThan(numeric(64)), numeric(2)),
						},
						numeric(3)
					)
				.add(
					inputParameter(":input")
				)
			),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e.name," +
		               "       CASE TYPE(e) WHEN Exempt THEN 'Exempt'" +
		               "                    WHEN Contractor THEN 'Contractor'" +
		               "                    WHEN Intern THEN 'Intern'" +
		               "                    ELSE 'NonExempt'" +
		               "       END " +
		               "FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(
				path("e.name"),
				case_(type("e"),
					new ExpressionTester[] {
						when(entity("Exempt"),     string("'Exempt'")),
						when(entity("Contractor"), string("'Contractor'")),
						when(entity("Intern"),     string("'Intern'")),
					},
					string("'NonExempt'")
				)
			),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}
}