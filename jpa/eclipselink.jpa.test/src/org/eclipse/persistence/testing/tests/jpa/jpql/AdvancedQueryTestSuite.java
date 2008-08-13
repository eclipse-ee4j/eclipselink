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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;


/**
 * <p>
 * <b>Purpose</b>: Test advanced JPA Query functionality.
 * <p>
 * <b>Description</b>: This tests query hints, caching and query optimization.
 * <p>
 */
public class AdvancedQueryTestSuite extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public AdvancedQueryTestSuite() {
        super();
    }

    public AdvancedQueryTestSuite(String name) {
        super(name);
    }

    // This method is run at the start of EVERY test case method.

    public void setUp() {

    }

    // This method is run at the end of EVERY test case method.

    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedQueryTestSuite");
        suite.addTest(new AdvancedQueryTestSuite("testQueryCacheFirstCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryCacheOnlyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryCacheOnlyCacheHitsOnSession"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPrimaryKeyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryExactPrimaryKeyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryTypeCacheHits"));
        return new TestSetup(suite) {

                //This method is run at the end of the SUITE only

                protected void tearDown() {
                    clearCache();
                }
                // This method is run at the start of the SUITE only.

                protected void setUp() {
                    //get session to start setup
                    DatabaseSession session = JUnitTestCase.getServerSession();
                    //create a new EmployeePopulator
                    EmployeePopulator employeePopulator = new EmployeePopulator();
                    new AdvancedTableCreator().replaceTables(session);
                    //initialize the global comparer object
                    comparer = new JUnitDomainObjectComparer();
                    //set the session for the comparer to use
                    comparer.setSession((AbstractSession)session.getActiveSession());
                    //Populate the tables
                    employeePopulator.buildExamples();
                    //Persist the examples in the database
                    employeePopulator.persistExample(session);
                }
            };
    }

    /**
     * Test that a cache hit will occur on a primary key query.
     */
    public void testQueryPrimaryKeyCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheByPrimaryKey);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();
            if (queryResult != employee) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test that a cache hit will occur on a primary key query.
     */
    public void testQueryTypeCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();
            if (queryResult != employee) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test that a cache hit will occur on a primary key query.
     */
    public void testQueryExactPrimaryKeyCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select employee from Employee employee where employee.id = :id");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheByExactPrimaryKey);
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();
            if (queryResult != employee) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test that a cache hit will occur on a query.
     */
    public void testQueryCacheFirstCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select employee from Employee employee where employee.firstName = :firstName");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();
            if (!queryResult.getFirstName().equals(employee.getFirstName())) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test that a cache hit will occur on a query.
     */
    public void testQueryCacheOnlyCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select employee from Employee employee where employee.firstName = :firstName");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("firstName", employee.getFirstName());
            // Test that list works as well.
            query.getResultList();
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }
    
    /**
     * Test that a cache hit will occur on a query when the object is not in the unit of work/em.
     */
    public void testQueryCacheOnlyCacheHitsOnSession() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            query = em.createQuery("Select employee from Employee employee where employee.id = :id");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("id", employee.getId());
            if (query.getSingleResult() == null) {
                fail("Query did not check session cache.");
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            query = em.createQuery("Select employee from Employee employee where employee.id = :id");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("id", employee.getId());
            if (query.getResultList().size() != 1) {
                fail("Query did not check session cache.");
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit do not occur: " + counter.getSqlStatements());
            }
        } finally {
            if (counter != null) {
                counter.remove();
            }
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
}
