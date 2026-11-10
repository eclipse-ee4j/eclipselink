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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.transaction.Synchronization;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentJtaRecord;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.transaction.JTATransactionController;

/** Real standalone JTA fixture; every transactional connection enlists its H2 XAResource. */
public final class AgentJtaFixture implements AutoCloseable {
    private static final Path OBJECT_STORE = configureObjectStore();
    private static final TransactionManager TRANSACTIONS = transactionManager();

    public final TransactionManager transactions = TRANSACTIONS;
    public final AtomicInteger physicalOpened = new AtomicInteger();
    public final AtomicInteger physicalClosed = new AtomicInteger();
    public final AtomicInteger activeEnlistments = new AtomicInteger();
    public final AtomicInteger synchronizationCompletions = new AtomicInteger();
    public final AtomicInteger illegalLocalTransactionCalls = new AtomicInteger();
    public final EntityManagerFactory factory;

    private final DataSource plainDataSource;
    private final XADataSource xaDataSource;
    private final Connection keeper;

    public AgentJtaFixture() throws Exception {
        if (transactions.getTransaction() != null) {
            throw new IllegalStateException("A previous test left a JTA transaction associated");
        }
        Class<?> dataSourceType = Class.forName("org.h2.jdbcx.JdbcDataSource");
        Object dataSource = dataSourceType.getConstructor().newInstance();
        dataSourceType.getMethod("setURL", String.class).invoke(dataSource,
                "jdbc:h2:mem:entityagent_jta_" + UUID.randomUUID().toString().replace("-", "") + ";LOCK_TIMEOUT=1000");
        dataSourceType.getMethod("setUser", String.class).invoke(dataSource, "sa");
        dataSourceType.getMethod("setPassword", String.class).invoke(dataSource, "");
        plainDataSource = (DataSource) dataSource;
        xaDataSource = (XADataSource) dataSource;
        keeper = plainDataSource.getConnection();
        try {
            try (java.sql.Statement statement = keeper.createStatement()) {
                statement.execute("CREATE TABLE EA_JTA_RECORD (ID BIGINT PRIMARY KEY, NAME VARCHAR(255), VERSION INTEGER)");
            }
            SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName("entityagent-jta-" + UUID.randomUUID());
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.JTA);
            info.setClassLoader(AgentJtaRecord.class.getClassLoader());
            info.setNewTempClassLoader(AgentJtaRecord.class.getClassLoader());
            info.setPersistenceUnitRootUrl(AgentJtaRecord.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(List.of(AgentJtaRecord.class.getName()));
            info.setExcludeUnlistedClasses(true);
            info.setJtaDataSource(new EnlistingDataSource());
            info.setNonJtaDataSource(plainDataSource);
            Properties properties = new Properties();
            properties.setProperty("jakarta.persistence.jdbc.user", "sa");
            properties.setProperty("jakarta.persistence.jdbc.password", "");
            properties.setProperty("eclipselink.target-server", Platform.class.getName());
            properties.setProperty("eclipselink.weaving", "false");
            properties.setProperty("eclipselink.logging.level", "OFF");
            properties.setProperty("eclipselink.cache.shared.default", "false");
            properties.setProperty("jakarta.persistence.validation.mode", "NONE");
            info.setProperties(properties);
            factory = new PersistenceProvider().createContainerEntityManagerFactory(info, Map.of());
        } catch (Exception | Error failure) {
            keeper.close();
            throw failure;
        }
    }

    public String observedName(long id) throws SQLException {
        // This connection is deliberately not enlisted, even when the test thread has a JTA transaction.
        try (Connection connection = plainDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT NAME FROM EA_JTA_RECORD WHERE ID = ?")) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? rows.getString(1) : null;
            }
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if (transactions.getTransaction() != null) {
                transactions.rollback();
            }
        } finally {
            try {
                if (factory.isOpen()) {
                    factory.close();
                }
            } finally {
                keeper.close();
            }
        }
    }

    public static void shutdownObjectStore() throws Exception {
        Class.forName("com.arjuna.ats.arjuna.objectstore.StoreManager").getMethod("shutdown").invoke(null);
        try (var paths = Files.walk(OBJECT_STORE)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private static Path configureObjectStore() {
        try {
            Path directory = Files.createTempDirectory("eclipselink-agent-jta-");
            Object bean = Class.forName("com.arjuna.ats.arjuna.common.arjPropertyManager")
                    .getMethod("getObjectStoreEnvironmentBean").invoke(null);
            bean.getClass().getMethod("setObjectStoreDir", String.class).invoke(bean, directory.toString());
            return directory;
        } catch (ReflectiveOperationException | java.io.IOException failure) {
            throw new ExceptionInInitializerError(failure);
        }
    }

    private static TransactionManager transactionManager() {
        try {
            return (TransactionManager) Class.forName("com.arjuna.ats.jta.TransactionManager")
                    .getMethod("transactionManager").invoke(null);
        } catch (ReflectiveOperationException failure) {
            throw new ExceptionInInitializerError(failure);
        }
    }

    public static class Controller extends JTATransactionController {
        public Controller() {
            super(TRANSACTIONS);
        }
    }

    public static class Platform extends ServerPlatformBase {
        public Platform(DatabaseSession session) {
            super(session);
            disableRuntimeServices();
        }

        @Override
        public Class<? extends ExternalTransactionController> getExternalTransactionControllerClass() {
            return Controller.class;
        }
    }

    private final class EnlistingDataSource implements DataSource {
        @Override
        public Connection getConnection() throws SQLException {
            XAConnection xa = xaDataSource.getXAConnection();
            Connection connection;
            try {
                connection = xa.getConnection();
            } catch (SQLException failure) {
                try {
                    xa.close();
                } catch (SQLException cleanupFailure) {
                    failure.addSuppressed(cleanupFailure);
                }
                throw failure;
            }
            physicalOpened.incrementAndGet();
            AtomicBoolean closed = new AtomicBoolean();
            AtomicBoolean enlisted = new AtomicBoolean();
            Transaction transaction;
            try {
                transaction = transactions.getTransaction();
                if (transaction != null) {
                    if (!transaction.enlistResource(xa.getXAResource())) {
                        throw new SQLException("Narayana refused H2 XA enlistment");
                    }
                    activeEnlistments.incrementAndGet();
                    enlisted.set(true);
                    transaction.registerSynchronization(new Synchronization() {
                        @Override
                        public void beforeCompletion() {
                        }

                        @Override
                        public void afterCompletion(int status) {
                            try {
                                closePhysical(connection, xa, closed);
                            } catch (SQLException failure) {
                                throw new IllegalStateException(failure);
                            } finally {
                                if (enlisted.compareAndSet(true, false)) {
                                    activeEnlistments.decrementAndGet();
                                }
                                synchronizationCompletions.incrementAndGet();
                            }
                        }
                    });
                }
            } catch (Exception failure) {
                try {
                    transactions.setRollbackOnly();
                } catch (Exception markFailure) {
                    failure.addSuppressed(markFailure);
                }
                try {
                    closePhysical(connection, xa, closed);
                } catch (SQLException cleanupFailure) {
                    failure.addSuppressed(cleanupFailure);
                } finally {
                    if (enlisted.compareAndSet(true, false)) {
                        activeEnlistments.decrementAndGet();
                    }
                }
                throw new SQLException("Could not enlist real H2 XA resource", failure);
            }
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[]{Connection.class},
                    (proxy, method, arguments) -> {
                        String name = method.getName();
                        if (name.equals("close")) {
                            // A container may close a logical handle before transaction completion.
                            // The enlisted physical XA resource must survive until afterCompletion.
                            if (transaction == null) {
                                closePhysical(connection, xa, closed);
                            }
                            return null;
                        }
                        if (name.equals("isClosed")) {
                            return closed.get();
                        }
                        if (transaction != null && (name.equals("commit") || name.equals("rollback")
                                || (name.equals("setAutoCommit") && Boolean.TRUE.equals(arguments[0])))) {
                            illegalLocalTransactionCalls.incrementAndGet();
                            throw new SQLException("Local transaction control on an enlisted XA connection");
                        }
                        if (name.equals("equals")) {
                            return proxy == arguments[0];
                        }
                        if (name.equals("hashCode")) {
                            return System.identityHashCode(proxy);
                        }
                        if (name.equals("unwrap") && ((Class<?>) arguments[0]).isInstance(proxy)) {
                            return proxy;
                        }
                        try {
                            return method.invoke(connection, arguments);
                        } catch (InvocationTargetException failure) {
                            throw failure.getCause();
                        }
                    });
        }

        private void closePhysical(Connection connection, XAConnection xa, AtomicBoolean closed) throws SQLException {
            if (closed.compareAndSet(false, true)) {
                try {
                    connection.close();
                } finally {
                    try {
                        xa.close();
                    } finally {
                        physicalClosed.incrementAndGet();
                    }
                }
            }
        }

        @Override
        public Connection getConnection(String user, String password) throws SQLException { return getConnection(); }
        @Override
        public PrintWriter getLogWriter() throws SQLException { return plainDataSource.getLogWriter(); }
        @Override
        public void setLogWriter(PrintWriter writer) throws SQLException { plainDataSource.setLogWriter(writer); }
        @Override
        public void setLoginTimeout(int timeout) throws SQLException { plainDataSource.setLoginTimeout(timeout); }
        @Override
        public int getLoginTimeout() throws SQLException { return plainDataSource.getLoginTimeout(); }
        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException { return plainDataSource.getParentLogger(); }
        @Override
        public <T> T unwrap(Class<T> type) throws SQLException {
            if (type.isInstance(this)) { return type.cast(this); }
            throw new SQLException("Unsupported datasource wrapper");
        }
        @Override
        public boolean isWrapperFor(Class<?> type) { return type.isInstance(this); }
    }
}
