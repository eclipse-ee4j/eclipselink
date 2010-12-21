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
import java.util.Set;

import javax.naming.NamingException;
import javax.persistence.Cache;
import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.models.wdf.jpa1.island.Island;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestCache extends JPA1Base {

    @Test
    public void testClone() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep1 = new Department(1, "aaa");
            env.beginTransaction(em);
            em.persist(dep1);
            env.commitTransactionAndClear(em); // clear context

            env.beginTransaction(em);
            Department dep2 = em.find(Department.class, 1);
            // Department number 1 now in Cache
            Assert.assertFalse("Entry no clone", dep1 == dep2);
            Department dep3 = em.find(Department.class, 1);
            Assert.assertTrue("duplicate Entity", dep2 == dep3);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testContains() throws NamingException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department wk = new Department(2, "Wasserkopp");
            env.beginTransaction(em);
            em.persist(wk);
            env.commitTransactionAndClear(em);

            wk = em.find(Department.class, wk.getId()); // read into cache

            Cache cache = em.getEntityManagerFactory().getCache();
            Assert.assertTrue(cache.contains(Department.class, wk.getId()));
            cache.evict(Department.class, wk.getId());
            Assert.assertFalse(cache.contains(Department.class, wk.getId()));
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testRelation() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Cache cache = env.getEntityManagerFactory().getCache();
        try {
            Department dep = new Department(3, "whatsoever");
            Review review = new Review(33, Date.valueOf("2009-05-08"), "blah");
            Set<Review> reviewSet = new HashSet<Review>();
            reviewSet.add(review);
            Employee emp = new Employee(17, "first", "last", dep);
            emp.setReviews(reviewSet);

            env.beginTransaction(em);
            em.persist(review);
            em.persist(dep);
            em.persist(emp);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());

            Assert.assertTrue(cache.contains(Department.class, dep.getId())); // eager
            // loading
            Assert.assertTrue(cache.contains(Employee.class, emp.getId()));
            Assert.assertFalse(cache.contains(Review.class, review.getId())); // not
            // cacheable
            env.commitTransactionAndClear(em);

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testInvalidateUpdate() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Cache cache = env.getEntityManagerFactory().getCache();
        try {
            env.beginTransaction(em);
            Department dep = new Department(4, "motleyCrew");
            em.persist(dep);
            env.commitTransactionAndClear(em); // write to DB

            env.beginTransaction(em);
            dep = em.find(Department.class, dep.getId()); // read into cache
            Assert.assertTrue(cache.contains(Department.class, dep.getId()));

            dep.setName("someUglyChanges");
            env.commitTransactionAndClear(em); // invalidate in cache
            Assert.assertFalse(cache.contains(Department.class, dep.getId()));

            env.beginTransaction(em);
            dep = em.find(Department.class, dep.getId()); // read again into
            // cache
            Assert.assertTrue(cache.contains(Department.class, dep.getId()));
            dep.setName("someMoreUglyChanges");
            em.flush(); // not invalidate cache
            Assert.assertTrue(cache.contains(Department.class, dep.getId()));
            em.clear(); // prepare invalidate for commit
            dep = em.find(Department.class, dep.getId()); // read own changes
            // from DB
            Assert.assertTrue(dep.getName().equals("someMoreUglyChanges"));
            // no change since flush but clear() stores changes for commit
            // invalidation
            env.commitTransactionAndClear(em);
            Assert.assertFalse(cache.contains(Department.class, dep.getId()));

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testInvalidateRemove() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Cache cache = env.getEntityManagerFactory().getCache();
        try {
            env.beginTransaction(em);
            Department dep = new Department(5, "SpaceCowboys");
            em.persist(dep);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            dep = em.find(Department.class, dep.getId());
            Assert.assertTrue(cache.contains(Department.class, dep.getId()));
            em.remove(dep);
            env.commitTransactionAndClear(em);

            Assert.assertFalse(cache.contains(Department.class, dep.getId()));

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testNotOwningSide() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Cache cache = env.getEntityManagerFactory().getCache();
        // final boolean isLazyToOneEnabled =
        // isLazyToOneRelationshipEnabled(em);
        try {
            env.beginTransaction(em);
            Department dep = new Department(40, "Dep");
            Employee emp = new Employee(35, "aaa", "bbb", dep);
            Cubicle cub = new Cubicle(8, 9, "blue", emp);
            emp.setCubicle(cub); // maintain second side
            em.persist(dep);
            em.persist(emp);
            em.persist(cub);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            emp = em.find(Employee.class, emp.getId()); // read into cache
            Assert.assertTrue(cache.contains(Department.class, dep.getId()));
            Assert.assertTrue(cache.contains(Employee.class, emp.getId()));
            // if (isLazyToOneEnabled) {
            // assertTrue(!cache.contains(Cubicle.class, cub.getId()));
            // Cubicle lazyCubicle = em.find(Cubicle.class, cub.getId());
            // assertTrue(lazyCubicle instanceof LazilyLoadable);
            // assertTrue(((LazilyLoadable) lazyCubicle)._isPending());
            // } else {
            // Assert.assertTrue(cache.contains(Cubicle.class, cub.getId()));
            // }

            emp.getCubicle().setEmployee(null); // notOwningSide changes here
            env.commitTransactionAndClear(em);

            Assert.assertTrue(cache.contains(Department.class, dep.getId()));
            Assert.assertTrue(cache.contains(Employee.class, emp.getId()));
            Assert.assertFalse(cache.contains(Cubicle.class, cub.getId()));

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testNotOwningSide2() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(41, "Dep");
            Employee emp = new Employee(36, "aaa", "bbb", dep);
            Cubicle cub = new Cubicle(9, 9, "blue", emp);
            emp.setCubicle(cub); // maintain second side
            em.persist(dep);
            em.persist(emp);
            em.persist(cub);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            cub = em.find(Cubicle.class, cub.getId()); // read into cache
            env.commitTransactionAndClear(em); // clear PC

            Assert.assertTrue("Employee is loitering in PC", !em.contains(emp));

            env.beginTransaction(em);
            cub = em.find(Cubicle.class, cub.getId()); // read from cache
            Assert.assertTrue("emp not reloaded", em.contains(emp));
            env.commitTransactionAndClear(em);

            /*
             * we expect three queries: Employee.bicycles, Employee.checkingAccount, employee.motorVehicles
             */

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testTimeToLive() throws InterruptedException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // initialize data
            env.beginTransaction(em);
            Island island = new Island("Amrum");
            em.persist(island);
            env.commitTransactionAndClear(em);
            int id = island.getId();

            // fill cache
            env.beginTransaction(em);
            island = em.find(Island.class, id);
            env.commitTransactionAndClear(em);

            // change on DB
            env.beginTransaction(em);
            em.createQuery("update Island i set i.name='Langeoog'").executeUpdate();
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);

            // change not reflected in cache
            island = em.find(Island.class, id);
            Assert.assertEquals("Amrum", island.getName());

            em.clear();

            // wait ten seconds (time to live of island)
            Thread.sleep(2500);

            // change must be reflected now
            island = em.find(Island.class, id);
            Assert.assertEquals("Langeoog", island.getName());

            env.rollbackTransactionAndClear(em);

        } finally {
            closeEntityManager(em);
        }
    }
}
