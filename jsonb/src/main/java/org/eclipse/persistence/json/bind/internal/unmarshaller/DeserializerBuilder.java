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
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.AbstractSerializerBuilder;
import org.eclipse.persistence.json.bind.internal.ComponentMatcher;
import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.adapter.DeserializerBinding;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.serializer.AbstractValueTypeDeserializer;
import org.eclipse.persistence.json.bind.internal.serializer.DefaultSerializers;
import org.eclipse.persistence.json.bind.internal.serializer.SerializerProvider;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.PolymorphismAdapter;
import org.eclipse.persistence.json.bind.model.TypeWrapper;

import javax.json.JsonStructure;
import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.bind.serializer.JsonbDeserializer;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Builder for currently processed items by unmarshaller.
 *
 * @author Roman Grigoriadi
 */
public class DeserializerBuilder extends AbstractSerializerBuilder<DeserializerBuilder, JsonBindingModel> {

    /**
     * Value type of json event.
     */
    private JsonValueType jsonValueType;

    public DeserializerBuilder withJsonValueType(JsonValueType valueType) {
        this.jsonValueType = valueType;
        return this;
    }

    /**
     * Build an fully initialized item.
     *
     * @return built item
     */
    @SuppressWarnings("unchecked")
    public JsonbDeserializer<?> build() {
        runtimeType = resolveRuntimeType();
        Class<?> rawType = ReflectionUtils.getRawType(getRuntimeType());

        //First check if user deserializer is registered for such type
        final ComponentMatcher componentMatcher = ProcessingContext.getJsonbContext().getComponentMatcher();
        Optional<DeserializerBinding<?>> userDeserializer =
                componentMatcher.getDeserialzierBinding(getRuntimeType(), getModel());
        if (userDeserializer.isPresent() &&
                !(wrapper instanceof UserDeserializerDeserializer && ReflectionUtils.getRawType(wrapper.getRuntimeType()).isAssignableFrom(rawType))) {
            return new UserDeserializerDeserializer<>(this, userDeserializer.get().getJsonbDeserializer());
        }

        //Second user adapter is registered.
        final Optional<AdapterBinding> adapterInfoOptional = componentMatcher.getAdapterBinding(getRuntimeType(), getModel());
        Optional<Class> rawTypeOptional = adapterInfoOptional.map(adapterInfo->{
            runtimeType = adapterInfo.getToType();
            wrapper = new AdaptedObjectDeserializer<>(adapterInfoOptional.get(), (AbstractContainerDeserializer<?>) wrapper);
            return ReflectionUtils.getRawType(getRuntimeType());
        });
        rawType = rawTypeOptional.orElse(rawType);

        //In case of Base64 json value would be string and recognition by JsonValueType would not work
        if (isByteArray(rawType)) {
            String strategy = ProcessingContext.getJsonbContext().getBinaryDataStrategy();
            switch (strategy) {
                case BinaryDataStrategy.BYTE:
                    return new ByteArrayDeserializer(this);
                default:
                    return new ByteArrayBase64Deserializer(getModel());
            }
        }

        //Third deserializer is a supported value type that serializes to JSON_VALUE
        if (isJsonValueType()) {
            final Optional<AbstractValueTypeDeserializer<?>> supportedTypeDeserializer = getSupportedTypeDeserializer(rawType);
            if (!supportedTypeDeserializer.isPresent()) {
                throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, getRuntimeType()));
            }
            return wrapAdapted(adapterInfoOptional, supportedTypeDeserializer.get());
        }

        JsonbDeserializer<?> deserializer;
        if (jsonValueType == JsonValueType.ARRAY) {
            if (JsonStructure.class.isAssignableFrom(rawType)) {
                return wrapAdapted(adapterInfoOptional, new JsonArrayDeserializer(this));
            } else if (rawType.isArray() || getRuntimeType() instanceof GenericArrayType) {
                deserializer = createArrayItem(rawType.getComponentType());
                return wrapAdapted(adapterInfoOptional, deserializer);
            } else if (Collection.class.isAssignableFrom(rawType)) {
                deserializer = new CollectionDeserializer<>(this);
                return wrapAdapted(adapterInfoOptional, deserializer);
            } else {
                throw new JsonbException("Can't deserialize JSON array into: " + getRuntimeType());
            }
        } else if(jsonValueType == JsonValueType.OBJECT) {
            if (JsonStructure.class.isAssignableFrom(rawType)) {
                return wrapAdapted(adapterInfoOptional, new JsonObjectDeserializer(this));
            } else if (Map.class.isAssignableFrom(rawType)) {
                final JsonbDeserializer<?> mapDeserializer = new MapDeserializer(this);
                return wrapAdapted(adapterInfoOptional, mapDeserializer);
            } else if (rawType.isInterface()) {
                throw new JsonbException(Messages.getMessage(MessageKeys.INFER_TYPE_FOR_UNMARSHALL, rawType.getName()));
            } else {
                if (adapterInfoOptional.isPresent()) {
                    runtimeType = adapterInfoOptional.get().getToType();
                    rawType = ReflectionUtils.getRawType(getRuntimeType());
                }

                classModel = getClassModel(rawType);

                if (TypeWrapper.class.isAssignableFrom(rawType)) {
                    return wrapAdapted(adapterInfoOptional,  new TypeWrapperDeserializer<>(this,
                            ((PolymorphismAdapter<?>) adapterInfoOptional.get().getAdapter()).getAllowedClasses()));
                }
                deserializer = new ObjectDeserializer<>(this);
                return wrapAdapted(adapterInfoOptional, deserializer);
            }
        }
        throw new JsonbException("unresolved type for deserialization: " + getRuntimeType());
    }

    private boolean isJsonValueType() {
        switch (jsonValueType) {
            case NULL:
            case BOOLEAN:
            case NUMBER:
            case STRING:
                return true;
            case OBJECT:
            case ARRAY:
                return false;
            default:
                throw new JsonbException("Unknown value type: " + jsonValueType);
        }
    }


    private Optional<AbstractValueTypeDeserializer<?>> getSupportedTypeDeserializer(Class<?> rawType) {
        final Optional<? extends SerializerProvider> supportedTypeDeserializerOptional = DefaultSerializers.getInstance().findValueSerializerProvider(rawType);
        if (supportedTypeDeserializerOptional.isPresent()) {
            return Optional.of(supportedTypeDeserializerOptional.get().provideDeserializer(getModel()));
        }
        return Optional.empty();
    }

    private JsonbDeserializer<?> wrapAdapted(Optional<AdapterBinding> adapterInfoOptional, JsonbDeserializer<?> item) {
        final Optional<JsonbDeserializer<?>> adaptedDeserializerOptional = adapterInfoOptional.map(adapterInfo -> {
            setAdaptedItemCaptor((AdaptedObjectDeserializer)wrapper, item);
            return (JsonbDeserializer<?>)wrapper;
        });
        return adaptedDeserializerOptional.orElse(item);
    }

    private <T,A> void setAdaptedItemCaptor(AdaptedObjectDeserializer<T,A> decoratorItem, JsonbDeserializer<T> adaptedItem) {
        decoratorItem.setAdaptedItem(adaptedItem);
    }

    private Type resolveRuntimeType() {
        Type toResolve = genericType != null ? genericType : getModel().getType();
        //Map Unknown objects to java.util.Map
        if (toResolve == Object.class) {
            return jsonValueType.getConversionType();
        }
        return ReflectionUtils.resolveType(wrapper, toResolve);
    }

    /**
     * Instance is not created in case of array items, because, we don't know how long it should be
     * till parser ends parsing.
     */
    private JsonbDeserializer<?> createArrayItem(Class<?> componentType) {
        if (componentType == byte.class) {
            return new ByteArrayDeserializer(this);
        } else if (componentType == short.class) {
            return new ShortArrayDeserializer(this);
        } else if (componentType == int.class) {
            return new IntArrayDeserializer(this);
        } else if (componentType == long.class) {
            return new LongArrayDeserializer(this);
        } else if (componentType == float.class) {
            return new FloatArrayDeserializer(this);
        } else if (componentType == double.class) {
            return new DoubleArrayDeserializer(this);
        } else {
            return new ObjectArrayDeserializer(this);
        }
    }

    private boolean isByteArray(Class<?> rawType) {
        return rawType.isArray() && rawType.getComponentType() == Byte.TYPE;
    }
}
