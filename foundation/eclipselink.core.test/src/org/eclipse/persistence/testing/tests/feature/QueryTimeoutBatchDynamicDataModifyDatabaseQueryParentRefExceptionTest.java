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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Bug 214910:  Add query timeout support to batched update queries (Oracle DB 9.0.1+)</p>
 * Test the query timeout feature in batch queries.
 * For data queries , a queryTimeout on the largest DatabaseQuery of the batch will be used.
 * For object queries, a queryTimeout on the largest DescriptorQueryManager (parent) or DatabaseQuery
 * of the batch will be used.<p>
 * A reference to a parent of a data query will be ignored - as the descriptor is null.
 * The reason we do not throw an exception is because the current nullValue for the queryTimeout
 * attribute on a mapping read from a deployment project is -1 = parent.<p>
 * Since we will also be not be overriding the -1 initialization in the abstract DatabaseQuery during
 * a newInstance() creation - we will allow inadvertent client -1 parent references as well.       
 */
public class QueryTimeoutBatchDynamicDataModifyDatabaseQueryParentRefExceptionTest extends QueryTimeoutBatchDatabaseQueryTest {
  
    protected boolean shouldBindAllParameters() { return false; }
    protected boolean shouldCacheAllStatements() { return true; }
    protected  int getNumberOfInserts() { return 1; }

    protected String getQuerySQLPrefix() {
    	return "insert into employee (emp_id, version) SELECT "; 
    }
    
	protected  int getParentQueryTimeout() { return 1; }
	// A -1 timeout should not override the parent - it is ignored for data queries
	protected  int getChildQueryTimeout() { return -1; }
	
    protected String getQuerySQLPostfix() {
    	return ", SUM(e.address_id) as version from address e, address b, address b, address c, address c, address c, address b"; 
    }    
    
    public QueryTimeoutBatchDynamicDataModifyDatabaseQueryParentRefExceptionTest() {
    	super();
        setDescription("Test that an invalid queryTimeout parent reference in a DataModifyQuery does not throw an exception in batch queries.");
    }
    
    public void test() {
    	super.test();        
        if(limitExceeded) {
        	System.out.println("QueryTimeoutBatch test completed without timeout.");
        } else {
        	System.out.println("QueryTimeoutBatch test completed successfully");
        }        
    }
        
    public void verify() {
        if(!limitNotExceeded) {
            throw new TestErrorException("QueryTimeoutBatchDynamicDataModifyDatabaseQueryParentRefExceptionTest Batch queryTimeout erroroccurred but was designed to pass.");
        }
    	// Make flag reentrant
    	initialize();
    }    
}
