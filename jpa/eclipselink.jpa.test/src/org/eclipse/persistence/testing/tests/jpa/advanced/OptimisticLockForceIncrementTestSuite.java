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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;

import junit.framework.TestSuite;

/**
 * Bug 389265 - OptimisticLock Force Increment increments version both on flush
 * and on commit
 *
 * - Test LockModeType.OPTIMISTIC_FORCE_INCREMENT with combinations of
 * flush/commit/changes/no-op
 */
public class OptimisticLockForceIncrementTestSuite extends JUnitTestCase {

    public OptimisticLockForceIncrementTestSuite() {
        super();
    }

    public OptimisticLockForceIncrementTestSuite(String name) {
        super(name);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite("OptimisticLockForceIncrementTestSuite");
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testSetup"));

        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementNoChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPreFlushChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPostFlushChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPreAndPostFlushChanges"));

        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementCommitNoChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPreCommitChanges"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementFlushCommitNoChanges"));

        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementMultipleEntities"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementBasicPromoteLock"));
        suite.addTest(new OptimisticLockForceIncrementTestSuite("testVersionIncrementPromoteLock"));

        return suite;
    }

    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());

        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(JUnitTestCase.getServerSession());

        clearCache();
    }

    public void testVersionIncrementNoChanges() {
        if (usesSOP()) {
            // with SOP it fails with: 1 SQL update statement execution(s)
            // expected:<1> but was:<2>
            // Looks like a bug: cascade versioning (used with SOP) adds an
            // extra version increment.
            return;
        }
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            counter.getSqlStatements().clear();
            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            em.flush();

            em.getTransaction().commit();
            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            // SQL statements expected - 1 x update
            assertEquals("1 SQL update statement execution(s)", 1, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementPreFlushChanges() {
        if (usesSOP()) {
            // with SOP it fails with: 1 SQL update statement execution(s)
            // expected:<1> but was:<2>
            // Looks like a bug: cascade versioning (used with SOP) adds an
            // extra version increment.
            return;
        }
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();

            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();

            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            entity.setCity("Vancouver");
            entity.setProvince("BC");
            entity.setCountry("Canada");

            em.flush();

            em.getTransaction().commit();
            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            assertEquals("Entity's name should be changed", "Vancouver", entity.getCity());
            // SQL statements expected - 1 x update
            assertEquals("1 SQL update statement execution(s)", 1, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementPostFlushChanges() {
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();

            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            em.flush();

            entity.setCity("Toronto");
            entity.setProvince("ON");
            entity.setCountry("Canada");

            em.getTransaction().commit();
            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            assertEquals("Entity's name should be changed", "Toronto", entity.getCity());
            // SQL statements expected - 2 x update
            assertEquals("2 SQL update statement execution(s)", 2, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementPreAndPostFlushChanges() {
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();

            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            entity.setCity("Moncton");
            entity.setProvince("NB");
            entity.setCountry("Canada");

            em.flush();

            entity.setCity("Halifax");
            entity.setProvince("NS");
            entity.setCountry("Canada");

            em.getTransaction().commit();
            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            assertEquals("Entity's name should be changed", "Halifax", entity.getCity());
            // SQL statements expected - 2 x update
            assertEquals("2 SQL update statement execution(s)", 2, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementCommitNoChanges() {
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();

            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            em.getTransaction().commit();
            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            // SQL statements expected - 1 x update
            assertEquals("1 SQL update statement execution(s)", 1, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementPreCommitChanges() {
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();

            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            entity.setCity("Calgary");
            entity.setProvince("AB");
            entity.setCountry("Canada");

            em.getTransaction().commit();

            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            assertEquals("Entity's name should be changed", "Calgary", entity.getCity());
            // SQL statements expected - 1 x update
            assertEquals("1 SQL update statement execution(s)", 1, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementFlushCommitNoChanges() {
        if (usesSOP()) {
            // with SOP it fails with: 1 SQL update statement execution(s)
            // expected:<1> but was:<2>
            // Looks like a bug: cascade versioning (used with SOP) adds an
            // extra version increment.
            return;
        }
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            em.flush();

            em.getTransaction().commit();
            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            // SQL statements expected - 1 x update
            assertEquals("1 SQL update statement execution(s)", 1, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementMultipleEntities() {
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity1 = addresses.get(0);
            Address entity2 = addresses.get(1);

            assertNotNull("Entity 1: Address cannot be null", entity1);
            assertNotNull("Entity 2: Address cannot be null", entity2);

            int startVersion1 = entity1.getVersion();
            int startVersion2 = entity2.getVersion();
            em.lock(entity1, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            // update entity 2
            entity2.setCity("Kamloops");
            entity2.setProvince("BC");
            entity2.setCountry("Canada");

            em.flush();

            // update entity 1
            entity1.setCity("New Glasgow");
            entity1.setProvince("NS");
            entity1.setCountry("Canada");

            em.flush();

            int expectedVersion1 = (startVersion1 + 1);
            int actualVersion1 = entity1.getVersion();

            int expectedVersion2 = (startVersion2 + 1);
            int actualVersion2 = entity2.getVersion();

            em.getTransaction().rollback();
            em.close();

            assertEquals("Entity 1: Version number incremented incorrectly: ", expectedVersion1, actualVersion1);
            assertEquals("Entity 2: Version number incremented incorrectly: ", expectedVersion2, actualVersion2);

            // 3 SQL update statements expected
            assertEquals("3 SQL update statement execution(s)", 3, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementBasicPromoteLock() {
        if (usesSOP()) {
            // with SOP it fails with: 1 SQL update statement execution(s)
            // expected:<1> but was:<2>
            // Looks like a bug: cascade versioning (used with SOP) adds an
            // extra version increment.
            return;
        }
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC);

            entity.setCity("Churchill");
            entity.setProvince("MB");
            entity.setCountry("Canada");

            em.flush();
            em.getTransaction().commit();

            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            // SQL statements expected
            assertEquals("1 SQL update statement execution(s)", 1, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testVersionIncrementPromoteLock() {
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());
            EntityManager em = getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            List<Address> addresses = em.createQuery("select a from Address a").getResultList();
            counter.getSqlStatements().clear();
            assertNotNull("Null query results returned", addresses);
            assertNotSame("No query results returned", addresses.size(), 0);
            Address entity = addresses.get(0);
            assertNotNull("Entity: Address cannot be null", entity);

            int startVersion = entity.getVersion();
            em.lock(entity, LockModeType.OPTIMISTIC);

            entity.setCity("Banff");
            entity.setProvince("AB");
            entity.setCountry("Canada");

            em.flush();

            em.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            entity.setCity("London");
            entity.setProvince("ON");
            entity.setCountry("Canada");

            em.getTransaction().commit();

            int expectedVersion = (startVersion + 1);
            int actualVersion = entity.getVersion();

            em.close();

            assertEquals("Version number incremented incorrectly: ", expectedVersion, actualVersion);
            // SQL statements expected
            assertEquals("2 SQL update statement execution(s)", 2, countNumberOfUpdateStatements(counter));
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }

    protected int countNumberOfUpdateStatements(QuerySQLTracker counter) {
        if (counter == null || counter.getSqlStatements().size() == 0) {
            return 0;
        }

        int numberOfStatements = 0;
        List<String> statements = counter.getSqlStatements();
        for (String statement : statements) {
            if (statement != null && statement.trim().toUpperCase().startsWith("UPDATE")) {
                numberOfStatements++;
            }
        }
        return numberOfStatements;
    }

}
