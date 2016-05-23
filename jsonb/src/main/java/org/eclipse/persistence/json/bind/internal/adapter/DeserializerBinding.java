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

package org.eclipse.persistence.json.bind.internal.adapter;

import javax.json.bind.serializer.JsonbDeserializer;
import java.lang.reflect.Type;

/**
 * Component containing deserializer.
 *
 * @author Roman Grigoriadi
 */
public class DeserializerBinding<T> extends AbstractComponentBinding {

    private final JsonbDeserializer<T> jsonbDeserializer;

    public DeserializerBinding(Type bindingType, JsonbDeserializer<T> jsonbDeserializer) {
        super(bindingType);
        this.jsonbDeserializer = jsonbDeserializer;
    }

    /**
     * Deserializer if any.
     *
     * @return deserializer
     */
    public JsonbDeserializer<T> getJsonbDeserializer() {
        return jsonbDeserializer;
    }

    @Override
    public Class<?> getComponentClass() {
        return jsonbDeserializer.getClass();
    }
}
