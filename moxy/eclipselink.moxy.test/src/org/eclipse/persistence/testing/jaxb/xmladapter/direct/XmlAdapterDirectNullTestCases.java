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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import javax.xml.bind.MarshalException;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterDirectNullTestCases extends JAXBWithJSONTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct_null.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct_null.json";
    private final static int DAY = 12; 
    private final static int MONTH = 4; 
    private final static int YEAR = 1997; 

    public XmlAdapterDirectNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);      
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyCalendar.class;
        classes[1] = MyCalendarType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyCalendar myCal = new MyCalendar();      
        return myCal;
    }
    
    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
        try{
            super.testJSONMarshalToOutputStream_FORMATTED();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue(nestedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)nestedException).getInternalException();
            assertTrue(actualException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testJSONMarshalToOutputStream() throws Exception{
        try{
            super.testJSONMarshalToOutputStream();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue(nestedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)nestedException).getInternalException();
            assertTrue(actualException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testJSONMarshalToStringWriter() throws Exception{
        try{
            super.testJSONMarshalToStringWriter();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);            
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
        try{
            super.testJSONMarshalToStringWriter_FORMATTED();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToContentHandler() throws Exception{
        try{
            super.testObjectToContentHandler();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToOutputStream() throws Exception{
        try{
            super.testObjectToOutputStream();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a XMLMarshalException but was " + nestedException.getClass().getName(), nestedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)nestedException).getInternalException();
            assertTrue("Nested exception should be a ConversionException but was " + actualException.getClass().getName(), actualException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToOutputStreamASCIIEncoding() throws Exception{
        try{
            super.testObjectToOutputStreamASCIIEncoding();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a XMLMarshalException but was " + nestedException.getClass().getName(), nestedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)nestedException).getInternalException();
            assertTrue("Nested exception should be a ConversionException but was " + actualException.getClass().getName(), actualException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToXMLDocument() throws Exception{
        try{
            super.testObjectToXMLDocument();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToXMLEventWriter() throws Exception{
        try{
            super.testObjectToXMLEventWriter();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    public void testObjectToXMLStreamWriter() throws Exception{
        try{
            super.testObjectToXMLStreamWriter();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    public void testObjectToXMLStreamWriterRecord() throws Exception{
        try{
            super.testObjectToXMLStreamWriterRecord();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
    }
    public void testObjectToXMLStringWriter() throws Exception{
        try{
            super.testObjectToXMLStringWriter();
        }catch(MarshalException me){
            Throwable nestedException = me.getLinkedException();
            assertTrue("Nested exception should be a ConversionException but was " + nestedException.getClass().getName(), nestedException instanceof ConversionException);
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testRoundTrip(){
        //no need to perform this test 
    }
}
