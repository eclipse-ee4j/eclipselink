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
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.sessions.remote.rmi.*;
import org.eclipse.persistence.testing.tests.remote.*;
import org.eclipse.persistence.testing.framework.*;

public class RemoteSessionTest extends TestCase {
    RemoteSession remoteSession = null;
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
        remoteSession = (RemoteSession)connection.createRemoteSession();
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
