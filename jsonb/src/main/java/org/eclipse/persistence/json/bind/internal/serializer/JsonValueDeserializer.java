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

import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Deserializer for Json value null, false and true.
 * @author Roman Grigoriadi
 */
public class JsonValueDeserializer extends AbstractValueTypeDeserializer<JsonValue> {

    public JsonValueDeserializer(JsonBindingModel model) {
        super(JsonValue.class, model);
    }

    @Override
    public JsonValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        final JsonParser.Event next = parser.next();
        switch (next) {
            case VALUE_TRUE:
                return JsonValue.TRUE;
            case VALUE_FALSE:
                return JsonValue.FALSE;
            case VALUE_NULL:
                return JsonValue.NULL;
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Unknown JSON value: "+next));
        }
    }

    @Override
    protected JsonValue deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        throw new UnsupportedOperationException();
    }
}
