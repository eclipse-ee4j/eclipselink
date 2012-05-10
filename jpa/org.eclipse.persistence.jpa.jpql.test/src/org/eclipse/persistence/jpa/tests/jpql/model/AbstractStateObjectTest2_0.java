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
package org.eclipse.persistence.jpa.tests.jpql.model;

/**
 * The abstract definition
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractStateObjectTest2_0 extends AbstractStateObjectTest {

	public static StateObjectTester stateObject_014() {

		// SELECT ENTRY(addr) FROM Alias a JOIN a.addresses addr

		return selectStatement(
			select(entry("addr")),
			from("Alias", "a", join("a.addresses", "addr"))
		);
	}

	public static StateObjectTester stateObject_139() {

		// SELECT p
		// FROM Employee e JOIN e.projects p
		// WHERE e.id = :id AND INDEX(p) = 1

		return selectStatement(
			select(variable("p")),
			from("Employee", "e", join("e.projects", "p")),
			where(path("e.id").equal(inputParameter(":id")).and(index("p").equal(numeric(1))))
		);
	}

	public static StateObjectTester stateObject_205() throws Exception {

		// UPDATE Employee e
		// SET e.salary =
		//    CASE WHEN e.rating = 1 THEN e.salary * 1.1
		//         WHEN e.rating = 2 THEN e.salary * 1.05
		//         ELSE e.salary * 1.01
		//    END

		return updateStatement(
			update("Employee", "e", set(
				path("e.salary"),
				case_(
					when(path("e.rating").equal(numeric(1)),
					     path("e.salary").multiplication(numeric(1.1))
					),
					when(path("e.rating").equal(numeric(2)),
					     path("e.salary").multiplication(numeric(1.05))
					),
					path("e.salary").multiplication(numeric(1.01))
				)
			))
		);
	}

	public static StateObjectTester stateObject_206() throws Exception {

		// SELECT e.name,
		//        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
		//                     WHEN Contractor THEN 'Contractor'
		//                     WHEN Intern THEN 'Intern'
		//                     ELSE 'NonExempt'
		//        END
		// FROM Employee e
		// WHERE e.dept.name = 'Engineering'

		return selectStatement(
			select(
				path("e.name"),
				case_(
					type("e"),
					new StateObjectTester[] {
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
	}

	public static StateObjectTester stateObject_207() throws Exception {

		// SELECT e.name,
		//        f.name,
		//        CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '
		//                    WHEN f.annualMiles > 25000 THEN 'Gold '
		//                    ELSE ''
		//               END,
		//               'Frequent Flyer')
		// FROM Employee e JOIN e.frequentFlierPlan f

		return selectStatement(
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
	}

	public static StateObjectTester stateObject_208() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (Exempt, Contractor)

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type("e").in(entity("Exempt"), entity("Contractor")))
		);
	}

	public static StateObjectTester stateObject_209() {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (:empType1, :empType2)

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type("e").in(inputParameter(":empType1"), inputParameter(":empType2")))
		);
	}

	public static StateObjectTester stateObject_210() {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN :empTypes

		InExpressionStateObjectTester inExpression = in(
			type("e"),
			inputParameter(":empTypes")
		);

		return selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(inExpression)
		);
	}

	public static StateObjectTester stateObject_211() {

		// SELECT TYPE(employee)
		// FROM Employee employee
		// WHERE TYPE(employee) <> Exempt

		return selectStatement(
			select(type("employee")),
			from("Employee", "employee"),
			where(type("employee").different(variable("Exempt")))
		);
	}

	public static StateObjectTester stateObject_212() {

		// SELECT t
		// FROM CreditCard c JOIN c.transactionHistory t
		// WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9

		return selectStatement(
			select(variable("t")),
			from("CreditCard", "c", join("c.transactionHistory", "t")),
			where(
					path("c.holder.name").equal(string("'John Doe'"))
				.and(
					index("t").between(numeric(0), numeric(9))))
		);
	}

	public static StateObjectTester stateObject_213() {

		// SELECT w.name
		// FROM Course c JOIN c.studentWaitlist w
		// WHERE c.name = 'Calculus'
		//       AND
		//       INDEX(w) = 0

		return selectStatement(
			select(path("w.name")),
			from("Course", "c", join("c.studentWaitlist", "w")),
			where(
					path("c.name").equal(string("'Calculus'"))
				.and(
					index("w").equal(numeric(0))))
		);
	}

	public static StateObjectTester stateObject_214() throws Exception {

		// UPDATE Employee e
		// SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1
		//                              WHEN 2 THEN e.salary * 1.05
		//                              ELSE e.salary * 1.01
		//                END

		return updateStatement(
			update(
				"Employee", "e",
				set("e.salary", case_(
					path("e.rating"),
					new StateObjectTester[] {
						when(numeric(1), path("e.salary").multiplication(numeric(1.1))),
						when(numeric(2), path("e.salary").multiplication(numeric(1.05))),
					},
					path("e.salary").multiplication(numeric(1.01))
				))
			)
		);
	}

	public static StateObjectTester stateObject_217() throws Exception {

		// SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA' AND a.county = 'Santa Clara'
		// ORDER BY o.quantity, taxedCost, a.zipcode

		return selectStatement(
			select(
				path("o.quantity"),
				selectItemAs(path("o.cost").multiplication(numeric(1.08)), "taxedCost"),
				path("a.zipcode")
			),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(
					path("a.state").equal(string("'CA'"))
				.and(
					path("a.county").equal(string("'Santa Clara'"))
				)
			),
			nullExpression(),
			nullExpression(),
			orderBy(
				orderByItem("o.quantity"),
				orderByItem(variable("taxedCost")),
				orderByItem("a.zipcode")
			)
		);
	}

	public static StateObjectTester stateObject_218() throws Exception {

		// SELECT AVG(o.quantity) as q, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// GROUP BY a.zipcode
		// ORDER BY q DESC";

		return selectStatement(
			select(
				selectItemAs(avg("o.quantity"), "q"),
				path("a.zipcode")
			),
			from("Customer", "c", join("c.orders", "o"), join("c.address", "a")),
			where(path("a.state").equal(string("'CA'"))),
			groupBy(path("a.zipcode")),
			nullExpression(),
			orderBy(orderByItemDesc(variable("q")))
		);
	}

	public static StateObjectTester stateObject_222() throws Exception {

		// SELECT e.salary / 1000D n
		// From Employee e

		return selectStatement(
			select(selectItem(path("e.salary").division(numeric("1000D")), "n")),
			from("Employee", "e")
		);
	}

	public static StateObjectTester stateObject_223() throws Exception {

		// SELECT MOD(a.id, 2) AS m
		// FROM Address a JOIN FETCH a.customerList
		// ORDER BY m, a.zipcode

		return selectStatement(
			select(selectItemAs(mod(path("a.id"), numeric(2)), "m")),
			from("Address", "a", joinFetch("a.customerList")),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(
				orderByItem(variable("m")),
				orderByItem(path("a.zipcode"))
			)
		);
	}
}