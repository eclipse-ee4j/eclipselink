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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadObjectQuery;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Bug 304423 - CacheIdentityMap is incorrectly synchronized
 * Test invoking many queries from multiple threads against a QueryCache
 * Underlying issue was an infinite loop in CacheIdentityMap ensureFixedSize()
 * @author dminsky
 */
public class ConcurrentQueryCacheQueryResultsTest extends TestCase {

    // constants
    protected static final int CACHE_SIZE = 5;
    protected static final int CONCURRENT_THREADS = 7;
    protected static final int TESTDATA_INSTANCES = 100;
    protected static final int NUMBER_OF_EXECUTIONS = 20000;
    protected static final int TIMEOUT_IN_SECONDS = 60;
    protected static final String QUERY_NAME = "findEmployeeByFirstName";    
    
    // shared results/testdata
    protected List<Employee> testEmployees;
    protected List<Boolean> testResults;
    protected boolean terminatedNormally;
    
    public ConcurrentQueryCacheQueryResultsTest() {
        super();
        setDescription("Test invoking many queries from multiple threads against a QueryCache");
    }
    
    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        
        // create a simple named query on the session and configure it with a small query results cache size,
        // set to CACHE_SIZE
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        query.setSelectionCriteria(builder.get("firstName").equal(builder.getParameter("firstName")));
        query.addArgument("firstName");
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy(CACHE_SIZE));
        getSession().addQuery(QUERY_NAME, query);
        
        testEmployees = new ArrayList<Employee>(TESTDATA_INSTANCES);
        
        // set all test results to false
        testResults = new ArrayList<Boolean>(NUMBER_OF_EXECUTIONS);
        for (int i = 0; i < NUMBER_OF_EXECUTIONS; i++) {
            testResults.add(i, Boolean.FALSE);
        }
        
        // create our own test data
        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (int i = 0; i < TESTDATA_INSTANCES; i++) {
            Employee emp = new Employee();
            emp.setFirstName(String.valueOf(UUID.randomUUID()).substring(0, 8));
            emp.setLastName(String.valueOf(UUID.randomUUID()).substring(0, 8));
            uow.registerObject(emp);
            testEmployees.add(emp);
        }
        uow.commit();
    }
    
    @Override
    public void test() throws Throwable {
        // configure a thread pool and ExecutorService, add Runnable instances to execute
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        for (int resultId = 0; resultId < NUMBER_OF_EXECUTIONS; resultId++) {
            executor.execute(new QueryWorkerThread(resultId));
        }
        executor.shutdown(); // no more tasks
        
        // configure a timeout and await termination
        try {
            terminatedNormally = executor.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw e;
        }
    }
    
    @Override
    public void verify() {
        // fail if timeout occurred, instead of 'normal' termination
        if (!terminatedNormally) {
            fail(getClass().getSimpleName() + " timed out whilst executing, after " + TIMEOUT_IN_SECONDS + " seconds");
        }
        // fail if any test result was incorrect (false)
        for (int resultId = 0; resultId < NUMBER_OF_EXECUTIONS; resultId++) {
            if (this.testResults.get(resultId) == Boolean.FALSE) {
                fail("Test result failed (false) for test result id: " + resultId);
            }
        }
    }
    
    @Override
    public void reset() {
        getSession().removeQuery(QUERY_NAME);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(testEmployees);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
    
    protected class QueryWorkerThread implements Runnable {
        
        protected int resultId;
        
        public QueryWorkerThread(int resultId) {
            super();
            this.resultId = resultId;
        }
        
        public void run() {
            int randomNumber = new Random().nextInt(TESTDATA_INSTANCES);
            Employee randomEmployee = testEmployees.get(randomNumber);
            
            Vector<String> args = new Vector<String>();
            args.add(randomEmployee.getFirstName());
            Employee employeeFound = (Employee) getSession().executeQuery(QUERY_NAME, args);
            
            assertEquals(randomEmployee.getId(), employeeFound.getId());
            assertEquals(randomEmployee.getFirstName(), employeeFound.getFirstName());
            assertEquals(randomEmployee.getLastName(), employeeFound.getLastName());
            
            testResults.set(this.resultId, Boolean.TRUE);
        }
        
    }
}
