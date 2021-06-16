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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestProblemException;


public class QueryTimeOutTest extends AutoVerifyTestCase {
    private boolean limitExceed;

    public QueryTimeOutTest() {
        setDescription("Test the query timeout setting");
        this.limitExceed = false;
    }

    public void test() {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("workbenchintegration.QueryOptions' QueryTimeoutTest skipped for this platform, "
                    + "the driver does not support query time-out.");
        }
        if (getSession().getPlatform().isMaxDB()) {
            throwWarning("workbenchintegration.QueryOptions' QueryTimeoutTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 326503)");
        }
        try {
            getSession().executeQuery("queryTimeOutQuery",
                                      org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        } catch (Exception e) {
            if (e instanceof org.eclipse.persistence.exceptions.DatabaseException) {
                limitExceed = true;
            }
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            throw new TestProblemException("Test thread was interrupted.  Test failed to run properly");
        }
    }

    public void verify() {
        if (!limitExceed) {
            throw new org.eclipse.persistence.testing.framework.TestWarningException("Query was executed too fast to get timeout");
        }
    }
}
