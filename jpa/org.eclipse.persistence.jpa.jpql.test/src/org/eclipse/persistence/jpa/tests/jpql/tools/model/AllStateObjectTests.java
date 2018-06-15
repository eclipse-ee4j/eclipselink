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
package org.eclipse.persistence.jpa.tests.jpql.tools.model;

import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The root test suite containing the unit-tests testing the {@link StateObject} API.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({

    // Test IJPQLQueryFormatter
    AllJPQLQueryFormatterTests.class,

    // Testing creating the state object manually
    AllManualCreationStateObjectTest1_0.class,
    AllManualCreationStateObjectTest2_0.class
})
@RunWith(JPQLTestRunner.class)
public final class AllStateObjectTests {

    private AllStateObjectTests() {
        super();
    }
}
