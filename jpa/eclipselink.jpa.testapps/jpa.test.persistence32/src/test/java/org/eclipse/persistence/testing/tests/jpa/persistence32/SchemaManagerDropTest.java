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

import jakarta.persistence.PersistenceException;
import jakarta.persistence.SchemaManager;
import junit.framework.Test;
import org.eclipse.persistence.logging.SessionLog;

import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemonSuite.TEAMS;
import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemonSuite.TRAINERS;
import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemonSuite.TYPES;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link SchemaManager#drop(boolean)} method on database with already existing schema.
 */
public class SchemaManagerDropTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerDropTest",
                new SchemaManagerDropTest("testDrop")
        );
    }
    public SchemaManagerDropTest() {
    }

    public SchemaManagerDropTest(String name) {
        super(name);
    }

    // Test SchemaManager drop method
    public void testDrop() {
        // Make sure that tables exist before being dropped
        createTables();
        // ...and persist call works
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
        // Drop the schema
        SchemaManager schemaManager = emf.getSchemaManager();
        schemaManager.drop(true);
        // Turn off logging to suppress expected SQL errors warnings
        int logLevel = emf.getDatabaseSession().getSessionLog().getLevel();
        emf.getDatabaseSession().getSessionLog().setLevel(SessionLog.OFF);
        // Verify that any attempt to store data throws an exception because of missing tables
        // - Team entity
        try {
            emf.runInTransaction(em -> {
                for (int i = 1; i < TEAMS.length; i++) {
                    em.persist(TEAMS[i]);
                }
            });
            fail("Calling persist on entity after database schema was deleted shall throw an exception.");
        } catch (PersistenceException pe) {
            // Silently ignore PersistenceException
        }
        // - Trainer entity
        try {
            emf.runInTransaction(em -> {
                for (int i = 1; i < TRAINERS.length; i++) {
                    em.persist(TRAINERS[i]);
                }
            });
            fail("Calling persist on entity after database schema was deleted shall throw an exception.");
        } catch (PersistenceException pe) {
            // Silently ignore PersistenceException
        }
        // - Type entity
        try {
            emf.runInTransaction(em -> {
                for (int i = 1; i < TYPES.length; i++) {
                    em.persist(TYPES[i]);
                }
            });
            fail("Calling persist on entity after database schema was deleted shall throw an exception.");
        } catch (PersistenceException pe) {
            // Silently ignore PersistenceException
        }
        emf.getDatabaseSession().getSessionLog().setLevel(logLevel);
    }

}
