/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     08/23/2010-2.2 Michael O'Brien
//        - 323043: application.xml module ordering may cause weaving not to occur causing an NPE.
//                       warn if expected "_persistence_//_vh" method not found
//                       instead of throwing NPE during deploy validation.
//     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
//     06/16/2015-2.7 Tomas Kraus
//       - 254437: Added getSystemProperty methods and fixed line separator property.
package org.eclipse.persistence.internal.security;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.JavaVersion;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL:
 * Privileged Access Helper provides a utility so all calls that require privileged access can use the same code.
 *
 * Do privileged blocks can be used with a security manager to grant a code base (eclipselink.jar) access to certain
 * Java operations such as reflection.  Generally a security manager is not enabled in a JVM, so this is not an issue.
 * If a security manager is enabled, then either the application can be configured to have access to operations such as
 * reflection, or only EclipseLink can be given access.  If only EclipseLink is desired to be given access then
 * do privileged must be enabled through the System property "eclipselink.security.usedoprivileged"=true.
 *
 * Note the usage of do privileged has major impacts on performance, so should normally be avoided.
 */
public class PrivilegedAccessHelper {
    private static final String TRUE_STRING = "true";
    private static boolean defaultUseDoPrivilegedValue = false;
    private static boolean shouldCheckPrivilegedAccess = true;
    private static boolean shouldUsePrivilegedAccess = false;

    private final static String[] legalProperties = { "file.separator", "java.io.tmpdir", JavaVersion.VM_VERSION_PROPERTY, "path.separator", "user.dir",
            "org.eclipse.persistence.fetchgroupmonitor", "org.eclipse.persistence.querymonitor", "SAP_J2EE_Engine_Version",
            PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, PersistenceUnitProperties.JAVASE_DB_INTERACTION,
            PersistenceUnitProperties.LOGGING_FILE, PersistenceUnitProperties.LOGGING_LEVEL,
            SystemProperties.ARCHIVE_FACTORY, SystemProperties.ENFORCE_TARGET_SERVER, SystemProperties.RECORD_STACK_ON_LOCK,
            SystemProperties.WEAVING_OUTPUT_PATH, SystemProperties.WEAVING_SHOULD_OVERWRITE, SystemProperties.WEAVING_REFLECTIVE_INTROSPECTION,
            SystemProperties.DO_NOT_PROCESS_XTOMANY_FOR_QBE, SystemProperties.ONETOMANY_DEFER_INSERTS,
            SystemProperties.CONCURRENCY_MANAGER_ACQUIRE_WAIT_TIME, SystemProperties.CONCURRENCY_MANAGER_BUILD_OBJECT_COMPLETE_WAIT_TIME, SystemProperties.CONCURRENCY_MANAGER_MAX_SLEEP_TIME,
            SystemProperties.CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_TINY_MESSAGE, SystemProperties.CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_MASSIVE_MESSAGE,
            SystemProperties.CONCURRENCY_MANAGER_ALLOW_INTERRUPTED_EXCEPTION, SystemProperties.CONCURRENCY_MANAGER_ALLOW_CONCURRENCY_EXCEPTION, SystemProperties.CONCURRENCY_MANAGER_ALLOW_STACK_TRACE_READ_LOCK,
            ServerPlatformBase.JMX_REGISTER_RUN_MBEAN_PROPERTY, ServerPlatformBase.JMX_REGISTER_DEV_MBEAN_PROPERTY,
            XMLPlatformFactory.XML_PLATFORM_PROPERTY};
    private final static Set<String> legalPropertiesSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(legalProperties)));

    private static final Set<String> exemptedPropertiesSet = Collections.emptySet();

    private static final Map<String, Class<?>> primitiveClasses;

    static {
        primitiveClasses = new HashMap<>();
        primitiveClasses.put("boolean", boolean.class);
        primitiveClasses.put("int", int.class);
        primitiveClasses.put("long", long.class);
        primitiveClasses.put("float", float.class);
        primitiveClasses.put("double", double.class);
        primitiveClasses.put("char", char.class);
        primitiveClasses.put("byte", byte.class);
        primitiveClasses.put("void", void.class);
        primitiveClasses.put("short", short.class);
    }

    /**
     * INTERNAL
     * A task that returns a result and shall not throw an exception.
     * Implementors define a single {@code call()} method with no arguments.
     *
     * @param <T> the result type of method call
     */
    @FunctionalInterface
    public interface PrivilegedCallable<T> {
        T call();
    }

    /**
     * INTERNAL
     * A task that returns a result and may throw an exception.
     * Implementors define a single {@code call()} method with no arguments.
     *
     * @param <T> the result type of method call
     */
    @FunctionalInterface
    public interface PrivilegedExceptionCallable<T> {
        T call() throws Exception;
    }

    /**
     * INTERNAL
     * A task that does not return any result and may throw an exception.
     * Implementors define a single {@code accept()} method with no arguments.
     */
    @FunctionalInterface
    public interface PrivilegedExceptionConsumer {
       void accept() throws Exception;
    }

    /**
     * INTERNAL
     * Specific {@code Exception} supplier for {@link PrivilegedExceptionCallable}.
     *
     * @param <E> specific {@link Exception} type
     */
    @FunctionalInterface
    public interface CallableExceptionSupplier<E extends Exception> {
        E get(Exception e);
    }

    /**
     * INTERNAL
     * Specific {@code Throwable} supplier for {@link PrivilegedExceptionCallable}.
     *
     * @param <T> specific {@link Throwable} type
     */
    @FunctionalInterface
    public interface CallableThrowableSupplier<T extends Throwable> {
        T get(Throwable t);
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedCallable} task using {@link AccessController#doPrivileged(PrivilegedAction)}
     * when privileged access is enabled.
     *
     * @param <T> {@link PrivilegedCallable} return type
     * @param task task to execute
     */
    @SuppressWarnings("removal")
    public static <T> T callDoPrivileged(final PrivilegedCallable<T> task) {
        if (shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged((PrivilegedAction<T>) task::call);
        } else {
            return task.call();
        }
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedExceptionCallable} task using {@link AccessController#doPrivileged(PrivilegedExceptionAction)}
     * when privileged access is enabled.
     *
     * @param <T> {@link PrivilegedExceptionCallable} return type
     * @param task task to execute
     */
    @SuppressWarnings("removal")
    public static <T> T callDoPrivilegedWithException(final PrivilegedExceptionCallable<T> task) throws Exception {
        if (shouldUsePrivilegedAccess()) {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<T>) task::call);
            // AccessController.doPrivileged wraps Exception instances with PrivilegedActionException. Let's unwrap them
            // to provide the same exception output as original callable without AccessController.doPrivileged
            } catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        } else {
            return task.call();
        }
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedExceptionCallable} task using {@link AccessController#doPrivileged(PrivilegedExceptionAction)}
     * when privileged access is enabled.
     *
     * @param <T> {@link PrivilegedExceptionCallable} return type
     * @param task task to execute
     */
    @SuppressWarnings("removal")
    public static <T> T callDoPrivilegedWithThrowable(final PrivilegedExceptionCallable<T> task) throws Throwable {
        if (shouldUsePrivilegedAccess()) {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<T>) task::call);
                // AccessController.doPrivileged wraps Exception instances with PrivilegedActionException. Let's unwrap them
                // to provide the same exception output as original callable without AccessController.doPrivileged
            } catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        } else {
            return task.call();
        }
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedExceptionConsumer} task using {@link AccessController#doPrivileged(PrivilegedExceptionAction)}
     * when privileged access is enabled.
     *
     * @param task task to execute
     */
    @SuppressWarnings("removal")
    public static void callDoPrivilegedWithException(final PrivilegedExceptionConsumer task) throws Exception {
        if (shouldUsePrivilegedAccess()) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
                    task.accept();
                    return null;
                });
                // AccessController.doPrivileged wraps Exception instances with PrivilegedActionException. Let's unwrap them
                // to provide the same exception output as original callable without AccessController.doPrivileged
            } catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        } else {
            task.accept();
        }
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedExceptionCallable} task using {@link AccessController#doPrivileged(PrivilegedExceptionAction)}
     * when privileged access is enabled.
     * If {@link Exception} is thrown from task, it will be processed by provided {@link CallableExceptionSupplier}.
     *
     * @param <T> {@link PrivilegedExceptionCallable} return type
     * @param <E> specific {@link Exception} type
     * @param task task to execute
     * @param exception specific {@link Exception} supplier
     */
    @SuppressWarnings("removal")
    public static <T,E extends Exception> T callDoPrivilegedWithException(
            final PrivilegedExceptionCallable<T> task, final CallableExceptionSupplier<E> exception) throws E {
        try {
            return callDoPrivilegedWithException(task);
        } catch (Exception e) {
            throw exception.get(e);
        }
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedExceptionCallable} task using {@link AccessController#doPrivileged(PrivilegedExceptionAction)}
     * when privileged access is enabled.
     * If {@link Throwable} is thrown from task, it will be processed by provided {@link CallableThrowableSupplier}.
     *
     * @param <T> {@link PrivilegedExceptionCallable} return type
     * @param <E> specific {@link Throwable} type
     * @param task task to execute
     * @param throwable specific {@link Throwable} supplier
     */
    @SuppressWarnings("removal")
    public static <T,E extends Throwable> T callDoPrivilegedWithThrowable(
            final PrivilegedExceptionCallable<T> task, final CallableThrowableSupplier<E> throwable) throws E {
        try {
            return callDoPrivilegedWithThrowable(task);
        } catch (Throwable e) {
            throw throwable.get(e);
        }
    }

    /**
     * INTERNAL
     * Executes provided {@link PrivilegedExceptionConsumer} task using {@link AccessController#doPrivileged(PrivilegedExceptionAction)}
     * when privileged access is enabled.
     * If {@link Exception} is thrown from task, it will be processed by provided {@link CallableExceptionSupplier}.
     *
     * @param <E> specific {@link Exception} type
     * @param task task to execute
     * @param exception specific {@link Exception} supplier
     */
    @SuppressWarnings("removal")
    public static <E extends Exception> void callDoPrivilegedWithException(
            final PrivilegedExceptionConsumer task, final CallableExceptionSupplier<E> exception) throws E {
        try {
            callDoPrivilegedWithException(task);
        } catch (Exception e) {
            throw exception.get(e);
        }
    }

    /**
     * INTERNAL
     * It will be used to set default value of property "eclipselink.security.usedoprivileged"
     * if not passed as system property. This is used by GlassfishPlatform.
     */
    public static void setDefaultUseDoPrivilegedValue(boolean def) {
        defaultUseDoPrivilegedValue = def;
        shouldCheckPrivilegedAccess = true;
    }

    /**
     * Finding a field within a class potentially has to navigate through it's superclasses to eventually
     * find the field.  This method is called by the public getDeclaredField() method and does a recursive
     * search for the named field in the given classes or it's superclasses.
     */
    private static <T> Field findDeclaredField(Class<T> javaClass, String fieldName) throws NoSuchFieldException {
        try {
            return javaClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            Class<? super T> superclass = javaClass.getSuperclass();
            if (superclass == null) {
                throw ex;
            } else {
                return findDeclaredField(superclass, fieldName);
            }
        }
    }

    /**
     * Finding a method within a class potentially has to navigate through it's superclasses to eventually
     * find the method.  This method is called by the public getDeclaredMethod() method and does a recursive
     * search for the named method in the given classes or it's superclasses.
     */
    private static <T> Method findMethod(Class<T> javaClass, String methodName, Class<?>[] methodParameterTypes) throws NoSuchMethodException {
        try {
            // use a combination of getDeclaredMethod() and recursion to ensure we get the non-public methods
            // getMethod will not help because it returns only public methods
            return javaClass.getDeclaredMethod(methodName, methodParameterTypes);
        } catch (NoSuchMethodException ex) {
            Class<? super T> superclass = javaClass.getSuperclass();
            if (superclass == null) {
                throw ex;
            } else {
                try{
                    return findMethod(superclass, methodName, methodParameterTypes);
                }catch (NoSuchMethodException lastEx){
                    throw ex;
                }
            }
        }
    }

    /**
     * Execute a java Class.forName().  Wrap the call in a doPrivileged block if necessary.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Class<T> getClassForName(final String className) throws ClassNotFoundException {
        // Check for primitive types.
        Class<?> primitive = primitiveClasses.get(className);
        if (primitive != null) {
            return (Class<T>) primitive;
        }
        return (Class<T>) Class.forName(className);
    }

    /**
     * Execute a java Class.forName() wrap the call in a doPrivileged block if necessary.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Class<T> getClassForName(final String className, final boolean initialize, final ClassLoader loader) throws ClassNotFoundException {
        // Check for primitive types.
        Class<?> primitive = primitiveClasses.get(className);
        if (primitive != null) {
            return (Class<T>) primitive;
        }
        return (Class<T>) Class.forName(className, initialize, loader);
    }

    /**
     * Gets the class loader for a given class. Wraps the call in a privileged block if necessary
     */
    public static ClassLoader getClassLoaderForClass(final Class<?> clazz) {
        return clazz.getClassLoader();
    }

    /**
     * Get the public constructor for the given class and given arguments and wrap it in doPrivileged if
     * necessary.  The shouldSetAccessible parameter allows the the setAccessible API to be called as well.
     * This option was added to avoid making multiple doPrivileged calls within InstantiationPolicy.
     * @param javaClass The class to get the Constructor for
     * @param args An array of classes representing the argument types of the constructor
     * @param shouldSetAccessible whether or not to call the setAccessible API
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Constructor<T> getConstructorFor(final Class<T> javaClass, final Class<?>[] args, final boolean shouldSetAccessible) throws NoSuchMethodException {
        Constructor<T> result = null;
        try {
            result = javaClass.getConstructor(args);
        } catch (NoSuchMethodException missing) {
            // Search for any constructor with the same number of arguments and assignable types.
            for (Constructor<?> constructor : javaClass.getConstructors()) {
                if (constructor.getParameterTypes().length == args.length) {
                    boolean found = true;
                    for (int index = 0; index < args.length; index++) {
                        Class<?> parameterType = Helper.getObjectClass(constructor.getParameterTypes()[index]);
                        Class<?> argType = Helper.getObjectClass(args[index]);
                        if ((!parameterType.isAssignableFrom(argType))
                                && (!argType.isAssignableFrom(parameterType))) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        result = (Constructor<T>) constructor;
                        break;
                    }
                }
            }
            if (result == null) {
                throw missing;
            }
        }
        if (shouldSetAccessible) {
            if (!result.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible",
                        "constructor", javaClass.getName());
            }
        }
        return result;
    }

    /**
     *  Get the context ClassLoader for a thread.  Wrap the call in a doPrivileged block if necessary.
     */
    public static ClassLoader getContextClassLoader(final Thread thread) {
        return thread.getContextClassLoader();
    }

    /**
     * Get the constructor for the given class and given arguments (regardless of whether it is public
     * or private))and wrap it in doPrivileged if necessary.  The shouldSetAccessible parameter allows
     * the the setAccessible API to be called as well. This option was added to avoid making multiple
     * doPrivileged calls within InstantiationPolicy.
     * @param javaClass The class to get the Constructor for
     * @param args An array of classes representing the argument types of the constructor
     * @param shouldSetAccessible whether or not to call the setAccessible API
     */
    public static <T> Constructor<T> getDeclaredConstructorFor(final Class<T> javaClass, final Class<?>[] args, final boolean shouldSetAccessible) throws NoSuchMethodException {
        Constructor<T> result = javaClass.getDeclaredConstructor(args);
        if (shouldSetAccessible) {
            if (!result.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible",
                        "declared constructor", javaClass.getName());
            }
        }
        return result;
    }

    /**
     * Get a field in a class or its superclasses and wrap the call in doPrivileged if necessary.
     * The shouldSetAccessible parameter allows the the setAccessible API to be called as well.
     * This option was added to avoid making multiple doPrivileged calls within InstanceVariableAttributeAccessor.
     * @param javaClass The class to get the field from
     * @param fieldName The name of the field
     * @param shouldSetAccessible whether or not to call the setAccessible API
     */
    public static Field getField(final Class<?> javaClass, final String fieldName, final boolean shouldSetAccessible) throws NoSuchFieldException {
        Field field = findDeclaredField(javaClass, fieldName);
        if (shouldSetAccessible) {
            if (!field.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible_in",
                        "field", fieldName, javaClass.getName());
            }
        }
        return field;
    }

    /**
     * Get a field actually declared in a class and wrap the call in doPrivileged if necessary.
     * The shouldSetAccessible parameter allows the the setAccessible API to be called as well.
     * This option was added to avoid making multiple doPrivileged calls within InstanceVariableAttributeAccessor.
     * @param javaClass The class to get the field from
     * @param fieldName The name of the field
     * @param shouldSetAccessible whether or not to call the setAccessible API
     */
    public static Field getDeclaredField(final Class<?> javaClass, final String fieldName, final boolean shouldSetAccessible) throws NoSuchFieldException {
        Field field = javaClass.getDeclaredField(fieldName);
        if (shouldSetAccessible) {
            if (!field.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible_in",
                        "declared field", fieldName, javaClass.getName());
            }
        }
        return field;
    }

    /**
     * Get the list of fields in a class.  Wrap the call in doPrivileged if necessary
     * Excludes inherited fields.
     * @param clazz the class to get the fields from.
     */
    public static Field[] getDeclaredFields(final Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * Get the list of public fields in a class.  Wrap the call in doPrivileged if necessary
     * @param clazz the class to get the fields from.
     */
    public static Field[] getFields(final Class<?> clazz) {
        return clazz.getFields();
    }

   /**
     * Return a method on a given class with the given method name and parameter
     * types. This call will NOT traverse the superclasses. Wrap the call in
     * doPrivileged if necessary.
     * @param clazz the class to get the method from
     * @param methodName the name of the method to get
     * @param methodParameterTypes a list of classes representing the classes of the
     *  parameters of the method.
     */
    public static Method getDeclaredMethod(final Class<?> clazz, final String methodName, final Class<?>[] methodParameterTypes) throws NoSuchMethodException {
         return clazz.getDeclaredMethod(methodName, methodParameterTypes);
    }

    /**
     * Get a method declared in the given class. Wrap the call in doPrivileged
     * if necessary. This call will traverse the superclasses. The
     * shouldSetAccessible parameter allows the the setAccessible API to be
     * called as well. This option was added to avoid making multiple
     * doPrivileged calls within MethodBasedAttributeAccessor.
     * @param javaClass The class to get the method from
     * @param methodName The name of the method to get
     * @param methodParameterTypes A list of classes representing the classes of the parameters of the mthod
     * @param shouldSetAccessible whether or not to call the setAccessible API
     */
    public static Method getMethod(final Class<?> javaClass, final String methodName, final Class<?>[] methodParameterTypes, final boolean shouldSetAccessible) throws NoSuchMethodException {
        Method method = findMethod(javaClass, methodName, methodParameterTypes);
        if (shouldSetAccessible) {
            if (!method.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible_in",
                        "method", methodName, javaClass.getName());
            }
        }
        return method;
    }

    /**
     * Get a public method declared in the given class. Wrap the call in doPrivileged if necessary.
     * This call will traverse the superclasses. The shouldSetAccessible parameter allows the the
     * setAccessible API to be called as well. This option was added to avoid making multiple
     * doPrivileged calls within MethodBasedAttributeAccessor.
     *
     * @param javaClass The class to get the method from
     * @param methodName The name of the method to get
     * @param methodParameterTypes A list of classes representing the classes of the parameters of the method
     * @param shouldSetAccessible whether or not to call the setAccessible API
     */
    public static Method getPublicMethod(final Class<?> javaClass, final String methodName, final Class<?>[] methodParameterTypes, final boolean shouldSetAccessible) throws NoSuchMethodException {
        // Return the (public) method - will traverse superclass(es) if necessary
        Method method = javaClass.getMethod(methodName, methodParameterTypes);
        if (shouldSetAccessible) {
            if (!method.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible_in",
                        "public method", methodName, javaClass.getName());
            }
        }
        return method;
    }

    /**
     * Get the list of methods in a class. Wrap the call in doPrivileged if
     * necessary. Excludes inherited methods.
     * @param clazz the class to get the methods from.
     */
    public static Method[] getDeclaredMethods(final Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }

    /**
     * Get the return type for a given method. Wrap the call in doPrivileged if necessary.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Class<T> getFieldType(final Field field) {
        return (Class<T>) field.getType();
    }

    /**
     * Check if {@code getSystemProperty} is allowed for provided property key.
     * @param key The name of the {@link System} property.
     * @return Value of {@code true} if {@code getSystemProperty} is allowed for this property key
     *         or {@code false} otherwise.
     */
    private static boolean isIllegalProperty(final String key) {
        return key == null || !(legalPropertiesSet.contains(key) || key.startsWith("eclipselink.")
                || key.startsWith("jakarta.persistence.") || key.startsWith("org.eclipse.persistence.")
                || key.startsWith("persistence.") || key.startsWith("javax.xml.") || key.startsWith("jakarta.xml."));
    }

    /**
     * Vet certain System properties as "safe" enough to override defaultUseDoPrivilegedValue setting.  Reserved
     * for properties that must be read before the application server runtime environment is known.  Consider
     * security implications very carefully before adding new system property names to this list.
     * @param key The name of the {@link System} property.
     * @return Value of {@code true} if {@code getSystemProperty} is allowed defaultUseDoPrivilegedValue override
     *         or {@code false} otherwise.
     */
    private static boolean isExemptedProperty(final String key) {
        return key != null && exemptedPropertiesSet.contains(key);
    }

    /**
     * INTERNAL:
     * Get the {@link System} property indicated by the specified {@code key}.
     * @param key The name of the {@link System} property.
     * @return The {@link String} value of the system property or {@code null} if property identified by {@code key}
     *         does not exist.
     * @since 2.6.2
     */
    public static String getSystemProperty(final String key) {
        if (isIllegalProperty(key)) {
            throw new IllegalArgumentException(
                    ExceptionLocalization.buildMessage("unexpect_argument", new Object[] {key}));
        }
        if (isExemptedProperty(key) || shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetSystemProperty(key));
        } else {
            return System.getProperty(key);
        }
    }

    /**
     * INTERNAL:
     * Get the {@link System} property indicated by the specified {@code key}.
     * @param key The name of the {@link System} property.
     * @param def The default value.
     * @return The {@link String} value of the system property or {@code def} if property identified by {@code key}
     *         does not exist.
     * @since 2.6.2
     */
    public static String getSystemProperty(final String key, final String def) {
        if (isIllegalProperty(key)) {
            throw new IllegalArgumentException(
                    ExceptionLocalization.buildMessage("unexpect_argument", new Object[] {key}));
        }
        if (isExemptedProperty(key) || shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetSystemProperty(key, def));
        } else {
            return System.getProperty(key, def);
        }
    }

    /**
     * INTERNAL:
     * Get boolean value of the {@link System} property indicated by the specified {@code key}.
     * @param key The name of the {@link System} property.
     * @param def The default value.
     * @return {@code true} if the property value is {@code "true"} (case insensitive)
     *         or the property is not defined and {@code def} is {@code true};
     *         {@code false} otherwise.
     * @since 2.6.3
     */
    public static boolean getSystemPropertyBoolean(final String key, final boolean def) {
        return TRUE_STRING.equalsIgnoreCase(
                getSystemProperty(key, def ? TRUE_STRING : ""));
    }

    /**
     * INTERNAL:
     * Get the line separator character.
     * @return The {@link String} containing the platform-appropriate characters for line separator.
     * @deprecated Use {@link System#lineSeparator()}
     */
    public static String getLineSeparator() {
        return System.lineSeparator();
    }

    /**
     * Get the list of parameter types for a given method.  Wrap the call in doPrivileged if necessary.
     * @param method The method to get the parameter types of
     */
    public static Class<?>[] getMethodParameterTypes(final Method method) {
        return method.getParameterTypes();
    }

    /**
     * Get the return type for a given method. Wrap the call in doPrivileged if necessary.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Class<T> getMethodReturnType(final Method method) {
        // 323148: a null method as a possible problem with module ordering breaking weaving - has been trapped by implementors of this method.
        return (Class<T>) method.getReturnType();
    }

    /**
     * Get the list of methods in a class. Wrap the call in doPrivileged if
     * necessary. This call will traverse the superclasses.
     * @param clazz the class to get the methods from.
     */
    public static Method[] getMethods(final Class<?> clazz) {
        return clazz.getMethods();
    }

    /**
     * Get the value of the given field in the given object.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getValueFromField(final Field field, final Object object) throws IllegalAccessException {
        return (T) field.get(object);
    }

    /**
     * Construct an object with the given Constructor and the given array of arguments.  Wrap the call in a
     * doPrivileged block if necessary.
     */
    public static <T> T invokeConstructor(final Constructor<T> constructor, final Object[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(args);
    }

    /**
     * Invoke the givenMethod on a givenObject. Assumes method does not take
     * parameters. Wrap in a doPrivileged block if necessary.
     */
    public static <T> T invokeMethod(final Method method, final Object object) throws IllegalAccessException, InvocationTargetException {
        return invokeMethod(method, object, null);
    }

    /**
     * Invoke the givenMethod on a givenObject using the array of parameters given.  Wrap in a doPrivileged block
     * if necessary.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T invokeMethod(final Method method, final Object object, final Object[] parameters) throws IllegalAccessException, InvocationTargetException {
        // Ensure the method is accessible.
        if (!method.isAccessible()) {
            if (!method.trySetAccessible()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC, "set_accessible_in",
                        "method", method.getName(), method.getDeclaringClass().getName() + " for invokation");
            }
        }
        return (T) method.invoke(object, parameters);
    }

    /**
     * Get a new instance of a class using the default constructor.  Wrap the call in a privileged block
     * if necessary.
     */
    public static <T> T newInstanceFromClass(final Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    /**
     * Set the value of a given field in the given object with the value given.  Wrap the call in a privileged block
     * if necessary.
     */
    public static void setValueInField(final Field field, final Object object, final Object value) throws IllegalAccessException {
        field.set(object, value);
    }

    /**
     * This method checks to see if calls should be made to doPrivileged.
     * It will only return true if a security manager is enabled,
     * and the "eclipselink.security.usedoprivileged" property is set.
     * <p>
     * Note: it is not possible to run EclipseLink using doPrivileged blocks when there is no SecurityManager
     * enabled.
     */
    public static boolean shouldUsePrivilegedAccess() {
        // #shouldUsePrivilegedAccess will be evaluated the first time this method is called and then 'reevaluated' only
        // after explicit call to #setDefaultUseDoPrivilegedValue().
        if (shouldCheckPrivilegedAccess) {
            if (System.getSecurityManager() != null) {
                String usePrivileged = AccessController.doPrivileged(new PrivilegedGetSystemProperty("eclipselink.security.usedoprivileged"));
                if (usePrivileged == null) {
                    shouldUsePrivilegedAccess = defaultUseDoPrivilegedValue;
                } else {
                    shouldUsePrivilegedAccess = usePrivileged.equalsIgnoreCase(TRUE_STRING);
                }
            } else {
                shouldUsePrivilegedAccess = false;
            }
            shouldCheckPrivilegedAccess = false;
        }
        return shouldUsePrivilegedAccess;
    }
}
