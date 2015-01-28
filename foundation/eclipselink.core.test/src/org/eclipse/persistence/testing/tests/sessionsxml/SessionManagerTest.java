/*******************************************************************************
 * Copyright (c) 2014, 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import junit.framework.TestCase;

import org.eclipse.persistence.sessions.factories.SessionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the API from SessionManager.
 */
public class SessionManagerTest extends TestCase {

    @Before
    @Override
    public void setUp() {
        //defend against poorly written tests
        ((ConcurrentMap<String, SessionManager>) getField("managers", null)).clear();
    }

    @After
    @Override
    public void tearDown() {
        resetContextHelper();
    }

    @Test
    public void testManagersCaching() {
        ConcurrentMap<String, SessionManager> registeredManagers = (ConcurrentMap<String, SessionManager>) getField("managers", null);
        Assert.assertTrue(registeredManagers.isEmpty());

        setContextHelper();

        ICtx.ctx = "test";
        SessionManager m1 = SessionManager.getManager();
        Assert.assertNotNull(m1);
        Assert.assertEquals(1, registeredManagers.size());

        ICtx.ctx = "test2";
        SessionManager m2 = SessionManager.getManager();
        Assert.assertNotNull(m2);
        Assert.assertEquals(2, registeredManagers.size());
        Assert.assertFalse(m1 == m2);

        ICtx.ctx = "test";
        SessionManager m3 = SessionManager.getManager();
        Assert.assertNotNull(m3);
        Assert.assertEquals(2, registeredManagers.size());
        Assert.assertEquals(m1, m3);
        Assert.assertTrue(m1 == m3);

        m1.destroy();
        m2.destroy();
        Assert.assertTrue(registeredManagers.isEmpty());
    }

    @Test
    public void testManagersCachingWithCustomManager() {
        ConcurrentMap<ClassLoader, SessionManager> registeredManagers = (ConcurrentMap<ClassLoader, SessionManager>) getField("managers", null);
        Assert.assertTrue(registeredManagers.isEmpty());

        setContextHelper();

        ICtx.ctx = "test3";
        SessionManager.setManager(new SM());
        SessionManager m1 = SessionManager.getManager();
        Assert.assertNotNull(m1);
        Assert.assertNotNull(getField("context", m1));
        Assert.assertEquals("test3", getField("context", m1));
        Assert.assertEquals(1, registeredManagers.size());
        Assert.assertTrue(m1 instanceof SM);

        ICtx.ctx = "test4";
        SessionManager m2 = SessionManager.getManager();
        Assert.assertNotNull(m2);
        Assert.assertNotNull(getField("context", m2));
        Assert.assertEquals("test4", getField("context", m2));
        Assert.assertEquals(2, registeredManagers.size());
        Assert.assertFalse(m2 instanceof SM);
        Assert.assertFalse(m1 == m2);

        ICtx.ctx = "test3";
        Assert.assertEquals(m1, SessionManager.getManager());

        m1.destroy();
        m2.destroy();
        Assert.assertTrue(registeredManagers.isEmpty());
    }

    @Test
    public void testAllManagers() {
        ConcurrentMap<ClassLoader, SessionManager> registeredManagers = (ConcurrentMap<ClassLoader, SessionManager>) getField("managers", null);
        Assert.assertTrue(registeredManagers.isEmpty());
        Collection<SessionManager> allManagers = SessionManager.getAllManagers();
        Assert.assertEquals(0, allManagers.size());
        setContextHelper();

        ICtx.ctx = "test5";
        SessionManager.setManager(new SM());
        SessionManager m1 = SessionManager.getManager();
        Assert.assertEquals(1, allManagers.size());

        ICtx.ctx = "test6";
        SessionManager m2 = SessionManager.getManager();
        Assert.assertEquals(2, SessionManager.getAllManagers().size());

        ICtx.ctx = "test5";
        Assert.assertEquals(m1, SessionManager.getManager());

        m1.destroy();
        m2.destroy();
        Assert.assertEquals(0, allManagers.size());
    }

    private Object getField(String field, Object o) {
        Field f = null;
        try {
            f = SessionManager.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (f != null) {
                f.setAccessible(false);
            }
        }
    }

    private void setContextHelper() {
        try {
            setPrivateStaticFinalField(SessionManager.class.getDeclaredField("ctxHelper"), setUpCtxHelper());
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void resetContextHelper() {
        try {
            setPrivateStaticFinalField(SessionManager.class.getDeclaredField("ctxHelper"), null);
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
            Class ctxHelperClass = Class.forName("org.eclipse.persistence.sessions.factories.SessionManager$ContextHelper");
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
        try {
            f.setAccessible(true);

            // remove final modifier from field
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

            f.set(null, value);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (modifiersField != null) {
                if (f != null) {
                    try {
                        modifiersField.setInt(f, f.getModifiers() & Modifier.FINAL);
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

    private static final class SM extends SessionManager {
        public SM() {
            //empty by intention to test the 'context' field initialization
            //through the setManager API
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

        static String ctx;

        public ICtx() {
        }

        public String getPartitionId() {
            return ctx;
        }

    }
}
