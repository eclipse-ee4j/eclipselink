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

public class CacheableNonePropertyConflictTest extends CacheableTestBase {

    public CacheableNonePropertyConflictTest() {
        super();
    }

    public CacheableNonePropertyConflictTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "NONE-Property-Conflict";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableNonePropertyConflictTest");

        suite.addTest(new CacheableNonePropertyConflictTest("testSetup"));
        suite.addTest(new CacheableNonePropertyConflictTest("testCachingOnNONEPropertyConflict"));
        return suite;
    }

    /**
     * Verifies that when the {@code <shared-cache-mode>} and jakarta.persistence.sharedCache.mode property
     * are set, the jakarta.persistence.sharedCache.mode property will win. In the persistence.xml,
     * jakarta.persistence.sharedCache.mode property is set to NONE while {@code <shared-cache-mode>} is set to
     * ALL.
     */
    public void testCachingOnNONEPropertyConflict() {
        assertCachingOnNONE(getPersistenceUnitServerSession());
    }

}
