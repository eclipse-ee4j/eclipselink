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
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.runners.Suite;

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in Jakarta Persistence 3.1.
 */
@Suite.SuiteClasses({
        // Test the parser with JPQL queries
        JPQLQueriesTest3_1.class,

})
public class JPQLParserTests3_1 {

    private JPQLParserTests3_1() {
        super();
    }

}
