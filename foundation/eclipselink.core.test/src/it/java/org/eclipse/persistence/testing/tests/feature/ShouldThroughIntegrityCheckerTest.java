/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
