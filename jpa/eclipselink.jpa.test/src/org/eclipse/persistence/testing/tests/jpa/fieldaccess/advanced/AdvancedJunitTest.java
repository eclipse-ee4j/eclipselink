/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import java.util.Arrays;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Child;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Man;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Parent;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Woman;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Golfer;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.GolferPK;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Vegetable;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.VegetablePK;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.WorldRank;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PartnerLink;

public class AdvancedJunitTest extends JUnitTestCase {
    public AdvancedJunitTest() {
        super();
    }
    
    public AdvancedJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("AdvancedJunitTest");

        suite.addTest(new AdvancedJunitTest("testSetup"));
        suite.addTest(new AdvancedJunitTest("testGF1818"));
        suite.addTest(new AdvancedJunitTest("testGF1894"));
        suite.addTest(new AdvancedJunitTest("testGF894"));
        suite.addTest(new AdvancedJunitTest("testManAndWoman"));
        suite.addTest(new AdvancedJunitTest("testStringArrayField"));
        suite.addTest(new AdvancedJunitTest("testBUG241388"));
        suite.addTest(new AdvancedJunitTest("testZeroId"));
        suite.addTest(new AdvancedJunitTest("testLazyToInterface"));
        
        return suite;
    }
    
    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    public String getPersistenceUnitName() {
        return "fieldaccess";
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getDatabaseSession());

        clearCache();
    }

    public void testGF1818() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Vegetable vegetable = new Vegetable();
            vegetable.setId(new VegetablePK("Carrot", "Orange"));
            vegetable.setCost(2.09);
        
            em.persist(vegetable);
            commitTransaction(em);
            
        } catch (Exception e) {
            fail("An exception was caught: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testGF1894() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Guy");
        emp.setLastName("Pelletier");
        
        Address address = new Address();
        address.setCity("College Town");
        
        emp.setAddress(address);
            
        try {   
            Employee empClone = em.merge(emp);
            assertNotNull("The id field for the merged new employee object was not generated.", empClone.getId());
            commitTransaction(em);
            
            Employee empFromDB = em.find(Employee.class, empClone.getId());
            assertNotNull("The version locking field for the merged new employee object was not updated after commit.", empFromDB.getVersion());
            
            beginTransaction(em);
            Employee empClone2 = em.merge(empFromDB);
            assertTrue("The id field on a existing merged employee object was modified on a subsequent merge.", empFromDB.getId().equals(empClone2.getId()));
            commitTransaction(em);
        } catch (javax.persistence.OptimisticLockException e) {
            fail("An optimistic locking exception was caught on the merge of a new object. An insert should of occurred instead.");
        }
        
        closeEntityManager(em);
    }

    /**
     * Test that a zero id can be used.
     */
    public void testZeroId() {
        EntityManager entityManager = createEntityManager();

        beginTransaction(entityManager);
        Golfer golfer = entityManager.find(Golfer.class, new GolferPK(0));
        WorldRank rank = entityManager.find(WorldRank.class, 0);
        if (golfer != null) {
        	entityManager.remove(golfer);
        }
        if (rank != null) {
        	entityManager.remove(rank);
        }
        commitTransaction(entityManager);
        
        beginTransaction(entityManager);
        rank = new WorldRank();
        rank.setId(0);
        entityManager.persist(rank);
        commitTransaction(entityManager);
        this.assertTrue("Zero id assigned sequence value.", rank.getId() == 0);

        closeEntityManager(entityManager);
        clearCache();
        entityManager = createEntityManager();

        beginTransaction(entityManager);
        rank = new WorldRank();
        rank.setId(0);
        rank = entityManager.merge(rank);
        commitTransaction(entityManager);
        this.assertTrue("Zero id assigned sequence value.", rank.getId() == 0);
        
        beginTransaction(entityManager);
        rank = entityManager.find(WorldRank.class, 0);
        this.assertTrue("Zero id assigned sequence value.", rank.getId() == 0);
    	entityManager.remove(rank);
    	commitTransaction(entityManager);
        closeEntityManager(entityManager);
    }
    
    public void testGF894() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            for (int i = 0; ; i++) {
                GolferPK golferPK = new GolferPK(i);
                Golfer golfer = em.find(Golfer.class, golferPK);
            
                if (golfer == null) {
                    golfer = new Golfer();
                    golfer.setGolferPK(golferPK);
                
                    WorldRank worldRank = new WorldRank();
                    worldRank.setId(i);
                    golfer.setWorldRank(worldRank);
                
                    em.persist(worldRank);
                    em.persist(golfer);
                    commitTransaction(em);
                
                    break;
                } 
            }
        } catch (Exception e) {
            fail("An exception was caught: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testManAndWoman() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            PartnerLink pLink1 = new PartnerLink();
            pLink1.setMan(new Man());
            em.persist(pLink1);
            
            PartnerLink pLink2 = new PartnerLink();
            pLink2.setWoman(new Woman());
            em.persist(pLink2);
            
            PartnerLink pLink3 = new PartnerLink();
            pLink3.setMan(new Man());
            pLink3.setWoman(new Woman());
            em.persist(pLink3);
            
            commitTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }

    // GF1673, 2674 Java SE 6 classloading error for String[] field
    public void testStringArrayField() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        VegetablePK pk = new VegetablePK("Tomato", "Red");
        String[] tags = {"California", "XE"};
        try {
            Vegetable vegetable = new Vegetable();
            vegetable.setId(pk);
            vegetable.setCost(2.09);
            vegetable.setTags(tags);
        
            em.persist(vegetable);
            commitTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
        
        em = createEntityManager();
        beginTransaction(em);
        Vegetable vegetable;
        try {
            vegetable = em.find(Vegetable.class, pk);
            commitTransaction(em);
            assertNotNull(vegetable);
            assertTrue(Arrays.equals(tags, vegetable.getTags()));
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testBUG241388() {
        Integer id = null;
        int childCount;
        
        //// PART 1 ////
        EntityManager em1 = createEntityManager();
        beginTransaction(em1);
        
        try {
            Parent p0 = new Parent(false);
            em1.persist(p0);
            em1.flush();
            id = p0.getId();
            
            Parent p1 = em1.find(Parent.class, id);
            p1.setSerialNumber("12345678");

            Child cs_1_1 = new Child();
            p1.addChild(cs_1_1);
            em1.flush();

            Child cs_1_2 = new Child();
            p1.addChild(cs_1_2);
            em1.flush();

            Parent chassis2 = em1.find(Parent.class, id);
            Child cs_2_1 = new Child();
            chassis2.addChild(cs_2_1);
            em1.flush();
            
            commitTransaction(em1);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackTransaction(em1);
        } finally {
            Parent pa = em1.find(Parent.class, id);
            childCount = pa.getChildren().size();
            closeEntityManager(em1);
        }
        
        //// PART 2 ////
        Parent chassis = createEntityManager().find(Parent.class, id);
        assertTrue("The same number of children where not returned from the cache", childCount == chassis.getChildren().size());
    }    

    /**
     * Test that instantiating a lazy relationship to an interface works.
     * This requires that the weaving indirection policy find to real field correctly.
     */
    public void testLazyToInterface() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Parent parent = null;
        Child child = null;
        try {
            parent = new Parent(false);
            child = parent.getChildren().get(0);
            em.persist(parent);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            child = em.find(Child.class, child.getId());
            ((JpaEntityManager)em.getDelegate()).copy(child, new CopyGroup());
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
}
