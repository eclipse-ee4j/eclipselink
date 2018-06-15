/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.eclipse.persistence.sessions.remote.rmi.*;
import org.eclipse.persistence.testing.tests.remote.*;
import org.eclipse.persistence.testing.framework.*;

public class RemoteSessionTest extends TestCase {
    DistributedSession remoteSession = null;
    RMIConnection connection = null;

    public RemoteSessionTest() {
    }

    public void setup() {
        //sneakily using some existing code
        RMIRemoteModel remote = new RMIRemoteModel();
        DatabaseSession dbSession = ((org.eclipse.persistence.sessions.Project)getSession().getProject().clone()).createDatabaseSession();
        dbSession.setSessionLog(getSession().getSessionLog());
        dbSession.login();
        RMIServerManagerController.start(dbSession);
        connection = remote.createConnection();
        remoteSession = (DistributedSession)connection.createRemoteSession();
    }

    public void reset() {
    }

    public void test() {
        Employee_XML emp = new Employee_XML();
        emp.firstName = "Matt";
        emp.lastName = "MacIvor";
        emp.gender = "Male";
        emp.payroll_xml = "<payroll><pay-period>bi-monthly</pay-period><salary>noyb</salary></payroll>";
        remoteSession.writeObject(emp);
    }
}
