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

import org.eclipse.persistence.json.bind.internal.cdi.JsonbComponentInstanceCreatorFactory;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Jsonb interface.
 *
 * @author Dmitry Kornilov
 */
public class JsonBinding implements Jsonb {

    private final JsonbContext jsonbContext;

    JsonBinding(JsonBindingBuilder builder) {
        this.jsonbContext = new JsonbContext(new MappingContext(), builder.getConfig(), JsonbComponentInstanceCreatorFactory.getComponentInstanceCreator(),
                builder.getProvider().orElse(JsonProvider.provider()));
    }

    private <T> T deserialize(final Type type, final JsonParser parser, final Unmarshaller unmarshaller) {
        return new JsonbContextCommand<T>() {
            @Override
            protected T doInProcessingContext() {
                return unmarshaller.deserialize(type, parser);
            }
        }.execute(unmarshaller);
    }

    @Override
    public <T> T fromJson(String str, Class<T> type) throws JsonbException {
        final JsonParser parser = new JsonbRiParser(jsonbContext.getJsonProvider().createParser(new StringReader(str)));
        final Unmarshaller unmarshaller = new Unmarshaller(jsonbContext);
        return deserialize(type, parser, unmarshaller);
    }

    @Override
    public <T> T fromJson(String str, Type type) throws JsonbException {
        JsonParser parser = new JsonbRiParser(jsonbContext.getJsonProvider().createParser(new StringReader(str)));
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext);
        return deserialize(type, parser, unmarshaller);
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonbException {
        JsonParser parser = new JsonbRiParser(jsonbContext.getJsonProvider().createParser(reader));
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext);
        return deserialize(type, parser, unmarshaller);
    }

    @Override
    public <T> T fromJson(Reader reader, Type type) throws JsonbException {
        JsonParser parser = new JsonbRiParser(jsonbContext.getJsonProvider().createParser(reader));
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext);
        return deserialize(type, parser, unmarshaller);
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> clazz) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext);
        return deserialize(clazz, inputStreamParser(stream), unmarshaller);
    }

    @Override
    public <T> T fromJson(InputStream stream, Type type) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext);
        return deserialize(type, inputStreamParser(stream), unmarshaller);
    }

    private JsonParser inputStreamParser(InputStream stream) {
        return new JsonbRiParser(jsonbContext.getJsonProvider().createParserFactory(createJsonpProperties(jsonbContext.getConfig()))
                .createParser(stream,
                        Charset.forName((String) jsonbContext.getConfig().getProperty(JsonbConfig.ENCODING).orElse("UTF-8"))));
    }

    @Override
    public String toJson(Object object) throws JsonbException {
        StringWriter writer = new StringWriter();
        final JsonGenerator generator = writerGenerator(writer);
        new Marshaller(jsonbContext).marshall(object, generator);
        return writer.toString();
    }

    @Override
    public String toJson(Object object, Type type) throws JsonbException {
        StringWriter writer = new StringWriter();
        final JsonGenerator generator = writerGenerator(writer);
        new Marshaller(jsonbContext, type).marshall(object, generator);
        return writer.toString();
    }

    @Override
    public void toJson(Object object, Writer writer) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext);
        marshaller.marshall(object, writerGenerator(writer));
    }

    @Override
    public void toJson(Object object, Type type, Writer writer) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, type);
        marshaller.marshall(object, writerGenerator(writer));
    }

    private JsonGenerator writerGenerator(Writer writer) {
        Map<String, ?> factoryProperties = createJsonpProperties(jsonbContext.getConfig());
        return new IJsonJsonGeneratorDecorator(jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(writer));
    }

    @Override
    public void toJson(Object object, OutputStream stream) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext);
        marshaller.marshall(object, streamGenerator(stream));
    }

    @Override
    public void toJson(Object object, Type type, OutputStream stream) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, type);
        marshaller.marshall(object, streamGenerator(stream));
    }

    private JsonGenerator streamGenerator(OutputStream stream) {
        Map<String, ?> factoryProperties = createJsonpProperties(jsonbContext.getConfig());
        final String encoding = (String) jsonbContext.getConfig().getProperty(JsonbConfig.ENCODING).orElse("UTF-8");
        return new IJsonJsonGeneratorDecorator(jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(stream, Charset.forName(encoding)));
    }

    @Override
    public void close() throws Exception {
        jsonbContext.getComponentInstanceCreator().close();
    }

    /**
     * Propagates properties from JsonbConfig to JSONP generator / parser factories.
     *
     * @param jsonbConfig jsonb config
     * @return properties for JSONP generator / parser
     */
    protected Map<String, ?> createJsonpProperties(JsonbConfig jsonbConfig) {
        final Map<String, Object> factoryProperties = new HashMap<>();
        //JSONP 1.0 actually ignores the value, just checks the key is present. Only set if JsonbConfig.FORMATTING is true.
        jsonbConfig.getProperty(JsonbConfig.FORMATTING).ifPresent(value->{
            if (!(value instanceof Boolean)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_FORMATTING_ILLEGAL_VALUE));
            }
            if ((Boolean) value) {
                factoryProperties.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
            }
        });
        return factoryProperties;
    }
}
