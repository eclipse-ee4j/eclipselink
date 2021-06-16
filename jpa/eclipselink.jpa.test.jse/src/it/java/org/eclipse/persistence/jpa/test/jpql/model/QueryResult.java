/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     05/11/2018-2.7 Will Dazey
//       - 534515: Incorrect return type set for CASE functions
package org.eclipse.persistence.jpa.test.jpql.model;

public class QueryResult {
        String str1;
        String str2;
        String str3;
        String str4;
        Long integer1;
        Long integer2;

        public QueryResult(String str1, String str2, String str3, String str4) {
            this.str1 = str1;
            this.str2 = str2;
            this.str3 = str3;
            this.str4 = str4;
        }

        public QueryResult(String str1, String str2, Long integer1, Long integer2) {
            this.str1 = str1;
            this.str2 = str2;
            this.integer1 = integer1;
            this.integer2 = integer2;
        }
}
