/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.core.descriptors.CoreInstantiationPolicy;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
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
        // NoSuchMethodError is not an Exception instance so Throwable is required
        return PrivilegedAccessHelper.callDoPrivilegedWithThrowable(
                () -> (defaultConstructor != null ? defaultConstructor : getDefaultConstructor()).newInstance((Object[])null),
                (t) -> {
                    if (t instanceof DescriptorException) {
                        return (DescriptorException) t;
                    } else if (t instanceof InvocationTargetException) {
                        return DescriptorException.targetInvocationWhileConstructorInstantiation(getDescriptor(), (InvocationTargetException) t);
                    } else if (t instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileConstructorInstantiation(getDescriptor(), (IllegalAccessException) t);
                    } else if (t instanceof InstantiationException) {
                        return DescriptorException.instantiationWhileConstructorInstantiation(getDescriptor(), (InstantiationException) t);
                    } else if (t instanceof NoSuchMethodError) {
                        // This exception is not documented but gets thrown.
                        return DescriptorException.noSuchMethodWhileConstructorInstantiation(getDescriptor(), t);
                    } else if (t instanceof NullPointerException) {
                        // Some JVMs will throw a NULL pointer exception here
                        return DescriptorException.nullPointerWhileConstructorInstantiation(getDescriptor(), t);
                    }
                    // This indicates unexpected problem in the code
                    return new RuntimeException(String.format(
                            "Invocation of default %s constructor failed", getDescriptor().getJavaClass().getName()), t);
                }
        );
    }

    /**
     * Build and return a new instance, using the factory.
     * The factory can be null, in which case the method is a static method defined by the descriptor class.
     */
    protected Object buildNewInstanceUsingFactory() throws DescriptorException {
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.invokeMethod(getMethod(), getFactory(), new Object[0]),
                (ex) -> {
                    if (ex instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileMethodInstantiation(getMethod().toString(), getDescriptor(), ex);
                    } else if (ex instanceof InvocationTargetException) {
                        return DescriptorException.targetInvocationWhileMethodInstantiation(getMethod().toString(), getDescriptor(), ex);
                    } else if (ex instanceof NullPointerException) {
                        // Some JVMs will throw a NULL pointer exception here
                        return DescriptorException.nullPointerWhileMethodInstantiation(getMethod().toString(), getDescriptor(), ex);
                    }
                    // This indicates unexpected problem in the code
                    return new RuntimeException(String.format(
                            "Invocation of factory %s class %s method failed", getFactory().getClass().getName(), getMethod().getName()), ex);
                }
        );
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
    protected Constructor buildDefaultConstructorFor(final Class<?> javaClass) throws DescriptorException {
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.getDeclaredConstructorFor(javaClass, new Class<?>[0], true),
                (ex) -> DescriptorException.noSuchMethodWhileInitializingInstantiationPolicy(javaClass.getName() + ".<Default Constructor>", getDescriptor(), ex)
        );
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
        final Constructor<?> factoryDefaultConstructor = buildFactoryDefaultConstructor();
        // NoSuchMethodError is not an Exception instance so Throwable is required
        return PrivilegedAccessHelper.callDoPrivilegedWithThrowable(
                () -> PrivilegedAccessHelper.invokeConstructor(factoryDefaultConstructor, null),
                (t) -> {
                    if (t instanceof InvocationTargetException) {
                        return DescriptorException.targetInvocationWhileConstructorInstantiationOfFactory(getDescriptor(), (InvocationTargetException) t);
                    } else if (t instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileConstructorInstantiationOfFactory(getDescriptor(), (IllegalAccessException) t);
                    } else if (t instanceof InstantiationException) {
                        return DescriptorException.instantiationWhileConstructorInstantiationOfFactory(getDescriptor(), (InstantiationException) t);
                    } else if (t instanceof NoSuchMethodError) {
                        // This exception is not documented but gets thrown.
                        return DescriptorException.noSuchMethodWhileConstructorInstantiationOfFactory(getDescriptor(), t);
                    } else if (t instanceof NullPointerException) {
                        // Some JVMs will throw a NULL pointer exception here
                        return DescriptorException.nullPointerWhileConstructorInstantiationOfFactory(getDescriptor(), t);
                    }
                    // This indicates unexpected problem in the code
                    return new RuntimeException(String.format("Invocation of %s factory constructor failed", factoryClassName), t);
                }
        );
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
        final Method factoryMethod = buildMethod(getFactoryClass(), factoryMethodName, new Class<?>[0]);
        return PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.invokeMethod(factoryMethod, null, null),
                (ex) -> {
                    final String factoryMethodName = getFactoryMethodName();
                    if (ex instanceof IllegalAccessException) {
                        return DescriptorException.illegalAccessWhileMethodInstantiationOfFactory(factoryMethodName, getDescriptor(), ex);
                    } else if (ex instanceof InvocationTargetException) {
                        return DescriptorException.targetInvocationWhileMethodInstantiationOfFactory(factoryMethodName, getDescriptor(), ex);
                    } else if (ex instanceof NullPointerException) {
                        // Some JVMs will throw a NULL pointer exception here
                        return DescriptorException.nullPointerWhileMethodInstantiationOfFactory(factoryMethodName, getDescriptor(), ex);
                    }
                    // This indicates unexpected problem in the code
                    return new RuntimeException(String.format("Invocation of %s factory method failed", factoryMethodName), ex);
                }
        );
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
    public void convertClassNamesToClasses(final ClassLoader classLoader) {
        if (factoryClassName == null) {
            return;
        }
        setFactoryClass(
                PrivilegedAccessHelper.callDoPrivilegedWithException(
                        () -> PrivilegedAccessHelper.getClassForName(factoryClassName, true, classLoader),
                        (ex) -> ValidationException.classNotFoundWhileConvertingClassNames(factoryClassName, ex)
                )
        );
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
