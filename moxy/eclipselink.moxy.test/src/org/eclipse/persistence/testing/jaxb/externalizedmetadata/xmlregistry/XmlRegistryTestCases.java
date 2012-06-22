/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 21/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlRegistry and XmlElementDecl via eclipselink-oxm.xml
 *
 */
public class XmlRegistryTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlregistry/";
    private static final String FOO = "This is some foo.";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlRegistryTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlRegistry schema generation via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlRegistrySchemaGen() {
        // load XML metadata
        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { ObjectFactory1.class }, CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlRegistry schema generation via eclipselink-oxm.xml.  Here a number of
     * element-decl declarations with non-local scopes exist.  
     * 
     * Positive test.
     */
    public void testXmlRegistrySchemaGenNonLocal() {
        // load XML metadata
        String metadataFile = PATH + "eclipselink-oxm-non-local.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { FooBar.class, ObjectFactory2.class }, CONTEXT_PATH, metadataFile, 1);
        // validate schema
        String controlSchema = PATH + "schema-non-local.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlRegistry via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlRegistryUnmarshalThenMarshal() {
        // load XML metadata
    	
    	Class[] classes = new Class[]{ObjectFactory1.class};
    	MySchemaOutputResolver outputResolver = generateSchema(classes, CONTEXT_PATH, PATH, 1);

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
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }

        assertNotNull("Unmarshalled object is null.", obj);
        assertTrue("Unmarshalled object is not an instance of JAXBElement", obj instanceof JAXBElement);
        JAXBElement jbElt = (JAXBElement) obj;
        assertTrue("JAXBElement does not wrap the string [" + FOO + "] as expected", jbElt.getValue().equals(FOO));
        
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
            ObjectFactory1 objFactory = new ObjectFactory1();
            JAXBElement<String> myJaxbElement = objFactory.createFoo(FOO);
            marshaller.marshal(myJaxbElement, testDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
        assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
    }
}
