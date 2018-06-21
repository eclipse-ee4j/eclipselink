/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql;

/**
 * This class provides a list of queries that are written against the EclipseLink 2.4 grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLQueries2_4 {

    private EclipseLinkJPQLQueries2_4() {
        super();
    }

    public static String query_001() {
        return "SELECT FUNC('NVL', e.firstName, 'NoFirstName'), " +
               "       func('NVL', e.lastName, 'NoLastName')    " +
               "FROM Employee e";
    }

    public static String query_002() {
        return "SELECT a " +
               "FROM Asset a, Geography selectedGeography " +
               "WHERE selectedGeography.id = :id AND " +
               "      a.id IN (:id_list) AND " +
               "      FUNC('ST_Intersects', a.geometry, selectedGeography.geometry) = 'TRUE'";
    }

    public static String query_003() {
        return "Select cast(e.firstName as char) " +
               "from Employee e " +
               "where cast(e.firstName as char) = 'Bob'";
    }

    public static String query_004() {
        return "Select cast(e.firstName as char(3)) " +
               "from Employee e " +
               "where cast(e.firstName as char(3)) = 'Bob'";
    }

    public static String query_005() {
        return "Select cast(e.firstName NUMERIC(5, 4)) from Employee e";
    }

    public static String query_006() {
        return "Select cast(e.firstName timestamp) from Employee e";
    }

    public static String query_007() {
        return "Select cast(e.firstName YEAR()) from Employee e";
    }

    public static String query_008() {
        return "Select cast(e.firstName as TIMESTAMP()) from Employee e";
    }

    public static String query_009() {
        return "Select e, e2 from Employee e left join Employee e2 on e.address = e2.address";
    }

    public static String query_010() {
        return "Select avg(sal.salary) " +
                "from (Select max(e.salary) salary, e.address.city city" +
                "      from Employee e" +
                "      group by e.address.city)" +
                "     sal";
    }

    public static String query_011() {
        return "Select addr " +
               "from (Select e from Employee e) a JOIN a.address addr";
    }

    public static String query_012() {
        return "Select addr " +
               "from (Select e from Employee e) as a JOIN a.address addr";
    }

    public static String query_013() {
        return "Select a from Address a where a.city = 'Ottawa' " +
               "union Select a2 from Address a2 " +
               "union all Select a2 from Address a2 " +
               "intersect Select a from Address a where a.city = 'Ottawa' " +
               "except Select a from Address a where a.city = 'Ottawa'";
    }

    public static String query_014() {
        return "Select e from Employee e where e.firstName regexp '^B.*'";
    }

    public static String query_015() {
        return "Select e from Employee e, table('TENANTS') t where e.tenant = t.TENANT and t.ACTIVE = true";
    }

    public static String query_016() {
        return "Update Employee set name='JPQL' WHERE column!='value'";
    }

    public static String query_017() {
        return "select DISTINCT NEW com.ca.waae.dbaccess.dao.MetaPropDef(p.id.metaId, p.jilName, p.dbTable, p.dbColumn, p.dbType) " +
               "FROM UjoMetaProperty p " +
               "WHERE p.dbTable!='tablename' " +
               "ORDER BY p.id.metaId";
    }
}
