/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.SchemaManager;
import jakarta.persistence.SchemaValidationException;
import junit.framework.Test;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.TableValidationException;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link jakarta.persistence.SchemaManager#validate()} method on database with existing but modified schema.
 */
public class SchemaManagerValidateOnMissingColumnTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerValidateOnMissingColumnTest",
                new SchemaManagerValidateOnMissingColumnTest("testValidateOnMissingColumn")
        );
    }
    public SchemaManagerValidateOnMissingColumnTest() {
    }

    public SchemaManagerValidateOnMissingColumnTest(String name) {
        super(name);
    }

    // Test SchemaManager#validate on a schema with a missing column
    public void testValidateOnMissingColumn() {
        // Make sure that tables exist
        createTables();
        
        // Modify current schema
        TableCreator tableCreator = getDefaultTableCreator();
        Map<String, TableDefinition> tableDefinitions = new HashMap<>(tableCreator.getTableDefinitions().size());
        for (TableDefinition tableDefinition : tableCreator.getTableDefinitions()) {
            String tableName = tableDefinition.getTable() == null
                    ? tableDefinition.getName()
                    : tableDefinition.getTable().getName();
            tableDefinitions.put(tableName, tableDefinition);
        }
        
        // Remove "NAME" field from "PERSISTENCE32_TEAM"
        tableDefinitions.get("PERSISTENCE32_TEAM")
                        .dropFieldOnDatabase(
                            emf.getDatabaseSession(), 
                            "NAME");
        
        if (emf.getDatabaseSession().getPlatform().isDB2()) {
            // After table modifications, DB2 needs a kind of secondary commit called a 'REORG'
            // to make the table available again.
            emf.getDatabaseSession()
               .priviledgedExecuteNonSelectingCall(
                   new SQLCall("CALL SYSPROC.ADMIN_CMD('REORG TABLE PERSISTENCE32_TEAM')"));
        }
        
        // Do the validation
        SchemaManager schemaManager = emf.getSchemaManager();
        try {
            // Test validation
            schemaManager.validate();
            fail("Schema validation shall throw an exception on missing column");
        } catch (SchemaValidationException sve) {
            // Validation is expected to fail and return all missing columns
            Exception[] exceptions = sve.getFailures();
            Map<String, Set<String>> missingColumns = Map.of("PERSISTENCE32_TEAM", Set.of("NAME"));
            for (TableValidationException exception : (TableValidationException[]) exceptions) {
                if (!(exception instanceof TableValidationException.MissingColumns)) {
                    fail("Exception is not an instance of TableValidationException.MissingColumns");
                }
                
                if (exception.getFailure() == TableValidationException.ValidationFailure.MISSING_COLUMNS) {
                    assertEquals("PERSISTENCE32_TEAM", exception.getTable());

                } else {
                    fail("Exception type is not MISSING_COLUMNS");
                }
                
                checkMissingColumns(missingColumns,
                                    exception.getTable(),
                                    exception.unwrap(TableValidationException.MissingColumns.class)
                                            .getColumns()
                                            .stream()
                                            .map(String::toUpperCase)
                                            .toList());
            }
        }
    }

    private void checkMissingColumns(Map<String, Set<String>> missingColumns, String table, List<String> columns) {
        Set<String> columnsToCheck = new HashSet<>(missingColumns.get(table));
        Set<String> checked = new HashSet<>(columnsToCheck.size());
        if (columnsToCheck.isEmpty()) {
            fail(String.format("Table %s is not expected to have missing columns", table));
        }
        for (String column : columns) {
            if (columnsToCheck.contains(column)) {
                columnsToCheck.remove(column);
                checked.add(column);
            } else {
                if (checked.contains(column)) {
                    fail(String.format("Duplicate missing %s column entry for table %s", column, table));
                } else {
                    fail(String.format("Missing column %s was not reported for table %s",  column, table));
                }
            }
        }
    }

}
