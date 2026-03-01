/*
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
        // Truncate the schema
        SchemaManager schemaManager = emf.getSchemaManager();
        schemaManager.truncate();
        // Verify that tables still exist but are empty
        // - Team count shall be 0
        int teamCount = emf.callInTransaction(
                em -> em.createQuery("SELECT count(t) FROM Team t", Integer.class).getFirstResult());
        assertEquals(teamCount, 0);
        // - Trainer count shall be 0
        int trainerCount = emf.callInTransaction(
                em -> em.createQuery("SELECT count(t) FROM Trainer t", Integer.class).getFirstResult());
        assertEquals(trainerCount, 0);
        // - Type count shall be 0
        int typeCount = emf.callInTransaction(
                em -> em.createQuery("SELECT count(t) FROM Type t", Integer.class).getFirstResult());
        assertEquals(typeCount, 0);
    }

}
