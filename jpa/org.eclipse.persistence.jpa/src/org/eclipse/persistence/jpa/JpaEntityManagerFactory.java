/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation as part of extensibility feature
 ******************************************************************************/  
package org.eclipse.persistence.jpa;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Defines the Interface for EclipseLink extensions to the EntityManagerFactory
 * <p>
 * @see javax.persistence.EntityManagerFactory
 */
public interface JpaEntityManagerFactory {
    
    /**
     * Returns the DatabaseSession that the Factory will be using and
     * initializes it if it is not available.
     */
    public DatabaseSessionImpl getDatabaseSession();

    /**
     * Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available.
     */
    public ServerSession getServerSession();
    
    /**
     * Returns the SessionBroker that the Factory will be using and
     * initializes it if it is not available.
     * 
     * Calls to this method should only be made on entity managers
     * representing composite persistence units.
     */
    public SessionBroker getSessionBroker();
    
    /**
     * Gets the underlying implementation of the EntityManagerFactory.
     * This method will return a version of EntityManagerFactory that is
     * based on the available metadata at the time it is called.  Future calls
     * to refresh will not affect that metadata on this EntityManagerFactory.
     * @return
     */
    public EntityManagerFactoryDelegate unwrap();
    
    /**
     * As this EntityManagerFactory to refresh itself.  This will cause the
     * underlying EntityManagerFactory implementation to be dropped and a new one
     * to be bootstrapped.  Existing EntityManagers will continue to use the old implementation
     * @param properties
     */
    public void refreshMetadata(Map properties);

}

