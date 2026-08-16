/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.record;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.record.model.RecordAggregateChildEntity;
import org.eclipse.persistence.jpa.test.record.model.RecordAggregateContainer;
import org.eclipse.persistence.jpa.test.record.model.RecordAggregateParentEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests a {@code @OneToMany} collection held inside an {@code @Embeddable} record.
 */
@RunWith(EmfRunner.class)
public class TestOneToManyInRecord {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = {
            RecordAggregateParentEntity.class, RecordAggregateContainer.class, RecordAggregateChildEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testPersistThenPersistAgain() {
        EntityManager em = emf.createEntityManager();
        try {
            // First persist
            em.getTransaction().begin();
            RecordAggregateChildEntity child = new RecordAggregateChildEntity("child");
            RecordAggregateParentEntity parent = new RecordAggregateParentEntity(1L, new RecordAggregateContainer(List.of(child)));
            em.persist(parent);
            em.getTransaction().commit();
            em.clear();

            // Load the parent again and re-persist it (as the DDD flow does on update).
            em.getTransaction().begin();
            RecordAggregateParentEntity loaded = em.find(RecordAggregateParentEntity.class, 1L);
            em.persist(loaded);
            em.getTransaction().commit();
            em.clear();

            RecordAggregateParentEntity found = em.find(RecordAggregateParentEntity.class, 1L);
            assertNotNull("Parent should be found", found);
            assertEquals(1, found.getContainer().children().size());
            assertEquals("child", found.getContainer().children().get(0).getName());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
