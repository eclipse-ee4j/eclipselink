/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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

    public ScrollableCursorAPITest() {
        setDescription("Test the scrollable cursor APIs like hasNext(), hasPrevious() when no result will be returned from the query");
    }

    protected void setup() {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("lastName").like("%blablablabla%"); //no data should be found
        query.setSelectionCriteria(exp);
        query.useScrollableCursor();
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
