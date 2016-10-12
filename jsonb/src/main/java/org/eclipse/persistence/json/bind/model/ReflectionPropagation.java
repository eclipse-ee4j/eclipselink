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

import org.eclipse.persistence.json.bind.internal.JsonbContext;

import javax.json.bind.JsonbException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Roman Grigoriadi
 */
public class ReflectionPropagation extends PropertyValuePropagation {

    private interface GetValueCommand {
        Object internalGetValue(Object object) throws IllegalAccessException, InvocationTargetException;

        default Object getValue(Object object) {
            try {
                return internalGetValue(object);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new JsonbException("Error getting value on: " + object, e);
            }
        }
    }

    private interface SetValueCommand {
        void internalSetValue(Object object, Object value) throws IllegalAccessException, InvocationTargetException;

        default void setValue(Object object, Object value) {
            try {
                internalSetValue(object, value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new JsonbException("Error getting value on: " + object, e);
            }
        }
    }

    private GetValueCommand getValueCommand;

    private SetValueCommand setValueCommand;

    ReflectionPropagation(Property property, JsonbContext ctx) {
        super(property, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void acceptMethod(Method method, OperationMode mode) {
        switch (mode) {
            case GET:
                getValueCommand = method::invoke;
                break;
            case SET:
                setValueCommand = method::invoke;
                break;
            default: throw new IllegalStateException("Unknown mode");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void acceptField(Field field, OperationMode mode) {
        switch (mode) {
            case GET:
                getValueCommand = field::get;
                break;
            case SET:
                setValueCommand = field::set;
                break;
            default: throw new IllegalStateException("Unknown mode");
        }
    }

    @Override
    void setValue(Object object, Object value) {
        setValueCommand.setValue(object, value);
    }

    @Override
    Object getValue(Object object) {
        return getValueCommand.getValue(object);
    }
}
