/*
 * Copyright (c) 2015, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     03/04/2015 - William Dazey
//       - 460862 : Added support for schema generation with persistence units with JTA transaction type.
package org.eclipse.persistence.jpa.test.ddl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import javax.tools.ToolProvider;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SchemaValidationException;
import jakarta.persistence.SchemaManagementAction;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.weaving.PersistenceWeaver;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.jpa.test.ddl.model.SchemaGenerationEntity;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestSchemaGen {
    private static final String jtaPersistenceUnit = "jta-pu";

    public static Throwable getBaseException(Exception e) {
        Throwable base = e;
        while (base.getCause() != null) {
            base = base.getCause();
        }
        return base;
    }

    /**
     * This test checks that PersistenceUnits with JTA transaction types can be
     * used to generate schema given that the 2.1 required properties are
     * included: jakarta.persistence.database-product-name
     * jakarta.persistence.database-major-version
     * jakarta.persistence.database-minor-version
     */
    @Test
    public void testJTASchemaGenerationWithProps() throws Exception {
        Properties map = new Properties();
        //map.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION);
        //map.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, out.getAbsolutePath());
        map.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "Oracle");
        map.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "10");
        map.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "1");
        Exception e = null;
        try {
            Persistence.generateSchema(jtaPersistenceUnit, map);
        } catch (Exception pe) {
            e = pe;
        }
        Assert.assertNull(e);
    }

    /**
     * This test checks the inverse of
     * TestSchemaGen.testJTASchemaGenerationWithProps() Without the 2.1
     * properties defined, generating schema should fail with no JTA-DS defined.
     */
    @Test
    public void testJTASchemaGenerationWithoutProps() throws Exception {
        Exception e = null;
        try {
            Persistence.generateSchema(jtaPersistenceUnit, new Properties());
        } catch (Exception pe) {
            e = pe;
        }
        Assert.assertNotNull(e);
        Throwable base = getBaseException(e);
        Assert.assertTrue(base instanceof EntityManagerSetupException);
    }

    @Test
    public void freshNoneAction() throws Exception {
        standard("none", false);
    }

    @Test
    public void reusedNoneAction() throws Exception {
        standard("none", true);
    }

    @Test
    public void freshCreateAction() throws Exception {
        standard("create", false);
    }

    @Test
    public void reusedCreateAction() throws Exception {
        standard("create", true);
    }

    @Test
    public void freshDropAction() throws Exception {
        standard("drop", false);
    }

    @Test
    public void reusedDropAction() throws Exception {
        standard("drop", true);
    }

    @Test
    public void freshDropAndCreateAction() throws Exception {
        standard("drop-and-create", false);
    }

    @Test
    public void reusedDropAndCreateAction() throws Exception {
        standard("drop-and-create", true);
    }

    @Test
    public void freshValidateAction() throws Exception {
        standard("validate", false);
    }

    @Test
    public void reusedValidateAction() throws Exception {
        standard("validate", true);
    }

    @Test
    public void freshPopulateAction() throws Exception {
        standard("populate", false);
    }

    @Test
    public void reusedPopulateAction() throws Exception {
        standard("populate", true);
    }

    @Test
    public void freshAbsentAction() throws Exception {
        standard(null, false);
    }

    @Test
    public void reusedAbsentAction() throws Exception {
        standard(null, true);
    }

    @Test
    public void freshValidateRejectsMissingTable() throws Exception {
        missing("validate", false);
    }

    @Test
    public void reusedValidateRejectsMissingTable() throws Exception {
        missing("validate", true);
    }

    @Test
    public void freshLegacyVerifyRejectsMissingTable() throws Exception {
        missing("verify", false);
    }

    @Test
    public void reusedLegacyVerifyRejectsMissingTable() throws Exception {
        missing("verify", true);
    }

    @Test
    public void freshLegacyVerifyValidatesThenLoads() throws Exception {
        standard("verify", false);
    }

    @Test
    public void reusedLegacyVerifyValidatesThenLoads() throws Exception {
        standard("verify", true);
    }

    @Test
    public void freshNoneStillWritesCreateScript() throws Exception {
        exercise("none", false, false, false, "create", EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void reusedNoneStillWritesCreateScript() throws Exception {
        exercise("none", true, false, false, "create", EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void freshNoneStillWritesDropAndCreateScript() throws Exception {
        exercise("none", false, false, false, "drop-and-create", EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void reusedNoneStillWritesDropAndCreateScript() throws Exception {
        exercise("none", true, false, false, "drop-and-create", EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void freshNoneDoesNotResolveLoadResource() throws Exception {
        exercise("none", false, false, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void reusedNoneDoesNotResolveLoadResource() throws Exception {
        exercise("none", true, false, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void freshDropDoesNotResolveLoadResource() throws Exception {
        exercise("drop", false, false, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void reusedDropDoesNotResolveLoadResource() throws Exception {
        exercise("drop", true, false, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void freshValidateDoesNotResolveLoadResource() throws Exception {
        exercise("validate", false, false, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void reusedValidateDoesNotResolveLoadResource() throws Exception {
        exercise("validate", true, false, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void freshInvalidSchemaDoesNotResolveLoadResource() throws Exception {
        exercise("validate", false, true, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void reusedInvalidSchemaDoesNotResolveLoadResource() throws Exception {
        exercise("validate", true, true, true, null, EntryPoint.NAMED_FACTORY);
    }

    @Test
    public void freshNamedSchemaRecognizesPopulate() throws Exception {
        exercise("populate", false, false, false, null, EntryPoint.NAMED_SCHEMA);
    }

    @Test
    public void freshNamedSchemaNoneWritesScriptWithoutLoading() throws Exception {
        exercise("none", false, false, false, "create", EntryPoint.NAMED_SCHEMA);
    }

    @Test
    public void reusedNamedSchemaRecognizesPopulate() throws Exception {
        exercise("populate", true, false, false, null, EntryPoint.NAMED_SCHEMA);
    }

    @Test
    public void reusedNamedSchemaNoneWritesScriptWithoutLoading() throws Exception {
        exercise("none", true, false, false, "create", EntryPoint.NAMED_SCHEMA);
    }

    @Test
    public void containerSchemaRecognizesPopulate() throws Exception {
        exercise("populate", false, false, false, null, EntryPoint.CONTAINER_SCHEMA);
    }

    @Test
    public void containerSchemaNoneWritesScriptWithoutLoading() throws Exception {
        exercise("none", false, false, false, "create", EntryPoint.CONTAINER_SCHEMA);
    }

    @Test
    public void containerSchemaRejectsMissingTable() throws Exception {
        exercise("validate", false, true, false, null, EntryPoint.CONTAINER_SCHEMA);
    }

    @Test
    public void containerFactoryRecognizesPopulate() throws Exception {
        exercise("populate", false, false, false, null, EntryPoint.CONTAINER_FACTORY);
    }

    @Test
    public void configurationDatabaseActions() throws Exception {
        for (String action : List.of("none", "create", "drop", "drop-and-create", "validate", "populate")) {
            exercise(action, false, false, false, null, EntryPoint.CONFIGURATION_SCHEMA);
        }
    }

    @Test
    public void configurationSchemaActionsPreserveAnOpenFactory() throws Exception {
        for (String action : List.of("none", "create", "drop", "drop-and-create", "validate", "populate")) {
            exercise(action, true, false, false, null, EntryPoint.CONFIGURATION_SCHEMA);
        }
    }

    @Test
    public void configurationNoneWritesScriptsWithoutLoading() throws Exception {
        exercise("none", false, false, true, "create", EntryPoint.CONFIGURATION_SCHEMA);
        exercise("none", true, false, true, "drop-and-create", EntryPoint.CONFIGURATION_SCHEMA);
    }

    @Test
    public void configurationValidationFailureDoesNotLoad() throws Exception {
        exercise("validate", false, true, true, null, EntryPoint.CONFIGURATION_SCHEMA);
        exercise("validate", true, true, true, null, EntryPoint.CONFIGURATION_SCHEMA);
    }

    @Test
    public void configurationDefaultActionPreservesExistingLoadBehavior() throws Exception {
        exercise(null, false, false, false, null, EntryPoint.CONFIGURATION_SCHEMA);
    }

    @Test
    public void configurationDefaultAllowsSystemActionAndExplicitNoneOverridesIt() throws Exception {
        String name = PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION;
        String previous = System.getProperty(name);
        try {
            System.setProperty(name, "populate");
            exercise(null, false, false, false, null, EntryPoint.CONFIGURATION_SCHEMA);
            exercise("none", false, false, true, null, EntryPoint.CONFIGURATION_SCHEMA);
        } finally {
            if (previous == null) {
                System.clearProperty(name);
            } else {
                System.setProperty(name, previous);
            }
        }
    }

    @Test
    public void configurationFacadeWritesScriptsOfflineAndDeploysExactlyOnce() throws Exception {
        StartupProbe probe = new StartupProbe();
        NoConnection connector = new NoConnection();
        StringWriter create = new StringWriter();
        StringWriter drop = new StringWriter();
        try {
            PersistenceConfiguration configuration = offlineConfiguration(probe, connector)
                    .schemaManagementScriptsAction(SchemaManagementAction.DROP_AND_CREATE)
                    .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, create)
                    .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, drop)
                    .property(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
            Persistence.generateSchema(configuration);
            assertCreateScript(create.toString(), "ACTION_VALUE");
            Assert.assertEquals("Explicit export must run each schema phase once", 1,
                    occurrences(create.toString(), "CREATE TABLE"));
            Assert.assertEquals(1, occurrences(drop.toString(), "DROP TABLE"));
            Assert.assertTrue(drop.toString().toUpperCase(Locale.ROOT).contains(TABLE));
            Assert.assertEquals("Script-only export must not acquire a connection", 0, connector.connections.get());
        } finally {
            probe.restore();
        }
    }

    @Test
    public void repeatedConfigurationExportsUseCurrentTargetsActionsAndMappingResources() throws Exception {
        StartupProbe probe = new StartupProbe();
        NoConnection connector = new NoConnection();
        StringWriter first = new StringWriter();
        StringWriter second = new StringWriter();
        StringWriter dropped = new StringWriter();
        AtomicInteger resolutions = new AtomicInteger();
        String mapping = "META-INF/schema-export/mapping.xml";
        ClassLoader loader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                if (mapping.equals(name)) {
                    resolutions.incrementAndGet();
                }
                return super.getResources(name);
            }
        };
        PersistenceConfiguration configuration = offlineConfiguration(probe, connector)
                .property(PersistenceUnitProperties.CLASSLOADER, loader)
                .schemaManagementScriptsAction(SchemaManagementAction.CREATE)
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, first);
        try {
            configuration.exportSchema();
            String firstOutput = first.toString();
            assertCreateScript(firstOutput, "ACTION_VALUE");
            probe.restore();
            probe = new StartupProbe();
            configuration.property(PersistenceUnitProperties.SESSION_CUSTOMIZER, probe)
                    .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, second)
                    .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, dropped)
                    .schemaManagementScriptsAction(SchemaManagementAction.DROP_AND_CREATE)
                    .mappingFile(mapping);
            configuration.exportSchema();
            assertCreateScript(second.toString(), "EXPORTED_VALUE");
            Assert.assertFalse(second.toString().contains("ACTION_VALUE"));
            Assert.assertTrue("The configured loader must resolve the mapping resource", resolutions.get() > 0);
            Assert.assertEquals("The earlier target must not be used again", firstOutput, first.toString());
            Assert.assertEquals(1, occurrences(dropped.toString(), "DROP TABLE"));
            Assert.assertEquals(0, connector.connections.get());
        } finally {
            probe.restore();
        }
    }

    @Test
    public void configurationFailureReleasesItsSetupAndAllowsCorrectedRequest() throws Exception {
        StartupProbe probe = new StartupProbe();
        try (Fixture fixture = new Fixture()) {
            fixture.prepareSentinel();
            PersistenceConfiguration configuration = configuration("schema-failure-" + IDENTITIES.incrementAndGet(),
                    properties(getClass().getClassLoader(), probe))
                    .schemaManagementDatabaseAction(SchemaManagementAction.VALIDATE)
                    .property(PersistenceUnitProperties.JDBC_DRIVER, "no.such.SchemaExportDriver");
            var setups = new HashSet<>(EntityManagerFactoryProvider.getEmSetupImpls().keySet());
            Assert.assertThrows(PersistenceException.class, configuration::exportSchema);
            Assert.assertEquals("Failed export must release its setup", setups,
                    EntityManagerFactoryProvider.getEmSetupImpls().keySet());
            probe.restore();
            probe = new StartupProbe();
            configuration.properties().remove(PersistenceUnitProperties.JDBC_DRIVER);
            configuration.property(PersistenceUnitProperties.SESSION_CUSTOMIZER, probe);
            configuration.exportSchema();
            Assert.assertEquals("Successful export must release its temporary setup", setups,
                    EntityManagerFactoryProvider.getEmSetupImpls().keySet());
            Assert.assertEquals(1, fixture.count(SENTINEL));
        } finally {
            probe.restore();
        }
    }

    @Test
    public void schemaExportPreservesCallerOwnedConnection() throws Exception {
        for (EntryPoint entryPoint : List.of(EntryPoint.CONFIGURATION_SCHEMA, EntryPoint.NAMED_SCHEMA,
                EntryPoint.CONTAINER_SCHEMA)) {
            callerOwnedConnection(entryPoint, false, false);
        }
    }

    @Test
    public void failedSchemaExportPreservesCallerOwnedConnection() throws Exception {
        for (EntryPoint entryPoint : List.of(EntryPoint.CONFIGURATION_SCHEMA, EntryPoint.NAMED_SCHEMA,
                EntryPoint.CONTAINER_SCHEMA)) {
            callerOwnedConnection(entryPoint, true, false);
        }
    }

    @Test
    public void schemaExportInfersDatabaseMetadataFromCallerOwnedConnection() throws Exception {
        for (EntryPoint entryPoint : List.of(EntryPoint.CONFIGURATION_SCHEMA, EntryPoint.NAMED_SCHEMA,
                EntryPoint.CONTAINER_SCHEMA)) {
            callerOwnedConnection(entryPoint, false, true);
        }
    }

    @Test
    public void suppliedSchemaExportPreservesLazyFactories() throws Exception {
        for (EntryPoint entryPoint : List.of(EntryPoint.CONFIGURATION_SCHEMA, EntryPoint.NAMED_SCHEMA,
                EntryPoint.CONTAINER_SCHEMA)) {
            retainedLazyFactory(entryPoint, false, false);
        }
    }

    @Test
    public void failedSuppliedSchemaExportPreservesLazyFactories() throws Exception {
        for (EntryPoint entryPoint : List.of(EntryPoint.CONFIGURATION_SCHEMA, EntryPoint.NAMED_SCHEMA,
                EntryPoint.CONTAINER_SCHEMA)) {
            retainedLazyFactory(entryPoint, true, false);
        }
    }

    @Test
    public void suppliedSchemaExportPreservesLazyNativeSessions() throws Exception {
        retainedLazyFactory(EntryPoint.CONTAINER_SCHEMA, false, true);
        retainedLazyFactory(EntryPoint.CONTAINER_SCHEMA, true, true);
    }

    private static void retainedLazyFactory(EntryPoint entryPoint, boolean fail, boolean nativeXml) throws Exception {
        boolean containerFlag = JavaSECMPInitializer.isInContainer();
        String target = System.getProperty(PersistenceUnitProperties.TARGET_DATABASE);
        StartupProbe probe = new StartupProbe();
        try (Fixture fixture = new Fixture()) {
            fixture.prepareSentinel();
            try (EntityManager caller = fixture.factory.createEntityManager()) {
                caller.getTransaction().begin();
                try {
                    caller.createNativeQuery("SELECT COUNT(*) FROM " + TABLE).getSingleResult();
                    Connection connection = caller.unwrap(Connection.class);
                    ConnectionGate connector = new ConnectionGate(session(fixture.factory).getLogin().getConnector());
                    List<Session> sessions = new ArrayList<>();
                    Map<String, Object> values = properties(TestSchemaGen.class.getClassLoader(), probe);
                    values.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, (SessionCustomizer) sessions::add);
                    values.put(PersistenceUnitProperties.JDBC_CONNECTOR, connector);
                    values.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
                    values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none");
                    String name = "schema-lazy-" + IDENTITIES.incrementAndGet();
                    SEPersistenceUnitInfo info = persistenceUnit(name, TestSchemaGen.class.getClassLoader());
                    info.setNewTempClassLoader(TestSchemaGen.class.getClassLoader());
                    if (nativeXml) {
                        values.put(PersistenceUnitProperties.SESSIONS_XML, "META-INF/schema-export/sessions.xml");
                        values.put(PersistenceUnitProperties.SESSION_NAME, "SchemaExportXml");
                        info.setManagedClassNames(List.of());
                        System.clearProperty(PersistenceUnitProperties.TARGET_DATABASE);
                    }
                    if (entryPoint == EntryPoint.CONFIGURATION_SCHEMA && !fail) {
                        // Identical effective configuration: the retained factory owns this original VALIDATE action.
                        values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "validate");
                        values.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection);
                    }
                    PersistenceConfiguration configuration = configuration(name, values);
                    PersistenceProvider provider = new PersistenceProvider();
                    EntityManagerFactory factory = switch (entryPoint) {
                        case CONFIGURATION_SCHEMA -> configuration.createEntityManagerFactory();
                        case CONTAINER_SCHEMA -> provider.createContainerEntityManagerFactory(info, values);
                        case NAMED_SCHEMA -> {
                            values.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
                            yield provider.createEntityManagerFactory(name, values);
                        }
                        default -> throw new AssertionError("Unsupported lazy factory entry point");
                    };
                    try (factory) {
                        var setup = ((EntityManagerFactoryImpl) factory).unwrap().getSetupImpl();
                        ServerSession retained = (ServerSession) setup.getSession();
                        String retainedName = retained.getName();
                        int references = setup.getFactoryCount();
                        var setups = new HashMap<>(EntityManagerFactoryProvider.getEmSetupImpls());
                        var registered = new HashMap<>(SessionManager.getManager().getSessions());
                        Assert.assertFalse(retained.isLoggedIn());
                        Assert.assertFalse(retained.getDefaultConnectionPool().isConnected());
                        Map<String, Object> export = new HashMap<>(values);
                        export.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
                        export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection);
                        export.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, fail ? "create" : "validate");
                        StringWriter nativeScript = new StringWriter();
                        if (nativeXml && !fail) {
                            // The native marker is a script dialect, not the dialect of every validation database.
                            export.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none");
                            export.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, "create");
                            export.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, nativeScript);
                        }
                        if (fail) {
                            export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SOURCE, "script");
                            export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE,
                                    new ProbeReader("", true));
                        }
                        Runnable generate = switch (entryPoint) {
                            case CONFIGURATION_SCHEMA -> !fail ? configuration::exportSchema
                                    : configuration(name, export)::exportSchema;
                            case CONTAINER_SCHEMA -> () -> provider.generateSchema(info, export);
                            case NAMED_SCHEMA -> () -> provider.generateSchema(name, export);
                            default -> throw new AssertionError("Unsupported schema entry point");
                        };
                        if (fail) {
                            Assert.assertThrows(PersistenceException.class, generate::run);
                        } else {
                            generate.run();
                        }
                        Assert.assertEquals("Only the temporary schema session is customized", 1, sessions.size());
                        Assert.assertNotSame(retained, sessions.getFirst());
                        if (nativeXml && !fail) {
                            assertCreateScript(nativeScript.toString(), "ACTION_VALUE");
                            Assert.assertTrue(sessions.getFirst().getPlatform() instanceof XmlSchemaPlatform);
                        }
                        Assert.assertSame(setup, ((EntityManagerFactoryImpl) factory).unwrap().getSetupImpl());
                        Assert.assertSame(retained, setup.getSession());
                        Assert.assertEquals(retainedName, retained.getName());
                        Assert.assertEquals(references, setup.getFactoryCount());
                        Assert.assertEquals(setups, EntityManagerFactoryProvider.getEmSetupImpls());
                        Assert.assertEquals(registered, SessionManager.getManager().getSessions());
                        Assert.assertFalse(retained.isLoggedIn());
                        Assert.assertFalse(retained.getDefaultConnectionPool().isConnected());
                        Assert.assertEquals(0, connector.connections.get());
                        connector.forbidden.set(false);
                        try (EntityManager manager = factory.createEntityManager()) {
                            manager.getTransaction().begin();
                            try {
                                Assert.assertNotNull(manager.find(SchemaGenerationEntity.class, SENTINEL));
                                manager.createNativeQuery(insert(LOADED, "retained lazy factory")).executeUpdate();
                                manager.getTransaction().commit();
                            } finally {
                                if (manager.getTransaction().isActive()) {
                                    manager.getTransaction().rollback();
                                }
                            }
                        }
                        try (var agent = factory.createEntityAgent()) {
                            Assert.assertNotNull(agent.find(SchemaGenerationEntity.class, LOADED));
                        }
                        Assert.assertTrue(connector.connections.get() > 0);
                        Assert.assertTrue(retained.getDefaultConnectionPool().isConnected());
                        Assert.assertEquals(2, sessions.size());
                        Assert.assertSame(retained, sessions.getLast());
                        if (nativeXml && !fail) {
                            Assert.assertEquals("The retained factory must not replay export scripts", 1,
                                    occurrences(nativeScript.toString(), "CREATE TABLE " + TABLE));
                        }
                        Assert.assertFalse(connection.isClosed());
                        Assert.assertFalse(connection.getAutoCommit());
                    }
                } finally {
                    caller.getTransaction().rollback();
                }
            }
            Assert.assertEquals(1, fixture.count(SENTINEL));
            Assert.assertEquals(1, fixture.count(LOADED));
        } finally {
            if (target != null) {
                System.setProperty(PersistenceUnitProperties.TARGET_DATABASE, target);
            }
            probe.restore();
            JavaSECMPInitializer.setIsInContainer(containerFlag);
        }
    }

    @Test
    public void suppliedSchemaExportDoesNotConsumeContainerTransformerPredeployment() throws Exception {
        boolean containerFlag = JavaSECMPInitializer.isInContainer();
        StartupProbe probe = new StartupProbe();
        try (Fixture fixture = new Fixture()) {
            fixture.prepareSentinel();
            try (EntityManager caller = fixture.factory.createEntityManager()) {
                caller.getTransaction().begin();
                try {
                    caller.createNativeQuery("SELECT COUNT(*) FROM " + TABLE).getSingleResult();
                    ConnectionGate connector = new ConnectionGate(session(fixture.factory).getLogin().getConnector());
                    Map<String, Object> values = properties(TestSchemaGen.class.getClassLoader(), probe);
                    values.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, (SessionCustomizer) ignored -> { });
                    values.put(PersistenceUnitProperties.JDBC_CONNECTOR, connector);
                    values.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
                    values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none");
                    String name = "schema-transformer-" + IDENTITIES.incrementAndGet();
                    values.put(PersistenceUnitProperties.SESSION_NAME, name);
                    SEPersistenceUnitInfo info = persistenceUnit(name, TestSchemaGen.class.getClassLoader());
                    info.setNewTempClassLoader(TestSchemaGen.class.getClassLoader());
                    PersistenceProvider provider = new PersistenceProvider();
                    provider.getClassTransformer(info, values);
                    var setup = EntityManagerFactoryProvider.getEntityManagerSetupImpl(name);
                    try {
                        Assert.assertNotNull(setup);
                        Assert.assertEquals(1, setup.getFactoryCount());
                        Session original = setup.getSession();
                        Map<String, Object> export = new HashMap<>(values);
                        export.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
                        export.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "validate");
                        export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, caller.unwrap(Connection.class));
                        provider.generateSchema(info, export);
                        Assert.assertSame(setup, EntityManagerFactoryProvider.getEntityManagerSetupImpl(name));
                        Assert.assertSame(original, setup.getSession());
                        Assert.assertEquals(1, setup.getFactoryCount());
                        Assert.assertEquals(0, connector.connections.get());
                        try (EntityManagerFactory factory = provider.createContainerEntityManagerFactory(info, values)) {
                            Assert.assertSame(setup, ((EntityManagerFactoryImpl) factory).unwrap().getSetupImpl());
                            Assert.assertEquals("Only the real factory takes over transformer predeployment", 1,
                                    setup.getFactoryCount());
                            connector.forbidden.set(false);
                            try (var agent = factory.createEntityAgent()) {
                                Assert.assertNotNull(agent.find(SchemaGenerationEntity.class, SENTINEL));
                            }
                            Assert.assertTrue(connector.connections.get() > 0);
                        }
                        Assert.assertFalse(EntityManagerFactoryProvider.getEmSetupImpls().containsKey(name));
                    } finally {
                        if (setup != null && !setup.isUndeployed()) {
                            setup.undeploy();
                        }
                    }
                } finally {
                    caller.getTransaction().rollback();
                }
            }
        } finally {
            probe.restore();
            JavaSECMPInitializer.setIsInContainer(containerFlag);
        }
    }

    @Test
    public void failedSuppliedSchemaExportReleasesOnlyItsDynamicTransformerMetadata() throws Exception {
        Path directory = Files.createTempDirectory("schema-dynamic-weaving-");
        boolean containerFlag = JavaSECMPInitializer.isInContainer();
        StartupProbe probe = new StartupProbe();
        try {
            Path source = directory.resolve("SchemaDynamicEntity.java");
            Files.writeString(source, """
                    package schema.dynamic;
                    @jakarta.persistence.Entity
                    @jakarta.persistence.Table(name="JPA_SCHEMA_ACTION")
                    public class SchemaDynamicEntity {
                        @jakarta.persistence.Id
                        @jakarta.persistence.Column(name="ACTION_ID")
                        private int id;
                        @jakarta.persistence.Column(name="ACTION_VALUE", length=40)
                        private String value;
                        public SchemaDynamicEntity() {}
                    }
                    """);
            String api = Path.of(Entity.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
            var compiler = ToolProvider.getSystemJavaCompiler();
            Assert.assertNotNull("This regression requires the validation JDK's compiler", compiler);
            Assert.assertEquals(0, compiler.run(null, null, null, "-proc:none", "--release", "21", "-classpath", api,
                    "-d", directory.toString(), source.toString()));
            try (DynamicSchemaLoader loader = new DynamicSchemaLoader(directory);
                    URLClassLoader temporaryLoader = new URLClassLoader(new URL[] {directory.toUri().toURL()},
                            TestSchemaGen.class.getClassLoader());
                    Fixture fixture = new Fixture()) {
                fixture.prepareSentinel();
                try (EntityManager caller = fixture.factory.createEntityManager()) {
                    caller.getTransaction().begin();
                    try {
                        caller.createNativeQuery("SELECT COUNT(*) FROM " + TABLE).getSingleResult();
                        ConnectionGate connector = new ConnectionGate(session(fixture.factory).getLogin().getConnector());
                        String name = "schema-dynamic-" + IDENTITIES.incrementAndGet();
                        WeavingUnitInfo info = new WeavingUnitInfo(loader);
                        info.setPersistenceUnitName(name);
                        info.setClassLoader(loader);
                        info.setNewTempClassLoader(temporaryLoader);
                        info.setPersistenceUnitRootUrl(directory.toUri().toURL());
                        info.setManagedClassNames(List.of(DynamicSchemaLoader.ENTITY));
                        info.setMappingFileNames(List.of());
                        info.setExcludeUnlistedClasses(true);
                        info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
                        info.setValidationMode("NONE");
                        info.setProperties(new Properties());
                        Map<String, Object> values = properties(loader, probe);
                        values.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, (SessionCustomizer) ignored -> { });
                        values.put(PersistenceUnitProperties.JDBC_CONNECTOR, connector);
                        values.put(PersistenceUnitProperties.WEAVING, "true");
                        values.put(PersistenceUnitProperties.SESSION_NAME, name);
                        values.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
                        values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none");
                        PersistenceProvider provider = new PersistenceProvider();
                        PersistenceWeaver original = (PersistenceWeaver) provider.getClassTransformer(info, values);
                        loader.transformers.add(original);
                        var metadata = original.getClassDetailsMap();
                        Assert.assertFalse(metadata.isEmpty());
                        var setup = EntityManagerFactoryProvider.getEntityManagerSetupImpl(name);
                        Session retained = setup.getSession();
                        try {
                            for (int attempt = 0; attempt < 2; attempt++) {
                                Map<String, Object> export = new HashMap<>(values);
                                export.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
                                export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, caller.unwrap(Connection.class));
                                export.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "create");
                                export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SOURCE, "script");
                                export.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE, new ProbeReader("", true));
                                PersistenceException failure = Assert.assertThrows(PersistenceException.class,
                                        () -> provider.generateSchema(info, export));
                                Assert.assertTrue(getBaseException(failure).getMessage().contains("Forbidden schema-action"));
                                Assert.assertEquals(attempt + 1, info.added.size());
                                Assert.assertTrue("The actual temporary transformer must initially retain metadata",
                                        info.metadataSizes.get(attempt) > 0);
                                Assert.assertNull("Failed schema disposal must clear the temporary transformer",
                                        info.added.get(attempt).getClassDetailsMap());
                                Assert.assertSame(metadata, original.getClassDetailsMap());
                                Assert.assertFalse(metadata.isEmpty());
                                Assert.assertSame(setup, EntityManagerFactoryProvider.getEntityManagerSetupImpl(name));
                                Assert.assertSame(retained, setup.getSession());
                                Assert.assertEquals(1, setup.getFactoryCount());
                                Assert.assertEquals(0, connector.connections.get());
                            }
                            connector.forbidden.set(false);
                            try (EntityManagerFactory factory = provider.createContainerEntityManagerFactory(info, values)) {
                                Assert.assertEquals(1, setup.getFactoryCount());
                                try (EntityManager manager = factory.createEntityManager()) {
                                    Class<?> entity = loader.loadClass(DynamicSchemaLoader.ENTITY);
                                    Assert.assertTrue("The fixture must be dynamically woven", PersistenceWeaved.class.isAssignableFrom(entity));
                                    Assert.assertEquals(1, loader.transformations);
                                    Assert.assertNotNull(manager.find(entity, SENTINEL));
                                    manager.getTransaction().begin();
                                    try {
                                        manager.createNativeQuery(insert(LOADED, "dynamic retained factory")).executeUpdate();
                                        manager.getTransaction().commit();
                                    } finally {
                                        if (manager.getTransaction().isActive()) {
                                            manager.getTransaction().rollback();
                                        }
                                    }
                                }
                            }
                            Assert.assertFalse(EntityManagerFactoryProvider.getEmSetupImpls().containsKey(name));
                        } finally {
                            if (!setup.isUndeployed()) {
                                setup.undeploy();
                            }
                        }
                    } finally {
                        caller.getTransaction().rollback();
                    }
                }
                Assert.assertEquals(1, fixture.count(LOADED));
            }
        } finally {
            probe.restore();
            JavaSECMPInitializer.setIsInContainer(containerFlag);
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    private static final class DynamicSchemaLoader extends URLClassLoader {
        private static final String ENTITY = "schema.dynamic.SchemaDynamicEntity";
        private final List<ClassTransformer> transformers = new ArrayList<>();
        private int transformations;

        private DynamicSchemaLoader(Path directory) throws IOException {
            super(new URL[] {directory.toUri().toURL()}, TestSchemaGen.class.getClassLoader());
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (!ENTITY.equals(name)) {
                return super.findClass(name);
            }
            String internalName = name.replace('.', '/');
            try (var input = findResource(internalName + ".class").openStream()) {
                byte[] bytes = input.readAllBytes();
                for (ClassTransformer transformer : transformers) {
                    byte[] transformed = transformer.transform(this, internalName, null, null, bytes);
                    if (transformed != null) {
                        bytes = transformed;
                        transformations++;
                    }
                }
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception failure) {
                throw new ClassNotFoundException(name, failure);
            }
        }
    }

    private static final class WeavingUnitInfo extends SEPersistenceUnitInfo {
        private final DynamicSchemaLoader loader;
        private final List<PersistenceWeaver> added = new ArrayList<>();
        private final List<Integer> metadataSizes = new ArrayList<>();

        private WeavingUnitInfo(DynamicSchemaLoader loader) {
            this.loader = loader;
        }

        @Override
        public void addTransformer(ClassTransformer transformer) {
            PersistenceWeaver weaver = (PersistenceWeaver) transformer;
            added.add(weaver);
            metadataSizes.add(weaver.getClassDetailsMap().size());
            loader.transformers.add(transformer);
        }
    }

    @Test
    public void suppliedSchemaExportRetainsTableSequenceMetadataWithoutStartingItsPool() throws Exception {
        StartupProbe probe = new StartupProbe();
        try (Fixture fixture = new Fixture()) {
            fixture.prepareSentinel();
            TableDefinition entityTable = new TableDefinition();
            entityTable.setName("JPA_SCHEMA_GENERATED");
            TableDefinition sequenceTable = new TableDefinition();
            sequenceTable.setName("JPA_SCHEMA_SEQUENCE");
            Assert.assertFalse("Refusing to acquire a pre-existing entity table", fixture.schema.checkTableExists(entityTable));
            Assert.assertFalse("Refusing to acquire a pre-existing sequence table", fixture.schema.checkTableExists(sequenceTable));
            try {
                ConnectionGate connector = new ConnectionGate(session(fixture.factory).getLogin().getConnector());
                List<Session> sessions = new ArrayList<>();
                Map<String, Object> values = properties(TestSchemaGen.class.getClassLoader(), probe);
                values.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, (SessionCustomizer) sessions::add);
                values.put(PersistenceUnitProperties.JDBC_CONNECTOR, connector);
                values.put(PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL, "true");
                values.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
                values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none");
                String name = "schema-table-sequence-" + IDENTITIES.incrementAndGet();
                PersistenceConfiguration original = new PersistenceConfiguration(name)
                        .managedClass(SchemaTableEntity.class).validationMode(ValidationMode.NONE)
                        .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL).properties(values);
                try (EntityManagerFactory factory = original.createEntityManagerFactory()) {
                    for (SchemaManagementAction action : List.of(SchemaManagementAction.CREATE, SchemaManagementAction.DROP_AND_CREATE)) {
                        try (EntityManager caller = fixture.factory.createEntityManager()) {
                            caller.getTransaction().begin();
                            try {
                                caller.createNativeQuery(insert(LOADED, "caller transaction")).executeUpdate();
                                Connection connection = caller.unwrap(Connection.class);
                                int isolation = connection.getTransactionIsolation();
                                StringWriter script = new StringWriter();
                                StringWriter dropScript = new StringWriter();
                                PersistenceConfiguration export = new PersistenceConfiguration(name)
                                        .managedClass(SchemaTableEntity.class).validationMode(ValidationMode.NONE)
                                        .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL).properties(values)
                                        .property(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection)
                                        .schemaManagementScriptsAction(action)
                                        .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, script)
                                        .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, dropScript);
                                export.exportSchema();
                                String ddl = script.toString().toUpperCase(Locale.ROOT);
                                Assert.assertTrue(ddl.contains("CREATE TABLE JPA_SCHEMA_GENERATED"));
                                Assert.assertTrue(ddl.contains("CREATE TABLE JPA_SCHEMA_SEQUENCE"));
                                Assert.assertTrue(ddl.contains("INSERT INTO JPA_SCHEMA_SEQUENCE"));
                                Assert.assertTrue(ddl.contains("SEQUENCE_NAME"));
                                Assert.assertTrue(ddl.contains("SEQUENCE_VALUE"));
                                if (action == SchemaManagementAction.DROP_AND_CREATE) {
                                    Assert.assertTrue(dropScript.toString().toUpperCase(Locale.ROOT)
                                            .contains("DROP TABLE JPA_SCHEMA_GENERATED"));
                                }
                                Assert.assertEquals(0, connector.connections.get());
                                Assert.assertTrue(((DatabaseSessionImpl) sessions.getLast()).getSequencingControl()
                                        .shouldUseSeparateConnection());
                                Assert.assertFalse(connection.isClosed());
                                Assert.assertFalse(connection.getAutoCommit());
                                Assert.assertEquals(isolation, connection.getTransactionIsolation());
                                Assert.assertSame(connection, caller.unwrap(Connection.class));
                                try (var statement = connection.createStatement();
                                        var rows = statement.executeQuery("SELECT COUNT(*) FROM " + TABLE)) {
                                    Assert.assertTrue(rows.next());
                                    Assert.assertEquals("Script export must leave the caller's transaction usable", 2, rows.getInt(1));
                                }
                            } finally {
                                caller.getTransaction().rollback();
                            }
                        }
                        Assert.assertEquals("Script export must not commit caller work", 0, fixture.count(LOADED));
                    }
                    // The caller chooses autocommit for DDL; transaction ownership is tested above without database DDL.
                    DatabaseSessionImpl fixtureSession = session(fixture.factory);
                    try (Connection connection = (Connection) fixtureSession.getLogin().connectToDatasource(null, fixtureSession)) {
                        connection.setAutoCommit(true);
                        int isolation = connection.getTransactionIsolation();
                        new PersistenceConfiguration(name).managedClass(SchemaTableEntity.class)
                                .validationMode(ValidationMode.NONE).transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                                .properties(values)
                                .property(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection)
                                .property(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "create")
                                .exportSchema();
                        Assert.assertFalse(connection.isClosed());
                        Assert.assertTrue(connection.getAutoCommit());
                        Assert.assertEquals(isolation, connection.getTransactionIsolation());
                        try (var statement = connection.createStatement();
                                var rows = statement.executeQuery("SELECT SEQUENCE_VALUE FROM JPA_SCHEMA_SEQUENCE WHERE SEQUENCE_NAME = 'schema'")) {
                            Assert.assertTrue("Schema export must create the allocation seed", rows.next());
                            Assert.assertEquals(0L, rows.getLong(1));
                        }
                    }
                    Assert.assertEquals(0, connector.connections.get());
                    Assert.assertTrue(fixture.schema.checkTableExists(entityTable));
                    Assert.assertTrue(fixture.schema.checkTableExists(sequenceTable));
                    connector.forbidden.set(false);
                    SchemaTableEntity entity = new SchemaTableEntity();
                    try (EntityManager manager = factory.createEntityManager()) {
                        manager.getTransaction().begin();
                        try {
                            manager.persist(entity);
                            manager.getTransaction().commit();
                        } finally {
                            if (manager.getTransaction().isActive()) {
                                manager.getTransaction().rollback();
                            }
                        }
                    }
                    Assert.assertNotNull("Ordinary sequencing must allocate an identifier", entity.id);
                    try (var agent = factory.createEntityAgent()) {
                        Assert.assertNotNull(agent.find(SchemaTableEntity.class, entity.id));
                    }
                    Assert.assertTrue(session(factory).getSequencingControl().shouldUseSeparateConnection());
                    Assert.assertTrue(connector.connections.get() > 0);
                }
            } finally {
                if (fixture.schema.checkTableExists(entityTable)) {
                    fixture.schema.dropTable(entityTable.getName());
                }
                if (fixture.schema.checkTableExists(sequenceTable)) {
                    fixture.schema.dropTable(sequenceTable.getName());
                }
                Assert.assertFalse(fixture.schema.checkTableExists(entityTable));
                Assert.assertFalse(fixture.schema.checkTableExists(sequenceTable));
            }
        } finally {
            probe.restore();
        }
    }

    @Entity
    @Table(name = "JPA_SCHEMA_GENERATED")
    public static class SchemaTableEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "schemaTableGenerator")
        @TableGenerator(name = "schemaTableGenerator", table = "JPA_SCHEMA_SEQUENCE",
                pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE",
                pkColumnValue = "schema", allocationSize = 5)
        @Column(name = "GENERATED_ID")
        private Long id;

        public SchemaTableEntity() {
        }
    }

    private static final class ConnectionGate extends DefaultConnector {
        private final Connector delegate;
        private final AtomicInteger connections = new AtomicInteger();
        private final AtomicBoolean forbidden = new AtomicBoolean(true);

        private ConnectionGate(Connector delegate) {
            this.delegate = delegate;
        }

        @Override
        public Connection connect(Properties properties, Session session) {
            connections.incrementAndGet();
            if (forbidden.get()) {
                throw new AssertionError("Schema export must not acquire an ordinary connection");
            }
            return delegate.connect(properties, session);
        }
    }

    @Test
    public void sessionsXmlSchemaExportPreservesTheConfiguredPlatform() throws Exception {
        for (boolean supplied : new boolean[] {false, true}) {
            StartupProbe probe = new StartupProbe();
            boolean containerFlag = JavaSECMPInitializer.isInContainer();
            String target = System.getProperty(PersistenceUnitProperties.TARGET_DATABASE);
            try (Fixture fixture = new Fixture()) {
                fixture.prepareSentinel();
                try (EntityManager manager = fixture.factory.createEntityManager()) {
                    manager.getTransaction().begin();
                    try {
                        manager.createNativeQuery("SELECT COUNT(*) FROM " + TABLE).getSingleResult();
                        Connection connection = manager.unwrap(Connection.class);
                        NoConnection normalConnector = new NoConnection();
                        Map<String, Object> values = properties(TestSchemaGen.class.getClassLoader(), probe);
                        values.put(PersistenceUnitProperties.SESSIONS_XML, "META-INF/schema-export/sessions.xml");
                        values.put(PersistenceUnitProperties.SESSION_NAME, "SchemaExportXml");
                        values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none");
                        values.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, "create");
                        StringWriter script = new StringWriter();
                        values.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, script);
                        if (supplied) {
                            values.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection);
                            values.put(PersistenceUnitProperties.JDBC_CONNECTOR, normalConnector);
                        }
                        SEPersistenceUnitInfo info = persistenceUnit("schema-xml-" + IDENTITIES.incrementAndGet(),
                                TestSchemaGen.class.getClassLoader());
                        info.setNewTempClassLoader(TestSchemaGen.class.getClassLoader());
                        info.setManagedClassNames(List.of());
                        // Only the native XML selects the script dialect for this request.
                        System.clearProperty(PersistenceUnitProperties.TARGET_DATABASE);
                        try {
                            new PersistenceProvider().generateSchema(info, values);
                        } finally {
                            if (target != null) {
                                System.setProperty(PersistenceUnitProperties.TARGET_DATABASE, target);
                            }
                        }
                        assertCreateScript(script.toString(), "ACTION_VALUE");
                        Assert.assertTrue("Native XML platform must be preserved",
                                probe.session.getPlatform() instanceof XmlSchemaPlatform);
                        XmlSchemaPlatform platform = (XmlSchemaPlatform) probe.session.getPlatform();
                        Assert.assertTrue("Connection data must initialize the selected platform", platform.initializations > 0);
                        if (supplied) {
                            Assert.assertEquals(1, platform.initializations);
                            Assert.assertSame(connection, platform.connection);
                        }
                        Assert.assertEquals(0, normalConnector.connections.get());
                        Assert.assertFalse(connection.isClosed());
                        Assert.assertFalse(connection.getAutoCommit());
                    } finally {
                        manager.getTransaction().rollback();
                    }
                }
                Assert.assertEquals(1, fixture.count(SENTINEL));
            } finally {
                probe.restore();
                JavaSECMPInitializer.setIsInContainer(containerFlag);
            }
        }
    }

    /** Native script dialect deliberately selected by XML rather than JDBC metadata. */
    public static class XmlSchemaPlatform extends H2Platform {
        private int initializations;
        private Connection connection;

        @Override
        public void initializeConnectionData(Connection value) {
            initializations++;
            connection = value;
        }
    }

    public static class XmlSchemaProject extends Project {
        public XmlSchemaProject() {
            RelationalDescriptor descriptor = new RelationalDescriptor();
            descriptor.setJavaClass(SchemaGenerationEntity.class);
            descriptor.setTableName(TABLE);
            descriptor.addPrimaryKeyFieldName("ACTION_ID");
            descriptor.addDirectMapping("id", "ACTION_ID");
            descriptor.addDirectMapping("value", "ACTION_VALUE");
            addDescriptor(descriptor);
        }
    }

    private static void callerOwnedConnection(EntryPoint entryPoint, boolean invalidMapping, boolean detectPlatform) throws Exception {
        StartupProbe probe = new StartupProbe();
        boolean containerFlag = JavaSECMPInitializer.isInContainer();
        try (Fixture fixture = new Fixture()) {
            fixture.prepareSentinel();
            try (EntityManager manager = fixture.factory.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    manager.createNativeQuery(insert(LOADED, "caller transaction")).executeUpdate();
                    Connection connection = manager.unwrap(Connection.class);
                    Assert.assertNotNull(connection);
                    Assert.assertFalse(connection.getAutoCommit());
                    int isolation = connection.getTransactionIsolation();
                    boolean readOnly = connection.isReadOnly();
                    String name = "schema-connection-" + IDENTITIES.incrementAndGet();
                    Map<String, Object> values = properties(TestSchemaGen.class.getClassLoader(), probe);
                    NoConnection normalConnector = new NoConnection();
                    values.put(PersistenceUnitProperties.JDBC_CONNECTOR, normalConnector);
                    values.put(PersistenceUnitProperties.TARGET_DATABASE,
                            detectPlatform ? "Auto" : session(fixture.factory).getPlatform().getClass().getName());
                    values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "validate");
                    values.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION, connection);
                    SEPersistenceUnitInfo info = persistenceUnit(name, TestSchemaGen.class.getClassLoader());
                    PersistenceConfiguration configuration = configuration(name, values);
                    if (invalidMapping) {
                        String mapping = "META-INF/schema-export/mapping.xml";
                        configuration.mappingFile(mapping);
                        info.setMappingFileNames(List.of(mapping));
                    }
                    Runnable export = switch (entryPoint) {
                        case CONFIGURATION_SCHEMA -> configuration::exportSchema;
                        case NAMED_SCHEMA -> () -> {
                            values.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
                            Persistence.generateSchema(name, values);
                        };
                        case CONTAINER_SCHEMA -> () -> new PersistenceProvider().generateSchema(info, values);
                        default -> throw new AssertionError("Unsupported schema entry point");
                    };
                    var setups = new HashSet<>(EntityManagerFactoryProvider.getEmSetupImpls().keySet());
                    if (invalidMapping) {
                        PersistenceException failure = Assert.assertThrows(PersistenceException.class, export::run);
                        Assert.assertTrue("Missing mapped column must fail schema validation",
                                getBaseException(failure) instanceof SchemaValidationException);
                    } else {
                        export.run();
                    }
                    Assert.assertEquals("A supplied schema connection must avoid the normal connector", 0,
                            normalConnector.connections.get());
                    Assert.assertEquals("Schema export must release its temporary setup", setups,
                            EntityManagerFactoryProvider.getEmSetupImpls().keySet());
                    Assert.assertFalse("Schema export must not close the caller's connection", connection.isClosed());
                    Assert.assertFalse("Schema export must preserve the caller's transaction", connection.getAutoCommit());
                    Assert.assertEquals(isolation, connection.getTransactionIsolation());
                    Assert.assertEquals(readOnly, connection.isReadOnly());
                    Assert.assertSame(connection, manager.unwrap(Connection.class));
                    try (var statement = connection.createStatement();
                            var result = statement.executeQuery("SELECT COUNT(*) FROM " + TABLE)) {
                        Assert.assertTrue(result.next());
                        Assert.assertEquals(2, result.getInt(1));
                    }
                } finally {
                    manager.getTransaction().rollback();
                }
            }
            Assert.assertEquals("Schema export must not commit caller work", 0, fixture.count(LOADED));
        } finally {
            probe.restore();
            JavaSECMPInitializer.setIsInContainer(containerFlag);
        }
    }

    private static PersistenceConfiguration offlineConfiguration(StartupProbe probe, NoConnection connector) {
        return configuration("schema-offline-" + IDENTITIES.incrementAndGet(),
                properties(TestSchemaGen.class.getClassLoader(), probe))
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none")
                .property(PersistenceUnitProperties.TARGET_DATABASE, "Auto")
                .property(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "H2")
                .property(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "2")
                .property(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "4")
                .property(PersistenceUnitProperties.JDBC_CONNECTOR, connector);
    }

    private static void assertCreateScript(String script, String column) {
        String output = script.toUpperCase(Locale.ROOT);
        Assert.assertTrue("Script must contain the mapped table", output.contains("CREATE TABLE " + TABLE));
        Assert.assertTrue("Script must contain the mapped identifier", output.contains("ACTION_ID"));
        Assert.assertTrue("Script must contain the current mapped value column", output.contains(column));
    }

    private static int occurrences(String script, String token) {
        return script.toUpperCase(Locale.ROOT).split(token, -1).length - 1;
    }

    private static final class NoConnection extends DefaultConnector {
        private final AtomicInteger connections = new AtomicInteger();

        @Override
        public Connection connect(Properties properties, Session session) {
            connections.incrementAndGet();
            throw new AssertionError("Script-only schema export attempted to acquire a connection");
        }
    }

    private static final String TABLE = "JPA_SCHEMA_ACTION";
    private static final int SENTINEL = 810001;
    private static final int LOADED = 810002;
    private static final AtomicLong IDENTITIES = new AtomicLong();

    private enum EntryPoint {
        NAMED_FACTORY, NAMED_SCHEMA, CONTAINER_FACTORY, CONTAINER_SCHEMA, CONFIGURATION_SCHEMA
    }

    private static void standard(String action, boolean reused) throws Exception {
        exercise(action, reused, false, false, null, EntryPoint.NAMED_FACTORY);
    }

    private static void missing(String action, boolean reused) throws Exception {
        exercise(action, reused, true, false, null, EntryPoint.NAMED_FACTORY);
    }

    /** Invocation maps, not persistent PU metadata, own every action and source. */
    private static void exercise(String action, boolean reused, boolean missing, boolean resource,
            String scripts, EntryPoint entryPoint) throws Exception {
        String identity = "schema-action-" + IDENTITIES.incrementAndGet();
        SourceLoader loader = new SourceLoader(identity + "-forbidden.sql");
        StartupProbe probe = new StartupProbe();
        boolean programmatic = entryPoint == EntryPoint.CONFIGURATION_SCHEMA;
        StartupProbe retainedProbe = programmatic && reused ? new StartupProbe() : probe;
        SEPersistenceUnitInfo info = persistenceUnit(identity, loader);
        boolean forbidden = "none".equals(action) || "drop".equals(action) || "validate".equals(action);
        ProbeReader reader = new ProbeReader(insert(LOADED, "loaded"), forbidden);
        StringWriter createScript = new StringWriter();
        StringWriter dropScript = new StringWriter();
        List<EntityManagerFactory> factories = new ArrayList<>();
        boolean containerFlag = JavaSECMPInitializer.isInContainer();
        try (Fixture fixture = new Fixture()) {
            if (!missing && !"create".equals(action)) {
                fixture.prepareSentinel();
            }
            DatabaseSessionImpl firstSession = null;
            if (reused) {
                EntityManagerFactory first;
                if (programmatic) {
                    first = configuration(identity, properties(loader, retainedProbe)).createEntityManagerFactory();
                    factories.add(first);
                    deploy(first);
                } else {
                    first = namedFactory(info, properties(loader, probe), factories);
                }
                firstSession = session(first);
                probe.reset();
            }
            Map<String, Object> values = properties(loader, probe);
            if (action != null) {
                values.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, action);
            }
            values.put(PersistenceUnitProperties.SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE,
                    resource ? loader.marker : reader);
            if (scripts != null) {
                values.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, scripts);
                values.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, createScript);
                values.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, dropScript);
            }
            RuntimeException failure = null;
            try {
                switch (entryPoint) {
                    case NAMED_FACTORY -> {
                        EntityManagerFactory factory = namedFactory(info, values, factories);
                        if (reused) {
                            Assert.assertSame("Second named factory must reuse the deployed session", firstSession, session(factory));
                        }
                    }
                    case NAMED_SCHEMA -> {
                        values.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
                        Persistence.generateSchema(info.getPersistenceUnitName(), values);
                    }
                    case CONTAINER_FACTORY -> {
                        EntityManagerFactory factory = new PersistenceProvider().createContainerEntityManagerFactory(info, values);
                        factories.add(factory);
                        deploy(factory);
                    }
                    case CONTAINER_SCHEMA -> new PersistenceProvider().generateSchema(info, values);
                    case CONFIGURATION_SCHEMA -> {
                        values.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
                        PersistenceConfiguration configuration = configuration(identity, values);
                        if (action != null && !"none".equals(action)) {
                            configuration.properties().remove(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION);
                            configuration.schemaManagementDatabaseAction(SchemaManagementAction.valueOf(
                                    action.toUpperCase(Locale.ROOT).replace('-', '_')));
                        }
                        if (scripts != null) {
                            configuration.properties().remove(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION);
                            configuration.schemaManagementScriptsAction(SchemaManagementAction.valueOf(
                                    scripts.toUpperCase(Locale.ROOT).replace('-', '_')));
                        }
                        Assert.assertTrue(new PersistenceProvider().generateSchema(configuration));
                    }
                }
            } catch (RuntimeException exception) {
                failure = exception;
            }
            Assert.assertNotNull("Startup instrumentation must be installed", probe.session);
            if (reused && !programmatic) {
                Assert.assertSame("Schema generation must reach the retained session", firstSession, probe.session);
            }
            if (reused && programmatic) {
                Assert.assertTrue("Schema generation must preserve the retained factory", factories.getFirst().isOpen());
                deploy(factories.getFirst());
            }
            if (missing) {
                Assert.assertTrue("Missing schema must fail with PersistenceException", failure instanceof PersistenceException);
                boolean validationCause = false;
                for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                    validationCause |= cause instanceof SchemaValidationException;
                }
                Assert.assertTrue("Missing schema must use actual schema validation", validationCause);
                Assert.assertFalse("Validation must not create the table", fixture.exists());
                reader.assertUntouched();
            } else {
                if (failure != null) {
                    throw failure;
                }
                if ("drop".equals(action)) {
                    Assert.assertFalse("Drop must remove the owned table", fixture.exists());
                } else {
                    Assert.assertTrue("Owned table must exist", fixture.exists());
                    int sentinel = "create".equals(action) || "drop-and-create".equals(action) ? 0 : 1;
                    Assert.assertEquals("Sentinel row state", sentinel, fixture.count(SENTINEL));
                    Assert.assertEquals("Load row state", forbidden ? 0 : 1, fixture.count(LOADED));
                    if (!forbidden) {
                        Assert.assertEquals("loaded", fixture.value(LOADED));
                        Assert.assertTrue("Permitted source must be read", reader.reads > 0);
                    }
                }
                if (forbidden) {
                    reader.assertUntouched();
                }
                if ("create".equals(action)) {
                    probe.assertBefore("CREATE TABLE", "INSERT INTO");
                } else if ("drop-and-create".equals(action)) {
                    probe.assertBefore("DROP TABLE", "CREATE TABLE");
                    probe.assertBefore("CREATE TABLE", "INSERT INTO");
                } else if (!"drop".equals(action)) {
                    probe.assertNoDdl();
                }
                if (scripts != null) {
                    String output = createScript.toString().toUpperCase(Locale.ROOT);
                    Assert.assertTrue("Independent create script must contain owned table DDL", output.contains("CREATE TABLE") && output.contains(TABLE));
                    if ("drop-and-create".equals(scripts)) {
                        output = dropScript.toString().toUpperCase(Locale.ROOT);
                        Assert.assertTrue("Independent drop script must contain owned table DDL", output.contains("DROP TABLE") && output.contains(TABLE));
                    }
                }
            }
            Assert.assertEquals("Forbidden String source must not be resolved", 0, loader.resolutions);
            Assert.assertTrue("Recognized database action must not warn: " + action, probe.unknownDatabaseActions.isEmpty());
        } finally {
            try {
                closeFactories(factories);
            } finally {
                try {
                    probe.restore();
                    if (retainedProbe != probe) {
                        retainedProbe.restore();
                    }
                } finally {
                    reader.finish();
                    createScript.close();
                    dropScript.close();
                    JavaSECMPInitializer.setIsInContainer(containerFlag);
                }
            }
        }
    }

    private static void closeFactories(List<EntityManagerFactory> factories) {
        RuntimeException failure = null;
        for (int index = factories.size() - 1; index >= 0; index--) {
            EntityManagerFactory factory = factories.get(index);
            try {
                if (factory.isOpen()) {
                    factory.close();
                }
            } catch (RuntimeException exception) {
                if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    private static SEPersistenceUnitInfo persistenceUnit(String name, ClassLoader loader) throws Exception {
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setPersistenceUnitName(name);
        info.setClassLoader(loader);
        info.setPersistenceUnitRootUrl(new File(".").toURI().toURL());
        info.setManagedClassNames(List.of(SchemaGenerationEntity.class.getName()));
        info.setMappingFileNames(List.of());
        info.setExcludeUnlistedClasses(true);
        info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        info.setValidationMode("NONE");
        info.setProperties(new Properties());
        return info;
    }

    private static Map<String, Object> properties(ClassLoader loader, StartupProbe probe) {
        Map<String, Object> values = new HashMap<>();
        values.put(PersistenceUnitProperties.CLASSLOADER, loader);
        values.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, probe);
        values.put(PersistenceUnitProperties.WEAVING, "false");
        values.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        values.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        return values;
    }

    private static PersistenceConfiguration configuration(String name, Map<String, Object> properties) {
        return new PersistenceConfiguration(name)
                .managedClass(SchemaGenerationEntity.class)
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE)
                .properties(properties);
    }

    private static EntityManagerFactory namedFactory(SEPersistenceUnitInfo info, Map<String, Object> properties,
            List<EntityManagerFactory> factories) {
        Map<String, Object> values = new HashMap<>(properties);
        values.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
        Assert.assertFalse("Standard-action tests must not use vendor DDL precedence", values.containsKey(PersistenceUnitProperties.DDL_GENERATION));
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), values);
        factories.add(factory);
        deploy(factory);
        return factory;
    }

    private static void deploy(EntityManagerFactory factory) {
        // A normal manager operation forces deferred none/absent-action deployment.
        try (EntityManager manager = factory.createEntityManager()) {
            manager.getTransaction().begin();
            manager.getTransaction().rollback();
        }
    }

    private static DatabaseSessionImpl session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getDatabaseSession();
    }

    private static String insert(int id, String value) {
        return "INSERT INTO " + TABLE + " (ACTION_ID, ACTION_VALUE) VALUES (" + id + ", '" + value + "')\n";
    }

    /** Fixture setup uses only this entity's metadata and acquires ownership by absence. */
    private static final class Fixture implements AutoCloseable {
        private final List<EntityManagerFactory> factories = new ArrayList<>();
        private final StartupProbe probe = new StartupProbe();
        private final EntityManagerFactory factory;
        private final SchemaManager schema;
        private final TableDefinition table = new TableDefinition();

        private Fixture() throws Exception {
            ClassLoader loader = TestSchemaGen.class.getClassLoader();
            SEPersistenceUnitInfo info = persistenceUnit("schema-action-fixture-" + IDENTITIES.incrementAndGet(), loader);
            factory = namedFactory(info, properties(loader, probe), factories);
            schema = new SchemaManager(session(factory));
            table.setName(TABLE);
            try {
                Assert.assertFalse("Refusing to acquire pre-existing schema-action table", exists());
            } catch (RuntimeException | Error exception) {
                try {
                    factory.close();
                } finally {
                    probe.restore();
                }
                throw exception;
            }
        }

        private boolean exists() {
            return schema.checkTableExists(table);
        }

        private void prepareSentinel() {
            schema.createDefaultTables(false);
            Assert.assertTrue("Fixture table creation must succeed", exists());
            try (EntityManager manager = factory.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    manager.createNativeQuery(insert(SENTINEL, "sentinel")).executeUpdate();
                    manager.getTransaction().commit();
                } finally {
                    if (manager.getTransaction().isActive()) {
                        manager.getTransaction().rollback();
                    }
                }
            }
        }

        private int count(int id) {
            try (EntityManager manager = factory.createEntityManager()) {
                return ((Number) manager.createNativeQuery("SELECT COUNT(*) FROM " + TABLE + " WHERE ACTION_ID = " + id).getSingleResult()).intValue();
            }
        }

        private String value(int id) {
            try (EntityManager manager = factory.createEntityManager()) {
                return (String) manager.createNativeQuery("SELECT ACTION_VALUE FROM " + TABLE + " WHERE ACTION_ID = " + id).getSingleResult();
            }
        }

        @Override
        public void close() {
            try {
                if (exists()) {
                    schema.dropDefaultTables();
                }
                Assert.assertFalse("Owned fixture table must be removed", exists());
            } finally {
                try {
                    factory.close();
                } finally {
                    probe.restore();
                }
            }
        }
    }

    private static final class ProbeReader extends StringReader {
        private final boolean forbidden;
        private int reads;
        private int closes;

        private ProbeReader(String source, boolean forbidden) {
            super(source);
            this.forbidden = forbidden;
        }

        @Override
        public int read() throws IOException {
            reads++;
            if (forbidden) {
                throw new IOException("Forbidden schema-action load Reader was consumed");
            }
            return super.read();
        }

        @Override
        public int read(char[] buffer, int offset, int length) throws IOException {
            reads++;
            if (forbidden) {
                throw new IOException("Forbidden schema-action load Reader was consumed");
            }
            return super.read(buffer, offset, length);
        }

        @Override
        public void close() {
            closes++;
            super.close();
        }

        private void assertUntouched() {
            Assert.assertEquals("Provider must not read forbidden load source", 0, reads);
            Assert.assertEquals("Provider must not close forbidden load source", 0, closes);
        }

        private void finish() {
            super.close();
        }
    }

    private static final class SourceLoader extends ClassLoader {
        private final String marker;
        private int resolutions;

        private SourceLoader(String marker) {
            super(TestSchemaGen.class.getClassLoader());
            this.marker = marker;
        }

        @Override
        public URL getResource(String name) {
            if (marker.equals(name)) {
                resolutions++;
                throw new IllegalStateException("Forbidden schema-action load resource was resolved");
            }
            return super.getResource(name);
        }
    }

    private static final class StartupProbe extends AbstractSessionLog implements SessionCustomizer {
        private final List<String> sql = new ArrayList<>();
        private final List<String> unknownDatabaseActions = new ArrayList<>();
        private Session session;
        private SessionLog previousLog;
        private final SessionEventAdapter listener = new SessionEventAdapter() {
            @Override
            public void preExecuteCall(SessionEvent event) {
                if (event.getCall() instanceof DatabaseCall call && call.getSQLString() != null) {
                    String statement = call.getSQLString().toUpperCase(Locale.ROOT);
                    if (statement.contains(TABLE)) {
                        sql.add(statement);
                    }
                }
            }
        };

        @Override
        public void customize(Session value) {
            if (session == null) {
                session = value;
                previousLog = value.getSessionLog();
                setLevel(SessionLog.WARNING);
                value.setSessionLog(this);
                value.getEventManager().addListener(listener);
            } else {
                Assert.assertSame("Customizer must retain its owned session", session, value);
            }
        }

        @Override
        public void log(SessionLogEntry entry) {
            if ("ddl_generation_unknown_property_value".equals(entry.getMessage())
                    && entry.getParameters() != null
                    && Arrays.asList(entry.getParameters()).contains(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION)) {
                unknownDatabaseActions.add(entry.getMessage());
            }
        }

        private void reset() {
            sql.clear();
            unknownDatabaseActions.clear();
        }

        private int index(String fragment) {
            for (int index = 0; index < sql.size(); index++) {
                if (sql.get(index).contains(fragment)) {
                    return index;
                }
            }
            return -1;
        }

        private void assertBefore(String first, String second) {
            int firstIndex = index(first);
            int secondIndex = index(second);
            Assert.assertTrue("Owned-table startup order: " + first + " before " + second,
                    firstIndex >= 0 && secondIndex > firstIndex);
        }

        private void assertNoDdl() {
            Assert.assertEquals("No owned table creation", -1, index("CREATE TABLE"));
            Assert.assertEquals("No owned table drop", -1, index("DROP TABLE"));
            Assert.assertEquals("No owned table alteration", -1, index("ALTER TABLE"));
        }

        private void restore() {
            if (session != null) {
                session.getEventManager().removeListener(listener);
                session.setSessionLog(previousLog);
            }
        }
    }
}
