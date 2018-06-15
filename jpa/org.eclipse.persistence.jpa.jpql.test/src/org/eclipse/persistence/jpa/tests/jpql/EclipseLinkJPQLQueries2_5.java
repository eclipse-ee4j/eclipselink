/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
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
        return "SELECT e FROM Employee e CONNECT BY e.managers";
    }

    public static String query_002() {
        return "SELECT employee " +
               "FROM Employee employee " +
               "START WITH employee.id = 100 " +
               "CONNECT BY employee.employees " +
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
