/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.Arrays;

import javax.persistence.EntityManager;

import junit.framework.*;
import junit.extensions.TestSetup;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;

import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Man;
import org.eclipse.persistence.testing.models.jpa.advanced.Woman;
import org.eclipse.persistence.testing.models.jpa.advanced.Golfer;
import org.eclipse.persistence.testing.models.jpa.advanced.GolferPK;
import org.eclipse.persistence.testing.models.jpa.advanced.Vegetable;
import org.eclipse.persistence.testing.models.jpa.advanced.VegetablePK;
import org.eclipse.persistence.testing.models.jpa.advanced.WorldRank;
import org.eclipse.persistence.testing.models.jpa.advanced.PartnerLink;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;

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
        TestSuite suite = new TestSuite(AdvancedJunitTest.class);

        return new TestSetup(suite) {
            protected void setUp() { 
                new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
            }

            protected void tearDown() {
                clearCache();
            }
        };
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
    
    public void testEL254937(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        LargeProject lp1 = new LargeProject();
        lp1.setName("one");
        em.persist(lp1);
        commitTransaction(em);
        em = createEntityManager();
        beginTransaction(em);
        em.remove(em.find(LargeProject.class, lp1.getId()));
        em.flush();
        System.out.println("done the flush!!!");
        org.eclipse.persistence.jpa.JpaEntityManager eclipselinkEm = (org.eclipse.persistence.jpa.JpaEntityManager)em;
        org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork uow = 
            (org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork)eclipselinkEm.getActiveSession();
        //duplicate the beforeCompletion call
        uow.issueSQLbeforeCompletion();
        //commit the transaction
        uow.setShouldTerminateTransaction(true);
        uow.commitTransaction();
        
        //duplicate the AfterCompletion call.  This should merge, removing the LargeProject from the shared cache
        uow.mergeClonesAfterCompletion();
        em = createEntityManager();
        LargeProject cachedLargeProject = em.find(LargeProject.class, lp1.getId());
        em.close();
        assertTrue("Entity removed during flush was not removed from the shared cache on commit", cachedLargeProject==null);
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

}
