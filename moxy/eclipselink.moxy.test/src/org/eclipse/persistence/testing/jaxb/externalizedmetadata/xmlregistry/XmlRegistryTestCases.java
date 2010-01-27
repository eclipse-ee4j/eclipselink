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
 * dmccann - January 21/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
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
        String metadataFile = PATH + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { ObjectFactory1.class }, CONTEXT_PATH, iStream, 1);
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
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { FooBar.class, ObjectFactory2.class }, CONTEXT_PATH, iStream, 1);
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
        String metadataFile = PATH + "eclipselink-oxm.xml";
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
            jCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { ObjectFactory1.class }, properties);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed.");
        }
        
        // load instance doc
        String src = PATH + "foo.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Object obj = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
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
        Marshaller marshaller = jCtx.createMarshaller();
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
