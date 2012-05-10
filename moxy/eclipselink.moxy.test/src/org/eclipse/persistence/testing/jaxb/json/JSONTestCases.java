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
 *     Denise Smith - August 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public abstract class JSONTestCases extends OXTestCase{
    protected JAXBContext jaxbContext;	  
    protected Marshaller jsonMarshaller;
    protected Unmarshaller jsonUnmarshaller;
    protected ClassLoader classLoader;
    protected XMLContext xmlContext;
	
    protected String controlJSONLocation;
    private String controlJSONWriteLocation;

    public JSONTestCases(String name) {
		super(name);
	}
    
    public void setUp() throws Exception{
    	try {
            super.setUp();		
            jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jsonUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
           
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
    public void jsonToObjectTest(Object testObject) throws Exception {
    	jsonToObjectTest(getReadControlObject(), testObject);
    }
	    
    public void jsonToObjectTest(Object control, Object testObject) throws Exception {
        log("\n**jsonToObjectTest**");
        log("Expected:");
        log(control.toString());
        log("Actual:");
        log(testObject.toString());

        if ((getReadControlObject() instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)control;
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj);
        } else {
       	
        	if(testObject instanceof Collection && control instanceof Collection){
        		Collection testCollection = (Collection)testObject;
        		Collection controlCollection = (Collection)control;
        		assertTrue(testCollection.size() == controlCollection.size());
        		Iterator testIter = testCollection.iterator();
        		Iterator controlIter = controlCollection.iterator();
        		while(controlIter.hasNext()){
        			assertEquals(controlIter.next(), testIter.next());
        		}
        	}else{
        	
               assertEquals(control, testObject);
        	}
        }
    }
	    
	   
	protected void compareStrings(String test, String testString) {
    
	    compareStrings(test, testString,getWriteControlJSON(), true);
	}
	
	protected void compareStrings(String test, String testString, String writeControlJSON, boolean removeWhitespace) {
	    log(test);
	    log("Expected (With All Whitespace Removed):");
	    String expectedString = loadFileToString(writeControlJSON);
	    if(removeWhitespace){
	       expectedString = expectedString.replaceAll("[ \b\t\n\r ]", "");
	    }
	    log(expectedString);
	  	   
	    log("\nActual (With All Whitespace Removed):");
	    if(removeWhitespace){
	    	testString = testString.replaceAll("[ \b\t\n\r]", "");
		}	    
	    log(testString);
	    assertEquals(expectedString, testString);
	}
	
	public void testObjectToJSONStringWriter() throws Exception {    	
	    StringWriter sw = new StringWriter();
		jsonMarshaller.marshal(getWriteControlObject(), sw);
		compareStrings("**testObjectToJSONStringWriter**", sw.toString());		       
    }
	
	 public void testJSONMarshalToOutputStream() throws Exception{
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        jsonMarshaller.marshal(getWriteControlObject(), os);
	        compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
	        os.close();
	    }

	 public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
	    jsonMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
		jsonMarshaller.marshal(getWriteControlObject(), os);
		compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()), getWriteControlJSONFormatted(), shouldRemoveWhitespaceFromControlDocJSON());
		os.close();
	}

	public void testJSONMarshalToStringWriter() throws Exception{
		StringWriter sw = new StringWriter();
		jsonMarshaller.marshal(getWriteControlObject(), sw);
		compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
	}

	public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
	    jsonMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		jsonMarshaller.marshal(getWriteControlObject(), sw);		
	    compareStrings("**testJSONMarshalToStringWriter**", sw.toString(), getWriteControlJSONFormatted(), shouldRemoveWhitespaceFromControlDocJSON());
	}

	protected String getWriteControlJSONFormatted(){
		return getWriteControlJSON();
	}
	
	protected boolean shouldRemoveWhitespaceFromControlDocJSON(){
		return true;
	}
}
