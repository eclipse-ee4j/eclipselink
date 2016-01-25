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
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbException;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Default types for JSON values when type cannot be inferred by reflection.
 * Fields defined as Object.class types, or raw generic fields are such cases.
 *
 * @author Roman Grigoriadi
 */
public enum JsonValueType {
    BOOLEAN(Boolean.class),
    NUMBER(BigDecimal.class),
    STRING(String.class),
    ARRAY(ArrayList.class),
    OBJECT(HashMap.class),
    NULL(null);

    JsonValueType(Class<?> supportedByType) {
        this.supportedByType = supportedByType;
    }

    private final Class<?> supportedByType;

    public Class<?> getConversionType() {
        return supportedByType;
    }

    public static JsonValueType of(JsonParser.Event event) {
        switch (event) {
            case VALUE_FALSE:
            case VALUE_TRUE:
                return BOOLEAN;
            case VALUE_STRING:
                return STRING;
            case VALUE_NUMBER:
                return NUMBER;
            case VALUE_NULL:
                return NULL;
            case START_ARRAY:
                return ARRAY;
            case START_OBJECT:
                return OBJECT;
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event.name()));
        }
    }
}
