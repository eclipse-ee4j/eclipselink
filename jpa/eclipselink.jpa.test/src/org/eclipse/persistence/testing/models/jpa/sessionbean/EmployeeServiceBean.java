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
@Stateless
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {
    @PersistenceContext(name="fieldaccess")
    protected EntityManager entityManager;

    public List findAll() {
        Query query = entityManager.createQuery("Select e from Employee e");
        return query.getResultList();
    }
    
    public Employee findById(int id) {
        Employee employee = entityManager.find(Employee.class, new Integer(id));
        employee.getAddress();
        return employee;
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
        entityManager.flush();
        return employee.getId();
    }
    
}
