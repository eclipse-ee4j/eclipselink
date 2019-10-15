/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.tableswithspacesmodel;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * This handles the constraint deletion requirements.
 */
public class EmployeeWithSpacesDeleteTest extends DeleteObjectTest {
    /**
     * ProjectDeleteTest constructor comment.
     */
    public EmployeeWithSpacesDeleteTest() {
        super();
    }

    /**
     * ProjectDeleteTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public EmployeeWithSpacesDeleteTest(Object originalObject) {
        super(originalObject);
    }

    public static void deleteDependencies(org.eclipse.persistence.sessions.Session session, Employee employee) {
        // CR2114 - following line modified; employee.getClass() passed as argument
        String appendString = ((AbstractSession)session).getPlatform(employee.getClass()).getTableQualifier();
        String startQuoteChar = ((DatasourcePlatform)((AbstractSession)session).getPlatform(employee.getClass())).getStartDelimiter();
        String endQuoteChar = ((DatasourcePlatform)((AbstractSession)session).getPlatform(employee.getClass())).getEndDelimiter();
        if (appendString.length() != 0)
            appendString = appendString + ".";

        org.eclipse.persistence.sessions.Session psession = ((AbstractSession)session).getSessionForClass(Project.class);
        // Must drop references first to appease constraints.
        psession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + startQuoteChar + "PROJECT TABLE" + endQuoteChar + " set LEADER_ID = null where LEADER_ID = " + employee.getId()));

        org.eclipse.persistence.sessions.Session esession = ((AbstractSession)session).getSessionForClass(Employee.class);
        esession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + startQuoteChar + "EMPLOYEE TABLE" + endQuoteChar + " set MANAGER_ID = null where MANAGER_ID = " + employee.getId()));
    }

    protected void setup() {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("Test system EmployeeWithSpacesSystem is not supported on Symfoware, "
                    + "it does not allow spaces in tables or columns. (bug 304906)");
        }
        super.setup();
        deleteDependencies(getSession(), (Employee)getOriginalObject());
    }
}
