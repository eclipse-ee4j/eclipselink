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
package org.eclipse.persistence.mappings.converters;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Session;

public class JsonTypeConverter implements Converter {

    public JsonTypeConverter() {
    }

    @Override
    public Object convertObjectValueToDataValue(Object jsonValue, Session session) {
        if (jsonValue instanceof JsonValue) {
            final StringWriter sw = new StringWriter(128);
            try (final JsonWriter jw = Json.createWriter(sw)) {
                jw.write((JsonValue)jsonValue);
            }
            return sw.toString();
        }
        throw new IllegalArgumentException("Source object is not an instance of JsonValue");
    }

    @Override
    public Object convertDataValueToObjectValue(Object jdbcValue, Session session) {
        if (jdbcValue instanceof String) {
            try (final JsonReader jr = Json.createReader(new StringReader((String)jdbcValue))) {
                return jr.readValue();
            }
        }
        throw new IllegalArgumentException("Source object is not an instance of String");
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        if (mapping.isDirectToFieldMapping()) {
            if (((AbstractDirectMapping)mapping).getFieldClassification() == null) {
                ((AbstractDirectMapping)mapping).setFieldClassification(String.class);
            }
        }
    }
}
