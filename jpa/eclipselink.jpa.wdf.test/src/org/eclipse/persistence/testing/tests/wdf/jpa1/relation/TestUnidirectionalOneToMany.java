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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;

import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;

@SuppressWarnings("unchecked")
public class TestUnidirectionalOneToMany extends JPA1Base {

    private static final int EMP_ID_VALUE = 4;
    private static final Integer EMP_ID = new Integer(EMP_ID_VALUE);
    private static final Set<Pair> SEED_SET = new HashSet<Pair>();
    static {
        SEED_SET.add(new Pair(EMP_ID_VALUE, 1));
        SEED_SET.add(new Pair(EMP_ID_VALUE, 2));
        SEED_SET.add(new Pair(EMP_ID_VALUE, 3));
    }

    @Before
    public void seedDataModel() throws SQLException {
        clearAllTables(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(17, "R&D");
            Employee emp = new Employee(EMP_ID_VALUE, "Hans", "Wurst", dep);
            Review r1 = new Review(1, Date.valueOf("2005-10-07"), "bla");
            Review r2 = new Review(2, Date.valueOf("2005-10-08"), "ble");
            Review r3 = new Review(3, Date.valueOf("2005-10-09"), "bli");
            emp.addReview(r1);
            emp.addReview(r2);
            emp.addReview(r3);
            em.persist(dep);
            em.persist(emp);
            em.persist(r1);
            em.persist(r2);
            em.persist(r3);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUnchanged() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // 1. do nothing
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, EMP_ID);
            // do nothing
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            checkJoinTable(SEED_SET);
            // 2. touch the reviews
            env.beginTransaction(em);
            emp = em.find(Employee.class, EMP_ID);
            emp.getReviews().size();
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            checkJoinTable(SEED_SET);
            // 3. trivial update
            env.beginTransaction(em);
            emp = em.find(Employee.class, EMP_ID);
            Set<Review> reviews = emp.getReviews();
            emp.setReviews(new HashSet<Review>(reviews));
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            checkJoinTable(SEED_SET);
        } finally {
            closeEntityManager(em);
        }
    }

    private void checkJoinTable(Set<Pair> expected) throws SQLException {
        DataSource ds = getEnvironment().getDataSource();
        Connection conn = ds.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT EMP_ID, REVIEW_ID FROM TMP_EMP_REVIEW");
            try {
                ResultSet rs = pstmt.executeQuery();
                try {
                    Set<Pair> actual = new HashSet<Pair>();
                    while (rs.next()) {
                        actual.add(new Pair(rs.getInt("EMP_ID"), rs.getInt("REVIEW_ID")));
                    }
                    verify(expected.size() == actual.size(), "actual set has wrong size " + actual.size() + " expected: "
                            + expected.size());
                    verify(expected.containsAll(actual), "actual set contains some elements missing in the expecetd set");
                    verify(actual.containsAll(expected), "expected set contains some elements missing in the actual set");
                } finally {
                    rs.close();
                }
            } finally {
                pstmt.close();
            }
        } finally {
            conn.close();
        }
    }

    @Test
    public void testDeleteEmployee() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, EMP_ID);
            verify(emp != null, "employee not found");
            em.remove(emp);
            env.commitTransactionAndClear(em);
            checkJoinTable(new HashSet<Pair>());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDeleteReview() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, EMP_ID);
            verify(emp != null, "employee not found");
            Set reviews = emp.getReviews();
            verify(reviews != null, "reviews are null");
            verify(reviews.size() == 3, "not exactly 3 reviews but " + reviews.size());
            Iterator iter = reviews.iterator();
            Review review = (Review) iter.next();
            int removedId = review.getId();
            // there are no managed relationships -> we have to remove the reviews on both sides
            em.remove(review);
            iter.remove();
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.remove(new Pair(EMP_ID_VALUE, removedId));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, EMP_ID);
            reviews = emp.getReviews();
            verify(reviews.size() == 2, "not exactly 2 reviews but " + reviews.size());
            Object object = em.find(Review.class, new Integer(removedId));
            verify(object == null, "review found");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testAdd() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, EMP_ID);
            verify(emp != null, "employee not found");
            Set<Review> reviews = emp.getReviews();
            Review r4 = new Review(4, Date.valueOf("2005-10-10"), "blo");
            em.persist(r4);
            reviews.add(r4);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.add(new Pair(EMP_ID_VALUE, 4));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, EMP_ID);
            reviews = emp.getReviews();
            verify(reviews.size() == 4, "not exactly 4 reviews but " + reviews.size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testExchange() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, EMP_ID);
            verify(emp != null, "employee not found");
            Set<Review> reviews = emp.getReviews();
            Iterator iter = reviews.iterator();
            Review review = (Review) iter.next();
            int removedId = review.getId();
            // there are no managed relationships -> we have to remove the reviews on both sides
            em.remove(review);
            iter.remove();
            Review r4 = new Review(4, Date.valueOf("2005-10-10"), "blo");
            em.persist(r4);
            reviews.add(r4);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.remove(new Pair(EMP_ID_VALUE, removedId));
            expected.add(new Pair(EMP_ID_VALUE, 4));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, EMP_ID);
            reviews = emp.getReviews();
            verify(reviews.size() == 3, "not exactly 3 reviews but " + reviews.size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMoveAllReviews() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final int newId = 15;
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, EMP_ID);
            verify(emp != null, "employee not found");
            Set<Review> reviews = emp.getReviews();
            Employee newEmp = new Employee(newId, "Paulchen", "M\u00fcller", null);
            newEmp.setReviews(reviews);
            emp.setReviews(null);
            em.persist(newEmp);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>();
            expected.add(new Pair(newId, 1));
            expected.add(new Pair(newId, 2));
            expected.add(new Pair(newId, 3));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, Integer.valueOf(newId));
            reviews = emp.getReviews();
            verify(reviews.size() == 3, "not exactly 3 reviews but " + reviews.size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private static class Pair {
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                Pair other = (Pair) obj;
                return other.empId == empId && other.reviewId == reviewId;
            }
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return empId + 17 * reviewId;
        }

        /**
         * @return Returns the empId.
         */
        @SuppressWarnings("unused")
        public int getEmpId() {
            return empId;
        }

        /**
         * @return Returns the reviewId.
         */
        @SuppressWarnings("unused")
        public int getReviewId() {
            return reviewId;
        }

        final int empId;
        final int reviewId;

        Pair(int e, int r) {
            empId = e;
            reviewId = r;
        }
    }
}
