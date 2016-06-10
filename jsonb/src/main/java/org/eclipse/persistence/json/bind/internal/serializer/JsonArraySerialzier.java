/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializes JSON array to json.
 *
 * @author Roman Grigoriadi
 */
public class JsonArraySerialzier extends AbstractJsonpSerializer<JsonArray> {

    protected JsonArraySerialzier(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void serializeInternal(JsonArray obj, JsonGenerator generator, SerializationContext ctx) {
        for (JsonValue value : obj) {
            generator.write(value);
        }
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartArray();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartArray(key);
    }
}
