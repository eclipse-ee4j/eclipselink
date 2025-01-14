/*
 * Copyright (c) 2022, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CacheableNonePropertyTest extends CacheableTestBase {

    public CacheableNonePropertyTest() {
        super();
    }

    public CacheableNonePropertyTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "NONE-Property";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableNonePropertyTest");

        suite.addTest(new CacheableNonePropertyTest("testSetup"));
        suite.addTest(new CacheableNonePropertyTest("testCachingOnNONEProperty"));
        suite.addTest(new CacheableNonePropertyTest("testDetailsOrder_Isolated"));
        suite.addTest(new CacheableNonePropertyTest("testDetailsOrder_Isolated_BeginEarlyTransaction"));
        return suite;
    }


    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to NONE using jakarta.persistence.sharedCache.mode property.
     */
    public void testCachingOnNONEProperty() {
        assertCachingOnNONE(getPersistenceUnitServerSession());
    }
}
