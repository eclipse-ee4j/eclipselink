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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Hobby;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestLockMethod extends JPA1Base {
    @Test
    public void testNonManagedEntity() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "dep1");
            env.beginTransaction(em);
            em.lock(dep, LockModeType.READ);
            flop("no exception NonManagedEntity");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testEntityForInsert() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "dep1");
            env.beginTransaction(em);
            em.persist(dep);
            em.lock(dep, LockModeType.READ);
            flop("no exception NonManagedEntity");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testEntityForDelete() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(100, "dep1");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(100));
            em.remove(dep);
            em.lock(dep, LockModeType.READ);
            flop("no exception NonManagedEntity");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testEntityDeleteExecuted() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(101, "dep1");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(101));
            em.remove(dep);
            em.flush();
            em.lock(dep, LockModeType.READ);
            flop("no exception NonManagedEntity");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNonVersionedEntity() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Hobby hobby = new Hobby("blah-muh");
            env.beginTransaction(em);
            em.persist(hobby);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            hobby = em.merge(hobby);
            try {
                em.lock(hobby, LockModeType.READ);
                flop("no exception NonVersionedEntity");
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(true);
            } catch (PersistenceException e) {
                Assert.assertTrue(true);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testIllegalArgument() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        String illegal = "pfui";
        try {
            env.beginTransaction(em);
            em.lock(illegal, LockModeType.READ);
            flop("no exception IllegalArgument");
        } catch (IllegalArgumentException iae) {
            Assert.assertTrue(true);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNoTransaction() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(2, "dep2");
            em.lock(dep, LockModeType.READ);
            flop("no exception TransactionRequired");
        } catch (TransactionRequiredException tre) {
            Assert.assertTrue(true);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testReadLock() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(3, "dep3");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.merge(dep);
            em.lock(dep, LockModeType.READ);
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testWriteLock() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(4, "dep4");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.merge(dep);
            int version = dep.getVersion();
            em.lock(dep, LockModeType.WRITE);
            env.commitTransaction(em);
            verify(dep.getVersion() > version, "version not incremented");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testNewEntity() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Department dep = new Department(5, "dep5");
            env.beginTransaction(em);
            em.persist(dep);
            em.lock(dep, LockModeType.READ);
            // verify entity is flushed to DB for locking
            con = env.getDataSource().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select count(*) from TMP_DEP where ID = 3");
            rs.next();
            verify(rs.getInt(1) == 1, "entity not flushed for lock");
            env.commitTransaction(em);
        } finally {
            rs.close();
            stmt.close();
            con.close();
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testLockOldVersion() {
        lockOldVersion(5, LockModeType.READ);
        lockOldVersion(6, LockModeType.WRITE);
    }

    private void lockOldVersion(int id, LockModeType lockMode) {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        Department dep1 = new Department(id, "dep" + id);
        try {
            // create entity in DB
            env.beginTransaction(em1);
            em1.persist(dep1);
            env.commitTransactionAndClear(em1);
            // read first version
            env.beginTransaction(em1);
            dep1 = em1.find(Department.class, new Integer(id));
            verify(dep1 != null, "Department is null");
            // change entity meanwhile
            env.beginTransaction(em2);
            Department dep2 = em2.find(Department.class, new Integer(id));
            dep2.setName("dep" + id + "x");
            env.commitTransactionAndClear(em2);
            // try to lock first version
            em1.lock(dep1, lockMode);
            env.commitTransactionAndClear(em1);
            flop("OptimisticLockException not thrown");
        } catch (RollbackException rbe) {
          Throwable cause = rbe.getCause();
          if (cause instanceof OptimisticLockException) {
              OptimisticLockException ole = (OptimisticLockException) cause;
              verify(dep1.equals(ole.getEntity()), "wrong entity");
          } else {
              Assert.fail("cause is no OLE");
          }
        } catch (OptimisticLockException ole) {
            verify(dep1.equals(ole.getEntity()), "wrong entity");
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
        }
    }

    @Test
    public void testDetachedEntity() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // case 1: detached because entity exists on db but is not known by persistence context
            Department dep = new Department(7, "dep7");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            try {
                em.lock(dep, LockModeType.READ);
                flop("exception not thrown as expected");
            } catch (IllegalArgumentException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransaction(em);
            em.clear();
            // case 2: detached because an object with same pk but different object identity is known by persistence context
            // case 2a: state of known object: FOR_INSERT
            dep = new Department(8, "dep8");
            Department depDetached = new Department(dep.getId(), "detached");
            env.beginTransaction(em);
            em.persist(dep);
            try {
                em.lock(depDetached, LockModeType.READ);
                flop("exception not thrown as expected");
            } catch (IllegalArgumentException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransaction(em);
            em.clear();
            // case 2b: state of known object: FOR_UPDATE
            dep = new Department(9, "dep9");
            depDetached = new Department(dep.getId(), "detached");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            try {
                em.lock(depDetached, LockModeType.READ);
                flop("exception not thrown as expected");
            } catch (IllegalArgumentException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransaction(em);
            em.clear();
            // case 2c: state of known object: FOR_DELETE
            dep = new Department(10, "dep10");
            depDetached = new Department(dep.getId(), "detached");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            em.remove(dep);
            try {
                em.lock(depDetached, LockModeType.READ);
                flop("exception not thrown as expected");
            } catch (IllegalArgumentException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransaction(em);
            em.clear();
            // case 2d: state of known object: DELETE_EXECUTED
            dep = new Department(11, "dep11");
            depDetached = new Department(dep.getId(), "detached");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            em.remove(dep);
            em.flush();
            try {
                em.lock(depDetached, LockModeType.READ);
                flop("exception not thrown as expected");
            } catch (IllegalArgumentException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransaction(em);
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testTransactionMarkedForRollback() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(12, "dep12");
            env.beginTransaction(em);
            em.persist(dep);
            em.flush();
            env.markTransactionForRollback(em);
            em.lock(dep, LockModeType.WRITE);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testReadLockTwice() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(13, "dep13");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.merge(dep);
            em.lock(dep, LockModeType.READ);
            em.lock(dep, LockModeType.READ);
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testWriteLockTwice() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(14, "dep14");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.merge(dep);
            em.lock(dep, LockModeType.WRITE);
            em.lock(dep, LockModeType.WRITE);
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPromoteLock() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(15, "dep15");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.merge(dep);
            em.lock(dep, LockModeType.READ);
            em.lock(dep, LockModeType.WRITE);
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

}
