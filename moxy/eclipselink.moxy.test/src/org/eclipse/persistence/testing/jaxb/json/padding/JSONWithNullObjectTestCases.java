/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - November 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.padding;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.numbers.NumberHolder;

public class JSONWithNullObjectTestCases extends JAXBWithJSONTestCases{

	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.json";
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.xml";

	public JSONWithNullObjectTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Simple.class});
		setControlJSON(JSON_RESOURCE);		
		setControlDocument(XML_RESOURCE);
	}

	@Override
	protected Object getControlObject() {
		//Sample sample = new Sample();
		//sample.id = "1111";
		//sample.name = "theName";
		
		JSONWithPadding test = new JSONWithPadding(null, "blah");
		return test;
	}
	
	public boolean isUnmarshalTest (){
		return false;
	}
	
	public void testJSONMarshalToOutputStream() throws Exception{
		try{
		    super.testJSONMarshalToOutputStream();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25003, ((XMLMarshalException)nestedException).getErrorCode());
			Exception internalException = (Exception) ((XMLMarshalException)nestedException).getInternalException();
			assertTrue(internalException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)internalException).getErrorCode());
			return;
		}
		fail("An error should have occurred");
		
	}
	
	public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
		try{
		    super.testJSONMarshalToOutputStream_FORMATTED();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25003, ((XMLMarshalException)nestedException).getErrorCode());
			Exception internalException = (Exception) ((XMLMarshalException)nestedException).getInternalException();
			assertTrue(internalException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)internalException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testJSONMarshalToStringWriter() throws Exception{
		try{
		    super.testJSONMarshalToStringWriter();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
		try{
		    super.testJSONMarshalToStringWriter_FORMATTED();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToContentHandler() throws Exception{
		try{
		    super.testObjectToContentHandler();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToOutputStream() throws Exception{
		try{
		    super.testObjectToOutputStream();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToOutputStreamASCIIEncoding() throws Exception{
		try{
		    super.testObjectToOutputStreamASCIIEncoding();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToXMLDocument() throws Exception{
		try{
		    super.testObjectToXMLDocument();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToXMLEventWriter() throws Exception{
		try{
		    super.testObjectToXMLEventWriter();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToXMLStreamWriter() throws Exception{
		try{
		    super.testObjectToXMLStreamWriter();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToXMLStreamWriterRecord() throws Exception{
		try{
		    super.testObjectToXMLStreamWriterRecord();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
	
	public void testObjectToXMLStringWriter() throws Exception{
		try{
		    super.testObjectToXMLStringWriter();
		}catch(JAXBException e){
			Exception nestedException = (Exception) e.getLinkedException();
			assertTrue(nestedException instanceof XMLMarshalException);
			assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
			return;
		}
		fail("An error should have occurred");		
	}
}
