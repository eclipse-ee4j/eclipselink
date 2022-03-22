/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - Martin Vojtek - 2.6.0 - Initial implementation
//  - Radek Felcman, Tomas Kraus - 2.7 - Security manager removal
package org.eclipse.persistence.testing.moxy.unit.jaxb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.MOXySystemProperties;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests MOXySystemProperties class using test specific properties.
 */
public class MOXySystemPropertiesTestCase {

    private static final String PROPERTY_TRUE_VALUE = "eclipselink.moxy.unit.jaxb.MOXySystemPropertiesTestCase.true_value";
    private static final String PROPERTY_FALSE_VALUE = "eclipselink.moxy.unit.jaxb.MOXySystemPropertiesTestCase.false_value";
    private static final String PROPERTY_STRING_VALUE = "eclipselink.moxy.unit.jaxb.MOXySystemPropertiesTestCase.string_value";

    private static final Boolean getBooleanInvoke(final String name) {
        Method mh;
        try {
            mh = PrivilegedAccessHelper.getMethod(MOXySystemProperties.class, "getBoolean", new Class<?>[] {String.class} , false);
            return PrivilegedAccessHelper.invokeMethod(mh, null, new Object[] {name});
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTrueProperty() {
        Boolean trueValue = getBooleanInvoke(PROPERTY_TRUE_VALUE);
        assertTrue(trueValue);
    }

    @Test
    public void testFalseProperty() {
        Boolean falseValue = getBooleanInvoke(PROPERTY_FALSE_VALUE);
        assertFalse(falseValue);
    }

    @Test
    public void testStringProperty() {
        String stringValue = PrivilegedAccessHelper.getSystemProperty(PROPERTY_STRING_VALUE);
        assertEquals("STRING", stringValue);
    }

}
