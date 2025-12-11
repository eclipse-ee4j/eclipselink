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
import java.util.List;
import java.util.Map;

import jakarta.persistence.SchemaManager;
import jakarta.persistence.SchemaValidationException;
import junit.framework.Test;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.TableValidationException;

import static org.eclipse.persistence.tools.schemaframework.TableValidationException.DifferentColumns.Difference;
import static org.eclipse.persistence.tools.schemaframework.TableValidationException.DifferentColumns.Type.TYPE_DIFFERENCE;
import static org.eclipse.persistence.tools.schemaframework.TableValidationException.DifferentColumns.TypeDifference;
import static org.eclipse.persistence.tools.schemaframework.TableValidationException.DifferentColumns;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link jakarta.persistence.SchemaManager#validate()} method on database with existing but modified schema.
 */
public class SchemaManagerValidateOnModifiedColumnTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerValidateOnModifiedColumnTest",
                new SchemaManagerValidateOnModifiedColumnTest("testValidateOnModifiedSchema")
        );
    }
    public SchemaManagerValidateOnModifiedColumnTest() {
    }

    public SchemaManagerValidateOnModifiedColumnTest(String name) {
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
        
        // Modify "NAME" field in "PERSISTENCE32_TRAINER"
        TableDefinition trainer = tableDefinitions.get("PERSISTENCE32_TRAINER");
        FieldDefinition nameField = trainer.getField("NAME");
        trainer.dropFieldOnDatabase(emf.getDatabaseSession(), "NAME");
        FieldDefinition newNameField = new FieldDefinition();
        newNameField.setName("NAME");
        
        // Different type
        newNameField.setTypeName("NUMERIC");
        
        // Different type size
        newNameField.setSize(nameField.getSize()+5);
        
        // Different nullable
        newNameField.setShouldAllowNull(nameField.shouldAllowNull());
        newNameField.setIsPrimaryKey(nameField.isPrimaryKey());
        newNameField.setUnique(nameField.isUnique());
        newNameField.setIsIdentity(nameField.isIdentity());
        trainer.addFieldOnDatabase(emf.getDatabaseSession(), newNameField);
        
        if (emf.getDatabaseSession().getPlatform().isDB2()) {
            // After table modifications, DB2 needs a kind of secondary commit called a 'REORG'
            // to make the table available again.
            emf.getDatabaseSession()
               .priviledgedExecuteNonSelectingCall(
                   new SQLCall("CALL SYSPROC.ADMIN_CMD('REORG TABLE PERSISTENCE32_TRAINER')"));
        }
        
        // Do the validation
        SchemaManager schemaManager = emf.getSchemaManager();
        
        try {
            // Test validation
            schemaManager.validate();
            fail("Schema validation shall throw an exception on modified column");
        } catch (SchemaValidationException sve) {
            // Validation is expected to fail and return all missing columns
            Exception[] exceptions = sve.getFailures();
            for (TableValidationException exception : (TableValidationException[]) exceptions) {
                if (!(exception instanceof DifferentColumns)) {
                    exception.printStackTrace();
                    fail("Exception is not an instance of TableValidationException.DifferentColumns");
                }
                
                List<Difference> differences = exception.unwrap(DifferentColumns.class).getDifferences();
                assertEquals(1, differences.size());
                
                for (Difference difference : differences) {
                    assertEquals("PERSISTENCE32_TRAINER", exception.getTable());
                    assertEquals(TYPE_DIFFERENCE, difference.getType());
                    TypeDifference typeDifference = difference.unwrap(TypeDifference.class);
                    
                    // Type names are database specific so let's just check that they are not the same
                    assertNotSame(typeDifference.getModelValue(), typeDifference.getDbValue());
                }
            }
        }
    }

}
