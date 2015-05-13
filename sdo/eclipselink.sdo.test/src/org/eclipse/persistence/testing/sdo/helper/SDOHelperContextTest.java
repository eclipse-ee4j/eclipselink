/**
 * ****************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Iaroslav Savytskyi - initial implementation
 * ****************************************************************************
 */
package org.eclipse.persistence.testing.sdo.helper;

import commonj.sdo.helper.HelperContext;
import junit.framework.TestCase;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * TestCase for SDOHelperContext
 */
public class SDOHelperContextTest extends TestCase {

    private static Map getMap() throws NoSuchFieldException, IllegalAccessException {
        Field mapField = SDOHelperContext.class.getDeclaredField("HELPER_CONTEXT_RESOLVERS");
        mapField.setAccessible(true);
        return (Map) mapField.get(null);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getMap().clear();
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
        final SDOHelperContext foo = new SDOHelperContext("Foo");
        SDOHelperContext.setHelperContextResolver(new SDOHelperContext.HelperContextResolver() {
            @Override
            public HelperContext getHelperContext(String id, ClassLoader classLoader) {
                return foo;
            }
        });

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

    private class TestResolver {

        private SDOHelperContext foo = new SDOHelperContext("Foo");

        private HelperContext getHelperContext(String id, ClassLoader classLoader) {
            return foo;
        }
    }

    private class ChildResolver extends TestResolver {

    }
}
