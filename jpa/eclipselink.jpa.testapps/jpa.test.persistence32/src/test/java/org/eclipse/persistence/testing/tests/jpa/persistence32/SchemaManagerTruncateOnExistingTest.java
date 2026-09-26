/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2023, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import jakarta.persistence.SchemaManager;
import junit.framework.Test;

import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemonSuite.TEAMS;
import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemonSuite.TRAINERS;
import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemonSuite.TYPES;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link SchemaManager#truncate()} method on database with already existing schema and data.
 */
public class SchemaManagerTruncateOnExistingTest extends AbstractSchemaManager {

    // Common prefix of all the tables of the persistence unit under test
    private static final String TABLE_NAME_PREFIX = "PERSISTENCE32_";

    public static Test suite() {
        return suite(
                "SchemaManagerTruncateOnExistingTest",
                new SchemaManagerTruncateOnExistingTest("testTruncateOnExistingSchema")
        );
    }
    public SchemaManagerTruncateOnExistingTest() {
    }

    public SchemaManagerTruncateOnExistingTest(String name) {
        super(name);
    }

    // Test SchemaManager truncate method
    public void testTruncateOnExistingSchema() {
        // Tables are always dropped in setUp() method
        // Make sure that tables exist and contain data
        createTables();
        emf.runInTransaction(em -> {
            for (int i = 1; i < TEAMS.length; i++) {
                em.persist(TEAMS[i]);
            }
            for (int i = 1; i < TRAINERS.length; i++) {
                em.persist(TRAINERS[i]);
            }
            for (int i = 1; i < TYPES.length; i++) {
                em.persist(TYPES[i]);
            }
        });
        // Foreign key constraints of the schema before the truncation
        Set<String> foreignKeysBeforeTruncate = foreignKeys();
        // Truncate the schema
        SchemaManager schemaManager = emf.getSchemaManager();
        schemaManager.truncate();
        // Verify that tables still exist but are empty
        // Team is referenced by a foreign key from Trainer, so truncating it requires
        // the constraint to be dropped first on databases that enforce this
        assertEquals("Team table shall be empty after truncate()", 0L, count("Team"));
        assertEquals("Trainer table shall be empty after truncate()", 0L, count("Trainer"));
        assertEquals("Type table shall be empty after truncate()", 0L, count("Type"));
        // Verify that the schema was left as it was found: constraints dropped to allow
        // the truncation must be restored
        assertEquals("Foreign key constraints shall not be modified by truncate()",
                     foreignKeysBeforeTruncate,
                     foreignKeys());
    }

    // Count of the entity instances stored in the database
    private long count(String entityName) {
        return emf.callInTransaction(
                em -> em.createQuery(String.format("SELECT count(e) FROM %s e", entityName), Long.class)
                        .getSingleResult());
    }

    // Foreign key constraints of the tables of the persistence unit under test as
    // "FK_TABLE.FK_COLUMN -> PK_TABLE.PK_COLUMN" values. Generated constraint names are
    // not part of the values, only the relationships that the constraints define.
    private Set<String> foreignKeys() {
        return emf.callInTransaction(em -> {
            Connection connection = em.unwrap(Connection.class);
            assertNotNull("Could not access the database connection", connection);
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                String catalog = connection.getCatalog();
                Set<String> foreignKeys = new TreeSet<>();
                for (String table : tables(metaData, catalog)) {
                    try (ResultSet keys = metaData.getImportedKeys(catalog, null, table)) {
                        while (keys.next()) {
                            foreignKeys.add(String.format("%s.%s -> %s.%s",
                                                          keys.getString("FKTABLE_NAME"),
                                                          keys.getString("FKCOLUMN_NAME"),
                                                          keys.getString("PKTABLE_NAME"),
                                                          keys.getString("PKCOLUMN_NAME")));
                        }
                    }
                }
                return foreignKeys;
            } catch (SQLException e) {
                throw new RuntimeException("Could not read the foreign key constraints", e);
            }
        });
    }

    // Names of the tables of the persistence unit under test. Names are read from the database
    // metadata to match the identifier case used by the database.
    private static Set<String> tables(DatabaseMetaData metaData, String catalog) throws SQLException {
        Set<String> tables = new TreeSet<>();
        try (ResultSet result = metaData.getTables(catalog, null, "%", new String[] {"TABLE"})) {
            while (result.next()) {
                String table = result.getString("TABLE_NAME");
                if (table != null && table.toUpperCase(Locale.ROOT).startsWith(TABLE_NAME_PREFIX)) {
                    tables.add(table);
                }
            }
        }
        return tables;
    }

}
