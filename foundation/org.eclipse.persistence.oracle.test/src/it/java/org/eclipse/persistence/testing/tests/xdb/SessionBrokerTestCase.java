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
