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

import org.eclipse.persistence.json.bind.internal.MappingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbException;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Builder for currently processed items by unmarshaller.
 *
 * @author Roman Grigoriadi
 */
public class CurrentItemBuilder {

    private final MappingContext mappingContext;

    /**
     * Not null with an exception of a root item.
     */
    private CurrentItem<?> wrapper;

    /**
     * Type is used when field model is not present.
     * In case of root, or embedded objects such as collections.
     */
    private Type genericType;

    /**
     * Runtime type resolved after expanding type variables and wildcards.
     */
    private Type runtimeType;

    /**
     * Common case of a field in arbitrary object.
     */
    private PropertyModel propertyModel;

    /**
     * Key name, that precedes object start in JSON string.
     */
    private String jsonKeyName;

    /**
     * Type of json event, in case of creating new item can be either ARRAY, or OBJECT.
     */
    private JsonValueType jsonValueType;

    /**
     * Actual instance of an object to be processed.
     */
    private Object instance;

    private final TypeConverter converter = ConvertersMapTypeConverter.getInstance();

    /**
     * In case of unknown object genericType.
     * Null for embedded objects such as collections, or known conversion types.
     */
    private ClassModel classModel;

    public CurrentItemBuilder(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    /**
     * Wrapper item for this item.
     * @param wrapper not null
     * @return builder instance for call chaining
     */
    public CurrentItemBuilder withWrapper(CurrentItem<?> wrapper) {
        this.wrapper = wrapper;
        return this;
    }

    /**
     * Type for underlying instance to be created from.
     * In case of type variable or wildcard, will be resolved recursively from parent items.
     * @param type type of instance not null
     * @return builder instance for call chaining
     */
    public CurrentItemBuilder withType(Type type) {
        this.genericType = unwrapAnonymous(type);
        return this;
    }

    /**
     * Model of a field for underlying instance. In case model is present, instance type is inferred from it.
     * @param propertyModel model of a field, not null
     * @return builder instance for call chaining
     */
    public CurrentItemBuilder withFieldModel(PropertyModel propertyModel) {
        this.propertyModel = propertyModel;
        return this;
    }

    /**
     * JSON key name is used for setting unmarshalled objects or values into the fields.
     * @param jsonKeyName
     * @return builder instance for call chaining
     */
    public CurrentItemBuilder withJsonKeyName(String jsonKeyName) {
        this.jsonKeyName = jsonKeyName;
        return this;
    }

    /**
     * JSON value type is used for inferring type of values, which should be set to {@link Object} fields,
     * or when raw generics are used.
     * @param jsonValueType type of JSON value returned by {@link javax.json.stream.JsonParser} not null
     * @return builder instance for call chaining
     */
    public CurrentItemBuilder withJsonValueType(JsonValueType jsonValueType) {
        this.jsonValueType = jsonValueType;
        return this;
    }

    /**
     * Build an fully initialized item.
     *
     * @return built item
     */
    public CurrentItem<?> build() {
        runtimeType = resolveRuntimeType();
        Class rawType = ReflectionUtils.getRawType(runtimeType);
        switch (jsonValueType) {
            case ARRAY:
                if (rawType.isArray() || runtimeType instanceof GenericArrayType) {
                    return createArrayItem();
                }
                if (Collection.class.isAssignableFrom(rawType)) {
                    return createCollectionItem();
                }
                throw new JsonbException(String.format("JSON array not expected for unmarshalling into field %s of type %s.", jsonKeyName, rawType));
            case OBJECT:
                if (Map.class.isAssignableFrom(rawType)) {
                    return createMapItem();
                }
                if (rawType.isInterface()) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.INFER_TYPE_FOR_UNMARSHALL, rawType.getName()));
                }
                if (converter.supportsFromJson(rawType)) {
                    throw new JsonbException(String.format("JSON object not expected for unmarshalling into field %s, of supported type %s.", jsonKeyName, rawType));
                }

                classModel = mappingContext.getOrCreateClassModel(rawType);
                instance = createInstance(classModel.getRawType());
                return new ObjectItem<>(this);
            default:
                throw new JsonbException(String.format("Invalid json type %s.", rawType));
        }
    }

    private Type resolveRuntimeType() {
        Type toResolve = propertyModel != null ? propertyModel.getPropertyType() : genericType;
        Type resolved = ReflectionUtils.resolveType(wrapper, toResolve);
        //If genericType cannot be resolved or is object, unmarshall to Map.
        if (resolved == Object.class) {
            resolved = jsonValueType.getConversionType();
        }
        return resolved;
    }

    /**
     * Instance is not created in case of array items, because, we don't know how long it should be
     * till parser ends parsing.
     */
    private CurrentItem<?> createArrayItem() {
        return new ArrayItem(this);
    }

    private CurrentItem<?> createCollectionItem() {
        Class<?> rawType = ReflectionUtils.getRawType(runtimeType);
        assert Collection.class.isAssignableFrom(rawType);

        if (rawType.isInterface()) {
            if (List.class.isAssignableFrom(rawType)) {
                this.instance = new ArrayList<>();
            }
            if (Set.class.isAssignableFrom(rawType)) {
                this.instance = new HashSet<>();
            }
            if (Queue.class.isAssignableFrom(rawType)) {
                this.instance = new ArrayDeque<>();
            }
        } else {
            instance = createInstance(rawType);
        }
        return new CollectionItem<>(this);
    }

    private CurrentItem<?> createMapItem() {
        Class<?> rawType = ReflectionUtils.getRawType(runtimeType);
        this.instance = rawType.isInterface() ? new HashMap<>() : createInstance(rawType);
        return new MapItem(this);
    }

    @SuppressWarnings("unchecked")
    private Object createInstance(Class clazz) {
        try {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CANT_CREATE_INSTANCE), e);
        } catch (NoSuchMethodException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR), e);
        }
    }

    private Type unwrapAnonymous(Type type) {
        if (type instanceof Class<?> && ((Class)type).isAnonymousClass()) {
            return ((Class) type).getGenericSuperclass();
        }
        return type;
    }

    /**
     *
     * @return
     */
    public MappingContext getMappingContext() {
        return mappingContext;
    }

    /**
     * Wrapper item for this item.
     * @return wrapper item
     */
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    /**
     * Model of a field for underlying instance. In case model is present, instance type is inferred from it.
     * @return model of a field
     */
    public PropertyModel getPropertyModel() {
        return propertyModel;
    }

    /**
     * JSON key name is used for setting unmarshalled objects or values into the fields.
     * @return JSON key name
     */
    public String getJsonKeyName() {
        return jsonKeyName;
    }

    /**
     * Created instance to work on / unmarshall into.
     * @return created instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Resolved runtime type for instance in case of {@link java.lang.reflect.TypeVariable} or {@link java.lang.reflect.WildcardType}
     * Otherwise provided type in type field, or type of field model.
     * @return runtime type
     */
    public Type getRuntimeType() {
        return runtimeType;
    }

    /**
     * Model of a class representing current item and instance (if any).
     * Known collection classes doesn't need such a model.
     * @return model of a class
     */
    public ClassModel getClassModel() {
        return classModel;
    }
}
