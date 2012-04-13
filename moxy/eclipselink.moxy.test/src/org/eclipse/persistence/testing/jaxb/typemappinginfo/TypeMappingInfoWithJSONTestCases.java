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
 *     Denise Smith -  November, 2009 
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.PropertyException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;

public abstract class TypeMappingInfoWithJSONTestCases extends TypeMappingInfoTestCases {
	protected String controlJSONLocation;
    protected String controlJSONWriteLocation;
        
	public TypeMappingInfoWithJSONTestCases(String name) throws Exception {
		super(name);
	}
 
    public void testXMLToObjectFromSourceWithTypeMappingInfoJSON() throws Exception {         
        InputStream instream = ClassLoader.getSystemResourceAsStream(controlJSONLocation);        
        StreamSource ss = new StreamSource(instream);
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.MEDIA_TYPE,"application/json");
        Object testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(ss, getTypeMappingInfo());
        instream.close();
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.MEDIA_TYPE,"application/xml");

        Object controlObj = getReadControlObject();
        xmlToObjectTest(testObject, controlObj);                  
    }  
    
  
    
    public void testObjectToResultWithTypeMappingInfoJSON() throws Exception {
    	jaxbMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
        Object objectToWrite = getWriteControlObject();
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);

        ((JAXBMarshaller)jaxbMarshaller).marshal(objectToWrite, result, getTypeMappingInfo()); 
        compareStrings("testObjectToResultWithTypeMappingInfoJSON",stringWriter.toString(), getWriteControlJSON());

        stringWriter.close();
	    jaxbMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/xml");
    }     


	
	public void testSchemaGen() throws Exception {
		testSchemaGen(getControlSchemaFiles());
	}
	
	
	    
	    public String getWriteControlJSON(){
	    	if(controlJSONWriteLocation != null){
	    		return controlJSONWriteLocation;
	    	}else{
	    		return controlJSONLocation;
	    	}
	    }
	    

	    public void setControlJSON(String location) {
	        this.controlJSONLocation = location;        
	    }
	    
	  

	    public void setWriteControlJSON(String location) {
	        this.controlJSONWriteLocation = location;        
	    }
	    
	
	    protected Object getJSONReadControlObject(){
	    	return getReadControlObject();
	    }
	    
	  
    protected void compareStrings(String test, String testString, String controlFileLocation) {
        log(test);
        log("Expected (With All Whitespace Removed):");
        
        String expectedString = loadFileToString(controlFileLocation);
        expectedString = expectedString.replaceAll("[ \b\t\n\r ]", "");        
        log(expectedString);
        
        log("\nActual (With All Whitespace Removed):");
        
        testString = testString.replaceAll("[ \b\t\n\r]", "");
        log(testString);
        assertEquals(expectedString, testString);
    }
 
    public void compareJAXBElementObjects(JAXBElement controlObj, JAXBElement testObj) {
        assertEquals(controlObj.getName().getLocalPart(), testObj.getName().getLocalPart());
        Object mapper = null;
		try {
			mapper = jaxbUnmarshaller.getProperty(JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER);
		} catch (PropertyException e) {
			e.printStackTrace();
			fail();
		}
        if(mapper != null){
            assertEquals(controlObj.getName().getNamespaceURI(), testObj.getName().getNamespaceURI());
        }
        assertEquals(controlObj.getDeclaredType(), testObj.getDeclaredType());

        Object controlValue = controlObj.getValue();
        Object testValue = testObj.getValue();

        if(controlValue == null) {
        	if(testValue == null){
        		return;
        	}
        	fail("Test value should have been null");
        }else{
        	if(testValue == null){
        		fail("Test value should not have been null");	
        	}
        }
        
        if(controlValue.getClass() == ClassConstants.ABYTE && testValue.getClass() == ClassConstants.ABYTE ||
        	controlValue.getClass() == ClassConstants.APBYTE && testValue.getClass() == ClassConstants.APBYTE){
        	compareValues(controlValue, testValue);
        }else if(controlValue.getClass().isArray()){
            if(testValue.getClass().isArray()){
                if(controlValue.getClass().getComponentType().isPrimitive()){
                    comparePrimitiveArrays(controlValue, testValue);
                }else{
                	compareObjectArrays(controlValue, testValue);                   
                }
            }else{
                fail("Expected an array value but was an " + testValue.getClass().getName());
            }
        }
        else if (controlValue instanceof Collection){
            Collection controlCollection = (Collection)controlValue;
            Collection testCollection = (Collection)testValue;
            Iterator<Object> controlIter = controlCollection.iterator();
            Iterator<Object> testIter = testCollection.iterator();
            assertEquals(controlCollection.size(), testCollection.size());
            while(controlIter.hasNext()){
                Object nextControl = controlIter.next();
                Object nextTest = testIter.next();
                compareValues(nextControl, nextTest);
            }
        }else{
        	compareValues(controlValue, testValue);
        }
    }

}





