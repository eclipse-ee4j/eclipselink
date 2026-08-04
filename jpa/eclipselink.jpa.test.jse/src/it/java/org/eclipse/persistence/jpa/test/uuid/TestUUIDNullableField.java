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

package org.eclipse.persistence.jpa.test.uuid;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDNullableFieldEntity;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Regression test for reading NULL values from a UUID-typed (non-PK) column.
 */
@RunWith(EmfRunner.class)
public class TestUUIDNullableField {

    private static final String TABLE_NAME = "uuid_nullable_field";
    private static final String UUID_COLUMN_NAME = "optional_uuid";

    @Emf(
            name = "TestUUIDNullableField",
            createTables = DDLGen.DROP_CREATE,
            classes = { UUIDNullableFieldEntity.class }
    )
    private EntityManagerFactory emf;

    @Before
    public void verifyPostgreSQLUUIDColumn() {
        Assume.assumeTrue("Test requires PostgreSQL native UUID column support", getPlatform().isPostgreSQL());

        EntityManager em = emf.createEntityManager();
        try {
            String dataType = (String) em.createNativeQuery(
                            "SELECT data_type FROM information_schema.columns "
                                    + "WHERE table_schema = current_schema() "
                                    + "AND table_name = ? AND column_name = ?")
                    .setParameter(1, TABLE_NAME)
                    .setParameter(2, UUID_COLUMN_NAME)
                    .getSingleResult();
            assertEquals("PostgreSQL column must use the native UUID type", "uuid", dataType);
        } finally {
            em.close();
        }
    }

    @Test
    public void testNullUUIDFieldRoundtrip() {
        Long id = persistWithNullUuid();
        verifyNullUuidOnRead(id);
    }

    @Test
    public void testNonNullUUIDFieldRoundtrip() {
        UUID uuid = UUID.randomUUID();
        Long id = persistWithUuid(uuid);
        verifyUuidOnRead(id, uuid);
    }

    private DatabasePlatform getPlatform() {
        return ((EntityManagerFactoryImpl) emf).getDatabaseSession().getPlatform();
    }

    private Long persistWithNullUuid() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            UUIDNullableFieldEntity entity = new UUIDNullableFieldEntity();
            entity.setName("null-uuid-entity");
            entity.setOptionalUuid(null);
            em.persist(entity);
            em.getTransaction().commit();
            return entity.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    private Long persistWithUuid(UUID uuid) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            UUIDNullableFieldEntity entity = new UUIDNullableFieldEntity();
            entity.setName("non-null-uuid-entity");
            entity.setOptionalUuid(uuid);
            em.persist(entity);
            em.getTransaction().commit();
            return entity.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    private void verifyNullUuidOnRead(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            // Clear cache so the entity is read from the database
            em.getEntityManagerFactory().getCache().evictAll();
            UUIDNullableFieldEntity entity = em.find(UUIDNullableFieldEntity.class, id);
            assertNotNull(entity);
            assertNull("UUID field should be null when NULL was stored", entity.getOptionalUuid());

            // Also verify via JPQL query, which goes through the stream/list path that originally failed
            List<UUIDNullableFieldEntity> results = em
                    .createQuery("SELECT e FROM UUIDNullableFieldEntity e WHERE e.id = :id", UUIDNullableFieldEntity.class)
                    .setParameter("id", id)
                    .getResultList();
            assertEquals(1, results.size());
            assertNull("UUID field should be null when read via JPQL", results.get(0).getOptionalUuid());
        } finally {
            em.close();
        }
    }

    private void verifyUuidOnRead(Long id, UUID expected) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            UUIDNullableFieldEntity entity = em.find(UUIDNullableFieldEntity.class, id);
            assertNotNull(entity);
            assertEquals("UUID field should round-trip correctly", expected, entity.getOptionalUuid());
        } finally {
            em.close();
        }
    }
}
