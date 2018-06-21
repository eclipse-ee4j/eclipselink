/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4 - January 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller.space;

import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class UnmarshalWithSpaceEventTestCases extends JAXBTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/space/testObject.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/space/testObjectWrite.xml";

    public UnmarshalWithSpaceEventTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{TestObject.class});
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);


    }

    @Override
    protected Object getControlObject() {
        TestObject testObject = new TestObject();
        testObject.theString = "abc123";
        return testObject;
    }

     public void testXMLToObjectFromXMLStreamReaderWithClass() throws Exception {
            if(null != XML_INPUT_FACTORY && isUnmarshalTest()) {
                InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
                XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
                jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/xml");
                JAXBElement testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, TestObject.class);
                instream.close();
                xmlToObjectTest(testObject, getJAXBElementControlObject());
            }
        }

     private JAXBElement getJAXBElementControlObject(){
         JAXBElement<TestObject> jbe = new JAXBElement<TestObject>(new QName("testObject"), TestObject.class, (TestObject)getControlObject());
         return jbe;
     }

}
