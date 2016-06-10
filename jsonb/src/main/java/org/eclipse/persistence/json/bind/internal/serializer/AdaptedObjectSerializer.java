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

import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.DefaultCustomization;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.Customization;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;

/**
 * Serializer for adapted object.
 * Converts object using adapter first, than seriazizes result with standard process.
 * @author Roman Grigoriadi
 */
public class AdaptedObjectSerializer<T, A> implements CurrentItem<T>, JsonbSerializer<T> {

    private final static class AdaptedObjectSerializerModel implements SerializerBindingModel {

        private final SerializerBindingModel wrapperSerializerModel;

        private final Type adaptedType;

        public AdaptedObjectSerializerModel(SerializerBindingModel wrapperSerializerModel, Type adaptedType) {
            this.wrapperSerializerModel = wrapperSerializerModel;
            this.adaptedType = adaptedType;
        }

        @Override
        public String getJsonWriteName() {
            return wrapperSerializerModel.getJsonWriteName();
        }

        /**
         * Array context if root.
         */
        @Override
        public Context getContext() {
            return wrapperSerializerModel != null ?
                    wrapperSerializerModel.getContext() : Context.JSON_ARRAY;
        }

        /**
         * Get wrapper customization or empty if wrapper not present (root).
         */
        @Override
        public Customization getCustomization() {
            return wrapperSerializerModel != null ?
            wrapperSerializerModel.getCustomization() : new DefaultCustomization();
        }


        @Override
        public Type getType() {
            return adaptedType;
        }
    }

    private final SerializerBindingModel model;

    private final AdapterBinding adapterInfo;

    public AdaptedObjectSerializer(SerializerBuilder builder, AdapterBinding  adapter) {
        this.model = new AdaptedObjectSerializerModel(builder.getModel(), adapter.getToType());
        this.adapterInfo = adapter;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        try {
            final JsonbAdapter<T, A> adapter = (JsonbAdapter<T, A>) adapterInfo.getAdapter();
            A adapted = adapter.adaptToJson(obj);
            final JsonbSerializer<A> serializer = (JsonbSerializer<A>) new SerializerBuilder().withObjectClass(adapted.getClass()).withModel(model).withWrapper(this).build();
            serializer.serialize(adapted, generator, ctx);
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, adapterInfo.getBindingType(), adapterInfo.getToType(), adapterInfo.getAdapter().getClass()), e);
        }
    }

    /**
     * Class model containing property for this item.
     *
     * @return class model
     */
    @Override
    public ClassModel getClassModel() {
        return null;
    }

    /**
     * Item wrapper. Null only in case of a root item.
     *
     * @return wrapper item of this item
     */
    @Override
    public CurrentItem<?> getWrapper() {
        return null;
    }

    /**
     * Binding model of this item in wrapper. May be JavaBean property or a container like collection.
     *
     * @return wrapper model.
     */
    @Override
    public JsonBindingModel getWrapperModel() {
        return model;
    }

    /**
     * Runtime type of a class. Can be a class, ParameterizedType, or TypeVariable.
     * When a field or a class is declared including generic information this will return runtime type info.
     *
     * @return
     */
    @Override
    public Type getRuntimeType() {
        return null;
    }
}
