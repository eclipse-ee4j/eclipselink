/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

    @Override
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
            query.setSQLString("SELECT SUM(e.EMP_ID) from EMPLOYEE e , EMPLOYEE b, EMPLOYEE c, EMPLOYEE d, EMPLOYEE f, EMPLOYEE g, EMPLOYEE h, EMPLOYEE g");
            query.setQueryTimeout(1);
            getSession().executeQuery(query);
        } catch (Exception e) {
            if (e instanceof DatabaseException) {
                limitExceed = true;
            }
        }
    }

    @Override
    public void verify() {
        if (!limitExceed) {
            throw new TestErrorException("Timeout did not occur.");
        }
    }
}
