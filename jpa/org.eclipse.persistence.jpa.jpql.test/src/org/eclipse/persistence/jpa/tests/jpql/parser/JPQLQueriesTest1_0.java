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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;

/**
 * This unit-tests tests the parsed tree representation of a JPQL query.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueriesTest1_0 extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("1+(", "1 + (");
			}
		};
	}

	@Test
	public void test_Query_001() {

		// SELECT e FROM Employee e

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);

		testQuery(query_001(), selectStatement);
	}

	@Test
	public void test_Query_002() {

		// SELECT e\nFROM Employee e

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);

		testQuery(query_002(), selectStatement);
	}

	@Test
	public void test_Query_003() {

		// SELECT e
      // FROM Employee e
      // WHERE e.department.name = 'NA42' AND
      //       e.address.state IN ('NY', 'CA')

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					path("e.department.name").equal(string("'NA42'"))
				.and(
					path("e.address.state").in(string("'NY'"), string("'CA'"))
				)
			)
		);

		testQuery(query_003(), selectStatement);
	}

	@Test
	public void test_Query_004() {

		// SELECT p.number
		// FROM Employee e, Phone p
		// WHERE     e = p.employee
		//       AND e.department.name = 'NA42'
		//       AND p.type = 'Cell'

		ExpressionTester selectStatement = selectStatement(
			select(path("p.number")),
			from("Employee", "e", "Phone", "p"),
			where(
					variable("e").equal(path("p.employee"))
				.and(
					path("e.department.name").equal(string("'NA42'"))
				)
				.and(
					path("p.type").equal(string("'Cell'"))
				)
			)
		);

		testQuery(query_004(), selectStatement);
	}

	@Test
	public void test_Query_005() {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		ExpressionTester selectStatement = selectStatement(
			select(
				variable("d"),
				count(variable("e")),
				max("e.salary"),
				avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			nullExpression(),
			groupBy(variable("d")),
			having(count(variable("e")).greaterThanOrEqual(numeric(5))),
			nullExpression()
		);

		testQuery(query_005(), selectStatement);
	}

	@Test
	public void test_Query_006() {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = ?1
		//       AND e.salary > ?2

		ExpressionTester andExpression =
				path("e.department").equal(inputParameter("?1"))
			.and(
				path("e.salary").greaterThan(inputParameter("?2")));

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);

		testQuery(query_006(), selectStatement);
	}

	@Test
	public void test_Query_007() {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = :dept
		//       AND e.salary > :base

		ExpressionTester andExpression =
				path("e.department").equal(inputParameter(":dept"))
			.and(
				path("e.salary").greaterThan(inputParameter(":base")));

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);

		testQuery(query_007(), selectStatement);
	}

	@Test
	public void test_Query_008() {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = 'NA65'
		//       AND e.name = 'UNKNOWN'' OR e.name = ''Roberts'

		ExpressionTester andExpression =
				path("e.department").equal(string("'NA65'"))
			.and(
				path("e.name").equal(string("'UNKNOWN'' OR e.name = ''Roberts'")));

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);

		testQuery(query_008(), selectStatement);
	}

	@Test
	public void test_Query_009() {

		// SELECT e
		// FROM Employee e
		// WHERE e.startDate BETWEEN ?1 AND ?2

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.startDate").between(inputParameter("?1"), inputParameter("?2")))
		);

		testQuery(query_009(), selectStatement);
	}

	@Test
	public void test_Query_010() {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = :dept AND
		//      e.salary = (SELECT MAX(e.salary)
		//                  FROM Employee e
		//                  WHERE e.department = :dept)

		ExpressionTester subquery = subquery(
			subSelect(max("e.salary")),
			subFrom("Employee", "e"),
			where(path("e.department").equal(inputParameter(":dept")))
		);

		ExpressionTester andExpression =
				path("e.department").equal(inputParameter(":dept"))
			.and(
				path("e.salary").equal(sub(subquery)));

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);

		testQuery(query_010(), selectStatement);
	}

	@Test
	public void test_Query_011() {

		// SELECT e
		// FROM Project p JOIN p.employees e
		// WHERE p.name = ?1
		// ORDER BY e.name

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Project", "p", join("p.employees", "e")),
			where(path("p.name").equal(inputParameter("?1"))),
			nullExpression(),
			nullExpression(),
			orderBy("e.name")
		);

		testQuery(query_011(), selectStatement);
	}

	@Test
	public void test_Query_012() {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS EMPTY";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(isEmpty("e.projects"))
		);

		testQuery(query_012(), selectStatement);
	}

	@Test
	public void test_Query_013() {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS NOT EMPTY";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(isNotEmpty("e.projects"))
		);

		testQuery(query_013(), selectStatement);
	}

	@Test
	public void test_Query_014() {

		// UPDATE Employee e
		// SET e.manager = ?1
		// WHERE e.department = ?2

		ExpressionTester updateStatement = updateStatement(
			update("Employee", "e", set("e.manager", inputParameter("?1"))),
			where(path("e.department").equal(inputParameter("?2")))
		);

		testQuery(query_014(), updateStatement);
	}

	@Test
	public void test_Query_015() {

		// DELETE FROM Project p
      // WHERE p.employees IS EMPTY

		ExpressionTester deleteStatement = deleteStatement(
			"Project",
			"p",
			where(isEmpty("p.employees"))
		);

		testQuery(query_015(), deleteStatement);
	}

	@Test
	public void test_Query_016() {

		// DELETE FROM Department d
		// WHERE d.name IN ('CA13', 'CA19', 'NY30')

		ExpressionTester inExpression = in(
			"d.name",
			string("'CA13'"),
			string("'CA19'"),
			string("'NY30'")
		);

		ExpressionTester deleteStatement = deleteStatement(
			"Department",
			"d",
			where(inExpression)
		);

		testQuery(query_016(), deleteStatement);
	}

	@Test
	public void test_Query_017() {

		// UPDATE Employee e
		// SET e.department = null
		// WHERE e.department.name IN ('CA13', 'CA19', 'NY30')

		ExpressionTester updateStatement = updateStatement(
			update("Employee", "e", set("e.department", NULL())),
			where(path("e.department.name").in(string("'CA13'"), string("'CA19'"), string("'NY30'")))
		);

		testQuery(query_017(), updateStatement);
	}

	@Test
	public void test_Query_018() {

		// SELECT d
		// FROM Department d
		// WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'

		ExpressionTester likeExpression = like(
			path("d.name"),
			string("'QA\\_%'"),
			'\\'
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("d")),
			from("Department", "d"),
			where(likeExpression)
		);

		testQuery(query_018(), selectStatement);
	}

	@Test
	public void test_Query_019() {

		// SELECT e
		// FROM Employee e
		// WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)

		ExpressionTester subQuery = subquery(
			subSelect(max("e2.salary")),
			subFrom("Employee", "e2")
		);

		ExpressionTester comparisonExpression = equal(
			path("e.salary"),
			sub(subQuery)
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparisonExpression)
		);

		testQuery(query_019(), selectStatement);
	}

	@Test
	public void test_Query_020() {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')

		ExpressionTester comparisonExpression1 = equal(
			path("p.employee"),
			variable("e")
		);

		ExpressionTester comparisonExpression2 = equal(
			path("p.type"),
			string("'Cell'")
		);

		ExpressionTester subQuery = subquery(
			subSelect(variable("p")),
			subFrom("Phone", "p"),
			where(and(comparisonExpression1, comparisonExpression2))
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(subQuery))
		);

		testQuery(query_020(), selectStatement);
	}

	@Test
	public void test_Query_021() {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
				exists(
					subquery(
						subSelect(variable("p")),
						subFrom(fromCollection("e.phones", "p")),
						where(path("p.type").equal(string("'Cell'")))
					)
				)
			)
		);

		testQuery(query_021(), selectStatement);
	}

	@Test
	public void test_Query_022() {

		// SELECT e
		// FROM Employee e
		// WHERE e.department IN (SELECT DISTINCT d
		//                        FROM Department d JOIN d.employees de JOIN de.projects p
		//                        WHERE p.name LIKE 'QA%')

		ExpressionTester subquery = subquery(
			subSelectDistinct(variable("d")),
			subFrom("Department", "d", join("d.employees", "de"), join("de.projects", "p")),
			where(like(path("p.name"), string("'QA%'")))
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(in("e.department", subquery))
		);

		testQuery(query_022(), selectStatement);
	}

	@Test
	public void test_Query_023() {

		// SELECT p
		// FROM Phone p
		// WHERE p.type NOT IN ('Office', 'Home')

		ExpressionTester notInExpression = notIn(
			"p.type",
			string("'Office'"),
			string("'Home'")
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("p")),
			from("Phone", "p"),
			where(notInExpression)
		);

		testQuery(query_023(), selectStatement);
	}

	@Test
	public void test_Query_024() {

		// SELECT m
		// FROM Employee m
		// WHERE (SELECT COUNT(e)
		//        FROM Employee e
		//        WHERE e.manager = m) > 0

		ExpressionTester comparisonExpression1 = equal(
			path("e.manager"),
			variable("m")
		);

		ExpressionTester subquery = subquery(
			subSelect(count(variable("e"))),
			subFrom("Employee", "e"),
			where(comparisonExpression1)
		);

		ExpressionTester comparisonExpression2 = greaterThan(
			sub(subquery),
			numeric(0L)
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("m")),
			from("Employee", "m"),
			where(comparisonExpression2)
		);

		testQuery(query_024(), selectStatement);
	}

	@Test
	public void test_Query_025() {

		// SELECT e
		// FROM Employee e
		// WHERE e MEMBER OF e.directs

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(memberOf("e", "e.directs"))
		);

		testQuery(query_025(), selectStatement);
	}

	@Test
	public void test_Query_026() {

		// SELECT e
		// FROM Employee e
		// WHERE NOT EXISTS (SELECT p
		//                   FROM e.phones p
		//                   WHERE p.type = 'Cell')

		ExpressionTester comparisonExpression = equal(
			path("p.type"),
			string("'Cell'")
		);

		ExpressionTester subquery = subquery(
			subSelect(variable("p")),
			subFrom(fromCollection("e.phones", "p")),
			where(comparisonExpression)
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notExists(subquery))
		);

		testQuery(query_026(), selectStatement);
	}

	@Test
	public void test_Query_027() {

		// SELECT e
		// FROM Employee e
		// WHERE e.directs IS NOT EMPTY AND
		//       e.salary < ALL (SELECT d.salary FROM e.directs d)

		ExpressionTester subquery = subquery(
			subSelect(path("d.salary")),
			subFrom(fromCollection("e.directs", "d"))
		);

		ExpressionTester comparisonExpression = lowerThan(
			path("e.salary"),
			all(subquery)
		);

		ExpressionTester andExpression = and(
			isNotEmpty("e.directs"),
			comparisonExpression
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);

		testQuery(query_027(), selectStatement);
	}

	@Test
	public void test_Query_028() {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p
		//                           WHERE p.name LIKE 'QA%')

		ExpressionTester subquery = subquery(
			subSelectDistinct(variable("d")),
			subFrom("Department", "d", join("d.employees", "de"), join("de.projects", "p")),
			where(like(path("p.name"), string("'QA%'")))
		);

		ExpressionTester comparisonExpression = equal(
			path("e.department"),
			anyExpression(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparisonExpression)
		);

		testQuery(query_028(), selectStatement);
	}

	@Test
	public void test_Query_029() {

		// SELECT d
		// FROM Department d
		// WHERE SIZE(d.employees) = 2

		ExpressionTester selectStatement = selectStatement(
			select(variable("d")),
			from("Department", "d"),
			where(equal(size("d.employees"), numeric(2L)))
		);

		testQuery(query_029(), selectStatement);
	}

	@Test
	public void test_Query_030() {

		// SELECT d
      // FROM Department d
      // WHERE (SELECT COUNT(e)
      //        FROM d.employees e) = 2

		ExpressionTester subquery = subquery(
			subSelect(count(variable("e"))),
			subFrom(fromCollection("d.employees", "e"))
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("d")),
			from("Department", "d"),
			where(equal(sub(subquery), numeric(2)))
		);

		testQuery(query_030(), selectStatement);
	}

	@Test
	public void test_Query_031() {

		// SELECT e
		// FROM Employee e
		// ORDER BY e.name DESC

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc("e.name"))
		);

		testQuery(query_031(), selectStatement);
	}

	@Test
	public void test_Query_032() {

		// SELECT e
      // FROM Employee e JOIN e.department d
      // ORDER BY d.name, e.name DESC

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join("e.department", "d")),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(
				orderByItem("d.name"),
				orderByItemDesc("e.name")
			)
		);

		testQuery(query_032(), selectStatement);
	}

	@Test
	public void test_Query_033() {

		// SELECT AVG(e.salary) FROM Employee e
		ExpressionTester selectStatement = selectStatement(
			select(avg("e.salary")),
			from("Employee", "e")
		);

		testQuery(query_033(), selectStatement);
	}

	@Test
	public void test_Query_034() {

		// SELECT d.name, AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d.name

		ExpressionTester selectStatement = selectStatement(
			select(path("d.name"), avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			nullExpression(),
			groupBy(path("d.name")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_034(), selectStatement);
	}

	@Test
	public void test_Query_035() {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name

		ExpressionTester selectStatement = selectStatement(
			select(path("d.name"), avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			where(isEmpty("e.directs")),
			groupBy(path("d.name")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_035(), selectStatement);
	}

	@Test
	public void test_Query_036() {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name
      // HAVING AVG(e.salary) > 50000

		ExpressionTester selectStatement = selectStatement(
			select(path("d.name"), avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			where(isEmpty("e.directs")),
			groupBy(path("d.name")),
			having(greaterThan(avg("e.salary"), numeric(50000))),
			nullExpression()
		);

		testQuery(query_036(), selectStatement);
	}

	@Test
	public void test_Query_037() {

		// SELECT e, COUNT(p), COUNT(DISTINCT p.type)
		// FROM Employee e JOIN e.phones p
		// GROUP BY e

		ExpressionTester selectStatement = selectStatement(
			select(variable("e"),
			       count(variable("p")),
			       countDistinct(path("p.type"))),
			from("Employee", "e", join("e.phones", "p")),
			nullExpression(),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_037(), selectStatement);
	}

	@Test
	public void test_Query_038() {

		// SELECT d.name, e.salary, COUNT(p)
		// FROM Department d JOIN d.employees e JOIN e.projects p
		// GROUP BY d.name, e.salary

		ExpressionTester selectStatement = selectStatement(
			select(path("d.name"),
			       path("e.salary"),
			       count(variable("p"))),
			from("Department", "d", join("d.employees", "e"),
			                        join("e.projects", "p")),
			nullExpression(),
			groupBy(path("d.name"), path("e.salary")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_038(), selectStatement);
	}

	@Test
	public void test_Query_039() {

		// SELECT e, COUNT(p)
		// FROM Employee e JOIN e.projects p
		// GROUP BY e
		// HAVING COUNT(p) >= 2

		ExpressionTester selectStatement = selectStatement(
			select(variable("e"), count(variable("p"))),
			from("Employee", "e", join("e.projects", "p")),
			nullExpression(),
			groupBy(variable("e")),
			having(greaterThanOrEqual(count(variable("p")), numeric(2))),
			nullExpression()
		);

		testQuery(query_039(), selectStatement);
	}

	@Test
	public void test_Query_040() {

		// UPDATE Employee e
		// SET e.salary = 60000
		// WHERE e.salary = 55000

		ExpressionTester updateStatement = updateStatement(
			update("Employee", "e", set("e.salary", numeric(60000))),
			where(equal(path("e.salary"), numeric(55000)))
		);

		testQuery(query_040(), updateStatement);
	}

	@Test
	public void test_Query_041() {

		// UPDATE Employee e
		// SET e.salary = e.salary + 5000
		// WHERE EXISTS (SELECT p
		//               FROM e.projects p
		//               WHERE p.name = 'Release1')

		ExpressionTester additionExpression = add(
			path("e.salary"),
			numeric(5000)
		);

		ExpressionTester subquery = subquery(
			subSelect(variable("p")),
			subFrom(fromCollection("e.projects", "p")),
			where(equal(path("p.name"), string("'Release1'")))
		);

		ExpressionTester updateStatement = updateStatement(
			update("Employee", "e", set("e.salary", additionExpression)),
			where(exists(subquery))
		);

		testQuery(query_041(), updateStatement);
	}

	@Test
	public void test_Query_042() {

		// UPDATE Phone p
		// SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)),
		//     p.type = 'Business'
		// WHERE p.employee.address.city = 'New York' AND p.type = 'Office'

		ExpressionTester concatExpression = concat(
			string("'288'"),
			substring(
				path("p.number"),
				locate(path("p.number"), string("'-'")),
				numeric(4L)
			)
		);

		ExpressionTester andExpression = and(
			equal(path("p.employee.address.city"), string("'New York'")),
			equal(path("p.type"), string("'Office'"))
		);

		ExpressionTester updateStatement = updateStatement(
			update(
				"Phone", "p",
				set("p.number", concatExpression),
				set("p.type", string("'Business'"))
			),
			where(andExpression)
		);

		testQuery(query_042(), updateStatement);
	}

	@Test
	public void test_Query_043() {

		// DELETE FROM Employee e
		// WHERE e.department IS NULL";

		ExpressionTester deleteStatement = deleteStatement(
			"Employee", "e",
			where(isNull(path("e.department")))
		);

		testQuery(query_043(), deleteStatement);
	}

	@Test
	public void test_Query_044() {

		// Select Distinct object(c)
		// From Customer c, In(c.orders) co
		// Where co.totalPrice >= Some (Select o.totalPrice
		//                              From Order o, In(o.lineItems) l
		//                              Where l.quantity = 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(equal(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = greaterThanOrEqual(
			path("co.totalPrice"),
			some(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_044(), selectStatement);
	}

	@Test
	public void test_Query_045() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= SOME (Select o.totalPrice
		//                              FROM Order o, IN(o.lineItems) l
		//                              WHERE l.quantity = 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(equal(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = lowerThanOrEqual(
			path("co.totalPrice"),
			some(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_045(), selectStatement);
	}

	@Test
	public void test_Query_046() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)

		ExpressionTester subquery = subquery(
			subSelect(max("o.totalPrice")),
			subFrom("Order", "o")
		);

		ExpressionTester comparisonExpression = equal(
			path("co.totalPrice"),
			any(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_046(), selectStatement);
	}

	@Test
	public void test_Query_047() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(equal(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = lowerThan(
			path("co.totalPrice"),
			any(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_047(), selectStatement);
	}

	@Test
	public void test_Query_048() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(
					path("co.totalPrice")
				.greaterThan(
					any(subquery(
						subSelect(path("o.totalPrice")),
						subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
						where(equal(path("l.quantity"), numeric(3L)))
					))
				)
			)
		);

		testQuery(query_048(), selectStatement);
	}

	@Test
	public void test_Query_049() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)

		ExpressionTester subquery = subquery(
			subSelect(min("o.totalPrice")),
			subFrom("Order", "o")
		);

		ExpressionTester comparisonExpression = different(
			path("co.totalPrice"),
			all(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_049(), selectStatement);
	}

	@Test
	public void test_Query_050() {

		// SELECT Distinct object(c)
      // FROM Customer c, IN(c.orders) co
      // WHERE co.totalPrice >= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity >= 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThanOrEqual(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = greaterThanOrEqual(
			path("co.totalPrice"),
			all(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_050(), selectStatement);
	}

	@Test
	public void test_Query_051() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity > 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThan(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = lowerThanOrEqual(
			path("co.totalPrice"),
			all(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_051(), selectStatement);
	}

	@Test
	public void test_Query_052() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)

		ExpressionTester subquery = subquery(
			subSelect(min("o.totalPrice")),
			subFrom("Order", "o")
		);

		ExpressionTester comparisonExpression = equal(
			path("co.totalPrice"),
			all(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_052(), selectStatement);
	}

	@Test
	public void test_Query_053() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThan(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = lowerThan(
			path("co.totalPrice"),
			all(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_053(), selectStatement);
	}

	@Test
	public void test_Query_054() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		ExpressionTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThan(path("l.quantity"), numeric(3L)))
		);

		ExpressionTester comparisonExpression = greaterThan(
			path("co.totalPrice"),
			all(subquery)
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);

		testQuery(query_054(), selectStatement);
	}

	@Test
	public void test_Query_055() {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT l
		//               FROM o.lineItems l
		//               where l.quantity > 3)

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", join("c.orders", "o")),
			where(
				exists(
					subquery(
						subSelect(variable("l")),
						subFrom(fromCollection("o.lineItems", "l")),
						where(path("l.quantity").greaterThan(numeric(3L)))
					)
				)
			)
		);

		testQuery(query_055(), selectStatement);
	}

	@Test
	public void test_Query_056() {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice BETWEEN 1000 AND 1200)

		ExpressionTester subquery = subquery(
			subSelect(variable("o")),
			subFrom(fromCollection("c.orders", "o")),
			where(between(path("o.totalPrice"), numeric(1000L), numeric(1200L)))
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", join("c.orders", "o")),
			where(exists(subquery))
		);

		testQuery(query_056(), selectStatement);
	}

	@Test
	public void test_Query_057() {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.state IN(Select distinct w.state
		//                       from c.work w
		//                       where w.state = :state)

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(
					path("c.home.state")
				.in(
					subquery(
						subSelectDistinct(path("w.state")),
						subFrom(fromCollection("c.work", "w")),
						where(path("w.state").equal(inputParameter(":state")))
					)
				)
			)
		);

		testQuery(query_057(), selectStatement);
	}

	@Test
	public void test_Query_058() {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		ExpressionTester subquery = subquery(
			subSelect(variable("c")),
			subFrom(fromCollection("o.customer", "c")),
			where(like(path("c.name"), string("'%Caruso'")))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			from("Order", "o"),
			where(exists(subquery))
		);

		testQuery(query_058(), selectStatement);
	}

	@Test
	public void test_Query_059() {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice > 1500)

		ExpressionTester subquery = subquery(
			subSelect(variable("o")),
			subFrom(fromCollection("c.orders", "o")),
			where(greaterThan(path("o.totalPrice"), numeric(1500)))
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(exists(subquery))
		);

		testQuery(query_059(), selectStatement);
	}

	@Test
	public void test_Query_060() {

		// SELECT c
		// FROM Customer c
		// WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)

		ExpressionTester subquery = subquery(
			subSelect(variable("o1")),
			subFrom(fromCollection("c.orders", "o1"))
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(notExists(subquery))
		);

		testQuery(query_060(), selectStatement);
	}

	@Test
	public void test_Query_061() {

		// select object(o)
		// FROM Order o
		// Where SQRT(o.totalPrice) > :doubleValue

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			from("Order", "o"),
			where(greaterThan(sqrt(path("o.totalPrice")), inputParameter(":doubleValue")))
		);

		testQuery(query_061(), selectStatement);
	}

	@Test
	public void test_Query_062() {

		// select sum(o.totalPrice)
		// FROM Order o
		// GROUP BY o.totalPrice
		// HAVING ABS(o.totalPrice) = :doubleValue

		ExpressionTester selectStatement = selectStatement(
			select(sum("o.totalPrice")),
			from("Order", "o"),
			nullExpression(),
			groupBy(path("o.totalPrice")),
			having(equal(abs(path("o.totalPrice")), inputParameter(":doubleValue"))),
			nullExpression()
		);

		testQuery(query_062(), selectStatement);
	}

	@Test
	public void test_Query_063() {

		// select c.name
		// FROM Customer c
		// Group By c.name
		// HAVING trim(TRAILING from c.name) = ' David R. Vincent'

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(trimTrailingFrom(path("c.name")), string("' David R. Vincent'"))),
			nullExpression()
		);

		testQuery(query_063(), selectStatement);
	}

	@Test
	public void test_Query_064() {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// Having trim(LEADING from c.name) = 'David R. Vincent '

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(trimLeadingFrom(path("c.name")), string("'David R. Vincent '"))),
			nullExpression()
		);

		testQuery(query_064(), selectStatement);
	}

	@Test
	public void test_Query_065() {

		// select c.name
		// FROM  Customer c
		// Group by c.name
		// HAVING trim(BOTH from c.name) = 'David R. Vincent'

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(trimBothFrom(path("c.name")), string("'David R. Vincent'"))),
			nullExpression()
		);

		testQuery(query_065(), selectStatement);
	}

	@Test
	public void test_Query_066() {

		// select c.name
		// FROM  Customer c
		// GROUP BY c.name
		// HAVING LOCATE('Frechette', c.name) > 0

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(greaterThan(locate(string("'Frechette'"), path("c.name")), numeric(0L))),
			nullExpression()
		);

		testQuery(query_066(), selectStatement);
	}

	@Test
	public void test_Query_067() {

		// select a.city
		// FROM  Customer c JOIN c.home a
		// GROUP BY a.city
		// HAVING LENGTH(a.city) = 10

		ExpressionTester selectStatement = selectStatement(
			select(path("a.city")),
			from("Customer", "c", join("c.home", "a")),
			nullExpression(),
			groupBy(path("a.city")),
			having(equal(length(path("a.city")), numeric(10L))),
			nullExpression()
		);

		testQuery(query_067(), selectStatement);
	}

	@Test
	public void test_Query_068() {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.country
		// HAVING UPPER(cc.country) = 'ENGLAND'

		ExpressionTester selectStatement = selectStatement(
			select(count("cc.country")),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.country")),
			having(equal(upper(path("cc.country")), string("'ENGLAND'"))),
			nullExpression()
		);

		testQuery(query_068(), selectStatement);
	}

	@Test
	public void test_Query_069() {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING LOWER(cc.code) = 'gbr'

		ExpressionTester selectStatement = selectStatement(
			select(count("cc.country")),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.code")),
			having(equal(lower(path("cc.code")), string("'gbr'"))),
			nullExpression()
		);

		testQuery(query_069(), selectStatement);
	}

	@Test
	public void test_Query_070() {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// HAVING c.name = concat(:fmname, :lname)

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(path("c.name"), concat(inputParameter(":fmname"), inputParameter(":lname")))),
			nullExpression()
		);

		testQuery(query_070(), selectStatement);
	}

	@Test
	public void test_Query_071() {

		// select count(c)
		// FROM  Customer c JOIN c.aliases a
		// GROUP BY a.alias
		// HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

		ExpressionTester selectStatement = selectStatement(
			select(count(variable("c"))),
			from("Customer", "c", join("c.aliases", "a")),
			nullExpression(),
			groupBy(path("a.alias")),
			having(equal(
				path("a.alias"),
				substring(inputParameter(":string1"), inputParameter(":int1"), inputParameter(":int2"))
			)),
			nullExpression()
		);

		testQuery(query_071(), selectStatement);
	}

	@Test
	public void test_Query_072() {

		// select c.country.country
		// FROM  Customer c
		// GROUP BY c.country.country

		ExpressionTester selectStatement = selectStatement(
			select(path("c.country.country")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.country.country")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_072(), selectStatement);
	}

	@Test
	public void test_Query_073() {

		// select Count(c)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING cc.code IN ('GBR', 'CHA')

		ExpressionTester selectStatement = selectStatement(
			select(count(variable("c"))),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.code")),
			having(in(path("cc.code"), string("'GBR'"), string("'CHA'"))),
			nullExpression()
		);

		testQuery(query_073(), selectStatement);
	}

	@Test
	public void test_Query_074() {

		// select c.name
		// FROM  Customer c JOIN c.orders o
		// WHERE o.totalPrice BETWEEN 90 AND 160
		// GROUP BY c.name

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c", join("c.orders", "o")),
			where(between(path("o.totalPrice"), numeric(90), numeric(160))),
			groupBy(path("c.name")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_074(), selectStatement);
	}

	@Test
	public void test_Query_075() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice > 10000

		ExpressionTester orExpression = or(
			equal(path("o.customer.id"), string("'1001'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);

		testQuery(query_075(), selectStatement);
	}

	@Test
	public void test_Query_076() {

		// select Distinct Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice < 1000

		ExpressionTester orExpression = or(
			equal(path("o.customer.id"), string("'1001'")),
			lowerThan(path("o.totalPrice"), numeric(1000))
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);

		testQuery(query_076(), selectStatement);
	}

	@Test
	public void test_Query_077() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

		ExpressionTester orExpression = or(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);

		testQuery(query_077(), selectStatement);
	}

	@Test
	public void test_Query_078() {

		// select DISTINCT o
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

		ExpressionTester orExpression = or(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(5000))
		);

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);

		testQuery(query_078(), selectStatement);
	}

	@Test
	public void test_Query_079() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice > 10000

		ExpressionTester andExpression = and(
			equal(path("o.customer.id"), string("'1001'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);

		testQuery(query_079(), selectStatement);
	}

	@Test
	public void test_Query_080() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice < 1000

		ExpressionTester andExpression = and(
			equal(path("o.customer.id"), string("'1001'")),
			lowerThan(path("o.totalPrice"), numeric(1000))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);

		testQuery(query_080(), selectStatement);
	}

	@Test
	public void test_Query_081() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

		ExpressionTester andExpression = and(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);

		testQuery(query_081(), selectStatement);
	}

	@Test
	public void test_Query_082() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

		ExpressionTester andExpression = and(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(500))
		);

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);

		testQuery(query_082(), selectStatement);
	}

	@Test
	public void test_Query_083() {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("p")),
			from("Product", "p"),
			where(notBetween(path("p.shelfLife.soldDate"), inputParameter(":date1"), inputParameter(":newdate")))
		);

		testQuery(query_083(), selectStatement);
	}

	@Test
	public void test_Query_084() {

		// SELECT DISTINCT o
		// From Order o
		// where o.totalPrice NOT BETWEEN 1000 AND 1200

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("o")),
			from("Order", "o"),
			where(notBetween(path("o.totalPrice"), numeric(1000), numeric(1200)))
		);

		testQuery(query_084(), selectStatement);
	}

	@Test
	public void test_Query_085() {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate BETWEEN :date1 AND :date6

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("p")),
			from("Product", "p"),
			where(between(path("p.shelfLife.soldDate"), inputParameter(":date1"), inputParameter(":date6")))
		);

		testQuery(query_085(), selectStatement);
	}

	@Test
	public void test_Query_086() {

		// SELECT DISTINCT a
		// from Alias a LEFT JOIN FETCH a.customers
		// where a.alias LIKE 'a%'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("a")),
			from("Alias", "a", leftJoinFetch("a.customers")),
			where(like(path("a.alias"), string("'a%'")))
		);

		testQuery(query_086(), selectStatement);
	}

	@Test
	public void test_Query_087() {

		// select Object(o)
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.name LIKE '%Caruso'

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			from("Order", "o", leftJoinFetch("o.customer")),
			where(like(path("o.customer.name"), string("'%Caruso'")))
		);

		testQuery(query_087(), selectStatement);
	}

	@Test
	public void test_Query_088() {

		// select o
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.home.city='Lawrence'

		ExpressionTester selectStatement = selectStatement(
			select(variable("o")),
			from("Order", "o", leftJoinFetch("o.customer")),
			where(equal(path("o.customer.home.city"), string("'Lawrence'")))
		);

		testQuery(query_088(), selectStatement);
	}

	@Test
	public void test_Query_089() {

		// SELECT DISTINCT c
		// from Customer c LEFT JOIN FETCH c.orders
		// where c.home.state IN('NY','RI')

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", leftJoinFetch("c.orders")),
			where(in(path("c.home.state"), string("'NY'"), string("'RI'")))
		);

		testQuery(query_089(), selectStatement);
	}

	@Test
	public void test_Query_090() {

		// SELECT c
		// from Customer c JOIN FETCH c.spouse

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c", joinFetch("c.spouse"))
		);

		testQuery(query_090(), selectStatement);
	}

	@Test
	public void test_Query_091() {

		// SELECT Object(c)
		// from Customer c INNER JOIN c.aliases a
		// where a.alias = :aName

		ExpressionTester selectStatement = selectStatement(
			select(object("c")),
			from("Customer", "c", innerJoin("c.aliases", "a")),
			where(equal(path("a.alias"), inputParameter(":aName")))
		);

		testQuery(query_091(), selectStatement);
	}

	@Test
	public void test_Query_092() {

		// SELECT Object(o)
		// from Order o INNER JOIN o.customer cust
		// where cust.name = ?1

		ExpressionTester selectStatement = selectStatement(
			select(object("o")),
			from("Order", "o", innerJoin("o.customer", "cust")),
			where(equal(path("cust.name"), inputParameter("?1")))
		);

		testQuery(query_092(), selectStatement);
	}

	@Test
	public void test_Query_093() {

		// SELECT DISTINCT object(c)
		// from Customer c INNER JOIN c.creditCards cc
		// where cc.type='VISA'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c", innerJoin("c.creditCards", "cc")),
			where(equal(path("cc.type"), string("'VISA'")))
		);

		testQuery(query_093(), selectStatement);
	}

	@Test
	public void test_Query_094() {

		// SELECT c
		// from Customer c INNER JOIN c.spouse s

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c", innerJoin("c.spouse", "s"))
		);

		testQuery(query_094(), selectStatement);
	}

	@Test
	public void test_Query_095() {

		// select cc.type
		// FROM CreditCard cc JOIN cc.customer cust
		// GROUP BY cc.type

		ExpressionTester selectStatement = selectStatement(
			select(path("cc.type")),
			from("CreditCard", "cc", join("cc.customer", "cust")),
			nullExpression(),
			groupBy(path("cc.type")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_095(), selectStatement);
	}

	@Test
	public void test_Query_096() {

		// select cc.code
		// FROM Customer c JOIN c.country cc
		// GROUP BY cc.code

		ExpressionTester selectStatement = selectStatement(
			select(path("cc.code")),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.code")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_096(), selectStatement);
	}

	@Test
	public void test_Query_097() {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where LOWER(a.alias)='sjc'

		ExpressionTester selectStatement = selectStatement(
			select(object("c")),
			from("Customer", "c", join("c.aliases", "a")),
			where(equal(lower(path("a.alias")), string("'sjc'"))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_097(), selectStatement);
	}

	@Test
	public void test_Query_098() {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where UPPER(a.alias)='SJC'

		ExpressionTester selectStatement = selectStatement(
			select(object("c")),
			from("Customer", "c", join("c.aliases", "a")),
			where(equal(upper(path("a.alias")), string("'SJC'"))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_098(), selectStatement);
	}

	@Test
	public void test_Query_099() {

		// SELECT c.id, a.alias
		// from Customer c LEFT OUTER JOIN c.aliases a
		// where c.name LIKE 'Ste%'
		// ORDER BY a.alias, c.id

		ExpressionTester selectStatement = selectStatement(
			select(path("c.id"), path("a.alias")),
			from("Customer", "c", leftOuterJoin("c.aliases", "a")),
			where(path("c.name").like(string("'Ste%'"))),
			nullExpression(),
			nullExpression(),
			orderBy(
				orderByItem(path("a.alias")),
				orderByItem(path("c.id"))
			)
		);

		testQuery(query_099(), selectStatement);
	}

	@Test
	public void test_Query_100() {

		// SELECT o.id, cust.id
		// from Order o LEFT OUTER JOIN o.customer cust
		// where cust.name=?1
		// ORDER BY o.id

		ExpressionTester selectStatement = selectStatement(
			select(path("o.id"), path("cust.id")),
			from("Order", "o", leftOuterJoin("o.customer", "cust")),
			where(path("cust.name").equal(inputParameter("?1"))),
			nullExpression(),
			nullExpression(),
			orderBy("o.id")
		);

		testQuery(query_100(), selectStatement);
	}

	@Test
	public void test_Query_101() {

		// SELECT DISTINCT c
		// from Customer c LEFT OUTER JOIN c.creditCards cc
		// where c.name LIKE '%Caruso'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", leftOuterJoin("c.creditCards", "cc")),
			where(path("c.name").like(string("'%Caruso'"))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_101(), selectStatement);
	}

	@Test
	public void test_Query_102() {

		// SELECT Sum(p.quantity)
		// FROM Product p

		ExpressionTester selectStatement = selectStatement(
			select(sum("p.quantity")),
			from("Product", "p")
		);

		testQuery(query_102(), selectStatement);
	}

	@Test
	public void test_Query_103() {

		// Select Count(c.home.city)
		// from Customer c

		ExpressionTester selectStatement = selectStatement(
			select(count("c.home.city")),
			from("Customer", "c")
		);

		testQuery(query_103(), selectStatement);
	}

	@Test
	public void test_Query_104() {

		// SELECT Sum(p.price)
		// FROM Product p

		ExpressionTester selectStatement = selectStatement(
			select(sum("p.price")),
			from("Product", "p")
		);

		testQuery(query_104(), selectStatement);
	}

	@Test
	public void test_Query_105() {

		// SELECT AVG(o.totalPrice)
		// FROM Order o

		ExpressionTester selectStatement = selectStatement(
			select(avg("o.totalPrice")),
			from("Order", "o")
		);

		testQuery(query_105(), selectStatement);
	}

	@Test
	public void test_Query_106() {

		// SELECT DISTINCT MAX(l.quantity)
		// FROM LineItem l

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(max("l.quantity")),
			from("LineItem", "l")
		);

		testQuery(query_106(), selectStatement);
	}

	@Test
	public void test_Query_107() {

		// SELECT DISTINCT MIN(o.id)
		// FROM Order o
		// where o.customer.name = 'Robert E. Bissett'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(min("o.id")),
			from("Order", "o"),
			where(path("o.customer.name").equal(string("'Robert E. Bissett'")))
		);

		testQuery(query_107(), selectStatement);
	}

	@Test
	public void test_Query_108() {

		// SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name)
		// FROM Customer c
		// where c.work.city = :workcity

		ExpressionTester selectStatement = selectStatement(
			select(new_(
				"com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer",
				path("c.id"),
				path("c.name")
			)),
			from("Customer", "c"),
			where(path("c.work.city").equal(inputParameter(":workcity")))
		);

		testQuery(query_108(), selectStatement);
	}

	@Test
	public void test_Query_109() {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) > 100

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(size("c.orders").greaterThan(numeric(100)))
		);

		testQuery(query_109(), selectStatement);
	}

	@Test
	public void test_Query_110() {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) >= 2

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(size("c.orders").greaterThanOrEqual(numeric(2)))
		);

		testQuery(query_110(), selectStatement);
	}

	@Test
	public void test_Query_111() {

		// select Distinct c
		// FROM Customer c LEFT OUTER JOIN c.work workAddress
		// where workAddress.zip IS NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", leftOuterJoin("c.work", "workAddress")),
			where(isNull(path("workAddress.zip")))
		);

		testQuery(query_111(), selectStatement);
	}

	@Test
	public void test_Query_112() {

		// SELECT DISTINCT c
		// FROM Customer c, IN(c.orders) o

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from(
				fromEntity("Customer", "c"),
				fromIn("c.orders", "o"))
		);

		testQuery(query_112(), selectStatement);
	}

	@Test
	public void test_Query_113() {

		// Select Distinct Object(c)
		// from Customer c
		// where c.name is null

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNull(path("c.name")))
		);

		testQuery(query_113(), selectStatement);
	}

	@Test
	public void test_Query_114() {

		// Select c.name
		// from Customer c
		// where c.home.street = '212 Edgewood Drive'

		ExpressionTester selectStatement = selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			where(path("c.home.street").equal(string("'212 Edgewood Drive'")))
		);

		testQuery(query_114(), selectStatement);
	}

	@Test
	public void test_Query_115() {

		// Select s.customer
		// from Spouse s
		// where s.id = '6'

		ExpressionTester selectStatement = selectStatement(
			select(path("s.customer")),
			from("Spouse", "s"),
			where(path("s.id").equal(string("'6'")))
		);

		testQuery(query_115(), selectStatement);
	}

	@Test
	public void test_Query_116() {

		// Select c.work.zip
		// from Customer c

		ExpressionTester selectStatement = selectStatement(
			select(path("c.work.zip")),
			from("Customer", "c")
		);

		testQuery(query_116(), selectStatement);
	}

	@Test
	public void test_Query_117() {

		// SELECT Distinct Object(c)
		// From Customer c, IN(c.home.phones) p
		// where p.area LIKE :area

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(
				fromEntity("Customer", "c"),
				fromIn("c.home.phones", "p")),
			where(
					path("p.area")
				.like(
					inputParameter(":area")
				)
			)
		);

		testQuery(query_117(), selectStatement);
	}

	@Test
	public void test_Query_118() {

		// SELECT DISTINCT Object(c)
		// from Customer c, in(c.aliases) a
		// where NOT a.customerNoop IS NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(not(isNull(path("a.customerNoop"))))
		);

		testQuery(query_118(), selectStatement);
	}

	@Test
	public void test_Query_119() {

		// select distinct object(c)
		// fRoM Customer c, IN(c.aliases) a
		// where c.name = :cName OR a.customerNoop IS NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("c.name").equal(inputParameter(":cName")).or(isNull(path("a.customerNoop"))))
		);

		testQuery(query_119(), selectStatement);
	}

	@Test
	public void test_Query_120() {

		// select Distinct Object(c)
		// from Customer c, in(c.aliases) a
		// where c.name = :cName AND a.customerNoop IS NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("c.name").equal(inputParameter(":cName")).and(isNull(path("a.customerNoop"))))
		);

		testQuery(query_120(), selectStatement);
	}

	@Test
	public void test_Query_121() {

		// sElEcT Distinct oBJeCt(c)
		// FROM Customer c, IN(c.aliases) a
		// WHERE a.customerNoop IS NOT NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(isNotNull(path("a.customerNoop")))
		);

		testQuery(query_121(), selectStatement);
	}

	@Test
	public void test_Query_122() {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE '%\\_%' escape '\\'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("a.alias").like(string("'%\\_%'"), string('\\')))
		);

		testQuery(query_122(), selectStatement);
	}

	@Test
	public void test_Query_123() {

		// Select Distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.customerNoop IS NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(isNull(path("a.customerNoop")))
		);

		testQuery(query_123(), selectStatement);
	}

	@Test
	public void test_Query_124() {

		// Select Distinct o.creditCard.balance
		// from Order o
		// ORDER BY o.creditCard.balance ASC

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(path("o.creditCard.balance")),
			from("Order", "o"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemAsc("o.creditCard.balance"))
		);

		testQuery(query_124(), selectStatement);
	}

	@Test
	public void test_Query_125() {

		// Select c.work.zip
		// from Customer c
		// where c.work.zip IS NOT NULL
		// ORDER BY c.work.zip ASC

		ExpressionTester selectStatement = selectStatement(
			select(path("c.work.zip")),
			from("Customer", "c"),
			where(isNotNull(path("c.work.zip"))),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemAsc("c.work.zip"))
		);

		testQuery(query_125(), selectStatement);
	}

	@Test
	public void test_Query_126() {

		// SELECT a.alias
		// FROM Alias AS a
		// WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1

		ExpressionTester orExpression =
				sub(
						isNull(path("a.alias"))
					.and(
						isNull(inputParameter(":param1"))))
			.or(
				path("a.alias").equal(inputParameter(":param1")));

		ExpressionTester selectStatement = selectStatement(
			select(path("a.alias")),
			fromAs("Alias", "a"),
			where(orExpression)
		);

		testQuery(query_126(), selectStatement);
	}

	@Test
	public void test_Query_127() {

		// Select Object(c)
		// from Customer c
		// where c.aliasesNoop IS NOT EMPTY or c.id <> '1'

		ExpressionTester orExpression =
				isNotEmpty("c.aliasesNoop")
			.or(
				path("c.id").different(string("'1'")));

		ExpressionTester selectStatement = selectStatement(
			select(object("c")),
			from("Customer", "c"),
			where(orExpression)
		);

		testQuery(query_127(), selectStatement);
	}

	@Test
	public void test_Query_128() {

		// Select Distinct Object(p)
		// from Product p
		// where p.name = ?1

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(path("p.name").equal(inputParameter("?1")))
		);

		testQuery(query_128(), selectStatement);
	}

	@Test
	public void test_Query_129() {

		// Select Distinct Object(p)
		// from Product p
		// where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)

		ExpressionTester addition =
				sub(numeric(500)
			.add(
				inputParameter(":int1")));

		ExpressionTester andExpression =
				sub(path("p.quantity").greaterThan(addition))
			.and(
				sub(isNull(path("p.partNumber"))));

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(andExpression)
		);

		testQuery(query_129(), selectStatement);
	}

	@Test
	public void test_Query_130() {

		// Select Distinct Object(o)
		// from Order o
		// where o.customer.name IS NOT NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(isNotNull(path("o.customer.name")))
		);

		testQuery(query_130(), selectStatement);
	}

	@Test
	public void test_Query_131() {

		// Select DISTINCT Object(p)
		// From Product p
		// where (p.quantity < 10) OR (p.quantity > 20)

		ExpressionTester orExpression =
				sub(path("p.quantity").lowerThan(numeric(10)))
			.or(
				sub(path("p.quantity").greaterThan(numeric(20))));

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(orExpression)
		);

		testQuery(query_131(), selectStatement);
	}

	@Test
	public void test_Query_132() {

		// Select DISTINCT Object(p)
		// From Product p
		// where p.quantity NOT BETWEEN 10 AND 20

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(path("p.quantity").notBetween(numeric(10), numeric(20)))
		);

		testQuery(query_132(), selectStatement);
	}

	@Test
	public void test_Query_133() {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where (p.quantity >= 10) AND (p.quantity <= 20)

		ExpressionTester andExpression =
				sub(path("p.quantity").greaterThanOrEqual(numeric(10)))
			.and(
				sub(path("p.quantity").lowerThanOrEqual(numeric(20))));

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(andExpression)
		);

		testQuery(query_133(), selectStatement);
	}

	@Test
	public void test_Query_134() {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where p.quantity BETWEEN 10 AND 20

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(path("p.quantity").between(numeric(10), numeric(20)))
		);

		testQuery(query_134(), selectStatement);
	}

	@Test
	public void test_Query_135() {

		// Select Distinct OBJECT(c)
		// from Customer c, IN(c.creditCards) b
		// where SQRT(b.balance) = :dbl

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.creditCards", "b")),
			where(sqrt(path("b.balance")).equal(inputParameter(":dbl")))
		);

		testQuery(query_135(), selectStatement);
	}

	@Test
	public void test_Query_136() {

		// Select Distinct OBJECT(c)
		// From Product p
		// where MOD(550, 100) = p.quantity

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Product", "p"),
			where(mod(numeric(550), numeric(100)).equal(path("p.quantity")))
		);

		testQuery(query_136(), selectStatement);
	}

	@Test
	public void test_Query_137() {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')

		ExpressionTester orExpression =
				sub(path("c.home.state").equal(string("'NH'")))
			.or(
				sub(path("c.home.state").equal(string("'RI'"))));

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(orExpression)
		);

		testQuery(query_137(), selectStatement);
	}

	@Test
	public void test_Query_138() {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// where c.home.state IN('NH', 'RI')

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(path("c.home.state").in(string("'NH'"), string("'RI'")))
		);

		testQuery(query_138(), selectStatement);
	}

	@Test
	public void test_Query_140() {

		// SELECT c
		// from Customer c
		// where c.home.city IN(:city)

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(path("c.home.city").in(inputParameter(":city")))
		);

		testQuery(query_140(), selectStatement);
	}

	@Test
	public void test_Query_141() {

		// Select Distinct Object(o)
		// from Order o, in(o.lineItems) l
		// where l.quantity NOT IN (1, 5)

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(path("l.quantity").notIn(numeric(1), numeric(5)))
		);

		testQuery(query_141(), selectStatement);
	}

	@Test
	public void test_Query_142() {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE o.sampleLineItem MEMBER OF o.lineItems

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.sampleLineItem").memberOf(collectionPath("o.lineItems")))
		);

		testQuery(query_142(), selectStatement);
	}

	@Test
	public void test_Query_143() {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE :param NOT MEMBER o.lineItems

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(inputParameter(":param").notMember(collectionPath("o.lineItems")))
		);

		testQuery(query_143(), selectStatement);
	}

	@Test
	public void test_Query_144() {

		// Select Distinct Object(o)
		// FROM Order o, LineItem l
		// WHERE l MEMBER o.lineItems

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o", "LineItem", "l"),
			where(variable("l").member(collectionPath("o.lineItems")))
		);

		testQuery(query_144(), selectStatement);
	}

	@Test
	public void test_Query_145() {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE 'sh\\_ll' escape '\\'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("a.alias").like(string("'sh\\_ll'"), string('\\')))
		);

		testQuery(query_145(), selectStatement);
	}

	@Test
	public void test_Query_146() {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop NOT MEMBER OF a.customersNoop

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(path("a.customerNoop").notMemberOf(collectionPath("a.customersNoop")))
		);

		testQuery(query_146(), selectStatement);
	}

	@Test
	public void test_Query_147() {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop MEMBER OF a.customersNoop

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(path("a.customerNoop").memberOf(collectionPath("a.customersNoop")))
		);

		testQuery(query_147(), selectStatement);
	}

	@Test
	public void test_Query_148() {

		// Select Distinct Object(a)
		// from Alias a
		// where LOCATE('ev', a.alias) = 3

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(locate(string("'ev'"), path("a.alias")).equal(numeric(3)))
		);

		testQuery(query_148(), selectStatement);
	}

	@Test
	public void test_Query_149() {

		// Select DISTINCT Object(o)
		// From Order o
		// WHERE o.totalPrice > ABS(:dbl)

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.totalPrice").greaterThan(abs(inputParameter(":dbl"))))
		);

		testQuery(query_149(), selectStatement);
	}

	@Test
	public void test_Query_150() {

		// Select Distinct OBjeCt(a)
		// From Alias a
		// WHERE LENGTH(a.alias) > 4

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(length(path("a.alias")).greaterThan(numeric(4)))
		);

		testQuery(query_150(), selectStatement);
	}

	@Test
	public void test_Query_151() {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = SUBSTRING(:string1, :int2, :int3)

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(
					path("a.alias")
				.equal(
					substring(inputParameter(":string1"), inputParameter(":int2"), inputParameter(":int3"))))
		);

		testQuery(query_151(), selectStatement);
	}

	@Test
	public void test_Query_152() {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = CONCAT('ste', 'vie')

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(
					path("a.alias")
				.equal(
					concat(string("'ste'"), string("'vie'"))))
		);

		testQuery(query_152(), selectStatement);
	}

	@Test
	public void test_Query_153() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.work.zip IS NOT NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNotNull(path("c.work.zip")))
		);

		testQuery(query_153(), selectStatement);
	}

	@Test
	public void test_Query_154() {

		// sELEct dIsTiNcT oBjEcT(c)
		// FROM Customer c
		// WHERE c.work.zip IS NULL

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNull(path("c.work.zip")))
		);

		testQuery(query_154(), selectStatement);
	}

	@Test
	public void test_Query_155() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS NOT EMPTY

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNotEmpty("c.aliases"))
		);

		testQuery(query_155(), selectStatement);
	}

	@Test
	public void test_Query_156() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS EMPTY

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isEmpty("c.aliases"))
		);

		testQuery(query_156(), selectStatement);
	}

	@Test
	public void test_Query_157() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip not like '%44_'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(path("c.home.zip").notLike(string("'%44_'")))
		);

		testQuery(query_157(), selectStatement);
	}

	@Test
	public void test_Query_158() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip LIKE '%77'"

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(path("c.home.zip").like(string("'%77'")))
		);

		testQuery(query_158(), selectStatement);
	}

	@Test
	public void test_Query_159() {

		// Select Distinct Object(c)
		// FROM Customer c Left Outer Join c.home h
		// WHERE h.city Not iN ('Swansea', 'Brookline')

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c", leftOuterJoin("c.home", "h")),
			where(path("h.city").notIn(string("'Swansea'"), string("'Brookline'")))
		);

		testQuery(query_159(), selectStatement);
	}

	@Test
	public void test_Query_160() {

		// select distinct c
		// FROM Customer c
		// WHERE c.home.city IN ('Lexington')

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(path("c.home.city").in(string("'Lexington'")))
		);

		testQuery(query_160(), selectStatement);
	}

	@Test
	public void test_Query_161() {

		// sElEcT c
		// FROM Customer c
		// Where c.name = :cName

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(path("c.name").equal(inputParameter(":cName")))
		);

		testQuery(query_161(), selectStatement);
	}

	@Test
	public void test_Query_162() {

		// select distinct Object(o)
		// From Order o
		// WHERE o.creditCard.approved = FALSE

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.creditCard.approved").equal(FALSE()))
		);

		testQuery(query_162(), selectStatement);
	}

	@Test
	public void test_Query_163() {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice NOT bETwEeN 1000 AND 1200

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.totalPrice").notBetween(numeric(1000), numeric(1200)))
		);

		testQuery(query_163(), selectStatement);
	}

	@Test
	public void test_Query_164() {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice BETWEEN 1000 AND 1200

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.totalPrice").between(numeric(1000), numeric(1200)))
		);

		testQuery(query_164(), selectStatement);
	}

	@Test
	public void test_Query_165() {

		// SELECT DISTINCT Object(o)
		// FROM Order o, in(o.lineItems) l
		// WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(	path("l.quantity")
					.lowerThan(
						numeric(2))
				.and(
						path("o.customer.name")
					.equal(
						string("'Robert E. Bissett'"))))
		);

		testQuery(query_165(), selectStatement);
	}

	@Test
	public void test_Query_166() {

		// select distinct Object(o)
		// FROM Order AS o, in(o.lineItems) l
		// WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from(fromEntityAs("Order", "o"), fromIn("o.lineItems", "l")),
			where(
					sub(path("l.quantity").lowerThan(numeric(2)))
				.and(
					sub(
						sub(
								path("o.totalPrice")
							.lowerThan(
								sub(
										numeric(3)
									.add(
											numeric(54).multiply(numeric(2))
										.add(
											numeric(-8)
										)
									)
								)
							)
						)
						.or(
							sub(
								path("o.customer.name").equal(string("'Robert E. Bissett'"))
							)
						)
					)
				)
			)
		);

		testQuery(query_166(), selectStatement);
	}

	@Test
	public void test_Query_167() {

		// SeLeCt DiStInCt oBjEcT(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			fromAs("Order", "o"),
			where(
					path("o.customer.name").equal(string("'Karen R. Tegan'"))
				.or(
					path("o.totalPrice").lowerThan(numeric(100))
				)
			)
		);

		testQuery(query_167(), selectStatement);
	}

	@Test
	public void test_Query_168() {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE NOT o.totalPrice < 4500

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(not(path("o.totalPrice").lowerThan(numeric(4500))))
		);

		testQuery(query_168(), selectStatement);
	}

	@Test
	public void test_Query_169() {

		// Select DISTINCT Object(P)
		// From Product p

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("P")),
			from("Product", "p")
		);

		testQuery(query_169(), selectStatement);
	}

	@Test
	public void test_Query_170() {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(
					path("c.home.street").equal(inputParameter(":street"))
				.or(
					path("c.home.city").equal(inputParameter(":city"))
				)
				.or(
					path("c.home.state").equal(inputParameter(":state"))
				)
				.or(
					path("c.home.zip").equal(inputParameter(":zip"))
				)
			)
		);

		testQuery(query_170(), selectStatement);
	}

	@Test
	public void test_Query_171() {

		// SELECT c
		// from Customer c
		// WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.home.street").equal(inputParameter(":street"))
				.and(
					path("c.home.city").equal(inputParameter(":city"))
				)
				.and(
					path("c.home.state").equal(inputParameter(":state"))
				)
				.and(
					path("c.home.zip").equal(inputParameter(":zip"))
				)
			)
		);

		testQuery(query_172(), selectStatement);
	}

	@Test
	public void test_Query_172() {

		// SELECT c
		// from Customer c
      // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.home.street").equal(inputParameter(":street"))
				.and(
					path("c.home.city").equal(inputParameter(":city"))
				)
				.and(
					path("c.home.state").equal(inputParameter(":state"))
				)
				.and(
					path("c.home.zip").equal(inputParameter(":zip"))
				)
			)
		);

		testQuery(query_172(), selectStatement);
	}

	@Test
	public void test_Query_173() {

		// Select Distinct Object(c)
		// FrOm Customer c, In(c.aliases) a
		// WHERE a.alias = :aName

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("a.alias").equal(inputParameter(":aName")))
		);

		testQuery(query_173(), selectStatement);
	}

	@Test
	public void test_Query_174() {

		// Select Distinct Object(c)
		// FROM Customer AS c

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(object("c")),
			fromAs("Customer", "c")
		);

		testQuery(query_174(), selectStatement);
	}

	@Test
	public void test_Query_175() {

		// Select Distinct o
		// from Order AS o
		// WHERE o.customer.name = :name

		ExpressionTester selectStatement = selectStatement(
			selectDistinct(variable("o")),
			fromAs("Order", "o"),
			where(path("o.customer.name").equal(inputParameter(":name")))
		);

		testQuery(query_175(), selectStatement);
	}

	@Test
	public void test_Query_176() {

		// UPDATE Customer c SET c.name = 'CHANGED'
		// WHERE c.orders IS NOT EMPTY

		ExpressionTester updateStatement = updateStatement(
			update("Customer", "c", set(path("c.name"), string("'CHANGED'"))),
			where(isNotEmpty("c.orders"))
		);

		testQuery(query_176(), updateStatement);
	}

	@Test
	public void test_Query_177() {

		// UPDATE DateTime SET date = CURRENT_DATE

		ExpressionTester updateStatement = updateStatement(
			update("DateTime", set("{datetime}.date", CURRENT_DATE()))
		);

		testQuery(query_177(), updateStatement);
	}

	@Test
	public void test_Query_178() {

		// SELECT c
		// FROM Customer c
		// WHERE c.firstName = :first AND
		//     c.lastName = :last

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.firstName").equal(inputParameter(":first"))
				.and(
					path("c.lastName").equal(inputParameter(":last"))
				)
			)
		);

		testQuery(query_178(), selectStatement);
	}

	@Test
	public void test_Query_179() {

		// SELECT OBJECT ( c ) FROM Customer AS c

		ExpressionTester selectStatement = selectStatement(
			select(object("c")),
			fromAs("Customer", "c")
		);

		testQuery(query_179(), selectStatement);
	}

	@Test
	public void test_Query_180() {

		// SELECT c.firstName, c.lastName
		// FROM Customer AS c

		ExpressionTester selectStatement = selectStatement(
			select(path("c.firstName"), path("c.lastName")),
			fromAs("Customer", "c")
		);

		testQuery(query_180(), selectStatement);
	}

	@Test
	public void test_Query_181() {

		// SELECT c.address.city
		// FROM Customer AS c

		ExpressionTester selectStatement = selectStatement(
			select(path("c.address.city")),
			fromAs("Customer", "c")
		);

		testQuery(query_181(), selectStatement);
	}

	@Test
	public void test_Query_182() {

		// SELECT new com.titan.domain.Name(c.firstName, c.lastName)
		// FROM Customer c

		ExpressionTester selectStatement = selectStatement(
			select(new_("com.titan.domain.Name", path("c.firstName"), path("c.lastName"))),
			from("Customer", "c")
		);

		testQuery(query_182(), selectStatement);
	}

	@Test
	public void test_Query_183() {

		// SELECT cbn.ship
		// FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn

		ExpressionTester selectStatement = selectStatement(
			select(path("cbn.ship")),
			from(
				fromEntityAs("Customer", "c"),
				fromIn("c.reservations", "r"),
				fromIn("r.cabins", "cbn")
			)
		);

		testQuery(query_183(), selectStatement);
	}

	@Test
	public void test_Query_184() {

		// Select c.firstName, c.lastName, p.number
		// From Customer c Left Join c.phoneNumbers p

		ExpressionTester selectStatement = selectStatement(
			select(path("c.firstName"), path("c.lastName"), path("p.number")),
			from("Customer", "c", leftJoin("c.phoneNumbers", "p"))
		);

		testQuery(query_184(), selectStatement);
	}

	@Test
	public void test_Query_185() {

		// SELECT r
		// FROM Reservation AS r
		// WHERE (r.amountPaid * .01) > 300.00

		ExpressionTester selectStatement = selectStatement(
			select(variable("r")),
			fromAs("Reservation", "r"),
			where(
					sub(path("r.amountPaid").multiply(numeric(".01")))
				.greaterThan(
					numeric("300.00")
				)
			)
		);

		testQuery(query_185(), selectStatement);
	}

	@Test
	public void test_Query_186() {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00

		ExpressionTester selectStatement = selectStatement(
			select(variable("s")),
			fromAs("Ship", "s"),
			where(
					path("s.tonnage").greaterThanOrEqual(numeric("80000.00"))
				.and(
					path("s.tonnage").lowerThanOrEqual(numeric("130000.00"))
				)
			)
		);

		testQuery(query_186(), selectStatement);
	}

	@Test
	public void test_Query_187() {

		// SELECT r
		// FROM Reservation r, IN ( r.customers ) AS cust
		// WHERE cust = :specificCustomer

		ExpressionTester selectStatement = selectStatement(
			select(variable("r")),
			from(fromEntity("Reservation", "r"), fromInAs("r.customers", "cust")),
			where(variable("cust").equal(inputParameter(":specificCustomer")))
		);

		testQuery(query_187(), selectStatement);
	}

	@Test
	public void test_Query_188() {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage BETWEEN 80000.00 AND 130000.00

		ExpressionTester selectStatement = selectStatement(
			select(variable("s")),
			fromAs("Ship", "s"),
			where(path("s.tonnage").between(numeric("80000.00"), numeric("130000.00")))
		);

		testQuery(query_188(), selectStatement);
	}

	@Test
	public void test_Query_189() {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00

		ExpressionTester selectStatement = selectStatement(
			select(variable("s")),
			fromAs("Ship", "s"),
			where(path("s.tonnage").notBetween(numeric("80000.00"), numeric("130000.00")))
		);

		testQuery(query_189(), selectStatement);
	}

	@Test
	public void test_Query_190() {

		// SELECT c
		// FROM Customer AS c
		//  WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			fromAs("Customer", "c"),
			where(path("c.address.state").in(string("'FL'"), string("'TX'"), string("'MI'"), string("'WI'"), string("'MN'")))
		);

		testQuery(query_190(), selectStatement);
	}

	@Test
	public void test_Query_191() {

		// SELECT cab
		// FROM Cabin AS cab
		// WHERE cab.deckLevel IN (1,3,5,7)

		ExpressionTester selectStatement = selectStatement(
			select(variable("cab")),
			fromAs("Cabin", "cab"),
			where(
					path("cab.deckLevel")
				.in(
					numeric(1),
					numeric(3),
					numeric(5),
					numeric(7)
				)
			)
		);

		testQuery(query_191(), selectStatement);
	}

	@Test
	public void test_Query_192() {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.address.state")
				.in(
					inputParameter("?1"),
					inputParameter("?2"),
					inputParameter("?3"),
					string("'WI'"),
					string("'MN'")
				)
			)
		);

		testQuery(query_192(), selectStatement);
	}

	@Test
	public void test_Query_193() {

		// SELECT c
		// FROM Customer c
		// WHERE c.address IS NULL

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(isNull(path("c.address")))
		);

		testQuery(query_193(), selectStatement);
	}

	@Test
	public void test_Query_194() {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state = 'TX' AND
		//       c.lastName = 'Smith' AND
		//       c.firstName = 'John'

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.address.state").equal(string("'TX'"))
				.and(
					path("c.lastName").equal(string("'Smith'"))
				)
				.and(
					path("c.firstName").equal(string("'John'"))
				)
			)
		);

		testQuery(query_194(), selectStatement);
	}

	@Test
	public void test_Query_195() {

		// SELECT crs
		// FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust
		// WHERE
		//  cust = :myCustomer
		//  AND
		//  cust MEMBER OF res.customers

		ExpressionTester selectStatement = selectStatement(
			select(variable("crs")),
			from(
				fromEntityAs("Cruise", "crs"),
				fromInAs("crs.reservations", "res"),
				fromEntityAs("Customer", "cust")
			),
			where(
					variable("cust").equal(inputParameter(":myCustomer"))
				.and(
					variable("cust").memberOf(collectionPath("res.customers"))
				)
			)
		);

		testQuery(query_195(), selectStatement);
	}

	@Test
	public void test_Query_196() {

		// SELECT c
		// FROM Customer AS c
		// WHERE    LENGTH(c.lastName) > 6
		//       AND
		//          LOCATE( c.lastName, 'Monson' ) > -1

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			fromAs("Customer", "c"),
			where(
					length(path("c.lastName")).greaterThan(numeric(6))
				.and(
						locate(path("c.lastName"), string("'Monson'"))
					.greaterThan(
						numeric(-1)
					)
				)
			)
		);

		testQuery(query_196(), selectStatement);
	}

	@Test
	public void test_Query_197() {

		// SELECT c
		// FROM Customer AS C
		// ORDER BY c.lastName

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			fromAs("Customer", "C"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy("c.lastName")
		);

		testQuery(query_197(), selectStatement);
	}

	@Test
	public void test_Query_198() {

		// SELECT c
		// FROM Customer AS C
      // WHERE c.address.city = 'Boston' AND c.address.state = 'MA'
		// ORDER BY c.lastName DESC

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			fromAs("Customer", "C"),
			where(
					path("c.address.city").equal(string("'Boston'"))
				.and(
					path("c.address.state").equal(string("'MA'"))
				)
			),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc("c.lastName"))
		);

		testQuery(query_198(), selectStatement);
	}

	@Test
	public void test_Query_199() {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name

		ExpressionTester selectStatement = selectStatement(
			select(path("cr.name"), count(variable("res"))),
			from("Cruise", "cr", leftJoin("cr.reservations", "res")),
			nullExpression(),
			groupBy(path("cr.name")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query_199(), selectStatement);
	}

	@Test
	public void test_Query_200() {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name
		// HAVING count(res) > 10

		ExpressionTester selectStatement = selectStatement(
			select(path("cr.name"), count(variable("res"))),
			from("Cruise", "cr", leftJoin("cr.reservations", "res")),
			nullExpression(),
			groupBy(path("cr.name")),
			having(count(variable("res")).greaterThan(numeric(10))),
			nullExpression()
		);

		testQuery(query_200(), selectStatement);
	}

	@Test
	public void test_Query_201() {

		// SELECT COUNT (res)
		// FROM Reservation res
		// WHERE res.amountPaid >
		//       (SELECT avg(r.amountPaid) FROM Reservation r)

		ExpressionTester selectStatement = selectStatement(
			select(count(variable("res"))),
			from("Reservation", "res"),
			where(
					path("res.amountPaid")
				.greaterThan(
					sub(
						subquery(
							subSelect(avg("r.amountPaid")),
							subFrom("Reservation", "r")
						)
					)
				)
			)
		);

		testQuery(query_201(), selectStatement);
	}

	@Test
	public void test_Query_202() {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 100000 < (
		//    SELECT SUM(res.amountPaid) FROM cr.reservations res
		// )

		ExpressionTester selectStatement = selectStatement(
			select(variable("cr")),
			from("Cruise", "cr"),
			where(
					numeric(100000)
				.lowerThan(
					sub(
						subquery(
							subSelect(sum("res.amountPaid")),
							subFrom(fromCollection("cr.reservations", "res"))
						)
					)
				)
			)
		);

		testQuery(query_202(), selectStatement);
	}

	@Test
	public void test_Query_203() {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 0 < ALL (
		//   SELECT res.amountPaid from cr.reservations res
		// )

		ExpressionTester selectStatement = selectStatement(
			select(variable("cr")),
			from("Cruise", "cr"),
			where(
					numeric(0)
				.lowerThan(
					all(
						subquery(
							subSelect(path("res.amountPaid")),
							subFrom(fromCollection("cr.reservations", "res"))
						)
					)
				)
			)
		);

		testQuery(query_203(), selectStatement);
	}

	@Test
	public void test_Query_204() {

		// UPDATE Reservation res
		// SET res.name = 'Pascal'
		// WHERE EXISTS (
		//    SELECT c
		//    FROM res.customers c
		//    WHERE c.firstName = 'Bill' AND c.lastName='Burke'
		// )

		ExpressionTester updateStatement = updateStatement(
			update("Reservation", "res", set(path("res.name"), string("'Pascal'"))),
			where(
				exists(
					subquery(
						subSelect(variable("c")),
						subFrom(fromCollection("res.customers", "c")),
						where(
								path("c.firstName").equal(string("'Bill'"))
							.and(
								path("c.lastName").equal(string("'Burke'"))
							)
						)
					)
				)
			)
		);

		testQuery(query_204(), updateStatement);
	}

	@Test
	public void test_Query_215() {

		// SELECT o
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity DESC, o.totalcost

		ExpressionTester selectStatement = selectStatement(
			select(variable("o")),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(path("a.state").equal(string("'CA'"))),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc("o.quantity"), orderByItem("o.totalcost"))
		);

		testQuery(query_215(), selectStatement);
	}

	@Test
	public void test_Query_216() {

		// SELECT o.quantity, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity, a.zipcode

		ExpressionTester selectStatement = selectStatement(
			select(path("o.quantity"), path("a.zipcode")),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(path("a.state").equal(string("'CA'"))),
			nullExpression(),
			nullExpression(),
			orderBy(
				orderByItem("o.quantity"),
				orderByItem("a.zipcode")
			)
		);

		testQuery(query_216(), selectStatement);
	}

	@Test
	public void test_Query_219() {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'

		ExpressionTester deleteStatement = deleteStatement(
			"Customer", "c",
			where(path("c.status").equal(string("'inactive'")))
		);

		testQuery(query_219(), deleteStatement);
	}

	@Test
	public void test_Query_220() {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'
		//       AND
		//       c.orders IS EMPTY

		ExpressionTester deleteStatement = deleteStatement(
			"Customer", "c",
			where(
					path("c.status").equal(string("'inactive'"))
				.and(
					isEmpty("c.orders"))
			)
		);

		testQuery(query_220(), deleteStatement);
	}

	@Test
	public void test_Query_221() {

		// UPDATE customer c
		// SET c.status = 'outstanding'
		// WHERE c.balance < 10000

		ExpressionTester updateStatement = updateStatement(
			update("customer", "c", set("c.status", string("'outstanding'"))),
			where(path("c.balance").lowerThan(numeric(10000)))
		);

		testQuery(query_221(), updateStatement);
	}

	@Test
	public void test_Query_228() {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join("e.phoneNumbers", "p")),
			where(
					path("e.firstName").equal(string("'Bob'"))
				.and(
					path("e.lastName").like(string("'Smith%'"))
				)
				.and(
					path("e.address.city").equal(string("'Toronto'"))
				)
				.and(
					path("p.areaCode").different(string("'2'"))
				)
			)
		);

		testQuery(query_228(), selectStatement);
	}

	@Test
	public void test_Query_229() {

		// Select e
		// From Employee e
		// Where Exists(Select a From e.address a Where a.zipCode = 27519)

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(
				subquery(
					subSelect(variable("a")),
					subFrom(identificationVariableDeclaration(rangeVariableDeclaration(
						collectionPath("e.address"),
						variable("a")
					))),
					where(path("a.zipCode").equal(numeric(27519)))
				)
			))
		);

		testQuery(query_229(), selectStatement);
	}

	@Test
	public void test_Query_230() {

		// Select e
		// From Employee e
		// Where Exists(Where Exists(Select e.name From In e.phoneNumbers Where e.zipCode = 27519))

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(
				subquery(
					subSelect(path("e.name")),
					subFrom(subFromIn("e.phoneNumbers")),
					where(path("e.zipCode").equal(numeric(27519)))
				)
			))
		);

		testQuery(query_230(), selectStatement);
	}

	@Test
	public void test_Query_231() {

		// UPDATE Employee e SET e.salary = e.salary*(1+(:percent/100))
		// WHERE EXISTS (SELECT p
		//               FROM e.projects p
		//               WHERE p.name LIKE :projectName)

		ExpressionTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set(
					"e.salary",
						path("e.salary")
					.multiply(
						sub(
								numeric(1)
							.add(
								sub(inputParameter(":percent").divide(numeric(100))
								)
							)
						)
					)
				)
			),
			where(exists(
				subquery(
					subSelect(variable("p")),
					subFrom(
						identificationVariableDeclaration(
							rangeVariableDeclaration(collectionPath("e.projects"), variable("p"))
						)
					),
					where(path("p.name").like(inputParameter(":projectName")))
				)
			))
		);

		testQuery(query_231(), updateStatement, buildQueryFormatter_1());
	}
}