/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions;

import java.util.*;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>: Encapsulate the information provided with session events.
 * This is used as the argument to any event raised by the session.
 * To register for events notification an event listener must be registered with the session.
 *
 * @see SessionEventManager#addListener(SessionEventListener)
 * @see Session#getEventManager()
 * @see SessionEventListener
 */
public class SessionEvent extends EventObject {

    /** Some events may have a query associated with them (pre/postExecuteQuery). */
    protected DatabaseQuery query;

    /** Some events may have a result associated with them (pre/postExecuteQuery). */
    protected Object result;

    /** The session or unit of work raising the event. */
    protected Session session;

    /** The code of the event being raised. This is an integer constant value as defined below. */
    protected int eventCode;

    /** Additional properties may be added. */
    protected Hashtable properties;
    public static final int PreExecuteQuery = 1;
    public static final int PostExecuteQuery = 2;
    public static final int PreBeginTransaction = 3;
    public static final int PostBeginTransaction = 4;
    public static final int PreCommitTransaction = 5;
    public static final int PostCommitTransaction = 6;
    public static final int PreRollbackTransaction = 7;
    public static final int PostRollbackTransaction = 8;

    // Unit of work events, only raised on unit of work.
    public static final int PostAcquireUnitOfWork = 9;
    public static final int PreCommitUnitOfWork = 10;
    public static final int PostCommitUnitOfWork = 11;
    public static final int PreReleaseUnitOfWork = 12;
    public static final int PostReleaseUnitOfWork = 13;
    public static final int PrepareUnitOfWork = 14;
    public static final int PostResumeUnitOfWork = 15;

    // Three-tier events, only raised on server/client session.
    public static final int PostAcquireClientSession = 16;
    public static final int PreReleaseClientSession = 17;
    public static final int PostReleaseClientSession = 18;
    public static final int PostAcquireConnection = 22;
    public static final int PostAcquireExclusiveConnection = 33;
    public static final int PreReleaseConnection = 23;
    public static final int PreReleaseExclusiveConnection = 34;

    // Database access events.
    public static final int OutputParametersDetected = 19;
    public static final int MoreRowsDetected = 20;
    public static final int PostConnect = 21;

    // Login events
    public static final int PreLogin = 24;
    public static final int PostLogin = 25;
    public static final int PreLogout = 40;
    public static final int PostLogout = 41;
    public static final int PreMergeUnitOfWorkChangeSet = 26;
    public static final int PreDistributedMergeUnitOfWorkChangeSet = 27;
    public static final int PostMergeUnitOfWorkChangeSet = 28;
    public static final int PostDistributedMergeUnitOfWorkChangeSet = 29;

    //ChangeSet Events
    public static final int PreCalculateUnitOfWorkChangeSet = 30;
    public static final int PostCalculateUnitOfWorkChangeSet = 31;
    public static final int MissingDescriptor = 32;
    public static final int NoRowsModified = 35;

    // last event value for this class as of Jan 26th, 2004 is 41

    /**
     * INTERNAL:
     * Create the event.
     */
    public SessionEvent(int eventCode, Session session) {
        super(session);
        this.session = session;
        this.eventCode = eventCode;
    }

    /**
     * PUBLIC:
     * The code of the session event being raised.
     * This is an integer constant value from this class.
     */
    public int getEventCode() {
        return eventCode;
    }

    /**
     * PUBLIC:
     * Additional properties may be added to the event.
     */
    public Hashtable getProperties() {
        if (properties == null) {
            properties = new Hashtable(2);
        }
        return properties;
    }

    /**
     * PUBLIC:
     * Additional properties may be added to the event.
     */
    public Object getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * PUBLIC:
     * Some events may have a query associated with them (pre/postExecuteQuery).
     */
    public DatabaseQuery getQuery() {
        return query;
    }

    /**
     * PUBLIC:
     * Some events may have a result associated with them (pre/postExecuteQuery).
     */
    public Object getResult() {
        return result;
    }

    /**
     * PUBLIC:
     * The session in which the event is raised.
     */
    public Session getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * The code of the session event being raised.
     * This is an integer constant value from this class.
     */
    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * INTERNAL:
     * Additional properties may be added to the event.
     */
    public void setProperties(Hashtable properties) {
        this.properties = properties;
    }

    /**
     * INTERNAL:
     * Additional properties may be added to the event.
     */
    public void setProperty(String name, Object value) {
        getProperties().put(name, value);
    }

    /**
     * INTERNAL:
     * Some events may have a query associated with them (pre/postExecuteQuery).
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * INTERNAL:
     * Some events may have a result associated with them (pre/postExecuteQuery).
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * INTERNAL:
     * The session in which the event is raised.
     */
    public void setSession(Session session) {
        this.session = session;
    }
}
