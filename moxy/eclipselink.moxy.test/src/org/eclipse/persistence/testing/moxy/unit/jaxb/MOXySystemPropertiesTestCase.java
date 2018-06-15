/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - Martin Vojtek - 2.6.0 - Initial implementation
package org.eclipse.persistence.testing.moxy.unit.jaxb;

import static org.junit.Assert.assertTrue;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.MOXySystemProperties;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests MOXySystemProperties class.
 *
 * @author Martin Vojtek
 *
 */
@RunWith(JMockit.class)
public class MOXySystemPropertiesTestCase {

    @Test
    public void getBooleanWithSecurityManager() {

        final String propertyName = "propertyName";

        new MockUp<MOXySystemProperties>() {
            @Mock
            private Boolean runDoPrivileged(final String propertyName) {
                return true;
            }
        };

        new Expectations(PrivilegedAccessHelper.class) {{
            PrivilegedAccessHelper.shouldUsePrivilegedAccess(); result = true;
        }};

        Boolean result = Deencapsulation.invoke(MOXySystemProperties.class, "getBoolean", propertyName);

        assertTrue(result);

    }

    @Test
    public void getBooleanWithoutSecurityManager() {

        final String propertyName = "propertyName";

        new MockUp<MOXySystemProperties>() {
            @Mock
            private Boolean getSystemPropertyValue(final String propertyName) {
                return true;
            }
        };

        new Expectations(PrivilegedAccessHelper.class) {{
            PrivilegedAccessHelper.shouldUsePrivilegedAccess(); result = false;
        }};

        Boolean result = Deencapsulation.invoke(MOXySystemProperties.class, "getBoolean", propertyName);

        assertTrue(result);

    }
}
