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

package org.eclipse.persistence.json.bind.serializers.model;

import org.eclipse.persistence.json.bind.adapters.PolymorphismAdapterTest;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class PolymorphicDeserializer implements JsonbDeserializer<PolymorphismAdapterTest.Animal> {
    @Override
    public PolymorphismAdapterTest.Animal deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Class<? extends PolymorphismAdapterTest.Animal> clazz = null;

        while (parser.hasNext()) {
            final JsonParser.Event next = parser.next();
            if (next == JsonParser.Event.KEY_NAME && parser.getString().equals("className")) {
                parser.next();
                try {
                    clazz = (Class<? extends PolymorphismAdapterTest.Animal>)Thread.currentThread().getContextClassLoader().loadClass(parser.getString());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            if (next == JsonParser.Event.KEY_NAME && parser.getString().equals("pojo")) {
                return ctx.deserialize(clazz, parser); //--- REENTRANT CALL---
            }
        }
        throw new IllegalStateException("animal not found!");
    }
}
