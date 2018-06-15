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
 * JPQL grammar defined in JPA 2.0 and the additional support provider by EclipseLink version 2.0,
 * 2.1, 2.2 and 2.3.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    EclipseLinkComparisonExpressionTest.class,
    EclipseLinkLikeExpressionTest.class,
    FuncExpressionTest.class,
    TreatExpressionTest.class
})
public final class EclipseLinkJPQLParserTests {

    private EclipseLinkJPQLParserTests() {
        super();
    }
}
