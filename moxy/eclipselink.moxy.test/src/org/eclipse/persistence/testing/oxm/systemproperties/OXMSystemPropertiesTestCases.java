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
        assertEquals("", OXMSystemProperties.xmlConversionTimeSuffix);
    }
}
