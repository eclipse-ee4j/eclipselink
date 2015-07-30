/*******************************************************************************
 * Copyright (c) 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Tomas Kraus - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.logging;

import static org.junit.Assert.assertEquals;

import org.eclipse.persistence.testing.framework.ReflectionHelper;

/**
 * jUnit tests helper that allows {@link org.eclipse.persistence.logging.LogCategory}
 * methods access.
 */
public class LogCategoryHelper {

    /**
     * Test {@link LogCategory} enumeration length (items count).
     */
    public static void testLength() {
        assertEquals("Logging category length value is incorrect", LogCategory.values().length, LogCategory.length);
    }

    /**
     * Test {@code LogCategory.toValue(String)} method.
     */
    public static void testToValue() {
        for (LogCategory category : LogCategory.values()) {
            String name = category.getName();
            String lower = name.toLowerCase();
            String upper = name.toUpperCase();
            LogCategory categoryFromName = LogCategory.toValue(name);
            LogCategory categoryFromLower = LogCategory.toValue(lower);
            LogCategory categoryFromUpper = LogCategory.toValue(upper);
            assertEquals("Logging category was not found for " + name, category, categoryFromName);
            assertEquals("Logging category was not found for " + lower, category, categoryFromLower);
            assertEquals("Logging category was not found for " + upper, category, categoryFromUpper);
        }

    }

    /**
     * Test {@code LogCategory.getNameSpace()} method.
     * @throws NoSuchFieldException If there is a problem in {@code ReflectionHelper} call.
     * @throws SecurityException If there is a problem in {@code ReflectionHelper} call.
     * @throws IllegalArgumentException If there is a problem in {@code ReflectionHelper} call.
     * @throws IllegalAccessException If there is a problem in {@code ReflectionHelper} call.
     */
    public static void testGetNameSpace()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final String namespacePrefix = (String)ReflectionHelper.getPrivateStatic(LogCategory.class, "NAMESPACE_PREFIX");
        for (LogCategory category : LogCategory.values()) {
            String name = category.getName();
            StringBuilder sb = new StringBuilder(namespacePrefix.length() + name.length());
            sb.append(namespacePrefix);
            sb.append(name);
            String nameSpace = sb.toString();
            assertEquals("Logger name space shall be " + nameSpace, nameSpace, category.getNameSpace());
        }
    }

}
