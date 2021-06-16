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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;

/**
 *  <b>Purpose:</b>An instance of TestAdapter used for running any test
 *  in a three-tier architecture.
 *  <p>
 *  On wrapped tests each call to getSession().acquireUnitOfWork() will
 *  create units of work on separate ClientSessions, each with their own
 *  exclusive connection.
 *  @author  Stephen McRitchie
 *  @since   10 used for testing pessimistic locking where competing UnitOfWorks
 *  share a global cache, each have an exclusive connection, and can block each
 *  other.
 */
public class ServerSessionTestAdapter extends UniversalSessionTestAdapter {
    protected Server server;

    public ServerSessionTestAdapter(TestCase wrappedTest) {
        super(wrappedTest);
        setName("ServerSession:" + wrappedTest.getName());
        setDescription("In three-tier: " + wrappedTest.getDescription());
    }

    public Session setupTestSession(Session oldSession) {
        DatabaseLogin login = (DatabaseLogin)oldSession.getLogin().clone();
        Project project = (Project)oldSession.getProject().clone();
        project.setLogin(login);
        this.server = new Server(project);
        this.server.serverSession.setLog(oldSession.getLog());
        this.server.serverSession.setLogLevel(oldSession.getLogLevel(null));
        this.server.login();
        this.server.copyDescriptors(oldSession);
        return this.server.serverSession;
    }

    public void tearDownTestSession(Session testSession) {
        testSession.release();
        this.server.logout();
    }
}
