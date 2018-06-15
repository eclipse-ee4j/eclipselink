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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects.ns;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.bind.marshaller.DataWriter;

public class MapNamespaceTestCases extends JAXBTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/ns/stringIntegerMap.xml";

    public MapNamespaceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {MapObject.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected MapObject getControlObject() {
        MapObject mo = new MapObject();
        mo.getMap().put("One", 1);
        return mo;
    }

    public void testObjectToXMLWithContentHandler() throws Exception {
        Object objectToWrite = getWriteControlObject();
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new DummyCharacterEscapeHandler());

        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);
        jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
        jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);

        try {
            jaxbMarshaller.marshal(objectToWrite, dataWriter);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        Document testDocument = getTestDocument(writer.toString());

        writer.close();

        objectToXMLDocumentTest(testDocument);
    }
    
    private class DummyCharacterEscapeHandler implements CharacterEscapeHandler {
        
        public void escape(char[] buf, int start, int len, boolean isAttValue, Writer out) throws IOException {
            if (len == 1 && buf[start] == '\n') {
                return;
            }

            StringWriter buffer = new StringWriter();

            for (int i = start; i < start + len; i++) {
                buffer.write(buf[i]);
            }
            
            out.write(buffer.toString());
        }
        
    }
}
