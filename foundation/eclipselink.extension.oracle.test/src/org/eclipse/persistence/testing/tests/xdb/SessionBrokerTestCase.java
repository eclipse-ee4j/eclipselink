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
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.broker.*;
import org.eclipse.persistence.testing.framework.TestCase;

public class SessionBrokerTestCase extends TestCase {
    SessionBroker broker;

    public SessionBrokerTestCase() {
        setDescription("Tests reading an writing XML through a SessionBroker");
    }

    public void setup() {
        Session session = getSession();

        broker = new SessionBroker();
        broker.setSessionLog(session.getSessionLog());
        broker.registerSession("session1", session);
    }

    public void reset() {
    }

    public void test() {
        Employee_XML emp = new Employee_XML();
        emp.firstName = "Matt";
        emp.lastName = "MacIvor";
        emp.gender = "Male";
        emp.payroll_xml = "<payroll><something>1212</something><tag12>wewewe</tag12></payroll>";
        broker.writeObject(emp);
    }
}
