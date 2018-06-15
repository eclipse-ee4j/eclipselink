/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
