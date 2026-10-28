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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SchemaValidationException;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.jpa.test.ddl.model.SchemaGenerationEntity;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.Session;
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

    private static final String TABLE = "JPA_SCHEMA_ACTION";
    private static final int SENTINEL = 810001;
    private static final int LOADED = 810002;
    private static final AtomicLong IDENTITIES = new AtomicLong();

    private enum EntryPoint {
        NAMED_FACTORY, NAMED_SCHEMA, CONTAINER_FACTORY, CONTAINER_SCHEMA
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
                EntityManagerFactory first = namedFactory(info, properties(loader, probe), factories);
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
                }
            } catch (RuntimeException exception) {
                failure = exception;
            }
            Assert.assertNotNull("Startup instrumentation must be installed", probe.session);
            if (reused) {
                Assert.assertSame("Schema generation must reach the retained session", firstSession, probe.session);
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
