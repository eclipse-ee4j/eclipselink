/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - May 2012
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.json.emptyroot;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class EmptyNullMarshalUnmarshalTestCases extends OXTestCase{

    private static final String EMPTY_JSON = "{}";    
	private JAXBContext ctx;
	private JAXBContext ctxNoRoot;
    
	public EmptyNullMarshalUnmarshalTestCases(String name) throws Exception {
		super(name);		
	}
	
	public void setUp() throws Exception{
		super.setUp();
		HashMap props = new HashMap();
		props.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
		
		ctx = (JAXBContext) JAXBContextFactory.createContext(new Class[]{Root.class}, props);
		ctxNoRoot = (JAXBContext) JAXBContextFactory.createContext(new Class[]{RootNoXmlRootElement.class}, props);
	}
	
	public void testJAXBElementNullValueIncludeRootTrue() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, null );		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.marshal(obj, sw);
		compareStrings("testJAXBElementNullValueIncludeRootTrue", EMPTY_JSON, sw.getBuffer().toString());
	}
	
	public void testJAXBElementNullValueIncludeRootFalse() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, null );		
		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		m.marshal(obj, sw);
		compareStrings("testJAXBElementNullValueIncludeRootFalse", EMPTY_JSON, sw.getBuffer().toString());
	}
	
	public void testJAXBElementEmptyValueIncludeRootTrue() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new Root() );		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.marshal(obj, sw);
		String expectedString = "{\"type\":\"root\"}";
		compareStrings("testJAXBElementEmptyValueIncludeRootTrue", expectedString, sw.getBuffer().toString());
	}
	
	public void testJAXBElementEmptyValueIncludeRootFalse() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new Root() );		
		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		m.marshal(obj, sw);
		String expectedString = "{\"type\":\"root\"}";
		compareStrings("testJAXBElementEmptyValueIncludeRootFalse", expectedString, sw.getBuffer().toString());
	}

	public void testJAXBElementEmptyValueNoRootIncludeRootTrue() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new RootNoXmlRootElement() );		
		Marshaller m = ctxNoRoot.createMarshaller();		
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.marshal(obj, sw);
		String expectedString = "{\"type\":\"rootNoXmlRootElement\"}";
		compareStrings("testJAXBElementEmptyValueNoRootIncludeRootTrue", expectedString, sw.getBuffer().toString());
	}
	
	public void testJAXBElementEmptyValueNoRootIncludeRootFalse() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new RootNoXmlRootElement() );		
		
		Marshaller m = ctxNoRoot.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		m.marshal(obj, sw);
		String expectedString = "{\"type\":\"rootNoXmlRootElement\"}";
		compareStrings("testJAXBElementEmptyValueNoRootIncludeRootFalse", expectedString, sw.getBuffer().toString());
	}

	public void testEmptyIncludeRootTrue() throws Exception{
		StringWriter sw = new StringWriter();		
		Object obj = new Root();		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.marshal(obj, sw);
		String expected = "{\"root\":{}}";
		compareStrings("testEmptyIncludeRootTrue", expected, sw.getBuffer().toString());
	}
	
	public void testEmptyIncludeRootFalse() throws Exception{
		StringWriter sw = new StringWriter();		
		Object obj = new Root();				
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		m.marshal(obj, sw);
		compareStrings("testEmptyIncludeRootFalse", EMPTY_JSON, sw.getBuffer().toString());
	}
	
	public void testEmptyNoRootIncludeRootTrue() throws Exception{
		StringWriter sw = new StringWriter();		
		Object obj = new RootNoXmlRootElement();		
		Marshaller m = ctxNoRoot.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.marshal(obj, sw);		
		compareStrings("testEmptyNoRootIncludeRootTrue", EMPTY_JSON, sw.getBuffer().toString());
	}
	
	public void testEmptyNoRootIncludeRootFalse() throws Exception{
		StringWriter sw = new StringWriter();		
		Object obj = new RootNoXmlRootElement();				
		Marshaller m = ctxNoRoot.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		m.marshal(obj, sw);
		compareStrings("testEmptyNoRootIncludeRootFalse", EMPTY_JSON, sw.getBuffer().toString());
	}
	
	public void testListJAXBElementIncludeRootTrue() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new Root() );
		JAXBElement obj2 = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new Root() );
		List theList = new ArrayList();
		theList.add(obj);
		theList.add(obj2);
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.marshal(theList, sw);
		String expectedString = "[{\"type\":\"root\"},{\"type\":\"root\"}]";
		compareStrings("testJAXBElementEmptyValueIncludeRootTrue", expectedString, sw.getBuffer().toString());
	}
	
	public void testListJAXBElementIncludeRootFalse() throws Exception{
		StringWriter sw = new StringWriter();		
		JAXBElement obj = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new Root() );
		JAXBElement obj2 = new JAXBElement<Object>(new QName(XMLConstants.EMPTY_STRING),Object.class, new Root() );
		List theList = new ArrayList();
		theList.add(obj);
		theList.add(obj2);
		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		m.marshal(theList, sw);
		String expectedString = "[{\"type\":\"root\"},{\"type\":\"root\"}]";
		compareStrings("testJAXBElementEmptyValueIncludeRootFalse", expectedString, sw.getBuffer().toString());
	}
	
	public void testUnmarshalIncludeRootTrue() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
		StringReader reader = new StringReader("{}");
		Object o = unmarshaller.unmarshal(reader);
		assertNull(o);
	}
	
	public void testUnmarshalIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("{}");
		Object o = unmarshaller.unmarshal(reader);
		assertNull(o);	
	}
	
	public void testUnmarshalClassIncludeRootTrue() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
		StringReader reader = new StringReader("{}");
		Object o = unmarshaller.unmarshal(new StreamSource(reader), Root.class);
		assertTrue (o instanceof JAXBElement);
		JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Root.class, null );
		compareJAXBElementObjects(controlObj, (JAXBElement)o);		
	}
	
	public void testUnmarshalClassIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("{}");
		Object o = unmarshaller.unmarshal(new StreamSource(reader), Root.class);

		assertTrue (o instanceof JAXBElement);
		JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Root.class, new Root() );
		compareJAXBElementObjects(controlObj, (JAXBElement)o);		
	}
	
	
	public void testUnmarshalNoRootTypeAttrIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctxNoRoot.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("{\"type\":\"rootNoXmlRootElement\"}");
		StringReader reader2 = new StringReader("{\"type\":\"rootNoXmlRootElement\"}");
		Object o = unmarshaller.unmarshal(new StreamSource(reader), RootNoXmlRootElement.class);
		assertTrue (o instanceof JAXBElement);
		JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),RootNoXmlRootElement.class, new RootNoXmlRootElement() );
		compareJAXBElementObjects(controlObj, (JAXBElement)o);		
		
		o = unmarshaller.unmarshal(new StreamSource(reader2));
		assertTrue (o instanceof JAXBElement);
		controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING), Object.class, new RootNoXmlRootElement() );
		compareJAXBElementObjects(controlObj, (JAXBElement)o);
	}
	
	public void testUnmarshalTypeAttrIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("{\"type\":\"root\"}");
		StringReader reader2 = new StringReader("{\"type\":\"root\"}");
		Object o = unmarshaller.unmarshal(new StreamSource(reader), Root.class);

		assertTrue (o instanceof JAXBElement);
		JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Root.class, new Root() );
		compareJAXBElementObjects(controlObj, (JAXBElement)o);
		
		Object o2 = unmarshaller.unmarshal(new StreamSource(reader2));
		assertTrue (o instanceof JAXBElement);
		controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Root.class, new Root() );
		compareJAXBElementObjects(controlObj, (JAXBElement)o);		

	}
	
	
	public void testUnmarshalEmptyListIncludeRootTrue() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
		StringReader reader = new StringReader("[]");
		Object o = unmarshaller.unmarshal(new StreamSource(reader));
		assertEquals(new ArrayList(), o);
	}
	
	public void testUnmarshalEmptyListIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("[]");
		Object o = unmarshaller.unmarshal(new StreamSource(reader));
		assertEquals(new ArrayList(), o);
	}
	
	public void testUnmarshalEmptyListObjectIncludeRootTrue() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
		StringReader reader = new StringReader("[{}]");
		Object o = unmarshaller.unmarshal(new StreamSource(reader));
		
		assertTrue (o instanceof List);
		assertEquals(1, ((List)o).size());
		assertNull(((List)o).get(0));

		//JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Object.class, null );
		//compareJAXBElementObjects(controlObj, (JAXBElement)((List)o).get(0));		
		//fail();
	}
	
	public void testUnmarshalEmptyListObjectIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("[{}]");
		Object o = unmarshaller.unmarshal(new StreamSource(reader));
		
		assertTrue (o instanceof List);
		assertEquals(1, ((List)o).size());
		assertNull(((List)o).get(0));
		//JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Object.class, null );
		//compareJAXBElementObjects(controlObj, (JAXBElement)((List)o).get(0));		
	}
	
	public void testUnmarshalEmptyListObjectClassIncludeRootTrue() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
		StringReader reader = new StringReader("[{}]");
		JAXBElement o = unmarshaller.unmarshal(new StreamSource(reader), Root.class);
		//assertTrue (o instanceof JAXBElement);
		//JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Root.class, new Root() );
		//compareJAXBElementObjects(controlObj, (JAXBElement)o);
		
		assertTrue(o.getDeclaredType().equals(Root.class));
		assertTrue(o.getName().equals(new QName("")));
		
		Object value = o.getValue();
		assertTrue (value instanceof List);
		assertEquals(1, ((List)value).size());
		
		
		JAXBElement controlObj = new JAXBElement(new QName(XMLConstants.EMPTY_STRING),Root.class, null );
		compareJAXBElementObjects(controlObj, (JAXBElement)((List)value).get(0));	
	}
	
	public void testUnmarshalEmptyListObjectClassIncludeRootFalse() throws Exception{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StringReader reader = new StringReader("[{}]");
		JAXBElement o = unmarshaller.unmarshal(new StreamSource(reader),Root.class);
		
		assertTrue(o.getDeclaredType().equals(Root.class));
		assertTrue(o.getName().equals(new QName("")));
		
		Object value = o.getValue();
		assertTrue (value instanceof List);
		assertEquals(1, ((List)value).size());

		assertTrue(((List)value).get(0) instanceof Root);
		assertNull(((Root)((List)value).get(0)).something);
	}
	
	protected void compareStrings(String test, String expectedString, String testString) {
	    log(test);
	    log("Expected (With All Whitespace Removed):");
	    log(expectedString);
	    log("\nActual (With All Whitespace Removed):");
	    testString = testString.replaceAll("[ \b\t\n\r]", "");
	    log(testString);
	    assertEquals(expectedString, testString);
	}
}
