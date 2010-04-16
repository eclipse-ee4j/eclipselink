/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import java.util.HashMap;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.ChildCacheableFalseEntity;
 
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
 */
public class CacheableModelJunitTest extends JUnitTestCase {
    private static int m_cacheableTrueEntity1Id;
    private static int m_cacheableTrueEntity2Id;
    private static int m_childCacheableFalseEntityId;
    
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

        suite.addTest(new CacheableModelJunitTest("testSetup"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnALL"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnNONE"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnENABLE_SELECTIVE"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnDISABLE_SELECTIVE"));
        
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
    
    /**
     * Test find using find properties  
     */
    public void testFindWithFindProperties() {
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
        em.refresh(cachedEntity);
        assertTrue("CacheableTrueEntity should have been refreshed.", cachedEntity.getName().equals(updatedName));
        closeEM(em);
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
        em.refresh(cachedEntity, properties);        
        assertTrue("CacheableTrueEntity should have been refreshed.", cachedEntity.getName().equals(updatedName));
        closeEM(em);
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
    
    public void testCreateEntities() {
        EntityManager em = null;
        
        try {
            em = createEntityManager("DISABLE_SELECTIVE");
            beginTransaction(em);
            
            CacheableTrueEntity cacheableTrueEntity = new CacheableTrueEntity();
            cacheableTrueEntity.setName("testCreateEntities");
            em.persist(cacheableTrueEntity);
            m_cacheableTrueEntity1Id = cacheableTrueEntity.getId();
            
            CacheableTrueEntity cacheableTrueEntity2 = new CacheableTrueEntity();
            cacheableTrueEntity2.setName("testCreateEntities");
            em.persist(cacheableTrueEntity2);
            m_cacheableTrueEntity2Id = cacheableTrueEntity2.getId();
            
            ChildCacheableFalseEntity childCacheableFalseEntity = new ChildCacheableFalseEntity();
            childCacheableFalseEntity.setName("testCreateEntities");
            em.persist(childCacheableFalseEntity);
            m_childCacheableFalseEntityId = childCacheableFalseEntity.getId();
            
            commitTransaction(em);
        } catch (Exception e) {
            fail("Error occurred creating some entities");
        } finally {
            closeEntityManager(em);   
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
    
    /**
     * Convenience method.
     */
    private boolean usesNoCache(ClassDescriptor descriptor) {
        return descriptor.isIsolated();
    }
}
