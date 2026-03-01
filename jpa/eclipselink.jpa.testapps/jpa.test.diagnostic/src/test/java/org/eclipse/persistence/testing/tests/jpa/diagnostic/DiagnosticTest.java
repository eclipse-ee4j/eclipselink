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
package org.eclipse.persistence.testing.tests.jpa.diagnostic;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.diagnostic.BranchBDiagnostic;
import org.eclipse.persistence.testing.models.jpa.diagnostic.BranchADiagnostic;
import org.eclipse.persistence.testing.models.jpa.diagnostic.DiagnosticTableCreator;

//This test is about additional diagnostic log messages. This is why org.eclipse.persistence.testing.tests.jpa.diagnostic.LogWrapper is used there.
//It's used as a log handler registered to the session. There is selected log messages counter.
public class DiagnosticTest extends JUnitTestCase {

    public DiagnosticTest() {
        super();
    }

    public DiagnosticTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CascadeDeletesTest");
        suite.addTest(new DiagnosticTest("testSetup"));
        suite.addTest(new DiagnosticTest("testCorruptedCachePersistenceUnitProperty"));
        suite.addTest(new DiagnosticTest("testCorruptedCacheQueryHint"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        new DiagnosticTableCreator().replaceTables(JUnitTestCase.getServerSession("diagnostic-test-pu"));
        clearCache("diagnostic-test-pu");
    }

    public void testCorruptedCachePersistenceUnitProperty() {
        final int BRANCHA_ID = 1;
        final int BRANCHB_ID = 11;

        EntityManager em = createEntityManager("diagnostic-with-property-test-pu");
        Session serverSession  = ((JpaEntityManager) em).getServerSession();
        LogWrapper logWrapper = new LogWrapper("corrupt_object_referenced_through_mapping");
        serverSession.setSessionLog(logWrapper);

        beginTransaction(em);

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
        commitTransaction(em);

        //Simulate business transaction where we do work with Entity removed
        em.getTransaction().begin();
        if (branchADiagnostic.getBranchBs().contains(branchBDiagnostic)) {
            branchADiagnostic.getBranchBs().remove(branchBDiagnostic);
        }
        branchBDiagnostic.setBranchA(null);
        em.remove(branchBDiagnostic);
        commitTransaction(em);
        //After commit is entity branchBDiagnostic removed from database but still exists as object in memory (detached state)

        //Assign detached entity (branchBDiagnostic) to attached (branchADiagnostic)
        //Simulation of code logical error to mix into same object tree attached and detached entities
        //Add already removed (detached) entity back to the object tree
        //Required prerequisite are: caching enabled
        em.getTransaction().begin();
        branchADiagnostic.getBranchBs().add(branchBDiagnostic);
        branchBDiagnostic.setBranchA(branchADiagnostic);
        //Detached entity (branchBDiagnostic) is not persisted again - logical error
        commitTransaction(em);

        //This em.find() will resolve objects from cache
        BranchADiagnostic branchADiagnosticFindResult = em.find(BranchADiagnostic.class, BRANCHA_ID);
        assertEquals(1, branchADiagnosticFindResult.getBranchBs().size());

        //Verifies, that diagnostic message is produced
        assertEquals(1, logWrapper.getMessageCount());

        closeEntityManagerAndTransaction(em);
    }

    public void testCorruptedCacheQueryHint() {
        final int BRANCHA_ID = 2;
        final int BRANCHB_ID = 22;

        EntityManager em = createEntityManager("diagnostic-test-pu");
        Session serverSession  = ((JpaEntityManager) em).getServerSession();
        LogWrapper logWrapper = new LogWrapper("corrupt_object_referenced_through_mapping");
        serverSession.setSessionLog(logWrapper);

        beginTransaction(em);

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
        commitTransaction(em);

        //Simulate business transaction where we do work with Entity removed
        em.getTransaction().begin();
        if (branchADiagnostic.getBranchBs().contains(branchBDiagnostic)) {
            branchADiagnostic.getBranchBs().remove(branchBDiagnostic);
        }
        branchBDiagnostic.setBranchA(null);
        em.remove(branchBDiagnostic);
        commitTransaction(em);
        //After commit is entity branchBDiagnostic removed from database but still exists as object in memory (detached state)

        //Assign detached entity (branchBDiagnostic) to attached (branchADiagnostic)
        //Simulation of code logical error to mix into same object tree attached and detached entities
        //Add already removed (detached) entity back to the object tree
        //Required prerequisite are: caching enabled
        em.getTransaction().begin();
        branchADiagnostic.getBranchBs().add(branchBDiagnostic);
        branchBDiagnostic.setBranchA(branchADiagnostic);
        //Detached entity (branchBDiagnostic) is not persisted again - logical error
        commitTransaction(em);

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

        closeEntityManagerAndTransaction(em);
    }
}
