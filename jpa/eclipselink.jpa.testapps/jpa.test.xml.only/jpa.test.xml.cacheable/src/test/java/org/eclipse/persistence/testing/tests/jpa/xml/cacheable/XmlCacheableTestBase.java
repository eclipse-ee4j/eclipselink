/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2025 IBM Corporation. All rights reserved.
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
//     06/19/2014-2.6: - Tomas Kraus (Oracle)
//       - 437578: Tests to verify @Cacheable inheritance in JPA 2.1
//     12/03/2015-2.6 Dalia Abo Sheasha
//       - 483582: Add the jakarta.persistence.sharedCache.mode property
package org.eclipse.persistence.testing.tests.jpa.xml.cacheable;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.cacheable.CacheableFalseEntity;
import org.eclipse.persistence.testing.models.jpa.xml.cacheable.CacheableForceProtectedEntity;
import org.eclipse.persistence.testing.models.jpa.xml.cacheable.CacheableProtectedEntity;
import org.eclipse.persistence.testing.models.jpa.xml.cacheable.CacheableTableCreator;

/*
 * By default the tests in this suite assume the "DISABLE_SELECTIVE" persistence
 * unit.
 *
 * If you want to test against a different PU be sure to ask for it accordingly.
 *
 * The list of Cacheable PU's available are:
 * "NONE"
 * "ALL"
 * "ENABLE_SELECTIVE"
 * "DISABLE_SELECTIVE"
 * "UNSPECIFIED"
 */
public class XmlCacheableTestBase extends JUnitTestCase {
    protected static int m_cacheableTrueEntity1Id;
    protected static int m_cacheableForceProtectedEntity1Id;
    protected static int m_cacheableTrueEntity2Id;
    protected static int m_childCacheableFalseEntityId;
    protected static int m_cacheableProtectedEntityId;
    protected static int m_forcedProtectedEntityCompositId;
    protected static int m_cacheableRelationshipsEntityId;

    public XmlCacheableTestBase() {
        super();
    }

    public XmlCacheableTestBase(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * Convenience method.
     */
    public void closeEM(EntityManager em) {
        if (em.isOpen()) {
            closeEntityManager(em);
        }
    }

    @Override
    public void setUp() {
        clearCache();
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CacheableTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    public void assertCachingOnALL(ServerSession session) {
        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertFalse("CacheableFalseEntity (ALL) from XML has caching turned off", usesNoCache(xmlFalseEntityDescriptor));

        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ALL) from XML has caching turned ff", usesNoCache(xmlTrueEntityDescriptor));

        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertFalse("SubCacheableFalseEntity (ALL) from XML has caching turned off", usesNoCache(xmlFalseSubEntityDescriptor));

        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableTrueEntity (ALL) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }

    public void assertCachingOnNONE(ServerSession session) {
        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (NONE) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));

        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertTrue("CacheableTrueEntity (NONE) from XML has caching turned on", usesNoCache(xmlTrueEntityDescriptor));

        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (NONE) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));

        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertTrue("SubCacheableTrueEntity (NONE) from XML has caching turned on", usesNoCache(xmlNoneSubEntityDescriptor));
    }
    
    public void assertCachingOnENABLE_SELECTIVE(ServerSession session) {
        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (ENABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));

        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ENABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlTrueEntityDescriptor));

        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (ENABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));

        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (ENABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }

    public void assertCachingOnDISABLE_SELECTIVE(ServerSession session) {
        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (DISABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));

        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (DISABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlTrueEntityDescriptor));

        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (DISABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));

        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (DISABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }

    public void assertCachingOnUNSPECIFIED(ServerSession session) {
        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (UNSPECIFIED) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));

        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (UNSPECIFIED) from XML has caching turned off", usesNoCache(xmlTrueEntityDescriptor));

        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (UNSPECIFIED) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));

        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (UNSPECIFIED) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }

     public void testProtectedIsolationWithLockOnCloneFalse(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ClassDescriptor descriptor = null;
        try{
            ServerSession session = em.unwrap(ServerSession.class);
            descriptor = session.getDescriptor(CacheableForceProtectedEntity.class);
            descriptor.setShouldLockForClone(false);
            session.readObject(CacheableForceProtectedEntity.class, new ExpressionBuilder().get("id").equal(m_cacheableForceProtectedEntity1Id));
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            CacheableFalseEntity cfe = cte.getCacheableFalse();
            assertNotNull("Failed to load protected relationship when setShouldLockForClone is set to false", cfe);
            CacheableProtectedEntity cpe = cfe.getProtectedEntity();
            assertNotNull("Failed to load protected relationship when setShouldLockForClone is set to false", cpe);
        }finally{
            if (descriptor != null){
                descriptor.setShouldLockForClone(true);
            }
        rollbackTransaction(em);

        closeEM(em);
        }
    }

    public void testProtectedRelationshipsMetadata(){
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        ClassDescriptor descriptor = session.getDescriptorForAlias("XML_ROTECTED_RELATIONSHIPS");
        for (DatabaseMapping mapping : descriptor.getMappings()){
            if (!mapping.isDirectToFieldMapping()){
                assertFalse("Relationship NONCacheable metadata was not processed correctly", mapping.isCacheable());
            }
        }
        closeEM(em);

    }

    /**
     * Convenience method.
     */
    protected boolean usesNoCache(ClassDescriptor descriptor) {
        return descriptor.isIsolated();
    }
}
