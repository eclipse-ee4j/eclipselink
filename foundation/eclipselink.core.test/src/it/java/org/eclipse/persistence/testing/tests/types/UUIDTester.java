/*
 * Copyright (c) 2022, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.types;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.util.UUID;
import java.util.Vector;

/**
 *  Tests java.util.UUID access to the database.
 */
public class UUIDTester extends TypeTester {

    private static final String UUID_TEST_VALUE = "4ae8976b-635f-46dc-937a-6016db49bd96";

    public UUID uuidValue;

    public UUIDTester() {
        this(UUID.fromString(UUID_TEST_VALUE));
    }

    public UUIDTester(UUID testValue) {
        super("uuidTest");
        uuidValue = testValue;
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(UUIDTester.class);
        descriptor.setTableName("UUIDS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("uuidValue", "UUIDV");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(UUIDTester.class);
        descriptor.setTableName("UUIDS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        try {
            descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
            descriptor.addDirectMapping("uuidValue", "getUuidValue", "setUuidValue", "UUIDV");
        } catch (DescriptorException exception) {
        }
        return descriptor;
    }

    public UUID getUuidValue() {
        return uuidValue;
    }

    public void setUuidValue(UUID uuidValue) {
        this.uuidValue = uuidValue;
    }

    /**
     *Return a platform independent definition of the database table.
     */
    public static TableDefinition tableDefinition(Session session) {
        TableDefinition definition = TypeTester.tableDefinition();
        FieldDefinition fieldDef;

        definition.setName("UUIDS");
        fieldDef = new FieldDefinition("UUIDV", UUID.class);
        fieldDef.setShouldAllowNull(false);
        definition.addField(fieldDef);

        return definition;
    }

    public static Vector testInstances() {
        Vector tests = new Vector(1);

        tests.add(new UUIDTester(UUID.fromString(UUID_TEST_VALUE)));
        return tests;
    }

    public String toString() {
        return "UUIDTester(" + getUuidValue() + ")";
    }
}
