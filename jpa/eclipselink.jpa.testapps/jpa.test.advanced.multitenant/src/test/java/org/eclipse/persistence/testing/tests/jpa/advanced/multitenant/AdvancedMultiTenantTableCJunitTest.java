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
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Trowel;

import java.util.List;

public class AdvancedMultiTenantTableCJunitTest extends AdvancedMultiTenantJunitBase {

    public AdvancedMultiTenantTableCJunitTest() {
        super();
    }

    public AdvancedMultiTenantTableCJunitTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "multi-tenant-table-per-tenant-C";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantTableCJunitTest");
        if (Boolean.getBoolean("run.metadata.cache.test.suite")) {
            suite.addTest(new AdvancedMultiTenantTableCJunitTest("testWriteProjectCache"));
        }
        suite.addTest(new AdvancedMultiTenantTableCJunitTest("testSetup"));
        suite.addTest(new AdvancedMultiTenantTableCJunitTest("testTablePerTenantC"));
        suite.addTest(new AdvancedMultiTenantTableCJunitTest("testTablePerTenantCQueries"));
        return suite;
    }

    public EntityManager createTableTenantCEntityManager(){
        return createEntityManager(getPersistenceUnitName());
    }

    public void testTablePerTenantC() {
        // Tenant is set in the persistence.xml file.
        EntityManager em = createTableTenantCEntityManager();

        try {
            beginTransaction(em);

            Candidate candidate = new Candidate();
            candidate.setName("C");

            candidate.addHonor("Raised most money");
            candidate.addHonor("Highest win margin");

            candidate.setSalary(9999999);

            Supporter supporter1 = new Supporter();
            supporter1.setName("Supporter1c");
            candidate.addSupporter(supporter1);

            Supporter supporter2 = new Supporter();
            supporter2.setName("Supporter2c");
            candidate.addSupporter(supporter2);

            Party party = new Party();
            party.setName("Conservatives");
            party.addCandidate(candidate);

            Riding riding = new Riding();
            riding.setName("Ottawa");
            candidate.setRiding(riding);

            // Persist our objects.
            em.persist(party);
            em.persist(candidate);
            em.persist(supporter2);
            em.persist(supporter1);
            em.persist(riding);

            Mason mason = new Mason();
            mason.setName("FromTenantC");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), "Best pointer");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), "Least screw-ups");

            Trowel trowel = new Trowel();
            trowel.setType("Pointing");
            mason.setTrowel(trowel);
            trowel.setMason(mason);

            em.persist(mason);
            em.persist(trowel);

            // Grab any id's for verification.
            candidateAId = candidate.getId();
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

    public void testTablePerTenantCQueries() {
        EntityManager em = createTableTenantCEntityManager();

        try {
            beginTransaction(em);

            List<Supporter> resultsFromNamedQuery = em.createNamedQuery("Supporter.findAll", Supporter.class).getResultList();
            List<Supporter> resultsFromDynamicQuery = em.createQuery("SELECT s FROM Supporter s ORDER BY s.id DESC", Supporter.class).getResultList();

            assertEquals("Incorrect number of supporters returned from named query.", 2, resultsFromNamedQuery.size());
            assertEquals("Incorrect number of supporters returned from dynamic query.", 2, resultsFromDynamicQuery.size());

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
