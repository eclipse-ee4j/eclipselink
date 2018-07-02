/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - December 08/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.TimeZone;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlSchemaType via eclipselink-oxm.xml
 *
 */
public class XmlSchemaTypeTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschematype/";

    /**
     * This is the preferred (and only) constructor.
     */
    public XmlSchemaTypeTestCases(String name) {
        super(name);
    }

    /**
     * Tests @XmlSchemaType schema generation via eclipselink-oxm.xml.  Here, a
     * package-level xml-schema-type declaration exists.  It's value "date"
     * should override the one set in code ("year").
     *
     * Positive test.
     */
    public void testXmlSchemaTypePkgSchemaGen() throws URISyntaxException {
        String metadataFile = PATH + "eclipselink-oxm-package.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, metadataFile, 1);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlSchemaType schema generation via eclipselink-oxm.xml.  Here, a
     * property-level xml-schema-type declaration exists.  It's value "date"
     * should override the one set in code ("year").
     *
     * Positive test.
     */
    public void testXmlSchemaTypePropSchemaGen() throws URISyntaxException {
        String metadataFile = PATH + "eclipselink-oxm-property.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, metadataFile, 1);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlSchemaType schema generation via eclipselink-oxm.xml.  Here, both package-level
     * and property-level xml-schema-type declarations exist.  It is expected that the property-
     * level value "date" will override the package-level one ("time"), as well as the package-
     * level one set in code ("year").
     *
     * Positive test.
     */
    public void testXmlSchemaTypeOverrideSchemaGen() throws URISyntaxException {
        String metadataFile = PATH + "eclipselink-oxm-override.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, metadataFile, 1);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlSchemaType schema generation via eclipselink-oxm.xml.  Here, both package-level
     * and property-level @XmlSchemaType annotations exist in code.  In Xml, there is a package-
     * level declaration ("day"), but it is expected that the property-level value "date" in code will
     * override it.
     *
     * Positive test.
     */
    public void testXmlSchemaTypeClassOverridesPackageSchemaGen() throws URISyntaxException {
        String metadataFile = PATH + "eclipselink-oxm-class-overrides-package.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { EmployeeWithAnnotation.class }, CONTEXT_PATH, metadataFile, 1);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema1.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests xml-schema-type schema generation via eclipselink-oxm.xml when
     * combined with xml-id.
     *
     * Positive test.
     */
    public void testXmlSchemaTypeWithIDSchemaGen() throws URISyntaxException {
        String metadataFile = PATH + "eclipselink-oxm-method.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { EmployeeWithMethods.class }, CONTEXT_PATH, metadataFile, 1);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema2.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlSchemaType schema generation combined with @XmlID.
     *
     * Positive test.
     */
    public void testXmlSchemaTypeWithIDAnnotationSchemaGen() throws URISyntaxException {
        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { EmployeeWithAnnotationOnMethod.class }, 1);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema3.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    public void testXmlSchemaType() {
        // load XML metadata and create JAXBContext
        String metadataFile = PATH + "eclipselink-oxm-property.xml";
        generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, metadataFile, 1);

        // This test's instance document contains a date in EST time zone, so temporarily
        // reset the VM time zone
        TimeZone currentTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Object obj = null;
        try {
            Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
            obj = unmarshaller.unmarshal(iDocStream);
            assertFalse("Unmarshalled object is null.", obj == null);
        } catch (Exception e) {
            fail("Unmarshal operation failed. " + e);
        }

        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            fail("An unexpected exception occurred loading control document [" + src + "]. " + e);
        }

        // marshal
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(obj, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (Exception e) {
            fail("Marshal operation failed. " + e);
        }

        TimeZone.setDefault(currentTimeZone);
    }

}
