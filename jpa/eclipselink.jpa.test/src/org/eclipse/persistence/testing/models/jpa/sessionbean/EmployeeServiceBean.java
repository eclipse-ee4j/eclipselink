/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.models.jpa.sessionbean;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;

/**
 * EmployeeService session bean.
 */
@Stateless(mappedName="EmployeeService")
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {
    @PersistenceContext(unitName="sessionbean")
    protected EntityManager entityManager;

    public List findAll() {
        Query query = entityManager.createQuery("Select e from Employee e");
        return query.getResultList();
    }

    public Employee findById(int id) {
        Employee employee = entityManager.find(Employee.class, new Integer(id));
        if (employee != null) {
            employee.getAddress();
        }
        return employee;
    }

    public List findByFirstName(String fname) {
        //NamedQuery("findAllFieldAccessEmployeesByFirstName")
        Query query = entityManager.createQuery("SELECT e FROM Employee e where e.firstName = :fname").setParameter("fname", fname);
    query.setHint("eclipselink.cache-usage", "CheckCacheOnly");
        return query.getResultList();
    }
    public Employee fetchById(int id) {
        Employee employee = entityManager.find(Employee.class, new Integer(id));
        employee.getAddress();
        employee.getManager();
        return employee;
    }

    public void update(Employee employee) {
        entityManager.merge(employee);
    }

    public int insert(Employee employee) {
        entityManager.persist(employee);
        if (employee.getDepartment() != null) {
            entityManager.persist(employee.getDepartment());
        }
        entityManager.flush();
        return employee.getId();
    }

   public void delete(Employee employee) {
         Employee emp = entityManager.find(Employee.class, employee.getId());
        entityManager.remove(emp);
    }
}
