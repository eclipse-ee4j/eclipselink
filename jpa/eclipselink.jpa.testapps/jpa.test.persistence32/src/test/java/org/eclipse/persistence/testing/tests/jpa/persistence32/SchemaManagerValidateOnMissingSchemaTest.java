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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.SchemaManager;
import jakarta.persistence.SchemaValidationException;
import junit.framework.Test;
import org.eclipse.persistence.tools.schemaframework.TableValidationException;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link SchemaManager#validate()} method on database with missing schema.
 */
public class SchemaManagerValidateOnMissingSchemaTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerValidateOnMissingSchemaTest",
                new SchemaManagerValidateOnMissingSchemaTest("testValidateOnMissingSchema")
        );
    }
    public SchemaManagerValidateOnMissingSchemaTest() {
    }

    public SchemaManagerValidateOnMissingSchemaTest(String name) {
        super(name);
    }

    // Test SchemaManager validate method on missing schema
    public void testValidateOnMissingSchema() {
        // Tables are always dropped in setUp() method
        SchemaManager schemaManager = emf.getSchemaManager();
        try {
            // Test validation
            schemaManager.validate();
            fail("Schema validation shall throw an exception on missing schema");
        } catch (SchemaValidationException sve) {
            // Validation is expected to fail and return all tables as missing
            Exception[] exceptions = sve.getFailures();
            String[] missingTables = new String[] {
                    "PERSISTENCE32_TEAM",
                    "PERSISTENCE32_TRAINER",
                    "PERSISTENCE32_TYPE",
                    "PERSISTENCE32_POKEMON",
                    "PERSISTENCE32_POKEMON_TYPE",
                    "PERSISTENCE32_SYNTAX_ENTITY",
                    "PERSISTENCE32_VERSION_ENTITY",
                    "PERSISTENCE32_SE_COLTABLE"
            };
            Set<String> missingTablesSet = new HashSet<>(Arrays.asList(missingTables));
            Set<String> initialMissingTablesSet = Set.copyOf(missingTablesSet);
            Set<String> checked = new HashSet<>(initialMissingTablesSet.size());
            for (TableValidationException exception : (TableValidationException[]) exceptions) {
                if (!(exception instanceof TableValidationException.MissingTable)) {
                    fail("Exception is not an instance of TableValidationException.MissingTable");
                }
                if (exception.getFailure() != TableValidationException.ValidationFailure.MISSING_TABLE) {
                    fail("Exception type is not MISSING_TABLE");
                }
                checkMissingTable(initialMissingTablesSet, missingTablesSet, checked, exception.getTable());
            }
        }
    }

}
