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
package org.eclipse.persistence.testing.tests.flashback;

import org.eclipse.persistence.history.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * <b>Purpose</b>:Any test run against a Time Aware Session.
 * <p>
 * <b>Responsibilities</b>:Wraps an existing test, and makes sure it
 * passes when executed using a TimeAwareSession.
 */
public class HistoricalSessionTest extends TestAdapter {
    public AsOfClause asOfClause;
    protected Session oldSession;

    public HistoricalSessionTest(AutoVerifyTestCase wrappedTest) {
        this(wrappedTest, AsOfClause.NO_CLAUSE);
    }

    public HistoricalSessionTest(AutoVerifyTestCase wrappedTest, Number systemChangeNumber) {
        this(wrappedTest, new AsOfSCNClause(systemChangeNumber));
    }

    public HistoricalSessionTest(AutoVerifyTestCase wrappedTest, AsOfClause asOfClause) {
        super(wrappedTest);
        this.asOfClause = asOfClause;
        setDescription("Using HistoricalSession: " + wrappedTest.getDescription());
    }

    protected void setup() throws Exception {
        oldSession = getSession();
        getExecutor().setSession(getSession().acquireHistoricalSession(asOfClause));
    }

    public void reset() {
        Session currentSession = getSession();
        getExecutor().setSession(oldSession);
        currentSession.release();
    }
}
