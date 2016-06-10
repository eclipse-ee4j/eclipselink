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

package org.eclipse.persistence.json.bind.serializer;

import org.eclipse.persistence.json.bind.internal.JsonbParser;
import org.eclipse.persistence.json.bind.internal.JsonbRiParser;
import org.eclipse.persistence.json.bind.internal.unmarshaller.UserDeserializerDeserializer;

import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;
import java.util.Stack;

/**
 * Decorator for JSONP parser. Adds some checks for parser cursor manipulation methods.
 *
 * @author Roman Grigoriadi
 */
public class UserDeserializerParser implements JsonbParser {

    private final JsonbParser jsonbParser;

    /**
     * Remembered parser level, which is applied to user deserializer structure.
     */
    private final JsonbRiParser.LevelContext level;

    /**
     * Constructs an instance with parser and context.
     * @param parser jsonb parser to decorate
     */
    public UserDeserializerParser(JsonbParser parser) {
        this.jsonbParser = parser;
        level = jsonbParser.getCurrentLevel();
    }

    /**
     * JsonParser in JSONB runtime is shared with user components, if user lefts cursor half way in progress
     * it must be advanced artificially to the end of JSON structure representing deserialized object.
     */
    public void advanceParserToEnd() {
        while (!level.isParsed() && jsonbParser.hasNext()) {
            next();
        }
    }

    @Override
    public boolean hasNext() {
        return !level.isParsed() && jsonbParser.hasNext();
    }

    @Override
    public Event next() {
        if (level.isParsed()) {
            throw new IllegalStateException("Parser level data inconsistent.");
        }
        return jsonbParser.next();
    }


    @Override
    public String getString() {
        return jsonbParser.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return jsonbParser.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return jsonbParser.getInt();
    }

    @Override
    public long getLong() {
        return jsonbParser.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return jsonbParser.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return jsonbParser.getLocation();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    /**
     * Moves parser to required event, if current event is equal to required does nothing.
     *
     * @param event required event
     */
    @Override
    public void moveTo(Event event) {
        jsonbParser.moveTo(event);
    }

    /**
     * Moves parser cursor to any JSON value.
     */
    @Override
    public Event moveToValue() {
        return jsonbParser.moveToValue();
    }

    /**
     * Moves parser cursor to START_OBJECT or START_ARRAY.
     */
    @Override
    public Event moveToStartStructure() {
        return jsonbParser.moveToStartStructure();
    }

    /**
     * Current level of JsonbRiParser.
     *
     * @return current level
     */
    @Override
    public JsonbRiParser.LevelContext getCurrentLevel() {
        return jsonbParser.getCurrentLevel();
    }

    /**
     * Skips a value or a structure.
     * If current event is START_ARRAY or START_OBJECT, whole structure is skipped to end.
     */
    @Override
    public void skipJsonStructure() {
        jsonbParser.skipJsonStructure();
    }
}
