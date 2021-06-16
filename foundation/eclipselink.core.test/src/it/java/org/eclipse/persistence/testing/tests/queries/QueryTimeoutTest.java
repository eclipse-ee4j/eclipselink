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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test the query timeout feature through using locking.
 */

public class QueryTimeoutTest extends TestCase {
    private boolean limitExceed;

    public QueryTimeoutTest() {
        setDescription("Test the query timeout setting");
        this.limitExceed = false;
    }

    public void test() {
        if (getSession().getLogin().getPlatform().isSymfoware()) {
            throwWarning("Test QueryTimeoutTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 304905)");
        }
        if (getSession().getPlatform().isMaxDB()) {
            throwWarning("Test QueryTimeoutTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 326503)");
        }
        if (getSession().getPlatform().isHANA()) {
            throwWarning("Test QueryTimeoutTest skipped for this platform, "
                    + "the driver does not support query timeout. (bug 384135)");
        }
        try {
            DataReadQuery query = new DataReadQuery();
            query.setSQLString("SELECT SUM(e.EMP_ID) from EMPLOYEE e , EMPLOYEE b, EMPLOYEE c, EMPLOYEE d, EMPLOYEE f, EMPLOYEE g, EMPLOYEE h");
            query.setQueryTimeout(1);
            getSession().executeQuery(query);
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                limitExceed = true;
            }
        }
    }

    public void verify() {
        if (!limitExceed) {
            throw new TestErrorException("Timeout did not occur.");
        }
    }
}
