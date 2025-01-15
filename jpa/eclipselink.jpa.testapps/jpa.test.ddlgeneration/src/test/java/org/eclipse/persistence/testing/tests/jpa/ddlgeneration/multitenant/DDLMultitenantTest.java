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
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Underboss;
import org.eclipse.persistence.testing.tests.jpa.ddlgeneration.DDLGenerationTestBase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class DDLMultitenantTest extends DDLGenerationTestBase {

    public static int family707;
    public static int family007;
    public static List<Integer> family707Mafiosos = new ArrayList<>();
    public static List<Integer> family707Contracts = new ArrayList<>();
    public static List<Integer> family007Mafiosos = new ArrayList<>();
    public static List<Integer> family007Contracts = new ArrayList<>();
    
    public DDLMultitenantTest() {
    }

    public DDLMultitenantTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLMultitenantTest");
        suite.addTest(new DDLMultitenantTest("testSetup"));
        suite.addTest(new DDLMultitenantTest("testCreateMafiaFamily707"));
        suite.addTest(new DDLMultitenantTest("testCreateMafiaFamily007"));
        suite.addTest(new DDLMultitenantTest("testValidateMafiaFamily707"));
        suite.addTest(new DDLMultitenantTest("testValidateMafiaFamily007"));
        return suite;
    }

    public void testCreateMafiaFamily707() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            // Set the tenant properties (within Glassfish, we can't do this till the EM becomes transactional)
            em.setProperty("tenant.id", "707");
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");

            MafiaFamily family = new MafiaFamily();
            family.setName("Gonzo");
            family.setRevenue(10000000.00);
            family.addTag("firstTag");
            family.addTag("secondTag");
            family.addTag("thirdTag");

            Boss boss = new Boss();
            boss.setFirstName("707");
            boss.setLastName("Boss");
            boss.setGender(Mafioso.Gender.Male);

            Underboss underboss = new Underboss();
            underboss.setFirstName("Under");
            underboss.setLastName("Boss");
            underboss.setGender(Mafioso.Gender.Male);

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Giggaloo");
            capo1.setGender(Mafioso.Gender.Female);

            Capo capo2 = new Capo();
            capo2.setFirstName("Capo");
            capo2.setLastName("CrazyGlue");
            capo2.setGender(Mafioso.Gender.Male);

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("Soldier");
            soldier1.setLastName("One");
            soldier1.setGender(Mafioso.Gender.Female);

            Soldier soldier2 = new Soldier();
            soldier2.setFirstName("Soldier");
            soldier2.setLastName("Two");
            soldier2.setGender(Mafioso.Gender.Male);

            Soldier soldier3 = new Soldier();
            soldier3.setFirstName("Soldier");
            soldier3.setLastName("Three");
            soldier3.setGender(Mafioso.Gender.Male);

            Soldier soldier4 = new Soldier();
            soldier4.setFirstName("Soldier");
            soldier4.setLastName("Four");
            soldier4.setGender(Mafioso.Gender.Male);

            Soldier soldier5 = new Soldier();
            soldier5.setFirstName("Soldier");
            soldier5.setLastName("Four");
            soldier5.setGender(Mafioso.Gender.Female);

            Contract contract1 = new Contract();
            contract1.setDescription("Whack 007 family boss");

            Contract contract2 = new Contract();
            contract2.setDescription("Acquire fully-automatic guns");

            Contract contract3 = new Contract();
            contract3.setDescription("Steal some money");

            // Populate the relationships.
            contract1.addSoldier(soldier1);
            contract1.addSoldier(soldier5);

            contract2.addSoldier(soldier1);
            contract2.addSoldier(soldier3);
            contract2.addSoldier(soldier5);

            contract3.addSoldier(soldier2);
            contract3.addSoldier(soldier3);
            contract3.addSoldier(soldier4);
            contract3.addSoldier(soldier5);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);
            capo2.setUnderboss(underboss);

            capo1.addSoldier(soldier1);
            capo1.addSoldier(soldier2);

            capo2.addSoldier(soldier3);
            capo2.addSoldier(soldier4);
            capo2.addSoldier(soldier5);

            underboss.addCapo(capo1);
            underboss.addCapo(capo2);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);
            family.addMafioso(capo2);

            family.addMafioso(soldier1);
            family.addMafioso(soldier2);
            family.addMafioso(soldier3);
            family.addMafioso(soldier4);
            family.addMafioso(soldier5);

            // Will cascade through the whole family.
            em.persist(family);

            // Store the ids to verify
            family707 = family.getId();
            family707Mafiosos = new ArrayList<>();
            family707Mafiosos.add(boss.getId());
            family707Mafiosos.add(underboss.getId());
            family707Mafiosos.add(capo1.getId());
            family707Mafiosos.add(capo2.getId());
            family707Mafiosos.add(soldier1.getId());
            family707Mafiosos.add(soldier2.getId());
            family707Mafiosos.add(soldier3.getId());
            family707Mafiosos.add(soldier4.getId());
            family707Mafiosos.add(soldier5.getId());

            family707Contracts = new ArrayList<>();
            family707Contracts.add(contract1.getId());
            family707Contracts.add(contract2.getId());
            family707Contracts.add(contract3.getId());
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

    public void testCreateMafiaFamily007() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            // Set the tenant properties (within Glassfish, we can't do this till the EM becomes transactional)
            em.setProperty("tenant.id", "007");
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

            MafiaFamily family = new MafiaFamily();
            family.setName("Bond");
            family.setRevenue(987654321.03);
            family.addTag("tag1");
            family.addTag("tag2");
            family.addTag("tag3");
            family.addTag("tag4");
            family.addTag("tag5");

            Boss boss = new Boss();
            boss.setFirstName("007");
            boss.setLastName("Boss");
            boss.setGender(Mafioso.Gender.Female);

            Underboss underboss = new Underboss();
            underboss.setFirstName("Second");
            underboss.setLastName("InCommand");
            underboss.setGender(Mafioso.Gender.Female);

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Lubey");
            capo1.setGender(Mafioso.Gender.Male);

            Capo capo2 = new Capo();
            capo2.setFirstName("Capo");
            capo2.setLastName("Greasy");
            capo2.setGender(Mafioso.Gender.Female);

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("First");
            soldier1.setLastName("Grunt");
            soldier1.setGender(Mafioso.Gender.Male);

            Soldier soldier2 = new Soldier();
            soldier2.setFirstName("Second");
            soldier2.setLastName("Grunt");
            soldier2.setGender(Mafioso.Gender.Female);

            Soldier soldier3 = new Soldier();
            soldier3.setFirstName("Third");
            soldier3.setLastName("Grunt");
            soldier3.setGender(Mafioso.Gender.Female);

            Soldier soldier4 = new Soldier();
            soldier4.setFirstName("Fourth");
            soldier4.setLastName("Grunt");
            soldier4.setGender(Mafioso.Gender.Female);

            Soldier soldier5 = new Soldier();
            soldier5.setFirstName("Fifth");
            soldier5.setLastName("Grunt");
            soldier5.setGender(Mafioso.Gender.Male);

            Soldier soldier6 = new Soldier();
            soldier6.setFirstName("Sixth");
            soldier6.setLastName("Grunt");
            soldier6.setGender(Mafioso.Gender.Male);

            Soldier soldier7 = new Soldier();
            soldier7.setFirstName("Seventh");
            soldier7.setLastName("Grunt");
            soldier7.setGender(Mafioso.Gender.Male);

            Contract contract1 = new Contract();
            contract1.setDescription("Whack 707 family boss");

            Contract contract2 = new Contract();
            contract2.setDescription("Acquire semi-automatic guns");

            Contract contract3 = new Contract();
            contract3.setDescription("Set up new financing deals");

            // Populate the relationships.
            contract1.addSoldier(soldier1);
            contract1.addSoldier(soldier5);

            contract2.addSoldier(soldier1);
            contract2.addSoldier(soldier3);
            contract2.addSoldier(soldier7);

            contract3.addSoldier(soldier2);
            contract3.addSoldier(soldier3);
            contract3.addSoldier(soldier4);
            contract3.addSoldier(soldier5);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);
            capo2.setUnderboss(underboss);

            capo1.addSoldier(soldier1);
            capo1.addSoldier(soldier2);

            capo2.addSoldier(soldier3);
            capo2.addSoldier(soldier4);
            capo2.addSoldier(soldier5);
            capo2.addSoldier(soldier6);
            capo2.addSoldier(soldier7);

            underboss.addCapo(capo1);
            underboss.addCapo(capo2);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);
            family.addMafioso(capo2);

            family.addMafioso(soldier1);
            family.addMafioso(soldier2);
            family.addMafioso(soldier3);
            family.addMafioso(soldier4);
            family.addMafioso(soldier5);
            family.addMafioso(soldier6);
            family.addMafioso(soldier7);

            // Will cascade through the whole family.
            em.persist(family);
            family007 = family.getId();
            family007Mafiosos = new ArrayList<>();
            family007Mafiosos.add(boss.getId());
            family007Mafiosos.add(underboss.getId());
            family007Mafiosos.add(capo1.getId());
            family007Mafiosos.add(capo2.getId());
            family007Mafiosos.add(soldier1.getId());
            family007Mafiosos.add(soldier2.getId());
            family007Mafiosos.add(soldier3.getId());
            family007Mafiosos.add(soldier4.getId());
            family007Mafiosos.add(soldier5.getId());
            family007Mafiosos.add(soldier6.getId());
            family007Mafiosos.add(soldier7.getId());

            family007Contracts = new ArrayList<>();
            family007Contracts.add(contract1.getId());
            family007Contracts.add(contract2.getId());
            family007Contracts.add(contract3.getId());
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

    public void testValidateMafiaFamily707() {
        EntityManager em = createEntityManager();

        try {
            validateMafiaFamily707(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testValidateMafiaFamily007() {
        EntityManager em = createEntityManager();

        try {
            validateMafiaFamily007(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    protected void validateMafiaFamily707(EntityManager em) {
        clearCache();
        em.clear();

        beginTransaction(em);
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");

        MafiaFamily family = em.find(MafiaFamily.class, family707);
        assertNotNull("The Mafia Family with id: " + family707 + ", was not found", family);
        assertEquals("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [3]", 3, family.getTags().size());
        assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
        assertFalse("No mafiosos part of 707 family", family.getMafiosos().isEmpty());

        // See if we can find any members of the other family.
        for (Integer id : family007Mafiosos) {
            assertNull("Found family 007 mafioso.", em.find(Mafioso.class, id));
        }

        // Query directly for the boss from the other family.
        Boss otherBoss = em.find(Boss.class, family007Mafiosos.get(0));
        assertNull("Found family 007 boss.", otherBoss);

        // See if we can find any contracts of the other family.
        for (Integer id : family007Contracts) {
            assertNull("Found family 007 contract.", em.find(Contract.class, id));
        }

        // Update all our contract descriptions to be 'voided'
        em.createNamedQuery("UpdateAllContractDescriptions").executeUpdate();
        commitTransaction(em);

        // Read and validate the contracts
        beginTransaction(em);
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");

        List<Contract> contracts = em.createNamedQuery("FindAllContracts", Contract.class).getResultList();
        assertEquals("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", 3, contracts.size());

        for (Contract contract : contracts) {
            assertEquals("Contract description was not voided.", "voided", contract.getDescription());
        }

        // See how many soldiers are returned from a jpql query
        List<?> soldiers = em.createQuery("SELECT s from Soldier s").getResultList();
        assertEquals("Incorrect number of soldiers were returned [" + soldiers.size() + "], expected [5]", 5, soldiers.size());

        if(getPersistenceUnitServerSession().getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test DDLGenerationTest partially skipped for this platform, "
                    +"which uses UpdateAll internally to check tenant-id when updating an entity using JOINED inheritance strategy. "
                    +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            commitTransaction(em);
            return;
        }

        // We know what the boss's id is for the 007 family to try to update him from the 707 pu.
        // The 007 family is validated after this test.
        Query query = em.createNamedQuery("UpdateBossName");
        query.setParameter("name", "Compromised");
        query.setParameter("id", family007Mafiosos.get(0));
        query.executeUpdate();
        commitTransaction(em);
    }

    protected void validateMafiaFamily007(EntityManager em) {
        clearCache();
        em.clear();

        beginTransaction(em);
        em.setProperty("tenant.id", "007");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

        MafiaFamily family =  em.find(MafiaFamily.class, family007);
        assertNotNull("The Mafia Family with id: " + family007 + ", was not found", family);
        assertEquals("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [5]", 5, family.getTags().size());
        assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
        assertFalse("No mafiosos part of 007 family", family.getMafiosos().isEmpty());

        // See if we can find any members of the other family.
        for (Integer id : family707Mafiosos) {
            assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
        }

        // Query directly for the boss from the other family.
        Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
        assertNull("Found family 707 boss.", otherBoss);

        // See if we can find any contracts of the other family.
        for (Integer id : family707Contracts) {
            assertNull("Found family 707 contract.", em.find(Contract.class, id));
        }

        // Read and validate our contracts
        List<Contract> contracts = em.createNamedQuery("FindAllContracts", Contract.class).getResultList();
        assertEquals("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", 3, contracts.size());

        for (Contract contract : contracts) {
            Assert.assertNotEquals("Contract description was voided.", "voided", contract.getDescription());
        }

        // Try a select named query
        List<?> families = em.createNamedQuery("findJPQLMafiaFamilies").getResultList();
        assertEquals("Incorrect number of families were returned [" + families.size() + "], expected [1]", 1, families.size());

        // Find our boss and make sure his name has not been compromised from the 707 family.
        Boss boss = em.find(Boss.class, family007Mafiosos.get(0));
        Assert.assertNotEquals("The Boss name has been compromised", "Compromised", boss.getFirstName());
        commitTransaction(em);
    }
}
