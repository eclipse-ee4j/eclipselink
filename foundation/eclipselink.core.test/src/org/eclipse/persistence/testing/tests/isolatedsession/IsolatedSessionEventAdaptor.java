/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEvent;

public class IsolatedSessionEventAdaptor extends SessionEventAdapter {
    public VerifyExclusiveConnectionTest test;

    public IsolatedSessionEventAdaptor(VerifyExclusiveConnectionTest test) {
        this.test = test;
    }

    public void postAcquireExclusiveConnection(SessionEvent event) {
        this.test.isPostEventFired = true;
    }

    public void preReleaseExclusiveConnection(SessionEvent event) {
        this.test.isPreEventFired = true;
    }
}
