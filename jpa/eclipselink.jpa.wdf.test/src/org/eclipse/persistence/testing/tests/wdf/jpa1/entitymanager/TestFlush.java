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

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.Node;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;

public class TestFlush extends JPA1Base {

    /*
     * For any entity Y referenced by a relationship from X, where the relationship to Y has not been annotated with the cascade
     * element value cascade=PERSIST or cascade=ALL: <p> If Y is new or removed, an IllegalStateException will be thrown by the
     * flush operation (and the transaction rolled back) or the transaction commit will fail.
     */
    @Test
    public void testRelationshipToNew() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // case 1: direct relationship Employee -> Cubicle (new) - 1:1
            Department dep = new Department(1, "dep");
            Employee emp1 = new Employee(2, "first", "last", dep);
            Cubicle cub1 = new Cubicle(new Integer(3), new Integer(3), "color", emp1);
            emp1.setCubicle(cub1);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp1);
            boolean flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            // case 2: direct relationship Employee -> Project (new) - n:m
            dep = new Department(4, "dep");
            emp1 = new Employee(5, "first", "last", dep);
            Project proj = new Project("project");
            Set<Project> emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            Set<Employee> projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp1);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            // case 3: indirect relationship Employee -> Project -> Employee (new)
            dep = new Department(7, "dep");
            emp1 = new Employee(8, "first1", "last1", dep);
            Employee emp2 = new Employee(9, "first2", "last2", dep);
            proj = new Project("project");
            emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            Set<Project> emp2Projects = new HashSet<Project>();
            emp2Projects.add(proj);
            emp2.setProjects(emp2Projects);
            projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            projEmployees.add(emp2);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp1);
            em.persist(proj);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * For any entity Y referenced by a relationship from X, where the relationship to Y has not been annotated with the cascade
     * element value cascade=PERSIST or cascade=ALL: <p> If Y is new or removed, an IllegalStateException will be thrown by the
     * flush operation (and the transaction rolled back) or the transaction commit will fail.
     */
    @SuppressWarnings("unchecked")
    @Test
    @ToBeInvestigated
    public void testRelationshipToRemoved() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // case 1: direct relationship Employee -> Cubicle (FOR_DELETE) - 1:1
            Department dep = new Department(101, "dep");
            Employee emp1 = new Employee(102, "first", "last", dep);
            Cubicle cub1 = new Cubicle(new Integer(103), new Integer(103), "color", emp1);
            emp1.setCubicle(cub1);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp1);
            em.persist(cub1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            cub1 = em.find(Cubicle.class, cub1.getId());
            em.remove(cub1);
            boolean flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            em.clear();
            // case 2: direct relationship Employee -> Project (FOR_DELETE) - n:m
            dep = new Department(104, "dep");
            emp1 = new Employee(105, "first", "last", dep);
            Project proj = new Project("project");
            Set<Project> emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            Set<Employee> projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(proj);
            em.persist(dep);
            em.persist(emp1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            proj = em.find(Project.class, proj.getId());
            emp1.getProjects().size();
            em.remove(proj);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            em.clear();
            // case 3: indirect relationship Employee -> Project -> Employee (FOR_DELETE)
            dep = new Department(107, "dep");
            emp1 = new Employee(108, "first1", "last1", dep);
            Employee emp2 = new Employee(109, "first2", "last2", dep);
            proj = new Project("project");
            emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            Set<Project> emp2Projects = new HashSet<Project>();
            emp2Projects.add(proj);
            emp2.setProjects(emp2Projects);
            projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            projEmployees.add(emp2);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(proj);
            em.persist(dep);
            em.persist(emp1);
            em.persist(emp2);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            emp2 = em.find(Employee.class, new Integer(emp2.getId()));
            proj = em.find(Project.class, proj.getId());
            emp1.getProjects().size();
            proj.getEmployees().size();
            em.remove(emp2);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            em.clear();
            // case 1b: direct relationship Employee -> Cubicle (DELETE_EXECUTED) - 1:1
            dep = new Department(111, "dep");
            emp1 = new Employee(112, "first", "last", dep);
            cub1 = new Cubicle(new Integer(113), new Integer(112), "color", emp1);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp1);
            em.persist(cub1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            cub1 = em.find(Cubicle.class, cub1.getId());
            em.remove(cub1);
            em.flush();
            emp1.setCubicle(cub1);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            em.clear();
            // case 2b: direct relationship Employee -> Project (DELETE_EXECUTED) - n:m
            dep = new Department(114, "dep");
            emp1 = new Employee(115, "first", "last", dep);
            proj = new Project("project");
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp1);
            em.persist(proj);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            proj = em.find(Project.class, proj.getId());
            em.remove(proj);
            em.flush();
            emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            proj.setEmployees(projEmployees);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            em.clear();
            // case 3b: indirect relationship Employee -> Project -> Employee (DELETE_EXECUTED)
            dep = new Department(117, "dep");
            emp1 = new Employee(118, "first1", "last1", dep);
            emp2 = new Employee(119, "first2", "last2", dep);
            proj = new Project("project");
            emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(proj);
            em.persist(dep);
            em.persist(emp1);
            em.persist(emp2);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            emp2 = em.find(Employee.class, new Integer(emp2.getId()));
            proj = em.find(Project.class, proj.getId());
            emp1.getProjects().size();
            projEmployees = proj.getEmployees();
            projEmployees.size();
            em.remove(emp2);
            em.flush();
            emp2Projects = new HashSet<Project>();
            emp2Projects.add(proj);
            emp2.setProjects(emp2Projects);
            projEmployees.add(emp2);
            flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to an unmanaged entity");
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * This test checks a special case that can occur with lazily loaded relationships:
     * <ul>
     * <li>Read Project proj1 and remove it.</li>
     * <li>Read Employee emp1 with relationship to proj1 (lazy loading).</li>
     * <li>Assign the set of emp1's projects to a new employee emp2 (forces implicit loading on flush).</li>
     * <li>Flush -> IllegalStateException expected because of relation emp2 -> proj1 (removed).</li>
     * </ul>
     */
    @Test
    @ToBeInvestigated
    public void testRelationshipToRemovedLazy() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // case 1: explicit flush
            Department dep = new Department(201, "dep");
            Employee emp1 = new Employee(202, "first", "last", dep);
            Project proj = new Project("project");
            Set<Project> emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            Set<Employee> projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(proj);
            em.persist(dep);
            em.persist(emp1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(dep.getId()));
            proj = em.find(Project.class, proj.getId());
            em.remove(proj);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            // copy all projects from emp1 to emp2 with out actually touching them
            Employee emp2 = new Employee(203, "aaa", "bbb", dep);
            emp2.setProjects(emp1.getProjects());
            em.persist(emp2);
            boolean flushFailed = false;
            try {
                em.flush();
            } catch (IllegalStateException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to a removed entity");
            em.clear();
            // case 2: implicit flush during commit
            dep = new Department(204, "dep");
            emp1 = new Employee(205, "first", "last", dep);
            proj = new Project("project");
            emp1Projects = new HashSet<Project>();
            emp1Projects.add(proj);
            emp1.setProjects(emp1Projects);
            projEmployees = new HashSet<Employee>();
            projEmployees.add(emp1);
            proj.setEmployees(projEmployees);
            env.beginTransaction(em);
            em.persist(proj);
            em.persist(dep);
            em.persist(emp1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(dep.getId()));
            proj = em.find(Project.class, proj.getId());
            em.remove(proj);
            emp1 = em.find(Employee.class, new Integer(emp1.getId()));
            // copy all projects from emp1 to emp2 with out actually touching them
            emp2 = new Employee(206, "aaa", "bbb", dep);
            emp2.setProjects(emp1.getProjects());
            em.persist(emp2);
            flushFailed = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                flushFailed = true;
            }
            verify(!env.isTransactionActive(em), "Transaction still active");
            verify(flushFailed, "flush succeeded although there is a relation to a removed entity");
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Force an exception during flush and check whether the current transaction is rolled back.
     */
    @Test
    public void testWithException() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Node node = new Node(301, true); // PostPersist method will throw a Node.MyRuntimeException
            env.beginTransaction(em);
            em.persist(node);
            boolean flushFailed = false;
            try {
                em.flush();
            } catch (Node.MyRuntimeException e) {
                // $JL-EXC$ this is expected behavior
                flushFailed = true;
                verify(env.isTransactionMarkedForRollback(em),
                        "IllegalStateException during flush did not mark transaction for rollback");
            }
            verify(flushFailed, "callback method did not throw exception as expected");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRestoreFieldAfterFlush() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final String initial = "initial";
            final int id = 301;
            Department department = new Department(id, initial);
            env.beginTransaction(em);
            em.persist(department);
            department.setName("changed");
            em.flush();
            // undo the change between flush and commit (on a new entity)
            department.setName(initial);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            department = em.find(Department.class, Integer.valueOf(id));
            verify(initial.equals(department.getName()), "wrong name: " + department.getName());
            department.setName("changed");
            em.flush();
            // lets try the same with a managed field
            department.setName(initial);
            env.commitTransactionAndClear(em);
            department = em.find(Department.class, Integer.valueOf(id));
            verify(initial.equals(department.getName()), "wrong name: " + department.getName());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRestoreRelationAfterFlush() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final int id = 302;
            Employee frank = new Employee(id, "Frank", "Schuster", null);
            env.beginTransaction(em);
            Review r1 = new Review(101, Date.valueOf("2006-10-19"), "Performance");
            frank.addReview(r1);
            Review r2 = new Review(102, Date.valueOf("2006-10-19"), "Passion");
            frank.addReview(r2);
            Review r3 = new Review(103, Date.valueOf("2006-10-19"), "Six-Sigma");
            frank.addReview(r3);
            em.persist(frank);
            em.persist(r1);
            em.persist(r2);
            em.persist(r3);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            frank = em.find(Employee.class, Integer.valueOf(id));
            Set<Review> reviewsFound = frank.getReviews();
            int foundSize = reviewsFound.size();
            // lets remove a department the same with a managed field
            Set<Review> set = new HashSet<Review>();
            set.add(r1);
            set.add(r2);
            frank.setReviews(set);
            em.flush();
            // undo the change
            frank.setReviews(reviewsFound);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            frank = em.find(Employee.class, Integer.valueOf(id));
            verify(frank.getReviews().size() == foundSize, "wrong number of reviews: " + frank.getReviews().size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNoTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            em.flush();
            flop("exception not thrown as expected");
        } catch (TransactionRequiredException e) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ToBeInvestigated
    public void testTransactionMarkedForRollback() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep = new Department(401, "dep401");
        try {
            env.beginTransaction(em);
            em.persist(dep);
            env.markTransactionForRollback(em);
            em.flush();
            // verify that entity is inserted
            Query query = em.createQuery("select d from Department d where d.id = ?1");
            query.setParameter(1, Integer.valueOf(dep.getId()));
            List<Department> result = query.getResultList();
            verify(result.size() == 1, "query returned " + result.size() + " entities");
            env.rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testChangedEntityIgnoredByFlush() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Employee emp = new Employee(911, "Robi", "Tobi", null);
        try {
            env.beginTransaction(em);
            em.persist(emp);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            Employee found = em.find(Employee.class, 911);
            found.clearPostUpdate();
            found.setLastName("lesbar");
            em.createQuery("select i from Island i").getResultList();
            verify(!found.postUpdateWasCalled(), "post update was called");
            env.rollbackTransactionAndClear(em);

        } finally {
            closeEntityManager(em);
        }
    }

}
