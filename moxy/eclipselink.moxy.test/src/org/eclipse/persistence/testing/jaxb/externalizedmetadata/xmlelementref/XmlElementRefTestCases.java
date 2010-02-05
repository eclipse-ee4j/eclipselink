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
 * dmccann - December 03/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlElementRef via eclipselink-oxm.xml
 *
 */
public class XmlElementRefTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementRefTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlElementRef schema generation via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlElementRefSchemaGen() {
        MySchemaOutputResolver outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementRef schema generation via eclipselink-oxm.xml.  Here, an
     * xml-element-wrapper is also used.
     * 
     * Positive test.
     */
    public void testXmlElementRefWithWrapperSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm-wrapper.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Foos.class, Bar.class }, CONTEXT_PATH, metadataFile, 1);
        
        // validate schema
        String controlSchema = PATH + "schema_wrapper.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementRef via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlElementRef() {
        // load XML metadata
    	
    	Class[] classesToProcess = new Class[] { Foo.class };
        MySchemaOutputResolver outputResolver = generateSchema(classesToProcess, CONTEXT_PATH , PATH, 1);
    	        
        // load instance doc
        String src = PATH + "foo.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Object obj = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
        Marshaller marshaller = jaxbContext.createMarshaller();
        try {
            marshaller.marshal(obj, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests @XmlElementRef via eclipselink-oxm.xml.  Here an xml-element-wrapper
     * is also used.
     * 
     * Positive test.
     */
    public void testXmlElementRefWithWrapper() {
        String metadataFile = PATH + "eclipselink-oxm-wrapper.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Foos.class, Bar.class }, CONTEXT_PATH, metadataFile, 1);
        
        // load instance doc
        String src = PATH + "foo-wrapper.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Object obj = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
        Marshaller marshaller = jaxbContext.createMarshaller();
        try {
            marshaller.marshal(obj, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
}
