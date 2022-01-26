/*******************************************************************************
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.systemproperties;

import org.eclipse.persistence.internal.oxm.OXMSystemProperties;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests OXMSystemProperties class. With default non-values. It requires to be isolated JVM to avoid impact to others tests or it should be executed with other tests with same system properties.
 */
public class OXMSystemPropertiesNonDefaultTestCases {

    static {
        System.setProperty(OXMSystemProperties.JSON_TYPE_COMPATIBILITY, "true");
        System.setProperty(OXMSystemProperties.JSON_USE_XSD_TYPES_PREFIX, "true");
        System.setProperty(OXMSystemProperties.XML_CONVERSION_TIME_SUFFIX, ".0");
    }


    @Test
    public void testNonDefaultProperties() {
        assertTrue(OXMSystemProperties.jsonTypeCompatiblity);
        assertTrue(OXMSystemProperties.jsonUseXsdTypesPrefix);
        assertEquals(".0", OXMSystemProperties.xmlConversionTimeSuffix);
    }
}
