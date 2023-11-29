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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.SchemaManager;
import jakarta.persistence.SchemaValidationException;
import junit.framework.Test;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.TableValidationException;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link jakarta.persistence.SchemaManager#validate()} method on database with existing but modified schema.
 */
public class SchemaManagerValidateOnSurplusColumnTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerValidateOnSurplusColumnTest",
                new SchemaManagerValidateOnSurplusColumnTest("testValidateOnModifiedSchema")
        );
    }
    public SchemaManagerValidateOnSurplusColumnTest() {
    }

    public SchemaManagerValidateOnSurplusColumnTest(String name) {
        super(name);
    }

    // Test SchemaManager validate method on existing valid schema
    public void testValidateOnModifiedSchema() {
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
        // Extend PERSISTENCE32_TRAINER with age field
        TableDefinition trainer = tableDefinitions.get("PERSISTENCE32_TRAINER");
        FieldDefinition ageField = new FieldDefinition();
        ageField.setName("age");
        ageField.setTypeName("NUMERIC");
        ageField.setSize(15);
        ageField.setShouldAllowNull(true);
        ageField.setIsPrimaryKey(false);
        ageField.setUnique(false);
        ageField.setIsIdentity(false);
        trainer.addFieldOnDatabase(emf.getDatabaseSession(), ageField);
        // Do the validation
        SchemaManager schemaManager = emf.getSchemaManager();
        try {
            // Test validation
            schemaManager.validate();
        } catch (SchemaValidationException sve) {
            // Validation is expected to fail and return all missing columns
            Exception[] exceptions = sve.getFailures();
            Map<String, Set<String>> surplusColumns = Map.of("PERSISTENCE32_TRAINER", Set.of("AGE"));
            for (TableValidationException exception : (TableValidationException[]) exceptions) {
                if (!(exception instanceof TableValidationException.SurplusColumns)) {
                    fail("Exception is not an instance of TableValidationException.SurplusColumns");
                }
                if (exception.getFailure() == TableValidationException.ValidationFailure.SURPLUS_COLUMNS) {
                    assertEquals("PERSISTENCE32_TRAINER", exception.getTable());
                } else {
                    fail("Exception type is not SURPLUS_COLUMNS");
                }
                checkSurplusColumns(surplusColumns,
                                    exception.getTable(),
                                    exception.unwrap(TableValidationException.SurplusColumns.class)
                                            .getColumns()
                                            .stream()
                                            .map(String::toUpperCase)
                                            .toList());
            }
        }
    }

    private void checkSurplusColumns(Map<String, Set<String>> surplusColumns, String table, List<String> columns) {
        Set<String> columnsToCheck = new HashSet<>(surplusColumns.get(table));
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
