/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.databaseaccess.Accessor;

public class ExclusiveIsolatedClientSession extends IsolatedClientSession {
    public ExclusiveIsolatedClientSession(ServerSession parent, ConnectionPolicy connectionPolicy) {
        super(parent, connectionPolicy);
        //the parents constructor sets an accessor, but we must set it back to null
        // as we will need our own.
        this.accessor = null;
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should never be called otherwise.
     */
    public void basicBeginTransaction() {
        //if we are begining the transaction then we should allow the accessor to
        //release the connection and re-acquire a new one.  This is usefull in
        //the case of external connection pools with JTA.
        if ((this.getWriteConnection() != null) && this.getLogin().shouldUseExternalConnectionPooling()) {
            // let the user remove the privledges
            getEventManager().preReleaseExclusiveConnection(this, this.getWriteConnection());
            //let the accessor release the connection
            this.getWriteConnection().decrementCallCount();
            //make the accessor re-acquire a new connection.  This supports the case
            //where a JTA transaction was started since the client session was first acquired.
            this.getWriteConnection().incrementCallCount(this);
            // let the user update the connection with required privledges.
            getEventManager().postAcquireExclusiveConnection(this, this.getWriteConnection());
        }
        super.basicBeginTransaction();
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should not be called otherwise.
     */
    public void basicCommitTransaction() {
        //if we are ending the transaction then we should allow the accessor to
        //release the connection and re-acquire a new one.  This is usefull in
        //the case of external connection pools with JTA.
        if ((this.getWriteConnection() != null) && this.getLogin().shouldUseExternalConnectionPooling()) {
            // let the user remove the privledges
            getEventManager().preReleaseExclusiveConnection(this, this.getWriteConnection());
            //let the accessor release the connection
            this.getWriteConnection().decrementCallCount();
            //make the accessor re-acquire a new connection.  This supports the case
            //where a JTA transaction was started since the client session was first acquired.
            this.getWriteConnection().incrementCallCount(this);
            // let the user update the connection with required privledges.
            getEventManager().postAcquireExclusiveConnection(this, this.getWriteConnection());
        }

        //Only releasee connection when transaction succeeds.  
        //If not, connection will be released in rollback.
        super.basicCommitTransaction();
    }

    /**
     * INTERNAL:
     * Override to acquire the connection from the pool at the last minute
     */
    public Object executeCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        if (query.getAccessor() == null) {
            query.setAccessor(getAccessor());
        }
        try {
            return query.getAccessor().executeCall(call, translationRow, this);
        } finally {
            if (call.isFinished()) {
                query.setAccessor(null);
            }
        }
    }

    /**
     * INTERNAL:
     * Gets the session which this query will be executed on.
     * Generally will be called immediately before the call is translated,
     * which is immediately before session.executeCall.
     * <p>
     * Since the execution session also knows the correct datasource platform
     * to execute on, it is often used in the mappings where the platform is
     * needed for type conversion, or where calls are translated.
     * <p>
     * Is also the session with the accessor.  Will return a ClientSession if
     * it is in transaction and has a write connection.
     * @return a session with a live accessor
     * @param query may store session name or reference class for brokers case
     */
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        AbstractSession executionSession = super.getExecutionSession(query);

        if (executionSession != this) {
            return executionSession;
        }

        if (!isActive()) {
            //client session was released but may exist in an indirection object.
            throw ValidationException.excusiveConnectionIsNoLongerAvailable(query, this);
        }

        //if the connection has not yet been acquired then do it here.
        if (getAccessor() == null) {
            this.getParent().acquireClientConnection(this);
        }
        return this;
    }

    public Accessor getAccessor() {
        return this.accessor;
    }

    /**
     * INTERNAL:
     * This method is extended from the superclass ClientSession.  This class
     * uses only one accessor for all connections and as such does not need a
     * seperate write connection.  The accessor will be used for all database
     * access
     */
    public Accessor getWriteConnection() {
        return this.accessor;
    }

    /**
     * INTERNAL:
     * As this session type should maintain it's transaction for its entire life-
     * cycle we will wait for 'release' before releasing connection.  By making
     * this method a no-op the connection will not be released until 'release()'
     * is called on the client session.
     */
    protected void releaseWriteConnection() {
        //do not release the connection until 'release()' is called on the
        //client session
    }

    /**
     * INTERNAL:
     * This method is extended from the superclass ClientSession.  This class
     * uses only one accessor for all connections and as such does not need a
     * seperate write connection.  The accessor will be used for all database
     * access
     */
    public void setWriteConnection(Accessor accessor) {
        if (getLogin().shouldUseExternalConnectionPooling()) {
            if (accessor != null) {
                accessor.incrementCallCount(this);// lets ensure that TopLink does not release this connection.
            } else {//the accessor is being released
                this.accessor.decrementCallCount();// let the accessor relase the connection
            }
        }
        this.accessor = accessor;
    }
}