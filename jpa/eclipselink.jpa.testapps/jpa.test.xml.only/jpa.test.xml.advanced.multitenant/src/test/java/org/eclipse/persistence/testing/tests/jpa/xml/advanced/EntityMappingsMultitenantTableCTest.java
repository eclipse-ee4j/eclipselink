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

package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Trowel;

public class EntityMappingsMultitenantTableCTest extends EntityMappingsMultitenantJunitBase {

    public EntityMappingsMultitenantTableCTest() {
        super();
    }

    public EntityMappingsMultitenantTableCTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-multi-tenant-table-per-tenant-C";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Extended Advanced MultitenantTableCTest Suite");
        suite.addTest(new EntityMappingsMultitenantTableCTest("testSetup"));
        suite.addTest(new EntityMappingsMultitenantTableCTest("testTablePerTenantC"));
        return suite;
    }

    public void testTablePerTenantC() {
        EntityManager em = createEntityManager(getPersistenceUnitName());

        try {
            beginTransaction(em);

            Candidate candidate = new Candidate();
            candidate.setName("C");

            candidate.addHonor("Raised most money");
            candidate.addHonor("Highest win margin");

            candidate.setSalary(9999999);

            Supporter supporter1 = new Supporter();
            supporter1.setName("Supporter1");
            candidate.addSupporter(supporter1);

            Supporter supporter2 = new Supporter();
            supporter2.setName("Supporter2");
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
}
