/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in JPA 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({

    // Test the parser with hundreds of JPQL queries
    JPQLQueriesTest2_0.class,

    // Individual unit-tests
    JPQLExpressionTest1_0.class,
    CaseExpressionTest.class,
    CoalesceExpressionTest.class,
    IndexExpressionTest.class,
    NullIfExpressionTest.class,
    ResultVariableTest.class,
    TypeExpressionTest.class,
})
public final class JPQLParserTests2_0 {

    private JPQLParserTests2_0() {
        super();
    }
}
