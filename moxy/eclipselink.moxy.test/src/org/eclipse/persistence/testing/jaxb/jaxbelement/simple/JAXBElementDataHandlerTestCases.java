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
//     Denise Smith - June 24/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;
import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;

public class JAXBElementDataHandlerTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/datahandler.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/datahandler.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/datahandlerschema.json";

    public JAXBElementDataHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTargetClass(DataHandler.class);
    }


    public Class getUnmarshalClass(){
        return DataHandler.class;
    }


    public void setUp() throws Exception{
        super.setUp();
        jaxbMarshaller.setAttachmentMarshaller(new MyJAXBAttachmentMarshaller(new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text")));
        jaxbUnmarshaller.setAttachmentUnmarshaller(new MyJAXBAttachmentUnmarshaller());
    }

    public Class[] getClasses(){
        Class[] classes = new Class[1];
        classes[0] = DataHandler.class;
        return classes;
    }

    protected Object getControlObject() {
        DataHandler value = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
        JAXBElement<DataHandler> jbe = new JAXBElement<DataHandler>(new QName("a", "b"),DataHandler.class, value);
        return jbe;
    }

    protected void compareValues(Object controlValue, Object testValue) {
        DataHandler dhControl = (DataHandler)controlValue;
        DataHandler dhTest = (DataHandler)testValue;
        assertEquals(dhControl.getContentType(),dhTest.getContentType());
        try{
            assertEquals(dhControl.getContent(),dhTest.getContent());
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    /*

    protected void comparePrimitiveArrays(Object controlValue, Object testValue){
        assertEquals(byte[].class, controlValue.getClass());
        assertEquals(byte[].class, testValue.getClass());

        byte[] controlArray = (byte[])controlValue;
        byte[] testArray = (byte[])testValue;

        assertEquals(controlArray.length, testArray.length);
        for (int i = 0; i < controlArray.length; i++) {
            assertEquals(controlArray[i], testArray[i]);
        }
    }
    */
    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);
    }

}
