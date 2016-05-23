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

import org.eclipse.persistence.json.bind.serializer.JsonbRiParser;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.util.Stack;

/**
 * Item for processing types, to which deserializer is bound.
 *
 * @author Roman Grigoriadi
 */
public class DeserializerItem<T> extends AbstractUnmarshallerItem<T> implements UnmarshallerItem<T> {

    private JsonbDeserializer<?> deserializer;

    private T deserializerResult;

    private final Stack<JsonParser.Event> level = new Stack<>();

    /**
     * Create instance of current item with its builder.
     * Contains user provided component for custom deserialization.
     * Decorates calls to JsonParser, with validation logic so user can't left parser cursor
     * in wrong position after returning from deserializer.
     *
     * @param builder
     */
    protected DeserializerItem(UnmarshallerItemBuilder builder, JsonbDeserializer<?> deserializer) {
        super(builder);
        this.deserializer = deserializer;
        level.push(getLastEvent());
    }

    @Override
    public void appendItem(UnmarshallerItem<?> valueItem) {
        //ignore internal deserialize() call in custom deserializer
    }

    @Override
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        throw new UnsupportedOperationException("operation is not supported");
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        throw new UnsupportedOperationException("operation is not supported");
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getInstance() {
        return deserializerResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeInternal() {
        deserializerResult = (T) deserializer.deserialize(new JsonbRiParser(this, getParser()), getContext(), getRuntimeType());
        advanceParserToEnd();
        if (getWrapper() != null) {
            ((UnmarshallerItem) getWrapper()).appendItem(this);
        }
    }

    /**
     * JsonParser in JSONB runtime is shared with user components, if user lefts cursor half way in progress
     * it must be advanced artificially to the end of JSON structure representing deserialized object.
     */
    private void advanceParserToEnd() {
        while (hasNext()) {
            next();
        }
    }

    /**
     * Decorates default next, maintains level of cursor.
     */
    @Override
    public JsonParser.Event next() {
        final JsonParser.Event next = super.next();
        switch (next) {
            case START_OBJECT:
            case START_ARRAY:
                level.push(next);
                break;
            case END_OBJECT:
            case END_ARRAY:
                level.pop();
                break;
            default:
                break;
        }
        return next;
    }

    /**
     * True if cursor of JsonParser has moved out of this item.
     * @return
     */
    @Override
    protected boolean parsed() {
        return level.empty();
    }

}