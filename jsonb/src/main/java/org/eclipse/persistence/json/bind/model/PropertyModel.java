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

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;

import javax.json.bind.JsonbException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A model for class field.
 *
 * @author Dmitry Kornilov
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
     * Field propertyName as it is written in JSON document during marshalling.
     * {@link javax.json.bind.annotation.JsonbProperty} customization on getter. Defaults to {@see propertyName} if not set.
     */
    private String writeName;

    /**
     * Field propertyName to read from JSON document during unmarshalling.
     * {@link javax.json.bind.annotation.JsonbProperty} customization on setter. Defaults to {@see propertyName} if not set.
     */
    private String readName;

    /**
     * Indicates that this field is nillable (@JsonbProperty(nillable=true)).
     */
    private boolean nillable;

    private final PropertyValuePropagation propagation;

    public PropertyModel(ClassModel classModel, Property property) {
        this.propertyName = property.getName();
        this.propertyType = property.getPropertyType();
        this.classModel = classModel;
        this.propagation = PropertyValuePropagation.createInstance(property);
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
        return propagation.isReadable();
    }

    /**
     * Property is writable. Based on access policy and java field modifiers.
     * @return true if can be deserialized from JSON
     */
    public boolean isWritable() {
        return propagation.isWritable();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Type getPropertyType() {
        return propertyType;
    }

    public String getWriteName() {
        if (writeName == null) {
            return propertyName;
        }
        return writeName;
    }

    public void setWriteName(String writeName) {
        this.writeName = writeName;
    }

    public String getReadName() {
        if (readName == null) {
            return propertyName;
        }
        return readName;
    }

    public void setReadName(String readName) {
        this.readName = readName;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public Method getGetter() {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, ReflectionUtils.getRawType(propertyType));
            return propertyDescriptor.getReadMethod();
        } catch (IntrospectionException e) {
            throw new JsonbException("", e);
        }
    }

    public ClassModel getClassModel() {
        return classModel;
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
