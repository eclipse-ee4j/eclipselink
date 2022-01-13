/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.jpa.json.model;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JSON value to String converter with method usage check callback.
 */
@Converter
public class JsonTestConverter implements AttributeConverter<JsonValue, String> {

    /** Method usage check callback. */
    public interface ConverterStatus {
        void usedToDatabaseColumn();
        void usedToEntityAttribute();
    }

    private static ConverterStatus status = null;

    /**
     * Set method usage check callback instance.
     *
     * @param status instance where to report method usage check result
     */
    public static void setConverterStatus(ConverterStatus status) {
        JsonTestConverter.status = status;
    }

    public JsonTestConverter() {
    }

    @Override
    public String convertToDatabaseColumn(JsonValue jsonValue) {
        status.usedToDatabaseColumn();
        final StringWriter sw = new StringWriter(128);
        try (final JsonWriter jw = Json.createWriter(sw)) {
            jw.write(jsonValue);
        }
        return sw.toString();
    }

    @Override
    public JsonValue convertToEntityAttribute(String jdbcValue) {
        status.usedToEntityAttribute();
        try (final JsonReader jr = Json.createReader(new StringReader(jdbcValue))) {
            return jr.readValue();
        }
    }

}