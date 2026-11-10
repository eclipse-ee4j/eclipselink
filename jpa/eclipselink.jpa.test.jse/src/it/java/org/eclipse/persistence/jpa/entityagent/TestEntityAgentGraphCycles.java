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
import java.util.function.Function;
import java.util.function.ToIntFunction;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.entityagent.model.AgentEagerNode;
import org.eclipse.persistence.jpa.entityagent.model.AgentLazyNode;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentGraphCycles {
    @Emf(name = "agent-woven-cycle", createTables = DDLGen.DROP_CREATE, classes = AgentLazyNode.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory woven;

    @Emf(name = "agent-plain-cycle", createTables = DDLGen.DROP_CREATE, classes = AgentEagerNode.class,
            mappingFiles = "META-INF/entityagent-plain-cycle.xml",
            properties = {@Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.weaving", value = "false")})
    private EntityManagerFactory plain;

    @Test
    public void wovenLazyCycleHasOneCopyPerIdentityAndNoReadsAfterClose() {
        assertTrue(new AgentLazyNode() instanceof PersistenceWeaved);
        verify(woven, AgentLazyNode.class, "EA_GRAPH_LAZY", AgentLazyNode::getNext,
                AgentLazyNode::getDetail, AgentLazyNode::getLoads);
    }

    @Test
    public void nonwovenEagerCycleHasOneCopyPerIdentityAndNoReadsAfterClose() {
        assertFalse(new AgentEagerNode() instanceof PersistenceWeaved);
        verify(plain, AgentEagerNode.class, "EA_GRAPH_EAGER", AgentEagerNode::getNext,
                AgentEagerNode::getDetail, AgentEagerNode::getLoads);
    }

    private <T> void verify(EntityManagerFactory factory, Class<T> type, String table,
            Function<T, T> next, Function<T, String> detail, ToIntFunction<T> loads) {
        try (EntityAgent setup = factory.createEntityAgent()) {
            setup.<Connection>runWithConnection(connection -> {
                try (var sql = connection.createStatement()) {
                    sql.executeUpdate("INSERT INTO " + table + " (ID, NAME, DETAIL) VALUES (1, 'first', 'first detail')");
                    sql.executeUpdate("INSERT INTO " + table + " (ID, NAME, DETAIL) VALUES (2, 'second', 'second detail')");
                    sql.executeUpdate("UPDATE " + table + " SET NEXT_ID = 2 WHERE ID = 1");
                    sql.executeUpdate("UPDATE " + table + " SET NEXT_ID = 1 WHERE ID = 2");
                }
            });
        }
        AtomicInteger calls = new AtomicInteger();
        SessionEventAdapter counter = new SessionEventAdapter() {
            @Override
            public void preExecuteCall(SessionEvent event) {
                if (calls.incrementAndGet() > 24) {
                    throw new IllegalStateException("Detached graph exceeded its bounded read budget");
                }
            }
        };
        var events = factory.unwrap(JpaEntityManagerFactory.class).getServerSession().getEventManager();
        events.addListener(counter);
        try {
            T first;
            try (EntityAgent agent = factory.createEntityAgent()) {
                var graph = agent.createEntityGraph(type);
                graph.addSubgraph("next").addAttributeNodes("next");
                first = agent.get(graph, 1L);
                T second = next.apply(first);
                assertSame(first, next.apply(second));
                assertSame(second, agent.fetch(second));
                assertEquals("first detail", detail.apply(first));
                assertEquals("second detail", detail.apply(second));
                assertEquals(1, loads.applyAsInt(first));
                assertEquals(1, loads.applyAsInt(second));
                assertFalse(factory.getCache().contains(type, 1L));
            }
            int before = calls.get();
            assertTrue(before > 0 && before <= 12);
            assertEquals("second detail", detail.apply(next.apply(first)));
            assertSame(first, next.apply(next.apply(first)));
            assertEquals(before, calls.get());
        } finally {
            events.removeListener(counter);
        }
    }

    @Test
    public void refreshNotifiesNewlyMaterializedEagerChildren() {
        try (EntityAgent agent = plain.createEntityAgent()) {
            agent.<Connection>runWithConnection(connection -> {
                try (var sql = connection.createStatement()) {
                    sql.executeUpdate("INSERT INTO EA_GRAPH_EAGER (ID, NAME, DETAIL) VALUES (101, 'parent', 'parent detail')");
                    sql.executeUpdate("INSERT INTO EA_GRAPH_EAGER (ID, NAME, DETAIL) VALUES (102, 'child', 'child detail')");
                    sql.executeUpdate("UPDATE EA_GRAPH_EAGER SET NEXT_ID = 102 WHERE ID = 101");
                }
            });
            AgentEagerNode parent = agent.get(AgentEagerNode.class, 101L);
            AgentEagerNode oldChild = parent.getNext();
            assertEquals(1, parent.getLoads());
            assertEquals(1, oldChild.getLoads());
            agent.refresh(parent);
            AgentEagerNode child = parent.getNext();
            org.junit.Assert.assertNotSame(oldChild, child);
            assertEquals("child detail", child.getDetail());
            assertEquals(2, parent.getLoads());
            assertEquals(1, child.getLoads());
            agent.refreshMultiple(java.util.List.of(parent));
            org.junit.Assert.assertNotSame(child, parent.getNext());
            assertEquals("child detail", parent.getNext().getDetail());
            assertEquals(3, parent.getLoads());
            assertEquals(1, parent.getNext().getLoads());
        }
    }
}
