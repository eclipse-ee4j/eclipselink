/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.schemagen.classarray;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml.Employee;

/**
 * Tests schema generation from a Class[].
 */
public class ClassArraySchemaGenTestCases extends SchemaGenTestCases {
    MySchemaOutputResolver outputResolver;
    boolean shouldGenerateSchema;
    static String PATH = "org/eclipse/persistence/testing/jaxb/schemagen/deploymentxml/";
    protected DocumentBuilder parser;

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public ClassArraySchemaGenTestCases(String name) throws Exception {
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
                fail("Additional global element Map setup failed: " + x.toString());
            }
            try {
                Class[] classesToBeBound = new Class[]{Employee.class};
                generateSchema(classesToBeBound, outputResolver, additionalGlobalElements);
            } catch (Exception ex) {
                fail("Schema generation failed unexpectedly: " + ex.toString());
            }
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
            assertTrue("Expected two schemas to be generated, but there were [ " + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == 2);
            shouldGenerateSchema = false;
        }
    }

    /**
     * Tests basic schema generation from deployment xml.
     */
    public void testSchemaGenFromClassArray() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "Employee.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation (Employee).
     *
     * @throws Exception
     */
    public void testSchemaGenFromClassArrayWithElements() throws Exception {
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
     * @throws Exception
     */
    public void testSchemaGenFromClassArrayWithStringElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "ASingleString.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests user-set additional global element generation (int)
     *
     * @throws Exception
     */
    public void testSchemaGenFromClassArrayWithIntElement() throws Exception {
        generateSchema();
        String result = validateAgainstSchema(PATH + "ASingleInt.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Test schema generation from multiple entities with different namespaces and verify generated schemas (by order and content).
     */
    public void testSchemaGenFromClassArrayVerifyOrder() throws Exception {
        final Class[] inputClasses = new Class[]{SecondType.class, WithoutNamespaceType.class, RootElement.class, FourthType.class, ThirdType.class, FirstType.class};
        final String[] xsdResources = new String[]{"schema1.xsd", "schema2.xsd", "schema3.xsd", "schema4.xsd", "schema5.xsd"};

        JAXBContext jaxbContext;
        MySchemaOutputResolver schemaOrderoutputResolver = new MySchemaOutputResolver();

        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(inputClasses, null);
            jaxbContext.generateSchema(schemaOrderoutputResolver);

        } catch (Exception ex) {
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", schemaOrderoutputResolver.schemaFiles.size() > 0);
        assertTrue("Expected five schemas to be generated, but there were [ " + schemaOrderoutputResolver.schemaFiles.size() + "]", schemaOrderoutputResolver.schemaFiles.size() == 5);
        List<File> generatedSchemas = schemaOrderoutputResolver.schemaFiles;
        List<File> controlSchemas = new ArrayList<>(xsdResources.length);
        for (String controlSchema : xsdResources) {
            URI controlSchemaURI = Thread.currentThread().getContextClassLoader().getResource(PATH + controlSchema).toURI();
            controlSchemas.add(new File(controlSchemaURI));
        }
        for (int i = 0; i < controlSchemas.size(); i++) {
            File nextControlValue = controlSchemas.get(i);
            File nextGeneratedValue = generatedSchemas.get(i);
            ExternalizedMetadataTestCases.compareSchemas(nextGeneratedValue, nextControlValue);
        }
    }
}
