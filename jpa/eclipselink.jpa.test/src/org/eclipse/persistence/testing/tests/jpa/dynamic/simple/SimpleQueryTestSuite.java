/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     07/16/2015-2.7 Lukas Jungmann, Tomas Kraus
//       - 429760: Tests whether dynamic entity descriptor is available in session during
//                 query execution after it was created within the same transaction.
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple;

import static org.eclipse.persistence.logging.SessionLog.WARNING;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleQueryTestSuite {

    /** Entity manager factory. */
    private static EntityManagerFactory emf = null;
    /** Entity manager. */
    private EntityManager em = null;
    /** Test logger. */
    private SessionLog log;

    /**
     * Initializes this test class. Entity manager factory is created.
     */
    @BeforeClass
    public static void setUp() {
        emf = DynamicTestHelper.createEMF(DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME);
    }

    /**
     * Tears down this test class. Entity manager factory is destroyed.
     */
    @AfterClass
    public static void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Creates database structures for this test.
     */
    public void initTable() {
        final EntityTransaction et = em.getTransaction();
        et.begin();
        try {
            em.createNativeQuery("CREATE TABLE SIMPLE_TYPE_1 (SID INTEGER, VAL_1 VARCHAR(15))").executeUpdate();
            et.commit();
        } catch (Exception ex) {
            log.log(WARNING, "Could not create SIMPLE_TYPE_1 table: ." + ex.getMessage());
            et.rollback();
            // This may fail without causing any serious problems.
        }
        et.begin();
        try {
            em.createNativeQuery("DELETE FROM SIMPLE_TYPE_1").executeUpdate();
            et.commit();
        } catch (Exception ex) {
            log.log(WARNING, "Could not clean SIMPLE_TYPE_1 table: ." + ex.getMessage());
            et.rollback();
            // Delete is required so exception here would fail the test.
            throw ex;
        }
    }

    /**
     * Deletes database structures for this test.
     */
    public void destroyTable() {
        final EntityTransaction et = em.getTransaction();
        et.begin();
        try {
            em.createNativeQuery("DROP TABLE SIMPLE_TYPE_1").executeUpdate();
            et.commit();
        } catch (Exception ex) {
            log.log(WARNING, "Could not drop SIMPLE_TYPE_1 table: ." + ex.getMessage());
            et.rollback();
            // This may fail without causing any serious problems.
        }
    }

    /**
     * Initializes environment before test.
     */
    @Before
    public void setUpTest() {
        em = emf.createEntityManager();
        log = em.unwrap(Session.class).getSessionLog();
        initTable();
    }

    /**
     * Cleans up environment after test.
     */
    @After
    public void tearDownTest() {
        destroyTable();
        em.close();
        em = null;
        log = null;
    }

    /**
     * Tests whether dynamic entity descriptor is available in session during query execution when it was created
     * right before it.
     */
    @Test
    public void testJPQLQuery() {
        final SessionLog log = em.unwrap(Session.class).getSessionLog();
        final EntityTransaction et = em.getTransaction();
        try {
            et.begin();

            DynamicHelper helper = new JPADynamicHelper(em);
            DynamicClassLoader dcl = helper.getDynamicClassLoader();
            Class<?> javaType = dcl.createDynamicClass("model.Simple");
            DynamicTypeBuilder typeBuilder = new JPADynamicTypeBuilder(
                    javaType, null, "SIMPLE_TYPE_1");
            typeBuilder.setPrimaryKeyFields("SID");
            typeBuilder.addDirectMapping("id", int.class, "SID");
            typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
            DynamicType dt = typeBuilder.getType();
            helper.addTypes(false, false, dt);

            DynamicEntity dt1 = dt.newDynamicEntity();
            dt1.set("id", 1);
            dt1.set("value1", "some string");
            em.persist(dt1);
            em.flush();

            DynamicEntity jpqlEntity = em.createQuery("SELECT s FROM Simple s", dt.getJavaClass()).getSingleResult();
            DynamicEntity foundEntity = em.find(dt.getJavaClass(), 1);
            Assert.assertEquals(jpqlEntity, foundEntity);

            et.commit();
        } catch (RollbackException e) {
            et.rollback();
            throw new RuntimeException(e);
        }
    }

}
