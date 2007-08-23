/* Copyright (c) 2003, 2006, Oracle. All rights reserved.  */
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.UniversalSessionTestAdapter;


/**
 *  <b>Purpose:</b>An instance of TestAdapter used for running any test
 *  on a ClientSessionBroker configuration.
 *  <p>
 *  The client session broker is simple (unary) and has a read connection pool.
 *  @author  Stephen McRitchie
 *  @since   10 used for testing flashback queries on different sessions.
 */
public class ClientSessionBrokerTestAdapter extends UniversalSessionTestAdapter {

    protected org.eclipse.persistence.testing.tests.clientserver.Server server;

    public ClientSessionBrokerTestAdapter(TestCase wrappedTest) {
        super(wrappedTest);
        setName("ClientSessionBroker:" + wrappedTest.getName());
        setDescription("On ClientSessionBroker: " + wrappedTest.getDescription());
    }

    public Session setupTestSession(Session oldSession) {
        DatabaseLogin login = (DatabaseLogin)oldSession.getLogin().clone();
        Project project = (Project)oldSession.getProject().clone();
        project.setLogin(login);
        this.server = new org.eclipse.persistence.testing.tests.clientserver.Server(project);
        this.server.serverSession.setLogLevel(oldSession.getLogLevel());
        this.server.serverSession.setLog(oldSession.getLog());
        this.server.login();
        this.server.copyDescriptors(oldSession);

        SessionBroker broker = new SessionBroker();
        broker.registerSession("serverSession", this.server.serverSession);

        // didn't login the broker.  Not needed and worried about why copyDescriptors above was for.
        return broker.acquireClientSessionBroker();
    }

    public void tearDownTestSession(Session testSession) {
        testSession.release();
        this.server.logout();
    }
}
