/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.employee;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.sessions.Session;

/**
 * This handles the constraint deletion requirements.
 */
public class EmployeeDeleteTest extends DeleteObjectTest {

    /**
     * ProjectDeleteTest constructor comment.
     */
    public EmployeeDeleteTest() {
        super();
    }

    /**
     * ProjectDeleteTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public EmployeeDeleteTest(Object originalObject) {
        super(originalObject);
    }

    public static void deleteDependencies(Session session, org.eclipse.persistence.testing.models.employee.domain.Employee employee) {
        // CR2114 - following line modified; employee.getClass() passed as argument
        String appendString = ((AbstractSession)session).getPlatform(employee.getClass()).getTableQualifier();
        if (appendString.length() != 0) {
            appendString = appendString + ".";
        }

        Session psession = ((AbstractSession)session).getSessionForClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);

        // Must drop references first to appease constraints.
        psession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + "PROJECT set LEADER_ID = null where LEADER_ID = " + employee.getId()));

        Session esession = ((AbstractSession)session).getSessionForClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        esession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + "EMPLOYEE set MANAGER_ID = null where MANAGER_ID = " + employee.getId()));
    }

    protected void setup() {
        super.setup();
        deleteDependencies(getSession(), (org.eclipse.persistence.testing.models.employee.domain.Employee)getOriginalObject());
    }
}
