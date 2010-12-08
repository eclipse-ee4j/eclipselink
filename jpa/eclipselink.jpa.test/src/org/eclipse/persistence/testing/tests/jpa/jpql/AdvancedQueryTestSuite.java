/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
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
import org.eclipse.persistence.testing.models.jpa.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.relationships.IsolatedItem;
import org.eclipse.persistence.testing.models.jpa.relationships.Mattel;
import org.eclipse.persistence.testing.models.jpa.relationships.Lego;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;


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
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testBasicMapLeftJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testMapJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testJoinFetchingCursor"));
        suite.addTest(new AdvancedQueryTestSuite("testJoinFetchingPagination"));
        suite.addTest(new AdvancedQueryTestSuite("testMapKeyJoinFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testMapKeyBatchFetching"));
        suite.addTest(new AdvancedQueryTestSuite("testJPQLCacheHits"));
        if (!isJPA10()) {
            suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_FORCE_INCREMENTLock"));
            suite.addTest(new AdvancedQueryTestSuite("testVersionChangeWithReadLock"));
            suite.addTest(new AdvancedQueryTestSuite("testVersionChangeWithWriteLock"));
            suite.addTest(new AdvancedQueryTestSuite("testNamedQueryAnnotationOverwritePersistenceXML"));
        }
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
            Query query = em.createQuery("Select employee from Employee employee");
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
            query = em.createQuery("Select employee from Employee employee where employee.id = :id");
            query.setHint(QueryHints.FETCH_GROUP_ATTRIBUTE, "firstName");
            query.setHint(QueryHints.FETCH_GROUP_ATTRIBUTE, "lastName");
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();
            if (counter.getSqlStatements().size() != 1) {
                fail("More than fetch group selected: " + counter.getSqlStatements());
            }
            queryResult.getGender();
            if (counter.getSqlStatements().size() != 2) {
                fail("Access to unfetch did not cause fetch: " + counter.getSqlStatements());
            }
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
            if (counter.getSqlStatements().size() != 1) {
                fail("More than join fetches selected: " + counter.getSqlStatements());
            }
            for (Employee each : result) {
                each.getAddress().getCity();
                each.getPhoneNumbers().size();
            }
            if (counter.getSqlStatements().size() != 1) {
                fail("Join fetches triggered query: " + counter.getSqlStatements());
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
     * Test cursored queries.
     */
    public void testCursors() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // Test cusored stream.
            Query query = em.createQuery("Select employee from Employee employee");
            query.setHint(QueryHints.CURSOR, true);
            query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 2);
            query.setHint(QueryHints.CURSOR_PAGE_SIZE, 5);
            query.setHint(QueryHints.CURSOR_SIZE, "Select count(*) from CMP3_EMPLOYEE");
            Cursor cursor = (Cursor)query.getSingleResult();
            cursor.nextElement();
            cursor.size();
            cursor.close();
            
            // Test cursor result API.
            JpaQuery jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery("Select employee from Employee employee");
            jpaQuery.setHint(QueryHints.CURSOR, true);
            cursor = jpaQuery.getResultCursor();
            cursor.nextElement();
            cursor.size();
            cursor.close();
            
            // Test scrollable cursor.
            jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery("Select employee from Employee employee");
            jpaQuery.setHint(QueryHints.SCROLLABLE_CURSOR, true);
            jpaQuery.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
            jpaQuery.setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.DEFAULT);
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
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Test multi object, as an array.
            query = em.createQuery("Select employee, employee.address, employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Object[] arrayResult = (Object[])query.getSingleResult();
            if ((arrayResult.length != 3) && (arrayResult[0] != employee) || (arrayResult[1] != employee.getAddress()) || (!arrayResult[2].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            List listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            if ((arrayResult.length != 3) || (arrayResult[0] != employee) || (arrayResult[1] != employee.getAddress()) || (!arrayResult[2].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test single object, as an array.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();
            if ((arrayResult.length != 1) || (!arrayResult[0].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            if ((arrayResult.length != 1) || (!arrayResult[0].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test multi object, as a Map.
            query = em.createQuery("Select employee, employee.address, employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Map mapResult = (Map)query.getSingleResult();
            if ((mapResult.size() != 3) ||(mapResult.get("employee") != employee) || (mapResult.get("address") != employee.getAddress()) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);
            if ((mapResult.size() != 3) ||(mapResult.get("employee") != employee) || (mapResult.get("address") != employee.getAddress()) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            
            // Test single object, as a Map.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            mapResult = (Map)query.getSingleResult();
            if ((mapResult.size() != 1) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);
            if ((mapResult.size() != 1) || (!mapResult.get("id").equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            
            // Test single object, as an array.
            query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.QUERY_TYPE, QueryType.Report);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();
            if (arrayResult[0] != employee) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test single object, as value.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            Object valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            listResult = query.getResultList();
            valueResult = listResult.get(0);
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            
            // Test multi object, as value.
            query = em.createQuery("Select employee.id, employee.firstName from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Value);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            
            // Test single object, as attribute.
            query = em.createQuery("Select employee.id from Employee employee where employee.id = :id and employee.firstName = :firstName");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Attribute);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            listResult = query.getResultList();
            valueResult = listResult.get(0);
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
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
            if ((arrayResult.length != 2) || (!arrayResult[0].equals(employee.getFirstName())) && (!arrayResult[1].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            List listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            if ((arrayResult.length != 2) || (!arrayResult[0].equals(employee.getFirstName())) && (!arrayResult[1].equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test single object, as an array.
            query = em.createNativeQuery("Select employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();
            if ((arrayResult.length != 1) || (!new Integer(((Number)arrayResult[0]).intValue()).equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            listResult = query.getResultList();
            arrayResult = (Object[])listResult.get(0);
            if ((arrayResult.length != 1) || (!new Integer(((Number)arrayResult[0]).intValue()).equals(employee.getId()))) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test multi object, as a Map.
            query = em.createNativeQuery("Select employee.F_NAME, employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            Map mapResult = (Map)query.getSingleResult();
            if ((mapResult.size() != 2) || (!mapResult.get("F_NAME").equals(employee.getFirstName())) || (!(new Integer(((Number)mapResult.get("EMP_ID")).intValue())).equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);
            if ((mapResult.size() != 2) || (!mapResult.get("F_NAME").equals(employee.getFirstName())) || (!(new Integer(((Number)mapResult.get("EMP_ID")).intValue())).equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            
            // Test single object, as a Map.
            query = em.createNativeQuery("Select employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            mapResult = (Map)query.getSingleResult();
            if ((mapResult.size() != 1) || (!(new Integer(((Number)mapResult.get("EMP_ID")).intValue())).equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            listResult = query.getResultList();
            mapResult = (Map)listResult.get(0);
            if ((mapResult.size() != 1) || (!(new Integer(((Number)mapResult.get("EMP_ID")).intValue())).equals(employee.getId()))) {
                fail("Map result not correct: " + mapResult);
            }
            
            // Test single object, as value.
            query = em.createNativeQuery("Select employee.EMP_ID from CMP3_EMPLOYEE employee where employee.EMP_ID = ? and employee.F_NAME = ?");
            query.setParameter(1, employee.getId());
            query.setParameter(2, employee.getFirstName());
            Object valueResult = query.getSingleResult();
            if (!(new Integer(((Number)valueResult).intValue())).equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            listResult = query.getResultList();
            valueResult = listResult.get(0);
            if (!(new Integer(((Number)valueResult).intValue())).equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
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
     * Test that the transaction is committed for a single native query transaction.
     */
    public void testNativeQueryTransactions() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.setFlushMode(FlushModeType.COMMIT);
            Query query = em.createNativeQuery("Update CMP3_EMPLOYEE set F_NAME = 'Bobo'");
            query.executeUpdate();
            commitTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            query = em.createNativeQuery("Select * from CMP3_EMPLOYEE where F_NAME = 'Bobo'");
            if (query.getResultList().size() == 0) {
                fail("Native query did not commit transaction.");
            }
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
            if (readQuery.getQueryResultsCachePolicy() == null) {
                fail("Query cache not set.");
            }
            if (readQuery.getQueryResultsCachePolicy().getMaximumCachedResults() != 200) {
                fail("Query cache size not set.");
            }
            if (!(readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy() instanceof TimeToLiveCacheInvalidationPolicy)) {
                fail("Query cache invalidation not set.");
            }
            if (((TimeToLiveCacheInvalidationPolicy)readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy()).getTimeToLive() != 50000) {
                fail("Query cache invalidation time not set.");
            }
            
            jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createNamedQuery("CachedTimeOfDayAllEmployees");
            readQuery = (ReadQuery)jpaQuery.getDatabaseQuery();
            if (readQuery.getQueryResultsCachePolicy() == null) {
                fail("Query cache not set.");
            }
            if (readQuery.getQueryResultsCachePolicy().getMaximumCachedResults() != 200) {
                fail("Query cache size not set.");
            }
            if (!(readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy() instanceof DailyCacheInvalidationPolicy)) {
                fail("Query cache invalidation not set.");
            }
            Calendar calendar = ((DailyCacheInvalidationPolicy)readQuery.getQueryResultsCachePolicy().getCacheInvalidationPolicy()).getExpiryTime();
            if ((calendar.get(Calendar.HOUR_OF_DAY) != 23 )
                    && (calendar.get(Calendar.MINUTE) != 59)
                    && (calendar.get(Calendar.SECOND) != 59)) {
                fail("Query cache invalidation time not set.");
            }

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            Query query = em.createNamedQuery("CachedAllEmployees");
            if (result.size() != query.getResultList().size()) {
                fail("List result size is not correct on 2nd cached query.");
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Query cache was not used: " + counter.getSqlStatements());
            }
            clearCache();
            // Preload uow to test query cache in uow.
            em.createQuery("Select e from Employee e").getResultList();
            query.getResultList();
            if (result.size() != query.getResultList().size()) {
                fail("List result size is not correct on cached query in unit of work.");
            }
            clearCache();
            // Also test query cache in early transaction.
            em.persist(new Address());
            em.flush();
            query.getResultList();
            if (result.size() != query.getResultList().size()) {
                fail("List result size is not correct on cached query in transaction.");
            }
            // Query by primary key.
            query = em.createNamedQuery("CachedNoEmployees");
            if (!query.getResultList().isEmpty()) {
                fail("List result size is not correct.");
            }
            // Also test empty query cache.
            counter.remove();
            counter = new QuerySQLTracker(getServerSession());
            if (!query.getResultList().isEmpty()) {
                fail("List result size is not correct.");
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Query cache was not used: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
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
        List result = em.createQuery("Select employee from Employee employee").getResultList();
        Employee employee = (Employee) result.get(0);
        Exception optimisticLockException = null;
       
        try {
            beginTransaction(em);
            
            // Query by primary key.
            Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
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
                closeEntityManager(em2);
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
        
        assertFalse("Proper exception not thrown when Query with LockModeType.READ is used.", optimisticLockException == null);
    }
    
    public void testQueryWRITELock(){
        // Cannot create parallel transactions.
        if (isOnServer()) {
            return;
        }

        // Load an employee into the cache.
        EntityManager em = createEntityManager();
        List result = em.createQuery("Select employee from Employee employee").getResultList();
        Employee employee = (Employee) result.get(0);
        Exception optimisticLockException = null;
        
        try {
            beginTransaction(em);
            
            // Query by primary key.
            Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
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
        }

        assertFalse("Proper exception not thrown when Query with LockModeType.WRITE is used.", optimisticLockException == null);
    }
    
    public void testQueryOPTIMISTICLock(){
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            // Load an employee into the cache.
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception optimisticLockException = null;
           
            try {
                beginTransaction(em);
                
                // Query by primary key.
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
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
                    closeEntityManager(em2);
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
            
            assertFalse("Proper exception not thrown when Query with LockModeType.READ is used.", optimisticLockException == null);
        }
    }
    
    public void testQueryOPTIMISTIC_FORCE_INCREMENTLock(){
        // Cannot create parallel transactions.
        if (! isOnServer()) {
            // Load an employee into the cache.
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception optimisticLockException = null;
            
            try {
                beginTransaction(em);
                
                // Query by primary key.
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
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
                } catch (RuntimeException ex) {
                    rollbackTransaction(em2);
                    closeEntityManager(em2);
                    throw ex;
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
            }
    
            assertFalse("Proper exception not thrown when Query with LockModeType.WRITE is used.", optimisticLockException == null);
        }
    }
    
    public void testQueryPESSIMISTIC_READLock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            PessimisticLockException pessimisticLockException = null;
        
            try {
                beginTransaction(em);
            
                EntityManager em2 = createEntityManager();
                try {
                    beginTransaction(em2);

                    List employees2 = em2.createQuery("Select employee from Employee employee").getResultList(); // 
                    Employee employee2 = (Employee) employees2.get(0);

                    // Find all the departments and lock them.
                    List employees = em.createQuery("Select employee from Employee employee").setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
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
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException == null);
        }
    }
    
    public void testQueryPESSIMISTIC_WRITELock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Exception pessimisticLockException = null;
        
            try {
                beginTransaction(em);
                
                EntityManager em2 = createEntityManager();
                try {
                    beginTransaction(em2);

                    List employees2 = em2.createQuery("Select employee from Employee employee").getResultList(); // 
                    Employee employee2 = (Employee) employees2.get(0);

                    // Find all the departments and lock them.
                    List employees = em.createQuery("Select employee from Employee employee").setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
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
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException == null);
        }
    }
    
    public void testQueryPESSIMISTIC_FORCE_INCREMENTLock() {
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
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.setLastName("Auger");
                commitTransaction(em);
                
                employee = em.find(Employee.class, employee.getId());
                assertTrue("The version was not updated on the pessimistic lock.", version1.intValue() < employee.getVersion().intValue());
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
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                rollbackTransaction(em);
                
                assertTrue("The last name is not updated by using PESSIMISTIC_FORCE_INCREMENT.", queryResult.getLastName().equals("Auger"));
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    public void testQueryPESSIMISTIC_READ_TIMEOUTLock() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        // Cannot create parallel entity managers in the server.
        // Lock timeout only supported on Oracle.
        if (! isOnServer() && session.getPlatform().isOracle()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;
           
            try {
                beginTransaction(em);
                
                // Query by primary key.
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
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
                    Query query2 = em2.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC_READ);
                    query2.setHint(QueryHints.REFRESH, true);
                    query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5);
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
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", lockTimeOutException == null);
        }
    }
    
    public void testQueryPESSIMISTIC_WRITE_TIMEOUTLock() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        // Cannot create parallel entity managers in the server.
        // Lock timeout only supported on Oracle.
        if (! isOnServer() && session.getPlatform().isOracle()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;
           
            try {
                beginTransaction(em);
                
                // Query by primary key.
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
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
                    Query query2 = em2.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                    query2.setHint(QueryHints.REFRESH, true);
                    query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5);
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
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", lockTimeOutException == null);
        }
    }

    public void testLockWithSecondaryTable() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            Exception pessimisticLockException = null;
        
            try {
                beginTransaction(em);
            
                EntityManager em2 = createEntityManager();
                
                try {
                    beginTransaction(em2);
                    
                    List employees2 = em2.createQuery("Select employee from Employee employee").getResultList();
                    Employee employee2 = (Employee) employees2.get(0);

                    // Find all the employees and lock them.
                    List employees = em.createQuery("Select employee from Employee employee").setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
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
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException == null);
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
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_READ);
                query.setHint(QueryHints.REFRESH, true);
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.setLastName("Burger");
                commitTransaction(em);

                employee = em.find(Employee.class, employee.getId());
                assertTrue("The version was not updated on the pessimistic read lock.", version1.intValue() < employee.getVersion().intValue());
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw ex;
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
            assertTrue("query hint", hints.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT).equals("15"));
            rollbackTransaction(em);
        } catch(Exception ex){
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally{
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
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName").setLockMode(LockModeType.PESSIMISTIC_WRITE);
                query.setHint(QueryHints.REFRESH, true);
                query.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
                queryResult.setLastName("Burger");
                commitTransaction(em);

                employee = em.find(Employee.class, employee.getId());
                assertTrue("The version was not updated on the pessimistic write lock.", version1.intValue() < employee.getVersion().intValue());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
            }
            for (Employee employee : results) {
                employee.getAddress();
                employee.getResponsibilities().size();
                employee.getDealers().size();
                employee.getPhoneNumbers().size();
                employee.getWorkWeek().size();
            }
            int queries = 1;
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 3) {
                fail("Should have been 3 query but was: " + counter.getSqlStatements().size());
            }
            for (Buyer buyer : results) {
                buyer.getCreditCards().size();
                buyer.getCreditLines().size();
            }
            int queries = 4;
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 2) {
                fail("Should have been 2 query but was: " + counter.getSqlStatements().size());
            }
            for (Buyer buyer : results) {
                buyer.getCreditCards().size();
                buyer.getCreditLines().size();
            }
            int queries = 2;
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 2) {
                fail("Should have been 2 query but was: " + counter.getSqlStatements().size());
            }
            boolean found = false;
            for (Buyer buyer : results) {
                found = found || buyer.getCreditCards().size() > 0;
                found = found || buyer.getCreditLines().size() > 0;
            }
            assertTrue("No data to join.", found);
            int queries = 2;
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 3) {
                fail("Should have been 3 queries but was: " + counter.getSqlStatements().size());
            }
            int queries = 5;
            for (Customer customer : results) {
                queries = queries + customer.getCSInteractions().size();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 3) {
                fail("Should have been 3 queries but was: " + counter.getSqlStatements().size());
            }
            int queries = 1;
            for (Customer customer : results) {
                queries = queries + customer.getCSInteractions().size();
            }
            assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 1) {
                fail("Should have been 13 queries but was: " + counter.getSqlStatements().size());
            }
            int queries = 1;
            for (Department department : results) {
                queries = queries + department.getEquipment().size();
                department.getEmployees().size();
                department.getManagers().size();
            }
            assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 1) {
                fail("Should have been " + 1 + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 1) {
                fail("Should have been 1 queries but was: " + counter.getSqlStatements().size());
            }
            int queries = 1;
            for (Department department : results) {
                queries = queries + department.getEquipment().size();
                department.getEmployees().size();
                department.getManagers().size();
            }
            assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 4) {
                fail("Should have been " + 4 + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
            }
            if (results.size() > 5) {
                fail("Should have only returned 5 objects but was: " + results.size());
            }
            for (Employee employee : results) {
                employee.getAddress();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 2) {
                fail("Should have been 2 queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 3) {
                fail("Should have been 3 query but was: " + counter.getSqlStatements().size());
            }
            if (results.size() > 5) {
                fail("Should have only returned 5 objects but was: " + results.size());
            }
            for (Employee employee : results) {
                employee.getAddress();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 3) {
                fail("Should have been 3 queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
            }
            for (Employee employee : result.getManagedEmployees()) {
                employee.getAddress();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 3) {
                fail("Should have been 3 queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 5) {
                fail("Should have been 5 query but was: " + counter.getSqlStatements().size());
            }
            for (Person person : result) {
                if (person instanceof Engineer) {
                    ((Engineer)person).getCompany();
                }
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 5) {
                fail("Should have been 5 queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
            }
            if (results.size() > 5) {
                fail("Should have only returned 5 objects but was: " + results.size());
            }
            for (Employee employee : results) {
                employee.getAddress();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 2) {
                fail("Should have been 2 queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
            }
            int count = 0;
            List<Employee> employees = new ArrayList<Employee>();
            while (results.hasNext()) {
                Employee employee = results.next();
                employee.getAddress();
                count++;
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > (count/5 + 2)) {
                fail("Should have been " + (count/5 + 2) + " queries but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 1) {
                fail("Should have been 1 query but was: " + counter.getSqlStatements().size());
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
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
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
            Query query = em.createQuery("Select employee from Employee employee");
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            query = em.createQuery("Select employee from Employee employee where employee.id = :id");
            query.setParameter("id", employee.getId());
            Employee queryResult = (Employee)query.getSingleResult();
            if (!queryResult.getId().equals(employee.getId())) {
                fail("Employees are not equal: " + employee + ", " + queryResult);
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Cache hit did not occur: " + counter.getSqlStatements());
            }
        } finally {
            rollbackTransaction(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }
}
