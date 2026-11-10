/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.advanced.concurrency;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;

import java.util.Map;

/**
 *  Verifies how {@code EntityManager.clear()} treats the lifecycle state of the entity manager's own unit
 *  of work ({@link UnitOfWorkImpl#getLifecycle()}).
 *  <p>
 *  A clear is deferred to the later {@code release()} only while the unit of work is synchronized with a
 *  transaction (Jakarta Transactions) and in one of the *Pending states: CommitPending (1),
 *  CommitTransactionPending (2) or MergePending (4). This is the afterCompletion callback of bug 259993, in
 *  which a container clears the entity manager while the transaction is still being completed. In every
 *  other case - Birth (0), or any state of a unit of work that is not synchronized - the unit of work is
 *  cleared and released (Death, 5), and the entity manager acquires a new one for what follows.
 *  <p>
 *  The tests force these states through internal API, so they are tightly coupled to the implementation:
 *  <ul>
 *     <li>{@code EntityManagerImpl.clear()}, which decides between deferring and clearing</li>
 *     <li>{@link UnitOfWorkImpl#setSynchronized(boolean)} and {@link UnitOfWorkImpl#setPendingMerge()}</li>
 *     <li>{@link UnitOfWorkImpl#getCloneToOriginals()}</li>
 *  </ul>
 *  They run both in Java SE and on the server.
 *  <p>
 *  History:
 *  <ul>
 *     <li>02/10/2009-1.1 Michael O'Brien - 259993: defer a clear() call to release() if the unit of work
 *         lifecycle is 1, 2 or 4 (*Pending)</li>
 *     <li>06/15/2010-2.1 - 316531: the deferral only applies to a synchronized unit of work</li>
 *     <li>09/24/2010-2.1 Michael O'Brien - 326097: assertion failures were ignored by a catch block;
 *         refactored to call clear() on the entity manager instead of clearForClose() on the unit of
 *         work</li>
 *     <li>2026 - the tests looked at a nested unit of work acquired from the entity manager's, which
 *         clear() and commit never touch, so their assertions held whatever happened; they now look at the
 *         entity manager's own unit of work, with expectations brought up to date with 316531</li>
 *  </ul>
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

    // The entity manager's own unit of work. Not getActiveSession().acquireUnitOfWork(): called on a unit
    // of work, acquireUnitOfWork() creates a new nested one, which em.clear() and the commit never touch,
    // so the lifecycle assertions below held whatever the entity manager did.
    private UnitOfWorkImpl getUnitOfWorkFromEntityManager(EntityManager em) {
        return (UnitOfWorkImpl) ((JpaEntityManager) em).getUnitOfWork();
    }

    public void testSetup() {
        clearCache();
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
    }

    @Override
    public void finalize() {
    }

    // This test is a pure unit test that directly sets and tries to clear the uow state
    // There are no actual entities managed in this example
    @SuppressWarnings({"unchecked"})
    public void testClearWhileEntityManagerInFakeMergePendingState4() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = null;
        UnitOfWorkImpl  uow = null;
        Map<Object, Object> cloneToOriginalsMap = null;
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
            cloneToOriginalsMap.put(dept, dept);
            // verify size 1
            assertEquals("cloneToOriginalsMap must be size 1", 1, cloneToOriginalsMap.size());

            // verify we are in birth state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);
            // setup the uow in a simulated state: synchronized with a transaction and merge pending, as during
            // the afterCompletion callback of bug 259993. Since bug 316531 the clear is only deferred for a
            // synchronized unit of work; an unsynchronized one is cleared and released whatever its state.
            uow.setSynchronized(true);
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
            // verify that the deferred clear left the map previously set on the uow as it was
            assertNotNull("cloneToOriginals Map must not be null after a clear in *Pending state", cloneToOriginalsMap);
            assertEquals("cloneToOriginalsMap must be size 1", 1, cloneToOriginalsMap.size());
            // not synchronized with anything really: undo, as it marks the parent session too
            uow.setSynchronized(false);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void testClearWhileEntityManagerInFakeBirthState0() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = null;
        UnitOfWorkImpl  uow = null;
        Map<Object, Object> cloneToOriginalsMap = null;
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
            cloneToOriginalsMap.put(dept, dept);
            // verify size 1
            assertEquals("cloneToOriginalsMap must be size 1", 1, cloneToOriginalsMap.size());

            // verify we are in birth state
            int lifecycleBefore = uow.getLifecycle();
            assertEquals("Birth state 0 is not set ", 0, lifecycleBefore);

            // simulate a clear() call in the middle of a merge
            em.clear();

            // Birth is not a *Pending state, so the clear is not deferred: the unit of work is cleared and
            // released. (This used to assert the opposite, which held only for the nested unit of work the
            // test looked at before.)
            int lifecycleAfter = uow.getLifecycle();
            assertEquals("Unit of work in Birth state 0 must be released (Death 5) by a clear()", 5, lifecycleAfter);
            // the clear drops the unit of work's map rather than emptying it, so ask for it again
            assertEquals("cloneToOriginalsMap must be cleared by a clear() in Birth state", 0, uow.getCloneToOriginals().size());
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
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
            beginTransaction(em);
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

            commitTransaction(em);

            // clear em
            em.clear();

            // the clear after the commit released the unit of work used in it (Death, 5); the entity

            // manager has a new one for what follows

            int lifecycleAfterCommit = getUnitOfWorkFromEntityManager(em).getLifecycle();
            assertEquals("Birth state 0 is not set after commit ", 0, lifecycleAfterCommit);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
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
            beginTransaction(em);
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

            commitTransaction(em);

            // don't clear em - leave following line commented
            //em.clear();

            int lifecycleAfterCommit = uow.getLifecycle();
            assertEquals("Birth state 0 is not set after commit ", 0, lifecycleAfterCommit);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
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
            beginTransaction(em);
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

            commitTransaction(em);

            em.clear();
            // the clear after the commit released the unit of work used in it (Death, 5); the entity
            // manager has a new one for what follows
            int lifecycleAfterCommit = getUnitOfWorkFromEntityManager(em).getLifecycle();
            assertEquals("Birth state 0 is not set after commit ", 0, lifecycleAfterCommit);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
}
