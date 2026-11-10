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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.jupiter.api.Test;

import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Dispatch controls; actual schema validation is exercised by the public JSE tests. */
class EntityManagerSetupImplSchemaGenerationTest {

    @Test
    void noneDoesNotLookUpLoadSource() {
        Fixture fixture = new Fixture("none");
        fixture.run();
        fixture.assertEvents();
    }

    @Test
    void dropDoesNotLookUpLoadSource() {
        Fixture fixture = new Fixture("drop");
        fixture.run();
        fixture.assertEvents("database:DROP");
    }

    @Test
    void validateIsRecognizedWithoutLoading() {
        Fixture fixture = new Fixture("validate");
        fixture.run();
        fixture.assertEvents();
        assertTrue(fixture.log.warnings.isEmpty());
    }

    @Test
    void createPrecedesSourceLookupAndLoading() {
        Fixture fixture = new Fixture("create");
        fixture.run();
        fixture.assertEvents("database:CREATE", "source", "load");
        assertSame(fixture.source, fixture.setup.loadedSource);
    }

    @Test
    void dropAndCreatePrecedeSourceLookupAndLoading() {
        Fixture fixture = new Fixture("drop-and-create");
        fixture.run();
        fixture.assertEvents("database:DROP", "database:CREATE", "source", "load");
    }

    @Test
    void populateIsRecognizedWithoutSchemaOperations() {
        Fixture fixture = new Fixture("populate");
        fixture.run();
        fixture.assertEvents("source", "load");
        assertTrue(fixture.log.warnings.isEmpty());
    }

    @Test
    void absentActionPreservesLoading() {
        Fixture fixture = new Fixture(null);
        fixture.run();
        fixture.assertEvents("source", "load");
    }

    @Test
    void legacyVerifyPreservesLoading() {
        Fixture fixture = new Fixture("verify");
        fixture.run();
        fixture.assertEvents("source", "load");
        assertTrue(fixture.log.warnings.isEmpty());
    }

    @Test
    void createOrExtendPreservesLoading() {
        Fixture fixture = new Fixture("create-or-extend-tables");
        fixture.run();
        fixture.assertEvents("database:EXTEND", "source", "load");
    }

    @Test
    void unknownActionPreservesWarningAndLoading() {
        Fixture fixture = new Fixture("unrecognized");
        fixture.run();
        fixture.assertEvents("source", "load");
        SessionLogEntry warning = fixture.singleWarning(SCHEMA_GENERATION_DATABASE_ACTION, "unrecognized");
        String options = (String) warning.getParameters()[3];
        for (String option : List.of("none", "create", "drop", "drop-and-create", "validate", "populate", "verify", "create-or-extend-tables")) {
            assertTrue(List.of(options.split(", ")).contains(option), option);
        }
    }

    @Test
    void mixedCaseNoneDoesNotLoad() {
        Fixture fixture = new Fixture("NoNe");
        fixture.run();
        fixture.assertEvents();
    }

    @Test
    void mixedCasePopulateIsRecognized() {
        Fixture fixture = new Fixture("PoPuLaTe");
        fixture.run();
        fixture.assertEvents("source", "load");
        assertTrue(fixture.log.warnings.isEmpty());
    }

    @Test
    void noneStillGeneratesCreateScript() {
        Fixture fixture = new Fixture("none");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "create");
        fixture.run();
        fixture.assertEvents("script:CREATE");
    }

    @Test
    void noneStillGeneratesDropAndCreateScripts() {
        Fixture fixture = new Fixture("none");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "drop-and-create");
        fixture.run();
        fixture.assertEvents("script:DROP_AND_CREATE");
    }

    @Test
    void validateStillGeneratesDropScript() {
        Fixture fixture = new Fixture("validate");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "drop");
        fixture.run();
        fixture.assertEvents("script:DROP");
    }

    @Test
    void scriptGenerationPrecedesAllowedLoading() {
        Fixture fixture = new Fixture("create");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "drop-and-create");
        fixture.run();
        fixture.assertEvents("database:CREATE", "script:DROP_AND_CREATE", "source", "load");
    }

    @Test
    void populateIsNotAddedAsScriptAction() {
        Fixture fixture = new Fixture("none");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "populate");
        fixture.run();
        fixture.assertEvents();
        fixture.singleWarning(SCHEMA_GENERATION_SCRIPTS_ACTION, "populate");
    }

    @Test
    void validateIsNotAddedAsScriptAction() {
        Fixture fixture = new Fixture("none");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "validate");
        fixture.run();
        fixture.assertEvents();
        fixture.singleWarning(SCHEMA_GENERATION_SCRIPTS_ACTION, "validate");
    }

    @Test
    void vendorNoneBypassesJpaActionsAndLoading() {
        Fixture fixture = new Fixture("create");
        fixture.properties.put(DDL_GENERATION, "none");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "create");
        fixture.run();
        fixture.assertEvents();
    }

    @Test
    void vendorDdlTakesPrecedence() {
        Fixture fixture = new Fixture("populate");
        fixture.properties.put(DDL_GENERATION, "create-tables");
        fixture.properties.put(SCHEMA_GENERATION_SCRIPTS_ACTION, "create");
        fixture.run();
        fixture.assertEvents("vendor:create-tables");
    }

    @Test
    void failedCreationDoesNotAcquireLoadSource() {
        Fixture fixture = new Fixture("create");
        fixture.setup.failDatabaseAction = true;
        assertThrows(IllegalStateException.class, fixture::run);
        fixture.assertEvents("database:CREATE");
    }

    @Test
    void invocationActionOverridesSystemAction() {
        String previous = System.getProperty(SCHEMA_GENERATION_DATABASE_ACTION);
        try {
            System.setProperty(SCHEMA_GENERATION_DATABASE_ACTION, "populate");
            Fixture fixture = new Fixture("none");
            fixture.run();
            fixture.assertEvents();
        } finally {
            restoreSystemAction(previous);
        }
    }

    @Test
    void systemActionStillControlsLoading() {
        String previous = System.getProperty(SCHEMA_GENERATION_DATABASE_ACTION);
        try {
            System.setProperty(SCHEMA_GENERATION_DATABASE_ACTION, "none");
            Fixture fixture = new Fixture(null);
            fixture.run();
            fixture.assertEvents();
        } finally {
            restoreSystemAction(previous);
        }
    }

    private static void restoreSystemAction(String previous) {
        if (previous == null) {
            System.clearProperty(SCHEMA_GENERATION_DATABASE_ACTION);
        } else {
            System.setProperty(SCHEMA_GENERATION_DATABASE_ACTION, previous);
        }
    }

    private static final class Fixture {
        final List<String> events = new ArrayList<>();
        final Object source = new Object();
        final RecordingSetup setup = new RecordingSetup(events);
        final RecordingLog log = new RecordingLog();
        final DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        final Map<String, Object> properties = new HashMap<>() {
            @Override
            public Object get(Object key) {
                if (SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE.equals(key)) {
                    events.add("source");
                }
                return super.get(key);
            }
        };

        Fixture(String action) {
            session.setSessionLog(log);
            if (action != null) {
                properties.put(SCHEMA_GENERATION_DATABASE_ACTION, action);
            }
            properties.put(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, source);
        }

        void run() {
            setup.writeDDL(properties, session, getClass().getClassLoader());
        }

        void assertEvents(String... expected) {
            assertEquals(List.of(expected), events);
        }

        SessionLogEntry singleWarning(String property, String value) {
            assertEquals(1, log.warnings.size());
            SessionLogEntry warning = log.warnings.getFirst();
            assertEquals("ddl_generation_unknown_property_value", warning.getMessage());
            assertEquals(property, warning.getParameters()[0]);
            assertEquals(value, warning.getParameters()[1]);
            return warning;
        }
    }

    private static final class RecordingSetup extends EntityManagerSetupImpl {
        private final List<String> events;
        private Object loadedSource;
        private boolean failDatabaseAction;

        RecordingSetup(List<String> events) {
            this.events = events;
            SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName("schema-action-dispatch");
            persistenceUnitInfo = info;
        }

        @Override
        protected void writeMetadataDDLToDatabase(TableCreationType type, Map<String, ?> properties,
                                                 DatabaseSessionImpl session, ClassLoader loader) {
            events.add("database:" + type);
            if (failDatabaseAction) {
                throw new IllegalStateException("Creation failed before load-source acquisition");
            }
        }

        @Override
        protected void writeMetadataDDLToScript(TableCreationType type, Map<String, ?> properties,
                                               DatabaseSessionImpl session, ClassLoader loader) {
            events.add("script:" + type);
        }

        @Override
        protected void writeDDL(String action, Map<String, ?> properties,
                                DatabaseSessionImpl session, ClassLoader loader) {
            events.add("vendor:" + action);
        }

        @Override
        protected void writeSourceScriptToDatabase(Object source, DatabaseSessionImpl session, ClassLoader loader) {
            loadedSource = source;
            events.add("load");
        }
    }

    private static final class RecordingLog extends AbstractSessionLog {
        private final List<SessionLogEntry> warnings = new ArrayList<>();

        RecordingLog() {
            setLevel(SessionLog.WARNING);
        }

        @Override
        public void log(SessionLogEntry entry) {
            if (entry.getLevel() == SessionLog.WARNING) {
                warnings.add(entry);
            }
        }
    }
}
