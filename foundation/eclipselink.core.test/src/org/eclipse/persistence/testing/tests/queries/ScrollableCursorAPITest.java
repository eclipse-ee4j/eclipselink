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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test the scrollable cursor apis like hasNext(), hasPrevious() when no result will be returned from the query
 */
public class ScrollableCursorAPITest extends TestCase {
    protected ScrollableCursor employeeStream;
    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;

    public ScrollableCursorAPITest() {
        setDescription("Test the scrollable cursor APIs like hasNext(), hasPrevious() when no result will be returned from the query");
    }

    protected void setup() {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isTimesTen() || getSession().getPlatform().isHANA()) {
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
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("lastName").like("%blablablabla%"); //no data should be found
        query.setSelectionCriteria(exp);
        if(TYPE_SCROLL_INSENSITIVE_isSupported && CONCUR_UPDATABLE_isSupported) {
            query.useScrollableCursor();
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
        employeeStream = (ScrollableCursor)getSession().executeQuery(query);
    }

    /**
     * Verify if the scrollable cursor APIs are functioning properly
     */
    protected void verify() {
        if (employeeStream.hasNext() || employeeStream.hasPrevious() || employeeStream.isLast() || 
            employeeStream.isFirst()) {
            employeeStream.close();
            throw new TestErrorException("The  the scrollable cursor APIs are not working properly");
        }
        employeeStream.close();

    }
}
