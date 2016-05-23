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

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A model for class property.
 * Property is JavaBean alike meta information field / getter / setter of a property in class.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class PropertyModel implements Comparable<PropertyModel> {
    /**
     * Field propertyName as in class.
     */
    private final String propertyName;

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

    /**
     * Creates instance.
     * @param classModel classModel of declaring class.
     * @param property javabean like property to model.
     */
    public PropertyModel(ClassModel classModel, Property property) {
        this.classModel = classModel;
        this.propertyName = property.getName();
        this.propertyType = property.getPropertyType();
        this.propagation = PropertyValuePropagation.createInstance(property);
        this.customization = introspectCustomization(property);
    }

    private PropertyCustomization introspectCustomization(Property property) {
        final AnnotationIntrospector introspector = AnnotationIntrospector.getInstance();
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
        builder.setAdapterInfo(introspector.getAdapterBinding(property));
        builder.setSerializerBinding(introspector.getSerializerBinding(property));
        builder.setDeserializerBinding(introspector.getDeserializerBinding(property));
        builder.setDateFormatter(introspector.getJsonbDateFormat(property));
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
    public PropertyCustomization getCustomization() {
        return customization;
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
}
