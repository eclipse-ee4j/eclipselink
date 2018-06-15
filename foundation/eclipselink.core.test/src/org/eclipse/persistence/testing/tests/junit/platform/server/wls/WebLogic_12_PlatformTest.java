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
package org.eclipse.persistence.testing.tests.junit.platform.server.wls;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.wls.WebLogic_12_Platform;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class WebLogic_12_PlatformTest {

    public WebLogic_12_PlatformTest() {
    }

    @After
    public void tearDown() {
        resetContextHelper();
    }

    @Test
    public void testUsesPartitions() {
        ServerPlatform platform = new WebLogic_12_Platform(null);
        Assert.assertFalse(platform.usesPartitions());

        setContextHelper();
        Assert.assertTrue(platform.usesPartitions());
    }

    @Test
    public void testGetPartitionId() {
        setContextHelper();
        ServerPlatform platform = new WebLogic_12_Platform(null);
        ICtx.ctx = "test3";
        Assert.assertEquals("test3", platform.getPartitionID());
        ICtx.ctx = "test1";
        Assert.assertEquals("test1", platform.getPartitionID());
    }

    @Test
    public void testGetPartitionName() {
        setContextHelper();
        WebLogic_12_Platform platform = new WebLogic_12_Platform(null);
        ICtx.nameCtx = "test3";
        Assert.assertEquals("test3", platform.getPartitionName());
        ICtx.nameCtx = "test1";
        Assert.assertEquals("test1", platform.getPartitionName());
    }

    @Test
    public void testIsGlobalRuntime() {
        setContextHelper();
        WebLogic_12_Platform platform = new WebLogic_12_Platform(null);
        ICtx.isGlobal = true;
        Assert.assertTrue(platform.isGlobalRuntime());
        ICtx.isGlobal = false;
        Assert.assertFalse(platform.isGlobalRuntime());
    }

    @Test
    public void testContextHelper() {
        Class contextHelperClass = null;
        for (Class<?> declaredClass : WebLogic_12_Platform.class.getDeclaredClasses()) {
            if ("org.eclipse.persistence.platform.server.wls.WebLogic_12_Platform$ContextHelper".equals(declaredClass.getName())) {
                contextHelperClass = declaredClass;
                break;
            }
        }
        Assert.assertNotNull("ContextHelper class not found", contextHelperClass);

        Method getCicManagerClassMethod = null;
        try {
            getCicManagerClassMethod = contextHelperClass.getDeclaredMethod("getCicManagerClass", String.class, String.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("getCicManagerClass method not found: " + e.getMessage());
        }
        Assert.assertNotNull("getCicManagerClass method not found", getCicManagerClassMethod);

        getCicManagerClassMethod.setAccessible(true);
        Object result = null;
        try {
            result = getCicManagerClassMethod.invoke(contextHelperClass, "org/eclipse/persistence/testing/tests/junit/platform/server/wls/WebLogic_12_PlatformTest.class", "org.eclipse.persistence.testing.tests.junit.platform.server.wls.WebLogic_12_PlatformTest");
        } catch (Exception e) {
            Assert.fail("Failed to invoke getCicManagerClass method: " + e.getMessage());
        }
        Assert.assertNotNull("Failed to retrieve test class", result);
        try {
            result = getCicManagerClassMethod.invoke(contextHelperClass, "this/should/not/resolve", "org.eclipse.persistence.testing.tests.junit.platform.server.wls.WebLogic_12_PlatformTest");
        } catch (Exception e) {
            Assert.fail("Failed to invoke getCicManagerClass method: " + e.getMessage());
        }
        Assert.assertNull("Should have failed to retrieve test class", result);
        getCicManagerClassMethod.setAccessible(false);
    }

    private void resetContextHelper() {
        try {
            setPrivateStaticFinalField(WebLogic_12_Platform.class.getDeclaredField("ctxHelper"), null);
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setContextHelper() {
        try {
            setPrivateStaticFinalField(WebLogic_12_Platform.class.getDeclaredField("ctxHelper"), setUpCtxHelper());
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object setUpCtxHelper() {
        IMgr imgr = new IMgr();
        Constructor c = null;
        Field instanceField = null;
        Object ctxHelperInstance = null;
        try {
            Class ctxHelperClass = Class.forName("org.eclipse.persistence.platform.server.wls.WebLogic_12_Platform$ContextHelper");
            c = ctxHelperClass.getDeclaredConstructors()[0];
            c.setAccessible(true);
            ctxHelperInstance = c.newInstance(imgr.getClass(), ICtx.class.getName());

            setPrivateStaticFinalField(ctxHelperClass.getDeclaredField("cicManagerClass"), IMgr.class);

            instanceField = ctxHelperClass.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, ctxHelperInstance);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (c != null) {
                c.setAccessible(false);
            }
            if (instanceField != null) {
                instanceField.setAccessible(false);
            }
        }
        return ctxHelperInstance;
    }

    private void setPrivateStaticFinalField(Field f, Object value) {
        Field modifiersField = null;
        int orig = 0;
        try {
            f.setAccessible(true);

            // remove final modifier from field
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            orig = f.getModifiers();
            modifiersField.setInt(f, orig & ~Modifier.FINAL);

            f.set(null, value);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (modifiersField != null) {
                if (f != null) {
                    try {
                        modifiersField.setInt(f, orig);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                modifiersField.setAccessible(false);
            }
            if (f != null) {
                f.setAccessible(false);
            }
        }
    }

    private static final class IMgr {

        public static IMgr getInstance() {
            return new IMgr();
        }

        public ICtx getCurrentComponentInvocationContext() {
            return new ICtx();
        }
    }

    private static final class ICtx {

        static volatile String ctx;
        static volatile String nameCtx;
        static volatile boolean isGlobal;

        public ICtx() {
        }

        public String getPartitionId() {
            return ctx;
        }

        public String getPartitionName() {
            return nameCtx;
        }

        public boolean isGlobalRuntime() {
            return isGlobal;
        }

    }
}
