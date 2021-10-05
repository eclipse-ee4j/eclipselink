/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     mmacivor - March 10/2009 - Initial implementation
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * Purpose:
 * Provide a version of Instantiation Policy that can make use of a multiple argument factory method.
 * The defaultValues specified on this policy will be passed in to the factory method for the parameter values.
 *
 * This is required for certain JAXB generated classes that have no 0 arg constructor and a factory method with multiple arguments.
 * @author mmacivor
 *
 */
public class MultiArgInstantiationPolicy extends InstantiationPolicy {
    private String[] parameterTypeNames;
    private Class[] parameterTypes;

    private Object[] defaultValues;


    public void setParameterTypeNames(String[] parameterTypeNames) {
        this.parameterTypeNames = parameterTypeNames;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setDefaultValues(Object[] values) {
        defaultValues = values;
    }

    // Handle Class.forName() wrapper exception
    private ValidationException invokeGetClassForNameException(final Exception ex) {
        return ValidationException.classNotFoundWhileConvertingClassNames(factoryClassName, ex);
    }

    @Override
    public void convertClassNamesToClasses(ClassLoader loader) {
        super.convertClassNamesToClasses(loader);
        if(parameterTypes == null) {
            if(parameterTypeNames != null) {
                Class<?>[] values = new Class[parameterTypeNames.length];
                for(int i = 0; i < values.length; i++) {
                    final String parameterTypeName = parameterTypeNames[i];
                    values[i] = PrivilegedAccessHelper.callDoPrivilegedWithException(
                            () -> org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(parameterTypeName, true, loader),
                            this::invokeGetClassForNameException
                    );
                 }
                this.parameterTypes = values;
            }
        }

    }
    protected void initializeMethod() throws DescriptorException {
        Class tempClass;
        if (this.getFactory() != null) {
            tempClass = this.getFactory().getClass();
        } else if (this.getFactoryClass() == null) {
            tempClass = this.getDescriptor().getJavaClass();
        } else {
            tempClass = this.getFactoryClass();
        }
        if(this.parameterTypes == null) {
            this.setMethod(this.buildMethod(tempClass, this.getMethodName(), new Class[0]));
        } else {
            this.setMethod(this.buildMethod(tempClass, this.getMethodName(), this.parameterTypes));
        }
    }

    // Invoke factory method
    private Object invokeFactoryMethod() throws Exception {
        return PrivilegedAccessHelper.invokeMethod(getMethod(), getFactory(), this.defaultValues);
    }

    // Handle factory method invocation exception
    @SuppressWarnings("unchecked")
    private <E extends Exception> E invokeFactoryMethodException(final Exception ex) {
        if (ex instanceof IllegalAccessException) {
            return (E) DescriptorException.illegalAccessWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), ex);
        } else if (ex instanceof InvocationTargetException) {
            return (E) DescriptorException.targetInvocationWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), ex);
        } else if (ex instanceof NullPointerException) {
            return (E) DescriptorException.nullPointerWhileMethodInstantiation(this.getMethod().toString(), this.getDescriptor(), ex);
        }
        return (E) new RuntimeException("Unexpected exception from MultiArgInstantiationPolicy.buildNewInstanceUsingFactory()", ex);
    }

    /**
     * Build and return a new instance, using the factory.
     * The factory can be null, in which case the method is a static method defined by the descriptor class.
     */
    protected Object buildNewInstanceUsingFactory() throws DescriptorException {
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                this::invokeFactoryMethod,
                this::invokeFactoryMethodException
        );
    }

}
