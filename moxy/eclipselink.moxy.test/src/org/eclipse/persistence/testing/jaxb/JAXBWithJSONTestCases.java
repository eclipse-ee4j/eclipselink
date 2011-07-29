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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

public abstract class JAXBWithJSONTestCases extends JAXBTestCases {

    private String controlJSONLocation;
    private String controlJSONWriteLocation;

    public JAXBWithJSONTestCases(String name) throws Exception {
        super(name);
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

    public void testJSONUnmarshalFromInputStream() throws Exception {
        Unmarshaller jsonUnmarshaller = getJAXBContext().createUnmarshaller();
        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        Object testObject = jsonUnmarshaller.unmarshal(inputStream);
        inputStream.close();
        assertEquals(getControlObject(), testObject);
    }

    public void testJSONUnmarshalFromInputSource() throws Exception {
        Unmarshaller jsonUnmarshaller = getJAXBContext().createUnmarshaller();
        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        InputSource inputSource = new InputSource(inputStream);
        Object testObject = jsonUnmarshaller.unmarshal(inputSource);
        inputStream.close();
        assertEquals(getControlObject(), testObject);
    }

    public void testJSONUnmarshalFromReader() throws Exception {
        Unmarshaller jsonUnmarshaller = getJAXBContext().createUnmarshaller();
        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        Reader reader = new InputStreamReader(inputStream);
        Object testObject = jsonUnmarshaller.unmarshal(reader);
        reader.close();
        inputStream.close();
        assertEquals(getControlObject(), testObject);
    }

    public void testJSONUnmarshalFromURL() throws Exception {
        Unmarshaller jsonUnmarshaller = getJAXBContext().createUnmarshaller();
        jsonUnmarshaller.setProperty("eclipselink.media.type", "application/json");

        URL url = getJSONURL();
        Object testObject = jsonUnmarshaller.unmarshal(url);
        assertEquals(getControlObject(), testObject);
    }

    public void testJSONMarshalToOutputStream() throws Exception{
        Marshaller jsonMarshaller = getJAXBContext().createMarshaller();
        jsonMarshaller.setProperty("eclipselink.media.type", "application/json");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jsonMarshaller.marshal(getWriteControlObject(), os);
        compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
        os.close();
    }

    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
        Marshaller jsonMarshaller = getJAXBContext().createMarshaller();
        jsonMarshaller.setProperty("eclipselink.media.type", "application/json");
        jsonMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jsonMarshaller.marshal(getWriteControlObject(), os);
        compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
        os.close();
    }

    public void testJSONMarshalToStringWriter() throws Exception{
        Marshaller jsonMarshaller = getJAXBContext().createMarshaller();
        jsonMarshaller.setProperty("eclipselink.media.type", "application/json");

        StringWriter sw = new StringWriter();
        jsonMarshaller.marshal(getWriteControlObject(), sw);
        compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
        Marshaller jsonMarshaller = getJAXBContext().createMarshaller();
        jsonMarshaller.setProperty("eclipselink.media.type", "application/json");
        jsonMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        jsonMarshaller.marshal(getWriteControlObject(), sw);
        compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
    }

    private void compareStrings(String test, String testString) {
        log(test);
        log("Expected (With All Whitespace Removed):");
        String expectedString = getJSONControlString(getWriteControlJSON()).replaceAll("[ \b\t\n\r ]", "");
        log(expectedString);
        log("\nActual (With All Whitespace Removed):");
        testString = testString.replaceAll("[ \b\t\n\r]", "");
        log(testString);
        assertEquals(expectedString, testString);
    }

    protected String getJSONControlString(String fileName){
        StringBuffer sb = new StringBuffer();
        try {            
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
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

    private URL getJSONURL() {
        return Thread.currentThread().getContextClassLoader().getResource(controlJSONLocation);
    }

}