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
package org.eclipse.persistence.json.bind.model;

import org.eclipse.persistence.json.bind.internal.AnnotationIntrospector;
import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.adapter.SerializerBinding;
import org.eclipse.persistence.json.bind.internal.serializer.AdaptedObjectSerializer;
import org.eclipse.persistence.json.bind.internal.serializer.DefaultSerializers;
import org.eclipse.persistence.json.bind.internal.serializer.SerializerProviderWrapper;
import org.eclipse.persistence.json.bind.internal.serializer.UserSerializerSerializer;

import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * A model for class property.
 * Property is JavaBean alike meta information field / getter / setter of a property in class.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class PropertyModel implements JsonBindingModel, Comparable<PropertyModel> {

    /**
     * Field propertyName as in class by java bean convention.
     */
    private final String propertyName;

    /**
     * Calculated name to be used when reading json document.
     */
    private final String readName;

    /**
     * Calculated name to be used when writing json document.
     */
    private final String writeName;

    /**
     * Field propertyType.
     */
    private final Type propertyType;

    /**
     * Model of the class this field belongs to.
     */
    private final ClassModel classModel;

    /**
     * Customization of this property.
     */
    final private PropertyCustomization customization;

    private final PropertyValuePropagation propagation;

    private final JsonbSerializer<?> propertySerializer;

    /**
     * Flag to cache serializer. If type is not resolved (TypeVariable or ParameterizedType containing TypeVariables)
     * Serializer / Adapter caching is not possible.
     */
    private final boolean resolvedType;

    /**
     * Creates instance.
     * @param classModel classModel of declaring class.
     * @param property javabean like property to model.
     */
    public PropertyModel(ClassModel classModel, Property property, JsonbContext jsonbContext) {
        this.classModel = classModel;
        this.propertyName = property.getName();
        this.propertyType = property.getPropertyType();
        this.propagation = PropertyValuePropagation.createInstance(property, jsonbContext);
        this.customization = introspectCustomization(property, jsonbContext);
        this.readName = calculateReadWriteName(customization.getJsonReadName(), jsonbContext.getPropertyNamingStrategy());
        this.writeName = calculateReadWriteName(customization.getJsonWriteName(), jsonbContext.getPropertyNamingStrategy());
        this.propertySerializer = resolveCachedSerializer();
        this.resolvedType = ReflectionUtils.isResolvedType(propertyType);
    }


    /**
     * Try to cache serializer for this bean property. Only if type cannot be changed during runtime.
     *
     * @return serializer instance to be cached
     */
    private JsonbSerializer<?> resolveCachedSerializer() {
        if (!ReflectionUtils.isResolvedType(propertyType)) {
            return null;
        }
        if (customization.getAdapterBinding() != null) {
            return new AdaptedObjectSerializer<>(this, customization.getAdapterBinding());
        }
        if (customization.getSerializerBinding() != null) {
            return new UserSerializerSerializer<>(this, customization.getSerializerBinding().getJsonbSerializer());
        }

        final Class<?> propertyRawType = ReflectionUtils.getRawType(propertyType);
        final Optional<SerializerProviderWrapper> valueSerializerProvider = DefaultSerializers.getInstance().findValueSerializerProvider(propertyRawType);
        if (valueSerializerProvider.isPresent()) {
            return valueSerializerProvider.get().getSerializerProvider().provideSerializer(this);
        }

        return null;
    }

    private AdapterBinding getUserAdapterBinding(Property property, JsonbContext jsonbContext) {
        final AdapterBinding adapterBinding = jsonbContext.getAnnotationIntrospector().getAdapterBinding(property);
        if (adapterBinding != null) {
            return adapterBinding;
        }
        return jsonbContext.getComponentMatcher().getAdapterBinding(propertyType, null).orElse(null);
    }

    private SerializerBinding<?> getUserSerializerBinding(Property property, JsonbContext jsonbContext) {
        final SerializerBinding serializerBinding = jsonbContext.getAnnotationIntrospector().getSerializerBinding(property);
        if (serializerBinding != null) {
            return serializerBinding;
        }
        return jsonbContext.getComponentMatcher().getSerialzierBinding(propertyType, null).orElse(null);
    }

    private PropertyCustomization introspectCustomization(Property property, JsonbContext jsonbContext) {
        final AnnotationIntrospector introspector = jsonbContext.getAnnotationIntrospector();
        final CustomizationBuilder builder = new CustomizationBuilder();
        //drop all other annotations for transient properties
        if (introspector.isTransient(property)) {
            builder.setJsonbTransient(true);
            return builder.buildPropertyCustomization();
        }
        builder.setJsonReadName(introspector.getJsonbPropertyJsonReadName(property));
        builder.setJsonWriteName(introspector.getJsonbPropertyJsonWriteName(property));
        builder.setNillable(classModel.getClassCustomization().isNillable()
                || introspector.isPropertyNillable(property));
        builder.setAdapterInfo(getUserAdapterBinding(property, jsonbContext));
        builder.setSerializerBinding(getUserSerializerBinding(property, jsonbContext));
        builder.setDeserializerBinding(introspector.getDeserializerBinding(property));
        builder.setDateFormatter(introspector.getJsonbDateFormat(property));
        builder.setNumberFormat(introspector.getJsonbNumberFormat(property));
        return builder.buildPropertyCustomization();
    }

    /**
     * Read a property.
     *
     * @param object object to read property from.
     * @return value in case property value is set and field is readable. If null or not readable (transient, static), return s null.
     */
    public Object getValue(Object object) {
        if (!isReadable()) {
            //nulls are omitted in produced JSON, unless overriden
            return null;
        }
        return propagation.getValue(object);
    }

    /**
     * Sets a property.
     *
     * If not writable (final, transient, static), ignores property.
     *
     * @param object Object to set value in.
     * @param value  Value to set.
     */
    public void setValue(Object object, Object value) {
        if (!isWritable()) {
            return;
        }
        propagation.setValue(object, value);
    }

    /**
     * Property is readable. Based on access policy and java field modifiers.
     * @return true if can be serialized to JSON
     */
    public boolean isReadable() {
        return !customization.isJsonbTransient() && propagation.isReadable();
    }

    /**
     * Property is writable. Based on access policy and java field modifiers.
     * @return true if can be deserialized from JSON
     */
    public boolean isWritable() {
        return !customization.isJsonbTransient() && propagation.isWritable();
    }

    /**
     * Default property name according to Field / Getter / Setter method names.
     * This name is use for identifying properties, for JSON serialization is used customized name
     * which may be derived from default name.
     * @return default name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Runtime type of a property. May be a TypeVariable or WildcardType.
     *
     * @return type of a property
     */
    public Type getPropertyType() {
        return propertyType;
    }

    /**
     * Model of declaring class of this property.
     * @return class model
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Introspected customization of a property.
     * @return immutable property customization
     */
    @Override
    public PropertyCustomization getCustomization() {
        return customization;
    }

    /**
     * Class of a property, either bean property type or collection / array component type.
     *
     * @return class type
     */
    @Override
    public Type getType() {
        return getPropertyType();
    }

    @Override
    public int compareTo(PropertyModel o) {
        return propertyName.compareTo(o.getPropertyName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyModel that = (PropertyModel) o;
        return Objects.equals(propertyName, that.propertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName);
    }

    @Override
    public JsonContext getContext() {
        return JsonContext.JSON_OBJECT;
    }

    public String getReadName() {
        return readName;
    }

    @Override
    public String getWriteName() {
        return writeName;
    }

    public JsonbSerializer<?> getPropertySerializer() {
        return propertySerializer;
    }

    /**
     * If customized by JsonbPropertyAnnotation, than is used, otherwise use strategy to translate.
     * Since this is cached for performance reasons strategy has to be consistent
     * with calculated values for same input.
     */
    private String calculateReadWriteName(String readWriteName, PropertyNamingStrategy strategy) {
        return readWriteName != null ? readWriteName : strategy.translateName(propertyName);
    }
}
