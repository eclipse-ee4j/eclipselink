/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in JPA 2.1.
 *
 * @version 2.4
 * @since 2.4
 */
@SuiteClasses({

    // Test the parser with hundreds of JPQL queries
    JPQLQueriesTest2_1.class,

    // Individual unit-tests
    FunctionExpressionTest.class,
    OnClauseTest.class,
    TreatExpressionTest.class
})
public final class JPQLParserTests2_1 {

    private JPQLParserTests2_1() {
        super();
    }
}
