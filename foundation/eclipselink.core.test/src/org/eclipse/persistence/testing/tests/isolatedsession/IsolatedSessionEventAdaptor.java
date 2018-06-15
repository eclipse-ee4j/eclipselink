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
