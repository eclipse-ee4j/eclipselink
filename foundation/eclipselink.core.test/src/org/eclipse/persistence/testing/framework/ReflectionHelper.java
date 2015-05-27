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
    public static final Object getPrivateStatic(final Class c, final String name)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field field = c.getDeclaredField(name);
        final boolean accessible = field.isAccessible();
        field.setAccessible(true);
        final Object value = field.get(null);
        field.setAccessible(accessible);
        return value;
     }

}
