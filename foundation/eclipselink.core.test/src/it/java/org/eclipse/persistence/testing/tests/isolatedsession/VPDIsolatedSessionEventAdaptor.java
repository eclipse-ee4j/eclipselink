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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class VPDIsolatedSessionEventAdaptor extends SessionEventAdapter {
    public int session_id;
    public boolean noRowsModified;

    public VPDIsolatedSessionEventAdaptor() {
        this.noRowsModified = false;
    }

    @Override
    public void postAcquireExclusiveConnection(SessionEvent event) {
        event.getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_SESSION.SET_IDENTIFIER(" + this.session_id + ")"));
    }

    @Override
    public void preReleaseExclusiveConnection(SessionEvent event) {
        event.getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_SESSION.CLEAR_IDENTIFIER()"));
    }

    @Override
    public void noRowsModified(SessionEvent event) {
        this.noRowsModified = true;
    }

    public void setSession_Id(int id) {
        this.session_id = id;
    }
}
