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
import jakarta.persistence.SchemaValidationException;
import junit.framework.Test;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link SchemaManager}.
 * Test {@link SchemaManager#validate()} method on database with existing and valid schema.
 */
public class SchemaManagerValidateOnValidSchemaTest extends AbstractSchemaManager {

    public static Test suite() {
        return suite(
                "SchemaManagerValidateOnValidSchemaTest",
                new SchemaManagerValidateOnValidSchemaTest("testValidateOnValidSchema")
        );
    }
    public SchemaManagerValidateOnValidSchemaTest() {
    }

    public SchemaManagerValidateOnValidSchemaTest(String name) {
        super(name);
    }

    // Test SchemaManager validate method on existing valid schema
    public void testValidateOnValidSchema() {
        // Make sure that tables exist
        createTables();
        SchemaManager schemaManager = emf.getSchemaManager();
        try {
            // Test validation
            schemaManager.validate();
        } catch (SchemaValidationException sve) {
            fail(sve.getLocalizedMessage());
        }
    }

}
