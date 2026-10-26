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

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.RollbackException;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Faults are confined to an owned H2 database and a single-connection provider pool.
 * These tests always use their own H2 fixture, regardless of the portable matrix platform.
 */
@RunWith(EmfRunner.class)
public class TestEntityAgentCleanup {
    @Test
    public void callbackErrorAndRollbackFailureDoNotLeakWorkToTheNextBorrower() throws Exception {
        try (Fixture fixture = new Fixture()) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            AssertionError primary = new AssertionError("callback failure");
            RuntimeException rollback = new IllegalStateException("one-shot rollback failure");
            failRollbackOnce(client, rollback);
            assertSame(primary, assertThrows(AssertionError.class, () -> agent.<Connection>runWithConnection(connection -> {
                try (var statement = connection.createStatement()) {
                    statement.executeUpdate("INSERT INTO EA_TARGET (ID, LABEL) VALUES (1, 'failed')");
                }
                throw primary;
            })));
            assertSame(rollback, primary.getSuppressed()[0]);
            assertThrows(IllegalStateException.class, () -> agent.find(AgentTarget.class, 1L));
            agent.close();
            fixture.assertAbsentBeforeAndAfterPeerCommit(1);
        }
    }

    @Test
    public void failedCommitRetainsItsCauseAndSuppressedRollbackFailure() throws Exception {
        try (Fixture fixture = new Fixture()) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            RuntimeException primary = new IllegalStateException("commit event failure");
            RuntimeException rollback = new IllegalStateException("rollback event failure");
            client.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void preCommitTransaction(SessionEvent event) {
                    if (event.getSession() == client) {
                        throw primary;
                    }
                }
            });
            failRollbackOnce(client, rollback);
            agent.getTransaction().begin();
            agent.insert(new AgentTarget(2, "failed commit"));
            RollbackException failure = assertThrows(RollbackException.class, () -> agent.getTransaction().commit());
            assertSame(primary, failure.getCause());
            assertSame(rollback, primary.getSuppressed()[0]);
            assertThrows(IllegalStateException.class, () -> agent.find(AgentTarget.class, 2L));
            agent.close();
            fixture.assertAbsentBeforeAndAfterPeerCommit(2);
        }
    }

    @Test
    public void releaseFailureKeepsDisposalRetryableAndPeerUsable() throws Exception {
        try (Fixture fixture = new Fixture()) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            RuntimeException release = new IllegalStateException("one-shot release failure");
            AtomicBoolean first = new AtomicBoolean(true);
            client.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void preReleaseClientSession(SessionEvent event) {
                    if (event.getSession() == client && first.getAndSet(false)) {
                        throw release;
                    }
                }
            });
            agent.getTransaction().begin();
            agent.insert(new AgentTarget(3, "failed release"));
            assertSame(release, assertThrows(RuntimeException.class, agent::close));
            assertFalse(agent.isOpen());
            assertThrows(IllegalStateException.class, () -> agent.find(AgentTarget.class, 3L));
            agent.close();
            assertThrows(IllegalStateException.class, agent::close);
            fixture.assertAbsentBeforeAndAfterPeerCommit(3);
        }
    }

    @Test
    public void failureAfterPhysicalCommitDoesNotClaimToUndoCommittedWork() throws Exception {
        try (Fixture fixture = new Fixture()) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            RuntimeException primary = new IllegalStateException("post-commit failure");
            client.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void postCommitTransaction(SessionEvent event) {
                    if (event.getSession() == client) {
                        throw primary;
                    }
                }
            });
            agent.getTransaction().begin();
            agent.insert(new AgentTarget(4, "committed"));
            RollbackException failure = assertThrows(RollbackException.class, () -> agent.getTransaction().commit());
            assertSame(primary, failure.getCause());
            assertEquals("committed", fixture.observedLabel(4));
            assertThrows(IllegalStateException.class, () -> agent.find(AgentTarget.class, 4L));
            agent.close();
            assertEquals("committed", fixture.observedLabel(4));
            try (EntityAgent peer = fixture.factory.createEntityAgent()) {
                peer.insert(new AgentTarget(104, "peer"));
            }
            assertEquals("peer", fixture.observedLabel(104));
        }
    }

    @Test
    public void physicalRollbackFailureDiscardsTheConnectionBeforePeerCommit() throws Exception {
        RollbackFault fault = new RollbackFault();
        try (Fixture fixture = new Fixture(fault)) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            Throwable cause = new IllegalArgumentException("original callback cause");
            RuntimeException primary = new IllegalStateException("callback failure before JDBC rollback", cause);
            AtomicReference<Accessor> failedAccessor = new AtomicReference<>();
            assertSame(primary, assertThrows(RuntimeException.class, () -> agent.<Connection>runWithConnection(connection -> {
                try (var statement = connection.createStatement()) {
                    statement.executeUpdate("INSERT INTO EA_TARGET (ID, LABEL) VALUES (5, 'failed physical rollback')");
                }
                Accessor accessor = client.getWriteConnection();
                assertTrue(accessor.isInTransaction());
                failedAccessor.set(accessor);
                fault.arm(connection, accessor);
                throw primary;
            })));
            assertSame(cause, primary.getCause());
            assertEquals(1, primary.getSuppressed().length);
            assertTrue("The exact JDBC failure must remain in the suppressed rollback cause",
                    hasCause(primary.getSuppressed()[0], fault.failure));
            assertEquals(1, fault.rollbackFailures.get());
            assertFalse("JDBC rollback must clear the transaction flag before physical discard",
                    fault.transactionFlagAtClose);
            assertTrue("The accessor must already be invalid when physical close begins", fault.invalidAtClose);
            assertFalse("A failed physical rollback must invalidate the accessor", failedAccessor.get().isValid());
            assertTrue("The contaminated physical connection must be closed", fault.physical.isClosed());
            assertTrue(fault.closeCalls.get() > 0);
            assertFalse(client.hasWriteConnection());
            var pool = client.getParent().getDefaultConnectionPool();
            assertEquals(0, pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size());
            assertThrows(IllegalStateException.class, () -> agent.find(AgentTarget.class, 5L));
            agent.close();
            assertEquals(null, fixture.observedLabel(5));
            try (EntityAgent peer = fixture.factory.createEntityAgent()) {
                peer.getTransaction().begin();
                peer.insert(new AgentTarget(105, "peer"));
                peer.<Connection>runWithConnection(connection -> {
                    assertNotSame("The peer must receive a different connection wrapper", fault.target, connection);
                    JdbcConnection handler = (JdbcConnection) Proxy.getInvocationHandler(connection);
                    assertNotSame("The peer must receive a different physical connection", fault.physical, handler.delegate);
                });
                peer.getTransaction().commit();
            }
            assertEquals("peer", fixture.observedLabel(105));
            assertEquals("The peer must not commit the failed transaction", null, fixture.observedLabel(5));
        }
    }

    @Test
    public void persistentPhysicalRollbackFailureDiscardsPendingConnectionAndAllowsCloseRetry() throws Exception {
        RollbackFault fault = new RollbackFault(true);
        try (Fixture fixture = new Fixture(fault)) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            var pool = client.getParent().getDefaultConnectionPool();
            RuntimeException primary = new IllegalStateException("callback failure with pending rollback");
            RuntimeException rollbackEvent = new IllegalStateException("initial rollback event failure");
            failRollbackOnce(client, rollbackEvent);
            assertSame(primary, assertThrows(RuntimeException.class, () -> agent.<Connection>runWithConnection(connection -> {
                try (var statement = connection.createStatement()) {
                    statement.executeUpdate("INSERT INTO EA_TARGET (ID, LABEL) VALUES (6, 'pending rollback')");
                }
                fault.arm(connection, client.getWriteConnection());
                throw primary;
            })));
            assertSame(rollbackEvent, primary.getSuppressed()[0]);
            assertEquals(0, fault.rollbackFailures.get());
            assertEquals(1, pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size());
            RuntimeException cleanup = assertThrows(RuntimeException.class, agent::close);
            assertTrue("The physical rollback failure must survive close", hasCause(cleanup, fault.failure));
            assertSame(rollbackEvent, primary.getSuppressed()[0]);
            assertFalse(agent.isOpen());
            assertTrue(fault.rollbackFailures.get() > 0);
            assertTrue(fault.physical.isClosed());
            assertTrue(fault.closeCalls.get() > 0);
            assertEquals("Discard must remove the failed accessor from the used pool", 0,
                    pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size());
            agent.close();
            assertThrows(IllegalStateException.class, agent::close);
            assertEquals(null, fixture.observedLabel(6));
            try (EntityAgent peer = fixture.factory.createEntityAgent()) {
                peer.getTransaction().begin();
                peer.insert(new AgentTarget(106, "peer"));
                peer.<Connection>runWithConnection(connection -> assertNotSame(fault.target, connection));
                peer.getTransaction().commit();
            }
            assertEquals("peer", fixture.observedLabel(106));
            assertEquals("The peer must not commit work from the discarded connection", null, fixture.observedLabel(6));
        }
    }

    @Test
    public void identicalCallbackAndRollbackFailureDoesNotSelfSuppress() throws Exception {
        try (Fixture fixture = new Fixture()) {
            EntityAgent agent = fixture.factory.createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            AssertionError primary = new AssertionError("same callback and rollback failure");
            AtomicBoolean first = new AtomicBoolean(true);
            client.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void preRollbackTransaction(SessionEvent event) {
                    if (event.getSession() == client && first.getAndSet(false)) {
                        throw primary;
                    }
                }
            });
            assertSame(primary, assertThrows(AssertionError.class, () -> agent.<Connection>runWithConnection(connection -> {
                try (var statement = connection.createStatement()) {
                    statement.executeUpdate("INSERT INTO EA_TARGET (ID, LABEL) VALUES (7, 'same failure')");
                }
                throw primary;
            })));
            assertEquals("A Throwable must never be suppressed onto itself", 0, primary.getSuppressed().length);
            agent.close();
            fixture.assertAbsentBeforeAndAfterPeerCommit(7);
        }
    }

    @Test
    public void factoryCallbackAndReleaseWithSameErrorPreserveIdentityAndAllowCloseRetry() throws Exception {
        try (Fixture fixture = new Fixture()) {
            AssertionError primary = new AssertionError("same factory callback and release failure");
            AtomicReference<EntityAgent> captured = new AtomicReference<>();
            AtomicBoolean first = new AtomicBoolean(true);
            assertSame(primary, assertThrows(AssertionError.class, () -> fixture.factory.runInTransaction(
                    EntityAgent.class, agent -> {
                        captured.set(agent);
                        ClientSession client = agent.unwrap(ClientSession.class);
                        client.getEventManager().addListener(new SessionEventAdapter() {
                            @Override
                            public void preReleaseClientSession(SessionEvent event) {
                                if (event.getSession() == client && first.getAndSet(false)) {
                                    throw primary;
                                }
                            }
                        });
                        agent.insert(new AgentTarget(8, "same factory failure"));
                        throw primary;
                    })));
            assertFalse("The factory must have attempted client disposal", first.get());
            assertEquals(0, primary.getSuppressed().length);
            assertFalse(captured.get().isOpen());
            captured.get().close();
            fixture.assertAbsentBeforeAndAfterPeerCommit(8);
        }
    }

    private static boolean hasCause(Throwable failure, Throwable expected) {
        for (Throwable current = failure; current != null; current = current.getCause()) {
            if (current == expected) {
                return true;
            }
            if (current.getCause() == current) {
                break;
            }
        }
        return false;
    }

    private static void failRollbackOnce(ClientSession client, RuntimeException failure) {
        AtomicBoolean first = new AtomicBoolean(true);
        client.getEventManager().addListener(new SessionEventAdapter() {
            @Override
            public void preRollbackTransaction(SessionEvent event) {
                if (event.getSession() == client && first.getAndSet(false)) {
                    throw failure;
                }
            }
        });
    }

    private static final class RollbackFault {
        private final SQLException failure = new SQLException("Owned H2 physical rollback fault", "08006");
        private final AtomicBoolean armed = new AtomicBoolean();
        private final AtomicInteger rollbackFailures = new AtomicInteger();
        private final AtomicInteger closeCalls = new AtomicInteger();
        private final boolean persistent;
        private Connection target;
        private Connection physical;
        private Accessor accessor;
        private boolean transactionFlagAtClose = true;
        private boolean invalidAtClose;

        private RollbackFault() {
            this(false);
        }

        private RollbackFault(boolean persistent) {
            this.persistent = persistent;
        }

        private void arm(Connection connection, Accessor accessor) {
            JdbcConnection handler = (JdbcConnection) Proxy.getInvocationHandler(connection);
            assertSame(this, handler.fault);
            target = connection;
            physical = handler.delegate;
            this.accessor = accessor;
            armed.set(true);
        }
    }

    private static final class JdbcConnection implements InvocationHandler {
        private final Connection delegate;
        private final RollbackFault fault;

        private JdbcConnection(Connection delegate, RollbackFault fault) {
            this.delegate = delegate;
            this.fault = fault;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                if ("equals".equals(method.getName())) {
                    return proxy == args[0];
                }
                if ("hashCode".equals(method.getName())) {
                    return System.identityHashCode(proxy);
                }
            }
            if (proxy == fault.target && "rollback".equals(method.getName())
                    && (args == null || args.length == 0) && fault.armed.get()
                    && (fault.persistent || fault.armed.compareAndSet(true, false))) {
                fault.rollbackFailures.incrementAndGet();
                // Fail before H2 sees rollback: only discarding this connection can undo its pending work.
                throw fault.failure;
            }
            if (proxy == fault.target && "close".equals(method.getName())) {
                if (fault.closeCalls.incrementAndGet() == 1) {
                    // disconnect() retags the discarded accessor after the physical close.
                    fault.transactionFlagAtClose = fault.accessor.isInTransaction();
                    fault.invalidAtClose = !fault.accessor.isValid();
                }
            }
            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException failure) {
                throw failure.getCause();
            }
        }
    }

    private static final class FaultConnector implements Connector {
        private static final long serialVersionUID = 1L;
        private final Connector delegate;
        private final RollbackFault fault;

        private FaultConnector(Connector delegate, RollbackFault fault) {
            this.delegate = delegate;
            this.fault = fault;
        }

        @Override
        public Object clone() {
            return new FaultConnector((Connector) delegate.clone(), fault);
        }

        @Override
        public Connection connect(Properties properties, Session session) {
            Connection physical = delegate.connect(properties, session);
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class}, new JdbcConnection(physical, fault));
        }

        @Override
        public void toString(PrintWriter writer) {
            delegate.toString(writer);
        }

        @Override
        public String getConnectionDetails() {
            return delegate.getConnectionDetails();
        }
    }

    private static final class Fixture implements AutoCloseable {
        private final String url = "jdbc:h2:mem:entityagent_cleanup_" + UUID.randomUUID().toString().replace("-", "");
        private final Connection keeper = DriverManager.getConnection(url, "sa", "");
        private final EntityManagerFactory factory;

        private Fixture() throws Exception {
            this(null);
        }

        private Fixture(RollbackFault fault) throws Exception {
            try {
                try (var statement = keeper.createStatement()) {
                    statement.executeUpdate("CREATE TABLE EA_TARGET (ID BIGINT PRIMARY KEY, LABEL VARCHAR(255))");
                }
                SEPersistenceUnitInfo unit = new SEPersistenceUnitInfo();
                unit.setPersistenceUnitName("entityagent-cleanup-" + UUID.randomUUID());
                unit.setPersistenceProviderClassName(PersistenceProvider.class.getName());
                unit.setPersistenceXMLSchemaVersion("4.0");
                unit.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
                unit.setClassLoader(AgentTarget.class.getClassLoader());
                unit.setNewTempClassLoader(AgentTarget.class.getClassLoader());
                unit.setPersistenceUnitRootUrl(AgentTarget.class.getProtectionDomain().getCodeSource().getLocation());
                unit.setManagedClassNames(List.of(AgentTarget.class.getName()));
                unit.setExcludeUnlistedClasses(true);
                Properties properties = new Properties();
                properties.setProperty("jakarta.persistence.jdbc.url", url);
                properties.setProperty("jakarta.persistence.jdbc.user", "sa");
                properties.setProperty("jakarta.persistence.jdbc.password", "");
                properties.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
                properties.setProperty("jakarta.persistence.validation.mode", "NONE");
                properties.setProperty("eclipselink.weaving", "false");
                properties.setProperty("eclipselink.cache.shared.default", "false");
                properties.setProperty("eclipselink.logging.level", "OFF");
                properties.setProperty("eclipselink.connection-pool.default.initial", "1");
                properties.setProperty("eclipselink.connection-pool.default.min", "1");
                properties.setProperty("eclipselink.connection-pool.default.max", "1");
                properties.setProperty("eclipselink.connection-pool.default.wait", "1000");
                if (fault != null) {
                    properties.put("eclipselink.session.customizer", (SessionCustomizer) session ->
                            session.getEventManager().addListener(new SessionEventAdapter() {
                                @Override
                                public void preLogin(SessionEvent event) {
                                    DatabaseLogin login = event.getSession().getLogin();
                                    login.setConnector(new FaultConnector(login.getConnector(), fault));
                                }
                            }));
                }
                unit.setProperties(properties);
                factory = new PersistenceProvider().createContainerEntityManagerFactory(unit, Map.of());
                try (EntityAgent agent = factory.createEntityAgent()) {
                    assertEquals(1, agent.unwrap(ClientSession.class).getParent().getDefaultConnectionPool().getMaxNumberOfConnections());
                }
            } catch (Exception | Error failure) {
                keeper.close();
                throw failure;
            }
        }

        private String observedLabel(long id) throws Exception {
            try (Connection observer = DriverManager.getConnection(url, "sa", "");
                    var statement = observer.prepareStatement("SELECT LABEL FROM EA_TARGET WHERE ID = ?")) {
                statement.setLong(1, id);
                try (var result = statement.executeQuery()) {
                    return result.next() ? result.getString(1) : null;
                }
            }
        }

        private void assertAbsentBeforeAndAfterPeerCommit(long id) throws Exception {
            assertEquals(null, observedLabel(id));
            try (EntityAgent peer = factory.createEntityAgent()) {
                peer.insert(new AgentTarget(id + 100, "peer"));
            }
            assertEquals("peer", observedLabel(id + 100));
            assertEquals("The next borrower must not commit failed work", null, observedLabel(id));
        }

        @Override
        public void close() throws Exception {
            factory.close();
            keeper.close();
        }
    }
}
