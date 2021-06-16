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
package org.eclipse.persistence.testing.tests.queries.repreparation;

import org.eclipse.persistence.testing.framework.TestSuite;

//Bug2804042
public class QueryRepreparationTestSuite extends TestSuite {
    public QueryRepreparationTestSuite() {
        setDescription("This suite tests if the SQL is regenerated for those methods that affect SQL.");
    }

    public void addTests() {
        addTest(new AddFunctionItemTest());
        addTest(new AddGroupingTest());
        addTest(new AddItemTest());
        addTest(new AddJoinedAttributeTest());
        addTest(new AddNonFetchedJoinedAttributeTest());
        addTest(new AddOrderingTest());
        addTest(new AddPartialAttributeTest());
        addTest(new AddPartialAttributeForCustomSQLTest());
        addTest(new RetrievePrimaryKeysTest());
        addTest(new UseDistinctTest());
    }
}
