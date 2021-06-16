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
package org.eclipse.persistence.internal.sessions.factories;

import java.util.Vector;

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
    protected Vector sessionsCompleted;

    /** This member variable stores the sessions that need to be retreived */
    protected Vector sessionNamesRequired;

    public SessionBrokerPlaceHolder() {
        super();
        this.sessionNamesRequired = new Vector();
        this.sessionsCompleted = new Vector();
    }

    public void addSessionName(String sessionName) {
        this.sessionNamesRequired.add(sessionName);
    }

    public Vector getSessionNamesRequired() {
        return this.sessionNamesRequired;
    }

    public Vector getSessionCompleted() {
        return this.sessionsCompleted;
    }
}
