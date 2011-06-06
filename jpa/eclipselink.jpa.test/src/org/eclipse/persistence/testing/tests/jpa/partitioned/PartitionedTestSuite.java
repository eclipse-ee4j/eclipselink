/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.partitioning.CustomPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.RangePartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.RoundRobinPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.UnionPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.ValuePartitioningPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.partitioned.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class PartitionedTestSuite extends JUnitTestCase {
    public static boolean validDatabase = true;
    public static boolean isRAC = false;
        
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
        suite.addTest(new PartitionedTestSuite("testPersistPartitioning"));
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
        Map properties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        if (getServerSession().getPlatform().isDerby()) {
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
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_FAILOVER,
                    "node2, node1");
        } else if (getServerSession().getPlatform().isH2()) {
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                    "2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    "jdbc:h2:test2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    "jdbc:h2:test3");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                    "8");
        } else if (getServerSession().getPlatform().isHSQL()) {
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                    "2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    "jdbc:hsqldb:file:test2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    "jdbc:hsqldb:file:test3");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                    "8");
        } else if (!isOnServer() && getServerSession().getPlatform().isOracle() && (getServerSession().getLogin().getURL().indexOf("ems56442") != -1)) {
            isRAC = true;
            // RAC testing (direct node).
            String url = getServerSession().getLogin().getURL();
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                    "2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    (url.substring(0, url.length() - 1)) + "3");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    url);
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                    "8");            
        } else if (!isOnServer() && getServerSession().getPlatform().isOracle() && (getServerSession().getLogin().getURL().indexOf("@(DESCRIPTION") != -1)) {
            isRAC = true;
            // UCP RAC callback testing.
            properties.put(
                    PersistenceUnitProperties.PARTITIONING_CALLBACK,
                    "org.eclipse.persistence.platform.database.oracle.ucp.UCPDataPartitioningCallback");
        } else if (isOnServer()) {
            isRAC = true;
            try {
                Class.forName("weblogic.jdbc.common.internal.DataSourceManager");
            } catch (Exception notWebLogic) {
                warning("Partitioning tests only run on WebLogic with GridLink.");
                return;
            }
        } else {
            isRAC = true;
            // Simulate a RAC using multiple connection pools to the same database.
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                    "2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    getServerSession().getLogin().getURL());
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    getServerSession().getLogin().getURL());
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                    "8");
        }
        getEntityManagerFactory(getPersistenceUnitName(), properties);
        if (isRAC) {
            // Disable replication and unioning in RAC.
            for (PartitioningPolicy policy : getDatabaseSession().getProject().getPartitioningPolicies().values()) {
                if (policy instanceof RoundRobinPartitioningPolicy) {
                    ((RoundRobinPartitioningPolicy)policy).setReplicateWrites(false);
                } else if (policy instanceof UnionPartitioningPolicy) {
                    ((UnionPartitioningPolicy)policy).setReplicateWrites(false);
                } else if (policy instanceof CustomPartitioningPolicy) {
                    ((EmployeePartitioningPolicy)((CustomPartitioningPolicy)policy).getPolicy()).setReplicate(false);
                } else if (policy instanceof RangePartitioningPolicy) {
                    ((RangePartitioningPolicy)policy).setUnionUnpartitionableQueries(false);
                } else if (policy instanceof ValuePartitioningPolicy) {
                    ((ValuePartitioningPolicy)policy).setUnionUnpartitionableQueries(false);
                }
            }
            CollectionMapping mapping = (CollectionMapping)getDatabaseSession().getDescriptor(Employee.class).getMappingForAttributeName("projects");
            PartitioningPolicy policy = getDatabaseSession().getProject().getPartitioningPolicy("defaut");
            mapping.setPartitioningPolicy(policy);
            mapping.getSelectionQuery().setPartitioningPolicy(policy);
            mapping = (CollectionMapping)getDatabaseSession().getDescriptor(Employee.class).getMappingForAttributeName("managedEmployees");
            mapping.setPartitioningPolicy(policy);
            mapping.getSelectionQuery().setPartitioningPolicy(policy);
        }
        new PartitionedTableCreator().replaceTables(getDatabaseSession());
        
        EntityManager em = createEntityManager();    
        try {
            PopulationManager.resetDefaultManager();
            beginTransaction(em);
            new EmployeePopulator().persistExample(em);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }        
        clearCache();
    }
    
    /**
     * Test reading.
     */
    public void testReadEmployee() {
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
        if (!this.validDatabase) {
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
                    if (found && !isRAC) {
                        fail("Data found in more than one partition.");
                    }
                    found = true;
                } else if (isRAC) {
                    fail("Data not found in all partitions.");
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
    
    /**
     * Test exclusive partitioning with persist.
     * The persist should decide the connection, not the query.
     */
    public void testPersistPartitioning() throws Exception {
        if (!this.validDatabase) {
            return;
        }
        if (isOnServer()) {
            return;
        }
        for (int count = 0; count < 2; count++) {
            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Always);
            EntityManager em = createEntityManager(properties);
            try {
                beginTransaction(em);
                Employee employee1 = new Employee();
                employee1.setLocation("Ottawa");
                em.persist(employee1);
                commitTransaction(em);
                closeEntityManager(em);
                clearCache();
                em = createEntityManager(properties);
                beginTransaction(em);
                Employee employee2 = new Employee();
                employee2.setLocation("Ottawa");
                em.persist(employee2);
                Employee result = em.find(Employee.class, new EmployeePK(employee1.getId(), "Toronto"));
                if (result != null) {
                    fail("Employee should not exist.");
                }
                result = em.find(Employee.class, new EmployeePK(employee1.getId(), "Ottawa"));
                rollbackTransaction(em);
                if (result == null) {
                    // Retry once, as sequence select could use wrong node.
                    if (count == 1) {
                        fail("Employee not found, wrong partition used.");
                    }
                } else {
                    return;
                }
            } finally {
                closeEntityManagerAndTransaction(em);            
            }
        }
    }
}