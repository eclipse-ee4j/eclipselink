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
package org.eclipse.persistence.testing.tests.queries.options;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.*;

import java.math.*;
import java.util.*;
import java.sql.*;
import java.lang.reflect.*;

/**
 * Test to verify that max rows, query timeout and result set fetch size are cleared
 * on PreparedStatement objects utilized by TopLink. After a query has been executed,
 * these settings must be cleared so that other queries do not use these options 
 * that are set local to each query. 
 * For Bug 5709179 - MAX-ROWS/TIMEOUT NOT RESET ON CACHED STATEMENTS
 * @author dminsky
 */
public class ClearQueryOptionsOnStatementTest extends AutoVerifyTestCase {

    private List employeesCreated;
    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;

    public ClearQueryOptionsOnStatementTest() {
        super();
        setDescription("This test verifies max rows, query timeout & result set fetch size are cleared on prepared statements");
    }
    
    public void setup() {
        TYPE_SCROLL_INSENSITIVE_isSupported = true;
        CONCUR_UPDATABLE_isSupported = true;
        if(getSession().getPlatform().isSQLServer()) {
            // In case either TYPE_SCROLL_INSENSITIVE or CONCUR_UPDATABLE used  
            // MS SQL Server  Version: 9.00.2050;  MS SQL Server 2005 JDBC Driver  Version: 1.2.2828.100 throws exception:
            // com.microsoft.sqlserver.jdbc.SQLServerException: The cursor type/concurrency combination is not supported.
            TYPE_SCROLL_INSENSITIVE_isSupported = false;
            CONCUR_UPDATABLE_isSupported = false;
        }
        // must enable statement caching
        getDatabaseSession().getLogin().cacheAllStatements();
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        UnitOfWork uow = getDatabaseSession().acquireUnitOfWork();
        employeesCreated = new ArrayList(10);
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(190), "Jak"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(191), "Daxter"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(192), "Ratchet"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(193), "Clank"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(194), "Crash"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(195), "Sonic"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(196), "Mario"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(197), "Luigi"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(198), "Peach"));
        employeesCreated.add(new QueryOptionEmployee(new BigDecimal(199), "Bowser"));
        uow.registerAllObjects(employeesCreated);
        uow.commit();
    }
    
    public void test() {
        DatabaseSession session = getDatabaseSession();
        testQueryTimeoutReset(session); 
        testMaxRowsReset(session);
        testResultSetFetchSizeReset(session);   
    }
    
    public void testMaxRowsReset(Session session) {
        // MAX ROWS
        // 1. Execute query to read employees with a max-rows set to 4
        ReadAllQuery query = new ReadAllQuery(QueryOptionEmployee.class);
        query.setMaxRows(4);
        List employees = (List) session.executeQuery(query);
        
        // 2. Check employees read = 4 per MaxRows setting - just with an assert
        if (employees.size() != 4) {
            throw new TestErrorException("Max Rows reset - Rows returned: " + employees.size() + " (expecting 4)");
        }
        
        // 3. Execute another (new) query 100 times with same SQL & no max-rows setting
        for (int iteration = 0; iteration < 100; iteration++) {
            query = new ReadAllQuery(QueryOptionEmployee.class);
            employees = (List) session.executeQuery(query);
            if (employees.size() <= 4) {
                throw new TestErrorException("Max Rows reset - Rows returned: " + employees.size() + " (expecting >= 10)");
            }
        }
    }

    public void testResultSetFetchSizeReset(Session session) {
        // H2 sets the query fetch size on the connection, and does not clear it, so this will fail.
        if (getSession().getLogin().getDatasourcePlatform().isH2()) {
            return;            
        }
        // Resultset fetch size
        ReadAllQuery query = new ReadAllQuery(QueryOptionEmployee.class);
        if(TYPE_SCROLL_INSENSITIVE_isSupported && CONCUR_UPDATABLE_isSupported) {
            query.useScrollableCursor(2);
        } else {
            ScrollableCursorPolicy policy = new ScrollableCursorPolicy();
            if(!TYPE_SCROLL_INSENSITIVE_isSupported) {
                policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_SENSITIVE);
            }
            if(!CONCUR_UPDATABLE_isSupported) {
                policy.setResultSetConcurrency(ScrollableCursorPolicy.CONCUR_READ_ONLY);
            }
            policy.setPageSize(10);
            query.useScrollableCursor(policy);
        }
        
        String sql = "SELECT ID, NAME, HISTORY_ID FROM QUERY_OPTION_EMPLOYEE";
        int fetchSize = 100;
        query.setSQLString(sql);
        query.setFetchSize(fetchSize);
        
        // The statement cache is protected - need to obtain the internal hashtable from the accessor 
        org.eclipse.persistence.internal.sessions.DatabaseSessionImpl impl = 
            (org.eclipse.persistence.internal.sessions.DatabaseSessionImpl) session;
        DatabaseAccessor accessor = (DatabaseAccessor) impl.getAccessor();
        Map statementCache = null;
        try {
            Method method = PrivilegedAccessHelper.getDeclaredMethod(DatabaseAccessor.class,
                "getStatementCache", new Class[]{});
            method.setAccessible(true);
            statementCache = (Map) method.invoke(accessor, new Object[] {});
        } catch (Exception nsme) {
            throwError("Could not invoke DatabaseAccessor>>getStatementCache()", nsme);
        }

        // now cache the statement's previous fetch size
        int previousFetchSize = 0;
        Statement statement = (Statement) statementCache.get(sql);
        if (statement != null) {
            try {
                previousFetchSize = statement.getFetchSize();
            } catch (SQLException sqle) {
                throwError("Error whilst invoking intial Statement>>getFetchSize()", sqle);
            }
        }

        // execute query        
        ScrollableCursor cursor = (ScrollableCursor) session.executeQuery(query);
        List employees = new ArrayList();
        while (cursor.hasNext()) {
            employees.add(cursor.next());
        }
        cursor.close();

        // now check the statement
        int postQueryFetchSize = 0;
        statement = (Statement) statementCache.get(sql);
        if (statement != null) {
            try {
                postQueryFetchSize = statement.getFetchSize();
            } catch (SQLException sqle) {
                throwError("Error whilst invoking secondary Statement>>getFetchSize()", sqle);
            }
        }
        
        if (postQueryFetchSize == fetchSize) {
            throwError("Statement fetch size was not reset");
        }

    }
    
    public void testQueryTimeoutReset(Session session) {
        boolean query1TimedOut = false;
        boolean query2TimedOut = false;
        // H2 sets the query timeout on the connection, and does not clear it, so this will fail.
        if (getSession().getLogin().getDatasourcePlatform().isH2()) {
            return;            
        }
        String sql;
        if (getSession().getLogin().getDatasourcePlatform().isDB2() || getSession().getLogin().getDatasourcePlatform().isMySQL()) {
          sql = "SELECT SUM(e.EMP_ID) from EMPLOYEE e , EMPLOYEE b, EMPLOYEE c,EMPLOYEE d";
        } else {
          sql = "SELECT SUM(e.EMP_ID) from EMPLOYEE a , EMPLOYEE b, EMPLOYEE c, EMPLOYEE d, EMPLOYEE e, EMPLOYEE f, EMPLOYEE g";
        }
        // set the lowest timeout value on a query which is virtually guaranteed to produce a timeout
        try {
            DataReadQuery query = new DataReadQuery();
            query.setSQLString(sql);
            query.setQueryTimeout(1);
            session.executeQuery(query);
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                // cache value for debug purposes
                query1TimedOut = true;
            }
        }
        
        // do not set a timeout on the query, and test for a timeout
        try {
            DataReadQuery query = new DataReadQuery();
            query.setSQLString(sql);
            session.executeQuery(query);
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                query2TimedOut = true;
            }
        }
        
        // we're interested in if query 2 timed out
        // if no timeout value was set, query 2 should not produce a timeout
        if (query2TimedOut == true) {
            throw new TestErrorException("Query timeout occurred - PreparedStatement query timeout setting not cleared");
        }
    }
    
    public void reset() {
        getDatabaseSession().getLogin().dontCacheAllStatements();
        UnitOfWork uow = getDatabaseSession().acquireUnitOfWork();
        uow.deleteAllObjects(employeesCreated);
        uow.commit();
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();        
    }

}
