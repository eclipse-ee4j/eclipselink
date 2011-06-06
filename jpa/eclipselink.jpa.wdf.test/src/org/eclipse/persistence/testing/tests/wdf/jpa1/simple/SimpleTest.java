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

package org.eclipse.persistence.testing.tests.wdf.jpa1.simple;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.component.Component;
import org.eclipse.persistence.testing.models.wdf.jpa1.component.Metric;
import org.eclipse.persistence.testing.models.wdf.jpa1.component.MetricFloat;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class SimpleTest extends JPA1Base {

    private static final String HASTIG = "Hastig";

    @Test
    public void testSimple() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(8, "acht");
            Employee emp = new Employee(3, "Hugo", "Hurtig", dep);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(emp);
            em.flush();
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep = em.find(Department.class, new Integer(8));
            verify(dep != null, "department is null");
            Department dep2 = em.find(Department.class, new Integer(8));
            verify(dep == dep2, "department is not unique");
            emp = em.find(Employee.class, new Integer(3));
            verify(emp != null, "employee is null");
            Employee emp2 = em.find(Employee.class, new Integer(3));
            verify(emp == emp2, "employee is not unique");
            emp.setLastName(HASTIG);
            dep.setName("88888888");
            em.flush();
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp = em.find(Employee.class, new Integer(3));
            dep = em.find(Department.class, new Integer(8));
            verify(emp != null, "employee is null");
            verify(HASTIG.equals(emp.getLastName()), "employee has wrong last name: " + emp.getLastName());
            em.remove(emp);
            em.remove(dep);
            em.flush();
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMetric() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Component component = new Component();
            em.persist(component);
            long componentId = component.getId();

            MetricFloat metric1 = new MetricFloat();
            metric1.setName("metric1");
            metric1.setComponentId(componentId);
            metric1.setInspectionId(1L);
            metric1.setValue(1.0F);
            em.persist(metric1);

            MetricFloat metric2 = new MetricFloat();
            metric2.setName("metric2");
            metric2.setComponentId(componentId);
            metric2.setInspectionId(1L);
            metric2.setValue(2.0F);
            em.persist(metric2);

            Collection<Metric> metrics = new HashSet<Metric>();
            metrics.add(metric1);
            metrics.add(metric2);

            component.setMetrics(metrics);
            env.commitTransaction(em);

            env.beginTransaction(em);
            component = em.find(Component.class, componentId);
            component.getMetrics().remove(metric1);
            env.commitTransaction(em);

        } finally {
            closeEntityManager(em);
        }
    }
}
