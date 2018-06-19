/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.json.JsonGeneratorResult;
import org.eclipse.persistence.oxm.json.JsonObjectBuilderResult;
import org.eclipse.persistence.oxm.json.JsonParserSource;
import org.eclipse.persistence.oxm.json.JsonStructureSource;
import org.xml.sax.InputSource;

public abstract class JAXBWithJSONTestCases extends JAXBTestCases {

    protected String controlJSONLocation;
    private String controlJSONWriteLocation;
    private String controlJSONWriteFormattedLocation;

    public JAXBWithJSONTestCases(String name) throws Exception {
        super(name);
    }

    protected Map<String, String> getAdditationalNamespaces() {
        return new HashMap<String, String>();
    }

    public void initXsiType() throws Exception {
        addXsiTypeToUnmarshaller(getJSONUnmarshaller());
        addXsiTypeToMarshaller(getJSONMarshaller());
    }

    private Map<String, String> getXsiTypeNamespaces() {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.putAll(getAdditationalNamespaces());
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");

        return namespaces;
    }

    protected void addXsiTypeToUnmarshaller(Unmarshaller u) throws Exception {
        u.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, getXsiTypeNamespaces());
    }

    protected void addXsiTypeToMarshaller(Marshaller u) throws Exception {
        u.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, getXsiTypeNamespaces());
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
          jsonToObjectTest(testObject, getJSONReadControlObject());
   }

    public void jsonToObjectTest(Object testObject, Object controlObject) throws Exception {
        if(controlObject == null){
            assertNull(testObject);
            return;
        }

        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(controlObject.toString());
        log("Actual:");
        log(testObject.toString());

        if ((controlObject instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)controlObject;
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj, false);
        } else {
            assertEquals(controlObject, testObject);
        }
    }

    public void testJSONUnmarshalFromInputStream() throws Exception {
        if(isUnmarshalTest()){
            getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());
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
    public MediaType getJSONUnmarshalMediaType(){
        return MediaType.APPLICATION_JSON;
    }

    public void testUnmarshalAutoDetect() throws Exception {
        if(isUnmarshalTest()){
               getJSONUnmarshaller().setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
            Object testObject = null;
            if(getUnmarshalClass() != null){
                testObject = getJSONUnmarshaller().unmarshal(new StreamSource(inputStream), getUnmarshalClass());
            }else{
                testObject = getJSONUnmarshaller().unmarshal(inputStream);
            }
            inputStream.close();
            jsonToObjectTest(testObject);

            assertTrue(getJSONUnmarshaller().getProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE) == Boolean.TRUE);
            if(null != XML_INPUT_FACTORY) {
                InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
                XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

                if(getUnmarshalClass() != null){
                    testObject = getJAXBUnmarshaller().unmarshal(xmlStreamReader, getUnmarshalClass());
                }else{
                    testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
                }
                instream.close();
                xmlToObjectTest(testObject);
            }
            assertTrue(getJSONUnmarshaller().getProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE)== Boolean.TRUE);

        }
    }

    public void testJSONUnmarshalFromInputSource() throws Exception {
        if(isUnmarshalTest()){
            getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());


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
            getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());


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

    public void testJSONUnmarshalFromSource() throws Exception {
        if(isUnmarshalTest()){
        getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());

            InputStream controlJSONInputStream = null;
            try {
                controlJSONInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
                Source source = new StreamSource(controlJSONInputStream);
                Object testObject = null;
                if (getUnmarshalClass() != null) {
                    testObject = getJSONUnmarshaller().unmarshal(source, getUnmarshalClass());
                } else {
                    testObject = getJSONUnmarshaller().unmarshal(source);
                }
                jsonToObjectTest(testObject);
            } finally {
                if (null != controlJSONInputStream) {
                    try {
                        controlJSONInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void testJSONUnmarshalFromJsonStructureSource() throws Exception {
        if(isUnmarshalTest()){
            getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());
           InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
           JsonReader reader = Json.createReader(inputStream);
           JsonStructure jsonStructure = reader.read();
           JsonStructureSource source = new JsonStructureSource(jsonStructure);

            Object testObject = null;
            if(getUnmarshalClass() != null){
                testObject = getJSONUnmarshaller().unmarshal(source, getUnmarshalClass());
            }else{
                testObject = getJSONUnmarshaller().unmarshal(source);
            }
            jsonToObjectTest(testObject);
        }
    }

    public void testJsonUnmarshalFromJsonParserSource() throws Exception {
        if (isUnmarshalTest()) {
            getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());
            try (
                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
                JsonParser parser = Json.createParser(inputStream)
            ) {
                JsonParserSource source = new JsonParserSource(parser);

                Object testObject;
                if (getUnmarshalClass() != null) {
                    testObject = getJSONUnmarshaller().unmarshal(source, getUnmarshalClass());
                } else {
                    testObject = getJSONUnmarshaller().unmarshal(source);
                }
                jsonToObjectTest(testObject);
            }
        }
    }

    public void testJSONUnmarshalFromURL() throws Exception {
        if(isUnmarshalTest()){
            getJSONUnmarshaller().setProperty(UnmarshallerProperties.MEDIA_TYPE, getJSONUnmarshalMediaType());
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
        getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            getJSONMarshaller().marshal(getWriteControlObject(), os);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        compareStringToControlFile("testJSONMarshalToOutputStream", new String(os.toByteArray()));
        os.close();
    }

    public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
        getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        getJSONMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
        getJSONMarshaller().marshal(getWriteControlObject(), os);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        compareStringToControlFile("testJSONMarshalToOutputStream_FORMATTED", new String(os.toByteArray()), getWriteControlJSONFormatted(), shouldRemoveWhitespaceFromControlDocJSON());
        os.close();
    }

    public void testJSONMarshalToStringWriter() throws Exception{
        getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        StringWriter sw = new StringWriter();
        try {
        getJSONMarshaller().marshal(getWriteControlObject(), sw);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        log(sw.toString());
        compareStringToControlFile("**testJSONMarshalToStringWriter**", sw.toString());
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
        getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        getJSONMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        try {
        getJSONMarshaller().marshal(getWriteControlObject(), sw);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        log(sw.toString());
        compareStringToControlFile("testJSONMarshalToStringWriter_FORMATTED", sw.toString(), getWriteControlJSONFormatted(),shouldRemoveWhitespaceFromControlDocJSON());
    }

    public void testJSONMarshalToBuilderResult() throws Exception{
        getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonObjectBuilderResult result = new JsonObjectBuilderResult(jsonObjectBuilder);
        try{
            getJSONMarshaller().marshal(getWriteControlObject(), result);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        JsonObject jsonObject = jsonObjectBuilder.build();

        StringWriter sw = new StringWriter();
        JsonWriter writer= Json.createWriter(sw);
        writer.writeObject(jsonObject);
        writer.close();

        log(sw.toString());
        compareStringToControlFile("**testJSONMarshalToBuilderResult**", sw.toString());
    }

    public void testJSONMarshalToGeneratorResult() throws Exception{
        getJSONMarshaller().setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");

        StringWriter sw = new StringWriter();
        JsonGenerator generator= Json.createGenerator(sw);
        JsonGeneratorResult result = new JsonGeneratorResult(generator);
        try{
            getJSONMarshaller().marshal(getWriteControlObject(), result);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        generator.flush();
        log(sw.toString());
        compareStringToControlFile("**testJSONMarshalToGeneratorResult**", sw.toString());
    }


    protected void compareStringToControlFile(String test, String testString) {
        compareStringToControlFile(test, testString, getWriteControlJSON());
    }

    protected void compareStringToControlFile(String test, String testString, String controlFileLocation) {
        compareStringToControlFile(test, testString, controlFileLocation, shouldRemoveEmptyTextNodesFromControlDoc());
    }

    protected void compareStringToControlFile(String test, String testString, String controlFileLocation, boolean removeWhitespace) {
        String expectedString = null;

        if (null == controlFileLocation) {
            expectedString = getControlJSONDocumentContent();
        } else {
            expectedString = loadFileToString(controlFileLocation);
        }

        expectedString = removeCopyrightFromString(expectedString);
        compareStrings(testString, testString, expectedString, removeWhitespace);
    }

    protected String getControlJSONDocumentContent() {
        return null;
    }

    protected void compareStrings(String test, String testString, String expectedString, boolean removeWhitespace) {
        log(test);
        if(removeWhitespace){
            log("Expected (With All Whitespace Removed):");
        }else{
            log("Expected");
        }

        if(removeWhitespace){
            expectedString = expectedString.replaceAll("[ \b\t\n\r]", "");
        }
        log(expectedString);
        if(removeWhitespace){
            log("\nActual (With All Whitespace Removed):");
        }else{
            log("\nActual");
        }

        if(removeWhitespace){
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

    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
        return true;
    }

    public void generateJSONSchema(InputStream controlSchema) throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(controlSchema);
        generateJSONSchema(controlSchemas);
    }


    public void generateJSONSchema(List<InputStream> controlSchemas) throws Exception {
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();

        Class theClass = getWriteControlObject().getClass();
        if(getWriteControlObject() instanceof JAXBElement){
             theClass = ((JAXBElement) getWriteControlObject()).getValue().getClass();
        }

        ((JAXBContext)getJAXBContext()).generateJsonSchema(outputResolver, theClass);

        List<Writer> generatedSchemas = outputResolver.getSchemaFiles();


        assertEquals("Wrong Number of Schemas Generated", controlSchemas.size(), generatedSchemas.size());

        for(int i=0; i<controlSchemas.size(); i++){
            InputStream controlInputstream = controlSchemas.get(i);
            Writer generated = generatedSchemas.get(i);
            log(generated.toString());
            String controlString =  loadInputStreamToString(controlInputstream);
            compareStrings("generateJSONSchema", generated.toString(), controlString, true);
        }
    }
}
