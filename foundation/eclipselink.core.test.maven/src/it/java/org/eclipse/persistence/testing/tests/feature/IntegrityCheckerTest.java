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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Test the functionality of IntegrityTestChecker
 */
public class IntegrityCheckerTest extends AutoVerifyTestCase {
    public IntegrityCheckerTest() {
        setDescription("Test for Descriptor Exceptions");
    }

    public void test() {
        boolean didErrorOccur = false;
        EmployeeProjectForIntegrityChecker ep = new EmployeeProjectForIntegrityChecker();
        ep.setLogin(getSession().getLogin());
        DatabaseSession ds = ep.createDatabaseSession();
        ds.setSessionLog(getSession().getSessionLog());
        try {
            ds.login();
        } catch (IntegrityException ic) {
            if (ic.getIntegrityChecker().getCaughtExceptions().size() != 15) {
                throw new TestErrorException("" + ic.getIntegrityChecker().getCaughtExceptions().size() + "  Not equal to the Number of Exceptions to the excepted 15.");
            }
            didErrorOccur = true;
        } finally {
            ds.logout();
        }
        if (!didErrorOccur) {
            throw new TestErrorException("no error found");
        }
    }
}
