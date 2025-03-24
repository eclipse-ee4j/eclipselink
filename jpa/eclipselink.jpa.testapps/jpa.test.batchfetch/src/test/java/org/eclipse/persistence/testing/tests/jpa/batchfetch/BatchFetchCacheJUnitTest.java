/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.batchfetch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.batchfetch.BatchFetchCacheTableCreator;
import org.eclipse.persistence.testing.models.jpa.batchfetch.Child;
import org.eclipse.persistence.testing.models.jpa.batchfetch.Parent;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BatchFetchCacheJUnitTest extends JUnitTestCase {

    public BatchFetchCacheJUnitTest() {
        super();
    }

    public BatchFetchCacheJUnitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("BatchFetchCacheJunitTest");
        suite.addTest(new BatchFetchCacheJUnitTest("testSetup"));
        suite.addTest(new BatchFetchCacheJUnitTest("testSelectChildren"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new BatchFetchCacheTableCreator().replaceTables(JUnitTestCase.getServerSession(
                getPersistenceUnitName()));
        EntityManager em = createEntityManager();
        createRecords(em);
    }

    public void createRecords(EntityManager em) {
        try {
            beginTransaction(em);
            Parent p1 = new Parent(1);
            Parent p2 = new Parent(2);
            Parent p3 = new Parent(3);
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            Child u1 = new Child(1, p1);
            Child u2 = new Child(2, p2);
            Child u3 = new Child(3, p3);
            em.persist(u1);
            em.persist(u2);
            em.persist(u3);

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

    public void testSelectChildren() {
        EntityManager em = createEntityManager();
        em.getEntityManagerFactory().getCache().evictAll();

        try {
            TypedQuery<Child> q = em.createQuery("SELECT c FROM Child c", Child.class);
            q.setHint(QueryHints.BATCH_SIZE, 1);
            List<Child> result = q.getResultList();
            assertEquals("Not all rows are selected", 3, result.size());
            List<Parent> parents = result.stream().map(Child::getParent).filter(Objects::nonNull).collect(
                    Collectors.toList());
            assertEquals("Not all rows have parents", 3, parents.size());
            List<Child> childrenOfParents = parents.stream()
                                                   .map(Parent::getChildren)
                                                   .flatMap(Collection::stream)
                                                   .filter(Objects::nonNull)
                                                   .collect(Collectors.toList());
            assertEquals("Not all parents have children", 3, childrenOfParents.size());
        } catch (RuntimeException e) {
            closeEntityManager(em);
            throw e;
        }
    }

    @Override
    public String getPersistenceUnitName() {
        return "batchfetch";
    }
}
