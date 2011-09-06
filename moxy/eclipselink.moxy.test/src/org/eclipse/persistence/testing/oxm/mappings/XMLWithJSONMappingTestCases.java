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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.eclipse.persistence.internal.oxm.record.json.JSONReader;
import org.eclipse.persistence.oxm.MediaType;
import org.xml.sax.InputSource;

public abstract class XMLWithJSONMappingTestCases extends XMLMappingTestCases{
    private String controlJSONLocation;
    private String controlWriteJSONLocation;
   
    
    public XMLWithJSONMappingTestCases(String name) throws Exception {
        super(name);
    }
			
    public void setControlJSON(String location) {
        this.controlJSONLocation = location;
    }
    
    public void setControlJSONWrite(String location) {
        this.controlWriteJSONLocation = location;
    }

    protected String getJSONWriteControlLocation() {
        if(controlWriteJSONLocation != null){
            return controlWriteJSONLocation;
        }
        return controlJSONLocation;
    }
    
    public void testJSONUnmarshalFromInputSource() throws Exception {
    	if(!(platform.name().equals(PLATFORM_DOC_PRES))){

            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
            InputSource inputSource = new InputSource(inputStream);
            xmlUnmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            
            Object testObject = xmlUnmarshaller.unmarshal(new JSONReader(null), inputSource);
            
    	    inputStream.close();
    	    assertEquals(getReadControlObject(), testObject);
    	}
    }
    
    public void testJSONMarshalToOutputStream() throws Exception{
    	if(!(platform.name().equals(PLATFORM_DOC_PRES))){

    	    xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            xmlMarshaller.marshal(getWriteControlObject(), os);
            compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
            os.close();
    	}
    }

    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{  
    	if(!(platform.name().equals(PLATFORM_DOC_PRES))){
    	    xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
        	xmlMarshaller.setFormattedOutput(true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            xmlMarshaller.marshal(getWriteControlObject(), os);
            compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
            os.close();
    	}
    }

    public void testJSONMarshalToStringWriter() throws Exception{
    	if(!(platform.name().equals(PLATFORM_DOC_PRES))){

    	    xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
            StringWriter sw = new StringWriter();
            xmlMarshaller.marshal(getWriteControlObject(), sw);
            compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
    	}
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
    	if(!(platform.name().equals(PLATFORM_DOC_PRES))){

    	    xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
        	xmlMarshaller.setFormattedOutput(true);

            StringWriter sw = new StringWriter();
            xmlMarshaller.marshal(getWriteControlObject(), sw);
            compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
    	}
    }

    private void compareStrings(String test, String testString) {
        log(test);
        log("Expected (With All Whitespace Removed):");
        String expectedString = getJSONControlString(getJSONWriteControlLocation()).replaceAll("[ \b\t\n\r' ']", "");
        log(expectedString);
        log("\nActual (With All Whitespace Removed):");
        testString = testString.replaceAll("[ \b\t\n\r]", "");
        log(testString);
        assertEquals(expectedString, testString);
    }

    protected String getJSONControlString(String fileName){
        StringBuffer sb = new StringBuffer();
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
           String str;
            while (bufferedReader.ready()) {
                sb.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return sb.toString();
    }
}
