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


import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItemBuilder;
import org.eclipse.persistence.json.bind.internal.unmarshaller.JsonValueType;

import javax.json.Json;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * JSONB unmarshaller.
 * Uses {@link JsonParser} to navigate through json string.
 *
 * @author Roman Grigoriadi
 */
public class Unmarshaller extends JsonTextProcessor {

    private final JsonParser parser;

    private final Type rootType;

    /**
     * Stack of processed objects.
     * As events are discovered by {@link JsonParser} objects are created and pushed to this stack.
     */
    private CurrentItem<?> currentItem;

    /**
     * Currently processed JSON item key name.
     */
    private String currentFieldName;

    /**
     * Create unmarshaller instance with string JSON.
     * @param mappingContext Context of class mappings.
     * @param rootType Class of a root object to be created.
     * @param json JSON to parse.
     */
    public Unmarshaller(MappingContext mappingContext, JsonbConfig jsonbConfig, Type rootType, String json) {
        super(mappingContext, jsonbConfig);
        this.rootType = rootType;
        this.parser = Json.createParser(new StringReader(json));
    }

    /**
     * Create unmarshaller instance with input stream.
     * @param mappingContext Context of class mappings.
     * @param rootType Class of a root object to be created.
     * @param jsonStream input stream with JSON.
     */
    public Unmarshaller(MappingContext mappingContext, JsonbConfig jsonbConfig, Type rootType, InputStream jsonStream) {
        super(mappingContext, jsonbConfig);
        this.rootType = rootType;
        this.parser = Json.createParser(jsonStream);
    }

    /**
     * Create unmarshaller instance with readable.
     * @param mappingContext Context of class mappings.
     * @param rootType Class of a root object to be created.
     * @param readable readable JSON.
     */
    public Unmarshaller(MappingContext mappingContext, JsonbConfig jsonbConfig, Type rootType, Readable readable) {
        super(mappingContext, jsonbConfig);
        this.rootType = rootType;
        this.parser = Json.createParser(new ReadableReader(readable));
    }


    /**
     * Drive the {@link JsonParser} and processes its events.
     * @param <T> Type of result.
     * @return Result instance of a root object.
     */
    @SuppressWarnings("unchecked")
    public <T> T parse() {
        final JsonbContext context = new JsonbContext(jsonbConfig, mappingContext);
        return new JsonbContextCommand<T>() {
            @Override
            protected T doInJsonbContext() {
                while (parser.hasNext()) {
                    JsonParser.Event event = parser.next();
                    switch (event) {
                        case START_OBJECT:
                        case START_ARRAY:
                            onObjectStarted(JsonValueType.of(event));
                            break;
                        case END_OBJECT:
                        case END_ARRAY:
                            onObjectEnded();
                            break;
                        case VALUE_FALSE:
                        case VALUE_TRUE:
                        case VALUE_STRING:
                        case VALUE_NUMBER:
                            onValue(parser.getString(), JsonValueType.of(event));
                            break;
                        case VALUE_NULL:
                            onValue(null, JsonValueType.NULL);
                            break;
                        case KEY_NAME:
                            currentFieldName = parser.getString();
                            break;
                        default:
                            throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event));
                    }
                }
                return (T) currentItem.getInstance();
            }
        }.execute(context);
    }

    /**
     * Determine class mappings and create an instance of a new item.
     * Currently processed item is pushed to stack, for waiting till new object is finished.
     */
    private void onObjectStarted(JsonValueType jsonValueType) {
        //Create root item object when parser encounters first parenthesis.
        if (currentItem == null) {
            currentItem = new CurrentItemBuilder().withType(rootType != Object.class ? rootType : jsonValueType.getConversionType()).withJsonValueType(jsonValueType).build();
            return;
        }
        CurrentItem wrapper = currentItem;
        currentItem = wrapper.newItem(getClassPropertyName(currentFieldName), jsonValueType);
    }

    private void onObjectEnded() {
        //root object finished
        if (currentItem.getWrapper() == null) {
            return;
        }
        CurrentItem<?> finished = currentItem;
        finished.getWrapper().appendItem(finished);
        currentItem = finished.getWrapper();
    }

    /**
     * Create supported type from JSON value.
     * @param value A JSON value.
     */
    private void onValue(String value, JsonValueType type) {
        currentItem.appendValue(getClassPropertyName(currentFieldName), value, type);
    }

    private String getClassPropertyName(String jsonKeyName) {
        final PropertyNamingStrategy namingStrategy = JsonbContext.getPropertyNamingStrategy();
        return namingStrategy != null ? namingStrategy.toModelPropertyName(jsonKeyName) : jsonKeyName;
    }
}
