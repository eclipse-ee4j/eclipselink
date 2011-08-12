/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableFalseDetail;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableFalseDetailWithBackPointer;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableFalseEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableForceProtectedEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableProtectedEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueDerivedIDEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueDerivedIDPK;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.ChildCacheableFalseEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableForceProtectedEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.ForceProtectedEntityWithComposite;
import org.eclipse.persistence.testing.models.jpa.cacheable.ProtectedEmbeddable;
import org.eclipse.persistence.testing.models.jpa.cacheable.ProtectedRelationshipsEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.SharedEmbeddable;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableRelationshipsEntity;
 
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
public class CacheableModelJunitTest extends JUnitTestCase {
    private static int m_cacheableTrueEntity1Id;
    private static int m_cacheableForceProtectedEntity1Id;
    private static int m_cacheableTrueEntity2Id;
    private static int m_childCacheableFalseEntityId;
    private static int m_cacheableProtectedEntityId;
    private static int m_forcedProtectedEntityCompositId;
    
	private static int m_cacheableRelationshipsEntityId;
    public CacheableModelJunitTest() {
        super();
    }
    
    public CacheableModelJunitTest(String name) {
        super(name);
    }
    
    /**
     * Convenience method. Default PU is DISABLE_SELETIVE
     */
    public void clearDSCache() {
        super.clearCache("DISABLE_SELECTIVE");
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
        return super.createEntityManager("DISABLE_SELECTIVE");
    }
    
    /**
     * Convenience method - Executes a straight up find on the EM provided or
     * a new one.
     */
    protected CacheableTrueEntity findCacheableTrueEntity(EntityManager em, int id) {
        if (em == null) {
            EntityManager myEm = createDSEntityManager();
            CacheableTrueEntity entity = myEm.find(CacheableTrueEntity.class, id);
            closeEM(myEm);
            return entity;
        } else {
            return em.find(CacheableTrueEntity.class, id);
        }
    }
    
    /**
     * Convenience method - Executes the select query using retrieve BYPASS
     * and store BYPASS on a new entity manager or the one provided.
     */
    protected CacheableTrueEntity findCacheableTrueEntity_BYPASS_BYPASS(EntityManager em, int id) {
        return findCacheableTrueEntityUsingQuery(em, "findCacheableTrueEntityByPK_BYPASS_BYPASS", id);
    }
    
    /**
     * Convenience method - Executes the select query using retrieve BYPASS
     * and store USE on a new entity manager or the one provided.
     */
    protected CacheableTrueEntity findCacheableTrueEntity_BYPASS_USE(EntityManager em, int id) {
        return findCacheableTrueEntityUsingQuery(em, "findCacheableTrueEntityByPK_RETRIEVE_BYPASS_STORE_USE", id);
    }
    
    /**
     * Convenience method - Executes the select query using retrieve USE
     * and store BYPASS on a new entity manager or the one provided.
     */
    protected CacheableTrueEntity findCacheableTrueEntity_USE_BYPASS(EntityManager em, int id) {
        return findCacheableTrueEntityUsingQuery(em, "findCacheableTrueEntityByPK_RETRIEVE_USE_STORE_BYPASS", id);
    }
    
    /**
     * Convenience method - Executes the select query on a new entity manager 
     * or the one provided.
     */
    protected CacheableTrueEntity findCacheableTrueEntityUsingQuery(EntityManager em, String query, int id) {
        CacheableTrueEntity entity = null;
        
        EntityManager emToUse;
        if (em == null) {
            // Create a new EM ...
            emToUse = createDSEntityManager();
        } else {
            // Use the em provided but do not close it.
            emToUse = em;
        }
        
        try {
            beginTransaction(emToUse);
            entity = (CacheableTrueEntity) emToUse.createNamedQuery(query).setParameter("id", id).getSingleResult();
            commitTransaction(emToUse);
        }  catch (Exception e) {
            fail("Error executing query: " + e);
        } finally {
            if (em == null) {
                closeEM(emToUse);
            }
        }
        
        return entity;
    }
    
    /**
     * Convenience method.
     */
    public ServerSession getDSServerSession() {
        return getPUServerSession("DISABLE_SELECTIVE");
    }
    
    /**
     * Convenience method.
     */
    public ServerSession getPUServerSession(String puName) {
        return JUnitTestCase.getServerSession(puName);
    }
    
    public void setUp() {
        super.setUp();
        clearDSCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableModelJunitTest");

        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new CacheableModelJunitTest("testSetup"));
            suite.addTest(new CacheableModelJunitTest("testCachingOnALL"));
            suite.addTest(new CacheableModelJunitTest("testCachingOnNONE"));
            suite.addTest(new CacheableModelJunitTest("testCachingOnENABLE_SELECTIVE"));
            suite.addTest(new CacheableModelJunitTest("testCachingOnDISABLE_SELECTIVE"));
            suite.addTest(new CacheableModelJunitTest("testCachingOnUNSPECIFIED"));
            
            // Test cache retrieve mode of BYPASS and USE through the EM.
            suite.addTest(new CacheableModelJunitTest("testCreateEntities"));
            
            suite.addTest(new CacheableModelJunitTest("testFindWithEMProperties"));
            suite.addTest(new CacheableModelJunitTest("testFindWithFindProperties"));
            
            suite.addTest(new CacheableModelJunitTest("testRefreshWithEMProperties"));
            suite.addTest(new CacheableModelJunitTest("testRefreshWithRefreshProperties"));
            
            // Test various usage scenarios ..
            suite.addTest(new CacheableModelJunitTest("testRetrieveBYPASSStoreUSE1"));
            suite.addTest(new CacheableModelJunitTest("testRetrieveBYPASSStoreUSE2"));
            suite.addTest(new CacheableModelJunitTest("testRetrieveUSEStoreBYPASS1"));
            suite.addTest(new CacheableModelJunitTest("testRetrieveUSEStoreBYPASS2"));
            suite.addTest(new CacheableModelJunitTest("testRetrieveBYPASSStoreBYPASS1"));
            suite.addTest(new CacheableModelJunitTest("testRetrieveBYPASSStoreBYPASS2"));
            suite.addTest(new CacheableModelJunitTest("testMultipleEMQueries"));
            suite.addTest(new CacheableModelJunitTest("testEMPropertiesOnCommit1"));
            suite.addTest(new CacheableModelJunitTest("testEMPropertiesOnCommit2"));
            suite.addTest(new CacheableModelJunitTest("testInheritanceCacheable"));
            
            suite.addTest(new CacheableModelJunitTest("testDetailsOrder_Isolated"));
            suite.addTest(new CacheableModelJunitTest("testDetailsOrder_Isolated_BeginEarlyTransaction"));
            suite.addTest(new CacheableModelJunitTest("testDetailsOrder_Shared"));
            suite.addTest(new CacheableModelJunitTest("testDetailsOrder_Shared_BeginEarlyTransaction"));
            suite.addTest(new CacheableModelJunitTest("testLoadMixedCacheTree"));
            suite.addTest(new CacheableModelJunitTest("testIsolatedIsolation"));
            suite.addTest(new CacheableModelJunitTest("testProtectedIsolation"));
            suite.addTest(new CacheableModelJunitTest("testProtectedCaching"));
            suite.addTest(new CacheableModelJunitTest("testReadOnlyTree"));
            
            suite.addTest(new CacheableModelJunitTest("testUpdateForceProtectedBasic"));
            suite.addTest(new CacheableModelJunitTest("testUpdateForceProtectedOneToOne"));
            suite.addTest(new CacheableModelJunitTest("testUpdateProtectedBasic"));
            suite.addTest(new CacheableModelJunitTest("testUpdateProtectedOneToMany"));
            
            suite.addTest(new CacheableModelJunitTest("testProtectedRelationshipsMetadata"));
            suite.addTest(new CacheableModelJunitTest("testForceProtectedFromEmbeddable"));
            suite.addTest(new CacheableModelJunitTest("testEmbeddableProtectedCaching"));
            suite.addTest(new CacheableModelJunitTest("testEmbeddableProtectedReadOnly"));
            suite.addTest(new CacheableModelJunitTest("testUpdateProtectedManyToOne"));
            suite.addTest(new CacheableModelJunitTest("testUpdateProtectedManyToMany"));
            suite.addTest(new CacheableModelJunitTest("testUpdateProtectedElementCollection"));
            suite.addTest(new CacheableModelJunitTest("testIsolationBeforeEarlyTxBegin"));
            
            // Bug 340074
            suite.addTest(new CacheableModelJunitTest("testFindWithLegacyFindProperties"));
            suite.addTest(new CacheableModelJunitTest("testFindWithEMLegacyProperties"));
            suite.addTest(new CacheableModelJunitTest("testMergeNonCachedWithRelationship"));
            suite.addTest(new CacheableModelJunitTest("testIndirectCollectionRefreshBehavior"));
            suite.addTest(new CacheableModelJunitTest("testDerivedIDProtectedRead"));
        }
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearDSCache();
    }
    
    /**
     * Test EM properties on commit.  
     */
    public void testEMPropertiesOnCommit1() {
        EntityManager em1 = createDSEntityManager();
        
        // Find the entity and put it in the cache ..
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em1, m_cacheableTrueEntity1Id);
        String staleName = cachedEntity.getName();
        
        // Create  a new Entity to delete
        beginTransaction(em1);
        CacheableTrueEntity entityToDelete = new CacheableTrueEntity();
        entityToDelete.setName("entityToDelete");
        em1.persist(entityToDelete);
        int entityToDeleteId = entityToDelete.getId();
        commitTransaction(em1);
        
        // No need to clear the EM, just set the new property to BYPASS.
        em1.setProperty(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        
        beginTransaction(em1);
        CacheableTrueEntity entity = findCacheableTrueEntity(em1, m_cacheableTrueEntity1Id);
        String updatedName = "testEMPropertiesOnCommit1";
        entity.setName(updatedName);
        
        CacheableTrueEntity deletedEntity1 = findCacheableTrueEntity(em1, entityToDeleteId);
        em1.remove(deletedEntity1);
        commitTransaction(em1);
        
        EntityManager em2 = createDSEntityManager();
        CacheableTrueEntity entity2 = findCacheableTrueEntity_USE_BYPASS(em2, m_cacheableTrueEntity1Id);
        assertTrue("The shared cache was updated when the EM property CacheStoreMode = BYPASS", entity2.getName().equals(staleName));
        
        em2.refresh(entity2);
        assertTrue("The entity was not refreshed with the updated name.", entity2.getName().equals(updatedName));
        
        HashMap props = new HashMap();
        props.put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
        props.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        CacheableTrueEntity deletedEntity2 = (CacheableTrueEntity) em2.find(CacheableTrueEntity.class, entityToDeleteId, props);
        assertTrue("The deleted entity was removed from the cache", deletedEntity2 == null);
        
        deletedEntity2 = em2.find(CacheableTrueEntity.class, entityToDeleteId);
        assertTrue("The deleted entity was removed from the database", deletedEntity2 == null);
        
        closeEM(em1);
        closeEM(em2);
    }
    
    /**
     * Test EM properties on commit.  
     */
    public void testEMPropertiesOnCommit2() {
        EntityManager em1 = createDSEntityManager();
        
        // Find the entities and put them in the shared cache ...
        CacheableTrueEntity cachedEntity1 = findCacheableTrueEntity(em1, m_cacheableTrueEntity1Id);
        CacheableTrueEntity cachedEntity2 = findCacheableTrueEntity(em1, m_cacheableTrueEntity2Id);
        String staleName = cachedEntity2.getName();
        
        // No need to clear the EM, just set the new property to BYPASS.
        em1.setProperty(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
        String updatedName = "testEMPropertiesOnCommit2";
        
        beginTransaction(em1);
        // Update entity1 through a query that uses cache store mode USE.
        Query query = em1.createQuery("UPDATE JPA_CACHEABLE_TRUE e SET e.name = :name " + "WHERE e.id = :id ").setParameter("name", updatedName).setParameter("id", m_cacheableTrueEntity1Id);
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE);
        query.executeUpdate();
        
        // Update entity2 manually.
        CacheableTrueEntity entity2 = findCacheableTrueEntity(em1, m_cacheableTrueEntity2Id);
        entity2.setName(updatedName);
        commitTransaction(em1);
        closeEM(em1);
        
        // Verify the cache in a separate entity manager.
        EntityManager em2 = createDSEntityManager();
        CacheableTrueEntity entity21 = findCacheableTrueEntity_USE_BYPASS(em2, m_cacheableTrueEntity1Id);
        assertTrue("The shared cache should have been updated", entity21.getName().equals(updatedName));
        
        CacheableTrueEntity entity22 = findCacheableTrueEntity_USE_BYPASS(em2, m_cacheableTrueEntity2Id);
        assertTrue("The shared cache should NOT have been updated", entity22.getName().equals(staleName));
        
        em2.refresh(entity22);
        assertTrue("The entity was not refreshed with the updated name.", entity22.getName().equals(updatedName));
        
        closeEM(em2);
    }
    
    /**
     * Test find using entity manager properties  
     */
    public void testFindWithEMProperties() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) { 
            EntityManager em = createDSEntityManager();
            
            // Put the entity in the UOW and shared cache.
            CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
            
            // Update the entity name in the shared cash through a different EM.
            updateCacheableTrueEntityNameInSharedCache("testCacheRetrieveModeBypassOnFindThroughEMProperties");
            
            // This should pick up the entity from the shared cache
            EntityManager em2 = createDSEntityManager();
            CacheableTrueEntity cachedEntity2 = em2.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            assertTrue("The shared cache was not updated.", cachedEntity2.getName().equals("testCacheRetrieveModeBypassOnFindThroughEMProperties"));
            closeEM(em2);
            
            // This setting should be ignored on a refresh operation ...
            em.setProperty(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
            
            // Set the refresh property.
            em.setProperty(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
            
            // Re-issue the find on the original EM.
            CacheableTrueEntity entity = em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            assertTrue("CacheableTrueEntity should have been refreshed.", entity.getName().equals("testCacheRetrieveModeBypassOnFindThroughEMProperties"));
            assertTrue("CacheableTrueEntity from UOW should have been refreshed.", cachedEntity.getName().equals(entity.getName()));
            assertTrue("Entity returned should be the same instance from the UOW cache", cachedEntity == entity);
            
            closeEM(em);
        }
    }
    
    /**
     * Test find using entity manager properties (legacy)
     */
    public void testFindWithEMLegacyProperties() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) { 
            EntityManager em = createDSEntityManager();
            
            // Put the entity in the UOW and shared cache.
            CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
            
            // Update the entity name in the shared cash through a different EM.
            updateCacheableTrueEntityNameInSharedCache("testCacheRetrieveModeBypassOnFindThroughEMProperties");
            
            // This should pick up the entity from the shared cache
            EntityManager em2 = createDSEntityManager();
            CacheableTrueEntity cachedEntity2 = em2.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            assertTrue("The shared cache was not updated.", cachedEntity2.getName().equals("testCacheRetrieveModeBypassOnFindThroughEMProperties"));
            closeEM(em2);
            
            // This setting should be ignored on a refresh operation ...
            em.setProperty("javax.persistence.cacheRetrieveMode", CacheRetrieveMode.USE); // legacy property
            
            // Set the refresh property.
            em.setProperty("javax.persistence.cacheStoreMode", CacheStoreMode.REFRESH); // legacy property
            
            // Re-issue the find on the original EM.
            CacheableTrueEntity entity = em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            assertTrue("CacheableTrueEntity should have been refreshed.", entity.getName().equals("testCacheRetrieveModeBypassOnFindThroughEMProperties"));
            assertTrue("CacheableTrueEntity from UOW should have been refreshed.", cachedEntity.getName().equals(entity.getName()));
            assertTrue("Entity returned should be the same instance from the UOW cache", cachedEntity == entity);
            
            closeEM(em);
        }
    }
    
    /**
     * Test find using find properties  
     */
    public void testFindWithFindProperties() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createDSEntityManager();
            
            // Put the entity in the UOW and shared cache.
            CacheableTrueEntity cachedEntity = em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            
            // Update the entity name, but BYPASS updating the shared cache through a different EM.
            updateCacheableTrueEntityNameAndBypassStore("testCacheRetrieveModeBypassOnFindThroughFindProperties");
            
            // This should pick up the entity from the shared cache (which should not of been updated
            EntityManager em2 = createDSEntityManager();
            CacheableTrueEntity cachedEntity2 = em2.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            assertFalse("The shared cache was updated.", cachedEntity2.getName().equals("testCacheRetrieveModeBypassOnFindThroughFindProperties"));
            closeEM(em2);
            
            // This setting should be ignored on a refresh operation ...
            em.setProperty(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
            
            // Set the refresh property.
            HashMap properties = new HashMap();
            properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
            
            // Re-issue the find on the original EM.
            CacheableTrueEntity entity = (CacheableTrueEntity) em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id, properties);
            assertTrue("CacheableTrueEntity should have been refreshed.", entity.getName().equals("testCacheRetrieveModeBypassOnFindThroughFindProperties"));
            assertTrue("CacheableTrueEntity from UOW should have been refreshed.", cachedEntity.getName().equals(entity.getName()));
            assertTrue("Entity returned should be the same instance from the UOW cache", cachedEntity == entity);
            
            closeEM(em);
        }
    }
    
    /**
     * Test find using find properties (legacy)
     */
    public void testFindWithLegacyFindProperties() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createDSEntityManager();
            
            // Put the entity in the UOW and shared cache.
            CacheableTrueEntity cachedEntity = em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            
            // Update the entity name, but BYPASS updating the shared cache through a different EM.
            updateCacheableTrueEntityNameAndBypassStore("testCacheRetrieveModeBypassOnFindThroughFindProperties");
            
            // This should pick up the entity from the shared cache (which should not of been updated
            EntityManager em2 = createDSEntityManager();
            CacheableTrueEntity cachedEntity2 = em2.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
            assertFalse("The shared cache was updated.", cachedEntity2.getName().equals("testCacheRetrieveModeBypassOnFindThroughFindProperties"));
            closeEM(em2);
            
            // This setting should be ignored on a refresh operation ...
            em.setProperty("javax.persistence.cacheRetrieveMode", CacheRetrieveMode.USE);
            
            // Set the refresh property.
            HashMap properties = new HashMap();
            properties.put("javax.persistence.cacheStoreMode", CacheStoreMode.REFRESH);
            
            // Re-issue the find on the original EM.
            CacheableTrueEntity entity = (CacheableTrueEntity) em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id, properties);
            assertTrue("CacheableTrueEntity should have been refreshed.", entity.getName().equals("testCacheRetrieveModeBypassOnFindThroughFindProperties"));
            assertTrue("CacheableTrueEntity from UOW should have been refreshed.", cachedEntity.getName().equals(entity.getName()));
            assertTrue("Entity returned should be the same instance from the UOW cache", cachedEntity == entity);
            
            closeEM(em);
        }
    }
    
    /**
     * Test refresh using EM properties 
     */
    public void testRefreshWithEMProperties() {
        // This will put the entity in the cache.
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
        
        // Update the database manually through a different EM
        String updatedName = "testRefreshWithEMProperties";
        updateCacheableTrueEntityNameInSharedCache(updatedName);
       
        // This setting should be ignored on a refresh operation ...
        em.setProperty(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
        beginTransaction(em);
        try{
            em.refresh(cachedEntity);
            commitTransaction(em);
        }catch(Exception ex){
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }finally{
            closeEM(em);
        }
        assertTrue("CacheableTrueEntity should have been refreshed.", cachedEntity.getName().equals(updatedName));
        
    }
    
    /**
     * Test refresh using refresh properties. 
     */
    public void testRefreshWithRefreshProperties() {
        // This will put the entity in the cache.
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
        
        // Update the database manually through a different EM
        String updatedName = "testRefreshWithRefreshProperties";
        updateCacheableTrueEntityNameInSharedCache(updatedName);
        
        HashMap properties = new HashMap();
        // This setting should be ignored on a refresh operation ...
        properties.put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
        beginTransaction(em);
        try{
            em.refresh(cachedEntity, properties);
        }catch(Exception ex){ 
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }finally{
            closeEM(em);
        }
        assertTrue("CacheableTrueEntity should have been refreshed.", cachedEntity.getName().equals(updatedName));
        
    }
    
    /**
     * Test: Using named query (when updated object in shared cache)
     * CacheRetrieveMode = BYPASS
     * CacheStoreMode = BYPASS
     */
    public void testRetrieveBYPASSStoreBYPASS1() {
        // Put the entity in the EM UOW and shared cache.
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
        
        // Update the entity name in the shared cache through a different EM.
        String updatedName = "testRetrieveBYPASSStoreBYPASS1";
        updateCacheableTrueEntityNameInSharedCache(updatedName);
        
        // Execute find by pk query using Retrieve BYPASS, Store BYPASS on EM.
        // It should return the same entity.
        CacheableTrueEntity entity1 = findCacheableTrueEntity_BYPASS_BYPASS(em, m_cacheableTrueEntity1Id);
        assertTrue("The entity instances must be the same", entity1 == cachedEntity);
        assertTrue("The name should not of been refreshed", entity1.getName().equals(cachedEntity.getName()));
            
        // Issue a find using refresh on EM, should pick up the updated name.
        HashMap properties = new HashMap();
        properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        CacheableTrueEntity entity1b = (CacheableTrueEntity) em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id, properties);
        assertTrue("CacheableTrueEntity should of been refreshed.", entity1b.getName().equals(updatedName));
        closeEM(em);
        
        // On a different EM execute a find by pk using Retrieve BYPASS, 
        // Store BYPASS, we should get the updated name.
        CacheableTrueEntity entity2 = findCacheableTrueEntity_BYPASS_BYPASS(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have been refreshed.", entity2.getName().equals(updatedName));
    }
    
    /**
     * Test: Using named query  (when updated object NOT in shared cache)
     * CacheRetrieveMode = BYPASS
     * CacheStoreMode = BYPASS
     */
    public void testRetrieveBYPASSStoreBYPASS2() {
        // Put the entity in the EM UOW and shared cache.
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
        
        // Update the entity name in the shared cache through a different EM.
        String updatedName = "testRetrieveBYPASSStoreBYPASS2";
        updateCacheableTrueEntityNameAndBypassStore(updatedName);
        
        // Execute find by pk query using Retrieve BYPASS, Store BYPASS on EM.
        // It should return the same entity.
        CacheableTrueEntity entity1a = findCacheableTrueEntity_BYPASS_BYPASS(em, m_cacheableTrueEntity1Id);
        assertTrue("The entity returned should match the cached instance", entity1a == cachedEntity);
        assertTrue("CacheableTrueEntity should have been refreshed.", entity1a.getName().equals(cachedEntity.getName()));
            
        // On a different EM issue a find (using internal EclipseLink defaults)
        CacheableTrueEntity entity2 = findCacheableTrueEntity(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should not of been refreshed.", entity2.getName().equals(entity1a.getName()));
            
        // Issue a find on EM1 using REFRESH.
        HashMap properties = new HashMap();
        properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        CacheableTrueEntity entity1b = (CacheableTrueEntity) em.find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id, properties);
        assertTrue("CacheableTrueEntity should be from the shared cache.", entity1b.getName().equals(updatedName));
        
        closeEM(em);
    }
    
    /**
     * Test: Named query using retrieve BYPASS and store USE (when updated object in shared cache).
     */
    public void testRetrieveBYPASSStoreUSE1() {
        // Put the entity in the EM UOW and shared cache.
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
        
        // Update the entity name in the shared cache through a different EM.
        String updatedName = "testRetrieveBYPASSStoreUSE1";
        updateCacheableTrueEntityNameInSharedCache(updatedName);
        
        // Execute find by pk query using Retrieve BYPASS, Store USE on EM.
        // It should return the same entity.
        CacheableTrueEntity entity1 = findCacheableTrueEntity_BYPASS_USE(em, m_cacheableTrueEntity1Id);
        assertTrue("The entity instances must be the same", entity1 == cachedEntity);
        assertTrue("The name should not of been refreshed", entity1.getName().equals(cachedEntity.getName()));
            
        // Execute find by pk query using Retrieve USE, Store BYPASS on a
        // different EM. The entity returned should have been read from the 
        // shared cache with the updated name.
        CacheableTrueEntity entity2 = findCacheableTrueEntity_USE_BYPASS(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have the name from the shared cache.", entity2.getName().equals(updatedName));
            
        closeEM(em);
    }
    
    /**
     * Test: Using a named query with BYPASS and USE (when updated object not in shared cache)
     */
    public void testRetrieveBYPASSStoreUSE2() {
        // Put the entity in the EM UOW and shared cache.
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
        
        // Update the database manually through a different EM and bypass
        // updating the shared cache.
        String updatedName = "testRetrieveBYPASSStoreUSE2";
        updateCacheableTrueEntityNameAndBypassStore(updatedName);
        
        // Execute find by pk query using Retrieve BYPASS, Store USE on EM.
        // It should return the same entity. As an optimization in this case,
        // the Store USE will update the shared cache.
        CacheableTrueEntity entity1 = findCacheableTrueEntity_BYPASS_USE(em, m_cacheableTrueEntity1Id);
        assertTrue("The entity instances must be the same", entity1 == cachedEntity);
        assertTrue("The name should not of been refreshed", entity1.getName().equals(cachedEntity.getName()));
        closeEM(em);
        
        // A find on a new EM should return the value from the shared cache 
        // which should have been updated with the Store USE value above
        CacheableTrueEntity entity2 = findCacheableTrueEntity(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have the name from the shared cache.", entity2.getName().equals(updatedName));
    }
    
    /**
     * Test: Named query using retrieve USE and store BYPASS (when updated object in shared cache).
     */
    public void testRetrieveUSEStoreBYPASS1() {
        // Put the entity in the UOW and shared cache for EM1
        EntityManager em = createDSEntityManager();
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em, m_cacheableTrueEntity1Id);
        
        // Update the entity name in the shared cash through a different EM.
        String updatedName = "testRetrieveUSEStoreBYPASS1";
        updateCacheableTrueEntityNameInSharedCache(updatedName);
        
        // Execute find by pk query using Retrieve USE, Store BYPASS on EM.
        // It should return the same entity.
        CacheableTrueEntity entity1 = findCacheableTrueEntity_USE_BYPASS(em, m_cacheableTrueEntity1Id);
        assertTrue("The entity returned should match the cached instance", entity1 == cachedEntity);
        assertTrue("CacheableTrueEntity should not have been refreshed.", entity1.getName().equals(cachedEntity.getName()));
        closeEM(em);
        
        // Execute a find by pk query using Retrieve USE, Store BYPASS on a 
        // different EM. The entity returned should have been read from the 
        // shared cache with the updated name.
        CacheableTrueEntity entity2 = findCacheableTrueEntity_USE_BYPASS(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have the name from the shared cache.", entity2.getName().equals(updatedName));
    }
    
    /**
     * Test: Named query using retrieve USE and store BYPASS (when updated object NOT in shared cache).
     */
    public void testRetrieveUSEStoreBYPASS2() {
        // Put the entity in the UOW and shared cache for EM1
        EntityManager em1 = createDSEntityManager();
        CacheableTrueEntity cachedEntity = findCacheableTrueEntity(em1, m_cacheableTrueEntity1Id);
        
        // Update the database manually through a different EM and bypass
        // updating the shared cache.
        String updatedName = "testRetrieveUSEStoreBYPASS2";
        updateCacheableTrueEntityNameAndBypassStore(updatedName);
        
        // Execute find by pk query using Retrieve USE, Store BYPASS on EM.
        // It should return the same entity.
        CacheableTrueEntity entity1 = findCacheableTrueEntity_USE_BYPASS(em1, m_cacheableTrueEntity1Id);
        assertTrue("The entity returned should match the cached instance", entity1 == cachedEntity);
        assertTrue("CacheableTrueEntity should not have been refreshed.", entity1.getName().equals(cachedEntity.getName()));
        closeEM(em1);
        
        // Issue a find on a different EM. The entity should come from the
        // shared cache and have a stale name.
        EntityManager em2 = createDSEntityManager();
        CacheableTrueEntity entity2 = findCacheableTrueEntity(em2, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have the name from the shared cache.", entity2.getName().equals(entity1.getName()));
            
        // Now refresh the entity, should get the updated name.
        em2.refresh(entity2);
        assertTrue("CacheableTrueEntity should have the name from database.", entity2.getName().equals(updatedName));
        closeEM(em2);
    }
   
    /**
     * Test EM properties on commit.  
     */
    public void testInheritanceCacheable() {
        EntityManager em1 = createDSEntityManager();
        
        beginTransaction(em1);
        CacheableTrueEntity cacheableEntity1 = new CacheableTrueEntity();
        cacheableEntity1.setName("cacheableEntity");
        em1.persist(cacheableEntity1);
        ChildCacheableFalseEntity nonCacheableEntity1 = new ChildCacheableFalseEntity();
        nonCacheableEntity1.setName("nonCacheableEntity");
        em1.persist(nonCacheableEntity1);
        commitTransaction(em1);
        
        closeEM(em1);
        
        EntityManager em2 = createDSEntityManager();
        HashMap props = new HashMap();
        props.put(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
        
        CacheableTrueEntity cacheableEntity2 = (CacheableTrueEntity) em2.find(CacheableTrueEntity.class, cacheableEntity1.getId(), props);
        CacheableTrueEntity nonCacheableEntity2a = (CacheableTrueEntity) em2.find(CacheableTrueEntity.class, nonCacheableEntity1.getId(), props);
        ChildCacheableFalseEntity nonCacheableEntity2b = (ChildCacheableFalseEntity) em2.find(ChildCacheableFalseEntity.class, nonCacheableEntity1.getId(), props);
        
        assertFalse("CacheableTrueEntity was not in the cache", cacheableEntity2 == null);
        assertTrue("ChildCacheableFalseEntity was in the cache", nonCacheableEntity2a == null);
        assertTrue("ChildCacheableFalseEntity was in the cache", nonCacheableEntity2b == null);

        closeEM(em2);
    }
    
    public void testMultipleEMQueries() {
        // Get the object in the shared cache.
        CacheableTrueEntity cachedEntity = createDSEntityManager().find(CacheableTrueEntity.class, m_cacheableTrueEntity1Id);
        
        // Update the database manually through a different EM and bypass
        // updating the shared cache.
        String updatedName = "testMultipleEMQueries";
        updateCacheableTrueEntityNameAndBypassStore(updatedName);
        
        // Execute find by pk query using Retrieve USE, Store BYPASS. The entity 
        // returned should have been read from the shared cache with the stale name.
        CacheableTrueEntity entity1 = findCacheableTrueEntity_USE_BYPASS(null, m_cacheableTrueEntity1Id);
        assertFalse("CacheableTrueEntity should not have the updated name.", entity1.getName().equals(updatedName));
            
        // Execute find by pk query using Retrieve BYPASS, Store USE. The entity 
        // returned should have been read from the database and the shared cache 
        // should have been updated.
        CacheableTrueEntity entity2 = findCacheableTrueEntity_BYPASS_USE(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have the updated name.", entity2.getName().equals(updatedName));
            
        // Execute find by pk query using Retrieve USE, Store BYPASS. The entity 
        // returned should have been read from the shared cache with the updated name.
        CacheableTrueEntity entity3 = findCacheableTrueEntity_USE_BYPASS(null, m_cacheableTrueEntity1Id);
        assertTrue("CacheableTrueEntity should have the updated name.", entity3.getName().equals(updatedName));
    }
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to ALL.
     */
    public void testCachingOnALL() {
        ServerSession session = getPUServerSession("ALL");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertFalse("CacheableFalseEntity (ALL) from annotations has caching turned off", usesNoCache(falseEntityDescriptor));
    
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ALL) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));
        
        ClassDescriptor protectedEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_PROTECTED");
        assertFalse("CacheableProtectedEntity (ALL) from annotations has caching turned off", usesNoCache(protectedEntityDescriptor));

        ClassDescriptor childFalseEntityDescriptor = session.getDescriptorForAlias("JPA_CHILD_CACHEABLE_FALSE");
        assertFalse("ChildCacheableFalseEntity (ALL) from annotations has caching turned off", usesNoCache(childFalseEntityDescriptor));
        
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertFalse("SubCacheableFalseEntity (ALL) from annotations has caching turned off", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (ALL) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

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
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to NONE.
     */
    public void testCachingOnNONE() {
        ServerSession session = getPUServerSession("NONE");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
    
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertTrue("CacheableTrueEntity (NONE) from annotations has caching turned on", usesNoCache(trueEntityDescriptor));

        ClassDescriptor protectedEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_PROTECTED");
        assertTrue("CacheableProtectedEntity (ALL) from annotations has caching turned off", usesNoCache(protectedEntityDescriptor));

        ClassDescriptor childFalseEntityDescriptor = session.getDescriptorForAlias("JPA_CHILD_CACHEABLE_FALSE");
        assertTrue("ChildCacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(childFalseEntityDescriptor));
        
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertTrue("SubCacheableNoneEntity (NONE) from annotations has caching turned on", usesNoCache(noneSubEntityDescriptor));

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
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to ENABLE_SELECTIVE.
     */
    public void testCachingOnENABLE_SELECTIVE() {
        ServerSession session = getPUServerSession("ENABLE_SELECTIVE");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (ENABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
        
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ENABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));

        ClassDescriptor childFalseEntityDescriptor = session.getDescriptorForAlias("JPA_CHILD_CACHEABLE_FALSE");
        assertTrue("ChildCacheableFalseEntity (ENABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(childFalseEntityDescriptor));
        
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (ENABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (ENABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

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
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to DISABLE_SELECTIVE.
     */
    public void testCachingOnDISABLE_SELECTIVE() {
        ServerSession session = getPUServerSession("DISABLE_SELECTIVE");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (DISABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
        
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (DISABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));
 
        ClassDescriptor childFalseEntityDescriptor = session.getDescriptorForAlias("JPA_CHILD_CACHEABLE_FALSE");
        assertTrue("ChildCacheableFalseEntity (DISABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(childFalseEntityDescriptor));
        
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (DISABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (DISABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

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
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to UNSPECIFIED.
     */
    public void testCachingOnUNSPECIFIED() {
        ServerSession session = getPUServerSession("UNSPECIFIED");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (UNSPECIFIED) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
        
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (UNSPECIFIED) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));
 
        ClassDescriptor childFalseEntityDescriptor = session.getDescriptorForAlias("JPA_CHILD_CACHEABLE_FALSE");
        assertTrue("ChildCacheableFalseEntity (UNSPECIFIED) from annotations has caching turned on", usesNoCache(childFalseEntityDescriptor));
        
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (UNSPECIFIED) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (UNSPECIFIED) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

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
    
    public void testCreateEntities() {
        EntityManager em = null;
        
        try {
            em = createEntityManager("DISABLE_SELECTIVE");
            beginTransaction(em);
            
            CacheableTrueEntity cacheableTrueEntity = new CacheableTrueEntity();
            cacheableTrueEntity.setName("testCreateEntities");
            em.persist(cacheableTrueEntity);
            m_cacheableTrueEntity1Id = cacheableTrueEntity.getId();
            
            CacheableForceProtectedEntity cacheableForceProtectedEntity = new CacheableForceProtectedEntity();
            cacheableForceProtectedEntity.setName("testCreateEntities");
            em.persist(cacheableForceProtectedEntity);
            m_cacheableForceProtectedEntity1Id = cacheableForceProtectedEntity.getId();

            CacheableFalseEntity cacheableFalseEntity = new CacheableFalseEntity();
            em.persist(cacheableFalseEntity);
            
            cacheableForceProtectedEntity.setCacheableFalse(cacheableFalseEntity);
            
            CacheableProtectedEntity cacheableProtectedEntity = new CacheableProtectedEntity();
            em.persist(cacheableProtectedEntity);
            m_cacheableProtectedEntityId = cacheableProtectedEntity.getId();
            cacheableFalseEntity.setProtectedEntity(cacheableProtectedEntity);
            
            CacheableTrueEntity cacheableTrueEntity2 = new CacheableTrueEntity();
            cacheableTrueEntity2.setName("testCreateEntities");
            em.persist(cacheableTrueEntity2);
            m_cacheableTrueEntity2Id = cacheableTrueEntity2.getId();
            
            ChildCacheableFalseEntity childCacheableFalseEntity = new ChildCacheableFalseEntity();
            childCacheableFalseEntity.setName("testCreateEntities");
            em.persist(childCacheableFalseEntity);
            m_childCacheableFalseEntityId = childCacheableFalseEntity.getId();
            
            ForceProtectedEntityWithComposite fpewc = new ForceProtectedEntityWithComposite();
            fpewc.setName("testCreateEntities");
            ProtectedEmbeddable pe = new ProtectedEmbeddable();
            fpewc.setProtectedEmbeddable(pe);
            CacheableFalseEntity cfe = new CacheableFalseEntity();
            pe.setCacheableFalseEntity(cfe);
            SharedEmbeddable se = new SharedEmbeddable();
            fpewc.setSharedEmbeddable(se);
            em.persist(fpewc);
            m_forcedProtectedEntityCompositId = fpewc.getId();
            em.persist(cfe);
            CacheableFalseDetail cacheableFalseDetailEntity = new CacheableFalseDetail();
            em.persist(cacheableFalseDetailEntity);

            CacheableRelationshipsEntity prse = new CacheableRelationshipsEntity();
            prse.setName("Test OneToMany");
            prse.addCacheableFalse(cacheableFalseEntity);
            prse.addCacheableProtected(cacheableProtectedEntity);
            prse.setCacheableFPE(cacheableForceProtectedEntity);
            prse.addCacheableFalseDetail(cacheableFalseDetailEntity);
            prse.addProtectedEmbeddable(pe);
            em.persist(prse);
            m_cacheableRelationshipsEntityId = prse.getId();
            commitTransaction(em);
        } finally {
            closeEntityManager(em);   
        }
    }
    
    public void testDetailsOrder_Isolated() {
        testDetailsOrder(false, false);
    }
    
    public void testDetailsOrder_Isolated_BeginEarlyTransaction() {
        testDetailsOrder(false, true);
    }
    
    public void testDetailsOrder_Shared() {
        testDetailsOrder(true, false);
    }
    
    public void testDetailsOrder_Shared_BeginEarlyTransaction() {
        testDetailsOrder(true, true);
    }
    
    /*
     * @param useSharedCache if true both Entity and Detail use shared cache (otherwise both use isolated cache)
     * @param beginEarlyTransaction if true both EntityManagers that read back objects to verify order will beginEarlyTransaction 
     */
    void testDetailsOrder(boolean useSharedCache, boolean beginEarlyTransaction) {
        String puName = useSharedCache ? "ALL" : "NONE";
        int entityId;
        int nDetails = 2;
        
        // create entity and details, persist them
        EntityManager em = createEntityManager(puName);        
        try {
            beginTransaction(em);
            CacheableFalseEntity entity = new CacheableFalseEntity();
            for(int i=0; i < nDetails; i++) {
                CacheableFalseDetail detail = new CacheableFalseDetail();
                detail.setDescription(Integer.toString(i));
                entity.getDetails().add(detail);
            }
            em.persist(entity);
            commitTransaction(em);
            entityId = entity.getId();
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
                
        try {
            // verify that the order is correct, then reverse the order
            clearCache(puName);
            em = createEntityManager(puName);        
            try {
                beginTransaction(em);
                if(beginEarlyTransaction) {
                    em.setProperty(EntityManagerProperties.JOIN_EXISTING_TRANSACTION, "true");
                }
                // verify that the order is correct
                CacheableFalseEntity entity = em.find(CacheableFalseEntity.class, entityId);
                assertTrue("Read back wrong number of details", nDetails == entity.getDetails().size());
                for(int i=0; i < nDetails; i++) {
                    CacheableFalseDetail detail = entity.getDetails().get(i);
                    int iExpected = Integer.parseInt(detail.getDescription());
                    assertTrue("Wrong index " + i + "; was expected " + iExpected, i == iExpected);
                }
    
                // reverse the order
                List<CacheableFalseDetail> copyDetails = new ArrayList(entity.getDetails());
                entity.getDetails().clear();
                for(int i=nDetails-1; i >= 0; i--) {
                    entity.getDetails().add(copyDetails.get(i));
                }
                commitTransaction(em);
            } finally {
                if(isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
    
            // verify that the order is still correct
            clearCache(puName);
            em = createEntityManager(puName);        
            try {
                beginTransaction(em);
                if(beginEarlyTransaction) {
                    em.setProperty(EntityManagerProperties.JOIN_EXISTING_TRANSACTION, "true");
                }
                CacheableFalseEntity entity = em.find(CacheableFalseEntity.class, entityId);
                assertTrue("After reverse read back wrong number of details", nDetails == entity.getDetails().size());
                for(int i=0; i < nDetails; i++) {
                    CacheableFalseDetail detail = entity.getDetails().get(i);
                    // the order has been reversed
                    int iExpected = nDetails - Integer.parseInt(detail.getDescription()) - 1;
                    assertTrue("After reverse wrong index " + i + "; was expected " + iExpected, i == iExpected);
                }
            } finally {
                if(isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        } finally {
            // clean up
            em = createEntityManager(puName);        
            try {
                beginTransaction(em);
                CacheableFalseEntity entity = em.find(CacheableFalseEntity.class, entityId);
                em.remove(entity);
                commitTransaction(em);
            } finally {
                if(isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        }
    }
    
    public void testLoadMixedCacheTree(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            assertNotNull("Did not load the CacheableTrue Entity", cte);
            CacheableFalseEntity cfe = cte.getCacheableFalse();
            assertNotNull("Did not load the CacheableFalse related Entity", cfe);
            CacheableProtectedEntity cpe = cfe.getProtectedEntity();
            assertNotNull("Did not load the Cacheable Protected related Entity", cpe);
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            assertNotNull("Did not load the CacheableRelationshipsEntity", cre);
            List<CacheableFalseEntity> cacheableFalses = cre.getCacheableFalses();
            assertNotNull("Did not load collections of CacheableRelationshipsEntity related(OneToMany) CacheableFalseEntity", cacheableFalses);
            List<CacheableProtectedEntity> cacheableProtects = cre.getCacheableProtecteds();
            assertNotNull("Did not load collections of CacheableRelationshipsEntity related(OneToMany) CacheableProtectedEntity", cacheableProtects);
            CacheableForceProtectedEntity cfpe = cre.getCacheableFPE();
            assertNotNull("Did not load the CacheableRelationshipsEntity related(ManyToOne) CacheableForceProtectedEntity", cfpe);
            List<CacheableFalseDetail> cacheableFalseDetails = cre.getCacheableFalseDetails();
            assertNotNull("Did not load collections of CacheableRelationshipsEntity related(ManyToMany) CacheableFalseDetail", cacheableFalseDetails);
            List<ProtectedEmbeddable> protectedEmbed = cre.getProtectedEmbeddables();
            assertNotNull("Did not load collections of CacheableRelationshipsEntity related(ElementCollection) ProtectedEmbeddable", protectedEmbed);
    }finally{
        rollbackTransaction(em);
        closeEM(em);
        }
    }
    
    public void testIsolatedIsolation(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            CacheableFalseEntity cfe = cte.getCacheableFalse();
            assertNull("An isolated Entity was found in the shared cache", em.unwrap(ServerSession.class).getIdentityMapAccessor().getFromIdentityMap(cfe));
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            for (CacheableFalseEntity cfe1 : cre.getCacheableFalses()){
                assertNull("An isolated Entity in many side of OneToMany relationship was found in the shared cache", em.unwrap(ServerSession.class).getIdentityMapAccessor().getFromIdentityMap(cfe1));
            }
            for (CacheableFalseDetail cfde1 : cre.getCacheableFalseDetails()){
                assertNull("An isolated Entity in many side of ManyToMany relationship was found in the shared cache", em.unwrap(ServerSession.class).getIdentityMapAccessor().getFromIdentityMap(cfde1));
            } 
    }finally{
        rollbackTransaction(em);
        closeEM(em);
        }
    }
    
    public void testProtectedIsolation(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            CacheableFalseEntity cfe = cte.getCacheableFalse();
            CacheableProtectedEntity cpe = cfe.getProtectedEntity();
            ServerSession session = em.unwrap(ServerSession.class);
            assertNull("An protected relationshipwas found in the shared cache", ((CacheableForceProtectedEntity)session.getIdentityMapAccessor().getFromIdentityMap(cte)).getCacheableFalse());
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            for (CacheableProtectedEntity cpe1 : ((CacheableRelationshipsEntity)session.getIdentityMapAccessor().getFromIdentityMap(cre)).getCacheableProtecteds()){
                assertNull("An protected relationship in OneToMany was found in the shared cache", cpe1);
            }
            assertNull("An protected relationship in ManyToOne was found in the shared cache", ((CacheableRelationshipsEntity)session.getIdentityMapAccessor().getFromIdentityMap(cre)).getCacheableFPE());
            for (ProtectedEmbeddable cpe2 : ((CacheableRelationshipsEntity)session.getIdentityMapAccessor().getFromIdentityMap(cre)).getProtectedEmbeddables()){
                assertNull("An protected relationship in ElementCollection was found in the shared cache", cpe2);
            }
        }finally{
        rollbackTransaction(em);
        closeEM(em);
        }
        
    }
    
    public void testForceProtectedFromEmbeddable(){
        EntityManager em = createDSEntityManager();
        ClassDescriptor forcedProtectedDescriptor = em.unwrap(ServerSession.class).getDescriptor(ForceProtectedEntityWithComposite.class);
        ClassDescriptor protectedEmbeddableDesc = forcedProtectedDescriptor.getMappingForAttributeName("protectedEmbeddable").getReferenceDescriptor();
        ClassDescriptor sharedEmbeddableDesc = forcedProtectedDescriptor.getMappingForAttributeName("sharedEmbeddable").getReferenceDescriptor();
        assertFalse("Isolation of Entity not altered when embeddable has noncacheable relationship", forcedProtectedDescriptor.isSharedIsolation());
        assertFalse("Isolation of Embeddable not altered when embeddable has noncacheable relationship", protectedEmbeddableDesc.isSharedIsolation());
        assertFalse("Isolation of Embeddable not altered when Parent Entity is Protected", sharedEmbeddableDesc.isSharedIsolation());
    }
    
    public void testEmbeddableProtectedCaching(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            ForceProtectedEntityWithComposite cte = em.find(ForceProtectedEntityWithComposite.class, m_forcedProtectedEntityCompositId);
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            System.out.println("====the size of the collection is 1--" + cre.getProtectedEmbeddables().size());
            ProtectedEmbeddable pe = cte.getProtectedEmbeddable();
        
            ServerSession session = em.unwrap(ServerSession.class);
            closeEM(em);
            ForceProtectedEntityWithComposite cachedCPE = (ForceProtectedEntityWithComposite) session.getIdentityMapAccessor().getFromIdentityMap(cte);
            CacheableRelationshipsEntity cachedCRE = (CacheableRelationshipsEntity) session.getIdentityMapAccessor().getFromIdentityMap(cre);
            assertNotNull("ForceProtectedEntityWithComposite was not found in the cache", cachedCPE);
            assertNotNull("CacheableRelationshipsEntity was not found in the cache", cachedCRE);
            cachedCPE.getProtectedEmbeddable().setName("NewName"+System.currentTimeMillis());
            System.out.println("====the size of the collection is 2--" + cachedCRE.getProtectedEmbeddables().size());
            //follwoing code is commented out due to bug 336651
            //cachedCRE.getProtectedEmbeddables().get(0).setName("NewName"+System.currentTimeMillis());
            em = createDSEntityManager();
            beginTransaction(em);
            ForceProtectedEntityWithComposite managedCPE = em.find(ForceProtectedEntityWithComposite.class, cte.getId());
            CacheableRelationshipsEntity managedCRE = em.find(CacheableRelationshipsEntity.class, cre.getId());
            assertEquals("Cache was not used for Protected Isolation", cachedCPE.getProtectedEmbeddable().getName(),managedCPE.getProtectedEmbeddable().getName());
            //follwoing code is commented out due to bug 336651
            //assertEquals("Cache was not used for Protected Isolation", cachedCRE.getProtectedEmbeddables().get(0).getName(),managedCRE.getProtectedEmbeddables().get(0).getName());
        }finally{
        rollbackTransaction(em);
        closeEM(em);
        }
    }
    
    // Bug 347168
    public void testEmbeddableProtectedReadOnly(){
        EntityManager em = createDSEntityManager();
        Map properties = new HashMap();
        properties.put(QueryHints.READ_ONLY, "true");
        ForceProtectedEntityWithComposite cte = null;
        try{
            cte = em.find(ForceProtectedEntityWithComposite.class, m_forcedProtectedEntityCompositId, properties);
        } catch (Exception e){
            fail("Exception while trying to do a read-only find to an protected entity with an embeddable: " + e.getMessage());
        }
        assertNotNull("Protected entity did not have aggregate when read back.", cte.getProtectedEmbeddable());
    }
       
    public void testProtectedCaching(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            CacheableFalseEntity cfe = cte.getCacheableFalse();
            CacheableProtectedEntity cpe = cfe.getProtectedEntity();
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            List<CacheableProtectedEntity> cacheableProtects = cre.getCacheableProtecteds();
            CacheableForceProtectedEntity cfpe = cre.getCacheableFPE();
            ServerSession session = em.unwrap(ServerSession.class);
            closeEM(em);
            CacheableProtectedEntity cachedCPE = (CacheableProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cpe);
            assertNotNull("CacheableProtectedEntity was not found in the cache", cachedCPE);
            CacheableForceProtectedEntity cachedCFPE = (CacheableForceProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cfpe);
            assertNotNull("CacheableForceProtectedEntity from ManyToOne relationship was not found in the cache", cachedCFPE);
            for (CacheableProtectedEntity cpe1 : cacheableProtects){
                CacheableProtectedEntity cachedCPE1 = (CacheableProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cpe1);
                assertNotNull("CacheableProtectedEntity from OneToMany relationship was not found in the cache", cachedCPE1);
            } 
            cachedCPE.setName("NewName"+System.currentTimeMillis());
            cachedCFPE.setName("NewName"+System.currentTimeMillis());
            em = createDSEntityManager();
            beginTransaction(em);
            CacheableProtectedEntity managedCPE = em.find(CacheableProtectedEntity.class, cpe.getId());
            CacheableForceProtectedEntity managedCFPE = em.find(CacheableForceProtectedEntity.class, cfpe.getId());
            assertEquals("Cache was not used for Protected Isolation", cachedCPE.getName(),managedCPE.getName());
            assertEquals("Cache was not used for Protected Isolation", cachedCFPE.getName(),managedCFPE.getName());
        }finally{
        rollbackTransaction(em);
        closeEM(em);
        }
    }
    
    public void testProtectedRelationshipsMetadata(){
        EntityManager em = createDSEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        ClassDescriptor descriptor = session.getDescriptor(ProtectedRelationshipsEntity.class);
        for (DatabaseMapping mapping : descriptor.getMappings()){
            if (!mapping.isDirectToFieldMapping()){
                assertTrue("Relationship NONCacheable metadata was not processed correctly", !mapping.isCacheable());
            }
        }
        descriptor = session.getDescriptorForAlias("XML_ROTECTED_RELATIONSHIPS");
        for (DatabaseMapping mapping : descriptor.getMappings()){
            if (!mapping.isDirectToFieldMapping()){
                assertTrue("Relationship NONCacheable metadata was not processed correctly", !mapping.isCacheable());
            }
        }
        em.close();
        
    }
    
    public void testReadOnlyTree(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            Query q = em.createQuery("Select c from JPA_CACHEABLE_FORCE_PROTECTED c");
            q.setHint(QueryHints.READ_ONLY, "true");
            CacheableForceProtectedEntity cte = (CacheableForceProtectedEntity) q.getResultList().get(0);
            assertNotNull("Did not load the CacheableTrue Entity", cte);
            CacheableFalseEntity cfe = cte.getCacheableFalse();
            assertNotNull("Did not load the CacheableFalse related Entity", cfe);
            CacheableProtectedEntity cpe = cfe.getProtectedEntity();
            assertNotNull("Did not load the Cacheable Protected related Entity", cpe);
            Query q1 = em.createQuery("Select c from JPA_CACHEABLE_RELATIONSHIPS c");
            q1.setHint(QueryHints.READ_ONLY, "true");
            CacheableRelationshipsEntity cre = (CacheableRelationshipsEntity) q1.getResultList().get(0);
            for (CacheableFalseEntity cfe1 : cre.getCacheableFalses()){
                assertNotNull("Did not load the CacheableFalse related Entity in OneToMany relationship", cfe1);
            }
            for (CacheableProtectedEntity cpe1 : cre.getCacheableProtecteds()){
                assertNotNull("Did not load the CacheableProtected related Entity in OneToMany relationship", cpe1);
            }
            CacheableForceProtectedEntity cfpe = cre.getCacheableFPE();
            assertNotNull("Did not load the Cacheable Force Protected related Entity in ManyToOne relationship", cfpe);
            for (CacheableFalseDetail cfde : cre.getCacheableFalseDetails()){
                assertNotNull("Did not load the CacheableFalse Details related Entity in ManyToMany relationship", cfde);
            }
        }finally{
            rollbackTransaction(em);
            closeEM(em);
        }
    }
    
    public void testUpdateForceProtectedOneToOne(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            CacheableFalseEntity oldcfe = cte.getCacheableFalse();
            ServerSession session = em.unwrap(ServerSession.class);
            CacheableFalseEntity cfe = new CacheableFalseEntity();
            em.persist(cfe);
            cte.setCacheableFalse(cfe);
            commitTransaction(em);
            CacheableForceProtectedEntity cachedCPE = (CacheableForceProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cte);
            assertNull("A protected OneToOne relationship was merged into the shared cache", cachedCPE.getCacheableFalse());
            ObjectReferenceMapping orm = (ObjectReferenceMapping) session.getDescriptor(CacheableForceProtectedEntity.class).getMappingForAttributeName("cacheableFalse");
            Object cacheableFalsefk = session.getIdentityMapAccessorInstance().getCacheKeyForObject(cte).getProtectedForeignKeys().get(orm.getSelectFields().get(0));
            assertEquals("FK update not cached", cfe.getId(), cacheableFalsefk);
            beginTransaction(em);
            cte.setCacheableFalse(oldcfe);
            em.remove(cfe);
            commitTransaction(em);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }
    
    public void testUpdateForceProtectedBasic(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            String newName = "SomeNewName" + System.currentTimeMillis();
            cte.setName(newName);
            commitTransaction(em);
            ServerSession session = em.unwrap(ServerSession.class);
            CacheableForceProtectedEntity cachedCPE = (CacheableForceProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cte);
            assertEquals("A Basic mapping in a Protected class was not merged into the shared cache.  Expected: "+ newName + " found: "+ cachedCPE.getName(), cachedCPE.getName(), newName);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }

    public void testUpdateProtectedBasic(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableProtectedEntity cte = em.find(CacheableProtectedEntity.class, m_cacheableProtectedEntityId);
            ServerSession session = em.unwrap(ServerSession.class);
            String newName = "SomeNewName" + System.currentTimeMillis();
            cte.setName(newName);
            commitTransaction(em);
            CacheableProtectedEntity cachedCPE = (CacheableProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cte);
            assertEquals("A Basic mapping in a Protected class was not merged into the shared cache", newName, cachedCPE.getName());
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }

    public void testUpdateProtectedOneToMany(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            ServerSession session = em.unwrap(ServerSession.class);
            CacheableProtectedEntity cfe = new CacheableProtectedEntity();
            em.persist(cfe);
            cfe.setForcedProtected(cte);
            cte.getCacheableProtecteds().add(cfe);
            commitTransaction(em);

            CacheableForceProtectedEntity cachedCPE = (CacheableForceProtectedEntity) session.getIdentityMapAccessor().getFromIdentityMap(cte);
            assertTrue("A protected OneToMany relationship was merged into the shared cache", cachedCPE.getCacheableProtecteds() == null || cachedCPE.getCacheableProtecteds().isEmpty());
            beginTransaction(em);
            cte.getCacheableProtecteds().clear();
            cfe.setForcedProtected(null);
            em.remove(cfe);
            commitTransaction(em);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }

	public void testUpdateProtectedManyToOne(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cfpe = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            ServerSession session = em.unwrap(ServerSession.class);
            CacheableRelationshipsEntity cre = new CacheableRelationshipsEntity();
            em.persist(cre);
            cre.setCacheableFPE(cfpe);
            commitTransaction(em);

            CacheableRelationshipsEntity cachedCRE = (CacheableRelationshipsEntity) session.getIdentityMapAccessor().getFromIdentityMap(cre);
            assertTrue("A protected ManyToOne relationship was merged into the shared cache", cachedCRE.getCacheableFPE() == null);
            beginTransaction(em);
            em.remove(cre);
            commitTransaction(em);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }

    public void testUpdateProtectedManyToMany(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            ServerSession session = em.unwrap(ServerSession.class);
            CacheableFalseDetail cfd1 = new CacheableFalseDetail();
            CacheableFalseDetail cfd2 = new CacheableFalseDetail();
            em.persist(cfd1);
            em.persist(cfd2);
            cre.addCacheableFalseDetail(cfd1);
            cre.addCacheableFalseDetail(cfd2);
            commitTransaction(em);

            CacheableRelationshipsEntity cachedCRE = (CacheableRelationshipsEntity) session.getIdentityMapAccessor().getFromIdentityMap(cre);
            assertTrue("A protected ManyToMany relationship was merged into the shared cache", cachedCRE.getCacheableFalseDetails() == null || cachedCRE.getCacheableFalseDetails().isEmpty());
            beginTransaction(em);
            cre.getCacheableFalseDetails().clear();
            em.remove(cfd1);
            em.remove(cfd2);
            commitTransaction(em);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }

    public void testUpdateProtectedElementCollection(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            ServerSession session = em.unwrap(ServerSession.class);
            ProtectedEmbeddable cem1 = new ProtectedEmbeddable();
            ProtectedEmbeddable cem2 = new ProtectedEmbeddable();
            cre.addProtectedEmbeddable(cem1);
            cre.addProtectedEmbeddable(cem2);
            commitTransaction(em);

            CacheableRelationshipsEntity cachedCRE = (CacheableRelationshipsEntity) session.getIdentityMapAccessor().getFromIdentityMap(cre);
            assertTrue("A protected ElementCollection relationship was merged into the shared cache", cachedCRE.getProtectedEmbeddables() == null || cachedCRE.getProtectedEmbeddables().isEmpty());
            beginTransaction(em);
            cre.getProtectedEmbeddables().clear();
            commitTransaction(em);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }

    public void testIsolationBeforeEarlyTxBegin(){
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        try{
            CacheableForceProtectedEntity cte = em.find(CacheableForceProtectedEntity.class, m_cacheableForceProtectedEntity1Id);
            ServerSession session = em.unwrap(ServerSession.class);
            CacheableProtectedEntity cfe = new CacheableProtectedEntity();
            em.persist(cfe);
            cfe.setForcedProtected(cte);
            cte.getCacheableProtecteds().add(cfe);
            em.flush();
            //commitTransaction(em);
            CacheableRelationshipsEntity cre = em.find(CacheableRelationshipsEntity.class, m_cacheableRelationshipsEntityId);
            CacheableRelationshipsEntity cachedCRE = (CacheableRelationshipsEntity) session.getIdentityMapAccessor().getFromIdentityMap(cre);
            assertTrue("A protected OneToMany relationship was merged into the shared cache", cachedCRE.getCacheableFalses() == null || cachedCRE.getCacheableFalses().isEmpty());
            commitTransaction(em);

            beginTransaction(em);
            cte.getCacheableProtecteds().clear();
            cfe.setForcedProtected(null);
            em.remove(cfe);
            commitTransaction(em);
        }finally{
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEM(em);
        }
    }
    /**
     * Convenience method. This will not update the entity in the shared cache.
     */
    protected void updateCacheableTrueEntityNameAndBypassStore(String name) {
        EntityManager em = createDSEntityManager();
        
        try {
            beginTransaction(em);
            Query query = em.createQuery("UPDATE JPA_CACHEABLE_TRUE e SET e.name = :name " + "WHERE e.id = :id ").setParameter("name", name).setParameter("id", m_cacheableTrueEntity1Id);
            query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
            query.executeUpdate();
            commitTransaction(em);
        } catch (Exception e) {
            fail("Error updating the entity through JPQL: " + e);
        } finally {
            closeEM(em);
        }
    }
    
    /**
     * Convenience method. This will update the entity in the shared cache.
     */
    protected void updateCacheableTrueEntityNameInSharedCache(String name) {
        updateCacheableTrueEntityNameInSharedCache(name, null);
    }
    
    /**
     * Convenience method. This will update the entity in the shared cache.
     */
    protected void updateCacheableTrueEntityNameInSharedCache(String name, EntityManager em) {
        EntityManager emToUse;
        
        if (em == null) {
            emToUse = createDSEntityManager();
        } else {
            emToUse = em;
        }
        
        try {
            beginTransaction(emToUse);
            emToUse.createQuery("UPDATE JPA_CACHEABLE_TRUE e SET e.name = :name " + "WHERE e.id = :id ").setParameter("name", name).setParameter("id", m_cacheableTrueEntity1Id).executeUpdate();
            commitTransaction(emToUse);
        } catch (Exception e) {
            fail("Error updating the entity through JPQL: " + e);
        } finally {
            if (em == null) {
                closeEM(emToUse);
            }
        }
    }
    
    // Bug 345478 - Incorrect foreign key parameter set when retrieving an eager @OneToMany
    public void testMergeNonCachedWithRelationship(){
        // create entity and details, persist them
        EntityManager em = createDSEntityManager();
        beginTransaction(em);
        CacheableFalseEntity entity = new CacheableFalseEntity();
        em.persist(entity);
        commitTransaction(em);
        em.clear();
        CacheableFalseDetailWithBackPointer detail = null;
        try{
            detail = new CacheableFalseDetailWithBackPointer();
            detail.setEntity(entity);
            List<CacheableFalseDetailWithBackPointer> details = new ArrayList<CacheableFalseDetailWithBackPointer>();
            details.add(detail);
            entity.setDetailsBackPointer(details);
            detail.setDescription("test");
            em.getTransaction().begin();
            detail = em.merge(detail);
            commitTransaction(em);
            
            em.refresh(detail);
            
            assertTrue("detail does not have it's entity.", detail.getEntity() != null);
            assertTrue("detail's entity does not have the backpointer.", detail.getEntity().getDetailsBackPointer().size() == 1);
        } finally {
            beginTransaction(em);
            em.merge(detail);
            em.remove(detail.getEntity());
            em.remove(detail);
            commitTransaction(em);
            closeEM(em);
        }

    }
    
     // Bug 347190
    public void testIndirectCollectionRefreshBehavior(){
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
    
            CacheableFalseEntity entity = new CacheableFalseEntity();
            CacheableFalseDetailWithBackPointer detail = new CacheableFalseDetailWithBackPointer();
            
            List<CacheableFalseDetailWithBackPointer> details = new ArrayList<CacheableFalseDetailWithBackPointer>();
            details.add(detail);
            entity.setDetailsBackPointer(details);
            detail.setEntity(entity);
            
            em.persist(entity);
    
            em.flush();
            em.clear();
            counter.getSqlStatements().clear();
            Query query = em.createQuery("SELECT e from JPA_CACHEABLE_FALSE e where e.id = :id").setParameter("id", entity.getId());
            query.setHint(QueryHints.REFRESH, HintValues.TRUE);
            entity = (CacheableFalseEntity)query.getResultList().get(0);
            IndirectList list = (IndirectList)entity.getDetailsBackPointer();
            assertFalse(list.isInstantiated());
            assertTrue(counter.getSqlStatements().size() == 2);
        } finally{
            rollbackTransaction(em);
            counter.remove();
            closeEntityManager(em);
        }
      }
    
    // Bug 352533
    public void testDerivedIDProtectedRead(){
        EntityManager em = createEntityManager();
        CacheableFalseEntity cf = null;
        CacheableTrueDerivedIDEntity ctdid  = null;
        try{
            beginTransaction(em);
            cf = new CacheableFalseEntity();
            em.persist(cf);
            em.flush();
            
            ctdid = new CacheableTrueDerivedIDEntity("desc1", cf);
            em.persist(ctdid);
            commitTransaction(em);
            em.clear();
            
            ctdid = em.find(CacheableTrueDerivedIDEntity.class, new CacheableTrueDerivedIDPK(ctdid.getPk().getDescription(), cf.getId()));
            assertNotNull("The protected cached relationship was not properly retrieved", ctdid.getCacheableFalse());
        } finally {
            beginTransaction(em);
            cf = em.find(CacheableFalseEntity.class, cf.getId());
            em.remove(cf);
            em.remove(ctdid);
            commitTransaction(em);
        }
        
    }
    
    /**
     * Convenience method.
     */
    private boolean usesNoCache(ClassDescriptor descriptor) {
        return descriptor.isIsolated();
    }
}
