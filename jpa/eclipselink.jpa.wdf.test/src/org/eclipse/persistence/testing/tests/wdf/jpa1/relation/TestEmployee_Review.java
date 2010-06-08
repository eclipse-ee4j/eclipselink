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

import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;

import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class TestEmployee_Review extends JPA1Base {
    private final Department _dep = new Department(17, "dep_17");
    private final Employee _emp = new Employee(7, "first", "last", _dep);
    private final Review _review1 = new Review(12, Date.valueOf("2005-09-12"), "bad day");
    private final Review _review2 = new Review(13, Date.valueOf("2005-09-13"), "bad day");

    @Override
    public void setup() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            _emp.addReview(_review1);
            _emp.addReview(_review2);
            em.persist(_dep);
            em.persist(_emp);
            em.persist(_review1);
            em.persist(_review2);
            em.flush();
            env.commitTransactionAndClear(em);
            Review rev = em.find(Review.class, new Integer(12));
            verify(rev != null, "Review null");
            verify(rev.getId() == 12, "wrong review");
            env.beginTransaction(em);
            Employee employee = em.find(Employee.class, new Integer(7));
            verify(employee != null, "employee not found");
            Set reviews = employee.getReviews();
            verify(reviews.size() == 2, "set has wrong size");
            verify(reviews.contains(_review1), "missing review 1");
            verify(reviews.contains(_review2), "missing review 2");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindIndividualReview() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Review rev = em.find(Review.class, new Integer(12));
            verify(rev != null, "Review null");
            verify(rev.getId() == 12, "wrong review");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNavigateReviewsInTransaction() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee employee = em.find(Employee.class, new Integer(7));
            verify(employee != null, "employee not found");
            Set reviews = employee.getReviews();
            verify(reviews.size() == 2, "set has wrong size");
            verify(reviews.contains(_review1), "missing review 1");
            verify(reviews.contains(_review2), "missing review 2");
            Iterator iter = reviews.iterator();
            while (iter.hasNext()) {
                verify(em.contains(iter.next()), "review in collection not contained in em");
            }
            env.rollbackTransactionAndClear(em);
            iter = reviews.iterator();
            while (iter.hasNext()) {
                verify(!em.contains(iter.next()), "review in collection contained in em");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNavigateReviewsOutsideTransaction() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            boolean failureExpected;
            if (getEnvironment().usesExtendedPC()) {
                failureExpected = false;
            } else {
                failureExpected = true;
            }
            Employee employee = em.find(Employee.class, new Integer(7));
            verify(employee != null, "employee not found");
            Set reviews = employee.getReviews();
            try {
                reviews.size();
                if (failureExpected) {
                    flop("missing persistence exception");
                }
            } catch (PersistenceException ex) {
                if (!failureExpected) {
                    throw ex;
                }
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNavigateDeserializedEmployeeFails() throws IOException, ClassNotFoundException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee employee = em.find(Employee.class, new Integer(7));
            verify(employee != null, "employee not found");
            employee = AbstractBaseTest.serializeDeserialize(employee);
            Set reviews = employee.getReviews();
            try {
                reviews.size();
                flop("missing persistence exception");
            } catch (PersistenceException ex) {
                // OK
            } catch (RuntimeException ex) {
                // OK EclipseLink throws ValidationException
            }
            assertTrue(env.isTransactionActive(em));
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNavigatedReviewsCanBeDeserialized() throws IOException, ClassNotFoundException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee employee = em.find(Employee.class, new Integer(7));
            verify(employee != null, "employee not found");
            Set reviews = employee.getReviews();
            // touch the set
            verify(reviews.size() == 2, "set has wrong size");
            env.rollbackTransactionAndClear(em);
            employee = AbstractBaseTest.serializeDeserialize(employee);
            reviews = employee.getReviews();
            verify(reviews.size() == 2, "set has wrong size");
        } finally {
            closeEntityManager(em);
        }
    }
}
