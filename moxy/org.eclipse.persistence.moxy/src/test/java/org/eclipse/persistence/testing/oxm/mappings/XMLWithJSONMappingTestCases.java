/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.internal.oxm.record.namespaces.MapNamespacePrefixMapper;
import org.eclipse.persistence.internal.oxm.record.namespaces.PrefixMapperNamespaceResolver;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLRoot;
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

    protected boolean getNamespaceAware(){
        return false;
    }

    protected String getAttributePrefix(){
        return null;
    }
    protected Map<String, String> getNamespaces(){
    return null;
    }

    protected Map<String, String> getAdditionalNamsespaces() {
        return new HashMap<String, String>();
    }

    public void testJSONUnmarshalFromInputSource() throws Exception {

    if(isUnmarshalTest() &&  !(platform.name().equals(PLATFORM_DOC_PRES) || platform.name().equals(PLATFORM_DOM))){

            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
            InputSource inputSource = new InputSource(inputStream);
            xmlUnmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            PrefixMapperNamespaceResolver nr =  null;
            if(getNamespaces() != null){
                NamespacePrefixMapper mapper = new MapNamespacePrefixMapper(getNamespaces());
                nr = new PrefixMapperNamespaceResolver(mapper, null);
                xmlUnmarshaller.setNamespaceResolver(nr);
            }
            Object testObject = xmlUnmarshaller.unmarshal(inputSource);

            inputStream.close();

            if ((getJSONReadControlObject() instanceof XMLRoot) && (testObject instanceof XMLRoot)) {
                XMLRoot controlObj = (XMLRoot)getReadControlObject();
                XMLRoot testObj = (XMLRoot)testObject;
                compareXMLRootObjects(controlObj, testObj);
            } else {
                assertEquals(getJSONReadControlObject(), testObject);
            }

        }
    }

    public Object getJSONReadControlObject(){
        return getReadControlObject();
    }

    public void testJSONMarshalToOutputStream() throws Exception{
        if(!(platform.name().equals(PLATFORM_DOC_PRES) || platform.name().equals(PLATFORM_DOM))){

            xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
            if(getNamespaces() != null){
                xmlMarshaller.setNamespacePrefixMapper(new MapNamespacePrefixMapper(getNamespaces()));
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                xmlMarshaller.marshal(getWriteControlObject(), os);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }
            compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
            os.close();
        }
    }

    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
        if(!(platform.name().equals(PLATFORM_DOC_PRES) || platform.name().equals(PLATFORM_DOM))){
            xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
            if(getNamespaces() != null){
                xmlMarshaller.setNamespacePrefixMapper(new MapNamespacePrefixMapper(getNamespaces()));
            }
            xmlMarshaller.setFormattedOutput(true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                xmlMarshaller.marshal(getWriteControlObject(), os);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }

            compareStrings("testJSONMarshalToOutputStream", new String(os.toByteArray()));
            os.close();
        }
    }

    public void testJSONMarshalToStringWriter() throws Exception{
        if(!(platform.name().equals(PLATFORM_DOC_PRES) || platform.name().equals(PLATFORM_DOM))){

            xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
            if(getNamespaces() != null){
                xmlMarshaller.setNamespacePrefixMapper(new MapNamespacePrefixMapper(getNamespaces()));
            }
            StringWriter sw = new StringWriter();
            try {
                xmlMarshaller.marshal(getWriteControlObject(), sw);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }

            compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
        }
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
        if(!(platform.name().equals(PLATFORM_DOC_PRES) || platform.name().equals(PLATFORM_DOM))){

            xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
            if(getNamespaces() != null){
                xmlMarshaller.setNamespacePrefixMapper(new MapNamespacePrefixMapper(getNamespaces()));
            }
            xmlMarshaller.setFormattedOutput(true);

            StringWriter sw = new StringWriter();
            try {
                xmlMarshaller.marshal(getWriteControlObject(), sw);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }

            compareStrings("**testJSONMarshalToStringWriter**", sw.toString());
        }
    }

    private void compareStrings(String test, String testString) {
        log(test);
        log("Expected (With All Whitespace Removed):");
        String expectedString = getJSONControlString(getJSONWriteControlLocation()).replaceAll("[ \b\t\n\r]", "");
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
