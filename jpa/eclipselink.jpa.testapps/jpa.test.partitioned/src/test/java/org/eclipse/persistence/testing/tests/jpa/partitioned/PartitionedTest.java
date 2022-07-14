/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     2010-10-27 - James Sutherland (Oracle) initial impl
package org.eclipse.persistence.testing.tests.jpa.partitioned;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.partitioning.CustomPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.RangePartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.RoundRobinPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.UnionPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.ValuePartitioningPolicy;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.partitioned.Employee;
import org.eclipse.persistence.testing.models.jpa.partitioned.EmployeePK;
import org.eclipse.persistence.testing.models.jpa.partitioned.EmployeePartitioningPolicy;
import org.eclipse.persistence.testing.models.jpa.partitioned.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.partitioned.Office;
import org.eclipse.persistence.testing.models.jpa.partitioned.PartitionedTableCreator;
import org.eclipse.persistence.testing.models.jpa.partitioned.Project;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class PartitionedTest extends JUnitTestCase {
    public static boolean validDatabase = true;
    public static boolean isRAC = false;

    public static Test suite() {
        TestSuite suite = new TestSuite("PartitioningTests");
        suite.addTest(new PartitionedTest("testSetup"));
        suite.addTest(new PartitionedTest("testReadEmployee"));
        suite.addTest(new PartitionedTest("testReadAllEmployee"));
        suite.addTest(new PartitionedTest("testPersistEmployee"));
        suite.addTest(new PartitionedTest("testRemoveEmployee"));
        suite.addTest(new PartitionedTest("testUpdateEmployee"));
        suite.addTest(new PartitionedTest("testReadProject"));
        suite.addTest(new PartitionedTest("testReadAllProject"));
        suite.addTest(new PartitionedTest("testPersistProject"));
        suite.addTest(new PartitionedTest("testRemoveProject"));
        suite.addTest(new PartitionedTest("testUpdateProject"));
        suite.addTest(new PartitionedTest("testPartitioning"));
        suite.addTest(new PartitionedTest("testPersistPartitioning"));
        suite.addTest(new PartitionedTest("testPersistOfficeWithLongName"));
        return suite;
    }

    public PartitionedTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
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
        final JpaEntityManagerFactory jpaEmf = getEntityManagerFactory().unwrap(JpaEntityManagerFactory.class);
        final ServerSession session = jpaEmf.getServerSession();
        DatabasePlatform databasePlatform = session.getPlatform();
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties());
        if (databasePlatform.isDerby()) {
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                    "2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    JUnitTestCaseHelper.getDatabasePropertiesForIndex("2").get(PersistenceUnitProperties.JDBC_URL));
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    JUnitTestCaseHelper.getDatabasePropertiesForIndex("3").get(PersistenceUnitProperties.JDBC_URL));
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                    "8");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_FAILOVER,
                    "node2, node1");
        } else if (databasePlatform.isH2()) {
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
        } else if (databasePlatform.isHSQL()) {
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
        } else if (!isOnServer() && databasePlatform.isOracle() && (session.getLogin().getURL().contains("ems56442"))) {
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
        } else if (!isOnServer() && databasePlatform.isOracle() && (session.getLogin().getURL().contains("@(DESCRIPTION"))) {
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
            // Do not run on Sybase as may hang.
            if (databasePlatform.isSybase()) {
                validDatabase = false;
                return;
            }
            isRAC = true;
            // Simulate a RAC using multiple connection pools to the same database.
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_MIN,
                    "2");
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node2." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    session.getLogin().getURL());
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_URL,
                    session.getLogin().getURL());
            properties.put(
                    PersistenceUnitProperties.CONNECTION_POOL + "node3." + PersistenceUnitProperties.CONNECTION_POOL_MAX,
                    "8");
        }

        jpaEmf.refreshMetadata(properties);

        final DatabaseSessionImpl dbSession = jpaEmf.getDatabaseSession();
        if (isRAC) {
            // Disable replication and unioning in RAC.
            for (PartitioningPolicy policy : dbSession.getProject().getPartitioningPolicies().values()) {
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
            CollectionMapping mapping = (CollectionMapping) dbSession.getDescriptor(Employee.class).getMappingForAttributeName("projects");
            PartitioningPolicy policy = dbSession.getProject().getPartitioningPolicy("defaut");
            mapping.setPartitioningPolicy(policy);
            mapping.getSelectionQuery().setPartitioningPolicy(policy);
            mapping = (CollectionMapping) dbSession.getDescriptor(Employee.class).getMappingForAttributeName("managedEmployees");
            mapping.setPartitioningPolicy(policy);
            mapping.getSelectionQuery().setPartitioningPolicy(policy);
        }
        new PartitionedTableCreator().replaceTables(dbSession);

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
        if (!validDatabase) {
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
        if (!validDatabase) {
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
        if (!validDatabase) {
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
        if (!validDatabase) {
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
        if (!validDatabase) {
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
        if (!validDatabase) {
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
        if (!validDatabase) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            verifyPersist(new EmployeePopulator().basicEmployeeExample2());
        }
    }

    /**
     * Test persist an Office with a long name to produce a potential negative hashcode.
     * Bug 371514 - fragile hashing in HashPartitioningPolicy
     */
    public void testPersistOfficeWithLongName() {
        if (!validDatabase) {
            return;
        }
        for (int index = 0; index < 25; index++) {
            String longName = "Office with a very, very, very long name indeed";
            longName += String.valueOf(UUID.randomUUID());
            verifyPersist(new Office(longName, index + 1));
        }
    }

    /**
     * Test persist.
     */
    public void testPersistProject() {
        if (!validDatabase) {
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
        if (!validDatabase) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            EntityManager em = createEntityManager();
            try {
                beginTransaction(em);
                List<Employee> employees = em.createQuery("Select e from Employee e", Employee.class).getResultList();
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
        if (!validDatabase) {
            return;
        }
        for (int index = 0; index < 3; index++) {
            EntityManager em = createEntityManager();
            try {
                beginTransaction(em);
                List<Project> projects = em.createQuery("Select p from Project p", Project.class).getResultList();
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
        if (!validDatabase) {
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
                List<?> results = query.getResultList();
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
        if (!validDatabase) {
            return;
        }
        if (isOnServer()) {
            return;
        }
        for (int count = 0; count < 2; count++) {
            Map<String, Object> properties = new HashMap<>();
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
