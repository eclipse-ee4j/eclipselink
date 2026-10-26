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
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.RollbackException;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementOrTypedQuery;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Set;

import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentResources {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = AgentTarget.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Test
    public void completeHandlerAndQueryContractsRejectClosedOwner() {
        EntityAgent agent = emf.createEntityAgent();
        EntityTransaction transaction = agent.getTransaction();
        StatementOrTypedQuery query = agent.createQuery("select t from AgentTarget t");
        Statement statement = agent.createStatement("delete from AgentTarget t");
        StoredProcedureQuery procedure = agent.createStoredProcedureQuery("EA_UNUSED_PROCEDURE");
        agent.close();
        assertFalse(agent.isOpen());
        assertSame(transaction, agent.getTransaction());
        agent.getProperties();
        assertClosed(agent, EntityAgent.class, Set.of("isOpen", "getProperties", "getTransaction"));
        assertClosed(transaction, EntityTransaction.class, Set.of());
        assertClosed(query, StatementOrTypedQuery.class, Set.of());
        assertClosed(query, TypedQuery.class, Set.of());
        assertClosed(statement, Statement.class, Set.of());
        assertClosed(procedure, StoredProcedureQuery.class, Set.of());
    }

    private static void assertClosed(Object target, Class<?> contract, Set<String> exceptions) {
        for (Method method : contract.getMethods()) {
            if (exceptions.contains(method.getName())) {
                continue;
            }
            Object[] arguments = new Object[method.getParameterCount()];
            for (int index = 0; index < arguments.length; index++) {
                if (method.getParameterTypes()[index] == int.class) {
                    arguments[index] = 1;
                }
            }
            InvocationTargetException failure = assertThrows(method.toString(), InvocationTargetException.class,
                    () -> method.invoke(target, arguments));
            assertTrue(method.toString() + ": " + failure.getCause().getClass().getName(),
                    failure.getCause() instanceof IllegalStateException);
        }
    }

    @Test
    public void localTransactionStateAndReuse() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            EntityTransaction transaction = agent.getTransaction();
            transaction.setTimeout(12);
            assertEquals(Integer.valueOf(12), transaction.getTimeout());
            assertThrows(IllegalArgumentException.class, () -> transaction.setTimeout(-1));
            assertThrows(IllegalStateException.class, transaction::commit);
            assertThrows(IllegalStateException.class, transaction::rollback);
            assertThrows(IllegalStateException.class, transaction::getRollbackOnly);
            transaction.begin();
            assertThrows(IllegalStateException.class, transaction::begin);
            transaction.setRollbackOnly();
            assertTrue(transaction.getRollbackOnly());
            assertThrows(RollbackException.class, transaction::commit);
            assertFalse(transaction.isActive());
            transaction.begin();
            transaction.commit();
            assertFalse(transaction.isActive());
            transaction.setTimeout(null);
            assertEquals(null, transaction.getTimeout());
        }
    }

    @Test
    public void activeCloseRollsBackOwnedWorkAndLeavesPeerUsable() {
        EntityAgent agent = emf.createEntityAgent();
        agent.getTransaction().begin();
        agent.insert(new AgentTarget(901, "rollback"));
        agent.close();
        try (EntityAgent peer = emf.createEntityAgent()) {
            assertEquals(null, peer.find(AgentTarget.class, 901L));
            peer.insert(new AgentTarget(902, "peer"));
            peer.delete(peer.get(AgentTarget.class, 902L));
        }
    }

    @Test
    public void callbackErrorPreservesIdentityAndDisposal() {
        EntityAgent agent = emf.createEntityAgent();
        AssertionError original = new AssertionError("owned callback error");
        agent.getTransaction().begin();
        assertSame(original, assertThrows(AssertionError.class,
                () -> agent.runWithConnection(connection -> { throw original; })));
        assertThrows(IllegalStateException.class, () -> agent.find(AgentTarget.class, 903L));
        agent.close();
        assertFalse(agent.isOpen());
    }

    @Test
    public void physicalConnectionFailureIsNotReportedAsUsable() {
        EntityAgent agent = emf.createEntityAgent();
        agent.getTransaction().begin();
        agent.<Connection>runWithConnection(Connection::close);
        assertThrows(RollbackException.class, () -> agent.getTransaction().commit());
        assertThrows(IllegalStateException.class, () -> agent.insert(new AgentTarget(904, "closed")));
        agent.close();
        assertFalse(agent.isOpen());
    }
}
