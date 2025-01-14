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
 * The test is testing against "DISABLE_SELECTIVE" persistence unit which has <shared-cache-mode> to be DISABLE_SELECTIVE
 */
public class CacheableDisableSelectivePropertyTest extends CacheableTestBase {

    public CacheableDisableSelectivePropertyTest() {
        super();
    }

    public CacheableDisableSelectivePropertyTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "DISABLE_SELECTIVE-Property";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableDisableSelectivePropertyTest");

        suite.addTest(new CacheableDisableSelectivePropertyTest("testSetup"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testCachingOnDISABLE_SELECTIVEProperty"));

        // Test cache retrieve mode of BYPASS and USE through the EM.
        suite.addTest(new CacheableDisableSelectivePropertyTest("testCreateEntities"));

        suite.addTest(new CacheableDisableSelectivePropertyTest("testFindWithEMProperties"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testFindWithFindProperties"));

        suite.addTest(new CacheableDisableSelectivePropertyTest("testRefreshWithEMProperties"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRefreshWithRefreshProperties"));

        // Test various usage scenarios ..
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRetrieveBYPASSStoreUSE1"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRetrieveBYPASSStoreUSE2"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRetrieveUSEStoreBYPASS1"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRetrieveUSEStoreBYPASS2"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRetrieveBYPASSStoreBYPASS1"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRetrieveBYPASSStoreBYPASS2"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testMultipleEMQueries"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testEMPropertiesOnCommit1"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testEMPropertiesOnCommit2"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testInheritanceCacheable"));

        suite.addTest(new CacheableDisableSelectivePropertyTest("testLoadMixedCacheTree"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testIsolatedIsolation"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testProtectedIsolation"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testProtectedCaching"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testProtectedIsolationWithLockOnCloneFalse"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testReadOnlyTree"));

        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateForceProtectedBasic"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateForceProtectedOneToOne"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateProtectedBasic"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateProtectedOneToMany"));

        suite.addTest(new CacheableDisableSelectivePropertyTest("testProtectedRelationshipsMetadata"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testForceProtectedFromEmbeddable"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testEmbeddableProtectedCaching"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testEmbeddableProtectedReadOnly"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateProtectedManyToOne"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateProtectedManyToMany"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateProtectedElementCollection"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testIsolationBeforeEarlyTxBegin"));

        // Bug 340074
        suite.addTest(new CacheableDisableSelectivePropertyTest("testFindWithLegacyFindProperties"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testFindWithEMLegacyProperties"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testMergeNonCachedWithRelationship"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testIndirectCollectionRefreshBehavior"));
        suite.addTest(new CacheableDisableSelectivePropertyTest("testDerivedIDProtectedRead"));

        // Bug 408262
        suite.addTest(new CacheableDisableSelectivePropertyTest("testRefreshProtectedEntityInEarlyTransaction"));

        // Bug 530680
        suite.addTest(new CacheableDisableSelectivePropertyTest("testUpdateSharedElementCollection"));

        return suite;
    }

    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to DISABLE_SELECTIVE using jakarta.persistence.sharedCache.mode property.
     */
    public void testCachingOnDISABLE_SELECTIVEProperty() {
        assertCachingOnDISABLE_SELECTIVE(getPersistenceUnitServerSession());
    }
}
