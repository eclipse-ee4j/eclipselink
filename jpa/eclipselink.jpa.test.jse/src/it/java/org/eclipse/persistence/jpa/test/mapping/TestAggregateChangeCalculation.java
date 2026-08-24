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

// Contributors:
//     07/23/2026 - Andreas Lemmer
//       - 1455: Do not calculate changes for aggregates registered in the UnitOfWork
package org.eclipse.persistence.jpa.test.mapping;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.mapping.model.AggregateChangeEmbeddable;
import org.eclipse.persistence.jpa.test.mapping.model.AggregateChangeEntity;
import org.eclipse.persistence.jpa.test.mapping.model.BatchFetchAggregateChangeEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Aggregates (embeddables) have no identity of their own, so they must never be change-tracked as
 * roots of the UnitOfWork. They can nevertheless end up in the UnitOfWork's clone mapping, because
 * ObjectBuilder.buildWorkingCopyCloneFromRow bypasses the guards of the register methods whenever
 * the aggregate rows are built by an ObjectLevelReadQuery of their own.
 * <p>
 * Committing such a UnitOfWork used to fail with a DescriptorException wrapping a
 * NullPointerException, because the descriptor UnitOfWorkImpl.calculateChanges resolves for the
 * aggregate is the uninitialized prototype registered on the session rather than the initialized
 * clone held by the owning mapping.
 */
@RunWith(EmfRunner.class)
public class TestAggregateChangeCalculation {

    @Emf(name = "aggregateChangeEMF", createTables = DDLGen.DROP_CREATE,
            classes = { AggregateChangeEntity.class, AggregateChangeEmbeddable.class })
    private EntityManagerFactory emf;

    @Emf(name = "batchFetchAggregateChangeEMF", createTables = DDLGen.DROP_CREATE,
            classes = { BatchFetchAggregateChangeEntity.class, AggregateChangeEmbeddable.class })
    private EntityManagerFactory batchFetchEmf;

    /**
     * Selecting the embeddables of an element collection directly registers them in the UnitOfWork.
     * The following commit must not try to calculate their changes. See issue #1455.
     */
    @Test
    public void testCommitAfterSelectingEmbeddables() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AggregateChangeEntity entity = new AggregateChangeEntity(1);
            entity.getEmbeds().add(new AggregateChangeEmbeddable("one"));
            entity.getEmbeds().add(new AggregateChangeEmbeddable("two"));
            em.persist(entity);
            em.getTransaction().commit();
            em.clear();

            List<AggregateChangeEmbeddable> embeds = em.createQuery(
                    "SELECT y FROM AggregateChangeEntity x JOIN x.embeds y", AggregateChangeEmbeddable.class)
                    .getResultList();
            Assert.assertEquals(2, embeds.size());

            // Used to throw: A NullPointerException was thrown while extracting a value from the
            // instance variable [value] in the object [AggregateChangeEmbeddable].
            em.getTransaction().begin();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Batch fetching an element collection of embeddables runs a ReadAllQuery on the aggregate
     * descriptor, which registers the embeddables in the UnitOfWork. Reading the owning entities
     * under a pessimistic lock makes them be built in the UnitOfWork rather than in the shared
     * cache, so the batch query is executed against the UnitOfWork. The following commit must not
     * try to calculate the changes of the batch fetched embeddables.
     */
    @Test
    public void testCommitAfterBatchFetchingEmbeddablesWithPessimisticLock() {
        EntityManager em = batchFetchEmf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (int id = 1; id <= 2; id++) {
                BatchFetchAggregateChangeEntity entity = new BatchFetchAggregateChangeEntity(id);
                entity.getEmbeds().add(new AggregateChangeEmbeddable("one-" + id));
                entity.getEmbeds().add(new AggregateChangeEmbeddable("two-" + id));
                em.persist(entity);
            }
            em.getTransaction().commit();
            em.clear();

            em.getTransaction().begin();
            List<BatchFetchAggregateChangeEntity> entities = em.createQuery(
                    "SELECT x FROM BatchFetchAggregateChangeEntity x", BatchFetchAggregateChangeEntity.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();
            Assert.assertEquals(2, entities.size());

            // Trigger the batch query for the element collection.
            for (BatchFetchAggregateChangeEntity entity : entities) {
                Assert.assertEquals(2, entity.getEmbeds().size());
            }

            // Used to throw: A NullPointerException was thrown while extracting a value from the
            // instance variable [value] in the object [AggregateChangeEmbeddable].
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
