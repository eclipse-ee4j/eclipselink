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

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestRelationships extends JPA1Base {

    private final Set<Department> ALL_DEPARTMENTS = new HashSet<Department>();
    private final Department dep10 = new Department(10, "ten");
    private final Department dep20 = new Department(20, "twenty");

    private void init() throws SQLException {
        clearAllTables();
        ALL_DEPARTMENTS.add(dep10);
        ALL_DEPARTMENTS.add(dep20);
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(dep10);
            em.persist(dep20);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRelationFieldInWhere() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = new Employee(15, "first", "last", dep10);
            em.persist(emp);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e from Employee e where e.department.name = 'ten'");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            Iterator iter = result.iterator();
            while (iter.hasNext()) {
                Employee employee = (Employee) iter.next();
                verify(employee.getId() == 15, "wrong employee");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRelationFieldInSelectList() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(15, "Hans", "M\u00fcller", dep10));
            em.persist(new Employee(16, "Fred", "Maier", dep10));
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e.firstname from Employee e where e.lastname = 'Maier'");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            for (Object object : result) {
                verify("Fred".equals(object), "wrong employee");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testEmpty() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(15, "Hans", "M\u00fcller", dep10));
            Employee fred = new Employee(16, "Fred", "Maier", dep10);
            Review review = new Review(1, Date.valueOf("2006-01-02"), "b");
            em.persist(review);
            fred.addReview(review);
            em.persist(fred);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e.firstname from Employee e where e.reviews is not empty");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            for (Object object : result) {
                verify("Fred".equals(object), "wrong department name");
            }
            query = em.createQuery("select e.firstname from Employee e where e.reviews is empty");
            result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            for (Object object : result) {
                verify("Hans".equals(object), "wrong department name");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testInJoin() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(15, "Hans", "M\u00fcller", dep10));
            Employee fred = new Employee(16, "Fred", "Maier", dep10);
            Review fredsReview = new Review(1, Date.valueOf("2006-01-02"), "first");
            em.persist(fredsReview);
            fred.addReview(fredsReview);
            em.persist(fred);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e.firstname from Employee e, in (e.reviews) r where r.id = 1");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            for (Object object : result) {
                verify("Fred".equals(object), "wrong department name");
            }
            query = em.createQuery("select e.firstname from Employee e, in (e.reviews) r where r.id = 2");
            result = query.getResultList();
            verify(result.size() == 0, "wrong resultcount");
            query = em.createQuery("select e.firstname from Employee e, in (e.reviews) r");
            result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testInnerJoin() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(15, "Hans", "M\u00fcller", dep10));
            Employee fred = new Employee(16, "Fred", "Maier", dep10);
            Review fredsReview = new Review(1, Date.valueOf("2006-01-02"), "first");
            em.persist(fredsReview);
            fred.addReview(fredsReview);
            em.persist(fred);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e.firstname from Employee e inner join e.reviews r where r.id = 1");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            for (Object object : result) {
                verify("Fred".equals(object), "wrong department name");
            }
            query = em.createQuery("select e.firstname from Employee e inner join e.reviews r where r.id = 2");
            result = query.getResultList();
            verify(result.size() == 0, "wrong resultcount");
            query = em.createQuery("select e.firstname from Employee e inner join e.reviews r");
            result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.SAPDB, DatabaseVendor.OPEN_SQL })
    public void testLeftJoin() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(15, "Hans", "M\u00fcller", dep10));
            Employee fred = new Employee(16, "Fred", "Maier", dep10);
            Review fredsReview = new Review(1, Date.valueOf("2006-01-02"), "first");
            em.persist(fredsReview);
            fred.addReview(fredsReview);
            em.persist(fred);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e from Employee e left join e.reviews r");
            List result = query.getResultList();
            verify(result.size() == 2, "wrong resultcount");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testInList() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(15, "Hans", "M\u00fcller", dep10));
            Employee fred = new Employee(16, "Fred", "Maier", dep10);
            Review fredsReview = new Review(1, Date.valueOf("2006-01-02"), "first");
            em.persist(fredsReview);
            fred.addReview(fredsReview);
            em.persist(fred);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select e.firstname from Employee e where e.lastname in ('Maier')");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            for (Object object : result) {
                verify("Fred".equals(object), "wrong department name");
            }
            query = em.createQuery("select e.firstname from Employee e where e.lastname in ('M\u00fcller', 'V\u00f6ller')");
            result = query.getResultList();
            Assert.assertEquals("wrong resultcount", 1, result.size(), 0);
        } finally {
            closeEntityManager(em);
        }
    }

    private static void persistAll(EntityManager em, Object... objects) {
        for (Object object : objects) {
            em.persist(object);
        }
    }

    @Test
    @ToBeInvestigated
    public void testEdmScenario() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hugo = new Employee(1, "Hugo", null, null);
            Employee emil = new Employee(2, "Emil", null, null);
            Employee paul = new Employee(3, "Paul", null, null);
            Employee knut = new Employee(4, "Knut", null, null);
            Project a = new Project("A");
            Project b = new Project("B");
            Project c = new Project("C");
            Project d = new Project("D");
            Bicycle bike1 = new Bicycle();
            Bicycle bike2 = new Bicycle();
            Bicycle bike3 = new Bicycle();
            Bicycle bike4 = new Bicycle();
            persistAll(em, hugo, emil, paul, knut, a, b, c, d, bike1, bike2, bike3, bike4);
            a.addEmployee(hugo);
            b.addEmployee(hugo);
            b.addEmployee(emil);
            c.addEmployee(emil);
            emil.addBicycle(bike1);
            paul.addBicycle(bike1);
            paul.addBicycle(bike2);
            knut.addBicycle(bike2);
            hugo.addBicycle(bike3);
            env.commitTransactionAndClear(em);
            // with subquery
            Query query = em.createQuery("select distinct b from Bicycle b join b.riders r "
                    + "where r.id in (select e.id from Project p join p.employees e where p.id = :id)");
            query.setParameter("id", a.getId()); // only hugo
            List result = query.getResultList();
            verify(result.size() == 1, "wrong size: " + result.size());
            Bicycle bike = (Bicycle) result.get(0);
            verify(bike.getId().equals(bike3.getId()), "bike has wrong id: " + bike.getId() + " expected bike with id "
                    + bike3.getId());
            query.setParameter("id", b.getId()); // hugo and emil
            result = query.getResultList();
            verify(result.size() == 2, "wrong size: " + result.size());
            for (Object possiblyBicycle : result) {
                final Bicycle bicycle = (Bicycle) possiblyBicycle;
                verify(bicycle.getId().equals(bike1.getId()) || bicycle.getId().equals(bike3.getId()), "unexpected id: "
                        + bicycle.getId() + "; exptected bike with id " + bike1.getId() + " or " + bike3.getId());
            }
            // with member of
            query = em.createQuery("select distinct b from Bicycle b join b.riders r, Project p "
                    + "where r member of p.employees and p.id = :id");
            query.setParameter("id", a.getId()); // only hugo
            result = query.getResultList();
            verify(result.size() == 1, "wrong size: " + result.size());
            bike = (Bicycle) result.get(0);
            verify(bike.getId().equals(bike3.getId()), "bike has wrong id: " + bike.getId() + "; expected bike with id "
                    + bike3.getId());
            query.setParameter("id", b.getId()); // hugo and emil
            result = query.getResultList();
            verify(result.size() == 2, "wrong size: " + result.size());
            for (Object possiblyBicycle : result) {
                final Bicycle bicycle = (Bicycle) possiblyBicycle;
                verify(bicycle.getId().equals(bike1.getId()) || bicycle.getId().equals(bike3.getId()), "unexpecetd id: "
                        + bicycle.getId() + "; expected bike with id " + bike3.getId() + " or " + bike1.getId());
            }
        } finally {
            closeEntityManager(em);
        }
    }
}
