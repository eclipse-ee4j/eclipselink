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

import java.io.IOException;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Hobby;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.models.wdf.jpa1.timestamp.Nasty;
import org.eclipse.persistence.testing.models.wdf.jpa1.timestamp.Timestamp;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Ignore;
import org.junit.Test;

public class TestMerge extends JPA1Base {

    @Test
    public void testMergeNew() {
        /*
         * If X is a new entity instance, a new managed entity instance X' is created and the state of X is copied into the new
         * managed entity instance X'.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "NEW");
            env.beginTransaction(em);
            Department dep2 = em.merge(dep);
            checkDepartment(dep2, 1, "NEW");
            env.commitTransactionAndClear(em);
            verify(!em.contains(dep), "entity manager contains department -> it cannot be detached");
            verify(!em.contains(dep2), "entity manager contains department -> it cannot be detached");
            verifyExistence(em, 1, "NEW");
        } finally {
            closeEntityManager(em);
        }
    }

    private void checkDepartment(Department department, int id, String name) {
        verify(department != null, "department is null");
        verify(id == department.getId(), "department has wrong id: " + department.getId());
        verify(name.equals(department.getName()), "department has wrong name: " + department.getName());
    }

    @Test
    @ToBeInvestigated
    public void testMergeDetachedWithRelation() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(101, "Aztec gods");
            Employee empDetached = new Employee(102, "Huitzil", "Opochtli", dep);
            Review revManaged = new Review(103, Date.valueOf("2006-01-01"), "managed");
            Review revDetached = new Review(104, Date.valueOf("2006-01-02"), "detached/in PC");
            Review revDetachedNotInPC = new Review(105, Date.valueOf("2006-01-03"), "detached/not in PC");
            Review revNew = new Review(106, Date.valueOf("2006-01-04"), "new");
            Review revAddedInManagedEmp = new Review(107, Date.valueOf("2006-01-05"), "added in managed emp");
            Hobby hobbyManaged = new Hobby("managed");
            Hobby hobbyDetached = new Hobby("detached/in PC");
            Hobby hobbyDetachedNotInPC = new Hobby("detached/not in PC");
            Hobby hobbyNew = new Hobby("new");
            Hobby hobbyAddedInManagedEmp = new Hobby("added in managed emp");
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(empDetached);
            em.persist(revManaged);
            em.persist(revDetached);
            em.persist(revDetachedNotInPC);
            em.persist(revAddedInManagedEmp);
            em.persist(hobbyManaged);
            em.persist(hobbyDetached);
            em.persist(hobbyDetachedNotInPC);
            em.persist(hobbyAddedInManagedEmp);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            // relations of the managed employee
            Employee empManaged = em.find(Employee.class, new Integer(empDetached.getId()));
            empManaged.setFirstName("Adrian");
            revManaged = em.find(Review.class, new Integer(revManaged.getId()));
            Review revSamePKAsDetached = em.find(Review.class, new Integer(revDetached.getId()));
            revSamePKAsDetached.setReviewText("same PK as detached review");
            revAddedInManagedEmp = em.find(Review.class, new Integer(revAddedInManagedEmp.getId()));
            em.persist(revAddedInManagedEmp);
            empManaged.addReview(revManaged);
            empManaged.addReview(revSamePKAsDetached);
            empManaged.addReview(revAddedInManagedEmp);
            hobbyManaged = em.find(Hobby.class, hobbyManaged.getId());
            Hobby hobbySamePKAsDetached = em.find(Hobby.class, hobbyDetached.getId());
            hobbySamePKAsDetached.setDescription("same PK as detached hobby");
            hobbyAddedInManagedEmp = em.find(Hobby.class, hobbyAddedInManagedEmp.getId());
            em.persist(hobbyAddedInManagedEmp);
            empManaged.addHobby(hobbyManaged);
            empManaged.addHobby(hobbySamePKAsDetached);
            empManaged.addHobby(hobbyAddedInManagedEmp);
            // relations of the detached employee
            empDetached.addReview(revManaged);
            empDetached.addReview(revDetached);
            empDetached.addReview(revDetachedNotInPC);
            empDetached.addReview(revNew);
            empDetached.addHobby(hobbyManaged);
            empDetached.addHobby(hobbyDetached);
            empDetached.addHobby(hobbyDetachedNotInPC);
            empDetached.addHobby(hobbyNew);
            Employee empAfterMerge = em.merge(empDetached);
            Set<Review> reviews = empAfterMerge.getReviews();
            List<Hobby> hobbies = empAfterMerge.getHobbies();
            verify(empAfterMerge == empManaged, "entity manager changed identity of managed object");
            verify(reviews.size() == 4, "Merged employee has " + reviews.size() + " reviews, expected 4");
            verify(hobbies.size() == 4, "Merged employee has " + hobbies.size() + " hobbies, expected 4");
            verify(empAfterMerge.getFirstName().equals(empDetached.getFirstName()), "First name not merged correctly");
            verify(containsIdentical(reviews, revManaged), "Merged employee does not contain revManaged");
            verify(containsIdentical(hobbies, hobbyManaged), "Merged employee does not contain hobbyManaged");
            verify(containsIdentical(reviews, revSamePKAsDetached), "Merged employee does not contain revSamePKAsDetached");
            verify(containsIdentical(hobbies, hobbySamePKAsDetached), "Merged employee does not contain hobbySamePKAsDetached");
            verify("same PK as detached review".equals(revSamePKAsDetached.getReviewText()),
                    "Text of revSamePKAsDetached changed");
            verify("same PK as detached hobby".equals(hobbySamePKAsDetached.getDescription()),
                    "Text of hobbySamePKAsDetached changed");
            verify(containsIdentical(reviews, revNew), "Merged employee does not contain revNew");
            verify(containsIdentical(hobbies, hobbyNew), "Merged employee does not contain hobbyNew");
            verify(!em.contains(revNew), "revNew is managed");
            verify(!em.contains(hobbyNew), "hobbyNew is managed");
            verify(!containsIdentical(reviews, revAddedInManagedEmp), "Merged employee contains revAddedInManagedEmp");
            verify(!containsIdentical(hobbies, hobbyAddedInManagedEmp), "Merged employee contains hobbyAddedInManagedEmp");
            verify(em.contains(revAddedInManagedEmp), "revAddedInManagedEmp is not managed");
            verify(em.contains(hobbyAddedInManagedEmp), "hobbyAddedInManagedEmp is not managed");
            int pk = revDetachedNotInPC.getId();
            Review revSamePK = null;
            for (Review rev : reviews) {
                if (rev.getId() == pk) {
                    revSamePK = rev;
                    break;
                }
            }
            verify(revSamePK != null, "Merged employee does not contain review with id " + pk);
            verify(em.contains(revSamePK), "Review " + pk + " is not managed");
            String hobbyId = hobbyDetachedNotInPC.getId();
            Hobby hobbySamePK = null;
            for (Hobby hobby : hobbies) {
                if (hobby.getId().equals(hobbyId)) {
                    hobbySamePK = hobby;
                    break;
                }
            }
            verify(hobbySamePK != null, "Merged employee does not contain hobby with id " + pk);
            verify(em.contains(hobbySamePK), "Hobby " + pk + " is not managed");
            // commit should fail because empAfterMerge has a relation to a new review/hobby
            boolean commitFailed = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                commitFailed = true;
            }
            verify(commitFailed, "Commit succeeded although employee had a relation to a new review");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeManaged() {
        /*
         * If X is a managed entity, it is ignored by the merge operation, however, the merge operation is cascaded to entities
         * referenced by relationships from X if these relationships have been annotated with the cascade element value
         * cascade=MERGE or cascade=ALL annotation.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: object is managed and in state NEW
            int id = 11;
            Department dep = new Department(id, "MANAGED_NEW");
            env.beginTransaction(em);
            em.persist(dep); // object is now in state NEW
            verify(em.contains(dep), "entity manager does not contain object");
            Department mergeResult = em.merge(dep); // this should be ignored
            checkDepartment(mergeResult, id, "MANAGED_NEW");
            verify(mergeResult == dep, "merge operation has changed identity of a managed object");
            env.commitTransactionAndClear(em);
            // case 2: object is managed and in state MANAGED
            id = 12;
            dep = new Department(id, "MANAGED");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // object is now in state MANAGED
            checkDepartment(dep, id, "MANAGED");
            verify(em.contains(dep), "entity manager does not contain object");
            mergeResult = em.merge(dep); // this should be ignored
            checkDepartment(mergeResult, id, "MANAGED");
            verify(mergeResult == dep, "merge operation has changed identity of a managed object");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testMergeRemoved() {
        /*
         * If X is a removed entity instance, an IllegalArgumentException will be thrown by the merge operation (or the
         * transaction commit will fail).
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // 1. find an existing department, remove it and call merge
            int id1 = 21;
            Department dep = new Department(id1, "REMOVE");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id1));
            em.remove(dep);
            // now the entity should be REMOVED
            boolean failed = false;
            try {
                em.merge(dep);
            } catch (IllegalArgumentException ex) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException ex) {
                failed = true;
            }
            verify(failed, "merge did succeed on a removed instance.");
            // 2. remove managed-new object
            // what happens is undefined by the spec. Consequently, we don't check anything.
            // 3. find an existing department, remove it and call merge on a different object with same primary key
            // (so the object we pass in is actually detached, but the object under control of entity manager
            // is in state REMOVED)
            int id = 23;
            Department depEM = new Department(id, "REMOVE");
            Department depClient = new Department(id, "NEW_NAME");
            env.beginTransaction(em);
            em.persist(depEM);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            depEM = em.find(Department.class, new Integer(id));
            checkDepartment(depEM, id, "REMOVE");
            em.remove(depEM); // this is now in state REMOVED
            failed = false;
            try {
                em.merge(depClient);
            } catch (IllegalArgumentException ex) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException ex) {
                failed = true;
            }
            verify(failed, "merge did succeed on a removed instance.");
            // 4. find an existing department, remove it, flush, and call merge
            id1 = 24;
            dep = new Department(id1, "REMOVE");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id1));
            em.remove(dep);
            em.flush();
            // now the entity should be in state DELETE_EXECUTED
            failed = false;
            try {
                em.merge(dep);
            } catch (IllegalArgumentException ex) {
                failed = true;
                env.rollbackTransactionAndClear(em);
            }
            try {
                if (env.isTransactionActive(em)) {
                    env.commitTransactionAndClear(em);
                }
            } catch (PersistenceException ex) {
                failed = true;
            }
            verify(failed, "merge did succeed on a removed instance.");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeDetached() {
        /*
         * If X is a detached entity, it is copied onto a pre-existing managed entity instance X' of the same identity or a new
         * managed copy of X is created.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id;
        try {
            // case 1: entity exists on DB, but not contained in persistence context
            id = 31;
            Department dep = new Department(id, "DETACHED");
            // firstly, we create a department on the database
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verify(!em.contains(dep), "entity manager contains department -> it cannot be detached");
            dep.setName("NEW_NAME");
            env.beginTransaction(em);
            Department dep2 = em.merge(dep);
            checkDepartment(dep2, id, "NEW_NAME");
            verify(em.contains(dep2), "entity manager does not contain department -> it cannot be merged");
            env.commitTransactionAndClear(em);
            verifyExistence(em, id, "NEW_NAME");
            // case 2: entity is contained in persistence context, but object to be merged has different object identity
            Department depEM;
            Department depClient;
            Department mergeResult;
            // case 2a: state of known object: FOR_INSERT
            id = 32;
            depEM = new Department(id, "ORIGINAL");
            depClient = new Department(id, "NEW_NAME");
            env.beginTransaction(em);
            em.persist(depEM); // this is now in state new
            checkDepartment(depEM, id, "ORIGINAL");
            verify(em.contains(depEM), "entity manager does not contain department -> it cannot be merged");
            mergeResult = em.merge(depClient);
            checkDepartment(mergeResult, id, "NEW_NAME");
            verify(em.contains(mergeResult), "entity manager does not contain merged department");
            verify(mergeResult == depEM, "entity manager changed identity of managed object");
            env.commitTransactionAndClear(em);
            verifyExistence(em, id, "NEW_NAME");
            // case 2b: state of known object: FOR_UPDATE
            id = 33;
            depEM = new Department(id, "ORIGINAL");
            env.beginTransaction(em);
            em.persist(depEM);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            depClient = em.find(Department.class, new Integer(id));
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            depClient.setName(("NEW_NAME"));
            depEM = em.find(Department.class, new Integer(id)); // this is now in state managed
            checkDepartment(depEM, id, "ORIGINAL");
            verify(em.contains(depEM), "entity manager does not contain department -> it cannot be merged");
            mergeResult = em.merge(depClient);
            checkDepartment(mergeResult, id, "NEW_NAME");
            verify(em.contains(mergeResult), "entity manager does not contain merged department");
            verify(mergeResult == depEM, "entity manager changed identity of managed object");
            env.commitTransactionAndClear(em);
            verifyExistence(em, id, "NEW_NAME");
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyExistence(final EntityManager em, int id, String name) {
        Department dep;
        dep = em.find(Department.class, new Integer(id));
        verify(dep != null, "department not found");
        verify(name.equals(dep.getName()), "department has wrong name: " + dep.getName());
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
                em.merge("Hutzliputz");
                flop("no IllegalArgumentException ");
            } catch (IllegalArgumentException e) {
                verify(true, "");
            } finally {
                env.rollbackTransactionAndClear(em);
            }
            env.beginTransaction(em);
            try {
                em.merge(null);
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
    @ToBeInvestigated
    public void testMergeNewWithRelation() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(201, "Aztec gods");
            Employee empNew = new Employee(202, "Huitzil", "Opochtli", dep);
            Review revManaged = new Review(203, Date.valueOf("2006-01-01"), "managed");
            Review revDetached = new Review(204, Date.valueOf("2006-01-02"), "detached/in PC");
            Review revDetachedNotInPC = new Review(205, Date.valueOf("2006-01-03"), "detached/not in PC");
            Review revNew = new Review(206, Date.valueOf("2006-01-04"), "new");
            Hobby hobbyManaged = new Hobby("managed");
            Hobby hobbyDetached = new Hobby("detached/in PC");
            Hobby hobbyDetachedNotInPC = new Hobby("detached/not in PC");
            Hobby hobbyNew = new Hobby("new");
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(revManaged);
            em.persist(revDetached);
            em.persist(revDetachedNotInPC);
            em.persist(hobbyManaged);
            em.persist(hobbyDetached);
            em.persist(hobbyDetachedNotInPC);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            revManaged = em.find(Review.class, new Integer(revManaged.getId()));
            Review revSamePKAsDetached = em.find(Review.class, new Integer(revDetached.getId()));
            revSamePKAsDetached.setReviewText("same PK as detached review");
            empNew.addReview(revManaged);
            empNew.addReview(revDetached);
            empNew.addReview(revDetachedNotInPC);
            empNew.addReview(revNew);
            hobbyManaged = em.find(Hobby.class, hobbyManaged.getId());
            Hobby hobbySamePKAsDetached = em.find(Hobby.class, hobbyDetached.getId());
            hobbySamePKAsDetached.setDescription("same PK as detached hobby");
            empNew.addHobby(hobbyManaged);
            empNew.addHobby(hobbyDetached);
            empNew.addHobby(hobbyDetachedNotInPC);
            empNew.addHobby(hobbyNew);
            Employee empAfterMerge = em.merge(empNew);
            Set<Review> reviews = empAfterMerge.getReviews();
            List<Hobby> hobbies = empAfterMerge.getHobbies();
            verify(em.contains(empAfterMerge), "Merged employee not managed");
            verify(empAfterMerge != empNew, "Merge did not copy new entity");
            verify(reviews.size() == 4, "Merged employee has " + reviews.size() + " reviews, expected 4");
            verify(hobbies.size() == 4, "Merged employee has " + hobbies.size() + " hobbies, expected 4");
            verify(containsIdentical(reviews, revManaged), "Merged employee does not contain revManaged");
            verify(containsIdentical(hobbies, hobbyManaged), "Merged employee does not contain hobbyManaged");
            verify(containsIdentical(reviews, revSamePKAsDetached), "Merged employee does not contain revSamePKAsDetached");
            verify(containsIdentical(hobbies, hobbySamePKAsDetached), "Merged employee does not contain hobbySamePKAsDetached");
            verify("same PK as detached review".equals(revSamePKAsDetached.getReviewText()),
                    "Text of revSamePKAsDetached changed");
            verify("same PK as detached hobby".equals(hobbySamePKAsDetached.getDescription()),
                    "Text of hobbySamePKAsDetached changed");
            verify(containsIdentical(reviews, revNew), "Merged employee does not contain revNew");
            verify(containsIdentical(hobbies, hobbyNew), "Merged employee does not contain hobbyNew");
            verify(!em.contains(revNew), "revNew is managed");
            verify(!em.contains(hobbyNew), "hobbyNew is managed");
            int pk = revDetachedNotInPC.getId();
            Review revSamePK = null;
            for (Review rev : reviews) {
                if (rev.getId() == pk) {
                    revSamePK = rev;
                    break;
                }
            }
            verify(revSamePK != null, "Merged employee does not contain review with id " + pk);
            verify(em.contains(revSamePK), "Review " + pk + " is not managed");
            String hobbyId = hobbyDetachedNotInPC.getId();
            Hobby hobbySamePK = null;
            for (Hobby hobby : hobbies) {
                if (hobby.getId().equals(hobbyId)) {
                    hobbySamePK = hobby;
                    break;
                }
            }
            verify(hobbySamePK != null, "Merged employee does not contain hobby with id " + pk);
            verify(em.contains(hobbySamePK), "Hobby " + pk + " is not managed");
            // commit should fail because empAfterMerge has a relation to a new review/hobby
            boolean commitFailed = false;
            try {
                env.commitTransactionAndClear(em);
            } catch (RuntimeException e) {
                if (!checkForIllegalStateException(e)) {
                    throw e;
                }
                commitFailed = true;
            }
            verify(commitFailed, "Commit succeeded although employee had a relation to a new review");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testWithLazyRelationPending() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // Case 1: Change observer pending in both detached and managed object
            Department dep = new Department(301, "Merge test department");
            Employee emp = new Employee(302, "Detached", "Employee", dep);
            Review rev1 = new Review(303, Date.valueOf("2006-01-01"), "one");
            Review rev2 = new Review(304, Date.valueOf("2006-01-02"), "two");
            Hobby hobby1 = new Hobby("counting beans");
            Hobby hobby2 = new Hobby("writing SDDs");
            emp.addReview(rev1);
            emp.addReview(rev2);
            emp.addHobby(hobby1);
            emp.addHobby(hobby2);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp);
            em.persist(rev1);
            em.persist(rev2);
            em.persist(hobby1);
            em.persist(hobby2);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            Employee empDetached = em.find(Employee.class, new Integer(emp.getId()));
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp = em.find(Employee.class, new Integer(emp.getId()));
            emp.setFirstName("Managed");
            emp = em.merge(empDetached);
            verify(empDetached.getFirstName().equals(emp.getFirstName()), "Merged employee has wrong first name");
            verify(emp.getReviews().size() == 2, "Merged employee has " + emp.getReviews().size() + " reviews, expected 2");
            verify(emp.getHobbies().size() == 2, "Merged employee has " + emp.getHobbies().size() + " hobbies, expected 2");
            env.commitTransactionAndClear(em);
            // Case 2: Change observer pending in detached object and loaded but unchanged in managed object
            env.beginTransaction(em);
            empDetached = em.find(Employee.class, new Integer(emp.getId()));
            emp.setFirstName("Detached");
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp = em.find(Employee.class, new Integer(emp.getId()));
            emp.setFirstName("Managed");
            Set<Review> reviews = emp.getReviews();
            verify(reviews.size() == 2, "Employee has " + reviews.size() + " reviews, expected 2");
            List<Hobby> hobbies = emp.getHobbies();
            verify(hobbies.size() == 2, "Employee has " + hobbies.size() + " hobbies, expected 2");
            emp = em.merge(empDetached);
            env.commitTransactionAndClear(em);
            verify(empDetached.getFirstName().equals(emp.getFirstName()), "Merged employee has wrong first name");
            reviews = emp.getReviews();
            verify(reviews.size() == 2, "Merged employee has " + reviews.size() + " reviews, expected 2");
            hobbies = emp.getHobbies();
            verify(hobbies.size() == 2, "Merged employee has " + hobbies.size() + " hobbies, expected 2");
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("boxing")
    @Test
    @ToBeInvestigated
    public void testIlegalArgumentWithLazyRelationPending() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: relationship to be merged has wrong owner
            Employee e1 = new Employee(401, "Detached", "Employee", null);
            Employee e2 = new Employee(402, "Detached", "Employee", null);
            Review review = new Review(403, Date.valueOf("2006-03-15"), "abc");
            e1.addReview(review);
            env.beginTransaction(em);
            em.persist(e1);
            em.persist(e2);
            em.persist(review);
            env.commitTransaction(em);
            em.clear();
            Employee detached1 = em.find(Employee.class, 401);
            Employee detached2 = em.find(Employee.class, 402);
            detached2.setReviews(detached1.getReviews());
            em.clear();
            env.beginTransaction(em);
            try {
                em.merge(detached2);
                flop("missing illegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(true);
            } finally {
                env.rollbackTransaction(em);
                em.clear();
            }
            // case 2: target relationship is pending but has wrong owner
            e1 = new Employee(411, "Detached", "Employee", null);
            e2 = new Employee(412, "Detached", "Employee", null);
            review = new Review(413, Date.valueOf("2006-03-15"), "abc");
            e1.addReview(review);
            env.beginTransaction(em);
            em.persist(e1);
            em.persist(e2);
            em.persist(review);
            env.commitTransaction(em);
            em.clear();
            detached1 = em.find(Employee.class, 411);
            detached2 = em.find(Employee.class, 412);
            em.clear();
            env.beginTransaction(em);
            try {
                e1 = em.find(Employee.class, 411);
                e2 = em.find(Employee.class, 412);
                e2.setReviews(e1.getReviews());
                em.merge(detached2);
                flop("missing illegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(true);
            } finally {
                env.rollbackTransaction(em);
                em.clear();
            }
            // case 3: target relationship is no change observer
            e1 = new Employee(421, "Detached", "Employee", null);
            env.beginTransaction(em);
            em.persist(e1);
            env.commitTransaction(em);
            em.clear();
            detached1 = em.find(Employee.class, 421);
            em.clear();
            env.beginTransaction(em);
            try {
                e1 = em.find(Employee.class, 421);
                e1.setReviews(new HashSet<Review>());
                em.merge(detached1);
                flop("missing illegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(true);
            } finally {
                env.rollbackTransaction(em);
                em.clear();
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("boxing")
    @Test
    @ToBeInvestigated
    public void testIlegalArgumentWithDeserializedEntity() throws IOException, ClassNotFoundException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: relationship to be merged has wrong owner
            Employee e1 = new Employee(501, "Detached", "Employee", null);
            Employee e2 = new Employee(502, "Detached", "Employee", null);
            Review review = new Review(503, Date.valueOf("2006-03-15"), "abc");
            e1.addReview(review);
            env.beginTransaction(em);
            em.persist(e1);
            em.persist(e2);
            em.persist(review);
            env.commitTransaction(em);
            em.clear();
            Employee detached1 = AbstractBaseTest.serializeDeserialize(em.find(Employee.class, 501));
            Employee detached2 = AbstractBaseTest.serializeDeserialize(em.find(Employee.class, 502));
            detached2.setReviews(detached1.getReviews());
            em.clear();
            env.beginTransaction(em);
            try {
                em.merge(detached2);
                flop("missing illegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(true);
            } finally {
                env.rollbackTransaction(em);
                em.clear();
            }
            // case 2: target relationship is pending but has wrong owner
            e1 = new Employee(511, "Detached", "Employee", null);
            e2 = new Employee(512, "Detached", "Employee", null);
            review = new Review(513, Date.valueOf("2006-03-15"), "abc");
            e1.addReview(review);
            env.beginTransaction(em);
            em.persist(e1);
            em.persist(e2);
            em.persist(review);
            env.commitTransaction(em);
            em.clear();
            detached1 = AbstractBaseTest.serializeDeserialize(em.find(Employee.class, 511));
            detached2 = AbstractBaseTest.serializeDeserialize(em.find(Employee.class, 512));
            em.clear();
            env.beginTransaction(em);
            try {
                e1 = em.find(Employee.class, 511);
                e2 = em.find(Employee.class, 512);
                e2.setReviews(e1.getReviews());
                em.merge(detached2);
                flop("missing illegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(true);
            } finally {
                env.rollbackTransaction(em);
                em.clear();
            }
            // case 3: target relationship is no change observer
            e1 = new Employee(521, "Detached", "Employee", null);
            env.beginTransaction(em);
            em.persist(e1);
            env.commitTransaction(em);
            em.clear();
            detached1 = AbstractBaseTest.serializeDeserialize(em.find(Employee.class, 521));
            em.clear();
            env.beginTransaction(em);
            try {
                e1 = em.find(Employee.class, 521);
                e1.setReviews(new HashSet<Review>());
                em.merge(detached1);
                flop("missing illegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(true);
            } finally {
                env.rollbackTransaction(em);
                em.clear();
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Checks whether the collection contains an entry identical to the given object. The method uses object identity for
     * comparison (not the object's <code>equals</code> method
     * 
     * @param collection
     *            the collection
     * @param o
     *            the object to search for in the set
     * @return <code>true</code> if the set contains o (and o is not <code>null</code>), <code>false</code> otherwise
     */
    private boolean containsIdentical(Collection<?> collection, Object o) {
        boolean contains = false;
        if (o != null) {
            for (Object entry : collection) {
                if (entry == o) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    @Ignore // @TestProperties(unsupportedEnvironments={JTANonSharedPCEnvironment.class, ResourceLocalEnvironment.class})
    public void testNoTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep = new Department(600, "dep600");
        try {
            em.merge(dep);
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
            Department dep = new Department(601, "dep601");
            env.beginTransaction(em);
            verify(!em.contains(dep), "entity contained in persistence context"); // this ensures that the pc is
            // available
            env.markTransactionForRollback(em);
            dep = em.merge(dep);
            verify(em.contains(dep), "entity not contained in persistence context");
            env.rollbackTransaction(em);
            // case 2: persistence context not yet available when tx marked for rollback
            // will throw an exception in the container-managed JTA case
            // if (!(env instanceof JTASharedPCEnvironment)) {
            {
                dep = new Department(602, "dep602");
                env.beginTransaction(em);
                env.markTransactionForRollback(em);
                dep = em.merge(dep);
                verify(em.contains(dep), "entity not contained in persistence context");
                env.rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyMergeNewEntityWithIdSetInPrePersist(Timestamp t1) {
        /*
         * If X is a new entity instance, a new managed entity instance X' is created and the state of X is copied into the new
         * managed entity instance X'.
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Timestamp t2 = em.merge(t1);
            verify(t2 != null, "merged timestamp is null");
            verify(t2.getId() != null && t2.getId() != Long.valueOf(0), "unexpected id: " + t2.getId());
            verify(em.contains(t2), "not contained");
            env.commitTransactionAndClear(em);
            verify(!em.contains(t1), "entity manager contains timestamp -> it cannot be detached");
            verify(!em.contains(t2), "entity manager contains timestamp -> it cannot be detached");
            Timestamp t3 = em.find(Timestamp.class, t2.getId());
            verify(t3 != null, "not found");
            verify(t3.getId().equals(t2.getId()), "unexpected id: " + t3.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeNewEntityWithIdSetInPrePersist() {
        // initial object
        verifyMergeNewEntityWithIdSetInPrePersist(new Timestamp());
        try {
            Thread.sleep(10); // make sure next entity gets new ID
        } catch (InterruptedException e) {
            // $JL-EXC$
        }
        Timestamp t = new Timestamp();
        t.setId(Long.valueOf(1));
        // 
        verifyMergeNewEntityWithIdSetInPrePersist(t);
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
                em.merge(new Nasty());
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
    public void testNastyTimestampTwiceNotInitial() {
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
                Nasty n2 = new Nasty();
                n2.setId(Long.valueOf(2000));
                em.merge(n2);
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
}
