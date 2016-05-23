/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.serializer;

import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Jsonb serialization context.
 *
 * @author Roman Grigoriadi
 */
public class JsonbRiGenerator implements JsonGenerator {

    private final JsonGenerator jsonGenerator;

    public JsonbRiGenerator(JsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
    }


    @Override
    public JsonGenerator writeStartObject() {
        return jsonGenerator.writeStartObject();
    }

    @Override
    public JsonGenerator writeStartObject(String s) {
        return jsonGenerator.writeStartObject(s);
    }

    @Override
    public JsonGenerator writeStartArray() {
        return jsonGenerator.writeStartArray();
    }

    @Override
    public JsonGenerator writeStartArray(String s) {
        return jsonGenerator.writeStartArray(s);
    }

    @Override
    public JsonGenerator write(String s, JsonValue jsonValue) {
        return jsonGenerator.write(s, jsonValue);
    }

    @Override
    public JsonGenerator write(String s, String s1) {
        return jsonGenerator.write(s, s1);
    }

    @Override
    public JsonGenerator write(String s, BigInteger bigInteger) {
        return jsonGenerator.write(s, bigInteger);
    }

    @Override
    public JsonGenerator write(String s, BigDecimal bigDecimal) {
        return jsonGenerator.write(s, bigDecimal);
    }

    @Override
    public JsonGenerator write(String s, int i) {
        return jsonGenerator.write(s, i);
    }

    @Override
    public JsonGenerator write(String s, long l) {
        return jsonGenerator.write(s, l);
    }

    @Override
    public JsonGenerator write(String s, double v) {
        return jsonGenerator.write(s, v);
    }

    @Override
    public JsonGenerator write(String s, boolean b) {
        return jsonGenerator.write(s, b);
    }

    @Override
    public JsonGenerator writeNull(String s) {
        return jsonGenerator.writeNull(s);
    }

    @Override
    public JsonGenerator writeEnd() {
        return jsonGenerator.writeEnd();
    }

    @Override
    public JsonGenerator write(JsonValue jsonValue) {
        return jsonGenerator.write(jsonValue);
    }

    @Override
    public JsonGenerator write(String s) {
        return jsonGenerator.write(s);
    }

    @Override
    public JsonGenerator write(BigDecimal bigDecimal) {
        return jsonGenerator.write(bigDecimal);
    }

    @Override
    public JsonGenerator write(BigInteger bigInteger) {
        return jsonGenerator.write(bigInteger);
    }

    @Override
    public JsonGenerator write(int i) {
        return jsonGenerator.write(i);
    }

    @Override
    public JsonGenerator write(long l) {
        return jsonGenerator.write(l);
    }

    @Override
    public JsonGenerator write(double v) {
        return jsonGenerator.write(v);
    }

    @Override
    public JsonGenerator write(boolean b) {
        return jsonGenerator.write(b);
    }

    @Override
    public JsonGenerator writeNull() {
        return jsonGenerator.writeNull();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        jsonGenerator.flush();
    }
}
