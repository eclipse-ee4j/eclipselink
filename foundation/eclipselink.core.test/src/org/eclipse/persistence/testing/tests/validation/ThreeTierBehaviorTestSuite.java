/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

/**
 * This type was created in VisualAge.
 */
public class ThreeTierBehaviorTestSuite extends org.eclipse.persistence.testing.framework.TestSuite {
    /**
     * ThreeTierBehaviorTestSuite constructor comment.
     */
    public ThreeTierBehaviorTestSuite() {
        super();
        setDescription("This test suite contains tests that verifiy the different behaviors of the three tier components");
        setName("ThreeTierBehaviorTestSuite");
    }

    public void addTests() {
        addTest(new ClientSessionConstructorValidationTest());
        addTest(new ServerSessionReadPoolTypeTest());
    }
}
