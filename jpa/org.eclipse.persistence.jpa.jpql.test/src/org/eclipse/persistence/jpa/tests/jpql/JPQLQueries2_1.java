/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * This class provides a list of queries that are written against the JPQL 2.1 grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueries2_1 {

	private JPQLQueries2_1() {
		super();
	}

	public static String query_001() {
		return "Select e " +
		       "From Employee e Join TREAT(e.projects AS LargeProject) lp " +
		       "Where lp.budget = :value";
	}

	public static String query_002() {
		return "Select e " +
		       "From Employee e Join TREAT(e.projects LargeProject) lp";
	}

	public static String query_003() {
		return "SELECT TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate " +
	          "FROM Product p";
	}
}