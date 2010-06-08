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

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Car;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;

public class TestContains extends JPA1Base {

    @Test
    public void testContainsNew() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Department dep = new Department(1, "NEW");
            verify(!em.contains(dep), "contains returns true for new object");
            Car car = new Car();
            car.setId(null);
            verify(!em.contains(car), "contains returned true for new car");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testContainsManaged() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final boolean extended = env.usesExtendedPC();
        try {
            env.beginTransaction(em);
            Department dep = new Department(11, "MANAGED");
            em.persist(dep);
            env.commitTransactionAndClear(em);
            // 1. managed after find ouside tx
            Object obj = em.find(Department.class, new Integer(11));
            if (extended) {
                verify(em.contains(obj), "object retrieved by find outside of transaction is not contained");
            } else {
                verify(!em.contains(obj), "object retrieved by find outside of transaction is contained");
            }
            em.clear();
            // 2. managed after find inside tx
            env.beginTransaction(em);
            obj = em.find(Department.class, new Integer(11));
            verify(em.contains(obj), "object retrieved by find inside transaction is not contained");
            env.rollbackTransactionAndClear(em);
            verify(!em.contains(obj), "object contained after rollback and clear");
            em.clear();
            // 3. managed after persist
            dep = new Department(12, "MANAGED_NEW");
            env.beginTransaction(em);
            em.persist(dep);
            verify(em.contains(dep), "object is not contained after persist");
            env.rollbackTransactionAndClear(em);
            verify(!em.contains(dep), "object contained after rollback");
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * The contains method returns false if the remove method has been called on the entity, or the remove operation has been
     * cascaded to it.
     */
    @Test
    public void testContainsRemoved() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(21, "REMOVED");
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verify(!em.contains(dep), "object contained after commit");
            // 1. remove found object
            env.beginTransaction(em);
            Object obj = em.find(Department.class, new Integer(21));
            verify(em.contains(obj), "object not contained after find inside tx");
            em.remove(obj);
            verify(!em.contains(obj), "object contained after remove");
            env.rollbackTransactionAndClear(em);
            verify(!em.contains(obj), "object contained after rollback");
            // 2. remove managed-new object
            // what happens is undefined by the spec. Consequently, we don't check anything.
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testContainsDetached() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        Department dep;
        Department detachedDep;
        int id;
        try {
            id = 31;
            dep = new Department(id, "DETACHED");
            // 1. after rollback
            env.beginTransaction(em);
            em.persist(dep);
            env.rollbackTransactionAndClear(em);
            verify(!em.contains(dep), "detached object contained after rollback");
            // 2. after commit
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            verify(!em.contains(dep), "detached object contained after commit");
            // 3. due to different managed object with same primary key
            id = 32;
            dep = new Department(id, "ORIGINAL");
            detachedDep = new Department(id, "DETACHED");
            env.beginTransaction(em);
            em.persist(dep); // object is now in state MANAGED_NEW
            verify(!em.contains(detachedDep), "detached object contained, another object with same pk is in state MANAGED_NEW");
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(id)); // object is now in state MANAGED
            verify(!em.contains(detachedDep), "detached object contained, another object with same pk is in state MANAGED");
            em.remove(dep); // object is now in state DELETED
            verify(!em.contains(detachedDep), "detached object contained, another object with same pk is in state DELETED");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNotAnEntity() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            try {
                em.contains("Hutzliputz");
                flop("no IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(true);
            }
            try {
                em.contains(null);
                flop("no IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(true);
            }
        } finally {
            closeEntityManager(em);
        }
    }
}
