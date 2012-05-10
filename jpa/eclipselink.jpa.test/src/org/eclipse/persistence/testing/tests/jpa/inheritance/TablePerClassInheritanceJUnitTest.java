/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.Collection;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.jpa.JpaEntityManager;

import org.eclipse.persistence.testing.models.jpa.inheritance.Assassin;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bomb;
import org.eclipse.persistence.testing.models.jpa.inheritance.ContractedPersonel;
import org.eclipse.persistence.testing.models.jpa.inheritance.DirectElimination;
import org.eclipse.persistence.testing.models.jpa.inheritance.DirectWeapon;
import org.eclipse.persistence.testing.models.jpa.inheritance.Elimination;
import org.eclipse.persistence.testing.models.jpa.inheritance.EliminationPK;
import org.eclipse.persistence.testing.models.jpa.inheritance.Gun;
import org.eclipse.persistence.testing.models.jpa.inheritance.IndirectElimination;
import org.eclipse.persistence.testing.models.jpa.inheritance.IndirectWeapon;
import org.eclipse.persistence.testing.models.jpa.inheritance.SocialClub;
import org.eclipse.persistence.testing.models.jpa.inheritance.SpecialAssassin;
import org.eclipse.persistence.testing.models.jpa.inheritance.Weapon;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.EntityManager;

public class TablePerClassInheritanceJUnitTest extends JUnitTestCase {
    private static Integer assassinId;
    private static Integer specialAssassinId;
    private static Integer socialClub1Id;
    private static Integer socialClub2Id;
    private static Integer socialClub3Id;
    private static Integer gunSerialNumber;
    private static EliminationPK directEliminationPK;
    private static EliminationPK indirectEliminationPK;
    
    public TablePerClassInheritanceJUnitTest() {
        super();
    }

    public TablePerClassInheritanceJUnitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("TablePerClassInheritanceJUnitTestSuite");
        suite.addTest(new TablePerClassInheritanceJUnitTest("testSetup"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testCreateAssassinWithGun"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateAssassinWithGun"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateGunToAssassin"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testAddDirectElimination"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateDirectElimination"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testAddIndirectElimination"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateIndirectElimination"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateAssassinWithBombAndEliminations"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testNamedQueryFindAllWeapons"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testNamedQueryFindAllWeaponsWhereDescriptionContainsSniper"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testCreateNewSocialClubsWithMembers"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateSocialClub1Members"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateSocialClub2Members"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testValidateSocialClub3Members"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testAssassinOptimisticLocking"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testSpecialAssassinOptimisticLocking"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testAssassinOptimisticLockingUsingEntityManagerAPI"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testGunOptimisticLocking"));
        suite.addTest(new TablePerClassInheritanceJUnitTest("testUpdateAllQuery"));
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testCreateAssassinWithGun() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);

            Assassin assassin = new Assassin();
            assassin.setName("Assassin1");
            
            Gun gun = new Gun();
            gun.setCaliber(new Integer(50));
            gun.setDescription("Sniper rifle");
            gun.setModel("9-112");
            
            assassin.setWeapon(gun);
            
            em.persist(assassin);
            assassinId = assassin.getId();
            gunSerialNumber = gun.getSerialNumber();
            
            commitTransaction(em);
        } catch (Exception exception) {
            exception.printStackTrace();
            fail("Error persisting new assassin: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testValidateAssassinWithGun() {
        EntityManager em = createEntityManager();
        
        Assassin assassin = em.find(Assassin.class, assassinId);
        assertNotNull("The assassin could not be read back.", assassin);
        
        Weapon weapon = assassin.getWeapon();
        assertNotNull("The assassin's weapon was null", weapon);
        assertTrue("The assassin's weapon was not a direct weapon", weapon.isDirectWeapon());
        assertTrue("The assassin's weapon was not a gun", ((DirectWeapon) weapon).isGun());
        
        closeEntityManager(em);
    }
    
    public void testValidateGunToAssassin() {
        EntityManager em = createEntityManager();
        
        Gun gun = em.find(Gun.class, gunSerialNumber);
        assertNotNull("The gun could not be read back.", gun);
        
        Assassin assassin = gun.getAssassin();
        assertNotNull("The gun's assassin was null", assassin);
        assertTrue("The gun is currently not assigned to the correct assassin", assassin.getId().equals(assassinId));
        
        closeEntityManager(em);
    }
    
    public void testAddDirectElimination() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);
            Assassin assassin = em.find(Assassin.class, assassinId);
            
            // Assassin already has a gun, therefore, correct weapon already set
            // for a direct elimination.
            DirectElimination directElimination = new DirectElimination();
            directElimination.setId(new Long(System.currentTimeMillis()).intValue());
            directElimination.setName("Joe Smuck");
            directElimination.setDescription("Because he has a big mouth");
            directElimination.setAssassin(assassin);
            em.persist(directElimination);
            directEliminationPK = directElimination.getPK();
            
            assassin.getEliminations().add(directElimination);
            
            commitTransaction(em);
        } catch (Exception e) {
            fail("Error adding new direct elimination: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testValidateDirectElimination() {
        EntityManager em = createEntityManager();
        
        Elimination directElimination = em.find(Elimination.class, directEliminationPK);
        assertNotNull("The direct elimination could not be read back.", directElimination);
        assertTrue("The elimination was not a direct elimination", directElimination.isDirectElimination());
        
        // Validate the weapon that was used for the direct elimination.
        Weapon weapon = ((DirectElimination) directElimination).getDirectWeapon();
        assertNotNull("The direct elimination's weapon was null", weapon);
        assertTrue("The direct elimination's weapon was not a direct weapon", weapon.isDirectWeapon());
        assertTrue("The direct elimination's weapon was not a gun", ((DirectWeapon) weapon).isGun());
        
        closeEntityManager(em);
    }
    
    public void testAddIndirectElimination() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            
            Assassin assassin = em.find(Assassin.class, assassinId);
        
            Bomb bomb = new Bomb();
            bomb.setBombType(Bomb.BOMBTYPE.DIRTY);
            bomb.setDescription("Nasty blasty");
            em.persist(bomb);
            
            // Must set the correct weapon before an elimination.
            assassin.setWeapon(bomb);
            
            IndirectElimination indirectElimination = new IndirectElimination();
            indirectElimination.setId(new Long(System.currentTimeMillis()).intValue());
            indirectElimination.setName("Jill Smuck");
            indirectElimination.setDescription("Because she has a big mouth");
            indirectElimination.setAssassin(assassin);
            em.persist(indirectElimination);
            indirectEliminationPK = indirectElimination.getPK();
            
            assassin.getEliminations().add(indirectElimination);
            
            commitTransaction(em);
        } catch (Exception e) {
            fail("Error adding new indirect elimination: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testValidateIndirectElimination() {
        EntityManager em = createEntityManager();
        
        Elimination indirectElimination = em.find(Elimination.class, indirectEliminationPK);
        assertNotNull("The indirect elimination could not be read back.", indirectElimination);
        assertTrue("The elimination was not an idirect elimination", indirectElimination.isIndirectElimination());
        
        // Validate the weapon that was used for the direct elimination.
        Weapon weapon = ((IndirectElimination) indirectElimination).getIndirectWeapon();
        assertNotNull("The indirect elimination's weapon was null", weapon);
        assertTrue("The indirect elimination's weapon was not an idirect weapon", weapon.isIndirectWeapon());
        assertTrue("The indirect elimination's weapon was not a bomb", ((IndirectWeapon) weapon).isBomb());
        
        closeEntityManager(em);
    }
    
    public void testValidateAssassinWithBombAndEliminations() {
        EntityManager em = createEntityManager();
        
        Assassin assassin = em.find(Assassin.class, assassinId);
        assertNotNull("The assassin could not be read back.", assassin);
        assassin.getEliminations();
        assertFalse("The assassin didn't have any eliminations", assassin.getEliminations().isEmpty());
        
        Weapon weapon = assassin.getWeapon();
        assertNotNull("The assassin's weapon was null", weapon);
        assertTrue("The assassin's weapon was not an indirect weapon", weapon.isIndirectWeapon());
        assertTrue("The assassin's weapon was not a bomb", ((IndirectWeapon) weapon).isBomb());
        
        closeEntityManager(em);
    }

    // At this point no Weapon objects have been created only only real weapons.
    // So if the query returns an empty collection, something is not working.
    // We have create a gun and a bomb at this point ...
    public void testNamedQueryFindAllWeapons() {
        EntityManager em = createEntityManager();
        
        try {        
            Query query = em.createNamedQuery("findAllWeapons");
            Collection<Weapon> weapons = query.getResultList();
            assertFalse("No weapons were returned", weapons.isEmpty());
            assertTrue("Expected weapon count of 2 not returned", weapons.size() == 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error issuing find all weapons query: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }
    
    // At this point no Weapon objects have been created only only real weapons.
    // So if the query returns an empty collection, something is not working.
    // We have create a gun and a bomb at this point, and the gun is a sniper rifle.
    public void testNamedQueryFindAllWeaponsWhereDescriptionContainsSniper() {
        EntityManager em = createEntityManager();
        
        try {        
            Query query = em.createNamedQuery("findAllWeaponsContainingDescription");

            query.setParameter("description", "Sniper%");
            Collection<Weapon> weapons = query.getResultList();
            assertFalse("No weapons were returned", weapons.isEmpty());
            assertTrue("Expected weapon count of 1 not returned", weapons.size() == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error issuing find all weapons query: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    // Maybe test that setting this flag does not cause an error. Internally
    // it gets ignored.
    // ((ObjectLevelReadQuery)((EJBQueryImpl) query).getDatabaseQuery()).setShouldOuterJoinSubclasses(true);
    public void testCreateNewSocialClubsWithMembers() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);

            // Create some new clubs.
            SocialClub socialClub1 = new SocialClub();
            socialClub1.setName("Jimmy's my name and killing is my game!");
            em.persist(socialClub1);
            socialClub1Id = socialClub1.getId();
            
            SocialClub socialClub2 = new SocialClub();
            socialClub2.setName("Sharp shooting");
            em.persist(socialClub2);
            socialClub2Id = socialClub2.getId();
            
            SocialClub socialClub3 = new SocialClub();
            socialClub3.setName("Precision explosions");
            em.persist(socialClub3);
            socialClub3Id = socialClub3.getId();
            
            // Create a couple new characters and add them to various clubs.
            ContractedPersonel contractedPersonel = new ContractedPersonel();
            contractedPersonel.setName("Hired Goon");
            em.persist(contractedPersonel);
            
            SpecialAssassin specialAssassin = new SpecialAssassin();
            specialAssassin.setName("IED Expert");
            em.persist(specialAssassin);
            specialAssassinId = specialAssassin.getId();
            
            Assassin assassin = em.find(Assassin.class, assassinId);
            
            socialClub1.addMember(assassin);
            socialClub1.addMember(contractedPersonel);
            socialClub1.addMember(specialAssassin);
            
            socialClub2.addMember(assassin);
            socialClub2.addMember(contractedPersonel);
            
            socialClub3.addMember(contractedPersonel);
            socialClub3.addMember(specialAssassin);
            
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testValidateSocialClub1Members() {
        EntityManager em = createEntityManager();
        
        SocialClub socialClub1 = createEntityManager().find(SocialClub.class, socialClub1Id);
        assertNotNull("The social club 1 could not be read back.", socialClub1);
        assertFalse("The member list was empty", socialClub1.getMembers().isEmpty());
        assertTrue("The member count was not the expected 3", socialClub1.getMembers().size() == 3);
        
        closeEntityManager(em);
    }
    
    public void testValidateSocialClub2Members() {
        EntityManager em = createEntityManager();
        
        SocialClub socialClub2 = createEntityManager().find(SocialClub.class, socialClub2Id);
        assertNotNull("The social club 1 could not be read back.", socialClub2);
        assertFalse("The member list was empty", socialClub2.getMembers().isEmpty());
        assertTrue("The member count was not the expected 2", socialClub2.getMembers().size() == 2);
        
        closeEntityManager(em);
    }
    
    public void testValidateSocialClub3Members() {
        EntityManager em = createEntityManager();
        
        SocialClub socialClub3 = createEntityManager().find(SocialClub.class, socialClub3Id);
        assertNotNull("The social club 1 could not be read back.", socialClub3);
        assertFalse("The member list was empty", socialClub3.getMembers().isEmpty());
        assertTrue("The member count was not the expected 2", socialClub3.getMembers().size() == 2);
        
        closeEntityManager(em);
    }
    
    // Tested for an Assassin and a SpecialAssasin
    protected void optimisticLockTestOnAssassin(Integer id) {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            
            em1.getTransaction().begin();
            em2.getTransaction().begin();
            
            RuntimeException caughtException = null;
            
            try {            
                Assassin assassin1 = em1.find(Assassin.class, id);
                Assassin assassin2 = em2.find(Assassin.class, id);
                
                assassin1.setName("Geezer");
                assassin2.setName("Guyzer");
                
                em1.getTransaction().commit();
                em2.getTransaction().commit();
                
                em1.close();
                em2.close();
            } catch (RuntimeException e) {
                if (em1.getTransaction().isActive()){
                    em1.getTransaction().rollback();
                }
                
                if (em2.getTransaction().isActive()){
                    em2.getTransaction().rollback();
                }
                
                if (e.getCause() instanceof javax.persistence.OptimisticLockException) {
                    caughtException = e;
                } else {
                    throw e;
                }
            } finally {
                em1.close();
                em2.close();
            }
            
            if (caughtException == null) {
                fail("Optimistic lock exception was not thrown.");
            }  
        }
    }
    
    public void testAssassinOptimisticLocking() {
        optimisticLockTestOnAssassin(assassinId);
    }
    
    // Just being thorough ... special assassin uses a difference column
    // for versioning through an attribute override, but if it didn't work 
    // we would have known long before now.
    public void testSpecialAssassinOptimisticLocking() {
        optimisticLockTestOnAssassin(specialAssassinId);
    }
    
    public void testAssassinOptimisticLockingUsingEntityManagerAPI() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            Assassin assassin = null;
                
            try {
                assassin = new Assassin();
                assassin.setName("OptLockAssassin");
                em.persist(assassin);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                    
                closeEntityManager(em);
                throw ex;
            }
                
            EntityManager em2 = createEntityManager();
            Exception optimisticLockException = null;
            beginTransaction(em);
                
            try {
                assassin = em.find(Assassin.class, assassin.getId());
                em.lock(assassin, LockModeType.WRITE);
                beginTransaction(em2);
                    
                try {
                    Assassin assassin2 = em2.find(Assassin.class, assassin.getId());
                    assassin2.setName("OptLockAssassin2");
                    commitTransaction(em2);
                    em2.close();
                } catch (RuntimeException ex) {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                        
                    closeEntityManager(em2);
                    throw ex;
                }
                
                try {
                    em.flush();
                } catch (PersistenceException exception) {
                    if (exception instanceof OptimisticLockException){
                        optimisticLockException = exception;
                    } else {
                        throw exception;
                    }
                }
                    
                rollbackTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                    
                closeEntityManager(em);
                throw ex;
            }
                
            try {
                beginTransaction(em);
                assassin = em.find(Assassin.class, assassin.getId());
                em.remove(assassin);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                    
                closeEntityManager(em);
                throw ex;
            }
                
            assertFalse("Proper exception not thrown when EntityManager.lock(object, OPTIMISTIC) is used.", optimisticLockException == null);
        }
    }
    
    // Gun uses an ALL_COLUMNS optimistic locking policy.
    public void testGunOptimisticLocking() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            
            em1.getTransaction().begin();
            em2.getTransaction().begin();
            
            RuntimeException caughtException = null;
            
            try {            
                Gun gun1 = em1.find(Gun.class, gunSerialNumber);
                Gun gun2 = em2.find(Gun.class, gunSerialNumber);
                
                gun1.setCaliber(new Integer(12));
                gun2.setCaliber(new Integer(22));
                
                em1.getTransaction().commit();
                em2.getTransaction().commit();
                
                em1.close();
                em2.close();
            } catch (RuntimeException e) {
                if (em1.getTransaction().isActive()){
                    em1.getTransaction().rollback();
                }
                
                if (em2.getTransaction().isActive()){
                    em2.getTransaction().rollback();
                }
                
                if (e.getCause() instanceof javax.persistence.OptimisticLockException) {
                    caughtException = e;
                } else {
                    throw e;
                }
            } finally {
                em1.close();
                em2.close();
            }
            
            if (caughtException == null) {
                fail("Optimistic lock exception was not thrown.");
            }  
        }
    }
    
    // Note: Update all for Table Per class only works with the one single
    // reference class. So it's not a full feature test.
    public void testUpdateAllQuery() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ExpressionBuilder eb = new ExpressionBuilder();
            UpdateAllQuery updateQuery = new UpdateAllQuery(Assassin.class);
            updateQuery.addUpdate(eb.get("name"), "Generic Assassin Name");
            ((JpaEntityManager)em.getDelegate()).getServerSession().executeQuery(updateQuery);
            Assassin assassin = (Assassin) em.find(ContractedPersonel.class, assassinId);
            em.refresh(assassin);
            commitTransaction(em);
            assertTrue("Update all did not work", assassin.getName().equals("Generic Assassin Name"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error issuing update all contracted personel query: " + e.getMessage());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    /**
     * Commenting out this test. It didn't not work and well I'll leave it here
     * for now in case we pick this up again some other time.
     */
    /*
    public void testBatchQueryHint(){
        int id1 = 0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Assassin assassin1 = new Assassin();
        assassin1.setName("Assassin1");
        Weapon weapon = new Weapon();
        weapon.setDescription("A generic weapon");
        weapon.setAssassin(assassin1);
        assassin1.setWeapon(weapon);
        em.persist(assassin1);
        
        //id1 = manager.getId();
        
        Assassin assassin2 = new Assassin();
        assassin2.setName("Assassin2");
        Poison poison = new Poison();
        poison.setDescription("A slow death poison");
        poison.setEffectTime(Poison.EFFECTTIME.PROLONGED);
        assassin2.setWeapon(poison);
        em.persist(assassin2);
        
        commitTransaction(em);
        em.clear();

        JpaQuery query = (JpaQuery) getEntityManagerFactory().createEntityManager().createQuery("SELECT a FROM Assassin a WHERE a.id > 0 order by a.name");
        query.setHint(QueryHints.BATCH, "a.weapon");
        query.setHint(QueryHints.BATCH, "a.eliminations");
        
        ReadAllQuery raq = (ReadAllQuery)query.getDatabaseQuery();
        List expressions = raq.getBatchReadAttributeExpressions();
        assertTrue(expressions.size() == 2);
        Expression exp = (Expression)expressions.get(0);
        assertTrue(exp.isQueryKeyExpression());
        assertTrue("Query key name was not weapon", exp.getName().equals("weapon"));
        exp = (Expression)expressions.get(1);
        assertTrue(exp.isQueryKeyExpression());
        assertTrue("Query key name was not eliminations", exp.getName().equals("eliminations"));

        List resultList = query.getResultList();
        Assassin assassin = (Assassin) resultList.get(0);
        assassin.getWeapon().hashCode();

        assassin = (Assassin) resultList.get(1);
        assassin.getWeapon().hashCode();
    }
    */
}

