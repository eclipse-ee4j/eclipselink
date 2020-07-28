/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Iaroslav Savytskyi - 2.6 - initial implementation
package org.eclipse.persistence.internal.oxm.record.json;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.XMLReaderAdapter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Reader for JSR-353 stream (StAX) parser.
 * <p/>
 * Could be instantiated with {@link JsonParserReader.JsonParserReaderBuilder#build()};
 */
public final class JsonParserReader extends XMLReaderAdapter {

    private final JsonParser parser;
    private final JsonStructureReader structureReader;

    /**
     * Parsing stack
     */
    private final Deque<JsonStructureBuilder> stack = new ArrayDeque<>();

    /**
     * Private constructor
     * Use {@link JsonParserReader.JsonParserReaderBuilder} to instantiate the class;
     */
    private JsonParserReader(JsonParserReaderBuilder b) {
        this.parser = b.parser;
        if (b.resultClass == null)
            this.structureReader = new JsonStructureReader(b.um);
        else
            this.structureReader = new JsonStructureReader(b.um, b.resultClass);
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        if (null == input) {
            doParsing(parser);
            return;
        }

        if (null != input.getCharacterStream()) {
            doParsing(Json.createParser(input.getCharacterStream()));
            return;
        }

        InputStream inputStream = null;
        try {
            if (null != (inputStream = input.getByteStream())) {
                doParsing(Json.createParser(inputStream));
                return;
            }

            try {
                URL url = new URL(input.getSystemId());
                inputStream = url.openStream();
            } catch (MalformedURLException malformedURLException) {
                try {
                    inputStream = new FileInputStream(input.getSystemId());
                } catch (FileNotFoundException fileNotFoundException) {
                    throw malformedURLException;
                }
            }
            doParsing(Json.createParser(inputStream));
        } catch (JsonException je) {
            throw XMLMarshalException.unmarshalException(je);
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }

    @Override
    public void parse(String systemId) {
        try {
            parse(new InputSource(systemId));
        } catch (IOException | SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    private void doParsing(JsonParser parser) throws SAXException, IOException {
        JsonStructureBuilder builder = null;
        while (parser.hasNext()) {
            builder = parseEvent(parser);
        }
        assert builder != null;
        JsonStructure jsonStructure = builder.build();
        structureReader.parseRoot(jsonStructure);
    }

    private JsonStructureBuilder parseEvent(JsonParser jp) throws SAXException {
        JsonParser.Event e = jp.next();

        JsonStructureBuilder top = stack.peek();
        switch (e) {
            case START_ARRAY: {
                JsonStructureBuilder b = new ArrayBuilder(Json.createArrayBuilder());
                stack.push(b);
                break;
            }
            case START_OBJECT: {
                JsonStructureBuilder b = new ObjectBuilder(Json.createObjectBuilder());
                stack.push(b);
                break;
            }
            case KEY_NAME: {
                top.setKey(jp.getString());
                break;
            }
            case VALUE_STRING: {
                top.add(jp.getString());
                break;
            }
            case VALUE_NUMBER: {
                top.add(jp.getBigDecimal());
                break;
            }
            case VALUE_TRUE: {
                top.add(Boolean.TRUE);
                break;
            }
            case VALUE_FALSE: {
                top.add(Boolean.FALSE);
                break;
            }
            case VALUE_NULL: {
                top.addNull();
                break;
            }
            case END_ARRAY:
            case END_OBJECT: { // adding build element to the upper one
                JsonStructureBuilder b = stack.pop();
                top = stack.peek();
                if (top != null)
                    top.add(b.build());
                return b;
            }
            default:
                throw new IllegalStateException("Unhandled event: " + e);
        }
        return null;
    }

    // ******************************** Redirecting requests to JsonStructureReader *******************************
    public boolean isNullRepresentedByXsiNil(AbstractNullPolicy nullPolicy) {
        return true;
    }

    @Override
    public Object convertValueBasedOnSchemaType(Field xmlField, Object value, ConversionManager conversionManager, AbstractUnmarshalRecord record) {
        return structureReader.convertValueBasedOnSchemaType(xmlField, value, conversionManager, record);
    }

    @Override
    public char getNamespaceSeparator() {
        return structureReader.getNamespaceSeparator();
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return structureReader.getErrorHandler();
    }

    @Override
    public ExtendedContentHandler getContentHandler() {
        return structureReader.getContentHandler();
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        structureReader.setContentHandler(contentHandler);
    }

    @Override
    public boolean isInCollection() {
        return structureReader.isInCollection();
    }

    @Override
    public MediaType getMediaType() {
        return Constants.APPLICATION_JSON;
    }

    @Override
    public boolean isNamespaceAware() {
        return structureReader.isNamespaceAware();
    }
    // ************************************************************************************************************

    /**
     * JsonStructure builder
     */
    private static interface JsonStructureBuilder {
        JsonStructure build();

        void add(JsonValue value);

        void add(String value);

        void add(BigDecimal value);

        void add(boolean value);

        void addNull();

        void setKey(String key);
    }

    /**
     * Builder for JsonParserReader
     */
    public static final class JsonParserReaderBuilder {
        private final JsonParser parser;
        private Unmarshaller um;
        private Class resultClass;

        public JsonParserReaderBuilder(JsonParser parser) {
            this.parser = parser;
        }

        public JsonParserReaderBuilder setUnmarshaller(Unmarshaller um) {
            this.um = um;
            return this;
        }

        public JsonParserReaderBuilder setResultClass(Class resultClass) {
            this.resultClass = resultClass;
            return this;
        }

        public JsonParserReader build() {
            if (parser == null)
                throw new NullPointerException("JsonParser can't be null");
            return new JsonParserReader(this);
        }
    }

    private static final class ObjectBuilder implements JsonStructureBuilder {
        private final JsonObjectBuilder b;
        private String key;

        public ObjectBuilder(JsonObjectBuilder b) {
            this.b = b;
        }

        @Override
        public JsonStructure build() {
            return b.build();
        }

        @Override
        public void add(JsonValue value) {
            b.add(key, value);
        }

        @Override
        public void add(String value) {
            b.add(key, value);
        }

        @Override
        public void add(BigDecimal value) {
            b.add(key, value);
        }

        @Override
        public void add(boolean value) {
            b.add(key, value);
        }

        @Override
        public void addNull() {
            b.addNull(key);
        }

        @Override
        public void setKey(String key) {
            this.key = key;
        }
    }

    private static final class ArrayBuilder implements JsonStructureBuilder {
        private final JsonArrayBuilder b;

        public ArrayBuilder(JsonArrayBuilder b) {
            this.b = b;
        }

        @Override
        public JsonStructure build() {
            return b.build();
        }

        @Override
        public void add(JsonValue value) {
            b.add(value);
        }

        @Override
        public void add(String value) {
            b.add(value);
        }

        @Override
        public void add(BigDecimal value) {
            b.add(value);
        }

        @Override
        public void add(boolean value) {
            b.add(value);
        }

        @Override
        public void addNull() {
            b.addNull();
        }

        @Override
        public void setKey(String key) {
            // noop
        }
    }
}
