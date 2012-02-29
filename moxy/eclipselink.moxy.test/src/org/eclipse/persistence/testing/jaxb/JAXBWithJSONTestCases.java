/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.xml.sax.InputSource;

public abstract class JAXBWithJSONTestCases extends JAXBTestCases {

    protected String controlJSONLocation;
    private String controlJSONWriteLocation;
    private String controlJSONWriteFormattedLocation;

    public JAXBWithJSONTestCases(String name) throws Exception {
        super(name);
    }

    public void setControlJSON(String location) {
        this.controlJSONLocation = location;        
    }
    
    public void setWriteControlJSON(String location) {
        this.controlJSONWriteLocation = location;        
    }

    public void setWriteControlFormattedJSON(String location) {
        this.controlJSONWriteFormattedLocation= location;        
    }
    
    public String getWriteControlJSON(){
    	if(controlJSONWriteLocation != null){
    		return controlJSONWriteLocation;
    	}else{
    		return controlJSONLocation;
    	}
    }

    public String getWriteControlJSONFormatted(){
    	if(controlJSONWriteFormattedLocation != null){
    		return controlJSONWriteFormattedLocation;
    	}else{    	
    	    return getWriteControlJSON();
    	}
    }
    
    protected Marshaller getJSONMarshaller() throws Exception{
    	return jaxbMarshaller;
    }
    
   protected Unmarshaller getJSONUnmarshaller() throws Exception{
	   return jaxbUnmarshaller;
    }
    
    public void jsonToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());

        if ((getReadControlObject() instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)getReadControlObject();
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj, false);
        } else {
        	assertEquals(getJSONReadControlObject(), testObject);
        }
    }
    
    public void testJSONUnmarshalFromInputStream() throws Exception {
    	if(isUnmarshalTest()){
	    	getJSONUnmarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        Object testObject = null;
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(inputStream), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(inputStream);
	        }
	        inputStream.close();
	        jsonToObjectTest(testObject);
    	}
    }
      

    public void testJSONUnmarshalFromInputSource() throws Exception {
    	if(isUnmarshalTest()){
	    	getJSONUnmarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");
	
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        InputSource inputSource = new InputSource(inputStream);
	        Object testObject = null;
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(inputStream), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(inputSource);
	        }
	        inputStream.close();
	        jsonToObjectTest(testObject);
    	}
    }

    public void testJSONUnmarshalFromReader() throws Exception {
    	if(isUnmarshalTest()){
	    	getJSONUnmarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");
	
	        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	        Reader reader = new InputStreamReader(inputStream);
	        Object testObject = null;
	        
	        if(getUnmarshalClass() != null){
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(reader), getUnmarshalClass());
	        }else{
	            testObject = getJSONUnmarshaller().unmarshal(reader);
	        }
	        
	        reader.close();
	        inputStream.close();
	        jsonToObjectTest(testObject);
    	}
    }
    

    public void testJSONUnmarshalFromURL() throws Exception {
    	if(isUnmarshalTest()){
	    	getJSONUnmarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");
	
	        URL url = getJSONURL();
	        Object testObject= null;
	        if(getUnmarshalClass() == null){
	        	testObject = getJSONUnmarshaller().unmarshal(url);
	        }else{
	        	testObject = getJSONUnmarshaller().unmarshal(new StreamSource(url.openStream()), getUnmarshalClass());
	        }
	        	
	        jsonToObjectTest(testObject);
    	}
    }
    

    public void testJSONMarshalToOutputStream() throws Exception{
    	getJSONMarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        getJSONMarshaller().marshal(getWriteControlObject(), os);
        compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
        os.close();
    }

    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
    	getJSONMarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");
    	getJSONMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        getJSONMarshaller().marshal(getWriteControlObject(), os);
        compareStrings("testJSONMarshalToOutputStream_FORMATTED", new String(os.toByteArray()), getWriteControlJSONFormatted());
        os.close();
    }

    public void testJSONMarshalToStringWriter() throws Exception{
    	getJSONMarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");

        StringWriter sw = new StringWriter();
        getJSONMarshaller().marshal(getWriteControlObject(), sw);
        compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
    	getJSONMarshaller().setProperty(JAXBContext.MEDIA_TYPE, "application/json");
    	getJSONMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        getJSONMarshaller().marshal(getWriteControlObject(), sw);        
        compareStrings("testJSONMarshalToStringWriter_FORMATTED", sw.toString(), getWriteControlJSONFormatted());
    }

    protected void compareStrings(String test, String testString) {
    	compareStrings(test, testString, getWriteControlJSON());
    }
    
    protected void compareStrings(String test, String testString, String controlFileLocation) {
        log(test);
        if(shouldRemoveEmptyTextNodesFromControlDoc()){
            log("Expected (With All Whitespace Removed):");
        }else{
        	log("Expected");
        }
        
        String expectedString = loadFileToString(controlFileLocation);
        if(shouldRemoveEmptyTextNodesFromControlDoc()){
        	expectedString = expectedString.replaceAll("[ \b\t\n\r ]", "");
        }
        log(expectedString);
        if(shouldRemoveEmptyTextNodesFromControlDoc()){
            log("\nActual (With All Whitespace Removed):");
        }else{
        	log("\nActual");
        }
        
        if(shouldRemoveEmptyTextNodesFromControlDoc()){
            testString = testString.replaceAll("[ \b\t\n\r]", "");
        }
        log(testString);
        assertEquals(expectedString, testString);
    }

    protected Object getJSONReadControlObject(){
    	return getReadControlObject();
    }

    private URL getJSONURL() {
        return Thread.currentThread().getContextClassLoader().getResource(controlJSONLocation);
    }

}