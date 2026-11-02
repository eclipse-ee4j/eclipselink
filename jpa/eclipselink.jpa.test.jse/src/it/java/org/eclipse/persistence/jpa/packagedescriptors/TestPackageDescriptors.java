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
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.tools.ToolProvider;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.QueryFlushMode;
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
    private static final String QUERIES_PACKAGE = "org.eclipse.persistence.jpa.packagedescriptors.queries";
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
            verifyRecordQueries(factory, "packageDescriptorFind", "packageDescriptorNative");
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
                    assertEquals(1, agent.createNamedStatement("packageDescriptorNativeRename")
                            .setParameter(1, "native-renamed").setParameter(2, 13).execute());
                    assertEquals("native-renamed", agent.get(Record.class, 13).value);
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

    @Test
    public void packageQueriesExecuteForBothHandlersWithTheirMetadata() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(METADATA_PACKAGE);
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "packageDescriptorFind", "packageDescriptorNative");
            try (EntityManager manager = factory.createEntityManager()) {
                var query = manager.createNamedQuery("packageDescriptorFind");
                assertEquals(QueryFlushMode.NO_FLUSH, query.getQueryFlushMode());
                assertEquals("7", String.valueOf(query.getHints().get("eclipselink.jdbc.fetch-size")));
            }
            assertEquals(1, factory.getMetamodel().getEntities().size());
        }
    }

    @Test
    public void containerAnnotationsPreserveInlineMappingsAndStatements() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(QUERIES_PACKAGE);
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "packageContainerFind", "packageContainerNative");
            verifyScalarQueries(factory, "packageContainerValues", "packageContainerColumns");
            verifyStatements(factory, "packageContainer");
        }
    }

    @Test
    public void repeatedPackageAnnotationsRegisterQueriesAndStatements() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(QUERIES_PACKAGE + ".repeated");
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "packageRepeatedFind", "packageRepeatedNative");
            verifyScalarQueries(factory, "packageRepeatedValues", "packageRepeatedColumns");
            verifyStatements(factory, "packageRepeated");
        }
    }

    @Test
    public void repeatedHolderQueriesDoNotInitializeOrEnrollTheirDeclaringClass() {
        try (Unit unit = new Unit(Record.class, QueryFixtures.Holder.class)) {
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "holderFind", "holderNative");
            verifyScalarQueries(factory, "holderValues", "holderColumns");
            verifyStatements(factory, "holder");
            assertEquals(1, factory.getMetamodel().getEntities().size());
            assertThrows(IllegalArgumentException.class, () -> factory.getMetamodel().entity(QueryFixtures.Holder.class));
        }
    }

    @Test
    public void identicalQueriesRetainOneRegistration() {
        try (Unit unit = new Unit(Record.class, QueryFixtures.FirstDuplicate.class, QueryFixtures.SecondDuplicate.class)) {
            EntityManagerFactory factory = unit.create();
            assertTrue(factory.getNamedQueries(Record.class).containsKey("duplicateQuery"));
            try (EntityManager manager = factory.createEntityManager()) {
                assertTrue(manager.createNamedQuery("duplicateQuery").getResultList().isEmpty());
            }
        }
    }

    @Test
    public void conflictingDeclarationsKeepTheExistingDuplicateError() throws Exception {
        try (StatementModule module = new StatementModule(); Unit unit = new Unit(Record.class, module.conflictingHolder)) {
            unit.info.setClassLoader(module.loader);
            unit.info.setNewTempClassLoader(module.loader);
            unit.packages(METADATA_PACKAGE);
            RuntimeException failure = assertThrows(RuntimeException.class, () -> unit.create().createEntityManager());
            assertTrue(failure.toString(), exceptionText(failure).contains("packageDescriptorFind"));
            assertTrue(exceptionText(failure).contains("Conflicting annotations with the same name"));
        }
    }

    @Test
    public void entityAndMappedSuperclassKeepTheirExistingQueryProducer() {
        try (Unit unit = new Unit(QueryFixtures.AnnotatedEntity.class, QueryFixtures.AnnotatedSuperclass.class)) {
            try (EntityManager manager = unit.create().createEntityManager()) {
                assertTrue(manager.createNamedQuery("entityQuery").getResultList().isEmpty());
                assertTrue(manager.createNamedQuery("superclassQuery").getResultList().isEmpty());
            }
        }
    }

    @Test
    public void xmlCompleteSuperclassesSuppressAnnotationsForEntityDescendants() {
        try (Unit unit = new Unit(QueryFixtures.XmlEntity.class, QueryFixtures.XmlSuperclass.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-superclass.xml"));
            EntityManagerFactory factory = unit.create();
            verifyEmptyQueries(factory, "xmlSuperclassQuery");
            verifyMissingSuperclassQueries(factory);
        }
    }

    @Test
    public void xmlEnabledSuperclassesRetainQueriesWithEntityDescendants() {
        insertQueryRecord();
        for (String mapping : List.of("incomplete", "false")) {
            try (Unit unit = new Unit(Record.class, QueryFixtures.XmlSuperclass.class, QueryFixtures.XmlEntity.class)) {
                unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-standalone-superclass-" + mapping + ".xml",
                        "META-INF/package-descriptors/query-superclass-descendant.xml"));
                EntityManagerFactory factory = unit.create();
                verifyStatements(factory, "xmlSuperclass");
                verifySuperclassQueries(factory);
                assertEquals(2, factory.getMetamodel().getEntities().size());
            }
        }
    }

    @Test
    public void xmlDefinitionsOverrideSuperclassAnnotationsWithEntityDescendants() {
        insertQueryRecord();
        try (Unit unit = new Unit(Record.class, QueryFixtures.XmlSuperclass.class, QueryFixtures.XmlEntity.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-standalone-superclass-false.xml",
                    "META-INF/package-descriptors/query-superclass-descendant.xml",
                    "META-INF/package-descriptors/query-superclass-override.xml"));
            EntityManagerFactory factory = unit.create();
            verifyEmptyQueries(factory, "xmlSuperclassAnnotation", "xmlSuperclassNative");
            verifyStatements(factory, "xmlSuperclass");
        }
    }

    @Test
    public void xmlStandaloneSuperclassesRetainAllQueryAnnotationFamilies() {
        insertQueryRecord();
        for (String mapping : List.of("incomplete", "false")) {
            try (Unit unit = new Unit(Record.class, QueryFixtures.XmlSuperclass.class)) {
                unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-standalone-superclass-" + mapping + ".xml"));
                EntityManagerFactory factory = unit.create();
                // No mapped entity descends from this superclass: its legacy
                // statements must not depend on a descendant processing it.
                verifyStatements(factory, "xmlSuperclass");
                verifySuperclassQueries(factory);
            }
        }
    }

    @Test
    public void xmlCompleteStandaloneSuperclassesSuppressAllQueryAnnotationFamilies() {
        try (Unit unit = new Unit(Record.class, QueryFixtures.XmlSuperclass.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-standalone-superclass-complete.xml"));
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "xmlStandaloneQuery");
            verifyMissingSuperclassQueries(factory);
        }
    }

    @Test
    public void xmlQueryDefinitionsOverridePackageAnnotations() {
        insertQueryRecord();
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(METADATA_PACKAGE);
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-override.xml"));
            verifyEmptyQueries(unit.create(), "packageDescriptorFind", "packageDescriptorNative");
        }
    }

    @Test
    public void globalMetadataCompleteKeepsXmlQueriesAndSuppressesDescriptorAndHolderQueries() {
        try (Unit unit = new Unit(Record.class, QueryFixtures.Holder.class)) {
            unit.packages(METADATA_PACKAGE);
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-complete.xml"));
            EntityManagerFactory factory = unit.create();
            verifyEmptyQueries(factory, "xmlOnlyQuery");
            try (EntityManager manager = factory.createEntityManager()) {
                for (String name : List.of("packageDescriptorFind", "packageDescriptorNative", "holderFind", "holderNative")) {
                    assertThrows(name, IllegalArgumentException.class, () -> manager.createNamedQuery(name));
                }
            }
        }
    }

    @Test
    public void sameNamedFactoriesKeepSeparateQueryMembership() {
        String name = "package-query-isolation-" + UUID.randomUUID();
        try (EntityManagerFactory absent = configuration(name).createEntityManagerFactory();
                EntityManagerFactory present = configuration(name).managedPackageDescriptor(METADATA_PACKAGE)
                        .createEntityManagerFactory()) {
            verifyRecordQueries(present, "packageDescriptorFind", "packageDescriptorNative");
            try (EntityManager manager = absent.createEntityManager()) {
                assertThrows(IllegalArgumentException.class, () -> manager.createNamedQuery("packageDescriptorFind"));
            }
        }
    }

    @Test
    public void cachedProjectRetainsPackageQueries() {
        try (Unit unit = new Unit(Record.class)) {
            unit.packages(METADATA_PACKAGE);
            MemoryCache cache = new MemoryCache();
            unit.properties.put(PersistenceUnitProperties.PROJECT_CACHE, cache);
            EntityManagerFactory first = unit.create();
            verifyEmptyQueries(first, "packageDescriptorFind", "packageDescriptorNative");
            first.close();
            verifyRecordQueries(unit.create(), "packageDescriptorFind", "packageDescriptorNative");
            assertEquals(1, cache.hits);
        }
    }

    @Test
    public void discoveredAndExcludedDescriptorsRespectQueryMembership() throws Exception {
        try (Archive archive = new Archive(METADATA_PACKAGE)) {
            try (Unit root = new Unit(Record.class)) {
                root.info.setPersistenceUnitRootUrl(archive.url());
                root.info.setExcludeUnlistedClasses(false);
                verifyEmptyQueries(root.create(), "packageDescriptorFind", "packageDescriptorNative");
            }
            try (Unit jar = new Unit(Record.class)) {
                jar.info.setJarFileUrls(List.of(archive.url()));
                jar.packages(METADATA_PACKAGE);
                verifyEmptyQueries(jar.create(), "packageDescriptorFind", "packageDescriptorNative");
            }
            try (Unit excluded = new Unit(Record.class)) {
                excluded.info.setPersistenceUnitRootUrl(archive.url());
                try (EntityManager manager = excluded.create().createEntityManager()) {
                    assertThrows(IllegalArgumentException.class, () -> manager.createNamedQuery("packageDescriptorFind"));
                }
            }
        }
    }

    @Test
    public void containerCompleteDescriptorListContributesQueries() throws Exception {
        try (Unit unit = new Unit(Record.class)) {
            unit.info.setPersistenceUnitRootUrl(new URL("file:/__unavailable_query_descriptor_root__/"));
            unit.properties.put(SystemProperties.ARCHIVE_FACTORY, ContainerArchiveFactory.class.getName());
            PersistenceUnitInfo supplied = (PersistenceUnitInfo) Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[] {PersistenceUnitInfo.class}, (proxy, method, args) -> {
                        if (method.getName().equals("getAllPackageDescriptors")) {
                            return List.of(METADATA_PACKAGE);
                        }
                        try {
                            return method.invoke(unit.info, args);
                        } catch (InvocationTargetException failure) {
                            throw failure.getCause();
                        }
                    });
            try (EntityManagerFactory factory = new PersistenceProvider()
                    .createContainerEntityManagerFactory(supplied, unit.properties)) {
                verifyRecordQueries(factory, "packageDescriptorFind", "packageDescriptorNative");
            }
        }
    }

    @Test
    public void ordinaryHolderPreservesMappingsLocksGraphsAndHints() {
        insertQueryRecord();
        try (Unit unit = new Unit(QueryFixtures.RichEntity.class, QueryFixtures.RichHolder.class)) {
            EntityManagerFactory factory = unit.create();
            for (boolean useAgent : List.of(false, true)) {
                try (EntityHandler handler = useAgent ? factory.createEntityAgent() : factory.createEntityManager()) {
                    assertEquals(41, ((QueryFixtures.RichEntity) handler.createNamedQuery("holderMapping").getSingleResult()).id);
                    assertEquals("original", ((QueryFixtures.Value) handler.createNamedQuery("holderConstructor").getSingleResult()).value());
                    var query = handler.createNamedQuery("holderLocked");
                    assertEquals(QueryFlushMode.NO_FLUSH, query.getQueryFlushMode());
                    assertEquals(LockModeType.PESSIMISTIC_READ, query.getLockMode());
                    assertEquals("EXTENDED", String.valueOf(query.getHints().get("jakarta.persistence.lock.scope")));
                    assertTrue(query.getHints().containsKey("jakarta.persistence.loadgraph"));
                    assertEquals("9", String.valueOf(query.getHints().get("eclipselink.jdbc.fetch-size")));
                    assertEquals(QueryFlushMode.NO_FLUSH, handler.createNamedQuery("holderMapping").getQueryFlushMode());
                    handler.getTransaction().begin();
                    try {
                        assertEquals(41, ((QueryFixtures.RichEntity) query.getSingleResult()).id);
                    } finally {
                        handler.getTransaction().rollback();
                    }
                }
            }
        }
    }

    @Test
    public void identicalExplicitAndOmittedDefaultsRetainOneRegistration() {
        insertQueryRecord();
        try (Unit unit = new Unit(Record.class, QueryFixtures.FirstDuplicate.class, QueryFixtures.SecondDuplicate.class,
                QueryFixtures.FirstExplicitDuplicate.class, QueryFixtures.SecondExplicitDuplicate.class)) {
            EntityManagerFactory factory = unit.create();
            try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
                for (String name : List.of("duplicateQuery", "explicitDefaultQuery")) {
                    assertEquals(41, ((Record) manager.createNamedQuery(name).getSingleResult()).id);
                    assertEquals(41, ((Record) agent.createNamedQuery(name).getSingleResult()).id);
                }
                for (String name : List.of("duplicateNative", "explicitDefaultNative")) {
                    assertEquals(41L, manager.createNamedQuery(name).getSingleResult());
                    assertEquals(41L, agent.createNamedQuery(name).getSingleResult());
                }
            }
        }
    }

    @Test
    public void cachedModuleStatementsSurviveMissingOrMalformedDescriptorResources() throws Exception {
        insertQueryRecord();
        for (String resource : List.of("readable", "missing", "malformed")) {
            try (StatementModule module = new StatementModule(); Unit unit = module.unit()) {
                Path descriptor = module.directory.resolve("classes/module-info.class");
                if (resource.equals("missing")) {
                    Files.delete(descriptor);
                } else if (resource.equals("malformed")) {
                    Files.write(descriptor, new byte[] {0, 1, 2, 3});
                }
                EntityManagerFactory factory = unit.create();
                for (boolean useAgent : List.of(false, true)) {
                    try (EntityHandler handler = useAgent ? factory.createEntityAgent() : factory.createEntityManager()) {
                        for (String name : List.of("moduleRename", "moduleNativeRename")) {
                            assertEquals("5", String.valueOf(handler.createNamedStatement(name).getHints().get("eclipselink.jdbc.timeout")));
                        }
                    }
                }
                verifyStatements(factory, "module");
                assertEquals(2, factory.getMetamodel().getEntities().size());
            }
        }
    }

    @Test
    public void xmlIncompleteEmbeddablesRetainAllQueryAnnotationFamilies() {
        insertQueryRecord();
        for (String mapping : List.of("incomplete", "false")) {
            try (Unit unit = new Unit(Record.class, QueryFixtures.EmbeddableHolder.class)) {
                unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-embeddable-" + mapping + ".xml"));
                EntityManagerFactory factory = unit.create();
                try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
                    for (String name : List.of("embeddableFind", "embeddableNative", "xmlEmbeddableQuery")) {
                        assertEquals(41, ((Record) manager.createNamedQuery(name).getSingleResult()).id);
                        assertEquals(41, ((Record) agent.createNamedQuery(name).getSingleResult()).id);
                    }
                }
                verifyStatements(factory, "embeddable");
            }
        }
    }

    @Test
    public void xmlCompleteEmbeddablesSuppressAllQueryAnnotationFamilies() {
        try (Unit unit = new Unit(Record.class, QueryFixtures.EmbeddableHolder.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-embeddable-complete.xml"));
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "xmlEmbeddableQuery");
            for (boolean useAgent : List.of(false, true)) {
                try (EntityHandler handler = useAgent ? factory.createEntityAgent() : factory.createEntityManager()) {
                    for (String name : List.of("embeddableFind", "embeddableNative")) {
                        assertThrows(name, IllegalArgumentException.class, () -> handler.createNamedQuery(name));
                    }
                    for (String name : List.of("embeddableRename", "embeddableDelete", "embeddableNativeRename", "embeddableNativeDelete")) {
                        assertThrows(name, IllegalArgumentException.class, () -> handler.createNamedStatement(name));
                    }
                }
            }
        }
    }

    @Test
    public void xmlCompleteEmbeddablesCannotConflictWithEnabledQueryDeclarations() throws Exception {
        try (StatementModule module = new StatementModule();
                Unit unit = new Unit(Record.class, QueryFixtures.EmbeddableHolder.class, module.conflictingEmbeddable)) {
            unit.info.setClassLoader(module.loader);
            unit.info.setNewTempClassLoader(module.loader);
            unit.info.setMappingFileNames(List.of("META-INF/package-descriptors/query-embeddable-conflict.xml"));
            EntityManagerFactory factory = unit.create();
            verifyRecordQueries(factory, "embeddableFind", "embeddableNative", "xmlEmbeddableQuery");
            verifyStatements(factory, "embeddable");
        }
    }

    private void insertQueryRecord() {
        fixture.runInTransaction(manager -> manager.persist(new Record(41)));
    }

    private void verifyRecordQueries(EntityManagerFactory factory, String... names) {
        insertQueryRecord();
        try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
            for (String name : names) {
                assertEquals(name, 41, ((Record) manager.createNamedQuery(name).getSingleResult()).id);
                assertEquals(name, 41, ((Record) agent.createNamedQuery(name).getSingleResult()).id);
            }
        }
    }

    private static void verifyScalarQueries(EntityManagerFactory factory, String values, String columns) {
        try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
            assertEquals("original", manager.createNamedQuery(values).getSingleResult());
            assertEquals("original", agent.createNamedQuery(values).getSingleResult());
            assertEquals(41L, manager.createNamedQuery(columns).getSingleResult());
            assertEquals(41L, agent.createNamedQuery(columns).getSingleResult());
        }
    }

    private static void verifyEmptyQueries(EntityManagerFactory factory, String... names) {
        try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
            for (String name : names) {
                assertTrue(name, manager.createNamedQuery(name).getResultList().isEmpty());
                assertTrue(name, agent.createNamedQuery(name).getResultList().isEmpty());
            }
        }
    }

    private static void verifySuperclassQueries(EntityManagerFactory factory) {
        for (boolean useAgent : List.of(false, true)) {
            try (EntityHandler handler = useAgent ? factory.createEntityAgent() : factory.createEntityManager()) {
                for (String name : List.of("xmlSuperclassAnnotation", "xmlSuperclassNative", "xmlStandaloneQuery")) {
                    assertTrue(factory.getNamedQueries(Record.class).containsKey(name));
                    assertEquals(41, ((Record) handler.createNamedQuery(name).getSingleResult()).id);
                }
            }
        }
    }

    private static void verifyMissingSuperclassQueries(EntityManagerFactory factory) {
        for (boolean useAgent : List.of(false, true)) {
            try (EntityHandler handler = useAgent ? factory.createEntityAgent() : factory.createEntityManager()) {
                for (String name : List.of("xmlSuperclassAnnotation", "xmlSuperclassNative")) {
                    assertThrows(name, IllegalArgumentException.class, () -> handler.createNamedQuery(name));
                }
                for (String name : List.of("xmlSuperclassRename", "xmlSuperclassDelete", "xmlSuperclassNativeRename", "xmlSuperclassNativeDelete")) {
                    assertThrows(name, IllegalArgumentException.class, () -> handler.createNamedStatement(name));
                }
            }
        }
    }

    private static void verifyStatements(EntityManagerFactory factory, String prefix) {
        for (boolean useAgent : List.of(false, true)) {
            for (boolean nativeSql : List.of(false, true)) {
                try (EntityHandler handler = useAgent ? factory.createEntityAgent() : factory.createEntityManager()) {
                    handler.getTransaction().begin();
                    try {
                        var rename = handler.createNamedStatement(prefix + (nativeSql ? "NativeRename" : "Rename"));
                        var delete = handler.createNamedStatement(prefix + (nativeSql ? "NativeDelete" : "Delete"));
                        if (nativeSql) {
                            rename.setParameter(1, "changed").setParameter(2, 41);
                            delete.setParameter(1, 41);
                        } else {
                            rename.setParameter("value", "changed").setParameter("id", 41);
                            delete.setParameter("id", 41);
                        }
                        assertEquals(1, rename.execute());
                        assertEquals("changed", handler.createNativeQuery("select ROW_VALUE from JPA_PD_RECORD where ROW_ID = 41").getSingleResult());
                        assertEquals(1, delete.execute());
                        assertTrue(handler.createNativeQuery("select ROW_VALUE from JPA_PD_RECORD where ROW_ID = 41").getResultList().isEmpty());
                    } finally {
                        handler.getTransaction().rollback();
                    }
                }
            }
        }
    }

    private static String exceptionText(Throwable failure) {
        StringBuilder text = new StringBuilder();
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            text.append(cause).append('\n');
        }
        return text.toString();
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

    /** A real module using the existing API class loader, without a second API module. */
    private static final class StatementModule implements AutoCloseable {
        final Path directory;
        final ClassLoader loader;
        final Class<?> record;
        final Class<?> other;
        final Class<?> conflictingHolder;
        final Class<?> conflictingEmbeddable;

        StatementModule() throws Exception {
            directory = Files.createTempDirectory("named-statement-module-");
            try {
                Path source = Files.createDirectories(directory.resolve("source/descriptor/fixture"));
                Path output = Files.createDirectories(directory.resolve("classes"));
                Path moduleInfo = directory.resolve("source/module-info.java");
                Files.writeString(moduleInfo, """
                        @jakarta.persistence.NamedStatement(name="moduleRename",
                            statement="update ModuleRecord r set r.value = :value where r.id = :id",
                            hints=@jakarta.persistence.QueryHint(name="eclipselink.jdbc.timeout", value="5"))
                        @jakarta.persistence.NamedStatement(name="moduleDelete", statement="delete from ModuleRecord r where r.id = :id")
                        @jakarta.persistence.NamedNativeStatement(name="moduleNativeRename",
                            statement="update JPA_PD_RECORD set ROW_VALUE = ? where ROW_ID = ?",
                            hints=@jakarta.persistence.QueryHint(name="eclipselink.jdbc.timeout", value="5"))
                        @jakarta.persistence.NamedNativeStatement(name="moduleNativeDelete", statement="delete from JPA_PD_RECORD where ROW_ID = ?")
                        open module descriptor.fixture { exports descriptor.fixture; }
                        """);
                Path entity = source.resolve("ModuleRecord.java");
                Files.writeString(entity, """
                        package descriptor.fixture;
                        @jakarta.persistence.Entity(name="ModuleRecord")
                        @jakarta.persistence.Table(name="JPA_PD_RECORD")
                        public class ModuleRecord {
                            @jakarta.persistence.Id @jakarta.persistence.Column(name="ROW_ID") public int id;
                            @jakarta.persistence.Column(name="ROW_VALUE") public String value;
                        }
                        """);
                Path secondEntity = source.resolve("Other.java");
                Files.writeString(secondEntity, """
                        package descriptor.fixture;
                        @jakarta.persistence.Entity(name="ModuleOther")
                        @jakarta.persistence.Table(name="JPA_PD_RECORD")
                        public class Other {
                            @jakarta.persistence.Id @jakarta.persistence.Column(name="ROW_ID") public int id;
                        }
                        """);
                Path embeddable = source.resolve("ConflictingEmbeddable.java");
                Files.writeString(embeddable, """
                        package descriptor.fixture;
                        @jakarta.persistence.Embeddable
                        @jakarta.persistence.NamedQuery(name="embeddableFind", query="select r from PackageDescriptorRecord r where r.id < 0")
                        @jakarta.persistence.NamedNativeQuery(name="embeddableNative", query="select ROW_ID from JPA_PD_RECORD where ROW_ID < 0")
                        @jakarta.persistence.NamedStatement(name="embeddableRename", statement="delete from PackageDescriptorRecord r")
                        @jakarta.persistence.NamedNativeStatement(name="embeddableNativeRename", statement="delete from JPA_PD_RECORD")
                        public class ConflictingEmbeddable { public String value; }
                        """);
                Path conflict = source.resolve("ConflictingHolder.java");
                Files.writeString(conflict, """
                        package descriptor.fixture;
                        @jakarta.persistence.NamedQuery(name="packageDescriptorFind",
                            query="select r from PackageDescriptorRecord r where r.id < 0")
                        public class ConflictingHolder {}
                        """);
                String classpath = Path.of(Persistence.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
                ByteArrayOutputStream diagnostics = new ByteArrayOutputStream();
                int status = ToolProvider.getSystemJavaCompiler().run(null, diagnostics, diagnostics,
                        "-proc:none", "--class-path", classpath, "--add-reads", "descriptor.fixture=ALL-UNNAMED",
                        "-d", output.toString(), moduleInfo.toString(), entity.toString(), secondEntity.toString(), embeddable.toString(), conflict.toString());
                assertEquals(diagnostics.toString(), 0, status);
                var configuration = ModuleLayer.boot().configuration().resolve(ModuleFinder.of(output), ModuleFinder.of(),
                        Set.of("descriptor.fixture"));
                var controller = ModuleLayer.defineModulesWithOneLoader(configuration, List.of(ModuleLayer.boot()),
                        TestPackageDescriptors.class.getClassLoader());
                Module module = controller.layer().findModule("descriptor.fixture").orElseThrow();
                controller.addReads(module, Persistence.class.getModule());
                loader = controller.layer().findLoader("descriptor.fixture");
                record = Class.forName("descriptor.fixture.ModuleRecord", false, loader);
                other = Class.forName("descriptor.fixture.Other", false, loader);
                conflictingHolder = Class.forName("descriptor.fixture.ConflictingHolder", false, loader);
                assertTrue(record.getModule().isNamed());
                assertEquals("descriptor.fixture", record.getModule().getName());
                conflictingEmbeddable = Class.forName("descriptor.fixture.ConflictingEmbeddable", false, loader);
                // Populate the JDK cache before the test changes descriptor bytes.
                assertEquals(2, module.getAnnotationsByType(jakarta.persistence.NamedStatement.class).length);
                assertEquals(2, module.getAnnotationsByType(jakarta.persistence.NamedNativeStatement.class).length);
            } catch (Throwable failure) {
                close();
                throw failure;
            }
        }

        Unit unit() {
            Unit unit = new Unit(record, other);
            unit.info.setClassLoader(loader);
            unit.info.setNewTempClassLoader(loader);
            return unit;
        }

        @Override
        public void close() throws IOException {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
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
