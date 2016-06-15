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
 * Used during class model initialization, than dereferenced.
 *
 * @author Roman Grigoriadi
 */
public class Property {

    private final String name;

    private final JsonbAnnotatedElement<Class<?>> declaringClassElement;

    private JsonbAnnotatedElement<Field> fieldElement;

    private JsonbAnnotatedElement<Method> getterElement;

    private JsonbAnnotatedElement<Method> setterElement;

    /**
     * Create instance of property.
     * @param name not null
     * @param declaringClassModel Class model for a class declaring property.
     */
    public Property(String name, JsonbAnnotatedElement<Class<?>> declaringClassModel) {
        this.name = name;
        this.declaringClassElement = declaringClassModel;
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
        if (fieldElement == null) {
            return null;
        }
        return fieldElement.getElement();
    }

    /**
     * @param field field not null
     */
    public void setField(Field field) {
        this.fieldElement = new JsonbAnnotatedElement<>(field);
    }

    /**
     * {@link Method} representing getter of a property if any.
     *
     * @return getter if present
     */
    public Method getGetter() {
        if (getterElement == null) {
            return null;
        }
        return getterElement.getElement();
    }

    /**
     * @param getter not null
     */
    public void setGetter(Method getter) {
        this.getterElement = new JsonbAnnotatedElement<>(getter);
    }

    /**
     * {@link Method} representing setter of a property if any.
     *
     * @return setter if present
     */
    public Method getSetter() {
        if (setterElement == null) {
            return null;
        }
        return setterElement.getElement();
    }

    /**
     * @param setter setter not null
     */
    public void setSetter(Method setter) {
        this.setterElement = new JsonbAnnotatedElement<>(setter);
    }

    /**
     * Class element with annotation under construction for declaring class of this property.
     * This ClassModel is not fully initialized yet.
     * @return ClassModel
     */
    public JsonbAnnotatedElement<Class<?>> getDeclaringClassElement() {
        return declaringClassElement;
    }

    /**
     * Extracts type from first not null element:
     * Field, Getter, Setter.
     *
     * @return type of a property
     */
    public Type getPropertyType() {
        if (getField() != null) {
            return getField().getGenericType();
        } else if (getGetter() != null) {
            return getGetter().getGenericReturnType();
        } else if (getSetter() != null) {
            Type[] genericParameterTypes = getSetter().getGenericParameterTypes();
            if (genericParameterTypes.length != 1) {
                throw new JsonbException("Invalid count of arguments for setter: " + getSetter());
            }
            return genericParameterTypes[0];
        }
        throw new JsonbException("Empty property: " + name);
    }

    /**
     * Element with field and its annotations.
     * @return field with annotations
     */
    public JsonbAnnotatedElement<Field> getFieldElement() {
        return fieldElement;
    }

    /**
     * Element with getter and its annotations.
     * @return getter with annotations
     */
    public JsonbAnnotatedElement<Method> getGetterElement() {
        return getterElement;
    }

    /**
     * Element with setter and its annotations.
     * @return setter with annotations
     */
    public JsonbAnnotatedElement<Method> getSetterElement() {
        return setterElement;
    }

}
