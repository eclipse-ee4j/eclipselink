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
import org.eclipse.persistence.jpa.test.uuid.model.UUIDUUIDEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestUUIDUUID {

    private static final String NAME_UUID = "Persist UUID UUID 1";

    @Emf(name = "uuidUUIDEmf", createTables = DDLGen.DROP_CREATE, classes = { UUIDUUIDEntity.class })
    private EntityManagerFactory uuidEmf;

    /**
     * Tests GenerationType.UUID with field based on java.util.UUID type.
     */
    @Test
    public void testUuid() {
        UUID uuid = persistUUIDUUIDEntity();
        queryAllUUIDUUIDEntity();
        queryWithParameterUUIDUUIDEntity(uuid);
        findUUIDUUIDEntityTest(uuid);
    }

    private UUID persistUUIDUUIDEntity() {
        EntityManager em = uuidEmf.createEntityManager();
        UUID uuidResult = null;
        try {
            em.getTransaction().begin();
            UUIDUUIDEntity entity = new UUIDUUIDEntity();
            entity.setName(NAME_UUID);
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

    public void queryAllUUIDUUIDEntity() {
        EntityManager em = uuidEmf.createEntityManager();
        try {
            List<UUIDUUIDEntity> testEntities = em.createQuery("SELECT e FROM UUIDUUIDEntity AS e").getResultList();
            assertTrue(testEntities.size() > 0);
            for (UUIDUUIDEntity entity: testEntities) {
                assertNotNull(entity.getId());
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

    public void queryWithParameterUUIDUUIDEntity(UUID id) {
        EntityManager em = uuidEmf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM UUIDUUIDEntity AS e WHERE e.id = :id");
            query.setParameter("id", id);
            List<UUIDUUIDEntity> testEntities = query.getResultList();
            assertTrue(testEntities.size() > 0);
            for (UUIDUUIDEntity entity: testEntities) {
                assertNotNull(entity);
                assertNotNull(entity.getId());
                assertEquals(id, entity.getId());
                assertEquals(NAME_UUID, entity.getName());
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

    public void findUUIDUUIDEntityTest(UUID id) {
        EntityManager em = uuidEmf.createEntityManager();
        try {
            UUIDUUIDEntity entity = em.find(UUIDUUIDEntity.class, id);
            assertNotNull(entity);
            assertEquals(id, entity.getId());
            assertEquals(NAME_UUID, entity.getName());
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

    @Test
    public void testIssue1554() {
        final String uuidName = "Issue testIssue1554 UUID";
        EntityManager em = uuidEmf.createEntityManager();
        try {
            em.getTransaction().begin();
            UUIDUUIDEntity entity = new UUIDUUIDEntity();
            entity.setName(uuidName);
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();
            UUID id = entity.getId();
            em.getTransaction().begin();
            var cb = em.getCriteriaBuilder();
            var query = cb.createTupleQuery();
            var root = query.from(UUIDUUIDEntity.class);
            query.multiselect(root.get("name"),
                    cb.localTime(),
                    cb.localDateTime(),
                    cb.localDate()
            );
            query.where(cb.equal(root.get("id"), id));
            List<?> result = em.createQuery(query).getResultList();
            em.getTransaction().commit();
            assertEquals(result.size(), 1);
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
