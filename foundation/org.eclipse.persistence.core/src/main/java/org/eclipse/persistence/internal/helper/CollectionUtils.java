/*
 * Copyright (c) 2024, 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.persistence.internal.helper;

import java.util.Collection;
import java.util.Map;

/**
 * An assortment of various utility methods.
 *
 * @author Arjan Tijms
 *
 */
public final class CollectionUtils {

    public static final String EMPTY_STRING = "";

    private CollectionUtils() {}

    public static boolean notNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }

        return true;
    }

    /**
        * Returns true if the given string is null or is blank.
        *
        * @param string The string to be checked on blankness.
        * @return True if the given string is null or is blank.
        */
       public static boolean isBlank(String string) {
           return string == null || string.isBlank();
       }

    /**
     * Returns true if the given string is null or is empty.
     *
     * @param string The string to be checked on emptiness.
     * @return True if the given string is null or is empty.
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Returns <code>true</code> if the given array is null or is empty.
     *
     * @param array The array to be checked on emptiness.
     * @return <code>true</code> if the given array is null or is empty.
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns <code>true</code> if the given collection is null or is empty.
     *
     * @param collection The collection to be checked on emptiness.
     * @return <code>true</code> if the given collection is null or is empty.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns <code>true</code> if the given map is null or is empty.
     *
     * @param map The map to be checked on emptiness.
     * @return <code>true</code> if the given map is null or is empty.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Returns <code>true</code> if the given object equals one of the given objects.
     * @param <T> The generic object type.
     * @param object The object to be checked if it equals one of the given objects.
     * @param objects The argument list of objects to be tested for equality.
     * @return <code>true</code> if the given object equals one of the given objects.
     */
    @SafeVarargs
    public static <T> boolean isOneOf(T object, T... objects) {
        for (Object other : objects) {
            if (object == null ? other == null : object.equals(other)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAnyNull(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }

        return false;
    }

}
