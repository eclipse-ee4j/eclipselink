/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - June 11/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.classarray;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref.Address;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref.Thing;
import org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml.Employee;

/**
 * Tests schema generation from a Class[].
 * 
 */
public class ClassArraySchemaGenTestCases extends SchemaGenTestCases {
    MySchemaOutputResolver outputResolver;
    boolean shouldGenerateSchema;
    static String PATH="org/eclipse/persistence/testing/jaxb/schemagen/deploymentxml/";

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
                Class[] classesToBeBound = new Class[] { Employee.class };
                generateSchema(classesToBeBound, outputResolver, additionalGlobalElements);
            } catch (Exception ex) {
                fail("Schema generation failed unexpectedly: " + ex.toString());
            }
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
            assertTrue("Expected two schemas to be generated, but there were [ " + outputResolver.schemaFiles.size()  + "]", outputResolver.schemaFiles.size() == 2);
            shouldGenerateSchema = false;
        }
    }

    /**
     * Tests basic schema generation from deployment xml.
     * 
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
}