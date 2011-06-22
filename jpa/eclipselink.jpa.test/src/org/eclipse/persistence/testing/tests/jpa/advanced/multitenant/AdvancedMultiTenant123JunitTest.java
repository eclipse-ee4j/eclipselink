/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.config.EntityManagerProperties;
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

public class AdvancedMultiTenant123JunitTest extends JUnitTestCase { 
    public static final String MULTI_TENANT_PU_123 = "MulitPU-2";

    public static int family123;
    public static int capo123Id;
    public static int soldier123Id;
    public static List<Integer> family123Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family123Contracts = new ArrayList<Integer>();
 
    public AdvancedMultiTenant123JunitTest() {
        super();
    }
    
    public AdvancedMultiTenant123JunitTest(String name) {
        super(name);
        setPuName(MULTI_TENANT_PU_123);
    }
    
    public void setUp() {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenant123JunitTest");
        //this test can be run with JPA10 server, but this test relies on AdvancedMultiTenantSharedEMFJunitTest, so make the //condition of this test complies with one of AdvancedMultiTenantSharedEMFJunitTest
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new AdvancedMultiTenant123JunitTest("testCreateMafiaFamily123"));
            suite.addTest(new AdvancedMultiTenant123JunitTest("testValidateMafiaFamily123"));
            //this test can be run on JPA1.0 servers, but it needs table created from AdvancedMultiTenantSharedEMFJunitTest.testSetup() and records inserted in above testCreateMafiaFamily123, these tests can't run on JPA1.0 servers
            suite.addTest(new  AdvancedMultiTenant123JunitTest("testComplexMultitenantQueries"));
        }
        return suite;
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
        
    public void testValidateMafiaFamily123() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU_123);
        try {
            clearCache(MULTI_TENANT_PU_123);
            em.clear();

            MafiaFamily family =  em.find(MafiaFamily.class, family123);
            assertNotNull("The Mafia Family with id: " + family123 + ", was not found", family);
            assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [1]", family.getTags().size() == 1);
            assertNull("The Mafia Family with id: " + AdvancedMultiTenantSharedEMFJunitTest.family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, AdvancedMultiTenantSharedEMFJunitTest.family707));
            assertNull("The Mafia Family with id: " + AdvancedMultiTenantSharedEMFJunitTest.family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, AdvancedMultiTenantSharedEMFJunitTest.family007));
            assertFalse("No mafiosos part of 123 family", family.getMafiosos().isEmpty());

            // See if we can find any members of the other family.
            for (Integer id : AdvancedMultiTenantSharedEMFJunitTest.family707Mafiosos) {
                assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
            }
            
            // Query directly for the boss from the other family.
            Boss otherBoss = em.find(Boss.class, AdvancedMultiTenantSharedEMFJunitTest.family707Mafiosos.get(0));
            assertNull("Found family 707 boss.", otherBoss);
            
            // See if we can find any contracts of the other family.
            for (Integer id : AdvancedMultiTenantSharedEMFJunitTest.family707Contracts) {
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

    public void testComplexMultitenantQueries() {
        EntityManager em = createEntityManager(MULTI_TENANT_PU_123);

        try {
            clearCache(MULTI_TENANT_PU_123);
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
                this.getServerSession(MULTI_TENANT_PU_123).setLogLevel(0);
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
