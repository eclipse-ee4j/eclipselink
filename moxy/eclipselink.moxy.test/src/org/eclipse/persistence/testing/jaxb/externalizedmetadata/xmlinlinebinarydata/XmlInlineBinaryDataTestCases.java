/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
     */
    public void testXmlInlineBinaryDataSchemaGen() {
        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { MyData.class }, CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }
    
    /**
     * Tests class level XmlInlineBinaryData annotation override via XML metadata.
     * Here, the class level annotation is overridden with 'false' in XML, hence 
     * we should use an attachment, and not inline the binary data.
     * 
     * Positive test.
     * @throws JAXBException 
     */
    public void testClassLevelXmlInlineBinaryOverride() throws JAXBException {
        String metadataFile = PATH + "eclipselink-oxm-class-override.xml";
        
        Class[] classes = new Class[] { MyDataClassAnnotation.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classes, CONTEXT_PATH, metadataFile, 1);
      
        // setup control object
        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text/xml");      
        MyDataClassAnnotation ctrlData = new MyDataClassAnnotation();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        ctrlData.setData(data);

        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.DATA_ID, data);
        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.BYTES_ID, new byte[] { 0, 1, 2, 3 });
        
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata-xop.xml";
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
            //marshaller.marshal(ctrlData, System.out);
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
     * Tests property level XmlInlineBinaryData annotation override.  Here,  the class level 
     * 'false' setting in XML metadata is overridden with an @XmlInlineBinaryData annotation
     * on the field.  The result is that we should inline the binary data as opposed to
     * using an attachment.
     * 
     * Positive test.
     * @throws JAXBException 
     */
    public void testPropertyLevelXmlInlineBinaryOverride() throws JAXBException {
        String metadataFile = PATH + "eclipselink-oxm-property.xml";
        
        Class[] classes = new Class[] { MyDataPropertyAnnotation.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classes, CONTEXT_PATH, metadataFile, 1);
        
        // setup control object
        MyDataPropertyAnnotation ctrlData = new MyDataPropertyAnnotation();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata.xml";
        InputStream iDocStream = loader.getResourceAsStream(instanceDoc);
        if (iDocStream == null) {
            fail("Couldn't load instance document [" + instanceDoc + "]");
        }
        
        MyDataPropertyAnnotation myData = null;
        try {
            myData = (MyDataPropertyAnnotation) unmarshaller.unmarshal(new StreamSource(iDocStream));
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
            //marshaller.marshal(ctrlData, System.out);
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
     * Tests Class level XmlInlineBinaryData annotation unmarshal/marshal.
     * No XML metadata is used for this test.
     * 
     * Positive test.
     *
     * THIS TEST CAN BE ENABLED UPON RESOLUTION OF BUG# 299948
     * @throws JAXBException 
     */
    public void testClassLevelXmlInlineBinaryNoOverride() throws JAXBException {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { MyDataClassAnnotation.class }, null);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }

        // setup control object
        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text/xml");      
        MyDataClassAnnotation ctrlData = new MyDataClassAnnotation();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        ctrlData.setData(data);

        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.DATA_ID, data);
        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.BYTES_ID, new byte[] { 0, 1, 2, 3 });
        
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata-handler.xml";
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
            //marshaller.marshal(ctrlData, System.out);
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
     * Tests property level XmlInlineBinaryData metadata override.  Here, the @XmlInlineBinaryData
     * annotation on the field is overridden with 'false' in XML.  The result is that we should 
     * not inline the binary data.
     * 
     * Positive test.
     * @throws JAXBException 
     */
    public void testPropertyLevelXmlInlineBinaryOverrideViaMetadata() throws JAXBException {
        String metadataFile = PATH + "eclipselink-oxm-property-override.xml";
        
        Class[] classes = new Class[] { MyDataPropertyAnnotation.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classes, CONTEXT_PATH, metadataFile, 1);
       
        // setup control object
        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text/xml");      
        MyDataPropertyAnnotation ctrlData = new MyDataPropertyAnnotation();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        ctrlData.setData(data);

        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.DATA_ID, data);
        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.BYTES_ID, new byte[] { 0, 1, 2, 3 });

        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata-xop.xml";
        InputStream iDocStream = loader.getResourceAsStream(instanceDoc);
        if (iDocStream == null) {
            fail("Couldn't load instance document [" + instanceDoc + "]");
        }
        
        MyDataPropertyAnnotation myData = null;
        try {
            myData = (MyDataPropertyAnnotation) unmarshaller.unmarshal(new StreamSource(iDocStream));
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
            //marshaller.marshal(ctrlData, System.out);
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
     * Tests class level XmlInlineBinaryData metadata override.  Here, there are no
     * class annotations, and no property level XML metadata declarations.
     * 
     * Positive test.
     * @throws JAXBException 
     */
    public void testClassLevelXmlInlineBinaryViaMetadata() throws JAXBException {
        String metadataFile = PATH + "eclipselink-oxm-class.xml";
        
        Class[] classes = new Class[] { MyData.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classes, CONTEXT_PATH, metadataFile, 1);
        
        // setup control object
        MyData ctrlData = new MyData();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        
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
            //marshaller.marshal(ctrlData, System.out);
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
     * Tests property level XmlInlineBinaryData metadata override.  Here, there are no
     * class annotations, and no class level XML metadata declarations.
     * 
     * Positive test.
     *
     * THIS TEST CAN BE ENABLED UPON RESOLUTION OF BUG# 299948
     * @throws JAXBException 
     */
    public void testPropertyLevelXmlInlineBinaryViaMetadata() throws JAXBException {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        
        Class[] classes = new Class[] { MyData.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classes, CONTEXT_PATH, metadataFile, 1);
       
        // setup control object
        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text/xml");      
        MyData ctrlData = new MyData();
        ctrlData.bytes = new byte[] { 0, 1, 2, 3 };
        ctrlData.setData(data);

        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.DATA_ID, data);
        MyAttachmentMarshaller.attachments.put(MyAttachmentMarshaller.BYTES_ID, new byte[] { 0, 1, 2, 3 });
        
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        String instanceDoc = PATH + "mydata-handler.xml";
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
            //marshaller.marshal(ctrlData, System.out);
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
}
