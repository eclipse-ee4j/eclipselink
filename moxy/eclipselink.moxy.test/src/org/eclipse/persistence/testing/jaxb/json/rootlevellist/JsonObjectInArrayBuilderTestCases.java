/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.json.JsonArrayBuilderResult;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class JsonObjectInArrayBuilderTestCases extends OXTestCase {
        
    public JsonObjectInArrayBuilderTestCases(String name){
        super(name);
    }
   
    public void testMarshalToArrayBuilderResult() throws Exception{
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{WithoutXmlRootElementRoot.class}, null);
        Marshaller jsonMarshaller = ctx.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilderResult result = new JsonArrayBuilderResult(jsonArrayBuilder);
        
        
        WithoutXmlRootElementRoot foo = new WithoutXmlRootElementRoot();
        foo.setName("FOO");
        
        jsonMarshaller.marshal(foo, result);
        
        WithoutXmlRootElementRoot foo2 = new WithoutXmlRootElementRoot();
        foo.setName("FOO2");
        
        jsonMarshaller.marshal(foo, result);

        JsonArray jsonArray = jsonArrayBuilder.build();          
        StringWriter sw = new StringWriter();
        JsonWriter writer= Json.createWriter(sw);
        writer.writeArray(jsonArray);
        writer.close();
        
        log(sw.toString());
        String controlString = "[{\"name\":\"FOO\"},{\"name\":\"FOO2\"}]";
        assertEquals(controlString, sw.toString());        
    }
    
    public void testMarshalToArrayBuilderResultWithRoot() throws Exception{
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{WithXmlRootElementRoot.class}, null);
        Marshaller jsonMarshaller = ctx.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilderResult result = new JsonArrayBuilderResult(jsonArrayBuilder);
        
        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        
        jsonMarshaller.marshal(foo, result);

        WithXmlRootElementRoot foo2 = new WithXmlRootElementRoot();
        foo2.setName("FOO2");
        jsonMarshaller.marshal(foo2, result);

        JsonArray jsonArray = jsonArrayBuilder.build();          
        StringWriter sw = new StringWriter();
        JsonWriter writer= Json.createWriter(sw);
        writer.writeArray(jsonArray);
        writer.close();
        
        
        log(sw.toString());
        String controlString = "[{\"root\":{\"name\":\"FOO\"}},{\"root\":{\"name\":\"FOO2\"}}]";
        assertEquals(controlString, sw.toString());        
    }
    
   
}
