/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.basic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TransactionRequiredException;

import org.eclipse.persistence.jpa.entityagent.model.AgentJtaRecord;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityManagerGetLockMode {
    @Emf(name = "get-lock-mode", classes = { AgentJtaRecord.class }, properties = {
            @Property(name = "eclipselink.cache.shared.default", value = "false") })
    private EntityManagerFactory emf;

    private EntityManager em;

    @Before
    public void setUp() {
        assertNotNull("The resource-local factory must be injected", emf);
        try (EntityManager seed = emf.createEntityManager()) {
            seed.getTransaction().begin();
            seed.createQuery("delete from AgentJtaRecord").executeUpdate();
            seed.persist(new AgentJtaRecord(1, "managed"));
            seed.getTransaction().commit();
        }
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void managedEntityBeforeTransactionHasNoLock() {
        AgentJtaRecord record = managedRecord();
        assertFalse(em.getTransaction().isActive());
        assertEquals(LockModeType.NONE, em.getLockMode(record));
        assertFalse(em.getTransaction().isActive());
    }

    @Test
    public void optimisticCommitClearsLockMode() {
        assertNoLockAfterCommit(LockModeType.OPTIMISTIC);
    }

    @Test
    public void pessimisticCommitClearsLockMode() {
        assertNoLockAfterCommit(LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    public void managedEntityAfterRollbackHasNoLock() {
        em.getTransaction().begin();
        AgentJtaRecord record = managedRecord();
        em.lock(record, LockModeType.PESSIMISTIC_WRITE);
        em.getTransaction().rollback();
        record = managedRecord();
        assertEquals(LockModeType.NONE, em.getLockMode(record));
    }

    @Test
    public void unmanagedArgumentsWithoutTransactionAreRejected() {
        AgentJtaRecord detached = managedRecord();
        em.detach(detached);
        assertFalse(em.contains(detached));
        for (Object argument : new Object[] {new AgentJtaRecord(2, "new"), detached, null, new Object()}) {
            assertThrows(IllegalArgumentException.class, () -> em.getLockMode(argument));
        }
        assertFalse(em.getTransaction().isActive());
    }

    @Test
    public void unmanagedArgumentsWithTransactionAreRejected() {
        AgentJtaRecord detached = managedRecord();
        em.detach(detached);
        assertFalse(em.contains(detached));
        for (Object argument : new Object[] {new AgentJtaRecord(2, "new"), detached, null, new Object()}) {
            em.getTransaction().begin();
            try {
                assertThrows(IllegalArgumentException.class, () -> em.getLockMode(argument));
            } finally {
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    public void removedEntityWithTransactionIsRejected() {
        em.getTransaction().begin();
        AgentJtaRecord record = managedRecord();
        em.remove(record);
        assertFalse(em.contains(record));
        assertThrows(IllegalArgumentException.class, () -> em.getLockMode(record));
    }

    @Test
    public void removedEntityAfterCommitIsRejected() {
        em.getTransaction().begin();
        AgentJtaRecord record = managedRecord();
        em.remove(record);
        em.getTransaction().commit();
        assertFalse(em.contains(record));
        assertThrows(IllegalArgumentException.class, () -> em.getLockMode(record));
    }

    @Test
    public void closedEntityManagerIsRejected() {
        AgentJtaRecord record = managedRecord();
        em.close();
        assertThrows(IllegalStateException.class, () -> em.getLockMode(record));
    }

    @Test
    public void activeTransactionRetainsLockModes() {
        for (LockModeType mode : new LockModeType[] {LockModeType.NONE, LockModeType.OPTIMISTIC,
                LockModeType.OPTIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_READ,
                LockModeType.PESSIMISTIC_WRITE, LockModeType.PESSIMISTIC_FORCE_INCREMENT}) {
            em.getTransaction().begin();
            try {
                AgentJtaRecord record = managedRecord();
                em.lock(record, mode);
                LockModeType actual = em.getLockMode(record);
                if (mode == LockModeType.PESSIMISTIC_READ) {
                    assertTrue(actual == LockModeType.PESSIMISTIC_READ || actual == LockModeType.PESSIMISTIC_WRITE);
                } else {
                    assertEquals(mode, actual);
                }
            } finally {
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    public void lockingOperationsStillRequireTransaction() {
        AgentJtaRecord record = managedRecord();
        assertThrows(TransactionRequiredException.class,
                () -> em.find(AgentJtaRecord.class, 1L, LockModeType.OPTIMISTIC));
        assertThrows(TransactionRequiredException.class, () -> em.lock(record, LockModeType.OPTIMISTIC));
        assertThrows(TransactionRequiredException.class, () -> em.refresh(record, LockModeType.OPTIMISTIC));
    }

    private AgentJtaRecord managedRecord() {
        AgentJtaRecord record = em.find(AgentJtaRecord.class, 1L);
        assertNotNull(record);
        assertTrue(em.contains(record));
        return record;
    }

    private void assertNoLockAfterCommit(LockModeType mode) {
        em.getTransaction().begin();
        AgentJtaRecord record = managedRecord();
        em.lock(record, mode);
        assertEquals(mode, em.getLockMode(record));
        em.getTransaction().commit();
        assertTrue(em.contains(record));
        assertEquals(LockModeType.NONE, em.getLockMode(record));
    }
}
