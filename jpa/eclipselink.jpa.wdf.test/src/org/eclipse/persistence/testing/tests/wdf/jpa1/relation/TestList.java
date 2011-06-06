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

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.Issue;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Course;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Hobby;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Material;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestList extends JPA1Base {
    private static final Integer EMP_ID_DORIS = Integer.valueOf(43);
    private static final Integer EMP_ID_SABINE = Integer.valueOf(44);

    @Override
    protected void setup()  {
        
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final Department dep = new Department(1, "Public Relations");
            em.persist(dep);
            final Employee emp1 = new Employee(EMP_ID_DORIS.intValue(), "Doris", "Schr\u00f6der-K\u00f6pf", dep);
            em.persist(emp1);
            final Employee emp2 = new Employee(EMP_ID_SABINE.intValue(), "Sabine", "Leutheusser-Schnarrenberger", dep);
            em.persist(emp2);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimpleCourse() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final Course course = createAndPersistCourse(em);
            final Long courseId = Long.valueOf(course.getCourseId());
            env.commitTransactionAndClear(em);
            final Course storedCourse = em.find(Course.class, courseId);
            verify(storedCourse != null, "didn't find course again");
            verify(storedCourse.getAttendees() != null, "course lost attendees");
            verify(storedCourse.getAttendees().size() == 2, "number of attendees in course (expected: 2, got: "
                    + storedCourse.getAttendees().size() + ").");
            for (final Employee attendee : storedCourse.getAttendees()) {
                verify(attendee.getId() == EMP_ID_DORIS.intValue() || attendee.getId() == EMP_ID_SABINE.intValue(),
                        "Wrong attendee: " + attendee);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    private Course createAndPersistCourse(final EntityManager em) {
        final Course course = new Course();
        course.addAttendee(em.find(Employee.class, EMP_ID_DORIS));
        course.addAttendee(em.find(Employee.class, EMP_ID_SABINE));
        em.persist(course);
        // bug 312244
        Material material = new Material();
        course.setMaterial(material);
        material.setCourse(course);
        em.persist(material);
        // bug 312244
        return course;
    }

    @Test
    public void testDelete() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final Course course = createAndPersistCourse(em);
            final Long courseId = Long.valueOf(course.getCourseId());
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            em.remove(em.merge(course));
            env.commitTransactionAndClear(em);
            final Course storedCourse = em.find(Course.class, courseId);
            verify(storedCourse == null, "did find course again");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeChangedRelation() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final Course course = createAndPersistCourse(em);
            final Long courseId = Long.valueOf(course.getCourseId());
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            // the remove of Doris takes place on the detached entity: but rather than relying on
            // cascade, the attendees are retrieved from em again.
            course.clearAttendees();
            course.addAttendee(em.find(Employee.class, EMP_ID_SABINE));
            final List<Employee> attendeesBeforeMerge = course.getAttendees();
            verify(attendeesBeforeMerge.size() == 1, "wrong number of attendees before merge(): " + attendeesBeforeMerge.size());
            final List<Employee> attendeesAfterMerge = em.merge(course).getAttendees();
            verify(attendeesAfterMerge.size() == 1, "wrong number of attendees after merge(): " + attendeesAfterMerge.size());
            env.commitTransactionAndClear(em);
            final Course storedCourse = em.find(Course.class, courseId);
            verify(storedCourse != null, "didnt find course again");
            verify(storedCourse.getAttendees() != null, "course lost attendees");
            verify(storedCourse.getAttendees().size() == 1, "number of attendees in course (expected: 1, got: "
                    + storedCourse.getAttendees().size() + ").");
            for (final Employee attendee : storedCourse.getAttendees()) {
                verify("Sabine".equals(attendee.getFirstName()), "Wrong attendee: " + attendee);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUpdate() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final Course course = createAndPersistCourse(em);
            final Long courseId = Long.valueOf(course.getCourseId());
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            // the remove of Doris takes place on the managed entity
            em.find(Course.class, courseId).removeAttendee(em.find(Employee.class, EMP_ID_DORIS));
            env.commitTransactionAndClear(em);
            final Course storedCourse = em.find(Course.class, courseId);
            verify(storedCourse != null, "didnt find course again");
            verify(storedCourse.getAttendees() != null, "course lost attendees");
            verify(storedCourse.getAttendees().size() == 1, "number of attendees in course (expected: 1, got: "
                    + storedCourse.getAttendees().size() + ").");
            for (final Employee attendee : storedCourse.getAttendees()) {
                verify("Sabine".equals(attendee.getFirstName()), "Wrong attendee: " + attendee);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testOrderBy() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final SortedMap<String, Employee> attendeeMap = new TreeMap<String, Employee>();
        try {
            env.beginTransaction(em);
            final Course course = createAndPersistCourse(em);
            final Long courseId = Long.valueOf(course.getCourseId());
            final Employee employee1 = em.find(Employee.class, EMP_ID_DORIS);
            attendeeMap.put(employee1.getLastName(), employee1);
            final Employee employee2 = em.find(Employee.class, EMP_ID_SABINE);
            attendeeMap.put(employee2.getLastName(), employee2);
            env.commitTransactionAndClear(em);
            
            env.beginTransaction(em);
            final Course storedCourse = em.find(Course.class, courseId);
            verify(storedCourse != null, "didnt find course again");
            final List<Employee> attendees = storedCourse.getAttendees();
            verify(attendees != null, "course lost attendees");
            final Iterator<Employee> orderedEmployees = attendees.iterator();
            for (final String lastName : attendeeMap.keySet()) {
                Employee nextEmployee = orderedEmployees.next();
                verify(nextEmployee.getLastName().equals(lastName), "wrong order of employees, expected name: " + lastName
                        + ", but got name: " + nextEmployee.getLastName());
            }
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRelationContainsEntities() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final Course course = createAndPersistCourse(em);
            final Long courseId = Long.valueOf(course.getCourseId());
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Course storedCourse = em.find(Course.class, courseId);
            if (null == storedCourse || null == storedCourse.getAttendees()) {
                flop("didnt find course/attendees again");
            }
            for (final Employee attendee : storedCourse.getAttendees()) {
                verify(em.contains(attendee), "Attending Emloyee not known to entity manager: " + attendee);
            }
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Issue(issueid=12)
    public void testCompoundOrderBy() {
        final String[] increasingCategories = new String[] { null, " ", "Nonsense", "Sport", "Work", };
        final String[] decreasingDescriptions = new String[] { "Grumbling and Cursing", "Bla-muh", "A description", " ", null, };
        int id = increasingCategories.length * decreasingDescriptions.length;
        final List<Hobby> sortedHobbies = new ArrayList<Hobby>();
        for (final String category : increasingCategories) {
            for (final String description : decreasingDescriptions) {
                id--;
                final Hobby hobby = new Hobby(description);
                hobby.setCategory(category);
                sortedHobbies.add(hobby);
            }
        }
        final List<Hobby> dorisSortedHobbies = new ArrayList<Hobby>(sortedHobbies);
        dorisSortedHobbies.remove(24);
        dorisSortedHobbies.remove(23);
        dorisSortedHobbies.remove(19);
        dorisSortedHobbies.remove(13);
        dorisSortedHobbies.remove(9);
        dorisSortedHobbies.remove(4);
        dorisSortedHobbies.remove(3);
        dorisSortedHobbies.remove(2);
        final List<Hobby> dorisShuffledHobbies = new ArrayList<Hobby>(dorisSortedHobbies);
        Collections.shuffle(dorisShuffledHobbies);
        final List<Hobby> sabineSortedHobbies = new ArrayList<Hobby>(sortedHobbies);
        sabineSortedHobbies.removeAll(dorisShuffledHobbies);
        final List<Hobby> sabineShuffledHobbies = new ArrayList<Hobby>(sabineSortedHobbies);
        Collections.shuffle(sabineShuffledHobbies);
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            for (final Hobby hobby : sortedHobbies) {
                em.persist(hobby);
            }
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee doris = em.find(Employee.class, EMP_ID_DORIS);
            doris.setHobbies(dorisShuffledHobbies);
            final Employee sabine = em.find(Employee.class, EMP_ID_SABINE);
            sabine.setHobbies(sabineShuffledHobbies);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee persistentDoris = em.find(Employee.class, EMP_ID_DORIS);
            final List<Hobby> persistentDorisHobbies = persistentDoris.getHobbies();
            verify(persistentDorisHobbies.equals(dorisSortedHobbies), "Doris' hobbies are not sorted correctly, got >>"
                    + persistentDorisHobbies + "<<, expected >>" + dorisSortedHobbies + "<<.");
            final Employee persistentSabine = em.find(Employee.class, EMP_ID_SABINE);
            final List<Hobby> persistentSabineHobbies = persistentSabine.getHobbies();
            env.commitTransaction(em);
            // no clear here, wana access persistentSabineHobbies outside the transaction.
            final boolean expectException = false; // env instanceof JTASharedPCEnvironment;
            try {
                verify(persistentSabineHobbies.equals(sabineSortedHobbies), "Sabine's hobbies are not sorted correctly, got >>"
                        + persistentSabineHobbies + "<<, expected >>" + sabineSortedHobbies + "<<.");
                if (expectException) {
                    flop("No exception when accessing a lazy list outside.");
                }
            } catch (PersistenceException ex) {
                if (expectException) {
                    Assert.assertTrue(true);
                } else {
                    throw ex;
                }
            }
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }
}
