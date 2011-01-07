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
package org.eclipse.persistence.testing.tests.jpa.advanced.concurrency;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;

/**
 *  This test suite verifies that the state/lifecycle on a unitOfWork does not reset to 0 (Birth)
 *  when a clearForClose() call is attempted in the middle of a *Pending state (1,2,4).
 *  
 *  Note: These tests verify internal API state that JPA functionality depends on.
 *  The tests are tightly coupled to the implementation of the following functions.
 *  Any change to the behavior of these functions may need to be reflected in these tests
 *  
 *     UnitOfWorkImpl.getCloneToOriginals()
 *     UnitOfWorkImpl.setPendingMerge()
 *     UnitOfWorkImpl.clearForClose()
 *     
 *  The server level JTA tests that verify that test this fix are the following   
 *   
 *     02/10/2009-1.1 Michael O'Brien 
 *        - 259993: Defer a clear() call to release() if uow lifecycle is 1,2 or 4 (*Pending).
 *     09/24/2010-2.1 Michael O'Brien 
 *        - 326097: assertion failures are ignored by catch block - refactor test
 *                       use refactored clear() instead of clearForClose()
 */
public class LifecycleJUnitTest extends JUnitTestCase {

    public LifecycleJUnitTest() {
        super();
    }
    
    public LifecycleJUnitTest(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("LifecycleJUnitTestSuite");
        suite.addTest(new LifecycleJUnitTest("testSetup"));
        suite.addTest(new LifecycleJUnitTest("testClearWhileEntityManagerInFakeMergePendingState4"));        
        suite.addTest(new LifecycleJUnitTest("testClearWhileEntityManagerInFakeBirthState0"));        
        suite.addTest(new LifecycleJUnitTest("testClearWhileEntityManagerInCommitPendingStateWithClearAfterCommit"));
        suite.addTest(new LifecycleJUnitTest("testClearWhileEntityManagerInCommitPendingStateWithNoClearAfterCommit"));        
        suite.addTest(new LifecycleJUnitTest("testClearAfterEntityManagerCommitFinished"));
                
        return suite;
    }
    
    // RESOURCE_LOCAL non container managed uow
    private UnitOfWorkImpl getUnitOfWorkFromEntityManager(EntityManager em) {
        return ((UnitOfWorkImpl)((JpaEntityManager)em).getActiveSession()).acquireUnitOfWork();    
    }
    
    public void testSetup() {
        clearCache();
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
    }

    public void finalize() {
    }
    
    // This test is a pure unit test that directly sets and tries to clear the uow state
    // There are no actual entities managed in this example
    public void testClearWhileEntityManagerInFakeMergePendingState4() {    
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = null;
        UnitOfWorkImpl  uow = null;
        Map cloneToOriginalsMap = null;
        Department dept = null;   
        
        try {
            em = emf.createEntityManager();
            // get the underlying uow
            uow = getUnitOfWorkFromEntityManager(em);
                        
            // force a get on the map to lazy initialize an empty map
            cloneToOriginalsMap = uow.getCloneToOriginals();
            // verify size 0
            // we don't have access to the protected function uow.hasCloneToOriginals();
            assertEquals("cloneToOriginalsMap must be size 0", 0, cloneToOriginalsMap.size());
            // Verify that cloneToOriginals is null and not lazy initialized
            dept = new Department();
            cloneToOriginalsMap.put(null, dept);
            // verify size 1
            assertEquals("cloneToOriginalsMap must be size 1", 1, cloneToOriginalsMap.size());
            
            // verify we are in birth state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);            
            // setup the uow in a simulated state
            uow.setPendingMerge(); // set state to 4 = MergePending

            // (via backdoor function) verify we are in PendingMerge state
            int lifecycleInMerge = uow.getLifecycle();
            assertEquals("MergePending state 4 is not set ", 4, lifecycleInMerge);
            // simulate a clear() call in the middle of a merge
            // 326097: This assertion used to be ignored by a catch block - it is a valid test but we want to clear on the EM not the UOW
            em.clear();

            // verify that the uow ignored the clear call
            int lifecycleAfter = uow.getLifecycle();
            assertEquals("UnModified MergePending state 4 should still be 4 and not Birth state 0 after a clear() ", 4, lifecycleAfter);            
            // verify that a map previously set on the uow was cleared to null by the
            // verify size 0
            assertNotNull("cloneToOriginals Map must not be null after a clear in *Pending state", cloneToOriginalsMap);
            assertEquals("cloneToOriginalsMap must be size 1", 1, cloneToOriginalsMap.size());
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testClearWhileEntityManagerInFakeBirthState0() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = null;
        UnitOfWorkImpl  uow = null;
        Map cloneToOriginalsMap = null;
        Department dept = null;   
        try {
            em = emf.createEntityManager();
            // get the underlying uow
            uow = getUnitOfWorkFromEntityManager(em);
                        
            // force a get on the map to lazy initialize an empty map
            cloneToOriginalsMap = uow.getCloneToOriginals();
            // verify size 0
            // we don't have access to the protected function uow.hasCloneToOriginals();
            assertEquals("cloneToOriginalsMap must be size 0", 0, cloneToOriginalsMap.size());
            // Verify that cloneToOriginals is null and not lazy initialized
            dept = new Department();
            cloneToOriginalsMap.put(null, dept);
            // verify size 1
            assertEquals("cloneToOriginalsMap must be size 1", 1, cloneToOriginalsMap.size());
            
            // verify we are in birth state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);            

            // simulate a clear() call in the middle of a merge
            em.clear();

            // verify that the uow ignored the clear call
            int lifecycleAfter = uow.getLifecycle();
            assertEquals("Unchanged Birth state 0 is not set ", 0, lifecycleAfter);            
            // verify that a map previously set on the em was not cleared to null by the clear
            // verify size 0
            cloneToOriginalsMap = uow.getCloneToOriginals();
            assertNotNull("cloneToOriginals Map must not be null after a clear in Birth state", cloneToOriginalsMap);
            assertEquals("cloneToOriginalsMap must be not be cleared to size 0", 1, cloneToOriginalsMap.size());
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    public void testClearWhileEntityManagerInFakeAfterExternalTransactionRolledBackState6() {
        
    }
    
    /**
     * This test simulates EE container callbacks that could occur that affect em lifecycle state.
     * Specifically it tests whether we handle an attempt to clear an entityManager 
     * that is in the middle of a commit.
     * We only clear the entityManager if we are in the states 
     * (Birth == 0, WriteChangesFailed==3, Death==5 or AfterExternalTransactionRolledBack==6).
     * If we are in one of the following *Pending states we defer the clear() to the release() call later  
     */
    public void testClearWhileEntityManagerInCommitPendingStateWithClearAfterCommit() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        Department dept = null;   
        try {
            em.getTransaction().begin();
            dept = new Department();
            // A merge will not populate the @Id field
            // A persist will populate the @Id field
            em.persist(dept);
        
            // simulate an attempt to call close() while we are in the middle of a commit
            UnitOfWorkImpl uow = getUnitOfWorkFromEntityManager(em);

            // get lifecycle state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);            
            
            em.clear();
            int lifecycleAfter = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after a clear on state Birth  ", 0, lifecycleAfter);            
        
            em.getTransaction().commit();

            // clear em
            em.clear();
            
            int lifecycleAfterCommit = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after commit ", 0, lifecycleAfterCommit);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testClearWhileEntityManagerInCommitPendingStateWithNoClearAfterCommit() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        Department dept = null;   
        try {
            em.getTransaction().begin();
            dept = new Department();
            // A merge will not populate the @Id field and will result in a PK null exception in any find later
            // A persist will populate the @Id field
            em.persist(dept);
        
            // simulate an attempt to call close() while we are in the middle of a commit
            UnitOfWorkImpl uow = getUnitOfWorkFromEntityManager(em);

            // get lifecycle state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);            
            
            em.clear();
            int lifecycleAfter = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after a clear on state Birth  ", 0, lifecycleAfter);            
        
            em.getTransaction().commit();

            // don't clear em - leave following line commented
            //em.clear();
            
            int lifecycleAfterCommit = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after commit ", 0, lifecycleAfterCommit);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    // This clear should pass because the state is always 0 Begin except for inside the commit()
    public void testClearAfterEntityManagerCommitFinished() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        Department dept = null;   
        try {
            em.getTransaction().begin();
            dept = new Department();
            em.persist(dept);
        
            // simulate an attempt to call close() while we are in the middle of a commit
            UnitOfWorkImpl uow = getUnitOfWorkFromEntityManager(em);

            // get lifecycle state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);            
            
            em.clear();
            int lifecycleAfter = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after a clear on state Birth  ", 0, lifecycleAfter);            
        
            em.getTransaction().commit();
                
            em.clear();
            int lifecycleAfterCommit = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after commit ", 0, lifecycleAfterCommit);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
}
