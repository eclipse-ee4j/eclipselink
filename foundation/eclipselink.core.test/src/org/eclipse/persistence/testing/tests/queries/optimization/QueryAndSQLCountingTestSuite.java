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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.testing.framework.*;

public class QueryAndSQLCountingTestSuite extends TestSuite {
    public QueryAndSQLCountingTestSuite() {
        setDescription("This test suite is designed for test that redirect logging to a custom log and allows a count to be maintained " + " of the number of SQL statements and queries that are run in a specific test.");
    }

    public void addTests() {
        addTest(new JoiningValueholderInstantiationTest());
    }
}
