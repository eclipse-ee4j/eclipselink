/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
