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
package org.eclipse.persistence.testing.tests.feature;

import java.util.List;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Bug 214910:  Add query timeout support to batched update queries (Oracle DB 9.0.1+)</p>
 * Test the query timeout feature in batch queries.
 * For data queries , a queryTimeout on the largest DatabaseQuery of the batch will be used.
 * For object queries, a queryTimeout on the largest DescriptorQueryManager (parent) or DatabaseQuery
 * of the batch will be used.       
 */
public abstract class QueryTimeoutBatchDatabaseQueryTest extends QueryTimeoutBatchTestCase {
  
	protected  int getParentQueryTimeout() { return 2; }
	protected  int getChildQueryTimeout() { return 2; }

    protected String getQuerySQLPostfix() {
    	return ", SUM(e.address_id) as version from address e, address b, address b, address c, address c, address c, address b"; 
    }
	
    public void test() {
        UnitOfWork uow = null;
        try {
        	uow = setupPlatform();        	
        	initializeDatabase(uow);
    		// Get new UOW
        	uow = getSession().acquireUnitOfWork();

        	int queryCount = 0;
        	for (int i = 0; i < getNumberOfInserts(); i++) {
            	DataModifyQuery query = new DataModifyQuery();
        		queryCount++;
                // The following insert will take around 5.2 seconds per row (the last (address c) is significant)
        		// insert into employee (emp_id, version) SELECT 40003, SUM(e.address_id) as version from address e, address b, address b, address c, address c;
        		StringBuffer sBuffer = new StringBuffer(getQuerySQLPrefix());
        		sBuffer.append(getCurrentIDSequence() + i);
        		//sBuffer.append(i);
        		sBuffer.append(getQuerySQLPostfix());
        		
        		query.setSQLString(sBuffer.toString());
        		// set different query timeouts - the largest will be used
        		query.setQueryTimeout(getChildQueryTimeout());
        		//session.executeQuery(query);
        		StringBuilder queryName = new StringBuilder("query");
        		queryName.append(i);
        		
        		// Force the last query to execute
        		if(queryCount == getNumberOfInserts()) {
        			query.setForceBatchStatementExecution(true);
        			// clear last queryTimeout - so we pick up one from a previous appendCall
        			//query.setQueryTimeout(0);
        		}
        		uow.addQuery(queryName.toString(), query);
        		uow.executeQuery(query);        		        		
        	}
                	
        	uow.commit();
        } catch (Exception e) {
    		/** Throws
    		 * Internal Exception: java.sql.SQLException: ORA-01013: user requested cancel of current operation
    		 */
        	//e.printStackTrace();
        	//System.err.print(e.getMessage());
            if (e instanceof DatabaseException) {
                limitExceeded = true;
                vendorErrorCodeEncountered = ((DatabaseException)e).getDatabaseErrorCode();
        		//System.out.println("test completed with timeout of " + getChildQueryTimeout() + " seconds and exception: " + vendorErrorCodeEncountered);
            } else {
            	//e.printStackTrace();
            }
            // Release transaction mutex
            ((AbstractSession)uow).rollbackTransaction();
        } finally {
        	resetPlatform();
        }
    }
    
    protected List  registerObjects(UnitOfWork uow) {
    	return null;
	}
    
    /**
     * This is a callback from the object loop in registerObjects that allows the test
     * to set a timeout globally on the DescriptorQueryManager
     */
    public void setDescriptorLevelQueryTimeout(DescriptorQueryManager queryManager) {
    }

    /**
     * This is a callback from the object loop in registerObjects that allows the test
     * to set a timeout on individual queries
     */
    public void setQueryLevelQueryTimeout(UnitOfWork uow, Object object) {
    }
    
}
