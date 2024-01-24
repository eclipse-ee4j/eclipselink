/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 11/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.typearray;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml.Employee;

/**
 * Tests schema generation from a Type[].
 *
 */
public class TypeArraySchemaGenTestCases extends SchemaGenTestCases {
    MySchemaOutputResolver outputResolver;
    boolean shouldGenerateSchema;
    static String PATH="org/eclipse/persistence/testing/jaxb/schemagen/deploymentxml/";
    static String TYPEARRAY_PATH="org/eclipse/persistence/testing/jaxb/schemagen/typearray/";

    /**
     * This is the preferred (and only) constructor.
     *
     */
    public TypeArraySchemaGenTestCases(String name) throws Exception {
        super(name);
        shouldGenerateSchema = true;
    }

    /**
     * Generate the schema for these tests once only.  If generation fails, it will do so
     * for each test (meaning all tests will result in a generation failure).  If generation
     * is successful it is not performed again.
     */
    private void generateSchema() {
        if (shouldGenerateSchema) {
            outputResolver = new MySchemaOutputResolver();
            Map<QName, Type> additionalGlobalElements = new HashMap<QName, Type>();
            Type[] typesToBeBound = new Type[2];
            try {
                Field employeesField = EmployeeHolder.class.getDeclaredField(EmployeeHolder.EMPLOYEES_FIELD_NAME);
                typesToBeBound[0] = Employee[].class;
                typesToBeBound[1] = employeesField.getGenericType();

                additionalGlobalElements.put(new QName("ASingleString"), String.class);
                additionalGlobalElements.put(new QName("example.com", "ASingleEmployee"), Employee.class);
                additionalGlobalElements.put(new QName("ASingleInt"), int.class);
                additionalGlobalElements.put(new QName("example.com", "AListOfEmployees"), employeesField.getGenericType());
                additionalGlobalElements.put(new QName("AnEmployeeArray"), Employee[].class);
            } catch (Exception x) {
                fail("Type[] and additional global element Map setup failed: " + x);
            }
            try {
                generateSchema(typesToBeBound, outputResolver, additionalGlobalElements);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Schema generation failed unexpectedly: " + ex);
            }
            assertTrue("No schemas were generated", !outputResolver.schemaFiles.isEmpty());
            assertTrue("Expected two schemas to be generated, but there were [ " + outputResolver.schemaFiles.size()  + "]", outputResolver.schemaFiles.size() == 2);
            shouldGenerateSchema = false;
        }
    }

    /**
     * Tests basic schema generation from deployment xml.
     *
     */
    public void testSchemaGenFromTypesArray() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "Employee.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation (Employee).
     *
     */
    public void testSchemaGenFromTypesArrayWithElements() throws Exception {
        String src = PATH + "ASingleEmployee.xml";
        generateSchema();
        String result = validateAgainstSchema(src, 1, outputResolver);
        if (result != null) {
            // account for map ordering differences between VMs
            if (validateAgainstSchema(src, 0, outputResolver) == null) {
                // success
                return;
            }
            fail(result);
        }
    }

    /**
     * Tests user-set additional global element generation (String)
     *
     */
    public void testSchemaGenFromTypesArrayWithStringElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "ASingleString.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation (int)
     *
     */
    public void testSchemaGenFromTypesArrayWithIntElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "ASingleInt.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation ({@code List<Employee>})
     *
     */
    public void testSchemaGenFromTypesArrayWithParameterizedElement() throws Exception {
        generateSchema();
        String src = TYPEARRAY_PATH + "AListOfEmployees.xml";
        String result = validateAgainstSchema(src, 1, outputResolver);
        if (result != null) {
            // account for map ordering differences between VMs
            if (validateAgainstSchema(src, 0, outputResolver) == null) {
                // success
                return;
            }
            fail(result);
        }
    }

    /**
     * Tests user-set additional global element generation (Employee[])
     *
     */
    public void testSchemaGenFromTypesArrayWithArrayElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(TYPEARRAY_PATH + "AnEmployeeArray.xml", outputResolver);
        assertTrue("Schemas validation failed unxepectedly: " + result, result == null);
    }
}
