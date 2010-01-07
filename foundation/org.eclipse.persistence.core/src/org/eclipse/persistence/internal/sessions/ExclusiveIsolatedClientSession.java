/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     
 *      05/28/2008-1.0M8 Andrei Ilitchev. 
 *      - 224964: Provide support for Proxy Authentication through JPA.
 *          No longer throws exception in case value holder for isolated class instantiated on the closed session.
 *          Instead after the session is closed it switches to a mode when it acquires write connection only for the duration of the query execution.
 *          The idea is all the necessary customization will be applied to the resurrected connection again, and cleared afterwards -
 *          may be expensive, but works and costs nothing unless actually used. 
 *          Moved setting of the accessor from getExecutionSession to executeCall because getExecutionSession is called sometimes
 *          without the need for connection (like createing a row), and that used to cause an unnecessary
 *          acquiring of the connection on the closed session.
 *     
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.Map;

import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.databaseaccess.Accessor;

public class ExclusiveIsolatedClientSession extends IsolatedClientSession {
    /**
     * If true all classes are read through the exclusive connection, otherwise only the isolated ones.
     */
    protected boolean shouldAlwaysUseExclusiveConnection;
    
    public ExclusiveIsolatedClientSession(ServerSession parent, ConnectionPolicy connectionPolicy) {
        this(parent, connectionPolicy, null);
    }

    public ExclusiveIsolatedClientSession(ServerSession parent, ConnectionPolicy connectionPolicy, Map properties) {
        super(parent, connectionPolicy, properties);
        //the parents constructor sets an accessor, but it will be never used.
        this.accessor = null;
        this.shouldAlwaysUseExclusiveConnection = connectionPolicy.isExclusiveAlways();
    }

    /**
     * INTERNAL:
     * Override to acquire the connection from the pool at the last minute
     */
    public Object executeCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        boolean shouldReleaseConnection = false;
        if (query.getAccessor() == null) {
            //if the connection has not yet been acquired then do it here.
            if (getAccessor() == null) {
                this.parent.acquireClientConnection(this);
                // the session has been already released and this query is likely instantiates a ValueHolder - 
                // release exclusive connection immediately after the query is executed, otherwise it may never be released.
                shouldReleaseConnection = !isActive();
            }
            query.setAccessor(getAccessor());
        }
        try {
            return query.getAccessor().executeCall(call, translationRow, this);
        } finally {
            if (call.isFinished()) {
                query.setAccessor(null);
            }
            // Note that connection could be release only if it has been acquired by the same query,
            // that allows to execute other queries from postAcquireConnection / preReleaseConnection events
            // without wiping out connection set by the original query or causing stack overflow, see
            // bug 299048 - Triggering indirection on closed ExclusiveIsolatedSession may cause exception 
            if(shouldReleaseConnection && getAccessor() != null) {
                this.parent.releaseClientSession(this);
            }
        }
    }

    /**
     * INTERNAL:
     * Always use writeConnection.
     */
    public Accessor getAccessor() {
        return this.writeConnection;
    }

    /**
     * INTERNAL:
     * Provided for consistency.
     */
    public void setAccessor(Accessor accessor) {
        setWriteConnection(accessor);
    }

    /**
     * INTERNAL:
     * As this session type should maintain it's transaction for its entire life-
     * cycle we will wait for 'release' before releasing connection.  By making
     * this method a no-op the connection will not be released until 'release()'
     * is called on the client session.
     */
    protected void releaseWriteConnection() {
        //do not release the connection until 'release()' is called on the client session
    }

    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used
     * right after the accessor is connected. 
     * Used by the session to rise an appropriate event.
     */
    public void postConnectExternalConnection(Accessor accessor) {
        super.postConnectExternalConnection(accessor);
        if (this.parent.hasEventManager()) {
            this.parent.getEventManager().postAcquireExclusiveConnection(this, accessor);
        }            
    }

    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used
     * right before the accessor is disconnected. 
     * Used by the session to rise an appropriate event.
     */
    public void preDisconnectExternalConnection(Accessor accessor) {
        super.preDisconnectExternalConnection(accessor);
        if (this.parent.hasEventManager()) {
            this.parent.getEventManager().preReleaseExclusiveConnection(this, accessor);
        }
    }
    
    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used.
     * If returns true, accessor used by the session keeps its
     * connection open until released by the session. 
     */
    public boolean isExclusiveConnectionRequired() {
        return isActive();
    }

    /**
     * PUBLIC:
     * Return if this session is an exclusive isolated client session.
     */
    public boolean isExclusiveIsolatedClientSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Helper method to calculate whether to execute this query locally or send
     * it to the server session.
     */
     protected boolean shouldExecuteLocally(DatabaseQuery query) {
         if (shouldAlwaysUseExclusiveConnection) {
             return true;
         } else {
             return super.shouldExecuteLocally(query);
         }
     }
}
