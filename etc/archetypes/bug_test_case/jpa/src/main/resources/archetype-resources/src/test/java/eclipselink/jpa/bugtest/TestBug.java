/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package eclipselink.jpa.bugtest;

import jakarta.persistence.*;

import eclipselink.jpa.bugtest.domain.TestEntityMaster;
import eclipselink.jpa.bugtest.domain.TestEntityDetail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.junit.Test;

import java.util.List;

public class TestBug {

    private static final long ID = 1L;
    private static final String NAME = "Master 1";

    @Test
    public void persistTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-jpa-pu");
        assertNotNull(entityManagerFactory);

        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            em.getTransaction().begin();
            boolean value = false;
            TestEntityMaster entity = new TestEntityMaster(10, "Persist Master 1");
            em.persist(entity);
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
    }

    @Test
    public void queryTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-jpa-pu");
        assertNotNull(entityManagerFactory);

        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            List<TestEntityMaster> testEntities = em.createQuery("SELECT e FROM TestEntityMaster AS e").getResultList();
            assertTrue(testEntities.size() > 0);
            assertEquals(ID, testEntities.get(0).getId());
            assertEquals(NAME, testEntities.get(0).getName());
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
    public void findTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-jpa-pu");
        assertNotNull(entityManagerFactory);

        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            TestEntityMaster testMaster = em.find(TestEntityMaster.class, ID);
            assertNotNull(testMaster);
            assertEquals(ID, testMaster.getId());
            assertEquals(NAME, testMaster.getName());

            List<TestEntityDetail> testDetails = testMaster.getDetails();
            assertNotNull(testDetails);
            assertEquals(2, testDetails.size());

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
    public void oldStyleTest() {
        Project project = XMLProjectReader.read("PoJoTest.xml");
        DatabaseSession session = project.createDatabaseSession();
        session.login();

        UnitOfWork uow = session.acquireUnitOfWork();
        TestEntityMaster testMaster = new TestEntityMaster();
        testMaster.setId(1);
        TestEntityMaster testMasterResult = (TestEntityMaster)uow.readObject(testMaster);
        assertNotNull(testMasterResult);
        assertEquals(ID, testMasterResult.getId());
        assertEquals(NAME, testMasterResult.getName());

    }
}
