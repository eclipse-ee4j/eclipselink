/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
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
import jakarta.persistence.EntityTransaction;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.identitymaps.model.WeakCacheEntity;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestWeakUnitOfWorkIdentityMap {

    private static final int ROW_COUNT = 40;

    @Emf(name = "weak-cache-3014", classes = {WeakCacheEntity.class}, createTables = DDLGen.DROP_CREATE,
            properties = {
                @Property(name = PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, value = "FORCE_WEAK"),
                @Property(name = PersistenceUnitProperties.WEAVING, value = "false")
            })
    private EntityManagerFactory emf;

    @Before
    public void seedRows() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            if (em.find(WeakCacheEntity.class, 1) == null) {
                transaction.begin();
                for (int id = 1; id <= ROW_COUNT; id++) {
                    em.persist(new WeakCacheEntity(id, "value-" + id));
                }
                transaction.commit();
            }
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            em.close();
        }
    }

    @Test
    public void keepsRepopulatedKeyAfterAutomaticCleanup() {
        verifyAutomaticCleanup(false);
    }

    @Test
    public void keepsReplacementKeyAfterDetachAndAutomaticCleanup() {
        verifyAutomaticCleanup(true);
    }

    @Test
    public void hardReferenceModeRetainsManagedIdentity() {
        EntityManager em = emf.createEntityManager(Map.of(
                PersistenceUnitProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, "HARD"));
        List<WeakCacheEntity> retained = new ArrayList<>();
        try {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) em.unwrap(JpaEntityManager.class).getUnitOfWork();
            Assert.assertEquals(ReferenceMode.HARD, unitOfWork.getReferenceMode());
            IdentityMap map = unitOfWork.getIdentityMapAccessorInstance().getIdentityMap(WeakCacheEntity.class);
            Assert.assertTrue(map instanceof UnitOfWorkIdentityMap);
            Assert.assertFalse(map instanceof WeakUnitOfWorkIdentityMap);
            WeakCacheEntity live = em.find(WeakCacheEntity.class, 1);
            retained.add(live);
            CacheKey key = unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(live);

            findRemainingRows(em, 2, retained);

            Assert.assertSame(key, map.getCacheKey(key.getKey(), false));
            Assert.assertSame(live, key.getObject());
            Assert.assertSame(live, em.find(WeakCacheEntity.class, 1));
            Assert.assertTrue(em.contains(live));
        } finally {
            Reference.reachabilityFence(retained);
            em.close();
        }
    }

    @Test
    public void failedCommitRestoresWeakMapAndContinuesAutomaticCleanup() {
        ClientSession client = emf.unwrap(JpaEntityManagerFactory.class).getServerSession().acquireClientSession();
        List<WeakCacheEntity> retained = new ArrayList<>();
        try {
            UnitOfWorkImpl unitOfWork = client.acquireUnitOfWork(ReferenceMode.FORCE_WEAK);
            try {
                Assert.assertEquals(ReferenceMode.FORCE_WEAK, unitOfWork.getReferenceMode());
                IdentityMapManager originalManager = unitOfWork.getIdentityMapAccessorInstance().getIdentityMapManager();
                IdentityMap originalMap = unitOfWork.getIdentityMapAccessorInstance().getIdentityMap(WeakCacheEntity.class);
                Assert.assertTrue(originalMap instanceof WeakUnitOfWorkIdentityMap);
                QueueableWeakCacheKey[] originalKeys = new QueueableWeakCacheKey[4];
                for (int id = 1; id <= originalKeys.length; id++) {
                    WeakCacheEntity entity = readRow(unitOfWork, id, retained);
                    originalKeys[id - 1] = (QueueableWeakCacheKey)
                            unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(entity);
                    Assert.assertSame(originalMap, originalKeys[id - 1].getOwningMap());
                }

                RuntimeException failure = new RuntimeException("Expected pre-commit failure");
                SessionEventAdapter listener = new SessionEventAdapter() {
                    @Override
                    public void preCommitUnitOfWork(SessionEvent event) {
                        Assert.assertSame(unitOfWork, event.getSession());
                        throw failure;
                    }
                };
                unitOfWork.getEventManager().addListener(listener);
                try {
                    Assert.assertSame(failure, Assert.assertThrows(RuntimeException.class, unitOfWork::commitAndResumeOnFailure));
                } finally {
                    unitOfWork.getEventManager().removeListener(listener);
                }
                Assert.assertTrue(unitOfWork.isActive());
                Assert.assertNotSame(originalManager, unitOfWork.getIdentityMapAccessorInstance().getIdentityMapManager());
                IdentityMap restoredMap = unitOfWork.getIdentityMapAccessorInstance().getIdentityMap(WeakCacheEntity.class);
                Assert.assertTrue(restoredMap instanceof WeakUnitOfWorkIdentityMap);
                Assert.assertNotSame(originalMap, restoredMap);
                QueueableWeakCacheKey[] restoredKeys = new QueueableWeakCacheKey[originalKeys.length];
                for (int index = 0; index < originalKeys.length; index++) {
                    restoredKeys[index] = (QueueableWeakCacheKey) restoredMap.getCacheKey(originalKeys[index].getKey(), false);
                    Assert.assertNotSame(originalKeys[index], restoredKeys[index]);
                    Assert.assertSame(originalKeys[index].reference, restoredKeys[index].reference);
                    Assert.assertSame(originalMap, restoredKeys[index].getOwningMap());
                }

                collect(restoredKeys[0].reference);
                WeakCacheEntity repopulated = readRow(unitOfWork, 1, retained);
                Assert.assertNotSame(retained.get(0), repopulated);
                Assert.assertSame(restoredKeys[0], unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(repopulated));
                Assert.assertNotSame(originalKeys[0].reference, restoredKeys[0].reference);

                unitOfWork.unregisterObject(retained.get(1));
                Assert.assertNull(restoredKeys[1].getOwningMap());
                collect(originalKeys[1].reference);
                WeakCacheEntity replacement = readRow(unitOfWork, 2, retained);
                Assert.assertNotSame(retained.get(1), replacement);
                QueueableWeakCacheKey replacementKey = (QueueableWeakCacheKey)
                        unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(replacement);
                Assert.assertNotSame(restoredKeys[1], replacementKey);
                Assert.assertSame(restoredMap, replacementKey.getOwningMap());

                // Enqueue after restoration so the resumed map receives the current dead entry's event.
                collect(restoredKeys[2].reference);
                for (int id = 5; id <= ROW_COUNT; id++) {
                    readRow(unitOfWork, id, retained);
                }

                Assert.assertNull("The restored dead key must be removed by automatic cleanup",
                        restoredMap.getCacheKey(restoredKeys[2].getKey(), false));
                Assert.assertNull(restoredKeys[2].getOwningMap());
                Assert.assertSame(restoredKeys[0], restoredMap.getCacheKey(restoredKeys[0].getKey(), false));
                Assert.assertSame(repopulated, restoredKeys[0].getObject());
                Assert.assertSame(originalMap, restoredKeys[0].getOwningMap());
                Assert.assertSame(replacementKey, restoredMap.getCacheKey(replacementKey.getKey(), false));
                Assert.assertSame(replacement, replacementKey.getObject());
                Assert.assertSame(restoredMap, replacementKey.getOwningMap());
                Assert.assertSame(restoredKeys[3], restoredMap.getCacheKey(restoredKeys[3].getKey(), false));
                Assert.assertSame(retained.get(3), restoredKeys[3].getObject());
                Assert.assertSame(originalMap, restoredKeys[3].getOwningMap());
                for (QueueableWeakCacheKey originalKey : originalKeys) {
                    Assert.assertSame(originalKey, originalMap.getCacheKey(originalKey.getKey(), false));
                    Assert.assertSame(originalMap, originalKey.getOwningMap());
                }
                Assert.assertSame(repopulated, readRow(unitOfWork, 1, retained));
                Assert.assertSame(replacement, readRow(unitOfWork, 2, retained));
                Assert.assertSame(retained.get(3), readRow(unitOfWork, 4, retained));
                Assert.assertTrue(unitOfWork.isActive());
            } finally {
                unitOfWork.release();
            }
        } finally {
            Reference.reachabilityFence(retained);
            client.release();
        }
    }

    private void verifyAutomaticCleanup(boolean detach) {
        EntityManager em = emf.createEntityManager();
        List<WeakCacheEntity> retained = new ArrayList<>();
        try {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) em.unwrap(JpaEntityManager.class).getUnitOfWork();
            Assert.assertEquals(ReferenceMode.FORCE_WEAK, unitOfWork.getReferenceMode());
            IdentityMap map = unitOfWork.getIdentityMapAccessorInstance().getIdentityMap(WeakCacheEntity.class);
            Assert.assertTrue(map instanceof WeakUnitOfWorkIdentityMap);
            WeakCacheEntity first = em.find(WeakCacheEntity.class, 1);
            retained.add(first);
            QueueableWeakCacheKey oldKey = (QueueableWeakCacheKey)
                    unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(first);
            Reference<?> oldReference = oldKey.reference;
            if (detach) {
                em.detach(first);
                Assert.assertFalse(em.contains(first));
                Assert.assertNull(oldKey.getOwningMap());
            }
            collect(oldReference);

            WeakCacheEntity live = em.find(WeakCacheEntity.class, 1);
            retained.add(live);
            Assert.assertNotSame(first, live);
            Assert.assertEquals("value-1", live.getValue());
            QueueableWeakCacheKey liveKey = (QueueableWeakCacheKey)
                    unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(live);
            if (detach) {
                Assert.assertNotSame(oldKey, liveKey);
            } else {
                Assert.assertSame(oldKey, liveKey);
                Assert.assertNotSame(oldReference, liveKey.reference);
            }

            WeakCacheEntity dead = em.find(WeakCacheEntity.class, 2);
            retained.add(dead);
            QueueableWeakCacheKey deadKey = (QueueableWeakCacheKey)
                    unitOfWork.getIdentityMapAccessorInstance().getCacheKeyForObject(dead);
            collect(deadKey.reference);

            // Fresh registrations cross the map's normal initial cleanup threshold of 32.
            findRemainingRows(em, 3, retained);

            Assert.assertNull("The dead control proves automatic cleanup ran", map.getCacheKey(deadKey.getKey(), false));
            Assert.assertNull(deadKey.getOwningMap());
            Assert.assertSame("An obsolete queue event must retain the live key", liveKey, map.getCacheKey(liveKey.getKey(), false));
            Assert.assertSame(live, liveKey.getObject());
            Assert.assertSame(map, liveKey.getOwningMap());
            Assert.assertSame(live, em.find(WeakCacheEntity.class, 1));
            Assert.assertTrue(em.contains(live));
        } finally {
            Reference.reachabilityFence(retained);
            em.close();
        }
    }

    private static void findRemainingRows(EntityManager em, int firstId, List<WeakCacheEntity> retained) {
        for (int id = firstId; id <= ROW_COUNT; id++) {
            WeakCacheEntity entity = em.find(WeakCacheEntity.class, id);
            Assert.assertNotNull(entity);
            retained.add(entity);
        }
    }

    private static WeakCacheEntity readRow(UnitOfWorkImpl unitOfWork, int id, List<WeakCacheEntity> retained) {
        WeakCacheEntity entity = (WeakCacheEntity) unitOfWork.readObject(WeakCacheEntity.class,
                new ExpressionBuilder().get("id").equal(id));
        Assert.assertNotNull(entity);
        retained.add(entity);
        return entity;
    }

    private static void collect(Reference<?> reference) {
        Assert.assertTrue(reference.enqueue());
        Assert.assertNull(reference.get());
    }
}
