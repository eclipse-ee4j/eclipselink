/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.identitymaps;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.internal.identitymaps.model.WeakCacheEntity;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestWeakUnitOfWorkIdentityMap {

    private static final int ENTITY_COUNT = 40;

    @Emf(name = "WeakUnitOfWorkIdentityMapPersistenceUnit",
            createTables = DDLGen.DROP_CREATE,
            classes = {WeakCacheEntity.class},
            properties = {
                    @Property(name = PersistenceUnitProperties.WEAVING, value = "false"),
                    @Property(name = PersistenceUnitProperties.CACHE_SHARED_DEFAULT, value = "false"),
                    @Property(name = PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, value = "HARD")})
    private EntityManagerFactory emf;

    @Before
    public void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM WeakCacheEntity").executeUpdate();
            for (int id = 1; id <= ENTITY_COUNT; id++) {
                em.persist(new WeakCacheEntity(id, "entity-" + id));
            }
            em.getTransaction().commit();
        } finally {
            close(em);
        }
    }

    @Test
    public void weakReadsAutomaticallyRemoveCollectedKeyAndPreserveLiveIdentity() {
        EntityManager em = emf.createEntityManager(Map.of(
                PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, ReferenceMode.FORCE_WEAK.name()));
        List<WeakCacheEntity> retained = new ArrayList<>();
        try {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) em.unwrap(JpaEntityManager.class).getUnitOfWork();
            assertEquals(ReferenceMode.FORCE_WEAK, unitOfWork.getReferenceMode());
            IdentityMapAccessor accessor = unitOfWork.getIdentityMapAccessorInstance();
            IdentityMap map = accessor.getIdentityMap(WeakCacheEntity.class);
            assertTrue(map instanceof WeakUnitOfWorkIdentityMap);

            WeakCacheEntity live = em.find(WeakCacheEntity.class, 1);
            assertNotNull(live);
            retained.add(live);
            CacheKey liveKey = accessor.getCacheKeyForObject(live);
            WeakCacheEntity collected = em.find(WeakCacheEntity.class, 2);
            assertNotNull(collected);
            CacheKey deadKey = accessor.getCacheKeyForObject(collected);
            assertTrue(deadKey instanceof QueueableWeakCacheKey);
            assertTrue(((QueueableWeakCacheKey) deadKey).reference.enqueue());
            assertNull(deadKey.getObject());
            Reference.reachabilityFence(collected);

            for (int id = 3; id <= ENTITY_COUNT; id++) {
                WeakCacheEntity entity = em.find(WeakCacheEntity.class, id);
                assertNotNull(entity);
                retained.add(entity);
            }

            assertEquals(ENTITY_COUNT - 1, map.getSize());
            assertNull(map.getCacheKey(deadKey.getKey(), false));
            assertNull(deadKey.getOwningMap());
            assertSame(liveKey, accessor.getCacheKeyForObject(live));
            assertSame(live, liveKey.getObject());
            assertSame(map, liveKey.getOwningMap());
            assertTrue(em.contains(live));
            assertSame(live, em.find(WeakCacheEntity.class, 1));
        } finally {
            Reference.reachabilityFence(retained);
            close(em);
        }
    }

    @Test
    public void hardReadsPreserveManagedIdentity() {
        EntityManager em = emf.createEntityManager();
        List<WeakCacheEntity> retained = new ArrayList<>();
        try {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) em.unwrap(JpaEntityManager.class).getUnitOfWork();
            assertEquals(ReferenceMode.HARD, unitOfWork.getReferenceMode());
            IdentityMapAccessor accessor = unitOfWork.getIdentityMapAccessorInstance();
            IdentityMap map = accessor.getIdentityMap(WeakCacheEntity.class);
            assertTrue(map instanceof UnitOfWorkIdentityMap);
            assertFalse(map instanceof WeakUnitOfWorkIdentityMap);

            WeakCacheEntity live = em.find(WeakCacheEntity.class, 1);
            assertNotNull(live);
            retained.add(live);
            CacheKey liveKey = accessor.getCacheKeyForObject(live);
            for (int id = 2; id <= ENTITY_COUNT; id++) {
                WeakCacheEntity entity = em.find(WeakCacheEntity.class, id);
                assertNotNull(entity);
                retained.add(entity);
            }

            assertEquals(ENTITY_COUNT, map.getSize());
            assertSame(liveKey, accessor.getCacheKeyForObject(live));
            assertSame(live, liveKey.getObject());
            assertSame(map, liveKey.getOwningMap());
            assertTrue(em.contains(live));
            assertSame(live, em.find(WeakCacheEntity.class, 1));
        } finally {
            Reference.reachabilityFence(retained);
            close(em);
        }
    }

    private static void close(EntityManager em) {
        if (em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
