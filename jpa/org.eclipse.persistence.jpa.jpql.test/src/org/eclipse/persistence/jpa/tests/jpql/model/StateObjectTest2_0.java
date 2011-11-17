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

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;

/**
 * The abstract definition
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class StateObjectTest2_0 extends AbstractStateObjectTest2_0 {

	@Test
	public void test_Query_139() throws Exception {

		// SELECT p
		// FROM Employee e JOIN e.projects p
		// WHERE e.id = :id AND INDEX(p) = 1

		testQuery(query_139(), stateObject_139());
	}

	@Test
	public void test_Query_205() throws Exception {

		// UPDATE Employee e
		// SET e.salary =
		//    CASE WHEN e.rating = 1 THEN e.salary * 1.1
		//         WHEN e.rating = 2 THEN e.salary * 1.05
		//         ELSE e.salary * 1.01
		//    END

		testQuery(query_205(), stateObject_205());
	}

	@Test
	public void test_Query_206() throws Exception {

		// SELECT e.name,
		//        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
		//                     WHEN Contractor THEN 'Contractor'
		//                     WHEN Intern THEN 'Intern'
		//                     ELSE 'NonExempt'
		//        END
		// FROM Employee e
		// WHERE e.dept.name = 'Engineering'

		testQuery(query_206(),  stateObject_206());
	}

	@Test
	public void test_Query_207() throws Exception {

		// SELECT e.name,
		//        f.name,
		//        CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '
		//                    WHEN f.annualMiles > 25000 THEN 'Gold '
		//                    ELSE ''
		//               END,
		//               'Frequent Flyer')
		// FROM Employee e JOIN e.frequentFlierPlan f

		testQuery(query_207(),  stateObject_207());
	}

	@Test
	public void test_Query_208() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (Exempt, Contractor)

		testQuery(query_208(),  stateObject_208());
	}

	@Test
	public void test_Query_209() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (:empType1, :empType2)

		testQuery(query_209(), stateObject_209());
	}

	@Test
	public void test_Query_210() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN :empTypes

		testQuery(query_210(), stateObject_210());
	}

	@Test
	public void test_Query_211() throws Exception {

		// SELECT TYPE(e)
		// FROM Employee e
		// WHERE TYPE(e) <> Exempt

		testQuery(query_211(), stateObject_211());
	}

	@Test
	public void test_Query_212() throws Exception {

		// SELECT t
		// FROM CreditCard c JOIN c.transactionHistory t
		// WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9

		testQuery(query_212(), stateObject_212());
	}

	@Test
	public void test_Query_213() throws Exception {

		// SELECT w.name
		// FROM Course c JOIN c.studentWaitlist w
		// WHERE c.name = 'Calculus'
		//       AND
		//       INDEX(w) = 0

		testQuery(query_213(), stateObject_213());
	}

	@Test
	public void test_Query_214() throws Exception {

		// UPDATE Employee e
		// SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1
		//                              WHEN 2 THEN e.salary * 1.05
		//                              ELSE e.salary * 1.01
		//                END

		testQuery(query_214(),  stateObject_214());
	}

	@Test
	public void test_Query_217() throws Exception {

		// SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA' AND a.county = 'Santa Clara'
		// ORDER BY o.quantity, taxedCost, a.zipcode

		testQuery(query_217(),  stateObject_217());
	}

	@Test
	public void test_Query_218() throws Exception {

		// SELECT AVG(o.quantity) as q, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// GROUP BY a.zipcode
		// ORDER BY q DESC";

		testQuery(query_218(),  stateObject_218());
	}

	@Test
	public void test_Query_222() throws Exception {

		// SELECT e.salary / 1000D n
		// From Employee e

		testQuery(query_222(),  stateObject_222());
	}

	@Test
	public void test_Query_223() throws Exception {

		// SELECT MOD(a.id, 2) AS m
		// FROM Address a JOIN FETCH a.customerList
		// ORDER BY m, a.zipcode

		testQuery(query_223(),  stateObject_223());
	}
}