/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.queries.SQLCall;

public class VPDIsolatedSessionEventAdaptor extends SessionEventAdapter {
    public int session_id;
    public boolean noRowsModified;

    public VPDIsolatedSessionEventAdaptor() {
        this.noRowsModified = false;
    }

    public void postAcquireExclusiveConnection(SessionEvent event) {
        event.getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_SESSION.SET_IDENTIFIER(" + this.session_id + ")"));
    }

    public void preReleaseExclusiveConnection(SessionEvent event) {
        event.getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_SESSION.CLEAR_IDENTIFIER()"));
    }

    public void noRowsModified(SessionEvent event) {
        this.noRowsModified = true;
    }

    public void setSession_Id(int id) {
        this.session_id = id;
    }
}
