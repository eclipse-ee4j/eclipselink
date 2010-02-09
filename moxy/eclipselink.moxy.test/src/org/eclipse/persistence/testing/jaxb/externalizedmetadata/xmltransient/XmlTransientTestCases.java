/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient;

import java.io.File;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance.Person;

/**
 * Tests XmlTransient via eclipselink-oxm.xml
 * 
 */
public class XmlTransientTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver;
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlTransientTestCases(String name) {
        super(name);
    }

    /**
     * Test marking the Address class as transient. Validation for employee.xml should succeed.
     * 
     * Positive test.
     */
    public void testXmlTransientOnClassValid() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String result = validateAgainstSchema(PATH + "employee.xml", EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Test marking the Address class as transient. Validation for address.xml should fail as
     * Address is marked transient.
     * 
     * Negative test.
     */
    public void testXmlTransientOnClassInvalid() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String result = validateAgainstSchema(PATH + "address.xml", null, outputResolver);
        assertTrue("Schema validation succeeded unxepectedly", result != null);
    }

    /**
     * Test marking the myInt property on Employee as transient. Validation for
     * employee-invalidproperty.xml should fail.
     * 
     * Negative test.
     */
    public void testXmlTransientOnProperty() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String result = validateAgainstSchema(PATH + "employee-invalidproperty.xml", null, outputResolver);
        assertTrue("Schema validation succeeded unxepectedly", result != null);
    }

    /**
     * Test marking the lastName field on Employee as transient. Validation for
     * employee-invalidfield.xml should fail.
     * 
     * Negative test.
     */
    public void testXmlTransientOnField() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String result = validateAgainstSchema(PATH + "employee-invalidfield.xml", null, outputResolver);
        assertTrue("Schema validation succeeded unxepectedly", result != null);
    }

    /**
     * Test marking the Employee class as transient, but overriding it in XML.
     * 
     * Positive test.
     */
    public void testUnsetXmlTransientOnClass() {
        String contextPath = CONTEXT_PATH + ".unset.classlevel";
        String path = PATH + "unset/classlevel/";
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.classlevel.Employee.class }, contextPath, path, 1);
        String result = validateAgainstSchema(PATH + "employee.xml", EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Test schema generation when a transient superclass is marked not transient via
     * eclipselink-oxm.xml
     * 
     * Positive test.
     */
    public void testXmlTransientWithInheritance() {
        String contextPath = CONTEXT_PATH + ".inheritance";
        String path = PATH + "inheritance/";
        outputResolver = generateSchema(new Class[] { Person.class, org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance.Employee.class }, contextPath, path, 1);

        // validate schema against control schema
        compareSchemas(new File(path + "schema.xsd"), outputResolver.schemaFiles.get(EMPTY_NAMESPACE));
    }
}
