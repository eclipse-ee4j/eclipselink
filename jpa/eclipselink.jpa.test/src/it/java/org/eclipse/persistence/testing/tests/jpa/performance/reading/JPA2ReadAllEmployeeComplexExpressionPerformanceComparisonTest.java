/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.testing.models.jpa.performance2.Employee;
import org.eclipse.persistence.testing.models.jpa.performance2.PhoneNumber;

/**
 * This test compares the performance of read all Employee.
 */
public class JPA2ReadAllEmployeeComplexExpressionPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {
    public JPA2ReadAllEmployeeComplexExpressionPerformanceComparisonTest() {
        super(false);
        setDescription("This test compares the performance of read all Employee in a criteria.");
    }

    /**
     * Read all.
     */
    public void test() throws Exception {
        EntityManager em = createEntityManager();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = qb.createQuery(Employee.class);
        Root<Employee> emp = criteriaQuery.from(Employee.class);
        Join<Employee, PhoneNumber> phone = emp.join("phoneNumbers");
        criteriaQuery.where(
                qb.and(
                        qb.equal(emp.get("firstName"), qb.parameter(String.class, "firstName")),
                        qb.equal(emp.get("lastName"), qb.parameter(String.class, "lastName")),
                        qb.equal(emp.get("address").get("city"), qb.parameter(String.class, "city")),
                        qb.equal(phone.get("areaCode"), qb.parameter(String.class, "areaCode"))));
        Query query = em.createQuery(criteriaQuery);
        query.setParameter("firstName", "Bob");
        query.setParameter("lastName", "Smith%");
        query.setParameter("city", "Toronto");
        query.setParameter("areaCode", "123");
        List result = list(query, em);
        result.size();
        em.close();
    }
}
