/* Copyright (c) 2003, 2005, Oracle. All rights reserved.  */
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

/**
 *  <b>Purpose:</b>An instance of TestAdapter used for running any test
 *  on a ClientSession configuration.
 *  <p>
 *  The client session is simple (unary) and has a read connection pool.
 *  @author  Stephen McRitchie
 *  @since   10 used for testing flashback queries on different sessions.
 */
public class ClientSessionTestAdapter extends ServerSessionTestAdapter {
    public ClientSessionTestAdapter(TestCase wrappedTest) {
        super(wrappedTest);
        setName("ClientSession:" + wrappedTest.getName());
        setDescription("On ClientSession: " + wrappedTest.getDescription());
    }

    public Session setupTestSession(Session oldSession) {
        return ((ServerSession)super.setupTestSession(oldSession)).acquireClientSession();
    }
}