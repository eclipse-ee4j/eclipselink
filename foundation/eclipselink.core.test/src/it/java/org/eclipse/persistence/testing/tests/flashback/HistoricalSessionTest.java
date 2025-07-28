/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.flashback;

import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.history.AsOfSCNClause;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestAdapter;

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

    @Override
    protected void setup() throws Exception {
        oldSession = getSession();
        getExecutor().setSession(getSession().acquireHistoricalSession(asOfClause));
    }

    @Override
    public void reset() {
        Session currentSession = getSession();
        getExecutor().setSession(oldSession);
        currentSession.release();
    }
}
