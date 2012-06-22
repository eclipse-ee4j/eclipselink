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
 *     05/24/2011-2.3 Guy Pelletier 
 *       - 345962: Join fetch query when using tenant discriminator column fails.
 *     06/1/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 9)
 *     06/30/2011-2.3.1 Guy Pelletier 
 *       - 341940: Add disable/enable allowing native queries
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType 
 *     11/15/2011-2.3.2 Guy Pelletier
 *       - 363820: Issue with clone method from VPDMultitenantPolicy
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.*;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Reward;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubCapo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubTask;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Task;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Underboss;

public class AdvancedMultiTenantJunitTest extends JUnitTestCase { 
    public static final String MULTI_TENANT_VPD_PU = "multi-tenant-vpd";
    public static final String MULTI_TENANT_PU = "multi-tenant-shared-emf";
    public static final String MULTI_TENANT_PU_123 = "multi-tenant-123";
    
    public static int family707;
    public static int family007;
    public static int family123;
    public static int capo123Id;
    public static int soldier123Id;
    public static List<Integer> family707Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family707Contracts = new ArrayList<Integer>();
    public static List<Integer> family007Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family007Contracts = new ArrayList<Integer>();
    public static List<Integer> family123Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family123Contracts = new ArrayList<Integer>();
    
    public AdvancedMultiTenantJunitTest() {
        super();
    }
    
    public AdvancedMultiTenantJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantJunitTest");
        
        suite.addTest(new AdvancedMultiTenantJunitTest("testSetup"));
        
        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateMafiaFamily707"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateMafiaFamily007"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateMafiaFamily123"));
        
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily707"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily007"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily707and007WithSameEM"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily123"));
        
        suite.addTest(new AdvancedMultiTenantJunitTest("testComplexMultitenantQueries"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testVPDEMPerTenant"));
        
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
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");
        
        try {
            beginTransaction(em);
            
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
        em.setProperty("tenant.id", "007");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

        try {
            beginTransaction(em);
            
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
    
    public void testValidateMafiaFamily707() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU);
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");

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
        em.setProperty("tenant.id", "007");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

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
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");

        try {
            validateMafiaFamily707(em);
            
            // Change the properties on the same EM and validate the next family.
            em.setProperty("tenant.id", "007");
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

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
            
            
            // Query directly for the boss from the other family.
            Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
            assertNull("Found family 707 boss.", otherBoss);
            
            // See if we can find any contracts of the other family.
            for (Integer id : family707Contracts) {
                assertNull("Found family 707 contract. ", em.find(Contract.class, id));
            }
            
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
        
    protected void validateMafiaFamily007(EntityManager em) {
        clearCache(MULTI_TENANT_PU);
        em.clear();
        
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
        List<Contract> contracts = em.createNamedQuery("FindAllContracts").getResultList();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", contracts.size() == 3);
        
        for (Contract contract : contracts) {
            assertFalse("Contract description was voided.", contract.getDescription().equals("voided"));
        }
        
        // Try a select named query
        List families = em.createNamedQuery("findAllMafiaFamilies").getResultList();
        assertTrue("Incorrect number of families were returned [" + families.size() + "], expected [1]",  families.size() == 1);
   
        // Find our boss and make sure his name has not been compromised from the 707 family.
        Boss boss = em.find(Boss.class, family007Mafiosos.get(0));
        assertFalse("The Boss name has been compromised", boss.getFirstName().equals("Compromised"));
    }
    
    protected void validateMafiaFamily707(EntityManager em) {
        clearCache(MULTI_TENANT_PU);
        em.clear();
        
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
            assertNull("Found family 007 contract.", em.find(Contract.class, id));
        }
        
        // Try deleting a contract from the 007 family.
        beginTransaction(em);
        Query deleteQuery = em.createNamedQuery("DeleteContractByPrimaryKey");
        deleteQuery.setParameter("id", family007Contracts.get(0));
        int result = deleteQuery.executeUpdate();
        assertTrue("Was able to delete a contract from the 007 family", result == 0);
        commitTransaction(em);
        
        // Update all our contract descriptions to be 'voided'
        beginTransaction(em);
        Query updateAllQuery = em.createNamedQuery("UpdateAllContractDescriptions");
        updateAllQuery.executeUpdate();
        // Need to check that tenant id column is present
        assertTrue("Tenant discriminator column not found in update all query", ((EJBQueryImpl) updateAllQuery).getDatabaseQuery().getCall().getSQLString().contains("TENANT_ID"));
        commitTransaction(em);
        
        // Read and validate the contracts
        List<Contract> contracts = em.createNamedQuery("FindAllContracts").getResultList();
        int contractNumber = contracts.size();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected [3]", contracts.size() == 3);
        
        for (Contract contract : contracts) {
            assertTrue("Contract description was not voided.", contract.getDescription().equals("voided"));
        }
        
        // See how many soldiers are returned from a jpql query
        List soldiers = em.createQuery("SELECT s from Soldier s").getResultList();
        assertTrue("Incorrect number of soldiers were returned [" + soldiers.size() + "], expected [5]",  soldiers.size() == 5);
        
        if(getServerSession(MULTI_TENANT_PU).getPlatform().isSymfoware()) {
            getServerSession(MULTI_TENANT_PU).logMessage("Test AdvancedMultiTenantJunitTest partially skipped for this platform, "
                    +"which uses UpdateAll internally to check tenant-id when updating an entity using JOINED inheritance strategy. "
                    +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        // We know what the boss's id is for the 007 family to try to update him from the 707 pu.
        // The 007 family is validated after this test.
        beginTransaction(em);
        Query query = em.createNamedQuery("UpdateBossName");
        query.setParameter("name", "Compromised");
        query.setParameter("id", family007Mafiosos.get(0));
        query.executeUpdate();
        commitTransaction(em);
    }
    
    public void testComplexMultitenantQueries() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU_123);

        try {
            clearCache(MULTI_TENANT_PU_123);
            em.clear();
            
            // Try passing in a sub entity as a parameter.
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
            
            if(getServerSession(MULTI_TENANT_PU).getPlatform().isSymfoware()) {
                getServerSession(MULTI_TENANT_PU).logMessage("Test AdvancedMultiTenantJunitTest partially skipped for this platform, "
                        +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            } else {
                // Try a delete all on multiple table (MafiaFamily)
                try {
                beginTransaction(em);
                List<MafiaFamily> allFamilies = em.createNamedQuery("findAllMafiaFamilies").getResultList();
                int families = allFamilies.size();
                assertTrue("More than one family was found ["+ families +"]", families == 1);
                Query deleteQuery = em.createNamedQuery("DeleteAllMafiaFamilies");
                deleteQuery.setHint(QueryHints.ALLOW_NATIVE_SQL_QUERY, true);
                int deletes = deleteQuery.executeUpdate();
                assertTrue("Incorrect number of families deleted [" + deletes + "], expected [" + families + "]", deletes == 1);
                commitTransaction(em);
                } catch (Exception e) {
                    fail("Exception encountered on delete all query with multiple table (with tenant discriminator columns): " + e);
                }
              
                // Some verification of what was deleted.
                EntityManager em007 = createEntityManager(MULTI_TENANT_PU);
                
                try {
                    List<MafiaFamily> families = em007.createNativeQuery("select * from JPA_MAFIA_FAMILY", MafiaFamily.class).getResultList();
                    assertTrue("Incorrect number of families found through SQL [" + families.size() + "], expected [2]", families.size() == 2);     
                    
                    // Clear out the shared cache with what we read through native SQL.
                    clearCache(MULTI_TENANT_PU);
                    em007.clear();
                    
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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    /**
     * This test will try to DDL generate on deploy. Meaning once we try to 
     * access the VPD PU we'll get an exception if we are not an oracle platform
     * so check before throwing an exception.
     * 
     * VPD is currently supported only on Oracle.
     */
    public void testVPDEMPerTenant() {
        EntityManager em1 = null;
        EntityManager em2 = null;
        
        try {
            em1 = createEntityManager(MULTI_TENANT_VPD_PU);
            em1.setProperty("tenant.id", "bsmith@here.com");
            
            em2 = createEntityManager(MULTI_TENANT_VPD_PU);
            em2.setProperty("tenant.id", "gdune@there.ca");
            
            testInsertTask(em1, "blah", false);
            testInsertTask(em2, "halb", false);
                
            assertTrue("Found more than one result", em1.createQuery("Select t from Task t").getResultList().size() == 1);
            assertTrue("Found more than one result", em2.createQuery("Select t from Task t").getResultList().size() == 1);
                
            Task task1 = testInsertTaskWithOneSubtask(em1, "Rock that Propsal", false, "Write Proposal", false);
            assertNotNull(em1.find(Task.class, task1.getId()));
            assertNull(em2.find(Task.class, task1.getId())); // negative test
                
            Task task3 = testInsertTask(em2, "mow lawn", true);
            assertNull(em1.find(Task.class, task3.getId())); // negative test
            assertNotNull(em2.find(Task.class, task3.getId()));
            
            SubTask task4 = testInsertSubTaskObject(em1, "SubTask Object Creation", true);
            assertNotNull(em1.find(SubTask.class, task4.getId()));
            assertNull(em2.find(SubTask.class, task4.getId())); // negative test
            
        } catch (RuntimeException e) {
            if (em1 != null && isTransactionActive(em1)){
                rollbackTransaction(em1);
            }
            
            if (em2 != null && isTransactionActive(em2)){
                rollbackTransaction(em2);
            }
            
            if (! getPlatform(MULTI_TENANT_VPD_PU).isOracle()) {
                warning("VPD tests currently run only on an Oracle platform");
            } else {
                throw e;
            }
        } finally {
            if (em1 != null) {
                closeEntityManager(em1);
            }
            
            if (em2 != null) {
                closeEntityManager(em2);
            }
        }
    }

    private Task testInsertTask(EntityManager em, String description, boolean isCompleted) {
        beginTransaction(em);
        Task task = new Task();
        task.setDescription(description);
        task.setCompleted(isCompleted);
        em.persist(task);
        commitTransaction(em);
        return task;
    }

    private Task testInsertTaskWithOneSubtask(EntityManager em, String description, boolean isCompleted, String subtaskDesc, boolean isSubtaskCompleted) {
        beginTransaction(em);        
        Task task = new Task();
        Task subtask = new Task();
        task.setDescription(description);
        task.setCompleted(isCompleted);
        subtask.setDescription(subtaskDesc);
        subtask.setCompleted(isSubtaskCompleted);
        task.addSubtask(subtask);
        em.persist(subtask);
        em.persist(task);
        commitTransaction(em);
        return task;
    }
    
    private SubTask testInsertSubTaskObject(EntityManager em, String description, boolean isCompleted) {
        beginTransaction(em);        
        SubTask subTask = new SubTask();
        subTask.setDescription(description);
        subTask.setCompleted(isCompleted);
        em.persist(subTask);
        commitTransaction(em);
        return subTask;
    }
}
