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
 * dmccann - January 12/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
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
 * Tests XmlInlineBinaryData via eclipselink-oxm.xml
 *
 */
public class XmlInlineBinaryDataTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlinlinebinarydata/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlInlineBinaryDataTestCases(String name) {
        super(name);
    }

    /**
     * Tests @XmlInlineBinaryData schema generation via eclipselink-oxm.xml.
     * 
     * Positive test.
     *
    public void testXmlInlineBinaryDataSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { MyData.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }*/
    
    /**
     * Tests Class level XmlInlineBinaryData annotation unmarshal/marshal.
     * No XML metadata is used for this test.
     * 
     * Positive test.
     */
    public void testClassLevelXmlInlineBinaryUnmarshalThenMarshal() {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { MyDataClassAnnotation.class }, null);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }

        // setup control object
        MyDataClassAnnotation ctrlData = new MyDataClassAnnotation();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata.xml";
        InputStream iDocStream = loader.getResourceAsStream(instanceDoc);
        if (iDocStream == null) {
            fail("Couldn't load instance document [" + instanceDoc + "]");
        }
        
        MyDataClassAnnotation myData = null;
        try {
            myData = (MyDataClassAnnotation) unmarshaller.unmarshal(new StreamSource(iDocStream));
            assertTrue("Document unmarshalled incorrectly - object does not match control object", myData.equals(ctrlData));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
        
        // test marshal
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        Document testDoc = parser.newDocument();
        try {
            marshaller.marshal(ctrlData, testDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }

        // setup control document
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(instanceDoc);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + instanceDoc + "].");
        }
        assertTrue("Marshal failed - documents are not equal", compareDocuments(ctrlDoc, testDoc));
    }
    
    /**
     * Tests property level XmlInlineBinaryData annotation 
     * unmarshal/marshal.
     * 
     * Positive test.
     *
    public void testPropertyLevelXmlInlineBinaryUnmarshalThenMarshal() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { MyData.class }, properties);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }

        // setup control object
        MyData ctrlData = new MyData();
        //ctrlData.bytesAttribute = new byte[] { 4, 5, 6, 7 };
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };

////////////////        
        //try {
            //Marshaller marshaller = jaxbContext.createMarshaller();
            //marshaller.marshal(ctrlData, System.out);
        //} catch (JAXBException e) {
        //    e.printStackTrace();
        //    fail("Marshal operation failed.");
       // }
///////////////
        
        
        
        
        
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata.xml";
        InputStream iDocStream = loader.getResourceAsStream(instanceDoc);
        if (iDocStream == null) {
            fail("Couldn't load instance document [" + instanceDoc + "]");
        }
        
        MyData myData = null;
        try {
            myData = (MyData) unmarshaller.unmarshal(new StreamSource(iDocStream));
            assertTrue("Document unmarshalled incorrectly - object does not match control object", myData.equals(ctrlData));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
        
        // test marshal
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        Document testDoc = parser.newDocument();
        try {
            marshaller.marshal(ctrlData, testDoc);
            marshaller.marshal(ctrlData, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }

        // setup control document
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(instanceDoc);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + instanceDoc + "].");
        }
        assertTrue("Marshal failed - documents are not equal", compareDocuments(ctrlDoc, testDoc));
    }*/
}
