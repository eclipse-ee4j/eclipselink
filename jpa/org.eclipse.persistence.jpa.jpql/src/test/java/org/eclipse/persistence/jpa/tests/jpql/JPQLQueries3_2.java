/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.tests.jpql;

/**
 * This class provides a list of queries that are written against the JPQL 3.2 grammar.
 */
public class JPQLQueries3_2 {

    private JPQLQueries3_2() {
        super();
    }

    public static String query_ConcatPipes_Select01() {
        return "SELECT c.firstName || 'Smith' FROM Customer c";
    }

    public static String query_ConcatPipes_Select02() {
        return "SELECT c.firstName || c.lastName FROM Customer c";
    }

    public static String query_ConcatPipes_Select_Chained() {
        return "SELECT c.firstName || 'Francis' || 'Smith' FROM Customer c";
    }

    public static String query_ConcatPipes_Where() {
        return "SELECT c FROM Customer c WHERE c.firstName || 'Smith' = 'JohnSmith'";
    }

    public static String query_ReplaceFunction_Select01() {
        return "SELECT REPLACE('Hello Vorld', 'V', 'W') FROM Customer c";
    }

    public static String query_ReplaceFunction_Select02() {
        return "SELECT REPLACE('Hella Warld', 'a', 'o') FROM Customer c";
    }

    public static String query_ReplaceFunction_Select03() {
        return "SELECT REPLACE(c.firstName, 'a', 'o') FROM Customer c";
    }

    public static String query_ReplaceFunction_Where() {
        return "SELECT c FROM Customer c WHERE REPLACE(c.firstName, 'o', 'a') = 'Jahn'";
    }

    public static String query_LeftFunction_Select01() {
        return "SELECT LEFT('Hello World', 5) FROM Customer c";
    }

    public static String query_LeftFunction_Select02() {
        return "SELECT LEFT(LEFT('Hello World', 5), 2) FROM Customer c";
    }

    public static String query_LeftFunction_Select03() {
        return "SELECT LEFT(c.firstName, 2) FROM Customer c";
    }

    public static String query_LeftFunction_Select04() {
        return "SELECT LEFT(cr.name, 5), COUNT (res) " +
                "FROM Cruise cr LEFT JOIN cr.reservations res " +
                "GROUP BY cr.name " +
                "HAVING count(res) > 10";
    }

    public static String query_LeftFunction_Where() {
        return "SELECT c FROM Customer c WHERE LEFT(c.firstName, 4) = 'John'";
    }

    public static String query_RightFunction_Select01() {
        return "SELECT RIGHT('Hello World', 5) FROM Customer c";
    }

    public static String query_RightFunction_Select02() {
        return "SELECT RIGHT(RIGHT('Hello World', 5), 2) FROM Customer c";
    }

    public static String query_RightFunction_Select03() {
        return "SELECT RIGHT(c.firstName, 2) FROM Customer c";
    }

    public static String query_RightFunction_Where() {
        return "SELECT c FROM Customer c WHERE RIGHT(c.firstName, 4) = 'John'";
    }

    public static String query_Union01() {
        return "Select a from Address a where a.city = 'Ottawa' " +
                "union Select a2 from Address a2 " +
                "union all Select a2 from Address a2 " +
                "intersect Select a from Address a where a.city = 'Ottawa' " +
                "except Select a from Address a where a.city = 'Ottawa'";
    }

}
