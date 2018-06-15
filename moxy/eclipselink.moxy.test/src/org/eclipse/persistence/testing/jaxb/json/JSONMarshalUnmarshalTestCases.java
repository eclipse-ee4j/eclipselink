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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.stream.JsonParser;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.json.JsonParserSource;
import org.eclipse.persistence.oxm.json.JsonStructureSource;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases.MyStreamSchemaOutputResolver;
import org.xml.sax.InputSource;

public abstract class JSONMarshalUnmarshalTestCases extends JSONTestCases{

    public JSONMarshalUnmarshalTestCases(String name) {
        super(name);
    }

    public Class getUnmarshalClass(){
        return null;
    }

    protected void compareStringToControlFile(String testName, String testString) {
        String expectedString = loadFileToString(getWriteControlJSON());
        compareStrings(testName, testString, expectedString, true);
    }

    public void testJSONUnmarshalFromInputStream() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        Object testObject = null;
        if(getUnmarshalClass() != null){
            testObject = jsonUnmarshaller.unmarshal(new StreamSource(inputStream), getUnmarshalClass());
        }else{
            testObject = jsonUnmarshaller.unmarshal(inputStream);
        }
        inputStream.close();
        jsonToObjectTest(testObject);
    }

    public void testJSONUnmarshalFromJsonStructureSource() throws Exception {

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        JsonReader reader = Json.createReader(inputStream);
        JsonStructure jsonStructure = reader.read();
        JsonStructureSource source = new JsonStructureSource(jsonStructure);

        Object testObject;
        if(getUnmarshalClass() != null){
            testObject = jsonUnmarshaller.unmarshal(source, getUnmarshalClass());
        }else{
            testObject = jsonUnmarshaller.unmarshal(source);
        }
        jsonToObjectTest(testObject);
    }

    public void testJsonUnmarshalFromJsonParserSource() throws Exception {
        try(
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
            JsonParser parser = Json.createParser(inputStream)
        ) {
            JsonParserSource source = new JsonParserSource(parser);

            Object testObject;
            if (getUnmarshalClass() != null) {
                testObject = jsonUnmarshaller.unmarshal(source, getUnmarshalClass());
            } else {
                testObject = jsonUnmarshaller.unmarshal(source);
            }
            jsonToObjectTest(testObject);
        }
    }

    public void testJSONUnmarshalFromInputSource() throws Exception {
         InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
         InputSource inputSource = new InputSource(inputStream);
          Object testObject = null;
            if(getUnmarshalClass() != null){
                testObject = jsonUnmarshaller.unmarshal(new StreamSource(inputStream), getUnmarshalClass());
            }else{
                testObject = jsonUnmarshaller.unmarshal(inputSource);
            }
         inputStream.close();
         jsonToObjectTest(testObject);
    }

    public void testJSONUnmarshalFromReader() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        Reader reader = new InputStreamReader(inputStream);
        Object testObject = null;
        if(getUnmarshalClass() != null){
            testObject = jsonUnmarshaller.unmarshal(new StreamSource(reader), getUnmarshalClass());
        }else{
            testObject = jsonUnmarshaller.unmarshal(reader);
        }
        reader.close();
        inputStream.close();
        jsonToObjectTest(testObject);
    }

    public void testJSONUnmarshalFromURL() throws Exception {
        URL url = getJSONURL();
        Object testObject = null;
        if(getUnmarshalClass() != null){
            testObject = jsonUnmarshaller.unmarshal(new StreamSource(url.openStream()), getUnmarshalClass());
        }else{
            testObject = jsonUnmarshaller.unmarshal(url);
        }
        jsonToObjectTest(testObject);
    }

    protected URL getJSONURL() {
        return Thread.currentThread().getContextClassLoader().getResource(controlJSONLocation);
    }
    public void generateJSONSchema(InputStream controlSchema) throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(controlSchema);
        generateJSONSchema(controlSchemas);
    }

    public void generateJSONSchema(List<InputStream> controlSchemas) throws Exception {
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();

        Class theClass = getWriteControlObject().getClass();
        if(theClass == JAXBElement.class){
             theClass = ((JAXBElement) getWriteControlObject()).getValue().getClass();
        }

        ((JAXBContext)jaxbContext).generateJsonSchema(outputResolver, theClass);
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
