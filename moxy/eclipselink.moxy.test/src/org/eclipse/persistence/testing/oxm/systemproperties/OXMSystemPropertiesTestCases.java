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
import static org.junit.Assert.assertFalse;

/**
 * Tests OXMSystemProperties class. With default values.
 */
public class OXMSystemPropertiesTestCases {

    @Test
    public void testProperties() {
        assertFalse(OXMSystemProperties.jsonTypeCompatiblity);
        assertFalse(OXMSystemProperties.jsonUseXsdTypesPrefix);
        assertEquals("type", OXMSystemProperties.jsonTypeAttributeName);
        assertFalse(OXMSystemProperties.jsonDisableNestedArrayName);
        assertEquals("", OXMSystemProperties.xmlConversionTimeSuffix);
    }
}
