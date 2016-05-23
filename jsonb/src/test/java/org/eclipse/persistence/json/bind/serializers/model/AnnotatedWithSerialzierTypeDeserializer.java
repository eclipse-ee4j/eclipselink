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

package org.eclipse.persistence.json.bind.serializers.model;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class AnnotatedWithSerialzierTypeDeserializer implements JsonbDeserializer<AnnotatedWithSerializerType> {
    /**
     * Deserialize an object from JSON.
     * Cursor of JsonParser is at START_OBJECT.
     *
     * @param parser Json parser
     * @param ctx    Deserialization context
     * @param rtType type of returned object
     * @return deserialized instance
     */
    @Override
    public AnnotatedWithSerializerType deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        AnnotatedWithSerializerType result = new AnnotatedWithSerializerType();
        parser.next(); parser.next();
        result.value = parser.getString();
        return result;
    }
}
