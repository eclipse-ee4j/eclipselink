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

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.JsonbParser;
import org.eclipse.persistence.json.bind.internal.JsonbRiParser;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;

/**
 * Common implementation for JSONP Object and Array.
 *
 * TODO JSONP 1.1
 * This is subject to change after JSONP 1.1 release,
 * which will have support for getting JsonObject and JsonArray directly from JsonParser.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractJsonpDeserializer<T extends JsonValue> extends AbstractContainerDeserializer<T> {

    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected AbstractJsonpDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void deserializeInternal(JsonbParser parser, Unmarshaller context) {
        this.parserContext = moveToFirst(parser);
        while (parser.hasNext()) {
            final JsonParser.Event event = parser.next();
            final String lastKey = parserContext.getLastKeyName();
            switch (event) {
                case START_OBJECT:
                case START_ARRAY:
                    deserializeNext(parser, context);
                    break;
                case VALUE_STRING:
                    appendString(lastKey, parser.getString());
                    break;
                case VALUE_NUMBER:
                    appendNumber(lastKey, parser.getBigDecimal());
                    break;
                case VALUE_NULL:
                    appendNull(lastKey);
                    break;
                case VALUE_FALSE:
                    appendBoolean(lastKey, Boolean.FALSE);
                    break;
                case VALUE_TRUE:
                    appendBoolean(lastKey, Boolean.TRUE);
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    return;
                case KEY_NAME:
                    break;
                default:
                    throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event));
            }
        }
    }

    @Override
    protected JsonBindingModel getModel() {
        return getWrapperModel();

    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveToStartStructure();
        return parser.getCurrentLevel();
    }

    /**
     * Determine class mappings and create an instance of a new deserializer.
     * Currently processed deserializer is pushed to stack, for waiting till new object is finished.
     *
     * @param parser
     * @param context
     */
    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        Class<?> type = parserContext.getLastEvent() == JsonParser.Event.START_OBJECT ? JsonObject.class : JsonArray.class;
        final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext()).withType(type).build();
        appendResult(deserializer.deserialize(parser, context, type));
    }

    protected abstract void appendString(String key, String value);
    protected abstract void appendNumber(String key, BigDecimal value);
    protected abstract void appendBoolean(String key, Boolean value);
    protected abstract void appendNull(String key);
}
