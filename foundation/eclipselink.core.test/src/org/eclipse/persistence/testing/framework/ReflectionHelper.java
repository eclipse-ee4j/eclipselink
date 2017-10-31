/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/14/2015-2.7 Tomas Kraus
 *       - Initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sun.misc.Unsafe;

/**
 * Reflection helper methods.
 */
public class ReflectionHelper {

    private static final Unsafe unsafe;
    static
    {
        try
        {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set value of private static field.
     * @param c        Class containing static field.
     * @param name     Static field name to be modified.
     * @param newValue New value to be set.
     * @throws NoSuchFieldException If a field with the specified name is not found.
     * @throws SecurityException If a security manager is present and access to the field was denied.
     * @throws IllegalArgumentException If an unwrapping conversion fails.
     * @throws IllegalAccessException If the underlying field is either inaccessible or final.
     */
    public static final void setPrivateStatic(final Class c, final String name, final Object newValue)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field field = c.getDeclaredField(name);
        final boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(null, newValue);
        field.setAccessible(accessible);
     }

    /**
     * Set value of private static final field.
     * Uses {@link sun.misc.Unsafe} which seems to be the only way to modify such a field in some cases.
     * @param c        Class containing static field.
     * @param name     Static field name to be modified.
     * @param newValue New value to be set.
     * @throws NoSuchFieldException If a field with the specified name is not found.
     * @throws SecurityException If a security manager is present and access to the field was denied.
     * @throws IllegalArgumentException If an unwrapping conversion fails.
     * @throws IllegalAccessException If the underlying field is either inaccessible or final.
     */
    public static final void setPrivateStaticFinal(final Class c, final String name, final Object newValue)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field field = c.getDeclaredField(name);
        final Object base = unsafe.staticFieldBase(field);
        final long offset = unsafe.staticFieldOffset(field);
        unsafe.putObject(base, offset, newValue);
     }

    /**
     * Get value of private static field.
     * @param c    Class containing static field.
     * @param name Static field name to be retrieved.
     * @return Static field value.
     * @throws NoSuchFieldException If a field with the specified name is not found.
     * @throws SecurityException If a security manager is present and access to the field was denied.
     * @throws IllegalArgumentException If an unwrapping conversion fails.
     * @throws IllegalAccessException If the underlying field is either inaccessible or final.
     */
    public static final <T> T getPrivateStatic(final Class c, final String name)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field field = c.getDeclaredField(name);
        final boolean accessible = field.isAccessible();
        field.setAccessible(true);
        final Object value = field.get(null);
        field.setAccessible(accessible);
        return (T)value;
     }

    /**
     * Invoke a method of the specified class instance.
     * @param name Method name.
     * @param obj Class instance containing method to invoke.
     * @param parameterTypes Method parameter array.
     * @param args An array of objects to be passed as arguments to the method call
     * @throws InvocationTargetException If the underlying constructor throws an exception.
     * @throws IllegalArgumentException  If an unwrapping conversion fails.
     * @throws IllegalAccessException    If the underlying field is either inaccessible or final.
     * @throws SecurityException         If a security manager is present and access to the field was denied.
     * @throws NoSuchMethodException     If a field with the specified name is not found.
     */
    public static final Object invokeMethod(
            final String name, final Object obj, final Class<?>[] parameterTypes, final Object... args)
            throws ReflectiveOperationException {
        Method m = obj.getClass().getDeclaredMethod(name, parameterTypes);
        boolean accessible = m.isAccessible();
        if (!accessible) {
            m.setAccessible(true);
        }
        Object result = m.invoke(obj, args);
        if (!accessible) {
            m.setAccessible(accessible);
        }
        return result;
    }

    /**
     * Invoke a method of the specified class instance.
     * @param name Method name.
     * @param obj Class instance containing method to invoke.
     * @param parameterTypes Method parameter array.
     * @param returnType Class to be returned.
     * @param args An array of objects to be passed as arguments to the method call
     * @throws InvocationTargetException If the underlying constructor throws an exception.
     * @throws IllegalArgumentException  If an unwrapping conversion fails.
     * @throws IllegalAccessException    If the underlying field is either inaccessible or final.
     * @throws SecurityException         If a security manager is present and access to the field was denied.
     * @throws NoSuchMethodException     If a field with the specified name is not found.
     */
    public static final <T> T invokeMethod(
            final String name, final Object obj, final Class<?>[] parameterTypes, final Class<T> returnType, final Object... args)
            throws ReflectiveOperationException {
        return returnType.cast(invokeMethod(name, obj, parameterTypes, args));
    }
    

    /**
     * Invoke static method of the specified class.
     * @param name Method name.
     * @param c Class containing method to invoke.
     * @param parameterTypes Method parameter array.
     * @param args An array of objects to be passed as arguments to the method call
     * @throws InvocationTargetException If the underlying constructor throws an exception.
     * @throws IllegalArgumentException  If an unwrapping conversion fails.
     * @throws IllegalAccessException    If the underlying field is either inaccessible or final.
     * @throws SecurityException         If a security manager is present and access to the field was denied.
     * @throws NoSuchMethodException     If a field with the specified name is not found.
     */
    public static final Object invokeStaticMethod(
            final String name, final Class c, final Class<?>[] parameterTypes, final Object... args)
            throws ReflectiveOperationException {
        Method m = c.getDeclaredMethod(name, parameterTypes);
        boolean accessible = m.isAccessible();
        if (!accessible) {
            m.setAccessible(true);
        }
        Object result = m.invoke(null, args);
        if (!accessible) {
            m.setAccessible(accessible);
        }
        return result;
    }

    /**
     * Invoke static method of the specified class.
     * @param name Method name.
     * @param c Class containing method to invoke.
     * @param parameterTypes Method parameter array.
     * @param returnType Class to be returned.
     * @param args An array of objects to be passed as arguments to the method call
     * @throws InvocationTargetException If the underlying constructor throws an exception.
     * @throws IllegalArgumentException  If an unwrapping conversion fails.
     * @throws IllegalAccessException    If the underlying field is either inaccessible or final.
     * @throws SecurityException         If a security manager is present and access to the field was denied.
     * @throws NoSuchMethodException     If a field with the specified name is not found.
     */
    public static final <T> T invokeStaticMethod(
            final String name, final Class c, final Class<?>[] parameterTypes, final Class<T> returnType, final Object... args)
            throws ReflectiveOperationException {
        return returnType.cast(invokeStaticMethod(name, c, parameterTypes, args));
    }

}
