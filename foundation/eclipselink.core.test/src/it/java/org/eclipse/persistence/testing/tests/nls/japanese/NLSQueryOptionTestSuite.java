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
