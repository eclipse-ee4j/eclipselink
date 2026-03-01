/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.uuid;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDStringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestStringUUID {

    private static final String NAME_String = "Persist UUID String 1";

    @Emf(name = "uuidStringEmf", createTables = DDLGen.DROP_CREATE, classes = { UUIDStringEntity.class })
    private EntityManagerFactory uuidEmf;

    /**
     * Tests GenerationType.UUID with field based on String type.
     */
    @Test
    public void testUuid() {
        String uuid = persistUUIDStringEntity();
        queryAllUUIDStringEntity();
        queryWithParameterUUIDStringEntity(uuid);
        findUUIDStringEntityTest(uuid);
    }

    private String persistUUIDStringEntity() {
        EntityManager em = uuidEmf.createEntityManager();
        String uuidResult = null;
        try {
            em.getTransaction().begin();
            UUIDStringEntity entity = new UUIDStringEntity();
            entity.setName(NAME_String);
            em.persist(entity);
            uuidResult = entity.getId();
            assertNotNull(uuidResult);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        return uuidResult;
    }

    public void queryAllUUIDStringEntity() {
        EntityManager em = uuidEmf.createEntityManager();
        try {
            List<UUIDStringEntity> testEntities = em.createQuery("SELECT e FROM UUIDStringEntity AS e").getResultList();
            assertTrue(!testEntities.isEmpty());
            for (UUIDStringEntity entity: testEntities) {
                assertNotNull(UUID.fromString(entity.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    public void queryWithParameterUUIDStringEntity(String id) {
        EntityManager em = uuidEmf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM UUIDStringEntity AS e WHERE e.id = :id");
            query.setParameter("id", id);
            List<UUIDStringEntity> testEntities = query.getResultList();
            assertTrue(!testEntities.isEmpty());
            for (UUIDStringEntity entity: testEntities) {
                assertNotNull(entity);
                assertNotNull(UUID.fromString(entity.getId()));
                assertEquals(id, entity.getId());
                assertEquals(NAME_String, entity.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    public void findUUIDStringEntityTest(String id) {
        EntityManager em = uuidEmf.createEntityManager();
        try {
            UUIDStringEntity entity = em.find(UUIDStringEntity.class, id);
            assertNotNull(entity);
            assertEquals(id, entity.getId());
            assertEquals(NAME_String, entity.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
