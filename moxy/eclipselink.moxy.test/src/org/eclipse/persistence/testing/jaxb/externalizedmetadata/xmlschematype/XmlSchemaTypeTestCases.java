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
 * dmccann - December 08/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
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
     * 
     * @param name
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
    public void testXmlSchemaTypePkgSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm-package.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlSchemaType schema generation via eclipselink-oxm.xml.  Here, a 
     * property-level xml-schema-type declaration exists.  It's value "date" 
     * should override the one set in code ("year").
     * 
     * Positive test.
     */
    public void testXmlSchemaTypePropSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm-property.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
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
    public void testXmlSchemaTypeOverrideSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm-override.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
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
    public void testXmlSchemaTypeClassOverridesPackageSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm-class-overrides-package.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { EmployeeWithAnnotation.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "schema1.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlSchemaType via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlSchemaType() {
        // load XML metadata
        String metadataFile = PATH + "eclipselink-oxm-property.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        // create context
        JAXBContext jCtx = null;
        try {
            jCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Employee.class }, properties);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed.");
        }
        
        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Object obj = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            obj = unmarshaller.unmarshal(iDocStream);
            assertFalse("Unmarshalled object is null.", obj == null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }

        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        // marshal
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(obj, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
}