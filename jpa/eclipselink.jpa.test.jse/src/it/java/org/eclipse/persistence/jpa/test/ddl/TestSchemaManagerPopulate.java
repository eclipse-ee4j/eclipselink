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
package org.eclipse.persistence.jpa.test.ddl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.jpa.test.ddl.model.PopulateRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.PUPropertiesProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE;

@RunWith(EmfRunner.class)
public class TestSchemaManagerPopulate implements PUPropertiesProvider {
    private static final String RESOURCE = "META-INF/schema-populate.sql";
    private static final long SENTINEL_ID = 701000L;
    private static final long SCRIPT_ID = 701001L;
    private static final long READER_ID = 701002L;
    private static Path scriptFile;

    @Emf(name = "PopulateResource", classes = {PopulateRecord.class})
    private EntityManagerFactory resource;

    @Emf(name = "PopulateUrl", classes = {PopulateRecord.class})
    private EntityManagerFactory url;

    @Emf(name = "PopulateReader", classes = {PopulateRecord.class})
    private EntityManagerFactory reader;

    @Emf(name = "PopulateNoSource", classes = {PopulateRecord.class})
    private EntityManagerFactory noSource;

    @Emf(name = "PopulateDestructiveAction", classes = {PopulateRecord.class}, createTables = DDLGen.NONE)
    private EntityManagerFactory destructiveAction;

    @Emf(name = "PopulateInvalidSource", classes = {PopulateRecord.class})
    private EntityManagerFactory invalidSource;

    @Override
    public Map<String, Object> getAdditionalPersistenceProperties(String puName) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("eclipselink.cache.shared.default", "false");
        switch (puName) {
            case "PopulateResource" -> properties.put(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, RESOURCE);
            case "PopulateUrl" -> properties.put(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, scriptUrl());
            case "PopulateReader" -> properties.put(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE,
                    new StringReader(insert(READER_ID, "from-reader")));
            case "PopulateDestructiveAction" -> {
                properties.put(SCHEMA_GENERATION_DATABASE_ACTION, "drop-and-create");
                properties.put(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, RESOURCE);
            }
            case "PopulateInvalidSource" -> properties.put(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, 17);
            default -> { }
        }
        return properties;
    }

    @Before
    public void prepareRows() {
        for (EntityManagerFactory factory : new EntityManagerFactory[] {
                resource, url, reader, noSource, destructiveAction, invalidSource}) {
            Assert.assertNotNull("The factory must be injected", factory);
            Assert.assertTrue("The factory must be open", factory.isOpen());
        }
        // Remove any bootstrap import before proving the explicit operation.
        removeRows();
        try (EntityManager em = resource.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(new PopulateRecord(SENTINEL_ID, "sentinel"));
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
        assertRows(resource, 1);
        assertValue(resource, SENTINEL_ID, "sentinel");
    }

    @After
    public void cleanRows() {
        removeRows();
    }

    @AfterClass
    public static void deleteScriptFile() throws IOException {
        if (scriptFile != null) {
            Files.deleteIfExists(scriptFile);
            scriptFile = null;
        }
    }

    @Test
    public void resourceImportPreservesSentinelAndReloadsRows() {
        resource.getSchemaManager().populate();
        assertImported(resource, SCRIPT_ID, "from-script");
    }

    @Test
    public void fileUrlImportPreservesSentinelAndReloadsRows() {
        url.getSchemaManager().populate();
        assertImported(url, SCRIPT_ID, "from-script");
    }

    @Test
    public void freshReaderImportPreservesSentinelAndReloadsRows() {
        reader.getSchemaManager().populate();
        assertImported(reader, READER_ID, "from-reader");
    }

    @Test
    public void noSourcePreservesExistingRows() {
        noSource.getSchemaManager().populate();
        assertRows(noSource, 1);
        assertValue(noSource, SENTINEL_ID, "sentinel");
    }

    @Test
    public void repeatedResourceImportReopensScript() {
        resource.getSchemaManager().populate();
        assertImported(resource, SCRIPT_ID, "from-script");
        removeImportedRow();
        resource.getSchemaManager().populate();
        assertImported(resource, SCRIPT_ID, "from-script");
    }

    @Test
    public void repeatedFileUrlImportReadsChangedFile() throws IOException {
        url.getSchemaManager().populate();
        assertImported(url, SCRIPT_ID, "from-script");
        removeImportedRow();
        try {
            Files.writeString(scriptFile, insert(SCRIPT_ID, "from-updated-file"), StandardCharsets.UTF_8);
            url.getSchemaManager().populate();
            assertImported(url, SCRIPT_ID, "from-updated-file");
        } finally {
            Files.writeString(scriptFile, insert(SCRIPT_ID, "from-script"), StandardCharsets.UTF_8);
        }
    }

    @Test
    public void explicitPopulateDoesNotReplayDestructiveStartupAction() {
        Assert.assertEquals("drop-and-create", destructiveAction.getProperties().get(SCHEMA_GENERATION_DATABASE_ACTION));
        destructiveAction.getSchemaManager().populate();
        assertImported(destructiveAction, SCRIPT_ID, "from-script");
    }

    @Test
    public void invalidSourcePreservesExistingRows() {
        Assert.assertThrows(PersistenceException.class, () -> invalidSource.getSchemaManager().populate());
        assertRows(invalidSource, 1);
        assertValue(invalidSource, SENTINEL_ID, "sentinel");
    }

    private static synchronized String scriptUrl() {
        if (scriptFile == null) {
            try (InputStream input = TestSchemaManagerPopulate.class.getClassLoader().getResourceAsStream(RESOURCE)) {
                Assert.assertNotNull("Packaged populate script must exist", input);
                scriptFile = Files.createTempFile("schema-populate-jse-", ".sql");
                Files.write(scriptFile, input.readAllBytes());
            } catch (IOException failure) {
                throw new IllegalStateException("Cannot prepare populate URL fixture", failure);
            }
        }
        return scriptFile.toUri().toString();
    }

    private static String insert(long id, String value) {
        return "INSERT INTO SCHEMA_POPULATE_RECORD (RECORD_ID, RECORD_VALUE) VALUES (" + id + ", '" + value + "')\n";
    }

    private void removeImportedRow() {
        executeDelete("DELETE FROM PopulateRecord r WHERE r.id = " + SCRIPT_ID);
        assertRows(resource, 1);
    }

    private void removeRows() {
        executeDelete("DELETE FROM PopulateRecord");
    }

    private void executeDelete(String query) {
        try (EntityManager em = resource.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createQuery(query).executeUpdate();
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    private static void assertImported(EntityManagerFactory factory, long id, String value) {
        assertRows(factory, 2);
        assertValue(factory, SENTINEL_ID, "sentinel");
        assertValue(factory, id, value);
        try (EntityManager em = factory.createEntityManager()) {
            em.clear();
            PopulateRecord record = em.find(PopulateRecord.class, id);
            Assert.assertNotNull("Imported row must be found in a fresh context", record);
            Assert.assertEquals(value, record.value);
        }
    }

    private static void assertRows(EntityManagerFactory factory, long expected) {
        try (EntityManager em = factory.createEntityManager()) {
            Assert.assertEquals(Long.valueOf(expected),
                    em.createQuery("SELECT COUNT(r) FROM PopulateRecord r", Long.class).getSingleResult());
        }
    }

    private static void assertValue(EntityManagerFactory factory, long id, String expected) {
        try (EntityManager em = factory.createEntityManager()) {
            Assert.assertEquals(expected, em.createQuery(
                    "SELECT r.value FROM PopulateRecord r WHERE r.id = :id", String.class)
                    .setParameter("id", id).getSingleResult());
        }
    }
}
