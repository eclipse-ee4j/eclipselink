/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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

import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemon.TEAMS;
import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemon.TRAINERS;
import static org.eclipse.persistence.testing.tests.jpa.persistence32.AbstractPokemon.TYPES;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link SchemaManager#create(boolean)} method on database with no schema.
 */
public class SchemaManagerCreateTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerCreateTest",
                new SchemaManagerCreateTest("testCreate")
        );
    }
    public SchemaManagerCreateTest() {
    }

    public SchemaManagerCreateTest(String name) {
        super(name);
    }

    // Test SchemaManager create method
    public void testCreate() {
        // Tables are always dropped in setUp() method
        // Create the schema
        SchemaManager schemaManager = emf.getSchemaManager();
        schemaManager.create(true);
        // Try to store data into the schema
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
    }



}
