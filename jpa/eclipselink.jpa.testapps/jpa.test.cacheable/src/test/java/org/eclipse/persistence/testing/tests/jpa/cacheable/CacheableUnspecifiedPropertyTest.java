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

/*
 * The test is testing against "UNSPECIFIED" persistence unit which has <shared-cache-mode> to be UNSPECIFIED
 */
public class CacheableUnspecifiedPropertyTest extends CacheableTestBase {

    public CacheableUnspecifiedPropertyTest() {
        super();
    }

    public CacheableUnspecifiedPropertyTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "UNSPECIFIED-Property";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableUnspecifiedPropertyTest");

        suite.addTest(new CacheableUnspecifiedPropertyTest("testSetup"));
        suite.addTest(new CacheableUnspecifiedPropertyTest("testCachingOnUNSPECIFIEDProperty"));

        return suite;
    }

    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to UNSPECIFIED using jakarta.persistence.sharedCache.mode property.
     */
    public void testCachingOnUNSPECIFIEDProperty() {
        assertCachingOnUNSPECIFIED(getPersistenceUnitServerSession());
    }

}
