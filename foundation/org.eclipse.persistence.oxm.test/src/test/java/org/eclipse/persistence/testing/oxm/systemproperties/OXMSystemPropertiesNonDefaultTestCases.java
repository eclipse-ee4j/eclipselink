/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.oxm.systemproperties;

import org.eclipse.persistence.internal.oxm.OXMSystemProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests OXMSystemProperties class. With default non-values. It requires to be isolated JVM to avoid impact to others tests or it should be executed with other tests with same system properties.
 */
public class OXMSystemPropertiesNonDefaultTestCases {

    static {
        System.setProperty(OXMSystemProperties.JSON_TYPE_COMPATIBILITY, "true");
        System.setProperty(OXMSystemProperties.JSON_USE_XSD_TYPES_PREFIX, "true");
        System.setProperty(OXMSystemProperties.JSON_TYPE_ATTRIBUTE_NAME, "jsonTestType");
        System.setProperty(OXMSystemProperties.JSON_DISABLE_NESTED_ARRAY_NAME, "true");
        System.setProperty(OXMSystemProperties.XML_CONVERSION_TIME_SUFFIX, ".0");
    }


    @Test
    public void testNonDefaultProperties() {
        assertTrue(OXMSystemProperties.jsonTypeCompatiblity);
        assertTrue(OXMSystemProperties.jsonUseXsdTypesPrefix);
        assertEquals("jsonTestType", OXMSystemProperties.jsonTypeAttributeName);
        assertTrue(OXMSystemProperties.jsonDisableNestedArrayName);
        assertEquals(".0", OXMSystemProperties.xmlConversionTimeSuffix);
    }
}
