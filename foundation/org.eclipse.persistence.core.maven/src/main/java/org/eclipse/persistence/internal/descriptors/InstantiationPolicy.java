/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.descriptors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.core.descriptors.CoreInstantiationPolicy;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Allows customization of how an object is created/instantiated.<p>
 *
 * So, here is how it works:<p>
 *
 * If there is no method specified<br>
 *     - all the other settings are ignored and<br>
 *     - the descriptor class's default constructor is invoked.<br>
 * If a factory is specified<br>
 *     - the factoryClass and factoryClassMethod are ignored and<br>
 *     - the method is invoked on the factory.<br>
 * If neither a factory nor a factoryClass are specified<br>
 *     - the factoryClassMethod is ignored and<br>
 *     - the method is invoked on the descriptor class (as a static).<br>
 * If only the factoryClass is specified<br>
 *     - the factory is created by invoking the factoryClass' default (zero-argument) constructor and<br>
 *     - the method is invoked on the resulting factory.<br>
 * If both the factoryClass and the factoryClassMethod are specified<br>
 *     - the factory is created by invoking the factoryClassMethod on the factoryClass (as a static) and<br>
 *     - the method is invoked on the resulting factory.<p>
 *
 * The only thing we can't support in the current configuration is invoking a static on some,
 * client-specified, factoryClass to build new instances of the descriptor class; and it's debatable
 * whether that is desirable...<p>
 *
 * It might be reasonable to rework this into a number of different classes that implement
 * an interface...
 */
public class InstantiationPolicy extends CoreInstantiationPolicy implements Cloneable, Serializable {

    /**
     * The method invoked on either the descriptor class (in which case it is static) or
     * the factory (in which case it is not static) to build a new instance of the descriptor class.
     */
    protected String methodName;

    /** The method is resolved during initialization, and it is not serialized. */
    protected transient Method method;

    /**
     * The class of the factory. The factory is instantiated by either invoking this class's default
     * (zero-argument) constructor or the factoryMethod specified below.
     */
    protected Class factoryClass;
    protected String factoryClassName;

    /**
     * Static method invoked on the factoryClass to get the factory instance. If this is null, the
     * factory class's default (zero-argument) constructor is invoked.
     */
    protected String factoryMethodName;

    /**
     * The object factory. This can be specified directly by the client, or it can be built dynamically
     * using the the factoryClass and, optionally, the factoryMethodName.
     */
    protected Object factory;

    /** Backpointer to descriptor. */
    protected ClassDescriptor descriptor;

    /** Must be transient because java.lang.Constructor is not serializable. */
    private transient Constructor defaultConstructor;

    /**
     * Default constructor
     */
    public InstantiationPolicy() {
        super();
    }

    /**
     * Build and return a new instance, using the appropriate mechanism.
     */
    @Override
    public Object buildNewInstance() throws DescriptorException {
        // PERF: Just check method-name.
        if (this.methodName == null) {
            return buildNewInstanceUsingDefaultConstructor();
        } else {
            return buildNewInstanceUsingFactory();
        }
    }

    /**
     * Build and return a new instance, using the default (zero-argument) constructor.
     */
    protected Object buildNewInstanceUsingDefaultConstructor() throws DescriptorException {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedInvokeConstructor(getDefaultConstructor(), null));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof InvocationTargetException){
                        throw DescriptorException.targetInvocationWhileConstructorInstantiation(getDescriptor(), throwableException);
                    } else if (throwableException instanceof IllegalAccessException){
                        throw DescriptorException.illegalAccessWhileConstructorInstantiation(getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.instantiationWhileConstructorInstantiation(getDescriptor(), throwableException);
                    }
                 }
            } else {
                // PERF: Check lazy init with null check.
                if (this.defaultConstructor == null) {
                    getDefaultConstructor();
                }
                return this.defaultConstructor.newInstance((Object[])null);
            }
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileConstructorInstantiation(getDescriptor(), exception);
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileConstructorInstantiation(getDescriptor(), exception);
        } catch (InstantiationException exception) {
            throw DescriptorException.instantiationWhileConstructorInstantiation(getDescriptor(), exception);
        } catch (NoSuchMethodError exception) {
            // This exception is not documented but gets thrown.
            throw DescriptorException.noSuchMethodWhileConstructorInstantiation(getDescriptor(), exception);
        } catch (NullPointerException exception) {
            // Some JVMs will throw a NULL pointer exception here
            throw DescriptorException.nullPointerWhileConstructorInstantiation(getDescriptor(), exception);
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
                    return AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod(), getFactory(), new Object[0]));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof IllegalAccessException) {
                        throw DescriptorException.illegalAccessWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.targetInvocationWhileMethodInstantiation(getMethod().toString(), this.getDescriptor(), throwableException);
                    }
                }
            } else {
                return PrivilegedAccessHelper.invokeMethod(getMethod(), getFactory(), new Object[0]);
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

    /**
     * INTERNAL:
     * Clones the InstantiationPolicy
     */
    @Override
    public Object clone() {
        try {
            // clones itself
            return super.clone();
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * Return the default (zero-argument) constructor for the descriptor class.
     */
    protected Constructor getDefaultConstructor() throws DescriptorException {
        // Lazy initialize, because the constructor cannot be serialized
        if (defaultConstructor == null) {
            this.setDefaultConstructor(this.buildDefaultConstructor());
        }
        return defaultConstructor;
    }

    /**
     * Build and return the default (zero-argument) constructor for the descriptor class.
     */
    protected Constructor buildDefaultConstructor() throws DescriptorException {
        return this.buildDefaultConstructorFor(this.getDescriptor().getJavaClass());
    }

    /**
     * Build and return the default (zero-argument) constructor for the specified class.
     */
    protected Constructor buildDefaultConstructorFor(Class javaClass) throws DescriptorException {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedGetDeclaredConstructorFor(javaClass, new Class[0], true));
                } catch (PrivilegedActionException exception) {
                    throw DescriptorException.noSuchMethodWhileInitializingInstantiationPolicy(javaClass.getName() + ".<Default Constructor>", getDescriptor(), exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.getDeclaredConstructorFor(javaClass, new Class[0], true);
            }
        } catch (NoSuchMethodException exception) {
            throw DescriptorException.noSuchMethodWhileInitializingInstantiationPolicy(javaClass.getName() + ".<Default Constructor>", getDescriptor(), exception);
        }
    }

    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    public Object getFactory() {
        return factory;
    }

    public Class getFactoryClass() {
        return factoryClass;
    }

    public String getFactoryClassName() {
        if ((factoryClassName == null) && (factoryClass != null)) {
            factoryClassName = factoryClass.getName();
        }
        return factoryClassName;
    }

    protected Method getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    /**
     * If necessary, initialize the factory and the method.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        if (this.isUsingDefaultConstructor()) {
            // May not have a constructor as may be abstract or interface so only lazy init.
            return;
        }
        try {
            this.initializeMethod();
            if (!(Modifier.isStatic(getMethod().getModifiers()))) {
                // If the factory has been specified directly, do not overwrite it
                if (this.getFactory() == null) {
                    this.setFactory(this.buildFactory());
                }
            }
        } catch (DescriptorException ex) {
            session.getIntegrityChecker().handleError(ex);
        }
    }

    protected Object buildFactory() throws DescriptorException {
        // If there is no factory class specified, there is no factory;
        // we will be using a static method defined by the descriptor class...
        if (this.getFactoryClass() == null) {
            return null;
        }

        // If there is a factory class specified but no factory method name,
        // instantiate the factory using the default constructor
        if (this.getFactoryMethodName() == null) {
            return this.buildFactoryUsingDefaultConstructor();
        }

        // If both the factory class and the factory method name have been specified,
        // instantiate the factory by invoking the static factory method
        return this.buildFactoryUsingStaticMethod();
    }

    /**
     * Build and return the factory, using its default constructor.
     */
    protected Object buildFactoryUsingDefaultConstructor() throws DescriptorException {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedInvokeConstructor(buildFactoryDefaultConstructor(), null));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof InvocationTargetException){
                        throw DescriptorException.targetInvocationWhileConstructorInstantiationOfFactory(getDescriptor(), throwableException);
                    } else if (throwableException instanceof IllegalAccessException){
                        throw DescriptorException.illegalAccessWhileConstructorInstantiationOfFactory(getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.instantiationWhileConstructorInstantiationOfFactory(getDescriptor(), throwableException);
                    }
                 }
            } else {
                return PrivilegedAccessHelper.invokeConstructor(buildFactoryDefaultConstructor(), null);
            }
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileConstructorInstantiationOfFactory(getDescriptor(), exception);
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileConstructorInstantiationOfFactory(getDescriptor(), exception);
        } catch (InstantiationException exception) {
            throw DescriptorException.instantiationWhileConstructorInstantiationOfFactory(getDescriptor(), exception);
        } catch (NoSuchMethodError exception) {
            // This exception is not documented but gets thrown.
            throw DescriptorException.noSuchMethodWhileConstructorInstantiationOfFactory(getDescriptor(), exception);
        } catch (NullPointerException exception) {
            // Some JVMs will throw a NULL pointer exception here
            throw DescriptorException.nullPointerWhileConstructorInstantiationOfFactory(getDescriptor(), exception);
        }
    }

    /**
     * Build and return the default (zero-argument) constructor for the factory class.
     */
    protected Constructor buildFactoryDefaultConstructor() throws DescriptorException {
        return this.buildDefaultConstructorFor(this.getFactoryClass());
    }

    /**
     * Build and return the factory, using the specified static method.
     */
    protected Object buildFactoryUsingStaticMethod() throws DescriptorException {
        Method factoryMethod = this.buildMethod(this.getFactoryClass(), this.getFactoryMethodName(), new Class[0]);

        try {
            // it should be static and zero-argument...
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedMethodInvoker(factoryMethod, null, null));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof IllegalAccessException) {
                        throw DescriptorException.illegalAccessWhileMethodInstantiationOfFactory(getFactoryMethodName(), getDescriptor(), throwableException);
                    } else {
                        throw DescriptorException.targetInvocationWhileMethodInstantiationOfFactory(getFactoryMethodName(), getDescriptor(), throwableException);
                    }

                }
            } else {
                return PrivilegedAccessHelper.invokeMethod(factoryMethod, null, null);
            }
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileMethodInstantiationOfFactory(getFactoryMethodName(), getDescriptor(), exception);
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileMethodInstantiationOfFactory(getFactoryMethodName(), getDescriptor(), exception);
        } catch (NullPointerException exception) {
            // Some JVMs will throw a NULL pointer exception here
            throw DescriptorException.nullPointerWhileMethodInstantiationOfFactory(getFactoryMethodName(), getDescriptor(), exception);
        }
    }

    /**
     * Initialize the method.
     * It is either a static on the descriptor class, or it is a non-static on the factory.
     */
    protected void initializeMethod() throws DescriptorException {
        Class tempClass;
        if (this.getFactory() != null) {
            tempClass = this.getFactory().getClass();
        } else if (this.getFactoryClass() == null) {
            tempClass = this.getDescriptor().getJavaClass();
        } else {
            tempClass = this.getFactoryClass();
        }
        this.setMethod(this.buildMethod(tempClass, this.getMethodName(), new Class[0]));
    }

    /**
     * Build the specified method.
     */
    protected Method buildMethod(Class methodClass, String methodName, Class[] methodParameterTypes) throws DescriptorException {
        try {
            return Helper.getDeclaredMethod(methodClass, methodName, methodParameterTypes);
        } catch (NoSuchMethodException exception) {
            throw DescriptorException.noSuchMethodWhileInitializingInstantiationPolicy(methodClass.getName() + "." + methodName, this.getDescriptor(), exception);
        } catch (SecurityException exception) {
            throw DescriptorException.securityWhileInitializingInstantiationPolicy(methodClass.getName() + "." + methodName, this.getDescriptor(), exception);
        }
    }

    /**
     * If no method name is specified, they we have to use the default (zero-argument) constructor.
     */
    public boolean isUsingDefaultConstructor() {
        return this.getMethodName() == null;
    }

    protected void setDefaultConstructor(Constructor defaultConstructor) {
        this.defaultConstructor = defaultConstructor;
    }

    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    protected void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    protected void setFactory(Object factory) {
        this.factory = factory;
    }

    protected void setFactoryClass(Class factoryClass) {
        this.factoryClass = factoryClass;
    }

    protected void setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
    }

    protected void setMethod(Method method) {
        this.method = method;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this InstantiationPolicy to actual class-based
     * settings.  This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        if (factoryClassName == null){
            return;
        }
        Class factoryClass = null;
        try{
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    factoryClass = AccessController.doPrivileged(new PrivilegedClassForName(factoryClassName, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(factoryClassName, exception.getException());
                }
            } else {
                factoryClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(factoryClassName, true, classLoader);
            }
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(factoryClassName, exc);
        }
        setFactoryClass(factoryClass);
    }


    @Override
    public String toString() {
        String mName = null;
        if (this.isUsingDefaultConstructor()) {
            mName = "<CONSTRUCTOR>";
        } else {
            mName = this.getMethodName();
        }
        return Helper.getShortClassName(this) + "(" + mName + ")";
    }

    public void useDefaultConstructorInstantiationPolicy() {
        setMethodName(null);
        setFactory(null);
        setFactoryClass(null);
        setFactoryClassName(null);
        setFactoryMethodName(null);
    }

    public void useFactoryInstantiationPolicy(Class factoryClass, String methodName) {
        setMethodName(methodName);
        setFactory(null);
        setFactoryClass(factoryClass);
        setFactoryClassName(factoryClass.getName());
        setFactoryMethodName(null);
    }

    public void useFactoryInstantiationPolicy(Class factoryClass, String methodName, String factoryMethodName) {
        setMethodName(methodName);
        setFactory(null);
        setFactoryClass(factoryClass);
        setFactoryClassName(factoryClass.getName());
        setFactoryMethodName(factoryMethodName);
    }

    @Override
    public void useFactoryInstantiationPolicy(String factoryClassName, String methodName) {
        setMethodName(methodName);
        setFactory(null);
        setFactoryClass(null);
        setFactoryClassName(factoryClassName);
        setFactoryMethodName(null);
    }

    public void useFactoryInstantiationPolicy(String factoryClassName, String methodName, String factoryMethodName) {
        setMethodName(methodName);
        setFactory(null);
        setFactoryClass(null);
        setFactoryClassName(factoryClassName);
        setFactoryMethodName(factoryMethodName);
    }

    public void useFactoryInstantiationPolicy(Object factory, String methodName) {
        setMethodName(methodName);
        setFactory(factory);
        setFactoryClass(null);
        setFactoryClassName(null);
        setFactoryMethodName(null);
    }

    public void useMethodInstantiationPolicy(String staticMethodName) {
        setMethodName(staticMethodName);
        setFactory(null);
        setFactoryClass(null);
        setFactoryClassName(null);
        setFactoryMethodName(null);
    }
}
