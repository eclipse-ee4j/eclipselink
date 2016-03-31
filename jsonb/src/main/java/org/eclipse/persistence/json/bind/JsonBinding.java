/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind;

import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.internal.MappingContext;
import org.eclipse.persistence.json.bind.internal.Marshaller;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.internal.cdi.JsonbComponentInstanceCreatorFactory;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;
import javax.json.spi.JsonProvider;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

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

    @Override
    public <T> T fromJson(String str, Class<T> type) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext, type, str);
        return unmarshaller.parse();
    }

    @Override
    public <T> T fromJson(String str, Type runtimeType) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext, runtimeType, str);
        return unmarshaller.parse();
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext, type, reader);
        return unmarshaller.parse();
    }

    @Override
    public <T> T fromJson(Reader reader, Type runtimeType) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext, runtimeType, reader);
        return unmarshaller.parse();
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> type) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext, type, stream);
        return unmarshaller.parse();
    }

    @Override
    public <T> T fromJson(InputStream stream, Type runtimeType) throws JsonbException {
        Unmarshaller unmarshaller = new Unmarshaller(jsonbContext, runtimeType, stream);
        return unmarshaller.parse();
    }

    @Override
    public String toJson(Object object) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext);
        return marshaller.marshallToString(object);
    }

    @Override
    public String toJson(Object object, Type runtimeType) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, runtimeType);
        return marshaller.marshallToString(object);
    }

    @Override
    public void toJson(Object object, Writer writer) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, writer);
        marshaller.marshall(object);
    }

    @Override
    public void toJson(Object object, Type runtimeType, Writer writer) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, runtimeType, writer);
        marshaller.marshall(object);
    }

    @Override
    public void toJson(Object object, OutputStream stream) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, stream);
        marshaller.marshall(object);
    }

    @Override
    public void toJson(Object object, Type runtimeType, OutputStream stream) throws JsonbException {
        final Marshaller marshaller = new Marshaller(jsonbContext, runtimeType, stream);
        marshaller.marshall(object);
    }

    @Override
    public void close() throws Exception {
        jsonbContext.getComponentInstanceCreator().close();
    }
}
