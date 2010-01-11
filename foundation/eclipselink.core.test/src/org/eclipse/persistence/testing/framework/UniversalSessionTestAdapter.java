/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.sessions.*;

/**
 *  <b>Purpose:</b>An abstract instance of TestAdapter used for running any test
 *  on any session configuration.
 *  @author  Stephen McRitchie
 *  @since   10 used for testing flashback queries on different sessions.
 *  @see org.eclipse.persistence.testing.ClientServerTests.ClientSessionTestAdapter
 *  @see org.eclipse.persistence.testing.tests.sessionbroker.ClientSessionBrokerTestAdapter
 *  @see org.eclipse.persistence.testing.tests.flashback.HistoricalSessionTest
 */
public abstract class UniversalSessionTestAdapter extends TestAdapter {
    protected Session oldSession;
    protected Session testSession;

    public UniversalSessionTestAdapter(TestCase wrappedTest) {
        super(wrappedTest);
        //setName("UniversalSession:" + wrappedTest.getName());
    }

    public abstract Session setupTestSession(Session oldSession);

    public abstract void tearDownTestSession(Session testSession);

    protected void setup() throws Throwable {
        super.setup();
        setOldSession(getSession());
        setTestSession(setupTestSession(getOldSession()));
        getExecutor().setSession(getTestSession());
    }

    public void reset() throws Throwable {
        super.reset();
        getExecutor().setSession(getOldSession());
        tearDownTestSession(getTestSession());
        setTestSession(null);
    }

    public Session getOldSession() {
        return oldSession;
    }

    public Session getTestSession() {
        return testSession;
    }

    public void setOldSession(Session oldSession) {
        this.oldSession = oldSession;
    }

    public void setTestSession(Session testSession) {
        this.testSession = testSession;
    }
}
