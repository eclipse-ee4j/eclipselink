/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.defaultmapping.jsonp;

import org.eclipse.persistence.json.bind.defaultmapping.jsonp.model.JsonpPojo;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Default mapping JSONP integration tests.
 *
 * @author Dmitry Kornilov
 */
public class JsonpTest {

    private Jsonb jsonb = JsonbBuilder.create();

    public static class JsonValueWrapper {
        public JsonValue jsonValue;

        public JsonValueWrapper(JsonValue jsonValue) {
            this.jsonValue = jsonValue;
        }
    }

    @Test
    public void testMarshallJsonObject() {
        
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("name", "home")
                .add("city", "Prague")
                .build();

        final JsonObjectBuilder customerBuilder = factory.createObjectBuilder();
        customerBuilder.add("f1", "abc123");
        customerBuilder.add("f2", BigDecimal.TEN);
        customerBuilder.add("f3", 12);
        customerBuilder.add("city", jsonObject);

        final JsonObjectBuilder wrapperBuilder = factory.createObjectBuilder();
        wrapperBuilder.add("f1", "abc");
        wrapperBuilder.add("cust", customerBuilder);

        final JsonObject wrapper = wrapperBuilder.build();

        assertEquals("{\"f1\":\"abc\",\"cust\":{\"f1\":\"abc123\",\"f2\":10,\"f3\":12,\"city\":{\"name\":\"home\",\"city\":\"Prague\"}}}", jsonb.toJson(wrapper));
    }

    @Test
    public void testMarshallJsonArray() {
        
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArray jsonArray = factory.createArrayBuilder()
                .add(1)
                .add(2)
                .build();

        assertEquals("{\"jsonValue\":[1,2]}", jsonb.toJson(new JsonValueWrapper(jsonArray)));
    }

    @Test
    public void testMarshallJsonValue() {
        assertEquals("{\"jsonValue\":true}", jsonb.toJson(new JsonValueWrapper(JsonValue.TRUE)));
    }

    @Test
    public void testMarshallJsonNumber() {
                assertEquals("{\"jsonValue\":10}", jsonb.toJson(new JsonValueWrapper(new JsonpLong(10))));
    }

    @Test
    public void testMarshallJsonString() {
                assertEquals("{\"jsonValue\":\"hello\"}", jsonb.toJson(new JsonValueWrapper(new JsonpString("hello"))));
    }

    @Test
    public void testJsonPojo() {
        JsonbConfig config = new JsonbConfig();
//        config.withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);


        JsonpPojo pojo = new JsonpPojo();
        final JsonObjectBuilder obj1builder = JsonProvider.provider().createObjectBuilder();
        obj1builder.add("strVal", "string value");
        obj1builder.add("numVal", 2.0d);
        obj1builder.addNull("nullVal");
        obj1builder.add("boolVal", Boolean.TRUE);

        final JsonObjectBuilder obj2Builder = JsonProvider.provider().createObjectBuilder();
        obj2Builder.add("innerStr", "string val");
        obj2Builder.add("innerNum", 11.1d);
        final JsonObject obj2 = obj2Builder.build();

        JsonArrayBuilder array1Builder = JsonProvider.provider().createArrayBuilder();
        array1Builder.addNull().add(false).add(11L).add(BigDecimal.TEN).add("array STR value").add(obj2);
        JsonArray jsonArray1 = array1Builder.build();

        obj1builder.add("innerJsonObject", obj2);
        obj1builder.add("innerArrayObject", jsonArray1);

        final JsonObject obj1 = obj1builder.build();
        pojo.jsonObject = obj1;

        JsonArrayBuilder arrayBuilder = JsonProvider.provider().createArrayBuilder();
        arrayBuilder.add(obj1).add(true).add(obj2).add(101.0d).add(BigDecimal.TEN);
        pojo.jsonArray = arrayBuilder.build();


        String expected = "{\"jsonArray\":[{\"strVal\":\"string value\",\"numVal\":2.0,\"nullVal\":null,\"boolVal\":true,\"innerJsonObject\":{\"innerStr\":\"string val\",\"innerNum\":11.1},\"innerArrayObject\":[null,false,11,10,\"array STR value\",{\"innerStr\":\"string val\",\"innerNum\":11.1}]},true,{\"innerStr\":\"string val\",\"innerNum\":11.1},101.0,10],\"jsonObject\":{\"strVal\":\"string value\",\"numVal\":2.0,\"nullVal\":null,\"boolVal\":true,\"innerJsonObject\":{\"innerStr\":\"string val\",\"innerNum\":11.1},\"innerArrayObject\":[null,false,11,10,\"array STR value\",{\"innerStr\":\"string val\",\"innerNum\":11.1}]}}";
        final String actual = jsonb.toJson(pojo);
//        System.out.println("actual = " + actual);
        assertEquals(expected, actual);

        JsonpPojo result = jsonb.fromJson(expected, JsonpPojo.class);
        assertEquals(pojo.jsonObject, result.jsonObject);
        assertEquals(pojo.jsonArray, result.jsonArray);
    }

    @Test
    public void testJsonObject() {
        final JsonObjectBuilder objBuilder = JsonProvider.provider().createObjectBuilder();
        objBuilder.add("boolTrue", Boolean.TRUE).add("boolFalse", Boolean.FALSE)
                .addNull("null").add("str", "String");

        JsonArrayBuilder arrBuilder = JsonProvider.provider().createArrayBuilder();
        arrBuilder.add(11L).add(Boolean.FALSE).add(BigDecimal.TEN);
        objBuilder.add("array", arrBuilder);

        JsonObject object = objBuilder.build();

        String expected = "{\"boolTrue\":true,\"boolFalse\":false,\"null\":null,\"str\":\"String\",\"array\":[11,false,10]}";
        assertEquals(expected, jsonb.toJson(object));

        JsonObject result = jsonb.fromJson(expected, JsonObject.class);

        assertEquals(object, result);
    }

    @Test
    public void testJsonArray() {

        JsonArrayBuilder arrBuilder = JsonProvider.provider().createArrayBuilder();
        arrBuilder.add(11L).add(Boolean.FALSE).add(BigDecimal.TEN);

        JsonObjectBuilder objBuilder = JsonProvider.provider().createObjectBuilder();
        objBuilder.add("boolTrue", Boolean.TRUE).add("boolFalse", Boolean.FALSE)
                .addNull("null").add("str", "String");

        arrBuilder.add(objBuilder);

        JsonArray arr = arrBuilder.build();

        String expected = "[11,false,10,{\"boolTrue\":true,\"boolFalse\":false,\"null\":null,\"str\":\"String\"}]";
        assertEquals(expected, jsonb.toJson(arr));

        JsonArray result = jsonb.fromJson(expected, JsonArray.class);

        assertEquals(arr, result);
    }

}
