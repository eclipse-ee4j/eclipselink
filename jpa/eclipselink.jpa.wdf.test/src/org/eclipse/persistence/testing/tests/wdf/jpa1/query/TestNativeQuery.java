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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestNativeQuery extends JPA1Base {
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
            Employee knut = new Employee(1, "Knut", "Maier", dep10);
            Employee fred = new Employee(2, "Fred", "Schmidt", null);
            Cubicle green = new Cubicle(Integer.valueOf(1), Integer.valueOf(2), "green", knut);
            knut.setCubicle(green);
            em.persist(dep10);
            em.persist(dep20);
            em.persist(green);
            em.persist(knut);
            em.persist(fred);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDefaultMapping() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNativeQuery("select * from TMP_DEP D where D.ID = 10", Department.class);
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Department, "wrong instance: " + object.getClass().getName());
            verify(em.contains(object), "object not contained in em");
            Department department = (Department) object;
            verify(department.getId() == 10, "wrong id: " + department.getId());
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDefaultMappingWithMappingResult() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNativeQuery("select * from TMP_DEP D where D.ID = 10", "departmentByClass");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Department, "wrong instance: " + object.getClass().getName());
            verify(em.contains(object), "object not contained in em");
            Department department = (Department) object;
            verify(department.getId() == 10, "wrong id: " + department.getId());
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDefaultMappingNamedNativeQuery() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNamedQuery("getDepartmentWithId10SQL_class");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Department, "wrong instance: " + object.getClass().getName());
            verify(em.contains(object), "object not contained in em");
            Department department = (Department) object;
            verify(department.getId() == 10, "wrong id: " + department.getId());
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testResultSetMappingNamedNativeQuery() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNamedQuery("getDepartmentWithId10SQL_mapping");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Department, "wrong instance: " + object.getClass().getName());
            verify(em.contains(object), "object not contained in em");
            Department department = (Department) object;
            verify(department.getId() == 10, "wrong id: " + department.getId());
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.MS_SQL_SERVER })
    @Test
    public void testColumnResult() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNamedQuery("getDepartmentName");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object != null, "object is null");
            verify("ten".equals(object), "wrong object: " + object);
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.MS_SQL_SERVER })
    @Test
    public void testFieldResult() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNamedQuery("getDepartmentFieldByField");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Department, "wrong instance: " + object.getClass().getName());
            verify(em.contains(object), "object not contained in em");
            Department department = (Department) object;
            verify(department.getId() == 10, "wrong id: " + department.getId());
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDefaultMappingWithParameter() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createNativeQuery("select * from TMP_DEP D where D.ID = ?", Department.class);
            query.setParameter(1, Integer.valueOf(10));
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Department, "wrong instance: " + object.getClass().getName());
            verify(em.contains(object), "object not contained in em");
            Department department = (Department) object;
            verify(department.getId() == 10, "wrong id: " + department.getId());
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.MS_SQL_SERVER })
    @Test
    public void testFieldByField() throws SQLException {
        clearAllTables();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Department dep1 = new Department(1, "one");
            Department dep2 = new Department(2, "two");
            em.persist(dep1);
            em.persist(dep2);
            getEnvironment().commitTransactionAndClear(em);
            Query query = em.createNamedQuery("getDepartmentFieldByField1");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Object[], "wrong instance: " + object.getClass().getName());
            Object[] objects = (Object[]) object;
            verify(objects.length == 2, "wrong length: " + objects.length);
            verify(((Number) objects[0]).intValue() == 1, "wrong id: " + ((Number) objects[0]).intValue());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }
}
