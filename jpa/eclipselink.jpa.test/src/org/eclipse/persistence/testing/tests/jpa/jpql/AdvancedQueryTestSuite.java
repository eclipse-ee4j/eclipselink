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

import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadQuery;
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
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTICLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTIC_FORCE_INCREMENTLock"));
        suite.addTest(new AdvancedQueryTestSuite("testQueryPESSIMISTICTIMEOUTLock"));
        
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
            ReadQuery readQuery = (ReadQuery)jpaQuery.getDatabaseQuery();
            readQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
            List result = jpaQuery.getResultList();

            // Count SQL.
            counter = new QuerySQLTracker(getServerSession());
            // Query by primary key.
            Query query = em.createNamedQuery("CachedAllEmployees");
            if (result.size() != query.getResultList().size()) {
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
    
    public void testQueryPESSIMISTICLock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createEntityManager();
            Exception pessimisticLockException = null;
        
            try {
                beginTransaction(em);
            
                // Find all the departments and lock them.
                List employees = em.createQuery("Select employee from Employee employee").setLockMode(LockModeType.PESSIMISTIC).getResultList();
                Employee employee = (Employee) employees.get(0);
                employee.setFirstName("New Pessimistic Employee");
            
                EntityManager em2 = createEntityManager();
            
                try {
                    beginTransaction(em2);
                
                    Employee employee2 = em2.find(Employee.class, employee.getId());
                    em2.lock(employee2, LockModeType.PESSIMISTIC);
                    employee2.setFirstName("Invalid Lock Employee");
                    
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.PessimisticLockException) {
                        pessimisticLockException = ex;
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
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", pessimisticLockException == null);
        }
    }
    
    public void testQueryPESSIMISTIC_FORCE_INCREMENTLock() {        
        Employee employee = null;
        Integer version1;
        Integer version2;
        
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
            
            assertTrue("The version was not updated on the pessimistic lock.", version1.intValue() < employee.getVersion().intValue());
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testQueryPESSIMISTICTIMEOUTLock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;
           
            try {
                beginTransaction(em);
                
                // Query by primary key.
                Query query = em.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
                query.setLockMode(LockModeType.PESSIMISTIC);
                query.setHint(QueryHints.REFRESH, true);
                query.setParameter("id", employee.getId());
                query.setParameter("firstName", employee.getFirstName());
                Employee queryResult = (Employee) query.getSingleResult();
            
                EntityManager em2 = createEntityManager();
            
                try {
                    beginTransaction(em2);
                
                    // Query by primary key.
                    Query query2 = em2.createQuery("Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName");
                    query2.setLockMode(LockModeType.PESSIMISTIC);
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
}
