/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     07/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     06/09/2010-2.0.3 Guy Pelletier 
 *       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.*;
import javax.persistence.EntityManager;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;
 
/*
 * The test is testing against "MulitPU-4" persistence unit which has <shared-cache-mode> to be DISABLE_SELECTIVE
 */
public class CacheableModelJunitTestDisableSelective extends CacheableModelJunitTest {
    
    public CacheableModelJunitTestDisableSelective() {
        super();
    }
    
    public CacheableModelJunitTestDisableSelective(String name) {
        super(name);
        setPuName("MulitPU-4");
    }
    
    /**
     * Convenience method. 
     */
    public void clearDSCache() {
        clearCache("MulitPU-4");
    }
    
    /**
     * Convenience method.
     */
    public void closeEM(EntityManager em) {
        if (em.isOpen()) {
            closeEntityManager(em);
        }
    }
    
    /**
     * Convenience method.
     */
    public EntityManager createDSEntityManager() {
        return createEntityManager("MulitPU-4");
    }
        
    /**
     * Convenience method.
     */
    public ServerSession getDSServerSession() {
        return getPUServerSession("MulitPU-4");
    }
    
    /**
     * Convenience method.
     */
    @Override
    public ServerSession getPUServerSession(String puName) {
        return JUnitTestCase.getServerSession("MulitPU-4");
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableModelJunitTestDisableSelective");

        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new CacheableModelJunitTestDisableSelective("testSetup"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testCachingOnDISABLE_SELECTIVE"));
            
            // Test cache retrieve mode of BYPASS and USE through the EM.
            suite.addTest(new CacheableModelJunitTestDisableSelective("testCreateEntities"));
            
            suite.addTest(new CacheableModelJunitTestDisableSelective("testFindWithEMProperties"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testFindWithFindProperties"));
            
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRefreshWithEMProperties"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRefreshWithRefreshProperties"));
            
            // Test various usage scenarios ..
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRetrieveBYPASSStoreUSE1"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRetrieveBYPASSStoreUSE2"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRetrieveUSEStoreBYPASS1"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRetrieveUSEStoreBYPASS2"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRetrieveBYPASSStoreBYPASS1"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testRetrieveBYPASSStoreBYPASS2"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testMultipleEMQueries"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testEMPropertiesOnCommit1"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testEMPropertiesOnCommit2"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testInheritanceCacheable"));

            suite.addTest(new CacheableModelJunitTestDisableSelective("testLoadMixedCacheTree"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testIsolatedIsolation"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testProtectedIsolation"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testProtectedCaching"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testReadOnlyTree"));
            
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateForceProtectedBasic"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateForceProtectedOneToOne"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateProtectedBasic"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateProtectedOneToMany"));

            suite.addTest(new CacheableModelJunitTestDisableSelective("testProtectedRelationshipsMetadata"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testForceProtectedFromEmbeddable"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testEmbeddableProtectedCaching"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testEmbeddableProtectedReadOnly"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateProtectedManyToOne"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateProtectedManyToMany"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testUpdateProtectedElementCollection"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testIsolationBeforeEarlyTxBegin"));
            
            // Bug 340074
            suite.addTest(new CacheableModelJunitTestDisableSelective("testFindWithLegacyFindProperties"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testFindWithEMLegacyProperties"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testMergeNonCachedWithRelationship"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testIndirectCollectionRefreshBehavior"));
            suite.addTest(new CacheableModelJunitTestDisableSelective("testDerivedIDProtectedRead"));
        }
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession("MulitPU-4"));
        clearDSCache();
    }

}
