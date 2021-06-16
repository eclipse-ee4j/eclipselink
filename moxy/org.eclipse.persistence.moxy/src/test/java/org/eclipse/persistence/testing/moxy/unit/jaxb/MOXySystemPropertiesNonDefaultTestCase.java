/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
