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
package org.eclipse.persistence.jpa.entityagent;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.Transaction;

import org.eclipse.persistence.jpa.entityagent.model.AgentJtaRecord;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Runs against an owned H2 database and the real standalone Narayana transaction manager. */
@RunWith(EmfRunner.class)
public class TestEntityAgentJta {
    private AgentJtaFixture fixture;

    @Before
    public void createFixture() throws Exception {
        fixture = new AgentJtaFixture();
        assertTrue(fixture.transactions.getClass().getName().startsWith("com.arjuna."));
    }

    @After
    public void closeFixture() throws Exception {
        if (fixture != null) {
            fixture.close();
            assertEquals(0, fixture.activeEnlistments.get());
            assertEquals(fixture.physicalOpened.get(), fixture.physicalClosed.get());
            assertEquals(0, fixture.illegalLocalTransactionCalls.get());
            assertEquals(Status.STATUS_NO_TRANSACTION, fixture.transactions.getStatus());
        }
    }

    @AfterClass
    public static void closeObjectStore() throws Exception {
        AgentJtaFixture.shutdownObjectStore();
    }

    @Test
    public void noCallerTransactionPermitsImmediateWriteAndRejectsResourceLocalTransaction() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            assertThrows(IllegalStateException.class, agent::getTransaction);
            agent.insert(new AgentJtaRecord(1, "immediate"));
            assertEquals(Status.STATUS_NO_TRANSACTION, fixture.transactions.getStatus());
            assertEquals("immediate", fixture.observedName(1));
            agent.delete(agent.get(AgentJtaRecord.class, 1L));
            assertNull(fixture.observedName(1));
        }
    }

    @Test
    public void callerCommitUsesEnlistedConnectionAndMakesWritesVisible() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            fixture.transactions.begin();
            Transaction caller = fixture.transactions.getTransaction();
            agent.insert(new AgentJtaRecord(2, "committed"));
            assertEquals("committed", agent.get(AgentJtaRecord.class, 2L).name);
            assertNull(fixture.observedName(2));
            Connection first = agent.<Connection, Connection>callWithConnection(connection -> connection);
            assertSame(first, agent.<Connection, Connection>callWithConnection(connection -> connection));
            assertSame(caller, fixture.transactions.getTransaction());
            assertTrue(fixture.activeEnlistments.get() > 0);
            fixture.transactions.commit();
            assertEquals("committed", fixture.observedName(2));
            assertEquals(0, fixture.activeEnlistments.get());
            assertTrue(fixture.synchronizationCompletions.get() > 0);
        }
    }

    @Test
    public void callerRollbackUndoesRealXaWritesAndAgentCanBeReused() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            fixture.transactions.begin();
            agent.insert(new AgentJtaRecord(3, "rolled-back"));
            assertEquals("rolled-back", agent.get(AgentJtaRecord.class, 3L).name);
            fixture.transactions.rollback();
            assertNull(fixture.observedName(3));
            assertEquals(0, fixture.activeEnlistments.get());
            fixture.transactions.begin();
            agent.insert(new AgentJtaRecord(3, "reused"));
            fixture.transactions.commit();
            assertEquals("reused", fixture.observedName(3));
        }
    }

    @Test
    public void closeBeforeCallerCommitPreservesTransactionAndXaResource() throws Exception {
        fixture.transactions.begin();
        Transaction caller = fixture.transactions.getTransaction();
        EntityAgent agent = fixture.factory.createEntityAgent();
        agent.insert(new AgentJtaRecord(4, "after-close"));
        agent.close();
        assertFalse(agent.isOpen());
        assertSame(caller, fixture.transactions.getTransaction());
        assertEquals(Status.STATUS_ACTIVE, caller.getStatus());
        assertTrue(fixture.activeEnlistments.get() > 0);
        assertNull(fixture.observedName(4));
        fixture.transactions.commit();
        assertEquals("after-close", fixture.observedName(4));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void closeBeforeCallerRollbackDoesNotCommit() throws Exception {
        fixture.transactions.begin();
        Transaction caller = fixture.transactions.getTransaction();
        EntityAgent agent = fixture.factory.createEntityAgent();
        agent.insert(new AgentJtaRecord(5, "discard"));
        agent.close();
        assertSame(caller, fixture.transactions.getTransaction());
        assertEquals(Status.STATUS_ACTIVE, caller.getStatus());
        fixture.transactions.rollback();
        assertNull(fixture.observedName(5));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void typedHelperOwnsNewTransactionAndClosesHandler() throws Exception {
        EntityAgent[] escaped = new EntityAgent[1];
        String result = fixture.factory.callInTransaction(EntityAgent.class, agent -> {
            escaped[0] = agent;
            agent.insert(new AgentJtaRecord(6, "helper-owned"));
            return agent.get(AgentJtaRecord.class, 6L).name;
        });
        assertEquals("helper-owned", result);
        assertFalse(escaped[0].isOpen());
        assertEquals(Status.STATUS_NO_TRANSACTION, fixture.transactions.getStatus());
        assertEquals("helper-owned", fixture.observedName(6));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void typedHelperBorrowsCallerTransactionWithoutCompletingIt() throws Exception {
        fixture.transactions.begin();
        Transaction caller = fixture.transactions.getTransaction();
        EntityAgent[] escaped = new EntityAgent[1];
        fixture.factory.runInTransaction(EntityAgent.class, agent -> {
            escaped[0] = agent;
            agent.insert(new AgentJtaRecord(7, "helper-borrowed"));
        });
        assertFalse(escaped[0].isOpen());
        assertSame(caller, fixture.transactions.getTransaction());
        assertEquals(Status.STATUS_ACTIVE, caller.getStatus());
        assertNull(fixture.observedName(7));
        fixture.transactions.commit();
        assertEquals("helper-borrowed", fixture.observedName(7));
    }

    @Test
    public void ownedHelperFailureRollsBackAndPreservesOriginalFailure() throws Exception {
        IllegalArgumentException expected = new IllegalArgumentException("owned helper failure");
        EntityAgent[] escaped = new EntityAgent[1];
        assertSame(expected, assertThrows(IllegalArgumentException.class,
                () -> fixture.factory.runInTransaction(EntityAgent.class, agent -> {
                    escaped[0] = agent;
                    agent.insert(new AgentJtaRecord(8, "discard"));
                    throw expected;
                })));
        assertFalse(escaped[0].isOpen());
        assertEquals(Status.STATUS_NO_TRANSACTION, fixture.transactions.getStatus());
        assertNull(fixture.observedName(8));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void borrowedHelperFailureMarksCallerForRollbackWithoutCompletingIt() throws Exception {
        fixture.transactions.begin();
        Transaction caller = fixture.transactions.getTransaction();
        IllegalArgumentException expected = new IllegalArgumentException("borrowed helper failure");
        assertSame(expected, assertThrows(IllegalArgumentException.class,
                () -> fixture.factory.callInTransaction(EntityAgent.class, agent -> {
                    agent.insert(new AgentJtaRecord(9, "discard"));
                    throw expected;
                })));
        assertSame(caller, fixture.transactions.getTransaction());
        assertEquals(Status.STATUS_MARKED_ROLLBACK, caller.getStatus());
        fixture.transactions.rollback();
        assertNull(fixture.observedName(9));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void ownedHelperErrorRollsBackAndPreservesOriginalError() throws Exception {
        AssertionError expected = new AssertionError("owned helper error");
        assertSame(expected, assertThrows(AssertionError.class,
                () -> fixture.factory.callInTransaction(EntityAgent.class, agent -> {
                    agent.insert(new AgentJtaRecord(14, "discard"));
                    throw expected;
                })));
        assertEquals(Status.STATUS_NO_TRANSACTION, fixture.transactions.getStatus());
        assertNull(fixture.observedName(14));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void borrowedHelperErrorMarksCallerWithoutCompletingIt() throws Exception {
        fixture.transactions.begin();
        Transaction caller = fixture.transactions.getTransaction();
        AssertionError expected = new AssertionError("borrowed helper error");
        assertSame(expected, assertThrows(AssertionError.class,
                () -> fixture.factory.runInTransaction(EntityAgent.class, agent -> {
                    agent.insert(new AgentJtaRecord(15, "discard"));
                    throw expected;
                })));
        assertSame(caller, fixture.transactions.getTransaction());
        assertEquals(Status.STATUS_MARKED_ROLLBACK, caller.getStatus());
        fixture.transactions.rollback();
        assertNull(fixture.observedName(15));
        assertEquals(0, fixture.activeEnlistments.get());
    }

    @Test
    public void connectionCallbackFailureMarksOnlyCallerTransaction() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            fixture.transactions.begin();
            Transaction caller = fixture.transactions.getTransaction();
            agent.insert(new AgentJtaRecord(10, "discard"));
            IllegalStateException expected = new IllegalStateException("connection callback failure");
            assertSame(expected, assertThrows(IllegalStateException.class,
                    () -> agent.runWithConnection(connection -> { throw expected; })));
            assertSame(caller, fixture.transactions.getTransaction());
            assertEquals(Status.STATUS_MARKED_ROLLBACK, caller.getStatus());
            fixture.transactions.rollback();
            assertNull(fixture.observedName(10));
            assertEquals("usable", agent.callWithConnection(connection -> "usable"));
        }
    }

    @Test
    public void synchronizationFailureRollsBackAndReleasesPhysicalResources() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            fixture.transactions.begin();
            agent.insert(new AgentJtaRecord(11, "discard"));
            AtomicInteger completedStatus = new AtomicInteger(-1);
            fixture.transactions.getTransaction().registerSynchronization(new Synchronization() {
                @Override
                public void beforeCompletion() { throw new IllegalStateException("synchronization failure"); }
                @Override
                public void afterCompletion(int status) { completedStatus.set(status); }
            });
            assertThrows(RollbackException.class, fixture.transactions::commit);
            assertEquals(Status.STATUS_ROLLEDBACK, completedStatus.get());
            assertNull(fixture.observedName(11));
            assertEquals(0, fixture.activeEnlistments.get());
            fixture.transactions.begin();
            agent.insert(new AgentJtaRecord(11, "reused-after-synchronization-failure"));
            fixture.transactions.commit();
            assertNotNull(fixture.observedName(11));
        }
    }

    @Test
    public void callerTransactionSupportsLocksAndMultipleAgents() throws Exception {
        try (EntityAgent first = fixture.factory.createEntityAgent(); EntityAgent second = fixture.factory.createEntityAgent()) {
            first.insert(new AgentJtaRecord(12, "locked"));
            assertThrows(TransactionRequiredException.class,
                    () -> first.get(AgentJtaRecord.class, 12L, LockModeType.PESSIMISTIC_WRITE));
            fixture.transactions.begin();
            Transaction caller = fixture.transactions.getTransaction();
            AgentJtaRecord record = first.get(AgentJtaRecord.class, 12L, LockModeType.PESSIMISTIC_WRITE);
            record.name = "changed";
            first.update(record);
            second.insert(new AgentJtaRecord(13, "second-agent"));
            assertSame(caller, fixture.transactions.getTransaction());
            fixture.transactions.commit();
            assertEquals("changed", fixture.observedName(12));
            assertEquals("second-agent", fixture.observedName(13));
            assertEquals(0, fixture.activeEnlistments.get());
        }
    }

    @Test
    public void callerCommitAcceptsOwnDeletesAfterGetQueryAndRefreshLocks() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            for (String producer : new String[] {"get", "query", "refresh"}) {
                agent.insert(new AgentJtaRecord(20, producer));
                fixture.transactions.begin();
                Transaction caller = fixture.transactions.getTransaction();
                AgentJtaRecord row = switch (producer) {
                    case "query" -> agent.createQuery("select r from AgentJtaRecord r where r.id=20", AgentJtaRecord.class)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
                    case "refresh" -> {
                        AgentJtaRecord result = agent.get(AgentJtaRecord.class, 20L);
                        agent.refresh(result, LockModeType.OPTIMISTIC);
                        yield result;
                    }
                    default -> agent.get(AgentJtaRecord.class, 20L, LockModeType.OPTIMISTIC);
                };
                agent.delete(row);
                assertSame(caller, fixture.transactions.getTransaction());
                assertNull(agent.find(AgentJtaRecord.class, 20L));
                fixture.transactions.commit();
                assertNull(fixture.observedName(20));
                assertEquals(0, fixture.activeEnlistments.get());
            }
        }
    }

    @Test
    public void callerCompletionRetiresBulkDeletesAndRebasesBulkVersionUpdates() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent()) {
            agent.insert(new AgentJtaRecord(21, "before"));
            fixture.transactions.begin();
            Transaction caller = fixture.transactions.getTransaction();
            agent.get(AgentJtaRecord.class, 21L, LockModeType.OPTIMISTIC);
            assertEquals(1, agent.createStatement("delete from AgentJtaRecord r where r.id=21").execute());
            assertSame(caller, fixture.transactions.getTransaction());
            fixture.transactions.commit();
            assertNull(fixture.observedName(21));

            agent.insert(new AgentJtaRecord(21, "before"));
            for (boolean commit : new boolean[] {false, true}) {
                fixture.transactions.begin();
                AgentJtaRecord before = agent.get(AgentJtaRecord.class, 21L, LockModeType.OPTIMISTIC);
                assertEquals(1, agent.createNativeQuery("UPDATE EA_JTA_RECORD SET NAME='bulk', VERSION=VERSION+1 WHERE ID=21")
                        .executeUpdate());
                if (commit) fixture.transactions.commit();
                else fixture.transactions.rollback();
                AgentJtaRecord after = agent.get(AgentJtaRecord.class, 21L);
                assertEquals(commit ? "bulk" : "before", after.name);
                assertEquals(before.version + (commit ? 1 : 0), after.version.intValue());
                assertEquals(0, fixture.activeEnlistments.get());
            }
        }
    }

    @Test
    public void competingVersionPreventsCallerBulkWriteAndCallerCommit() throws Exception {
        try (EntityAgent agent = fixture.factory.createEntityAgent(); EntityAgent peer = fixture.factory.createEntityAgent()) {
            agent.insert(new AgentJtaRecord(22, "before"));
            fixture.transactions.begin();
            Transaction caller = fixture.transactions.getTransaction();
            agent.get(AgentJtaRecord.class, 22L, LockModeType.OPTIMISTIC);
            assertSame(caller, fixture.transactions.suspend());
            try {
                AgentJtaRecord changed = peer.get(AgentJtaRecord.class, 22L);
                changed.name = "peer";
                peer.update(changed);
            } finally {
                fixture.transactions.resume(caller);
            }
            assertThrows(OptimisticLockException.class, () -> agent.createStatement(
                    "update AgentJtaRecord r set r.name='own bulk', r.version=r.version+1 where r.id=22").execute());
            assertSame(caller, fixture.transactions.getTransaction());
            assertEquals("peer", agent.get(AgentJtaRecord.class, 22L).name);
            assertThrows(RollbackException.class, fixture.transactions::commit);
            assertEquals("peer", fixture.observedName(22));
            assertEquals(0, fixture.activeEnlistments.get());
        }
    }
}
