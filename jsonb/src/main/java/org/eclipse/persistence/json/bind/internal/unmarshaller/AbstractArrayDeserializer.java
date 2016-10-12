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

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.JsonbParser;
import org.eclipse.persistence.json.bind.internal.JsonbRiParser;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.GenericArrayType;
import java.util.List;

/**
 * Common array unmarshalling item implementation.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractArrayDeserializer<T> extends AbstractContainerDeserializer<T> implements EmbeddedItem {

    /**
     * Runtime type class of an array.
     */
    protected final Class<?> componentClass;

    private final JsonBindingModel model;

    protected AbstractArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
        if (getRuntimeType() instanceof GenericArrayType) {
            componentClass = ReflectionUtils.resolveRawType(this, ((GenericArrayType) getRuntimeType()).getGenericComponentType());
        } else {
            componentClass = ReflectionUtils.getRawType(getRuntimeType()).getComponentType();
        }
        this.model = new ContainerModel(componentClass, resolveContainerModelCustomization(componentClass, builder.getJsonbContext()));
    }

    /**
     * Binding model for current deserializer.
     *
     * @return model
     */
    @Override
    protected JsonBindingModel getModel() {
        return model;
    }

    @Override
    public void appendResult(Object result) {
        appendCaptor(result);
    }

    @SuppressWarnings("unchecked")
    private <X> void appendCaptor(X value) {
        ((List<X>) getItems()).add(value);
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext()).withType(componentClass)
                .withModel(model).build();
        appendResult(deserializer.deserialize(parser, context, componentClass));
    }

    protected abstract List<?> getItems();

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_ARRAY);
        return parser.getCurrentLevel();
    }
}