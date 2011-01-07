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
 * dmccann - October 15/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
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
 * Tests XmlList via eclipselink-oxm.xml
 *
 */
public class XmlListTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmllist/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlListTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlList via eclipselink-oxm.xml.  This tests validates
     * schema generation when an @XmlList is present in code/xml.   
     * 
     * Positive test.
     */
    public void doSchemaGeneration() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        shouldGenerateSchema = false;
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlList via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlList() {
        if (shouldGenerateSchema) {
            doSchemaGeneration();
        }
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests exception handling for @XmlList via eclipselink-oxm.xml.
     * This test should cause an exception as xml-list can only be used
     * with a collection or array, and in this case it is being set on
     * a String property.
     * 
     * Negative test.
     */
    public void testXmlListInvalid() {
        String metadataFile = PATH + "eclipselink-oxm-invalid.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        boolean exception = false;

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver msor = new MySchemaOutputResolver();
        try {
            JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
            jaxbContext.generateSchema(msor);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue("The expected exception was not thrown", exception);
    }

    /**
     * Tests @XmlList via eclipselink-oxm.xml.  Here the @XmlList annotation
     * set on stringData is overridden to false.
     * 
     * Positive test.
     */
    public void testXmlListNoStringData() {
        String metadataFile = PATH + "eclipselink-oxm-no-stringdata.xml";
        MySchemaOutputResolver msor = generateSchemaWithFileName(CONTEXT_PATH, metadataFile, 1);

        // validate schema
        String controlSchema = PATH + "schema-no-stringdata.xsd";
        compareSchemas(msor.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlList via eclipselink-oxm.xml.  A marshal operation is 
     * performed to ensure the list is processed correctly.
     * 
     * Positive test.
     */
    public void testXmlListMarshal() {
        if (shouldGenerateSchema) {
            doSchemaGeneration();
        }
        
        Employee emp = new Employee();
        java.util.List<String> data = new ArrayList<String>();
        data.add("xxx");
        data.add("yyy");
        data.add("zzz");
        emp.data = data;
        
        Document testDoc = parser.newDocument();
        
        Marshaller marshaller = getJAXBContext().createMarshaller();
        try {
            marshaller.marshal(emp, testDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee.");
        }

        String src = PATH + "employee.xml";
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
    }

    /**
     * Tests @XmlList via eclipselink-oxm.xml.  An unmarshal operation 
     * is performed to ensure the list is processed correctly.
     * 
     * Positive test.
     */
    public void testXmlListUnmarshal() {
        if (shouldGenerateSchema) {
            doSchemaGeneration();
        }
        
        Employee emp = null;
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        Employee ctrlEmp = new Employee();
        java.util.List<String> data = new ArrayList<String>();
        data.add("xxx");
        data.add("yyy");
        data.add("zzz");
        ctrlEmp.data = data;
        
        assertTrue("The unmarshalled Employee did not match the control Employee object.", ctrlEmp.equals(emp));
    }
    
    /**
     * Tests xml-list use with xml-attribute via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlListOnXmlAttribute() {
        String metadataFile = PATH + "eclipselink-oxm-data-attribute.xml";

        MySchemaOutputResolver msor = generateSchemaWithFileName(CONTEXT_PATH, metadataFile, 1);

        /*
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        */
        
        // validate schema
        String controlSchema = PATH + "schema-data-attribute.xsd";
        compareSchemas(msor.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        String src = PATH + "employee-data-attribute.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, msor);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
}
