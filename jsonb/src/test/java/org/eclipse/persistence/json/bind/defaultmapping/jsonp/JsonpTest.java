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

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.junit.Test;

import javax.json.*;
import javax.json.bind.Jsonb;

import static org.junit.Assert.assertEquals;

/**
 * Default mapping JSONP integration tests.
 *
 * @author Dmitry Kornilov
 */
public class JsonpTest {

    public static class JsonValueWrapper {
        public JsonValue jsonValue;

        public JsonValueWrapper(JsonValue jsonValue) {
            this.jsonValue = jsonValue;
        }
    }

    @Test
    public void testMarshallJsonObject() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("name", "home")
                .add("city", "Prague")
                .build();

        assertEquals("{\"name\":\"home\",\"city\":\"Prague\"}", jsonb.toJson(jsonObject));
    }

    @Test
    public void testMarshallJsonArray() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArray jsonArray = factory.createArrayBuilder()
                .add(1)
                .add(2)
                .build();

        assertEquals("{\"jsonValue\":[1,2]}", jsonb.toJson(new JsonValueWrapper(jsonArray)));
    }

    @Test
    public void testMarshallJsonValue() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{\"jsonValue\":true}", jsonb.toJson(new JsonValueWrapper(JsonValue.TRUE)));
    }

    @Test
    public void testMarshallJsonNumber() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{\"jsonValue\":10}", jsonb.toJson(new JsonValueWrapper(new JsonpLong(10))));
    }

    @Test
    public void testMarshallJsonString() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{\"jsonValue\":\"hello\"}", jsonb.toJson(new JsonValueWrapper(new JsonpString("hello"))));
    }

    // TODO more tests, more sophisticated array and structure tests
}
