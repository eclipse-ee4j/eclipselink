/*
 * Copyright (c) 2022, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import org.eclipse.persistence.jpa.test.uuid.model.UUIDUUIDEntity;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Ignore
public abstract class TestUuidCommon {

    private static final String NAME_UUID1 = "Persist UUID UUID 1";
    private static final String NAME_UUID2 = "Merge UUID UUID 2";

    abstract EntityManagerFactory getEmf();

    /**
     * Tests GenerationType.UUID with field based on java.util.UUID type.
     */
    @Test
    public void testUuid() {
        UUID uuid1 = persistUUIDUUIDEntity();
        mergeUUIDUUIDEntity();
        queryAllUUIDUUIDEntity();
        queryWithParameterUUIDUUIDEntity(uuid1);
        findUUIDUUIDEntityTest(uuid1);
    }

    private UUID persistUUIDUUIDEntity() {
        EntityManager em = getEmf().createEntityManager();
        UUID uuidResult = null;
        try {
            em.getTransaction().begin();
            UUIDUUIDEntity entity = new UUIDUUIDEntity();
            entity.setName(NAME_UUID1);
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

    private UUID mergeUUIDUUIDEntity() {
        EntityManager em = getEmf().createEntityManager();
        UUID uuidResult = null;
        try {
            em.getTransaction().begin();
            UUIDUUIDEntity entity = new UUIDUUIDEntity();
            entity.setName(NAME_UUID2);
            entity = em.merge(entity);
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
        EntityManager em = getEmf().createEntityManager();
        try {
            List<UUIDUUIDEntity> testEntities = em.createQuery("SELECT e FROM UUIDUUIDEntity AS e").getResultList();
            assertTrue(!testEntities.isEmpty());
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
        EntityManager em = getEmf().createEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM UUIDUUIDEntity AS e WHERE e.id = :id");
            query.setParameter("id", id);
            List<UUIDUUIDEntity> testEntities = query.getResultList();
            assertTrue(!testEntities.isEmpty());
            for (UUIDUUIDEntity entity: testEntities) {
                assertNotNull(entity);
                assertNotNull(entity.getId());
                assertEquals(id, entity.getId());
                assertEquals(NAME_UUID1, entity.getName());
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
        EntityManager em = getEmf().createEntityManager();
        try {
            UUIDUUIDEntity entity = em.find(UUIDUUIDEntity.class, id);
            assertNotNull(entity);
            assertEquals(id, entity.getId());
            assertEquals(NAME_UUID1, entity.getName());
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
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            UUIDUUIDEntity entity = new UUIDUUIDEntity();
            entity.setName(uuidName);
            entity.setId(UUID.fromString("d04a98a2-4408-4cf3-b232-5cfaab58afc9"));
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
