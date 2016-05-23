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

import org.eclipse.persistence.json.bind.internal.unmarshaller.DeserializerItem;

import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;

/**
 * Decorator for JSONP parser. Adds some checks for parser cursor manipulation methods.
 *
 * @author Roman Grigoriadi
 */
public class JsonbRiParser implements JsonParser {

    private final JsonParser jsonParser;

    private final DeserializerItem<?> current;

    /**
     * Constructs an instance with parser and context.
     * @param current currently processed item
     */
    public JsonbRiParser(DeserializerItem<?> current, JsonParser parser) {
        this.current = current;
        this.jsonParser = parser;
    }

    /**
     * Returns true only if cursor would not be advanced beyond end of currently processed structure.
     *
     * @return true if next is available
     */
    @Override
    public boolean hasNext() {
        return current.hasNext();
    }

    /**
     * Delegates next to currently processed item object.
     *
     * @return next parser event
     */
    @Override
    public Event next() {
        return current.next();
    }

    @Override
    public String getString() {
        return jsonParser.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return jsonParser.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return jsonParser.getInt();
    }

    @Override
    public long getLong() {
        return jsonParser.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return jsonParser.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return jsonParser.getLocation();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }
}
