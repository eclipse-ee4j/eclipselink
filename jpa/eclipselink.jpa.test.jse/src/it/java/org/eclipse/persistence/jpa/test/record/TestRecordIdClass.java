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
import org.eclipse.persistence.jpa.test.record.model.RecordIdClass;
import org.eclipse.persistence.jpa.test.record.model.RecordIdClassEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for a {@code java.lang.Record} used as a JPA {@code @IdClass}.
 */
@RunWith(EmfRunner.class)
public class TestRecordIdClass {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { RecordIdClassEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testPersistAndFind() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            RecordIdClassEntity entity = new RecordIdClassEntity("code-1", "description-1");
            em.persist(entity);
            em.getTransaction().commit();
            em.clear();

            RecordIdClass idClass = new RecordIdClass(entity.getId(), entity.getCode());
            RecordIdClassEntity found = em.find(RecordIdClassEntity.class, idClass);
            assertNotNull("Entity should be found after persist", found);
            assertEquals("description-1", found.getDescription());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testGetIdentifier() {
        RecordIdClassEntity entity = new RecordIdClassEntity("code-1", "description-1");

        Object identifier = emf.getPersistenceUnitUtil().getIdentifier(entity);

        assertNotNull("Identifier should not be null", identifier);
        assertTrue("Identifier should be a RecordIdClass", identifier instanceof RecordIdClass);
        RecordIdClass idClass = (RecordIdClass) identifier;
        assertEquals(entity.getId(), idClass.id());
        assertEquals("code-1", idClass.code());
    }
}
