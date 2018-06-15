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
//     Denise Smith - 2.3.1
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class RepeatedUnmarshalTestCases extends OXTestCase{

    private static final String XML_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/valid.xml";
    private static final String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/invalid.xml";
    private static final String JSON_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/valid.json";

    private TestObject controlObject;
    private JAXBContext jaxbContext;

    public RepeatedUnmarshalTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception{
        super.setUp();
        controlObject = new TestObject();

        String controlString ="This is testing that if an unmarshal operation fails the unmarshaller will be left in a clean state so it can be reused to unmarshal subsequent documents";
        controlObject.bytes = controlString.getBytes();

        Class[] classes = new Class[1];
        classes[0] = TestObject.class;
        jaxbContext = JAXBContextFactory.createContext(classes, null, Thread.currentThread().getContextClassLoader());
    }

    public void testUnmarshalAfterFailedUnmarshal() throws JAXBException{
        JAXBUnmarshaller unm = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        Object unmarshalledObject = unm.unmarshal(inputStream);

        assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject.equals(controlObject));

        //invalid doc that causes an array index out of bounds exception
        try{
            unmarshalledObject = unm.unmarshal(ClassLoader.getSystemResourceAsStream(XML_RESOURCE_INVALID));
        }catch(Exception e){
            //Don't do anything we just fail here to test if subsequent unmarshals work
        }
        unmarshalledObject = unm.unmarshal(ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID));

        assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject != null);
        assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject.equals(controlObject));

    }

    public void testChangeMediaType() throws JAXBException{
        JAXBUnmarshaller unm = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        StreamSource ss = new StreamSource(inputStream);
        Object unmarshalledObject = unm.unmarshal(ss);

        assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject.equals(controlObject));

        unm.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        ss = new StreamSource(inputStream);

        unmarshalledObject = unm.unmarshal(new StreamSource(ClassLoader.getSystemResourceAsStream(JSON_RESOURCE_VALID)));
        assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject.equals(controlObject));

        unm.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML);
        unmarshalledObject = unm.unmarshal(new StreamSource(ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID)));
        assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject.equals(controlObject));


    }
}
