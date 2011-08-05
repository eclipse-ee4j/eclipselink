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
 *     Denise Smith - August 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;

import org.eclipse.persistence.testing.oxm.OXTestCase;

public class NoRootElementTestCases extends OXTestCase{    
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/norootelement/address.json";    
    private JAXBContext jaxbContext;
    
	public NoRootElementTestCases(String name) throws Exception {
	    super(name);		
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    jaxbContext = JAXBContextFactory.createContext(new Class[]{Address.class}, null, classLoader);

	}

	public Object getReadControlObject() {
		Address addr = new Address();
		addr.setId(10);
		addr.setCity("Ottawa");
		addr.setStreet("Main street");
		
		QName name = new QName("");
		JAXBElement jbe = new JAXBElement<Address>(name, Address.class, addr);
		return jbe;
	}

	 public void testObjectToJSONStringWriter() throws Exception {    	
	    Marshaller jsonMarshaller = jaxbContext.createMarshaller();
	    jsonMarshaller.setProperty("eclipselink.media.type", "application/json");
	        
	    StringWriter sw = new StringWriter();
	    Object obj = ((JAXBElement)getReadControlObject()).getValue();
	    jsonMarshaller.marshal(obj, sw);
	    compareStrings("**testObjectToJSONStringWriter**", sw.toString());
	       
	 }
	 
	 public void testJAXBElementToJSONStringWriter() throws Exception {    	
	    Marshaller jsonMarshaller = jaxbContext.createMarshaller();
	    jsonMarshaller.setProperty("eclipselink.media.type", "application/json");
	        
	    StringWriter sw = new StringWriter();
	    jsonMarshaller.marshal(getReadControlObject(), sw);
	    compareStrings("**testJAXBElementToJSONStringWriter**", sw.toString());
	        
	 }
	 
     private void compareStrings(String testName, String testString) {
	    log(testName);
	    log("Expected (With All Whitespace Removed):");
	    String expectedString = loadFileToString(JSON_RESOURCE).replaceAll("[ \b\t\n\r ]", "");
	    log(expectedString);
	    log("\nActual (With All Whitespace Removed):");
	    testString = testString.replaceAll("[ \b\t\n\r]", "");
	    log(testString);
	    assertEquals(expectedString, testString);
	 }
	 
    public void testJSONToObjectFromInputSourceWithClass() throws Exception {    	
        Unmarshaller jsonUnmarshaller = jaxbContext.createUnmarshaller();
        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JSON_RESOURCE);
        StreamSource ss = new StreamSource(inputStream);
        JAXBElement testObject = ((JAXBUnmarshaller)jsonUnmarshaller).unmarshal(ss, Address.class);
        inputStream.close();
        jsonToObjectTest(testObject);
    }
    
    public void testJSONToObjectFromReaderWithClass() throws Exception {    	
        Unmarshaller jsonUnmarshaller = jaxbContext.createUnmarshaller();
        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JSON_RESOURCE);
        InputStreamReader reader = new InputStreamReader(inputStream);
        StreamSource ss = new StreamSource(reader);
        JAXBElement testObject = ((JAXBUnmarshaller)jsonUnmarshaller).unmarshal(ss, Address.class);
        inputStream.close();

        jsonToObjectTest(testObject);
    }
    
    
    public void jsonToObjectTest(Object testObject) throws Exception {
        log("\n**jsonToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());

        if ((getReadControlObject() instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)getReadControlObject();
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj);
        } else {
            fail("Should have returned a JAXBElement but didn't");
        }
    }
    
   
}

