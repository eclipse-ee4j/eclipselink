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

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.timestamp.Nasty;
import org.eclipse.persistence.testing.models.wdf.jpa1.timestamp.Timestamp;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Ignore;
import org.junit.Test;



public class TestPersist extends JPA1Base {

    private void verifyExistenceOnDatabase(int departmentId, String tableName) throws SQLException {
        if (tableName == null || !tableName.startsWith("TMP_")) {
            throw new IllegalArgumentException(
                    "None or illegal tableName was submitted to this method. Valid tableNames have to begin with 'TMP_'.");
        }
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("select ID from ").append(tableName).append(" where ID = ?");
            PreparedStatement stmt = conn.prepareStatement(builder.toString());
            try {
                stmt.setInt(1, departmentId);
                ResultSet rs = stmt.executeQuery();
                try {
                    verify(rs.next(), "no department with id " + departmentId + " found using JDBC.");
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
    public void testPersistNewDepartment() throws SQLException {
        /*
         * If X is a new entity, it becomes managed. The entity X will be entered into the database at or before transaction
         * commit or as a result of the flush operation.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "R&D");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(1, "TMP_DEP");
            Department dep2 = new Department(2, "QM");
            env.beginTransaction(em);
            em.persist(dep2);
            em.flush();
            // will not work on Oracle!
            // verify existence after flush
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(2, "TMP_DEP");
            // TODO verify cascading
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPersistNewBasicTypesFieldAccess() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            BasicTypesFieldAccess btfa = new BasicTypesFieldAccess(1);
            btfa.fill();
            env.beginTransaction(em);
            em.persist(btfa);
            env.commitTransactionAndClear(em);
            // verify existence after commit
            verifyExistenceOnDatabase(1, "TMP_BASIC_TYPES_FA");
            BasicTypesFieldAccess btfa2 = new BasicTypesFieldAccess(2);
            btfa2.fill();
            env.beginTransaction(em);
            em.persist(btfa2);
            em.flush();
            // will not work on Oracle!
            // verify existence after flush
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(2, "TMP_BASIC_TYPES_FA");
            // TODO verify cascading
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyPersistManaged(boolean flushBeforePersist) {
        /*
         * If X is a pre-existing managed entity, it is ignored by the persist operation. However, the persist operation is
         * cascaded to entities referenced by X, if the relationships from X to these other entities is annotated with the
         * cascade=PERSIST or cascade=ALL annotation element value or specified with the equivalent XML descriptor element.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // 1. managed via find
            final int id1 = (flushBeforePersist ? 100 : 0) + 11;
            Department dep = new Department(id1, "OLD");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            // find a department in the state MANAGED
            dep = em.find(Department.class, new Integer(id1));
            verify(dep != null, "department not found");
            dep.setName("CHANGED");
            if (flushBeforePersist) {
                em.flush();
            }
            // this persist should be ignored as object is already managed
            em.persist(dep);
            // TODO verify that persist is ignored (use callback methods)
            // TODO verify cascading
            env.commitTransactionAndClear(em);
            // 2. managed via persist
            final int id2 = (flushBeforePersist ? 100 : 0) + 12;
            dep = new Department(id2, "OLD");
            env.beginTransaction(em);
            em.persist(dep);
            dep.setName("CHANGED");
            if (flushBeforePersist) {
                em.flush();
            }
            // this persist should be ignored as object is already managed
            em.persist(dep);
            // TODO verify that persist is ignored (use callback methods)
            // TODO verify cascading
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyPersistRemoved(boolean flushBeforePersist) {
        /*
         * If X is a removed entity, it becomes managed.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // 1. find an existing department, remove it and call persist
            final int id1 = (flushBeforePersist ? 100 : 0) + 21;
            Department dep = new Department(id1, "REMOVE");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id1));
            em.remove(dep);
            // now, the entity should be REMOVED
            if (flushBeforePersist) {
                em.flush();
            }
            em.persist(dep);
            // now, the entity should be managed again
            dep.setName("REINVIGORATED");
            env.commitTransactionAndClear(em);
            
            dep = em.find(Department.class, new Integer(id1));
            verify(dep != null, "department not found");
            verify("REINVIGORATED".equals(dep.getName()), "department has wrong name: " + dep.getName());
            
            // 2. persist a new department, remove it and call persist
            env.beginTransaction(em);
            final int id2 = (flushBeforePersist ? 100 : 0) + 22;
            dep = new Department(id2, "REMOVE");
            em.persist(dep);
            em.remove(dep);
            if (flushBeforePersist) {
                em.flush();
            }
            em.persist(dep);
            dep.setName("REINVIGORATED");
            env.commitTransactionAndClear(em);
            
            dep = em.find(Department.class, new Integer(id2));
            verify(dep != null, "department not found");
            verify("REINVIGORATED".equals(dep.getName()), "department has wrong name: " + dep.getName());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPersistManaged() {
        verifyPersistManaged(false);
    }

    @Test
    public void testPersistManagedFlushed() {
        verifyPersistManaged(true);
    }

    @Test
    public void testPersistRemoved() {
        verifyPersistRemoved(false);
    }

    @Test
    public void testPersistRemovedFlushed() {
        verifyPersistRemoved(true);
    }

    @Test
    public void testPersistDetached() throws SQLException {
        /*
         * <li>If X is a detached object, the EntityExistsException may be thrown when the persist operation is invoked, 
         * or the EntityExistsException or another PersistenceException may be thrown at flush or commit time.</li>
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        try {
            // case 1: detached because entity exists on db but is not known by persistence context
            id = 31;
            Department dep = new Department(id, "DETACHED");
            // firstly, we create a department on the database
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(id, "TMP_DEP");
            // now, the department is detached, we try to persist it again
            env.beginTransaction(em);
            boolean failed = false;
            try {
                em.persist(dep);
            } catch (EntityExistsException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                failed = true;
            }
            verify(failed, "persist did succeed on a detached instance.");
            // case 2: detached because an object with same pk but different object identity is known by persistence context
            // case 2a: state of known object: FOR_INSERT
            id = 32;
            dep = new Department(id, "ORIGINAL");
            Department detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep); // this is now in state new
            try {
                em.persist(detachedDep);
            } catch (EntityExistsException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                failed = true;
            }
            verify(failed, "persist did succeed on a detached instance.");
            // case 2b: state of known object: FOR_UPDATE
            id = 33;
            dep = new Department(id, "ORIGINAL");
            detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(id, "TMP_DEP");
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // this is now in state managed
            try {
                em.persist(detachedDep);
            } catch (EntityExistsException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                failed = true;
            }
            verify(failed, "persist did succeed on a detached instance.");
            // case 2c: state of known object: FOR_DELETE
            id = 34;
            dep = new Department(id, "ORIGINAL");
            detachedDep = new Department(id, "DETACHED");
            failed = false;
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(id, "TMP_DEP");
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep); // this is now in state deleted
            try {
                em.persist(detachedDep);
            } catch (EntityExistsException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                failed = true;
            }
            verify(failed, "persist did succeed on a detached instance.");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testPersistDeleteExecuted() throws SQLException {
        /*
         * If X is a detached object, an IllegalArgumentException will be thrown by the persist operation (or the transaction
         * commit will fail).
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        try {
            // case 2d: state of known object: DELETE_EXECUTED
            id = 35;
            Department dep = new Department(id, "ORIGINAL");
            Department detachedDep = new Department(id, "DETACHED");
            boolean failed = false;
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(id, "TMP_DEP");
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep);
            em.flush();
            try {
                em.persist(detachedDep);
            } catch (IllegalArgumentException e) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
                failed = true;
            }
            verify(failed, "persist did succeed on a detached instance.");
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * throws IllegalArgumentException, if instance is not an entity
     */
    @Test
    public void testNotAnEntity() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            try {
                em.persist("Hutzliputz");
                flop("no IllegalArgumentException ");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            } finally {
                env.rollbackTransactionAndClear(em);
            }
            env.beginTransaction(em);
            try {
                em.persist(null);
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

    @Ignore // @TestProperties(unsupportedEnvironments={JTANonSharedPCEnvironment.class, ResourceLocalEnvironment.class})
    public void testNoTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep = new Department(41, "dep41");
        try {
            em.persist(dep);
            flop("exception not thrown as expected");
        } catch (TransactionRequiredException e) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testTransactionMarkedForRollback() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: persistence context already available when tx marked for rollback
            Department dep = new Department(42, "dep42");
            env.beginTransaction(em);
            verify(!em.contains(dep), "entity contained in persistence context"); // this ensures that the pc is available
            env.markTransactionForRollback(em);
            em.persist(dep);
            verify(em.contains(dep), "entity not contained in persistence context");
            env.rollbackTransaction(em);
            // case 2: persistence context not yet available when tx marked for rollback
            // will throw an exception in the container-managed JTA case
            if (!(env instanceof /* JTASharedPCEnvironment */Object)) {
                dep = new Department(43, "dep43");
                env.beginTransaction(em);
                env.markTransactionForRollback(em);
                em.persist(dep);
                verify(em.contains(dep), "entity not contained in persistence context");
                env.rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPersistNewEntityWithIdModifiedInPrePersist() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Timestamp timestamp = new Timestamp();
            verify(timestamp.getId() == null, "id is not null");
            env.beginTransaction(em);
            em.persist(timestamp);
            Long value = timestamp.getId();
            verify(value != null, "id is null");
            env.commitTransactionAndClear(em);
            verify(em.find(Timestamp.class, value) != null, "not found");
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyPersistRemovedEntityWithIdModifiedInPrePersist(boolean removeExecuted) {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Timestamp timestamp = new Timestamp();
            verify(timestamp.getId() == null, "id is not null");
            env.beginTransaction(em);
            em.persist(timestamp);
            env.commitTransaction(em);
            
            env.beginTransaction(em);
            final Long id1 = timestamp.getId();
            timestamp = em.find(Timestamp.class, id1);
            em.remove(timestamp);
            if (removeExecuted) {
                em.flush();
            }
            
            em.persist(timestamp);
            
            env.commitTransactionAndClear(em);
            final Long id2 = timestamp.getId();
            
            Assert.assertFalse(id2.equals(id1));
            
            Assert.assertNull(em.find(Timestamp.class, id1));
            Assert.assertNotNull(em.find(Timestamp.class, id1));

            
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testPersistRemovedEntityWithIdModifiedInPrePersist() {
        verifyPersistRemovedEntityWithIdModifiedInPrePersist(false);
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testPersistRemovedEntityWithIdModifiedInPrePersistFlushed() {
        verifyPersistRemovedEntityWithIdModifiedInPrePersist(true);
    }

    @Test
    public void testNastyTimestampTwice() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Nasty nasty = new Nasty();
            verify(nasty.getId() == null, "id is not null");
            env.beginTransaction(em);
            em.persist(nasty);
            Long value = nasty.getId();
            verify(value != null, "id is null");
            try {
                em.persist(new Nasty());
                env.commitTransaction(em);
                flop("persisting second nasty timestamp succeeded");
            } catch (PersistenceException ex) {
                Assert.assertTrue(true);
            }
            if (env.isTransactionActive(em)) {
                env.rollbackTransactionAndClear(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDuprec() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department department = new Department(999, "X");
            em.persist(department);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            try {
                em.persist(department); // exception here
                env.commitTransaction(em); // or here
                flop("duprec not detected");
            } catch (PersistenceException ex) {
                // OK
            } catch (/* JTARollback */Exception ex) {
                // JTA OK
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDuprecBatch() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department d1 = new Department(998, "X");
            em.persist(d1);
            Department d2 = new Department(997, "X");
            em.persist(d2);
            Department d3 = new Department(996, "X");
            em.persist(d3);
            Department d4 = new Department(995, "X");
            em.persist(d4);
            Department d5 = new Department(994, "X");
            em.persist(d5);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            try {
                d1 = new Department(993, "X");
                em.persist(d1);
                d2 = new Department(992, "X");
                em.persist(d2);
                d3 = new Department(991, "X");
                em.persist(d3);
                em.persist(d4); // duprec: 995
                d5 = new Department(990, "X");
                em.persist(d5);
                env.commitTransaction(em); // or here
                flop("duprec not detected");
            } catch (PersistenceException ex) {
                // OK
            } catch (/* JTARollback */Exception ex) {
                // JTA OK
            }
        } finally {
            closeEntityManager(em);
        }
    }

}
