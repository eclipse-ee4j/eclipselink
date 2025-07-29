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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.insurance.HealthClaim;


/**
 * Tests a basic session xml file that is built and validated against the
 * XML Schema
 *
 * @author Guy Pelletier
 * @version 1.0
 */
public class SessionsXMLSchemaTest extends AutoVerifyTestCase {
    DatabaseSession employeeSession;

    public SessionsXMLSchemaTest() {
        setDescription("Test loading of a basic session xml against the XML Schema");
    }

    @Override
    public void reset() {
        if (employeeSession != null && employeeSession.isConnected()) {
            employeeSession.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession);
            employeeSession = null;
        }
    }

    @Override
    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSession.xml");

        // log in the session
            employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), true, true); // refresh the session
    }

    @Override
    protected void verify() {
        if (employeeSession == null) {
            throw new TestErrorException("Employee session is null");
        }

        if (employeeSession.getDescriptor(Employee.class) == null) {
            throw new TestErrorException("Missing a descriptor from the Employee project");
        }

        if (employeeSession.getDescriptor(HealthClaim.class) == null) {
            throw new TestErrorException("Missing a descriptor from the Insurance project");
        }

        if (!employeeSession.isConnected()) {
            throw new TestErrorException("Employee session is connected");
        }
    }
}
