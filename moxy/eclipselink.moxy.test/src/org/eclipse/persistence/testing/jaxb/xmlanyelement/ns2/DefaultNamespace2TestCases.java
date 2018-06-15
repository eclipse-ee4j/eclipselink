/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns2;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.record.XMLStreamWriterRecord;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultNamespace2TestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/root2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/root2.json";

    public DefaultNamespace2TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
    }

       @Override
    protected Root getControlObject() {
        Root root = new Root();

        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document document = xmlPlatform.createDocument();
        Element element = document.createElementNS("namespace1", "childelem");
        root.setChild(element);

        return root;
    }

       public Root getReadControlObject() {
           Root root = new Root();

           XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
           Document document = xmlPlatform.createDocument();
           Element element = document.createElementNS("namespace1", "childelem");
           element.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", "namespace1");
           root.setChild(element);

           return root;
       }

       @Override
       protected Root getJSONReadControlObject() {
           Root root = new Root();

           XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
           Element element = xmlPlatform.createDocument().createElement("childelem");
           root.setChild(element);

           return root;
       }

       public void testObjectToXMLStreamWriterRepairing() throws Exception {
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(true));
            XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");

            int sizeBefore = getNamespaceResolverSize(desc);
            try {
                jaxbMarshaller.marshal(objectToWrite, streamWriter);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }

            streamWriter.flush();
            int sizeAfter = getNamespaceResolverSize(desc);

            assertEquals(sizeBefore, sizeAfter);
            Document testDocument = getTestDocument(writer.toString());

            writer.close();
            objectToXMLDocumentTest(testDocument);
        }
    }

       public void testObjectToXMLStreamWriterRepairingRecord() throws Exception {
           if(XML_OUTPUT_FACTORY != null) {
               StringWriter writer = new StringWriter();

               XMLOutputFactory factory = XMLOutputFactory.newInstance();
               factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(true));
               XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

               Object objectToWrite = getWriteControlObject();
               XMLDescriptor desc = null;
               if (objectToWrite instanceof XMLRoot) {
                   desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
               } else {
                   desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
               }
               jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");

               int sizeBefore = getNamespaceResolverSize(desc);
               XMLStreamWriterRecord record = new XMLStreamWriterRecord(streamWriter);
               try {
                   ((org.eclipse.persistence.jaxb.JAXBMarshaller)jaxbMarshaller).marshal(objectToWrite, record);
               } catch(Exception e) {
                   assertMarshalException(e);
                   return;
               }
               if(expectsMarshalException){
                   fail("An exception should have occurred but didn't.");
                   return;
               }

               streamWriter.flush();
               int sizeAfter = getNamespaceResolverSize(desc);

               assertEquals(sizeBefore, sizeAfter);

               Document testDocument = getTestDocument(writer.toString());
               writer.close();
               objectToXMLDocumentTest(testDocument);
           }
       }



}
