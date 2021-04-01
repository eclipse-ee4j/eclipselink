/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     04/19/2020-3.0 Alexandre Jacob
//        - 544202: Fixed refresh of foreign key values when cacheKey was invalidated

package org.eclipse.persistence.jpa.test.sharedcache;

import static org.junit.Assert.assertEquals;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.sharedcache.model.Student;
import org.eclipse.persistence.jpa.test.sharedcache.model.Teacher;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.tools.profiler.PerformanceMonitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

@RunWith(EmfRunner.class)
public class TestSharedCacheWithNonCacheable {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Teacher.class, Student.class },
        properties = {
            @Property(name = "eclipselink.logging.level", value = "FINE"),
            @Property(name = "eclipselink.logging.parameters", value = "true"),
            @Property(name = "eclipselink.cache.shared.default", value = "false"),
            @Property(name = "eclipselink.profiler", value = "PerformanceMonitor")
        })
    private EntityManagerFactory emf;

    private PerformanceMonitor performanceMonitor;

    @Before
    public void setUp() {
        Session session = ((JpaEntityManager) emf.createEntityManager()).getServerSession();
        performanceMonitor = (PerformanceMonitor) session.getProfiler();

        // We populate DB with 2 students and 1 teacher
        Student student1 = new Student(1, "Picasso");
        Student student2 = new Student(2, "Pablo");

        Teacher teacher = new Teacher(1, "Foo", student1);

        final EntityManager em = this.emf.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        em.persist(student1);
        em.persist(student2);
        em.persist(teacher);
        transaction.commit();

        em.close();
    }

    @Test
    public void testSharedCacheWithNonCacheable() throws Exception {
        // 1 : We want to get our Teacher 1. He is supposed to be in the shared cache.
        // The Student linked to our Teacher should not be in the cache. (@Noncacheable)
        {
            Teacher teacher = this.findTeacher(1);

            assertEquals(teacher.getId(), 1);
            assertEquals(teacher.getName(), "Foo");

            // Our Teacher IS in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 1L); // + 1

            Student student = teacher.getStudent();

            assertEquals(student.getId(), 1); // trigger lazy loading of our Student

            // Our Student is NOT in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 1L); // no change
        }

        // 2 : We change our Teacher 1 in native SQL. The name and the linked student are changed.
        {
            EntityManager em = emf.createEntityManager();

            EntityTransaction transaction = em.getTransaction();

            transaction.begin();
            em.createNativeQuery("update TEACHER set NAME = ?, STUDENT_ID = ? where ID = ?")
                .setParameter(1, "Bar")
                .setParameter(2, 2)
                .setParameter(3, 1)
                .executeUpdate();
            transaction.commit();

            em.close();
        }

        // 3 : We want to get our Teacher 1 ONE MORE TIME. He is still supposed to be in the shared cache.
        // The Student linked to our Teacher should not be in the cache. (@Noncacheable)
        {
            Teacher teacher = this.findTeacher(1);

            assertEquals(teacher.getId(), 1);
            assertEquals(teacher.getName(), "Foo");

            // Our Teacher IS in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 2L); // + 1

            Student student = teacher.getStudent();

            assertEquals(student.getId(), 1); // trigger lazy loading of our Student

            // Our Student is NOT in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 2L); // no change
        }

        // 4 : Now we clear shared cache.
        {
            emf.getCache().evict(Teacher.class);
        }

        // 5 : We want to get our Teacher 1 for the THIRD TIME. He is not in the shared cache anymore (invalidated)
        // Data should reflect our update from (2)
        {
            Teacher teacher = this.findTeacher(1);

            assertEquals(teacher.getId(), 1);
            assertEquals(teacher.getName(), "Bar"); // Updated

            // Our Teacher is NOT in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 2L); // no change

            Student student = teacher.getStudent();

            assertEquals(student.getId(), 2); // trigger lazy loading of our Student

            // Our Student is NOT in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 2L); // no change
        }

        // 6 : We want to get our Teacher 1 for the FOURTH TIME. He is back in the shared.
        // Data should reflect our update from (2)
        {
            Teacher teacher = this.findTeacher(1);

            assertEquals(teacher.getId(), 1);
            assertEquals(teacher.getName(), "Bar"); // Updated

            // Our Teacher IS in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 3L); // + 1

            Student student = teacher.getStudent();

            // Before correction of bug 544202 this value was 1 because CacheKey.protectedForeignKeys was never updated
            assertEquals(student.getId(), 2); // trigger lazy loading of our Student

            // Our Student is NOT in shared cache
            assertEquals(performanceMonitor.getOperationTime(SessionProfiler.CacheHits), 3L); // no change
        }
    }

    private Teacher findTeacher(int id) {
        final EntityManager em = this.emf.createEntityManager();
        Teacher teacher = em.find(Teacher.class, id);
        teacher.getStudent().getId();
        em.close();

        return teacher;
    }
}
