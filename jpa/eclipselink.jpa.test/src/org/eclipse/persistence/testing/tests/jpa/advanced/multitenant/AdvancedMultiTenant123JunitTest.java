/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Reward;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubCapo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Underboss;

public class AdvancedMultiTenant123JunitTest extends AdvancedMultiTenantJunitTest {

    public AdvancedMultiTenant123JunitTest() {
        super();
    }

    public String getMULTI_TENANT_PU_123() { return "MulitPU-2"; }

    public AdvancedMultiTenant123JunitTest(String name) {
        super(name);
        setPuName(getMULTI_TENANT_PU_123());
    }

    public void setUp() {}

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenant123JunitTest");
        if (! JUnitTestCase.isJPA10()) {
            if (System.getProperty("run.metadata.cache.test.suite").compareTo("true") == 0) {
                suite.addTest(new AdvancedMultiTenant123JunitTest("testWriteProjectCache"));
            }
            suite.addTest(new AdvancedMultiTenant123JunitTest("testSetup"));
            suite.addTest(new AdvancedMultiTenant123JunitTest("testCreateMafiaFamily123"));
            suite.addTest(new AdvancedMultiTenant123JunitTest("testValidateMafiaFamily123"));
            suite.addTest(new AdvancedMultiTenant123JunitTest("testComplexMultitenantQueries"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(getMULTI_TENANT_PU_123()));
    }

    public void testWriteProjectCache(){
        new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite().testFileBasedProjectCacheLoading(getMULTI_TENANT_PU_123());
    }

    /**
     * Because single PU units are tested at one time on the server, the
     * SE testcase cannot not be used as is since static variables (of other
     * families) will not have been populated. This validation test must be
     * paired down.
     */
    @Override
    public void testValidateMafiaFamily123() {
        EntityManager em = create123EntityManager();

        try {
            clearCache(getMULTI_TENANT_PU_123());
            em.clear();

            MafiaFamily family =  em.find(MafiaFamily.class, family123);
            assertNotNull("The Mafia Family with id: " + family123 + ", was not found", family);
            assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [1]", family.getTags().size() == 1);
            assertFalse("No mafiosos part of 123 family", family.getMafiosos().isEmpty());

            // Try a native sql query. Should get an exception since the
            // eclipselink.jdbc.allow-native-sql-queries property is set to
            // false for this PU.
            boolean exceptionCaught = false;
            List mafiaFamilies = null;
            try {
                mafiaFamilies = em.createNativeQuery("select * from JPA_MAFIA_FAMILY").getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a native query.", exceptionCaught);

            exceptionCaught = false;
            try {
                mafiaFamilies = em.createNamedQuery("findSQLMafiaFamilies").getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a named native query.", exceptionCaught);

            // Try a select named query
            List families = em.createNamedQuery("findAllMafiaFamilies").getResultList();
            assertTrue("Incorrect number of families were returned [" + families.size() + "], expected [1]",  families.size() == 1);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testComplexMultitenantQueries() {
        EntityManager em = createEntityManager(getMULTI_TENANT_PU_123());

        try {
            clearCache(getMULTI_TENANT_PU_123());
            em.clear();

            // Try passing in a sub entity as a parameter.
            try {
                beginTransaction(em);
            try {
                Query q = em.createQuery("SELECT s FROM Soldier s WHERE s.capo=?1");
                SubCapo subCapo = new SubCapo();
                subCapo.setId(capo123Id);
                q.setParameter(1, subCapo);
                List<Soldier> soldiers = q.getResultList();
                assertTrue("Incorrect number of soldiers returned [" + soldiers.size() +"], expected [1]", soldiers.size() == 1);
                assertTrue("Mafioso returned was not a soldier", soldiers.get(0).isSoldier());
                assertTrue("Soldier returned was not the expected soldier", soldiers.get(0).getId() == soldier123Id);
            } catch (Exception e) {
                fail("Exception encountered on named parameter query (with tenant discriminator columns) : " + e);
            }

            // Try a join fetch
            try {
                TypedQuery<MafiaFamily> q = em.createQuery("SELECT m FROM MafiaFamily m ORDER BY m.id DESC", MafiaFamily.class);
                q.setHint(QueryHints.FETCH, "m.mafiosos");
                q.getResultList();
            } catch (Exception e) {
                fail("Exception encountered on join fetch query (with tenant discriminator columns): " + e);
            }

            // Try a nested join fetch
            try {
                TypedQuery<MafiaFamily> q = em.createQuery("SELECT f FROM MafiaFamily f ORDER BY f.id ASC", MafiaFamily.class);
                q.setHint(QueryHints.FETCH, "f.mafiosos.rewards");
                q.getResultList();
            } catch (Exception e) {
                fail("Exception encountered on nested join fetch query (with tenant discriminator columns): " + e);
            }

            // Try a batch fetch
            try {
                TypedQuery<MafiaFamily> query = em.createQuery("SELECT f FROM MafiaFamily f", MafiaFamily.class);
                query.setHint(QueryHints.BATCH, "f.mafiosos");
                List<MafiaFamily> families = query.getResultList();

                // Should only be one family
                assertTrue("Incorrect number of families returned [" + families.size() +"], expected [1]", families.size() == 1);

                int size = families.get(0).getMafiosos().size();
                assertTrue("Incorrect number of mafiosos returned [" + size + "], expected [6]", size == 6);

            } catch (Exception e) {
                fail("Exception encountered on batch fetch query (with tenant discriminator columns): " + e);
            }

            // Try a multiple select
            try {
                Query query = em.createQuery("SELECT m.address, m.family FROM Mafioso m WHERE m.address.city = 'Ottawa' AND m.family.name LIKE 'Galore'", MafiaFamily.class);
                List results = query.getResultList();
                int size = results.size();
                assertTrue("Incorrect number of results returned [" + size + "], expected [6]", size == 6);
            } catch (Exception e) {
                fail("Exception encountered on mulitple select statement (with tenant discriminator columns): " + e);
            }

            commitTransaction(em);

            } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
            } finally {
                closeEntityManager(em);
            }

            // Try a delete all on single table (Contracts)
            try {
                beginTransaction(em);
                int contracts = em.createNamedQuery("FindAllContracts").getResultList().size();
                int deletes = em.createNamedQuery("DeleteAllContracts").executeUpdate();
                assertTrue("Incorrect number of contracts deleted [" + deletes + "], expected [" + contracts + "]", deletes == 2);
                commitTransaction(em);
            } catch (Exception e) {
                fail("Exception encountered on delete all query with single table (with tenant discriminator columns): " + e);
            }

            // Try a delete all on multiple table (MafiaFamily)
            try {
                beginTransaction(em);
                List<MafiaFamily> allFamilies = em.createNamedQuery("findAllMafiaFamilies").getResultList();
                int families = allFamilies.size();
                assertTrue("More than one family was found ["+ families +"]", families == 1);
                int deletes = em.createNamedQuery("DeleteAllMafiaFamilies").executeUpdate();
                assertTrue("Incorrect number of families deleted [" + deletes + "], expected [" + families + "]", deletes == 1);
                commitTransaction(em);
            } catch (Exception e) {
                fail("Exception encountered on delete all query with multiple table (with tenant discriminator columns): " + e);
            }

            // Some verification of what was deleted.
            /* the following part is commented out on server since server doesn't support nested Entity Managers
            EntityManager em007 = createEntityManager(MULTI_TENANT_PU);
            try {
                beginTransaction(em);
                List<MafiaFamily> families = em.createNativeQuery("select * from JPA_MAFIA_FAMILY", MafiaFamily.class).getResultList();
                assertTrue("Incorrect number of families found through SQL [" + families.size() + "], expected [2]", families.size() == 2);
                commitTransaction(em);

                beginTransaction(em007);
                em007.setProperty("tenant.id", "007");
                em007.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");
                MafiaFamily family = em007.find(MafiaFamily.class, family007);
                assertFalse("Family 007 tags were nuked in delete all query above!", family.getTags().isEmpty());
                assertFalse("Family 007 revenue was nuked in delete all query above!", family.getRevenue() == null);
                commitTransaction(em007);
            } catch (Exception e) {
                fail("Exception caught: " + e);
            } finally {
                if (isTransactionActive(em007)) {
                    rollbackTransaction(em007);
                }
                closeEntityManager(em007);
            }*/
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

}
