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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 *  CR#4272
 *  Test use of statement caching with ScrollableCursors.
 */
public class ScrollableCursorStatementCachingReadTest extends TestCase {
    protected Exception caughtException = null;
    protected int size;

    protected boolean origionalBindingState;
    protected boolean origionalStatementCachingState;
    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;

    public ScrollableCursorStatementCachingReadTest() {
        setDescription("This test verifies that useScrollableCursor together with shouldCacheStatement on a query returns correct results");
    }

    protected void setup() {
        this.origionalBindingState = this.getSession().getPlatform().shouldBindAllParameters();

        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }
        TYPE_SCROLL_INSENSITIVE_isSupported = true;
        CONCUR_UPDATABLE_isSupported = true;
        if(getSession().getPlatform().isSQLServer()) {
            // In case either TYPE_SCROLL_INSENSITIVE or CONCUR_UPDATABLE used  
            // MS SQL Server  Version: 9.00.2050;  MS SQL Server 2005 JDBC Driver  Version: 1.2.2828.100 throws exception:
            // com.microsoft.sqlserver.jdbc.SQLServerException: The cursor type/concurrency combination is not supported.
            TYPE_SCROLL_INSENSITIVE_isSupported = false;
            CONCUR_UPDATABLE_isSupported = false;
        }

        this.origionalStatementCachingState = this.getSession().getPlatform().shouldCacheAllStatements();
        this.getSession().getPlatform().setShouldBindAllParameters(true);
        this.getSession().getPlatform().setShouldCacheAllStatements(true);
    }

    public void test() {

        ReadAllQuery query1 = new ReadAllQuery();
        query1.setReferenceClass(Employee.class);
        Vector resultSet = null;

        ReadAllQuery query2 = new ReadAllQuery();
        query2.setReferenceClass(Employee.class);
        if(TYPE_SCROLL_INSENSITIVE_isSupported && CONCUR_UPDATABLE_isSupported) {
            query2.useScrollableCursor();
        } else {
            ScrollableCursorPolicy policy = new ScrollableCursorPolicy();
            if(!TYPE_SCROLL_INSENSITIVE_isSupported) {
                policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_SENSITIVE);
            }
            if(!CONCUR_UPDATABLE_isSupported) {
                policy.setResultSetConcurrency(ScrollableCursorPolicy.CONCUR_READ_ONLY);
            }
            policy.setPageSize(10);
            query2.useScrollableCursor(policy);
        }

        ScrollableCursor cursor = null;

        resultSet = (Vector)getSession().executeQuery(query1); //caches the statement
        try {
            cursor = (ScrollableCursor)getSession().executeQuery(query2);
            size = cursor.size();
        } catch (org.eclipse.persistence.exceptions.DatabaseException dbe) {
            caughtException = dbe;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected void verify() {
        if (caughtException != null) {
            throw new TestErrorException("Exception is thrown because scrollable cursor is used on a cached statement.  A new statement should be built when scrollable cursor is used.");
        }
        if (size != 12) {
            throw new TestErrorException("The number of streamed objects does not match the number of objects stored on the database");
        }
    }

    public void reset() {
        this.getSession().getPlatform().setShouldCacheAllStatements(this.origionalStatementCachingState);
        this.getSession().getPlatform().setShouldBindAllParameters(this.origionalBindingState);
    }
}
