/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     09/27/2026 - Andreas Lemmer
//       - 2885: batch fetching in both directions must not overflow the stack
package org.eclipse.persistence.testing.tests.jpa.batchfetch;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.batchfetch.BatchFetchEagerTableCreator;
import org.eclipse.persistence.testing.models.jpa.batchfetch.LazyChild;
import org.eclipse.persistence.testing.models.jpa.batchfetch.LazyParent;

import java.util.List;

/**
 * Both sides of the relationship are batch fetched lazily. Nothing is fetched while the objects are built,
 * so a batch still builds all of its objects at once, and loading them must not overflow the stack either.
 * More rows than the default batch size of 500 are used, so there is more than one batch.
 */
public class BatchFetchLazyJUnitTest extends JUnitTestCase {
    private static final int NUM_PARENTS = 700;

    public BatchFetchLazyJUnitTest() {
        super();
    }

    public BatchFetchLazyJUnitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("BatchFetchLazyJUnitTest");
        suite.addTest(new BatchFetchLazyJUnitTest("testSetup"));
        suite.addTest(new BatchFetchLazyJUnitTest("testMappingsAreLazy"));
        suite.addTest(new BatchFetchLazyJUnitTest("testSelectParents"));
        suite.addTest(new BatchFetchLazyJUnitTest("testSelectChildren"));
        suite.addTest(new BatchFetchLazyJUnitTest("testFindParent"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new BatchFetchEagerTableCreator().replaceTables(JUnitTestCase.getServerSession(getPersistenceUnitName()));
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            for (int i = 1; i <= NUM_PARENTS; i++) {
                LazyParent parent = new LazyParent(i);
                em.persist(parent);
                em.persist(new LazyChild(i, parent));
            }
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Without weaving the many to one would silently be eager, and the test would not test the lazy case.
     */
    public void testMappingsAreLazy() {
        assertTrue("children must be lazy", getMapping(LazyParent.class, "children").isLazy());
        assertTrue("parent must be lazy", getMapping(LazyChild.class, "parent").isLazy());
    }

    private ForeignReferenceMapping getMapping(Class<?> entityClass, String attributeName) {
        return (ForeignReferenceMapping) getServerSession(getPersistenceUnitName()).getDescriptor(entityClass).getMappingForAttributeName(attributeName);
    }

    public void testSelectParents() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            List<LazyParent> parents = em.createQuery("SELECT p FROM LazyParent p", LazyParent.class).getResultList();
            assertEquals("Not all parents are selected", NUM_PARENTS, parents.size());
            parents.forEach(BatchFetchLazyJUnitTest::verifyParent);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testSelectChildren() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            List<LazyChild> children = em.createQuery("SELECT c FROM LazyChild c", LazyChild.class).getResultList();
            assertEquals("Not all children are selected", NUM_PARENTS, children.size());
            for (LazyChild child : children) {
                verifyParent(child.getParent());
                assertSame("Wrong child of parent " + child.getParent().getId(), child, child.getParent().getChildren().iterator().next());
            }
        } finally {
            closeEntityManager(em);
        }
    }

    public void testFindParent() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            verifyParent(em.find(LazyParent.class, 1L));
        } finally {
            closeEntityManager(em);
        }
    }

    private static void verifyParent(LazyParent parent) {
        assertEquals("Wrong number of children of parent " + parent.getId(), 1, parent.getChildren().size());
        LazyChild child = parent.getChildren().iterator().next();
        assertEquals("Wrong child of parent " + parent.getId(), parent.getId(), child.getId());
        assertSame("Wrong parent of child " + child.getId(), parent, child.getParent());
    }

    @Override
    public String getPersistenceUnitName() {
        return "batchfetch";
    }
}
