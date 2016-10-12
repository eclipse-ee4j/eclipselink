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

/**
 * Jsonb processing (serializing/deserialzing) context.
 * Instance is thread bound (in contrast to {@link JsonbContext}.
 *
 * @author Roman Grigoriadi
 */
public abstract class ProcessingContext {

    protected static final String NULL = "null";


    protected final JsonbContext jsonbContext;
    /**
     * Parent instance for marshaller and unmarshaller.
     *
     * @param jsonbContext context of Jsonb
     */
    public ProcessingContext(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
    }

    /**
     * Jsonb context.
     * @return jsonb context
     */
    public JsonbContext getJsonbContext() {
        return jsonbContext;
    }

    /**
     * Mapping context.
     * @return mapping context
     */
    public MappingContext getMappingContext() {
        return getJsonbContext().getMappingContext();
    }

}
