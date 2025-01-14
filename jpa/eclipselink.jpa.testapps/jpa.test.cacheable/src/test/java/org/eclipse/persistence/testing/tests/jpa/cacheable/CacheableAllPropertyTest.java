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

package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CacheableAllPropertyTest extends CacheableTestBase {

    public CacheableAllPropertyTest() {
        super();
    }

    public CacheableAllPropertyTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "ALL-Property";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableAllPropertyTest");

        suite.addTest(new CacheableAllPropertyTest("testSetup"));
        suite.addTest(new CacheableAllPropertyTest("testCachingOnALLProperty"));
        suite.addTest(new CacheableAllPropertyTest("testDetailsOrder_Shared"));
        suite.addTest(new CacheableAllPropertyTest("testDetailsOrder_Shared_BeginEarlyTransaction"));
        return suite;
    }

    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to ALLProperty using jakarta.persistence.sharedCache.mode property.
     */
    public void testCachingOnALLProperty() {
        assertCachingOnALL(getPersistenceUnitServerSession());
    }

}
