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
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.adapter.AdapterMatcher;
import org.eclipse.persistence.json.bind.internal.adapter.JsonbAdapterInfo;
import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class Marshaller extends JsonTextProcessor {

    private static final Logger logger = Logger.getLogger(Marshaller.class.getName());

    private static final String QUOTE = "\"";

    /**
     *  Runtime types in marshaller are used for inferring runtime generic types of processed objects,
     *  to be able to match generic adapters {@link JsonbAdapter}. In case of Field {@code Box<T>} in Class {@code Pojo<T>}, T is inferred from
     *  runtimeTypeInfo and correctly adapted by a generic adapter for a {@code Box<T>}.
     */
    private Optional<RuntimeTypeInfo> runtimeTypeInfo;

    private Stack<PropertyModel> propertyModelStack = new Stack<>();

    public Marshaller(MappingContext mappingContext, JsonbConfig jsonbConfig) {
        super(mappingContext, jsonbConfig);
        runtimeTypeInfo = Optional.empty();
    }

    public Marshaller(MappingContext mappingContext, JsonbConfig jsonbConfig, Type rootRuntimeType) {
        super(mappingContext, jsonbConfig);
        Objects.requireNonNull(rootRuntimeType);
        this.runtimeTypeInfo = Optional.of(new RuntimeTypeHolder(null, rootRuntimeType));
    }

    /**
     * Marshals a given object.
     *
     * @param object object to marshal.
     * @return JSON representation of object
     */
    public String marshall(Object object) {
        return new JsonbContextCommand<String>() {
            @Override
            protected String doInJsonbContext() {
                return marshallInternal(object);
            }
        }.execute(new JsonbContext(jsonbConfig, mappingContext));
    }

    public void marshall(Object object, Appendable appendable) throws IOException {
        appendable.append(new JsonbContextCommand<String>() {
            @Override
            protected String doInJsonbContext() {
                return marshallInternal(object);
            }
        }.execute(new JsonbContext(jsonbConfig, mappingContext)));
    }


    public void marshall(Object object, OutputStream stream) throws IOException {
        stream.write(new JsonbContextCommand<String>() {
            @Override
            protected String doInJsonbContext() {
                return marshallInternal(object);
            }
        }.execute(new JsonbContext(jsonbConfig, mappingContext)).getBytes("UTF-8"));
    }


    /**
     * Marshals a given object.
     *
     * @param object object to marshal.
     * @return JSON representation of object
     */
    @SuppressWarnings("unchecked")
    private String marshallInternal(final Object object) {
        if (object == null
                || object instanceof Optional && !((Optional) object).isPresent()
                || object instanceof OptionalInt && !((OptionalInt) object).isPresent()
                || object instanceof OptionalLong && !((OptionalLong) object).isPresent()
                || object instanceof OptionalDouble && !((OptionalDouble) object).isPresent()) {
            return NULL;

        }
        final Type objectRuntimeType = runtimeTypeInfo.map(RuntimeTypeInfo::getRuntimeType).orElse(object.getClass());
        final Optional<JsonbAdapterInfo> adapterInfoOptional = getMarshallerAdapterInfo(objectRuntimeType);
        Optional<Object> adaptedValue = adapterInfoOptional.map(info->{
            logger.fine(Messages.getMessage(MessageKeys.ADAPTER_FOUND, info.getFromType().getTypeName(), info.getToType().getTypeName()));
            pushRuntimeType(info.getToType());
            try {
                return ((JsonbAdapter<Object, Object>) info.getAdapter()).adaptFrom(object);
            } catch (Exception e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, objectRuntimeType, info.getToType(), info.getAdapter().getClass()), e);
            }
        });
        final Object value = adaptedValue.orElse(object);

        String result;
        if (value instanceof Optional) {
            result = marshallInternal(((Optional) value).get());

        } else if (value instanceof Collection) {
            result = marshallCollection((Collection<?>) value);

        } else if (value instanceof Map) {
            result = marshallMap((Map<?, ?>) value);

        } else if (value.getClass().isArray()) {
            result = marshallArray(value);

        } else if (converter.supportsToJson(value.getClass())) {
            result = converter.toJson(value);

        } else {
            result = marshallObject(value);
        }
        adapterInfoOptional.ifPresent(adapterInfo->popRuntimeType());
        return result;
    }

    private Optional<JsonbAdapterInfo> getMarshallerAdapterInfo(Type runtimeType) {
        final AdapterMatcher matcher = AdapterMatcher.getInstance();
        return propertyModelStack.empty() ?
                matcher.getAdapterInfo(runtimeType) : matcher.getAdapterInfo(runtimeType, propertyModelStack.peek());
    }

    private String marshallObject(Object object) {
        // Deal with inheritance
        final List<PropertyModel> allProperties = new LinkedList<>();
        for (Class clazz = object.getClass(); clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
            final List<PropertyModel> properties = new ArrayList<>(mappingContext.getOrCreateClassModel(clazz).getProperties().values());
            List<PropertyModel> filteredAndSorted = properties.stream().filter(propertyModel -> !allProperties.contains(propertyModel)).sorted().collect(toList());
            allProperties.addAll(0, filteredAndSorted);
        }

        return allProperties.stream()
                .map((model) -> marshallProperty(object, model))
                .filter(Objects::nonNull)
                .collect(joining(",", "{", "}"));
    }

    @SuppressWarnings("unchecked")
    private String marshallProperty(Object object, PropertyModel propertyModel) {
        logger.finest("Serializing property: "+propertyModel.getPropertyName()+" in class "+propertyModel.getClassModel().getRawType().getSimpleName());
        Object value = propertyModel.getValue(object);
        if (value == null) {
            return propertyModel.getCustomization().isNillable() ?
                    keyValue(propertyModel.getCustomization().getJsonWriteName(), "null") : null;
        }

        propertyModelStack.push(propertyModel);
        pushRuntimeType(propertyModel.getPropertyType());
        String result = null;
        if (!(value instanceof OptionalInt && !((OptionalInt) value).isPresent()
                || value instanceof OptionalLong && !((OptionalLong) value).isPresent()
                || value instanceof OptionalDouble && !((OptionalDouble) value).isPresent())) {
            result = keyValue(propertyModel.getCustomization().getJsonWriteName(), marshallInternal(value));
        }
        popRuntimeType();
        propertyModelStack.pop();
        return result;
    }

    private String marshallArray(Object array) {
        if (array.getClass().getComponentType().isPrimitive()) {
            return marshallCollection(boxArray(array));
        } else {
            return marshallCollection(Arrays.asList((Object[])array));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> boxArray(Object primitiveArray) {
        final int length = Array.getLength(primitiveArray);
        final Collection<T> result = new ArrayList<>(length);

        for (int i = 0; i < Array.getLength(primitiveArray); i++) {
            final Object wrapped = Array.get(primitiveArray, i);
            result.add((T) wrapped);
        }
        return result;
    }

    private String marshallCollection(Collection<?> collection) {
        runtimeTypeInfo.ifPresent(rti -> pushRuntimeType(((ParameterizedType) rti.getRuntimeType()).getActualTypeArguments()[0]));
        final String result = collection.stream()
                .map(this::marshallInternal)
                .collect(joining(",", "[", "]"));
        popRuntimeType();
        return result;
    }

    private String marshallMap(Map<?, ?> map) {
        runtimeTypeInfo.ifPresent(rti -> pushRuntimeType(((ParameterizedType) rti.getRuntimeType()).getActualTypeArguments()[1]));
        final String result = map.keySet().stream()
                .map((key) -> keyValue(key.toString(), marshallInternal(map.get(key))))
                .collect(joining(",", "{", "}"));
        popRuntimeType();
        return result;
    }

    private String keyValue(String key, Object value) {
        return quoteString(getJsonPropertyName(key)) + ":" + value;
    }

    private String getJsonPropertyName(String classPropertyName) {
        final PropertyNamingStrategy namingStrategy = JsonbContext.getPropertyNamingStrategy();
        return namingStrategy != null ? namingStrategy.toJsonPropertyName(classPropertyName) : classPropertyName;
    }

    private String quoteString(String string) {
        return String.join("", QUOTE, string, QUOTE);
    }

    private void pushRuntimeType(Type runtimeType) {
        runtimeTypeInfo.ifPresent(rti -> {
            Type resolvedType = ReflectionUtils.resolveType(rti, runtimeType);
            logger.finest(String.format("Pushed runtime type [%s], resolved from [%s].", resolvedType.getTypeName(), runtimeType.getTypeName()));
            runtimeTypeInfo = Optional.of(new RuntimeTypeHolder(rti, resolvedType));
        });
    }

    private void popRuntimeType() {
        runtimeTypeInfo.ifPresent(rti -> {
            logger.finest(String.format("Popping runtime type [%s]", rti.getRuntimeType().getTypeName()));
            this.runtimeTypeInfo = Optional.ofNullable(rti.getWrapper());
        });
    }
}
