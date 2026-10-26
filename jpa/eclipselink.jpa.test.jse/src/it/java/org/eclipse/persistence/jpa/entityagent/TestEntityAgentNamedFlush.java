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
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.QueryFlushMode;

import org.eclipse.persistence.jpa.entityagent.model.AgentNamedFlushRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(EmfRunner.class)
public class TestEntityAgentNamedFlush {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = AgentNamedFlushRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Test
    public void namedQueryFlushOverridesManagerCommitMode() {
        assertEquals(1, countWithPendingInsert("agentNamedFlush", FlushModeType.COMMIT));
    }

    @Test
    public void namedNativeQueryFlushOverridesManagerCommitMode() {
        assertEquals(1, countWithPendingInsert("agentNativeFlush", FlushModeType.COMMIT));
    }

    @Test
    public void namedQueryNoFlushOverridesManagerAutoMode() {
        assertEquals(0, countWithPendingInsert("agentNamedNoFlush", FlushModeType.AUTO));
    }

    @Test
    public void namedNativeQueryNoFlushOverridesManagerAutoMode() {
        assertEquals(0, countWithPendingInsert("agentNativeNoFlush", FlushModeType.AUTO));
    }

    @Test
    public void defaultNamedQueryFlushFollowsManagerMode() {
        assertEquals(0, countWithPendingInsert("agentNamedDefaultFlush", FlushModeType.COMMIT));
        assertEquals(1, countWithPendingInsert("agentNamedDefaultFlush", FlushModeType.AUTO));
        assertEquals(0, countWithPendingInsert("agentNativeDefaultFlush", FlushModeType.COMMIT));
        assertEquals(1, countWithPendingInsert("agentNativeDefaultFlush", FlushModeType.AUTO));
    }

    @Test
    public void explicitProviderFlushHintRetainsPrecedence() {
        assertEquals(0, countWithPendingInsert("agentNamedFlushHint", FlushModeType.AUTO));
    }

    @Test
    public void agentNamedQueriesExposeConfiguredFlushModes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertEquals(QueryFlushMode.FLUSH, agent.createNamedQuery("agentNamedFlush").getQueryFlushMode());
            assertEquals(QueryFlushMode.NO_FLUSH, agent.createNamedQuery("agentNamedNoFlush").getQueryFlushMode());
            assertEquals(QueryFlushMode.FLUSH, agent.createNamedQuery("agentNativeFlush").getQueryFlushMode());
            assertEquals(QueryFlushMode.NO_FLUSH, agent.createNamedQuery("agentNativeNoFlush").getQueryFlushMode());
        }
    }

    private int countWithPendingInsert(String name, FlushModeType mode) {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                manager.setFlushMode(mode);
                manager.persist(new AgentNamedFlushRecord(1));
                return ((Number) manager.createNamedQuery(name).getSingleResult()).intValue();
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }
}
