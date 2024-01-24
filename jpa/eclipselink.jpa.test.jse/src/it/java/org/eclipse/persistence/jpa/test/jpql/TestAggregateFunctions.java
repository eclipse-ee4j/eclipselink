/*
 * Copyright (c) 2019, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.jpql;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.NoResultEntity;
import org.eclipse.persistence.jpa.test.jpql.model.SimpleEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestAggregateFunctions {

    @Emf(name = "noResultEMF", createTables = DDLGen.DROP_CREATE, classes = { NoResultEntity.class })
    private EntityManagerFactory noResultEmf;

    @Emf(name = "ResultEMF", createTables = DDLGen.DROP_CREATE, classes = { SimpleEntity.class })
    private EntityManagerFactory resultEmf;

    /**
     * Complex test of the aggregate functions in JPQL. 
     * For this test, there must be zero results in the entity table and the Entity state field 
     * must be a primitive type.
     * JPA 2.1 specification; Section 4.8.5 states aggregate functions (MIN, MAX, AVG, &amp; SUM)
     * must return a result of NULL if there are no values to apply the aggregate function to
     */
    @Test
    public void testEmptyAggregateFunctionsWithPrimitives() {
        EntityManager em = noResultEmf.createEntityManager();
        try {
            //Check to make sure the table is empty first
            TypedQuery<NoResultEntity> checkQuery = em.createQuery("SELECT n FROM NoResultEntity n", NoResultEntity.class);
            List<NoResultEntity> checkResult = checkQuery.getResultList();
            Assert.assertEquals("Entity table NoResultEntity must be empty for this test", 0, checkResult.size());

            Query q = em.createQuery("SELECT MIN(n.primitive) FROM NoResultEntity n");
            Object res = q.getSingleResult();
            Assert.assertNull("Result of MIN aggregate should have been NULL", res);

            q = em.createQuery("SELECT MAX(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertNull("Result of MAX aggregate should have been NULL", res);

            q = em.createQuery("SELECT AVG(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertNull("Result of AVG aggregate should have been NULL", res);

            q = em.createQuery("SELECT SUM(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertNull("Result of SUM aggregate should have been NULL", res);

            q = em.createQuery("SELECT COUNT(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of COUNT aggregate should have been a Long", 0L, res);
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
     * Complex test of the aggregate functions in JPQL. 
     * For this test, there must be zero results in the entity table and the Entity state field 
     * must be a primitive wrapper type.
     * JPA 2.1 specification; Section 4.8.5 states aggregate functions (MIN, MAX, AVG, &amp; SUM)
     * must return a result of NULL if there are no values to apply the aggregate function to
     */
    @Test
    public void testEmptyAggregateFunctionsWithWrappers() {
        EntityManager em = noResultEmf.createEntityManager();
        try {
            //Check to make sure the table is empty first
            TypedQuery<NoResultEntity> checkQuery = em.createQuery("SELECT n FROM NoResultEntity n", NoResultEntity.class);
            List<NoResultEntity> checkResult = checkQuery.getResultList();
            Assert.assertEquals("Entity table NoResultEntity must be empty for this test", 0, checkResult.size());

            Query q = em.createQuery("SELECT MIN(n.wrapper) FROM NoResultEntity n");
            Object res = q.getSingleResult();
            Assert.assertNull("Result of MIN aggregate should have been NULL", res);

            q = em.createQuery("SELECT MAX(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertNull("Result of MAX aggregate should have been NULL", res);

            q = em.createQuery("SELECT AVG(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertNull("Result of AVG aggregate should have been NULL", res);

            q = em.createQuery("SELECT SUM(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertNull("Result of SUM aggregate should have been NULL", res);

            q = em.createQuery("SELECT COUNT(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of COUNT aggregate should have been a Long", 0L, res);
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
     * Complex test of the aggregate functions in JPQL. 
     * For this test, there must be {@literal >}0 results in the entity table and the Entity state field
     * must be a primitive type.
     * This test is verification that aggregates return the correct result
     */
    @Test
    public void testAggregateFunctionsWithPrimitives() {
        List<SimpleEntity> entities = new ArrayList<>();

        SimpleEntity se = new SimpleEntity();
        se.setKeyString("SimpleEntity1");
        se.setItemInteger2(0);
        entities.add(se);

        SimpleEntity se2 = new SimpleEntity();
        se2.setKeyString("SimpleEntity2");
        se2.setItemInteger2(0);
        entities.add(se2);

        EntityManager em = resultEmf.createEntityManager();
        try {
            //populate
            em.getTransaction().begin();
            for(SimpleEntity e : entities) {
                em.persist(e);
            }
            em.getTransaction().commit();

            Query q = em.createQuery("SELECT MIN(se.itemInteger2) FROM SimpleEntity se");
            Object res = q.getSingleResult();
            Assert.assertEquals("Result of MIN aggregate should have been NULL", 0, res);

            Query q2 = em.createQuery("SELECT MAX(se.itemInteger2) FROM SimpleEntity se");
            Object res2 = q2.getSingleResult();
            Assert.assertEquals("Result of MAX aggregate should have been NULL", 0, res2);

            Query q3 = em.createQuery("SELECT AVG(se.itemInteger2) FROM SimpleEntity se");
            Object res3 = q3.getSingleResult();
            Assert.assertEquals("Result of AVG aggregate should have been NULL", (double) 0, res3);

            Query q4 = em.createQuery("SELECT SUM(se.itemInteger2) FROM SimpleEntity se");
            Object res4 = q4.getSingleResult();
            Assert.assertEquals("Result of SUM aggregate should have been NULL", 0L, res4);

            Query q5 = em.createQuery("SELECT COUNT(se.itemInteger2) FROM SimpleEntity se");
            Object res5 = q5.getSingleResult();
            Assert.assertEquals("Result of COUNT aggregate should have been a Long", 2L, res5);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                //clean up
                em.getTransaction().begin();
                for(SimpleEntity e : entities) {
                    em.remove(e);
                }
                em.getTransaction().commit();
                em.close();
            }
        }
    }

    /**
     * Complex test of the aggregate functions in JPQL. 
     * For this test, there must be {@literal >}0 results in the entity table and the Entity state field
     * must be a primitive wrapper type.
     * This test is verification that aggregates return the correct result
     */
    @Test
    public void testAggregateFunctionsWithWrappers() {
        List<SimpleEntity> entities = new ArrayList<>();

        SimpleEntity se = new SimpleEntity();
        se.setKeyString("SimpleEntity1");
        se.setItemInteger1(0);
        entities.add(se);

        SimpleEntity se2 = new SimpleEntity();
        se2.setKeyString("SimpleEntity2");
        se2.setItemInteger1(0);
        entities.add(se2);

        EntityManager em = resultEmf.createEntityManager();
        try {
            //populate
            em.getTransaction().begin();
            for(SimpleEntity e : entities) {
                em.persist(e);
            }
            em.getTransaction().commit();

            Query q = em.createQuery("SELECT MIN(se.itemInteger1) FROM SimpleEntity se");
            Object res = q.getSingleResult();
            Assert.assertEquals("Result of MIN aggregate should have been NULL", 0, res);

            Query q2 = em.createQuery("SELECT MAX(se.itemInteger1) FROM SimpleEntity se");
            Object res2 = q2.getSingleResult();
            Assert.assertEquals("Result of MAX aggregate should have been NULL", 0, res2);

            Query q3 = em.createQuery("SELECT AVG(se.itemInteger1) FROM SimpleEntity se");
            Object res3 = q3.getSingleResult();
            Assert.assertEquals("Result of AVG aggregate should have been NULL", (double) 0, res3);

            Query q4 = em.createQuery("SELECT SUM(se.itemInteger1) FROM SimpleEntity se");
            Object res4 = q4.getSingleResult();
            Assert.assertEquals("Result of SUM aggregate should have been NULL", 0L, res4);

            Query q5 = em.createQuery("SELECT COUNT(se.itemInteger1) FROM SimpleEntity se");
            Object res5 = q5.getSingleResult();
            Assert.assertEquals("Result of COUNT aggregate should have been a Long", 2L, res5);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                //clean up
                em.getTransaction().begin();
                for(SimpleEntity e : entities) {
                    em.remove(e);
                }
                em.getTransaction().commit();
                em.close();
            }
        }
    }
}
