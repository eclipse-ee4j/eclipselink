/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.model;

/**
 * The abstract definition
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractStateObjectTest1_0 extends AbstractStateObjectTest {

	public static StateObjectTester stateObject_001() {

		// SELECT e FROM Employee e

		return selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);
	}

	public static StateObjectTester stateObject_002() {

		// SELECT e\nFROM Employee e

		return selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);
	}

	public static StateObjectTester stateObject_003() {

		// SELECT e
      // FROM Employee e
      // WHERE e.department.name = 'NA42' AND
      //       e.address.state IN ('NY', 'CA')

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					path("e.department.name").equal(string("'NA42'"))
				.and(
					path("e.address.state").in(string("'NY'"), string("'CA'"))
				)
			)
		);
	}

	public static StateObjectTester stateObject_004() {

		// SELECT p.number
		// FROM Employee e, Phone p
		// WHERE     e = p.employee
		//       AND e.department.name = 'NA42'
		//       AND p.type = 'Cell'

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_005() {

		// SELECT d, COUNT(e), MAX(e.salary), AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d
		// HAVING COUNT(e) >= 5

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_006() {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = ?1
		//       AND e.salary > ?2

		StateObjectTester andExpression =
				path("e.department").equal(inputParameter("?1"))
			.and(
				path("e.salary").greaterThan(inputParameter("?2")));

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_007() {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = :dept
		//       AND e.salary > :base

		StateObjectTester andExpression =
				path("e.department").equal(inputParameter(":dept"))
			.and(
				path("e.salary").greaterThan(inputParameter(":base")));

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_008() {

		// SELECT e
		// FROM Employee e
		// WHERE     e.department = 'NA65'
		//       AND e.name = 'UNKNOWN'' OR e.name = ''Roberts'

		StateObjectTester andExpression =
				path("e.department").equal(string("'NA65'"))
			.and(
				path("e.name").equal(string("'UNKNOWN'' OR e.name = ''Roberts'")));

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_009() {

		// SELECT e
		// FROM Employee e
		// WHERE e.startDate BETWEEN ?1 AND ?2

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.startDate").between(inputParameter("?1"), inputParameter("?2")))
		);
	}

	public static StateObjectTester stateObject_010() {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = :dept AND
		//      e.salary = (SELECT MAX(e.salary)
		//                  FROM Employee e
		//                  WHERE e.department = :dept)

		StateObjectTester subquery = subquery(
			subSelect(max("e.salary")),
			subFrom("Employee", "e"),
			where(path("e.department").equal(inputParameter(":dept")))
		);

		StateObjectTester andExpression =
				path("e.department").equal(inputParameter(":dept"))
			.and(
				path("e.salary").equal(sub(subquery)));

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_011() {

		// SELECT e
		// FROM Project p JOIN p.employees e
		// WHERE p.name = ?1
		// ORDER BY e.name

		return selectStatement(
			select(variable("e")),
			from("Project", "p", join("p.employees", "e")),
			where(path("p.name").equal(inputParameter("?1"))),
			nullExpression(),
			nullExpression(),
			orderBy("e.name")
		);
	}

	public static StateObjectTester stateObject_012() {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS EMPTY";

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(isEmpty("e.projects"))
		);
	}

	public static StateObjectTester stateObject_013() {

		// SELECT e
		// FROM Employee e
		// WHERE e.projects IS NOT EMPTY";

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(isNotEmpty("e.projects"))
		);
	}

	public static StateObjectTester stateObject_014() {

		// UPDATE Employee e
		// SET e.manager = ?1
		// WHERE e.department = ?2

		return updateStatement(
			update("Employee", "e", set("e.manager", inputParameter("?1"))),
			where(path("e.department").equal(inputParameter("?2")))
		);
	}

	public static StateObjectTester stateObject_015() {

		// DELETE FROM Project p
      // WHERE p.employees IS EMPTY

		return deleteStatement(
			"Project",
			"p",
			where(isEmpty("p.employees"))
		);
	}

	public static StateObjectTester stateObject_016() {

		// DELETE FROM Department d
		// WHERE d.name IN ('CA13', 'CA19', 'NY30')

		StateObjectTester inExpression = in(
			"d.name",
			string("'CA13'"),
			string("'CA19'"),
			string("'NY30'")
		);

		return deleteStatement(
			"Department",
			"d",
			where(inExpression)
		);
	}

	public static StateObjectTester stateObject_017() {

		// UPDATE Employee e
		// SET e.department = null
		// WHERE e.department.name IN ('CA13', 'CA19', 'NY30')

		return updateStatement(
			update("Employee", "e", set("e.department", NULL())),
			where(path("e.department.name").in(string("'CA13'"), string("'CA19'"), string("'NY30'")))
		);
	}

	public static StateObjectTester stateObject_018() {

		// SELECT d
		// FROM Department d
		// WHERE d.name LIKE 'QA\\_%' ESCAPE '\\'

		StateObjectTester likeExpression = like(
			path("d.name"),
			string("'QA\\_%'"),
			'\\'
		);

		return selectStatement(
			select(variable("d")),
			from("Department", "d"),
			where(likeExpression)
		);
	}

	public static StateObjectTester stateObject_019() {

		// SELECT e
		// FROM Employee e
		// WHERE e.salary = (SELECT MAX(e2.salary) FROM Employee e2)

		StateObjectTester subQuery = subquery(
			subSelect(max("e2.salary")),
			subFrom("Employee", "e2")
		);

		StateObjectTester comparisonExpression = equal(
			path("e.salary"),
			sub(subQuery)
		);

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_020() {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM Phone p WHERE p.employee = e AND p.type = 'Cell')

		StateObjectTester comparisonExpression1 = equal(
			path("p.employee"),
			variable("e")
		);

		StateObjectTester comparisonExpression2 = equal(
			path("p.type"),
			string("'Cell'")
		);

		StateObjectTester subQuery = subquery(
			subSelect(variable("p")),
			subFrom("Phone", "p"),
			where(and(comparisonExpression1, comparisonExpression2))
		);

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(subQuery))
		);
	}

	public static StateObjectTester stateObject_021() {

		// SELECT e
		// FROM Employee e
		// WHERE EXISTS (SELECT p FROM e.phones p WHERE p.type = 'Cell')

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
				exists(
					subquery(
						subSelect(variable("p")),
						subFrom(fromDerivedPath("e.phones", "p")),
						where(path("p.type").equal(string("'Cell'")))
					)
				)
			)
		);
	}

	public static StateObjectTester stateObject_022() {

		// SELECT e
		// FROM Employee e
		// WHERE e.department IN (SELECT DISTINCT d
		//                        FROM Department d JOIN d.employees de JOIN de.projects p
		//                        WHERE p.name LIKE 'QA%')

		StateObjectTester subquery = subquery(
			subSelectDistinct(variable("d")),
			subFrom("Department", "d", join("d.employees", "de"), join("de.projects", "p")),
			where(like(path("p.name"), string("'QA%'")))
		);

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(in("e.department", subquery))
		);
	}

	public static StateObjectTester stateObject_023() {

		// SELECT p
		// FROM Phone p
		// WHERE p.type NOT IN ('Office', 'Home')

		StateObjectTester notInExpression = notIn(
			"p.type",
			string("'Office'"),
			string("'Home'")
		);

		return selectStatement(
			select(variable("p")),
			from("Phone", "p"),
			where(notInExpression)
		);
	}

	public static StateObjectTester stateObject_024() {

		// SELECT m
		// FROM Employee m
		// WHERE (SELECT COUNT(e)
		//        FROM Employee e
		//        WHERE e.manager = m) > 0

		StateObjectTester comparisonExpression1 = equal(
			path("e.manager"),
			variable("m")
		);

		StateObjectTester subquery = subquery(
			subSelect(count(variable("e"))),
			subFrom("Employee", "e"),
			where(comparisonExpression1)
		);

		StateObjectTester comparisonExpression2 = greaterThan(
			sub(subquery),
			numeric(0L)
		);

		return selectStatement(
			select(variable("m")),
			from("Employee", "m"),
			where(comparisonExpression2)
		);
	}

	public static StateObjectTester stateObject_025() {

		// SELECT e
		// FROM Employee e
		// WHERE e MEMBER OF e.directs

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(memberOf("e", "e.directs"))
		);
	}

	public static StateObjectTester stateObject_026() {

		// SELECT e
		// FROM Employee e
		// WHERE NOT EXISTS (SELECT p
		//                   FROM e.phones p
		//                   WHERE p.type = 'Cell')

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
				notExists(
					subquery(
						subSelect(variable("p")),
						subFrom(fromDerivedPath("e.phones", "p")),
						where(path("p.type").equal(string("'Cell'")))
					)
				)
			)
		);
	}

	public static StateObjectTester stateObject_027() {

		// SELECT e
		// FROM Employee e
		// WHERE e.directs IS NOT EMPTY AND
		//       e.salary < ALL (SELECT d.salary FROM e.directs d)

		StateObjectTester subquery = subquery(
			subSelect(path("d.salary")),
			subFrom(fromDerivedPath("e.directs", "d"))
		);

		StateObjectTester comparisonExpression = lowerThan(
			path("e.salary"),
			all(subquery)
		);

		StateObjectTester andExpression = and(
			isNotEmpty("e.directs"),
			comparisonExpression
		);

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_028() {

		// SELECT e
		// FROM Employee e
		// WHERE e.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.employees de JOIN de.projects p
		//                           WHERE p.name LIKE 'QA%')

		StateObjectTester subquery = subquery(
			subSelectDistinct(variable("d")),
			subFrom("Department", "d", join("d.employees", "de"), join("de.projects", "p")),
			where(like(path("p.name"), string("'QA%'")))
		);

		StateObjectTester comparisonExpression = equal(
			path("e.department"),
			anyExpression(subquery)
		);

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_029() {

		// SELECT d
		// FROM Department d
		// WHERE SIZE(d.employees) = 2

		return selectStatement(
			select(variable("d")),
			from("Department", "d"),
			where(equal(size("d.employees"), numeric(2L)))
		);
	}

	public static StateObjectTester stateObject_030() {

		// SELECT d
      // FROM Department d
      // WHERE (SELECT COUNT(e)
      //        FROM d.employees e) = 2

		StateObjectTester subquery = subquery(
			subSelect(count(variable("e"))),
			subFrom(fromDerivedPath("d.employees", "e"))
		);

		return selectStatement(
			select(variable("d")),
			from("Department", "d"),
			where(equal(sub(subquery), numeric(2)))
		);
	}

	public static StateObjectTester stateObject_031() {

		// SELECT e
		// FROM Employee e
		// ORDER BY e.name DESC

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc("e.name"))
		);
	}

	public static StateObjectTester stateObject_032() {

		// SELECT e
      // FROM Employee e JOIN e.department d
      // ORDER BY d.name, e.name DESC

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_033() {

		// SELECT AVG(e.salary) FROM Employee e
		return selectStatement(
			select(avg("e.salary")),
			from("Employee", "e")
		);
	}

	public static StateObjectTester stateObject_034() {

		// SELECT d.name, AVG(e.salary)
		// FROM Department d JOIN d.employees e
		// GROUP BY d.name

		return selectStatement(
			select(path("d.name"), avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			nullExpression(),
			groupBy(path("d.name")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_035() {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name

		return selectStatement(
			select(path("d.name"), avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			where(isEmpty("e.directs")),
			groupBy(path("d.name")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_036() {

		// SELECT d.name, AVG(e.salary)
      // FROM Department d JOIN d.employees e
      // WHERE e.directs IS EMPTY
      // GROUP BY d.name
      // HAVING AVG(e.salary) > 50000

		return selectStatement(
			select(path("d.name"), avg("e.salary")),
			from("Department", "d", join("d.employees", "e")),
			where(isEmpty("e.directs")),
			groupBy(path("d.name")),
			having(greaterThan(avg("e.salary"), numeric(50000))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_037() {

		// SELECT e, COUNT(p), COUNT(DISTINCT p.type)
		// FROM Employee e JOIN e.phones p
		// GROUP BY e

		return selectStatement(
			select(variable("e"),
			       count(variable("p")),
			       countDistinct(path("p.type"))),
			from("Employee", "e", join("e.phones", "p")),
			nullExpression(),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_038() {

		// SELECT d.name, e.salary, COUNT(p)
		// FROM Department d JOIN d.employees e JOIN e.projects p
		// GROUP BY d.name, e.salary

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_039() {

		// SELECT e, COUNT(p)
		// FROM Employee e JOIN e.projects p
		// GROUP BY e
		// HAVING COUNT(p) >= 2

		return selectStatement(
			select(variable("e"), count(variable("p"))),
			from("Employee", "e", join("e.projects", "p")),
			nullExpression(),
			groupBy(variable("e")),
			having(greaterThanOrEqual(count(variable("p")), numeric(2))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_040() {

		// UPDATE Employee e
		// SET e.salary = 60000
		// WHERE e.salary = 55000

		return updateStatement(
			update("Employee", "e", set("e.salary", numeric(60000))),
			where(equal(path("e.salary"), numeric(55000)))
		);
	}

	public static StateObjectTester stateObject_041() {

		// UPDATE Employee e
		// SET e.salary = e.salary + 5000
		// WHERE EXISTS (SELECT p
		//               FROM e.projects p
		//               WHERE p.name = 'Release1')

		StateObjectTester additionExpression = add(
			path("e.salary"),
			numeric(5000)
		);

		StateObjectTester subquery = subquery(
			subSelect(variable("p")),
			subFrom(fromDerivedPath("e.projects", "p")),
			where(equal(path("p.name"), string("'Release1'")))
		);

		return updateStatement(
			update("Employee", "e", set("e.salary", additionExpression)),
			where(exists(subquery))
		);
	}

	public static StateObjectTester stateObject_042() {

		// UPDATE Phone p
		// SET p.number = CONCAT('288', SUBSTRING(p.number, LOCATE(p.number, '-'), 4)),
		//     p.type = 'Business'
		// WHERE p.employee.address.city = 'New York' AND p.type = 'Office'

		StateObjectTester concatExpression = concat(
			string("'288'"),
			substring(
				path("p.number"),
				locate(path("p.number"), string("'-'")),
				numeric(4L)
			)
		);

		StateObjectTester andExpression = and(
			equal(path("p.employee.address.city"), string("'New York'")),
			equal(path("p.type"), string("'Office'"))
		);

		return updateStatement(
			update(
				"Phone", "p",
				set("p.number", concatExpression),
				set("p.type", string("'Business'"))
			),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_043() {

		// DELETE FROM Employee e
		// WHERE e.department IS NULL";

		return deleteStatement(
			"Employee", "e",
			where(isNull(path("e.department")))
		);
	}

	public static StateObjectTester stateObject_044() {

		// Select Distinct object(c)
		// From Customer c, In(c.orders) co
		// Where co.totalPrice >= Some (Select o.totalPrice
		//                              From Order o, In(o.lineItems) l
		//                              Where l.quantity = 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(equal(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = greaterThanOrEqual(
			path("co.totalPrice"),
			some(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_045() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= SOME (Select o.totalPrice
		//                              FROM Order o, IN(o.lineItems) l
		//                              WHERE l.quantity = 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(equal(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = lowerThanOrEqual(
			path("co.totalPrice"),
			some(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_046() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ANY (Select MAX(o.totalPrice) FROM Order o)

		StateObjectTester subquery = subquery(
			subSelect(max("o.totalPrice")),
			subFrom("Order", "o")
		);

		StateObjectTester comparisonExpression = equal(
			path("co.totalPrice"),
			any(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_047() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(equal(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = lowerThan(
			path("co.totalPrice"),
			any(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_048() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ANY (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity = 3)

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_049() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <> ALL (Select MIN(o.totalPrice) FROM Order o)

		StateObjectTester subquery = subquery(
			subSelect(min("o.totalPrice")),
			subFrom("Order", "o")
		);

		StateObjectTester comparisonExpression = different(
			path("co.totalPrice"),
			all(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_050() {

		// SELECT Distinct object(c)
      // FROM Customer c, IN(c.orders) co
      // WHERE co.totalPrice >= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity >= 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThanOrEqual(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = greaterThanOrEqual(
			path("co.totalPrice"),
			all(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_051() {

		// SELECT Distinct object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice <= ALL (Select o.totalPrice
		//                             FROM Order o, IN(o.lineItems) l
		//                             WHERE l.quantity > 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThan(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = lowerThanOrEqual(
			path("co.totalPrice"),
			all(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_052() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice = ALL (Select MIN(o.totalPrice) FROM Order o)

		StateObjectTester subquery = subquery(
			subSelect(min("o.totalPrice")),
			subFrom("Order", "o")
		);

		StateObjectTester comparisonExpression = equal(
			path("co.totalPrice"),
			all(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_053() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice < ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThan(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = lowerThan(
			path("co.totalPrice"),
			all(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_054() {

		// SELECT DISTINCT object(c)
		// FROM Customer c, IN(c.orders) co
		// WHERE co.totalPrice > ALL (Select o.totalPrice
		//                            FROM Order o, IN(o.lineItems) l
		//                            WHERE l.quantity > 3)

		StateObjectTester subquery = subquery(
			subSelect(path("o.totalPrice")),
			subFrom(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(greaterThan(path("l.quantity"), numeric(3L)))
		);

		StateObjectTester comparisonExpression = greaterThan(
			path("co.totalPrice"),
			all(subquery)
		);

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.orders", "co")),
			where(comparisonExpression)
		);
	}

	public static StateObjectTester stateObject_055() {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT l
		//               FROM o.lineItems l
		//               where l.quantity > 3)

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", join("c.orders", "o")),
			where(
				exists(
					subquery(
						subSelect(variable("l")),
						subFrom(fromDerivedPath("o.lineItems", "l")),
						where(path("l.quantity").greaterThan(numeric(3L)))
					)
				)
			)
		);
	}

	public static StateObjectTester stateObject_056() {

		// SELECT DISTINCT c
		// FROM Customer c JOIN c.orders o
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice BETWEEN 1000 AND 1200)

		StateObjectTester subquery = subquery(
			subSelect(variable("o")),
			subFrom(fromDerivedPath("c.orders", "o")),
			where(between(path("o.totalPrice"), numeric(1000L), numeric(1200L)))
		);

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", join("c.orders", "o")),
			where(exists(subquery))
		);
	}

	public static StateObjectTester stateObject_057() {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.state IN(Select distinct w.state
		//                       from c.work w
		//                       where w.state = :state)

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(
					path("c.home.state")
				.in(
					subquery(
						subSelectDistinct(path("w.state")),
						subFrom(fromDerivedPath("c.work", "w")),
						where(path("w.state").equal(inputParameter(":state")))
					)
				)
			)
		);
	}

	public static StateObjectTester stateObject_058() {

		// Select Object(o)
		// from Order o
		// WHERE EXISTS (Select c
		//               From o.customer c
		//               WHERE c.name LIKE '%Caruso')

		StateObjectTester subquery = subquery(
			subSelect(variable("c")),
			subFrom(fromDerivedPath("o.customer", "c")),
			where(like(path("c.name"), string("'%Caruso'")))
		);

		return selectStatement(
			select(object("o")),
			from("Order", "o"),
			where(exists(subquery))
		);
	}

	public static StateObjectTester stateObject_059() {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE EXISTS (SELECT o
		//               FROM c.orders o
		//               where o.totalPrice > 1500)

		StateObjectTester subquery = subquery(
			subSelect(variable("o")),
			subFrom(fromDerivedPath("c.orders", "o")),
			where(greaterThan(path("o.totalPrice"), numeric(1500)))
		);

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(exists(subquery))
		);
	}

	public static StateObjectTester stateObject_060() {

		// SELECT c
		// FROM Customer c
		// WHERE NOT EXISTS (SELECT o1 FROM c.orders o1)

		StateObjectTester subquery = subquery(
			subSelect(variable("o1")),
			subFrom(fromDerivedPath("c.orders", "o1"))
		);

		return selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(notExists(subquery))
		);
	}

	public static StateObjectTester stateObject_061() {

		// select object(o)
		// FROM Order o
		// Where SQRT(o.totalPrice) > :doubleValue

		return selectStatement(
			select(object("o")),
			from("Order", "o"),
			where(greaterThan(sqrt(path("o.totalPrice")), inputParameter(":doubleValue")))
		);
	}

	public static StateObjectTester stateObject_062() {

		// select sum(o.totalPrice)
		// FROM Order o
		// GROUP BY o.totalPrice
		// HAVING ABS(o.totalPrice) = :doubleValue

		return selectStatement(
			select(sum("o.totalPrice")),
			from("Order", "o"),
			nullExpression(),
			groupBy(path("o.totalPrice")),
			having(equal(abs(path("o.totalPrice")), inputParameter(":doubleValue"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_063() {

		// select c.name
		// FROM Customer c
		// Group By c.name
		// HAVING trim(TRAILING from c.name) = ' David R. Vincent'

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(trimTrailingFrom(path("c.name")), string("' David R. Vincent'"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_064() {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// Having trim(LEADING from c.name) = 'David R. Vincent '

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(trimLeadingFrom(path("c.name")), string("'David R. Vincent '"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_065() {

		// select c.name
		// FROM  Customer c
		// Group by c.name
		// HAVING trim(BOTH from c.name) = 'David R. Vincent'

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(trimBothFrom(path("c.name")), string("'David R. Vincent'"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_066() {

		// select c.name
		// FROM  Customer c
		// GROUP BY c.name
		// HAVING LOCATE('Frechette', c.name) > 0

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(greaterThan(locate(string("'Frechette'"), path("c.name")), numeric(0L))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_067() {

		// select a.city
		// FROM  Customer c JOIN c.home a
		// GROUP BY a.city
		// HAVING LENGTH(a.city) = 10

		return selectStatement(
			select(path("a.city")),
			from("Customer", "c", join("c.home", "a")),
			nullExpression(),
			groupBy(path("a.city")),
			having(equal(length(path("a.city")), numeric(10L))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_068() {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.country
		// HAVING UPPER(cc.country) = 'ENGLAND'

		return selectStatement(
			select(count("cc.country")),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.country")),
			having(equal(upper(path("cc.country")), string("'ENGLAND'"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_069() {

		// select count(cc.country)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING LOWER(cc.code) = 'gbr'

		return selectStatement(
			select(count("cc.country")),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.code")),
			having(equal(lower(path("cc.code")), string("'gbr'"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_070() {

		// select c.name
		// FROM  Customer c
		// Group By c.name
		// HAVING c.name = concat(:fmname, :lname)

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.name")),
			having(equal(path("c.name"), concat(inputParameter(":fmname"), inputParameter(":lname")))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_071() {

		// select count(c)
		// FROM  Customer c JOIN c.aliases a
		// GROUP BY a.alias
		// HAVING a.alias = SUBSTRING(:string1, :int1, :int2)

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_072() {

		// select c.country.country
		// FROM  Customer c
		// GROUP BY c.country.country

		return selectStatement(
			select(path("c.country.country")),
			from("Customer", "c"),
			nullExpression(),
			groupBy(path("c.country.country")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_073() {

		// select Count(c)
		// FROM  Customer c JOIN c.country cc
		// GROUP BY cc.code
		// HAVING cc.code IN ('GBR', 'CHA')

		return selectStatement(
			select(count(variable("c"))),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.code")),
			having(in(path("cc.code"), string("'GBR'"), string("'CHA'"))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_074() {

		// select c.name
		// FROM  Customer c JOIN c.orders o
		// WHERE o.totalPrice BETWEEN 90 AND 160
		// GROUP BY c.name

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c", join("c.orders", "o")),
			where(between(path("o.totalPrice"), numeric(90), numeric(160))),
			groupBy(path("c.name")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_075() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice > 10000

		StateObjectTester orExpression = or(
			equal(path("o.customer.id"), string("'1001'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		return selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_076() {

		// select Distinct Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' OR o.totalPrice < 1000

		StateObjectTester orExpression = or(
			equal(path("o.customer.id"), string("'1001'")),
			lowerThan(path("o.totalPrice"), numeric(1000))
		);

		return selectStatement(
			selectDistinct(object("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_077() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 10000

		StateObjectTester orExpression = or(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		return selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_078() {

		// select DISTINCT o
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice > 5000

		StateObjectTester orExpression = or(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(5000))
		);

		return selectStatement(
			selectDistinct(variable("o")),
			fromAs("Order", "o"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_079() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice > 10000

		StateObjectTester andExpression = and(
			equal(path("o.customer.id"), string("'1001'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		return selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_080() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.id = '1001' AND o.totalPrice < 1000

		StateObjectTester andExpression = and(
			equal(path("o.customer.id"), string("'1001'")),
			lowerThan(path("o.totalPrice"), numeric(1000))
		);

		return selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_081() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 10000

		StateObjectTester andExpression = and(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(10000))
		);

		return selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_082() {

		// select Object(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' AND o.totalPrice > 500

		StateObjectTester andExpression = and(
			equal(path("o.customer.name"), string("'Karen R. Tegan'")),
			greaterThan(path("o.totalPrice"), numeric(500))
		);

		return selectStatement(
			select(object("o")),
			fromAs("Order", "o"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_083() {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate

		return selectStatement(
			selectDistinct(variable("p")),
			from("Product", "p"),
			where(notBetween(path("p.shelfLife.soldDate"), inputParameter(":date1"), inputParameter(":newdate")))
		);
	}

	public static StateObjectTester stateObject_084() {

		// SELECT DISTINCT o
		// From Order o
		// where o.totalPrice NOT BETWEEN 1000 AND 1200

		return selectStatement(
			selectDistinct(variable("o")),
			from("Order", "o"),
			where(notBetween(path("o.totalPrice"), numeric(1000), numeric(1200)))
		);
	}

	public static StateObjectTester stateObject_085() {

		// SELECT DISTINCT p
		// From Product p
		// where p.shelfLife.soldDate BETWEEN :date1 AND :date6

		return selectStatement(
			selectDistinct(variable("p")),
			from("Product", "p"),
			where(between(path("p.shelfLife.soldDate"), inputParameter(":date1"), inputParameter(":date6")))
		);
	}

	public static StateObjectTester stateObject_086() {

		// SELECT DISTINCT a
		// from Alias a LEFT JOIN FETCH a.customers
		// where a.alias LIKE 'a%'

		return selectStatement(
			selectDistinct(variable("a")),
			from("Alias", "a", leftJoinFetch("a.customers")),
			where(like(path("a.alias"), string("'a%'")))
		);
	}

	public static StateObjectTester stateObject_087() {

		// select Object(o)
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.name LIKE '%Caruso'

		return selectStatement(
			select(object("o")),
			from("Order", "o", leftJoinFetch("o.customer")),
			where(like(path("o.customer.name"), string("'%Caruso'")))
		);
	}

	public static StateObjectTester stateObject_088() {

		// select o
		// from Order o LEFT JOIN FETCH o.customer
		// where o.customer.home.city='Lawrence'

		return selectStatement(
			select(variable("o")),
			from("Order", "o", leftJoinFetch("o.customer")),
			where(equal(path("o.customer.home.city"), string("'Lawrence'")))
		);
	}

	public static StateObjectTester stateObject_089() {

		// SELECT DISTINCT c
		// from Customer c LEFT JOIN FETCH c.orders
		// where c.home.state IN('NY','RI')

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", leftJoinFetch("c.orders")),
			where(in(path("c.home.state"), string("'NY'"), string("'RI'")))
		);
	}

	public static StateObjectTester stateObject_090() {

		// SELECT c
		// from Customer c JOIN FETCH c.spouse

		return selectStatement(
			select(variable("c")),
			from("Customer", "c", joinFetch("c.spouse"))
		);
	}

	public static StateObjectTester stateObject_091() {

		// SELECT Object(c)
		// from Customer c INNER JOIN c.aliases a
		// where a.alias = :aName

		return selectStatement(
			select(object("c")),
			from("Customer", "c", innerJoin("c.aliases", "a")),
			where(equal(path("a.alias"), inputParameter(":aName")))
		);
	}

	public static StateObjectTester stateObject_092() {

		// SELECT Object(o)
		// from Order o INNER JOIN o.customer cust
		// where cust.name = ?1

		return selectStatement(
			select(object("o")),
			from("Order", "o", innerJoin("o.customer", "cust")),
			where(equal(path("cust.name"), inputParameter("?1")))
		);
	}

	public static StateObjectTester stateObject_093() {

		// SELECT DISTINCT object(c)
		// from Customer c INNER JOIN c.creditCards cc
		// where cc.type='VISA'

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c", innerJoin("c.creditCards", "cc")),
			where(equal(path("cc.type"), string("'VISA'")))
		);
	}

	public static StateObjectTester stateObject_094() {

		// SELECT c
		// from Customer c INNER JOIN c.spouse s

		return selectStatement(
			select(variable("c")),
			from("Customer", "c", innerJoin("c.spouse", "s"))
		);
	}

	public static StateObjectTester stateObject_095() {

		// select cc.type
		// FROM CreditCard cc JOIN cc.customer cust
		// GROUP BY cc.type

		return selectStatement(
			select(path("cc.type")),
			from("CreditCard", "cc", join("cc.customer", "cust")),
			nullExpression(),
			groupBy(path("cc.type")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_096() {

		// select cc.code
		// FROM Customer c JOIN c.country cc
		// GROUP BY cc.code

		return selectStatement(
			select(path("cc.code")),
			from("Customer", "c", join("c.country", "cc")),
			nullExpression(),
			groupBy(path("cc.code")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_097() {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where LOWER(a.alias)='sjc'

		return selectStatement(
			select(object("c")),
			from("Customer", "c", join("c.aliases", "a")),
			where(equal(lower(path("a.alias")), string("'sjc'"))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_098() {

		// select Object(c)
		// FROM Customer c JOIN c.aliases a
		// where UPPER(a.alias)='SJC'

		return selectStatement(
			select(object("c")),
			from("Customer", "c", join("c.aliases", "a")),
			where(equal(upper(path("a.alias")), string("'SJC'"))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_099() {

		// SELECT c.id, a.alias
		// from Customer c LEFT OUTER JOIN c.aliases a
		// where c.name LIKE 'Ste%'
		// ORDER BY a.alias, c.id

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_100() {

		// SELECT o.id, cust.id
		// from Order o LEFT OUTER JOIN o.customer cust
		// where cust.name=?1
		// ORDER BY o.id

		return selectStatement(
			select(path("o.id"), path("cust.id")),
			from("Order", "o", leftOuterJoin("o.customer", "cust")),
			where(path("cust.name").equal(inputParameter("?1"))),
			nullExpression(),
			nullExpression(),
			orderBy("o.id")
		);
	}

	public static StateObjectTester stateObject_101() {

		// SELECT DISTINCT c
		// from Customer c LEFT OUTER JOIN c.creditCards cc
		// where c.name LIKE '%Caruso'

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", leftOuterJoin("c.creditCards", "cc")),
			where(path("c.name").like(string("'%Caruso'"))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_102() {

		// SELECT Sum(p.quantity)
		// FROM Product p

		return selectStatement(
			select(sum("p.quantity")),
			from("Product", "p")
		);
	}

	public static StateObjectTester stateObject_103() {

		// Select Count(c.home.city)
		// from Customer c

		return selectStatement(
			select(count("c.home.city")),
			from("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_104() {

		// SELECT Sum(p.price)
		// FROM Product p

		return selectStatement(
			select(sum("p.price")),
			from("Product", "p")
		);
	}

	public static StateObjectTester stateObject_105() {

		// SELECT AVG(o.totalPrice)
		// FROM Order o

		return selectStatement(
			select(avg("o.totalPrice")),
			from("Order", "o")
		);
	}

	public static StateObjectTester stateObject_106() {

		// SELECT DISTINCT MAX(l.quantity)
		// FROM LineItem l

		return selectStatement(
			selectDistinct(max("l.quantity")),
			from("LineItem", "l")
		);
	}

	public static StateObjectTester stateObject_107() {

		// SELECT DISTINCT MIN(o.id)
		// FROM Order o
		// where o.customer.name = 'Robert E. Bissett'

		return selectStatement(
			selectDistinct(min("o.id")),
			from("Order", "o"),
			where(path("o.customer.name").equal(string("'Robert E. Bissett'")))
		);
	}

	public static StateObjectTester stateObject_108() {

		// SELECT NEW com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer(c.id, c.name)
		// FROM Customer c
		// where c.work.city = :workcity

		return selectStatement(
			select(new_(
				"com.sun.ts.tests.ejb30.persistence.query.language.schema30.Customer",
				path("c.id"),
				path("c.name")
			)),
			from("Customer", "c"),
			where(path("c.work.city").equal(inputParameter(":workcity")))
		);
	}

	public static StateObjectTester stateObject_109() {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) > 100

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(size("c.orders").greaterThan(numeric(100)))
		);
	}

	public static StateObjectTester stateObject_110() {

		// SELECT DISTINCT c
		// FROM Customer c
		// WHERE SIZE(c.orders) >= 2

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(size("c.orders").greaterThanOrEqual(numeric(2)))
		);
	}

	public static StateObjectTester stateObject_111() {

		// select Distinct c
		// FROM Customer c LEFT OUTER JOIN c.work workAddress
		// where workAddress.zip IS NULL

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c", leftOuterJoin("c.work", "workAddress")),
			where(isNull(path("workAddress.zip")))
		);
	}

	public static StateObjectTester stateObject_112() {

		// SELECT DISTINCT c
		// FROM Customer c, IN(c.orders) o

		return selectStatement(
			selectDistinct(variable("c")),
			from(
				fromEntity("Customer", "c"),
				fromIn("c.orders", "o"))
		);
	}

	public static StateObjectTester stateObject_113() {

		// Select Distinct Object(c)
		// from Customer c
		// where c.name is null

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNull(path("c.name")))
		);
	}

	public static StateObjectTester stateObject_114() {

		// Select c.name
		// from Customer c
		// where c.home.street = '212 Edgewood Drive'

		return selectStatement(
			select(path("c.name")),
			from("Customer", "c"),
			where(path("c.home.street").equal(string("'212 Edgewood Drive'")))
		);
	}

	public static StateObjectTester stateObject_115() {

		// Select s.customer
		// from Spouse s
		// where s.id = '6'

		return selectStatement(
			select(path("s.customer")),
			from("Spouse", "s"),
			where(path("s.id").equal(string("'6'")))
		);
	}

	public static StateObjectTester stateObject_116() {

		// Select c.work.zip
		// from Customer c

		return selectStatement(
			select(path("c.work.zip")),
			from("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_117() {

		// SELECT Distinct Object(c)
		// From Customer c, IN(c.home.phones) p
		// where p.area LIKE :area

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_118() {

		// SELECT DISTINCT Object(c)
		// from Customer c, in(c.aliases) a
		// where NOT a.customerNoop IS NULL

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(not(isNull(path("a.customerNoop"))))
		);
	}

	public static StateObjectTester stateObject_119() {

		// select distinct object(c)
		// fRoM Customer c, IN(c.aliases) a
		// where c.name = :cName OR a.customerNoop IS NULL

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("c.name").equal(inputParameter(":cName")).or(isNull(path("a.customerNoop"))))
		);
	}

	public static StateObjectTester stateObject_120() {

		// select Distinct Object(c)
		// from Customer c, in(c.aliases) a
		// where c.name = :cName AND a.customerNoop IS NULL

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("c.name").equal(inputParameter(":cName")).and(isNull(path("a.customerNoop"))))
		);
	}

	public static StateObjectTester stateObject_121() {

		// sElEcT Distinct oBJeCt(c)
		// FROM Customer c, IN(c.aliases) a
		// WHERE a.customerNoop IS NOT NULL

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(isNotNull(path("a.customerNoop")))
		);
	}

	public static StateObjectTester stateObject_122() {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE '%\\_%' escape '\\'

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("a.alias").like(string("'%\\_%'"), "'\\'"))
		);
	}

	public static StateObjectTester stateObject_123() {

		// Select Distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.customerNoop IS NULL

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(isNull(path("a.customerNoop")))
		);
	}

	public static StateObjectTester stateObject_124() {

		// Select Distinct o.creditCard.balance
		// from Order o
		// ORDER BY o.creditCard.balance ASC

		return selectStatement(
			selectDistinct(path("o.creditCard.balance")),
			from("Order", "o"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemAsc("o.creditCard.balance"))
		);
	}

	public static StateObjectTester stateObject_125() {

		// Select c.work.zip
		// from Customer c
		// where c.work.zip IS NOT NULL
		// ORDER BY c.work.zip ASC

		return selectStatement(
			select(path("c.work.zip")),
			from("Customer", "c"),
			where(isNotNull(path("c.work.zip"))),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemAsc("c.work.zip"))
		);
	}

	public static StateObjectTester stateObject_126() {

		// SELECT a.alias
		// FROM Alias AS a
		// WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1

		StateObjectTester orExpression =
				sub(
						isNull(path("a.alias"))
					.and(
						isNull(inputParameter(":param1"))))
			.or(
				path("a.alias").equal(inputParameter(":param1")));

		return selectStatement(
			select(path("a.alias")),
			fromAs("Alias", "a"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_127() {

		// Select Object(c)
		// from Customer c
		// where c.aliasesNoop IS NOT EMPTY or c.id <> '1'

		StateObjectTester orExpression =
				isNotEmpty("c.aliasesNoop")
			.or(
				path("c.id").different(string("'1'")));

		return selectStatement(
			select(object("c")),
			from("Customer", "c"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_128() {

		// Select Distinct Object(p)
		// from Product p
		// where p.name = ?1

		return selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(path("p.name").equal(inputParameter("?1")))
		);
	}

	public static StateObjectTester stateObject_129() {

		// Select Distinct Object(p)
		// from Product p
		// where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)

		StateObjectTester addition =
				sub(numeric(500)
			.add(
				inputParameter(":int1")));

		StateObjectTester andExpression =
				sub(path("p.quantity").greaterThan(addition))
			.and(
				sub(isNull(path("p.partNumber"))));

		return selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_130() {

		// Select Distinct Object(o)
		// from Order o
		// where o.customer.name IS NOT NULL

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(isNotNull(path("o.customer.name")))
		);
	}

	public static StateObjectTester stateObject_131() {

		// Select DISTINCT Object(p)
		// From Product p
		// where (p.quantity < 10) OR (p.quantity > 20)

		StateObjectTester orExpression =
				sub(path("p.quantity").lowerThan(numeric(10)))
			.or(
				sub(path("p.quantity").greaterThan(numeric(20))));

		return selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_132() {

		// Select DISTINCT Object(p)
		// From Product p
		// where p.quantity NOT BETWEEN 10 AND 20

		return selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(path("p.quantity").notBetween(numeric(10), numeric(20)))
		);
	}

	public static StateObjectTester stateObject_133() {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where (p.quantity >= 10) AND (p.quantity <= 20)

		StateObjectTester andExpression =
				sub(path("p.quantity").greaterThanOrEqual(numeric(10)))
			.and(
				sub(path("p.quantity").lowerThanOrEqual(numeric(20))));

		return selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(andExpression)
		);
	}

	public static StateObjectTester stateObject_134() {

		// Select DISTINCT OBJECT(p)
		// From Product p
		// where p.quantity BETWEEN 10 AND 20

		return selectStatement(
			selectDistinct(object("p")),
			from("Product", "p"),
			where(path("p.quantity").between(numeric(10), numeric(20)))
		);
	}

	public static StateObjectTester stateObject_135() {

		// Select Distinct OBJECT(c)
		// from Customer c, IN(c.creditCards) b
		// where SQRT(b.balance) = :dbl

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.creditCards", "b")),
			where(sqrt(path("b.balance")).equal(inputParameter(":dbl")))
		);
	}

	public static StateObjectTester stateObject_136() {

		// Select Distinct OBJECT(c)
		// From Product p
		// where MOD(550, 100) = p.quantity

		return selectStatement(
			selectDistinct(object("c")),
			from("Product", "p"),
			where(mod(numeric(550), numeric(100)).equal(path("p.quantity")))
		);
	}

	public static StateObjectTester stateObject_137() {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// WHERE (c.home.state = 'NH') OR (c.home.state = 'RI')

		StateObjectTester orExpression =
				sub(path("c.home.state").equal(string("'NH'")))
			.or(
				sub(path("c.home.state").equal(string("'RI'"))));

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(orExpression)
		);
	}

	public static StateObjectTester stateObject_138() {

		// SELECT DISTINCT Object(c)
		// from Customer c
		// where c.home.state IN('NH', 'RI')

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(path("c.home.state").in(string("'NH'"), string("'RI'")))
		);
	}

	public static StateObjectTester stateObject_140() {

		// SELECT c
		// from Customer c
		// where c.home.city IN(:city)

		return selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(path("c.home.city").in(inputParameter(":city")))
		);
	}

	public static StateObjectTester stateObject_141() {

		// Select Distinct Object(o)
		// from Order o, in(o.lineItems) l
		// where l.quantity NOT IN (1, 5)

		return selectStatement(
			selectDistinct(object("o")),
			from(fromEntity("Order", "o"), fromIn("o.lineItems", "l")),
			where(path("l.quantity").notIn(numeric(1), numeric(5)))
		);
	}

	public static StateObjectTester stateObject_142() {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE o.sampleLineItem MEMBER OF o.lineItems

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.sampleLineItem").memberOf(collectionPath("o.lineItems")))
		);
	}

	public static StateObjectTester stateObject_143() {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE :param NOT MEMBER o.lineItems

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(inputParameter(":param").notMember(collectionPath("o.lineItems")))
		);
	}

	public static StateObjectTester stateObject_144() {

		// Select Distinct Object(o)
		// FROM Order o, LineItem l
		// WHERE l MEMBER o.lineItems

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o", "LineItem", "l"),
			where(variable("l").member(collectionPath("o.lineItems")))
		);
	}

	public static StateObjectTester stateObject_145() {

		// select distinct Object(c)
		// FROM Customer c, in(c.aliases) a
		// WHERE a.alias LIKE 'sh\\_ll' escape '\\'

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("a.alias").like(string("'sh\\_ll'"), "'\\'"))
		);
	}

	public static StateObjectTester stateObject_146() {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop NOT MEMBER OF a.customersNoop

		return selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(path("a.customerNoop").notMemberOf(collectionPath("a.customersNoop")))
		);
	}

	public static StateObjectTester stateObject_147() {

		// Select Distinct Object(a)
		// FROM Alias a
		// WHERE a.customerNoop MEMBER OF a.customersNoop

		return selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(path("a.customerNoop").memberOf(collectionPath("a.customersNoop")))
		);
	}

	public static StateObjectTester stateObject_148() {

		// Select Distinct Object(a)
		// from Alias a
		// where LOCATE('ev', a.alias) = 3

		return selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(locate(string("'ev'"), path("a.alias")).equal(numeric(3)))
		);
	}

	public static StateObjectTester stateObject_149() {

		// Select DISTINCT Object(o)
		// From Order o
		// WHERE o.totalPrice > ABS(:dbl)

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.totalPrice").greaterThan(abs(inputParameter(":dbl"))))
		);
	}

	public static StateObjectTester stateObject_150() {

		// Select Distinct OBjeCt(a)
		// From Alias a
		// WHERE LENGTH(a.alias) > 4

		return selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(length(path("a.alias")).greaterThan(numeric(4)))
		);
	}

	public static StateObjectTester stateObject_151() {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = SUBSTRING(:string1, :int2, :int3)

		return selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(
					path("a.alias")
				.equal(
					substring(inputParameter(":string1"), inputParameter(":int2"), inputParameter(":int3"))))
		);
	}

	public static StateObjectTester stateObject_152() {

		// Select Distinct Object(a)
		// From Alias a
		// WHERE a.alias = CONCAT('ste', 'vie')

		return selectStatement(
			selectDistinct(object("a")),
			from("Alias", "a"),
			where(
					path("a.alias")
				.equal(
					concat(string("'ste'"), string("'vie'"))))
		);
	}

	public static StateObjectTester stateObject_153() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.work.zip IS NOT NULL

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNotNull(path("c.work.zip")))
		);
	}

	public static StateObjectTester stateObject_154() {

		// sELEct dIsTiNcT oBjEcT(c)
		// FROM Customer c
		// WHERE c.work.zip IS NULL

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNull(path("c.work.zip")))
		);
	}

	public static StateObjectTester stateObject_155() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS NOT EMPTY

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isNotEmpty("c.aliases"))
		);
	}

	public static StateObjectTester stateObject_156() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.aliases IS EMPTY

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(isEmpty("c.aliases"))
		);
	}

	public static StateObjectTester stateObject_157() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip not like '%44_'

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(path("c.home.zip").notLike(string("'%44_'")))
		);
	}

	public static StateObjectTester stateObject_158() {

		// Select Distinct Object(c)
		// FROM Customer c
		// WHERE c.home.zip LIKE '%77'"

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c"),
			where(path("c.home.zip").like(string("'%77'")))
		);
	}

	public static StateObjectTester stateObject_159() {

		// Select Distinct Object(c)
		// FROM Customer c Left Outer Join c.home h
		// WHERE h.city Not iN ('Swansea', 'Brookline')

		return selectStatement(
			selectDistinct(object("c")),
			from("Customer", "c", leftOuterJoin("c.home", "h")),
			where(path("h.city").notIn(string("'Swansea'"), string("'Brookline'")))
		);
	}

	public static StateObjectTester stateObject_160() {

		// select distinct c
		// FROM Customer c
		// WHERE c.home.city IN ('Lexington')

		return selectStatement(
			selectDistinct(variable("c")),
			from("Customer", "c"),
			where(path("c.home.city").in(string("'Lexington'")))
		);
	}

	public static StateObjectTester stateObject_161() {

		// sElEcT c
		// FROM Customer c
		// Where c.name = :cName

		return selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(path("c.name").equal(inputParameter(":cName")))
		);
	}

	public static StateObjectTester stateObject_162() {

		// select distinct Object(o)
		// From Order o
		// WHERE o.creditCard.approved = FALSE

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.creditCard.approved").equal(FALSE()))
		);
	}

	public static StateObjectTester stateObject_163() {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice NOT bETwEeN 1000 AND 1200

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.totalPrice").notBetween(numeric(1000), numeric(1200)))
		);
	}

	public static StateObjectTester stateObject_164() {

		// SELECT DISTINCT Object(o)
		// From Order o
		// where o.totalPrice BETWEEN 1000 AND 1200

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(path("o.totalPrice").between(numeric(1000), numeric(1200)))
		);
	}

	public static StateObjectTester stateObject_165() {

		// SELECT DISTINCT Object(o)
		// FROM Order o, in(o.lineItems) l
		// WHERE l.quantity < 2 AND o.customer.name = 'Robert E. Bissett'

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_166() {

		// select distinct Object(o)
		// FROM Order AS o, in(o.lineItems) l
		// WHERE (l.quantity < 2) AND ((o.totalPrice < (3 + 54 * 2 + -8)) OR (o.customer.name = 'Robert E. Bissett'))

		return selectStatement(
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
											numeric(54).multiplication(numeric(2))
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
	}

	public static StateObjectTester stateObject_167() {

		// SeLeCt DiStInCt oBjEcT(o)
		// FROM Order AS o
		// WHERE o.customer.name = 'Karen R. Tegan' OR o.totalPrice < 100

		return selectStatement(
			selectDistinct(object("o")),
			fromAs("Order", "o"),
			where(
					path("o.customer.name").equal(string("'Karen R. Tegan'"))
				.or(
					path("o.totalPrice").lowerThan(numeric(100))
				)
			)
		);
	}

	public static StateObjectTester stateObject_168() {

		// Select Distinct Object(o)
		// FROM Order o
		// WHERE NOT o.totalPrice < 4500

		return selectStatement(
			selectDistinct(object("o")),
			from("Order", "o"),
			where(not(path("o.totalPrice").lowerThan(numeric(4500))))
		);
	}

	public static StateObjectTester stateObject_169() {

		// Select DISTINCT Object(P)
		// From Product p

		return selectStatement(
			selectDistinct(object("P")),
			from("Product", "p")
		);
	}

	public static StateObjectTester stateObject_170() {

		// SELECT DISTINCT c
		// from Customer c
		// WHERE c.home.street = :street OR c.home.city = :city OR c.home.state = :state or c.home.zip = :zip

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_171() {

		// SELECT c
		// from Customer c
		// WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_172() {

		// SELECT c
		// from Customer c
      // WHERE c.home.street = :street AND c.home.city = :city AND c.home.state = :state and c.home.zip = :zip

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_173() {

		// Select Distinct Object(c)
		// FrOm Customer c, In(c.aliases) a
		// WHERE a.alias = :aName

		return selectStatement(
			selectDistinct(object("c")),
			from(fromEntity("Customer", "c"), fromIn("c.aliases", "a")),
			where(path("a.alias").equal(inputParameter(":aName")))
		);
	}

	public static StateObjectTester stateObject_174() {

		// Select Distinct Object(c)
		// FROM Customer AS c

		return selectStatement(
			selectDistinct(object("c")),
			fromAs("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_175() {

		// Select Distinct o
		// from Order AS o
		// WHERE o.customer.name = :name

		return selectStatement(
			selectDistinct(variable("o")),
			fromAs("Order", "o"),
			where(path("o.customer.name").equal(inputParameter(":name")))
		);
	}

	public static StateObjectTester stateObject_176() {

		// UPDATE Customer c SET c.name = 'CHANGED'
		// WHERE c.orders IS NOT EMPTY

		return updateStatement(
			update("Customer", "c", set(path("c.name"), string("'CHANGED'"))),
			where(isNotEmpty("c.orders"))
		);
	}

	public static StateObjectTester stateObject_177() {

		// UPDATE DateTime SET date = CURRENT_DATE

		return updateStatement(
			update("DateTime", set("date", CURRENT_DATE()))
		);
	}

	public static StateObjectTester stateObject_178() {

		// SELECT c
		// FROM Customer c
		// WHERE c.firstName = :first AND
		//     c.lastName = :last

		return selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.firstName").equal(inputParameter(":first"))
				.and(
					path("c.lastName").equal(inputParameter(":last"))
				)
			)
		);
	}

	public static StateObjectTester stateObject_179() {

		// SELECT OBJECT ( c ) FROM Customer AS c

		return selectStatement(
			select(object("c")),
			fromAs("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_180() {

		// SELECT c.firstName, c.lastName
		// FROM Customer AS c

		return selectStatement(
			select(path("c.firstName"), path("c.lastName")),
			fromAs("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_181() {

		// SELECT c.address.city
		// FROM Customer AS c

		return selectStatement(
			select(path("c.address.city")),
			fromAs("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_182() {

		// SELECT new com.titan.domain.Name(c.firstName, c.lastName)
		// FROM Customer c

		return selectStatement(
			select(new_("com.titan.domain.Name", path("c.firstName"), path("c.lastName"))),
			from("Customer", "c")
		);
	}

	public static StateObjectTester stateObject_183() {

		// SELECT cbn.ship
		// FROM Customer AS c, IN ( c.reservations ) r, IN ( r.cabins ) cbn

		return selectStatement(
			select(path("cbn.ship")),
			from(
				fromEntityAs("Customer", "c"),
				fromIn("c.reservations", "r"),
				fromIn("r.cabins", "cbn")
			)
		);
	}

	public static StateObjectTester stateObject_184() {

		// Select c.firstName, c.lastName, p.number
		// From Customer c Left Join c.phoneNumbers p

		return selectStatement(
			select(path("c.firstName"), path("c.lastName"), path("p.number")),
			from("Customer", "c", leftJoin("c.phoneNumbers", "p"))
		);
	}

	public static StateObjectTester stateObject_185() {

		// SELECT r
		// FROM Reservation AS r
		// WHERE (r.amountPaid * .01) > 300.00

		return selectStatement(
			select(variable("r")),
			fromAs("Reservation", "r"),
			where(
					sub(path("r.amountPaid").multiplication(numeric(".01")))
				.greaterThan(
					numeric("300.00")
				)
			)
		);
	}

	public static StateObjectTester stateObject_186() {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage >= 80000.00 AND s.tonnage <= 130000.00

		return selectStatement(
			select(variable("s")),
			fromAs("Ship", "s"),
			where(
					path("s.tonnage").greaterThanOrEqual(numeric("80000.00"))
				.and(
					path("s.tonnage").lowerThanOrEqual(numeric("130000.00"))
				)
			)
		);
	}

	public static StateObjectTester stateObject_187() {

		// SELECT r
		// FROM Reservation r, IN ( r.customers ) AS cust
		// WHERE cust = :specificCustomer

		return selectStatement(
			select(variable("r")),
			from(fromEntity("Reservation", "r"), fromInAs("r.customers", "cust")),
			where(variable("cust").equal(inputParameter(":specificCustomer")))
		);
	}

	public static StateObjectTester stateObject_188() {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage BETWEEN 80000.00 AND 130000.00

		return selectStatement(
			select(variable("s")),
			fromAs("Ship", "s"),
			where(path("s.tonnage").between(numeric("80000.00"), numeric("130000.00")))
		);
	}

	public static StateObjectTester stateObject_189() {

		// SELECT s
		// FROM Ship AS s
		// WHERE s.tonnage NOT BETWEEN 80000.00 AND 130000.00

		return selectStatement(
			select(variable("s")),
			fromAs("Ship", "s"),
			where(path("s.tonnage").notBetween(numeric("80000.00"), numeric("130000.00")))
		);
	}

	public static StateObjectTester stateObject_190() {

		// SELECT c
		// FROM Customer AS c
		//  WHERE c.address.state IN ('FL', 'TX', 'MI', 'WI', 'MN')

		return selectStatement(
			select(variable("c")),
			fromAs("Customer", "c"),
			where(path("c.address.state").in(string("'FL'"), string("'TX'"), string("'MI'"), string("'WI'"), string("'MN'")))
		);
	}

	public static StateObjectTester stateObject_191() {

		// SELECT cab
		// FROM Cabin AS cab
		// WHERE cab.deckLevel IN (1,3,5,7)

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_192() {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state IN(?1, ?2, ?3, 'WI', 'MN')

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_193() {

		// SELECT c
		// FROM Customer c
		// WHERE c.address IS NULL

		return selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(isNull(path("c.address")))
		);
	}

	public static StateObjectTester stateObject_194() {

		// SELECT c
		// FROM Customer c
		// WHERE c.address.state = 'TX' AND
		//       c.lastName = 'Smith' AND
		//       c.firstName = 'John'

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_195() {

		// SELECT crs
		// FROM Cruise AS crs, IN(crs.reservations) AS res, Customer AS cust
		// WHERE
		//  cust = :myCustomer
		//  AND
		//  cust MEMBER OF res.customers

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_196() {

		// SELECT c
		// FROM Customer AS c
		// WHERE    LENGTH(c.lastName) > 6
		//       AND
		//          LOCATE( c.lastName, 'Monson' ) > -1

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_197() {

		// SELECT c
		// FROM Customer AS C
		// ORDER BY c.lastName

		return selectStatement(
			select(variable("c")),
			fromAs("Customer", "C"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy("c.lastName")
		);
	}

	public static StateObjectTester stateObject_198() {

		// SELECT c
		// FROM Customer AS C
      // WHERE c.address.city = 'Boston' AND c.address.state = 'MA'
		// ORDER BY c.lastName DESC

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_199() {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name

		return selectStatement(
			select(path("cr.name"), count(variable("res"))),
			from("Cruise", "cr", leftJoin("cr.reservations", "res")),
			nullExpression(),
			groupBy(path("cr.name")),
			nullExpression(),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_200() {

		// SELECT cr.name, COUNT (res)
		// FROM Cruise cr LEFT JOIN cr.reservations res
		// GROUP BY cr.name
		// HAVING count(res) > 10

		return selectStatement(
			select(path("cr.name"), count(variable("res"))),
			from("Cruise", "cr", leftJoin("cr.reservations", "res")),
			nullExpression(),
			groupBy(path("cr.name")),
			having(count(variable("res")).greaterThan(numeric(10))),
			nullExpression()
		);
	}

	public static StateObjectTester stateObject_201() {

		// SELECT COUNT (res)
		// FROM Reservation res
		// WHERE res.amountPaid >
		//       (SELECT avg(r.amountPaid) FROM Reservation r)

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_202() {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 100000 < (
		//    SELECT SUM(res.amountPaid) FROM cr.reservations res
		// )

		return selectStatement(
			select(variable("cr")),
			from("Cruise", "cr"),
			where(
					numeric(100000)
				.lowerThan(
					sub(
						subquery(
							subSelect(sum("res.amountPaid")),
							subFrom(fromDerivedPath("cr.reservations", "res"))
						)
					)
				)
			)
		);
	}

	public static StateObjectTester stateObject_203() {

		// SELECT cr
		// FROM Cruise cr
		// WHERE 0 < ALL (
		//   SELECT res.amountPaid from cr.reservations res
		// )

		return selectStatement(
			select(variable("cr")),
			from("Cruise", "cr"),
			where(
					numeric(0)
				.lowerThan(
					all(
						subquery(
							subSelect(path("res.amountPaid")),
							subFrom(fromDerivedPath("cr.reservations", "res"))
						)
					)
				)
			)
		);
	}

	public static StateObjectTester stateObject_204() {

		// UPDATE Reservation res
		// SET res.name = 'Pascal'
		// WHERE EXISTS (
		//    SELECT c
		//    FROM res.customers c
		//    WHERE c.firstName = 'Bill' AND c.lastName='Burke'
		// )

		return updateStatement(
			update("Reservation", "res", set(path("res.name"), string("'Pascal'"))),
			where(
				exists(
					subquery(
						subSelect(variable("c")),
						subFrom(fromDerivedPath("res.customers", "c")),
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
	}

	public static StateObjectTester stateObject_215() {

		// SELECT o
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity DESC, o.totalcost

		return selectStatement(
			select(variable("o")),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(path("a.state").equal(string("'CA'"))),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc("o.quantity"), orderByItem("o.totalcost"))
		);
	}

	public static StateObjectTester stateObject_216() {

		// SELECT o.quantity, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// ORDER BY o.quantity, a.zipcode

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_219() {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'

		return deleteStatement(
			"Customer", "c",
			where(path("c.status").equal(string("'inactive'")))
		);
	}

	public static StateObjectTester stateObject_220() {

		// DELETE
		// FROM Customer c
		// WHERE c.status = 'inactive'
		//       AND
		//       c.orders IS EMPTY

		return deleteStatement(
			"Customer", "c",
			where(
					path("c.status").equal(string("'inactive'"))
				.and(
					isEmpty("c.orders"))
			)
		);
	}

	public static StateObjectTester stateObject_221() {

		// UPDATE customer c
		// SET c.status = 'outstanding'
		// WHERE c.balance < 10000

		return updateStatement(
			update("customer", "c", set("c.status", string("'outstanding'"))),
			where(path("c.balance").lowerThan(numeric(10000)))
		);
	}

	public static StateObjectTester stateObject_228() {

		// Select e
		// from Employee e join e.phoneNumbers p
		// where    e.firstName = 'Bob'
		//      and e.lastName like 'Smith%'
		//      and e.address.city = 'Toronto'
		//      and p.areaCode <> '2'

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_229() {

		// Select e
		// From Employee e
		// Where Exists(Select a From e.address a Where a.zipCode = 27519)

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(
				subquery(
					subSelect(variable("a")),
					subFrom(fromDerivedPath("e.address", "a")),
					where(path("a.zipCode").equal(numeric(27519)))
				)
			))
		);
	}

	public static StateObjectTester stateObject_230() {

		// Select e
		// From Employee e
		// Where Exists(Select e.name
		//              From In e.phoneNumbers
		//              Where e.zipCode = 27519)

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
				exists(
					subquery(
						subSelect(path("e.name")),
						subFrom(subFromDerivedIn("e.phoneNumbers")),
						where(path("e.zipCode").equal(numeric(27519)))
					)
				)
			)
		);
	}
}