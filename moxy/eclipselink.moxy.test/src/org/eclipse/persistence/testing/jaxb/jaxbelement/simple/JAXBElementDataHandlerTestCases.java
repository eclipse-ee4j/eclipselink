/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - June 24/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;

public class JAXBElementDataHandlerTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/datahandler.xml";

	public JAXBElementDataHandlerTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);	
		setTargetClass(DataHandler.class);		
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
}
