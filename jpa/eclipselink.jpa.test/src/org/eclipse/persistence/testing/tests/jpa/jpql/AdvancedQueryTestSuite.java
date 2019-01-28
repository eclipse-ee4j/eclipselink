/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.PessimisticLock;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritancePopulator;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Buyer;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;

import org.junit.Assert;

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
        suite.addTest(new AdvancedQueryTestSuite("testSetup"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryCacheFirstCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryCacheOnlyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryCacheOnlyCacheHitsOnSession"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPrimaryKeyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryExactPrimaryKeyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryTypeCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryCache"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryREADLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryWRITELock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryOPTIMISTICLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryOPTIMISTIC_FORCE_INCREMENTLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_READLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_WRITELock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_READ_TIMEOUTLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_WRITE_TIMEOUTLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTICLockWithLimit"));
        suite.addTest(new AdvancedQueryTestSuite("testPESSIMISTIC_LockWithDefaultTimeOutUnit"));
        suite.addTest(new AdvancedQueryTestSuite("testPESSIMISTIC_LockWithSecondsTimeOutUnit"));
        suite.addTest(new AdvancedQueryTestSuite("testObjectResultType"));
        suite.addTest(new AdvancedQueryTestSuite("testNativeResultType"));
        suite.addTest(new AdvancedQueryTestSuite("testCursors"));
        suite.addTest(new AdvancedQueryTestSuite("testFetchGroups"));
        suite.addTest(new AdvancedQueryTestSuite("testMultipleNamedJoinFetchs"));
        suite.addTest(new AdvancedQueryTestSuite("testNativeQueryTransactions"));
        suite.addTest(new AdvancedQueryTestSuite("testLockWithSecondaryTable"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingJOIN"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingEXISTS"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingIN"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingIN5"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingIN2"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingCursor"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingPagination"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingPagination2"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingReadObject"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingInheritance"));
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapBatchFetchingJOIN"));
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapBatchFetchingEXISTS"));
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapBatchFetchingIN"));
        suite.addTest(new AdvancedQueryTestSuite("testMapBatchFetchingJOIN"));
        suite.addTest(new AdvancedQueryTestSuite("testMapBatchFetchingEXISTS"));
        suite.addTest(new AdvancedQueryTestSuite("testMapBatchFetchingIN"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchingINCache"));
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapLeftJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testBatchFetchOuterJoin"));
        suite.addTest(new AdvancedQueryTestSuite("testJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testMapJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testJoinFetchingCursor"));
        suite.addTest(new AdvancedQueryTestSuite("testJoinFetchingPagination"));
        suite.addTest(new AdvancedQueryTestSuite("testMapKeyJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testMapKeyBatchFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testJPQLCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testCacheIndexes"));
        suite.addTest(new AdvancedQueryTestSuite("testSQLHint"));
        suite.addTest(new AdvancedQueryTestSuite("testLoadGroup"));
        suite.addTest(new AdvancedQueryTestSuite("testConcurrentLoadGroup"));
        if (!isJPA10()) {
            suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_FORCE_INCREMENTLock"));
            suite.addTest(new AdvancedQueryTestSuite("testVersionChangeWithReadLock"));
            suite.addTest(new AdvancedQueryTestSuite("testVersionChangeWithWriteLock"));
            suite.addTest(new AdvancedQueryTestSuite("testNamedQueryAnnotationOverwritePersistenceXML"));
        }
        suite.addTest(new AdvancedQueryTestSuite("testTearDown"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
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

        new RelationshipsTableManager().replaceTables(session);
        //populate the relationships model and persist as well
        new RelationshipsExamples().buildExamples(session);

        new InheritanceTableCreator().replaceTables(session);
        InheritancePopulator inheritancePopulator = new InheritancePopulator();
        inheritancePopulator.buildExamples();

        //Persist the examples in the database
        inheritancePopulator.persistExample(session);
    }

    public void testTearDown() {
        JUnitTestCase.closeEntityManagerFactory(getPersistenceUnitName());
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
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheByPrimaryKey);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();
            Assert.assertEquals("Employees are not equal", employee, queryResult);
            Assert.assertEquals("Cache hit should not occur", 0, counter.getSqlStatements().size());
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }


    /**
     * Test using the hint hint.
     */
    public void testSQLHint() {
        if (!getDatabaseSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            counter = new QuerySQLTracker(getServerSession());

            Query query = em.createNamedQuery("findAllAddressesByPostalCode");
            query.setParameter("postalcode", "K2H8C2");
            query.getResultList();

            Assert.assertTrue("No statements returned", counter.getSqlStatements().size() > 0);
            Assert.assertEquals("SQL hint was not used: " + counter.getSqlStatements(), -1, (counter.getSqlStatements().get(0)).indexOf("/*"));
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
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();
            Assert.assertEquals("Employees are not equal", employee, queryResult);
            Assert.assertEquals("Cache hit should not occur", 0, counter.getSqlStatements().size());
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test fetch groups.
     */
    public void testFetchGroups() {
        if (!isWeavingEnabled()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            rollbackTransaction(em);
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            beginTransaction(em);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.id = :id");
            query.setHint(QueryHints.FETCH_GROUP_ATTRIBUTE, "firstName");
            query.setHint(QueryHints.FETCH_GROUP_ATTRIBUTE, "lastName");
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();
            Assert.assertEquals("More than fetch group selected", 1, counter.getSqlStatements().size());
            queryResult.getGender();
            Assert.assertEquals("Access to unfetch did not cause fetch", 2, counter.getSqlStatements().size());
            verifyObject(employee);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test multiple fetch joining from named queries.
     */
    public void testMultipleNamedJoinFetchs() {
        if (!isWeavingEnabled()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            clearCache();
            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            Query query = em.createNamedQuery("findAllEmployeesJoinAddressPhones");
            List<Employee> result = query.getResultList();
            Assert.assertEquals("More than join fetches selected", 1, counter.getSqlStatements().size());
            for (Employee each : result) {
                each.getAddress().getCity();
                each.getPhoneNumbers().size();
            }
            Assert.assertEquals("Join fetches triggered query", 1, counter.getSqlStatements().size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test cursored queries.
     */
    public void testCursors() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // Test cusored stream.
            Query query = em.createQuery("Select e from Employee e");
            query.setHint(QueryHints.CURSOR, true);
            query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 2);
            query.setHint(QueryHints.CURSOR_PAGE_SIZE, 5);
            query.setHint(QueryHints.CURSOR_SIZE, "Select count(*) from CMP3_EMPLOYEE");
            Cursor cursor = (Cursor)query.getSingleResult();
            cursor.nextElement();
            cursor.size();
            cursor.close();

            // Test cursor result API.
            JpaQuery jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery("Select e from Employee e");
            jpaQuery.setHint(QueryHints.CURSOR, true);
            cursor = jpaQuery.getResultCursor();
            cursor.nextElement();
            cursor.size();
            cursor.close();

            // Test scrollable cursor.
            jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery("Select e from Employee e");
            jpaQuery.setHint(QueryHints.SCROLLABLE_CURSOR, true);
            jpaQuery.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
            String resultSetType = ResultSetType.DEFAULT;
            // HANA supports only TYPE_FORWARD_ONLY, see bug 384116
            if (getPlatform().isHANA()) {
                resultSetType = ResultSetType.ForwardOnly;
            }
            jpaQuery.setHint(QueryHints.RESULT_SET_TYPE, resultSetType);
            ScrollableCursor scrollableCursor = (ScrollableCursor)jpaQuery.getResultCursor();
            scrollableCursor.next();
            scrollableCursor.close();

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Test the result type of various queries.
     */
    public void testObjectResultType() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // Load an employee into the cache.
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Test multi object, as an array.
            query = em.createQuery("Select employee, employee.address, employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Object[] arrayResult = (Object[])query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 3, arrayResult.length);
            Assert.assertEquals("Incorrect result", employee, arrayResult[0]);
            Assert.assertEquals("Incorrect result", employee.getAddress(), arrayResult[1]);
            Assert.assertEquals("Incorrect result", employee.getId(), arrayResult[2]);

            List listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 3, arrayResult.length);
            Assert.assertEquals("Incorrect result", employee, arrayResult[0]);
            Assert.assertEquals("Incorrect result", employee.getAddress(), arrayResult[1]);
            Assert.assertEquals("Incorrect result", employee.getId(), arrayResult[2]);

            // Test single object, as an array.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 1, arrayResult.length);
            Assert.assertEquals("Incorrect result", employee.getId(), arrayResult[0]);

            listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 1, arrayResult.length);
            Assert.assertEquals("Incorrect result", employee.getId(), arrayResult[0]);

            // Test multi object, as a Map.
            query = em.createQuery("Select employee, employee.address, employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Map mapResult = (Map)query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 3, mapResult.size());
            Assert.assertEquals("Incorrect result", employee, mapResult.get("employee"));
            Assert.assertEquals("Incorrect result", employee.getAddress(), mapResult.get("address"));
            Assert.assertEquals("Incorrect result", employee.getId(), mapResult.get("id"));

            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 3, mapResult.size());
            Assert.assertEquals("Incorrect result", employee, mapResult.get("employee"));
            Assert.assertEquals("Incorrect result", employee.getAddress(), mapResult.get("address"));
            Assert.assertEquals("Incorrect result", employee.getId(), mapResult.get("id"));

            // Test single object, as a Map.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            mapResult = (Map)query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 1, mapResult.size());
            Assert.assertEquals("Incorrect result", employee.getId(), mapResult.get("id"));

            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 1, mapResult.size());
            Assert.assertEquals("Incorrect result", employee.getId(), mapResult.get("id"));

            // Test single object, as an array.
            query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.Report);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();

            Assert.assertEquals("Incorrect result", employee, arrayResult[0]);

            // Test single object, as value.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Object valueResult = query.getSingleResult();

            Assert.assertEquals("Incorrect result", employee.getId(), valueResult);

            listResult = query.getResultList();
            valueResult = listResult.get(0);

            Assert.assertEquals("Incorrect result", employee.getId(), valueResult);

            // Test multi object, as value.
            query = em.createQuery("Select employee.id, employee.firstName from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Value);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();

            Assert.assertEquals("Incorrect result", employee.getId(), valueResult);

            // Test single object, as attribute.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Attribute);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();

            Assert.assertEquals("Incorrect result", employee.getId(), valueResult);

            listResult = query.getResultList();
            valueResult = listResult.get(0);

            Assert.assertEquals("Incorrect result", employee.getId(), valueResult);
        } finally {
            rollbackTransaction(em);
        }
    }

    /**
     * Test the result type of various native queries.
     */
    public void testNativeResultType() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // Load an employee into the cache.
            Query query = em.createNativeQuery("Select * from CMP3_EMPLOYEE employee", Employee.class);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Test multi object, as an array.
            query = em.createNativeQuery("Select employee.F_NAME, employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            Object[] arrayResult = (Object[])query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 2, arrayResult.length);
            Assert.assertEquals("Incorrect result", employee.getFirstName(), arrayResult[0]);
            //TODO: Possible bug: arrayResult[1] type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)arrayResult[1]).intValue()));

            List listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            Assert.assertEquals("Incorrect number of results", 2, arrayResult.length);
            Assert.assertEquals("Incorrect result", employee.getFirstName(), arrayResult[0]);
            //TODO: Possible bug: arrayResult[1] type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)arrayResult[1]).intValue()));

            // Test single object, as an array.
            query = em.createNativeQuery("Select employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 1, arrayResult.length);
            //TODO: Possible bug: arrayResult[0] type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)arrayResult[0]).intValue()));

            listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 1, arrayResult.length);
            //TODO: Possible bug: arrayResult[0] type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)arrayResult[0]).intValue()));

            // Test multi object, as a Map.
            query = em.createNativeQuery("Select employee.F_NAME, employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            Map mapResult = (Map)query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 2, mapResult.size());
            Assert.assertEquals("Incorrect result", employee.getFirstName(), mapResult.get("F_NAME"));
            //TODO: Possible bug: mapResult.get("EMP_ID") type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)mapResult.get("EMP_ID")).intValue()));

            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 2, mapResult.size());
            Assert.assertEquals("Incorrect result", employee.getFirstName(), mapResult.get("F_NAME"));
            //TODO: Possible bug: mapResult.get("EMP_ID") type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)mapResult.get("EMP_ID")).intValue()));

            // Test single object, as a Map.
            query = em.createNativeQuery("Select employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            mapResult = (Map)query.getSingleResult();

            Assert.assertEquals("Incorrect number of results", 1, mapResult.size());
            //TODO: Possible bug: mapResult.get("EMP_ID") type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)mapResult.get("EMP_ID")).intValue()));

            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);

            Assert.assertEquals("Incorrect number of results", 1, mapResult.size());
            //TODO: Possible bug: mapResult.get("EMP_ID") type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)mapResult.get("EMP_ID")).intValue()));

            // Test single object, as value.
            query = em.createNativeQuery("Select employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            Object valueResult = query.getSingleResult();

            //TODO: Possible bug: valueResult type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)valueResult).intValue()));

            listResult = query.getResultList();
            valueResult = listResult.get(0);

            //TODO: Possible bug: valueResult type is Long instead of Integer
            Assert.assertEquals("Incorrect result", employee.getId(), new Integer(((Number)valueResult).intValue()));
        } finally {
            rollbackTransaction(em);
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
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.id = :id");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheByExactPrimaryKey);
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();

            Assert.assertEquals("Employees are not equal", employee, queryResult);
            Assert.assertEquals("Incorrect number of queries returned", 0, counter.getSqlStatements().size());
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test that the transaction is committed for a single native query transaction.
     */
    public void testNativeQueryTransactions() {
        Employee emp = (Employee)getServerSession().readObject(Employee.class);
        if (emp == null) {
            fail("Test problem: no Employees in the db, nothing to update");
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.setFlushMode(FlushModeType.COMMIT);
            Query query = em.createNativeQuery("Update CMP3_EMPLOYEE set F_NAME = 'Bobo' where EMP_ID = " + emp.getId());
            query.executeUpdate();
            commitTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            query = em.createNativeQuery("Select * from CMP3_EMPLOYEE where F_NAME = 'Bobo' AND EMP_ID = " + emp.getId());

            Assert.assertNotEquals("Native query did not commit transaction", 0, query.getResultList().size());

            // clean up - bring back the original name
            em.setFlushMode(FlushModeType.COMMIT);
            query = em.createNativeQuery("Update CMP3_EMPLOYEE set F_NAME = '"+emp.getFirstName()+"' where EMP_ID = " + emp.getId());
            query.executeUpdate();
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
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
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.firstName = :firstName");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee)query.getSingleResult();

            Assert.assertEquals("Employees are not equal", employee.getFirstName(), queryResult.getFirstName());
            Assert.assertEquals("Incorrect number of queries returned", 0, counter.getSqlStatements().size());
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
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.firstName = :firstName");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("firstName", employee.getFirstName());
            // Test that list works as well.
            query.getResultList();

            Assert.assertEquals("Incorrect number of queries returned", 0, counter.getSqlStatements().size());
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
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            query = em.createQuery("Select e from Employee e where e.id = :id");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("id", employee.getId());

            Assert.assertNotNull("Query did not check session cache", query.getSingleResult());
            Assert.assertEquals("Incorrect number of queries returned", 0, counter.getSqlStatements().size());

            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            query = em.createQuery("Select e from Employee e where e.id = :id");
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            query.setParameter("id", employee.getId());

            Assert.assertEquals("Incorrect number of results", 1, query.getResultList().size());
            Assert.assertEquals("Incorrect number of queries returned", 0, counter.getSqlStatements().size());
        } finally {
            if (counter != null) {
                counter.remove();
            }
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Test the query cache.
     */
    public void testQueryCache() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.
            JpaQuery jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createNamedQuery("CachedAllEmployees");
            List result = jpaQuery.getResultList();
            ReadQuery readQuery = (ReadQuery)jpaQuery.getDatabaseQuery();

            Assert.assertNotNull("Query cache not set", readQuery.getQueryResultsCachePolicy());
            Assert.assertEquals("Query cache size not set", 200, readQuery.getQueryResultsCachePolicy().getMaximumCachedResults());
            Assert.assertTrue("Query cache invalidation not set", readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy() instanceof TimeToLiveCacheInvalidationPolicy);
            Assert.assertEquals("Query cache invalidation time not set", 50000, ((TimeToLiveCacheInvalidationPolicy)readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy()).getTimeToLive());

            jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createNamedQuery("CachedTimeOfDayAllEmployees");
            readQuery = (ReadQuery)jpaQuery.getDatabaseQuery();

            Assert.assertNotNull("Query cache not set", readQuery.getQueryResultsCachePolicy());
            Assert.assertEquals("Query cache size not set", 200, readQuery.getQueryResultsCachePolicy().getMaximumCachedResults());
            Assert.assertTrue("Query cache invalidation not set", readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy() instanceof DailyCacheInvalidationPolicy);

            Calendar calendar = ((DailyCacheInvalidationPolicy)readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy()).getExpiryTime();
            Assert.assertEquals("Query cache invalidation time not se", 23, calendar.get(Calendar.HOUR_OF_DAY));
            Assert.assertEquals("Query cache invalidation time not se", 59, calendar.get(Calendar.MINUTE));
            Assert.assertEquals("Query cache invalidation time not se", 59, calendar.get(Calendar.SECOND));

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            Query query = em.createNamedQuery("CachedAllEmployees");
            Assert.assertEquals("List result size is not correct on 2nd cached query", result.size(), query.getResultList().size());
            Assert.assertEquals("Query cache was not used: " + counter.getSqlStatements(), 0, counter.getSqlStatements().size());
            clearCache();

            // Preload uow to test query cache in uow.
            em.createQuery("Select e from Employee e").getResultList();
            query.getResultList();
            Assert.assertEquals("List result size is not correct on cached query in unit of work", result.size(), query.getResultList().size());
            clearCache();

            // Also test query cache in early transaction.
            em.persist(new Address());
            em.flush();
            query.getResultList();
            Assert.assertEquals("List result size is not correct on cached query in transaction", result.size(), query.getResultList().size());

            // Query by primary key.
            query = em.createNamedQuery("CachedNoEmployees");
            Assert.assertEquals("List result size is not correct", 0, query.getResultList().size());

            // Also test empty query cache.
            counter.remove();
            counter = new QuerySQLTracker(getServerSession());
            Assert.assertEquals("List result size is not correct", 0, query.getResultList().size());
            Assert.assertEquals("Query cache was not used: " + counter.getSqlStatements(), 0, counter.getSqlStatements().size());

            rollbackTransaction(em);
            beginTransaction(em);
            query = em.createNamedQuery("CachedEmployeeJoinAddress");
            result = query.getResultList();
            // Test that an insert triggers the query cache to invalidate.
            Employee employee = new Employee();
            Address address = new Address();
            address.setCity("Ottawa");
            employee.setAddress(address);
            em.persist(employee);
            commitTransaction(em);
            beginTransaction(em);
            query = em.createNamedQuery("CachedEmployeeJoinAddress");
            Assert.assertEquals("Query result cache not invalidated", result.size() + 1, query.getResultList().size());

            address = em.merge(address);
            address.setCity("nowhere");
            commitTransaction(em);
            beginTransaction(em);
            query = em.createNamedQuery("CachedEmployeeJoinAddress");
            Assert.assertEquals("Query result cache not invalidated", result.size(), query.getResultList().size());

            em.remove(em.find(Employee.class, employee.getId()));
            commitTransaction(em);
            query = em.createNamedQuery("CachedEmployeeJoinAddress");
            Assert.assertEquals("Query result cache not invalidated on delete", result.size(), query.getResultList().size());
        } finally {
            closeEntityManagerAndTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    public void testQueryREADLock(){
        // Cannot create parallel entity managers in the server.
        if (isOnServer()) {
            return;
        }

        // Load an employee into the cache.
        EntityManager em = createEntityManager();
        List result = em.createQuery("Select e from Employee e").getResultList();
        Employee employee = (Employee) result.get(0);
        Exception optimisticLockException = null;

        try {
            beginTransaction(em);

            // Query by primary key.
            Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
            query.setLockMode(LockModeType.READ);
            query.setHint(QueryHints.REFRESH, true);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee) query.getSingleResult();
            queryResult.toString();

            EntityManager em2 = createEntityManager();

            try {
                beginTransaction(em2);
                Employee employee2 = em2.find(Employee.class, employee.getId());
                employee2.setFirstName("Read");
                commitTransaction(em2);
            } catch (RuntimeException ex) {
                rollbackTransaction(em2);
                throw ex;
            } finally {
                closeEntityManagerAndTransaction(em2);
            }

            try {
                em.flush();
            } catch (PersistenceException exception) {
                if (exception instanceof OptimisticLockException) {
                    optimisticLockException = exception;
                } else {
                    throw exception;
                }
            }

            rollbackTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw ex;
        } finally {
            closeEntityManager(em);
        }

        Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.READ is used.", optimisticLockException);
    }

    public void testQueryWRITELock(){
        // Cannot create parallel transactions.
        if (isOnServer()) {
            return;
        }

        // Load an employee into the cache.
        EntityManager em = createEntityManager();
        List result = em.createQuery("Select e from Employee e").getResultList();
        Employee employee = (Employee) result.get(0);
        Exception optimisticLockException = null;

        try {
            beginTransaction(em);

            // Query by primary key.
            Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
            query.setLockMode(LockModeType.WRITE);
            query.setHint(QueryHints.REFRESH, true);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Employee queryResult = (Employee) query.getSingleResult();

            EntityManager em2 = createEntityManager();

            try {
                beginTransaction(em2);

                Employee employee2 = em2.find(Employee.class, queryResult.getId());
                employee2.setFirstName("Write");
                commitTransaction(em2);
            } catch (RuntimeException ex) {
                rollbackTransaction(em2);
                closeEntityManager(em2);
                throw ex;
            } finally {
                closeEntityManagerAndTransaction(em2);
            }

            commitTransaction(em);
        } catch (RollbackException exception) {
            if (exception.getCause() instanceof OptimisticLockException){
                optimisticLockException = exception;
            }
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            throw ex;
        } finally {
            closeEntityManagerAndTransaction(em);
        }

        Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.WRITE is used.", optimisticLockException);
    }

    public void testQueryOPTIMISTICLock(){
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            // Load an employee into the cache.
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select e from Employee e").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception optimisticLockException = null;

            try {
                beginTransaction(em);

                // Query by primary key.
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                query.setLockMode(LockModeType.OPTIMISTIC);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.toString();

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);
                    Employee employee2 = em2.find(Employee.class, employee.getId());
                    employee2.setFirstName("Optimistic");
                    commitTransaction(em2);
                } catch (RuntimeException ex) {
                    rollbackTransaction(em2);
                    throw ex;
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                try {
                    em.flush();
                } catch (PersistenceException exception) {
                    if (exception instanceof OptimisticLockException) {
                        optimisticLockException = exception;
                    } else {
                        throw exception;
                    }
                }

                rollbackTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }

                throw ex;
            } finally {
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.READ is used.", optimisticLockException);
        }
    }

    public void testQueryOPTIMISTIC_FORCE_INCREMENTLock(){
        // Cannot create parallel transactions.
        if (! isOnServer()) {
            // Load an employee into the cache.
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select e from Employee e").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception optimisticLockException = null;

            try {
                beginTransaction(em);

                // Query by primary key.
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);

                    Employee employee2 = em2.find(Employee.class, queryResult.getId());
                    employee2.setFirstName("OptimisticForceIncrement");
                    commitTransaction(em2);
                } finally {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                    closeEntityManager(em2);
                }

                commitTransaction(em);
            } catch (RollbackException exception) {
                if (exception.getCause() instanceof OptimisticLockException){
                    optimisticLockException = exception;
                }
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.WRITE is used.", optimisticLockException);
        }
    }

    public void testQueryPESSIMISTIC_READLock() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isHANA()) {
            // HANA currently doesn't support pessimistic locking with queries on multiple tables
            // feature is under development (see bug 384129), but test should be skipped for the time being
            return;
        }
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            PessimisticLockException pessimisticLockException = null;

            try {
                beginTransaction(em);

                EntityManager em2 = createEntityManager();
                try {
                    beginTransaction(em2);

                    List employees2 = em2.createQuery("Select e from Employee e").getResultList(); //
                    Employee employee2 = (Employee) employees2.get(0);

                    // Find all the departments and lock them.
                    List employees = em.createQuery("Select e from Employee e").setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
                    Employee employee = (Employee) employees.get(0);
                    employee.setFirstName("New Pessimistic Employee");

                    HashMap properties = new HashMap();
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.lock(employee2, LockModeType.PESSIMISTIC_READ, properties);
                    employee2.setFirstName("Invalid Lock Employee");

                    commitTransaction(em2);
                } catch (javax.persistence.PessimisticLockException ex) {
                    pessimisticLockException = ex;
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException);
        }
    }

    public void testQueryPESSIMISTIC_WRITELock() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isHANA()) {
            // HANA currently doesn't support pessimistic locking with queries on multiple tables
            // feature is under development (see bug 384129), but test should be skipped for the time being
            return;
        }
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Exception pessimisticLockException = null;

            try {
                beginTransaction(em);

                EntityManager em2 = createEntityManager();
                try {
                    beginTransaction(em2);

                    List employees2 = em2.createQuery("Select e from Employee e").getResultList(); //
                    Employee employee2 = (Employee) employees2.get(0);

                    // Find all the departments and lock them.
                    List employees = em.createQuery("Select e from Employee e").setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
                    Employee employee = (Employee) employees.get(0);
                    employee.setFirstName("New Pessimistic Employee");


                    HashMap properties = new HashMap();
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.lock(employee2, LockModeType.PESSIMISTIC_READ, properties);
                    employee2.setFirstName("Invalid Lock Employee");

                    commitTransaction(em2);
                } catch (javax.persistence.PessimisticLockException ex) {
                    pessimisticLockException = ex;
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException);
        }
    }

    public void testQueryPESSIMISTIC_FORCE_INCREMENTLock() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isHANA()) {
            // HANA currently doesn't support pessimistic locking with queries on multiple tables
            // feature is under development (see bug 384129), but test should be skipped for the time being
            return;
        }
        if (isSelectForUpateSupported()) {
            Employee employee = null;
            Integer version1;

            EntityManager em = createEntityManager();
            beginTransaction(em);

            try {
                employee = new Employee();
                employee.setFirstName("Guillaume");
                employee.setLastName("Aujet");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw ex;
            }

            version1 = employee.getVersion();

            try {
                beginTransaction(em);
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.setLastName("Auger");
                commitTransaction(em);

                employee = em.find(Employee.class, employee.getId());
                Assert.assertTrue("The version was not updated on the pessimistic lock", version1.intValue() < employee.getVersion().intValue());
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw ex;
            }

            //Verify if the entity has been updated correctly by using PESSIMISTIC_FORCE_INCREMENT as PESSIMISTIC_WRITE
            try {
                beginTransaction(em);
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                rollbackTransaction(em);

                assertEquals("The last name is not updated by using PESSIMISTIC_FORCE_INCREMENT", "Auger", queryResult.getLastName());
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        }
    }

    public void testQueryPESSIMISTIC_READ_TIMEOUTLock() {
        ServerSession session = JUnitTestCase.getServerSession();

        // Cannot create parallel entity managers in the server.
        // Lock timeout only supported on Oracle.
        if (! isOnServer() && session.getPlatform().supportsWaitForUpdate()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select e from Employee e").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;

            try {
                beginTransaction(em);

                // Query by primary key.
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                query.setLockMode(LockModeType.PESSIMISTIC_READ);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.toString();

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);

                    // Query by primary key.
                    Query query2 = em2.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC_READ);
                    query2.setHint(QueryHints.REFRESH, true);
                    query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5000);
                    query2.setParameter("id", employee.getId());
                    query2.setParameter("firstName", employee.getFirstName());
                    Employee employee2 = (Employee) query2.getSingleResult();
                    employee2.setFirstName("Invalid Lock Employee");
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeOutException = ex;
                    } else {
                        throw ex;
                    }
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", lockTimeOutException);
        }
    }

    public void testQueryPESSIMISTIC_WRITE_TIMEOUTLock() {
        ServerSession session = JUnitTestCase.getServerSession();

        // Cannot create parallel entity managers in the server.
        // Lock timeout only supported on Oracle.
        if (! isOnServer() && session.getPlatform().supportsWaitForUpdate()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select e from Employee e").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;

            try {
                beginTransaction(em);

                // Query by primary key.
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.toString();

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);

                    // Query by primary key.
                    Query query2 = em2.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                    query2.setHint(QueryHints.REFRESH, true);
                    query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5000);
                    query2.setParameter("id", employee.getId());
                    query2.setParameter("firstName", employee.getFirstName());
                    Employee employee2 = (Employee) query2.getSingleResult();
                    employee2.setFirstName("Invalid Lock Employee");
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeOutException = ex;
                    } else {
                        throw ex;
                    }
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", lockTimeOutException);
        }
    }

    public void testPESSIMISTIC_LockWithDefaultTimeOutUnit() {
        ServerSession session = JUnitTestCase.getServerSession();

        // Cannot create parallel entity managers in the server.
        // Lock timeout only supported on Oracle.
        if (! isOnServer() && session.getPlatform().supportsWaitForUpdate()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select e from Employee e").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;

            try {
                beginTransaction(em);

                // Query by primary key.
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                query.setLockMode(LockModeType.PESSIMISTIC_READ);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                //Set timeout for 2000 milliseconds (2 seconds)
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 2000);
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.toString();

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);

                    // Query by primary key.
                    Query query2 = em2.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC_READ);
                    query2.setHint(QueryHints.REFRESH, true);
                    query2.setParameter("id", employee.getId());
                    query2.setParameter("firstName", employee.getFirstName());

                    // sleep for 2 seconds (2000 milliseconds) to allow the first lock to timeout
                    // if a LockTimeoutException is thrown, the first lock must have set for longer than expected
                    try {
                        Thread.sleep(2000);
                    } catch(Exception e){}

                    Employee employee2 = (Employee) query2.getSingleResult();
                    employee2.setFirstName("Invalid Lock Employee");
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeOutException = ex;
                    } else {
                        throw ex;
                    }
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                rollbackTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNull("A javax.persistence.LockTimeoutException was unexpectedly thrown", lockTimeOutException);
        }
    }

    public void testPESSIMISTIC_LockWithSecondsTimeOutUnit() {
        ServerSession session = JUnitTestCase.getServerSession();

        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && session.getPlatform().supportsWaitForUpdate()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select e from Employee e").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;

            try {
                beginTransaction(em);

                // Query by primary key.
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                query.setLockMode(LockModeType.PESSIMISTIC_READ);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                //Set timeout for 2 seconds
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 2);
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT_UNIT, "SECONDS");
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.toString();

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);

                    // Query by primary key.
                    Query query2 = em2.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC_READ);
                    query2.setHint(QueryHints.REFRESH, true);
                    query2.setParameter("id", employee.getId());
                    query2.setParameter("firstName", employee.getFirstName());

                    // only sleep for 1 second (1000 milliseconds)
                    // a LockTimeoutException is expected to be thrown since the lock timeout should still be in effect
                    try {
                        Thread.sleep(1000);
                    } catch(Exception e){}

                    Employee employee2 = (Employee) query2.getSingleResult();
                    employee2.setFirstName("Invalid Lock Employee");
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeOutException = ex;
                    } else {
                        throw ex;
                    }
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                rollbackTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("A javax.persistence.LockTimeoutException was expected to be thrown", lockTimeOutException);
        }
    }
    
    public void testLockWithSecondaryTable() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isHANA()) {
            // HANA currently doesn't support pessimistic locking with queries on multiple tables
            // feature is under development (see bug 384129), but test should be skipped for the time being
            return;
        }
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Exception pessimisticLockException = null;

            try {
                beginTransaction(em);

                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);

                    List employees2 = em2.createQuery("Select e from Employee e").getResultList();
                    Employee employee2 = (Employee) employees2.get(0);

                    // Find all the employees and lock them.
                    List employees = em.createQuery("Select e from Employee e").setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
                    Employee employee = (Employee) employees.get(0);
                    employee.setSalary(90000);

                    HashMap properties = new HashMap();
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.lock(employee2, LockModeType.PESSIMISTIC_WRITE, properties);
                    employee2.setSalary(100000);
                    commitTransaction(em2);
                } catch (PessimisticLockException ex) {
                        pessimisticLockException = ex;
                } finally {
                    closeEntityManagerAndTransaction(em2);
                }

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            Assert.assertNotNull("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException);
        }
    }

    public void testVersionChangeWithReadLock() {
        if (isSelectForUpateNoWaitSupported()){
            Employee employee = null;
            Integer version1;

            EntityManager em = createEntityManager();
            beginTransaction(em);

            try {
                employee = new Employee();
                employee.setFirstName("Version Change");
                employee.setLastName("Readlock");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }

                closeEntityManager(em);
                throw ex;
            }

            version1 = employee.getVersion();

            try {
                beginTransaction(em);
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_READ);
                query.setHint(QueryHints.REFRESH, true);
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.setLastName("Burger");
                commitTransaction(em);

                employee = em.find(Employee.class, employee.getId());

                Assert.assertTrue("The version was not updated on the pessimistic read lock.", version1.intValue() < employee.getVersion().intValue());
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }
        }
    }

    public void testNamedQueryAnnotationOverwritePersistenceXML() throws Exception {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Query query = em.createNamedQuery("findAllEmployeesByIdAndFirstName");
            Map<String, Object> hints = query.getHints();

            Assert.assertEquals("query hint timeout is incorrect", "15000", hints.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testVersionChangeWithWriteLock() {
        if (isSelectForUpateNoWaitSupported()) {
            Employee employee = null;
            Integer version1;

            EntityManager em = createEntityManager();
            beginTransaction(em);

            try {
                employee = new Employee();
                employee.setFirstName("Version Change");
                employee.setLastName("Writelock");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }

                closeEntityManager(em);
                throw ex;
            }

            version1 = employee.getVersion();

            try {
                beginTransaction(em);
                Query query = em.createQuery("Select e from Employee e where e.id = :id and e.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_WRITE);
                query.setHint(QueryHints.REFRESH, true);
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.setLastName("Burger");
                commitTransaction(em);

                employee = em.find(Employee.class, employee.getId());

                Assert.assertTrue("The version was not updated on the pessimistic write lock.", version1.intValue() < employee.getVersion().intValue());
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw ex;
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

        }
    }

    /**
     * Test batch fetching.
     */
    public void testBatchFetchingIN() {
        testBatchFetching(BatchFetchType.IN, 1000);
    }

    /**
     * Test batch fetching.
     */
    public void testBatchFetchingIN5() {
        testBatchFetching(BatchFetchType.IN, 5);
    }

    /**
     * Test batch fetching.
     */
    public void testBatchFetchingIN2() {
        testBatchFetching(BatchFetchType.IN, 2);
    }

    /**
     * Test batch fetching.
     */
    public void testBatchFetchingJOIN() {
        testBatchFetching(BatchFetchType.JOIN, 0);
    }

    /**
     * Test batch fetching.
     */
    public void testBatchFetchingEXISTS() {
        testBatchFetching(BatchFetchType.EXISTS, 0);
    }

    /**
     * Test batch fetching.
     */
    public void testBatchFetching(BatchFetchType type, int size) {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e where e.gender = :g1 or e.gender = :g2");
            query.setHint(QueryHints.BATCH_SIZE, size);
            query.setHint(QueryHints.BATCH_TYPE, type);
            query.setHint(QueryHints.BATCH, "e.address");
            query.setHint(QueryHints.BATCH, "e.manager");
            query.setHint(QueryHints.BATCH, "e.projects");
            query.setHint(QueryHints.BATCH, "e.managedEmployees");
            query.setHint(QueryHints.BATCH, "e.responsibilities");
            query.setHint(QueryHints.BATCH, "e.dealers");
            query.setHint(QueryHints.BATCH, "e.phoneNumbers");
            //query.setHint(QueryHints.BATCH, "e.department"); is join fetched already.
            query.setHint(QueryHints.BATCH, "e.workWeek");
            query.setParameter("g1", Gender.Male);
            query.setParameter("g2", Gender.Female);
            List<Employee> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            for (Employee employee : results) {
                employee.getAddress();
                employee.getManager();
                employee.getProjects().size();
                employee.getManagedEmployees().size();
                employee.getResponsibilities().size();
                employee.getDealers().size();
                employee.getPhoneNumbers().size();
                employee.getWorkWeek().size();
            }
            int queries = 11;
            if (size == 2) {
                queries = 55;
            } else if (size == 5) {
                queries = 30;
            }

            if (isWeavingEnabled()) {
                Assert.assertFalse("Number of statements is incorrect", counter.getSqlStatements().size() > queries);
            }
            if (type != BatchFetchType.JOIN) {
                for (String sql : counter.getSqlStatements()) {
                    if(sql.indexOf("PROJ_TYPE") == -1) {
                        Assert.assertTrue("SQL (" + sql + ") should not have contained 'DISTINCT'", sql.indexOf("DISTINCT") == -1);
                    }
                }
            }

            clearCache();
            for (Employee employee : results) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching with IN and a partial cache.
     */
    public void testBatchFetchingINCache() {
        testBatchFetchingINCache(1);
        testBatchFetchingINCache(2);
        testBatchFetchingINCache(3);
        testBatchFetchingINCache(4);
        testBatchFetchingINCache(5);
    }

    /**
     * Test batch fetching with IN and a partial cache.
     * This tests that the paging is working correctly for different page sizes and empty and disjoint pages.
     */
    public void testBatchFetchingINCache(int batchSize) {
        clearCache();
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select a from Address a where exists (Select e from Employee e where e.address = a and e.gender = :g1)");
            query.setParameter("g1", Gender.Female);
            query.getResultList();
            query = em.createQuery("Select e from Employee e where e.gender = :g1 or e.gender = :g2");
            query.setHint(QueryHints.BATCH_SIZE, batchSize);
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.BATCH, "e.address");
            query.setHint(QueryHints.BATCH, "e.manager");
            query.setParameter("g1", Gender.Male);
            query.setParameter("g2", Gender.Female);
            List<Employee> results = query.getResultList();
            for (Employee employee : results) {
                employee.getAddress();
                employee.getManager();
            }
            clearCache();
            for (Employee employee : results) {
                verifyObject(employee);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test join fetching.
     */
    public void testJoinFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e where e.gender = :g1 or e.gender = :g2");
            query.setHint(QueryHints.LEFT_FETCH, "e.address");
            //query.setHint(QueryHints.LEFT_FETCH, "e.manager"); - has eagers
            //query.setHint(QueryHints.LEFT_FETCH, "e.projects"); - has eagers
            //query.setHint(QueryHints.LEFT_FETCH, "e.managedEmployees"); - has eagers
            query.setHint(QueryHints.LEFT_FETCH, "e.responsibilities");
            query.setHint(QueryHints.LEFT_FETCH, "e.dealers");
            query.setHint(QueryHints.LEFT_FETCH, "e.phoneNumbers");
            //query.setHint(QueryHints.BATCH, "e.department"); is join fetched already.
            query.setHint(QueryHints.LEFT_FETCH, "e.workWeek");
            query.setParameter("g1", Gender.Male);
            query.setParameter("g2", Gender.Female);
            List<Employee> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of statements is incorrect", 1, counter.getSqlStatements().size());
            }

            for (Employee employee : results) {
                employee.getAddress();
                employee.getResponsibilities().size();
                employee.getDealers().size();
                employee.getPhoneNumbers().size();
                employee.getWorkWeek().size();
            }

            if (isWeavingEnabled()) {
                Assert.assertFalse("Number of statements is incorrect", counter.getSqlStatements().size() > 1);
            }

            clearCache();
            for (Employee employee : results) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching of maps.
     */
    public void testBasicMapBatchFetchingJOIN() {
        testBasicMapBatchFetching(BatchFetchType.JOIN, 0);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testBasicMapBatchFetchingIN() {
        testBasicMapBatchFetching(BatchFetchType.IN, 100);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testBasicMapBatchFetchingEXISTS() {
        testBasicMapBatchFetching(BatchFetchType.EXISTS, 0);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetchingJOIN() {
        testMapBatchFetching(BatchFetchType.JOIN, 0);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetchingIN() {
        testMapBatchFetching(BatchFetchType.IN, 100);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetchingEXISTS() {
        testMapBatchFetching(BatchFetchType.EXISTS, 0);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testBasicMapBatchFetching(BatchFetchType type, int size) {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select b from Buyer b where b.name like :name");
            query.setHint(QueryHints.BATCH_SIZE, size);
            query.setHint(QueryHints.BATCH_TYPE, type);
            query.setHint(QueryHints.BATCH, "e.creditCards");
            query.setHint(QueryHints.BATCH, "e.creditLines");
            query.setParameter("name", "%Gold%");
            List<Buyer> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 3, counter.getSqlStatements().size());
            }

            for (Buyer buyer : results) {
                buyer.getCreditCards().size();
                buyer.getCreditLines().size();
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 4, counter.getSqlStatements().size());
            }

            clearCache();
            for (Buyer buyer : results) {
                verifyObject(buyer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test join fetching of maps.
     */
    public void testBasicMapJoinFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select b from Buyer b where b.name like :name");
            query.setHint(QueryHints.FETCH, "e.creditCards");
            query.setHint(QueryHints.FETCH, "e.creditLines");
            query.setParameter("name", "%Gold%");
            List<Buyer> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            for (Buyer buyer : results) {
                buyer.getCreditCards().size();
                buyer.getCreditLines().size();
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            clearCache();
            for (Buyer buyer : results) {
                verifyObject(buyer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test join fetching of maps.
     */
    public void testBasicMapLeftJoinFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select b from Buyer b where b.name like :name");
            query.setHint(QueryHints.LEFT_FETCH, "e.creditCards");
            query.setHint(QueryHints.LEFT_FETCH, "e.creditLines");
            query.setParameter("name", "%Gold%");
            List<Buyer> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            boolean found = false;
            for (Buyer buyer : results) {
                found = found || buyer.getCreditCards().size() > 0;
                found = found || buyer.getCreditLines().size() > 0;
            }

            assertTrue("No data to join.", found);
            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            clearCache();
            for (Buyer buyer : results) {
                verifyObject(buyer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetching(BatchFetchType type, int size) {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select c from Customer c");
            query.setHint(QueryHints.BATCH_SIZE, size);
            query.setHint(QueryHints.BATCH_TYPE, type);
            query.setHint(QueryHints.BATCH, "e.CSInteractions");
            query.setHint(QueryHints.BATCH, "e.CCustomers");
            List<Customer> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertTrue("Number of queries is incorrect", counter.getSqlStatements().size() <= 3);
            }

            int queries = 5;
            for (Customer customer : results) {
                queries = queries + customer.getCSInteractions().size();
            }

            if (isWeavingEnabled()) {
                Assert.assertTrue("Number of queries is incorrect", counter.getSqlStatements().size() <= queries);
            }

            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test load groups.
     */
    public void testLoadGroup() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select c from Customer c");
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CSInteractions");
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CCustomers");
            List<Customer> results = query.getResultList();
            counter.getSqlStatements().clear();
            for (Customer customer : results) {
                customer.getCSInteractions().size();
            }

            Assert.assertEquals("Load group should have loaded attributes", 0, counter.getSqlStatements().size());

            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test concurrent load groups.
     */
    public void testConcurrentLoadGroup() {
        clearCache();
        boolean concurrent = getDatabaseSession().isConcurrent();
        getDatabaseSession().setIsConcurrent(true);
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select c from Customer c");
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CSInteractions");
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CCustomers");
            List<Customer> results = query.getResultList();
            counter.getSqlStatements().clear();
            for (Customer customer : results) {
                customer.getCSInteractions().size();
            }

            Assert.assertEquals("Load group should have loaded attributes", 0, counter.getSqlStatements().size());

            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
            getDatabaseSession().setIsConcurrent(concurrent);
        }
    }

    /**
     * Test join fetching of maps.
     */
    public void testMapJoinFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select c from Customer c");
            query.setHint(QueryHints.LEFT_FETCH, "e.CSInteractions");
            query.setHint(QueryHints.LEFT_FETCH, "e.CCustomers");
            List<Customer> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertTrue("Number of queries is incorrect", counter.getSqlStatements().size() <= 3);
            }

            int queries = 1;
            for (Customer customer : results) {
                queries = queries + customer.getCSInteractions().size();
            }

            Assert.assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled()) {
                Assert.assertTrue("Number of queries is incorrect", counter.getSqlStatements().size() <= queries);
            }

            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test join fetching of maps.
     */
    public void testMapKeyJoinFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select d from ADV_DEPT d");
            query.setHint(QueryHints.LEFT_FETCH, "d.equipment");
            query.setHint(QueryHints.LEFT_FETCH, "d.employees");
            query.setHint(QueryHints.LEFT_FETCH, "d.managers");
            List<Department> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            int queries = 1;
            for (Department department : results) {
                queries = queries + department.getEquipment().size();
                department.getEmployees().size();
                department.getManagers().size();
            }

            Assert.assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            clearCache();
            for (Department department : results) {
                verifyObject(department);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test join fetching of maps.
     */
    public void testMapKeyBatchFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select d from ADV_DEPT d");
            query.setHint(QueryHints.BATCH, "d.equipment");
            query.setHint(QueryHints.BATCH, "d.employees");
            query.setHint(QueryHints.BATCH, "d.managers");
            List<Department> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            int queries = 1;
            for (Department department : results) {
                queries = queries + department.getEquipment().size();
                department.getEmployees().size();
                department.getManagers().size();
            }

            Assert.assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 4, counter.getSqlStatements().size());
            }

            clearCache();
            for (Department department : results) {
                verifyObject(department);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching using first/max results.
     */
    public void testBatchFetchingPagination() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e");
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.BATCH_SIZE, 5);
            query.setHint(QueryHints.BATCH, "e.address");
            query.setHint(QueryHints.BATCH, "e.manager");
            query.setFirstResult(5);
            query.setMaxResults(5);
            List<Employee> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }
            Assert.assertEquals("Incorrect number of results", 5, results.size());

            for (Employee employee : results) {
                employee.getAddress();
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            clearCache();
            for (Employee employee : results) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test join fetching using first/max results.
     */
    public void testJoinFetchingPagination() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e");
            query.setHint(QueryHints.LEFT_FETCH, "e.address");
            query.setHint(QueryHints.LEFT_FETCH, "e.phoneNumbers");
            query.setFirstResult(5);
            query.setMaxResults(5);
            List<Employee> results = query.getResultList();
            int nExpectedStatements = 3;
            if (usesSOP()) {
                // In SOP case there are no sql to read PhoneNumbers - they are read from sopObject instead.
                nExpectedStatements = 1;
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", nExpectedStatements, counter.getSqlStatements().size());
            }
            Assert.assertTrue("Incorrect number of results", results.size() <= 5);

            for (Employee employee : results) {
                employee.getAddress();
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", nExpectedStatements, counter.getSqlStatements().size());
            }

            clearCache();
            counter.remove();
            counter = null;
            for (Employee employee : results) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching using read object query.
     */
    public void testBatchFetchingReadObject() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e");
            query.setHint(QueryHints.BATCH, "e.managedEmployees");
            query.setHint(QueryHints.BATCH, "e.managedEmployees.address");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
            Employee result = (Employee)query.getSingleResult();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            for (Employee employee : result.getManagedEmployees()) {
                employee.getAddress();
            }

            if (isWeavingEnabled()) {
                Assert.assertTrue("Number of queries is incorrect", counter.getSqlStatements().size() <= 3);
            }

            clearCache();
            verifyObject(result);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching with outer joins.
     */
    public void testBatchFetchOuterJoin() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select p from Person p left join p.bestFriend f order by f.title");
            query.setHint(QueryHints.BATCH, "p.bestFriend");
            List<Person> result = query.getResultList();

            Assert.assertEquals("Incorrect number of results", 8, result.size());
            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            for (Person person : result) {
                person.getBestFriend();
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            clearCache();
            for (Person person : result) {
                verifyObject(person);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching on inheritance.
     */
    public void testBatchFetchingInheritance() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select p from Person p");
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.BATCH, "p.company");
            List<Person> result = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 5, counter.getSqlStatements().size());
            }

            for (Person person : result) {
                if (person instanceof Engineer) {
                    ((Engineer)person).getCompany();
                }
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 5, counter.getSqlStatements().size());
            }

            clearCache();
            for (Person person : result) {
                verifyObject(person);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching using first/max results.
     */
    public void testBatchFetchingPagination2() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e");
            query.setHint(QueryHints.BATCH, "e.address");
            query.setHint(QueryHints.BATCH, "e.manager");
            query.setFirstResult(5);
            query.setMaxResults(5);
            List<Employee> results = query.getResultList();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }
            Assert.assertEquals("Incorrect number of results", 5, results.size());

            for (Employee employee : results) {
                employee.getAddress();
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 2, counter.getSqlStatements().size());
            }

            clearCache();
            for (Employee employee : results) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test batch fetching using a cursor.
     */
    public void testBatchFetchingCursor() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e");
            query.setHint(QueryHints.BATCH_TYPE, String.valueOf(BatchFetchType.IN)); // Test as String as well.
            query.setHint(QueryHints.BATCH, "e.address");
            query.setHint(QueryHints.BATCH, "e.manager");
            query.setHint(QueryHints.CURSOR_PAGE_SIZE, 5);
            query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 2);
            Iterator<Employee> results = (Iterator<Employee>)query.getSingleResult();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            int count = 0;
            List<Employee> employees = new ArrayList<Employee>();
            while (results.hasNext()) {
                Employee employee = results.next();
                employee.getAddress();
                count++;
            }

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", count/5 + 2, counter.getSqlStatements().size());
            }

            clearCache();
            for (Employee employee : employees) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test join fetching using a cursor.
     */
    public void testJoinFetchingCursor() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            Query query = em.createQuery("Select e from Employee e order by e.id"); // Currently need to order for multiple 1-m joins.
            query.setHint(QueryHints.LEFT_FETCH, "e.address");
            //query.setHint(QueryHints.LEFT_FETCH, "e.manager"); - has eagers
            //query.setHint(QueryHints.LEFT_FETCH, "e.projects"); - has eagers
            //query.setHint(QueryHints.LEFT_FETCH, "e.managedEmployees"); - has eagers
            query.setHint(QueryHints.LEFT_FETCH, "e.responsibilities");
            query.setHint(QueryHints.LEFT_FETCH, "e.dealers");
            query.setHint(QueryHints.LEFT_FETCH, "e.phoneNumbers");
            //query.setHint(QueryHints.BATCH, "e.department"); is join fetched already.
            query.setHint(QueryHints.LEFT_FETCH, "e.workWeek");
            query.setHint(QueryHints.CURSOR_PAGE_SIZE, 5);
            query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 2);
            Iterator<Employee> results = (Iterator<Employee>)query.getSingleResult();

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", 1, counter.getSqlStatements().size());
            }

            int count = 0;
            List<Employee> employees = new ArrayList<Employee>();
            while (results.hasNext()) {
                Employee employee = results.next();
                employees.add(employee);
                employee.getAddress();
                employee.getResponsibilities().size();
                employee.getDealers().size();
                employee.getPhoneNumbers().size();
                employee.getWorkWeek().size();
                count++;
            }
            int queries = 1;

            if (isWeavingEnabled()) {
                Assert.assertEquals("Number of queries is incorrect", queries, counter.getSqlStatements().size());
            }

            clearCache();
            for (Employee employee : employees) {
                verifyObject(employee);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test cache hits on pk JPQL queries.
     */
    public void testJPQLCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.
            Query query = em.createQuery("Select e from Employee e");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.id = :id");
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();

            Assert.assertEquals("Employees are not equal", employee.getId(), queryResult.getId());
            Assert.assertEquals("Cache hit should not occur", 0, counter.getSqlStatements().size());
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test cache indexes.
     */
    public void testCacheIndexes() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        Buyer buyer = null;
        Employee employee = null;
        String lastName = null;
        try {
            // Load an employee into the cache.
            Query query = em.createQuery("Select e from Employee e where e.lastName = 'Chanley'");
            List result = query.getResultList();
            employee = (Employee)result.get(0);
            lastName = employee.getLastName();

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select e from Employee e where e.firstName = :firstName and e.lastName = :lastName");
            query.setParameter("firstName", employee.getFirstName());
            query.setParameter("lastName", employee.getLastName());
            counter.getSqlStatements().clear();
            Employee queryResult = (Employee)query.getSingleResult();

            Assert.assertEquals("Employees are not equal", employee, queryResult);
            Assert.assertEquals("Cache hit should not occur", 0, counter.getSqlStatements().size());

            employee.setLastName("fail");
            commitTransaction(em);
            query = em.createQuery("Select e from Employee e where e.firstName = :firstName and e.lastName = :lastName");
            query.setParameter("firstName", employee.getFirstName());
            query.setParameter("lastName", lastName);
            counter.getSqlStatements().clear();
            try {
                queryResult = null;
                queryResult = (Employee)query.getSingleResult();
            } catch (NoResultException ignore) {}

            Assert.assertNull("Employees should not be found", queryResult);
            Assert.assertNotEquals("Cache hit should not occur", 0, counter.getSqlStatements().size());

            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            buyer = new Buyer();
            buyer.setName("index");
            buyer.setDescription("description");
            em.persist(buyer);
            commitTransaction(em);
            query = em.createQuery("Select b from Buyer b where b.name = :name");
            query.setParameter("name", buyer.getName());
            counter.getSqlStatements().clear();
            Buyer queryResult2 = (Buyer)query.getSingleResult();

            Assert.assertEquals("Buyers are not equal", buyer.getName(), queryResult2.getName());
            Assert.assertEquals("Cache hit do not occur", 0, counter.getSqlStatements().size());
        } finally {
            if (counter != null) {
                counter.remove();
            }
            closeEntityManagerAndTransaction(em);
            em = createEntityManager();
            beginTransaction(em);
            if (buyer != null) {
                buyer = em.find(Buyer.class, buyer.getId());
                em.remove(buyer);
            }
            if (employee != null) {
                Employee reset = em.find(Employee.class, employee.getId());
                reset.setLastName(lastName);
            }
            commitTransaction(em);
            closeEntityManagerAndTransaction(em);
        }
    }

    public void testQueryPESSIMISTICLockWithLimit() throws InterruptedException {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            TypedQuery<Employee> query = em.createQuery("Select e from Employee e where e.lastName != :lastName", Employee.class);
            query.setParameter("lastName", "Chanley");
            query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock);
            query.setFirstResult(5);
            query.setMaxResults(2);
            List<Employee> results = query.getResultList();
            final Employee e = results.get(0);
            final String name = e.getFirstName();
            Assert.assertEquals("Incorrect number of objects returned", 2, results.size());
            clearCache();

            final EntityManager em2 = createEntityManager();
            try {
                // P2 (Non-repeatable read)
                Runnable runnable = new Runnable() {
                    @Override
                        public void run() {
                        try {
                            beginTransaction(em2);
                            TypedQuery<Employee> query2 = em2.createQuery("select e from Employee e where e.id = :id", Employee.class);
                            query2.setParameter("id", e.getId());
                            query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5000);
                            Employee emp = query2.getSingleResult(); // might wait for lock to be released
                            emp.setFirstName("Trouba");
                            commitTransaction(em2); // might wait for lock to be released
                        } catch (javax.persistence.RollbackException ex) {
                            if (!ex.getMessage().contains("org.eclipse.persistence.exceptions.DatabaseException")) {
                                ex.printStackTrace();
                                fail("Expected: org.eclipse.persistence.exceptions.DatabaseException, but was: " + ex);
                            }
                        }
                    }
                };

                Thread t2 = new Thread(runnable);
                t2.start();
                Thread.sleep(1000); // allow t2 to attempt update
                em.refresh(e);

                Assert.assertEquals("pessimistic lock failed: parallel transaction modified locked entity (non-repeatable read)", name, e.getFirstName());

                rollbackTransaction(em); // release lock
                t2.join(); // wait until t2 finished
            } finally {
                if (isTransactionActive(em2)) {
                    rollbackTransaction(em2);
                }
                closeEntityManager(em2);
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
