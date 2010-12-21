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

package org.eclipse.persistence.testing.tests.wdf.jpa1.entitymanager;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the EntityManager.close() method on an application-managed EntityManager
 */

public class TestClose extends JPA1Base {

    @Test
    public void dummyTestMethod() {
        return;
    }

    @Test
    public void testPersistClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.persist("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.merge("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.remove("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.find(Department.class, new Integer(1));
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testGetReferenceClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.getReference(Department.class, new Integer(1));
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFlushClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.flush();
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSetFlushModeClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.setFlushMode(FlushModeType.AUTO);
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testGetFlushModeClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.getFlushMode();
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testLockClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.lock("Hugo", LockModeType.READ);
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRefreshClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.refresh("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testClearClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.clear();
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testContainsClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.contains("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCreateQueryClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.createQuery("not a query");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCreateNamedQueryClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.createNamedQuery("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCreateNativeQueryClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.createNativeQuery("Hugo");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
            try {
                em.createNativeQuery("Hugo", Department.class);
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
            try {
                em.createNativeQuery("Hugo", "Otto");
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testJoinTransactionClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.joinTransaction();
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testGetDelegateClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.getDelegate();
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Ignore //  @TestProperties(unsupportedEnvironments = { ResourceLocalAnnotationsEnvironment.class, JTANonSharedPCEnvironment.class, JTASharedPCEnvironment.class })
    // NullPointer
    public void testGetTransactionClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            EntityTransaction tx = em.getTransaction();
            verify(tx != null, "EntityTransaction is null");
            tx.begin();
            tx.commit();
            tx.begin();
            tx.rollback();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCloseActiveTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep1 = new Department(11, "Muggles");
        Department dep2 = new Department(12, "Wizards");
        try {
            env.beginTransaction(em);
            em.persist(dep1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep1 = em.find(Department.class, new Integer(dep1.getId()));
            em.persist(dep2);
            em.close(); // persistence context should remain active
            try {
                em.contains(dep1); // should not work any longer
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCloseClosed() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            try {
                em.close();
                flop("operation on a closed entity manager did not throw IllegalStateException");
            } catch (IllegalStateException e) {
                // $JL-EXC$ expected behavior
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testQuery() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Query simpleQuery = em.createQuery("select d from Department d");
            Query namedQuery = em.createNamedQuery("getDepartmentByName"); // select d from Department d where d.name = ?1
            Query nativeQuery = em.createNativeQuery("delete from department");
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            verifyQuery(simpleQuery);
            verifyQuery(namedQuery);
            verifyQuery(nativeQuery);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCloseInsideTransaction() {
        final JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "QA");
            env.beginTransaction(em);
            em.persist(dep);
            em.close();
            verify(!em.isOpen(), "EntityManager is not closed");
            env.commitTransaction(em);
            // it would be nice to test that the underlying EntityManagerImpl is closed, but how?
            em = env.getEntityManager();
            verify(!em.contains(dep), "persistence context not empty");
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            verify(dep != null, "Department is null");
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyQuery(Query query) {
        try {
            query.getResultList();
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.getSingleResult();
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.executeUpdate();
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setMaxResults(1);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setFirstResult(1);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setHint("name", new Object());
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setParameter("name", new Object());
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        } catch (UnsupportedOperationException e) {
            // $JL-EXC$ happens with native queries
        }
        try {
            query.setParameter("name", new Date(), TemporalType.DATE);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        } catch (UnsupportedOperationException e) {
            // $JL-EXC$ happens with native queries
        }
        try {
            query.setParameter("name", new GregorianCalendar(), TemporalType.DATE);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        } catch (UnsupportedOperationException e) {
            // $JL-EXC$ happens with native queries
        }
        try {
            query.setParameter(1, new Object());
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setParameter(1, new Date(), TemporalType.DATE);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setParameter(1, new GregorianCalendar(), TemporalType.DATE);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
        try {
            query.setFlushMode(FlushModeType.AUTO);
            flop("operation on a query with closed entity manager did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
        }
    }

}
