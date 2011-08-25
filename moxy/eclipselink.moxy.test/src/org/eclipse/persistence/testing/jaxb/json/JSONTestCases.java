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
package org.eclipse.persistence.testing.jaxb.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.jaxb.json.norootelement.Address;
import org.eclipse.persistence.testing.oxm.OXTestCase;


public abstract class JSONTestCases extends OXTestCase{
	private JAXBContext jaxbContext;	  
	protected Marshaller jsonMarshaller;
	protected Unmarshaller jsonUnmarshaller;
	protected ClassLoader classLoader;
    protected XMLContext xmlContext;
	
    protected String controlJSONLocation;
    private String controlJSONWriteLocation;

    public JSONTestCases(String name) {
		super(name);
	}
    
    public void setUp(){
    	try {
			super.setUp();		
            jsonMarshaller.setProperty("eclipselink.media.type", "application/json");
	        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");
           
    	} catch (Exception e) {		
			e.printStackTrace();
			fail("An error occurred during setup");
		}
    }
    
    public void setControlJSON(String location) {
        this.controlJSONLocation = location;        
    }
    
    public void setWriteControlJSON(String location) {
        this.controlJSONWriteLocation = location;        
    }
    
    public String getWriteControlJSON(){
    	if(controlJSONWriteLocation != null){
    		return controlJSONWriteLocation;
    	}else{
    		return controlJSONLocation;
    	}
    }
    
    abstract protected Object getControlObject();

    /*
     * Returns the object to be used in a comparison on a read
     * This will typically be the same object used to write
     */
    public Object getReadControlObject() {
        return getControlObject();
    }

    /*
     * Returns the object to be written to XML which will be compared
     * to the control document.
     */
    public Object getWriteControlObject() {
        return getControlObject();
    }

    public Map getProperties(){
    	return null;
    }
    
    public void setClasses(Class[] newClasses) throws Exception {
        classLoader = Thread.currentThread().getContextClassLoader();
        jaxbContext = JAXBContextFactory.createContext(newClasses, getProperties(), classLoader);
        xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        jsonMarshaller = jaxbContext.createMarshaller();
        jsonUnmarshaller = jaxbContext.createUnmarshaller();
    }

    
	 public void testObjectToJSONStringWriter() throws Exception {    	
		        
		    StringWriter sw = new StringWriter();
		    Object obj = ((JAXBElement)getReadControlObject()).getValue();
		    jsonMarshaller.marshal(obj, sw);
		    compareStrings("**testObjectToJSONStringWriter**", sw.toString());
		       
		 }
		 
		 public void testJAXBElementToJSONStringWriter() throws Exception {    	
		    StringWriter sw = new StringWriter();
		    jsonMarshaller.marshal(getReadControlObject(), sw);
		    compareStrings("**testJAXBElementToJSONStringWriter**", sw.toString());
		        
		 }
		 
	     private void compareStrings(String testName, String testString) {
		    log(testName);
		    log("Expected (With All Whitespace Removed):");
		    String expectedString = loadFileToString(getWriteControlJSON()).replaceAll("[ \b\t\n\r ]", "");
		    log(expectedString);
		    log("\nActual (With All Whitespace Removed):");
		    testString = testString.replaceAll("[ \b\t\n\r]", "");
		    log(testString);
		    assertEquals(expectedString, testString);
		 }
		 
	    public void testJSONToObjectFromInputSourceWithClass() throws Exception {    	

	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        StreamSource ss = new StreamSource(inputStream);
	        JAXBElement testObject = ((JAXBUnmarshaller)jsonUnmarshaller).unmarshal(ss, Address.class);
	        inputStream.close();
	        jsonToObjectTest(testObject);
	    }
	    
	    public void testJSONToObjectFromReaderWithClass() throws Exception {    		        
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
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
