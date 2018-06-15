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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterDirectExceptionTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct.json";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct_null.xml";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct_null.json";

    public XmlAdapterDirectExceptionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyCalendarException.class;
        classes[1] = MyCalendarType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyCalendarException myCal = new MyCalendarException();
        MyCalendarType myCalType = new MyCalendarType();
        myCalType.day=12;
        myCalType.month=4;
        myCalType.year=1997;
        myCal.date = myCalType;
        return myCal;
    }

    public void setUp() throws Exception{
        super.setUp();
        MyCalendarExceptionAdapter.marshalHit = false;
        MyCalendarExceptionAdapter.unmarshalHit = false;
    }

    public Object getReadControlObject() {
        MyCalendarException myCal = new MyCalendarException();
        return myCal;
    }

    public void xmlToObjectTest(Object testObject) throws Exception{
        super.xmlToObjectTest(testObject);
        assertTrue(MyCalendarExceptionAdapter.unmarshalHit);
    }

    public void jsonToObjectTest(Object testObject) throws Exception{
        super.jsonToObjectTest(testObject);
        assertTrue(MyCalendarExceptionAdapter.unmarshalHit);
    }

    public void objectToXMLStringWriter(Object objectToWrite) throws Exception{
        super.objectToXMLStringWriter(objectToWrite);
        assertTrue(MyCalendarExceptionAdapter.marshalHit);
    }

   // @Override
    //public void assertMarshalException(Exception exception) throws Exception {
      //  Throwable nestedException = exception.getCause();
       // assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
   // }

    public void testRoundTrip(){
        //no need to perform this test
    }

}
