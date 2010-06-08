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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests a basic session xml file that is built and validated against the
 * XML Schema
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date November 18, 2003
 */
public class SessionsXMLSchemaReloadTest extends TestCase {
    DatabaseSession employeeSession;
    DatabaseSession employeeSession2;

    public SessionsXMLSchemaReloadTest() {
        setDescription("Test loading of a basic session xml against the XML Schema");
    }

    public void reset() {
        if (employeeSession != null && employeeSession.isConnected()) {
            employeeSession.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession);
            employeeSession = null;
        }
        if (employeeSession2 != null && employeeSession2.isConnected()) {
            employeeSession2.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession2);
            employeeSession2 = null;
        }
    }

    public void test() {
        SessionManager.getManager().getSessions().remove("EmployeeSession");
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSession.xml");
        employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader());
        employeeSession2 = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSession", new AlternateLoader());
    }

    protected void verify() {
        if (employeeSession == null) {
            throw new TestErrorException("Employee session is null");
        }

        if (employeeSession.getDescriptor(Employee.class) == null) {
            throw new TestErrorException("Missing a descriptor from the Employee project");
        }

        if (employeeSession == employeeSession2) {
            throw new TestErrorException("Failed to reset session when loading with different class loader");
        }
        if (employeeSession.isConnected()) {
            throw new TestErrorException("Employee session is connected");
        }
        if (!employeeSession2.isConnected()) {
            throw new TestErrorException("Employee session # 2 is not connected");
        }
    }

    public class AlternateLoader extends ClassLoader {
        public AlternateLoader() {
            super(ClassLoader.getSystemClassLoader());
        }
    }
}
