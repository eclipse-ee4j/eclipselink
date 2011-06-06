/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
