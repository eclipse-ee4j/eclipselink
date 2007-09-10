/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.nls.japanese;


//package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;
import org.eclipse.persistence.testing.framework.TestSuite;

//Named queries are added in MW, therefore deployment XML or TopLink project.  Test all the query options.
public class NLSQueryOptionTestSuite extends TestSuite {
    public NLSQueryOptionTestSuite() {
        setDescription("[NLS_Japanese] This suite tests all of the functionality of the query options.");
    }

    public void addTests() {
        addTest(new NLSRefreshIdentityMapResultsTest());
        addTest(new NLSQueryTimeOutTest());
        addTest(new NLSMaxRowsTest());
        addTest(new NLSMemoryQueryReturnConfirmedTest());
        addTest(new NLSMemoryQueryReturnNotConfirmedTest());
        addTest(new NLSMemoryQueryThrowExceptionTest());
        addTest(new NLSMemoryQueryTriggerIndirectionTest());
        addTest(new NLSDoNotUseDistinctTest());
        addTest(new NLSUseDistinctTest());
        addTest(new NLSshouldPrepareTest());
    }
}