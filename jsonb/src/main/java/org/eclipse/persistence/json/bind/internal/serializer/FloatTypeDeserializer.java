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

import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class FloatTypeDeserializer extends AbstractNumberDeserializer<Float> {

    public FloatTypeDeserializer(JsonBindingModel model) {
        super(Float.class, model);
    }

    /**
     * Convert string value to object.
     *
     * @param jsonValue    json value
     * @param unmarshaller unmarshaller instance
     * @param rtType
     * @return deserialized object
     */
    @Override
    protected Float deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return deserializeForamtted(jsonValue, false).map(num->Float.parseFloat(num.toString()))
                .orElseGet(()->Float.parseFloat(jsonValue));
    }
}
