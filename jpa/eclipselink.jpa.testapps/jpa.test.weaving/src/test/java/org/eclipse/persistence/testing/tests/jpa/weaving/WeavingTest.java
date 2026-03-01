/*
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.weaving;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.PersistenceObject;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.weaving.WeavingChangeListener;
import org.eclipse.persistence.testing.models.jpa.weaving.WeavingEntityInDir;
import org.eclipse.persistence.tools.weaving.jpa.StaticWeaveProcessor;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class WeavingTest extends JUnitTestCase {

    private final String PU_NAME = "weaving-dir-test-pu";
    private final String WEAVING_ENTITY_CLASS = "org.eclipse.persistence.testing.models.jpa.weaving.WeavingEntity";
    private final String SOURCE_JAR = "source.jar";
    private final String TARGET_JAR = "target" + System.getProperty(SystemProperties.ASM_SERVICE, "") + ".jar";

    private final String DIR_WEAVING_ENTITY_CLASS = "org.eclipse.persistence.testing.models.jpa.weaving.WeavingEntityInDir";
    private final int ID = 1;
    private final int NEW_INT_FIELD_VALUE = 100;

    public WeavingTest() {
        super();
    }

    public WeavingTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return PU_NAME;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("WeavingTest");
        suite.addTest(new WeavingTest("setup"));
        suite.addTest(new WeavingTest("testWeavingDir"));
        suite.addTest(new WeavingTest("testWeavingDirChangeListener"));
        suite.addTest(new WeavingTest("testWeavingDirFetchGroup"));
        suite.addTest(new WeavingTest("testWeavingDirInvokeGeneratedMethods"));
        suite.addTest(new WeavingTest("testWeavingJar"));
        return suite;
    }

    public void setup() {
        WeavingEntityInDir weavingEntityInDir = new WeavingEntityInDir(ID);
        weavingEntityInDir.setBooleanField(true);
        weavingEntityInDir.setByteField((byte)1);
        weavingEntityInDir.setShortField((short)2);
        weavingEntityInDir.setIntField(3);
        weavingEntityInDir.setLongField(4);
        weavingEntityInDir.setFloatField(1.1F);
        weavingEntityInDir.setDoubleField(2.2);
        weavingEntityInDir.setStringField("abcde");
        weavingEntityInDir.setBlobField(new byte[] {1, 2, 3});

        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(weavingEntityInDir);
            commitTransaction(em);
            em.clear();
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testWeavingDir() throws Throwable {
        final String PU_PATH = "META-INF/persistence.xml";
        URL source =  Thread.currentThread().getContextClassLoader().getResource(".");
        URL target =  Thread.currentThread().getContextClassLoader().getResource(".");
        URL puInfo =  new URL("file:" + Thread.currentThread().getContextClassLoader().getResource(PU_PATH).getFile().replace(PU_PATH, ""));

        StaticWeaveProcessor staticWeaverProcessor= new StaticWeaveProcessor(source,target);
        staticWeaverProcessor.setPersistenceInfo(puInfo.getFile());
        staticWeaverProcessor.performWeaving();

        ClassLoader classLoader = new URLClassLoader(new URL[]{target});
        Class weavingEntityClass = classLoader.loadClass(DIR_WEAVING_ENTITY_CLASS);
        //Check existence of various methods added to entity by weaving
        assertNotNull(weavingEntityClass.getMethod("_persistence_post_clone"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_shallow_clone"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getId"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getCacheKey"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getPropertyChangeListener"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_checkFetched", String.class));
        assertNotNull(weavingEntityClass.getMethod("_persistence_checkFetchedForSet", String.class));

    }

    public void testWeavingDirChangeListener() {
        WeavingEntityInDir weavingEntityInDir = null;
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            weavingEntityInDir = em.find(WeavingEntityInDir.class, ID);
            // Verify _persistence_checkFetchedForSet(...) as blobField has "@Basic(fetch= FetchType.LAZY)"
            weavingEntityInDir.setBlobField(null);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        //Test change listener support generated by weaving
        WeavingChangeListener weavingChangeListener = new WeavingChangeListener();
        ((ChangeTracker)weavingEntityInDir)._persistence_setPropertyChangeListener(weavingChangeListener);
        weavingEntityInDir.setIntField(NEW_INT_FIELD_VALUE);
        assertTrue(weavingChangeListener.isListenerInvoked());
    }

    public void testWeavingDirFetchGroup() {
        WeavingEntityInDir weavingEntityInDir = null;
        EntityManager em = createEntityManager();
        try {
            TypedQuery query = em.createQuery("SELECT w FROM WeavingEntityInDir w WHERE w.id = ?1", WeavingEntityInDir.class);
            query.setHint(QueryHints.FETCH_GROUP_NAME, "FloatingPointFieldsOnly");
            query.setParameter(1, ID);
            weavingEntityInDir = (WeavingEntityInDir)query.getSingleResult();
            assertNotNull(((FetchGroupTracker)weavingEntityInDir)._persistence_getFetchGroup());
            assertEquals(3, weavingEntityInDir.getIntField());
            assertNull(((FetchGroupTracker)weavingEntityInDir)._persistence_getFetchGroup());
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testWeavingDirInvokeGeneratedMethods() throws Throwable {
        final int NEW_INT_FIELD_VALUE = 100;
        WeavingEntityInDir weavingEntityInDir = null;
        EntityManager em = createEntityManager();
        try {
            weavingEntityInDir = em.find(WeavingEntityInDir.class, ID);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        //Test _persistence_set_intField method generated by weaving
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType mt = MethodType.methodType(void.class, int.class);
        MethodHandle mh = lookup.findVirtual(WeavingEntityInDir.class, "_persistence_set_intField", mt);
        mh.invoke(weavingEntityInDir, NEW_INT_FIELD_VALUE);
        assertEquals(NEW_INT_FIELD_VALUE, weavingEntityInDir.getIntField());
        assertEquals(NEW_INT_FIELD_VALUE, ((PersistenceObject)weavingEntityInDir)._persistence_get("intField"));

        //Test _persistence_shallow_clone method generated by weaving
        Object weavingEntityInDirCloned = ((PersistenceObject)weavingEntityInDir)._persistence_shallow_clone();
        assertNotNull(weavingEntityInDirCloned);
    }

    public void testWeavingJar() throws IOException, URISyntaxException, ClassNotFoundException, NoSuchMethodException {
        URL source =  Thread.currentThread().getContextClassLoader().getResource(SOURCE_JAR);
        URL target = new URL(source.toString().replace(SOURCE_JAR, TARGET_JAR));

        StaticWeaveProcessor staticWeaverProcessor= new StaticWeaveProcessor(source,target);
        staticWeaverProcessor.performWeaving();

        ClassLoader classLoader = new URLClassLoader(new URL[]{target});
        Class weavingEntityClass = classLoader.loadClass(WEAVING_ENTITY_CLASS);
        //Check existence of various methods added to entity by weaving
        assertNotNull(weavingEntityClass.getMethod("_persistence_post_clone"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_shallow_clone"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getId"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getCacheKey"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getPropertyChangeListener"));
    }
}
