/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitTransactionType;

import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Controlled statement failures on an owned H2 connection, through both public insert APIs. */
@RunWith(EmfRunner.class)
public class TestEntityAgentInsertFailures {
    @Test
    public void foreignVendorCodesAndNonuniqueConstraintsRemainPersistenceFailures() throws Exception {
        try (Fixture fixture = new Fixture()) {
            for (int code : new int[]{1, 1062, 2601, 2627, -803}) {
                fixture.assertInsertFailure(new SQLException("unrelated vendor code", "HY000", code), false);
            }
            fixture.assertInsertFailure(new SQLException("foreign key", "23503"), false);
            fixture.assertInsertFailure(new SQLException("not null", "23502"), false);
            fixture.assertInsertFailure(new SQLException("check", "23513"), false);
        }
    }

    @Test
    public void chainedAndWrappedDuplicatesPreserveOriginalCause() throws Exception {
        try (Fixture fixture = new Fixture()) {
            SQLException batch = new SQLException("batch", "HY000");
            batch.setNextException(new SQLException("duplicate", "23505"));
            fixture.assertInsertFailure(batch, true);
            fixture.assertInsertFailure(new SQLException("driver wrapper",
                    new IllegalStateException("cause wrapper", new SQLException("duplicate", "23505"))), true);
        }
    }

    @Test
    public void suppressedCleanupDoesNotChangeInsertExceptionType() throws Exception {
        try (Fixture fixture = new Fixture()) {
            SQLException primary = new SQLException("primary statement failure", "HY000");
            primary.addSuppressed(new SQLException("cleanup duplicate", "23505"));
            fixture.assertInsertFailure(primary, false);
        }
    }

    @Test
    public void duplicateDuringUpdateIsNotTranslatedAsAnInsert() throws Exception {
        try (Fixture fixture = new Fixture()) {
            try (EntityAgent agent = fixture.factory.createEntityAgent()) {
                agent.insert(new AgentTarget(2, "before"));
                fixture.failure.set(new SQLException("update duplicate", "23505"));
                PersistenceException failure = assertThrows(PersistenceException.class,
                        () -> agent.update(new AgentTarget(2, "after")));
                assertFalse(failure instanceof EntityExistsException);
            }
            try (EntityManager em = fixture.factory.createEntityManager()) {
                em.getTransaction().begin();
                em.find(AgentTarget.class, 2L).label = "after";
                fixture.failure.set(new SQLException("update duplicate", "23505"));
                PersistenceException failure = assertThrows(PersistenceException.class, em::flush);
                assertFalse(failure instanceof EntityExistsException);
                assertTrue(em.getTransaction().getRollbackOnly());
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    public void compositeOracleMemberClassifiesOnlyItsDuplicateCodes() throws Exception {
        assertCompositeInsertFailures(new OraclePlatform(), "23000", new int[]{1}, 1062);
    }

    @Test
    public void compositeMySQLMemberClassifiesOnlyItsDuplicateCodes() throws Exception {
        assertCompositeInsertFailures(new MySQLPlatform(), "23000", new int[]{1062}, 1);
    }

    @Test
    public void compositeSQLServerMemberClassifiesOnlyItsDuplicateCodes() throws Exception {
        assertCompositeInsertFailures(new SQLServerPlatform(), "42000", new int[]{2601, 2627}, 1062);
    }

    private void assertCompositeInsertFailures(DatabasePlatform platform, String duplicateState,
                                              int[] duplicateCodes, int foreignCode) throws Exception {
        try (Fixture fixture = new Fixture(platform)) {
            assertSame(DatabasePlatform.class, fixture.broker.getPlatform().getClass());
            for (int code : duplicateCodes) {
                fixture.assertManagerInsertFailure(new SQLException("member duplicate", duplicateState, code), true, platform);
            }
            fixture.assertManagerInsertFailure(new SQLException("foreign vendor code", duplicateState, foreignCode), false, platform);
            fixture.assertManagerInsertFailure(new SQLException("not null", "23502"), false, platform);
        }
    }

    private static final class FaultConnector implements Connector {
        private static final long serialVersionUID = 1L;
        private final Connector delegate;
        private final AtomicReference<SQLException> failure;

        private FaultConnector(Connector delegate, AtomicReference<SQLException> failure) {
            this.delegate = delegate;
            this.failure = failure;
        }

        @Override
        public Object clone() {
            return new FaultConnector((Connector) delegate.clone(), failure);
        }

        @Override
        public Connection connect(Properties properties, Session session) {
            Connection connection = delegate.connect(properties, session);
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[]{Connection.class},
                    (proxy, method, args) -> {
                        try {
                            Object value = method.invoke(connection, args);
                            if (method.getName().equals("prepareStatement") && args[0] instanceof String sql
                                    && (sql.startsWith("INSERT") || sql.startsWith("UPDATE"))) {
                                PreparedStatement statement = (PreparedStatement) value;
                                return Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
                                        new Class<?>[]{PreparedStatement.class}, (statementProxy, statementMethod, statementArgs) -> {
                                            if (statementMethod.getName().startsWith("execute")) {
                                                SQLException injected = failure.getAndSet(null);
                                                if (injected != null) {
                                                    throw injected;
                                                }
                                            }
                                            try {
                                                return statementMethod.invoke(statement, statementArgs);
                                            } catch (InvocationTargetException exception) {
                                                throw exception.getCause();
                                            }
                                        });
                            }
                            return value;
                        } catch (InvocationTargetException exception) {
                            throw exception.getCause();
                        }
                    });
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
        private final AtomicReference<SQLException> failure = new AtomicReference<>();
        private final Connection keeper;
        private final EntityManagerFactory factory;
        private final EntityManagerFactory memberFactory;
        private final SessionBroker broker;

        private Fixture() throws Exception {
            this(null);
        }

        private Fixture(DatabasePlatform memberPlatform) throws Exception {
            broker = memberPlatform == null ? null : new SessionBroker();
            if (broker != null) {
                broker.setLogLevel(SessionLog.OFF);
            }
            String url = "jdbc:h2:mem:insert-failures-" + UUID.randomUUID();
            keeper = DriverManager.getConnection(url, "sa", "");
            EntityManagerFactory createdFactory = null;
            try {
                try (var statement = keeper.createStatement()) {
                    statement.executeUpdate("CREATE TABLE EA_TARGET (ID BIGINT PRIMARY KEY, LABEL VARCHAR(255))");
                }
                SEPersistenceUnitInfo unit = new SEPersistenceUnitInfo();
                unit.setPersistenceUnitName("insert-failures-" + UUID.randomUUID());
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
                properties.put("eclipselink.session.customizer", (SessionCustomizer) session -> {
                    // The fixture disables weaving and must detect ordinary managed field changes.
                    session.getDescriptor(AgentTarget.class).setObjectChangePolicy(new DeferredChangeDetectionPolicy());
                    if (broker != null) {
                        // Register before login so the broker initializes its member's descriptors.
                        broker.registerSession("insert-failure-member", session);
                    }
                    session.getEventManager().addListener(new SessionEventAdapter() {
                            @Override
                            public void preLogin(SessionEvent event) {
                                DatabaseLogin login = event.getSession().getLogin();
                                login.setConnector(new FaultConnector(login.getConnector(), failure));
                            }
                    });
                });
                unit.setProperties(properties);
                createdFactory = new PersistenceProvider().createContainerEntityManagerFactory(unit, Map.of());
                memberFactory = createdFactory;
                if (broker == null) {
                    factory = memberFactory;
                } else {
                    AbstractSession member = memberFactory.unwrap(AbstractSession.class);
                    // Connect to the owned H2 fixture first; classify injected failures as the member vendor.
                    memberPlatform.initializeConnectionData(keeper);
                    member.getLogin().setPlatform(memberPlatform);
                    broker.login();
                    factory = new EntityManagerFactoryImpl(broker);
                }
            } catch (Exception | Error exception) {
                if (createdFactory != null) {
                    createdFactory.close();
                }
                keeper.close();
                throw exception;
            }
        }

        private void assertInsertFailure(SQLException injected, boolean unique) {
            try (EntityAgent agent = factory.createEntityAgent()) {
                failure.set(injected);
                assertFailure(assertThrows(PersistenceException.class, () -> agent.insert(new AgentTarget(1, "agent"))), injected, unique);
                assertNull(failure.get());
            }
            assertManagerInsertFailure(injected, unique, null);
        }

        private void assertManagerInsertFailure(SQLException injected, boolean unique, DatabasePlatform memberPlatform) {
            try (EntityManager em = factory.createEntityManager()) {
                em.getTransaction().begin();
                em.persist(new AgentTarget(1, "em"));
                failure.set(injected);
                PersistenceException exception = assertThrows(PersistenceException.class, em::flush);
                assertFailure(exception, injected, unique);
                if (memberPlatform != null) {
                    DatabaseException database = (DatabaseException) exception.getCause();
                    assertSame(memberPlatform, database.getQuery().getExecutionSession().getPlatform());
                }
                assertNull(failure.get());
                assertTrue(em.getTransaction().getRollbackOnly());
                em.getTransaction().rollback();
            }
        }

        private void assertFailure(PersistenceException exception, SQLException injected, boolean unique) {
            if (unique) {
                assertTrue(exception instanceof EntityExistsException);
            } else {
                assertFalse(exception instanceof EntityExistsException);
            }
            assertTrue(exception.getCause() instanceof DatabaseException);
            assertSame(injected, ((DatabaseException) exception.getCause()).getInternalException());
        }

        @Override
        public void close() throws Exception {
            try {
                factory.close();
            } finally {
                try {
                    if (broker != null) {
                        broker.logout();
                        memberFactory.close();
                    }
                } finally {
                    keeper.close();
                }
            }
        }
    }
}
