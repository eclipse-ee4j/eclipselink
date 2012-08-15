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
 * This class provides a list of queries that are written against the EclipseLink 2.5 grammar.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLQueries2_5 {

	private EclipseLinkJPQLQueries2_5() {
		super();
	}

	public static String query_001() {
		return "SELECT e FROM Employee e CONNECT BY e.id = e.manager.id";
	}

	public static String query_002() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "CONNECT BY e.employee.id = e.manager.id AND" +
		       "           e.account_mgr_id = e.customer_id";
	}

	public static String query_003() {
		return "SELECT employee " +
		       "FROM Employee employee " +
		       "START WITH employee.id = 100 " +
		       "CONNECT BY employee.id = employee.manager.id " +
		       "ORDER BY employee.last_name";
	}

	public static String query_004() {
		return "SELECT e " +
		       "FROM Employee e " +
		       "AS OF TIMESTAMP FUNC('TO_TIMESTAMP', '2003-04-04 09:30:00', 'YYYY-MM-DD HH:MI:SS') " +
		       "WHERE e.name = 'JPQL'";
	}

	public static String query_005() {
		return "select e " +
		       "from Employee e " +
		       "as of scn 7920 " +
		       "where e.id = 222";
	}

	public static String query_006() {
		return "UPDATE DateTime SET timestamp = CURRENT_TIMESTAMP";
	}

	public static String query_007() {
		return "UPDATE DateTime SET scn = CURRENT_TIMESTAMP";
	}
}