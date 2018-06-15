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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.unit;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;

import junit.framework.TestCase;

public class EMFProviderTest extends TestCase {

    private final Map<String, String> overrides = new HashMap<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("eclipselink.testProp", "elTestPropValue");
        System.setProperty("javax.persistence.testProp", "jxpTestPropValue");
        System.setProperty("persistence.testProp", "perTestPropValue");
        System.setProperty(PersistenceUnitProperties.JAVASE_DB_INTERACTION, "dbTestPropValue");
        System.setProperty("java.testProp", "should not be able to read this!");
        overrides.put("java.testProp", "some/path");
        overrides.put("eclipselink.testProp", "elOverride");
        overrides.put("javax.persistence.testProp", "jxpOverride");
        overrides.put("persistence.testProp", "perOverride");
        overrides.put(PersistenceUnitProperties.JAVASE_DB_INTERACTION, "dbOverride");
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        for (String propKey : overrides.keySet()) {
            System.clearProperty(propKey);
        }
        assertNull(System.getProperty("java.testProp"));
        assertNull(System.getProperty("eclipselink.testProp"));
        assertNull(System.getProperty("javax.persistence.testProp"));
        assertNull(System.getProperty("persistence.testProp"));
        assertNull(System.getProperty(PersistenceUnitProperties.JAVASE_DB_INTERACTION));
    }

    public void testGetConfigProperty() {
        assertEquals("elTestPropValue", EntityManagerFactoryProvider.getConfigPropertyAsString("eclipselink.testProp", null));
        assertEquals("jxpTestPropValue", EntityManagerFactoryProvider.getConfigPropertyAsString("javax.persistence.testProp", null));
        assertEquals("perTestPropValue", EntityManagerFactoryProvider.getConfigPropertyAsString("persistence.testProp", null));
        assertEquals("dbTestPropValue", EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.JAVASE_DB_INTERACTION, null));
        try {
            assertEquals("should not be able to read this!", EntityManagerFactoryProvider.getConfigPropertyAsString("java.testProp", null));
            fail("should not read JVM property");
        } catch (IllegalArgumentException ie) {
            //expected
        }
        assertEquals("elOverride", EntityManagerFactoryProvider.getConfigPropertyAsString("eclipselink.testProp", overrides));
        assertEquals("jxpOverride", EntityManagerFactoryProvider.getConfigPropertyAsString("javax.persistence.testProp", overrides));
        assertEquals("perOverride", EntityManagerFactoryProvider.getConfigPropertyAsString("persistence.testProp", overrides));
        assertEquals("dbOverride", EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.JAVASE_DB_INTERACTION, overrides));
        assertEquals("some/path", EntityManagerFactoryProvider.getConfigPropertyAsString("java.testProp", overrides));
    }
}
