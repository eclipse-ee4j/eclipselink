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

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.jpa.entityagent.model.AgentRaceRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static jakarta.persistence.sql.ResultSetMapping.entity;
import static org.junit.Assert.*;

@RunWith(EmfRunner.class)
public class TestEntityAgentReadLockMutations {
    @Emf(name = "agent-read-lock-mutations", createTables = DDLGen.DROP_CREATE, classes = AgentRaceRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Before @After
    public void cleanup() {
        emf.runInTransaction(manager -> manager.createStatement("delete from AgentRaceRecord").execute());
    }

    private AgentRaceRecord observed(long id) {
        try (EntityAgent observer = emf.createEntityAgent()) {
            return observer.find(AgentRaceRecord.class, id);
        }
    }

    @Test
    public void successfulDeleteRetiresLocksFromGetQueryRefreshAndNativeMapping() {
        for (String producer : new String[] {"get", "query", "refresh", "native"}) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.insert(new AgentRaceRecord(1, producer));
                agent.getTransaction().begin();
                AgentRaceRecord row = switch (producer) {
                    case "query" -> agent.createQuery("select r from AgentRaceRecord r where r.id=1", AgentRaceRecord.class)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
                    case "refresh" -> {
                        AgentRaceRecord result = agent.get(AgentRaceRecord.class, 1L);
                        agent.refresh(result, LockModeType.OPTIMISTIC);
                        yield result;
                    }
                    case "native" -> agent.createNativeQuery("SELECT * FROM EA_RACE_RECORD WHERE ID=1",
                            entity(AgentRaceRecord.class).withLockMode(LockModeType.OPTIMISTIC)).getSingleResult();
                    default -> agent.get(AgentRaceRecord.class, 1L, LockModeType.OPTIMISTIC);
                };
                agent.delete(row);
                assertNull(agent.find(AgentRaceRecord.class, 1L));
                agent.getTransaction().commit();
                assertNull("Committed own deletion through " + producer, observed(1));
            }
        }
    }

    private int bulk(EntityAgent agent, String producer, boolean delete) {
        if (producer.equals("criteria")) {
            var builder = agent.getCriteriaBuilder();
            if (delete) {
                var criteria = builder.createCriteriaDelete(AgentRaceRecord.class);
                var root = criteria.from(AgentRaceRecord.class);
                criteria.where(builder.equal(root.get("id"), 1L));
                return agent.createStatement(criteria).execute();
            }
            var criteria = builder.createCriteriaUpdate(AgentRaceRecord.class);
            var root = criteria.from(AgentRaceRecord.class);
            criteria.set(root.get("name"), "bulk");
            criteria.set(root.<Integer>get("version"), builder.sum(root.<Integer>get("version"), 1));
            criteria.where(builder.equal(root.get("id"), 1L));
            return agent.createStatement(criteria).execute();
        }
        if (producer.equals("native")) {
            return agent.createNativeQuery(delete ? "DELETE FROM EA_RACE_RECORD WHERE ID=1"
                    : "UPDATE EA_RACE_RECORD SET NAME='bulk', VERSION=VERSION+1 WHERE ID=1").executeUpdate();
        }
        String jpql = delete ? "delete from AgentRaceRecord r where r.id=1"
                : "update AgentRaceRecord r set r.name='bulk', r.version=r.version+1 where r.id=1";
        return producer.equals("statement") ? agent.createStatement(jpql).execute() : agent.createQuery(jpql).executeUpdate();
    }

    @Test
    public void successfulBulkDeletesRetireOriginalReadObligations() {
        for (String producer : new String[] {"statement", "jpql", "criteria", "native"}) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.insert(new AgentRaceRecord(1, producer));
                agent.getTransaction().begin();
                agent.get(AgentRaceRecord.class, 1L, LockModeType.OPTIMISTIC);
                assertEquals(1, bulk(agent, producer, true));
                agent.getTransaction().commit();
                assertNull(observed(1));
            }
        }
    }

    @Test
    public void successfulBulkVersionUpdatesAdvanceOriginalReadObligations() {
        for (String producer : new String[] {"statement", "jpql", "criteria", "native"}) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.insert(new AgentRaceRecord(1, producer));
                agent.getTransaction().begin();
                int version = agent.get(AgentRaceRecord.class, 1L, LockModeType.OPTIMISTIC).version;
                assertEquals(1, bulk(agent, producer, false));
                agent.getTransaction().commit();
                AgentRaceRecord row = observed(1);
                assertEquals("bulk", row.name);
                assertEquals(version + 1, row.version);
                agent.delete(row);
            }
        }
    }

    @Test
    public void callerRollbackUndoesOwnBulkDeletionAndVersionUpdate() {
        for (boolean delete : new boolean[] {true, false}) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.insert(new AgentRaceRecord(1, "before"));
                int version = observed(1).version;
                agent.getTransaction().begin();
                agent.get(AgentRaceRecord.class, 1L, LockModeType.OPTIMISTIC);
                assertEquals(1, bulk(agent, delete ? "statement" : "native", delete));
                agent.getTransaction().rollback();
                AgentRaceRecord row = observed(1);
                assertEquals("before", row.name);
                assertEquals(version, row.version);
                agent.delete(row);
            }
        }
    }

    @Test
    public void competingVersionIsRejectedBeforeAnyOwnBulkSqlExecutes() {
        for (String producer : new String[] {"statement", "jpql", "criteria", "native"}) {
            try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent()) {
                agent.insert(new AgentRaceRecord(1, "before"));
                agent.getTransaction().begin();
                agent.get(AgentRaceRecord.class, 1L, LockModeType.OPTIMISTIC);
                AgentRaceRecord changed = peer.get(AgentRaceRecord.class, 1L);
                changed.name = "peer";
                peer.update(changed);
                ClientSession client = agent.unwrap(ClientSession.class);
                AtomicInteger writes = new AtomicInteger();
                client.getEventManager().addListener(new SessionEventAdapter() {
                    @Override public void preExecuteCall(SessionEvent event) {
                        if (event.getSession() == client && event.getCall() instanceof DatabaseCall call
                                && call.getQuery().isModifyQuery()) writes.incrementAndGet();
                    }
                });
                assertThrows(OptimisticLockException.class, () -> bulk(agent, producer, false));
                assertEquals("Original version must be validated before DML", 0, writes.get());
                agent.getTransaction().rollback();
                assertEquals("peer", observed(1).name);
                agent.getTransaction().begin();
                assertEquals(1, bulk(agent, producer, false));
                assertTrue("The SQL witness must observe a successful DML call", writes.get() > 0);
                agent.getTransaction().rollback();
                assertEquals("peer", observed(1).name);
                peer.delete(changed);
            }
        }
    }

    @Test
    public void failedDeleteKeepsTheOriginalReadObligation() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent()) {
            agent.insert(new AgentRaceRecord(1, "before"));
            agent.getTransaction().begin();
            AgentRaceRecord row = agent.get(AgentRaceRecord.class, 1L, LockModeType.OPTIMISTIC);
            ClientSession client = agent.unwrap(ClientSession.class);
            IllegalStateException expected = new IllegalStateException("injected failure before DELETE SQL");
            SessionEventAdapter failure = new SessionEventAdapter() {
                @Override public void preExecuteQuery(SessionEvent event) {
                    if (event.getSession() == client && event.getQuery() instanceof DeleteObjectQuery) throw expected;
                }
            };
            client.getEventManager().addListener(failure);
            try {
                assertSame(expected, assertThrows(IllegalStateException.class, () -> agent.delete(row)));
            } finally {
                client.getEventManager().removeListener(failure);
            }
            assertTrue(agent.getTransaction().isActive());
            AgentRaceRecord changed = peer.get(AgentRaceRecord.class, 1L);
            changed.name = "peer after failed delete";
            peer.update(changed);
            RollbackException rejected = assertThrows(RollbackException.class, agent.getTransaction()::commit);
            assertTrue(rejected.getCause() instanceof OptimisticLockException);
            assertEquals("peer after failed delete", observed(1).name);
        }
    }

    @Test
    public void ownUpdatesAndForceIncrementsRemainValidAtCommit() {
        for (LockModeType mode : new LockModeType[] {LockModeType.OPTIMISTIC,
                LockModeType.OPTIMISTIC_FORCE_INCREMENT, LockModeType.PESSIMISTIC_FORCE_INCREMENT}) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.insert(new AgentRaceRecord(1, "before"));
                int initial = observed(1).version;
                agent.getTransaction().begin();
                AgentRaceRecord row = agent.get(AgentRaceRecord.class, 1L, mode);
                row.name = "own update";
                agent.update(row);
                agent.getTransaction().commit();
                AgentRaceRecord result = observed(1);
                assertEquals("own update", result.name);
                assertEquals(row.version, result.version);
                assertTrue(result.version > initial);
                agent.delete(result);
            }
        }
    }
}
