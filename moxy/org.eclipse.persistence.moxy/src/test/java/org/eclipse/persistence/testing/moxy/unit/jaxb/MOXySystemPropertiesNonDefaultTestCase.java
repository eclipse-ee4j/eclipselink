/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.moxy.unit.jaxb;

import org.eclipse.persistence.jaxb.MOXySystemProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests MOXySystemProperties class. With default non-values. It requires isolated JVM to avoid impact to others tests or it should be executed with other tests with same system properties.
 */
public class MOXySystemPropertiesNonDefaultTestCase {

    static {
        System.setProperty(MOXySystemProperties.XML_ID_EXTENSION, "true");
        System.setProperty(MOXySystemProperties.XML_VALUE_EXTENSION, "true");
        System.setProperty(MOXySystemProperties.JSON_TYPE_COMPATIBILITY, "true");
        System.setProperty(MOXySystemProperties.JSON_USE_XSD_TYPES_PREFIX, "true");
        System.setProperty(MOXySystemProperties.MOXY_LOGGING_LEVEL, "FINE");
        System.setProperty(MOXySystemProperties.MOXY_LOG_PAYLOAD, "true");
    }

    @Test
    public void testProperties() {
        assertTrue(MOXySystemProperties.xmlIdExtension);
        assertTrue(MOXySystemProperties.xmlValueExtension);
        assertTrue(MOXySystemProperties.jsonTypeCompatibility);
        assertTrue(MOXySystemProperties.jsonUseXsdTypesPrefix);
        assertEquals("FINE", MOXySystemProperties.moxyLoggingLevel);
        assertTrue(MOXySystemProperties.moxyLogPayload);
    }
}
