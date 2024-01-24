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
// dmccann - April 30/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

/**
 * Tests schema generation from deployment xml.
 *
 */
public class DeploymentXMLSchemaGenTestCases extends SchemaGenTestCases {
    MySchemaOutputResolver outputResolver;
    boolean shouldGenerateSchema;
    static String PATH="org/eclipse/persistence/testing/jaxb/schemagen/deploymentxml/";
    static String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml";

    /**
     * This is the preferred (and only) constructor.
     *
     */
    public DeploymentXMLSchemaGenTestCases(String name) throws Exception {
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
            try {
                additionalGlobalElements.put(new QName("ASingleString"), String.class);
                additionalGlobalElements.put(new QName("example.com", "ASingleEmployee"), Employee.class);
                additionalGlobalElements.put(new QName("ASingleInt"), int.class);
            } catch (Exception x) {
                fail("Additional global element Map setup failed: " + x);
            }
            try {
                generateSchema(CONTEXT_PATH, outputResolver, additionalGlobalElements);
            } catch (Exception ex) {
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
    public void testSchemaGenFromProjectXml() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "Employee.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation (Employee).
     *
     */
    public void testSchemaGenFromProjectXmlWithElements() throws Exception {
        generateSchema();
        String src = PATH + "ASingleEmployee.xml";
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
    public void testSchemaGenFromProjectXmlWithStringElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "ASingleString.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation (int)
     *
     */
    public void testSchemaGenFromProjectXmlWithIntElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "ASingleInt.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
}
