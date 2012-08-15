/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_0.*;

/**
 * This unit-tests tests the parsed tree representation of a JPQL query based on the JPA 2.0 grammar.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueriesTest2_0 extends JPQLParserTest {

	@Test
	public void test_Query_001() {

		// UPDATE Employee e
		// SET e.salary =
		//    CASE WHEN e.rating = 1 THEN e.salary * 1.1
		//         WHEN e.rating = 2 THEN e.salary * 1.05
		//         ELSE e.salary * 1.01
		//    END

		ExpressionTester updateStatement = updateStatement(
			update("Employee", "e", set(
				path("e.salary"),
				case_(
					when(path("e.rating").equal(numeric(1)),
					     path("e.salary").multiply(numeric(1.1))
					),
					when(path("e.rating").equal(numeric(2)),
					     path("e.salary").multiply(numeric(1.05))
					),
					path("e.salary").multiply(numeric(1.01))
				)
			))
		);

		testQuery(query_001(), updateStatement);
	}

	@Test
	public void test_Query_002() {

		// SELECT e.name,
		//        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
		//                     WHEN Contractor THEN 'Contractor'
		//                     WHEN Intern THEN 'Intern'
		//                     ELSE 'NonExempt'
		//        END
		// FROM Employee e, Contractor c
		// WHERE e.dept.name = 'Engineering'

		ExpressionTester selectStatement = selectStatement(
			select(
				path("e.name"),
				case_(
					type("e"),
					new ExpressionTester[] {
						when(entity("Exempt"),     string("'Exempt'")),
						when(entity("Contractor"), string("'Contractor'")),
						when(entity("Intern"),     string("'Intern'"))
					},
					string("'NonExempt'")
				)
			),
			from("Employee", "e", "Contractor", "c"),
			where(path("e.dept.name").equal(string("'Engineering'")))
		);

		testQuery(query_002(), selectStatement);
	}

	@Test
	public void test_Query_003() {

		// SELECT e.name,
		//        f.name,
		//        CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '
		//                    WHEN f.annualMiles > 25000 THEN 'Gold '
		//                    ELSE ''
		//               END,
		//               'Frequent Flyer')
		// FROM Employee e JOIN e.frequentFlierPlan f

		ExpressionTester selectStatement = selectStatement(
			select(
				path("e.name"),
				path("f.name"),
				concat(
					case_(
						when(path("f.annualMiles").greaterThan(numeric(50000)),
						     string("'Platinum '")),
						when(path("f.annualMiles").greaterThan(numeric(25000)),
						     string("'Gold '")),
						string("''")
					),
					string("'Frequent Flyer'")
				)
			),
			from("Employee", "e", join("e.frequentFlierPlan", "f"))
		);

		testQuery(query_003(), selectStatement);
	}

	@Test
	public void test_Query_004() {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (Exempt, Contractor)

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type("e").in(entity("Exempt"), entity("Contractor")))
		);

		testQuery(query_004(), selectStatement);
	}

	@Test
	public void test_Query_005() {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (:empType1, :empType2)

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type("e").in(inputParameter(":empType1"), inputParameter(":empType2")))
		);

		testQuery(query_005(), selectStatement);
	}

	@Test
	public void test_Query_006() {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN :empTypes

		InExpressionTester inExpression = in(
			type("e"),
			inputParameter(":empTypes")
		);

		inExpression.hasSpaceAfterIn     = true;
		inExpression.hasLeftParenthesis  = false;
		inExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(inExpression)
		);

		testQuery(query_006(), selectStatement);
	}

	@Test
	public void test_Query_007() {

		// SELECT TYPE(employee)
		// FROM Employee employee
		// WHERE TYPE(employee) <> Exempt

		ExpressionTester selectStatement = selectStatement(
			select(type("employee")),
			from("Employee", "employee"),
			where(type("employee").different(variable("Exempt")))
		);

		testQuery(query_007(), selectStatement);
	}

	@Test
	public void test_Query_008() {

		// SELECT t
		// FROM CreditCard c JOIN c.transactionHistory t
		// WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9

		ExpressionTester selectStatement = selectStatement(
			select(variable("t")),
			from("CreditCard", "c", join("c.transactionHistory", "t")),
			where(
					path("c.holder.name").equal(string("'John Doe'"))
				.and(
					index("t").between(numeric(0), numeric(9))))
		);

		testQuery(query_008(), selectStatement);
	}

	@Test
	public void test_Query_009() {

		// SELECT w.name
		// FROM Course c JOIN c.studentWaitlist w
		// WHERE c.name = 'Calculus'
		//       AND
		//       INDEX(w) = 0

		ExpressionTester selectStatement = selectStatement(
			select(path("w.name")),
			from("Course", "c", join("c.studentWaitlist", "w")),
			where(
					path("c.name").equal(string("'Calculus'"))
				.and(
					index("w").equal(numeric(0))))
		);

		testQuery(query_009(), selectStatement);
	}

	@Test
	public void test_Query_010() {

		// UPDATE Employee e
		// SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1
		//                              WHEN 2 THEN e.salary * 1.05
		//                              ELSE e.salary * 1.01
		//                END

		ExpressionTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set("e.salary", case_(
					path("e.rating"),
					new ExpressionTester[] {
						when(numeric(1), path("e.salary").multiply(numeric(1.1))),
						when(numeric(2), path("e.salary").multiply(numeric(1.05))),
					},
					path("e.salary").multiply(numeric(1.01))
				))
			)
		);

		testQuery(query_010(), updateStatement);
	}

	@Test
	public void test_Query_011() {

		// SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA' AND a.county = 'Santa Clara'
		// ORDER BY o.quantity, taxedCost, a.zipcode

		ExpressionTester selectStatement = selectStatement(
			select(
				path("o.quantity"),
				resultVariableAs(path("o.cost").multiply(numeric(1.08)), "taxedCost"),
				path("a.zipcode")
			),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(
					path("a.state").equal(string("'CA'"))
				.and(
					path("a.county").equal(string("'Santa Clara'"))
				)
			),
			orderBy(
				orderByItem("o.quantity"),
				orderByItem(variable("taxedCost")),
				orderByItem("a.zipcode")
			)
		);

		testQuery(query_011(), selectStatement);
	}

	@Test
	public void test_Query_012() {

		// SELECT AVG(o.quantity) as q, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// GROUP BY a.zipcode
		// ORDER BY q DESC";

		ExpressionTester selectStatement = selectStatement(
			select(
				resultVariableAs(avg("o.quantity"), "q"),
				path("a.zipcode")
			),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(path("a.state").equal(string("'CA'"))),
			groupBy(path("a.zipcode")),
			orderBy(orderByItemDesc(variable("q")))
		);

		testQuery(query_012(), selectStatement);
	}

	@Test
	public void test_Query_013() {

		// SELECT e.salary / 1000D n
		// From Employee e

		ExpressionTester selectStatement = selectStatement(
			select(resultVariable(path("e.salary").divide(numeric("1000D")), "n")),
			from("Employee", "e")
		);

		testQuery(query_013(), selectStatement);
	}

	@Test
	public void test_Query_014() {

		// SELECT MOD(a.id, 2) AS m
		// FROM Address a JOIN FETCH a.customerList
		// ORDER BY m, a.zipcode

		ExpressionTester selectStatement = selectStatement(
			select(resultVariableAs(mod(path("a.id"), numeric(2)), "m")),
			from("Address", "a", joinFetch("a.customerList")),
			orderBy(
				orderByItem(variable("m")),
				orderByItem(path("a.zipcode"))
			)
		);

		testQuery(query_014(), selectStatement);
	}

	@Test
	public void test_Query_015() {

		// SELECT ENTRY(addr) FROM Alias a JOIN a.addresses addr

		ExpressionTester selectStatement = selectStatement(
			select(entry("addr")),
			from("Alias", "a", join("a.addresses", "addr"))
		);

		testQuery(query_015(), selectStatement);
	}

	@Test
	public void test_Query_016() {

		// SELECT p
		// FROM Employee e JOIN e.projects p
		// WHERE e.id = :id AND INDEX(p) = 1

		ExpressionTester selectStatement = selectStatement(
			select(variable("p")),
			from("Employee", "e", join("e.projects", "p")),
			where(
					path("e.id").equal(inputParameter(":id"))
				.and(
					index("p").equal(numeric(1))
				)
			)
		);

		testQuery(query_016(), selectStatement);
	}
}