/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.*;

/**
 * Test the functionality of IntegrityTestChecker
 */
public class ShouldCheckDatabaseTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public ShouldCheckDatabaseTest() {
        setDescription("Test for checking the database tables and fields");
    }

    public void test() {
        EmployeeProjectForDatabaseChecking project = new EmployeeProjectForDatabaseChecking();
        project.setDatasourceLogin(getSession().getDatasourceLogin());
        DatabaseSessionImpl session = (DatabaseSessionImpl)new Project(getSession().getDatasourceLogin()).createDatabaseSession();
        boolean caughtError = false;
        try {
            session.setSessionLog(getSession().getSessionLog());

            session.login();
            session.setIntegrityChecker(new IntegrityChecker());
            session.getIntegrityChecker().checkDatabase();
            session.addDescriptors(project);

        } catch (IntegrityException integrityException) {
            if (integrityException.getIntegrityChecker().getCaughtExceptions().size() != 5) {
                throw new TestErrorException("" + integrityException.getIntegrityChecker().getCaughtExceptions().size() + "  Not equal to the Number of Exceptions to the expected 5.");
            }
            caughtError = true;
        } finally {
            session.logout();
        }

        if (!caughtError) {
            throw new TestErrorException("Test is Failed, no database error ");
        }
    }
}
