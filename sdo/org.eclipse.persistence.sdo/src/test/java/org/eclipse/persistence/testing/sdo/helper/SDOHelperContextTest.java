/*
 * Copyright (c) 2015, 2023 Oracle and/or its affiliates. All rights reserved.
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
// Iaroslav Savytskyi - initial implementation
package org.eclipse.persistence.testing.sdo.helper;

import commonj.sdo.helper.HelperContext;
import junit.framework.TestCase;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import static org.eclipse.persistence.sdo.SDOSystemProperties.SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * TestCase for SDOHelperContext
 */
public class SDOHelperContextTest extends TestCase {

    private String strictTypeCheckingPropertyValueBackup;

    private static Map getMap() throws NoSuchFieldException, IllegalAccessException {
        Field mapField = SDOHelperContext.class.getDeclaredField("HELPER_CONTEXT_RESOLVERS");
        mapField.setAccessible(true);
        return (Map) mapField.get(null);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getMap().clear();
        strictTypeCheckingPropertyValueBackup = System.getProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME);
        System.clearProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (strictTypeCheckingPropertyValueBackup != null) {
            System.setProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME, strictTypeCheckingPropertyValueBackup);
        } else {
            System.clearProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME);
        }
        strictTypeCheckingPropertyValueBackup = null;
    }

    public void testSetNullHelperContextResolver() {
        SDOHelperContext.setHelperContextResolver(null);
        assertNotNull("Expected that HelperContextResolver coulnd't be null", SDOHelperContext.getHelperContextResolver());
    }

    public void testSetBadHelperContextResolver() {
        try {
            SDOHelperContext.setHelperContextResolver(new Object());
            fail("Expected exception to be thrown if incorrect helperContextResolver object is set");
        } catch (Exception e) {
            // pass
        }
    }

    public void testDefaultImplementationIsSet() {
        SDOHelperContext.HelperContextResolver hcr = SDOHelperContext.getHelperContextResolver();
        assertNotNull("Default HelperContextResolver shouldn't be null", hcr);
        assertNotNull("Default HelperContext shouldn't be null", hcr.getHelperContext(null, this.getClass().getClassLoader()));
    }

    public void testDefaultIsUsedIfNothigSet() {
        assertNotNull("Default HelperContext shouldn't be null", SDOHelperContext.getHelperContext("1_unique_id"));
    }

    public void testResolverMethod() {
        TestResolver tr = new TestResolver();
        SDOHelperContext.setHelperContextResolver(tr);
        SDOHelperContext hc = (SDOHelperContext) SDOHelperContext.getHelperContext("2_unique_id_");
        assertEquals("Expected helperContext generated with HelperContextResolver to be returned", hc, tr.foo);
    }

    public void testInheritedResolverMethod() {
        TestResolver tr = new ChildResolver();
        SDOHelperContext.setHelperContextResolver(tr);
        SDOHelperContext hc = (SDOHelperContext) SDOHelperContext.getHelperContext("3_unique_id__");
        assertEquals("Expected helperContext generated with HelperContextResolver to be returned", hc, tr.foo);
    }

    public void testNewContextGeneratedWithResolverReturned() {
        SDOHelperContext foo = new SDOHelperContext("Foo");
        SDOHelperContext.setHelperContextResolver((id, classLoader) -> foo);

        SDOHelperContext hc = (SDOHelperContext) SDOHelperContext.getHelperContext("4_unique_id___");
        assertEquals("Expected helperContext generated with HelperContextResolver to be returned", hc, foo);
        SDOHelperContext.setHelperContextResolver(null);
        hc = (SDOHelperContext) SDOHelperContext.getHelperContext("5_unique_id____");
        assertFalse("Expected helperContext to be different", foo.equals(hc));
    }

    public void testResolverCleanUp() throws Exception {
        Map map = getMap();
        SDOHelperContext.setHelperContextResolver(new TestResolver());
        assertTrue("Expected 1 HelperContextResolver to be set", map.size() == 1);
        Method reset = SDOHelperContext.class.getDeclaredMethod("resetHelperContext", String.class);
        reset.setAccessible(true);
        reset.invoke(null, "");
        assertTrue("Expected map to be empty", map.isEmpty());
    }

    public void testResolverRemove() throws Exception {
        Map map = getMap();
        SDOHelperContext.setHelperContextResolver(new TestResolver());
        assertTrue("Expected 1 HelperContextResolver to be set", map.size() == 1);
        SDOHelperContext.removeHelerContextResolver();
        assertTrue("Expected map to be empty", map.isEmpty());
    }

    /**
     * Checks default value of {@link SDOHelperContext#isStrictTypeCheckingEnabled()}.
     */
    public void testTypeCheckingStrictnessFlagDefault() {
        SDOHelperContext ctx = new SDOHelperContext("testHelperContext");
        assertTrue("Expected default value of SDOHelperContext#isStrictTypeCheckingEnabled() is true.",
                ctx.isStrictTypeCheckingEnabled());
    }

    /**
     * Checks setting {@link SDOHelperContext#isStrictTypeCheckingEnabled()}
     * using {@link SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME} property.
     */
    public void testTypeCheckingStrictnessFlagSystemPropertyFalse() {
        System.setProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME, "false");
        SDOHelperContext ctx = new SDOHelperContext("testHelperContext");
        assertFalse("Expected value of SDOHelperContext#isStrictTypeCheckingEnabled() is false.",
                ctx.isStrictTypeCheckingEnabled());
    }

    /**
     * Checks setting {@link SDOHelperContext#isStrictTypeCheckingEnabled()}
     * using {@link SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME} property.
     */
    public void testTypeCheckingStrictnessFlagSystemPropertyTrue() {
        System.setProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME, "true");
        SDOHelperContext ctx = new SDOHelperContext("testHelperContext");
        assertTrue("Expected value of SDOHelperContext#isStrictTypeCheckingEnabled() is true.",
                ctx.isStrictTypeCheckingEnabled());
    }

    private class TestResolver {

        private SDOHelperContext foo = new SDOHelperContext("Foo");

        private HelperContext getHelperContext(String id, ClassLoader classLoader) {
            return foo;
        }
    }

    private class ChildResolver extends TestResolver {

    }
}
