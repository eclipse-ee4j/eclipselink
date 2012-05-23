/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.dcn;

import java.util.List;

import javax.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.dcn.Employee;
import org.eclipse.persistence.testing.models.jpa.dcn.LargeProject;
import org.eclipse.persistence.testing.models.jpa.dcn.Project;
import org.eclipse.persistence.testing.models.jpa.dcn.SmallProject;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

/**
 * TestSuite to test Oracle Database Change event Notification.
 * ** To run this test suite the CHANGE NOTIFICATION privilege must be granted to the user.
 * GRANT SCOTT CHANGE NOTIFICATION
 */
public class DCNTestSuite extends JUnitTestCase {

    public static String DCN2 = "dcn2";
    public static int SLEEP = 2000;
    
    public static boolean supported = false;

    public DCNTestSuite(){
    }

    public DCNTestSuite(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DCNTestSuite");
        suite.addTest(new DCNTestSuite("testSetup"));
        suite.addTest(new DCNTestSuite("testUpdate"));
        suite.addTest(new DCNTestSuite("testUpdateProject"));
        suite.addTest(new DCNTestSuite("testUpdateSecondaryTable"));
        suite.addTest(new DCNTestSuite("testUpdateElementCollection"));
        suite.addTest(new DCNTestSuite("testUpdateManyToMany"));
        suite.addTest(new DCNTestSuite("testUpdateOneToMany"));
        suite.addTest(new DCNTestSuite("testNewUpdate"));
        suite.addTest(new DCNTestSuite("testRemove"));
        suite.addTest(new DCNTestSuite("testUpdateAll"));
        suite.addTest(new DCNTestSuite("testNativeSQL"));
        return suite;
    }
    
    @Override
    public String getPersistenceUnitName() {
        return "dcn";
    }
    
    public void sleep() {
        try {
            Thread.sleep(SLEEP);
        } catch (Exception ignore) {}
    }

    public void testSetup() {
        supported = getDatabaseSession().getPlatform().isOracle();
        if(!supported) {
            return;
        }
        TableCreator creator = new DefaultTableGenerator(getDatabaseSession().getProject()).generateDefaultTableCreator();
        creator.replaceTables(getDatabaseSession());
        // Need to reconnect to register with the new tables.
        getDatabaseSession().logout();
        getDatabaseSession(DCN2).logout();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            for (int index = 0; index < 10; index++) {
                Employee employee = new Employee();
                employee.setName("E-" + index);
                em.persist(employee);
            }
            for (int index = 0; index < 10; index++) {
                Project project = new SmallProject();
                project.setName("S-" + index);
                em.persist(project);
                project = new LargeProject();
                project.setName("L-" + index);
                em.persist(project);
            }
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        clearCache(DCN2);
    }
    
    /**
     * Test that updates to existing objects are invalidated.
     */
    public void testUpdate() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            try {
                employee = (Employee)em.createQuery("Select e from Employee e").getResultList().get(0);
                employee.setName(employee.getName() + "+");
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                Employee employee2 = em.find(Employee.class, id);
                if (!employee.getName().equals(employee2.getName())) {
                    fail("Names do not match: " + employee.getName() + " - " + employee2.getName());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that updates to existing objects are invalidated.
     */
    public void testUpdateProject() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select p from Project p").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            Project project = null;
            try {
                project = (Project)em.createQuery("Select p from Project p").getResultList().get(0);
                project.setName(project.getName() + "+");
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(project);
                Project project2 = em.find(Project.class, id);
                if (!project.getName().equals(project2.getName())) {
                    fail("Names do not match: " + project.getName() + " - " + project2.getName());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that updates to existing objects are invalidated.
     */
    public void testUpdateSecondaryTable() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            try {
                employee = (Employee)em.createQuery("Select e from Employee e").getResultList().get(0);
                employee.setSalary(employee.getSalary() + 100);
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                Employee employee2 = em.find(Employee.class, id);
                if (employee.getSalary() != employee2.getSalary()) {
                    fail("Salaries do not match: " + employee.getSalary() + " - " + employee2.getSalary());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that updates to existing objects are invalidated.
     */
    public void testUpdateElementCollection() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e left join fetch e.responsiblities").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            try {
                employee = (Employee)em.createQuery("Select e from Employee e").getResultList().get(0);
                employee.getResponsiblities().add("make coffee");
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                Employee employee2 = em.find(Employee.class, id);
                if (!employee.getResponsiblities().equals(employee2.getResponsiblities())) {
                    fail("Responsiblities do not match: " + employee.getResponsiblities() + " - " + employee2.getResponsiblities());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that updates to existing objects are invalidated.
     */
    public void testUpdateManyToMany() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e left join fetch e.projects").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            try {
                employee = (Employee)em.createQuery("Select e from Employee e").getResultList().get(0);
                SmallProject project = new SmallProject();
                em.persist(project);
                employee.getProjects().add(project);
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                Employee employee2 = em.find(Employee.class, id);
                if (!employee.getProjects().equals(employee2.getProjects())) {
                    fail("Projects do not match: " + employee.getProjects() + " - " + employee2.getProjects());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that updates to existing objects are invalidated.
     */
    public void testUpdateOneToMany() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e left join fetch e.managedEmployees").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            try {
                employee = (Employee)em.createQuery("Select e from Employee e").getResultList().get(0);
                Employee newEmployee = new Employee();
                em.persist(newEmployee);
                newEmployee.setManager(employee);
                employee.getManagedEmployees().add(newEmployee);
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                Employee employee2 = em.find(Employee.class, id);
                if (!employee.getManagedEmployees().equals(employee2.getManagedEmployees())) {
                    fail("managedEmployees do not match: " + employee.getManagedEmployees() + " - " + employee2.getManagedEmployees());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that updates to new objects are invalidated.
     */
    public void testNewUpdate() {
        if (this.supported) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            Employee employee = new Employee();
            employee.setName("new");
            try {
                em.persist(employee);
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            em = createEntityManager(DCN2);
            beginTransaction(em);
            Employee employee2 = null;
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                employee2 = em.find(Employee.class, id);
                if (!employee.getName().equals(employee2.getName())) {
                    fail("Names do not match: " + employee.getName() + " - " + employee2.getName());
                }
                employee2.setName("changed");
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager();
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                employee = em.find(Employee.class, id);
                if (!employee.getName().equals(employee2.getName())) {
                    fail("Names do not match: " + employee.getName() + " - " + employee2.getName());
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that removing in once session invalidates the object.
     */
    public void testRemove() {
        if (this.supported) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            Employee employee = new Employee();
            employee.setName("new");
            try {
                em.persist(employee);
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            em = createEntityManager(DCN2);
            beginTransaction(em);
            Employee employee2 = null;
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                employee2 = em.find(Employee.class, id);
                em.remove(employee2);
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager();
            try {
                Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(employee);
                employee = em.find(Employee.class, id);
                if (employee != null) {
                    fail("Employee not deleted:" + employee);
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Test that update all invalidates the objects.
     */
    public void testUpdateAll() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            try {
                em.createQuery("Update Employee e set e.name = CONCAT(e.name, '+')").executeUpdate();
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                List<Employee> result = em.createQuery("Select e from Employee e").getResultList();
                for (Employee employee : result) {
                    if (employee.getName().indexOf("+") == -1) {
                        fail("Employee name not correct: " + employee);
                    }
                }
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }
    
    /**
     * Test that native sql invalidates the objects.
     */
    public void testNativeSQL() {
        if (this.supported) {
            EntityManager em = createEntityManager(DCN2);
            try {
                em.createQuery("Select e from Employee e").getResultList();
            } finally {
                em.close();
            }
            em = createEntityManager();
            beginTransaction(em);
            try {
                em.createNativeQuery("Update DCN_EMPLOYEE e set e.NAME = 'foobar'").executeUpdate();
                commitTransaction(em);
            } finally {
                closeEntityManagerAndTransaction(em);
            }
            sleep();
            em = createEntityManager(DCN2);
            try {
                List<Employee> result = em.createQuery("Select e from Employee e").getResultList();
                for (Employee employee : result) {
                    if (!employee.getName().equals("foobar")) {
                        fail("Employee name not correct: " + employee);
                    }
                }
            } finally {
                closeEntityManagerAndTransaction(em);
            }
        }
    }
}
