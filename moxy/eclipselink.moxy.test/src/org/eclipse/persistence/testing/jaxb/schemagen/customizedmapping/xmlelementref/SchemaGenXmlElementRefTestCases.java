/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - May 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

import junit.framework.TestCase;

/**
 * Tests @XmlElementRef annotation processing.
 *
 */
public class SchemaGenXmlElementRefTestCases extends SchemaGenTestCases {
    MySchemaOutputResolver outputResolver;
    boolean shouldGenerateSchema = true;
    static String PATH = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlelementref/";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public SchemaGenXmlElementRefTestCases(String name) throws Exception {
        super(name);
    }

    /**
     * Generate the schema for these tests once only.  If generation fails, it will do so
     * for each test (meaning all tests will result in a generation failure).  If generation
     * is successful it is not performed again.
     */
    private void generateSchema() {
        if (shouldGenerateSchema) {
            outputResolver = new MySchemaOutputResolver();
            try {
                Class[] classes = new Class[]{ Employee.class, Address.class, Thing.class };
                JAXBContext context = (org.eclipse.persistence.jaxb.JAXBContext) org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(classes, null);
                context.generateSchema(outputResolver);
            } catch (Exception ex) {
                fail("Schema generation failed unexpectedly: " + ex.toString());
            }
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
            assertTrue("More than one shcema was generated unxepectedly", outputResolver.schemaFiles.size() == 1);
            shouldGenerateSchema = false;
        }
    }

    /**
     * ElementRef on collection results in minOccurs=0, maxOccurs=unbounded
     *
     * Zero or more 'address' elements are allowed.
     */
    public void testElementRefSingleAddress() {
        generateSchema();
        String result = validateAgainstSchema(PATH + "emp0.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * ElementRef on collection results in minOccurs=0, maxOccurs=unbounded
     *
     * Zero or more 'address' elements are allowed.
     */
    public void testElementRefMultipleAddresses() {
        generateSchema();
        String result = validateAgainstSchema(PATH + "emp1.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * ElementRef on collection results in minOccurs=0, maxOccurs=unbounded
     *
     * Zero or more 'address' elements are allowed.
     */
    public void testElementRefNoAddresses() {
        generateSchema();
        String result = validateAgainstSchema(PATH + "emp2.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Exception case:  ElementRef on single property results in minOccurs=1, maxOccurs=1
     *
     * One 'thing' is required.
     */
    public void testElementRefRequired() {
        generateSchema();
        String result = validateAgainstSchema(PATH + "emp3.xml", outputResolver);
        assertTrue("Schema validation passed unexpectedly", result != null);
    }
}
