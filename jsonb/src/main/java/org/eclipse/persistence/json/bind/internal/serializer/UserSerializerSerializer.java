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

package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.JsonContext;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializes an object with user defined serializer.
 *
 * @author Roman Grigoriadi
 */
public class UserSerializerSerializer<T> implements JsonbSerializer<T> {

    private final JsonbSerializer<T> userSerializer;

    private JsonBindingModel model;

    /**
     * Create instance of current item with its builder.
     *
     * @param model model
     * @param userSerializer user serializer
     */
    public UserSerializerSerializer(JsonBindingModel model, JsonbSerializer<T> userSerializer) {
        this.model = model;
        this.userSerializer = userSerializer;
    }


    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        if (model.getContext() == JsonContext.JSON_OBJECT) {
            generator.writeKey(model.getWriteName());
        }
        userSerializer.serialize(obj, generator, ctx);
    }

}
