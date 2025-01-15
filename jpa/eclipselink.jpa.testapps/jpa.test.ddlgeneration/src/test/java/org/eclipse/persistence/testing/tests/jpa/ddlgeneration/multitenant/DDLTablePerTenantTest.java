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

package org.eclipse.persistence.testing.tests.jpa.ddlgeneration.multitenant;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Trowel;
import org.eclipse.persistence.testing.tests.jpa.ddlgeneration.DDLGenerationTestBase;

public class DDLTablePerTenantTest extends DDLGenerationTestBase {

    private static final String DDL_TPT_PU = "ddlTablePerTenantGeneration";

    public DDLTablePerTenantTest() {
    }

    public DDLTablePerTenantTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "ddlTablePerTenantGeneration";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLTablePerTenantTest");
        suite.addTest(new DDLTablePerTenantTest("testSetup"));
        suite.addTest(new DDLTablePerTenantTest("testTablePerTenant"));
        return suite;
    }

    public void testTablePerTenant() {
        // Test the DDL generated tenant.
        EntityManager em = createEntityManager(DDL_TPT_PU);

        try {
            beginTransaction(em);

            Candidate candidate = new Candidate();
            candidate.setName("DDL");

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
            mason.setName("FromTenantDDL");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), "Best pointer");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), "Least screw-ups");

            mason.addHoursWorked(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), 10);
            mason.addHoursWorked(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), 11);

            mason.addUniSelf(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), mason);

            Trowel trowel = new Trowel();
            trowel.setType("Pointing");
            mason.setTrowel(trowel);
            trowel.setMason(mason);

            em.persist(mason);
            em.persist(trowel);

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
