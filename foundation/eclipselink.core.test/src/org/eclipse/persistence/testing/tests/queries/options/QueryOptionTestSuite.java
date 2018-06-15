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
package org.eclipse.persistence.testing.tests.queries.options;

import org.eclipse.persistence.testing.framework.TestSuite;

//Named queries are added in MW, therefore deployment XML or TopLink project.  Test all the query options.
public class QueryOptionTestSuite extends TestSuite {

    public QueryOptionTestSuite() {
        setDescription("This suite tests all of the functionality of the query options.");
    }

    public void addTests() {
        addTest(new RefreshCascadeAllNoIndirectionTest());
        addTest(new ReadObjectQueryDisableCacheHitsTest());
        addTest(new ClearQueryOptionsOnStatementTest());
    }

}
