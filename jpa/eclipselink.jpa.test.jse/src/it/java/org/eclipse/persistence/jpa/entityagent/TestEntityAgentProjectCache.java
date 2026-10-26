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

import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.TransactionRequiredException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentCallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentNamedOptionsRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentXmlCallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.callbacks.AgentDiscoveryRecord;
import org.eclipse.persistence.jpa.entityagent.model.callbacks.AgentDiscoveryExcludedRecord;
import org.eclipse.persistence.jpa.entityagent.model.callbacks.AgentDiscoveredCallbackListener;
import org.eclipse.persistence.jpa.metadata.FileBasedProjectCache;
import org.eclipse.persistence.jpa.metadata.ProjectCache;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Public H2-only bootstrap checks; serialized files contain only owned fixture configuration. */
@RunWith(EmfRunner.class)
public class TestEntityAgentProjectCache {
    private static final Class<?>[] CALLBACK_CLASSES = {AgentCallbackRecord.class, AgentXmlCallbackRecord.class,
            AgentDiscoveryRecord.class, AgentDiscoveryExcludedRecord.class, AgentDiscoveredCallbackListener.class};

    @Test
    public void serializedProjectRestoresDeclaredDiscoveredAndPackageCallbacks() throws Exception {
        try (CacheUnit unit = new CacheUnit(CALLBACK_CLASSES)) {
            try (EntityManagerFactory original = unit.factory("java-serialization", true)) {
                verifyCallbacks(original);
                assertTrue(Files.size(unit.file) > 0);
            }
            RecordingCache cache = new RecordingCache();
            try (EntityManagerFactory restored = unit.factory(cache, false)) {
                verifyCacheWasUsed(restored, cache);
                verifyCallbacks(restored);
            }
        }
    }

    @Test
    public void customProjectCacheRestoresNamedGraphAndAgentResultLock() throws Exception {
        verifyQueryMetadata(true);
    }

    @Test
    public void customProjectCacheRestoresNamedGraphAndManagedResultLock() throws Exception {
        verifyQueryMetadata(false);
    }

    @Test
    public void cachedNamedQueryGraphAndScopeDeclarationsRemainOperational() throws Exception {
        try (CacheUnit unit = new CacheUnit(AgentNamedOptionsRecord.class)) {
            try (EntityManagerFactory original = unit.factory("java-serialization", true);
                    EntityAgent agent = original.createEntityAgent()) {
                AgentNamedOptionsRecord record = new AgentNamedOptionsRecord();
                record.id = 1;
                record.name = "cached";
                record.tags.add("tag");
                record.notes.add("note");
                agent.insert(record);
            }
            RecordingCache cache = new RecordingCache();
            try (EntityManagerFactory restored = unit.factory(cache, false)) {
                verifyCacheWasUsed(restored, cache);
                for (String queryName : List.of("EANamed.graph", "EANamed.xmlGraph")) {
                    try (EntityManager manager = restored.createEntityManager()) {
                        AgentNamedOptionsRecord record = manager.createNamedQuery(queryName, AgentNamedOptionsRecord.class)
                                .getSingleResult();
                        assertTrue(restored.getPersistenceUnitUtil().isLoaded(record, "tags"));
                        assertFalse(restored.getPersistenceUnitUtil().isLoaded(record, "notes"));
                        assertEquals(List.of("tag"), record.tags);
                    }
                }
                for (String queryName : List.of("EANamed.extended", "EANamed.xmlExtended")) {
                    try (EntityManager manager = restored.createEntityManager(); Connection peer = unit.peer()) {
                        manager.getTransaction().begin();
                        try {
                            manager.createNamedQuery(queryName, AgentNamedOptionsRecord.class).getSingleResult();
                            assertBlocked(peer, "UPDATE EA_NAMED_TAG SET TAG_VALUE=TAG_VALUE WHERE OWNER_ID=1");
                            assertBlocked(peer, "UPDATE EA_NAMED_NOTE SET NOTE_VALUE=NOTE_VALUE WHERE OWNER_ID=1");
                        } finally {
                            manager.getTransaction().rollback();
                        }
                        assertAvailable(peer, "UPDATE EA_NAMED_TAG SET TAG_VALUE=TAG_VALUE WHERE OWNER_ID=1");
                    }
                }
            }
        }
    }

    @Test
    public void incompatibleSerializedProjectIsRebuiltWithoutManualDeletion() throws Exception {
        Path incompatible = Files.createTempFile("entityagent-incompatible-cache-", ".bin");
        try {
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(incompatible))) {
                output.writeObject("A non-Project stream must be rejected by the configured input filter");
            }
            verifyRebuildFromCache(incompatible);
        } finally {
            Files.deleteIfExists(incompatible);
        }
    }

    /** Also used by the external two-JDK proof with an actual pinned older-provider cache file. */
    public static void verifyRebuildFromCache(Path previousCache) throws Exception {
        try (CacheUnit unit = new CacheUnit(CALLBACK_CLASSES)) {
            Files.copy(previousCache, unit.file, StandardCopyOption.REPLACE_EXISTING);
            RecordingCache cache = new RecordingCache();
            try (EntityManagerFactory factory = unit.factory(cache, true)) {
                verifyCallbacks(factory);
                assertEquals(0, cache.hits);
                assertEquals(1, cache.stores);
                assertNotNull(new FileBasedProjectCache().retrieveProject(unit.cacheProperties(),
                        TestEntityAgentProjectCache.class.getClassLoader(), server(factory).getSessionLog()));
            }
        }
    }

    private static void verifyQueryMetadata(boolean agentResult) throws Exception {
        try (CacheUnit unit = new CacheUnit(AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class)) {
            try (EntityManagerFactory original = unit.factory("java-serialization", true);
                    EntityAgent agent = original.createEntityAgent()) {
                AgentQueryGroup group = new AgentQueryGroup(16, "group");
                agent.insert(group);
                agent.insert(new AgentQueryRecord(17, "record", group));
            }
            RecordingCache cache = new RecordingCache();
            try (EntityManagerFactory restored = unit.factory(cache, false); Connection peer = unit.peer()) {
                verifyCacheWasUsed(restored, cache);
                if (agentResult) {
                    try (EntityAgent agent = restored.createEntityAgent()) {
                        var graph = agent.getEntityGraph(AgentQueryRecord.class, "EAQuery.group");
                        assertTrue(graph.getAttributeNodes().stream().anyMatch(node -> node.getAttributeName().equals("group")));
                        AgentQueryRecord graphResult = agent.get(graph, 17);
                        assertEquals("group", graphResult.getGroup().name);
                        assertEquals("record", graphResult.code.value());
                        assertThrows(TransactionRequiredException.class, () -> agent.createNativeQuery(
                                "SELECT * FROM EA_QUERY_RECORD WHERE ID=17", "EAQuery.lockedMapping").getSingleResult());
                        agent.getTransaction().begin();
                        try {
                            AgentQueryRecord record = (AgentQueryRecord) agent.createNativeQuery(
                                    "SELECT * FROM EA_QUERY_RECORD WHERE ID=17", "EAQuery.lockedMapping").getSingleResult();
                            assertEquals(Integer.valueOf(17), record.id);
                            assertEquals(1, record.loadCallbacks);
                            assertBlocked(peer, "UPDATE EA_QUERY_RECORD SET TITLE=TITLE WHERE ID=17");
                        } finally {
                            agent.getTransaction().rollback();
                        }
                    }
                } else {
                    try (EntityManager manager = restored.createEntityManager()) {
                        assertEquals(1, manager.getEntityGraph(AgentQueryRecord.class, "EAQuery.group").getAttributeNodes().size());
                        manager.getTransaction().begin();
                        try {
                            AgentQueryRecord record = (AgentQueryRecord) manager.createNativeQuery(
                                    "SELECT * FROM EA_QUERY_RECORD WHERE ID=17", "EAQuery.lockedMapping").getSingleResult();
                            assertTrue(manager.contains(record));
                            assertEquals("record", record.code.value());
                            assertBlocked(peer, "UPDATE EA_QUERY_RECORD SET TITLE=TITLE WHERE ID=17");
                        } finally {
                            manager.getTransaction().rollback();
                        }
                    }
                }
                assertAvailable(peer, "UPDATE EA_QUERY_RECORD SET TITLE=TITLE WHERE ID=17");
            }
        }
    }

    private static void verifyCallbacks(EntityManagerFactory factory) {
        try (EntityAgent agent = factory.createEntityAgent()) {
            AgentDiscoveryRecord discovered = new AgentDiscoveryRecord(31);
            agent.insert(discovered);
            assertEquals(List.of("discovered-pre", "package-pre", "declared-pre", "entity-pre"),
                    discovered.events.subList(0, 4));
            assertEquals(6, discovered.events.size());
            agent.delete(discovered);
            AgentDiscoveryExcludedRecord excluded = new AgentDiscoveryExcludedRecord(32);
            agent.insert(excluded);
            assertFalse(excluded.events.contains("package-pre"));
            assertEquals(List.of("discovered-pre", "declared-pre", "entity-pre"), excluded.events.subList(0, 3));
            agent.delete(excluded);
            AgentXmlCallbackRecord xml = new AgentXmlCallbackRecord(33, "xml");
            agent.insert(xml);
            assertEquals(List.of("xml-pre-insert", "xml-post-insert"), xml.events);
            agent.delete(xml);
            AgentCallbackRecord record = new AgentCallbackRecord(34, "upsert");
            agent.upsert(record);
            assertEquals(List.of("pre-upsert", "post-upsert"), record.events);
            AgentCallbackRecord loaded = agent.get(AgentCallbackRecord.class, 34);
            assertEquals(List.of("post-load:upsert"), loaded.events);
            agent.delete(loaded);
            assertEquals(Long.valueOf(0), agent.createQuery(
                    "select count(r) from AgentCallbackRecord r", Long.class).getSingleResult());
        }
        try (EntityManager manager = factory.createEntityManager()) {
            manager.getTransaction().begin();
            AgentCallbackRecord record = new AgentCallbackRecord(35, "managed");
            manager.persist(record);
            manager.flush();
            assertTrue(record.events.containsAll(List.of("pre-persist", "post-persist", "pre-insert", "post-insert")));
            manager.remove(record);
            manager.flush();
            assertTrue(record.events.containsAll(List.of("pre-remove", "post-remove", "pre-delete", "post-delete")));
            manager.getTransaction().commit();
        }
    }

    private static void verifyCacheWasUsed(EntityManagerFactory factory, RecordingCache cache) {
        if (cache.retrieved == null && cache.retrievalFailure != null) {
            throw new AssertionError("Project cache retrieval failed", cache.retrievalFailure);
        }
        assertSame("Deployment must use the deserialized Project", cache.retrieved, server(factory).getProject());
        assertEquals(1, cache.hits);
        assertEquals("A cache hit must bypass metadata rebuilding", 0, cache.stores);
    }

    private static ServerSession server(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private static void assertBlocked(Connection peer, String sql) throws SQLException {
        try (java.sql.Statement statement = peer.createStatement()) {
            assertThrows(SQLException.class, () -> statement.executeUpdate(sql));
        } finally {
            peer.rollback();
        }
    }

    private static void assertAvailable(Connection peer, String sql) throws SQLException {
        try (java.sql.Statement statement = peer.createStatement()) {
            assertEquals(1, statement.executeUpdate(sql));
            peer.commit();
        }
    }

    public static final class RecordingCache implements ProjectCache {
        private final FileBasedProjectCache delegate = new FileBasedProjectCache();
        private Project retrieved;
        private int hits;
        private int stores;
        private Throwable retrievalFailure;

        @Override
        public Project retrieveProject(Map<String, ?> properties, ClassLoader loader, SessionLog log) {
            retrieved = delegate.retrieveProject(properties, loader, new DefaultSessionLog() {
                @Override
                public void logThrowable(int level, String category, Throwable failure) {
                    retrievalFailure = failure;
                }
            });
            if (retrieved != null) {
                hits++;
            }
            return retrieved;
        }

        @Override
        public void storeProject(Project project, Map<String, ?> properties, SessionLog log) {
            stores++;
            delegate.storeProject(project, properties, log);
        }
    }

    private static final class CacheUnit implements AutoCloseable {
        private final DataSource dataSource;
        private final Connection keeper;
        private final Path directory = Files.createTempDirectory("entityagent-project-cache-");
        private final Path file = directory.resolve("project.bin");
        private final List<Class<?>> classes;
        private final String url;

        private CacheUnit(Class<?>... classes) throws Exception {
            this.classes = Arrays.asList(classes);
            url = "jdbc:h2:mem:project-cache-" + UUID.randomUUID();
            Class<?> type = Class.forName("org.h2.jdbcx.JdbcDataSource");
            Object source = type.getConstructor().newInstance();
            type.getMethod("setURL", String.class).invoke(source, url);
            type.getMethod("setUser", String.class).invoke(source, "sa");
            type.getMethod("setPassword", String.class).invoke(source, "");
            dataSource = (DataSource) source;
            keeper = dataSource.getConnection();
        }

        private Map<String, Object> cacheProperties() {
            return Map.of(PersistenceUnitProperties.PROJECT_CACHE_FILE, file.toString());
        }

        private EntityManagerFactory factory(Object cache, boolean createTables) {
            SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName("project-cache-unit-" + UUID.randomUUID());
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(TestEntityAgentProjectCache.class.getClassLoader());
            info.setNewTempClassLoader(TestEntityAgentProjectCache.class.getClassLoader());
            info.setPersistenceUnitRootUrl(TestEntityAgentProjectCache.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(classes.stream().map(Class::getName).toList());
            if (classes.contains(AgentXmlCallbackRecord.class)) {
                info.setMappingFileNames(List.of("META-INF/entityagent-callbacks.xml"));
            } else if (classes.contains(AgentNamedOptionsRecord.class)) {
                info.setMappingFileNames(List.of("META-INF/entityagent-named-options.xml"));
            }
            info.setExcludeUnlistedClasses(true);
            info.setNonJtaDataSource(dataSource);
            Properties properties = new Properties();
            properties.setProperty("jakarta.persistence.jdbc.url", url);
            properties.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
            properties.setProperty("jakarta.persistence.jdbc.user", "sa");
            properties.setProperty("jakarta.persistence.jdbc.password", "");
            properties.setProperty("jakarta.persistence.validation.mode", "NONE");
            properties.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform");
            properties.setProperty("eclipselink.weaving", "false");
            properties.setProperty("eclipselink.logging.level", "OFF");
            properties.setProperty("eclipselink.cache.shared.default", "false");
            properties.setProperty("eclipselink.ddl-generation", createTables ? "create-tables" : "none");
            info.setProperties(properties);
            Map<String, Object> overrides = new HashMap<>(cacheProperties());
            overrides.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
            overrides.put(PersistenceUnitProperties.PROJECT_CACHE, cache);
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), overrides);
            server(factory);
            return factory;
        }

        private Connection peer() throws SQLException {
            Connection connection = dataSource.getConnection();
            try (java.sql.Statement statement = connection.createStatement()) {
                statement.execute("SET LOCK_TIMEOUT 100");
            }
            connection.setAutoCommit(false);
            return connection;
        }

        @Override
        public void close() throws Exception {
            try {
                keeper.close();
            } finally {
                Files.deleteIfExists(file);
                Files.deleteIfExists(directory);
            }
        }
    }
}
