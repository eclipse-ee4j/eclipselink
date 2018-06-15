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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     07/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     06/09/2010-2.0.3 Guy Pelletier
//       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
//     06/19/2014-2.6: - Tomas Kraus (Oracle)
//       - Fixed PU name and registered this suite in FullRegressionTestSuite
//       - 437578: Tests to verify @Cacheable inheritance in JPA 2.1
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.*;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;

/*
 * The test is testing against "MulitPU-3" persistence unit which has <shared-cache-mode> to be ENABLE_SELECTIVE
 */
public class CacheableModelJunitTestEnableSelective extends CacheableModelJunitTest {

    private static final String PU_NAME = "ENABLE_SELECTIVE";

    public CacheableModelJunitTestEnableSelective() {
        super();
    }

    public CacheableModelJunitTestEnableSelective(String name) {
        super(name);
        setPuName(PU_NAME);
    }

    public void setUp() {
        clearCache(PU_NAME);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableModelJunitTestEnableSelective");

        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new CacheableModelJunitTestEnableSelective("testSetup"));
            suite.addTest(new CacheableModelJunitTestEnableSelective("testCachingOnENABLE_SELECTIVE"));
            suite.addTest(new CacheableModelJunitTestEnableSelective("testCacheableInheritanceBasedOnFalse"));
            suite.addTest(new CacheableModelJunitTestEnableSelective("testCacheableInheritanceBasedOnTrue"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession(PU_NAME));
        clearCache(PU_NAME);
    }

    /**
     * Convenience method.
     */
    @Override
    public ServerSession getPUServerSession(String puName) {
        return JUnitTestCase.getServerSession(PU_NAME);
    }

    /**
     * Test caching with <code>@Cacheable(false)</code> in parent class, <code>@Cacheable(true)</code> in child class
     * and another child class which just inherits <code>@Cacheable</code> value from parent class.
     */
    public void testCacheableInheritanceBasedOnFalse() {
        runTestCacheableInheritanceBasedOnFalse(getEntityManagerFactory(), createEntityManager());
    }

    /**
     * Test caching with <code>@Cacheable(true)</code> in parent class, <code>@Cacheable(false)</code> in child class
     * and another child class which just inherits <code>@Cacheable</code> value from parent class.
     */
    public void testCacheableInheritanceBasedOnTrue() {
        runTestCacheableInheritanceBasedOnTrue(getEntityManagerFactory(), createEntityManager());
    }

}
