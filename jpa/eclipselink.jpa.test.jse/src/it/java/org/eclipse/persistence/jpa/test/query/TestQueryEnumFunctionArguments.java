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
package org.eclipse.persistence.jpa.test.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.query.model.EnumFunctionEntity;
import org.eclipse.persistence.jpa.test.query.model.EnumFunctionStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * An enum literal or parameter that is an argument of CASE, COALESCE or NULLIF must be converted
 * through the enum mapping of the attribute it is compared with or assigned to (issue #2848).
 * <p>
 * Rows before each test: 1 PENDING, 2 PENDING, 3 DONE.
 */
@RunWith(EmfRunner.class)
public class TestQueryEnumFunctionArguments {

    private static final String STATUS = EnumFunctionStatus.class.getName() + ".";

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EnumFunctionEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testUpdateSetSearchedCaseLiterals() {
        int rows = executeUpdate("UPDATE EnumFunctionEntity e SET e.status = CASE WHEN e.id = 1 THEN "
                + STATUS + "DONE ELSE " + STATUS + "PENDING END", null);
        Assert.assertEquals(3, rows);
        assertStatusColumn("DONE", "PENDING", "PENDING");
    }

    @Test
    public void testUpdateSetSimpleCaseLiteral() {
        int rows = executeUpdate("UPDATE EnumFunctionEntity e SET e.status = CASE e.id WHEN 1 THEN "
                + STATUS + "DONE ELSE e.status END", null);
        Assert.assertEquals(3, rows);
        assertStatusColumn("DONE", "PENDING", "DONE");
    }

    @Test
    public void testUpdateSetCoalesceLiteral() {
        int rows = executeUpdate("UPDATE EnumFunctionEntity e SET e.status = COALESCE(NULLIF(e.status, e.status), "
                + STATUS + "DONE)", null);
        Assert.assertEquals(3, rows);
        assertStatusColumn("DONE", "DONE", "DONE");
    }

    @Test
    public void testUpdateSetCoalesceParameter() {
        int rows = executeUpdate("UPDATE EnumFunctionEntity e SET e.status = COALESCE(NULLIF(e.status, e.status), :status)",
                EnumFunctionStatus.DONE);
        Assert.assertEquals(3, rows);
        assertStatusColumn("DONE", "DONE", "DONE");
    }

    @Test
    public void testUpdateSetNullIfLiteral() {
        int rows = executeUpdate("UPDATE EnumFunctionEntity e SET e.status = NULLIF(e.status, " + STATUS + "DONE)", null);
        Assert.assertEquals(3, rows);
        assertStatusColumn("PENDING", "PENDING", null);
    }

    @Test
    public void testSelectComparedWithSearchedCaseLiterals() {
        List<Integer> ids = selectIds("SELECT e FROM EnumFunctionEntity e WHERE e.status = CASE WHEN e.id = 1 THEN "
                + STATUS + "PENDING ELSE " + STATUS + "DONE END", null);
        Assert.assertEquals(Arrays.asList(1, 3), ids);
    }

    @Test
    public void testSelectCoalesceLiteralComparedWithLiteral() {
        List<Integer> ids = selectIds("SELECT e FROM EnumFunctionEntity e WHERE COALESCE(e.status, "
                + STATUS + "PENDING) = " + STATUS + "DONE", null);
        Assert.assertEquals(Collections.singletonList(3), ids);
    }

    @Test
    public void testSelectNullIfLiteralIsNull() {
        List<Integer> ids = selectIds("SELECT e FROM EnumFunctionEntity e WHERE NULLIF(e.status, "
                + STATUS + "DONE) IS NULL", null);
        Assert.assertEquals(Collections.singletonList(3), ids);
    }

    @Test
    public void testSelectParameterComparedWithNullIfParameter() {
        // A parameter on both sides of the comparison must not make the local bases reference each other
        List<Integer> ids = selectIds("SELECT e FROM EnumFunctionEntity e WHERE :status = NULLIF(:status, e.status)",
                EnumFunctionStatus.PENDING);
        Assert.assertEquals(Collections.singletonList(3), ids);
    }

    @Test
    public void testSelectLiteralComparedWithNullIfLiteral() {
        List<Integer> ids = selectIds("SELECT e FROM EnumFunctionEntity e WHERE " + STATUS + "PENDING = NULLIF("
                + STATUS + "PENDING, e.status)", null);
        Assert.assertEquals(Collections.singletonList(3), ids);
    }

    @Test
    public void testSelectParameterComparedWithCaseParameter() {
        List<Integer> ids = selectIds("SELECT e FROM EnumFunctionEntity e WHERE :status = CASE WHEN e.id = 1 THEN e.status ELSE :status END",
                EnumFunctionStatus.DONE);
        Assert.assertEquals(Arrays.asList(2, 3), ids);
    }

    @Test
    public void testCriteriaCoalesceValueComparedWithValue() {
        List<Integer> ids = inTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EnumFunctionEntity> cq = cb.createQuery(EnumFunctionEntity.class);
            Root<EnumFunctionEntity> root = cq.from(EnumFunctionEntity.class);
            Path<EnumFunctionStatus> status = root.get("status");
            cq.where(cb.equal(cb.coalesce(status, EnumFunctionStatus.PENDING), EnumFunctionStatus.DONE));
            return ids(em.createQuery(cq).getResultList());
        });
        Assert.assertEquals(Collections.singletonList(3), ids);
    }

    @Test
    public void testCriteriaComparedWithSelectCaseValues() {
        List<Integer> ids = inTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EnumFunctionEntity> cq = cb.createQuery(EnumFunctionEntity.class);
            Root<EnumFunctionEntity> root = cq.from(EnumFunctionEntity.class);
            Path<EnumFunctionStatus> status = root.get("status");
            cq.where(cb.equal(status, cb.<EnumFunctionStatus>selectCase()
                    .when(cb.equal(root.get("id"), 1), EnumFunctionStatus.PENDING)
                    .otherwise(EnumFunctionStatus.DONE)));
            return ids(em.createQuery(cq).getResultList());
        });
        Assert.assertEquals(Arrays.asList(1, 3), ids);
    }

    @Test
    public void testCriteriaUpdateSetNullIfValue() {
        int rows = inTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<EnumFunctionEntity> cu = cb.createCriteriaUpdate(EnumFunctionEntity.class);
            Root<EnumFunctionEntity> root = cu.from(EnumFunctionEntity.class);
            Path<EnumFunctionStatus> status = root.get("status");
            cu.set(status, cb.nullif(status, EnumFunctionStatus.DONE));
            return em.createQuery(cu).executeUpdate();
        });
        Assert.assertEquals(3, rows);
        assertStatusColumn("PENDING", "PENDING", null);
    }

    private int executeUpdate(String jpql, EnumFunctionStatus status) {
        return inTransaction(em -> {
            Query query = em.createQuery(jpql);
            if (status != null) {
                query.setParameter("status", status);
            }
            return query.executeUpdate();
        });
    }

    private List<Integer> selectIds(String jpql, EnumFunctionStatus status) {
        return inTransaction(em -> {
            TypedQuery<EnumFunctionEntity> query = em.createQuery(jpql, EnumFunctionEntity.class);
            if (status != null) {
                query.setParameter("status", status);
            }
            return ids(query.getResultList());
        });
    }

    private <T> T inTransaction(Function<EntityManager, T> work) {
        populate();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM EnumFunctionEntity e").executeUpdate();
            em.persist(new EnumFunctionEntity(1, EnumFunctionStatus.PENDING));
            em.persist(new EnumFunctionEntity(2, EnumFunctionStatus.PENDING));
            em.persist(new EnumFunctionEntity(3, EnumFunctionStatus.DONE));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        emf.getCache().evictAll();
    }

    /**
     * Reads the column directly, so a value written without the enum conversion (an ordinal) is detected.
     */
    private void assertStatusColumn(String... expected) {
        EntityManager em = emf.createEntityManager();
        try {
            List<?> rows = em.createNativeQuery("SELECT STATUS FROM ENUM_FUNCTION_ENTITY ORDER BY ID").getResultList();
            Assert.assertEquals(Arrays.asList(expected), rows);
        } finally {
            em.close();
        }
    }

    private static List<Integer> ids(List<EnumFunctionEntity> entities) {
        List<Integer> ids = new ArrayList<>();
        for (EnumFunctionEntity entity : entities) {
            ids.add(entity.getId());
        }
        Collections.sort(ids);
        return ids;
    }
}
