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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestRemove extends JPA1Base {

    private void verifyAbsenceOnDatabase(int departmentId) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select ID, NAME from TMP_DEP where ID = ?");
            try {
                stmt.setInt(1, departmentId);
                ResultSet rs = stmt.executeQuery();
                try {
                    verify(!rs.next(), "department with id " + departmentId + " found using JDBC.");
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    @Test
    public void testRemoveNew() throws SQLException {
        /*
         * If X is a new entity, it is ignored by the remove operation. However, the remove operation is cascaded to entities
         * referenced by X, if the relationships from X to these other entities is annotated with the cascade=REMOVE or
         * cascade=ALL annotation element value.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "HUGO");
            env.beginTransaction(em);
            em.remove(dep);
            // TODO verify remove is ignored on this entity using lifecycle callbacks
            // TODO verify
            env.commitTransactionAndClear(em);
            verifyAbsenceOnDatabase(1);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyRemoveManaged(boolean flushBeforeRemove) throws SQLException {
        /*
         * If X is a managed entity, the remove operation causes it to become removed. The remove operation is cascaded to
         * entities referenced by X, if the relationships from X to these other entities is annotated with the cascade=REMOVE or
         * cascade=ALL annotation element value.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // 1. managed and in state MANAGED
            final int id1 = (flushBeforeRemove ? 100 : 0) + 11;
            Department dep = new Department(id1, "OLD");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            // find a department in the state MANAGED
            dep = em.find(Department.class, new Integer(id1));
            verify(dep != null, "department not found");
            dep.setName("NEW");
            if (flushBeforeRemove) {
                em.flush();
            }
            em.remove(dep);
            env.commitTransactionAndClear(em);
            verifyAbsenceOnDatabase(id1);
            // 2. managed and in state NEW
            final int id2 = (flushBeforeRemove ? 100 : 0) + 12;
            dep = new Department(id2, "OLD");
            env.beginTransaction(em);
            em.persist(dep);
            dep.setName("NEW");
            if (flushBeforeRemove) {
                em.flush();
            }
            em.remove(dep);
            env.commitTransactionAndClear(em);
            verifyAbsenceOnDatabase(id2);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveManaged() throws SQLException {
        verifyRemoveManaged(false);
    }

    @Test
    public void testRemoveManagedFlushed() throws SQLException {
        verifyRemoveManaged(true);
    }

    private void verifyRemoveRemoved(boolean flushBeforePersist) throws SQLException {
        /*
         * If X is a removed entity, it is ignored by the remove operation.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // 1. find an existing department, remove it and remove it again
            final int id1 = (flushBeforePersist ? 100 : 0) + 21;
            Department dep = new Department(id1, "REMOVE");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id1));
            em.remove(dep);
            // no, the entity should be REMOVED
            if (flushBeforePersist) {
                em.flush();
            }
            em.remove(dep);
            env.commitTransactionAndClear(em);
            // now, the entity should be managed again
            verifyAbsenceOnDatabase(id1);
            // 2. persist a new depatrment, remove it and call remove
            env.beginTransaction(em);
            final int id2 = (flushBeforePersist ? 100 : 0) + 22;
            dep = new Department(id2, "REMOVE");
            em.persist(dep);
            em.remove(dep);
            if (flushBeforePersist) {
                em.flush();
            }
            em.remove(dep);
            env.commitTransactionAndClear(em);
            verifyAbsenceOnDatabase(id2);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveRemoved() throws SQLException {
        verifyRemoveRemoved(false);
    }

    @Test
    public void testRemoveRemovedFlushed() throws SQLException {
        verifyRemoveRemoved(true);
    }

    @Test
    @ToBeInvestigated
    public void testRemoveDetached() {
        /*
         * If X is a detached entity, an IllegalArgumentException will be thrown by the remove operation (or the transaction
         * commit will fail).
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        Department detachedDep;
        try {
            // case 1: detached because entity exists on db but is not known by persistence context
            id = 31;
            dep = new Department(id, "DETACHED");
            // firstly, we create a department on the database
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            // now the department is detached, we try to remove it
            env.beginTransaction(em);
            boolean failed = false;
            try {
                em.remove(dep);
            } catch (IllegalArgumentException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException e) {
                failed = true;
            }
            verify(failed, "remove succeeded on a detached instance.");
            // case 2: detached because an object with same pk but different object identity is known by persistence context
            // case 2a: state of known object: FOR_INSERT
            id = 32;
            dep = new Department(id, "MANAGED_NEW");
            detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep); // object is now in state MANAGED_NEW
            try {
                em.remove(detachedDep);
            } catch (IllegalArgumentException e) {
                failed = true;
                env.rollbackTransaction(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException e) {
                failed = true;
            }
            verify(failed, "remove succeeded on a detached instance.");
            // case 2b: state of known object: FOR_UPDATE
            id = 33;
            dep = new Department(id, "MANAGED");
            detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // object is now in state MANAGED
            try {
                em.remove(detachedDep);
            } catch (IllegalArgumentException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException e) {
                failed = true;
            }
            verify(failed, "remove succeeded on a detached instance.");
            // case 2c: state of known object: FOR_DELETE
            id = 34;
            dep = new Department(id, "DELETED");
            detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep); // object is now in state DELETED
            try {
                em.remove(detachedDep);
            } catch (IllegalArgumentException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException e) {
                failed = true;
            }
            verify(failed, "remove succeeded on a detached instance.");
            // case 2d: state of known object: DELETE_EXECUTED
            id = 35;
            dep = new Department(id, "DELETED");
            detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep);
            em.flush();
            try {
                em.remove(detachedDep);
            } catch (IllegalArgumentException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException e) {
                failed = true;
            }
            verify(failed, "remove succeeded on a detached instance.");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNotAnEntity() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            try {
                em.remove("Hutzliputz");
                flop("no IllegalArgumentException ");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            } finally {
                env.rollbackTransactionAndClear(em);
            }
            env.beginTransaction(em);
            try {
                em.remove(null);
                flop("no IllegalArgumentException ");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            } finally {
                env.rollbackTransactionAndClear(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveWithCompositeKey() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Cubicle cub = new Cubicle(new Integer(30), new Integer(31), "green", null /* employee */);
            CubiclePrimaryKeyClass cubKey = new CubiclePrimaryKeyClass(new Integer(30), new Integer(31));
            env.beginTransaction(em);
            em.persist(cub);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            cub = em.find(Cubicle.class, cubKey);
            em.remove(cub);
            env.commitTransactionAndClear(em);
            cub = em.find(Cubicle.class, cubKey);
            verify(cub == null, "cubicle not removed");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    // @TestProperties(unsupportedEnvironments={JTANonSharedPCEnvironment.class, ResourceLocalEnvironment.class})
    public void testNoTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep = new Department(40, "dep40");
        try {
            em.remove(dep);
        } catch (TransactionRequiredException e) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    // missing helpermethod JUnitTestCase.markTransactionForRollback
    public void testTransactionMarkedForRollback() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep = new Department(41, "dep41");
        try {
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            env.markTransactionForRollback(em);
            em.remove(dep);
            verify(!em.contains(dep), "entity still contained in persistence context");
            env.rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
