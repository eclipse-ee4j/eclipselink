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
import org.eclipse.persistence.jpa.test.record.model.NestedRecordChild;
import org.eclipse.persistence.jpa.test.record.model.NestedRecordEntity;
import org.eclipse.persistence.jpa.test.record.model.NestedRecordParent;
import org.eclipse.persistence.jpa.test.record.model.NestedRecordSimple;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for a nested {@code @Embedded} record used inside another {@code @Embeddable} record.
 */
@RunWith(EmfRunner.class)
public class TestNestedRecordEmbeddable {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = {
            NestedRecordEntity.class, NestedRecordParent.class, NestedRecordChild.class, NestedRecordSimple.class })
    private EntityManagerFactory emf;

    @Test
    public void testPersistNestedEmbeddedRecord() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            NestedRecordSimple simple = new NestedRecordSimple(100L, "value-1");
            em.persist(simple);

            NestedRecordChild child = new NestedRecordChild("kind-1", simple);
            NestedRecordParent parent = new NestedRecordParent("status-1", child);
            NestedRecordEntity entity = new NestedRecordEntity(1L, parent, "description-1");

            em.persist(entity);
            em.getTransaction().commit();
            em.clear();

            NestedRecordEntity found = em.find(NestedRecordEntity.class, 1L);
            assertNotNull("Entity should be found after persist", found);
            assertEquals("description-1", found.getDescription());
            assertNotNull("Nested parent should not be null", found.getParent());
            assertEquals("kind-1", found.getParent().child().kind());
            assertEquals("value-1", found.getParent().child().simple().getValue());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
