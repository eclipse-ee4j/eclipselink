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

import javax.json.bind.JsonbException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Property of a class, field, getter and setter methods (javabean alike).
 *
 * @author Roman Grigoriadi
 */
public class Property {

    private final String name;

    private final ClassModel declaringClassModel;

    private Field field;

    private Method getter;

    private Method setter;

    /**
     * Create instance of property.
     * @param name not null
     * @param declaringClassModel Class model for a class declaring property.
     */
    public Property(String name, ClassModel declaringClassModel) {
        this.name = name;
        this.declaringClassModel = declaringClassModel;
    }

    /**
     * Name of a property, java bean convention.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * {@link Field} representing property if any
     *
     * @return field if present
     */
    public Field getField() {
        return field;
    }

    /**
     * @param field field not null
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * {@link Method} representing getter of a property if any.
     *
     * @return getter if present
     */
    public Method getGetter() {
        return getter;
    }

    /**
     * @param getter not null
     */
    public void setGetter(Method getter) {
        this.getter = getter;
    }

    /**
     * {@link Method} representing setter of a property if any.
     *
     * @return setter if present
     */
    public Method getSetter() {
        return setter;
    }

    /**
     * @param setter setter not null
     */
    public void setSetter(Method setter) {
        this.setter = setter;
    }

    /**
     * ClassModel under construction for declaring class of this property.
     * This ClassModel is not fully initialized yet.
     * @return ClassModel
     */
    public ClassModel getDeclaringClassModel() {
        return declaringClassModel;
    }

    /**
     * Extracts type from first not null element:
     * Field, Getter, Setter.
     *
     * @return type of a property
     */
    public Type getPropertyType() {
        if (field != null) {
            return field.getGenericType();
        } else if (getter != null) {
            return getter.getGenericReturnType();
        } else if (setter != null) {
            Type[] genericParameterTypes = setter.getGenericParameterTypes();
            if (genericParameterTypes.length != 1) {
                throw new JsonbException("Invalid count of arguments for setter: " + setter);
            }
            return genericParameterTypes[0];
        }
        throw new JsonbException("Empty property: " + name);
    }
}
