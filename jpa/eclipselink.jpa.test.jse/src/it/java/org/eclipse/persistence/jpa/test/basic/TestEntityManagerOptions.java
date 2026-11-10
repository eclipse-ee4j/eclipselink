/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.basic;

import java.util.Map;
import java.util.Set;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Query;
import jakarta.persistence.Statement;
import jakarta.persistence.StoredProcedureQuery;

import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.query.model.SimpleQueryEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestEntityManagerOptions {

    @Emf(classes = {SimpleQueryEntity.class})
    private EntityManagerFactory emf;

    @Before
    public void verifyFactory() {
        Assert.assertNotNull("The test factory must be injected", emf);
        Assert.assertTrue("The test factory must be open", emf.isOpen());
    }

    @Test
    public void freshRetrieveModeDefaultsToUse() {
        try (EntityManager em = emf.createEntityManager()) {
            Assert.assertEquals(CacheRetrieveMode.USE, em.getCacheRetrieveMode());
        }
    }

    @Test
    public void freshStoreModeDefaultsToUse() {
        try (EntityManager em = emf.createEntityManager()) {
            Assert.assertEquals(CacheStoreMode.USE, em.getCacheStoreMode());
        }
    }

    @Test
    public void freshOptionsIncludeDefaults() {
        try (EntityManager em = emf.createEntityManager()) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, em.getFlushMode());
        }
    }

    @Test
    public void flushOptionPreservesCacheDefaults() {
        try (EntityManager em = emf.createEntityManager()) {
            em.addOption(FlushModeType.COMMIT);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, FlushModeType.COMMIT);
        }
    }

    @Test
    public void flushSetterPreservesCacheDefaults() {
        try (EntityManager em = emf.createEntityManager()) {
            em.setFlushMode(FlushModeType.COMMIT);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, FlushModeType.COMMIT);
        }
    }

    @Test
    public void nullCreationMapUsesDefaults() {
        try (EntityManager em = emf.createEntityManager((Map<?, ?>) null)) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, em.getFlushMode());
        }
    }

    @Test
    public void emptyCreationMapUsesDefaults() {
        try (EntityManager em = emf.createEntityManager(Map.of())) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, em.getFlushMode());
        }
    }

    @Test
    public void nullCreationOptionsUseDefaults() {
        try (EntityManager em = emf.createEntityManager((EntityManager.CreationOption[]) null)) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, em.getFlushMode());
        }
    }

    @Test
    public void emptyCreationOptionsUseDefaults() {
        try (EntityManager em = emf.createEntityManager(new EntityManager.CreationOption[0])) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, em.getFlushMode());
        }
    }

    @Test
    public void retrieveSetterPreservesStoreDefault() {
        try (EntityManager em = emf.createEntityManager()) {
            em.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
            assertOptions(em, CacheRetrieveMode.BYPASS, CacheStoreMode.USE, em.getFlushMode());
        }
    }

    @Test
    public void storeSetterPreservesRetrieveDefault() {
        try (EntityManager em = emf.createEntityManager()) {
            em.setCacheStoreMode(CacheStoreMode.BYPASS);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.BYPASS, em.getFlushMode());
        }
    }

    @Test
    public void cacheOptionsPreserveSiblingDefaults() {
        try (EntityManager em = emf.createEntityManager()) {
            em.addOption(CacheRetrieveMode.BYPASS);
            assertOptions(em, CacheRetrieveMode.BYPASS, CacheStoreMode.USE, em.getFlushMode());
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.addOption(CacheStoreMode.REFRESH);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.REFRESH, em.getFlushMode());
        }
    }

    @Test
    public void typedCreationOptionsPreserveSiblingDefaults() {
        try (EntityManager em = emf.createEntityManager(CacheRetrieveMode.BYPASS)) {
            assertOptions(em, CacheRetrieveMode.BYPASS, CacheStoreMode.USE, em.getFlushMode());
        }
        try (EntityManager em = emf.createEntityManager(CacheStoreMode.REFRESH)) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.REFRESH, em.getFlushMode());
        }
    }

    @Test
    public void creationPropertiesPreserveSiblingDefaults() {
        try (EntityManager em = emf.createEntityManager(Map.of("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS))) {
            assertOptions(em, CacheRetrieveMode.BYPASS, CacheStoreMode.USE, em.getFlushMode());
        }
        try (EntityManager em = emf.createEntityManager(Map.of("jakarta.persistence.cache.storeMode", CacheStoreMode.REFRESH))) {
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.REFRESH, em.getFlushMode());
        }
    }

    @Test
    public void replacingModesPreservesOtherOptionFamilies() {
        try (EntityManager em = emf.createEntityManager(CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS, FlushModeType.COMMIT)) {
            em.addOption(CacheRetrieveMode.USE);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.BYPASS, FlushModeType.COMMIT);
            Assert.assertFalse(em.getOptions().contains(CacheRetrieveMode.BYPASS));
            em.setCacheStoreMode(CacheStoreMode.REFRESH);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.REFRESH, FlushModeType.COMMIT);
            Assert.assertFalse(em.getOptions().contains(CacheStoreMode.BYPASS));
        }
    }

    @Test
    public void optionSnapshotsAreIndependentlyMutable() {
        try (EntityManager em = emf.createEntityManager()) {
            Set<EntityManager.Option> earlier = em.getOptions();
            Set<EntityManager.Option> original = Set.copyOf(earlier);
            Set<EntityManager.Option> changed = em.getOptions();
            changed.clear();
            changed.add(CacheRetrieveMode.BYPASS);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, em.getFlushMode());
            em.addOption(CacheRetrieveMode.BYPASS);
            em.addOption(FlushModeType.COMMIT);
            Assert.assertEquals(original, earlier);
            assertOptions(em, CacheRetrieveMode.BYPASS, CacheStoreMode.USE, FlushModeType.COMMIT);
        }
    }

    @Test
    public void queryCacheModesKeepDefaultsAndOverrides() {
        try (EntityManager em = emf.createEntityManager()) {
            Query query = em.createQuery("SELECT e FROM SimpleQueryEntity e");
            Assert.assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
            Assert.assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
            query.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
            query.setCacheStoreMode(CacheStoreMode.REFRESH);
            Assert.assertEquals(CacheRetrieveMode.BYPASS, query.getCacheRetrieveMode());
            Assert.assertEquals(CacheStoreMode.REFRESH, query.getCacheStoreMode());
        }
    }

    @Test
    public void procedureCacheModesKeepDefaultsAndOverrides() {
        try (EntityManager em = emf.createEntityManager()) {
            StoredProcedureQuery query = em.createStoredProcedureQuery("options_metadata_only");
            Assert.assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
            Assert.assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
            query.setCacheStoreMode(CacheStoreMode.BYPASS);
            Assert.assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
            Assert.assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
        }
    }

    @Test
    public void statementCacheModesKeepDefaultsAndOverrides() {
        try (EntityManager em = emf.createEntityManager()) {
            Statement statement = em.createStatement("DELETE FROM SimpleQueryEntity e WHERE e.id = -1");
            Assert.assertEquals(CacheRetrieveMode.USE, statement.getCacheRetrieveMode());
            Assert.assertEquals(CacheStoreMode.USE, statement.getCacheStoreMode());
            statement.setCacheStoreMode(CacheStoreMode.BYPASS);
            Assert.assertEquals(CacheRetrieveMode.USE, statement.getCacheRetrieveMode());
            Assert.assertEquals(CacheStoreMode.BYPASS, statement.getCacheStoreMode());
        }
    }

    @Test
    public void closedManagerRejectsOptionAccess() {
        EntityManager em = emf.createEntityManager();
        em.close();
        Assert.assertThrows(IllegalStateException.class, em::getOptions);
        Assert.assertThrows(IllegalStateException.class, () -> em.addOption(FlushModeType.COMMIT));
    }

    @Test
    public void optionReadsAllowPersistenceAndQuery() {
        try (EntityManager em = emf.createEntityManager()) {
            em.addOption(FlushModeType.COMMIT);
            assertOptions(em, CacheRetrieveMode.USE, CacheStoreMode.USE, FlushModeType.COMMIT);
            try {
                em.getTransaction().begin();
                em.persist(new SimpleQueryEntity(405001L, 42, "cache-options"));
                em.flush();
                em.clear();
                Assert.assertEquals("cache-options", em.createQuery(
                        "SELECT e.strVal1 FROM SimpleQueryEntity e WHERE e.id = :id", String.class)
                        .setParameter("id", 405001L).getSingleResult());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    private static void assertOptions(EntityManager em, CacheRetrieveMode retrieve, CacheStoreMode store, FlushModeType flush) {
        Assert.assertEquals(retrieve, em.getCacheRetrieveMode());
        Assert.assertEquals(store, em.getCacheStoreMode());
        Assert.assertEquals(flush, em.getFlushMode());
        Set<EntityManager.Option> options = em.getOptions();
        Assert.assertTrue(options.contains(retrieve));
        Assert.assertTrue(options.contains(store));
        Assert.assertTrue(options.contains(flush));
    }
}
