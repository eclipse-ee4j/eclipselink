/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.criteria;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.DatabaseSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;


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
        //suite.addTest(new AdvancedQueryTestSuite("testQueryPrimaryKeyCacheHits"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryExactPrimaryKeyCacheHits"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryTypeCacheHits"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryCache"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryREADLock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryWRITELock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryOPTIMISTICLock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryOPTIMISTIC_FORCE_INCREMENTLock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_READLock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_WRITELock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_FORCE_INCREMENTLock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_READ_TIMEOUTLock"));
        //suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_WRITE_TIMEOUTLock"));        
        suite.addTest(new AdvancedQueryTestSuite("testObjectResultType"));
        //suite.addTest(new AdvancedQueryTestSuite("testNativeResultType"));
        suite.addTest(new AdvancedQueryTestSuite("testCursors"));
        //suite.addTest(new AdvancedQueryTestSuite("testFetchGroups"));
        //suite.addTest(new AdvancedQueryTestSuite("testMultipleNamedJoinFetchs"));
        //suite.addTest(new AdvancedQueryTestSuite("testNativeQueryTransactions"));
        
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
    }
    
    /**
     * Test that a cache hit will occur on a primary key query.
     */
    public void testUntypedPath() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            CriteriaQuery<Employee> empCQ= qb.createQuery(Employee.class);
            Root<Employee> from = empCQ.from(Employee.class);
            Join<Employee, Address> join = from.join("address");
            empCQ.where(qb.greaterThanOrEqualTo(from.get("id").as(Integer.class), qb.literal(5)));
            query = em.createQuery(empCQ);
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
     * Test that a cache hit will occur on a primary key query.
     */
    public void testTupleQuery() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.
            CriteriaBuilder qb = em.getCriteriaBuilder();

            Query query = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            CriteriaQuery<Tuple> cq = qb.createQuery(Tuple.class);
            Root from = cq.from(Employee.class);
            cq.multiselect(from.get("id"), from.get("firstName"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            TypedQuery<Tuple> typedQuery = em.createQuery(cq);

            typedQuery.setParameter("id", employee.getId());
            typedQuery.setParameter("firstName", employee.getFirstName());

            Tuple queryResult = typedQuery.getSingleResult();
            assertTrue("Query Results do not match selection", queryResult.get(0).equals(employee.getId()) && queryResult.get(1).equals(employee.getFirstName()));
        } finally {
            rollbackTransaction(em);
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
            Query query = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            query.setHint(QueryHints.CURSOR, true);
            query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 2);
            query.setHint(QueryHints.CURSOR_PAGE_SIZE, 5);
            query.setHint(QueryHints.CURSOR_SIZE, "Select count(*) from CMP3_EMPLOYEE");
            Cursor cursor = (Cursor)query.getSingleResult();
            cursor.nextElement();
            cursor.size();
            cursor.close();
            
            // Test cursor result API.
            JpaQuery jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            jpaQuery.setHint(QueryHints.CURSOR, true);
            cursor = jpaQuery.getResultCursor();
            cursor.nextElement();
            cursor.size();
            cursor.close();
            
            // Test scrollable cursor.
            jpaQuery = (JpaQuery)((EntityManager)em.getDelegate()).createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
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
            Query query = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class));
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);
            
            CriteriaBuilder qb = em.getCriteriaBuilder();
            // Test multi object, as an array.
            CriteriaQuery<?> cq = qb.createQuery(Object[].class);
            Root<Employee> from = cq.from(Employee.class);
            cq.multiselect(from, from.get("address"), from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")),qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
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
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), (qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName")))));
            query = em.createQuery(cq);
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
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.alias("employee"), from.get("address").alias("address"), from.get("id").alias("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
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
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id").alias("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
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
            cq = qb.createQuery(Employee.class);
            from = cq.from(Employee.class);
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.QUERY_TYPE, QueryType.Report);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            arrayResult = (Object[])query.getSingleResult();
            if (arrayResult[0] != employee) {
                fail("Array result not correct: " + arrayResult);
            }
            
            // Test single object, as value.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
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
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"), from.get("firstName"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
            query.setHint(QueryHints.RESULT_TYPE, ResultType.Value);
            query.setParameter("id", employee.getId());
            query.setParameter("firstName", employee.getFirstName());
            valueResult = query.getSingleResult();
            if (! valueResult.equals(employee.getId())) {
                fail("Value result not correct: " + valueResult);
            }
            
            // Test single object, as attribute.
            cq = qb.createQuery(Object[].class);
            from = cq.from(Employee.class);
            cq.multiselect(from.get("id"));
            cq.where(qb.and(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")), qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            query = em.createQuery(cq);
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
     * Test that a cache hit will occur on a primary key query.
     */
    public void testQueryExactPrimaryKeyCacheHits() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        QuerySQLTracker counter = null;
        try {
            // Load an employee into the cache.  
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(0);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")));
            query = em.createQuery(cq);
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
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName")));
            query = em.createQuery(cq);
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
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName")));
            query = em.createQuery(cq);
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
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Employee.class);
            Query query = em.createQuery(cq);
            List result = query.getResultList();
            Employee employee = (Employee)result.get(result.size() - 1);

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            cq = qb.createQuery(Employee.class);
             Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")));
            query = em.createQuery(cq);
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
            cq = qb.createQuery(Employee.class);
            from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")));
            query = em.createQuery(cq);
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
