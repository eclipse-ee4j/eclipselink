/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import jakarta.persistence.EntityManager;
import junit.framework.*;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SupporterInfo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SupporterInfoSub;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Trowel;

import java.util.List;

public class AdvancedMultiTenantTableJunitTest extends AdvancedMultiTenantJunitBase {

    public AdvancedMultiTenantTableJunitTest() {
        super();
    }

    public AdvancedMultiTenantTableJunitTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "multi-tenant-table-per-tenant";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantTableJunitTest");
        if (Boolean.getBoolean("run.metadata.cache.test.suite")) {
            suite.addTest(new AdvancedMultiTenantTableJunitTest("testWriteProjectCache"));
        }
        suite.addTest(new AdvancedMultiTenantTableJunitTest("testSetup"));
        suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantA"));
        suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantAQueries"));
        suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantB"));
        suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantBQueries"));
        return suite;
    }

    public EntityManager createTableTenantEntityManager(){
        return createEntityManager(getPersistenceUnitName());
    }

    public void testTablePerTenantA() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            // Valid to set the table per tenant qualifier now.
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "A");

            Candidate candidateA = new Candidate();
            candidateA.setName("CA");

            candidateA.addHonor("Raised most money");
            candidateA.addHonor("Highest win margin");

            candidateA.setSalary(9999999);

            Supporter supporter1 = new Supporter();
            supporter1.setName("Supporter1a");
            SupporterInfo supporter1Info = new SupporterInfo();
            supporter1Info.setDescription("Supporter1aDesc");
            SupporterInfoSub supporter1InfoSub = new SupporterInfoSub();
            supporter1InfoSub.setSubDescription("Supporter1aSubDesc");
            supporter1Info.setSubInfo(supporter1InfoSub);
            supporter1.setInfo(supporter1Info);
            candidateA.addSupporter(supporter1);

            Supporter supporter2 = new Supporter();
            supporter2.setName("Supporter2a");
            SupporterInfo supporter2Info = new SupporterInfo();
            supporter2Info.setDescription("Supporter2aDesc");
            SupporterInfoSub supporter2InfoSub = new SupporterInfoSub();
            supporter2InfoSub.setSubDescription("Supporter2aSubDesc");
            supporter2Info.setSubInfo(supporter2InfoSub);
            supporter2.setInfo(supporter2Info);
            candidateA.addSupporter(supporter2);

            Supporter supporter3 = new Supporter();
            supporter3.setName("Supporter3a");
            SupporterInfo supporter3Info = new SupporterInfo();
            supporter3Info.setDescription("Supporter3aDesc");
            SupporterInfoSub supporter3InfoSub = new SupporterInfoSub();
            supporter3InfoSub.setSubDescription("Supporter3aSubDesc");
            supporter3Info.setSubInfo(supporter3InfoSub);
            supporter3.setInfo(supporter3Info);
            candidateA.addSupporter(supporter3);

            Party party = new Party();
            party.setName("Conservatives");
            party.addCandidate(candidateA);

            Riding riding = new Riding();
            riding.setName("Ottawa");
            candidateA.setRiding(riding);

            // Persist our objects.
            em.persist(party);
            em.persist(candidateA);
            em.persist(supporter1);
            em.persist(supporter2);
            em.persist(supporter3);
            em.persist(riding);

            Mason mason = new Mason();
            mason.setName("FromTenantA");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), "Best pointer");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), "Least screw-ups");

            Trowel trowel = new Trowel();
            trowel.setType("Pointing");
            mason.setTrowel(trowel);
            trowel.setMason(mason);

            em.persist(mason);
            em.persist(trowel);

            // Grab any id's for verification.
            candidateAId = candidateA.getId();
            ridingId = riding.getId();
            partyId = party.getId();
            supporter1Id = supporter1.getId();
            supporter2Id = supporter2.getId();
            masonId = mason.getId();

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantAQueries() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "A");

            List resultsFromNamedQuery = em.createNamedQuery("Supporter.findAll").getResultList();
            List resultsFromDynamicQuery = em.createQuery("SELECT s FROM Supporter s ORDER BY s.id DESC").getResultList();

            assertTrue("Incorrect number of supporters returned from named query.", resultsFromNamedQuery.size() == 3);
            assertTrue("Incorrect number of supporters returned from dynamic query.", resultsFromDynamicQuery.size() == 3);

            // Test some more complex joining queries.
            List<Supporter> results = em.createNamedQuery("Supporter.findBySupporterInfo").setParameter("desc", "Supporter1aDesc").getResultList();
            assertFalse("No results returned.", results == null);
            assertTrue("Single result not returned.", results.size() == 1);
            assertTrue("Didn't return supporter1a", results.get(0).getName().equals("Supporter1a"));
            results = em.createNamedQuery("Supporter.findBySupporterInfoSub").setParameter("subDesc", "Supporter3aSubDesc").getResultList();
            assertFalse("No results returned.", results == null);
            assertTrue("Single result not returned.", results.size() == 1);
            assertTrue("Didn't return supporter3a", results.get(0).getName().equals("Supporter3a"));

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantB() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            // Valid to set the table per tenant qualifier now.
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "B");

            // Should not find these ...
            assertNull("CandidateA was found from tenant B.", em.find(Candidate.class, candidateAId));
            assertNull("Supporter1 was found from tenant B.", em.find(Supporter.class, supporter1Id));
            assertNull("Supporter2 was found from tenant B.", em.find(Supporter.class, supporter2Id));
            assertNull("Mason was found from tenant B.", em.find(Mason.class, masonId));

            // Should find these ...
            Riding riding = em.find(Riding.class, ridingId);
            assertNotNull("Riding was not found from tenant B", riding);
            Party party = em.find(Party.class, partyId);
            assertNotNull("Party was not found from tenant B", party);
            // TODO: can't do this (would have to initialize the mapping from party again to the new descriptor.
            // future implementation maybe.
            //assertTrue("Party had candidates", party.getCandidates().isEmpty());

            Candidate candidateB = new Candidate();
            candidateB.setName("CB");
            candidateB.setSalary(100);
            em.persist(candidateB);
            commitTransaction(em);

            em.clear();
            clearCache(getPersistenceUnitName());

            beginTransaction(em);

            // Valid to set the table per tenant qualifier now.
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "B");

            Candidate candidateBRefreshed = em.find(Candidate.class, candidateB.getId());

            assertNull("Candidate B has a Party when he shouldn't have.", candidateBRefreshed.getParty());
            assertNull("Candidate B has a Riding when he shouldn't have.", candidateBRefreshed.getRiding());
            assertTrue("Candidate B had supporters when he shouldn't have.", candidateBRefreshed.getSupporters().isEmpty());
            assertTrue("Candidate B had honors when he shouldn't have.", candidateBRefreshed.getHonors().isEmpty());
            assertTrue("Candidate B had the incorrect salary.", candidateBRefreshed.getSalary() == 100);

            commitTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantBQueries() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "B");

            List resultsFromNamedQuery = em.createNamedQuery("Supporter.findAll").getResultList();
            List resultsFromDynamicQuery = em.createQuery("SELECT s FROM Supporter s ORDER BY s.id DESC").getResultList();

            assertTrue("Incorrect number of supporters returned from named query.", resultsFromNamedQuery.isEmpty());
            assertTrue("Incorrect number of supporters returned from dynamic query.", resultsFromDynamicQuery.isEmpty());

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

}
