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
package org.eclipse.persistence.json.bind.internal;


import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.unmarshaller.JsonValueType;
import org.eclipse.persistence.json.bind.internal.unmarshaller.UnmarshallerItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.UnmarshallerItemBuilder;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * JSONB unmarshaller.
 * Uses {@link JsonParser} to navigate through json string.
 *
 * @author Roman Grigoriadi
 */
public class Unmarshaller extends ProcessingContext implements DeserializationContext {

    /**
     * Creates instance of unmarshaller.
     * @param jsonbContext context to use
     */
    public Unmarshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
    }

    private UnmarshallerItem<?> current;

            @Override
    public <T> T deserialize(Class<T> clazz, JsonParser parser) {
        final JsonParser.Event rootEvent = getRootEvent(parser);
        if (isValueEvent(rootEvent)) {
            return convertDefault(clazz, parser.getString());
                    }
        return deserializeItem(clazz, parser, rootEvent);
                }

    @Override
    public <T> T deserialize(Type type, JsonParser parser) {
        return deserializeItem(type, parser, getRootEvent(parser));
            }

    @SuppressWarnings("unchecked")
    private <T> T deserializeItem(Type type, JsonParser parser, JsonParser.Event event) {
        final UnmarshallerItem<?> item = new UnmarshallerItemBuilder(this, parser).withWrapper(current)
                .withType(type).withJsonValueType(JsonValueType.of(event)).build();
        item.deserialize();
        return (T) item.getInstance();
    }

    private JsonParser.Event getRootEvent(JsonParser parser) {
        if (parser.getLocation().getStreamOffset() == 0 || current.getLastEvent() == JsonParser.Event.KEY_NAME) {
            return parser.next();
        }
        return current.getLastEvent();
    }


    private <T> T convertDefault(Class<T> fromClass, String value) {
        if (!converter.supportsFromJson(fromClass)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CONVERSION_NOT_SUPPORTED, fromClass));
        }
        return converter.fromJson(value, fromClass, null);
        }

    /**
     * Get currently processed json item.
     * @return current item
     */
    public UnmarshallerItem<?> getCurrent() {
        return current;
    }

    /**
     * Set currently processed item.
     * @param current current item
     */
    public void setCurrent(UnmarshallerItem<?> current) {
        this.current = current;
    }

    private boolean isValueEvent(JsonParser.Event event) {
        switch (event) {
            case VALUE_FALSE:
            case VALUE_TRUE:
            case VALUE_NUMBER:
            case VALUE_STRING:
                return true;
            default: return false;
    }
    }

}
