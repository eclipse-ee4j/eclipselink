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
//       - 2885: eager batch fetching in both directions must not overflow the stack
package org.eclipse.persistence.testing.tests.jpa.batchfetch;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.batchfetch.BatchFetchEagerTableCreator;
import org.eclipse.persistence.testing.models.jpa.batchfetch.WovenEagerChild;
import org.eclipse.persistence.testing.models.jpa.batchfetch.WovenEagerParent;

import java.util.List;

/**
 * Same as {@link BatchFetchEagerJUnitTest}, but with eclipselink.weaving.eager: the eager relationships use
 * indirection and are instantiated right after their object is built, which recursed just the same.
 */
public class BatchFetchEagerWeavingJUnitTest extends JUnitTestCase {
    private static final int NUM_PARENTS = 700;

    public BatchFetchEagerWeavingJUnitTest() {
        super();
    }

    public BatchFetchEagerWeavingJUnitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("BatchFetchEagerWeavingJUnitTest");
        suite.addTest(new BatchFetchEagerWeavingJUnitTest("testSetup"));
        suite.addTest(new BatchFetchEagerWeavingJUnitTest("testMappingsAreWovenEager"));
        suite.addTest(new BatchFetchEagerWeavingJUnitTest("testSelectParents"));
        suite.addTest(new BatchFetchEagerWeavingJUnitTest("testSelectChildren"));
        suite.addTest(new BatchFetchEagerWeavingJUnitTest("testFindParent"));
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
                WovenEagerParent parent = new WovenEagerParent(i);
                em.persist(parent);
                em.persist(new WovenEagerChild(i, parent));
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
     * The test only means something if the eager relationships were really woven to use indirection.
     */
    public void testMappingsAreWovenEager() {
        for (ForeignReferenceMapping mapping : List.of(getMapping(WovenEagerParent.class, "children"), getMapping(WovenEagerChild.class, "parent"))) {
            assertTrue(mapping.getAttributeName() + " must use indirection", mapping.usesIndirection());
            assertFalse(mapping.getAttributeName() + " must be eager", mapping.isLazy());
        }
    }

    private ForeignReferenceMapping getMapping(Class<?> entityClass, String attributeName) {
        return (ForeignReferenceMapping) getServerSession(getPersistenceUnitName()).getDescriptor(entityClass).getMappingForAttributeName(attributeName);
    }

    public void testSelectParents() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            List<WovenEagerParent> parents = em.createQuery("SELECT p FROM WovenEagerParent p", WovenEagerParent.class).getResultList();
            assertEquals("Not all parents are selected", NUM_PARENTS, parents.size());
            parents.forEach(BatchFetchEagerWeavingJUnitTest::verifyParent);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testSelectChildren() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            List<WovenEagerChild> children = em.createQuery("SELECT c FROM WovenEagerChild c", WovenEagerChild.class).getResultList();
            assertEquals("Not all children are selected", NUM_PARENTS, children.size());
            for (WovenEagerChild child : children) {
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
            verifyParent(em.find(WovenEagerParent.class, 1L));
        } finally {
            closeEntityManager(em);
        }
    }

    private static void verifyParent(WovenEagerParent parent) {
        assertEquals("Wrong number of children of parent " + parent.getId(), 1, parent.getChildren().size());
        WovenEagerChild child = parent.getChildren().iterator().next();
        assertEquals("Wrong child of parent " + parent.getId(), parent.getId(), child.getId());
        assertSame("Wrong parent of child " + child.getId(), parent, child.getParent());
    }

    @Override
    public String getPersistenceUnitName() {
        return "batchfetch-weaving-eager";
    }
}
