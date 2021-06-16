/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
import static org.junit.Assert.assertFalse;

/**
 * Tests MOXySystemProperties class. With default values.
 */
public class MOXySystemPropertiesTestCase {

    @Test
    public void testProperties() {
        assertFalse(MOXySystemProperties.xmlIdExtension);
        assertFalse(MOXySystemProperties.xmlValueExtension);
        assertFalse(MOXySystemProperties.jsonTypeCompatibility);
        assertFalse(MOXySystemProperties.jsonUseXsdTypesPrefix);
        assertEquals("INFO", MOXySystemProperties.moxyLoggingLevel);
        assertFalse(MOXySystemProperties.moxyLogPayload);
    }

}
