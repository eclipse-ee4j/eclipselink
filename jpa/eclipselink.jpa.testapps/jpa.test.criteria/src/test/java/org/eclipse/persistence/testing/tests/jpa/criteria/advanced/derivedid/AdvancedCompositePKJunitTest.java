/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.criteria.advanced.derivedid;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MasterCorporal;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MasterCorporalId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Sargeant;

public class AdvancedCompositePKJunitTest extends JUnitTestCase {

    public AdvancedCompositePKJunitTest() {
        super();
    }

    public AdvancedCompositePKJunitTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced-derivedid";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedCompositePKJunitTest");

        suite.addTest(new AdvancedCompositePKJunitTest("testSetup"));

        // MappedById tests (see spec page 30 for more info)
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample1"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CompositePKTableCreator().replaceTables(getPersistenceUnitServerSession());
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testMappedByIdExample1() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Sargeant sargeant = new Sargeant();
        MasterCorporal masterCorporal = new MasterCorporal();
        MasterCorporalId masterCorporalId = new MasterCorporalId();

        try {
            sargeant.setName("Sarge");
            em.persist(sargeant);

            masterCorporalId.setName("Corpie");
            masterCorporal.setId(masterCorporalId);
            masterCorporal.setSargeant(sargeant);
            em.persist(masterCorporal);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        //clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            //SELECT d FROM MasterCorporal d WHERE d.id.name = 'Joe' AND d.sargeant.sargeantId = sargeant.getSargeantId()
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<MasterCorporal> cq = qb.createQuery(MasterCorporal.class);
            Root<MasterCorporal> from = cq.from(MasterCorporal.class);
            cq.where(qb.and(qb.equal(from.get("id").get("name"), "Corpie"), qb.equal(from.get("sargeant").get("sargeantId"), sargeant.getSargeantId()) ) );
            Query query1 = em.createQuery(cq);
            MasterCorporal results1 = (MasterCorporal)query1.getSingleResult();
            //SELECT d FROM MasterCorporal d WHERE d.id.name = 'Joe' AND d.id.sargeantPK = sargeant.getSargeantId()
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(MasterCorporal.class);
            from = cq.from(MasterCorporal.class);
            cq.where(qb.and(qb.equal(from.get("id").get("name"), "Corpie"), qb.equal(from.get("id").get("sargeantPK"), sargeant.getSargeantId()) ) );

            Query query2 = em.createQuery(cq);
            MasterCorporal results2 = (MasterCorporal)query2.getSingleResult();

            MasterCorporal refreshedMasterCorporal = em.find(MasterCorporal.class, masterCorporalId);
            assertTrue("The master corporal read back did not match the original", getPersistenceUnitServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));
            assertTrue("The master corporal read using criteria expression1 is not the same instance as returned by the finder", refreshedMasterCorporal==results1);
            assertTrue("The master corporal read using criteria expression2 is not the same instance as returned by the finder", refreshedMasterCorporal==results2);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

}
