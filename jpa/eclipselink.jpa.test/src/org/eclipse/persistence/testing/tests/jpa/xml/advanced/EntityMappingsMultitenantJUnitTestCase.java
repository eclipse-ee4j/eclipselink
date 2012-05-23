/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/23/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     04/21/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 5)
 *     06/30/2011-2.3.1 Guy Pelletier 
 *       - 341940: Add disable/enable allowing native queries
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.AdvancedMultiTenantTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Trowel;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Underboss;

public class EntityMappingsMultitenantJUnitTestCase extends JUnitTestCase { 
    public static final String MULTI_TENANT_PU = "extended-multi-tenant-shared-emf";
    public static final String MULTI_TENANT_PU_123 = "extended-multi-tenant-123";
    public static final String MULTI_TENANT_TABLE_PER_TENANT_PU = "extended-multi-tenant-table-per-tenant";
    public static final String MULTI_TENANT_TABLE_PER_TENANT_C_PU = "extended-multi-tenant-table-per-tenant-C";
    private static final String MULTI_TENANT_TABLE_PER_TENANT = null;
    
    public static long candidateAId;
    public static long supporter1Id;
    public static long supporter2Id;
    public static int ridingId;
    public static int partyId;
    public static int masonId;
    
    public static int family707;
    public static int family007;
    public static int family123;
    public static List<Integer> family707Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family707Contracts = new ArrayList<Integer>();
    public static List<Integer> family007Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family007Contracts = new ArrayList<Integer>();
    public static List<Integer> family123Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family123Contracts = new ArrayList<Integer>();
    
    public EntityMappingsMultitenantJUnitTestCase() {
        super();
    }
    
    public EntityMappingsMultitenantJUnitTestCase(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Extended Advanced Multitenant Test Suite");
        
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testSetup"));
        
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testCreateMafiaFamily707"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testCreateMafiaFamily007"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testCreateMafiaFamily123"));
        
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testValidateMafiaFamily707"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testValidateMafiaFamily007"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testValidateMafiaFamily707and007WithSameEM"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testValidateMafiaFamily123"));
        
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testTablePerTenantA"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testTablePerTenantB"));
        suite.addTest(new EntityMappingsMultitenantJUnitTestCase("testTablePerTenantC"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(((org.eclipse.persistence.jpa.JpaEntityManager) createEntityManager(MULTI_TENANT_PU)).getServerSession());    
    }
    
    public void testCreateMafiaFamily707() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU);

        try {
            beginTransaction(em);
            //on server side, you have to set the em properties after transaction begins
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
            family707Mafiosos.add(boss.getId());
            family707Mafiosos.add(underboss.getId());
            family707Mafiosos.add(capo1.getId());
            family707Mafiosos.add(capo2.getId());
            family707Mafiosos.add(soldier1.getId());
            family707Mafiosos.add(soldier2.getId());
            family707Mafiosos.add(soldier3.getId());
            family707Mafiosos.add(soldier4.getId());
            family707Mafiosos.add(soldier5.getId());
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
        EntityManager em = createEntityManager(MULTI_TENANT_PU);
        try {
            beginTransaction(em);
            //on server side, you have to set the em properties after transaction begins
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

    public void testCreateMafiaFamily123() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU_123);

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
            
            Underboss underboss = new Underboss();
            underboss.setFirstName("Number2");
            underboss.setLastName("Galore");
            underboss.setGender(Mafioso.Gender.Male);
            
            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Galore");
            capo1.setGender(Mafioso.Gender.Male);
            
            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("Grunt");
            soldier1.setLastName("Galore");
            soldier1.setGender(Mafioso.Gender.Male);
            
            Contract contract1 = new Contract();
            contract1.setDescription("Whack all other family boss");
            
            Contract contract2 = new Contract();
            contract2.setDescription("Pillage, pillage, pillage!");
            
            // Populate the relationships.
            contract1.addSoldier(soldier1);
            
            contract2.addSoldier(soldier1);
            
            boss.setUnderboss(underboss);
            
            capo1.setUnderboss(underboss);
            
            capo1.addSoldier(soldier1);
            
            underboss.addCapo(capo1);
            
            family.addMafioso(boss);
            family.addMafioso(underboss);
            
            family.addMafioso(capo1);

            family.addMafioso(soldier1);
            
            // Will cascade through the whole family.
            em.persist(family);
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
    
    public void testValidateMafiaFamily707() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU);

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
        EntityManager em = createEntityManager(MULTI_TENANT_PU);

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
    
    public void testValidateMafiaFamily707and007WithSameEM() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU);

        try {
            validateMafiaFamily707(em);
            

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
    
    public void testValidateMafiaFamily123() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU_123);

        try {
            clearCache(MULTI_TENANT_PU_123);
            em.clear();
            
            MafiaFamily family =  em.find(MafiaFamily.class, family123);
            assertNotNull("The Mafia Family with id: " + family123 + ", was not found", family);
            assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [1]", family.getTags().size() == 1);
            assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
            assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
            assertFalse("No mafiosos part of 123 family", family.getMafiosos().isEmpty());
            
            // See if we can find any members of the other family.
            for (Integer id : family707Mafiosos) {
                assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
            }
            
            // Try a native sql query. Should get an exception since the
            // eclipselink.jdbc.allow-native-sql-queries property is set to 
            // false for this PU.
            boolean exceptionCaught = false;
            List mafiaFamilies = null;
            try {
                mafiaFamilies = em.createNativeQuery("select * from XML_MAFIA_FAMILY").getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }
            
            assertTrue("No exception was caught from issuing a native query.", exceptionCaught);
            
            // Query directly for the boss from the other family.
            Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
            assertNull("Found family 707 boss.", otherBoss);
            
            // See if we can find any contracts of the other family.
            for (Integer id : family707Contracts) {
                assertNull("Found family 707 contract. ", em.find(Contract.class, id));
            }
            
            // Try a select named query
            List families = em.createNamedQuery("findJPQLXMLMafiaFamilies").getResultList();
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
    
    protected void validateMafiaFamily007(EntityManager em) {
        clearCache(MULTI_TENANT_PU);
        em.clear();
        beginTransaction(em);

        em.setProperty("tenant.id", "007");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");
        
        MafiaFamily family =  em.find(MafiaFamily.class, family007);
        assertNotNull("The Mafia Family with id: " + family007 + ", was not found", family);
        assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [5]", family.getTags().size() == 5);
        assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
        assertNull("The Mafia Family with id: " + family123 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family123));
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
        List<Contract> contracts = em.createNamedQuery("FindAllXmlContracts").getResultList();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", contracts.size() == 3);
        
        for (Contract contract : contracts) {
            assertFalse("Contract description was voided.", contract.getDescription().equals("voided"));
        }
        
        // Try a select named query
        List families = em.createNamedQuery("findJPQLXMLMafiaFamilies").getResultList();
        assertTrue("Incorrect number of families were returned [" + families.size() + "], expected [1]",  families.size() == 1); 
   
        // Find our boss and make sure his name has not been compromised from the 707 family.
        Boss boss = em.find(Boss.class, family007Mafiosos.get(0));
        assertFalse("The Boss name has been compromised", boss.getFirstName().equals("Compromised"));
        commitTransaction(em);
    }
    
    protected void validateMafiaFamily707(EntityManager em) {
        clearCache(MULTI_TENANT_PU);
        em.clear();
        
        beginTransaction(em);

        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");
        MafiaFamily family = em.find(MafiaFamily.class, family707);
        assertNotNull("The Mafia Family with id: " + family707 + ", was not found", family);
        assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [3]", family.getTags().size() == 3);
        assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
        assertNull("The Mafia Family with id: " + family123 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family123));
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
            assertNull("Found family 007 contract. ", em.find(Contract.class, id));
        }
        
        // Update all our contract descriptions to be 'voided'
        em.createNamedQuery("UpdateAllXmlContractDescriptions").executeUpdate();
        
        // Read and validate the contracts
        List<Contract> contracts = em.createNamedQuery("FindAllXmlContracts").getResultList();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", contracts.size() == 3);
        
        // See how many soldiers are returned from a jpql query
        List soldiers = em.createQuery("SELECT s from XMLSoldier s").getResultList();
        assertTrue("Incorrect number of soldiers were returned [" + soldiers.size() + "], expected [5]",  soldiers.size() == 5);
        
        if(getServerSession(MULTI_TENANT_PU).getPlatform().isSymfoware()) {
            getServerSession(MULTI_TENANT_PU).logMessage("Test EntityMappingsMultiTenantJUnitTestCase partially skipped for this platform, "
                    +"which uses UpdateAll internally to check tenant-id when updating an entity using JOINED inheritance strategy. "
                    +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        // We know what the boss's id is for the 007 family to try to update him from the 707 pu.
        // The 007 family is validated after this test.
        Query query = em.createNamedQuery("UpdateXMLBossName");
        query.setParameter("name", "Compromised");
        query.setParameter("id", family007Mafiosos.get(0));
        query.executeUpdate();
        commitTransaction(em);
    }
    
    public void testTablePerTenantA() {
        EntityManager em = createEntityManager(MULTI_TENANT_TABLE_PER_TENANT_PU);
        
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
        EntityManager em = createEntityManager(MULTI_TENANT_TABLE_PER_TENANT_PU);
        
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
            clearCache(MULTI_TENANT_TABLE_PER_TENANT_PU);
            
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
    
    public void testTablePerTenantC() {
        // Set the tenant through EMF properties.
        Map properties = JUnitTestCaseHelper.getDatabaseProperties();
        properties.put(PersistenceUnitProperties.EXCLUDE_ECLIPSELINK_ORM_FILE, "true");
        properties.put(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, "false");
        properties.put(PersistenceUnitProperties.SESSION_NAME, "extended-multi-tenant-C");
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "C");
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");
        
        EntityManager em = createEntityManager(MULTI_TENANT_TABLE_PER_TENANT_C_PU, properties);
        
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

