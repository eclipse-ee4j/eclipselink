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

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.sql.Connection;

import org.eclipse.persistence.jpa.entityagent.model.AgentCacheRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentCache {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = AgentCacheRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    private void seed(long id) {
        emf.runInTransaction(em -> em.persist(new AgentCacheRecord(id, "stored")));
        assertEquals("loaded:stored", managedLabel(id));
        assertTrue(emf.getCache().contains(AgentCacheRecord.class, id));
    }

    private String managedLabel(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(AgentCacheRecord.class, id).label;
        }
    }

    private void databaseLabel(EntityAgent agent, long id, String label) {
        agent.<Connection>runWithConnection(connection -> {
            try (var statement = connection.prepareStatement("UPDATE EA_CACHE_RECORD SET LABEL = ? WHERE ID = ?")) {
                statement.setString(1, label);
                statement.setLong(2, id);
                assertEquals(1, statement.executeUpdate());
            }
        });
    }

    @Test
    public void refreshPopulatesRawCacheAndDoesNotPublishCallbackMutation() {
        seed(1001);
        try (EntityAgent agent = emf.createEntityAgent()) {
            databaseLabel(agent, 1001, "fresh");
            AgentCacheRecord first = agent.get(AgentCacheRecord.class, 1001L, CacheStoreMode.REFRESH);
            assertEquals("loaded:fresh", first.label);
            assertEquals(1, first.loads);
            first.label = "caller mutation";
            AgentCacheRecord second = agent.get(AgentCacheRecord.class, 1001L);
            assertNotSame(first, second);
            assertEquals("loaded:fresh", second.label);
            assertEquals("loaded:fresh", managedLabel(1001));
        }
    }

    @Test
    public void bypassDoesNotRefreshExistingSharedEntry() {
        seed(1002);
        try (EntityAgent agent = emf.createEntityAgent(CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS)) {
            databaseLabel(agent, 1002, "fresh");
            assertEquals("loaded:fresh", agent.get(AgentCacheRecord.class, 1002L).label);
            assertEquals("loaded:stored", managedLabel(1002));
        }
    }

    @Test
    public void rolledBackRefreshNeverPublishesUncommittedState() {
        seed(1003);
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            AgentCacheRecord record = agent.get(AgentCacheRecord.class, 1003L);
            record.label = "uncommitted";
            agent.update(record);
            assertEquals("loaded:uncommitted", agent.get(AgentCacheRecord.class, 1003L, CacheStoreMode.REFRESH).label);
            assertEquals("loaded:stored", managedLabel(1003));
            agent.getTransaction().rollback();
            assertEquals("loaded:stored", managedLabel(1003));
            assertEquals("loaded:stored", agent.get(AgentCacheRecord.class, 1003L).label);
        }
    }

    @Test
    public void committedEntityWritesInvalidateOnlyAfterCommit() {
        seed(1004);
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            AgentCacheRecord record = agent.get(AgentCacheRecord.class, 1004L);
            record.label = "committed";
            agent.update(record);
            assertEquals("loaded:stored", managedLabel(1004));
            agent.getTransaction().commit();
            assertEquals("loaded:committed", managedLabel(1004));
            record.label = "immediate";
            agent.update(record);
            assertEquals("loaded:immediate", managedLabel(1004));
        }
    }

    @Test
    public void bulkDmlDefersInvalidationAndHonorsRollback() {
        seed(1005);
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement("update AgentCacheRecord r set r.label = 'committed' where r.id = 1005").execute());
            assertEquals("loaded:stored", managedLabel(1005));
            agent.getTransaction().commit();
            assertEquals("loaded:committed", managedLabel(1005));
            agent.getTransaction().begin();
            agent.createStatement("update AgentCacheRecord r set r.label = 'rolled-back' where r.id = 1005").execute();
            agent.getTransaction().rollback();
            assertEquals("loaded:committed", managedLabel(1005));
        }
    }

    @Test
    public void ordinaryPessimisticFindNotifiesOnceForWarmAndInvalidatedCache() {
        seed(2001);
        for (boolean invalidated : new boolean[] {false, true}) {
            if (invalidated) {
                try (EntityAgent agent = emf.createEntityAgent()) {
                    AgentCacheRecord record = agent.get(AgentCacheRecord.class, 2001L);
                    record.label = "changed";
                    agent.update(record);
                }
            }
            try (EntityManager manager = emf.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    AgentCacheRecord result = manager.find(AgentCacheRecord.class, 2001L, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
                    assertEquals(invalidated ? "loaded:changed" : "loaded:stored", result.label);
                    assertEquals(1, result.loads);
                } finally { manager.getTransaction().rollback(); }
            }
        }
    }

    @Test
    public void ordinaryPessimisticQueryNotifiesOnceForWarmAndInvalidatedCache() {
        seed(2002);
        for (boolean invalidated : new boolean[] {false, true}) {
            if (invalidated) {
                try (EntityAgent agent = emf.createEntityAgent()) {
                    AgentCacheRecord record = agent.get(AgentCacheRecord.class, 2002L);
                    record.label = "changed";
                    agent.update(record);
                }
            }
            try (EntityManager manager = emf.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    AgentCacheRecord result = manager.createQuery("select r from AgentCacheRecord r where r.id=2002", AgentCacheRecord.class)
                            .setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE).getSingleResult();
                    assertEquals(invalidated ? "loaded:changed" : "loaded:stored", result.label);
                    assertEquals(1, result.loads);
                } finally { manager.getTransaction().rollback(); }
            }
        }
    }
}
