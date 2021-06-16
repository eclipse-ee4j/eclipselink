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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test the functionality of IntegrityTestChecker
 */
public class ShouldNotThroughIntegrityCheckerTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public ShouldNotThroughIntegrityCheckerTest() {
        setDescription("Test for don't catching all the Descriptor Exceptions");
    }

    public void test() {
        EmployeeProjectForIntegrityChecker project = new EmployeeProjectForIntegrityChecker();
        project.setDatasourceLogin(getSession().getDatasourceLogin());
        DatabaseSession session = new Project(getSession().getDatasourceLogin()).createDatabaseSession();
        session.dontLogMessages();
        boolean caughtError = false;
        try {
            session.login();
            session.setIntegrityChecker(new IntegrityChecker());
            session.getIntegrityChecker().dontCatchExceptions();
            session.addDescriptors(project);
        } catch (DescriptorException exception) {
            caughtError = true;
        } finally {
            session.logout();
        }

        if (!caughtError) {
            throw new TestErrorException("Test is Failed, no exception was thrown");
        }
    }
}
