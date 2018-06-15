/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AutoDetectSmallDocTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/employee-small.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/employee-small.json";
    private final static String SMALL_FAIL_DOC = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/small-fails.xml";

    public AutoDetectSmallDocTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = TestObjectFactory.class;
        setClasses(classes);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
    }

    public MediaType getXMLUnmarshalMediaType(){
          return MediaType.APPLICATION_XML;
    }

    public MediaType getJSONUnmarshalMediaType(){
       return MediaType.APPLICATION_JSON;
    }

    @Override
    protected Object getControlObject() {
        EmployeeCollection employee = new EmployeeCollection();
        return employee;
    }

    public void testXMLToObjectFromInputStreamFails() throws Exception{
         InputStream instream = ClassLoader.getSystemResourceAsStream(SMALL_FAIL_DOC);
         jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML);
         try{
             Object testObject = jaxbUnmarshaller.unmarshal(instream);
             instream.close();

             xmlToObjectTest(testObject);
         }catch(UnmarshalException e){
             return;
         }
         fail("An UnmarshalException should have occurred");
    }

    public void testJSONToObjectFromInputStreamFails() throws Exception{
         InputStream instream = ClassLoader.getSystemResourceAsStream(SMALL_FAIL_DOC);
         jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
         try{
             Object testObject = jaxbUnmarshaller.unmarshal(instream);
             instream.close();
             jsonToObjectTest(testObject);
         }catch(UnmarshalException e){
             return;
         }
         fail("An UnmarshalException should have occurred");
    }

}
