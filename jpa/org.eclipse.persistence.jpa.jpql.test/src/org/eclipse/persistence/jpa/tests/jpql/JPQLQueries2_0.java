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
package org.eclipse.persistence.jpa.tests.jpql;

/**
 * This class provides a list of queries that are written against the JPQL 2.0 grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueries2_0 {

	private JPQLQueries2_0() {
		super();
	}

	public static String query_001() {
		return "UPDATE Employee e " +
		       "SET e.salary = " +
		       "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		       "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
		       "         ELSE e.salary * 1.01" +
		       "    END";
	}

	public static String query_002() {
		return "SELECT e.name, " +
		       "       CASE TYPE(e) WHEN Exempt THEN 'Exempt' " +
		       "                    WHEN Contractor THEN 'Contractor' " +
		       "                    WHEN Intern THEN 'Intern' " +
		       "                    ELSE 'NonExempt' " +
		       "       END " +
		       "FROM Employee e, Contractor c " +
		       "WHERE e.dept.name = 'Engineering'";
	}

	public static String query_003() {
		return "SELECT e.name, " +
		       "       f.name, " +
		       "       CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum '" +
		       "                   WHEN f.annualMiles > 25000 THEN 'Gold ' " +
		       "                   ELSE '' " +
		       "              END, " +
		       "              'Frequent Flyer') " +
		       "FROM Employee e JOIN e.frequentFlierPlan f";
	}

	public static String query_004() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) IN (Exempt, Contractor)";
	}

	public static String query_005() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) IN (:empType1, :empType2)";
	}

	public static String query_006() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "WHERE TYPE(e) IN :empTypes";
	}

	public static String query_007() {
		return "SELECT TYPE(employee) " +
		       "FROM Employee employee " +
		       "WHERE TYPE(employee) <> Exempt";
	}

	public static String query_008() {
		return "SELECT t " +
		       "FROM CreditCard c JOIN c.transactionHistory t " +
		       "WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9";
	}

	public static String query_009() {
		return "SELECT w.name " +
		       "FROM Course c JOIN c.studentWaitlist w " +
		       "WHERE c.name = 'Calculus' " +
		       "      AND " +
		       "      INDEX(w) = 0";
	}

	public static String query_010() {
		return "UPDATE Employee e " +
		       "SET e.salary = CASE e.rating WHEN 1 THEN e.salary * 1.1 " +
		       "                             WHEN 2 THEN e.salary * 1.05 " +
		       "                             ELSE e.salary * 1.01 " +
		       "               END";
	}

	public static String query_011() {
		return "SELECT o.quantity, o.cost*1.08 AS taxedCost, a.zipcode " +
		       "FROM Customer c JOIN c.orders o JOIN c.address a " +
		       "WHERE a.state = 'CA' AND a.county = 'Santa Clara' " +
		       "ORDER BY o.quantity, taxedCost, a.zipcode";
	}

	public static String query_012() {
		return "SELECT AVG(o.quantity) as q, a.zipcode " +
		       "FROM Customer c JOIN c.orders o JOIN c.address a " +
		       "WHERE a.state = 'CA' " +
		       "GROUP BY a.zipcode " +
		       "ORDER BY q DESC";
	}

	public static String query_013() {
		return "SELECT e.salary / 1000D n " +
		       "From Employee e";
	}

	public static String query_014() {
		return "SELECT MOD(a.id, 2) AS m " +
		       "FROM Address a JOIN FETCH a.customerList " +
		       "ORDER BY m, a.zipcode";
	}

	public static String query_015() {
		return "SELECT ENTRY(addr) FROM Alias a JOIN a.addresses addr";
	}

	public static String query_016() {
		return "SELECT p " +
		       "FROM Employee e JOIN e.projects p " +
		       "WHERE e.id = :id AND INDEX(p) = 1";
	}
}