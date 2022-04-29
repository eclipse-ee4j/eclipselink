/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for JPA 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.tests.jpql;

/**
 * This class provides a list of queries that are written against the JPQL 3.1 grammar.
 */
public class JPQLQueries3_1 {

    private JPQLQueries3_1() {
        super();
    }

    public static String query_Ceiling() {
        return "SELECT o.totalPrice, CEILING(o.totalPrice) FROM Order o";
    }

    public static String query_Floor() {
        return "SELECT o.totalPrice, FLOOR(o.totalPrice) FROM Order o";
    }

    public static String query_Exp() {
        return "SELECT o.totalPrice, EXP(o.totalPrice) FROM Order o";
    }

    public static String query_Ln() {
        return "SELECT o.totalPrice, LN(o.totalPrice) FROM Order o";
    }

    public static String query_Sign() {
        return "SELECT o.totalPrice, SIGN(o.totalPrice) FROM Order o";
    }

    public static String query_Power() {
        return "SELECT o.totalPrice, POWER(o.totalPrice, 2) FROM Order o";
    }

    public static String query_Round() {
        return "SELECT o.totalPrice, ROUND(o.totalPrice, 1) FROM Order o";
    }

}
