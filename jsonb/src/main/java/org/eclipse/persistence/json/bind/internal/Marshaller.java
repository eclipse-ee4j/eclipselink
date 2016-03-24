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
import org.eclipse.persistence.json.bind.internal.internalOrdering.*;
import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.serializer.JsonpSerializers;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;
import org.eclipse.persistence.json.bind.model.TypeWrapper;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.json.stream.JsonGenerator;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;
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
public class Marshaller extends JsonTextProcessor {

    private static final Logger logger = Logger.getLogger(Marshaller.class.getName());

    /**
     *  Runtime types in marshaller are used for inferring runtime generic types of processed objects,
     *  to be able to match generic adapters {@link JsonbAdapter}. In case of Field {@code Box<T>} in Class {@code Pojo<T>}, T is inferred from
     *  runtimeTypeInfo and correctly adapted by a generic adapter for a {@code Box<T>}.
     */
    private Optional<RuntimeTypeInfo> runtimeTypeInfo;

    private Stack<PropertyModel> propertyModelStack = new Stack<>();

    private static final HashMap<String, PropOrderStrategy> orderStrategies = new HashMap<>();

    static {
        orderStrategies.put(PropertyOrderStrategy.LEXICOGRAPHICAL, new LexicographicalOrderStrategy());
        orderStrategies.put(PropertyOrderStrategy.REVERSE, new ReverseOrderStrategy());
        orderStrategies.put(PropertyOrderStrategy.ANY, new AnyOrderStrategy());
    }

    private final JsonGenerator jsonGenerator;

    private final StringWriter stringWriter;

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext
     */
    public Marshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
        this.stringWriter = new StringWriter();
        this.jsonGenerator = createGenerator(stringWriter);
        this.runtimeTypeInfo = Optional.empty();
    }

    /**
     * Helper constructor
     * @param jsonbContext Context of Jsonb
     * @param jsonGenerator created generator instance
     */
    public Marshaller(JsonbContext jsonbContext, JsonGenerator jsonGenerator) {
        super(jsonbContext);
        this.jsonGenerator = jsonGenerator;
        stringWriter = null;
        this.runtimeTypeInfo = Optional.empty();
    }

    /**
     * Creates Marshaller for generation to OutputStream
     * @param jsonbContext Context of Jsonb
     * @param outputStream stream to marshall into
     */
    public Marshaller(JsonbContext jsonbContext, OutputStream outputStream) {
        super(jsonbContext);
        this.jsonGenerator = createGenerator(outputStream);
        stringWriter = null;
        this.runtimeTypeInfo = Optional.empty();
    }

    private JsonGenerator createGenerator(Writer writer) {
        Map<String, ?> factoryProperties = createJsonpProperties(jsonbContext.getConfig());
        return jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(writer);
    }

    private JsonGenerator createGenerator(OutputStream outputStream) {
        Map<String, ?> factoryProperties = createJsonpProperties(jsonbContext.getConfig());
        final String encoding = (String) jsonbContext.getConfig().getProperty(JsonbConfig.ENCODING).orElse("UTF-8");
        return jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(outputStream, Charset.forName(encoding));
    }

    /**
     * Creates Marshaller for generation to Writer
     *
     * @param jsonbContext Context of Jsonb
     * @param writer writer to marshall into
     */
    public Marshaller(JsonbContext jsonbContext, Writer writer) {
        this(jsonbContext, jsonbContext.getJsonProvider().createGenerator(writer));
    }

    /**
     * Creates Marshaller for generation to String with runtime type information.
     *
     * @param jsonbContext Context of Jsonb
     * @param rootRuntimeType runtime type for generic information
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType) {
        super(jsonbContext);
        Objects.requireNonNull(rootRuntimeType);
        this.runtimeTypeInfo = Optional.of(new RuntimeTypeHolder(null, rootRuntimeType));
        this.stringWriter = new StringWriter();
        this.jsonGenerator = jsonbContext.getJsonProvider().createGenerator(stringWriter);
    }

    /**
     * Helper constructor
     *
     * @param jsonbContext Context of Jsonb
     * @param rootRuntimeType runtime type for generic information
     * @param jsonGenerator created generator instance
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType, JsonGenerator jsonGenerator) {
        super(jsonbContext);
        Objects.requireNonNull(rootRuntimeType);
        this.runtimeTypeInfo = Optional.of(new RuntimeTypeHolder(null, rootRuntimeType));
        this.jsonGenerator = jsonGenerator;
        stringWriter = null;
    }

    /**
     * Helper constructor.
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType, Writer writer) {
        this(jsonbContext, rootRuntimeType, jsonbContext.getJsonProvider().createGenerator(writer));
    }

    /**
     * Marshals a given object to a string.
     *
     * @param object object to marshal.
     * @return JSON representation of object
     */
    public String marshallToString(Object object) {
        new JsonbContextCommand() {
            @Override
            protected void doInJsonbContext() {
                marshallObject(Optional.empty(), object);
                jsonGenerator.close();
            }
        }.execute(jsonbContext);
        return stringWriter.toString();
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     *
     * @param object object to marshall
     */
    public void marshall(Object object) {
        new JsonbContextCommand() {
            @Override
            protected void doInJsonbContext() {
                marshallObject(Optional.empty(), object);
                jsonGenerator.close();
            }
        }.execute(jsonbContext);
    }


    /**
     * Marshals a given object.
     *
     * @param object object to marshal.
     * @param keyName name of a json key if any, nullable
     * @return JSON representation of object
     */
    @SuppressWarnings("unchecked")
    private void marshallObject(final Optional<String> keyName, final Object object) {
        final Type objectRuntimeType = runtimeTypeInfo.map(RuntimeTypeInfo::getRuntimeType).orElse(object.getClass());
        final Optional<JsonbAdapterInfo> adapterInfoOptional = getMarshallerAdapterInfo(objectRuntimeType);
        Optional<Object> adaptedValue = adapterInfoOptional.map(info->{
            logger.fine(Messages.getMessage(MessageKeys.ADAPTER_FOUND, info.getFromType().getTypeName(), info.getToType().getTypeName()));
            pushRuntimeType(info.getToType());
            try {
                return ((JsonbAdapter<Object, Object>) info.getAdapter()).adaptToJson(object);
            } catch (Exception e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, objectRuntimeType, info.getToType(), info.getAdapter().getClass()), e);
            }
        });
        final Object value = adaptedValue.orElse(object);

        if (value instanceof Optional) {
            marshallObject(keyName, ((Optional) value).get());

        } else if (value instanceof Collection) {
            marshallCollection(keyName, (Collection<?>) value);

        } else if (value instanceof Map) {
            marshallMap(keyName, (Map<?, ?>) value);

        } else if (value.getClass().isArray()) {
            marshallArray(keyName, value);

        } else if (JsonpSerializers.getInstance().supports(value)) {
            JsonpSerializers.getInstance().serialize(keyName, value, jsonGenerator);
        } else if (converter.supportsToJson(value.getClass())) {
            JsonpSerializers.getInstance().serialize(keyName, converter.toJson(value), jsonGenerator);
        } else {
            writeStartObject(keyName);
            marshallObjectProperties(value);
            jsonGenerator.writeEnd();
        }
        adapterInfoOptional.ifPresent(adapterInfo->popRuntimeType());
    }

    private Optional<JsonbAdapterInfo> getMarshallerAdapterInfo(Type runtimeType) {
        if (preventTypeWrapperAdaptingRecursion()) {
            return Optional.empty();
        }
        final AdapterMatcher matcher = AdapterMatcher.getInstance();
        return propertyModelStack.empty() ?
                matcher.getAdapterInfo(runtimeType) : matcher.getAdapterInfo(runtimeType, propertyModelStack.peek());
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

    private void marshallObjectProperties(Object object) {
        // Deal with inheritance
        final List<PropertyModel> allProperties = new LinkedList<>();
        for (Class clazz = object.getClass(); clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
            ClassModel classModel = JsonbContext.getInstance().getMappingContext().getOrCreateClassModel(clazz);
            final List<PropertyModel> properties = new ArrayList<>(classModel.getProperties().values());
            Optional<JsonbPropertyOrder> jsonbPropertyOrder = AnnotationIntrospector.getInstance().getJsonbPropertyOrderAnnotation(clazz);
            List<PropertyModel> filteredAndSorted;
            //Check if the class has JsonbPropertyOrder annotation defined
            if (!jsonbPropertyOrder.isPresent()) {
                //Sorting fields according to selected or default order
                String propertyOrderStrategy = JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY).isPresent() ? (String) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY).get() : PropertyOrderStrategy.LEXICOGRAPHICAL;
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
                .forEach((propertyModel) -> marshallProperty(object, propertyModel));
    }

    @SuppressWarnings("unchecked")
    private void marshallProperty(Object object, PropertyModel propertyModel) {
        Object value = propertyModel.getValue(object);
        if (value == null || isEmptyOptional(value)) {
            if (propertyModel.getCustomization().isNillable()) {
                jsonGenerator.writeNull(getJsonPropertyName(propertyModel.getCustomization().getJsonWriteName()));
            }
            return;
        }
        logger.finest("Serializing property: "+propertyModel.getPropertyName()+" in class "+propertyModel.getClassModel().getRawType().getSimpleName());
        propertyModelStack.push(propertyModel);
        pushRuntimeType(propertyModel.getPropertyType());
        marshallObject(Optional.of(getJsonPropertyName(propertyModel.getCustomization().getJsonWriteName())), value);
        popRuntimeType();
        propertyModelStack.pop();
    }

    private void marshallArray(Optional<String> keyName, Object array) {
        if (array.getClass().getComponentType().isPrimitive()) {
            marshallCollection(keyName, boxArray(array));
        } else {
            marshallCollection(keyName, Arrays.asList((Object[])array));
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

    private void marshallCollection(Optional<String> keyName, Collection<?> collection) {
        runtimeTypeInfo.ifPresent(rti -> pushRuntimeType(((ParameterizedType) rti.getRuntimeType()).getActualTypeArguments()[0]));
        if (keyName.isPresent()) {
            jsonGenerator.writeStartArray(keyName.get());
        } else {
            jsonGenerator.writeStartArray();
        }
        collection.stream().forEach((item)->{
            if (item == null || isEmptyOptional(item)) {
                jsonGenerator.writeNull();
                return;
            }
            marshallObject(Optional.empty(), item);
        });
        jsonGenerator.writeEnd();
        popRuntimeType();
    }

    private void marshallMap(Optional<String> keyName, Map<?, ?> map) {
        runtimeTypeInfo.ifPresent(rti -> pushRuntimeType(((ParameterizedType) rti.getRuntimeType()).getActualTypeArguments()[1]));
        writeStartObject(keyName);
        map.keySet().stream().forEach((key) -> {
            final String keysString = String.valueOf(key);
            final Object value = map.get(key);
            if (value == null || isEmptyOptional(value)) {
                jsonGenerator.writeNull(keysString);
                return;
            }
            marshallObject(Optional.of(keysString), value);
        });
        jsonGenerator.writeEnd();
        popRuntimeType();
    }

    private String getJsonPropertyName(String classPropertyName) {
        final PropertyNamingStrategy namingStrategy = JsonbContext.getInstance().getPropertyNamingStrategy();
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

    private void writeStartObject(Optional<String> key) {
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
}
