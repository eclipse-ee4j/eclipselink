/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.record.XMLStreamWriterRecord;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JAXBInheritanceNSSeparatorTestCases extends JAXBInheritanceNSTestCases {
    private static final String JSON_RESOURCE =  "org/eclipse/persistence/testing/jaxb/inheritance/ns/inheritanceNSSeparator.json";

    public JAXBInheritanceNSSeparatorTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, '*');
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, '*');
    }

    public JAXBMarshaller getJSONMarshaller() throws Exception{
        JAXBMarshaller m = super.getJSONMarshaller();
        m.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, '*');
        return m;
    }

    public void testUnmarshalFromFile() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE).getFile());
        Object testObject = jaxbUnmarshaller.unmarshal(file);
        jsonToObjectTest(testObject);
    }

      public void testObjectToXMLStreamWriterRecordJSON() throws Exception {
            if(XML_OUTPUT_FACTORY != null) {
                StringWriter writer = new StringWriter();

                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
                XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

                Object objectToWrite = getWriteControlObject();
                XMLDescriptor desc = null;
                if (objectToWrite instanceof XMLRoot) {
                    desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
                } else {
                    desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
                }

                jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

                int sizeBefore = getNamespaceResolverSize(desc);
                XMLStreamWriterRecord record = new XMLStreamWriterRecord(streamWriter);
                ((org.eclipse.persistence.jaxb.JAXBMarshaller)jaxbMarshaller).marshal(objectToWrite, record);

                streamWriter.flush();
                int sizeAfter = getNamespaceResolverSize(desc);

                assertEquals(sizeBefore, sizeAfter);
                StringReader reader = new StringReader(writer.toString());
                InputSource inputSource = new InputSource(reader);
                Document testDocument = parser.parse(inputSource);
                writer.close();
                reader.close();
                objectToXMLDocumentTest(testDocument);
            }
        }
}
