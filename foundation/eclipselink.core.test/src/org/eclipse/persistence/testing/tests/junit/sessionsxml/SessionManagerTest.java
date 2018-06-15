/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.junit.sessionsxml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.eclipse.persistence.exceptions.ServerPlatformException;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.platform.server.ServerPlatformDetector;
import org.eclipse.persistence.platform.server.ServerPlatformUtils;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the API from SessionManager.
 */
public class SessionManagerTest {

    private List<ServerPlatformDetector> detectors;
    private SessionLog originalLogger;

    @Before
    public void setUp() {
        originalLogger = (SessionLog) getField(SessionManager.class, "LOG", null);
        SessionLog log = new LogWrapper();
        log.setLevel(originalLogger.getLevel());
        setStaticField(SessionManager.class, "LOG", log);
    }

    @After
    public void tearDown() {
        if (detectors != null) {
            for (Iterator<ServerPlatformDetector> i = detectors.iterator(); i.hasNext();) {
                ServerPlatformDetector detector = i.next();
                if (Detector.class.getName().equals(detector.getClass().getName())) {
                    i.remove();
                    break;
                }
            }
            setStaticField(ServerPlatformUtils.class, "SERVER_PLATFORM_CLS", null);
        }
        setStaticField(SessionManager.class, "LOG", originalLogger);
        reinitManager(false, true);
    }

    @Test
    public void testConcurrency() {
        reinitManager(true, true);

        SessionManager sm = SessionManager.getManager();
        Assert.assertEquals("test", getField(SessionManager.class, "context", sm));
        final Platform platform = (Platform) getField(SessionManager.class, "detectedPlatform", sm);

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            // create and start testing threads
            new Thread(new Worker(startSignal, doneSignal, platform)).start();
        }
        //create and start context switching thread
        ContextSwitcher cs = new ContextSwitcher(platform);
        Thread contextSwitcher = new Thread(cs);
        contextSwitcher.start();

        // let all threads proceed
        startSignal.countDown();
        try {
         // wait for all to finish
            doneSignal.await();
        } catch (InterruptedException e) {
            //ignore
        }

        //finish the context switching thread
        cs.stop = true;
        try {
            contextSwitcher.join();
        } catch (InterruptedException e) {
            //ignore
        }
        //check what we can
        Set<String> contexts = cs.values;
        ConcurrentMap<String, SessionManager> registeredManagers = (ConcurrentMap<String, SessionManager>) getField(SessionManager.class, "managers", null);
        //cannot do this as there may not be any thread catching some particular value
        //Assert.assertEquals(contexts.size()  + 1, registeredManagers.size());
        for (String context: contexts) {
            if ("test".equals(context)) {
                continue;
            }
            platform.partitionId = context;
            SessionManager.getManager().destroy();
        }
        Assert.assertEquals(1, registeredManagers.size());
    }

    @Test
    public void testAllManagers() {
        Collection<SessionManager> allManagers = SessionManager.getAllManagers();
        Assert.assertEquals(1, allManagers.size());
        Assert.assertNull(getField(SessionManager.class, "context", allManagers.iterator().next()));
        reinitManager(true, true);
        allManagers = SessionManager.getAllManagers();
        Assert.assertEquals(1, allManagers.size());
        Assert.assertEquals("test", getField(SessionManager.class, "context", allManagers.iterator().next()));
    }

    @Test
    public void testCustomManager() {
        SessionManager sm = new SM();
        SessionManager.setManager(sm);
        SessionManager m1 = SessionManager.getManager();
        Assert.assertNotNull(m1);
        Assert.assertNull(getField(SessionManager.class, "context", m1));
        Collection<SessionManager> allManagers = SessionManager.getAllManagers();
        Assert.assertEquals(1, allManagers.size());
        Assert.assertTrue(sm == allManagers.iterator().next());

        reinitManager(true, true);
        SessionManager.setManager(sm);
        m1 = SessionManager.getManager();
        Assert.assertNotNull(m1);
        Assert.assertEquals("test", getField(SessionManager.class, "context", m1));
        allManagers = SessionManager.getAllManagers();
        Assert.assertEquals(1, allManagers.size());
        Assert.assertTrue(sm == allManagers.iterator().next());
        sm.destroy();
    }

    @Test
    public void testInvalidPlatform() {
        // platform class name can be invalid/not found
        reinitManager(true, false);
        SessionManager sm = SessionManager.getManager();
        LogWrapper logger = (LogWrapper) getField(SessionManager.class, "LOG", null);
        Assert.assertEquals(SessionLog.WARNING, logger.getLastLevel());
        Assert.assertEquals(SessionLog.CONNECTION, logger.getLastCategory());
        Throwable t = logger.getLastThrowable();
        Assert.assertTrue("invalid excpetion type: " + t, t instanceof ServerPlatformException);
        Assert.assertTrue("invalid excpetion type: " + t.getCause(), t.getCause() instanceof ClassNotFoundException);
    }

    @Test
    public void testNPEFromPlatform() {
        // platform impl can throw NPE which is being wrapped by other exceptions
        Platform.forceNPE = true;
        try {
            reinitManager(true, true);
            SessionManager sm = SessionManager.getManager();
            LogWrapper logger = (LogWrapper) getField(SessionManager.class, "LOG", null);
            Assert.assertEquals(SessionLog.WARNING, logger.getLastLevel());
            Assert.assertEquals(SessionLog.CONNECTION, logger.getLastCategory());
            Throwable t = logger.getLastThrowable();
            Assert.assertTrue("invalid excpetion type: " + t, t instanceof ServerPlatformException);
        } finally {
            Platform.forceNPE = false;
        }
    }

    private void reinitManager(boolean addDetector, boolean validDetector) {
        if (addDetector) {
            detectors = (List<ServerPlatformDetector>) getField(ServerPlatformUtils.class, "PLATFORMS", null);
            detectors.add(0, new Detector(validDetector));
            setStaticField(ServerPlatformUtils.class, "SERVER_PLATFORM_CLS", null);
        }
        Method m = null;
        try {
            m = SessionManager.class.getDeclaredMethod("init");
            m.setAccessible(true);
            m.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            if (m != null) {
                try {
                    m.setAccessible(false);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    private Object getField(Class c, String field, Object o) {
        Field f = null;
        try {
            f = c.getDeclaredField(field);
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

    private void setStaticField(Class c, String field, Object value) {
        Field f = null;
        try {
            f = c.getDeclaredField(field);
            setPrivateStaticFinalField(f, value);
        } catch (IllegalArgumentException | SecurityException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (f != null) {
                f.setAccessible(false);
            }
        }
    }

    private void setPrivateStaticFinalField(Field f, Object value) {
        Field modifiersField = null;
        int orig = 0;
        try {
            f.setAccessible(true);
            orig = f.getModifiers();
            if (Modifier.isFinal(orig)) {
                // remove final modifier from field
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, orig & ~Modifier.FINAL);
            }
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

    private static final class SM extends SessionManager {
        public SM() {
            //empty by intention to test the 'context' field initialization
            //through the setManager API
        }
    }

    private static final class LogWrapper extends DefaultSessionLog {
        private volatile Throwable lastThrowable;
        private volatile String lastCategory;
        private volatile int lastLevel;

        @Override
        public void logThrowable(int level, String category, Throwable throwable) {
            this.lastLevel = level;
            this.lastCategory = category;
            this.lastThrowable = throwable;
            super.logThrowable(level, category, throwable);
        }

        /**
         * @return the throwable
         */
        public Throwable getLastThrowable() {
            return lastThrowable;
        }

        /**
         * @return the category
         */
        public String getLastCategory() {
            return lastCategory;
        }

        /**
         * @return the level
         */
        public int getLastLevel() {
            return lastLevel;
        }

    }

    public static final class Platform extends ServerPlatformBase {

        private volatile String partitionId = "test";
        private static boolean forceNPE = false;

        public Platform(DatabaseSession newDatabaseSession) {
            super(newDatabaseSession);
            if (forceNPE) {
                throw new NullPointerException();
            }
        }

        @Override
        public Class getExternalTransactionControllerClass() {
            return null;
        }

        @Override
        public boolean usesPartitions() {
            return true;
        }

        @Override
        public String getPartitionID() {
            return partitionId;
        }
    }

    private static class Detector implements ServerPlatformDetector {

        private final boolean valid;

        Detector(boolean valid) {
            this.valid = valid;
        }

        @Override
        public String checkPlatform() {
            return valid ? Platform.class.getName() : "non-existing-class-name";
        }
    }

    /**
     *  Randomly pick a number which is used as a context and changes it in random interval
     */
    private static final class ContextSwitcher implements Runnable {
        private final Random r = new Random(System.currentTimeMillis());
        private volatile boolean stop = false;
        private final Platform p;
        //contains names which were assigned to partitionId during test run
        private final Set<String> values = new HashSet<>();

        public ContextSwitcher(Platform p) {
            this.p = p;
        }
        @Override
        public void run() {
            while (!stop) {
                String id = String.valueOf(r.nextInt(10));
                p.partitionId = id;
                values.add(id);
                try {
                    Thread.sleep(r.nextInt(3));
                } catch (InterruptedException ie) {

                }
            }
        }
    };

    private static final class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        private final Platform p;

        Worker(CountDownLatch startSignal, CountDownLatch doneSignal, Platform p) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.p = p;
        }

        @Override
        public void run() {
            try {
                startSignal.await();
                //actual test
                SessionManager sm = SessionManager.getManager();
                Assert.assertNotNull(sm);
            } catch (InterruptedException ex) {
                //ignore
            } finally {
                doneSignal.countDown();
            }
        }
    }
}
