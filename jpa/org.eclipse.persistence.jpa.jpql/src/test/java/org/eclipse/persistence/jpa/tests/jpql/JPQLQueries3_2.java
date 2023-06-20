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
}
