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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Ignore;
import org.junit.Test;

public class TestRefresh extends JPA1Base {

    @Test
    public void testRefreshNew() {
        /*
         * Refresh on an entity that is not under control of the entity manager should throw an IllegalArgumentException.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        try {
            id = 1;
            dep = new Department(id, "NEW");
            env.beginTransaction(em);
            try {
                em.refresh(dep);
                flop("refresh did not throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            }
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testRefreshManagedNew() throws SQLException {
        /*
         * Note: The specification doesn't state explicitly how to behave in this case, so we test our interpretation: - If the
         * entity doesn't exist on the database, nothing is changed - If the entity exists on the database, the data is loaded
         * and the state changes from FOR_INSERT to FOR_UPDATE (in order to avoid errors at flush time)
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        try {
            // case 2: state MANAGED_NEW, but exists on DB (inserted in different tx)
            id = 12;
            dep = new Department(id, "MANAGED_NEW");
            Department depInserted = new Department(id, "INSERTED");
            env.beginTransaction(em);
            em.persist(dep);
            insertDepartmentIntoDatabase(depInserted);
            verifyExistenceOnDatabase(id);
            // entity is now in state MANAGED_NEW, but record exists on db
            em.refresh(dep);
            checkDepartment(dep, id, "INSERTED"); // this should now be in state MANAGED
            verify(em.contains(dep), "Department is not managed");
            dep.setName("UPDATED");
            env.commitTransactionAndClear(em);
            // verify that updated name present on db
            dep = em.find(Department.class, new Integer(id));
            checkDepartment(dep, id, "UPDATED");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testRefreshManagedNewNotOnDB() throws SQLException {
        /*
         * Note: The specification doesn't state explicitly how to behave in this case, so we test our interpretation: - If the
         * entity doesn't exist on the database, nothing is changed - If the entity exists on the database, the data is loaded
         * and the state changes from FOR_INSERT to FOR_UPDATE (in order to avoid errors at flush time)
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        try {
            // case 1: state MANAGED_NEW and does not exist on DB
            id = 11;
            dep = new Department(id, "MANAGED_NEW");
            env.beginTransaction(em);
            em.persist(dep); // this is now in state MANAGED_NEW, but not on db
            em.refresh(dep); // nothing should happen
            verify(em.contains(dep), "Department is not managed");
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(id);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRefreshManaged() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        Department updatedDep;
        try {
            // case 1: undo own changes
            id = 21;
            dep = new Department(id, "MANAGED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // this is now in state MANAGED
            dep.setName("UPDATED");
            em.refresh(dep);
            checkDepartment(dep, id, "MANAGED");
            verify(em.contains(dep), "Department is not managed");
            env.commitTransactionAndClear(em);
            // verify that original name present on db
            dep = em.find(Department.class, new Integer(id));
            checkDepartment(dep, id, "MANAGED");
            // case 2: refresh with data changed on db in a different tx
            id = 22;
            dep = new Department(id, "MANAGED");
            updatedDep = new Department(id, "UPDATED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // this is now in state MANAGED
            updateDepartmentOnDatabase(updatedDep);
            em.refresh(dep);
            checkDepartment(dep, id, "UPDATED");
            verify(em.contains(dep), "Department is not managed");
            dep.setName("MANAGED");
            env.commitTransactionAndClear(em);
            // verify that original name present on db
            dep = em.find(Department.class, new Integer(id));
            checkDepartment(dep, id, "MANAGED");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testRefreshManagedCheckContains() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        try {
            // case 3: try to refresh, but record has been deleted on db in a different tx
            /*
             * We expect an EntityNotFoundException. However, the specification does not state explicitly in which state the
             * managed entity should be after the exception. We are going to remove the entity from the persistence context, so
             * it is detached afterwards.
             */
            id = 23;
            dep = new Department(id, "MANAGED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // this is now in state MANAGED
            deleteDepartmentFromDatabase(id);
            verifyAbsenceFromDatabase(em, id);
            try {
                em.refresh(dep);
                flop("refresh did not throw EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                verify(true, "");
            }
            verify(!em.contains(dep), "entity still managed after EntityNotFoundException");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testRefreshDeleted() throws SQLException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        Department updatedDep;
        try {
            // case 1: undo own changes
            id = 31;
            dep = new Department(id, "DELETED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep); // this is now in state DELETED
            dep.setName("UPDATED");
            em.refresh(dep);
            checkDepartment(dep, id, "DELETED");
            verify(!em.contains(dep), "Department is managed");
            env.commitTransactionAndClear(em);
            verifyAbsenceFromDatabase(em, id);
            // case 2: refresh with data changed on db in a different tx
            id = 32;
            dep = new Department(id, "DELETED");
            updatedDep = new Department(id, "UPDATED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep); // this is now in state DELETED
            updateDepartmentOnDatabase(updatedDep);
            em.refresh(dep);
            checkDepartment(dep, id, "UPDATED");
            verify(!em.contains(dep), "Department is managed");
            env.commitTransactionAndClear(em);
            verifyAbsenceFromDatabase(em, id);
            // case 3: try to refresh, but record has been deleted on db in a different tx
            /*
             * We expect an EntityNotFoundException. However, the specification does not state explicitly in which state the
             * managed entity should be after the exception. In our implementation, the entity is kept in the persistence
             * context in state DELETE_EXECUTED.
             */
            id = 33;
            dep = new Department(id, "DELETED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep); // this is now in state DELETED
            deleteDepartmentFromDatabase(id);
            verifyAbsenceFromDatabase(em, id);
            try {
                em.refresh(dep);
                flop("refresh did not throw EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                verify(true, "");
            }
            verifyAbsenceFromDatabase(em, id);
            env.rollbackTransactionAndClear(em);
            // case 4: try to refresh, but record has been deleted on db by flushing the remove operation
            id = 34;
            dep = new Department(id, "DELETED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep);
            em.flush(); // this is now in state DELETE_EXECUTED
            verifyAbsenceFromDatabase(em, id);
            try {
                em.refresh(dep);
                flop("refresh did not throw EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                verify(true, "");
            }
            env.rollbackTransactionAndClear(em);
            verifyExistenceOnDatabase(id);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRefreshDetached() {
        /*
         * Refresh on a detached entity should throw an IllegalArgumentException.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        Department detachedDep;
        try {
            // case 1: entity exists on DB, but not contained in persistence context
            id = 41;
            dep = new Department(id, "DETACHED");
            // firstly, we create a department on the database
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            try {
                em.refresh(dep);
                flop("refresh did not throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            }
            env.rollbackTransactionAndClear(em);
            // case 2: entity is contained in persistence context, but object to be merged has different object identity
            // case 2a: state of known object: MANAGED_NEW
            id = 42;
            dep = new Department(id, "MANAGED_NEW");
            detachedDep = new Department(id, "DETACHED");
            env.beginTransaction(em);
            em.persist(dep); // this is now in state new
            try {
                em.refresh(detachedDep); // this object is detached
                flop("refresh did not throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            }
            env.rollbackTransactionAndClear(em);
            // case 2b: state of known object: MANAGED
            id = 43;
            dep = new Department(id, "MANAGED");
            detachedDep = new Department(id, "DETACHED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // this is now in state MANAGED
            try {
                em.refresh(detachedDep); // this object is detached
                flop("refresh did not throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            }
            env.rollbackTransactionAndClear(em);
            // case 2c: state of known object: DELETED
            id = 44;
            dep = new Department(id, "DELETED");
            detachedDep = new Department(id, "DETACHED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id));
            em.remove(dep); // this is now in state DELETED
            try {
                em.refresh(detachedDep); // this object is detached
                flop("refresh did not throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            }
            env.rollbackTransactionAndClear(em);
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
                em.refresh("Hutzliputz");
                flop("no IllegalArgumentException ");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            } finally {
                env.rollbackTransactionAndClear(em);
            }
            env.beginTransaction(em);
            try {
                em.refresh(null);
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

    @Ignore // @TestProperties(unsupportedEnvironments = { JTANonSharedPCEnvironment.class, ResourceLocalEnvironment.class })
    public void testNoTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        Department dep;
        try {
            id = 61;
            dep = new Department(id, "NO_TX");
            verify(!env.isTransactionActive(em), "transaction is active, can't execute test");
            try {
                em.refresh(dep);
                flop("refresh did not throw TransactionRequiredException");
            } catch (TransactionRequiredException e) {
                verify(true, "");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRefreshManagedWithRelationships() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: undo own changes
            Department dep = new Department(101, "Evangelists");
            Employee emp = new Employee(102, "First", "Last", dep);
            Review rev1 = new Review(103, Date.valueOf("2006-02-03"), "Code inspection");
            Review rev2 = new Review(104, Date.valueOf("2006-02-04"), "Design review");
            emp.addReview(rev1);
            emp.addReview(rev2);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp);
            em.persist(rev1);
            em.persist(rev2);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp = em.find(Employee.class, new Integer(emp.getId()));
            rev1 = em.find(Review.class, new Integer(rev1.getId()));
            Review rev3 = new Review(105, Date.valueOf("2006-02-05"), "Test coverage");
            Set<Review> reviews = new HashSet<Review>();
            reviews.add(rev1);
            reviews.add(rev3);
            emp.setReviews(reviews);
            rev1.setReviewText("UPDATED");
            em.refresh(emp);
            verify(em.contains(emp), "Employee is not managed");
            Set<Review> reviewsAfterRefresh = emp.getReviews();
            verify(reviewsAfterRefresh.size() == 2, "Employee contains wrong number of reviews: " + reviewsAfterRefresh.size());
            for (Review rev : reviewsAfterRefresh) {
                int id = rev.getId();
                verify(id == rev1.getId() || id == rev2.getId(), "Employee has wrong review: " + id);
                verify(em.contains(rev), "Review " + id + " is not managed");
            }
            env.commitTransactionAndClear(em);
            // verify that original name present on db
            rev1 = em.find(Review.class, new Integer(rev1.getId()));
            verify("UPDATED".equals(rev1.getReviewText()), "Rev1 has wrong text: " + rev1.getReviewText());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testTransactionMarkedForRollback() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep = new Department(111, "dep111");
        try {
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            dep.setName("updated");
            env.markTransactionForRollback(em);
            em.refresh(dep);
            checkDepartment(dep, dep.getId(), "dep111");
            verify(em.contains(dep), "entity not contained in persistence context");
            env.rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyExistenceOnDatabase(int departmentId) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select ID, NAME from TMP_DEP where ID = ?");
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

    private void verifyAbsenceFromDatabase(EntityManager em, int id) {
        Query query = em.createQuery("SELECT d.id from Department d where d.id = ?1");
        query.setFlushMode(FlushModeType.COMMIT);
        query.setParameter(1, Integer.valueOf(id));
        verify(query.getResultList().size() == 0, "wrong result list size");
    }

    private void deleteDepartmentFromDatabase(int departmentId) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("delete from TMP_DEP where ID = ?");
            try {
                stmt.setInt(1, departmentId);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    private void insertDepartmentIntoDatabase(Department department) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("insert into TMP_DEP (ID, NAME, VERSION) values (?, ?, ?)");
            try {
                stmt.setInt(1, department.getId());
                stmt.setString(2, department.getName());
                stmt.setShort(3, (short) 0);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    private void updateDepartmentOnDatabase(Department department) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("update TMP_DEP set NAME = ? where ID = ?");
            try {
                stmt.setString(1, department.getName());
                stmt.setInt(2, department.getId());
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    private void checkDepartment(Department department, int id, String name) {
        verify(department != null, "department is null");
        verify(id == department.getId(), "department has wrong id: " + department.getId());
        verify(name.equals(department.getName()), "department has wrong name: " + department.getName());
    }
}
