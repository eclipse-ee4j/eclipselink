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
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.batchfetch.BatchFetchEagerTableCreator;
import org.eclipse.persistence.testing.models.jpa.batchfetch.EagerChild;
import org.eclipse.persistence.testing.models.jpa.batchfetch.EagerParent;

import java.util.List;

/**
 * Both sides of the relationship are batch fetched eagerly. Building the objects of a batch used to fetch
 * the next batch for every single object, which overflowed the stack well below the batch size.
 * More rows than the default batch size of 500 are used, so there is more than one batch.
 */
public class BatchFetchEagerJUnitTest extends JUnitTestCase {
    private static final int NUM_PARENTS = 700;

    public BatchFetchEagerJUnitTest() {
        super();
    }

    public BatchFetchEagerJUnitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("BatchFetchEagerJUnitTest");
        suite.addTest(new BatchFetchEagerJUnitTest("testSetup"));
        suite.addTest(new BatchFetchEagerJUnitTest("testSelectParents"));
        suite.addTest(new BatchFetchEagerJUnitTest("testSelectChildren"));
        suite.addTest(new BatchFetchEagerJUnitTest("testFindParent"));
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
                EagerParent parent = new EagerParent(i);
                em.persist(parent);
                em.persist(new EagerChild(i, parent));
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

    public void testSelectParents() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            List<EagerParent> parents = em.createQuery("SELECT p FROM EagerParent p", EagerParent.class).getResultList();
            assertEquals("Not all parents are selected", NUM_PARENTS, parents.size());
            parents.forEach(BatchFetchEagerJUnitTest::verifyParent);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testSelectChildren() {
        EntityManager em = createEntityManager();
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            List<EagerChild> children = em.createQuery("SELECT c FROM EagerChild c", EagerChild.class).getResultList();
            assertEquals("Not all children are selected", NUM_PARENTS, children.size());
            for (EagerChild child : children) {
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
            verifyParent(em.find(EagerParent.class, 1L));
        } finally {
            closeEntityManager(em);
        }
    }

    private static void verifyParent(EagerParent parent) {
        assertEquals("Wrong number of children of parent " + parent.getId(), 1, parent.getChildren().size());
        EagerChild child = parent.getChildren().iterator().next();
        assertEquals("Wrong child of parent " + parent.getId(), parent.getId(), child.getId());
        assertSame("Wrong parent of child " + child.getId(), parent, child.getParent());
    }

    @Override
    public String getPersistenceUnitName() {
        return "batchfetch";
    }
}
