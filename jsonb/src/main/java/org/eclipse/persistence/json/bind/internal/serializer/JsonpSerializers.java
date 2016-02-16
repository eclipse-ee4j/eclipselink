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

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Roman Grigoriadi
 */
public class JsonpSerializers {

    private static final List<AbstractJsonpSerializer<?>> serializers = new ArrayList<>();

    private static final JsonpSerializers instance;

    static {
        instance = new JsonpSerializers();
        serializers.add(new JsonpStringSerializer());
        serializers.add(new JsonpIntegerSerializer());
        serializers.add(new JsonpLongSerializer());
        serializers.add(new JsonpDoubleSerializer());
        serializers.add(new JsonpShortSerializer());
        serializers.add(new JsonpByteSerializer());
        serializers.add(new JsonpBigDecimalSerializer());
        serializers.add(new JsonpBigIntegerSerializer());
        serializers.add(new JsonpBooleanSerializer());
        serializers.add(new JsonpOptionalLongSerializer());
        serializers.add(new JsonpOptionalIntSerializer());
        serializers.add(new JsonpOptionalDoubleSerializer());
        serializers.add(new JsonpJsonValueSerializer());
    }

    private JsonpSerializers() {
    }

    /**
     * Instance of Serializer delegating serialization by type.
     *
     * @return Serializer instance.
     */
    public static JsonpSerializers getInstance() {
        return instance;
    }

    /**
     * Value is instance of supported type for jsonp serialization.
     * @param value value to check
     * @param <T> Type of value
     * @return true if can be serialized
     */
    public <T> boolean supports(T value) {
        return value instanceof String ||
                value instanceof Short ||
                value instanceof Long ||
                value instanceof Integer ||
                value instanceof Double ||
                value instanceof BigDecimal ||
                value instanceof BigInteger ||
                value instanceof Boolean ||
                value instanceof JsonValue ||
                value instanceof Byte ||
                value instanceof OptionalInt ||
                value instanceof OptionalLong ||
                value instanceof OptionalDouble;
    }

    /**
     * Serializes value with key with {@link JsonGenerator}
     * @param keyName key name
     * @param value value to serialize
     * @param jsonGenerator jsongenerator to use
     * @param <T> type of value
     */
    public final <T> void serialize(Optional<String> keyName, T value, JsonGenerator jsonGenerator) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(jsonGenerator);
        if (!supports(value)) {
            throw new IllegalArgumentException(Messages.getMessage(MessageKeys.UNSUPPORTED_JSONP_SERIALIZER_VALUE, value.getClass()));
        }
        @SuppressWarnings("unchecked")
        AbstractJsonpSerializer<T> serializer = (AbstractJsonpSerializer<T>) getSerializer(value);
        if (keyName.isPresent()) {
            serializer.writeValue(keyName.get(), value, jsonGenerator);
        } else {
            serializer.writeValue(value, jsonGenerator);
        }
    }

    private <T> AbstractJsonpSerializer<?> getSerializer(T value) {
        for (AbstractJsonpSerializer<?> serializer : serializers) {
            if (serializer.supports(value)) {
                return serializer;
            }
        }
        throw new IllegalArgumentException(Messages.getMessage(MessageKeys.UNSUPPORTED_JSONP_SERIALIZER_VALUE, value.getClass()));
    }
}
