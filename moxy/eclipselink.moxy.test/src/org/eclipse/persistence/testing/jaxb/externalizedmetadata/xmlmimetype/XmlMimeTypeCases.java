/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype;

import java.io.File;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;


import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlMimeType via eclipselink-oxm.xml
 *
 */
public class XmlMimeTypeCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmimetype/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlMimeTypeCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }
    
    /**
     * Tests @XmlMimeType override via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlMimeTypeSchemaGen() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }
    
    public void testXmlMimeTypeUnmarshalThenMarshal() {
    	
    	Class[] classes = new Class[] { AttTypes.class };
    	MySchemaOutputResolver outputResolver = generateSchema(classes, CONTEXT_PATH, PATH, 1);
    	
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
}
