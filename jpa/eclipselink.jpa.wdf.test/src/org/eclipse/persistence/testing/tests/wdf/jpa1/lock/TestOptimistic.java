/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.lock;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.Node;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Ignore;
import org.junit.Test;

// TODO remove restrictions
public class TestOptimistic extends JPA1Base {

    @Test
    public void testOptimisticLockExceptionUpdateUpdate() {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        int id = 10;
        int version = 0;
        Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "one");
        try {
            env.beginTransaction(em1);
            em1.persist(rev1);
            env.commitTransactionAndClear(em1);
            
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            version = rev1.getVersion();
            
            EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
            try {
                env.beginTransaction(em2);
                Review rev2 = em2.find(Review.class, new Integer(id));
                rev2.setReviewText("two"); // 1 update
                env.commitTransactionAndClear(em2); 
            } finally {
                closeEntityManager(em2);
            }
            
            rev1.setReviewText("1"); // 2 update
            env.commitTransactionAndClear(em1);
            flop("OptimisticLockException not thrown");
        } catch (RollbackException rbe) {
          assertRBEisOLE(rev1, rbe);
        } catch (OptimisticLockException ole) {
            assertFailingEntity(rev1, ole);
        } finally {
            closeEntityManager(em1);
        }
        // bug 309681: version is incremented in spite of roll back -> not an
        // issue
        // verify(version == rev1.getVersion(), "wrong version:" + version + " != " + rev1.getVersion());
    }

    private void assertFailingEntity(Review rev1, OptimisticLockException ole) {
        // $JL-EXC$ expected behavior
        Object failingEntity = ole.getEntity();
        if (failingEntity != null) {
            verify(rev1.equals(failingEntity), "wrong entity");
        }
    }

    @Test
    public void testOptimisticLockExceptionDeleteUpdate() {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        int id = 20;
        Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "one");
        try {
            env.beginTransaction(em1);
            em1.persist(rev1);
            env.commitTransactionAndClear(em1);
            
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            env.beginTransaction(em2);
            Review rev2 = em2.find(Review.class, new Integer(id));
            em2.remove(rev2); // 1 delete
            env.commitTransactionAndClear(em2);
            rev1.setReviewText("1"); // 2 update
            env.commitTransactionAndClear(em1);
            flop("OptimisticLockException not thrown");
        } catch (RollbackException rbe) {
            assertRBEisOLE(rev1, rbe);
        } catch (OptimisticLockException ole) {
            assertFailingEntity(rev1, ole);
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
        }
    }

    private void assertRBEisOLE(Review rev1, RollbackException rbe) {
        Throwable cause = rbe.getCause();
        if (cause instanceof OptimisticLockException) {
            assertFailingEntity(rev1, (OptimisticLockException) cause);
        } else {
            flop("Rollback not caused by OLE");
        }
    }

    @Test
    public void testOptimisticLockExceptionUpdateDelete() {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        int id = 30;
        Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "one");
        try {
            env.beginTransaction(em1);
            em1.persist(rev1);
            env.commitTransactionAndClear(em1);
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            env.beginTransaction(em2);
            Review rev2 = em2.find(Review.class, new Integer(id));
            rev2.setReviewDate(Date.valueOf("2005-12-23")); // 1 update
            env.commitTransactionAndClear(em2);
            em1.remove(rev1); // 2 delete
            env.commitTransactionAndClear(em1);
            flop("OptimisticLockException not thrown");
        } catch (RollbackException rbe) {
            assertRBEisOLE(rev1, rbe);  
        } catch (OptimisticLockException ole) {
            assertFailingEntity(rev1, ole);
            try {
                env.rollbackTransactionAndClear(em1);
                flop("no rollback after OptimisticLockException");
            } catch (IllegalStateException ise) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
        }
    }

    @Test
    public void testOptimisticLockExceptionDeleteDelete() {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        int id = 40;
        Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "one");
        try {
            env.beginTransaction(em1);
            em1.persist(rev1);
            env.commitTransactionAndClear(em1);
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            env.beginTransaction(em2);
            Review rev2 = em2.find(Review.class, new Integer(id));
            em2.remove(rev2); // 1 delete
            env.commitTransactionAndClear(em2);
            em1.remove(rev1); // 2 delete
            env.commitTransactionAndClear(em1);
            flop("OptimisticLockException not thrown");
        } catch (RollbackException rbe) {
            assertRBEisOLE(rev1, rbe);
        } catch (OptimisticLockException ole) {
            assertFailingEntity(rev1, ole);
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
        }
    }

    @Test
    public void testNoChange() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        try {
            int id = 50;
            int version = 0;
            Review rev = new Review(id, Date.valueOf("2005-12-07"), "1");
            env.beginTransaction(em);
            em.persist(rev);
            env.commitTransactionAndClear(em);
            version = rev.getVersion();
            env.beginTransaction(em);
            rev = em.find(Review.class, new Integer(id));
            verify(rev != null, "Review is null");
            rev.setReviewText(rev.getReviewText()); // no change
            env.commitTransactionAndClear(em);
            verify(version == rev.getVersion(), "wrong version");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testIllegalVersionAccessNew() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        boolean caughtException = false;
        try {
            int id = 60;
            Review rev = new Review(id, Date.valueOf("2005-12-07"), "1");
            env.beginTransaction(em);
            em.persist(rev);
            rev.setVersion(5);
            env.commitTransactionAndClear(em);
        } catch (PersistenceException e) {
            // $JL-EXC$ expected behavior
            caughtException = true;
        } finally {
            closeEntityManager(em);
            verify(caughtException, "PersistenceException not thrown for versionModification");
        }
    }

    @Test
    public void testIllegalVersionAccessManaged() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        boolean caughtException = false;
        try {
            int id = 70;
            Review rev = new Review(id, Date.valueOf("2005-12-07"), "1");
            env.beginTransaction(em);
            em.persist(rev);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            rev = em.merge(rev);
            rev.setVersion(7);
            env.commitTransactionAndClear(em);
        } catch (PersistenceException e) {
            // $JL-EXC$ expected behavior
            caughtException = true;
        } finally {
            closeEntityManager(em);
        }
        verify(caughtException, "PersistenceException not thrown for versionModification");
    }

    @Test
    public void testFlushWithVersion() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        try {
            int id = 80;
            Review rev = new Review(id, Date.valueOf("2005-12-07"), "1");
            env.beginTransaction(em);
            em.persist(rev);
            em.flush(); // 1st version
            int startVersion = rev.getVersion();
            rev.setReviewDate(Date.valueOf("2005-12-23"));
            env.commitTransactionAndClear(em); // 2nd version
            env.beginTransaction(em);
            rev = em.merge(rev);
            rev.setReviewText("AAA");
            em.flush(); // 3rd version
            rev.setReviewText("BBB");
            env.commitTransactionAndClear(em); // 4th version
            env.beginTransaction(em);
            rev = em.merge(rev);
            em.flush();
            env.commitTransactionAndClear(em); // still 4th version
            rev = em.find(Review.class, new Integer(id));
            verify(rev.getReviewText().equals("BBB"), "wrong reviewText");
            verify(rev.getReviewDate().equals(Date.valueOf("2005-12-23")), "wrong reviewDate");
            verify(rev.getVersion() == startVersion + 3, "wrong version");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeWithVersion() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        int id = 90;
        Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "a");
        Review rev2 = new Review(id, Date.valueOf("2005-12-07"), "b");
        try {
            env.beginTransaction(em);
            em.persist(rev1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            rev1 = em.merge(rev2);
            flop("OptimisticLockException not thrown for merge with old version");
        } catch (OptimisticLockException ole) {
            assertFailingEntity(rev2, ole);
            verify(env.isTransactionMarkedForRollback(em), "transaction not marked for rollback on OptimisticLockException");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRefreshWithVersion() {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        try {
            int id = 100;
            Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "x");
            env.beginTransaction(em1);
            em1.persist(rev1);
            env.commitTransactionAndClear(em1);
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            env.beginTransaction(em2);
            Review rev2 = em2.find(Review.class, new Integer(id));
            rev2.setReviewDate(Date.valueOf("2005-12-23"));
            env.commitTransactionAndClear(em2);
            em1.refresh(rev1);
            rev1.setReviewText("y");
            env.commitTransactionAndClear(em1);
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
        }
    }

    @Test
    public void testVersion() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        try {
            int id = 110;
            int version = 0;
            Review rev = new Review(id, Date.valueOf("2005-12-07"), "1");
            env.beginTransaction(em);
            em.persist(rev);
            env.commitTransactionAndClear(em);
            for (int i = 0; i < 3; i++) {
                env.beginTransaction(em);
                rev = em.find(Review.class, new Integer(id));
                verify(rev != null, "Review is null");
                rev.setReviewText(new Integer(i).toString());
                env.commitTransactionAndClear(em);
                verify(version < rev.getVersion(), "new version to small");
                version = rev.getVersion();
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPersistAgain() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        try {
            int id = 120;
            Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "x");
            env.beginTransaction(em);
            em.persist(rev1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            rev1 = em.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            em.persist(rev1);
            verify(rev1.getVersion() > 0, "Version reset");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Ignore   // this only works on oracle
    public void testIsoLevel() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        boolean caughtException = false;
        try {
            Connection con = env.getDataSource().getConnection();
            verify(con.getTransactionIsolation() == Connection.TRANSACTION_READ_COMMITTED, "wrong isoLevel");
            int id = 130;
            Review rev1 = new Review(id, Date.valueOf("2005-12-07"), "1");
            env.beginTransaction(em1); // create entity
            em1.persist(rev1);
            env.commitTransactionAndClear(em1);
            env.beginTransaction(em1); // Tx1 read and flush change
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            rev1.setReviewText("2");
            em1.flush();
            env.beginTransaction(em2); // Tx2 read flushed entity ?
            Review rev2 = em2.find(Review.class, new Integer(id));
            verify(rev2 != null, "Review is null");
            env.rollbackTransactionAndClear(em1); // Tx1 rollback first change and commit second
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, new Integer(id));
            verify(rev1 != null, "Review is null");
            rev1.setReviewText("3");
            env.commitTransactionAndClear(em1);
            rev2.setReviewText("4"); // Tx2 commit based on rollback version
            env.commitTransactionAndClear(em2);
        } catch (OptimisticLockException ole) {
            // $JL-EXC$ expected behavior
            caughtException = true;
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
            verify(caughtException, "OptimisticLockException not thrown");
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testNode() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManagerFactory().createEntityManager();
        int NUMBER_OF_NODES = 7;
        int NUMBER_OF_UPDATES = 3;
        try {
            Node first = new Node(0, null);
            first.setName("!0");
            Node parent = first;
            env.beginTransaction(em);
            em.persist(first);
            for (int i = 1; i < NUMBER_OF_NODES; i++) {
                Node tmp = new Node(i, parent);
                tmp.setName(new Integer(i).toString());
                em.persist(tmp);
                parent = tmp;
            }
            env.commitTransactionAndClear(em);
            for (int i = 0; i < NUMBER_OF_UPDATES; i++) {
                updateNodes(env, em, Integer.toString(i));
            }
        } finally {
            closeEntityManager(em);
        }
    }

    private void updateNodes(JPAEnvironment env, EntityManager em, String newName) {
        env.beginTransaction(em);
        Node tmp = em.find(Node.class, new Integer(0));
        verify(tmp.getVersion() != 0, "wrong version");
        while (tmp != null) {
            tmp.setName(newName);
            Set<Node> children = tmp.getChildren();
            if (!children.isEmpty()) {
                tmp = children.iterator().next(); // linkedList
                verify(tmp.getVersion() != 0, "wrong version");
            } else {
                tmp = null;
            }
        }
        env.commitTransactionAndClear(em);
    }

    @Test
    public void testRealOptimism() {
        verify(true, "always everything fine");
    }
}
