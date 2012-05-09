/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - Initial Implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.events;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.w3c.dom.Document;

public class ExternalMetadataTestCases extends RootWithCompositeObjectTestCases {

    public ExternalMetadataTestCases(String name) throws Exception {
        super(name);
    }
    
    @Override
    public Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/events/employee-oxm.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.events", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        return properties;
    }
    
    public void testObjectToOutputStream() throws Exception {
        Object objectToWrite = getWriteControlObject();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);
        jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
        jaxbMarshaller.marshal(objectToWrite, stream);

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);     
        
        if(getProperties() != null){
             log("************test with JSON bindings*********");
             ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
             JAXBContext jaxbContextFromJSONBindings = createJaxbContextFromJSONBindings();
             Marshaller jaxbMarshallerFromJSONBindings = jaxbContextFromJSONBindings.createMarshaller();
             jaxbMarshallerFromJSONBindings.setAttachmentMarshaller(jaxbMarshaller.getAttachmentMarshaller());
             
             
             jaxbMarshallerFromJSONBindings.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, jaxbMarshaller.getProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER));
             

             //before marshalling object again, need to reset it's triggered events
             ((Employee)objectToWrite).triggeredEvents = new ArrayList();
             jaxbMarshallerFromJSONBindings.marshal(objectToWrite, stream2);                         
             InputStream is2 = new ByteArrayInputStream(stream2.toByteArray());
             Document testDocument2 = parser.parse(is2);
             stream2.close();
             is2.close();

             objectToXMLDocumentTest(testDocument2);     
        }
    }    
}


