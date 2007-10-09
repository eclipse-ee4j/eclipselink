/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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