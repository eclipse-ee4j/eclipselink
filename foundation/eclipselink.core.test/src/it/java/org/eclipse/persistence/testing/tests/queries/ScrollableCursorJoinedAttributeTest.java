/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.List;
import java.util.Vector;

/**
 * Test using a ScrollableCursor with a simple joined attribute.
 * Bug 351509 - Null Pointer Exception when using ScrollableCursor on a OneToMany Mapping
 */
public class ScrollableCursorJoinedAttributeTest extends TestCase {

    protected List cursoredResults;
    protected Exception caughtException;

    public ScrollableCursorJoinedAttributeTest() {
        super();
        setDescription("Scrollable Cursor Test incorporating a joined attribute");
    }

    @Override
    public void test() {
        if (getSession().getPlatform().isHANA() || getSession().getPlatform().isSQLServer()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        cursoredResults = new Vector();
        ReadAllQuery cursoredQuery = new ReadAllQuery(Employee.class);
        cursoredQuery.useScrollableCursor();
        cursoredQuery.addJoinedAttribute(cursoredQuery.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        cursoredQuery.addOrdering(cursoredQuery.getExpressionBuilder().get("id"));

        try {
            ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(cursoredQuery);
            while (cursor.hasNext()) {
                Object result = cursor.next();
                cursoredResults.add(result);
            }
            cursor.close();
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Override
    public void verify() {
        if (caughtException != null) {
            throwError("Cursored query should not result in an exception", caughtException);
        }
        assertNotSame("Test data for cursored results should be nonzero", 0, cursoredResults.size());
    }

    @Override
    public void reset() {
        this.cursoredResults = null;
    }

}
