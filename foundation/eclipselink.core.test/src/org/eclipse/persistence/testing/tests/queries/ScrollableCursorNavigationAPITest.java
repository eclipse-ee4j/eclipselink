/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Added to increase code coverage.
 * Should be used both for local and for remote scrollable cursors.
 */
public class ScrollableCursorNavigationAPITest extends TestCase {
    protected boolean useConforming = false;
    ConformingTestConfiguration configuration = null;

    protected String navigationError = null;
    protected Exception caughtException = null;

    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;
    
    public ScrollableCursorNavigationAPITest() {
        setDescription("This test tests various API which is used to navigate scrollable cursors.");

    }

    public ScrollableCursorNavigationAPITest(boolean useConforming) {
        this();
        if (useConforming) {
            setName("ScrollableCursorNavigationAPIConformingTest");
            this.useConforming = useConforming;
            this.configuration = new ConformingTestConfiguration();
        }
    }

    protected void setup() {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }
        //MySQL ResultSet.relative(int) does not work when attempting to move beyond the first-1/last+1 row.  Seems a bug
        if (getSession().getPlatform().isMySQL()) {
            throw new TestWarningException("Not supported in MySQL");
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
        if(getSession().getPlatform().isSymfoware()) {
            // Symfoware supports updatable cursors, but considers SQL queries
            // that select from multiple tables as non-updatable, thus raising
            // an exception for this test.
            CONCUR_UPDATABLE_isSupported = false;
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        if (configuration != null) {
            configuration.setup(getSession());
            getExecutor().setSession(configuration.getUnitOfWork());
        }
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();

        if (configuration != null) {
            ExpressionBuilder emp = new ExpressionBuilder();
            Expression exp = emp.get("salary").greaterThan(50000);
            query.setSelectionCriteria(exp);
            query.conformResultsInUnitOfWork();
        }
        ScrollableCursor cursor = null;

        try {
            query.setReferenceClass(Employee.class);
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
                policy.setPageSize(2);
                query.useScrollableCursor(policy);
            }
            cursor = (ScrollableCursor)getSession().executeQuery(query);

            try {
                boolean isFirst = cursor.first();
                if (!cursor.isFirst() || !isFirst) {
                    navigationError = "cursor.first() does not result in cursor.isFirst() returning true.";
                }
                Object second = cursor.next();
                Object first = cursor.previous();
                if (first.equals(second)) {
                    navigationError = "cursor.next() and cursor.previous() are not complementary.";
                }
                if (!second.equals(cursor.next())) {
                    navigationError = "cursor.next() does not move the cursor forward.";
                }
                boolean isRelative = cursor.relative(1);
                if (!isRelative || !second.equals(cursor.previous())) {
                    navigationError = "cursor.relative() does not move the cursor the proper number of spaces.";
                }
                boolean isAbsolute = cursor.absolute(1);
                if (!second.equals(cursor.next())) {
                    navigationError = "cursor.absolute(0) move a cursor to the beginning of the cursor.";
                }
                cursor.beforeFirst();
                if (!cursor.isBeforeFirst()) {
                    navigationError = 
                            "cursor.beforeFirst() does not result in cursor.isBeforeFirst() returning true.";
                }
                if (!first.equals(cursor.next())) {
                    navigationError = "cursor.beforeFirst() does not set the cursor position properly.";
                }

                boolean isLast = cursor.last();
                if (!isLast || !cursor.isLast()) {
                    navigationError = "cursor.last() does not result in cursor.isLast() returning true.";
                }
                cursor.afterLast();

                if (!cursor.isAfterLast()) {
                    navigationError = "cursor.afterLast() does not result in cursor.isAfterLast() returning true.";
                }
                Object last = cursor.previous();
                int size = cursor.size();
                cursor.relative(size);
                Object lastBySize = cursor.previous();
                if (!last.equals(lastBySize)) {
                    navigationError = "The last item in the list is not correct.";
                }

            } catch (org.eclipse.persistence.exceptions.QueryException ex) {
                caughtException = ex;
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void reset() {
        if (configuration != null) {
            getExecutor().setSession(configuration.getUnitOfWork().getParent());
            configuration.reset();
        }
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }


    protected void verify() {

        if (navigationError != null) {
            throw new TestErrorException("Cursor Navigation produced incorrect results. " + navigationError);
        }

        if (caughtException != null) {
            throw new TestErrorException("Cursor navigation caused a QueryException.", caughtException);
        }

    }

}
