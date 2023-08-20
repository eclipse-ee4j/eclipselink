/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: This class is used to represent a Session Broker within a SessionManager.
 * If a session Broker is requested from the SessionManager then this object is created.  Once
 * all of the required sessions have been loaded into the SesssionManger then the SessionBroker
 * will be returned.  Before that null will be returned.
 *
 * @since TopLink 4.0
 * @author Gordon Yorke
 */
public class SessionBrokerPlaceHolder extends org.eclipse.persistence.sessions.broker.SessionBroker {

    /** This member variable stores the sessions that have been retreived */
    protected List<String> sessionsCompleted;

    /** This member variable stores the sessions that need to be retreived */
    protected List<String> sessionNamesRequired;

    public SessionBrokerPlaceHolder() {
        super();
        this.sessionNamesRequired = new ArrayList<>();
        this.sessionsCompleted = new ArrayList<>();
    }

    public void addSessionName(String sessionName) {
        this.sessionNamesRequired.add(sessionName);
    }

    public List<String> getSessionNamesRequired() {
        return this.sessionNamesRequired;
    }

    public List<String> getSessionCompleted() {
        return this.sessionsCompleted;
    }
}
