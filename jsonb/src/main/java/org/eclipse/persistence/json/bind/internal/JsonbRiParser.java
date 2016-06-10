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

import javax.json.bind.JsonbException;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Stack;

/**
 * Decorator for JSONP parser used by JSONB.
 * @author Roman Grigoriadi
 */
public class JsonbRiParser implements JsonParser, JsonbParser {

    /**
     * State holder for current json structure level.
     */
    public static class LevelContext {
        private final LevelContext parent;
        private JsonParser.Event lastEvent;
        private String lastStringValue;
        private String lastKeyName;
        private boolean parsed;

        public LevelContext(LevelContext parent) {
            this.parent = parent;
        }

        public JsonParser.Event getLastEvent() {
            return lastEvent;
        }

        private void setLastEvent(JsonParser.Event lastEvent) {
            this.lastEvent = lastEvent;
        }

        public String getLastStringValue() {
            return lastStringValue;
        }

        private void setLastStringValue(String lastStringValue) {
            this.lastStringValue = lastStringValue;
        }

        public String getLastKeyName() {
            return lastKeyName;
        }

        private void setLastKeyName(String lastKeyName) {
            this.lastKeyName = lastKeyName;
        }

        public LevelContext getParent() {
            return parent;
        }

        public boolean isParsed() {
            return parsed;
        }

        private void finish() {
            if (parsed) {
                throw new IllegalStateException("Level already parsed");
            }
            parsed = true;
        }
    }

    private final JsonParser jsonParser;

    private final Stack<LevelContext> level = new Stack<>();

    public JsonbRiParser(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public boolean hasNext() {
        return  jsonParser.hasNext();
    }

    public long getLong() {
        return jsonParser.getLong();
    }

    public int getInt() {
        return jsonParser.getInt();
    }

    public JsonParser.Event next() {
        final JsonParser.Event next = jsonParser.next();
        LevelContext current = level.empty() ? null : level.peek();
        if (current != null) {
            current.setLastEvent(next);
        }
        switch (next) {
            case START_ARRAY:
            case START_OBJECT:
                final LevelContext newLevel = new LevelContext(current);
                newLevel.setLastEvent(next);
                level.push(newLevel);
                break;
            case END_ARRAY:
            case END_OBJECT:
                level.pop().finish();
                break;
            case KEY_NAME:
                getCurrentLevel().setLastKeyName(jsonParser.getString());
                break;
            default:
                break;
        }
        return next;
    }

    public boolean isIntegralNumber() {
        return jsonParser.isIntegralNumber();
    }

    public BigDecimal getBigDecimal() {
        return jsonParser.getBigDecimal();
    }

    public JsonLocation getLocation() {
        return jsonParser.getLocation();
    }

    public void close() {
        jsonParser.close();
    }

    public String getString() {
        final String value = jsonParser.getString();
        getCurrentLevel().setLastStringValue(value);
        return value;
    }

    @Override
    public void moveTo(JsonParser.Event required) {
        if (!level.empty() && level.peek().getLastEvent() == required) {
            return;
        }

        final Event next = next();
        if (next == required) {
            return;
        }

        throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Event " + required + " not found." + getLastDataMsg()));
    }

    @Override
    public Event moveToValue() {
        return moveTo(Event.VALUE_STRING, Event.VALUE_NUMBER, Event.VALUE_FALSE, Event.VALUE_TRUE, Event.VALUE_NULL);
    }

    @Override
    public Event moveToStartStructure() {
        return moveTo(Event.START_OBJECT, Event.START_ARRAY);
    }

    private Event moveTo(Event... events) {
        if (!level.empty() && contains(events, level.peek().getLastEvent())) {
            return level.peek().getLastEvent();
        }

        final Event next = next();
        if (contains(events, next)) {
            return next;
        }

        throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Parser event ["+Arrays.toString(events)+"] not found." + getLastDataMsg()));
    }

    private boolean contains(Event[] events, Event candidate) {
        for (Event event : events) {
            if (event == candidate) {
                return true;
            }
        }
        return false;
    }

    private String getLastDataMsg() {
        StringBuilder builder = new StringBuilder();
        final LevelContext currentLevel = getCurrentLevel();
        builder.append(" Last data: [").append("EVENT: ").append(currentLevel.getLastEvent()).append(" KEY_NAME: ")
                .append(currentLevel.getLastKeyName()).append("]");
        return builder.toString();
    }

    @Override
    public LevelContext getCurrentLevel() {
        return level.peek();
    }


    @Override
    public void skipJsonStructure() {
        final LevelContext currentLevel = level.peek();
        switch (currentLevel.getLastEvent()) {
            case START_ARRAY:
            case START_OBJECT:
                while (!currentLevel.isParsed()) {
                    next();
                }
                return;
            default:
                return;
        }
    }
}
