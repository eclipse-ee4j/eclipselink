package org.eclipse.persistence.testing.models.jpa.sessionbean;

import java.util.List;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;

/**
 * EmployeeService session bean interface.
 */
public interface EmployeeService {
    List findAll();
    
    Employee findById(int id);
    
    Employee fetchById(int id);
    
    void update(Employee employee);
    
    int insert(Employee employee);
}
