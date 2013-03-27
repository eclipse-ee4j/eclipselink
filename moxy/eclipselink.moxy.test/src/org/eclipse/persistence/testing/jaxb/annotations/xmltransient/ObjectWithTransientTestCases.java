/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - October 18, 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import java.util.ArrayList;

import javax.xml.bind.MarshalException;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ObjectWithTransientTestCases extends JAXBWithJSONTestCases{

    public ObjectWithTransientTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { ObjectWithTransient.class });
    }

    @Override
    protected Object getControlObject() {
        ObjectWithTransient obj = new ObjectWithTransient();
        obj.testString = "theTest";
        obj.transientThing = new TransientClass();
        return obj;
    }
    
    public boolean isUnmarshalTest(){
        return false;
    }
    
    public void testJSONMarshalToOutputStream() throws Exception{
        try{
            super.testJSONMarshalToOutputStream();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)linkedException).getCause();
            assertTrue(actualException instanceof XMLMarshalException);
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)actualException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
        try{
            super.testJSONMarshalToOutputStream_FORMATTED();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)linkedException).getCause();
            assertTrue(actualException instanceof XMLMarshalException);
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)actualException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testJSONMarshalToStringWriter() throws Exception{
        try{
            super.testJSONMarshalToStringWriter();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    } 
    
    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
        try{
            super.testJSONMarshalToStringWriter_FORMATTED();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToContentHandler() throws Exception{
        try{
            super.testObjectToContentHandler();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);            
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToOutputStream() throws Exception{
        try{
            super.testObjectToOutputStream();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)linkedException).getCause();
            assertTrue(actualException instanceof XMLMarshalException);
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)actualException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToOutputStreamASCIIEncoding() throws Exception{
        try{
            super.testObjectToOutputStreamASCIIEncoding();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);
            Throwable actualException = ((XMLMarshalException)linkedException).getCause();
            assertTrue(actualException instanceof XMLMarshalException);
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)actualException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToXMLDocument() throws Exception{
        try{
            super.testObjectToXMLDocument();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);            
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());      return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToXMLEventWriter() throws Exception{
        try{
            super.testObjectToXMLEventWriter();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);            
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());     
            return;
        }
        fail("An exception should have been thrown");
    }
    public void testObjectToXMLStreamWriter() throws Exception{
        try{
            super.testObjectToXMLStreamWriter();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);            
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    public void testObjectToXMLStreamWriterRecord() throws Exception{
        try{
            super.testObjectToXMLStreamWriterRecord();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);            
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
    
    public void testObjectToXMLStringWriter() throws Exception{
        try{
            super.testObjectToXMLStringWriter();
        }catch(MarshalException e){
            Throwable linkedException = e.getLinkedException();
            assertTrue(linkedException instanceof XMLMarshalException);            
            assertEquals("Wrong XMLMarshalExcpetion was thrown",XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT ,((XMLMarshalException)linkedException).getErrorCode());
            return;
        }
        fail("An exception should have been thrown");
    }
}
