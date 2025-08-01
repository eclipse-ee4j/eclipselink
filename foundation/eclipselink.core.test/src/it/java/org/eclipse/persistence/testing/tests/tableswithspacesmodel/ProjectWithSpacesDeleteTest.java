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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.tableswithspacesmodel;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;

// This handles the constraint deletion requirements.
public class ProjectWithSpacesDeleteTest extends DeleteObjectTest {

    /**
     * ProjectDeleteTest constructor comment.
     */
    public ProjectWithSpacesDeleteTest() {
        super();
    }

    /**
     * ProjectDeleteTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public ProjectWithSpacesDeleteTest(Object originalObject) {
        super(originalObject);
    }

    @Override
    protected void setup() {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("Test system EmployeeWithSpacesSystem is not supported on Symfoware, "
                    + "it does not allow spaces in tables or columns. (bug 304906)");
        }
        super.setup();
        // CR2114; Project.class passed as an argument
        String appendString = getAbstractSession().getPlatform(org.eclipse.persistence.testing.models.employee.domain.Project.class).getTableQualifier();
        if (!appendString.isEmpty()) {
            appendString = appendString + ".";
        }
        String startQuoteChar = getAbstractSession().getPlatform(org.eclipse.persistence.testing.models.employee.domain.Project.class).getStartDelimiter();
        String endQuoteChar = getAbstractSession().getPlatform(org.eclipse.persistence.testing.models.employee.domain.Project.class).getEndDelimiter();

        // Must drop references first to appease constraints.
        Session session = getAbstractSession().getSessionForClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("delete from " + appendString + startQuoteChar + "PROJ EMP" + endQuoteChar + " where PROJ_ID = " + ((org.eclipse.persistence.testing.models.employee.domain.Project)getOriginalObject()).getId()));
    }
}
