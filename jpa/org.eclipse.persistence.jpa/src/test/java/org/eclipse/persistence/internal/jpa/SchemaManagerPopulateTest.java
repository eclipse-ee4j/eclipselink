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

import jakarta.persistence.PersistenceException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE;

class SchemaManagerPopulateTest {
    @Test
    void noSourceDoesNothing() {
        withSystemSource(null, () -> {
            RecordingSession session = new RecordingSession();
            new SchemaManagerImpl(session, Map.of()).populate();
            Assertions.assertTrue(session.sql.isEmpty());
        });
    }

    @Test
    void readerPreservesStatementsAndCloseBehavior() {
        RecordingSession session = new RecordingSession();
        TrackingReader reader = new TrackingReader("  # comment\n\n first statement  \nsecond statement");
        manager(session, reader).populate();
        Assertions.assertEquals(List.of("first statement", "second statement"), session.sql);
        Assertions.assertEquals(2, reader.closes);
    }

    @Test
    void factoryPropertyOverridesSystemSource() {
        withSystemSource("invalid location", () -> {
            RecordingSession session = new RecordingSession();
            manager(session, new StringReader("factory source")).populate();
            Assertions.assertEquals(List.of("factory source"), session.sql);
        });
    }

    @Test
    void systemSourceUsesConfiguredLoader() throws Exception {
        RecordingSession session = new RecordingSession();
        ResourceLoader loader = new ResourceLoader("system source");
        session.getDatasourcePlatform().getConversionManager().setLoader(loader);
        withSystemSource("populate-resource", () -> new SchemaManagerImpl(session, Map.of()).populate());
        Assertions.assertEquals(List.of("system source"), session.sql);
        Assertions.assertEquals(1, loader.opens);
    }

    @Test
    void configuredLoaderWinsOverUnrelatedContextLoader() throws Exception {
        RecordingSession session = new RecordingSession();
        ResourceLoader loader = new ResourceLoader("configured source");
        session.getDatasourcePlatform().getConversionManager().setLoader(loader);
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(new ClassLoader(null) { });
            manager(session, "populate-resource").populate();
            Assertions.assertEquals(List.of("configured source"), session.sql);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }

    @Test
    void currentThreadLoaderPolicyIsHonored() throws Exception {
        RecordingSession session = new RecordingSession();
        ResourceLoader loader = new ResourceLoader("context source");
        session.getDatasourcePlatform().getConversionManager().setShouldUseClassLoaderFromCurrentThread(true);
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            manager(session, "populate-resource").populate();
            Assertions.assertEquals(List.of("context source"), session.sql);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }

    @Test
    void resourceReopensWithUtf8AndUrlCachingDisabled() throws Exception {
        RecordingSession session = new RecordingSession();
        ResourceLoader loader = new ResourceLoader("café");
        session.getDatasourcePlatform().getConversionManager().setLoader(loader);
        SchemaManagerImpl manager = manager(session, "populate-resource");
        manager.populate();
        manager.populate();
        Assertions.assertEquals(List.of("café", "café"), session.sql);
        Assertions.assertEquals(2, loader.opens);
        Assertions.assertEquals(2, loader.closes);
        Assertions.assertFalse(loader.cachingObserved);
    }

    @Test
    void fileUrlIsReopenedOnLaterCalls() throws Exception {
        Path script = Files.createTempFile("schema-populate-unit-", ".sql");
        try {
            RecordingSession session = new RecordingSession();
            Files.writeString(script, "first file", StandardCharsets.UTF_8);
            SchemaManagerImpl manager = manager(session, script.toUri().toString());
            manager.populate();
            Files.writeString(script, "second file", StandardCharsets.UTF_8);
            manager.populate();
            Assertions.assertEquals(List.of("first file", "second file"), session.sql);
        } finally {
            Files.deleteIfExists(script);
        }
    }

    @Test
    void unsupportedSourceTypeKeepsPersistenceException() {
        Assertions.assertThrows(PersistenceException.class, () -> manager(new RecordingSession(), 17).populate());
    }

    @Test
    void missingSourceLocationKeepsPersistenceException() {
        PersistenceException failure = Assertions.assertThrows(PersistenceException.class,
                () -> manager(new RecordingSession(), "missing populate location").populate());
        Assertions.assertInstanceOf(java.net.MalformedURLException.class, failure.getCause());
    }

    @Test
    void readerFailureIsWrappedAndReaderClosed() {
        TrackingReader reader = new TrackingReader("unused");
        reader.fail = true;
        PersistenceException failure = Assertions.assertThrows(PersistenceException.class,
                () -> manager(new RecordingSession(), reader).populate());
        Assertions.assertInstanceOf(IOException.class, failure.getCause());
        Assertions.assertEquals(1, reader.closes);
    }

    @Test
    void databaseFailureDoesNotPreventLaterStatements() {
        RecordingSession session = new RecordingSession();
        session.rejectFirst = true;
        manager(session, new StringReader("rejected\naccepted")).populate();
        Assertions.assertEquals(List.of("rejected", "accepted"), session.sql);
    }

    @Test
    void ordinarySessionBackedFactoryRequiresNoSetup() {
        RecordingSession session = new RecordingSession();
        session.setProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, new StringReader("session-backed source"));
        try (EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl(session)) {
            factory.getSchemaManager().populate();
            Assertions.assertEquals(List.of("session-backed source"), session.sql);
        }
    }

    @Test
    void compositeBrokerFailsBeforePropertyReadOrSql() {
        TrackingReader reader = new TrackingReader("must not run");
        Map<String, Object> properties = new AbstractMap<>() {
            @Override
            public Object get(Object key) {
                Assertions.fail("Composite populate must reject before property lookup");
                return null;
            }
            @Override
            public Set<Entry<String, Object>> entrySet() {
                return Set.of(Map.entry(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, reader));
            }
        };
        SessionBroker broker = new SessionBroker() {
            @Override
            public void executeNonSelectingSQL(String sql) {
                Assertions.fail("Composite populate must reject before SQL");
            }
        };
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> new SchemaManagerImpl(broker, properties).populate());
        Assertions.assertEquals(0, reader.reads);
        Assertions.assertEquals(0, reader.closes);
    }

    @Test
    void existingProtectedWrapperKeepsReaderContract() {
        RecordingSession session = new RecordingSession();
        TrackingReader reader = new TrackingReader(" # ignored\nwrapped source\n");
        new EntityManagerSetupImpl().writeSourceScriptToDatabase(reader, session, getClass().getClassLoader());
        Assertions.assertEquals(List.of("wrapped source"), session.sql);
        Assertions.assertEquals(2, reader.closes);
    }

    @Test
    void existingProtectedWrapperClosesReaderOnFailure() {
        TrackingReader reader = new TrackingReader("unused");
        reader.fail = true;
        Assertions.assertThrows(PersistenceException.class, () -> new EntityManagerSetupImpl()
                .writeSourceScriptToDatabase(reader, new RecordingSession(), getClass().getClassLoader()));
        Assertions.assertEquals(1, reader.closes);
    }

    private static SchemaManagerImpl manager(RecordingSession session, Object source) {
        return new SchemaManagerImpl(session, Map.of(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, source));
    }

    private static void withSystemSource(String source, Runnable action) {
        String old = System.getProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE);
        try {
            if (source == null) {
                System.clearProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE);
            } else {
                System.setProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, source);
            }
            action.run();
        } finally {
            if (old == null) {
                System.clearProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE);
            } else {
                System.setProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, old);
            }
        }
    }

    private static class RecordingSession extends DatabaseSessionImpl {
        final List<String> sql = new ArrayList<>();
        boolean rejectFirst;

        RecordingSession() {
            super(new DatabaseLogin());
        }

        @Override
        public void executeNonSelectingSQL(String statement) {
            sql.add(statement);
            if (rejectFirst && sql.size() == 1) {
                throw DatabaseException.sqlException(new SQLException("expected SQL failure"));
            }
        }
    }

    private static class TrackingReader extends StringReader {
        int reads;
        int closes;
        boolean fail;

        TrackingReader(String content) {
            super(content);
        }
        @Override
        public int read() throws IOException {
            reads++;
            if (fail) {
                throw new IOException("expected reader failure");
            }
            return super.read();
        }
        @Override
        public void close() {
            closes++;
            super.close();
        }
    }

    private static class ResourceLoader extends ClassLoader {
        final URL resource;
        int opens;
        int closes;
        boolean cachingObserved;

        ResourceLoader(String content) throws Exception {
            super(null);
            resource = new URL(null, "memory:populate", new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL url) {
                    return new URLConnection(url) {
                        @Override
                        public void connect() { }
                        @Override
                        public InputStream getInputStream() {
                            opens++;
                            cachingObserved |= getUseCaches();
                            return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)) {
                                @Override
                                public void close() throws IOException {
                                    closes++;
                                    super.close();
                                }
                            };
                        }
                    };
                }
            });
        }
        @Override
        public URL getResource(String name) {
            return "populate-resource".equals(name) ? resource : null;
        }
    }
}
