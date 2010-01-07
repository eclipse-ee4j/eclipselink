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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug 214910:  Add query timeout support to batched update queries</p>
 * Test the query timeout feature in batch queries.
 * For data queries , a queryTimeout on the largest DatabaseQuery of the batch will be used.
 * For object queries, a queryTimeout on the largest DescriptorQueryManager (parent) or DatabaseQuery
 * of the batch will be used.
 */
public abstract class QueryTimeoutBatchTestCase extends TestCase {
    // Send an invalid timeout of -1 sec to the preparedStatement to force and SQLException
    protected static final int TIMEOUT_INVALID = -1;
    protected long sequence_start = 40000;
    // This boolean should be true if the timeout occurred
    protected boolean limitExceeded;
    // This boolean should be true if the timeout did not occur as dictated by the test case
    protected boolean limitNotExceeded;    
    // This is the actual error code that should match the expected error code - specific to DB vendor
    protected int vendorErrorCodeEncountered;
    
    // For Oracle 9+ we support query timeout passing during batch updates
    protected boolean unsupportedPlatform = false;
    
    /** Save platform state */
    protected boolean usesBatchWriting;
    protected boolean shouldBindAllParameters;
    protected boolean shouldCacheAllStatements;
    protected boolean usesJDBCBatchWriting;
    protected boolean usesNativeBatchWriting;
    protected boolean usesStringBinding;
    
    protected abstract int getParentQueryTimeout();
    protected abstract int getChildQueryTimeout();
    protected abstract int getNumberOfInserts();
    protected abstract String getQuerySQLPrefix();    
    protected abstract String getQuerySQLPostfix();    
    // true = parameterizedSQLBatchWritingMechanism
    protected abstract boolean shouldBindAllParameters();
    protected abstract boolean shouldCacheAllStatements();
    protected abstract List<Employee> registerObjects(UnitOfWork uow);    
    protected abstract void setDescriptorLevelQueryTimeout(DescriptorQueryManager queryManager);
    protected abstract void setQueryLevelQueryTimeout(UnitOfWork uow, Object object);    
        
    public QueryTimeoutBatchTestCase() {
    	initialize();
    }

    protected void initialize() {
    	limitExceeded = false;
    	limitNotExceeded = true;
    }

    protected long getCurrentIDSequence() { return sequence_start; }
    protected void setCurrentIDSequence(long aSequence) { sequence_start = aSequence; }
    
	protected boolean verifyErrorCode() {
		return false;
	}

    protected int getExpectedErrorCode() {
    	return 17068; // SQLException Invalid argument(s) in call
    }
	
    protected void initializeDatabase(UnitOfWork uow) {
    	try {
    		// Add expected inserts to sequence
    		DataModifyQuery modifyQuery = new DataModifyQuery();
    		
            String sequenceTableName = "SEQUENCE";
            if (getSession().getPlatform().getDefaultSequence().isTable()) {
                sequenceTableName = getSession().getPlatform().getQualifiedSequenceTableName();
            }
    		modifyQuery.setSQLString("UPDATE " + sequenceTableName + " SET SEQ_COUNT = SEQ_COUNT + 10 WHERE SEQ_NAME = 'EMP_SEQ'");
    		modifyQuery.setForceBatchStatementExecution(true);
    		uow.addQuery("modify1", modifyQuery);    		
    		uow.executeQuery(modifyQuery);
    		//uow.commit();
    	
    		// Get next sequence
    		DataReadQuery readQuery = new DataReadQuery();
    		readQuery.setSQLString("SELECT SEQ_COUNT FROM " + sequenceTableName + " WHERE SEQ_NAME = 'EMP_SEQ'");
    		uow.addQuery("read1", readQuery);    		
    		Object resultFromRead = uow.executeQuery(readQuery);
    		DatabaseRecord dbRecord = (DatabaseRecord)((Vector)resultFromRead).get(0);
    		setCurrentIDSequence(((BigDecimal)dbRecord.get("SEQ_COUNT")).longValue());
    		uow.commit();
    	} catch (Exception e) {
    		System.out.println("QueryTimeoutBatchTest could not get EMP_SEQ sequence");
    		setCurrentIDSequence(40000 + Math.round(Math.random()));
    	}
    }
    
    /**
     * Setup the platform to perform batch inserts
     * Return to previous state after running
     * @return
     */
    protected UnitOfWork setupPlatform() {
        Session session = getSession();
        DatabasePlatform platform = session.getPlatform();
        // Created for BUG# 214910 - Batch query timeout (Oracle 9.0.1+) only
        if(!getSession().getPlatform().usesNativeBatchWriting() || !getSession().getPlatform().usesNativeBatchWriting()) {
        	unsupportedPlatform = true;
        }

        if (!platform.isOracle()) {
            System.out.println("Native batch writing is not supported on this database.");
        } else {
        	// Save current settings
        	usesBatchWriting = platform.usesBatchWriting();
            shouldBindAllParameters = platform.shouldBindAllParameters();
            shouldCacheAllStatements = platform.shouldCacheAllStatements();
            usesJDBCBatchWriting = platform.usesJDBCBatchWriting();
            usesNativeBatchWriting = platform.usesNativeBatchWriting();
            usesStringBinding = platform.usesStringBinding();

        	// Modify settings
            platform.setUsesBatchWriting(true);
            platform.setShouldBindAllParameters(shouldBindAllParameters());
            platform.setShouldCacheAllStatements(shouldCacheAllStatements());
            // Use the JDBC driver batch support (over TopLink)
            platform.setUsesJDBCBatchWriting(true);
            // Use DB specific SQL
            platform.setUsesNativeBatchWriting(true);
            //platform.setUsesStringBinding(false);
        }        
        return session.acquireUnitOfWork();
    }

    protected void resetPlatform() {
    	// Save current settings
        Session session = getSession();
        DatabasePlatform platform = session.getPlatform();        
        if (!platform.isOracle()) {
            System.out.println("Native batch writing is not supported on this database.");
        } else {
        	platform.setUsesBatchWriting(usesBatchWriting);
        	platform.setShouldBindAllParameters(shouldBindAllParameters);
        	platform.setShouldCacheAllStatements(shouldCacheAllStatements);
        	platform.setUsesJDBCBatchWriting(usesJDBCBatchWriting);
        	platform.setUsesNativeBatchWriting(usesNativeBatchWriting);
        	platform.setUsesStringBinding(usesStringBinding);
        }
    }
    
    public void verifyBefore(UnitOfWork uow) {
    }
    
    public void verify() {
        if (!limitExceeded || (verifyErrorCode() && getExpectedErrorCode() != vendorErrorCodeEncountered))  {
            throw new TestErrorException("Batch timeout did not occur.");
        }
    }
    
    protected List updateObjects(List objectListForEditing, UnitOfWork uow) {
    	// NOP
    	return new ArrayList();
    }
    
}
