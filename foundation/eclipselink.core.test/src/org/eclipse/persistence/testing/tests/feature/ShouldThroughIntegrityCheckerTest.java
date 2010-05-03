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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test the functionality of IntegrityTestChecker
 */
public class ShouldThroughIntegrityCheckerTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public ShouldThroughIntegrityCheckerTest() {
        setDescription("Test for catching all the Descriptor Exceptions");
    }

    public void test() {
        EmployeeProjectForIntegrityChecker project = new EmployeeProjectForIntegrityChecker();
        project.setDatasourceLogin(getSession().getDatasourceLogin());
        DatabaseSession session = new Project(getSession().getDatasourceLogin()).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        boolean caughtError = false;
        try {
            session.login();
            session.addDescriptors(project);
        } catch (IntegrityException integrityException) {
            if (integrityException.getIntegrityChecker().getCaughtExceptions().size() != 15) {
                throw new TestErrorException("" + integrityException.getIntegrityChecker().getCaughtExceptions().size() + "  Not equal to the Number of Exceptions to the excepted 15.");
            }
            caughtError = true;
        } finally {
            session.logout();
        }

        if (!caughtError) {
            throw new TestErrorException("Test is Failed, no error thrown");
        }
    }
}
