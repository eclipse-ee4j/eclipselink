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
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubCapo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Underboss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedMultiTenant123JunitTest extends AdvancedMultiTenantJunitBase {

    public AdvancedMultiTenant123JunitTest() {
        super();
    }

    public AdvancedMultiTenant123JunitTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "multi-tenant-123";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenant123JunitTest");
        if (Boolean.getBoolean("run.metadata.cache.test.suite")) {
            suite.addTest(new AdvancedMultiTenant123JunitTest("testWriteProjectCache"));
        }
        suite.addTest(new AdvancedMultiTenant123JunitTest("testSetup"));
        suite.addTest(new AdvancedMultiTenant123JunitTest("testCreateEMF123WithoutAllTenantPropertiesSet"));
        suite.addTest(new AdvancedMultiTenant123JunitTest("testCreateMafiaFamily123"));
        suite.addTest(new AdvancedMultiTenant123JunitTest("testValidateMafiaFamily123"));
        suite.addTest(new AdvancedMultiTenant123JunitTest("testComplexMultitenantQueries"));
        return suite;
    }

    public EntityManager create123EntityManager(){
        return createEntityManager(getPersistenceUnitName());
    }

    public EntityManager createTenant123EntityManager(){
        EntityManager em = null;
        //properties passed in createEntityManager() won't work on server since server side entity manager is injected, so we have "eclipselink.tenant-id" in server persistence.xml
        if (! isOnServer()) {
            Map<String, String> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
            properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "123");
            em = createEntityManager(getPersistenceUnitName(), properties);
        }else{
            em = create123EntityManager();
        }
        return em;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    @Override
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(createTenant123EntityManager().unwrap(ServerSession.class));
    }

    public void testCreateMafiaFamily123() {
        EntityManager em = createTenant123EntityManager();
        try {
            beginTransaction(em);

            MafiaFamily family = new MafiaFamily();
            family.setName("Galore");
            family.setRevenue(4321.03);
            family.addTag("newtag1");

            Boss boss = new Boss();
            boss.setFirstName("Kitty");
            boss.setLastName("Galore");
            boss.setGender(Mafioso.Gender.Female);
            boss.addReward("Reward 1");
            boss.addReward("Reward 2");
            boss.setAddress(new Address("1st Street", "Ottawa", "Ontario", "Canada", "B1T 2Y1"));

            Underboss underboss = new Underboss();
            underboss.setFirstName("Number2");
            underboss.setLastName("Galore");
            underboss.setGender(Mafioso.Gender.Male);
            underboss.addReward("Reward 3");
            underboss.addReward("Reward 4");
            underboss.setAddress(new Address("2nd Street", "Ottawa", "Ontario", "Canada", "B1T 2Y2"));

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Galore");
            capo1.setGender(Mafioso.Gender.Male);
            capo1.setAddress(new Address("3rd Street", "Ottawa", "Ontario", "Canada", "B1T 2Y3"));

            Capo capo2 = new Capo();
            capo2.setFirstName("Drill");
            capo2.setLastName("Bit");
            capo2.setGender(Mafioso.Gender.Male);
            capo2.setAddress(new Address("4th Street", "Ottawa", "Ontario", "Canada", "B1T 2Y4"));

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("Grunt");
            soldier1.setLastName("Galore");
            soldier1.setGender(Mafioso.Gender.Male);
            soldier1.addReward("Reward 5");
            soldier1.addReward("Reward 6");
            soldier1.addReward("Reward 7");
            soldier1.addReward("Reward 8");
            soldier1.setAddress(new Address("5th Street", "Ottawa", "Ontario", "Canada", "B1T 2Y5"));

            Soldier soldier2 = new Soldier();
            soldier2.setFirstName("Hammer");
            soldier2.setLastName("Head");
            soldier2.setGender(Mafioso.Gender.Male);
            soldier2.addReward("Reward 9");
            soldier2.addReward("Reward 10");
            soldier2.setAddress(new Address("6th Street", "Ottawa", "Ontario", "Canada", "B1T 2Y6"));

            Contract contract1 = new Contract();
            contract1.setDescription("Whack all other family bosses");

            Contract contract2 = new Contract();
            contract2.setDescription("Pillage, pillage, pillage!");

            // Populate the relationships.
            contract1.addSoldier(soldier1);
            contract2.addSoldier(soldier1);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);
            capo1.addSoldier(soldier1);

            capo2.addSoldier(soldier2);

            underboss.addCapo(capo1);
            underboss.addCapo(capo2);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);
            family.addMafioso(capo2);

            family.addMafioso(soldier1);
            family.addMafioso(soldier2);

            // Will cascade through the whole family.
            em.persist(family);
            capo123Id = capo1.getId();
            soldier123Id = soldier1.getId();
            family123 = family.getId();
            family123Mafiosos.add(boss.getId());
            family123Mafiosos.add(underboss.getId());
            family123Mafiosos.add(capo1.getId());
            family123Mafiosos.add(soldier1.getId());
            family123Contracts.add(contract1.getId());
            family123Contracts.add(contract2.getId());
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

    /**
     * Because single PU units are tested at one time on the server, the
     * SE testcase cannot not be used as is since static variables (of other
     * families) will not have been populated. This validation test must be
     * paired down.
     */
    public void testValidateMafiaFamily123() {
        EntityManager em = create123EntityManager();

        try {
            clearCache(getPersistenceUnitName());
            em.clear();

            MafiaFamily family =  em.find(MafiaFamily.class, family123);
            assertNotNull("The Mafia Family with id: " + family123 + ", was not found", family);
            assertEquals("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [1]", 1, family.getTags().size());
            assertFalse("No mafiosos part of 123 family", family.getMafiosos().isEmpty());

            if (!isOnServer()) {
                assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
                assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));

                // See if we can find any members of the other family.
                for (Integer id : family707Mafiosos) {
                    assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
                }
            }

            // Try a native sql query. Should get an exception since the
            // eclipselink.jdbc.allow-native-sql-queries property is set to
            // false for this PU.
            boolean exceptionCaught = false;
            List<MafiaFamily> mafiaFamilies = null;
            try {
                mafiaFamilies = em.createNativeQuery("select * from JPA_MAFIA_FAMILY", MafiaFamily.class).getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a native query.", exceptionCaught);

            exceptionCaught = false;
            try {
                mafiaFamilies = em.createNamedQuery("findSQLMafiaFamilies", MafiaFamily.class).getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a named native query.", exceptionCaught);

//            if (!isOnServer()) {
//                // Query directly for the boss from the other family.
//                Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
//                assertNull("Found family 707 boss.", otherBoss);
//
//                // See if we can find any contracts of the other family.
//                for (Integer id : family707Contracts) {
//                    assertNull("Found family 707 contract. ", em.find(Contract.class, id));
//                }
//            }

            // Try a select named query
            List<MafiaFamily> families = em.createNamedQuery("findAllMafiaFamilies", MafiaFamily.class).getResultList();
            assertEquals("Incorrect number of families were returned [" + families.size() + "], expected [1]", 1, families.size());
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
        EntityManager em = createEntityManager(getPersistenceUnitName());

        try {
            clearCache(getPersistenceUnitName());
            em.clear();

            // Try passing in a sub entity as a parameter.
            try {
                beginTransaction(em);
            try {
                TypedQuery<Soldier> q = em.createQuery("SELECT s FROM Soldier s WHERE s.capo=?1", Soldier.class);
                SubCapo subCapo = new SubCapo();
                subCapo.setId(capo123Id);
                q.setParameter(1, subCapo);
                List<Soldier> soldiers = q.getResultList();
                assertEquals("Incorrect number of soldiers returned [" + soldiers.size() + "], expected [1]", 1, soldiers.size());
                assertTrue("Mafioso returned was not a soldier", soldiers.get(0).isSoldier());
                assertEquals("Soldier returned was not the expected soldier", soldiers.get(0).getId(), soldier123Id);
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
                assertEquals("Incorrect number of families returned [" + families.size() + "], expected [1]", 1, families.size());

                int size = families.get(0).getMafiosos().size();
                assertEquals("Incorrect number of mafiosos returned [" + size + "], expected [6]", 6, size);

            } catch (Exception e) {
                fail("Exception encountered on batch fetch query (with tenant discriminator columns): " + e);
            }

            // Try a multiple select
            try {
                TypedQuery<MafiaFamily> query = em.createQuery("SELECT m.address, m.family FROM Mafioso m WHERE m.address.city = 'Ottawa' AND m.family.name LIKE 'Galore'", MafiaFamily.class);
                List<MafiaFamily> results = query.getResultList();
                int size = results.size();
                assertEquals("Incorrect number of results returned [" + size + "], expected [6]", 6, size);
            } catch (Exception e) {
                fail("Exception encountered on mulitple select statement (with tenant discriminator columns): " + e);
            }

            commitTransaction(em);

            } catch (RuntimeException e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }

            // Try a delete all on single table (Contracts)
            try {
                beginTransaction(em);
                int contracts = em.createNamedQuery("FindAllContracts").getResultList().size();
                int deletes = em.createNamedQuery("DeleteAllContracts").executeUpdate();
                assertEquals("Incorrect number of contracts deleted [" + deletes + "], expected [" + contracts + "]", 2, deletes);
                commitTransaction(em);
            } catch (Exception e) {
                fail("Exception encountered on delete all query with single table (with tenant discriminator columns): " + e);
            }

            if(getServerSession(getPersistenceUnitName()).getPlatform().isSymfoware()) {
                getServerSession(getPersistenceUnitName()).logMessage("Test AdvancedMultiTenant123JunitTest partially skipped for this platform, "
                        +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            } else {
                // Try a delete all on multiple table (MafiaFamily)
                try {
                    beginTransaction(em);
                    List<MafiaFamily> allFamilies = em.createNamedQuery("findAllMafiaFamilies", MafiaFamily.class).getResultList();
                    int families = allFamilies.size();
                    assertEquals("More than one family was found [" + families + "]", 1, families);
                    Query deleteQuery = em.createNamedQuery("DeleteAllMafiaFamilies");
                    deleteQuery.setHint(QueryHints.ALLOW_NATIVE_SQL_QUERY, true);
                    int deletes = deleteQuery.executeUpdate();
                    assertEquals("Incorrect number of families deleted [" + deletes + "], expected [" + families + "]", 1, deletes);
                    commitTransaction(em);
                } catch (Exception e) {
                    fail("Exception encountered on delete all query with multiple table (with tenant discriminator columns): " + e);
                }
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        /*
        if (!isOnServer()) {
            // Some verification of what was deleted.
            // the following part is commented out on server since server doesn't support nested Entity Managers
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
            }
        }
         */
    }

    /**
     * This test is expected to fail.
     */
    public void testCreateEMF123WithoutAllTenantPropertiesSet() {
        if (isOnServer()) {
            // EMF is configured properly for the server case
            return;
        }
        closeEntityManagerFactory(getPersistenceUnitName());
        EntityManager em = null;
        boolean exceptionCaught = false;
        try {
            em = create123EntityManager();
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } finally {
            if (em != null) {
                closeEntityManager(em);
            }

            closeEntityManagerFactory(getPersistenceUnitName());
        }

        assertTrue("No exception received on a non shared emf without all tenant properties provided", exceptionCaught);
    }
}
