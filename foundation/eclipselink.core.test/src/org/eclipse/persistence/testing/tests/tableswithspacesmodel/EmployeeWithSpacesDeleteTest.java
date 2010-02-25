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
        String quoteChar = ((DatasourcePlatform)((AbstractSession)session).getPlatform(employee.getClass())).getIdentifierQuoteCharacter();
        if (appendString.length() != 0)
            appendString = appendString + ".";

        org.eclipse.persistence.sessions.Session psession = ((AbstractSession)session).getSessionForClass(Project.class);
        // Must drop references first to appease constraints.
        psession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + quoteChar + "PROJECT TABLE" + quoteChar + " set LEADER_ID = null where LEADER_ID = " + employee.getId()));

        org.eclipse.persistence.sessions.Session esession = ((AbstractSession)session).getSessionForClass(Employee.class);
        esession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + quoteChar + "EMPLOYEE TABLE" + quoteChar + " set MANAGER_ID = null where MANAGER_ID = " + employee.getId()));
    }

    protected void setup() {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("Test system EmployeeWithSpacesSystem is not supported on Symfoware, "
                    + "it does not allow spaces in tables or columns.");
        }
        super.setup();
        deleteDependencies(getSession(), (Employee)getOriginalObject());
    }
}
