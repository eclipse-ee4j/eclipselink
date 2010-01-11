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
package org.eclipse.persistence.testing.tests.performance;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performance test that allows cache and parameterizedSQL to be configured.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public abstract class PerformanceTest extends PerformanceRegressionTestCase {
    protected boolean shouldUseParameterizedSQL;
    protected boolean shouldCache;
    protected boolean shouldBatch;
    protected boolean shouldUseEmulatedDB;

    // Can be used to ensure cached objects do not garbage collect.
    protected List allObjects;    
    
    public PerformanceTest() {
        this.shouldCache = false;
        this.shouldUseParameterizedSQL = false;
        this.shouldBatch = false;
        this.shouldUseEmulatedDB = false;
    }

    public String getName() {
        String name = super.getName();
        if (shouldCache) {
            name = name + "Cached";
        }
        if (shouldUseParameterizedSQL) {
            name = name + "ParameterizedSQL";
        }
        if (shouldBatch) {
            name = name + "Batched";
        }
        if (shouldUseEmulatedDB) {
            name = name + "EmulatedDB";
        }
        return name;
    }

    public boolean shouldCache() {
        return shouldCache;
    }

    public boolean shouldUseEmulatedDB() {
        return shouldUseEmulatedDB;
    }

    public void setShouldUseEmulatedDB(boolean shouldUseEmulatedDB) {
        this.shouldUseEmulatedDB = shouldUseEmulatedDB;
    }
    
    public boolean shouldBatch() {
        return shouldBatch;
    }

    public void setShouldBatch(boolean shouldBatch) {
        this.shouldBatch = shouldBatch;
    }

    public void setShouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
    }

    public boolean shouldUseParameterizedSQL() {
        return shouldUseParameterizedSQL;
    }

    public void setShouldUseParameterizedSQL(boolean shouldUseParameterizedSQL) {
        this.shouldUseParameterizedSQL = shouldUseParameterizedSQL;
    }

    /**
     * Configure parameterized SQL.
     */
    public void setup() {
        if (shouldUseParameterizedSQL()) {
            getSession().getLogin().cacheAllStatements();
        }
        if (shouldBatch()) {
            getSession().getLogin().useBatchWriting();
            if (shouldUseParameterizedSQL()) {
                getSession().getLogin().setMaxBatchWritingSize(20);
            } else {
                getSession().getLogin().setMaxBatchWritingSize(32000);
            }
        }
        if (shouldUseEmulatedDB()) {
            setupEmulatedDB();
        }
    }
    
    /**
     * Swap the session to use an emulated session.
     * Load the employee data into the emulated DB.
     */
    public void setupEmulatedDB() {
        Session session = buildEmulatedSession();
        /*
        EmulatedConnection connection = (EmulatedConnection)((org.eclipse.persistence.internal.sessions.AbstractSession)session).getAccessor().getConnection();

        String sql;
        Vector rows;
        ComplexQueryResult result;
        
        // Load read all employees.
        ReadAllQuery readAllQuery = new ReadAllQuery(Employee.class);
        readAllQuery.setShouldIncludeData(true);
        result = (ComplexQueryResult)getSession().executeQuery(readAllQuery);
        List employees = (List)result.getResult();
        rows = (Vector)result.getData();
        sql = readAllQuery.getSQLString();
        connection.putRows(sql, rows);
        
        // Load Bob query.
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        ReadObjectQuery bobQuery = new ReadObjectQuery(Employee.class, expression);
        bobQuery.setShouldIncludeData(true);
        result = (ComplexQueryResult)getSession().executeQuery(bobQuery);
        rows = new Vector();
        rows.add(result.getData());
        sql = bobQuery.getSQLString();
        connection.putRows(sql, rows);
        
        // Load read pk.
        Iterator iterator = employees.iterator();
        Employee employee = (Employee)iterator.next();
        ReadObjectQuery readObjectQuery = new ReadObjectQuery(employee);
        readObjectQuery.setShouldIncludeData(true);
        readObjectQuery.setShouldRefreshIdentityMapResult(true);
        DatabaseRecord record = new DatabaseRecord();
        record.put("EMP_ID", employee.getId());
        readObjectQuery.prepareCall(getSession(), record);
        sql = readObjectQuery.getSQLString();
        rows = new Vector();
        rows.add(((ComplexQueryResult)getSession().executeQuery(readObjectQuery)).getData());
        connection.putRows(sql, rows);
        
        // Load sequence query.
        rows = new Vector();
        record = new DatabaseRecord();
        record.put("NEXTVAL", new Long(12345));
        rows.add(record);
        connection.putRows("SELECT EMP_SEQ.NEXTVAL FROM DUAL", rows);
        connection.putRows("SELECT ADDRESS_SEQ.NEXTVAL FROM DUAL", rows);*/

        getExecutor().swapSession(session);
    }

    /**
     * Clear cache.
     */
    public void test() throws Exception {
        if (!shouldCache()) {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        }
    }

    /**
     * Reset parameterized SQL.
     */
    public void reset() {
        if (shouldUseParameterizedSQL()) {
            getSession().getLogin().dontCacheAllStatements();
        }
        if (shouldBatch()) {
            getSession().getLogin().dontUseBatchWriting();
        }
        if (shouldUseEmulatedDB()) {
            getExecutor().resetSession();
        }
    }
}
