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

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 *  CR#4139
 *  Test use of next and previous with ScrollableCursors
 */
public class ScrollableCursorBackwardReadingTest extends TestCase {
    protected boolean useConforming = false;
    ConformingTestConfiguration configuration = null;

    protected boolean cursorSuccess = false;
    protected Exception caughtException = null;
    Vector readWithNext = null;
    Vector readWithPrevious = null;

    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;

    public ScrollableCursorBackwardReadingTest() {
        setDescription("This test verifies that the number of objects read in from the end of a scrollable cursor to the start" + 
                       " matches the number of object read in using a normal query");
    }

    public ScrollableCursorBackwardReadingTest(boolean useConforming) {
        this();
        if (useConforming) {
            setName("ScrollableCursorBackwardReadingConformingTest");
            this.useConforming = useConforming;
            this.configuration = new ConformingTestConfiguration();
        }
    }

    protected void setup() {
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
        if(getSession().getPlatform().isSymfoware()) {
            // Symfoware supports updatable cursors, but considers SQL queries
            // that select from multiple tables as non-updatable, thus raising
            // an exception for this test.
            CONCUR_UPDATABLE_isSupported = false;
        }
        readWithNext = new Vector();
        readWithPrevious = new Vector();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        if (configuration != null) {
            configuration.setup(getSession());
            getExecutor().setSession(configuration.getUnitOfWork());
        }
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();
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
            //
            if (configuration != null) {
                ExpressionBuilder builder = new ExpressionBuilder();
                Expression exp = builder.get("salary").greaterThan(50000);
                query.setSelectionCriteria(exp);
                query.conformResultsInUnitOfWork();
            }
            cursor = (ScrollableCursor)getSession().executeQuery(query);

            try {
                // test to see if we can iterate through a list and then iterate
                // in reverse through the same list.
                int totalItems = 0;
                while (cursor.hasNext()) {
                    readWithNext.addElement(cursor.next());
                    totalItems++;
                }
                while (cursor.hasPrevious()) {
                    readWithPrevious.addElement(cursor.previous());
                    totalItems--;
                }

                cursorSuccess = (totalItems == 0);

                int size = readWithPrevious.size();
                for (int i = 0; i < readWithNext.size(); i++) {
                    cursorSuccess = 
                            (cursorSuccess && (readWithNext.elementAt(i) == readWithPrevious.elementAt((size - 1) - 
                                                                                                       i)));
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
        if (caughtException != null) {
            throw new TestErrorException("Cursor navigation caused a QueryException.", caughtException);
        }
        if (!cursorSuccess) {
            throw new TestErrorException("Cursor navigation failed.  Either next() or previous is not " + 
                                         "returning the correct result.");
        }
    }
}
