/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     07/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     06/09/2010-2.0.3 Guy Pelletier
//       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.Test;
import junit.framework.TestSuite;

/*
 * The test is testing against "DISABLE_SELECTIVE" persistence unit which has <shared-cache-mode> to be DISABLE_SELECTIVE
 */
public class CacheableDisableSelectiveTest extends CacheableTestBase {

    public CacheableDisableSelectiveTest() {
        super();
    }

    public CacheableDisableSelectiveTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "DISABLE_SELECTIVE";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableDisableSelectiveTest");

        suite.addTest(new CacheableDisableSelectiveTest("testSetup"));
        suite.addTest(new CacheableDisableSelectiveTest("testCachingOnDISABLE_SELECTIVE"));

        // Test cache retrieve mode of BYPASS and USE through the EM.
        suite.addTest(new CacheableDisableSelectiveTest("testCreateEntities"));

        suite.addTest(new CacheableDisableSelectiveTest("testFindWithEMProperties"));
        suite.addTest(new CacheableDisableSelectiveTest("testFindWithFindProperties"));

        suite.addTest(new CacheableDisableSelectiveTest("testRefreshWithEMProperties"));
        suite.addTest(new CacheableDisableSelectiveTest("testRefreshWithRefreshProperties"));

        // Test various usage scenarios ..
        suite.addTest(new CacheableDisableSelectiveTest("testRetrieveBYPASSStoreUSE1"));
        suite.addTest(new CacheableDisableSelectiveTest("testRetrieveBYPASSStoreUSE2"));
        suite.addTest(new CacheableDisableSelectiveTest("testRetrieveUSEStoreBYPASS1"));
        suite.addTest(new CacheableDisableSelectiveTest("testRetrieveUSEStoreBYPASS2"));
        suite.addTest(new CacheableDisableSelectiveTest("testRetrieveBYPASSStoreBYPASS1"));
        suite.addTest(new CacheableDisableSelectiveTest("testRetrieveBYPASSStoreBYPASS2"));
        suite.addTest(new CacheableDisableSelectiveTest("testMultipleEMQueries"));
        suite.addTest(new CacheableDisableSelectiveTest("testEMPropertiesOnCommit1"));
        suite.addTest(new CacheableDisableSelectiveTest("testEMPropertiesOnCommit2"));
        suite.addTest(new CacheableDisableSelectiveTest("testInheritanceCacheable"));

        suite.addTest(new CacheableDisableSelectiveTest("testLoadMixedCacheTree"));
        suite.addTest(new CacheableDisableSelectiveTest("testIsolatedIsolation"));
        suite.addTest(new CacheableDisableSelectiveTest("testProtectedIsolation"));
        suite.addTest(new CacheableDisableSelectiveTest("testProtectedCaching"));
        suite.addTest(new CacheableDisableSelectiveTest("testProtectedIsolationWithLockOnCloneFalse"));
        suite.addTest(new CacheableDisableSelectiveTest("testReadOnlyTree"));

        suite.addTest(new CacheableDisableSelectiveTest("testUpdateForceProtectedBasic"));
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateForceProtectedOneToOne"));
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateProtectedBasic"));
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateProtectedOneToMany"));

        suite.addTest(new CacheableDisableSelectiveTest("testProtectedRelationshipsMetadata"));
        suite.addTest(new CacheableDisableSelectiveTest("testForceProtectedFromEmbeddable"));
        suite.addTest(new CacheableDisableSelectiveTest("testEmbeddableProtectedCaching"));
        suite.addTest(new CacheableDisableSelectiveTest("testEmbeddableProtectedReadOnly"));
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateProtectedManyToOne"));
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateProtectedManyToMany"));
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateProtectedElementCollection"));
        suite.addTest(new CacheableDisableSelectiveTest("testIsolationBeforeEarlyTxBegin"));

        // Bug 340074
        suite.addTest(new CacheableDisableSelectiveTest("testFindWithLegacyFindProperties"));
        suite.addTest(new CacheableDisableSelectiveTest("testFindWithEMLegacyProperties"));
        suite.addTest(new CacheableDisableSelectiveTest("testMergeNonCachedWithRelationship"));
        suite.addTest(new CacheableDisableSelectiveTest("testIndirectCollectionRefreshBehavior"));
        suite.addTest(new CacheableDisableSelectiveTest("testDerivedIDProtectedRead"));

        // Bug 408262
        suite.addTest(new CacheableDisableSelectiveTest("testRefreshProtectedEntityInEarlyTransaction"));

        // Bug 530680
        suite.addTest(new CacheableDisableSelectiveTest("testUpdateSharedElementCollection"));

        return suite;
    }

    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to DISABLE_SELECTIVE using {@code <shared-cache-mode>}.
     */
    public void testCachingOnDISABLE_SELECTIVE() {
        assertCachingOnDISABLE_SELECTIVE(getPersistenceUnitServerSession());
    }

}
