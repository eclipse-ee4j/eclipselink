/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
* <p>
* <b>Purpose</b>: Provides the implementation for the EntityManager Factory.
* <p>
* <b>Description</b>: This class will store a reference to the active ServerSession.  When a request
* is made for an EntityManager an new EntityManager is created with the ServerSession and returned.
* The primary consumer of these EntityManager is assumed to be either the Container.    There is
* one EntityManagerFactory per deployment.
* @see javax.persistence.EntityManager
* @see org.eclipse.persistence.jpa.EntityManager
* @see org.eclipse.persistence.jpa.EntityManagerFactory
*/

/*  @author  gyorke
 *  @since   TopLink 10.1.3 EJB 3.0 Preview
 */
public class EntityManagerFactoryImpl 
    extends org.eclipse.persistence.internal.jpa.base.EntityManagerFactoryImpl
    implements EntityManagerFactory 
{

    /**
     * Will return an instance of the Factory.  Should only be called by TopLink.
     * @param serverSession
     */
    public EntityManagerFactoryImpl(ServerSession serverSession) {
        super(serverSession);
    }

    /**
     * Will return an instance of the Factory.  Should only be called by TopLink.
     * @param serverSession
     */
    public EntityManagerFactoryImpl(EntityManagerSetupImpl setupImpl, Map properties) {
        super(setupImpl, properties);
    }

    /**
     * PUBLIC:
     * Returns an EntityManager for this deployment
     */
    public EntityManager createEntityManager() {
        return (EntityManager) createEntityManagerImpl(false);
    }
    
    /**
     * PUBLIC:
     * Returns an EntityManager for this deployment
     */
    public EntityManager createEntityManager(Map properties) {
        return (EntityManager) createEntityManagerImpl(properties, false);
    }

    //TODO change the way create works to deal with how the specification works with persistence contexts
    protected org.eclipse.persistence.internal.jpa.base.EntityManagerImpl createEntityManagerImplInternal(Map properties, boolean extended) {
        return new EntityManagerImpl(this, properties, false, extended);
    }
}
