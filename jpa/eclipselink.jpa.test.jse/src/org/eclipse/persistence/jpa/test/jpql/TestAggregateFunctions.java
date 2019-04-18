/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.NoResultEntity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestAggregateFunctions {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { NoResultEntity.class })
    private EntityManagerFactory emf;

    /**
     * Complex test of the aggregate functions in JPQL. 
     * For this test, there must be zero results in the entity table and the Entity state field 
     * must be a primitive type.
     * JPA 2.1 specification; Section 4.8.5 states aggregate functions (MIN, MAX, AVG, & SUM) 
     * must return a result of NULL if there are no values to apply the aggregate function to
     */
    @Test
    public void testEmptyAggregateFunctionsWithPrimitives() {
        EntityManager em = emf.createEntityManager();
        try {
            //Check to make sure the table is empty first
            TypedQuery<NoResultEntity> checkQuery = em.createQuery("SELECT n FROM NoResultEntity n", NoResultEntity.class);
            List<NoResultEntity> checkResult = checkQuery.getResultList();
            Assert.assertEquals("Entity table NoResultEntity must be empty for this test", 0, checkResult.size());

            Query q = em.createQuery("SELECT MIN(n.primitive) FROM NoResultEntity n");
            Object res = q.getSingleResult();
            Assert.assertEquals("Result of MIN aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT MAX(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of MAX aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT AVG(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of AVG aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT SUM(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of SUM aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT COUNT(n.primitive) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of COUNT aggregate should have been a Long", new Long(0), res);
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
     * JPA 2.1 specification; Section 4.8.5 states aggregate functions (MIN, MAX, AVG, & SUM) 
     * must return a result of NULL if there are no values to apply the aggregate function to
     */
    @Test
    public void testEmptyAggregateFunctionsWithWrappers() {
        EntityManager em = emf.createEntityManager();
        try {
            //Check to make sure the table is empty first
            TypedQuery<NoResultEntity> checkQuery = em.createQuery("SELECT n FROM NoResultEntity n", NoResultEntity.class);
            List<NoResultEntity> checkResult = checkQuery.getResultList();
            Assert.assertEquals("Entity table NoResultEntity must be empty for this test", 0, checkResult.size());

            Query q = em.createQuery("SELECT MIN(n.wrapper) FROM NoResultEntity n");
            Object res = q.getSingleResult();
            Assert.assertEquals("Result of MIN aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT MAX(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of MAX aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT AVG(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of AVG aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT SUM(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of SUM aggregate should have been NULL", null, res);

            q = em.createQuery("SELECT COUNT(n.wrapper) FROM NoResultEntity n");
            res = q.getSingleResult();
            Assert.assertEquals("Result of COUNT aggregate should have been a Long", new Long(0), res);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
