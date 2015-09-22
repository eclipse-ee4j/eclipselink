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

import org.eclipse.persistence.json.bind.internal.Context;
import org.eclipse.persistence.json.bind.internal.Marshaller;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Implementation of Jsonb interface.
 *
 * @author Dmitry Kornilov
 */
public class JsonBinding implements Jsonb {
    private final Context context;

    JsonBinding(JsonBindingBuilder builder) {
        // TODO set internal properties of the context from builder
        context = new Context();
    }

    @Override
    public <T> T fromJson(String str, Class<T> type) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(String str, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(Readable readable, Class<T> type) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(Readable readable, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> type) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(InputStream stream, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public String toJson(Object object) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        return marshaller.marshall(object);
    }

    @Override
    public String toJson(Object object, Type runtimeType) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        return marshaller.marshall(object, runtimeType);
    }

    @Override
    public void toJson(Object object, Appendable appendable) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        try {
            marshaller.marshall(object, appendable);
        } catch (IOException e) {
            throw new JsonbException("Cannot marshall object.", e);
        }
    }

    @Override
    public void toJson(Object object, Type runtimeType, Appendable appendable) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        try {
            marshaller.marshall(object, runtimeType, appendable);
        } catch (IOException e) {
            throw new JsonbException("Cannot marshall object.", e);
        }
    }

    @Override
    public void toJson(Object object, OutputStream stream) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        try {
            marshaller.marshall(object, stream);
        } catch (IOException e) {
            throw new JsonbException("Cannot marshall object.", e);
        }
    }

    @Override
    public void toJson(Object object, Type runtimeType, OutputStream stream) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        try {
            marshaller.marshall(object, runtimeType, stream);
        } catch (IOException e) {
            throw new JsonbException("Cannot marshall object.", e);
        }
    }
}
