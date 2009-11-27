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
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.EmploymentPeriod;
import org.junit.Test;

public class TestQueryAPI extends QueryTest {

    private static final String queryStringOrderedParam = "SELECT e FROM Employee e WHERE e.firstname = ?1";
    private static final String queryStringNamedParam = "SELECT e FROM Employee e WHERE e.firstname = :firstname";

    private static final String queryStringOrderedCalendarParam = "SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa.utilCalendar = ?1";
    private static final String queryStringNamedCalendarParam = "SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa.utilCalendar = :parameter1";
    private static final String queryStringOrderedDateParam = "SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa.utilDate = ?1";
    private static final String queryStringNamedDateParam = "SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa.utilDate = :parameter1";

    @Override
    protected void setup() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Employee emp = new Employee();
            EmploymentPeriod empPeriod = new EmploymentPeriod();
            empPeriod.setStartDate(new Date(System.currentTimeMillis() - Integer.MAX_VALUE));
            empPeriod.setEndDate(new Date(System.currentTimeMillis()));
            emp.setFirstName("Hans");
            emp.setLastName("Wurst");
            emp.setEmploymentPeriod(empPeriod);
            env.beginTransaction(em);
            em.persist(emp);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void assertOrderedParameterValid(final Query query, int number, final Object value) {
        query.setParameter(number, value);
    }

    private void assertOrderedParameterInvalid(final Query query, int number, final Object value) {
        boolean passed = false;
        try {
            query.setParameter(number, value);
        } catch (IllegalArgumentException e) {
            passed = true;
        }
        verify(passed, ("missing IllegalArgumentException"));
    }

    private void assertNamedParameterValid(final Query query, final String name, final Object value) {
        query.setParameter(name, value);
    }

    private void assertNamedParameterInvalid(final Query query, final String name, final Object value) {
        boolean passed = false;
        try {
            query.setParameter(name, value);
        } catch (IllegalArgumentException e) {
            passed = true;
        }
        verify(passed, "missing IllegalArgumentException");
    }

    private void assertValidQuery(final EntityManager em, final String query) {
        em.createQuery(query);
    }

    private void assertInvalidQuery(final EntityManager em, final String query) {
        boolean passed = false;
        try {
            em.createQuery(query);
        } catch (IllegalArgumentException iaex) {
            passed = true;
        }
        verify(passed, "missing IllegalArgumentException");
    }

    @Test
    public void testQueryCreation() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            assertValidQuery(em, "SELECT e FROM Employee e");
            assertInvalidQuery(em, "SELECT BLAAfjdslkaf fasflkfj weoi jasdf");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testParameterSetting() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();

        try {
            // test valid ordered param
            Query q1 = em.createQuery(queryStringOrderedParam);
            assertOrderedParameterValid(q1, 1, "hugo");

            // test valid named param
            Query q2 = em.createQuery(queryStringNamedParam);
            assertNamedParameterValid(q2, "firstname", "elmar");

            // test invalid typed ordered param
            Query q3 = em.createQuery(queryStringOrderedParam);
            assertOrderedParameterInvalid(q3, 1, new Integer(2));

            // test invalid typed named param
            Query q4 = em.createQuery(queryStringNamedParam);
            assertNamedParameterInvalid(q4, "firstname", new Integer(3));

            // test invalid named ordered param
            Query q5 = em.createQuery(queryStringOrderedParam);
            assertOrderedParameterInvalid(q5, 2, "wursti");

            // test invalid named named param
            Query q6 = em.createQuery(queryStringNamedParam);
            assertNamedParameterInvalid(q6, "lastname", "olga");

            // test calendar named param
            Query q7 = em.createQuery(queryStringNamedCalendarParam);
            assertParameterValid(q7, new InputParameterHolder("parameter1", Calendar.getInstance(), TemporalType.TIMESTAMP));

            // test calendar ordered param
            Query q8 = em.createQuery(queryStringOrderedCalendarParam);
            assertParameterValid(q8, new InputParameterHolder(1, Calendar.getInstance(), TemporalType.TIMESTAMP));

            // test calendar ordered param
            @SuppressWarnings("unused")
            Query q9 = em.createQuery(queryStringOrderedDateParam);
            assertParameterValid(q8, new InputParameterHolder(1, new java.util.Date(System.currentTimeMillis()),
                    TemporalType.TIMESTAMP));

            // test calendar ordered param
            @SuppressWarnings("unused")
            Query q10 = em.createQuery(queryStringNamedDateParam);
            assertParameterValid(q8, new InputParameterHolder(1, new java.util.Date(System.currentTimeMillis()),
                    TemporalType.TIMESTAMP));

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testTemporalParameterSettingInvalid() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();

        try {
            // test calendar named param
            Query q7 = em.createQuery(queryStringNamedCalendarParam);
            assertParameterInvalid(q7, new InputParameterHolder("parameter1", Calendar.getInstance()));

            // test calendar ordered param
            Query q8 = em.createQuery(queryStringOrderedCalendarParam);
            assertParameterInvalid(q8, new InputParameterHolder(1, Calendar.getInstance()));

            // test calendar ordered param
            Query q9 = em.createQuery(queryStringOrderedDateParam);
            assertParameterInvalid(q9, new InputParameterHolder(1, new java.util.Date(System.currentTimeMillis())));

            // test calendar ordered param
            Query q10 = em.createQuery(queryStringNamedDateParam);
            assertParameterInvalid(q10, new InputParameterHolder(1, new java.util.Date(System.currentTimeMillis())));

        } finally {
            closeEntityManager(em);
        }
    }

}
