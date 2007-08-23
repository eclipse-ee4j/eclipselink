/* Copyright (c) 2003, 2007, Oracle. All rights reserved.  */
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