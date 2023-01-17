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

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.diagnostic;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.test.diagnostic.model.BranchADiagnostic;
import org.eclipse.persistence.jpa.test.diagnostic.model.BranchBDiagnostic;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//This test is about additional diagnostic log messages. This is why org.eclipse.persistence.testing.tests.jpa.diagnostic.LogWrapper is used there.
//It's used as a log handler registered to the session. There is selected log messages counter.
public class TestDiagnostic {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cachedeadlockdetection-pu");
        EntityManager em = emf.createEntityManager();
        try {
            DatabaseSession session = ((EntityManagerImpl) em).getDatabaseSession();
            try {
                session.executeNonSelectingSQL("DROP TABLE BRANCHB_DIAGNOSTIC");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("DROP TABLE BRANCHA_DIAGNOSTIC");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("CREATE TABLE BRANCHA_DIAGNOSTIC (id integer NOT NULL, PRIMARY KEY(id))");
                session.executeNonSelectingSQL("CREATE TABLE BRANCHB_DIAGNOSTIC (id integer NOT NULL, brancha_fk integer, PRIMARY KEY(id))");
                session.executeNonSelectingSQL("ALTER TABLE BRANCHB_DIAGNOSTIC ADD CONSTRAINT fk_brancha FOREIGN KEY ( brancha_fk ) REFERENCES brancha_diagnostic (id)");
            } catch (Exception ignore) {
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
        }
    }

    @Test
    public void testCorruptedCachePersistenceUnitProperty() {
        final int BRANCHA_ID = 1;
        final int BRANCHB_ID = 11;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("diagnostic-with-property-test-pu");
        EntityManager em = emf.createEntityManager();
        Session serverSession  = ((JpaEntityManager) em).getServerSession();
        LogWrapper logWrapper = new LogWrapper("corrupt_object_referenced_through_mapping");
        serverSession.setSessionLog(logWrapper);

        em.getTransaction().begin();

        //Initialize data
        BranchADiagnostic branchADiagnostic = new BranchADiagnostic();
        BranchBDiagnostic branchBDiagnostic = new BranchBDiagnostic();
        branchADiagnostic.setId(BRANCHA_ID);
        branchADiagnostic.getBranchBs().add(branchBDiagnostic);
        branchBDiagnostic.setId(BRANCHB_ID);
        branchBDiagnostic.setBranchA(branchADiagnostic);
        em.persist(branchADiagnostic);
        em.persist(branchBDiagnostic);
        em.flush();
        em.getTransaction().commit();

        //Simulate business transaction where we do work with Entity removed
        em.getTransaction().begin();
        if (branchADiagnostic.getBranchBs().contains(branchBDiagnostic)) {
            branchADiagnostic.getBranchBs().remove(branchBDiagnostic);
        }
        branchBDiagnostic.setBranchA(null);
        em.remove(branchBDiagnostic);
        em.getTransaction().commit();
        //After commit is entity branchBDiagnostic removed from database but still exists as object in memory (detached state)

        //Assign detached entity (branchBDiagnostic) to attached (branchADiagnostic)
        //Simulation of code logical error to mix into same object tree attached and detached entities
        //Add already removed (detached) entity back to the object tree
        //Required prerequisite are: caching enabled
        em.getTransaction().begin();
        branchADiagnostic.getBranchBs().add(branchBDiagnostic);
        branchBDiagnostic.setBranchA(branchADiagnostic);
        //Detached entity (branchBDiagnostic) is not persisted again - logical error
        em.getTransaction().commit();

        //This em.find() will resolve objects from cache
        BranchADiagnostic branchADiagnosticFindResult = em.find(BranchADiagnostic.class, BRANCHA_ID);
        assertEquals(1, branchADiagnosticFindResult.getBranchBs().size());

        //Verifies, that diagnostic message is produced
        assertEquals(1, logWrapper.getMessageCount());

        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        if (em.isOpen()) {
            em.close();
        }
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    public void testCorruptedCacheQueryHint() {
        final int BRANCHA_ID = 2;
        final int BRANCHB_ID = 22;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("diagnostic-test-pu");
        EntityManager em = emf.createEntityManager();
        Session serverSession  = ((JpaEntityManager) em).getServerSession();
        LogWrapper logWrapper = new LogWrapper("corrupt_object_referenced_through_mapping");
        serverSession.setSessionLog(logWrapper);

        em.getTransaction().begin();

        //Initialize data
        BranchADiagnostic branchADiagnostic = new BranchADiagnostic();
        BranchBDiagnostic branchBDiagnostic = new BranchBDiagnostic();
        branchADiagnostic.setId(BRANCHA_ID);
        branchADiagnostic.getBranchBs().add(branchBDiagnostic);
        branchBDiagnostic.setId(BRANCHB_ID);
        branchBDiagnostic.setBranchA(branchADiagnostic);
        em.persist(branchADiagnostic);
        em.persist(branchBDiagnostic);
        em.flush();
        em.getTransaction().commit();

        //Simulate business transaction where we do work with Entity removed
        em.getTransaction().begin();
        if (branchADiagnostic.getBranchBs().contains(branchBDiagnostic)) {
            branchADiagnostic.getBranchBs().remove(branchBDiagnostic);
        }
        branchBDiagnostic.setBranchA(null);
        em.remove(branchBDiagnostic);
        em.getTransaction().commit();
        //After commit is entity branchBDiagnostic removed from database but still exists as object in memory (detached state)

        //Assign detached entity (branchBDiagnostic) to attached (branchADiagnostic)
        //Simulation of code logical error to mix into same object tree attached and detached entities
        //Add already removed (detached) entity back to the object tree
        //Required prerequisite are: caching enabled
        em.getTransaction().begin();
        branchADiagnostic.getBranchBs().add(branchBDiagnostic);
        branchBDiagnostic.setBranchA(branchADiagnostic);
        //Detached entity (branchBDiagnostic) is not persisted again - logical error
        em.getTransaction().commit();

        //This em.find() will resolve objects from cache
        Map<String, Object> findProperties = new HashMap<>();
        findProperties.put("eclipselink.query-results-cache.validation", true);
        BranchADiagnostic branchADiagnosticFindResult = em.find(BranchADiagnostic.class, BRANCHA_ID, findProperties);
        assertEquals(1, branchADiagnosticFindResult.getBranchBs().size());

        Query query = em.createNamedQuery("findBranchADiagnosticById", BranchADiagnostic.class);
        query.setHint("eclipselink.query-results-cache.validation", true);
        query.setParameter("id", BRANCHA_ID);
        BranchADiagnostic branchADiagnosticQueryResult = (BranchADiagnostic)query.getSingleResult();
        assertEquals(1, branchADiagnosticQueryResult.getBranchBs().size());

        assertEquals(2, logWrapper.getMessageCount());

        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        if (em.isOpen()) {
            em.close();
        }
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
