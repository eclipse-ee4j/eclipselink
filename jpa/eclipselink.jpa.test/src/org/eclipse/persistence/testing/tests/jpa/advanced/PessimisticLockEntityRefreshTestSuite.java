/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;

/**
 * <p>
 * <b>Purpose</b>: Test Pessimistic Locking Entity Refresh functionality.
 * <p>
 * <b>Description</b>: Test entity refresh functionality under different locking situations.<br>
 * Implemented for EclipseLink bug: 409433
 */
public class PessimisticLockEntityRefreshTestSuite extends JUnitTestCase {

    public PessimisticLockEntityRefreshTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("PessimisticLocking Entity Refresh TestSuite");
        if (!isJPA10()) {
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testSetup"));

            // test lock-on-find an object multiple times with the same lock type
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticWriteLockFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticReadLockFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementLockFind"));

            // test lock-on-find  an object then find it again with no lock type
            // no refresh is expected on a subsequent normal (no lock) find
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticReadLockAndNormalFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticWriteLockAndNormalFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementLockAndNormalFind"));

            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticReadThenWriteLockFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticWriteThenReadLockFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementThenReadLockFind"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementThenWriteLockFind"));

            // test finding and locking an object multiple times with the same lock type
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticWriteLockFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticReadLockFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementLockFindAndLock"));

            // test finding and lock an object then find it again with no lock type
            // no refresh is ever expected on a subsequent normal (no lock) find
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticReadLockAndNormalFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticWriteLockAndNormalFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementLockAndNormalFindAndLock"));

            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticReadThenWriteLockFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticWriteThenReadLockFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementThenReadLockFindAndLock"));
            suite.addTest(new PessimisticLockEntityRefreshTestSuite("testPessimisticForceIncrementThenWriteLockFindAndLock"));
        }
        return suite;
    }

    public void testSetup() {
        ServerSession session = JUnitTestCase.getServerSession();
        new AdvancedTableCreator().replaceTables(session);
        clearCache();
    }

    public void testPessimisticReadLockAndNormalFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_READ, LockModeType.NONE);
    }

    public void testPessimisticWriteLockAndNormalFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.NONE);
    }

    public void testPessimisticForceIncrementLockAndNormalFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.NONE);
    }

    public void testPessimisticReadLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_READ);
    }

    public void testPessimisticWriteLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_WRITE);
    }

    public void testPessimisticForceIncrementLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
    }

    public void testPessimisticReadThenWriteLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_WRITE);
    }

    public void testPessimisticWriteThenReadLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_READ);
    }

    public void testPessimisticForceIncrementThenReadLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_READ);
    }

    public void testPessimisticForceIncrementThenWriteLockFind() {
        doFindWithLockModes(LockModeType.PESSIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_WRITE);
    }

    // Find-Lock tests

    public void testPessimisticReadLockAndNormalFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_READ, LockModeType.NONE);
    }

    public void testPessimisticWriteLockAndNormalFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.NONE);
    }

    public void testPessimisticForceIncrementLockAndNormalFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.NONE);
    }

    public void testPessimisticReadLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_READ);
    }

    public void testPessimisticWriteLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_WRITE);
    }

    public void testPessimisticForceIncrementLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
    }

    public void testPessimisticReadThenWriteLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_WRITE);
    }

    public void testPessimisticWriteThenReadLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_READ);
    }

    public void testPessimisticForceIncrementThenReadLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_READ);
    }

    public void testPessimisticForceIncrementThenWriteLockFindAndLock() {
        doFindAndLockWithLockModes(LockModeType.PESSIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_WRITE);
    }

    /**
     * Test utility method
     * @param lockMode1 - the lock mode for the first find()
     * @param lockMode2 - the lock mode for the second find()
     */
    public void doFindWithLockModes(LockModeType lockMode1, LockModeType lockMode2) {
        if (!isSelectForUpateSupported()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Address entity = new Address("Wellington St.", "Ottawa", "ON", "Canada", "K1A0A6");
            em.persist(entity);
            em.flush();
            int entityId = entity.getID();

            entity = em.find(Address.class, entityId, lockMode1);
            assertNotNull("Test data: entity should not be null", entity);
            entity.setCountry("changed");

            Address entityRetrievedAgain = em.find(Address.class, entityId, lockMode2);

            assertNotNull("Test data: entity retrieved again should not be null", entityRetrievedAgain);
            assertTrue("Same entity instance should be returned from find", entity == entityRetrievedAgain);
            assertEquals("Field value is expected to be the same", "changed", entityRetrievedAgain.getCountry());
            assertEquals("Version field is expected to be the same", entity.getVersion(), entityRetrievedAgain.getVersion());
        } catch (RuntimeException ex) {
            throw ex;
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test utility method
     * @param lockMode1 - the lock mode for the first lock()
     * @param lockMode2 - the lock mode for the second lock()
     */
    public void doFindAndLockWithLockModes(LockModeType lockMode1, LockModeType lockMode2) {
        if (!isSelectForUpateSupported()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Address entity = new Address("Wellington St.", "Ottawa", "ON", "Canada", "K1A0A6");
            em.persist(entity);
            em.flush();
            int entityId = entity.getID();

            entity = em.find(Address.class, entityId);
            em.lock(entity, lockMode1);

            assertNotNull("Test data: entity should not be null", entity);
            entity.setCountry("changed");

            Address entityRetrievedAgain = em.find(Address.class, entityId);
            em.lock(entityRetrievedAgain, lockMode2);

            assertNotNull("Test data: entity retrieved again should not be null", entityRetrievedAgain);
            assertTrue("Same entity instance should be returned from find", entity == entityRetrievedAgain);
            assertEquals("Field value is expected to be the same", "changed", entityRetrievedAgain.getCountry());
            assertEquals("Version field is expected to be the same", entity.getVersion(), entityRetrievedAgain.getVersion());
        } catch (RuntimeException ex) {
            throw ex;
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

}
