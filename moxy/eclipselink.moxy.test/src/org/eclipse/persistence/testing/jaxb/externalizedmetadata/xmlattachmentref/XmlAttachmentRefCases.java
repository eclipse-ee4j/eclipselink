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
 * dmccann - November 12/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.Employee;
import org.w3c.dom.Document;

/**
 * Tests XmlAttachmentRef via eclipselink-oxm.xml
 *
 */
public class XmlAttachmentRefCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattachmentref/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAttachmentRefCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }

    /**
     * Tests @XmlAttachmentRef override via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testSchemaGen() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }
    
    /**
     * Tests unmarshalling an instance doc with an attachment, then marshalling out the
     * object and comparing the documents.
     * 
     * Positive test.
     * @throws JAXBException 
     */
    public void testXmlAttachmentRefUnmarshalThenMarshal() throws JAXBException {
      /*  String metadataFile = PATH + "eclipselink-oxm.xml";
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
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { AttTypes.class }, properties);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }
*/
    	
    	Class[] classesToProcess = new Class[] { AttTypes.class };
        MySchemaOutputResolver outputResolver = generateSchema(classesToProcess, CONTEXT_PATH , PATH, 1);
    	
        // test unmarshal
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");      
        MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
        
        String instanceDoc = PATH + "att-types.xml";
        InputStream iDocStream = loader.getResourceAsStream(instanceDoc);
        if (iDocStream == null) {
            fail("Couldn't load instance document [" + instanceDoc + "]");
        }
        
        AttTypes attTypes = null;
        try {
            attTypes = (AttTypes) unmarshaller.unmarshal(new StreamSource(iDocStream));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
        
        // test marshal
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        Document testDoc = parser.newDocument();
        try {
            marshaller.marshal(attTypes, testDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }

        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(instanceDoc);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + instanceDoc + "].");
        }
        assertTrue("Unmarshal then marshal failed", compareDocuments(ctrlDoc, testDoc));
    }
    
    /**
     * Tests exception handling:  an xml-attachment-ref is applied to a 
     * non-DataHandler property.
     * 
     * Negative test.
     */
    public void testInvalidXmlAttachmentRef() {
        String metadataFile = PATH + "eclipselink-oxm-invalid.xml";
  /*      
    	Class[] classesToProcess = new Class[] { AttTypes.class };
        boolean exceptionOccurred = false;

    	try{
            MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classesToProcess, CONTEXT_PATH , metadataFile, 1);
    	}catch(JAXBException e1) {
            exceptionOccurred = true;
        }
        
        assertTrue("The expected exception did not occur.", exceptionOccurred);
*/
  
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean exceptionOccurred = false;
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { AttTypes.class }, properties);
        } catch (JAXBException e1) {
            exceptionOccurred = true;
        }
        
        assertTrue("The expected exception did not occur.", exceptionOccurred);
    }
}
