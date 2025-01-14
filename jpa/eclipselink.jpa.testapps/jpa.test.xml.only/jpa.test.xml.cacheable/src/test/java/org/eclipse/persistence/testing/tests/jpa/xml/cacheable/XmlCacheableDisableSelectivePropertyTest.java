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
 * The test is testing against "DISABLE_SELECTIVE" persistence unit which has <shared-cache-mode> to be DISABLE_SELECTIVE
 */
public class XmlCacheableDisableSelectivePropertyTest extends XmlCacheableTestBase {

    public XmlCacheableDisableSelectivePropertyTest() {
        super();
    }

    public XmlCacheableDisableSelectivePropertyTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "DISABLE_SELECTIVE-Property";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XmlCacheableDisableSelectivePropertyTest");

        suite.addTest(new XmlCacheableDisableSelectivePropertyTest("testSetup"));
        suite.addTest(new XmlCacheableDisableSelectivePropertyTest("testCachingOnDISABLE_SELECTIVEProperty"));
        suite.addTest(new XmlCacheableDisableSelectivePropertyTest("testProtectedRelationshipsMetadata"));

        return suite;
    }

    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to DISABLE_SELECTIVE using jakarta.persistence.sharedCache.mode property.
     */
    public void testCachingOnDISABLE_SELECTIVEProperty() {
        assertCachingOnDISABLE_SELECTIVE(getPersistenceUnitServerSession());
    }
}
