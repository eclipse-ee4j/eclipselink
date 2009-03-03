/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.models.jpa.performance;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * EmployeeService session bean.
 */
@Stateless(mappedName="EmployeeService")
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {
    @PersistenceContext(name="performance")
    protected EntityManager entityManager;

    public List findAll() {
        Query query = entityManager.createQuery("Select e from Employee e");
        return query.getResultList();
    }
    
    public Employee findById(long id) {
        Employee employee = entityManager.find(Employee.class, id);
        employee.getAddress();
        return employee;
    }
    
    public Employee fetchById(long id) {
        Employee employee = entityManager.find(Employee.class, id);
        employee.getAddress();
        employee.getManager();
        return employee;
    }
    
    public void update(Employee employee) {
        entityManager.merge(employee);
    }
    
    public long insert(Employee employee) {
        entityManager.persist(employee);
        entityManager.flush();
        return employee.getId();
    }
    
}
