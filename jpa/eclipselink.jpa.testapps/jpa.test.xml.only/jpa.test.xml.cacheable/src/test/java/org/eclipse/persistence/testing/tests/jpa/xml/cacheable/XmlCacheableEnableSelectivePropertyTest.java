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

package org.eclipse.persistence.testing.tests.jpa.xml.cacheable;

import junit.framework.Test;
import junit.framework.TestSuite;

/*
 * The test is testing against "ENABLE_SELECTIVE" persistence unit which has <shared-cache-mode> to be ENABLE_SELECTIVE
 */
public class XmlCacheableEnableSelectivePropertyTest extends XmlCacheableTestBase {

    public XmlCacheableEnableSelectivePropertyTest() {
        super();
    }

    public XmlCacheableEnableSelectivePropertyTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "ENABLE_SELECTIVE-Property";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XmlCacheableEnableSelectivePropertyTest");

        suite.addTest(new XmlCacheableEnableSelectivePropertyTest("testSetup"));
        suite.addTest(new XmlCacheableEnableSelectivePropertyTest("testCachingOnENABLE_SELECTIVEProperty"));

        return suite;
    }

    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to ENABLE_SELECTIVE using jakarta.persistence.sharedCache.mode property.
     */
    public void testCachingOnENABLE_SELECTIVEProperty() {
        assertCachingOnENABLE_SELECTIVE(getPersistenceUnitServerSession());
    }
}
