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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestSetFunctions extends JPA1Base {

    private void verifyBigDecimal(EntityManager em, String txt, BigDecimal expected) {
        Query query = em.createQuery(txt);
        List result = query.getResultList();
        verify(result.size() == 1, "wrong resultcount");
        Iterator iter = result.iterator();
        verify(iter.hasNext(), "no row found");
        BigDecimal bigDecimal = (BigDecimal) iter.next();
        verify(bigDecimal.compareTo(expected) == 0, "wrong result: " + bigDecimal);
        verify(!iter.hasNext(), "too many rows found");
    }

    private void verifyDouble(EntityManager em, String txt, Double expected) {
        Query query = em.createQuery(txt);
        List result = query.getResultList();
        verify(result.size() == 1, "wrong resultcount");
        Iterator iter = result.iterator();
        verify(iter.hasNext(), "no row found");
        Double doubleValue = (Double) iter.next();
        verify(doubleValue.compareTo(expected) == 0, "wrong result: " + doubleValue);
        verify(!iter.hasNext(), "too many rows found");
    }

    @Override
    protected void setup() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(new Employee(1, "Ulla", "Schmidt", null, BigDecimal.valueOf(5000)));
            em.persist(new Employee(2, "Knut", "M\u00fcller", null, BigDecimal.valueOf(6000)));
            em.persist(new Employee(3, "Kuno", "Maier", null, BigDecimal.valueOf(10000)));
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSum() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            verifyBigDecimal(em, "select sum(e.salary) from Employee e", BigDecimal.valueOf(21000));
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMin() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            verifyBigDecimal(em, "select min(e.salary) from Employee e", BigDecimal.valueOf(5000));
            verifyBigDecimal(em, "select min(e.salary) from Employee e where e.firstname like 'K%'", BigDecimal.valueOf(6000));
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindEmployeeWithMinimalSalary() {
        // TODO subqueries not finished yet
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em
                    .createQuery("select e from Employee e where e.salary = (select min (e2.salary) from Employee e2) ");
            List result = query.getResultList();
            Iterator iterator = result.iterator();
            verify(iterator.hasNext(), "no row found");
            Employee first = (Employee) iterator.next();
            verify(first.getId() == 1, "wrong employee");
            verify(!iterator.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMax() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            verifyBigDecimal(em, "select max(e.salary) from Employee e", BigDecimal.valueOf(10000));
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testAverage() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            verifyDouble(em, "select avg(e.salary) from Employee e", Double.valueOf(7000));
        } finally {
            closeEntityManager(em);
        }
    }
}
