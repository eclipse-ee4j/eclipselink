/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.models.jpa.sessionbean;

import java.util.List;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;

/**
 * EmployeeService session bean.
 */
@Stateless(mappedName="EmployeeService")
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {
    @PersistenceContext(unitName="sessionbean")
    protected EntityManager entityManager;

    @Override
    public List findAll() {
        Query query = entityManager.createQuery("Select e from Employee e");
        return query.getResultList();
    }

    @Override
    public Employee findById(int id) {
        Employee employee = entityManager.find(Employee.class, Integer.valueOf(id));
        if (employee != null) {
            employee.getAddress();
        }
        return employee;
    }

    @Override
    public List findByFirstName(String fname) {
        //NamedQuery("findAllFieldAccessEmployeesByFirstName")
        Query query = entityManager.createQuery("SELECT e FROM Employee e where e.firstName = :fname").setParameter("fname", fname);
    query.setHint("eclipselink.cache-usage", "CheckCacheOnly");
        return query.getResultList();
    }
    @Override
    public Employee fetchById(int id) {
        Employee employee = entityManager.find(Employee.class, Integer.valueOf(id));
        employee.getAddress();
        employee.getManager();
        return employee;
    }

    @Override
    public void update(Employee employee) {
        entityManager.merge(employee);
    }

    @Override
    public int insert(Employee employee) {
        entityManager.persist(employee);
        if (employee.getDepartment() != null) {
            entityManager.persist(employee.getDepartment());
        }
        entityManager.flush();
        return employee.getId();
    }

   @Override
   public void delete(Employee employee) {
         Employee emp = entityManager.find(Employee.class, employee.getId());
        entityManager.remove(emp);
    }
}
