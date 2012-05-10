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

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_0.*;

/**
 * This tests the automatic creation by the builder of a {@link StateObject} by converting the
 * parsed representation of a JPQL query using the JPQL grammar defined in JPA 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class StateObjectTest2_0 extends AbstractStateObjectTest2_0 {

	@Test
	public void test_Query_001() throws Exception {

		// UPDATE Employee e
		// SET e.salary =
		//    CASE WHEN e.rating = 1 THEN e.salary * 1.1
		//         WHEN e.rating = 2 THEN e.salary * 1.05
		//         ELSE e.salary * 1.01
		//    END

		testQuery(query_001(), stateObject_205());
	}

	@Test
	public void test_Query_002() throws Exception {

		// SELECT e.name,
		//        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
		//                     WHEN Contractor THEN 'Contractor'
		//                     WHEN Intern THEN 'Intern'
		//                     ELSE 'NonExempt'
		//        END
		// FROM Employee e, Contractor c
		// WHERE e.dept.name = 'Engineering'

		testQuery(query_002(), stateObject_206());
	}

	@Test
	public void test_Query_003() throws Exception {

		// SELECT e.name,
		//        f.name,
		//        CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '
		//                    WHEN f.annualMiles > 25000 THEN 'Gold '
		//                    ELSE ''
		//               END,
		//               'Frequent Flyer')
		// FROM Employee e JOIN e.frequentFlierPlan f

		testQuery(query_003(), stateObject_207());
	}

	@Test
	public void test_Query_004() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (Exempt, Contractor)

		testQuery(query_004(), stateObject_208());
	}

	@Test
	public void test_Query_005() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN (:empType1, :empType2)

		testQuery(query_005(), stateObject_209());
	}

	@Test
	public void test_Query_006() throws Exception {

		// SELECT e
		// FROM Employee e
		// WHERE TYPE(e) IN :empTypes

		testQuery(query_006(), stateObject_210());
	}

	@Test
	public void test_Query_007() throws Exception {

		// SELECT TYPE(employee)
		// FROM Employee employee
		// WHERE TYPE(employee) <> Exempt

		testQuery(query_007(), stateObject_211());
	}

	@Test
	public void test_Query_008() throws Exception {

		// SELECT t
		// FROM CreditCard c JOIN c.transactionHistory t
		// WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9

		testQuery(query_008(), stateObject_212());
	}

	@Test
	public void test_Query_009() throws Exception {

		// SELECT w.name
		// FROM Course c JOIN c.studentWaitlist w
		// WHERE c.name = 'Calculus'
		//       AND
		//       INDEX(w) = 0

		testQuery(query_009(), stateObject_213());
	}

	@Test
	public void test_Query_010() throws Exception {

		// UPDATE Employee e
		// SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1
		//                              WHEN 2 THEN e.salary * 1.05
		//                              ELSE e.salary * 1.01
		//                END

		testQuery(query_010(), stateObject_214());
	}

	@Test
	public void test_Query_011() throws Exception {

		// SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA' AND a.county = 'Santa Clara'
		// ORDER BY o.quantity, taxedCost, a.zipcode

		testQuery(query_011(), stateObject_217());
	}

	@Test
	public void test_Query_012() throws Exception {

		// SELECT AVG(o.quantity) as q, a.zipcode
		// FROM Customer c JOIN c.orders o JOIN c.address a
		// WHERE a.state = 'CA'
		// GROUP BY a.zipcode
		// ORDER BY q DESC";

		testQuery(query_012(), stateObject_218());
	}

	@Test
	public void test_Query_013() throws Exception {

		// SELECT e.salary / 1000D n
		// From Employee e

		testQuery(query_013(), stateObject_222());
	}

	@Test
	public void test_Query_014() throws Exception {

		// SELECT MOD(a.id, 2) AS m
		// FROM Address a JOIN FETCH a.customerList
		// ORDER BY m, a.zipcode

		testQuery(query_014(), stateObject_223());
	}

	@Test
	public void test_Query_015() throws Exception {

		// SELECT ENTRY(addr) FROM Alias a JOIN a.addresses addr

		testQuery(query_015(), stateObject_014());
	}

	@Test
	public void test_Query_016() throws Exception {

		// SELECT p
		// FROM Employee e JOIN e.projects p
		// WHERE e.id = :id AND INDEX(p) = 1

		testQuery(query_016(), stateObject_139());
	}
}