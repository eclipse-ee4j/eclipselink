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

import org.eclipse.persistence.json.bind.internal.JsonbParser;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.OptionalDouble;

/**
 * @author David Král
 */
public class OptionalDoubleTypeDeserializer extends AbstractValueTypeDeserializer<OptionalDouble> {

    public OptionalDoubleTypeDeserializer(JsonBindingModel model) {
        super(OptionalDouble.class, model);
    }

    @Override
    public OptionalDouble deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        final JsonParser.Event next = ((JsonbParser) parser).moveToValue();
        if (next == JsonParser.Event.VALUE_NULL) {
            return OptionalDouble.empty();
        }
        String value = parser.getString();
        return deserialize(value, (Unmarshaller) ctx, rtType);
    }

    @Override
    protected OptionalDouble deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return OptionalDouble.of(Double.parseDouble(jsonValue));
    }
}
