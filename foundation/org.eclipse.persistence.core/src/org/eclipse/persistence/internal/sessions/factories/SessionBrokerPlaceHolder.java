/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
