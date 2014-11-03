/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Field;
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

    private ClassLoader originalClassLoader = null;

    @Before
    public void setUp() {
        originalClassLoader = Thread.currentThread().getContextClassLoader();
        //defend against poorly written tests
        ((ConcurrentMap<ClassLoader, SessionManager>) getField("managers", null)).clear();
    }

    @After
    public void tearDown() {
        Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    @Test
    public void testManagersCaching() {
        ConcurrentMap<ClassLoader, SessionManager> registeredManagers = (ConcurrentMap<ClassLoader, SessionManager>) getField("managers", null);
        Assert.assertTrue(registeredManagers.isEmpty());

        CL cl1 = new CL();
        Thread.currentThread().setContextClassLoader(cl1);
        SessionManager m1 = SessionManager.getManager();
        Assert.assertNotNull(m1);
        Assert.assertEquals(1, registeredManagers.size());

        CL cl2 = new CL();
        Thread.currentThread().setContextClassLoader(cl2);
        SessionManager m2 = SessionManager.getManager();
        Assert.assertNotNull(m2);
        Assert.assertEquals(2, registeredManagers.size());
        Assert.assertNotEquals(m1, m2);
        
        Thread.currentThread().setContextClassLoader(cl1);
        Assert.assertEquals(m1, SessionManager.getManager());
        
        m1.destroy();
        m2.destroy();
        Assert.assertTrue(registeredManagers.isEmpty());
    }

    @Test
    public void testManagersCachingWithCustomManager() {
        ConcurrentMap<ClassLoader, SessionManager> registeredManagers = (ConcurrentMap<ClassLoader, SessionManager>) getField("managers", null);
        Assert.assertTrue(registeredManagers.isEmpty());

        CL cl1 = new CL();
        Thread.currentThread().setContextClassLoader(cl1);
        SessionManager.setManager(new SM());
        SessionManager m1 = SessionManager.getManager();
        Assert.assertNotNull(m1);
        Assert.assertNotNull(getField("loader", m1));
        Assert.assertEquals(1, registeredManagers.size());
        Assert.assertTrue(m1 instanceof SM);

        CL cl2 = new CL();
        Thread.currentThread().setContextClassLoader(cl2);
        SessionManager m2 = SessionManager.getManager();
        Assert.assertNotNull(m2);
        Assert.assertNotNull(getField("loader", m2));
        Assert.assertEquals(2, registeredManagers.size());
        Assert.assertFalse(m2 instanceof SM);
        Assert.assertNotEquals(m1, m2);
        
        Thread.currentThread().setContextClassLoader(cl1);
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

        CL cl1 = new CL();
        Thread.currentThread().setContextClassLoader(cl1);
        SessionManager.setManager(new SM());
        SessionManager m1 = SessionManager.getManager();
        Assert.assertEquals(1, allManagers.size());

        CL cl2 = new CL();
        Thread.currentThread().setContextClassLoader(cl2);
        SessionManager m2 = SessionManager.getManager();
        Assert.assertEquals(2, SessionManager.getAllManagers().size());

        Thread.currentThread().setContextClassLoader(cl1);
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
    
    private static final class CL extends ClassLoader {
    }

    private static final class SM extends SessionManager {
        public SM() {
            //empty by intention to test the 'loader' field initialization
            //through the setManager API
        }
    }
}
