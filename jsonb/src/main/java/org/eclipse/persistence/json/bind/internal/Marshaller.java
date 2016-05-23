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

import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.adapter.SerializerBinding;
import org.eclipse.persistence.json.bind.internal.internalOrdering.AnnotationOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.AnyOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.LexicographicalOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.PropOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.ReverseOrderStrategy;
import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.serializer.JsonpSerializers;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.Customization;
import org.eclipse.persistence.json.bind.model.PropertyModel;
import org.eclipse.persistence.json.bind.model.TypeWrapper;
import org.eclipse.persistence.json.bind.serializer.JsonbRiGenerator;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Stack;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * TODO current marshaller implementation will throw StackOverflowError with very large object trees.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class Marshaller extends ProcessingContext implements SerializationContext {

    private enum Context {
        ROOT,
        PROPERTY,
        ARRAY;
    }

    private static final Logger logger = Logger.getLogger(Marshaller.class.getName());

    /**
     *  Runtime types in marshaller are used for inferring runtime generic types of processed objects,
     *  to be able to match generic adapters {@link JsonbAdapter}. In case of Field {@code Box<T>} in Class {@code Pojo<T>}, T is inferred from
     *  runtimeTypeInfo and correctly adapted by a generic adapter for a {@code Box<T>}.
     */
    private Optional<RuntimeTypeInfo> runtimeTypeInfo;

    private final Stack<PropertyModel> propertyModelStack = new Stack<>();

    //TODO this is temporary helper.
    //TODO Current state of marshaller is getting messy and hard to maintain, splitting to several smaller controllers is needed.
    private final Stack<Context> contextStack = new Stack<>();

    private static final HashMap<String, PropOrderStrategy> orderStrategies = new HashMap<>();

    static {
        orderStrategies.put(PropertyOrderStrategy.LEXICOGRAPHICAL, new LexicographicalOrderStrategy());
        orderStrategies.put(PropertyOrderStrategy.REVERSE, new ReverseOrderStrategy());
        orderStrategies.put(PropertyOrderStrategy.ANY, new AnyOrderStrategy());
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     * @param rootRuntimeType type of root object
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType) {
        super(jsonbContext);
        this.runtimeTypeInfo = Optional.of(new RuntimeTypeHolder(null, rootRuntimeType));
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     */
    public Marshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
        this.runtimeTypeInfo = Optional.empty();
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     *
     * @param object object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshall(Object object, JsonGenerator jsonGenerator) {
        new JsonbContextCommand<Void>() {
            @Override
            protected Void doInProcessingContext() {
                marshallAndClose(object, jsonGenerator);
                return null;
    }
        }.execute(this);
    }

    private void marshallAndClose(Object object, JsonGenerator jsonGenerator) {
                contextStack.push(Context.ROOT);
        marshallObject(Optional.empty(), object, jsonGenerator);
                contextStack.pop();
                jsonGenerator.close();
            }

            @Override
    public <T> void serialize(String key, T object, JsonGenerator generator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(object);
        if (converter.supportsToJson(object.getClass())) {
            generator.write(key, converter.toJson(object, null));
            return;
            }
        marshallObject(Optional.of(key), object, generator);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        if (converter.supportsToJson(object.getClass())) {
            generator.write(converter.toJson(object, null));
            return;
        }
        marshallObject(Optional.empty(), object, generator);
    }

    /**
     * Marshals a given object.
     *
     * @param object object to marshal.
     * @param keyName name of a json key if any, nullable
     * @return JSON representation of object
     */
    @SuppressWarnings("unchecked")
    public <T> void marshallObject(final Optional<String> keyName, final T object, JsonGenerator jsonGenerator) {
        final Type objectRuntimeType = runtimeTypeInfo.map(RuntimeTypeInfo::getRuntimeType).orElse(object.getClass());

        Optional<SerializerBinding<T>> serializerOptional = getSerializer(objectRuntimeType);
        if (serializerOptional.isPresent()) {
            writeStartObject(keyName, jsonGenerator);
            serializerOptional.get().getJsonbSerializer().serialize(object, new JsonbRiGenerator(jsonGenerator), this);
            jsonGenerator.writeEnd();
            return;
        }

        final Optional<AdapterBinding> adapterInfoOptional = getMarshallerAdapterInfo(objectRuntimeType);
        Optional<Object> adaptedValue = adapterInfoOptional.map(info->{
            logger.fine(Messages.getMessage(MessageKeys.ADAPTER_FOUND, info.getBindingType().getTypeName(), info.getToType().getTypeName()));
            pushRuntimeType(info.getToType());
            try {
                return ((JsonbAdapter<Object, Object>) info.getAdapter()).adaptToJson(object);
            } catch (Exception e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, objectRuntimeType, info.getToType(), info.getAdapter().getClass()), e);
            }
        });

        final Object value = adaptedValue.orElse(object);

        if (value instanceof Optional) {
            marshallObject(keyName, ((Optional) value).get(), jsonGenerator);

        } else if (value instanceof Collection) {
            marshallCollection(keyName, (Collection<?>) value, jsonGenerator);

        } else if (value instanceof Map) {
            marshallMap(keyName, (Map<?, ?>) value, jsonGenerator);

        } else if (value.getClass().isArray()
                && !(value instanceof byte[])) {
            marshallArray(keyName, value, jsonGenerator);

        } else if (JsonpSerializers.getInstance().supports(value)) {
            JsonpSerializers.getInstance().serialize(keyName, value, jsonGenerator);
        } else if (converter.supportsToJson(value.getClass())) {
            JsonpSerializers.getInstance().serialize(keyName,
                    converter.toJson(value, getCustomization(value.getClass())),
                    jsonGenerator);
        } else {
            writeStartObject(keyName, jsonGenerator);
            marshallObjectProperties(value, jsonGenerator);
            jsonGenerator.writeEnd();
        }
        adapterInfoOptional.ifPresent(adapterInfo->popRuntimeType());
    }

    private <T> Optional<SerializerBinding<T>> getSerializer(Type runtimeType) {
        return ProcessingContext.getJsonbContext().getComponentMatcher().getSerialzierBinding(runtimeType,
                propertyModelStack.empty() ? null : propertyModelStack.peek());
    }

    private Optional<AdapterBinding> getMarshallerAdapterInfo(Type runtimeType) {
        if (preventTypeWrapperAdaptingRecursion()) {
            return Optional.empty();
        }
        final ComponentMatcher matcher = ProcessingContext.getJsonbContext().getComponentMatcher();
        return matcher.getAdapterBinding(runtimeType,
                propertyModelStack.empty() ? null : propertyModelStack.peek());
    }

    /**
     * Class of T instance in type wrapper is adapted to TypeWrapper, which would cause a StackOverflow
     * due to cycling adapting recursion.
     *
     * TODO consider more generic approach for detecting adapter recursion
     */
    private boolean preventTypeWrapperAdaptingRecursion() {
        return !propertyModelStack.empty() && propertyModelStack.peek().getClassModel().getRawType() == TypeWrapper.class;
    }

    private void marshallObjectProperties(Object object, JsonGenerator jsonGenerator) {
        // Deal with inheritance
        final List<PropertyModel> allProperties = new LinkedList<>();
        final MappingContext mappingContext = ProcessingContext.getMappingContext();
        for (Class clazz = object.getClass(); clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
            ClassModel classModel = mappingContext.getOrCreateClassModel(clazz);
            final List<PropertyModel> properties = new ArrayList<>(classModel.getProperties().values());
            Optional<JsonbPropertyOrder> jsonbPropertyOrder = AnnotationIntrospector.getInstance().getJsonbPropertyOrderAnnotation(clazz);
            List<PropertyModel> filteredAndSorted;
            //Check if the class has JsonbPropertyOrder annotation defined
            if (!jsonbPropertyOrder.isPresent()) {
                //Sorting fields according to selected or default order
                String propertyOrderStrategy = ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY).isPresent() ? (String) ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY).get() : PropertyOrderStrategy.LEXICOGRAPHICAL;
                if (!orderStrategies.containsKey(propertyOrderStrategy)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, propertyOrderStrategy));
                }
                filteredAndSorted = orderStrategies.get(propertyOrderStrategy).sortProperties(properties);
            } else {
                filteredAndSorted = new AnnotationOrderStrategy(jsonbPropertyOrder.get().value()).sortProperties(properties);
            }
            filteredAndSorted = filteredAndSorted.stream().filter(propertyModel -> !allProperties.contains(propertyModel)).collect(toList());
            allProperties.addAll(0, filteredAndSorted);
        }

        allProperties.stream()
                .forEach((propertyModel) -> marshallProperty(object, propertyModel, jsonGenerator));
    }

    @SuppressWarnings("unchecked")
    private void marshallProperty(Object object, PropertyModel propertyModel, JsonGenerator jsonGenerator) {
        Object value = propertyModel.getValue(object);
        if (value == null || isEmptyOptional(value)) {
            if (propertyModel.getCustomization().isNillable()) {
                jsonGenerator.writeNull(getJsonPropertyName(propertyModel.getCustomization().getJsonWriteName()));
            }
            return;
        }
        logger.finest("Serializing property: "+propertyModel.getPropertyName()+" in class "+propertyModel.getClassModel().getRawType().getSimpleName());
        propertyModelStack.push(propertyModel);
        contextStack.push(Context.PROPERTY);
        pushRuntimeType(propertyModel.getPropertyType());
        marshallObject(Optional.of(getJsonPropertyName(propertyModel.getCustomization().getJsonWriteName())), value, jsonGenerator);
        popRuntimeType();
        propertyModelStack.pop();
        contextStack.pop();
    }

    private void marshallArray(Optional<String> keyName, Object array, JsonGenerator jsonGenerator) {
        if (array.getClass().getComponentType().isPrimitive()) {
            marshallCollection(keyName, boxArray(array), jsonGenerator);
        } else {
            marshallCollection(keyName, Arrays.asList((Object[])array), jsonGenerator);
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

    private void marshallCollection(Optional<String> keyName, Collection<?> collection, JsonGenerator jsonGenerator) {
        runtimeTypeInfo.ifPresent(rti -> pushRuntimeType(((ParameterizedType) rti.getRuntimeType()).getActualTypeArguments()[0]));
        if (keyName.isPresent()) {
            jsonGenerator.writeStartArray(keyName.get());
        } else {
            jsonGenerator.writeStartArray();
        }
        contextStack.push(Context.ARRAY);
        collection.stream().forEach((item)->{
            if (item == null || isEmptyOptional(item)) {
                jsonGenerator.writeNull();
                return;
            }
            marshallObject(Optional.empty(), item, jsonGenerator);
        });
        contextStack.pop();
        jsonGenerator.writeEnd();
        popRuntimeType();
    }

    private void marshallMap(Optional<String> keyName, Map<?, ?> map, JsonGenerator jsonGenerator) {
        runtimeTypeInfo.ifPresent(rti -> pushRuntimeType(((ParameterizedType) rti.getRuntimeType()).getActualTypeArguments()[1]));
        writeStartObject(keyName, jsonGenerator);
        map.keySet().stream().forEach((key) -> {
            final String keysString = String.valueOf(key);
            final Object value = map.get(key);
            if (value == null || isEmptyOptional(value)) {
                jsonGenerator.writeNull(keysString);
                return;
            }
            marshallObject(Optional.of(keysString), value, jsonGenerator);
        });
        jsonGenerator.writeEnd();
        popRuntimeType();
    }

    private String getJsonPropertyName(String classPropertyName) {
        final PropertyNamingStrategy namingStrategy = ProcessingContext.getJsonbContext().getPropertyNamingStrategy();
        return namingStrategy != null ? namingStrategy.toJsonPropertyName(classPropertyName) : classPropertyName;
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

    private void writeStartObject(Optional<String> key, JsonGenerator jsonGenerator) {
        if (key.isPresent()) {
            jsonGenerator.writeStartObject(key.get());
        } else {
            jsonGenerator.writeStartObject();
        }
    }

    private <T> boolean isEmptyOptional(T value) {
        return value instanceof Optional<?> && !((Optional<?>) value).isPresent()
                || value instanceof OptionalInt && !((OptionalInt) value).isPresent()
                || value instanceof OptionalLong && !((OptionalLong) value).isPresent()
                || value instanceof OptionalDouble && !((OptionalDouble) value).isPresent();
    }

    private Customization getCustomization(Class<?> marshalledType) {
        final Context currentContext = contextStack.peek();
        switch (currentContext) {
            case ROOT:
            case ARRAY:
                ClassModel model = jsonbContext.getMappingContext().getClassModel(marshalledType);
                return model != null ? model.getClassCustomization() : null;
            case PROPERTY:
                return propertyModelStack.peek().getCustomization();
            default:
                throw new IllegalStateException("Illegal marshaller context: "+ currentContext);
        }
    }
}
