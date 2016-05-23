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
 * Roman Grigoriadi
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;

/**
 * Jsonb processing (serializing/deserialzing) context.
 * Instance is thread bound (in contrast to {@link JsonbContext}.
 *
 * @author Roman Grigoriadi
 */
public abstract class ProcessingContext {

    private static final ThreadLocal<ProcessingContext> instances = new ThreadLocal<>();

    protected static final String NULL = "null";

    protected final TypeConverter converter;

    protected final JsonbContext jsonbContext;
    /**
     * Parent instance for marshaller and unmarshaller.
     *
     * @param jsonbContext context of Jsonb
     */
    public ProcessingContext(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
        this.converter = ConvertersMapTypeConverter.getInstance();
    }


    static void setInstance(ProcessingContext context) {
        if (instances.get() != null) {
            throw new IllegalStateException("JsonbContext already set!");
        }
        instances.set(context);
    }

    static void removeInstance() {
        if (instances.get() == null) {
            throw new IllegalStateException("JsonbContext is not set!");
        }
        instances.remove();
    }

    /**
     * Instance of processing context.
     * @return instance
     */
    public static ProcessingContext getInstance() {
        return instances.get();
    }

    /**
     * Jsonb context.
     * @return jsonb context
     */
    public static JsonbContext getJsonbContext() {
        return getInstance().jsonbContext;
    }

    /**
     * Mapping context.
     * @return mapping context
     */
    public static MappingContext getMappingContext() {
        return getJsonbContext().getMappingContext();
    }

}
