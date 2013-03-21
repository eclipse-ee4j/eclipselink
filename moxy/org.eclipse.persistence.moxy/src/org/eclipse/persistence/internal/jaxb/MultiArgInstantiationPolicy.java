/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - March 10/2009 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

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
    
    @Override
    public void convertClassNamesToClasses(ClassLoader loader) {
        super.convertClassNamesToClasses(loader);
        if(parameterTypes == null) {
            if(parameterTypeNames != null) {
                Class[] values = new Class[parameterTypeNames.length];
                for(int i = 0; i < values.length; i++) {
                    try{
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                            try {
                                values[i] = (Class)AccessController.doPrivileged(new PrivilegedClassForName(parameterTypeNames[i], true, loader));
                            } catch (PrivilegedActionException exception) {
                                throw ValidationException.classNotFoundWhileConvertingClassNames(parameterTypeNames[i], exception.getException());
                            }
                        } else {
                            values[i] = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(parameterTypeNames[i], true, loader);
                        }
                    } catch (ClassNotFoundException exc){
                        throw ValidationException.classNotFoundWhileConvertingClassNames(factoryClassName, exc);
                    }                    
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
    
    /**
     * Build and return a new instance, using the factory.
     * The factory can be null, in which case the method is a static method defined by the descriptor class.
     */
    protected Object buildNewInstanceUsingFactory() throws DescriptorException {
        try {
            // If the method is static, the first argument is ignored and can be null
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod(), getFactory(), this.defaultValues));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof IllegalAccessException) {
                        throw DescriptorException.illegalAccessWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.targetInvocationWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), throwableException);
                    }
                }
            } else {
                return PrivilegedAccessHelper.invokeMethod(getMethod(), getFactory(), this.defaultValues);
            }
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), exception);
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), exception);
        } catch (NullPointerException exception) {
            // Some JVMs will throw a NULL pointer exception here
            throw DescriptorException.nullPointerWhileMethodInstantiation(this.getMethod().toString(), this.getDescriptor(), exception);
        }
    }    

}
