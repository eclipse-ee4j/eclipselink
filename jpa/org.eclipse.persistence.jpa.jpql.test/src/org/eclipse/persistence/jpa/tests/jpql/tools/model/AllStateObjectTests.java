/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
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
