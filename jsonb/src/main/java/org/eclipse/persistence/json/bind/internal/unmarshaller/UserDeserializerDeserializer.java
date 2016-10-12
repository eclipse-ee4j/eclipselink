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
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.serializer.UserDeserializerParser;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * Item for processing types, to which deserializer is bound.
 *
 * @author Roman Grigoriadi
 */
public class UserDeserializerDeserializer<T> extends AbstractContainerDeserializer<T> {

    private JsonbDeserializer<?> deserializer;

    private T deserializerResult;

    /**
     * Create instance of current item with its builder.
     * Contains user provided component for custom deserialization.
     * Decorates calls to JsonParser, with validation logic so user can't left parser cursor
     * in wrong position after returning from deserializer.
     *
     * @param builder
     */
    protected UserDeserializerDeserializer(DeserializerBuilder builder, JsonbDeserializer<?> deserializer) {
        super(builder);
        this.deserializer = deserializer;
    }

    @Override
    public void appendResult(Object result) {
        //ignore internal deserialize() call in custom deserializer
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getInstance(Unmarshaller unmarshaller) {
        return deserializerResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeInternal(JsonbParser parser, Unmarshaller context) {
        parserContext = moveToFirst(parser);
        final UserDeserializerParser userDeserializerParser = new UserDeserializerParser(parser);
        deserializerResult = (T) deserializer.deserialize(userDeserializerParser, context, getRuntimeType());
        userDeserializerParser.advanceParserToEnd();
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        throw new UnsupportedOperationException("Not supported for user deserializer");
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_OBJECT);
        return parser.getCurrentLevel();
    }

    /**
     * Binding model for current deserializer.
     *
     * @return model
     */
    @Override
    protected JsonBindingModel getModel() {
        return getWrapperModel();
    }
}