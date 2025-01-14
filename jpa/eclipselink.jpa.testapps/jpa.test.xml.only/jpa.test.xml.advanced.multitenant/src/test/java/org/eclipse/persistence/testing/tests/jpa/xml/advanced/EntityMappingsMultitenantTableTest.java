/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Trowel;

public class EntityMappingsMultitenantTableTest extends EntityMappingsMultitenantJunitBase {

    public EntityMappingsMultitenantTableTest() {
        super();
    }

    public EntityMappingsMultitenantTableTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-multi-tenant-table-per-tenant";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Extended Advanced MultitenantTableTestTest Suite");

        suite.addTest(new EntityMappingsMultitenantTableTest("testSetup"));

        suite.addTest(new EntityMappingsMultitenantTableTest("testTablePerTenantA"));
        suite.addTest(new EntityMappingsMultitenantTableTest("testTablePerTenantB"));

        return suite;
    }

    public void testTablePerTenantA() {
        EntityManager em = createEntityManager(getPersistenceUnitName());

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
            supporter1.setName("Supporter1");
            candidateA.addSupporter(supporter1);

            Supporter supporter2 = new Supporter();
            supporter2.setName("Supporter2");
            candidateA.addSupporter(supporter2);

            Party party = new Party();
            party.setName("Conservatives");
            party.addCandidate(candidateA);

            Riding riding = new Riding();
            riding.setName("Ottawa");
            candidateA.setRiding(riding);

            // Persist our objects.
            em.persist(party);
            em.persist(candidateA);
            em.persist(supporter2);
            em.persist(supporter1);
            em.persist(riding);

            Mason mason = new Mason();
            mason.setName("FromTenantA");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), "Concrete float master");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), "Form stake out expert");

            Trowel trowel = new Trowel();
            trowel.setType("Concrete");
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

    public void testTablePerTenantB() {
        EntityManager em = createEntityManager(getPersistenceUnitName());

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
            assertEquals("Candidate B had the incorrect salary.", 100, candidateBRefreshed.getSalary());

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
}

