/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     2010-10-27 - James Sutherland (Oracle) initial impl
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.jpa.partitioned;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import junit.framework.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.partitioned.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class PartitionedTestSuite extends JUnitTestCase {
        
    public static Test suite() {
        TestSuite suite = new TestSuite("PartitioningTests");
        suite.addTest(new PartitionedTestSuite("testSetup"));
        suite.addTest(new PartitionedTestSuite("testReadEmployee"));
        suite.addTest(new PartitionedTestSuite("testReadAllEmployee"));
        suite.addTest(new PartitionedTestSuite("testPersistEmployee"));
        suite.addTest(new PartitionedTestSuite("testRemoveEmployee"));
        suite.addTest(new PartitionedTestSuite("testUpdateEmployee"));
        suite.addTest(new PartitionedTestSuite("testReadProject"));
        suite.addTest(new PartitionedTestSuite("testReadAllProject"));
        suite.addTest(new PartitionedTestSuite("testPersistProject"));
        suite.addTest(new PartitionedTestSuite("testRemoveProject"));
        suite.addTest(new PartitionedTestSuite("testUpdateProject"));
        suite.addTest(new PartitionedTestSuite("testPartitioning"));
        return suite;
    }
    
    public PartitionedTestSuite(String name) {
        super(name);
    }
    
    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    @Override
    public String getPersistenceUnitName() {
        return "partitioned";
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        if (!getServerSession().getPlatform().isDerby()) {
            warning("Partitioning tests only run on Derby.");
            return;
        }
        Map properties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        properties.put(
                PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                "2");
        properties.put(
                PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                "jdbc:derby:node2;create=true");
        properties.put(
                PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                "jdbc:derby:node3;create=true");
        properties.put(
                PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                "8");
        getEntityManagerFactory(getPersistenceUnitName(), properties);
        new PartitionedTableCreator().replaceTables(getDatabaseSession());
        
        EntityManager em = createEntityManager();    
        try {
            PopulationManager.resetDefaultManager();
            new EmployeePopulator().persistExample(em);
            closeEntityManager(em);
        }  catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }            
            closeEntityManager(em);
            throw e;
        }        
        clearCache();
    }
    
    /**
     * Test reading.
     */
    public void testReadEmployee() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        EntityManager em = createEntityManager();    
        try {
            beginTransaction(em);
            Query query = em.createQuery("Select e from Employee e where e.firstName = :name");
            query.setParameter("name", "Bob");
            Employee employee = (Employee)query.getSingleResult();
            for (int index = 0; index < 3; index++) {
                if (!((Employee)query.getSingleResult()).getFirstName().equals("Bob")) {
                    fail("Employee not correct.");
                }
            }
            commitTransaction(em);
            closeEntityManager(em);
            for (int index = 0; index < 3; index++) {
                clearCache();
                em = createEntityManager();
                if (!(em.find(Employee.class, employee.pk())).getFirstName().equals("Bob")) {
                    fail("Employee not correct.");
                }
                closeEntityManager(em);
            }
        }  catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }            
            closeEntityManager(em);
            throw e;
        }
        verifyObject(new EmployeePopulator().employeeExample1());
        verifyObject(new EmployeePopulator().employeeExample2());
        verifyObject(new EmployeePopulator().employeeExample3());
    }
    
    /**
     * Test reading.
     */
    public void testReadProject() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        EntityManager em = createEntityManager();    
        try {
            beginTransaction(em);
            Query query = em.createQuery("Select p from Project p where p.name = :name");
            query.setParameter("name", "Sales Reporting");
            Project project = (Project)query.getSingleResult();
            for (int index = 0; index < 3; index++) {
                if (!((Project)query.getSingleResult()).getName().equals("Sales Reporting")) {
                    fail("Project not correct.");
                }
            }
            commitTransaction(em);
            closeEntityManager(em);
            for (int index = 0; index < 3; index++) {
                clearCache();
                em = createEntityManager();
                if (!(em.find(Project.class, project.getId())).getName().equals("Sales Reporting")) {
                    fail("Project not correct.");
                }
                closeEntityManager(em);
            }            
        }  catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }            
            closeEntityManager(em);
            throw e;
        }
        verifyObject(new EmployeePopulator().largeProjectExample1());
        verifyObject(new EmployeePopulator().largeProjectExample2());
        verifyObject(new EmployeePopulator().smallProjectExample1());
    }
    
    /**
     * Test reading.
     */
    public void testReadAllEmployee() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        EntityManager em = createEntityManager();    
        try {
            beginTransaction(em);
            int size = em.createQuery("Select e from Employee e").getResultList().size();
            for (int index = 0; index < 3; index++) {
                if (size != em.createQuery("Select e from Employee e").getResultList().size()) {
                    fail("Query result size does not match.");
                }
            }
            commitTransaction(em);
            closeEntityManager(em);
        }  catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Test reading.
     */
    public void testReadAllProject() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        EntityManager em = createEntityManager();    
        try {
            beginTransaction(em);
            int size = em.createQuery("Select p from Project p").getResultList().size();
            for (int index = 0; index < 3; index++) {
                if (size != em.createQuery("Select p from Project p").getResultList().size()) {
                    fail("Query result size does not match.");
                }
            }
            commitTransaction(em);
            closeEntityManager(em);
        }  catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Test remove.
     */
    public void testRemoveEmployee() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            verifyPersistAndRemove(new EmployeePopulator().basicEmployeeExample1());
        }
    }
    
    /**
     * Test remove.
     */
    public void testRemoveProject() {
        if (!getDatabaseSession().getPlatform().isDerby()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            verifyPersistAndRemove(new EmployeePopulator().basicLargeProjectExample2());
        }
    }
    
    /**
     * Test persist.
     */
    public void testPersistEmployee() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            verifyPersist(new EmployeePopulator().basicEmployeeExample2());
        }
    }
    
    /**
     * Test persist.
     */
    public void testPersistProject() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            verifyPersist(new EmployeePopulator().basicLargeProjectExample1());
            verifyPersist(new EmployeePopulator().basicSmallProjectExample3());
        }
    }
    
    /**
     * Test update.
     */
    public void testUpdateEmployee() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            EntityManager em = createEntityManager();
            try {
                beginTransaction(em);
                List<Employee> employees = em.createQuery("Select e from Employee e").getResultList();
                for (Employee employee : employees) {
                    employee.setLastName(employee.getLastName() + "2");
                    employee.addResponsibility("new" + index);
                    employee.getAddress().setStreet(employee.getAddress().getStreet() + "2");
                }
                commitTransaction(em);
                for (Employee employee : employees) {
                    verifyObject(employee);
                    verifyObject(employee.getAddress());
                }
                clearCache();
                for (Employee employee : employees) {
                    verifyObject(employee);
                    verifyObject(employee.getAddress());
                }
                closeEntityManager(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }            
                closeEntityManager(em);
                throw e;
            }
        }
    }
    
    /**
     * Test update.
     */
    public void testUpdateProject() {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            EntityManager em = createEntityManager();
            try {
                beginTransaction(em);
                List<Project> projects = em.createQuery("Select p from Project p").getResultList();
                for (Project project : projects) {
                    project.setDescription(project.getDescription() + "2");
                }
                commitTransaction(em);
                for (Project project : projects) {
                    verifyObject(project);
                }
                clearCache();
                for (Project project : projects) {
                    verifyObject(project);
                }
                closeEntityManager(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }            
                closeEntityManager(em);
                throw e;
            }
        }
    }
    
    /**
     * Test that partitioning is being used.
     */
    public void testPartitioning() throws Exception {
        if (!getServerSession().getPlatform().isDerby()) {
            return;
        }
        boolean found = false;
        EntityManager em = createEntityManager();
        try {
            Project project = (Project)em.createQuery("Select p from Project p").getResultList().get(0);
            for (int index = 0; index < 3; index++) {
                beginTransaction(em);
                Query query = em.createNativeQuery("Select * from PART_PROJECT where PROJ_ID = ?");
                query.setParameter(1, project.getId());
                List results = query.getResultList();
                if (!results.isEmpty()) {
                    if (found) {
                        fail("Data found in more than one partition.");
                    }
                    found = true;
                }
                commitTransaction(em);
            }
            if (!found) {
                fail("Data not found in any partition.");
            }
        } catch (Exception e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);            
        }
    }
}