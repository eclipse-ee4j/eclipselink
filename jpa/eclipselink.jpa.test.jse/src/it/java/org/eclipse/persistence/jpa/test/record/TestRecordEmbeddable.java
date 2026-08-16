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
import org.eclipse.persistence.jpa.test.record.model.RecordEntity;
import org.eclipse.persistence.jpa.test.record.model.RecordId;
import org.eclipse.persistence.jpa.test.record.model.RecordValue;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@code java.lang.Record} used as JPA {@code @Embeddable} types.
 * <p>
 * This test covers:
 * <ul>
 *   <li>{@code @EmbeddedId} with a record type</li>
 *   <li>{@code @Embedded} attribute with a record type</li>
 *   <li>Persist, find, and merge operations</li>
 * </ul>
 * <p>
 * Prior to the fix for <a href="https://github.com/eclipse-ee4j/eclipselink/issues/2656">#2656</a>,
 * EclipseLink threw {@code IllegalAccessException: Can not set final field} when
 * method-accessed embeddable records were processed. The root cause was that
 * {@code RecordCopyPolicy} and {@code RecordInstantiationPolicy} were only
 * initialized for field-accessed descriptors, not method-accessed ones.
 */
@RunWith(EmfRunner.class)
public class TestRecordEmbeddable {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = {
            RecordEntity.class, RecordId.class, RecordValue.class })
    private EntityManagerFactory emf;

    @Test
    public void testPersistAndFind() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            UUID uuid = UUID.randomUUID();
            RecordId id = new RecordId(uuid);
            RecordValue value = new RecordValue("test-description", 42);
            RecordEntity entity = new RecordEntity(id, "test-entity", value);

            em.persist(entity);
            em.getTransaction().commit();

            // Clear to force a fresh read from the database
            em.clear();

            RecordEntity found = em.find(RecordEntity.class, id);
            assertNotNull("Entity should be found after persist", found);
            assertEquals("test-entity", found.getName());
            assertNotNull("Embedded value should not be null", found.getValue());
            assertEquals("test-description", found.getValue().description());
            assertEquals(42, found.getValue().amount());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testMerge() {
        EntityManager em = emf.createEntityManager();
        try {
            // Persist
            em.getTransaction().begin();
            UUID uuid = UUID.randomUUID();
            RecordId id = new RecordId(uuid);
            RecordValue value = new RecordValue("original", 10);
            RecordEntity entity = new RecordEntity(id, "original-name", value);
            em.persist(entity);
            em.getTransaction().commit();
            em.clear();

            // Merge with updated values
            em.getTransaction().begin();
            RecordValue updatedValue = new RecordValue("updated", 20);
            RecordEntity updated = new RecordEntity(id, "updated-name", updatedValue);
            RecordEntity merged = em.merge(updated);
            em.getTransaction().commit();

            assertNotNull("Merged entity should not be null", merged);
            assertEquals("updated-name", merged.getName());
            assertEquals("updated", merged.getValue().description());
            assertEquals(20, merged.getValue().amount());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testGetIdentifier() {
        UUID uuid = UUID.randomUUID();
        RecordId id = new RecordId(uuid);
        RecordEntity entity = new RecordEntity(id, "test-entity", new RecordValue("desc", 1));

        Object identifier = emf.getPersistenceUnitUtil().getIdentifier(entity);

        assertNotNull("Identifier should not be null", identifier);
        assertTrue("Identifier should be a RecordId", identifier instanceof RecordId);
        assertEquals("Identifier should equal the entity's embedded id", id, identifier);
    }
}
