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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.rmi.registry.Registry;

import java.util.Iterator;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;

/**
 * This class is used in the DistributedSession test to test the 
 * Cache Synchronisation feature.  This object places a RemoteDispatcher into
 * an RMI registry and then starts a session connection.
 */
public abstract class DistributedServer extends Thread {
    /** This attribute holds this threads session */
    public DatabaseSession session;

    /** This attribute holds the binding name for this server */
    public String serverName;

    /**
     * DistributedServer constructor comment.
     */
    public DistributedServer(Session testSssion) {
        super();
        if (testSssion.isSessionBroker()) {
            this.session = new SessionBroker();
            Iterator enumtr = ((SessionBroker)testSssion).getSessionsByName().keySet().iterator();
            while (enumtr.hasNext()) {
                String name = (String)enumtr.next();
                DatabaseSession newMemberSession = ((SessionBroker)testSssion).getSessionForName(name).getProject().createDatabaseSession();
                ((SessionBroker)this.session).registerSession(name, newMemberSession);
            }
        } else {
            this.session = testSssion.getProject().createDatabaseSession();
        }
        this.session.setSessionLog(testSssion.getSessionLog());
        this.session.login();
        if (testSssion.isSessionBroker()) {
            Iterator enumtr = ((SessionBroker)testSssion).getSessionsByName().keySet().iterator();
            while (enumtr.hasNext()) {
                String name = (String)enumtr.next();
                Session oldMemberSession = ((SessionBroker)testSssion).getSessionForName(name);
                Session newMemberSession = ((SessionBroker)this.session).getSessionForName(name);
                ((DatabaseAccessor)((AbstractSession)newMemberSession).getAccessor()).closeConnection();
                ((AbstractSession)newMemberSession).setAccessor(((AbstractSession)oldMemberSession).getAccessor());
            }
        } else {
            ((DatabaseAccessor)((AbstractSession)this.session).getAccessor()).closeConnection();
            ((AbstractSession)this.session).setAccessor(((AbstractSession)testSssion).getAccessor());
        }
    }

    /**
     * Returns the session from the distributed server
     * @return org.eclipse.persistence.sessions.DatabaseSession the Session from a particular distributed session
     */
    public org.eclipse.persistence.sessions.DatabaseSession getDistributedSession() {
        return session;
    }

    /**
     * This method returns the globally unique name of a particular server
     * @return java.lang.String the name of the server
     */
    public java.lang.String getServerName() {
        return serverName;
    }


    public boolean isObjectValid(Object object) {
        return session.getIdentityMapAccessor().isValid(object);
    }

    /**
     * This method starts the server and makes the dispatcher available
     * Creation date: (7/21/00 9:58:37 AM)
     */
    public abstract void run();

    public abstract void stopServer();

    /**
     * INTERNAL:
     * This method is used to set the globally Unique server name for the Distributed Server
     * @param newServerName java.lang.String
     */
    public void setServerName(java.lang.String newServerName) {
        serverName = newServerName;
    }

    /**
     * Removes this current server from the registry
     * Creation date: (7/21/00 10:53:37 AM)
     * @param registry java.rmi.registry.Registry
     */
    public void unbind(Registry registry) {
        try {
            registry.unbind(getServerName());
        } catch (Exception exception) {
        }
    }
}
