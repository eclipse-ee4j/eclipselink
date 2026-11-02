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
package org.eclipse.persistence.internal.jpa;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.weaving.PersistenceWeaver;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityManagerSetupImplSchemaConnectionTest {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void onlyDisposableTransformersAreClearedAtFinalUndeployEvenWhenDisposalFails(boolean disposable) throws Exception {
        RuntimeException failure = new IllegalStateException("Expected disposal failure");
        EntityManagerSetupImpl setup = new EntityManagerSetupImpl("schema-disposal", "schema-disposal") {
            @Override
            protected void removeSessionFromGlobalSessionManager() {
                throw failure;
            }
        };
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setPersistenceUnitName("schema-disposal");
        setup.persistenceUnitInfo = info;
        setup.session = new DatabaseSessionImpl();
        setup.session.setName("schema-disposal");
        setup.state = EntityManagerSetupImpl.STATE_DEPLOY_FAILED;
        setup.factoryCount = 2;
        setup.setIsDisposableSchemaGeneration(disposable);
        PersistenceWeaver weaver = new PersistenceWeaver(new HashMap<>());
        var metadata = weaver.getClassDetailsMap();
        var field = EntityManagerSetupImpl.class.getDeclaredField("weaver");
        field.setAccessible(true);
        field.set(setup, weaver);
        setup.undeploy();
        assertEquals(1, setup.getFactoryCount());
        assertSame(metadata, weaver.getClassDetailsMap());
        assertSame(failure, assertThrows(IllegalStateException.class, setup::undeploy));
        assertTrue(setup.isUndeployed());
        assertNull(setup.getSession());
        if (disposable) {
            assertNull(weaver.getClassDetailsMap());
            assertNull(field.get(setup));
        } else {
            assertSame(metadata, weaver.getClassDetailsMap());
            assertSame(weaver, field.get(setup));
        }
        setup.undeploy();
        assertEquals(0, setup.getFactoryCount());
    }

    @Test
    void onlySchemaRequestsWithSuppliedConnectionsUseBorrowedInitialization() {
        Fixture fixture = new Fixture(false);
        for (boolean requiresConnection : new boolean[] {false, true}) {
            for (String action : List.of("none", "validate")) {
                Map<String, Object> properties = new HashMap<>();
                properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, action);
                properties.put(PersistenceUnitProperties.TARGET_DATABASE, H2Platform.class.getName());
                properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, fixture.connection);
                LoginProbe session = new LoginProbe();
                EntityManagerFactoryProvider.login(session, properties, requiresConnection);
                assertEquals(requiresConnection ? "login" : "borrowed", session.operation);
                if (!requiresConnection) {
                    assertSame(fixture.connection, session.borrowed);
                }
                properties.remove(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION);
                session = new LoginProbe();
                EntityManagerFactoryProvider.login(session, properties, requiresConnection);
                assertEquals(!requiresConnection && action.equals("none") ? "initialize" : "login", session.operation);
                properties.remove(PersistenceUnitProperties.TARGET_DATABASE);
                session = new LoginProbe();
                EntityManagerFactoryProvider.login(session, properties, requiresConnection);
                assertEquals(!requiresConnection && action.equals("none") ? "initialize" : "detect", session.operation);
            }
        }
    }

    @Test
    void borrowedInitializationDetectsMetadataWithoutUsingTheNormalConnector() {
        Fixture fixture = new Fixture(false);
        fixture.session.setDatasourceAndInitialize(fixture.connection);
        assertTrue(fixture.session.getPlatform() instanceof H2Platform);
        assertEquals(1, fixture.driverMetadataReads);
        assertTrue(fixture.metadataReads > 0);
        fixture.assertConnectionUntouched();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ordinaryMetadataDetectionRetainsItsConnectionOwnershipAndErrorPolicy(boolean metadataFails) {
        Fixture fixture = new Fixture(false);
        fixture.login.setConnector(new DefaultConnector() {
            @Override
            public Connection connect(Properties properties, Session session) {
                fixture.replacements++;
                return fixture.connection;
            }
        });
        if (metadataFails) {
            fixture.metadataFailure = new SQLException("Ordinary no-connect detection retains its error policy");
        }
        fixture.session.setDatasourceAndInitialize();
        assertEquals(1, fixture.replacements);
        assertEquals(List.of("close"), fixture.connectionMutations);
        assertTrue(fixture.metadataReads > 0);
        if (!metadataFails) {
            assertTrue(fixture.session.getPlatform() instanceof H2Platform);
        }
    }

    @Test
    void borrowedInitializationPreservesAnExplicitPlatformWithoutReadingMetadata() {
        Fixture fixture = new Fixture(false);
        H2Platform platform = new H2Platform();
        fixture.login.setPlatform(platform);
        fixture.session.setProperty(PersistenceUnitProperties.TARGET_DATABASE, H2Platform.class.getName());
        fixture.metadataFailure = new SQLException("Metadata must not be read with an explicit platform");
        fixture.session.setDatasourceAndInitialize(fixture.connection);
        assertSame(platform, fixture.session.getPlatform());
        assertEquals(0, fixture.metadataReads);
        fixture.assertConnectionUntouched();
    }

    @Test
    void borrowedInitializationUsesExplicitProductMetadataWithoutReadingTheConnection() {
        Fixture fixture = new Fixture(false);
        fixture.session.setProperty(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "H2");
        fixture.session.setProperty(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "2");
        fixture.session.setProperty(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "4");
        fixture.metadataFailure = new SQLException("Explicit product metadata must be sufficient");
        fixture.session.setDatasourceAndInitialize(fixture.connection);
        assertTrue(fixture.session.getPlatform() instanceof H2Platform);
        assertEquals(0, fixture.metadataReads);
        fixture.assertConnectionUntouched();
    }

    @Test
    void borrowedMetadataFailurePropagatesWithoutFallbackOrConnectionCleanup() {
        Fixture fixture = new Fixture(false);
        fixture.metadataFailure = new SQLException("Expected supplied metadata failure");
        DatabaseException failure = assertThrows(DatabaseException.class,
                () -> fixture.session.setDatasourceAndInitialize(fixture.connection));
        assertSame(fixture.metadataFailure, failure.getCause());
        assertEquals(1, fixture.metadataReads);
        fixture.assertConnectionUntouched();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void selectedPlatformInitializesFromTheActualConnectionBeforeDescriptors(boolean nativePlatform) {
        Fixture fixture = new Fixture(false);
        HookPlatform platform = new HookPlatform(fixture.initializationEvents);
        fixture.login.setPlatform(platform);
        if (!nativePlatform) {
            fixture.session.setProperty(PersistenceUnitProperties.TARGET_DATABASE, HookPlatform.class.getName());
        }
        fixture.metadataFailure = new SQLException("An explicitly selected platform must not be replaced");
        fixture.session.setDatasourceAndInitialize(fixture.connection, nativePlatform);
        assertSame(platform, fixture.session.getPlatform());
        assertSame(fixture.connection, platform.supplied);
        assertEquals(List.of("connection-data", "descriptors"), fixture.initializationEvents);
        assertEquals(0, fixture.metadataReads);
        fixture.assertConnectionUntouched();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void borrowedSequenceInitializationRestoresTheSeparateConnectionSetting(boolean initializationFails) {
        for (boolean separate : new boolean[] {false, true}) {
            Fixture fixture = new Fixture(false);
            fixture.session.getSequencingControl().setShouldUseSeparateConnection(separate);
            if (initializationFails) {
                fixture.descriptorFailure = new IllegalStateException("Expected descriptor initialization failure");
                assertSame(fixture.descriptorFailure, assertThrows(IllegalStateException.class,
                        () -> fixture.session.setDatasourceAndInitialize(fixture.connection)));
            } else {
                fixture.session.setDatasourceAndInitialize(fixture.connection);
            }
            assertEquals(Boolean.FALSE, fixture.separateConnectionDuringInitialization);
            assertEquals(separate, fixture.session.getSequencingControl().shouldUseSeparateConnection());
            fixture.assertConnectionUntouched();
        }
    }

    @Test
    void platformInitializationFailureDoesNotInitializeDescriptorsOrTakeConnectionOwnership() {
        Fixture fixture = new Fixture(false);
        HookPlatform platform = new HookPlatform(fixture.initializationEvents);
        fixture.login.setPlatform(platform);
        platform.failure = new SQLException("Expected platform connection initialization failure");
        DatabaseException failure = assertThrows(DatabaseException.class,
                () -> fixture.session.setDatasourceAndInitialize(fixture.connection, true));
        assertSame(platform.failure, failure.getInternalException());
        assertSame(fixture.connection, platform.supplied);
        assertEquals(List.of("connection-data"), fixture.initializationEvents);
        fixture.assertConnectionUntouched();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void schemaStatementsDoNotTakeOwnershipOfSuppliedConnections(boolean externallyPooled) throws Exception {
        Fixture fixture = new Fixture(externallyPooled);
        DatabaseSessionImpl schema = fixture.schema();
        assertSame(fixture.connection, schema.getAccessor().getConnection());
        assertSame(fixture.session.getPlatform(), schema.getPlatform());
        try (AutoCloseable ignored = (AutoCloseable) schema.getAccessor()) {
            schema.executeNonSelectingSQL("UPDATE EXAMPLE SET VALUE = 1");
            schema.executeNonSelectingSQL("UPDATE EXAMPLE SET VALUE = 2");
            assertEquals(2, fixture.executions);
            assertFalse(fixture.closed);
            assertFalse(schema.getAccessor().usesExternalConnectionPooling());
        }
        fixture.assertReleasedStatementsOnly();
        assertEquals(externallyPooled, fixture.login.shouldUseExternalConnectionPooling(),
                "Borrowing must not mutate the shared login");
        assertSame(fixture.session, fixture.setup.getDatabaseSession(Map.of()));
    }

    @Test
    void borrowedDdlSessionRetainsPreparedSequenceMetadataWithoutOwningItsLifecycle() throws Exception {
        Fixture fixture = new Fixture(false);
        List<String> operations = new ArrayList<>();
        fixture.sequencingMetadata = (Sequencing) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {Sequencing.class}, (proxy, method, arguments) -> {
                    operations.add(method.getName());
                    return Fixture.defaultValue(method.getReturnType());
                });
        fixture.session.getSequencingControl().setShouldUseSeparateConnection(true);
        DatabaseSessionImpl schema = fixture.schema();
        assertSame(fixture.sequencingMetadata, schema.getSequencing());
        assertFalse(schema.getSequencingControl().shouldUseSeparateConnection(),
                "The borrowed wrapper must not share the source sequencing home");
        try (AutoCloseable ignored = (AutoCloseable) schema.getAccessor()) {
            schema.executeNonSelectingSQL("UPDATE EXAMPLE SET VALUE = 1");
        }
        assertSame(fixture.sequencingMetadata, fixture.session.getSequencing());
        assertTrue(fixture.session.getSequencingControl().shouldUseSeparateConnection());
        assertTrue(operations.isEmpty(), "Borrowed cleanup must not operate on source sequencing");
        fixture.assertReleasedStatementsOnly();
    }

    @Test
    void executionFailureStillClosesStatementsWithoutTouchingTheConnection() throws Exception {
        Fixture fixture = new Fixture(true);
        fixture.failure = new SQLException("Expected schema execution failure", "42000");
        DatabaseSessionImpl schema = fixture.schema();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            try (AutoCloseable ignored = (AutoCloseable) schema.getAccessor()) {
                schema.executeNonSelectingSQL("UPDATE EXAMPLE SET VALUE = 1");
            }
        });
        assertSame(fixture.failure, exception.getInternalException());
        fixture.assertReleasedStatementsOnly();
    }

    @Test
    void closedCallerConnectionFailsWithoutObtainingAReplacement() throws Exception {
        Fixture fixture = new Fixture(true);
        fixture.closed = true;
        DatabaseSessionImpl schema = fixture.schema();
        try (AutoCloseable ignored = (AutoCloseable) schema.getAccessor()) {
            assertThrows(DatabaseException.class, () -> schema.executeNonSelectingSQL("UPDATE EXAMPLE SET VALUE = 1"));
        }
        assertTrue(fixture.connectionMutations.isEmpty());
        assertEquals(0, fixture.replacements);
    }

    private static final class HookPlatform extends H2Platform {
        final List<String> events;
        Connection supplied;
        SQLException failure;

        HookPlatform(List<String> events) {
            this.events = events;
        }

        @Override
        public void initializeConnectionData(Connection connection) throws SQLException {
            events.add("connection-data");
            supplied = connection;
            if (failure != null) {
                throw failure;
            }
        }
    }

    private static final class LoginProbe extends DatabaseSessionImpl {
        String operation;
        Connection borrowed;

        @Override
        public void setDatasourceAndInitialize(Connection connection) {
            operation = "borrowed";
            borrowed = connection;
        }

        @Override
        public void setDatasourceAndInitialize() {
            operation = "initialize";
        }

        @Override
        public void login() {
            operation = "login";
        }

        @Override
        public void loginAndDetectDatasource() {
            operation = "detect";
        }
    }

    private static final class Fixture {
        final List<String> connectionMutations = new ArrayList<>();
        final List<String> initializationEvents = new ArrayList<>();
        final DatabaseLogin login = new DatabaseLogin();
        final DatabaseSessionImpl session = new DatabaseSessionImpl(login) {
            @Override
            public Sequencing getSequencing() {
                return sequencingMetadata == null ? super.getSequencing() : sequencingMetadata;
            }

            @Override
            public void initializeDescriptors() {
                initializationEvents.add("descriptors");
                separateConnectionDuringInitialization = getSequencingControl().shouldUseSeparateConnection();
                if (descriptorFailure != null) {
                    throw descriptorFailure;
                }
                super.initializeDescriptors();
            }
        };
        final EntityManagerSetupImpl setup = new EntityManagerSetupImpl("schema-connection", "schema-connection");
        final Connection connection;
        int replacements;
        int statements;
        int statementCloses;
        int executions;
        int metadataReads;
        int driverMetadataReads;
        boolean closed;
        SQLException failure;
        SQLException metadataFailure;
        IllegalStateException descriptorFailure;
        Boolean separateConnectionDuringInitialization;
        Sequencing sequencingMetadata;

        Fixture(boolean externallyPooled) {
            login.setUsesExternalConnectionPooling(externallyPooled);
            login.setConnector(new DefaultConnector() {
                @Override
                public Connection connect(Properties properties, Session owner) {
                    replacements++;
                    throw new AssertionError("A supplied connection must never be replaced");
                }
            });
            session.setLogLevel(org.eclipse.persistence.logging.SessionLog.OFF);
            setup.session = session;
            connection = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Connection.class},
                    (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "isClosed" -> closed;
                            case "getMetaData" -> {
                                metadataReads++;
                                if (metadataFailure != null) {
                                    throw metadataFailure;
                                }
                                yield Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {DatabaseMetaData.class},
                                        (metadata, operation, values) -> switch (operation.getName()) {
                                            case "getDatabaseProductName" -> "H2";
                                            case "getDatabaseProductVersion" -> "2.4.240";
                                            case "getDatabaseMajorVersion" -> 2;
                                            case "getDriverName" -> {
                                                driverMetadataReads++;
                                                yield "schema-metadata-driver";
                                            }
                                            default -> defaultValue(operation.getReturnType());
                                        });
                            }
                            case "createStatement", "prepareStatement" -> {
                                if (closed) {
                                    throw new SQLException("Caller connection is closed", "08003");
                                }
                                yield statement();
                            }
                            case "close", "commit", "rollback", "setAutoCommit", "setTransactionIsolation", "setReadOnly" -> {
                                connectionMutations.add(method.getName());
                                yield null;
                            }
                            case "getAutoCommit", "isReadOnly" -> false;
                            case "getTransactionIsolation" -> Connection.TRANSACTION_READ_COMMITTED;
                            case "toString" -> "caller-owned schema connection";
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            default -> defaultValue(method.getReturnType());
                        };
                    });
        }

        DatabaseSessionImpl schema() {
            return setup.getDatabaseSession(Map.of(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection));
        }

        PreparedStatement statement() {
            statements++;
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {PreparedStatement.class},
                    (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "executeUpdate" -> {
                                executions++;
                                if (failure != null) {
                                    throw failure;
                                }
                                yield 1;
                            }
                            case "close" -> {
                                statementCloses++;
                                yield null;
                            }
                            case "getConnection" -> connection;
                            default -> defaultValue(method.getReturnType());
                        };
                    });
        }

        void assertReleasedStatementsOnly() {
            assertTrue(statements > 0);
            assertEquals(statements, statementCloses, "All temporary statements must be closed");
            assertConnectionUntouched();
        }

        void assertConnectionUntouched() {
            assertTrue(connectionMutations.isEmpty(), connectionMutations.toString());
            assertEquals(0, replacements);
        }

        private static Object defaultValue(Class<?> type) {
            if (type == boolean.class) {
                return false;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == long.class) {
                return 0L;
            }
            return null;
        }
    }
}
