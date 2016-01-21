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

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Propagates values to fields using {@link java.lang.invoke.MethodHandle}
 * <p>
 * Uses field getter / setter implicitly if those are present and public.
 * Uses field direct access otherwise if field is public.
 * </p>
 * Access policy could be modified by {@link javax.json.bind.annotation.JsonbVisibility}
 *
 * @author Roman Grigoriadi
 */
class MethodHandleValuePropagation extends PropertyValuePropagation {

    private MethodHandle getHandle;

    private MethodHandle setHandle;


    MethodHandleValuePropagation(Property property) {
        super(property);
    }

    @Override
    protected void acceptMethod(Method method, OperationMode mode) {
        try {
            switch (mode) {
                case GET:
                    getHandle = MethodHandles.lookup().unreflect(method);
                    break;
                case SET:
                    setHandle = MethodHandles.lookup().unreflect(method);
                    break;
                default:
                    throw new IllegalStateException("Unknown mode");
            }
        } catch (IllegalAccessException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CREATING_HANDLES), e);
        }
    }




    @Override
    protected void acceptField(Field field, OperationMode mode) {
        try {
            switch (mode) {
                case GET:
                    getHandle = MethodHandles.lookup().unreflectGetter(field);
                    break;
                case SET:
                    setHandle = MethodHandles.lookup().unreflectSetter(field);
                    break;
                default:
                    throw new IllegalStateException("Unknown mode");
            }
        } catch (IllegalAccessException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CREATING_HANDLES), e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object object, Object value) {
        try {
            setHandle.invoke(object, value);
        } catch (Throwable throwable) {
            throw new JsonbException(Messages.getMessage(MessageKeys.SETTING_VALUE_WITH, setHandle), throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object object) {
        try {
            return getHandle.invoke(object);
        } catch (Throwable throwable) {
            throw new JsonbException(Messages.getMessage(MessageKeys.GETTING_VALUE_WITH, getHandle), throwable);
        }
    }

}
