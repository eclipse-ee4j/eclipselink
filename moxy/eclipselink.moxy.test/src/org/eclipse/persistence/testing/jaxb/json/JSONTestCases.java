/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - August 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.json.JsonArrayBuilderResult;
import org.eclipse.persistence.oxm.json.JsonGeneratorResult;
import org.eclipse.persistence.oxm.json.JsonObjectBuilderResult;
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


    protected void compareStringToControlFile(String test, String testString) {

        compareStringToFile(test, testString,getWriteControlJSON(), true);
    }

    protected void compareStringToFile(String test, String testString, String writeControlJSON, boolean removeWhitespace) {
        String expectedString = loadFileToString(writeControlJSON);
        compareStrings(test, testString, expectedString, removeWhitespace);
    }

    protected void compareStrings(String test, String testString, String expectedString, boolean removeWhitespace) {
        log(test);
        log("Expected (With All Whitespace Removed):");
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
        compareStringToControlFile("**testObjectToJSONStringWriter**", sw.toString());
    }

     public void testJSONMarshalToOutputStream() throws Exception{
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            jsonMarshaller.marshal(getWriteControlObject(), os);
            compareStringToControlFile("testJSONMarshalToOutputStream", new String(os.toByteArray()));
            os.close();
        }

     public void testJSONMarshalToBuilderResult() throws Exception{
           Object writeControlObject = getWriteControlObject();
           if(writeControlObject instanceof Collection || writeControlObject.getClass().isArray()){
               marshalToArrayBuilderResult();
           }else{
               marshalToObjectBuilderResult();
           }
     }

       public void marshalToObjectBuilderResult() throws Exception{

            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            JsonObjectBuilderResult result = new JsonObjectBuilderResult(jsonObjectBuilder);
            jsonMarshaller.marshal(getWriteControlObject(), result);

            JsonObject jsonObject = jsonObjectBuilder.build();
            StringWriter sw = new StringWriter();

            JsonWriter writer= Json.createWriter(sw);
            writer.writeObject(jsonObject);
            writer.close();
            log(sw.toString());
            compareStringToControlFile("**testJSONMarshalToBuilderResult**", sw.toString());
        }

    public void marshalToArrayBuilderResult() throws Exception{

           JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
           JsonArrayBuilderResult result = new JsonArrayBuilderResult(jsonArrayBuilder);
           jsonMarshaller.marshal(getWriteControlObject(), result);

           JsonArray jsonArray = jsonArrayBuilder.build();
           StringWriter sw = new StringWriter();
           JsonWriter writer= Json.createWriter(sw);
           writer.writeArray(jsonArray);
           writer.close();

           log(sw.toString());
           compareStringToControlFile("**testJSONMarshalToBuilderResult**", sw.toString());
       }

    public void testJSONMarshalToGeneratorResult() throws Exception{

        StringWriter sw = new StringWriter();
        JsonGenerator jsonGenerator = Json.createGenerator(sw);
        JsonGeneratorResult result = new JsonGeneratorResult(jsonGenerator);
        jsonMarshaller.marshal(getWriteControlObject(), result);
        jsonGenerator.flush();

        log(sw.toString());
        compareStringToControlFile("**testJSONMarshalToGeneratorResult**", sw.toString());
    }

     public void testJSONMarshalToOutputStream_FORMATTED() throws Exception{
        jsonMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jsonMarshaller.marshal(getWriteControlObject(), os);
        compareStringToFile("testJSONMarshalToOutputStream", new String(os.toByteArray()), getWriteControlJSONFormatted(), shouldRemoveWhitespaceFromControlDocJSON());
        os.close();
    }

    public void testJSONMarshalToStringWriter() throws Exception{
        StringWriter sw = new StringWriter();
        jsonMarshaller.marshal(getWriteControlObject(), sw);
        compareStringToControlFile("**testJSONMarshalToStringWriter**", sw.toString());
    }

    public void testJSONMarshalToStringWriter_FORMATTED() throws Exception{
        jsonMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        jsonMarshaller.marshal(getWriteControlObject(), sw);
        compareStringToFile("**testJSONMarshalToStringWriter**", sw.toString(), getWriteControlJSONFormatted(), shouldRemoveWhitespaceFromControlDocJSON());
    }

    protected String getWriteControlJSONFormatted(){
        return getWriteControlJSON();
    }

    protected boolean shouldRemoveWhitespaceFromControlDocJSON(){
        return true;
    }
}
