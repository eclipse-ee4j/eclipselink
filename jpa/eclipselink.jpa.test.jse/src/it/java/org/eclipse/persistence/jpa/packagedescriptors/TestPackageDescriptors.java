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
package org.eclipse.persistence.jpa.packagedescriptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.metadata.ProjectCache;
import org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.DiscoveredListener;
import org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Excluded;
import org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Generated;
import org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Record;
import org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Unlisted;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Portable deployment and database-operation checks for package membership. */
@RunWith(EmfRunner.class)
public class TestPackageDescriptors {
    private static final String MODEL_PACKAGE = Record.class.getPackageName();
    private static final String METADATA_PACKAGE = "org.eclipse.persistence.jpa.packagedescriptors.metadata";
    private static final List<String> PACKAGE_EVENTS = List.of(
            "package-first-pre", "package-second-pre", "declared-pre", "entity-pre");
    private static final List<String> ALL_EVENTS = List.of(
            "discovered-pre", "package-first-pre", "package-second-pre", "declared-pre", "entity-pre");
    private static final List<String> ENTITY_EVENTS = List.of("declared-pre", "entity-pre");

    @Emf(name = "package-descriptor-fixture", createTables = DDLGen.DROP_CREATE,
            classes = {Record.class, Excluded.class, Generated.class}, properties = {
            @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
            @Property(name = "eclipselink.weaving", value = "false")})
    private EntityManagerFactory fixture;

    @Before
    @After
    public void clearOwnedRows() {
        fixture.runInTransaction(manager -> {
            manager.createStatement("delete from PackageDescriptorRecord").execute();
            manager.createStatement("delete from PackageDescriptorExcluded").execute();
            manager.createStatement("delete from PackageDescriptorGenerated").execute();
        });
        DiscoveredListener.FACTORIES.set(0);
    }

    @Test
    public void xmlPackageDeclarationRegistersOrderedEntityCallbacks() {
        Map<String, Object> properties = properties();
        properties.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/package-descriptors/persistence.xml");
        properties.put(PersistenceUnitProperties.SESSION_NAME, "package-descriptors-xml-" + UUID.randomUUID());
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("package-descriptors-xml", properties)) {
            verifyInsert(factory, false, ALL_EVENTS);
            verifyInsert(factory, true, ALL_EVENTS);
            assertEquals(1, DiscoveredListener.FACTORIES.get());
        }
    }

    @Test
    public void programmaticPackageDeclarationRegistersCallbacksWithoutEnrollingClasses() {
        PersistenceConfiguration configuration = configuration("package-descriptors-config-" + UUID.randomUUID())
                .managedPackageDescriptor(MODEL_PACKAGE);
        try (EntityManagerFactory factory = configuration.createEntityManagerFactory()) {
            verifyInsert(factory, false, PACKAGE_EVENTS);
            assertEquals(0, DiscoveredListener.FACTORIES.get());
            assertEquals(1, factory.getMetamodel().getEntities().size());
            assertThrows(IllegalArgumentException.class, () -> factory.getMetamodel().entity(Unlisted.class));
        }
    }

    @Test
    public void programmaticPackageMembershipSeparatesSameNamedFactories() {
        String name = "package-descriptors-config-" + UUID.randomUUID();
        try (EntityManagerFactory absent = configuration(name).createEntityManagerFactory();
                EntityManagerFactory present = configuration(name).managedPackageDescriptor(MODEL_PACKAGE)
                        .createEntityManagerFactory()) {
            verifyInsert(absent, false, ENTITY_EVENTS);
            verifyInsert(present, false, PACKAGE_EVENTS);
            assertNotSame(absent.unwrap(JpaEntityManagerFactory.class).getServerSession(),
                    present.unwrap(JpaEntityManagerFactory.class).getServerSession());
        }
    }

    @Test
    public void containerCompletePackageListWorksWithoutExplicitListOrDescriptorScanning() throws Exception {
        try (Unit unit = new Unit(Record.class)) {
            unit.info.setPersistenceUnitRootUrl(new URL("file:/__unavailable_package_descriptor_root__/"));
            unit.properties.put(SystemProperties.ARCHIVE_FACTORY, ContainerArchiveFactory.class.getName());
            PersistenceUnitInfo supplied = (PersistenceUnitInfo) Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[] {PersistenceUnitInfo.class}, (proxy, method, args) -> {
                        if (method.getName().equals("getAllPackageDescriptors")) {
                            return List.of(MODEL_PACKAGE);
                        }
                        try {
                            return method.invoke(unit.info, args);
                        } catch (InvocationTargetException failure) {
                            throw failure.getCause();
                        }
                    });
            try (EntityManagerFactory factory = new PersistenceProvider()
                    .createContainerEntityManagerFactory(supplied, unit.properties)) {
                verifyInsert(factory, false, PACKAGE_EVENTS);
            }
        }
    }

    @Test
    public void listingAnEntityDoesNotEnrollItsExcludedPackageDescriptor() {
        try (Unit unit = new Unit(Record.class)) {
            verifyInsert(unit.create(), false, ENTITY_EVENTS);
            assertTrue(unit.info.getManagedPackageDescriptors().isEmpty());
            assertTrue(unit.info.getAllPackageDescriptors().isEmpty());
        }
    }

    @Test
    public void duplicatePackageDeclarationsRegisterEachListenerOnce() {
        try (Unit unit = new Unit(Record.class, DiscoveredListener.class)) {
            unit.packages(MODEL_PACKAGE, MODEL_PACKAGE);
            verifyInsert(unit.create(), false, ALL_EVENTS);
            assertEquals(List.of(MODEL_PACKAGE), unit.info.getManagedPackageDescriptors());
            assertEquals(1, DiscoveredListener.FACTORIES.get());
        }
    }

    @Test
    public void excludedDefaultListenersRetainDiscoveredAndDeclaredListeners() {
        try (Unit unit = new Unit(Excluded.class, DiscoveredListener.class)) {
            unit.packages(MODEL_PACKAGE);
            try (EntityAgent agent = unit.create().createEntityAgent()) {
                Excluded record = new Excluded();
                record.id = 11;
                agent.getTransaction().begin();
                try {
                    agent.insert(record);
                    assertEquals(List.of("discovered-pre", "declared-pre", "entity-pre"), record.events);
                } finally {
                    agent.getTransaction().rollback();
                }
            }
        }
    }

    @Test
    public void packagePostUpdateCallbacksRunInDeclarationOrderForAgentWrites() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(MODEL_PACKAGE);
            try (EntityAgent agent = unit.create().createEntityAgent()) {
                Record record = new Record(12);
                agent.getTransaction().begin();
                try {
                    agent.insert(record);
                    record.events.clear();
                    record.value = "updated";
                    agent.update(record);
                    assertEquals(List.of("package-first-post-update", "package-second-post-update"), record.events);
                } finally {
                    agent.getTransaction().rollback();
                }
            }
        }
    }

    @Test
    public void metadataOnlyPackageContributesNamedStatementWithoutAnEntityInThatPackage() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(METADATA_PACKAGE);
            try (EntityAgent agent = unit.create().createEntityAgent()) {
                agent.getTransaction().begin();
                try {
                    agent.insert(new Record(13));
                    assertEquals(1, agent.createNamedStatement("packageDescriptorRename")
                            .setParameter("value", "renamed").setParameter("id", 13).execute());
                    assertEquals("renamed", agent.get(Record.class, 13).value);
                } finally {
                    agent.getTransaction().rollback();
                }
            }
            assertEquals(1, unit.create().getMetamodel().getEntities().size());
        }
    }

    @Test
    public void rootScanningDiscoversAnnotatedDescriptorWithoutEnrollingPackageClasses() throws Exception {
        try (Archive archive = new Archive(MODEL_PACKAGE); Unit unit = new Unit(Record.class)) {
            unit.info.setPersistenceUnitRootUrl(archive.url());
            unit.info.setExcludeUnlistedClasses(false);
            EntityManagerFactory factory = unit.create();
            verifyInsert(factory, false, PACKAGE_EVENTS);
            assertTrue(unit.info.getManagedPackageDescriptors().isEmpty());
            assertEquals(List.of(MODEL_PACKAGE), unit.info.getAllPackageDescriptors());
            assertEquals(1, factory.getMetamodel().getEntities().size());
            assertEquals(0, DiscoveredListener.FACTORIES.get());
            assertFalse(PersistenceUnitProcessor.buildClassSet(unit.info, unit.properties).stream()
                    .anyMatch(name -> name.endsWith("package-info") || name.endsWith("module-info")));
        }
    }

    @Test
    public void excludedRootDoesNotDiscoverItsPackageDescriptor() throws Exception {
        try (Archive archive = new Archive(MODEL_PACKAGE); Unit unit = new Unit(Record.class)) {
            unit.info.setPersistenceUnitRootUrl(archive.url());
            verifyInsert(unit.create(), false, ENTITY_EVENTS);
            assertTrue(unit.info.getAllPackageDescriptors().isEmpty());
        }
    }

    @Test
    public void referencedJarDiscoveryRemainsEnabledWhenRootIsExcluded() throws Exception {
        try (Archive archive = new Archive(MODEL_PACKAGE); Unit unit = new Unit(Record.class)) {
            unit.info.setJarFileUrls(List.of(archive.url()));
            verifyInsert(unit.create(), false, PACKAGE_EVENTS);
            assertTrue(unit.info.getManagedPackageDescriptors().isEmpty());
            assertEquals(List.of(MODEL_PACKAGE), unit.info.getAllPackageDescriptors());
        }
    }

    @Test
    public void explicitAndScannedPackageDescriptorIsProcessedOnlyOnce() throws Exception {
        try (Archive archive = new Archive(MODEL_PACKAGE); Unit unit = new Unit(Record.class)) {
            unit.info.setJarFileUrls(List.of(archive.url()));
            unit.packages(MODEL_PACKAGE);
            verifyInsert(unit.create(), false, PACKAGE_EVENTS);
            assertEquals(List.of(MODEL_PACKAGE), unit.info.getAllPackageDescriptors());
        }
    }

    @Test
    public void ormPackageQualifierDoesNotEnrollPackageDescriptor() {
        try (Unit unit = new Unit()) {
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/package.xml"));
            verifyInsert(unit.create(), false, ENTITY_EVENTS);
            assertTrue(unit.info.getAllPackageDescriptors().isEmpty());
        }
    }

    @Test
    public void metadataCompleteEntitySuppressesPackageCallbacks() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(MODEL_PACKAGE);
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/complete.xml"));
            verifyInsert(unit.create(), false, List.of());
        }
    }

    @Test
    public void projectCacheRestoresPackageAndExplicitlyManagedLifecycleListeners() {
        try (Unit unit = new Unit(Record.class, DiscoveredListener.class)) {
            unit.packages(MODEL_PACKAGE);
            MemoryCache cache = new MemoryCache();
            unit.properties.put(PersistenceUnitProperties.PROJECT_CACHE, cache);
            EntityManagerFactory first = unit.create();
            verifyInsert(first, false, ALL_EVENTS);
            assertEquals(1, DiscoveredListener.FACTORIES.get());
            first.close();
            EntityManagerFactory restored = unit.create();
            verifyInsert(restored, false, ALL_EVENTS);
            assertEquals(1, cache.hits);
            assertEquals(2, DiscoveredListener.FACTORIES.get());
        }
    }

    @Test
    public void legacyPersistenceUnitRetainsImplicitPackageGenerator() {
        try (Unit unit = new Unit(Generated.class)) {
            unit.info.setPersistenceXMLSchemaVersion("3.2");
            verifyGeneratedIdentity(unit.create());
        }
    }

    @Test
    public void explicitlyRegisteredPackageProvidesGeneratorInPersistence40() {
        try (Unit unit = new Unit(Generated.class)) {
            unit.packages(MODEL_PACKAGE);
            verifyGeneratedIdentity(unit.create());
        }
    }

    private static void verifyGeneratedIdentity(EntityManagerFactory factory) {
        try (EntityManager manager = factory.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                Generated record = new Generated();
                manager.persist(record);
                manager.flush();
                assertNotNull(record.id);
                assertTrue(record.id > 0);
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }

    private static void verifyInsert(EntityManagerFactory factory, boolean managerWrite, List<String> expected) {
        Record record = new Record(10);
        if (managerWrite) {
            try (EntityManager manager = factory.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    manager.persist(record);
                    manager.flush();
                    assertEquals(expected, record.events);
                } finally {
                    manager.getTransaction().rollback();
                }
            }
        } else {
            try (EntityAgent agent = factory.createEntityAgent()) {
                agent.getTransaction().begin();
                try {
                    agent.insert(record);
                    assertEquals(expected, record.events);
                } finally {
                    agent.getTransaction().rollback();
                }
            }
        }
    }

    private static PersistenceConfiguration configuration(String name) {
        return new PersistenceConfiguration(name).provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .managedClass(Record.class).properties(properties());
    }

    private static Map<String, Object> properties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "false");
        properties.put(PersistenceUnitProperties.DDL_GENERATION, "none");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        properties.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String name : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(name);
            if (value != null) {
                properties.put(name, value);
            }
        }
        return properties;
    }

    private static final class Unit implements AutoCloseable {
        final SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        final Map<String, Object> properties = properties();
        final List<EntityManagerFactory> factories = new ArrayList<>();

        Unit(Class<?>... classes) {
            info.setPersistenceUnitName("package-descriptors-" + UUID.randomUUID());
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(TestPackageDescriptors.class.getClassLoader());
            info.setNewTempClassLoader(TestPackageDescriptors.class.getClassLoader());
            info.setPersistenceUnitRootUrl(TestPackageDescriptors.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(Arrays.stream(classes).map(Class::getName).toList());
            info.setExcludeUnlistedClasses(true);
            info.setProperties(new Properties());
        }

        void packages(String... names) {
            info.setManagedPackageDescriptors(Arrays.asList(names));
        }

        EntityManagerFactory create() {
            Map<String, Object> settings = new HashMap<>(properties);
            settings.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), settings);
            factories.add(factory);
            return factory;
        }

        @Override
        public void close() {
            for (EntityManagerFactory factory : factories.reversed()) {
                if (factory.isOpen()) {
                    factory.close();
                }
            }
        }
    }

    private static final class Archive implements AutoCloseable {
        final Path file;

        Archive(String packageName) throws Exception {
            file = Files.createTempFile("package-descriptor-fixture-", ".jar");
            String resource = packageName.replace('.', '/') + "/package-info.class";
            try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(file));
                    InputStream source = TestPackageDescriptors.class.getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(resource, source);
                output.putNextEntry(new JarEntry(resource));
                source.transferTo(output);
                output.closeEntry();
            }
        }

        URL url() throws Exception {
            return file.toUri().toURL();
        }

        @Override
        public void close() throws Exception {
            Files.deleteIfExists(file);
        }
    }

    /** A container archive permits optional mapping lookups but forbids rediscovery. */
    public static final class ContainerArchiveFactory extends ArchiveFactoryImpl {
        @Override
        public org.eclipse.persistence.jpa.Archive createArchive(URL root, Map properties) {
            return new org.eclipse.persistence.jpa.Archive() {
                @Override
                public Iterator<String> getEntries() {
                    throw new AssertionError("Container-supplied package descriptors must not be rescanned");
                }

                @Override
                public InputStream getEntry(String name) {
                    return null;
                }

                @Override
                public URL getEntryAsURL(String name) {
                    return null;
                }

                @Override
                public URL getRootURL() {
                    return root;
                }

                @Override
                public InputStream getDescriptorStream() {
                    return null;
                }

                @Override
                public void close() {
                }
            };
        }
    }

    /** Snapshot metadata at store time, as FileBasedProjectCache does. */
    private static final class MemoryCache implements ProjectCache {
        private byte[] snapshot;
        private int hits;

        @Override
        public Project retrieveProject(Map<String, ?> properties, ClassLoader loader, SessionLog log) {
            if (snapshot == null) {
                return null;
            }
            hits++;
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(snapshot))) {
                return (Project) input.readObject();
            } catch (IOException | ClassNotFoundException failure) {
                throw new IllegalStateException("Could not restore the cached project fixture", failure);
            }
        }

        @Override
        public void storeProject(Project project, Map<String, ?> properties, SessionLog log) {
            try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(project);
                output.flush();
                snapshot = bytes.toByteArray();
            } catch (IOException failure) {
                throw new IllegalStateException("Could not snapshot the cached project fixture", failure);
            }
        }
    }
}
