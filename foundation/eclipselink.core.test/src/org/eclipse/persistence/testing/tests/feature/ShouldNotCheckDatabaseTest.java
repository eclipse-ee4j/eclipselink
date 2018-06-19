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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test the functionality of IntegrityTestChecker
 */
public class ShouldNotCheckDatabaseTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public ShouldNotCheckDatabaseTest() {
        setDescription("Test for don't checking the database tables and fields");
    }

    public void test() {
        EmployeeProjectForDatabaseChecking project = new EmployeeProjectForDatabaseChecking();
        project.setDatasourceLogin(getSession().getDatasourceLogin());
        DatabaseSession session = new Project(getSession().getDatasourceLogin()).createDatabaseSession();

        try {
            session.setSessionLog(getSession().getSessionLog());
            session.login();
            session.addDescriptors(project);
        } catch (IntegrityException exception) {
            throw new TestErrorException("Test is Failed, database Error", exception);
        } finally {
            session.logout();
        }
    }
}
